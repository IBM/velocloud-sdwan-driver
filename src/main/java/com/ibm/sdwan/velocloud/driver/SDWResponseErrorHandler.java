package com.ibm.sdwan.velocloud.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component("SDWResponseErrorHandler")
public class SDWResponseErrorHandler extends SdwanResponseErrorHandler {

    @Autowired
    public SDWResponseErrorHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected String endpointDescription() {
        return "VELOCLOUD-SDWAN";
    }

}