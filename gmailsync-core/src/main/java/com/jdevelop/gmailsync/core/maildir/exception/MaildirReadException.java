package com.jdevelop.gmailsync.core.maildir.exception;

public class MaildirReadException extends Exception {

    public MaildirReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaildirReadException(String message) {
        super(message);
    }

    public MaildirReadException(Throwable cause) {
        super(cause);
    }

}
