package com.jdevelop.gmailsync.cli.facade;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.mail.Message;

import org.apache.log4j.Logger;

import com.jdevelop.gmailsync.cli.facade.exception.FacadeException;
import com.jdevelop.gmailsync.cli.facade.observer.MessageAddObserver;
import com.jdevelop.gmailsync.cli.facade.transport.RetryingTransportImpl;
import com.jdevelop.gmailsync.core.authentication.Credentials;
import com.jdevelop.gmailsync.core.email.EmailDescriptor;
import com.jdevelop.gmailsync.core.email.EmailMessage;
import com.jdevelop.gmailsync.core.email.adapter.EmailDescriptorAdapterInterface;
import com.jdevelop.gmailsync.core.email.adapter.JavaMailDescriptorAdapter;
import com.jdevelop.gmailsync.core.email.adapter.JavaMailMessageAdapter;
import com.jdevelop.gmailsync.core.email.adapter.MessageAdapterInterface;
import com.jdevelop.gmailsync.core.email.adapter.exception.MessageAdapterException;
import com.jdevelop.gmailsync.core.exception.handler.GenericExceptionHandler;
import com.jdevelop.gmailsync.core.maildir.exception.MaildirReadException;
import com.jdevelop.gmailsync.core.maildir.reader.MaildirReader;
import com.jdevelop.gmailsync.core.maildir.resolver.MaildirResolver;
import com.jdevelop.gmailsync.core.maildir.resolver.SimpleMaildirResolver;
import com.jdevelop.gmailsync.core.storage.DescriptorStorageInterface;
import com.jdevelop.gmailsync.core.storage.StorageFactory;
import com.jdevelop.gmailsync.core.storage.exception.StorageException;
import com.jdevelop.gmailsync.core.transport.TransportInterface;
import com.jdevelop.gmailsync.core.transport.exception.RemoteFolderException;
import com.jdevelop.gmailsync.core.transport.exception.TransportException;
import com.jdevelop.gmailsync.core.transport.imaps.GmailMessageUploader;

public class GmailSyncFacade implements MailSyncingFacade {

    private static final Logger log = Logger.getLogger(GmailSyncFacade.class);

    private Set<MessageAddObserver> messageAddObservers = new HashSet<MessageAddObserver>();

    public void registerMessageAddObserver(MessageAddObserver messageAddObserver) {
        messageAddObservers.add(messageAddObserver);
    }

    public void unregisterMessageAddObserver(
            MessageAddObserver messageAddObserver) {
        messageAddObservers.remove(messageAddObserver);
    }

    @Override
    public void syncEmails(String descriptorStoragePath, File[] folders,
            Credentials credentials) throws FacadeException {
        MaildirResolver resolver = new SimpleMaildirResolver();
        StorageFactory.initStoragePath(descriptorStoragePath);
        DescriptorStorageInterface descriptorStorage = null;
        try {
            descriptorStorage = StorageFactory.getDescriptorStorage();
        } catch (StorageException e) {
            throw new FacadeException("Can not initialize storage", e);
        }
        EmailDescriptorAdapterInterface<Message> descriptorAdapter = new JavaMailDescriptorAdapter();
        MessageAdapterInterface<Message> messageAdapter = new JavaMailMessageAdapter();
        TransportInterface transport = null;
        try {
            transport = new GmailMessageUploader("[Gmail]/All Mail",
                    credentials);
            transport = new RetryingTransportImpl(5, 1000, transport);
        } catch (RemoteFolderException e) {
            GenericExceptionHandler.handleException(new FacadeException(
                    "Could not initialize transport", e));
        }
        for (File file : folders) {
            File[] maildirs = resolver.resolveMaildirs(file);
            try {
                Iterator<Message> emailIterator = new MaildirReader(maildirs);
                while (emailIterator.hasNext()) {
                    Message message = emailIterator.next();
                    EmailDescriptor emailDescriptor = null;
                    EmailMessage emailMessage = null;
                    try {
                        emailDescriptor = descriptorAdapter
                                .getEmailDescriptor(message);
                        boolean descriptorExists = descriptorStorage
                                .checkIfDescriptorExists(emailDescriptor);
                        if (!descriptorExists) {
                            emailMessage = messageAdapter.getMessage(message);
                            notifyObserversOnStart(emailMessage);
                            transport.uploadMessage(emailMessage);
                            notifyObserversOnSuccess(emailMessage);
                            descriptorStorage.addDescriptor(emailDescriptor);
                        }
                    } catch (MessageAdapterException e) {
                        log.warn("Can not get message descriptor for "
                                + message + ", skipping", e);
                        notifyObserversOnFailure(emailMessage);
                        GenericExceptionHandler
                                .handleException(new FacadeException(e));
                    } catch (StorageException e) {
                        notifyObserversOnFailure(emailMessage);
                        GenericExceptionHandler
                                .handleException(new FacadeException(e));
                    } catch (TransportException e) {
                        notifyObserversOnFailure(emailMessage);
                        GenericExceptionHandler
                                .handleException(new FacadeException(e));
                    }
                }
            } catch (MaildirReadException e) {
                log.warn("Error reading maildirs at " + file, e);
            }
        }
    }

    private void notifyObserversOnFailure(EmailMessage emailMessage) {
        for (MessageAddObserver observer : messageAddObservers)
            observer.onMessageUploadFailure(emailMessage);
    }

    private void notifyObserversOnSuccess(EmailMessage emailMessage) {
        for (MessageAddObserver observer : messageAddObservers)
            observer.onMessageUploadSuccess(emailMessage);
    }

    private void notifyObserversOnStart(EmailMessage msg) {
        for (MessageAddObserver observer : messageAddObservers)
            observer.onMessageUploadStart(msg);
    }

}