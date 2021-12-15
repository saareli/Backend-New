package com.dev.objects;

public class UserObject {
    private int id;
    private String username;
    private String password;
    private String token;
    private int loginTries;


    public UserObject(String username, String password,String token) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.loginTries = 0;
    }
    public UserObject() {
        this.username = "";
        this.password = "";
        this.token="";
        this.loginTries = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLoginTries() {
        return loginTries;
    }

    public void setLoginTries(int numberOfLoginAttempts) {
        this.loginTries = numberOfLoginAttempts;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}