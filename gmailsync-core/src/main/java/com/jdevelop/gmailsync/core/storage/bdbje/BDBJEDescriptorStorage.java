package com.jdevelop.gmailsync.core.storage.bdbje;

import com.jdevelop.gmailsync.core.email.EmailDescriptor;
import com.jdevelop.gmailsync.core.storage.DescriptorStorageInterface;
import com.jdevelop.gmailsync.core.storage.exception.StorageException;
import com.sleepycat.je.Database;

public class BDBJEDescriptorStorage implements DescriptorStorageInterface {

    private final DatabaseInstance instance;

    public BDBJEDescriptorStorage(DatabaseInstance instance) {
        this.instance = instance;
    }

    public void addDescriptor(EmailDescriptor descriptor)
            throws StorageException {
        Database messagesDb = instance.getMessagesDatabase();
    }

    public boolean checkIfDescriptorExists(EmailDescriptor descriptor)
            throws StorageException {
        return false;
    }

}
