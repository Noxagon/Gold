package com.sp.gold.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.Result;
import com.sp.gold.Adpaters.ScannerAdapter;
import com.sp.gold.R;
import com.sp.gold.Utils.UserHelper;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ScannerActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final String TAG = "ScannerActivity";
    private boolean camPerm = false;
    private CodeScanner mCodeScanner;
    private CodeScannerView mCodeScannerView;
    private Context mContext;
    private View bottomScanner;
    private RecyclerView recyclerView;
    private BottomSheetBehavior behavior;
    private FirebaseFirestore db;
    private UserHelper helper = null;
    private ProgressBar scannerBar;

    private String name;
    private String friendID;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        mContext = ScannerActivity.this;
        db = FirebaseFirestore.getInstance();
        helper = new UserHelper(mContext);
        scannerBar = findViewById(R.id.scannerBar);

        initBottomSheet();
        initCodeScanner();
    }

    private void initBottomSheet() {
//        btmTitle = findViewById(R.id.bottom_title);
//        btmText = findViewById(R.id.bottom_text);
        bottomScanner = findViewById(R.id.bottom_scanner);
        recyclerView = findViewById(R.id.scannerRecyclerView);

        behavior = BottomSheetBehavior.from(bottomScanner);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if (!mCodeScanner.isPreviewActive()) {
                            mCodeScanner.startPreview();
                        }
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void initCodeScanner() {
        mCodeScannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, mCodeScannerView);

        camPerm = checkCamPerm();
        if (camPerm) {
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrCodeFilter(result.getText());
                        }
                    });
                }
            });
            mCodeScannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        } else {
            reqCamPerm();
        }
    }

    private void qrCodeFilter(String code) {
        if (code.toLowerCase().startsWith("friend:")) {
            scannerBar.setVisibility(View.VISIBLE);
            friendID = code.replace("friend:", "");
            DocumentReference docRef = db.collection("users").document(friendID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        scannerBar.setVisibility(View.INVISIBLE);
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            name = document.getString("name");
                            final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(layoutManager);
                            final ScannerAdapter adapter = new ScannerAdapter(mContext, "Send Friend Request to:", name, 0);
                            recyclerView.setAdapter(adapter);
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else {
                            Log.d(TAG, "No such document");
                            Toast.makeText(ScannerActivity.this, "No user found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Failed: ", task.getException());
                    }
                }
            });
        } else if (code.toLowerCase().startsWith("event:")) {
            scannerBar.setVisibility(View.VISIBLE);
            eventID = code.replace("event:", "");
            DocumentReference docRef = db.collection("events").document(eventID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        scannerBar.setVisibility(View.INVISIBLE);
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            name = document.getString("name");
                            final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(layoutManager);
                            final ScannerAdapter adapter = new ScannerAdapter(mContext, "Join Event:", name, 1);
                            recyclerView.setAdapter(adapter);
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else {
                            Log.d(TAG, "No such document");
                            Toast.makeText(ScannerActivity.this, "No event found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Failed: ", task.getException());
                    }
                }
            });
        } else if (code.toLowerCase().startsWith("https://")) {
            Intent intent = new Intent(mContext, WebActivity.class);
            intent.putExtra("URI", code);
            startActivity(intent);
        } else {
            Toast.makeText(ScannerActivity.this, "Invalid code, please try again!", Toast.LENGTH_SHORT).show();
            mCodeScanner.startPreview();
        }
    }

    public void closeBottomSheet() {
        mCodeScanner.startPreview();
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void requestFriend() {
        Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            String uinFin = helper.getUIN(cursor);
            String username = helper.getName(cursor);
            DocumentReference friendRef = db.collection("users").document(friendID);

            Map<String, String> program = new HashMap<>();
            program.put("type", "FR");
            program.put("code", uinFin);
            program.put("name", username);

            friendRef.update("inbox", FieldValue.arrayUnion(program)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(mContext, "Friend Request Sent!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("TAG", "User not registered.");
        }

        closeBottomSheet();
    }

    public void addFriend() {
        Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            String uinFin = helper.getUIN(cursor);
            String username = helper.getName(cursor);
            DocumentReference docRef = db.collection("users").document(uinFin);
            DocumentReference friendRef = db.collection("users").document(friendID);

            //DocumentReference friendRef = db.collection("users").document(friendID);
            Map<String, String> friends = new HashMap<>();
            friends.put("uid", friendID);
            friends.put("name", name);

            docRef.update("friends", FieldValue.arrayUnion(friends)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(mContext, "New friend added!", Toast.LENGTH_SHORT).show();
                }
            });

            Map<String, String> program = new HashMap<>();
            program.put("type", "FR");
            program.put("code", uinFin);
            program.put("name", username);

            friendRef.update("inbox", FieldValue.arrayUnion(program));
        } else {
            Log.d("TAG", "User not registered.");
        }

        closeBottomSheet();
    }

    public void addEvent() {
        Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            String uinFin = helper.getUIN(cursor);
            DocumentReference docRef = db.collection("events").document(eventID);
            docRef.update("members", FieldValue.arrayUnion(uinFin)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(mContext, "Event joined!", Toast.LENGTH_SHORT).show();
                }
            });

            DocumentReference userRef = db.collection("users").document(uinFin);
            userRef.update("points", FieldValue.increment(1));
        } else {
            Log.d("TAG", "User not registered.");
        }

        closeBottomSheet();
    }

    @Override
    protected void onResume() {
        if (camPerm) {
            mCodeScanner.startPreview();
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: Scanner");
        if (camPerm) {
            mCodeScanner.releaseResources();
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        super.onPause();
    }

    private boolean checkCamPerm() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void reqCamPerm() {
        ActivityCompat.requestPermissions(ScannerActivity.this, new String[] { Manifest.permission.CAMERA }, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    camPerm = true;
                } else {
                    finish();
                    Toast.makeText(this, "This feature requires Camera Permission!", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }
}
