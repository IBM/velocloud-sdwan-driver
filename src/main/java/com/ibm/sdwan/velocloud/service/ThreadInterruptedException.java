package com.ibm.sdwan.velocloud.service;

public class ThreadInterruptedException extends RuntimeException {

    public ThreadInterruptedException() {
    }

    public ThreadInterruptedException(String message) {
        super(message);
    }

    public ThreadInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThreadInterruptedException(Throwable cause) {
        super(cause);
    }

}
