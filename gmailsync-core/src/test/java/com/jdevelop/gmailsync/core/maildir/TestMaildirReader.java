package com.jdevelop.gmailsync.core.maildir;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Iterator;

import javax.mail.Folder;
import javax.mail.Message;

import org.junit.Test;

import com.jdevelop.gmailsync.core.maildir.exception.MaildirReadException;
import com.jdevelop.gmailsync.core.maildir.reader.MaildirReader;

import static junit.framework.Assert.assertEquals;

public class TestMaildirReader {

    private static final String MESSAGE_ID_HDR = "message-id";

    private static final int EXPECTED_MESSAGES = 26;

    private File[] maildirs = null;

    @Test(expected = MaildirReadException.class)
    public void testReadingEmptyMaildirs() throws Exception {
        maildirs = new File[] {
                createFile("src/test/maildir-content/jibx-empty"),
                createFile("src/test/maildir-content/tagsoup-empty") };
        Iterator<Message> reader = new MaildirReader(maildirs);
        int messages = 0;
        while (reader.hasNext()) {
            reader.next();
            messages++;
        }
    }

    @Test
    public void testReadValidMaildirs() throws Exception {
        maildirs = new File[] { createFile("src/test/maildir-content/jibx"),
                createFile("src/test/maildir-content/tagsoup"),
                createFile("src/test/maildir-content/javacc"),
                createFile("src/test/maildir-content/x.org"),
                createFile("src/test/maildir-content/slide") };
        int messages = 0;
        MailFolderChangedListener changedListener = new MailFolderChangedListener() {

            @Override
            public void fireFolderChangeEvent(Folder folder) {
                System.out.println("Name " + folder.getStore().getURLName());
            }

        };
        Iterator<Message> reader = new MaildirReader(maildirs)
                .setFolderChangedListener(changedListener);
        while (reader.hasNext()) {
            Message message = reader.next();
            System.out.println(message.getHeader(MESSAGE_ID_HDR)[0]);
            messages++;
        }
        assertEquals(EXPECTED_MESSAGES, messages);
    }

    @Test
    public void testReadingMixedMaildirs() throws Exception {
        maildirs = new File("src/test/maildir-content")
                .listFiles(new FileFilter() {

                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });
        Iterator<Message> reader = new MaildirReader(maildirs);
        System.out.println(Arrays.deepToString(maildirs));
        int messages = 0;
        while (reader.hasNext()) {
            Message next = reader.next();
            System.out.println(next.getHeader(MESSAGE_ID_HDR)[0]);
            messages++;
        }
        assertEquals(EXPECTED_MESSAGES, messages);
    }

    private File createFile(String path) {
        return new File(path);
    }

}