package com.jdevelop.gmailsync.core.transport.folder;

import javax.mail.Folder;
import javax.mail.MessagingException;

import com.jdevelop.gmailsync.core.transport.exception.RemoteFolderException;

public class DefaultFolderManagement implements FolderManagementInterface {

    @Override
    public Folder createFolder(Folder parent, String name)
            throws RemoteFolderException {
        try {
            Folder folder = parent.getFolder(name);
            if (!folder.exists())
                folder.create(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES);
            folder.open(Folder.READ_WRITE);
            return folder;
        } catch (MessagingException e) {
            throw new RemoteFolderException(e);
        }
    }

    @Override
    public Folder[] listFolders(Folder folder) throws RemoteFolderException {
        try {
            if (folder.isOpen())
                folder.open(Folder.READ_ONLY);
            return folder.list();
        } catch (MessagingException e) {
            throw new RemoteFolderException(e);
        }
    }

    @Override
    public void removeFolder(Folder folder) throws RemoteFolderException {
        try {
            if (folder.isOpen())
                folder.close(true);
            folder.delete(true);
        } catch (MessagingException e) {
            throw new RemoteFolderException(e);
        }
    }

}