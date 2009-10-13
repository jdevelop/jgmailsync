package com.jdevelop.gmailsync.core.transport;

import com.jdevelop.gmailsync.core.email.EmailMessage;

/**
 * Declares methods to be used for uploading an Email message
 * to the server
 */
public interface TransportInterface {

    void uploadMessage(EmailMessage message);

}
