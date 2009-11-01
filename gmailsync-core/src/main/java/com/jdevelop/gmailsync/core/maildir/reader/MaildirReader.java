package com.jdevelop.gmailsync.core.maildir.reader;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import com.jdevelop.gmailsync.core.maildir.FolderChangeException;
import com.jdevelop.gmailsync.core.maildir.MailFolderChangedListener;
import com.jdevelop.gmailsync.core.maildir.exception.MaildirReadException;

public class MaildirReader implements Iterator<Message> {

    private static final int FIRST_MESSAGE_IN_FOLDER = 1;

    private final File[] maildirs;

    private int currentDirIdx;

    private int currentMessageIdx;

    private int numberOfMessagesInFolder;

    private Session session;

    private Folder currentFolder;

    private MailFolderChangedListener folderChangedListener;

    public MaildirReader(File[] maildirs) throws MaildirReadException {
        this.maildirs = maildirs;
        currentDirIdx = 0;
        currentMessageIdx = FIRST_MESSAGE_IN_FOLDER;
        session = Session.getInstance(new Properties());
        try {
            initCurrentFolder();
            // ensure that message index is before the head
            currentMessageIdx = 0;
            if (numberOfMessagesInFolder == 0 && !hasNextNotEmptyMaildir())
                throw new MaildirReadException("Maildirs are empty");
        } catch (MessagingException e) {
            throw new MaildirReadException(e);
        }
    }

    private void initCurrentFolder() throws MessagingException {
        final File currentDir = maildirs[currentDirIdx];
        String dirName = "maildir:///" + currentDir.getAbsolutePath();
        Store store = session.getStore(new URLName(dirName));
        store.connect();
        currentFolder = store.getFolder("INBOX");
        currentFolder.open(Folder.READ_ONLY);
        currentMessageIdx = FIRST_MESSAGE_IN_FOLDER;
        numberOfMessagesInFolder = currentFolder.getMessageCount();
        if (folderChangedListener != null)
            try {
                folderChangedListener.fireFolderChangeEvent(currentDir
                        .getName());
            } catch (FolderChangeException e) {
                throw new MessagingException("Can not change folder", e);
            }
    }

    @Override
    public boolean hasNext() {
        return ++currentMessageIdx <= numberOfMessagesInFolder
                || hasNextNotEmptyMaildir();
    }

    private boolean hasNextNotEmptyMaildir() {
        boolean hasMessages = false;
        do {
            if (currentDirIdx + 1 >= maildirs.length) {
                resetNumberOfMessages();
                break;
            }
            currentDirIdx++;
            try {
                currentFolder.close(true);
                initCurrentFolder();
            } catch (FolderNotFoundException e) {
                resetNumberOfMessages();
            } catch (MessagingException e) {
                throw new IllegalStateException(e);
            }
            if (numberOfMessagesInFolder > 0) {
                hasMessages = true;
                break;
            }
        } while (currentDirIdx < maildirs.length);
        return hasMessages;
    }

    private void resetNumberOfMessages() {
        numberOfMessagesInFolder = 0;
    }

    @Override
    public Message next() {
        if (currentMessageIdx > numberOfMessagesInFolder) {
            currentMessageIdx = FIRST_MESSAGE_IN_FOLDER;
            final boolean hasNotEmptyMaildirs = hasNextNotEmptyMaildir();
            if (!hasNotEmptyMaildirs)
                throw new IllegalStateException("No messages available "
                        + maildirs[currentDirIdx]);
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

    public MaildirReader setFolderChangedListener(
            MailFolderChangedListener listener) {
        this.folderChangedListener = listener;
        return this;
    }

}