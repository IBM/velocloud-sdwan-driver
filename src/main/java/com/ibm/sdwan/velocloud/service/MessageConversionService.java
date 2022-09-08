package com.ibm.sdwan.velocloud.service;

import com.ibm.sdwan.velocloud.model.ExecutionRequest;

import java.util.Map;

public interface MessageConversionService {

    String generateMessageFromRequest(String messageType, ExecutionRequest executionRequest) throws MessageConversionException;
    Map<String, Object> extractPropertiesFromMessage(String message);

}