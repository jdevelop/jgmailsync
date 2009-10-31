package com.jdevelop.gmailsync.cli.test.facade;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.jdevelop.gmailsync.cli.facade.GmailSyncFacade;
import com.jdevelop.gmailsync.cli.facade.MailSyncingFacade;
import com.jdevelop.gmailsync.cli.facade.exception.FacadeException;
import com.jdevelop.gmailsync.cli.facade.observer.MessageAddObserver;
import com.jdevelop.gmailsync.core.authentication.Credentials;
import com.jdevelop.gmailsync.core.email.EmailMessage;

import static com.jdevelop.gmailsync.cli.test.facade.TestMessageSyncing.DumpMessageAction.print;

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
                allowing(observer).onMessageUploadFailure(
                        with(any(EmailMessage.class)));
                will(print(System.err));
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
        facade
                .syncEmails(
                        dir.getAbsolutePath(),
                        new File[] {
                                new File(
                                        "../gmailsync-core/src/test/maildir-content") },
                        new Credentials("jdeveloptest", "gfhjkmyf["));
    }

    static class DumpMessageAction implements Action {

        private PrintStream printStream;

        public DumpMessageAction(PrintStream printStream) {
            super();
            this.printStream = printStream;
        }

        @Override
        public void describeTo(Description arg0) {
        }

        @Override
        public Object invoke(Invocation invocation) throws Throwable {
            dumpMessage(printStream, invocation);
            return null;
        }

        public static Action print(PrintStream printStream) {
            return new DumpMessageAction(printStream);
        }

    }

    private static void dumpMessage(PrintStream printStream,
            Invocation invocation) throws MessagingException {
        EmailMessage emailMsg = (EmailMessage) invocation.getParameter(0);
        Message msg = emailMsg.getMessage();
        printStream.println(Arrays.deepToString(msg.getFrom()) + " : "
                + msg.getSubject());
    }

}
