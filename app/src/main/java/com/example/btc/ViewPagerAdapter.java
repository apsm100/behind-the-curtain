package com.example.btc;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * A simple pager adapter that represents 5 ExampleFragment objects, in
 * sequence.
 */
class ViewPagerAdapter extends FragmentStateAdapter {

    Confession[] confessionsArray;

    public ViewPagerAdapter(FragmentActivity fa, Confession[] confessions) {
        super(fa);
        this.confessionsArray = confessions;
    }

    @Override
    public Fragment createFragment(int position) {
        return ItemsFragment.newInstance(confessionsArray);
    }

    @Override
    public int getItemCount() {
        return confessionsArray.length;
    }
}