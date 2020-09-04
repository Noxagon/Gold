package com.sp.gold.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.gold.Adpaters.TabsAdapter;
import com.sp.gold.Fragments.FriendFragment;
import com.sp.gold.Fragments.LeaderboardFragment;
import com.sp.gold.Fragments.MapFragment;
import com.sp.gold.Fragments.ProgramFragment;
import com.sp.gold.R;
import com.sp.gold.Utils.GPSTracker;
import com.sp.gold.Utils.MapViewModel;
import com.sp.gold.Utils.SharedViewModel;
import com.sp.gold.Utils.UserHelper;

import java.lang.reflect.Field;

public class ExploreActivity extends AppCompatActivity {
    private static final String TAG = "ExploreActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private MapViewModel model;
    private GPSTracker gpsTracker;

    private AppBarLayout appBarLayout;
    private TabsAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Context mContext;
    private FirebaseFirestore db;
    private UserHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        final Toolbar toolbar = findViewById(R.id.explore_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mContext = ExploreActivity.this;
        db = FirebaseFirestore.getInstance();
        helper = new UserHelper(mContext);
        gpsTracker = new GPSTracker(mContext);

        appBarLayout = findViewById(R.id.explore_appbar);
        viewPager = findViewById(R.id.explore_pager);
        tabLayout = findViewById(R.id.explore_tabs);
        adapter = new TabsAdapter(this);
        adapter.addFragment(new ProgramFragment());
        adapter.addFragment(new MapFragment());
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("List");
                        tab.setIcon(R.drawable.round_menu_24);
                        break;
                    case 1:
                        tab.setText("Map");
                        tab.setIcon(R.drawable.round_map_24);
                        break;
                }
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewPager.setUserInputEnabled(true);
                        appBarLayout.setExpanded(true);
                        break;
                    case 1:
                        viewPager.setUserInputEnabled(false);
                        appBarLayout.setExpanded(false);
                        checkGPSPermissions();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        initUserPoints();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gpsTracker.canGetLocation()) {
            gpsTracker.stopUsingGPS();
        }
    }

    public void checkGPSPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionState2 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionState1 == PackageManager.PERMISSION_DENIED || permissionState2 == PackageManager.PERMISSION_DENIED) {
            showEnablePermissionAlert();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        gpsTracker.getLocation();
                    }
                } else {
                    finish();
                    Toast.makeText(mContext, "This feature requires Location Permission!", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    public void switchToMap(String name) {
        viewPager.setCurrentItem(1);
        model = new ViewModelProvider(this).get(MapViewModel.class);
        model.setName(name);
        model.setCC(true);
    }

    public void showEnablePermissionAlert() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private void initUserPoints() {
        final Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            String uinFin = helper.getUIN(cursor);
            DocumentReference userRef = db.collection("users").document(uinFin);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    if (document.exists()) {
                        TextView userPoints = (TextView) findViewById(R.id.explore_points);
                        if (document.get("points") != null) {
                            userPoints.setText(String.valueOf(document.get("points")));
                        } else {
                            userPoints.setText("0");
                        }
                    }
                }
            });
        }
    }

//    public void onMapSelected(String name) {
//        MapFragment frag = (MapFragment)
//                getSupportFragmentManager().findFragmentById(R.id.map_fragment);
//        if (frag != null) {
//            frag.findCC(name);
//        } else {
//            MapFragment newFragment = new MapFragment();
//            Bundle args = new Bundle();
//            args.putString("CC", name);
//            newFragment.setArguments(args);
//
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.map, newFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        }
//    }
}