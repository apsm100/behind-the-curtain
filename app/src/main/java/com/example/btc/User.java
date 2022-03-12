package com.example.btc;

import java.io.Serializable;

public class User implements Serializable {

    private String email;
    private String username;
    private String school;
    private String displayName;

    public User(String username, String email, String school, String displayName) {
        this.username = username;
        this.email = email;
        this.school = school;
        this.displayName = displayName;
    }
    public User() {
        // empty overloaded constructor
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
