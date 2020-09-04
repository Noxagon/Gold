package com.sp.gold.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kenilt.loopingviewpager.widget.LoopingViewPager;
import com.sp.gold.Adpaters.RewardCardAdapter;
import com.sp.gold.R;

import java.util.ArrayList;
import java.util.Arrays;

public class RewardCategoryFragment extends Fragment {
    private static final String TAG = "RewardCategoryFragment";
    private LoopingViewPager groceryPager, healthPager, homePager;
    private RewardCardAdapter adapter;

    private String[] groceryUrls = {
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/groceries%2Fmilk.jpg?alt=media&token=029428b1-d567-4597-a522-75210f24b9de",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/groceries%2Frice.jpg?alt=media&token=add6f251-1d78-413d-98a7-d21b073c77e8",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/groceries%2Foat.jpg?alt=media&token=262610e5-72e4-4adc-8733-5c774236111a"
    };

    private String[] groceryText = {
            "Farmhouse Fresh Milk",
            "Naturel Organic Brown Rice",
            "Quaker 100% Wholegrain Instant Oatmeal",
    };

    private Integer[] groceryPoints = {
            5,
            10,
            10
    };

    private String[] groceryCode = {
            "GR001",
            "GR002",
            "GR003"
    };

    private String[] healthUrls = {
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/health%2Fbrands.jpg?alt=media&token=58eae122-a797-48d4-b239-11a58c9da656",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/health%2Fcalcium.jpg?alt=media&token=fc0cd655-d63c-45ee-9de3-0f1eedc39f47",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/health%2Fgrain.jpg?alt=media&token=4351f58d-4da9-488b-9311-055aa7b89214"
    };

    private String[] healthText = {
            "Brand's Essence of Chicken",
            "Caltrate Plus Minerals 100 Tablets",
            "NH Nutri Grains Instant Multimix Grain"
    };

    private Integer[] healthPoints = {
            15,
            25,
            20
    };

    private String[] healthCode = {
            "HL001",
            "HL002",
            "HL003"
    };

    private String[] homeUrls = {
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/home%2Ffan.png?alt=media&token=0aa58e7a-af24-4cad-b559-6774c72cde0a",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/home%2Fosim.jpg?alt=media&token=9e96a401-68e2-47ce-bd21-a4167376e9a7",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/home%2Ftoilet.jpg?alt=media&token=d244ddda-7e26-4e06-8137-995334eba237"
    };

    private String[] homeText = {
            "Dyson Pure Cool™ air purifier tower fan",
            "OSIM uDivine V Massage Chair",
            "Carex E-Z Lock Raised Toilet Seat with Handles"
    };

    private Integer[] homePoints = {
            200,
            500,
            30
    };

    private String[] homeCode = {
            "HM001",
            "HM002",
            "HM003"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reward_category, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initGroceryPager(view);
        initHealthPager(view);
        initHomePager(view);
    }

    private void initGroceryPager(View view) {
        groceryPager = view.findViewById(R.id.grocery_pager);
        final ArrayList<String> mImages = new ArrayList(Arrays.asList(groceryUrls));
        final ArrayList<String> mNames = new ArrayList(Arrays.asList(groceryText));
        final ArrayList<Integer> mPoints = new ArrayList(Arrays.asList(groceryPoints));
        final ArrayList<String> mCode = new ArrayList(Arrays.asList(groceryCode));
        adapter = new RewardCardAdapter(getContext(), mImages, mNames, mPoints, mCode);
        groceryPager.setAdapter(adapter);
    }

    private void initHealthPager(View view) {
        healthPager = view.findViewById(R.id.health_pager);
        final ArrayList<String> mImages = new ArrayList(Arrays.asList(healthUrls));
        final ArrayList<String> mNames = new ArrayList(Arrays.asList(healthText));
        final ArrayList<Integer> mPoints = new ArrayList(Arrays.asList(healthPoints));
        final ArrayList<String> mCode = new ArrayList(Arrays.asList(healthCode));
        adapter = new RewardCardAdapter(getContext(), mImages, mNames, mPoints, mCode);
        healthPager.setAdapter(adapter);
    }

    private void initHomePager(View view) {
        homePager = view.findViewById(R.id.home_pager);
        final ArrayList<String> mImages = new ArrayList(Arrays.asList(homeUrls));
        final ArrayList<String> mNames = new ArrayList(Arrays.asList(homeText));
        final ArrayList<Integer> mPoints = new ArrayList(Arrays.asList(homePoints));
        final ArrayList<String> mCode = new ArrayList(Arrays.asList(homeCode));
        adapter = new RewardCardAdapter(getContext(), mImages, mNames, mPoints, mCode);
        homePager.setAdapter(adapter);
    }
}