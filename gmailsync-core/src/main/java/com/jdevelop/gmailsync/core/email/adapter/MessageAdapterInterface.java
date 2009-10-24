package com.jdevelop.gmailsync.core.email.adapter;

import com.jdevelop.gmailsync.core.email.EmailMessage;

public interface MessageAdapterInterface<Source> {

    EmailMessage getMessage(Source sourceMessage);

}
