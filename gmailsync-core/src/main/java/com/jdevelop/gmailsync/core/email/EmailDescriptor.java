package com.jdevelop.gmailsync.core.email;

import java.io.Serializable;

public class EmailDescriptor implements Serializable {

    protected final String messageId;

    protected EmailDescriptor(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((messageId == null) ? 0 : messageId.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmailDescriptor other = (EmailDescriptor) obj;
        if (messageId == null) {
            if (other.messageId != null)
                return false;
        } else if (!messageId.equals(other.messageId))
            return false;
        return true;
    }

}
