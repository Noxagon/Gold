package com.sp.gold.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sp.gold.Adpaters.TabsAdapter;
import com.sp.gold.Fragments.AchievementFragment;
import com.sp.gold.Fragments.InboxFragment;
import com.sp.gold.R;
import com.sp.gold.Utils.UserHelper;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private TabsAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FirebaseFirestore db;
    private UserHelper helper = null;
    private String uinFin = "";
    private String[] tabs = {"Inbox", "Achievements"};

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = FirebaseFirestore.getInstance();

        initParallaxBar();
        initUserProfile();

        ImageView logoutBtn = (ImageView) findViewById(R.id.profile_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper = new UserHelper(ProfileActivity.this);
                helper.deleteAll();

                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initParallaxBar() {
        final Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        viewPager = findViewById(R.id.profile_pager);
        tabLayout = findViewById(R.id.profile_tabs);
        adapter = new TabsAdapter(this);
        adapter.addFragment(new InboxFragment());
        adapter.addFragment(new AchievementFragment());
        viewPager.setAdapter(adapter);
//        tabLayout.setupWithViewPager(viewPager);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: tab.setText("Inbox"); break;
                    case 1: tab.setText("Achievements"); break;
                }
            }
        }).attach();
    }

    private void initUserProfile() {
        helper = new UserHelper(ProfileActivity.this);
        Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            uinFin = helper.getUIN(cursor);
            String codeStr = "friend:" + uinFin;
            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(codeStr, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.alpha(Color.TRANSPARENT));
                    }
                }
                ((ImageView) findViewById(R.id.profile_qr)).setImageBitmap(bmp);
                ((TextView) findViewById(R.id.profile_name)).setText(helper.getName(cursor));
            } catch (WriterException e) {
                e.printStackTrace();
            }

            Log.d("TAG", "verifyUser: " + uinFin);
        } else {
            Log.d("TAG", "User not registered.");
        }
    }

    public void addFriend(String friendID, String friendName) {
        Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            String uinFin = helper.getUIN(cursor);
            String username = helper.getName(cursor);
            DocumentReference docRef = db.collection("users").document(uinFin);
            DocumentReference friendRef = db.collection("users").document(friendID);

            //DocumentReference friendRef = db.collection("users").document(friendID);
            Map<String, String> friends = new HashMap<>();
            friends.put("uid", friendID);
            friends.put("name", friendName);

            docRef.update("friends", FieldValue.arrayUnion(friends)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Toast.makeText(ProfileActivity.this, "New friend added!", Toast.LENGTH_SHORT).show();
                }
            });

            Map<String, String> program = new HashMap<>();
            program.put("type", "FB");
            program.put("code", uinFin);
            program.put("name", username);

            friendRef.update("inbox", FieldValue.arrayUnion(program));
        } else {
            Log.d("TAG", "User not registered.");
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        //getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}