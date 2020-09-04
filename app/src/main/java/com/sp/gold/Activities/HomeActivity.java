package com.sp.gold.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sp.gold.Adpaters.HomeAdapter;
import com.sp.gold.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private String[] name = {"Explore", "Rewards", "Scan", "Friends", "Profile"};
    private int[] image = {
            R.drawable.round_explore_24,
            R.drawable.round_whatshot_24,
            R.drawable.round_qr_code_scanner_24,
            R.drawable.round_people_24,
            R.drawable.round_account_circle_24
    };
    private String[] card = {"#E23838", "#F78200", "#FFB900", "#5EBD3E", "#009CDF"};
    // "#EE4C52", "#FF8C00", "#F9AF15", "#7AC95F", "#54B5D2"
    // "#E23838", "#F78200", "#FFB900", "#5EBD3E", "#009CDF"

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<Integer> mImages = new ArrayList<>();
    private ArrayList<String> mCards = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;
    private TextToSpeech tts;
    private RelativeLayout calView;
    private TextView dateStr, timeStr;

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    private String period;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initCalendar();
        getImages();
    }

    @Override
    public void onStart() {
        super.onStart();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    timeStr.setText(timeFormat.format(new Date()));
                    dateStr.setText(dateFormat.format(new Date()));
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    private void initCalendar() {
        Log.d(TAG, "initCalendar: init calendar");
        calView = findViewById(R.id.calendarView);
        ViewGroup.LayoutParams params = calView.getLayoutParams();
        params.height = getScreenHeight() * 4 / 10;
        calView.setLayoutParams(params);

        dateStr = findViewById(R.id.dateStr);
        timeStr = findViewById(R.id.timeStr);

        dateStr.setText(dateFormat.format(new Date()));
        timeStr.setText(timeFormat.format(new Date()));

        calView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Calendar current = Calendar.getInstance();
                if (current.get(Calendar.AM_PM) == Calendar.AM) {
                    period = "AM";
                } else {
                    period = "PM";
                }

                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            tts.setLanguage(Locale.ENGLISH);
                            tts.setSpeechRate(0.7f);

                            if (!tts.isSpeaking()) {
                                tts.speak("Time is " + current.get(Calendar.HOUR) + ", " + current.get(Calendar.MINUTE) + period, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    }
                });
                return true;
            }
        });
    }

    private void getImages(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        for (int i = 0; i < name.length; i++) {
            mNames.add(name[i]);
            mImages.add(image[i]);
            mCards.add(card[i]);
        }

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview");
        RecyclerView recyclerView = findViewById(R.id.cardRecyclerView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        final HomeAdapter adapter = new HomeAdapter(this, mNames, mImages, mCards);
        recyclerView.setAdapter(adapter);

        smoothScroller.setTargetPosition(2);
        layoutManager.startSmoothScroll(smoothScroller);
    }

    public static class CenterSmoothScroller extends LinearSmoothScroller {
        CenterSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
