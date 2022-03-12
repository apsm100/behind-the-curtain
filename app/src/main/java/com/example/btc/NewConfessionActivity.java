package com.example.btc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NewConfessionActivity extends FirebaseAuthentication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_confession);
        TextView usernameTextView = findViewById(R.id.textView_new_confession_username);
        getUser(object -> {
            User user = (User) object;
            usernameTextView.setText(user.getDisplayName());
        });

    }

    public void closeClick(View view) {
        finish();
    }
}