package com.jdevelop.gmailsync.core.folder;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.jdevelop.gmailsync.core.TestOptions;
import com.jdevelop.gmailsync.core.transport.folder.DefaultFolderManagement;
import com.jdevelop.gmailsync.core.transport.folder.FolderManagementInterface;

public class FolderManagementTest {

    private Session session;

    private Store store;

    @Before
    public void init() throws Exception {
        Properties sessionProperties = new Properties();
        sessionProperties.setProperty("mail.store.protocol", "imaps");
        session = Session.getInstance(sessionProperties);
        store = session.getStore();
        store.connect(TestOptions.server, TestOptions.username,
                TestOptions.password);
    }

    @Test
    public void testFolderManagement() throws Exception {
        FolderManagementInterface folderMgmt = new DefaultFolderManagement();
        Folder defaultFolder = store.getDefaultFolder();
        folderMgmt.createFolder(defaultFolder, "SampleOne");
        Folder[] listFolders = folderMgmt.listFolders(defaultFolder);
        Folder found = findFolder(listFolders, "SampleOne");
        if (found == null)
            Assert.fail("Folder not found");
        folderMgmt.removeFolder(found);
        listFolders = folderMgmt.listFolders(defaultFolder);
        found = findFolder(listFolders, "SampleOne");
        if (found != null)
            Assert.fail("Folder was not removed");
    }

    private Folder findFolder(Folder[] listFolders, String name) {
        Folder found = null;
        for (Folder folder : listFolders) {
            System.out.println("Folder " + folder);
            if (name.equals(folder.getName())) {
                found = folder;
                break;
            }
        }
        return found;
    }

}
