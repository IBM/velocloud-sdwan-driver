package com.ibm.sdwan.velocloud.model.velocloud;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class GetEdgeRequest {

    private  int enterpriseId;
    private  int Id;
    private  String activationKey;
 
    
}