package com.jdevelop.gmailsync.core.transport.imaps;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

import com.jdevelop.gmailsync.core.TestOptions;
import com.jdevelop.gmailsync.core.authentication.Credentials;
import com.jdevelop.gmailsync.core.email.EmailMessage;
import com.jdevelop.gmailsync.core.transport.TransportInterface;
import com.jdevelop.gmailsync.core.transport.folder.DefaultFolderManagement;

public class TestImapsUploading {

    @Test
    public void testUploadingOfAEmail() throws Exception {
        TransportInterface transport = new GmailMessageUploader(
                new DefaultFolderManagement(), "ibatis/another folder2",
                new Credentials(TestOptions.username, TestOptions.password));
        Properties sessionProperties = new Properties();
        Session session = Session.getInstance(sessionProperties);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("bofh@redwerk.com"));
        msg.setRecipient(RecipientType.TO, new InternetAddress(
                "jdeveloptest@gmail.com"));
        msg.setText("Tes message here");
        transport.uploadMessage(new EmailMessage(msg));
    }

}
