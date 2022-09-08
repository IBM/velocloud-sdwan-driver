package com.ibm.sdwan.velocloud.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.sdwan.velocloud.config.SDWDriverProperties;
import com.ibm.sdwan.velocloud.driver.SDWanDriver;
import com.ibm.sdwan.velocloud.driver.SdwanResponseException;
import com.ibm.sdwan.velocloud.model.ExecutionRequestPropertyValue;
import com.ibm.sdwan.velocloud.model.GenericExecutionRequestPropertyValue;
import com.ibm.sdwan.velocloud.model.velocloud.EdgeStatusMessage;
import com.ibm.sdwan.velocloud.model.velocloud.GetEdgeRequest;
import com.ibm.sdwan.velocloud.model.velocloud.PollingEdgeStatus;
import com.ibm.sdwan.velocloud.service.ExternalMessagingService;
import com.ibm.sdwan.velocloud.service.InternalMessagingService;
import com.ibm.sdwan.velocloud.service.MessageConversionException;
import com.ibm.sdwan.velocloud.service.MessageConversionService;

@SpringBootTest
public class PollingEdgeStatusServiceListenerTest {

	private PollingEdgeStatusServiceListener pollingEdgeStatusService;
	@Mock
	private SDWanDriver sdwanDriver;
	@Mock
	private InternalMessagingService internalMessagingService;
	@Mock
	private SDWDriverProperties sdwDriverProperties;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private GetEdgeRequest getEdgeRequest;
	@Mock
	private MessageConversionService messageConversionService;
	@Mock
	private ExternalMessagingService externalMessagingService;

	private EdgeStatusMessage edgeStatusMessage;

	private Map<String, ExecutionRequestPropertyValue> resourceProperties;

	@BeforeEach
	public void setUp() {
		pollingEdgeStatusService = new PollingEdgeStatusServiceListener(sdwanDriver, internalMessagingService,
				sdwDriverProperties, objectMapper, getEdgeRequest, messageConversionService, externalMessagingService);
		resourceProperties = new HashMap<>();
		resourceProperties.put("id", new GenericExecutionRequestPropertyValue(123));
		resourceProperties.put("enterpriseId", new GenericExecutionRequestPropertyValue(345));
		resourceProperties.put("activationKey", new GenericExecutionRequestPropertyValue("activationKey"));

		Map<String, Object> dummyDepLocProperties = new HashMap<>();
		dummyDepLocProperties.put("dummy", "dummy");
		dummyDepLocProperties.put("enterpriseId", "1");
		edgeStatusMessage =  new EdgeStatusMessage();
		edgeStatusMessage.setDeploymentLocationProperties(dummyDepLocProperties);

	}

	@Test
	@DisplayName("Testing positive scenario for ListenForPolling EdgeStatusMessages Status Ready")
	public void testListenForPollingEdgeStatusMessagesStatusReady()
			throws MessageConversionException, JsonMappingException, JsonProcessingException {
		
		edgeStatusMessage.setStatusMessage(PollingEdgeStatus.READY);
		Mockito.when(objectMapper.readValue("", EdgeStatusMessage.class)).thenReturn(edgeStatusMessage);
		pollingEdgeStatusService.listenForPollingEdgeStatusMessages("");
		verify(messageConversionService, times(1)).extractPropertiesFromMessage(null);
	}

	@Test
	@DisplayName("Testing positive scenario for ListenForPolling EdgeStatusMessages Status Not Ready")
	public void testListenForPollingEdgeStatusMessagesStatusNotReady()
			throws MessageConversionException, JsonMappingException, JsonProcessingException {
		edgeStatusMessage.setStatusMessage(PollingEdgeStatus.NOT_READY);
		Mockito.when(objectMapper.readValue("", EdgeStatusMessage.class)).thenReturn(edgeStatusMessage); 
		pollingEdgeStatusService.listenForPollingEdgeStatusMessages("");
		verify(messageConversionService, times(1)).extractPropertiesFromMessage(null);
	}
	
	@Test
	@DisplayName("Testing positive scenario for ListenForPolling EdgeStatusMessages Status Not Ready With DEGRADED property")
	public void testListenForPollingEdgeStatusMessagesStatusNotReadyWithDEGRADEDProperty()
			throws MessageConversionException, JsonMappingException, JsonProcessingException {
		edgeStatusMessage.setStatusMessage(PollingEdgeStatus.NOT_READY);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("DEGRADED", "DEGRADED");
		Mockito.when(messageConversionService.extractPropertiesFromMessage(Mockito.any())).thenReturn(responseMap);
		Mockito.when(objectMapper.readValue("", EdgeStatusMessage.class)).thenReturn(edgeStatusMessage);
		pollingEdgeStatusService.listenForPollingEdgeStatusMessages("");
		verify(internalMessagingService, times(1)).sendEdgeStatusAsyncResponse(Mockito.any());
	}
	
	@Test
	@DisplayName("Testing exception scenario for ListenForPolling EdgeStatusMessages Status Not Ready With DEGRADED property and enterpriseId integer")
	public void testListenForPollingEdgeStatusMessagesStatusNotReadyWithDEGRADEDPropertyAndEnterpriseId()
			throws MessageConversionException, JsonMappingException, JsonProcessingException {
		edgeStatusMessage.getDeploymentLocationProperties().put("enterpriseId", 1);
		edgeStatusMessage.setStatusMessage(PollingEdgeStatus.NOT_READY);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("DEGRADED", "DEGRADED");
		Mockito.when(messageConversionService.extractPropertiesFromMessage(Mockito.any())).thenReturn(responseMap);
		Mockito.when(objectMapper.readValue("", EdgeStatusMessage.class)).thenReturn(edgeStatusMessage);
		Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(JsonProcessingException.class);
		Assertions.assertThrows(MessageConversionException.class,
				() -> pollingEdgeStatusService.listenForPollingEdgeStatusMessages(""));
	}

	@Test
	@DisplayName("Testing exception scenario for ListenForPolling EdgeStatusMessages - RestClientResponseException")
	public void testListenForPollingEdgeStatusMessagesRestClientResponseException()
			throws MessageConversionException, JsonMappingException, JsonProcessingException {		
		edgeStatusMessage.setStatusMessage(PollingEdgeStatus.NOT_READY);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("DEGRADED", "DEGRADED");
		Mockito.when(messageConversionService.extractPropertiesFromMessage(Mockito.any())).thenThrow(RestClientResponseException.class);
		Mockito.when(objectMapper.readValue("", EdgeStatusMessage.class)).thenReturn(edgeStatusMessage);
		Assertions.assertThrows(SdwanResponseException.class,
				() -> pollingEdgeStatusService.listenForPollingEdgeStatusMessages(""));
	}

	@Test
	@DisplayName("Testing exception scenario for ListenForPolling EdgeStatusMessages - ParserException")
	public void testListenForPollingEdgeStatusMessagesParserException()
			throws MessageConversionException, JsonMappingException, JsonProcessingException {
		Mockito.when(objectMapper.readValue("", EdgeStatusMessage.class)).thenThrow(JsonProcessingException.class);
		Assertions.assertThrows(MessageConversionException.class,
				() -> pollingEdgeStatusService.listenForPollingEdgeStatusMessages(""));

	}

}