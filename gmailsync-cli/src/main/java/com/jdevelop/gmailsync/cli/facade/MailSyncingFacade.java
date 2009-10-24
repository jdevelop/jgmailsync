package com.jdevelop.gmailsync.cli.facade;

import java.io.File;

import com.jdevelop.gmailsync.cli.facade.exception.FacadeException;
import com.jdevelop.gmailsync.core.authentication.Credentials;

public interface MailSyncingFacade {

    void syncEmails(String descriptorStorage, File[] folders,
            Credentials credentials)
            throws FacadeException;

}
