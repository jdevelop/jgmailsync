package com.jdevelop.gmailsync.core.authentication;

public class Credentials {

    private final String username;

    private final String password;

    public Credentials(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
