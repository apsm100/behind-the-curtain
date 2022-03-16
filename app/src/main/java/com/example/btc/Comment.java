package com.example.btc;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Comment  implements Serializable {
    @DocumentId
    private String documentId;
    private String userId;
    private Date date;
    private String data;
    private ArrayList<String> upVoteIds;
    private ArrayList<String> downVoteIds;
    private int voteCount;
    private String commentDocumentId;

    public String getDocumentId() {
        return documentId;
    }

    public Comment(){}
    public String getUserId() {
        return userId;
    }

    public boolean addUpVote(String userId) {
        if (!upVoteIds.contains(userId)) {
            upVoteIds.add(userId);
            setVoteCount();
            return true;
        }
        return false;
    }

    public boolean addDownVote(String userId) {
        if (!downVoteIds.contains(userId)) {
            downVoteIds.add(userId);
            setVoteCount();
            return true;
        }
        return false;
    }

    public boolean removeUpVote(String userId) {
        if (upVoteIds.contains(userId)) {
            upVoteIds.remove(userId);
            setVoteCount();
            return true;
        }
        return false;
    }

    public boolean removeDownVote(String userId) {
        if (downVoteIds.contains(userId)) {
            downVoteIds.remove(userId);
            setVoteCount();
            return true;
        }
        return false;
    }

    public void setVoteCount() {
        voteCount = upVoteIds.size() - downVoteIds.size();
    }

    public ArrayList<String> getUpVoteIds() {
        return upVoteIds;
    }

    public ArrayList<String> getDownVoteIds() {
        return downVoteIds;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public Date getDate() {
        return date;
    }

    public String getCommentDocumentId() {
        return commentDocumentId;
    }

    public String getData() {
        return data;
    }

    public Comment(String userId, String data, Date date, ArrayList<String> upVoteIds, ArrayList<String> downVoteIds, String commentDocumentId) {
        this.date = date;
        this.userId = userId;
        this.data = data;
        this.upVoteIds = upVoteIds;
        this.downVoteIds = downVoteIds;
        this.commentDocumentId = commentDocumentId;
    }
}
