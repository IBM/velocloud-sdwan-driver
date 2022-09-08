package com.ibm.sdwan.velocloud.driver;

import com.ibm.sdwan.velocloud.model.ProblemDetails;
import org.springframework.web.client.RestClientException;


public class SdwanResponseException extends RestClientException {

    public static final int DEFAULT_STATUS_VALUE = 0;

    private final ProblemDetails problemDetails;

    public SdwanResponseException(String msg) {
        this(msg, new ProblemDetails(DEFAULT_STATUS_VALUE, msg));
    }

    public SdwanResponseException(Throwable ex) {
        this(ex.getLocalizedMessage(), ex, new ProblemDetails(DEFAULT_STATUS_VALUE, ex.getLocalizedMessage()));
    }

    public SdwanResponseException(String msg, Throwable ex) {
        this(msg, ex, new ProblemDetails(DEFAULT_STATUS_VALUE, String.format("%s: %s", msg, ex.getLocalizedMessage())));
    }

    public SdwanResponseException(String msg, ProblemDetails problemDetails) {
        super(msg);
        this.problemDetails = problemDetails;
    }

    public SdwanResponseException(String msg, Throwable ex, ProblemDetails problemDetails) {
        super(msg, ex);
        this.problemDetails = problemDetails;
    }

    public ProblemDetails getProblemDetails() {
        return problemDetails;
    }

}