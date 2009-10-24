package com.jdevelop.gmailsync.core.storage;

import java.io.File;

import com.jdevelop.gmailsync.core.storage.bdbje.BDBJEDescriptorStorage;
import com.jdevelop.gmailsync.core.storage.bdbje.DatabaseInstance;
import com.jdevelop.gmailsync.core.storage.bdbje.TxDatabaseImpl;
import com.jdevelop.gmailsync.core.storage.exception.StorageException;

public class StorageFactory {

    private static String storagePath;

    private StorageFactory() {
    }

    public static void initStoragePath(String path) {
        storagePath = path;
    }

    public static DescriptorStorageInterface getDescriptorStorage()
            throws StorageException {
        if (storagePath == null)
            throw new IllegalStateException("Storage path is not initialized");
        File dbStoragePath = new File(storagePath);
        DatabaseInstance instance = new TxDatabaseImpl(dbStoragePath);
        return new BDBJEDescriptorStorage(instance);
    }

}
