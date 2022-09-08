package com.ibm.sdwan.velocloud.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.sdwan.velocloud.config.SDWDriverProperties;
import com.ibm.sdwan.velocloud.driver.SDWanDriver;
import com.ibm.sdwan.velocloud.driver.SdwanResponseException;
import com.ibm.sdwan.velocloud.model.alm.ExecutionAsyncResponse;
import com.ibm.sdwan.velocloud.model.alm.ExecutionStatus;
import com.ibm.sdwan.velocloud.model.alm.FailureDetails;
import com.ibm.sdwan.velocloud.model.alm.FailureDetails.FailureCode;
import com.ibm.sdwan.velocloud.model.velocloud.EdgeStatusMessage;
import com.ibm.sdwan.velocloud.model.velocloud.GetEdgeRequest;
import com.ibm.sdwan.velocloud.model.velocloud.PollingEdgeStatus;
import com.ibm.sdwan.velocloud.service.ExternalMessagingService;
import com.ibm.sdwan.velocloud.service.InternalMessagingService;
import com.ibm.sdwan.velocloud.service.MessageConversionException;
import com.ibm.sdwan.velocloud.service.MessageConversionService;
import com.ibm.sdwan.velocloud.service.ThreadInterruptedException;

import static com.ibm.sdwan.velocloud.utils.Constants.*;

@Component
public class PollingEdgeStatusServiceListener {
    private static final Logger logger = LoggerFactory.getLogger(PollingEdgeStatusServiceListener.class);

    private final SDWanDriver sdwanDriver;
    private final InternalMessagingService internalMessagingService;
    private final SDWDriverProperties sdwDriverProperties;
    private final ObjectMapper objectMapper;
    private final GetEdgeRequest getEdgeRequest;
    private final MessageConversionService messageConversionService;
    private final ExternalMessagingService externalMessagingService;

    @Autowired
    public PollingEdgeStatusServiceListener(SDWanDriver sdwanDriver, InternalMessagingService internalMessagingService,
            SDWDriverProperties sdwDriverProperties, ObjectMapper objectMapper, GetEdgeRequest getEdgeRequest,
            MessageConversionService messageConversionService, ExternalMessagingService externalMessagingService) {
        this.sdwanDriver = sdwanDriver;
        this.internalMessagingService = internalMessagingService;
        this.sdwDriverProperties = sdwDriverProperties;
        this.objectMapper = objectMapper;
        this.getEdgeRequest = getEdgeRequest;
        this.messageConversionService = messageConversionService;
        this.externalMessagingService = externalMessagingService;

    }

    @PreDestroy
    public void close() {
        logger.info("Shutting down Delete Lifecycle Management Operation for EdgeStatus ...");
    }

