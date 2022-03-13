package com.example.btc;

import java.io.Serializable;

public class Heart implements Serializable {
    private String userId;
    public Heart(){}
    public Heart(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
