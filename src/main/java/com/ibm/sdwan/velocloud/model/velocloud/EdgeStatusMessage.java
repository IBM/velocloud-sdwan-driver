package com.ibm.sdwan.velocloud.model.velocloud;

import java.util.Map;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
public class EdgeStatusMessage {
    private PollingEdgeStatus statusMessage;
    private String requestId;
    private Map<String, Object> deploymentLocationProperties;
    private int id;
    private String activationKey;
    private String deletePayload;
    private String tenantId;

}
