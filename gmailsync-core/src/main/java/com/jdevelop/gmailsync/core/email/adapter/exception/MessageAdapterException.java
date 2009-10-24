package com.jdevelop.gmailsync.core.email.adapter.exception;

public class MessageAdapterException extends Exception {

    public MessageAdapterException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageAdapterException(Throwable cause) {
        super(cause);
    }

}
