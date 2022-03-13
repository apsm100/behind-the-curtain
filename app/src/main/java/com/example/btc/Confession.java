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


    public Confession(User user, String text, ArrayList<Comment> comments, ArrayList<Heart> hearts, Date date) {
        this.date = date;
        this.user = user;
        this.text = text;
        this.comments = comments;
        this.hearts = hearts;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public ArrayList<Heart> getHearts() {
        return hearts;
    }

    public Date getDate() {
        return date;
    }
}
