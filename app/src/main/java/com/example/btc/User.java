package com.example.btc;

import java.io.Serializable;

@SuppressWarnings("unused")
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

    public String getSchool() {

        // empty getter for firebase.
        return school;
    }

    public User() {
        // empty overloaded constructor for firebase
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }
}
