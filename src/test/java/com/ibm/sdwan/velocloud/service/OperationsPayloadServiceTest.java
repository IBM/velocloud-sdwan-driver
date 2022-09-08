package com.ibm.sdwan.velocloud.service;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.sdwan.velocloud.driver.SDWanDriver;
import com.ibm.sdwan.velocloud.model.ExecutionRequest;
import com.ibm.sdwan.velocloud.model.ExecutionRequestPropertyValue;
import com.ibm.sdwan.velocloud.model.GenericExecutionRequestPropertyValue;
import com.ibm.sdwan.velocloud.model.ResourceManagerDeploymentLocation;
import com.ibm.sdwan.velocloud.model.velocloud.EdgeStatusMessage;
import com.ibm.sdwan.velocloud.service.impl.PollingEdgeStatusServiceListener;

@SpringBootTest
public class OperationsPayloadServiceTest {

    @Mock
    private SDWanDriver sdwanDriver;
    @Autowired
    private ObjectMapper mapper;

    private OperationsPayloadService operationsPayloadService; // under test
    private ExecutionRequest executionRequest;
    @Mock
    private  EdgeStatusMessage edgeStatusMessage;
    @Mock
    private PollingEdgeStatusServiceListener pollingEdgeStatusServiceListener;
    @Mock
    private  MessageConversionService messageConversionService;
    private Map<String, ExecutionRequestPropertyValue> resourceProperties;
    
    Map<String, Object> dummyDepLocProperties;

    @BeforeEach
    public void setUp() {
        operationsPayloadService = new OperationsPayloadService(sdwanDriver, mapper);
        executionRequest = new ExecutionRequest();
        executionRequest.setLifecycleName("Create");
        resourceProperties = new HashMap<>();
        dummyDepLocProperties = new HashMap<>();
        dummyDepLocProperties.put("dummy", "dummy");
        ResourceManagerDeploymentLocation deploymentLocation = new ResourceManagerDeploymentLocation();
        deploymentLocation.setProperties(dummyDepLocProperties);
        executionRequest.setDeploymentLocation(deploymentLocation);
    }

