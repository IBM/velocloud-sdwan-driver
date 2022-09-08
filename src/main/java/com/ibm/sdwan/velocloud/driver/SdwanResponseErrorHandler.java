package com.ibm.sdwan.velocloud.driver;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class SdwanResponseErrorHandler extends DefaultResponseErrorHandler {
    
    private final static Logger logger = LoggerFactory.getLogger(SdwanResponseErrorHandler.class);
    private final ObjectMapper objectMapper;

    @Autowired
    public SdwanResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        try {
            super.handleError(clientHttpResponse);
        } catch (RestClientResponseException e) {
            logger.error("Received sdwan-compliant error when communicating with " +endpointDescription()+" : "+e);
            // First, check that the response contains JSON
            if (e.getResponseHeaders() != null && e.getResponseHeaders().getContentType() != null && e.getResponseHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)) {
                throw new SdwanResponseException(String.format("Received sdwan-compliant error when communicating with %s: %s", endpointDescription()), e);               
            }
            // Else, attempt to extract information out of the error response (as best as possible)
            final String responseBody = e.getResponseBodyAsString();
            String detailsMessage = e.getStatusText();
            if (StringUtils.hasLength(responseBody)) {
                detailsMessage += ": " + responseBody;
            }
            logger.error("detailedMessage: " +detailsMessage);
            throw new SdwanResponseException(String.format("Caught REST client exception when communicating with %s", endpointDescription()), e);
        } catch (Exception e) {
            logger.error("Caught general exception when communicating with " +endpointDescription()+" : "+e);
            throw new SdwanResponseException(String.format("Caught general exception when communicating with %s", endpointDescription()), e);
        }
    }

    protected abstract String endpointDescription();

}