package com.example.btc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CommentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        initializeViews();
    }

    private void initializeViews(){
        Button closeButton = findViewById(R.id.button_comments_close);
        closeButton.setOnClickListener(view -> {
            finish();
        });
    }
}