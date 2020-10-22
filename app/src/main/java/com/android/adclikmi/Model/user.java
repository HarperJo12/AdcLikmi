package com.android.adclikmi.Model;

/**
 * Marcel 2019 *
 **/

public class user {
    private String username;
    private int level;
    private String token;

    public user(String username, int level, String token) {
        this.username = username;
        this.level = level;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
