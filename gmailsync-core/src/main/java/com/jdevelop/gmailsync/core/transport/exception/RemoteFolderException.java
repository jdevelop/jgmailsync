package com.jdevelop.gmailsync.core.transport.exception;

public class RemoteFolderException extends TransportException {

    public RemoteFolderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteFolderException(String message) {
        super(message);
    }

    public RemoteFolderException(Throwable cause) {
        super(cause);
    }

}
