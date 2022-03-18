package com.example.btc;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommentsActivity extends FirebaseAuthentication {

    private TextView time;
    private TextView username;
    private Button heart;
    private TextView text;
    private TextInputLayout textEditor;
    private Confession model;
    private CommentsAdapter adapter;
    private boolean allowPost;
    private RecyclerView recyclerView;
    private TextView textview_empty_placeholder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        model = (Confession) getIntent().getSerializableExtra("postObject");

        initializeViews();

    }

    private void initializeViews() {
        Confession model = (Confession) getIntent().getSerializableExtra("postObject");
        time = findViewById(R.id.textView_comments_time);
        username = findViewById(R.id.textView_comments_username);
        text = findViewById(R.id.textview_comments_text);
        heart = findViewById(R.id.button_comments_heart);
        textEditor = findViewById(R.id.textInputLayout_comments_newcomment);

        username.setText(model.getDisplayName());
        text.setText(model.getText());
        setTime(time);
        setIfUserHeart(model);
        setHeartClickListener(heart);
        handleFirebaseQuery();
        setCommentPlaceHolder(model);
        setTextInputLayoutListeners(textEditor);
    }

    public void setTextInputLayoutListeners(TextInputLayout textEditor) {
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
                    textEditor.setEndIconTintList(getColorStateList(R.color.disabledSend));
                    textEditor.setHint(R.string.comments_textviewplaceholder);
                } else {
                    allowPost = true;
                    textEditor.setEndIconDrawable(R.drawable.ic_baseline_arrow_circle_up_24_success);
                    textEditor.setEndIconTintList(getColorStateList(R.color.BTCPrimary));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                setReplyTag(s);
            }
        });

        textEditor.setEndIconOnClickListener(view -> {
                    if (allowPost) {
                        textEditor.setEnabled(false);
                        addCommentToFirebase(textEditor.getEditText().getText().toString(), (String) textEditor.getTag());
                        textEditor.setTag(null);
                    }
                }
        );
    }

    public void setCommentPlaceHolder(Confession model) {
        int comments = model.getComments();
        textview_empty_placeholder = findViewById(R.id.textview_empty_placeholder);
        if (comments > 0) {
            textview_empty_placeholder.setText("");
            textview_empty_placeholder.setVisibility(View.GONE);
        } else {
            textview_empty_placeholder.setVisibility(View.VISIBLE);
            textview_empty_placeholder.setText(R.string.comments_empty_recyclerview);
        }
    }

    public void setHeartClickListener(Button heart) {
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

    public void setIfUserHeart(Confession model) {
        if (model.getHearts().contains(currentUser.getUid())) {
            ((MaterialButton) heart).setIconResource(R.drawable.ic_heart_filled);
        } else {
            ((MaterialButton) heart).setIconResource(R.drawable.ic_heart_outline);
        }
    }

    public void setTime(TextView time) {
        Date now = new Date(System.currentTimeMillis());
        long timeElapsed = getDateDiff(model.getDate(), now, TimeUnit.MINUTES);
        String timeLessThan60minutes = timeElapsed + " Minute" + ((timeElapsed == 1) ? "" : "s") + " Ago";
        String lessThan24Hours = timeElapsed / 60 + " Hour" + ((timeElapsed / 60 == 1) ? "" : "s") + " Ago";
        String MoreThan24Hours = timeElapsed / 1440 + " Day" + ((timeElapsed / 1440 == 1) ? "" : "s") + " Ago";

        if (timeElapsed < 60) {
            if (timeElapsed == 0) {
                time.setText(R.string.date_recent_post);
            } else {
                time.setText(timeLessThan60minutes);
            }
        } else if (timeElapsed < 1440) {
            time.setText(lessThan24Hours);
        } else {
            time.setText(MoreThan24Hours);
        }
    }

    public void setReplyTag(Spannable s) {
        String[] str;
        str = String.valueOf(s).split("\\s+");
        int size;
        if (str.length > 0 && str[0].length() > 0) {
            size = getReplyTagSize(str[0]);
        } else {
            textEditor.setHint(R.string.comments_textviewplaceholder);
            textEditor.setTag(null);
            return;
        }
        if (str[0].length() == size) {
            textEditor.setHint(R.string.comments_textviewplaceholder_reply);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.BTCPrimary)), 0, size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            textEditor.setHint(R.string.comments_textviewplaceholder);
            textEditor.setTag(null);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public int getReplyTagSize(String str) {
        if (str.charAt(0) == '@') {
            if (str.contains("BCIT#")) {
                return 12;
            } else if (str.contains("UBC#") || str.contains("SFU#")) {
                return 11;
            }
        }
        return 0;
    }

    private boolean isTextValid(CharSequence text) {
        return text.length() >= 1;
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

    private void addCommentToFirebase(String text, String tag) {
        Comment comment = new Comment(auth.getCurrentUser().getDisplayName(), text, new Date(), new ArrayList<>(), new ArrayList<>(), model.getDocumentId(), tag);
        model.addComment();

        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(positionStart);
            }
        };

        recyclerView.getAdapter().registerAdapterDataObserver(observer);

        db.collection("confessions")
                .document(model.getDocumentId())
                .collection("comments").add(comment).addOnSuccessListener(documentReference -> {
            textview_empty_placeholder.setText("");
            textview_empty_placeholder.setVisibility(View.GONE);
            textEditor.getEditText().setText("");
            textEditor.clearFocus();
            textEditor.setEnabled(true);
            hideKeyboard(this);
            recyclerView.getAdapter().unregisterAdapterDataObserver(observer);
        });

        db.collection("confessions")
                .document(model.getDocumentId())
                .update("comments", model.getComments());

        db.collection("confessions")
                .document(model.getDocumentId())
                .update("popularityIndex", model.getPopularityIndex());
    }


    private void handleFirebaseQuery() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("confessions")
                .document(model.getDocumentId())
                .collection("comments")
                .orderBy("voteCount", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new CommentsAdapter(options, model.getDisplayName(), auth.getCurrentUser().getDisplayName(), this.findViewById(R.id.textInputLayout_comments_newcomment));
        recyclerView = findViewById(R.id.recyclerView_comments);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
    }


    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

}