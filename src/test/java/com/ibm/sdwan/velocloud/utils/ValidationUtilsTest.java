package com.ibm.sdwan.velocloud.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ibm.sdwan.velocloud.config.SDWDriverConstants;
import com.ibm.sdwan.velocloud.driver.SDWanDriver;
import com.ibm.sdwan.velocloud.service.MissingPropertyException;

public class ValidationUtilsTest {
	
	@Test
	public void validateDeploymentPropertiesTest() {
		Map<String, Object> deploymentLocationProperties = new HashMap<String, Object>();
		deploymentLocationProperties.put(SDWanDriver.API_CONTEXT, null);
		assertThrows(MissingPropertyException.class, () -> {
			ValidationUtils.validateDeploymentProperties(deploymentLocationProperties);
		});
		deploymentLocationProperties.put(SDWanDriver.API_CONTEXT, "");
		assertThrows(MissingPropertyException.class, () -> {
			ValidationUtils.validateDeploymentProperties(deploymentLocationProperties);
		});
		
		deploymentLocationProperties.put(SDWanDriver.API_CONTEXT, "api");
		deploymentLocationProperties.put(SDWDriverConstants.RC_SERVER_URL, "");
		assertThrows(MissingPropertyException.class, () -> {
			ValidationUtils.validateDeploymentProperties(deploymentLocationProperties);
		});
		deploymentLocationProperties.put(SDWDriverConstants.RC_SERVER_URL, null);
		assertThrows(MissingPropertyException.class, () -> {
			ValidationUtils.validateDeploymentProperties(deploymentLocationProperties);
		});
		
		deploymentLocationProperties.put(SDWDriverConstants.RC_SERVER_URL, "rc");
		deploymentLocationProperties.put("enterpriseId", "");
		assertThrows(MissingPropertyException.class, () -> {
			ValidationUtils.validateDeploymentProperties(deploymentLocationProperties);
		});
		deploymentLocationProperties.put("enterpriseId", null);
		assertThrows(MissingPropertyException.class, () -> {
			ValidationUtils.validateDeploymentProperties(deploymentLocationProperties);
		});
		
		deploymentLocationProperties.put("enterpriseId", "enterpriseId");
		deploymentLocationProperties.put(SDWanDriver.API_AUTH_TOKEN, "");
		assertThrows(MissingPropertyException.class, () -> {
			ValidationUtils.validateDeploymentProperties(deploymentLocationProperties);
		});
		deploymentLocationProperties.put(SDWanDriver.API_AUTH_TOKEN, null);
		assertThrows(MissingPropertyException.class, () -> {
			ValidationUtils.validateDeploymentProperties(deploymentLocationProperties);
		});
		
	}

}
