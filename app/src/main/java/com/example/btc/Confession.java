package com.example.btc;

import java.io.Serializable;
import java.util.Date;

public class Confession implements Serializable {
    private final User user;
    private final String text;
    private final Comment[] comments;
    private final Heart[] hearts;
    private final Date date;


    public Confession(User user, String message, Comment[] comments, Heart[] hearts) {
        this.date = new Date(System.currentTimeMillis());
        this.user = user;
        this.text = message;
        this.comments = comments;
        this.hearts = hearts;
    }

    public User getUser() {
        return user;
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
