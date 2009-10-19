package com.jdevelop.gmailsync.core.maildir;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;

import javax.mail.Message;

import junit.framework.Assert;

import org.junit.Test;

import com.jdevelop.gmailsync.core.maildir.reader.MaildirReader;

public class TestMaildirReader {

    @Test
    public void testReadingMaildir() throws Exception {
        Iterator<Message> reader = new MaildirReader(new File(
                "src/test/maildir-content").listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        }));
        int messages = 0;
        while (reader.hasNext()) {
            Message next = reader.next();
            System.out.println(next.getSubject());
            messages++;
        }
        Assert.assertFalse("Found " + messages, 0 == messages);
    }

}