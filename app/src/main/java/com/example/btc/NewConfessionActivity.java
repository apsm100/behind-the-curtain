package com.example.btc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class NewConfessionActivity extends FirebaseAuthentication {
    User userObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_confession);
        LinearProgressIndicator progressBar = findViewById(R.id.progressBar_new_confession);
        TextView usernameTextView = findViewById(R.id.textView_new_confession_username);
        getUser((object -> {
            userObj = (User) object;
            usernameTextView.setText(userObj.getDisplayName());
        }), progressBar);

    }

    public void closeClick(View view) {
        finish();
    }
}