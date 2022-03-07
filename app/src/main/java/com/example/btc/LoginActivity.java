package com.example.btc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get input text
        TextInputLayout textInputLayoutUsername = findViewById(R.id.TextView_login_username);
        TextInputLayout textInputLayoutPassword = findViewById(R.id.TextView_login_password);



    }





}