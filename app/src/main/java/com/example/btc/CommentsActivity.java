package com.example.btc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommentsActivity extends FirebaseAuthentication {

    public final static String modelKey = "postObject";
    private TextView time;
    private TextView username;
    private Button heart;
    private TextView text;
    private LinearProgressIndicator progressBar;
    private Confession model;
    private CommentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        model = (Confession) getIntent().getSerializableExtra("postObject");

        initializeViews();

    }

    private void initializeViews(){
        Button closeButton = findViewById(R.id.button_comments_done);
        closeButton.setOnClickListener(view -> finish());

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

        Button button_comments_add = findViewById(R.id.button_comments_report);
        button_comments_add.setOnClickListener(view -> {

            Intent intent = new Intent(CommentsActivity.this, NewCommentActivity.class);
            intent.putExtra(modelKey, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });

        int comments = model.getComments();

        TextView textview_empty_placeholder = findViewById(R.id.textview_empty_placeholder);
        if (comments > 0 ){
            textview_empty_placeholder.setText("");
            textview_empty_placeholder.setVisibility(View.GONE);
        } else {
            textview_empty_placeholder.setVisibility(View.VISIBLE);
            textview_empty_placeholder.setText("No Comments Yet...");
        }

        handleFirebaseQuery();
    }


    private void handleFirebaseQuery(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("confessions")
                .document(model.getDocumentId())
                .collection("comments")
                .orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class).setLifecycleOwner(this)
                .build();

        adapter = new CommentsAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_comments);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
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