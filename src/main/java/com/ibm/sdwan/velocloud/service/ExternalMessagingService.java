package com.ibm.sdwan.velocloud.service;

import com.ibm.sdwan.velocloud.model.LcmOpOccPollingRequest;
import com.ibm.sdwan.velocloud.model.alm.ExecutionAsyncResponse;

import java.time.Duration;


public interface ExternalMessagingService {

    void sendExecutionAsyncResponse(ExecutionAsyncResponse request, String tenantId);

    void sendDelayedExecutionAsyncResponse(ExecutionAsyncResponse request, String tenantId, Duration delay);

    void sendLcmOpOccPollingRequest(LcmOpOccPollingRequest request);

}