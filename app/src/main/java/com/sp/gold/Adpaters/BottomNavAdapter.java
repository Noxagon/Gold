package com.sp.gold.Adpaters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sp.gold.Fragments.RewardCategoryFragment;
import com.sp.gold.Fragments.RewardVoucherFragment;
import com.sp.gold.Fragments.RewardStoreFragment;

public class BottomNavAdapter extends FragmentStateAdapter {

    private static final String TAG = "ScreenSlidePagerAdapter";
    private static final int NUM_PAGES = 3;

    public BottomNavAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        //return new RegisterPageFragment();
        switch (position) {
            case 0:
                return new RewardCategoryFragment();
            case 1:
                return new RewardStoreFragment();
            case 2:
                return new RewardVoucherFragment();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}