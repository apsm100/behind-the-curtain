package com.example.btc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Confession implements Serializable {
    private final User user;
    private final String text;
    private final ArrayList<Comment> comments;
    private final ArrayList<Heart> hearts;
    private final Date date;


    public Confession(User user, String message, ArrayList<Comment> comments, ArrayList<Heart> hearts) {
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

//    public Comment[] getComments() {
//        return comments;
//    }
//
//    public Heart[] getHearts() {
//        return hearts;
//    }
}
