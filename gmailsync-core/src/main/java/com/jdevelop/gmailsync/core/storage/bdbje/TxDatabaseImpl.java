package com.jdevelop.gmailsync.core.storage.bdbje;

import java.io.File;

import com.jdevelop.gmailsync.core.storage.exception.StorageException;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class TxDatabaseImpl implements DatabaseInstance {

    private static final String CLASS_CATALOG_DB_NAME = "class_catalog";

    private final String dbPath;

    private Environment env;

    private Database messagesDatabase;

    private StoredClassCatalog storedCatalog;

    private Database classDb;

    public TxDatabaseImpl(String dbPath) {
        this.dbPath = dbPath;
    }

    public void close() throws StorageException {
        try {
            storedCatalog.close();
            classDb.close();
            env.close();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    public Database getMessagesDatabase() {
        return null;
    }

    public void open() throws StorageException {
        File envHome = new File(dbPath);
        if (!envHome.exists() && !envHome.mkdirs())
            throw new StorageException("Can not create database at " + envHome);
        EnvironmentConfig envCfg = new EnvironmentConfig();
        envCfg.setAllowCreate(true);
        envCfg.setTransactional(true);
        envCfg.setTxnWriteNoSync(true);
        envCfg.setTxnNoSync(true);
        try {
            env = new Environment(envHome, envCfg);
            classDb = env.openDatabase(null, CLASS_CATALOG_DB_NAME,
                    createDatabaseConfig(true, true));
            storedCatalog = new StoredClassCatalog(classDb);
            messagesDatabase = env.openDatabase(null, "messages_data",
                    createDatabaseConfig(true, true));
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }
    
    private DatabaseConfig createDatabaseConfig(boolean transactional,
            boolean allowCreate) {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(allowCreate);
        databaseConfig.setTransactional(transactional);
        return databaseConfig;
    }

}
