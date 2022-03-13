package com.example.btc;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * A simple pager adapter that represents 5 ExampleFragment objects, in
 * sequence.
 */
class ViewPagerAdapter extends FragmentStateAdapter {



    public ViewPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        return ItemsFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}