package com.jdevelop.gmailsync.core.storage.bdbje;

import java.io.File;

import com.jdevelop.gmailsync.core.storage.exception.StorageException;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class TxDatabaseImpl implements DatabaseInstance {

    private final File envHome;

    private Environment env;

    private Database messagesDatabase;

    public TxDatabaseImpl(File dbPath) {
        this.envHome = dbPath;
    }

    public void close() throws StorageException {
        try {
            messagesDatabase.close();
            env.close();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    public Database getMessagesDatabase() {
        return messagesDatabase;
    }

    public void open() throws StorageException {
        if (!envHome.exists() && !envHome.mkdirs())
            throw new StorageException("Can not create database at " + envHome);
        EnvironmentConfig envCfg = new EnvironmentConfig();
        envCfg.setAllowCreate(true);
        envCfg.setTransactional(true);
        envCfg.setTxnWriteNoSync(true);
        envCfg.setTxnNoSync(true);
        try {
            env = new Environment(envHome, envCfg);
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
