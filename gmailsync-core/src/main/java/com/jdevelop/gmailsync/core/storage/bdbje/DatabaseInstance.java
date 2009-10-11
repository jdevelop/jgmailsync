package com.jdevelop.gmailsync.core.storage.bdbje;

import com.jdevelop.gmailsync.core.storage.exception.StorageException;
import com.sleepycat.je.Database;

public interface DatabaseInstance {

    void open() throws StorageException;

    Database getMessagesDatabase();

    void close() throws StorageException;
    
}
