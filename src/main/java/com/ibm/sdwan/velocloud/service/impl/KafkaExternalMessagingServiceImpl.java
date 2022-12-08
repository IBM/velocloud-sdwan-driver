package com.ibm.sdwan.velocloud.service.impl;

import java.time.Duration;

import com.ibm.sdwan.velocloud.config.SDWDriverProperties;
import com.ibm.sdwan.velocloud.model.LcmOpOccPollingRequest;
import com.ibm.sdwan.velocloud.model.alm.ExecutionAsyncResponse;
import com.ibm.sdwan.velocloud.service.ExternalMessagingService;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.ibm.sdwan.velocloud.utils.Constants.*;

@Service
public class KafkaExternalMessagingServiceImpl implements ExternalMessagingService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaExternalMessagingServiceImpl.class);

    private final SDWDriverProperties properties;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaExternalMessagingServiceImpl(SDWDriverProperties properties, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.properties = properties;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override public void sendExecutionAsyncResponse(ExecutionAsyncResponse request, String tenantId) {
        try {
            final String message = objectMapper.writeValueAsString(request);
            if(tenantId.isEmpty()) {        
                ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(properties.getTopics().getLifecycleResponsesTopic(), message);
                future.addCallback(sendResult -> logger.debug("ExecutionAsyncResponse successfully sent"),
                        exception -> logger.warn("Exception while sending ExecutionAsyncResponse", exception));
            }else {
                logger.info("TenantId in Kafka: " + tenantId);
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(properties.getTopics().getLifecycleResponsesTopic(), message);
                producerRecord.headers().add(TENANT_ID, tenantId.getBytes());
                ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(producerRecord);
                future.addCallback(sendResult -> logger.debug("ExecutionAsyncResponse successfully sent"),
                        exception -> logger.warn("Exception while sending ExecutionAsyncResponse", exception));
            }
        } catch (JsonProcessingException e) {
            logger.warn("Exception generating message text from ExecutionAsyncResponse", e);
        }
    }

    @Override
    @Async
    public void sendDelayedExecutionAsyncResponse(ExecutionAsyncResponse request, String tenantId, Duration delay) {
        if (delay != null) {
            try {
                Thread.sleep(delay.toMillis());
            } catch (InterruptedException e) {
                logger.error("Thread interrupted during sleep", e);
            }
        }
        sendExecutionAsyncResponse(request, tenantId);
    }

    @Override public void sendLcmOpOccPollingRequest(LcmOpOccPollingRequest request) {
        try {
            try {
                Thread.sleep(properties.getLcmOpOccPollingDelay().toMillis());
            } catch (InterruptedException e) {
                logger.error("Thread interrupted during sleep", e);
            }
            final String message = objectMapper.writeValueAsString(request);
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(properties.getTopics().getLcmOpOccPollingTopic(), message);

            future.addCallback(sendResult -> logger.debug("Submitted request to poll for LcmOpOcc [{}]", request.getVnfLcmOpOccId()),
                    exception -> logger.warn("Exception sending LcmOpOccPollingRequest", exception));
        } catch (JsonProcessingException e) {
            logger.warn("Exception generating message text from LcmOpOccPollingRequest", e);
        }
    }

}