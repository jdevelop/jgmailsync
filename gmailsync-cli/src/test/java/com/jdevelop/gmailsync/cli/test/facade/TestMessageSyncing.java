package com.jdevelop.gmailsync.cli.test.facade;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.jdevelop.gmailsync.cli.facade.GmailSyncFacade;
import com.jdevelop.gmailsync.cli.facade.MailSyncingFacade;
import com.jdevelop.gmailsync.cli.facade.exception.FacadeException;
import com.jdevelop.gmailsync.cli.facade.observer.MessageAddObserver;
import com.jdevelop.gmailsync.core.authentication.Credentials;
import com.jdevelop.gmailsync.core.email.EmailMessage;

public class TestMessageSyncing {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testSyncing() throws Exception {
        GmailSyncFacade facade = new GmailSyncFacade();
        File root = folder.getRoot();
        File dir = new File(root, "storage");
        dir.mkdirs();
        Mockery mockery = new Mockery();
        final MessageAddObserver observer = mockery
                .mock(MessageAddObserver.class);
        Expectations expectations = new Expectations() {
            {
                exactly(26).of(observer).onMessageUploadStart(
                        with(any(EmailMessage.class)));
                exactly(26).of(observer).onMessageUploadSuccess(
                        with(any(EmailMessage.class)));
            }
        };
        mockery.checking(expectations);
        facade.registerMessageAddObserver(observer);
        syncIt(facade, dir);
        mockery.assertIsSatisfied();
        expectations = new Expectations() {
            {
                exactly(0).of(observer).onMessageUploadStart(
                        with(any(EmailMessage.class)));
                exactly(0).of(observer).onMessageUploadSuccess(
                        with(any(EmailMessage.class)));
            }
        };
        syncIt(facade, dir);
        mockery.assertIsSatisfied();
    }

    private void syncIt(MailSyncingFacade facade, File dir)
            throws FacadeException {
        facade.syncEmails(dir.getAbsolutePath(), new File[] { new File(
                "../gmailsync-core/src/test/maildir-content") },
                new Credentials("jdeveloptest", "gfhjkmyf["));
    }

}
