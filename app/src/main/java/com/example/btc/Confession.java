package com.example.btc;

import java.io.Serializable;

public class Confession implements Serializable {
    private final String username;
    private final String text;
    private final String[] comments;
    private final String[] hearts;


    public Confession(String username, String message, String[] comments, String[] hearts) {
        this.username = username;
        this.text = message;
        this.comments = comments;
        this.hearts = hearts;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return text;
    }

    public String getText() {
        return text;
    }

    public String[] getComments() {
        return comments;
    }

    public String[] getHearts() {
        return hearts;
    }
}
