package com.sp.gold.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sp.gold.R;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private FirebaseAuth mAuth;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mp = MediaPlayer.create(getBaseContext(), R.raw.splash);
        mp.start();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                checkUserExist();
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void checkUserExist() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent myIntent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(myIntent);
        } else {
            Intent myIntent = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(myIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp.isPlaying()) {
            mp.stop();
        }
    }
}