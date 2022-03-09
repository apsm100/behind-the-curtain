package com.example.btc;

import java.util.Date;

public class Comment {
    private final String userId;
    private final Date date;
    private final String data;

    public Comment(String userId, String data) {
        this.date = new Date(System.currentTimeMillis());
        this.userId = userId;
        this.data = data;
    }
}