    @Test
    @DisplayName("Testing positive scenario for creating payload for addvlan")
    public void testPayloadforAddVlan() throws MessageConversionException{
        // pass only the mandatory properties
        resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        resourceProperties.put("vce_private_subnet_ip", new GenericExecutionRequestPropertyValue("192.11.11.11"));
        resourceProperties.put("private_subnet_cidr", new GenericExecutionRequestPropertyValue("192.11.11.11/24"));
        resourceProperties.put("static_route_cidr", new GenericExecutionRequestPropertyValue("10.0.0.1/24"));
        executionRequest.setResourceProperties(resourceProperties);

        String expected = "{\"returnData\":true,\"_update\":{\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"lan\":{\"networks\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"dhcp\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"enabled\":false},\"cidrIp\":\"192.11.11.11\",\"advertise\":true,\"cidrPrefix\":24,\"netmask\":\"255.255.255.0\"}]}}},\"name\":\"deviceSettings\",\"id\":456}\n";
        when(sdwanDriver.getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null)).thenReturn(
                "[{\"configurationType\":\"test\",\"bastionState\":\"tests\",\"created\":\"das\",\"description\":\"dasd\",\"edgeCount\":1,\"effective\":\"sads\",\"id\":123,\"logicalId\":\"rwe\",\"modified\":\"fdw\",\"name\":\"eert\",\"schemaVersion\":\"v1\",\"version\":\"a1\",\"isStaging\":345,\"modules\":[{\"id\":456,\"name\":\"deviceSettings\",\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"lan\":{\"networks\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"dhcp\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\"}}]}}}]}]");

        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        String payload = operationsPayloadService.buildPayloadForAddVlan(executionRequest, null);
        Assertions.assertEquals(payload.trim(), expected.trim());
        verify(sdwanDriver, times(0)).execute(anyString(), anyMap(), anyString(),anyString());
    }

    @Test
    @DisplayName("Testing exception scenario for creating payload for addvlan - missing property id")
    public void testThrowsExceptionforAddVlanIfMissingProperty_id() {
        //resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        resourceProperties.put("vce_private_subnet_ip", new GenericExecutionRequestPropertyValue("192.11.11.11"));
        resourceProperties.put("private_subnet_cidr", new GenericExecutionRequestPropertyValue("192.11.11.11/24"));
        executionRequest.setResourceProperties(resourceProperties);

        when(sdwanDriver.getEdgeConfigurationStack(anyMap(), anyString(),anyString())).thenReturn(
                "[{\"configurationType\":\"test\",\"bastionState\":\"tests\",\"created\":\"das\",\"description\":\"dasd\",\"edgeCount\":1,\"effective\":\"sads\",\"id\":123,\"logicalId\":\"rwe\",\"modified\":\"fdw\",\"name\":\"eert\",\"schemaVersion\":\"v1\",\"version\":\"a1\",\"isStaging\":345,\"modules\":[{\"id\":456,\"name\":\"deviceSettings\",\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"lan\":{\"networks\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"dhcp\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\"}}]}}}]}]");

        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        Assertions.assertThrows(MissingPropertyException.class, ()->operationsPayloadService.buildPayloadForAddVlan(executionRequest, null));
    }

    @Test
    @DisplayName("Testing exception scenario for creating payload for addvlan - missing property private_subnet_cidr")
    public void testThrowsExceptionforAddVlanIfMissingProperty_vce_private_subnet_ip()  {
        resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        //resourceProperties.put("vce_private_subnet_ip", new GenericExecutionRequestPropertyValue("192.11.11.11"));
        resourceProperties.put("private_subnet_cidr", new GenericExecutionRequestPropertyValue("192.11.11.11/24"));
        executionRequest.setResourceProperties(resourceProperties);

        when(sdwanDriver.getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null)).thenReturn(
                "[{\"configurationType\":\"test\",\"bastionState\":\"tests\",\"created\":\"das\",\"description\":\"dasd\",\"edgeCount\":1,\"effective\":\"sads\",\"id\":123,\"logicalId\":\"rwe\",\"modified\":\"fdw\",\"name\":\"eert\",\"schemaVersion\":\"v1\",\"version\":\"a1\",\"isStaging\":345,\"modules\":[{\"id\":456,\"name\":\"deviceSettings\",\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"lan\":{\"networks\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"dhcp\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\"}}]}}}]}]");

        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        Assertions.assertThrows(MissingPropertyException.class, ()->operationsPayloadService.buildPayloadForAddVlan(executionRequest, null));
    }

    @Test
    @DisplayName("Testing exception scenario for creating payload for addvlan - missing property private_subnet_cidr")
    public void testThrowsExceptionforAddVlanIfMissingProperty_private_subnet_cidr()  {
        resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        resourceProperties.put("vce_private_subnet_ip", new GenericExecutionRequestPropertyValue("192.11.11.11"));
        //resourceProperties.put("private_subnet_cidr", new GenericExecutionRequestPropertyValue("192.11.11.11/24"));
        executionRequest.setResourceProperties(resourceProperties);

        when(sdwanDriver.getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null)).thenReturn(
                "[{\"configurationType\":\"test\",\"bastionState\":\"tests\",\"created\":\"das\",\"description\":\"dasd\",\"edgeCount\":1,\"effective\":\"sads\",\"id\":123,\"logicalId\":\"rwe\",\"modified\":\"fdw\",\"name\":\"eert\",\"schemaVersion\":\"v1\",\"version\":\"a1\",\"isStaging\":345,\"modules\":[{\"id\":456,\"name\":\"deviceSettings\",\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"lan\":{\"networks\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"dhcp\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\"}}]}}}]}]");

        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        Assertions.assertThrows(MissingPropertyException.class, ()->operationsPayloadService.buildPayloadForAddVlan(executionRequest, null));
    }
    
    @Test
    @DisplayName("Testing positive scenario for creating payload for VceStaticIP")
    public void testPayloadforVceStaticIP() throws MessageConversionException{
        // pass only the mandatory properties
        resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        resourceProperties.put("gateway_ip", new GenericExecutionRequestPropertyValue("192.11.11.11/24"));
        resourceProperties.put("static_route_cidr", new GenericExecutionRequestPropertyValue("10.0.0.1/24"));
        executionRequest.setResourceProperties(resourceProperties);

        String expected = "{\"returnData\":true,\"_update\":{\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"segments\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"routes\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"static\":[\"abc\",{\"cost\":0,\"wanInterface\":\"\",\"vlanId\":null,\"subinterfaceId\":-1,\"destination\":\"10.0.0.1\",\"description\":\"\",\"icmpProbeLogicalId\":null,\"cidrPrefix\":\"24\",\"sourceIp\":null,\"netmask\":\"255.255.255.0\",\"gateway\":\"192.11.11.11/24\",\"preferred\":true,\"advertise\":true}]}}]}},\"name\":\"deviceSettings\",\"id\":456}";
        when(sdwanDriver.getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null)).thenReturn(
                "[{\"configurationType\":\"test\",\"bastionState\":\"tests\",\"created\":\"das\",\"description\":\"dasd\",\"edgeCount\":1,\"effective\":\"sads\",\"id\":123,\"logicalId\":\"rwe\",\"modified\":\"fdw\",\"name\":\"eert\",\"schemaVersion\":\"v1\",\"version\":\"a1\",\"isStaging\":345,\"modules\":[{\"id\":456,\"name\":\"deviceSettings\",\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"segments\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"routes\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"static\":[\"abc\"]}}]}}]}]");

        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        String payload = operationsPayloadService.buildPayloadForVceStaticIP(executionRequest, null);
        Assertions.assertEquals(expected.trim(), payload.trim());
        verify(sdwanDriver).getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null);
        verify(sdwanDriver, times(0)).execute(anyString(), anyMap(), anyString(),anyString());
    }
    
    @Test
    @DisplayName("Testing positive scenario for creating payload for DeleteVceStaticIP")
    public void testPayloadforDeleteVceStaticIP() throws MessageConversionException{
        // pass only the mandatory properties
        resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        resourceProperties.put("static_route_cidr", new GenericExecutionRequestPropertyValue("10.0.0.1/24"));
        executionRequest.setResourceProperties(resourceProperties);

        String expected = "{\"returnData\":true,\"_update\":{\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"segments\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"routes\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"static\":[]}}]}},\"name\":\"deviceSettings\",\"id\":456}";
        when(sdwanDriver.getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null)).thenReturn(
                "[{\"configurationType\":\"test\",\"bastionState\":\"tests\",\"created\":\"das\",\"description\":\"dasd\",\"edgeCount\":1,\"effective\":\"sads\",\"id\":123,\"logicalId\":\"rwe\",\"modified\":\"fdw\",\"name\":\"eert\",\"schemaVersion\":\"v1\",\"version\":\"a1\",\"isStaging\":345,\"modules\":[{\"id\":456,\"name\":\"deviceSettings\",\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"segments\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"routes\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"static\":[{\"destination\":\"10.0.0.1\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\"}]}}]}}]}]");

        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        String payload = operationsPayloadService.buildPayloadForDeleteVceStaticIP(executionRequest, null);
        Assertions.assertEquals(payload.trim(), expected.trim());
        verify(sdwanDriver, times(0)).execute(anyString(), anyMap(), anyString(),anyString());
    }

    @Test
    @DisplayName("Testing exception scenario for creating payload for VceStaticIP - missing property deviceSettings")
    public void testThrowsExceptionforVceStaticIPIfMissingProperty_device_settings()  {
    	 // pass only the mandatory properties
        resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        executionRequest.setResourceProperties(resourceProperties);

        when(sdwanDriver.getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null)).thenReturn(
                "[{\"configurationType\":\"test\",\"bastionState\":\"tests\",\"created\":\"das\",\"description\":\"dasd\",\"edgeCount\":1,\"effective\":\"sads\",\"id\":123,\"logicalId\":\"rwe\",\"modified\":\"fdw\",\"name\":\"eert\",\"schemaVersion\":\"v1\",\"version\":\"a1\",\"isStaging\":345,\"modules\":[{\"id\":456,\"name\":\"device\",\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"segments\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"routes\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"static\":[\"abc\"]}}]}}]}]");

        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        Assertions.assertThrows(MessageConversionException.class, ()->operationsPayloadService.buildPayloadForVceStaticIP(executionRequest, null));
    }
    
    @Test
    @DisplayName("Testing exception scenario for creating payload for VceStaticIP - missing property edge config")
    public void testThrowsExceptionforVceStaticIPIfMissingProperty_edge_config_null()  {
    	 // pass only the mandatory properties
        resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        executionRequest.setResourceProperties(resourceProperties);

        when(sdwanDriver.getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null)).thenReturn("test");
 
        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        Assertions.assertThrows(MessageConversionException.class, ()->operationsPayloadService.buildPayloadForVceStaticIP(executionRequest, null));
    }
    @Test
    @DisplayName("Testing exception scenario for creating payload for VceStaticIP - missing property id")
    public void testThrowsExceptionforVceStaticIPIfMissingProperty_id() {
        executionRequest.setResourceProperties(resourceProperties);
        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        Assertions.assertThrows(MissingPropertyException.class, ()->operationsPayloadService.buildPayloadForVceStaticIP(executionRequest, null));
    }
	
    @Test
    @DisplayName("Testing exception scenario for creating payload for VceStaticIP - missing property static_route_cidr")
    public void testThrowsExceptionforVceStaticIPIfMissingProperty_static_route_cidr()  {
    	 // pass only the mandatory properties
        resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        resourceProperties.put("gateway_ip", new GenericExecutionRequestPropertyValue("192.11.11.11/24"));
        executionRequest.setResourceProperties(resourceProperties);

        when(sdwanDriver.getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null)).thenReturn(
                "[{\"configurationType\":\"test\",\"bastionState\":\"tests\",\"created\":\"das\",\"description\":\"dasd\",\"edgeCount\":1,\"effective\":\"sads\",\"id\":123,\"logicalId\":\"rwe\",\"modified\":\"fdw\",\"name\":\"eert\",\"schemaVersion\":\"v1\",\"version\":\"a1\",\"isStaging\":345,\"modules\":[{\"id\":456,\"name\":\"deviceSettings\",\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"segments\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"routes\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"static\":[\"abc\"]}}]}}]}]");

        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        Assertions.assertThrows(MissingPropertyException.class, ()->operationsPayloadService.buildPayloadForVceStaticIP(executionRequest, null));
    }
    @Test
    @DisplayName("Testing exception scenario for creating payload for VceStaticIP - missing property gateway_ip")
    public void testThrowsExceptionforVceStaticIPIfMissingProperty_gateway_ip()  {
    	 // pass only the mandatory properties
        resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        resourceProperties.put("static_route_cidr", new GenericExecutionRequestPropertyValue("10.0.0.1/24"));
        executionRequest.setResourceProperties(resourceProperties);

        when(sdwanDriver.getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null)).thenReturn(
                "[{\"configurationType\":\"test\",\"bastionState\":\"tests\",\"created\":\"das\",\"description\":\"dasd\",\"edgeCount\":1,\"effective\":\"sads\",\"id\":123,\"logicalId\":\"rwe\",\"modified\":\"fdw\",\"name\":\"eert\",\"schemaVersion\":\"v1\",\"version\":\"a1\",\"isStaging\":345,\"modules\":[{\"id\":456,\"name\":\"deviceSettings\",\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"segments\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"routes\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"static\":[\"abc\"]}}]}}]}]");

        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        Assertions.assertThrows(MissingPropertyException.class, ()->operationsPayloadService.buildPayloadForVceStaticIP(executionRequest, null));
    }
    @Test
    @DisplayName("Testing exception scenario for creating payload for DeleteVceStaticIP - missing property static_route_cidr")
    public void testPayloadforDeleteVceStaticIPIfMissingProperty_static_route_cidr() throws MessageConversionException{
        // pass only the mandatory properties
        resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        executionRequest.setResourceProperties(resourceProperties);

        when(sdwanDriver.getEdgeConfigurationStack(dummyDepLocProperties, "{\"edgeId\":123}",null)).thenReturn(
                "[{\"configurationType\":\"test\",\"bastionState\":\"tests\",\"created\":\"das\",\"description\":\"dasd\",\"edgeCount\":1,\"effective\":\"sads\",\"id\":123,\"logicalId\":\"rwe\",\"modified\":\"fdw\",\"name\":\"eert\",\"schemaVersion\":\"v1\",\"version\":\"a1\",\"isStaging\":345,\"modules\":[{\"id\":456,\"name\":\"deviceSettings\",\"data\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"segments\":[{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"Raina\":\"82\",\"routes\":{\"Kohli\":\"120\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\",\"static\":[{\"destination\":\"10.0.0.1\",\"Yuvraj\":\"94\",\"Dhoni\":\"102\"}]}}]}}]}]");

        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        Assertions.assertThrows(MissingPropertyException.class, ()->operationsPayloadService.buildPayloadForDeleteVceStaticIP(executionRequest, null));
    }
    
    @Test
    @DisplayName("Testing exception scenario for creating payload for DeleteVceStaticIP - missing property id")
    public void testThrowsExceptionforDeleteVceStaticIPIfMissingProperty_id() {
        executionRequest.setResourceProperties(resourceProperties);
        Assertions.assertNotNull(sdwanDriver);
        Assertions.assertNotNull(operationsPayloadService);
        Assertions.assertThrows(MissingPropertyException.class, ()->operationsPayloadService.buildPayloadForDeleteVceStaticIP(executionRequest, null));
    }
    
    @Test
    @DisplayName("Testing positive scenario for IsEdgeAvailable - ID as String")
    public void testIsEdgeAvailableIdAsString() throws MessageConversionException {
    	resourceProperties.put("id", new GenericExecutionRequestPropertyValue("123"));
        resourceProperties.put("activationKey", new GenericExecutionRequestPropertyValue("10.0.0.1/24"));
        executionRequest.setResourceProperties(resourceProperties);
        executionRequest.getDeploymentLocation().getProperties().put("enterpriseId", 1);
        Mockito.when(pollingEdgeStatusServiceListener.buildPayloadForGetEdge(Mockito.anyMap(),Mockito.anyInt(),
        		Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("test");
    	Boolean result = operationsPayloadService.isEdgeAvailable(executionRequest);
    	Assertions.assertFalse(result);
    }
    
    @Test
    @DisplayName("Testing exception scenario for IsEdgeAvailable - Id as Int")
    public void testIsEdgeAvailableIdAsInt() throws MessageConversionException {
    	resourceProperties.put("id", new GenericExecutionRequestPropertyValue(123));
        resourceProperties.put("activationKey", new GenericExecutionRequestPropertyValue("10.0.0.1/24"));
        executionRequest.setResourceProperties(resourceProperties);
        executionRequest.getDeploymentLocation().getProperties().put("enterpriseId", 1);
        Mockito.when(pollingEdgeStatusServiceListener.buildPayloadForGetEdge(Mockito.anyMap(),Mockito.anyInt(),
        		Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("test");
    	Boolean result = operationsPayloadService.isEdgeAvailable(executionRequest);
    	Assertions.assertFalse(result);
    }
    
}