package com.sp.gold.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.sp.gold.Config.Config;
import com.sp.gold.R;
import com.sp.gold.Utils.UserHelper;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProgramActivity extends AppCompatActivity {
    private static final String TAG = "ProgramActivity";
    private static final int PAYPAL_REQUEST_CODE = 7777;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    private FirebaseFirestore db;
    private UserHelper helper = null;
    private Context mContext;
    private DocumentReference userRef, progRef;
    private String uinFin, progName, mCode, mAmount;
    private long userPts, itemsPts;

    private ImageView mImage;
    private TextView mName, mCost, mPoints, mLoc, mSesh, mTime, mDate, mErr;
    private Button mRegPts, mRegPayPal, mReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        mContext = ProgramActivity.this;
        helper = new UserHelper(mContext);
        db = FirebaseFirestore.getInstance();

        mImage = findViewById(R.id.programImage);
        mName = findViewById(R.id.programName);
        mCost = findViewById(R.id.programCost);
        mPoints = findViewById(R.id.programPoints);
        mLoc = findViewById(R.id.programLocation);
        mSesh = findViewById(R.id.programSession);
        mTime = findViewById(R.id.programTime);
        mDate = findViewById(R.id.programDate);
        mErr = findViewById(R.id.regError);

        mRegPts = findViewById(R.id.regPoints);
        mRegPts.setEnabled(false);
        mRegPayPal = findViewById(R.id.regPayPal);
        mReturn = findViewById(R.id.regReturn);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mCode = extras.getString("CODE");
            }
        } else {
            mCode = (String) savedInstanceState.getSerializable("CODE");
        }

        progRef = db.collection("events").document(mCode);
        progRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String nameStr = documentSnapshot.getString("name");
                    progName = nameStr;
                    String locStr = documentSnapshot.getString("location");
                    String urlStr = documentSnapshot.getString("url");
                    double costStr = (double) documentSnapshot.get("cost");
                    mAmount = String.format("%.2f", costStr);
                    long ptsStr = (long) documentSnapshot.get("points");
                    itemsPts = ptsStr;
                    long seshStr = (long) documentSnapshot.get("sessions");

                    Map<String, String> time = (Map<String, String>) documentSnapshot.get("time");
                    String timeStart = time.get("start");
                    String timeEnd = time.get("end");
                    String timeStr = timeStart + " - " + timeEnd;

                    Map<String, String> date = (Map<String, String>) documentSnapshot.get("date");
                    String dateStart = date.get("start");
                    String dateEnd = date.get("end");
                    String dateStr = dateStart + " - " + dateEnd;

                    initCursor();

                    Glide.with(mContext)
                            .load(urlStr)
                            .into(mImage);
                    mName.setText(nameStr);
                    mLoc.setText(locStr);
                    mCost.setText(String.format("%.2f", costStr));
                    mPoints.setText(String.valueOf(ptsStr));
                    mSesh.setText(String.valueOf(seshStr));
                    mTime.setText(timeStr);
                    mDate.setText(dateStr);
                }
            }
        });

        mRegPts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.update("points", FieldValue.increment(-itemsPts));
                Map<String, String> program = new HashMap<>();
                program.put("type", "PG");
                program.put("code", mCode);
                program.put("name", progName);
                userRef.update("inbox", FieldValue.arrayUnion(program));
                userRef.update("events", FieldValue.arrayUnion(mCode));
                finish();
            }
        });

        mRegPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPayment();
            }
        });

        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initCursor() {
        final Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            uinFin = helper.getUIN(cursor);
            userRef = db.collection("users").document(uinFin);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.get("points") != null) {
                            userPts = (long) documentSnapshot.get("points");
                            if (userPts >= itemsPts) {
                                mRegPts.setEnabled(true);
                            } else {
                                mErr.setText("Insufficient Points!");
                            }
                        }
                    }
                }
            });
        }
    }

    private void processPayment() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(mAmount)),"SGD",
                "Purchase Goods",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", mAmount));
                        Map<String, String> program = new HashMap<>();
                        program.put("type", "PG");
                        program.put("code", mCode);
                        program.put("name", progName);
                        userRef.update("inbox", FieldValue.arrayUnion(program));
                        userRef.update("events", FieldValue.arrayUnion(mCode));
                        userRef.update("spending", FieldValue.increment(Double.valueOf(mAmount)));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
    }
}