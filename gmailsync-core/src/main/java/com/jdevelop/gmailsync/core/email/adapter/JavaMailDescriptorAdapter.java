package com.jdevelop.gmailsync.core.email.adapter;

import javax.mail.Message;
import javax.mail.MessagingException;

import com.jdevelop.gmailsync.core.email.EmailDescriptor;
import com.jdevelop.gmailsync.core.email.adapter.exception.MessageAdapterException;

public class JavaMailDescriptorAdapter implements
        EmailDescriptorAdapterInterface<Message> {

    @Override
    public EmailDescriptor getEmailDescriptor(Message source)
            throws MessageAdapterException {
        String[] header = null;
        try {
            header = source.getHeader("message-id");
        } catch (MessagingException e) {
            throw new MessageAdapterException(e);
        }
        if (header == null || header.length == 0)
            return null;
        return new EmailDescriptor(header[0]);
    }

}
