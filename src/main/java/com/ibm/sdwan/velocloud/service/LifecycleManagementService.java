package com.ibm.sdwan.velocloud.service;

import com.google.common.collect.ImmutableMap;
import com.ibm.sdwan.velocloud.config.SDWDriverProperties;
import com.ibm.sdwan.velocloud.driver.SDWanDriver;
import com.ibm.sdwan.velocloud.driver.SdwanResponseException;
import com.ibm.sdwan.velocloud.model.ExecutionAcceptedResponse;
import com.ibm.sdwan.velocloud.model.ExecutionRequest;
import com.ibm.sdwan.velocloud.model.alm.ExecutionAsyncResponse;
import com.ibm.sdwan.velocloud.model.alm.ExecutionStatus;
import com.ibm.sdwan.velocloud.model.velocloud.EdgeStatusMessage;
import com.ibm.sdwan.velocloud.model.velocloud.PollingEdgeStatus;
import com.ibm.sdwan.velocloud.utils.ValidationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;

import static com.ibm.sdwan.velocloud.utils.Constants.*;

@Service("LifecycleManagementService")
public class LifecycleManagementService {

    private final static Logger logger = LoggerFactory.getLogger(LifecycleManagementService.class);
    private final SDWanDriver sdwanDriver;
    private final MessageConversionService messageConversionService;
    private final ExternalMessagingService externalMessagingService;
    private final SDWDriverProperties rcDriverProperties;
    private final OperationsPayloadService operationsPayloadService;
    private final InternalMessagingService internalMessagingService;
    private final EdgeStatusMessage edgeStatusMessage;

    private static final Map<String, String> lcVCOperationsMap = ImmutableMap.<String, String>builder().
            put(LIFECYCLE_CREATE, "EdgeProvision").
            put(LIFECYCLE_DELETE, "DeleteEdge").
            build();

    @Autowired
    public LifecycleManagementService(SDWanDriver sdwanDriver, MessageConversionService messageConversionService,
            ExternalMessagingService externalMessagingService, SDWDriverProperties rcDriverProperties,
            OperationsPayloadService operationsPayloadService, InternalMessagingService internalMessagingService,
            EdgeStatusMessage edgeStatusMessage) {
        this.sdwanDriver = sdwanDriver;
        this.messageConversionService = messageConversionService;
        this.externalMessagingService = externalMessagingService;
        this.rcDriverProperties = rcDriverProperties;
        this.operationsPayloadService = operationsPayloadService;
        this.internalMessagingService = internalMessagingService;
        this.edgeStatusMessage = edgeStatusMessage;
    }

