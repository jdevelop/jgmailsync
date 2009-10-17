package com.jdevelop.gmailsync.core.transport.exception;

public abstract class TransportException extends Exception {

    protected TransportException() {
        super();
    }

    protected TransportException(String message, Throwable cause) {
        super(message, cause);
    }

    protected TransportException(String message) {
        super(message);
    }

    protected TransportException(Throwable cause) {
        super(cause);
    }

}
