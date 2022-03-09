package com.example.btc;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Get input text
        mAuth = FirebaseAuth.getInstance();
        TextInputLayout textInputLayoutUsername = findViewById(R.id.AutoCompleteTextView_signup_school);
        TextInputLayout textInputLayoutPassword = findViewById(R.id.TextView_signup_confirm_password);

    }


    public void login(View view) {

        EditText editText_login_username = findViewById(R.id.editText_login_username);
        EditText editText_login_password = findViewById(R.id.editText_login_password);
        String email = editText_login_username.getText().toString() + "_@btc.com".trim();
        String password = editText_login_password.getText().toString();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            editText_login_username.setText("");
                            editText_login_password.setText("");
                        }
                    }
                });
    }


    public void signup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("Exit", false);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            System.out.println("New user signed in: " + currentUser.getEmail());
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            System.out.println("No user signed in");
        }
    }


}