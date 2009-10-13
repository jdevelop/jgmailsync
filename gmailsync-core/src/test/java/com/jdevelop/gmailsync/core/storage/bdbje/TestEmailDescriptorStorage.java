package com.jdevelop.gmailsync.core.storage.bdbje;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.jdevelop.gmailsync.core.email.EmailDescriptor;
import com.jdevelop.gmailsync.core.storage.DescriptorStorageInterface;

public class TestEmailDescriptorStorage {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    private File dbDir;
    
    @Before
    public void setUp() throws Exception {
        folder.create();
        dbDir = folder.newFolder("dbDir");
        System.out.println("Using database directory " + dbDir);
    }
    
    @Test
    public void testStorageCreation() throws Exception {
        DatabaseInstance dbInstance = new TxDatabaseImpl(dbDir);
        dbInstance.open();
        assertNotNull(dbInstance.getMessagesDatabase());
        dbInstance.close();
    }
    
    @Test
    public void testDescriptorStorage() throws Exception {
        DatabaseInstance dbInstance = new TxDatabaseImpl(dbDir);
        dbInstance.open();
        DescriptorStorageInterface storage = new BDBJEDescriptorStorage(
                dbInstance);
        EmailDescriptor testDescriptor = new EmailDescriptor("my msgid");
        storage.addDescriptor(testDescriptor);
        assertTrue(storage.checkIfDescriptorExists(testDescriptor));
        storage.removeDescriptor(testDescriptor);
        assertFalse(storage.checkIfDescriptorExists(testDescriptor));
        dbInstance.close();
    }
    
}
