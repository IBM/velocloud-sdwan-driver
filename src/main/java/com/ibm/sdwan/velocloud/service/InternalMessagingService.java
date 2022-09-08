package com.ibm.sdwan.velocloud.service;

import com.ibm.sdwan.velocloud.model.velocloud.EdgeStatusMessage;

public interface InternalMessagingService {

    void sendEdgeStatusAsyncResponse(EdgeStatusMessage edgeStatusMessage);

}