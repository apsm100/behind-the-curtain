package com.example.btc;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

abstract class FirebaseAuthentication extends AppCompatActivity{
    protected FirebaseFirestore db;
    protected FirebaseAuth auth;
    protected FirebaseUser currentUser;
    protected String username;

    public FirebaseAuthentication() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        currentUser =  FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            username = currentUser.getEmail().substring(0, 6);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public void getUser(Callback callback, LinearProgressIndicator progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                callback.call(user);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void addConfession(Confession confession, Callback callback, LinearProgressIndicator progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference docRef = db.collection("posts").document();
        docRef.set(confession).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                callback.call(o);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}
