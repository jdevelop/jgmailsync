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
        Store store = session.getStore(new URLName(dirName));
        store.connect();
        currentFolder = store.getFolder(currentDir.getName());
        numberOfMessagesInFolder = currentFolder.getMessageCount();
    }

    @Override
    public boolean hasNext() {
        return currentMessageIdx < numberOfMessagesInFolder
                || currentDirIdx < maildirs.length;
    }

    @Override
    public Message next() {
        currentMessageIdx++;
        if (currentMessageIdx >= numberOfMessagesInFolder) {
            currentMessageIdx = 0;
            currentDirIdx++;
            if (currentDirIdx >= maildirs.length)
                throw new IllegalStateException(
                        "No more messages are available");
            try {
                initCurrentFolder();
            } catch (MessagingException e) {
                throw new IllegalStateException(e);
            }
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