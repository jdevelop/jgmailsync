package com.jdevelop.gmailsync.cli.facade;

import java.io.File;
import java.util.Iterator;

import javax.mail.Message;

import org.apache.log4j.Logger;

import com.jdevelop.gmailsync.cli.facade.exception.FacadeException;
import com.jdevelop.gmailsync.core.authentication.Credentials;
import com.jdevelop.gmailsync.core.email.EmailDescriptor;
import com.jdevelop.gmailsync.core.email.EmailMessage;
import com.jdevelop.gmailsync.core.email.adapter.EmailDescriptorAdapterInterface;
import com.jdevelop.gmailsync.core.email.adapter.JavaMailDescriptorAdapter;
import com.jdevelop.gmailsync.core.email.adapter.JavaMailMessageAdapter;
import com.jdevelop.gmailsync.core.email.adapter.MessageAdapterInterface;
import com.jdevelop.gmailsync.core.email.adapter.exception.MessageAdapterException;
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

    @Override
    public void syncEmails(String descriptorStoragePath, File[] folders,
            Credentials credentials) throws FacadeException {
        MaildirResolver resolver = new SimpleMaildirResolver();
        StorageFactory.initStoragePath(descriptorStoragePath);
        DescriptorStorageInterface descriptorStorage = StorageFactory
                .getDescriptorStorage();
        EmailDescriptorAdapterInterface<Message> descriptorAdapter = new JavaMailDescriptorAdapter();
        MessageAdapterInterface<Message> messageAdapter = new JavaMailMessageAdapter();
        TransportInterface transport = null;
        try {
            transport = new GmailMessageUploader("[Gmail]/All messages",
                    credentials);
        } catch (RemoteFolderException e) {
            throw new FacadeException("Could not initialize transport", e);
        }
        for (File file : folders) {
            File[] maildirs = resolver.resolveMaildirs(file);
            try {
                Iterator<Message> emailIterator = new MaildirReader(maildirs);
                while (emailIterator.hasNext()) {
                    Message message = emailIterator.next();
                    EmailDescriptor emailDescriptor = null;
                    try {
                        emailDescriptor = descriptorAdapter
                                .getEmailDescriptor(message);
                        boolean descriptorExists = descriptorStorage
                                .checkIfDescriptorExists(emailDescriptor);
                        if (!descriptorExists) {
                            EmailMessage emailMessage = messageAdapter
                                    .getMessage(message);
                            transport.uploadMessage(emailMessage);
                            descriptorStorage.addDescriptor(emailDescriptor);
                        }
                    } catch (MessageAdapterException e) {
                        log.warn("Can not get message descriptor for "
                                + message + ", skipping", e);
                    } catch (StorageException e) {
                        //TODO allow ignoring of the exception here
                        throw new FacadeException(e);
                    } catch (TransportException e) {
                        //TODO allow ignoring of the exception here
                        throw new FacadeException(e);
                    }
                }
            } catch (MaildirReadException e) {
                log.warn("Error reading maildirs at " + file, e);
            }
        }
    }

}