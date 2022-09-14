package com.ibm.sdwan.velocloud.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.sdwan.velocloud.config.SDWDriverProperties;
import com.ibm.sdwan.velocloud.model.velocloud.EdgeStatusMessage;

@SpringBootTest
public class KafkaInternalMessagingServiceImplTest {
	
	@Autowired
	private  SDWDriverProperties properties;
	@Mock
    private  KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private  ObjectMapper objectMapper;
    private KafkaInternalMessagingServiceImpl kafkaInternalMessagingServiceImpl;
    
    @BeforeEach
 	public void setUp() {
    	kafkaInternalMessagingServiceImpl = new KafkaInternalMessagingServiceImpl(properties, kafkaTemplate, objectMapper);
 	}
    
    @Test
 	@DisplayName("Testing positive scenario to Send Delayed Execution Async Response")
    public void  sendEdgeStatusAsyncResponse() throws JsonProcessingException {
    	EdgeStatusMessage edgeStatusMessage = new EdgeStatusMessage();
    	edgeStatusMessage.setId(123);
    	SendResult<String, Object> sendResult = mock(SendResult.class);
 		ListenableFuture<SendResult<String, String>> responseFuture = mock(ListenableFuture.class);
 		Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("test");
 		Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.anyString())).thenReturn(responseFuture);
    	kafkaInternalMessagingServiceImpl.sendEdgeStatusAsyncResponse(edgeStatusMessage);
	   
   }
 
    @Test
 	@DisplayName("Testing exception scenario for Async Response--JsonProcessingException")
    public void  sendEdgeStatusAsyncResponseexception() throws JsonProcessingException {
    	SendResult<String, Object> sendResult = mock(SendResult.class);
 		ListenableFuture<SendResult<String, String>> responseFuture = mock(ListenableFuture.class);

 		Mockito.when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
 		kafkaInternalMessagingServiceImpl.sendEdgeStatusAsyncResponse(null);
	   
   }
    
}
