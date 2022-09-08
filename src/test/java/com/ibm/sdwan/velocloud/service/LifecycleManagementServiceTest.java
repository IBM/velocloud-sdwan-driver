package com.ibm.sdwan.velocloud.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.ibm.sdwan.velocloud.config.SDWDriverProperties;
import com.ibm.sdwan.velocloud.driver.SDWanDriver;
import com.ibm.sdwan.velocloud.driver.SdwanResponseException;
import com.ibm.sdwan.velocloud.model.ExecutionAcceptedResponse;
import com.ibm.sdwan.velocloud.model.ExecutionRequest;
import com.ibm.sdwan.velocloud.model.ExecutionRequestPropertyValue;
import com.ibm.sdwan.velocloud.model.GenericExecutionRequestPropertyValue;
import com.ibm.sdwan.velocloud.model.ResourceManagerDeploymentLocation;
import com.ibm.sdwan.velocloud.model.velocloud.EdgeStatusMessage;
import com.ibm.sdwan.velocloud.utils.Constants;

@SpringBootTest
public class LifecycleManagementServiceTest {

	private LifecycleManagementService lifecycleManagementService;
	private ExecutionRequest executionRequest;
    private Map<String, ExecutionRequestPropertyValue> resourceProperties;
    @Mock
    private SDWanDriver sdwanDriver;
	@Mock
    private MessageConversionService messageConversionService;
	@Mock
    private ExternalMessagingService externalMessagingService;
	@Mock
    private SDWDriverProperties rcDriverProperties;
	@Mock
    private OperationsPayloadService operationsPayloadService;
	@Mock
    private InternalMessagingService internalMessagingService;
	@Mock
	private  EdgeStatusMessage edgeStatusMessage;

