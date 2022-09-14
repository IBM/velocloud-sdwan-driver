package com.ibm.sdwan.velocloud.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;

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
import com.ibm.sdwan.velocloud.model.alm.ExecutionAsyncResponse;

@SpringBootTest
public class KafkaExternalMessagingServiceImplTest {
	
	@Autowired
 	private SDWDriverProperties properties;
 	@Mock
 	private KafkaTemplate<String, String> kafkaTemplate;
 	@Mock
 	private ObjectMapper objectMapper;
 	private KafkaExternalMessagingServiceImpl kafkaExternalMessagingServiceImpl;

 	@BeforeEach
 	public void setUp() {
 		kafkaExternalMessagingServiceImpl = new KafkaExternalMessagingServiceImpl(properties, kafkaTemplate,
 				objectMapper);
 	}

 	@Test
 	@DisplayName("Testing positive scenario to Send Delayed Execution Async Response")
 	public void sendDelayedExecutionAsyncResponseTest() throws JsonProcessingException {
 		ExecutionAsyncResponse executionAsyncResponse = new ExecutionAsyncResponse();
 		executionAsyncResponse.setRequestId("id");
 		SendResult<String, Object> sendResult = mock(SendResult.class);
 		ListenableFuture<SendResult<String, String>> responseFuture = mock(ListenableFuture.class);

 		Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("test");
 		Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.anyString())).thenReturn(responseFuture);
 		kafkaExternalMessagingServiceImpl.sendDelayedExecutionAsyncResponse(executionAsyncResponse,"1",
 				Duration.ofMillis(100));
 		verify(objectMapper, times(1)).writeValueAsString(executionAsyncResponse);
 	}

}
