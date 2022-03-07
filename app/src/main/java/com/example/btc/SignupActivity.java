package com.example.btc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        String[] listOfSchools = new String[]{"BCIT", "SFU", "UBC"};
        AutoCompleteTextView autoCompleteTextViewUserName = findViewById(R.id.AutoCompleteTextView_signup_school);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listOfSchools);
        autoCompleteTextViewUserName.setAdapter(adapter);

    }
}