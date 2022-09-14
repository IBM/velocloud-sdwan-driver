package com.ibm.sdwan.velocloud.driver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ibm.sdwan.velocloud.config.SDWDriverConstants;
import com.ibm.sdwan.velocloud.utils.Constants;

@SpringBootTest
public class SDWanDriverTest {
	
	private static final String DESCRIPTORS_API_URL = "http://localhost:8080/api/velocloud/edge/getEdgeConfigurationStack";
	
	private SDWanDriver sdWanDriver;
	@Mock
	private RestTemplate restTemplate;
	
	Map<String, Object> deploymentLocationProperties;
	
	@BeforeEach
	public void setUp() {
		sdWanDriver = new SDWanDriver(restTemplate);
		
		deploymentLocationProperties = new HashMap<String, Object>();
		deploymentLocationProperties.put(SDWanDriver.API_CONTEXT, "api/velocloud");
		deploymentLocationProperties.put(SDWDriverConstants.RC_SERVER_URL, "http://localhost:8080/");
		deploymentLocationProperties.put(SDWanDriver.API_AUTH_TOKEN, "token");
	}
	
	@Test
 	@DisplayName("Testing positive scenario to get EdgeConfigurationStack")
	public void getEdgeConfigurationStackTest() {
		
		ResponseEntity<String> response = new ResponseEntity<String>("", HttpStatus.OK);
		ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(restTemplate.exchange(eq(DESCRIPTORS_API_URL), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), 
				eq(String.class))).thenReturn(response);
		sdWanDriver.getEdgeConfigurationStack(deploymentLocationProperties, "", "");
		 verify(restTemplate, times(1)).exchange(eq(DESCRIPTORS_API_URL), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), 
					eq(String.class));
	}
	
	@Test
	@DisplayName("Testing positive scenario to get EdgeExecuteAPI")
	public void getEdgeExecuteAPITest() {
		ResponseEntity<String> response = new ResponseEntity<String>("", HttpStatus.OK);
		ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(restTemplate.exchange(eq(DESCRIPTORS_API_URL), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), 
				eq(String.class))).thenReturn(response);
		Boolean result = sdWanDriver.getEdgeExecuteAPI(deploymentLocationProperties, "");
		assertTrue(result);
	}
	
	@Test
	@DisplayName("Testing negative  scenario for to get EdgeExecuteAPI Exception")
	public void getEdgeExecuteAPITestException() {
		String url = "http://localhost:8080/api/velocloud/edge/getEdge";
		ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), 
				eq(String.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST,"edge not found"));
		Boolean result = sdWanDriver.getEdgeExecuteAPI(deploymentLocationProperties, "");
		assertFalse(result);
	}
	
	@Test
	@DisplayName("Testing scenario to Execute URL for LIFECYCLE_CREATE")
	public void executeURLforLIFECYCLE_CREATE () {
		String url = "http://localhost:8080/api/velocloud/edge/edgeProvision";
		ResponseEntity<String> response = new ResponseEntity<String>("test", HttpStatus.OK);
		ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), 
				eq(String.class))).thenReturn(response);
		String res = sdWanDriver.execute(Constants.LIFECYCLE_CREATE, deploymentLocationProperties, "", "");
		assertNotNull(res);
	}
	
	@Test
	@DisplayName("Testing scenario to execute URL - with Exception")
	public void executeURLforException () {
		String url = "http://localhost:8080/api/velocloud/edge/edgeProvision";
		ResponseEntity<String> response = new ResponseEntity<String>("", HttpStatus.OK);
		ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), 
				eq(String.class))).thenReturn(response);
		assertThrows(IllegalArgumentException.class, ()->{
			sdWanDriver.execute("TEST", deploymentLocationProperties, "", "");
        });
		
	}
	
	@Test
	@DisplayName("Testing  scenario for execute URL for LIFECYCLE_DELETE - with error")
	public void executeURLforLIFECYCLE_DELETEWithError() {
		String url = "http://localhost:8080/api/velocloud/edge/deleteEdge";
		String resp = null;
		ResponseEntity<String> response = new ResponseEntity<String>(resp, HttpStatus.ACCEPTED);
		ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), 
				eq(String.class))).thenReturn(response);
		assertThrows(SdwanResponseException.class, ()->{
			sdWanDriver.execute(Constants.LIFECYCLE_DELETE, deploymentLocationProperties, "", "");
			
        });
		
	}
	
	@Test
	@DisplayName("Testing positive scenario Execute URL for LIFECYCLE_OPERATION_DELETE_VCE_STATIC_IP")
	public void executeURLforLIFECYCLE_OPERATION_DELETE_VCE_STATIC_IP() {
		String url = "http://localhost:8080/api/velocloud/configuration/updateConfigurationModule";
		String resp = null;
		ResponseEntity<String> response = new ResponseEntity<String>(resp, HttpStatus.BAD_REQUEST);
		ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), 
				eq(String.class))).thenReturn(response);
		assertThrows(SdwanResponseException.class, ()->{
			sdWanDriver.execute(Constants.LIFECYCLE_OPERATION_DELETE_VCE_STATIC_IP, deploymentLocationProperties, "", "");
			
        });
		
	}
	
	@Test
	@DisplayName("Testing positive scenario for ExecuteEdge - with exception")
	public void executeEdgeTest_withException() {
		String url = "http://localhost:8080/api/velocloud/edge/getEdge";
		ResponseEntity<String> response = new ResponseEntity<String>("", HttpStatus.OK);
		ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), 
				eq(String.class))).thenReturn(response);
		String res =sdWanDriver.execute(Constants.LIFECYCLE_DELETE, deploymentLocationProperties, "", Constants.GET_EDGE,"");
		assertNotNull(res);
	}

}
