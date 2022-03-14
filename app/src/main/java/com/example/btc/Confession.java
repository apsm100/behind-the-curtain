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
    private ArrayList<Comment> comments;
    private ArrayList<String> hearts;
    private Date date;
    private int popularityIndex;

    public String getDocumentId() {
        return documentId;
    }

    public Confession(User user, String text, ArrayList<Comment> comments, ArrayList<String> hearts, Date date) {
        this.date = date;
        this.user = user;
        this.text = text;
        this.comments = comments;
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

    public void addComment(Comment comment) {
        comments.add(comment);
        setPopularityIndex();
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        setPopularityIndex();
    }

    public int getPopularityIndex() {
        return popularityIndex;
    }

    public void setPopularityIndex() {
        popularityIndex = hearts.size() + (comments.size() / 4);
    }

    public String getText() {
        return text;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) { this.comments = comments; }

    public ArrayList<String> getHearts() {
        return hearts;
    }

    public Date getDate() {
        return date;
    }
}
