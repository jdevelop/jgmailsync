package com.jdevelop.gmailsync.core.maildir.resolver;

import java.io.File;

/**
 * Resolves the set of maildirs, starting from given folder
 */
public interface MaildirResolver {

    File[] resolveMaildirs(File root);

}
