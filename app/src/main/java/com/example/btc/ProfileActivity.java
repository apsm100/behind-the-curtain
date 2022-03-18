package com.example.btc;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class ProfileActivity extends FirebaseAuthentication {

    LinearProgressIndicator progressBar;
    TextInputLayout usernameTextInputLayout;
    TextInputLayout oldPasswordTextInputLayout;
    TextInputLayout newPasswordTextInputLayout;
    EditText usernameTextView;
    EditText oldPasswordTextView;
    EditText newPasswordTextView;
    Button updateButton;
    Button cancelButton;
    Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeViews();
    }

    private void disableUpdateButton() {
        updateButton.setEnabled(false);
        updateButton.setAlpha((float) 0.2);
    }

    private void enableUpdateButton() {
        updateButton.setEnabled(true);
        updateButton.setAlpha(1);
    }


    private void setErrorMessageFromException(Exception exception) {
        try {
            throw exception;
        }
        catch (FirebaseAuthInvalidCredentialsException e) {
            oldPasswordTextInputLayout.setError("Your current password is incorrect.");
            oldPasswordTextInputLayout.requestFocus();
            enableUpdateButton();
            setProgressBar(false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Snackbar.make(findViewById(R.id.ContraintLayout_profile),
                    Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
            disableUpdateButton();
            setProgressBar(false);
        }
    }

    private void updateButtonClicked(View view) {
        if (isValidProfile()) {
            setProgressBar(true);
            String oldPassword = oldPasswordTextView.getText().toString();
            reAuthUser(currentUser.getEmail(), oldPassword);
        }
    }

    private void updatePassword(){
        String newPassword = newPasswordTextView.getText().toString();
        currentUser.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(findViewById(R.id.ContraintLayout_profile),
                                "Password Changed Successfully.", Snackbar.LENGTH_SHORT).show();
                        resetTextViews();
                    } else {
                        setErrorMessageFromException(task.getException());
                    }
                });
    }

    private void resetTextViews(){

        oldPasswordTextView.setText("");
        newPasswordTextView.setText("");
        oldPasswordTextInputLayout.setError(null);
        newPasswordTextInputLayout.setError(null);
        oldPasswordTextInputLayout.clearFocus();
        newPasswordTextInputLayout.clearFocus();
    }

    private void cancelButtonClicked(View view) {
        finish();
    }

    private void signOutButtonClicked(View view) {
        auth.signOut();
    }

    private void initializeViews() {

        // PROGRESS BAR
        progressBar = findViewById(R.id.ProgressBar_profile);
        setProgressBar(false);

        // USERNAME //
        usernameTextInputLayout = findViewById(R.id.TextInputLayout_profile_username);
        usernameTextView = usernameTextInputLayout.getEditText();
        Objects.requireNonNull(usernameTextView).setText(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser())
                .getDisplayName()).split("#")[1]);

        oldPasswordTextInputLayout = findViewById(R.id.TextInputLayout_profile_oldpassword);
        oldPasswordTextInputLayout.setHelperText("Password must be between 6 to 15 characters long");
        oldPasswordTextView = oldPasswordTextInputLayout.getEditText();
        Objects.requireNonNull(oldPasswordTextView).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isValidProfile();
            }
        });


        newPasswordTextInputLayout = findViewById(R.id.TextInputLayout_profile_newpassword);
        newPasswordTextInputLayout.setHelperText("Password must be between 6 to 15 characters long");
        newPasswordTextView = newPasswordTextInputLayout.getEditText();
        Objects.requireNonNull(newPasswordTextView).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isValidProfile();
            }
        });


        updateButton = findViewById(R.id.Button_profile_update);
        cancelButton = findViewById(R.id.Button_profile_cancel);
        signOutButton = findViewById(R.id.Button_profile_signout);


        updateButton.setOnClickListener(this::updateButtonClicked);
        cancelButton.setOnClickListener(this::cancelButtonClicked);
        signOutButton.setOnClickListener(this::signOutButtonClicked);

    }

    private void setProgressBar(Boolean value) {
        if (value) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    public boolean isValidProfile() {
        String oldPassword = oldPasswordTextView.getText().toString();
        String newPassword = newPasswordTextView.getText().toString();

        boolean isValid = true;

        if (!isPasswordValid(oldPassword)) {
            oldPasswordTextInputLayout.setError("Password must be between 6 to 15 characters long");
            isValid = false;
        } else {
            oldPasswordTextInputLayout.setErrorEnabled(false);
        }

        if (!isPasswordValid(newPassword)) {
            newPasswordTextInputLayout.setError("Password must be between 6 to 15 characters long");
            isValid = false;
        } else {
            newPasswordTextInputLayout.setErrorEnabled(false);
        }

        return isValid;
    }


    private boolean isPasswordValid(String password) {
        // Matches exactly between 6 to 15 digits
        Pattern validPasswordPattern = Pattern.compile("(?=.*[0-9a-zA-Z]).{6,15}");
        return validPasswordPattern.matcher(password).matches();
    }


    private void reAuthUser(String email, String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        updatePassword();
                    }else {
                        oldPasswordTextInputLayout.setError("Current password is incorrect");
                        setProgressBar(false);
                    }
                });
    }

    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null){
                Intent loginActivity = new Intent(this, LoginActivity.class);
                loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginActivity);
            }
        });
    }
}