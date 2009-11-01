package com.jdevelop.gmailsync.core.transport.folder;

import javax.mail.Folder;

import com.jdevelop.gmailsync.core.transport.exception.RemoteFolderException;

/**
 * Declares methods to be used for managing folders
 */
public interface FolderManagementInterface {

    Folder createFolder(Folder parent, String name)
            throws RemoteFolderException;

    void removeFolder(Folder folder) throws RemoteFolderException;

    Folder[] listFolders(Folder folder) throws RemoteFolderException;

}
