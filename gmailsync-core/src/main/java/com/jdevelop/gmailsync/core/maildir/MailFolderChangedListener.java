package com.jdevelop.gmailsync.core.maildir;


public interface MailFolderChangedListener {

    void fireFolderChangeEvent(String folder) throws FolderChangeException;

}
