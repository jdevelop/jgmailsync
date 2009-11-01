package com.jdevelop.gmailsync.core;

import java.io.IOException;
import java.util.Properties;

public class TestOptions {

    public static final String username;

    public static final String password;

    public static final String server;

    static {
        Properties props = new Properties();
        try {
            props.load(TestOptions.class
                    .getResourceAsStream("/credentials.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        username = props.getProperty("username");
        password = props.getProperty("password");
        server = props.getProperty("server");
    }

}
