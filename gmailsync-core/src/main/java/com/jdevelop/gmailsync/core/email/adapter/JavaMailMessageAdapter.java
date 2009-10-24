package com.jdevelop.gmailsync.core.email.adapter;

import javax.mail.Message;

import com.jdevelop.gmailsync.core.email.EmailMessage;

public class JavaMailMessageAdapter implements MessageAdapterInterface<Message> {

    @Override
    public EmailMessage getMessage(Message sourceMessage) {
        return new EmailMessage(sourceMessage);
    }

}