	private String tenantId;	
	@BeforeEach
	public void setUp() {
		lifecycleManagementService = new LifecycleManagementService(sdwanDriver, messageConversionService, externalMessagingService, rcDriverProperties, operationsPayloadService, internalMessagingService, edgeStatusMessage);
		executionRequest = new ExecutionRequest();
		tenantId= "1";
        resourceProperties = new HashMap<>();
        Map<String, Object> dummyDepLocProperties = new HashMap<>();
        dummyDepLocProperties.put("dummy", "dummy");
        dummyDepLocProperties.put(SDWanDriver.API_CONTEXT, "api");
        dummyDepLocProperties.put("SDWANServerUrl", "url");
        dummyDepLocProperties.put(SDWanDriver.API_AUTH_TOKEN, "auth");
        dummyDepLocProperties.put("enterpriseId", "enterpriseId");
        ResourceManagerDeploymentLocation deploymentLocation = new ResourceManagerDeploymentLocation();
        deploymentLocation.setProperties(dummyDepLocProperties);
        executionRequest.setDeploymentLocation(deploymentLocation);
	}

	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Add Vlan")
	public void executeLifecycleAddVlanTest() throws MessageConversionException {
		executionRequest.setLifecycleName(Constants.LIFECYCLE_OPERATION_ADD_VLAN);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		ExecutionAcceptedResponse result = lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
		Assertions.assertNotNull(result);
	}

	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Add VceStatic")
	public void executeLifecycleAddVceStaticTest() throws MessageConversionException {
		executionRequest.setLifecycleName(Constants.LIFECYCLE_OPERATION_ADD_VCE_STATIC_IP);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		ExecutionAcceptedResponse result = lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
		Assertions.assertNotNull(result);
	}

	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Delete VceStatic")
	public void executeLifecycleDeleteVceStaticTest() throws MessageConversionException {
		executionRequest.setLifecycleName(Constants.LIFECYCLE_OPERATION_DELETE_VCE_STATIC_IP);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		Mockito.when(operationsPayloadService.isEdgeAvailable(Mockito.any())).thenReturn(Boolean.TRUE);
		ExecutionAcceptedResponse result = lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
		Assertions.assertNotNull(result);
	}
	
	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Delete VceStatic - with isEdgeAvailable false")
	public void executeLifecycleDeleteVceStaticTestWithIsEdgeAvailableFalse() throws MessageConversionException {
		executionRequest.setLifecycleName(Constants.LIFECYCLE_OPERATION_DELETE_VCE_STATIC_IP);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		Mockito.when(operationsPayloadService.isEdgeAvailable(Mockito.any())).thenReturn(Boolean.FALSE);
		assertThrows(SdwanResponseException.class, ()->{
			lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
        });
	}

	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Create")
	public void executeLifecycleCreateTest() throws MessageConversionException {
		executionRequest.setLifecycleName(Constants.LIFECYCLE_CREATE);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		ExecutionAcceptedResponse result = lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
		Assertions.assertNotNull(result);
	}

	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Delete - with Id String")
	public void executeLifecycleDeleteTestWithIdString() throws MessageConversionException {
		resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
		resourceProperties.put("activationKey", new GenericExecutionRequestPropertyValue("123"));		
		executionRequest.setResourceProperties(resourceProperties);
		executionRequest.setLifecycleName(Constants.LIFECYCLE_DELETE);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		Mockito.when(operationsPayloadService.isEdgeAvailable(Mockito.any())).thenReturn(Boolean.TRUE);
		ExecutionAcceptedResponse result = lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
		Assertions.assertNotNull(result);
	}
	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Delete - with Id Int")
	public void executeLifecycleDeleteTestWithIdInt() throws MessageConversionException {
		resourceProperties.put("id", new GenericExecutionRequestPropertyValue(123));
		resourceProperties.put("activationKey", new GenericExecutionRequestPropertyValue("123"));
		executionRequest.setResourceProperties(resourceProperties);
		executionRequest.setLifecycleName(Constants.LIFECYCLE_DELETE);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		Mockito.when(operationsPayloadService.isEdgeAvailable(Mockito.any())).thenReturn(Boolean.TRUE);
		ExecutionAcceptedResponse result = lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
		Assertions.assertNotNull(result);
	}
	
	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Delete - with activationKey empty")
	public void executeLifecycleDeleteTestWithactivationKeyEmpty() throws MessageConversionException {
		resourceProperties.put("id", new GenericExecutionRequestPropertyValue(123));
		resourceProperties.put("activationKey", new GenericExecutionRequestPropertyValue(""));
		executionRequest.setResourceProperties(resourceProperties);
		executionRequest.setLifecycleName(Constants.LIFECYCLE_DELETE);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		Mockito.when(operationsPayloadService.isEdgeAvailable(Mockito.any())).thenReturn(Boolean.TRUE);
		assertThrows(MissingPropertyException.class, ()->{
			lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
        });
	}
	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Delete - with activationKey null")
	public void executeLifecycleDeleteTestWithactivationKeyNull() throws MessageConversionException {
		resourceProperties.put("id", new GenericExecutionRequestPropertyValue(123));
		executionRequest.setResourceProperties(resourceProperties);
		executionRequest.setLifecycleName(Constants.LIFECYCLE_DELETE);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		Mockito.when(operationsPayloadService.isEdgeAvailable(Mockito.any())).thenReturn(Boolean.TRUE);
		assertThrows(MissingPropertyException.class, ()->{
			lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
        });
	}
	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Delete - with isEdgeAvailable false")
	public void executeLifecycleDeleteTestWithIsEdgeAvailableFalse() throws MessageConversionException {
		executionRequest.setLifecycleName(Constants.LIFECYCLE_DELETE);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		Mockito.when(operationsPayloadService.isEdgeAvailable(Mockito.any())).thenReturn(Boolean.FALSE);
		assertThrows(SdwanResponseException.class, ()->{
			lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
        });
		
	}
	@Test
	@DisplayName("Testing positive scenario for Execute Lifecycle Delete Default")
	public void executeLifecycleDeleteDefaultTest() throws MessageConversionException {
		executionRequest.setLifecycleName(Constants.NOT_READY);
		Mockito.when(operationsPayloadService.buildPayloadForAddVlan(Mockito.any(), Mockito.anyString())).thenReturn("");
		Mockito.when(sdwanDriver.execute(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(),Mockito.anyString())).thenReturn("");
		assertThrows(IllegalArgumentException.class, ()->{
			lifecycleManagementService.executeLifecycle(executionRequest,tenantId);
        });
	}
}