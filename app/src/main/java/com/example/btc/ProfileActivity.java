package com.example.btc;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button profile_button_cancel = findViewById(R.id.profile_button_cancel);
        profile_button_cancel.setOnClickListener(view -> finish());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = "";
        if (currentUser != null && currentUser.getEmail() != null) {
            email = currentUser.getEmail();
        }
        email = email.substring(0, 6);

        EditText editText_profile_username = findViewById(R.id.editText_profile_username);
        editText_profile_username.setText(email);

        Button profile_button_update = findViewById(R.id.profile_button_update);
        profile_button_update.setOnClickListener(view -> {
            EditText editText_profile_newPassword = findViewById(R.id.editText_profile_newpassword);
            String newPassword = editText_profile_newPassword.getText().toString();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user!= null){
                reAuth(user.getEmail());
                user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("Password has been changed.");
                        } else {
                            System.out.println("Failed to change password.");
                            System.out.println(task.getResult());
                        }
                    });
            }
            editText_profile_newPassword.setText("");
        });

    }

    private void reAuth(String email) {
        EditText editText_profile_password = findViewById(R.id.editText_profile_password);
        String oldPassword = editText_profile_password.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, oldPassword);
        assert user != null;
        System.out.println(email + " " + oldPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");
                    }
                });
        editText_profile_password.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            System.out.println("New user signed in: " + currentUser.getEmail());
            Button signOut = findViewById(R.id.profile_button_signout);
            signOut.setOnClickListener(view -> {
                FirebaseAuth.getInstance().signOut();
                ProfileActivity.this.finish();
            });
        } else {
            System.out.println("No user signed in\nBringing back to sign in page");
            super.finish();
        }
    }
}