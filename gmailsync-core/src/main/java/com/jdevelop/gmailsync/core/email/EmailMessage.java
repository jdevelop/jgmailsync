package com.jdevelop.gmailsync.core.email;

import javax.mail.Message;

/**
 * Holds reference to the file or email
 */
public class EmailMessage {

    private final Message message;

    public EmailMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

}
