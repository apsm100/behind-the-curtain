package com.example.btc;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.resources.MaterialResources;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CommentsActivity extends FirebaseAuthentication {

    private TextView time;
    private TextView username;
    private Button heart;
    private TextView text;
    private TextInputLayout textEditor;
    private LinearProgressIndicator progressBar;
    private Confession model;
    private CommentsAdapter adapter;
    private boolean allowPost;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        model = (Confession) getIntent().getSerializableExtra("postObject");

        initializeViews();

    }

    private void initializeViews() {
        Button closeButton = findViewById(R.id.button_comments_done);
        closeButton.setOnClickListener(view -> finish());

        Confession model = (Confession) getIntent().getSerializableExtra("postObject");

        progressBar = findViewById(R.id.progressBar_comments);
        setProgressBar(false);

        time = findViewById(R.id.textView_comments_time);
        username = findViewById(R.id.textView_comments_username);
        text = findViewById(R.id.textview_comments_text);
        heart = findViewById(R.id.button_comments_heart);
        textEditor = findViewById(R.id.textInputLayout_comments_newcomment);

        username.setText(model.getUser().getDisplayName());
        text.setText(model.getText());

        Date now = new Date(System.currentTimeMillis());
        long timeElapsed = getDateDiff(model.getDate(), now, TimeUnit.MINUTES);
        String timeLessThan60minutes = timeElapsed + " Minutes Ago";
        String lessThan24Hours = timeElapsed / 60 + " Hours Ago";
        String MoreThan24Hours = timeElapsed / 1440 + " Days Ago";

        if (timeElapsed < 60) {
            time.setText(timeLessThan60minutes);
        } else if (timeElapsed < 1440) {
            time.setText(lessThan24Hours);
        } else {
            time.setText(MoreThan24Hours);
        }


        if (model.getHearts().contains(currentUser.getUid())) {
            ((MaterialButton) heart).setIconResource(R.drawable.ic_heart_filled);
        } else {
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

        int comments = model.getComments();

        TextView textview_empty_placeholder = findViewById(R.id.textview_empty_placeholder);
        if (comments > 0) {
            textview_empty_placeholder.setText("");
            textview_empty_placeholder.setVisibility(View.GONE);
        } else {
            textview_empty_placeholder.setVisibility(View.VISIBLE);
            textview_empty_placeholder.setText("No Comments Yet...");
        }

        handleFirebaseQuery();

        textEditor.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isTextValid(s)) {
                    allowPost = false;
                    textEditor.setEndIconDrawable(R.drawable.ic_baseline_arrow_circle_up_24);
                    textEditor.setEndIconTintList(null);

                } else {
                   allowPost = true;
                    textEditor.setEndIconDrawable(R.drawable.ic_baseline_arrow_circle_up_24_success);
                    textEditor.setEndIconTintList(getColorStateList(R.color.BTCPrimary));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        textEditor.setEndIconOnClickListener(view ->{
            if (allowPost){
                textEditor.setEnabled(false);
                setProgressBar(true);
                addCommentToFirebase(textEditor.getEditText().getText().toString());
            }
        }
        );

    }

    private boolean isTextValid(CharSequence text){

        if (text.length() < 1){
            return false;
        }

        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }


    private void addCommentToFirebase(String text) {
        Comment comment = new Comment(model.getUser().getDisplayName(), text, new Date());

        model.addComment();

        db.collection("confessions")
                .document(model.getDocumentId())
            .collection("comments").add(comment).addOnSuccessListener(documentReference -> {
            textEditor.getEditText().setText("");
            textEditor.clearFocus();
            textEditor.setEnabled(true);
            hideKeyboard(this);
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

        });

        db.collection("confessions")
                .document(model.getDocumentId())
                .update("comments", model.getComments());

        db.collection("confessions")
                .document(model.getDocumentId())
                .update("popularityIndex", model.getPopularityIndex());

        setProgressBar(false);

    }



    private void handleFirebaseQuery() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("confessions")
                .document(model.getDocumentId())
                .collection("comments")
                .orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new CommentsAdapter(options);
        recyclerView = findViewById(R.id.recyclerView_comments);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
    }


    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private void setProgressBar(Boolean value) {
        if (value) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}