package com.jdevelop.gmailsync.core.email;

public class SimpleEmailDescriptor extends EmailDescriptor {

    protected SimpleEmailDescriptor(String messageId) {
        super(messageId);
    }

    public String getMessageId() {
        return messageId;
    }

}
