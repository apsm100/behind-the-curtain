package com.example.btc;

import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
        setViewAdapter();
    }


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

    public void setViewAdapter() {
        ViewPager2 viewPager2 = findViewById(R.id.ViewPager_home);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.TabLayout_home);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    if (position == 0){
                        tab.setText("Popular");
                        tab.setIcon(R.drawable.ic_baseline_trending_up_24);
                    }else {
                        tab.setText("Recent");
                        tab.setIcon(R.drawable.ic_baseline_refresh_24);

                    }
                });

        tabLayoutMediator.attach();
    }



}