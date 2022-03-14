package com.example.btc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewConfessionActivity extends FirebaseAuthentication {
    User userObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_confession);
        LinearProgressIndicator progressBar = findViewById(R.id.progressBar_new_confession);
        TextView usernameTextView = findViewById(R.id.textView_new_confession_username);
        Button postButton = findViewById(R.id.button_new_confession_post);
        postButton.setOnClickListener(this::createConfession);
        getUser((object -> {
            userObj = (User) object;
            usernameTextView.setText(userObj.getDisplayName());
        }), progressBar);


        Button cancelButton = findViewById(R.id.button_new_confession_cancel);
        cancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    public void createConfession(View view) {
        if (userObj == null) {
            return;
        }
        LinearProgressIndicator progressBar = findViewById(R.id.progressBar_new_confession);
        EditText editText = findViewById(R.id.editTextTextMultiLine_new_confession);

        // TEST COMMENT HEARTS
        ArrayList<String> hearts = new ArrayList<>();
//        comments.add(new Comment("123456", "Wassap", new Date(System.currentTimeMillis())));
//        comments.add(new Comment("123456", "Wassap", new Date(System.currentTimeMillis())));
//        comments.add(new Comment("123456", "Wassap", new Date(System.currentTimeMillis())));
//        hearts.add(new Heart("123456"));
//        hearts.add(new Heart("123456"));
//        hearts.add(new Heart("123456"));

        Confession confession = new Confession(userObj, editText.getText().toString(), hearts, new Date(System.currentTimeMillis()));
        addConfession(confession, (object -> {
            this.finish();
        }), progressBar);

    }

    public void closeClick(View view) {
        finish();
    }
}