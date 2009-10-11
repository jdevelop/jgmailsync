package com.jdevelop.gmailsync.core.storage;

import com.jdevelop.gmailsync.core.email.EmailDescriptor;
import com.jdevelop.gmailsync.core.storage.exception.StorageException;

public interface DescriptorStorageInterface {

    boolean checkIfDescriptorExists(EmailDescriptor descriptor)
            throws StorageException;

    void addDescriptor(EmailDescriptor descriptor)
            throws StorageException;

}
