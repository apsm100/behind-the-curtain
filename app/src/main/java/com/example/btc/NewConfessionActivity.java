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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_confession);
        LinearProgressIndicator progressBar = findViewById(R.id.progressBar_new_confession);
        TextView usernameTextView = findViewById(R.id.textView_new_confession_username);
        Button postButton = findViewById(R.id.button_new_confession_post);
        postButton.setOnClickListener(this::createConfession);

        usernameTextView.setText(auth.getCurrentUser().getDisplayName());

        Button cancelButton = findViewById(R.id.button_new_confession_cancel);
        cancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    public void createConfession(View view) {
        LinearProgressIndicator progressBar = findViewById(R.id.progressBar_new_confession);
        EditText editText = findViewById(R.id.editTextTextMultiLine_new_confession);

        ArrayList<String> hearts = new ArrayList<>();

        Confession confession = new Confession(auth.getCurrentUser().getDisplayName(), editText.getText().toString(), hearts, new Date(System.currentTimeMillis()));
        addConfession(confession, (object -> {
            this.finish();
        }), progressBar);

    }

    public void closeClick(View view) {
        finish();
    }
}