package com.example.btc;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

abstract class FirebaseAuthentication extends AppCompatActivity{
    protected FirebaseFirestore db;
    protected FirebaseAuth auth;
    protected FirebaseUser currentUser;

    public FirebaseAuthentication() {
        db = FirebaseFirestore.getInstance();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        currentUser =  FirebaseAuth.getInstance().getCurrentUser();

    }


    @Override
    protected void onStart() {
        super.onStart();

//        Bundle extras = getIntent().getExtras();
//        boolean exit = true;
//        if (extras != null) {
//            exit = extras.getBoolean("Exit");
//        }
//        System.out.println(exit);
//        if (!exit) {
//            return;
//        }

    }
}