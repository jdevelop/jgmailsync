package com.jdevelop.gmailsync.core.transport.imaps;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import com.jdevelop.gmailsync.core.authentication.Credentials;
import com.jdevelop.gmailsync.core.email.EmailMessage;
import com.jdevelop.gmailsync.core.transport.TransportInterface;
import com.jdevelop.gmailsync.core.transport.exception.MessageTransferException;
import com.jdevelop.gmailsync.core.transport.exception.RemoteFolderException;

public class GmailMessageUploader implements TransportInterface {

    private final Folder folder;

    public GmailMessageUploader(String folderName, Credentials credentials)
            throws RemoteFolderException {
        Properties sessionProperties = new Properties();
        sessionProperties.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getInstance(sessionProperties);
        try {
            Store store = session.getStore();
            store.connect("imap.gmail.com", credentials.getUsername(),
                    credentials.getPassword());
            this.folder = store.getFolder(folderName);
            if (!folder.exists())
                folder.create(Folder.HOLDS_MESSAGES);
        } catch (Exception e) {
            throw new RemoteFolderException(e);
        }
    }

    @Override
    public void uploadMessage(EmailMessage message)
            throws MessageTransferException {
        try {
            folder.appendMessages(new Message[] { message.getMessage() });
        } catch (MessagingException e) {
            throw new MessageTransferException(e);
        }
    }

}
