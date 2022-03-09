package com.example.btc;

import java.io.Serializable;

public class Confession implements Serializable {
    private final String username;
    private final String text;
    private final Comment[] comments;
    private final Heart[] hearts;


    public Confession(String username, String message, Comment[] comments, Heart[] hearts) {
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

    public Comment[] getComments() {
        return comments;
    }

    public Heart[] getHearts() {
        return hearts;
    }
}
