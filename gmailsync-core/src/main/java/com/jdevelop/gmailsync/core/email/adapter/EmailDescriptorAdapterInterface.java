package com.jdevelop.gmailsync.core.email.adapter;

import com.jdevelop.gmailsync.core.email.EmailDescriptor;
import com.jdevelop.gmailsync.core.email.adapter.exception.MessageAdapterException;

public interface EmailDescriptorAdapterInterface<Source> {

    EmailDescriptor getEmailDescriptor(Source source)
            throws MessageAdapterException;

}
