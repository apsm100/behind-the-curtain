package com.example.btc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get input text
        TextInputLayout textInputLayoutUsername = findViewById(R.id.AutoCompleteTextView_signup_school);
        TextInputLayout textInputLayoutPassword = findViewById(R.id.TextView_signup_confirm_password);

    }


    public void login(View view){
        Intent intent=new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    public void signup(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }




}