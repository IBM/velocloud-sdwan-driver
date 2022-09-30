package com.ibm.sdwan.velocloud.utils;

import java.util.Map;

import org.springframework.util.StringUtils;

import com.ibm.sdwan.velocloud.service.MissingPropertyException;

import static com.ibm.sdwan.velocloud.config.SDWDriverConstants.RC_SERVER_URL;
import static com.ibm.sdwan.velocloud.driver.SDWanDriver.*;

public class ValidationUtils {

    public static void validateDeploymentProperties(Map<String, Object> deploymentLocationProperties) {
        if(deploymentLocationProperties==null || deploymentLocationProperties.isEmpty())
        {
            throw new MissingPropertyException(" deploymentLocation property is mandatory and it is missing.");
        }
        if (deploymentLocationProperties.get(API_CONTEXT) instanceof String) {
            if (!StringUtils.hasLength((String) deploymentLocationProperties.get(API_CONTEXT))) {
                throw new MissingPropertyException(" apiContext property is mandatory and it is missing in deploymentLocation");
            }
        } else if (deploymentLocationProperties.get(API_CONTEXT) == null) {
            throw new MissingPropertyException(" apiContext property is mandatory and it is missing in deploymentLocation");
        }

        if (deploymentLocationProperties.get(RC_SERVER_URL) instanceof String) {
            if (!StringUtils.hasLength((String) deploymentLocationProperties.get(RC_SERVER_URL))) {
                throw new MissingPropertyException(" SDWANServerUrl property is mandatory and it is missing in deploymentLocation");
            }
        } else if (deploymentLocationProperties.get(RC_SERVER_URL) == null) {
            throw new MissingPropertyException(" SDWANServerUrl property is mandatory and it is missing in deploymentLocation");
        }

        if (deploymentLocationProperties.get("enterpriseId") instanceof String) {
            if (!StringUtils.hasLength((String) deploymentLocationProperties.get("enterpriseId"))) {
                throw new MissingPropertyException(" enterpriseId property is mandatory and it is missing in deploymentLocation");
            }
        } else if (deploymentLocationProperties.get("enterpriseId") == null) {
            throw new MissingPropertyException(" enterpriseId property is mandatory and it is missing in deploymentLocation");
        }

        if (deploymentLocationProperties.get(API_AUTH_TOKEN) instanceof String) {
            if (!StringUtils.hasLength((String) deploymentLocationProperties.get(API_AUTH_TOKEN))) {
                throw new MissingPropertyException(" apiAuthToken property is mandatory and it is missing in deploymentLocation");
            }
        } else if (deploymentLocationProperties.get(API_AUTH_TOKEN) == null) {
            throw new MissingPropertyException(" apiAuthToken property is mandatory and it is missing in deploymentLocation");
        }

    }

}
