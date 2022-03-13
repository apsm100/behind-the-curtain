package com.example.btc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends FirebaseAuthentication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadConfessions();

        Button profileButton = findViewById(R.id.button_home_profile);
        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
            profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
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
    }

    public void newConfession(View view) {
        Intent intent = new Intent(HomeActivity.this, NewConfessionActivity.class);
        startActivity(intent);
    }

    public void loadConfessions() {
        LinearProgressIndicator progressBar = findViewById(R.id.progressBar_home);
        addConfessionsListener((objects -> {
            ArrayList<Confession> confessions = (ArrayList<Confession>) objects;
            setViewAdapter(confessions.toArray(new Confession[0]));
        }), progressBar);
    }

    public void setViewAdapter(Confession[] confessions) {
        ViewPager2 viewPager2 = findViewById(R.id.ViewPager_home);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, confessions);
        viewPager2.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.TabLayout_home);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0){
                            tab.setText("Popular");
                            tab.setIcon(R.drawable.ic_baseline_trending_up_24);
                        }else {
                            tab.setText("Popular");
                            tab.setIcon(R.drawable.ic_baseline_refresh_24);

                        }
                    }
                });

        tabLayoutMediator.attach();
    }


}