package com.jdevelop.gmailsync.cli.facade.observer;

import com.jdevelop.gmailsync.core.email.EmailMessage;

public interface MessageAddObserver {

    void onMessageUploadStart(EmailMessage message);

    void onMessageUploadSuccess(EmailMessage message);

    void onMessageUploadFailure(EmailMessage message);

}
