package com.example.btc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommentsActivity extends AppCompatActivity {

    private TextView time;
    private TextView username;
    private Button heart;
    private TextView text;
    private LinearProgressIndicator progressBar;

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

        Confession model = (Confession) getIntent().getSerializableExtra("postObject");

        progressBar = findViewById(R.id.progressBar_comments);
        setProgressBar(false);

        time = findViewById(R.id.textView_comments_time);
        username = findViewById(R.id.textView_comments_username);
        text = findViewById(R.id.textview_comments_text);
        heart = findViewById(R.id.button_comments_heart);

        username.setText(model.getUser().getDisplayName());
        text.setText(model.getText());

        Date now = new Date(System.currentTimeMillis());
        System.out.println(model.getDate().toString());
        System.out.println(getDateDiff(model.getDate(),now,TimeUnit.HOURS));

    }


    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,timeUnit);
    }

    private void setProgressBar(Boolean value) {
        if (value) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}