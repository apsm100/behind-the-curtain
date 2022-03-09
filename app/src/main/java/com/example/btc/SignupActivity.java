package com.example.btc;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class SignupActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    String[] listOfSchools = new String[]{"BCIT", "SFU", "UBC"};
    String currentlySelectedSchool;
    String customToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
//        Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

//        Generate custom token
        customToken = generateString();

        EditText editTextName = findViewById(R.id.editText_signup_username);
        editTextName.setText(generateString());
        editTextName.setFocusable(false);

        AutoCompleteTextView autoCompleteTextViewUserName = findViewById(R.id.AutoCompleteTextView_signup_school);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listOfSchools);
        autoCompleteTextViewUserName.setAdapter(adapter);
        autoCompleteTextViewUserName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentlySelectedSchool = autoCompleteTextViewUserName.getAdapter().getItem(i).toString();
            }
        });

        Button cancelButton = findViewById(R.id.button_signup_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();
        boolean exit = true;
        if (extras != null) {
            exit = extras.getBoolean("Exit");
        }
        System.out.println(exit);
        if (!exit) {
            return;
        }

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            System.out.println("New user signed in: " + currentUser.getEmail());
        } else {
            System.out.println("No user signed in\nBringing back to sign in page");
            super.finish();
        }
    }

    public void onClick(View v) {

        EditText editTextName = findViewById(R.id.editText_signup_username);
        String name = editTextName.getText().toString();
        EditText editTextPassword = findViewById(R.id.editText_signup_password);
        String password = editTextPassword.getText().toString();

        System.out.println(name);
        System.out.println(password);

        if (name.isEmpty() || password.isEmpty() || currentlySelectedSchool == null) {

            Snackbar.make(findViewById(R.id.LinearLayout_signupactivity),
                    "Please fill all fields", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String customEmail = name + "_" + "@btc.com";
        mAuth.createUserWithEmailAndPassword(customEmail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Map<String, Object> user = new HashMap<>();
                            user.put("Username", customToken);
                            user.put("Email", customEmail);
                            user.put("School", currentlySelectedSchool);
                            user.put("DisplayName", currentlySelectedSchool + "#" + customToken);

                            db.collection("users")
                                    .document(customToken)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "User added!");
                                            Log.d(TAG, "createUserWithEmail:success");
                                            customToken = generateString();
                                            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error, User not added", e);
                                            Snackbar.make(findViewById(R.id.LinearLayout_signupactivity), e.toString(), Snackbar.LENGTH_LONG).show();
                                            EditText editTextName = findViewById(R.id.editText_signup_username);
                                            editTextName.setText(customToken);
                                        }
                                    });
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });


    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString().substring(0, 12);
        return uuid.replace("-", "");
    }
}