package com.ibm.sdwan.velocloud.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.sdwan.velocloud.config.SDWDriverProperties;
import com.ibm.sdwan.velocloud.model.velocloud.EdgeStatusMessage;
import com.ibm.sdwan.velocloud.service.InternalMessagingService;

@Service
public class KafkaInternalMessagingServiceImpl implements InternalMessagingService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaInternalMessagingServiceImpl.class);

    private final SDWDriverProperties properties;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaInternalMessagingServiceImpl(SDWDriverProperties properties,
            KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.properties = properties;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendEdgeStatusAsyncResponse(EdgeStatusMessage edgeStatusMessage) {
        String message;
        try {
            message = objectMapper.writeValueAsString(edgeStatusMessage);
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(properties.getTopics().getPollingEdgeStatusTopic(), message);
            future.addCallback(sendResult -> logger.debug("EdgeStatusMessage successfully sent"),
                               exception  -> logger.error("Exception while sending EdgeStatusMessage", exception));
        } catch (JsonProcessingException e) {
            logger.error("Exception while generating message text from EdgeStatusMessage", e);
            e.printStackTrace();
        }

    }

}