    public ExecutionAcceptedResponse executeLifecycle(ExecutionRequest executionRequest, String tenantId) throws MessageConversionException {
        final String requestId = UUID.randomUUID().toString();
        Map<String, Object> outputs = new HashMap<>();
        String lifecycleName = executionRequest.getLifecycleName();
        logger.info("Processing execution request");
        ValidationUtils.validateDeploymentProperties(executionRequest.getDeploymentLocation().getProperties());
        try {
            final String requestPayload;
            switch (lifecycleName) {
                case LIFECYCLE_OPERATION_ADD_VLAN:
                    // build payload for addvlan and executes API
                    String addvlanPayload = this.operationsPayloadService.buildPayloadForAddVlan(executionRequest,requestId);
                    this.sdwanDriver.execute(lifecycleName, executionRequest.getDeploymentLocation().getProperties(),
                            addvlanPayload, requestId);
                    break;
                case LIFECYCLE_OPERATION_ADD_VCE_STATIC_IP:
                    // build payload for addVceStaticIP and executes API
                    String addVceStaticIP = this.operationsPayloadService.buildPayloadForVceStaticIP(executionRequest,requestId);
                    this.sdwanDriver.execute(lifecycleName, executionRequest.getDeploymentLocation().getProperties(),
                            addVceStaticIP, requestId);
                    break;
                case LIFECYCLE_OPERATION_DELETE_VCE_STATIC_IP:
                    // check Edge exist or not
                    if (operationsPayloadService.isEdgeAvailable(executionRequest)) {
                        // build payload for deleteVceStaticIP and executes API
                        String deleteVceStaticIP = this.operationsPayloadService
                                .buildPayloadForDeleteVceStaticIP(executionRequest,requestId);
                        this.sdwanDriver.execute(lifecycleName,
                                executionRequest.getDeploymentLocation().getProperties(),
                                deleteVceStaticIP,requestId);
                    } else {
                        throw new SdwanResponseException(
                                String.format(
                                        "The specified Edge doesn't exist, so can't perform deleteVceStaticRoutingForCIDR operation"));
                    }
                    break;
                case LIFECYCLE_CREATE:
                    // EdgeProvision payloads are passed from package and values are substituted
                    // with resource properties.
                    requestPayload = messageConversionService
                            .generateMessageFromRequest(lcVCOperationsMap.get(lifecycleName), executionRequest);
                    String response = this.sdwanDriver.execute(lifecycleName,
                            executionRequest.getDeploymentLocation().getProperties(), requestPayload, requestId);
                    outputs = messageConversionService.extractPropertiesFromMessage(response);
                    break;
                case LIFECYCLE_DELETE:
                    // check Edge exist or not
                    if (operationsPayloadService.isEdgeAvailable(executionRequest)) {
                        edgeStatusMessage.setStatusMessage(PollingEdgeStatus.NOT_READY);
                        edgeStatusMessage.setRequestId(requestId);
                        edgeStatusMessage
                                .setDeploymentLocationProperties(
                                        executionRequest.getDeploymentLocation().getProperties());
                        // fetching EDGE_ID from resource properties.
                        if (executionRequest.getProperties().get(EDGE_ID) instanceof String) {
                            edgeStatusMessage
                                    .setId(Integer.parseInt((String) executionRequest.getProperties().get(EDGE_ID)));
                        } else {
                            edgeStatusMessage.setId((int) executionRequest.getProperties().get(EDGE_ID));
                        }
                        if (executionRequest.getProperties().get(ACTIVATION_KEY) instanceof String) {
                            if (!StringUtils.hasLength((String) executionRequest.getProperties().get(ACTIVATION_KEY))) {
                                throw new MissingPropertyException(
                                        " activationKey property is mandatory and it is missing");
                            }
                        } else if (executionRequest.getProperties().get(ACTIVATION_KEY) == null) {
                            throw new MissingPropertyException(
                                    " activationKey property is mandatory and it is missing");
                        }
                        edgeStatusMessage
                                .setActivationKey((String) executionRequest.getProperties().get(ACTIVATION_KEY));
                        requestPayload = messageConversionService.generateMessageFromRequest(DELETE_EDGE,
                                executionRequest);
                        edgeStatusMessage.setDeletePayload(requestPayload);
                        edgeStatusMessage.setTenantId(tenantId);
                        internalMessagingService.sendEdgeStatusAsyncResponse(edgeStatusMessage);
                        return new ExecutionAcceptedResponse(requestId);
                    } else {
                        throw new SdwanResponseException(
                                String.format(
                                        "The specified Edge doesn't exist, so can't perform Delete lifecycle"));
                    }

                default:
                    throw new IllegalArgumentException(String.format(
                            "Requested transition [%s] is not supported by this lifecycle driver", lifecycleName));
            }
        } catch (RestClientResponseException e) {
            throw new SdwanResponseException(
                    String.format(
                            "Caught REST client exception when communicating with Velocloud-SDWAN: " + e.getMessage()));
        }
        // send delayed response to Kafka except delete lifecycle
        externalMessagingService.sendDelayedExecutionAsyncResponse(
                new ExecutionAsyncResponse(requestId, ExecutionStatus.COMPLETE, null, outputs, Collections.emptyMap()),tenantId,
                rcDriverProperties.getExecutionResponseDelay());

        return new ExecutionAcceptedResponse(requestId); 
    }
}
