package com.example.btc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String[] heartsArray = new String[]{"user1", "user2", "user 3", "user 4"};
        String[] commentsArray = new String[]{"user5", "user55", "user23423", "user234234"};


        Confession[] confessions = {
                new Confession("BCIT#102043", "THIS IS MESSAGE 1", heartsArray, commentsArray),
                new Confession("SFU#3849839", "THIS IS MESSAGE 2", heartsArray, commentsArray)
        };

//        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.FragmentContainerView_home, ItemsFragment.newInstance(confessions));
//        fragmentTransaction.commit();
//


//        ViewPager2 viewPager2 = findViewById(R.id.ViewPager_home);
//        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, confessions);
//        viewPager2.setAdapter(viewPagerAdapter);
//
//        TabLayout tabLayout = findViewById(R.id.tabLayout_home);
//        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
//                new TabLayoutMediator.TabConfigurationStrategy() {
//                    @Override
//                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//
//                    }
//                });
//
//        tabLayoutMediator.attach();

    }
}