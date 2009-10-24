package com.jdevelop.gmailsync.core.maildir;

import javax.mail.Folder;

public interface MailFolderChangedListener {

    void fireFolderChangeEvent(Folder folder) throws FolderChangeException;

}
