package com.jdevelop.gmailsync.core.transport.exception;

public class MessageTransferException extends TransportException {

    public MessageTransferException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageTransferException(String message) {
        super(message);
    }

    public MessageTransferException(Throwable cause) {
        super(cause);
    }

}