    @KafkaListener(topics = "${rcdriver.topics.pollingEdgeStatusTopic}")
    public void listenForPollingEdgeStatusMessages(final String message) throws MessageConversionException {
        Map<String, Object> responseMap = new HashMap<>();
        final EdgeStatusMessage edgeStatusMessage;
        // Deserialize message into EdgeStatusMessage
        try {
            edgeStatusMessage = objectMapper.readValue(message, EdgeStatusMessage.class);
        } catch (JsonProcessingException e) {
            throw new MessageConversionException("Exception while generating ExecutionRequest form message", e);
        }

        Map<String, Object> deploymentLocationProperties = edgeStatusMessage.getDeploymentLocationProperties();
        int id = edgeStatusMessage.getId();
        String activationKey = edgeStatusMessage.getActivationKey();
        String deletePayload = edgeStatusMessage.getDeletePayload();
        String requestId = edgeStatusMessage.getRequestId();
        PollingEdgeStatus status = edgeStatusMessage.getStatusMessage();
        String tenantId = edgeStatusMessage.getTenantId();

        logger.debug("Status received : " + status);
        try {
            switch (status) {
                case READY:
                    // makes edgeDelete API request.
                    logger.debug("Inside READY status exceution ");
                    String deleteResponse = this.sdwanDriver.execute(LIFECYCLE_DELETE, deploymentLocationProperties,
                            deletePayload, DELETE_EDGE, requestId);
                    responseMap = messageConversionService.extractPropertiesFromMessage(deleteResponse);
                    // send brent kafka changes
                    logger.debug("Sending response to external Kafka Topic ");
                    externalMessagingService.sendExecutionAsyncResponse(new ExecutionAsyncResponse(requestId,
                            ExecutionStatus.COMPLETE, null, responseMap, Collections.emptyMap()), tenantId);
                    break;
                case NOT_READY:
                    // wait for 5 secs
                    logger.debug("Inside NOT_READY status exceution ");
                    try {
                        Thread.sleep(sdwDriverProperties.getEdgePollingDelay().toMillis());
                    } catch (InterruptedException interruptedException) {
                        handleAsyncError(requestId, interruptedException, tenantId);
                        throw new ThreadInterruptedException("Thread interrupted during sleep");
                    }
                    // call for velocloud API to get status
                    String getEdgeRequestPayload = buildPayloadForGetEdge(deploymentLocationProperties, id,
                            activationKey, requestId, tenantId);
                    String getEdgeResponse = this.sdwanDriver.execute(LIFECYCLE_DELETE,
                            deploymentLocationProperties, getEdgeRequestPayload, GET_EDGE, requestId);
                    responseMap = messageConversionService.extractPropertiesFromMessage(getEdgeResponse);
                    if (responseMap.containsValue(DEGRADED_STATE) || responseMap.containsValue(CONNECTED_STATE)) {
                        // send not-ready status to kakfa topic
                        logger.debug("Sending not_ready status to internal Kafka topic");
                        edgeStatusMessage.setStatusMessage(PollingEdgeStatus.NOT_READY);
                        internalMessagingService.sendEdgeStatusAsyncResponse(edgeStatusMessage);
                    } else {
                        // send ready status to kakfa topic
                        logger.debug("Sending ready status to internal Kafka topic ");
                        edgeStatusMessage.setStatusMessage(PollingEdgeStatus.READY);
                        internalMessagingService.sendEdgeStatusAsyncResponse(edgeStatusMessage);
                    }
                    break;
                default:
                    handleAsyncError(requestId, new IllegalArgumentException("Internal Kafka topic/message issue"), tenantId);
                    throw new IllegalArgumentException(String.format(
                            "Edge status [%s] is not supported by kafka topic used in delete lifecycle", status));
            }
        } catch (RestClientResponseException restClientResponseException) {
            handleAsyncError(requestId, restClientResponseException, tenantId);
            throw new SdwanResponseException(
                    String.format(
                            "Caught REST client exception when communicating with Velocloud-SDWAN: "
                                    + restClientResponseException.getMessage()));
        }
    }

    public String buildPayloadForGetEdge(Map<String, Object> deploymentLocationProperties, int id, String activationKey, String requestId, String tenantId)
            throws MessageConversionException {
        if (deploymentLocationProperties.get("enterpriseId") instanceof String) {
            getEdgeRequest.setEnterpriseId(Integer.parseInt((String) deploymentLocationProperties.get("enterpriseId")));
        } else {
            getEdgeRequest.setEnterpriseId((int) deploymentLocationProperties.get("enterpriseId"));
        }
        getEdgeRequest.setId(id);
        getEdgeRequest.setActivationKey(activationKey);
        logger.debug("Payload for getEdge API call " + getEdgeRequest.toString());
        try {
            return objectMapper.writeValueAsString(getEdgeRequest);
        } catch (JsonProcessingException jsonProcessingException) {
            handleAsyncError(requestId, jsonProcessingException, tenantId);
            throw new MessageConversionException("could not convert Map to Json string");
        }
    }
    
    public void handleAsyncError(String requestId, Throwable exception, String tenantId) {

        FailureDetails failureDetails = new FailureDetails();
        failureDetails.setFailureCode(FailureCode.INTERNAL_ERROR);
        failureDetails.setDescription("Velocloud-SDWAN: " + exception.getMessage());

        externalMessagingService.sendExecutionAsyncResponse(new ExecutionAsyncResponse(requestId,
                ExecutionStatus.FAILED, failureDetails, null, Collections.emptyMap()), tenantId);

    }
}
