package com.ibm.sdwan.velocloud.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ibm.sdwan.velocloud.model.velocloud.MessageDirection;
import com.ibm.sdwan.velocloud.model.velocloud.MessageType;
import com.ibm.sdwan.velocloud.utils.LogUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ibm.sdwan.velocloud.config.SDWDriverConstants.RC_SERVER_URL;
import static com.ibm.sdwan.velocloud.utils.Constants.*;

@Component
public class SDWanDriver {
    private final static Logger logger = LoggerFactory.getLogger(SDWanDriver.class);

    public static final String API_CONTEXT = "apiContext";
    public static final String API_AUTH_TOKEN = "apiAuthToken";
    private static final String API_CREATE_ENDPOINT = "/edge/edgeProvision";
    private static final String API_DELETE_ENDPOINT = "/edge/deleteEdge";
    private static final String API_UPDATE_ENDPOINT = "/configuration/updateConfigurationModule";
    private static final String API_GET_EDGE_CONFIG_ENDPOINT = "/edge/getEdgeConfigurationStack";
    private static final String API_GET_EDGE = "/edge/getEdge";


    private final RestTemplate restTemplate;


    @Autowired
    public SDWanDriver(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getEdgeConfigurationStack(Map<String, Object> deploymentLocationProperties,  String payload, String driverRequestId){
        String apiContext = (String)deploymentLocationProperties.get(API_CONTEXT);
        final String url = deploymentLocationProperties.get(RC_SERVER_URL) + apiContext + API_GET_EDGE_CONFIG_ENDPOINT;
        logger.debug("url = {}", url);
        return executeAPI(url, deploymentLocationProperties, payload, driverRequestId);
    }

    public String execute(String lifecycleName, Map<String, Object> deploymentLocationProperties,  String payload, String driverRequestId){
        final String url = getURL(lifecycleName, deploymentLocationProperties);
        logger.debug("url = {}", url);
        return executeAPI(url, deploymentLocationProperties, payload, driverRequestId);
    }

    private String executeAPI(String url, Map<String, Object> deploymentLocationProperties, String payload, String driverRequestId) {
        String apiAuthToken = (String)deploymentLocationProperties.get(API_AUTH_TOKEN);
        final HttpHeaders headers = getHttpHeaders(apiAuthToken);
        final HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
        UUID uuid = UUID.randomUUID();
        LogUtils.logEnabledMDC(payload, MessageType.REQUEST, MessageDirection.SENT, uuid.toString(),
                MediaType.APPLICATION_JSON_VALUE, PROTOCOL_TYPE, getRequestProtocolMetaData(url), driverRequestId);
        final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                String.class);
        LogUtils.logEnabledMDC(responseEntity.getBody(), MessageType.RESPONSE, MessageDirection.RECEIVED,
                uuid.toString(), MediaType.APPLICATION_JSON_VALUE, PROTOCOL_TYPE,
                getProtocolMetaData(url, responseEntity), driverRequestId);
        checkResponseEntityMatches(responseEntity, HttpStatus.OK, true);
        return responseEntity.getBody();
    }

    private String getURL(String lifecycleName, Map<String, Object> deploymentLocationProperties) {
        String apiContext = (String)deploymentLocationProperties.get(API_CONTEXT);
        switch (lifecycleName){
            case LIFECYCLE_CREATE:
                return deploymentLocationProperties.get(RC_SERVER_URL) + apiContext + API_CREATE_ENDPOINT;
            case LIFECYCLE_DELETE:
                return deploymentLocationProperties.get(RC_SERVER_URL) + apiContext + API_DELETE_ENDPOINT;
            case LIFECYCLE_OPERATION_ADD_VLAN:
            case LIFECYCLE_OPERATION_ADD_VCE_STATIC_IP:
            case LIFECYCLE_OPERATION_DELETE_VCE_STATIC_IP:
                //  For addvlan, addVceStaticIP and deleteVceStaticRoutingForCIDR operations, same velocloud orchestrator API endpoint is used.
                return deploymentLocationProperties.get(RC_SERVER_URL) + apiContext + API_UPDATE_ENDPOINT;
            default:
                throw new IllegalArgumentException(String.format("Requested transition [%s] is not supported by this lifecycle driver", lifecycleName));
        }
    }

