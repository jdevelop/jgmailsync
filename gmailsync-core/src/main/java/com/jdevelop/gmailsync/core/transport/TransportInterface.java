package com.jdevelop.gmailsync.core.transport;

import com.jdevelop.gmailsync.core.email.EmailMessage;
import com.jdevelop.gmailsync.core.transport.exception.TransportException;

/**
 * Declares methods to be used for uploading an Email message
 * to the server
 */
public interface TransportInterface {

    void uploadMessage(EmailMessage message) throws TransportException;

}
