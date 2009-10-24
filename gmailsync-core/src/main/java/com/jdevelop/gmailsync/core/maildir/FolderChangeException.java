package com.jdevelop.gmailsync.core.maildir;

public class FolderChangeException extends Exception {

    public FolderChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FolderChangeException(Throwable cause) {
        super(cause);
    }

}
