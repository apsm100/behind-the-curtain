package com.example.btc;

import java.io.Serializable;
import java.util.Date;

public class Comment  implements Serializable {
    private final String userId;
    private final Date date;
    private final String data;

    public Comment(String userId, String data) {
        this.date = new Date(System.currentTimeMillis());
        this.userId = userId;
        this.data = data;
    }
}
