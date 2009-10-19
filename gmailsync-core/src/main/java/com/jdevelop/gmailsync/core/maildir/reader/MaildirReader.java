package com.jdevelop.gmailsync.core.maildir.reader;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import com.jdevelop.gmailsync.core.maildir.exception.MaildirReadException;

public class MaildirReader implements Iterator<Message> {

    private final File[] maildirs;

    private int currentDirIdx;

    private int currentMessageIdx;

    private int numberOfMessagesInFolder;

    private Session session;

    private Folder currentFolder;

    public MaildirReader(File[] maildirs) throws MaildirReadException {
        this.maildirs = maildirs;
        currentDirIdx = 0;
        currentMessageIdx = 0;
        session = Session.getInstance(new Properties());
        try {
            initCurrentFolder();
        } catch (MessagingException e) {
            throw new MaildirReadException(e);
        }
    }

    private void initCurrentFolder() throws MessagingException {
        final File currentDir = maildirs[currentDirIdx];
        final File parentDir = currentDir.getParentFile();
        String dirName = "maildir://" + parentDir.getAbsolutePath();
        System.out.println(dirName);
        Store store = session.getStore(new URLName(dirName));
        store.connect();
        currentFolder = store.getFolder(currentDir.getName());
        System.out.println(currentFolder.getFullName());
        currentFolder.open(Folder.READ_ONLY);
        numberOfMessagesInFolder = currentFolder.getMessageCount();
        System.out.println(numberOfMessagesInFolder);
    }

    @Override
    public boolean hasNext() {
        return currentMessageIdx < numberOfMessagesInFolder
                || hasNextNotEmptyMaildir();
    }

    private boolean hasNextNotEmptyMaildir() {
        boolean hasMessages = false;
        do {
            try {
                initCurrentFolder();
            } catch (MessagingException e) {
                throw new IllegalStateException(e);
            }
            if (numberOfMessagesInFolder > 0) {
                hasMessages = true;
                break;
            }
            ++currentDirIdx;
        } while (currentDirIdx < maildirs.length);
        return hasMessages;
    }

    @Override
    public Message next() {
        currentMessageIdx++;
        if (currentMessageIdx >= numberOfMessagesInFolder) {
            currentMessageIdx = 0;
            final boolean hasNotEmptyMaildirs = hasNextNotEmptyMaildir();
            if (!hasNotEmptyMaildirs)
                throw new IllegalStateException("No messages available");
        }
        try {
            return currentFolder.getMessage(currentMessageIdx);
        } catch (MessagingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void remove() {
        // not implemented
        throw new IllegalStateException("Can not remove current message");
    }

}