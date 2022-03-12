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

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Heart[] heartsArray = new Heart[]{new Heart("abcdefg"), new Heart("abcdefg")};
        Comment[] commentsArray = new Comment[]{new Comment("abcedfg", "Wow this is a great app.")};


        Confession[] confessions = {
                new Confession("BCIT#102043", "THIS IS MESSAGE 1", commentsArray, heartsArray),
                new Confession("SFU#3849839", "THIS IS MESSAGE 2", commentsArray, heartsArray)
        };


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

        Button profileButton = findViewById(R.id.button_home_profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            System.out.println("New user signed in: " + currentUser.getEmail());
        } else{
            System.out.println("No user signed in\nBringing back to sign in page");
            finish();
        }
    }

    public void newConfession(View view) {
        Intent intent = new Intent(HomeActivity.this, NewConfessionActivity.class);
        startActivity(intent);
    }
}