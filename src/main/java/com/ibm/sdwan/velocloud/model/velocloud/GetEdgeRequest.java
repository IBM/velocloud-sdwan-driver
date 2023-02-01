package com.ibm.sdwan.velocloud.model.velocloud;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetEdgeRequest {

    private  int enterpriseId;
    private  int Id;
    private  String activationKey;
 
    
}