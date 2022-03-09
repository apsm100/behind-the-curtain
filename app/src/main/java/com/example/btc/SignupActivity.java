package com.example.btc;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Random;
import java.util.regex.Pattern;


public class SignupActivity extends FirebaseAuthentication {

    String[] listOfSchools = new String[]{"BCIT", "SFU", "UBC"};
    String currentlySelectedSchool;

    LinearProgressIndicator progressBar;
    AutoCompleteTextView autoCompleteTextViewSchool;
    TextInputLayout autoCompleteTextViewSchoolInputLayout;
    TextInputLayout usernameTextInputLayout;
    TextInputLayout passwordTextInputLayout;
    TextInputLayout passwordConfirmTextInputLayout;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeViews();
    }

    private void initializeViews() {

        // PROGRESS BAR
        progressBar = findViewById(R.id.ProgressBar_signup);
        showProgressBar(false);

        // SCHOOL NAME
        autoCompleteTextViewSchool = findViewById(R.id.AutoCompleteTextView_signup_school);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listOfSchools);
        autoCompleteTextViewSchool.setAdapter(adapter);
        autoCompleteTextViewSchool.setOnItemClickListener(this::schoolNameSelected);

        autoCompleteTextViewSchoolInputLayout = findViewById(R.id.AutoCompleteTextViewLayout_signup_school);

        // USERNAME //
        usernameTextInputLayout = findViewById(R.id.editTextLayout_signup_username);
        usernameTextInputLayout.getEditText().setText(getRandomNumberString());
        usernameTextInputLayout.setHelperText("Username should be a 6 digit number");
        usernameTextInputLayout.setFocusable(false);

        // PASSWORD
        passwordTextInputLayout = findViewById(R.id.editTextLayout_signup_password);
        passwordTextInputLayout.setHelperText("Password must be between 6 to 15 characters long");

        // CONFIRM PASSWORD
        passwordConfirmTextInputLayout = findViewById(R.id.editTextLayout_signup_confirm_password);
        passwordConfirmTextInputLayout.setHelperText("Password must match password entered above");

        // Cancel Button
        Button cancelButton = findViewById(R.id.button_signup_cancel);
        cancelButton.setOnClickListener(this::cancelButtonClicked);

        // Signup Button
        signupButton = findViewById(R.id.button_signup_signup);
        signupButton.setOnClickListener(this::signUpButtonClicked);

    }

    private void schoolNameSelected(AdapterView<?> schoolNameList, View view, int position, long l) {
        currentlySelectedSchool = schoolNameList.getItemAtPosition(position).toString();
    }

    private void cancelButtonClicked(View view) {
        finish();
    }


    private void signUpButtonClicked(View view) {
        String username = usernameTextInputLayout.getEditText().getText().toString();
        String password = passwordTextInputLayout.getEditText().getText().toString();
        String confirmPassword = passwordConfirmTextInputLayout.getEditText().getText().toString();

        boolean allValid = true;

        if (currentlySelectedSchool == null) {
            autoCompleteTextViewSchoolInputLayout.setError("You must select a school");
            allValid = false;
        } else {
            autoCompleteTextViewSchoolInputLayout.setError("");
        }

        if (!isUsernameValid(username)) {
            usernameTextInputLayout.setError("Username must be exactly a 6 digit number");
            allValid = false;
        } else {
            usernameTextInputLayout.setErrorEnabled(false);
        }

        if (!isPasswordValid(password)) {
            passwordTextInputLayout.setError("Password must be between 6 to 15 characters long");
            allValid = false;
        } else {
            passwordTextInputLayout.setErrorEnabled(false);
        }


        if (!isPasswordMatch(password, confirmPassword)) {
            passwordConfirmTextInputLayout.setError("Passwords do not match");
            allValid = false;
        } else {
            passwordConfirmTextInputLayout.setErrorEnabled(false);
        }

        if (allValid) {
            String email = username + "@btc.com";
            String displayName = currentlySelectedSchool + "#" + username;
            User user = new User(username, email, currentlySelectedSchool, displayName);
            disableSignUpButton();
            showProgressBar(true);
            registerUser(user, password);
        }

    }

    private void showProgressBar(Boolean value) {
        if (value) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setErrorMessageFromException(Exception exception) {

        try {
            throw exception;
        } catch (FirebaseAuthUserCollisionException e) {
            usernameTextInputLayout.setError("This username is already in use");
            usernameTextInputLayout.requestFocus();
            enableSignUpButton();
            showProgressBar(false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void disableSignUpButton() {
        signupButton.setEnabled(false);
        signupButton.setAlpha((float) 0.2);
    }

    private void enableSignUpButton() {
        signupButton.setEnabled(true);
        signupButton.setAlpha(1);
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


    private boolean isPasswordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword) & password.length() > 0;
    }


    protected void registerUser(User user, String password) {
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showProgressBar(true);
                        addUserToCollection(user);
                    } else {
                        setErrorMessageFromException(task.getException());
                    }
                });

    }


    private void addUserToCollection(User user) {
        db.collection("users")
                .document(user.getUsername())
                .set(user)
                .addOnSuccessListener(success -> {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(R.id.LinearLayout_signupactivity), e.toString(), Snackbar.LENGTH_SHORT).show();
                    EditText editTextName = findViewById(R.id.editText_signup_username);
                    editTextName.setText(user.getUsername());
                });

        showProgressBar(false);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.valueOf(number);
    }
}