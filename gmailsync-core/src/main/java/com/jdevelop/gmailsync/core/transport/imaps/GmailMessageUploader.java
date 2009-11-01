package com.jdevelop.gmailsync.core.transport.imaps;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import com.jdevelop.gmailsync.core.authentication.Credentials;
import com.jdevelop.gmailsync.core.email.EmailMessage;
import com.jdevelop.gmailsync.core.maildir.FolderChangeException;
import com.jdevelop.gmailsync.core.maildir.MailFolderChangedListener;
import com.jdevelop.gmailsync.core.transport.TransportInterface;
import com.jdevelop.gmailsync.core.transport.exception.MessageTransferException;
import com.jdevelop.gmailsync.core.transport.exception.RemoteFolderException;
import com.jdevelop.gmailsync.core.transport.folder.FolderManagementInterface;

public class GmailMessageUploader implements TransportInterface,
        MailFolderChangedListener {

    private Folder folder;

    private Store store;

    private FolderManagementInterface folderManagement;

    public GmailMessageUploader(FolderManagementInterface folderManagement,
            String folderName, Credentials credentials)
            throws RemoteFolderException {
        this.folderManagement = folderManagement;
        Properties sessionProperties = new Properties();
        sessionProperties.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getInstance(sessionProperties);
        try {
            store = session.getStore();
            store.connect("imap.gmail.com", credentials.getUsername(),
                    credentials.getPassword());
            this.folder = store.getFolder(folderName);
            if (!folder.exists())
                folder = folderManagement.createFolder(
                        store.getDefaultFolder(), folderName);
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

    @Override
    public void fireFolderChangeEvent(String newFolderName)
            throws FolderChangeException {
        try {
            if (folder.isOpen())
                folder.close(true);
            folder = store.getFolder(newFolderName);
            if (!folder.exists())
                folder = folderManagement.createFolder(
                        store.getDefaultFolder(), newFolderName);
        } catch (MessagingException e) {
            throw new FolderChangeException(e);
        } catch (RemoteFolderException rfe) {
            throw new FolderChangeException(rfe);
        }
    }

}