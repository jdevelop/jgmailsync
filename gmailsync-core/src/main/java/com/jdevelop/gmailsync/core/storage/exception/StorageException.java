package com.jdevelop.gmailsync.core.storage.exception;

public class StorageException extends Exception {

    private static final long serialVersionUID = -6221625616640961480L;

    public StorageException() {
        super();
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }

}
