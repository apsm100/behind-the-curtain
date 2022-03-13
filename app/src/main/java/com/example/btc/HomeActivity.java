package com.example.btc;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends FirebaseAuthentication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button profileButton = findViewById(R.id.button_home_profile);
        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        });

        Button newConfessionButton = findViewById(R.id.button_home_newconfession);
        newConfessionButton.setOnClickListener(this::newConfession);
    }


    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null){
                Intent loginActivity = new Intent(this, HomeActivity.class);
                loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginActivity);
            }
        });
        loadConfessions();
    }

    public void newConfession(View view) {
        Intent intent = new Intent(HomeActivity.this, NewConfessionActivity.class);
        startActivity(intent);
    }

    public void loadConfessions() {
        Query query = db.collection("confessions").orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Confession> options = new FirestoreRecyclerOptions.Builder<Confession>()
                .setQuery(query, Confession.class)
                .build();
        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<Confession, ConfessionsAdapter.ViewHolder>(options) {
            @Override
            public void onBindViewHolder(ConfessionsAdapter.ViewHolder viewHolder, int position, Confession model) {
                FirebaseAuthentication firebaseAuthentication = new FirebaseAuthentication();
                Button heartButton = viewHolder.getHeart();
                // Get element from your dataset at this position and replace the
                // contents of the view with that element
                viewHolder.getUsername().setText(model.getUser().getDisplayName());
                viewHolder.getText().setText(model.getText());
                viewHolder.getComment().setText(String.valueOf(model.getComments().size()));
                heartButton.setText(String.valueOf(model.getHearts().size()));

                ArrayList<String> heartsList = model.getHearts();
                String userId = firebaseAuthentication.currentUser.getUid();
                String documentId = model.getDocumentId();
                FirebaseFirestore db = firebaseAuthentication.db;

                updateHeartIcon(heartsList, heartButton, userId);

                viewHolder.getHeart().setOnClickListener(view -> {
                    if (heartsList.contains(userId)) {
                        heartsList.remove(userId);
                        db.collection("confessions")
                                .document(documentId)
                                .update("hearts", FieldValue.arrayRemove(userId));
                    } else {
                        heartsList.add(userId);
                        db.collection("confessions")
                                .document(documentId)
                                .update("hearts", FieldValue.arrayUnion(userId));
                    }
                });
            }

            private void updateHeartIcon(ArrayList<String> heartsList, Button heartButton, String userId) {
                if (heartsList.contains(userId)) {
                    heartButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.heart_filled, 0, 0, 0);
                } else {
                    heartButton
                            .setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    R.drawable.heart_outline, 0, 0, 0);
                }
            }

            @Override
            public ConfessionsAdapter.ViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_confession, group, false);
                return new ConfessionsAdapter.ViewHolder(view);
            }
        };
        RecyclerView recyclerView = findViewById(R.id.recyclerView_home);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();

//        TabLayout tabLayout = findViewById(R.id.TabLayout_home);
//        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
//                new TabLayoutMediator.TabConfigurationStrategy() {
//                    @Override
//                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                        if (position == 0){
//                            tab.setText("Popular");
//                            tab.setIcon(R.drawable.ic_baseline_trending_up_24);
//                        }else {
//                            tab.setText("Popular");
//                            tab.setIcon(R.drawable.ic_baseline_refresh_24);
//
//                        }
//                    }
//                });
//
//        tabLayoutMediator.attach();
    }




}