    /**
     * Creates HTTP headers, populating the content type (as application/json and Authorization if provided)
     * @param apiAuthToken Authorization token is added if provided
     * @return HttpHeaders
     */
    private HttpHeaders getHttpHeaders(String apiAuthToken) throws SdwanResponseException {
        final HttpHeaders headers = new HttpHeaders();
        if(StringUtils.hasLength(apiAuthToken)) {
            headers.set("Authorization", "Token " + apiAuthToken);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    /**
     * Utility method that checks if the HTTP status code matches the expected value and that it contains a response body (if desired)
     *
     * @param responseEntity       response to check
     * @param expectedStatusCode   HTTP status code to check against
     * @param containsResponseBody whether the response should contain a body
     */
    private void checkResponseEntityMatches(final ResponseEntity<String> responseEntity, final HttpStatus expectedStatusCode, final boolean containsResponseBody) throws SdwanResponseException {
        // Check response code matches expected value (log a warning if incorrect 2xx status seen)
        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getStatusCode() != expectedStatusCode) {
            // Be lenient on 2xx response codes
            logger.warn("Invalid status code [{}] received, was expecting [{}]", responseEntity.getStatusCode(), expectedStatusCode);
        } else if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new SdwanResponseException(String.format("Invalid status code [%s] received", responseEntity.getStatusCode()));
        }
        // Check if the response body is populated (or not) as expected
        if (containsResponseBody && responseEntity.getBody() == null) {
            throw new SdwanResponseException("No response body");
        } else if (!containsResponseBody && responseEntity.getBody() != null) {
            throw new SdwanResponseException("No response body expected");
        }
    }


    public String execute(String lifecycleName, Map<String, Object> deploymentLocationProperties,  String payload, String veloCloudOpertion, String driverRequestId) {
        final String url = getURL(lifecycleName, deploymentLocationProperties, veloCloudOpertion);
        logger.debug("url = {}", url);
        return executeAPI(url, deploymentLocationProperties, payload, driverRequestId);
    }

    private String getURL(String lifecycleName, Map<String, Object> deploymentLocationProperties, String veloCloudOpertion) {
        String apiContext = (String)deploymentLocationProperties.get(API_CONTEXT);
        switch (lifecycleName){
            case LIFECYCLE_DELETE:
                switch(veloCloudOpertion){
                    case GET_EDGE:
                         logger.debug("Exceuting "+veloCloudOpertion+" api call");
                         return deploymentLocationProperties.get(RC_SERVER_URL) + apiContext + API_GET_EDGE;
                    case DELETE_EDGE:
                         logger.debug("Exceuting "+veloCloudOpertion+" api call");
                         return deploymentLocationProperties.get(RC_SERVER_URL) + apiContext + API_DELETE_ENDPOINT;           
                }
            default:
                throw new IllegalArgumentException(String.format("Requested veloCloudOpertion for [%s] is not supported by this lifecycle driver", lifecycleName));
        }
    }

    public Boolean getEdgeExecuteAPI(Map<String, Object> deploymentLocationProperties, String payload) {
        String apiContext = (String) deploymentLocationProperties.get(API_CONTEXT);
        final String url = deploymentLocationProperties.get(RC_SERVER_URL) + apiContext + API_GET_EDGE;
        logger.debug("url = {}", url);
        String apiAuthToken = (String) deploymentLocationProperties.get(API_AUTH_TOKEN);
        final HttpHeaders headers = getHttpHeaders(apiAuthToken);
        final HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
        try {
            final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                    String.class);
        } catch (HttpClientErrorException e) {
            logger.info("Error message: " +e.getMessage());
            logger.info("Status Code: " + e.getStatusCode());
            if (e.getStatusCode().is4xxClientError()) {
                if (e.getMessage().contains("edge not found")) {
                    return false;
                }
            }
        }
        return true;
    }

    Map<String,Object> getProtocolMetaData(String url,ResponseEntity responseEntity){

        Map<String,Object> protocolMetadata=new HashMap<>();

        protocolMetadata.put("status",responseEntity.getStatusCode());
        protocolMetadata.put("status_code",responseEntity.getStatusCodeValue());
        protocolMetadata.put("url",url);

        return protocolMetadata;

    }

    Map<String,Object> getRequestProtocolMetaData(String url){

        Map<String,Object> protocolMetadata=new HashMap<>();
        protocolMetadata.put("url",url);
        return protocolMetadata;
    }
}
