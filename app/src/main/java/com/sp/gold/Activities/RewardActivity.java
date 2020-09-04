package com.sp.gold.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sp.gold.Adpaters.BottomNavAdapter;
import com.sp.gold.R;

import java.lang.reflect.Field;

public class RewardActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager2;
    private BottomNavAdapter bottomNavAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        viewPager2 = findViewById(R.id.reward_pager);
        bottomNavigationView = findViewById(R.id.reward_nav);

        bottomNavAdapter = new BottomNavAdapter(this);
        viewPager2.setAdapter(bottomNavAdapter);

        initListener();
        reduceDragSensitivity();
        bottomNavigationView.setSelectedItemId(R.id.menu_store);
    }

    private void reduceDragSensitivity() {
        try {
            Field ff = ViewPager2.class.getDeclaredField("mRecyclerView") ;
            ff.setAccessible(true);
            RecyclerView recyclerView = (RecyclerView) ff.get(viewPager2);
            Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop") ;
            touchSlopField.setAccessible(true);
            int touchSlop = (int) touchSlopField.get(recyclerView);
            touchSlopField.set(recyclerView,touchSlop*5);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void moveToCategory() {
        viewPager2.setCurrentItem(0, true);
    }

    public void moveToVoucher() {
        viewPager2.setCurrentItem(2, true);
    }

    private void initListener() {
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.menu_category).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.menu_store).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.menu_voucher).setChecked(true);
                        break;
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_category:
                        viewPager2.setCurrentItem(0, true);
                        break;
                    case R.id.menu_store:
                        viewPager2.setCurrentItem(1, true);
                        break;
                    case R.id.menu_voucher:
                        viewPager2.setCurrentItem(2, true);
                        break;
                }
                return true;
            }
        });
    }
}