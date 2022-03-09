package com.example.btc;

import java.io.Serializable;

public class Heart implements Serializable {
    private String userId;
    public Heart(String userId) {
        this.userId = userId;
    }
}
