package com.example.btc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.FieldValue;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommentsActivity extends FirebaseAuthentication {

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
        long timeElapsed = getDateDiff(model.getDate(),now,TimeUnit.MINUTES);
        String timeLessThan60minutes = timeElapsed + " Minutes Ago";
        String lessThan24Hours = timeElapsed / 60 + " Hours Ago";
        String MoreThan24Hours = timeElapsed / 1440 + " Days Ago";

        if (timeElapsed < 60){
            time.setText(timeLessThan60minutes);
        }else if (timeElapsed < 1440){
            time.setText(lessThan24Hours);
        }else {
            time.setText(MoreThan24Hours);
        }


        if (model.getHearts().contains(currentUser.getUid())){
            ((MaterialButton) heart).setIconResource(R.drawable.ic_heart_filled);
        }else {
            ((MaterialButton) heart).setIconResource(R.drawable.ic_heart_outline);
        }

        heart.setOnClickListener(view -> {
            if (model.getHearts().contains(currentUser.getUid())) {
                model.removeHeart(currentUser.getUid());
                db.collection("confessions")
                        .document(model.getDocumentId())
                        .update("hearts", FieldValue.arrayRemove(currentUser.getUid()));
                ((MaterialButton) heart).setIconResource(R.drawable.ic_heart_outline);
            } else {
                model.addHeart(currentUser.getUid());
                db.collection("confessions")
                        .document(model.getDocumentId())
                        .update("hearts", FieldValue.arrayUnion(currentUser.getUid()));
                ((MaterialButton) heart).setIconResource(R.drawable.ic_heart_filled);
            }
            db.collection("confessions")
                    .document(model.getDocumentId())
                    .update("popularityIndex", model.getPopularityIndex());
        });

    }


    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    private void setProgressBar(Boolean value) {
        if (value) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}