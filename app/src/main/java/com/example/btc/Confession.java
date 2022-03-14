package com.example.btc;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Confession implements Serializable {
    @DocumentId
    private String documentId;
    private User user;
    private String text;
    private int comments;
    private ArrayList<String> hearts;
    private Date date;
    private int popularityIndex;

    public String getDocumentId() {
        return documentId;
    }

    public Confession(User user, String text, ArrayList<String> hearts, Date date) {
        this.date = date;
        this.user = user;
        this.text = text;
        this.comments = 0;
        this.hearts = hearts;
        setPopularityIndex();
    }

    public Confession(){}

    public User getUser() {
        return user;
    }

    public void addHeart(String userId) {
        hearts.add(userId);
        setPopularityIndex();
    }

    public void removeHeart(String userId) {
        hearts.remove(userId);
        setPopularityIndex();
    }

    public void addComment() {
        comments += 1;
        setPopularityIndex();
    }

    public void removeComment() {
        comments -= 1;
        setPopularityIndex();
    }

    public int getPopularityIndex() {
        return popularityIndex;
    }

    public void setPopularityIndex() {
        popularityIndex = hearts.size() + (comments / 4);
    }

    public String getText() {
        return text;
    }

    public int getComments() {
        return comments;
    }

    public ArrayList<String> getHearts() {
        return hearts;
    }

    public Date getDate() {
        return date;
    }
}
