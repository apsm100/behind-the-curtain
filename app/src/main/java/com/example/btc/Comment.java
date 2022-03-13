package com.example.btc;

import java.io.Serializable;
import java.util.Date;

public class Comment  implements Serializable {
    String userId;
    Date date;
    String data;
    public Comment(){}
    public String getUserId() {
        return userId;
    }

    public Date getDate() {
        return date;
    }

    public String getData() {
        return data;
    }

    public Comment(String userId, String data, Date date) {
        this.date = date;
        this.userId = userId;
        this.data = data;
    }
}
