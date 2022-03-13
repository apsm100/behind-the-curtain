package com.example.btc;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

class FirebaseAuthentication extends AppCompatActivity{
    protected FirebaseFirestore db;
    protected FirebaseAuth auth;
    protected FirebaseUser currentUser;
    protected String username;

    public FirebaseAuthentication() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser =  FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            username = currentUser.getEmail().substring(0, 6);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        auth = FirebaseAuth.getInstance();
//        currentUser =  FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null){
//            username = currentUser.getEmail().substring(0, 6);
//        }
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
        DocumentReference docRef = db.collection("confessions").document();
        docRef.set(confession).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                callback.call(o);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

//    public void getConfessions(Callback callback, LinearProgressIndicator progressBar) {
//        progressBar.show();
//        db.collection("confessions").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot documentSnapshots) {
//                if (!documentSnapshots.isEmpty()) {
//                    List<Confession> confessions = documentSnapshots.toObjects(Confession.class);
//                    callback.call(confessions);
//                    progressBar.hide();
//                }
//            }
//        });
//
//    }

    public void addConfessionsListener(Callback callback, LinearProgressIndicator progressBar) {
        db.collection("confessions").orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        if (!value.isEmpty()) {
                            List<Confession> confessions = value.toObjects(Confession.class);
                            callback.call(confessions);
                            progressBar.hide();
                        }
                    }
                });
    }

    public String getUsername() {
        return username;
    }

    public void updateHearts(Callback callback, String confessionId, ArrayList<Heart> hearts) {
        db.collection("confessions").document(confessionId).update("hearts", hearts).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                callback.call(o);
            }
        });
    }


}
