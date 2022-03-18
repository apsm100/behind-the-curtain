package com.example.btc;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

class FirebaseAuthentication extends AppCompatActivity{
    protected final FirebaseFirestore db;
    protected final FirebaseAuth auth;
    protected final FirebaseUser currentUser;

    public FirebaseAuthentication() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser =  FirebaseAuth.getInstance().getCurrentUser();
//        Clear local persistence data:
//        db.clearPersistence();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void addConfession(Confession confession, Callback callback, LinearProgressIndicator progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference docRef = db.collection("confessions").document();
        docRef.set(confession).addOnSuccessListener((OnSuccessListener<Object>) o -> {
            callback.call(o);
            progressBar.setVisibility(View.INVISIBLE);
        });
    }
}
