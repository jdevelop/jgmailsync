package com.jdevelop.gmailsync.core.email;

import java.io.File;

/**
 * Holds reference to the file or email
 */
public class EmailMessage {

    private final File emailFile;

    public EmailMessage(File emailFile) {
        super();
        this.emailFile = emailFile;
    }

    public File getEmailFile() {
        return emailFile;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((emailFile == null) ? 0 : emailFile.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmailMessage other = (EmailMessage) obj;
        if (emailFile == null) {
            if (other.emailFile != null)
                return false;
        } else if (!emailFile.equals(other.emailFile))
            return false;
        return true;
    }

}
