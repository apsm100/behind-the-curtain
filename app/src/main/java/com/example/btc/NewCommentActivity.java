package com.example.btc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class NewCommentActivity extends AppCompatActivity {

    Confession model;
    protected FirebaseFirestore db;
    protected FirebaseAuth auth;
    protected FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser =  FirebaseAuth.getInstance().getCurrentUser();

        model = (Confession) getIntent().getSerializableExtra("postObject");

        SetupViews();
    }

    private void SetupViews(){

        Button button_new_comment_cancel = findViewById(R.id.button_new_comment_cancel);
        button_new_comment_cancel.setOnClickListener(view -> finish());

        Button button_new_comment_post = findViewById(R.id.button_new_comment_post);
        button_new_comment_post.setOnClickListener(view -> {

            button_new_comment_post.setEnabled(false);
            EditText editTextTextMultiLine_new_comment = findViewById(R.id.editTextTextMultiLine_new_comment);
            String commentData = editTextTextMultiLine_new_comment.getText().toString();
            if (!commentData.isEmpty()){
                System.out.println("sucessful click");
                db.collection("users").document(Objects.requireNonNull(currentUser.getEmail()).substring(0,6)).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        Comment comment = new Comment(document.getString("displayName"), commentData, new Date());

                        model.addComment(comment);

                        db.collection("confessions")
                                .document(model.getDocumentId())
                                .update("comments", FieldValue.arrayUnion(currentUser.getUid()));

                        button_new_comment_post.setEnabled(true);
                        Intent intent = new Intent(NewCommentActivity.this, CommentsActivity.class);
                        intent.putExtra("postObject", model);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

            }
            button_new_comment_post.setEnabled(true);
        });


    }
}