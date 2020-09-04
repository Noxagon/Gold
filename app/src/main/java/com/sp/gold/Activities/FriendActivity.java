package com.sp.gold.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.gold.Adpaters.TabsAdapter;
import com.sp.gold.Fragments.FriendFragment;
import com.sp.gold.Fragments.LeaderboardFragment;
import com.sp.gold.R;
import com.sp.gold.Utils.UserHelper;

import java.util.ArrayList;
import java.util.Map;

public class FriendActivity extends AppCompatActivity {
    private static final String TAG = "FriendActivity";
    private TabsAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Context mContext;
    private FirebaseFirestore db;
    private UserHelper helper = null;

    private ArrayList<Object> friend = new ArrayList<>();
    private ArrayList<Map<String, String>> friend2 = new ArrayList<>();
    private static final int REQUEST_PHONE_CALL = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        final Toolbar toolbar = findViewById(R.id.friend_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mContext = FriendActivity.this;
        db = FirebaseFirestore.getInstance();
        helper = new UserHelper(mContext);

        viewPager = findViewById(R.id.friend_pager);
        tabLayout = findViewById(R.id.friend_tabs);
        adapter = new TabsAdapter(this);
        adapter.addFragment(new FriendFragment());
        adapter.addFragment(new LeaderboardFragment());
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: tab.setText("Friends"); break;
                    case 1: tab.setText("Leaderboard"); break;
                }
            }
        }).attach();
    }

    public void removeFriend(final int pos) {
        //Toast.makeText(this, "Hello" + pos, Toast.LENGTH_SHORT).show();
        Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            String uinFin = helper.getUIN(cursor);
            final DocumentReference docRef = db.collection("users").document(uinFin);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            friend = (ArrayList<Object>) document.get("friends");
                            docRef.update("friends", FieldValue.arrayRemove(friend.get(pos)));

                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "Failed: ", task.getException());
                    }
                }
            });
        } else {
            Log.d("TAG", "User not registered.");
        }
    }

    public void callFriend(final int pos) {
        if (ContextCompat.checkSelfPermission(FriendActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FriendActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            Cursor cursor = helper.getAll();
            if (cursor != null && cursor.moveToFirst()) {
                String uinFin = helper.getUIN(cursor);
                final DocumentReference docRef = db.collection("users").document(uinFin);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                friend2 = (ArrayList<Map<String, String>>) document.get("friends");
                                String friendID = friend2.get(pos).get("uid");

                                DocumentReference friendRef = db.collection("users").document(friendID);
                                friendRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            String phoneNum = "+65" + documentSnapshot.getString("phone");
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + phoneNum));
                                            FriendActivity.this.startActivity(intent);
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "Failed: ", task.getException());
                        }
                    }
                });
            } else {
                Log.d("TAG", "User not registered.");
            }
        }
    }
}