package com.example.btc;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends FirebaseAuthentication {

    LinearProgressIndicator progressBar;
    TextInputLayout usernameTextInputLayout;
    TextInputLayout passwordTextInputLayout;
    EditText usernameTextView;
    EditText passwordTextView;
    TextView errorMessageTextView;
    Button loginButton;
    Button signupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Get input text
        initializeViews();

    }

    private void initializeViews() {

        // PROGRESS BAR
        progressBar = findViewById(R.id.ProgressBar_login);
        setProgressBar(false);

        usernameTextInputLayout = findViewById(R.id.TextInputLayout_login_username);
        usernameTextView = usernameTextInputLayout.getEditText();
        usernameTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUsernameValid(s.toString())) {
                    usernameTextInputLayout.setError("Username must be exactly a 6 digit number");
                } else {
                    usernameTextInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        passwordTextInputLayout = findViewById(R.id.TextInputLayout_login_password);
        passwordTextView = passwordTextInputLayout.getEditText();
        passwordTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isPasswordValid(s.toString())) {
                    passwordTextInputLayout.setError("Password must be between 6 to 15 characters long");
                } else {
                    passwordTextInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        errorMessageTextView = findViewById(R.id.textView_login_miscerrormessage);

        loginButton = findViewById(R.id.button_login_login);
        loginButton.setOnClickListener(this::loginButtonClicked);

        signupButton = findViewById(R.id.button_login_signup);
        signupButton.setOnClickListener(this::signup);

    }

    private void loginButtonClicked(View view) {
        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        // Clear blocked error message if exits.
        errorMessageTextView.setText("");

        if (isValidLogin()) {
            String email = username + "@btc.com";
            disableLoginButton();
            setProgressBar(true);
            login(email, password);
        }
    }


    public boolean isValidLogin() {
        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        boolean isValid = true;

        if (!isUsernameValid(username)) {
            usernameTextInputLayout.setError("Username must be exactly a 6 digit number");
            isValid = false;
        } else {
            usernameTextInputLayout.setErrorEnabled(false);
        }

        if (!isPasswordValid(password)) {
            passwordTextInputLayout.setError("Password must be between 6 to 15 characters long");
            return false;
        } else {
            passwordTextInputLayout.setErrorEnabled(false);
        }

        return isValid;
    }

    private boolean isPasswordValid(String password) {
        // Matches exactly between 6 to 15 digits
        Pattern validPasswordPattern = Pattern.compile("(?=.*[0-9a-zA-Z]).{6,15}");
        return validPasswordPattern.matcher(password).matches();
    }

    private boolean isUsernameValid(String username) {
        // Matches exactly 6 digits
        Pattern validUsernamePattern = Pattern.compile("[0-9]{6}");
        return validUsernamePattern.matcher(username).matches();
    }

    private void disableLoginButton() {
        loginButton.setEnabled(false);
        loginButton.setAlpha((float) 0.2);
    }

    private void enableLoginButton() {
        loginButton.setEnabled(true);
        loginButton.setAlpha(1);
    }

    private void setProgressBar(Boolean value) {
        if (value) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    private void setErrorMessageFromException(Exception exception) {
        try {
            throw exception;
        } catch (FirebaseAuthInvalidUserException e) {
            usernameTextInputLayout.setError("The username was not found");
            usernameTextInputLayout.requestFocus();
            enableLoginButton();
            setProgressBar(false);
        } catch (FirebaseAuthInvalidCredentialsException e) {
            passwordTextInputLayout.setError("Sorry, your password was incorrect. " +
                    "Please double-check your password.");
            passwordTextInputLayout.requestFocus();
            enableLoginButton();
            setProgressBar(false);
        } catch (Exception e) {
            errorMessageTextView.setText(getString(R.string.login_blockedmessage));
            setProgressBar(false);
            enableLoginButton();
        }
    }

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        setErrorMessageFromException(task.getException());
                    }
                });
    }


    public void signup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }



    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null){
                Intent mainActivity = new Intent(this, HomeActivity.class);
                startActivity(mainActivity);
                finishAffinity();
            }
        });
    }
}