package com.sp.gold.Adpaters;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sp.gold.Fragments.RegisterNRICFragment;
import com.sp.gold.Fragments.RegisterPhoneFragment;
import com.sp.gold.Fragments.RegisterVerifyFragment;

public class RegisterAdapter extends FragmentStateAdapter {
    private static final String TAG = "RegisterAdapter";
    private static final int NUM_PAGES = 3;

    public RegisterAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        //return new RegisterPageFragment();
        switch (position) {
            case 0:
                return new RegisterNRICFragment();
            case 1:
                return new RegisterPhoneFragment();
            case 2:
                return new RegisterVerifyFragment();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}