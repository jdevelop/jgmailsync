package com.jdevelop.gmailsync.cli.facade.exception;

public class FacadeException extends Exception {

    public FacadeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FacadeException(Throwable cause) {
        super(cause);
    }

}
