package com.sp.gold.Activities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.gold.R;
import com.sp.gold.Utils.UserHelper;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private UserHelper helper = null;
    private FirebaseFirestore db;
    private Context mContext;

    private String addrStr = "";
    private String counStr = "";
    private long userPts = 0;
    private long itemsPts = 0;

    private String uinFin, mCode, mType;

    private Button mBtn, mRtn;
    private ImageView mImage;
    private CardView mCard;
    private TextView mTitle, mCountry, mAddr, mErr, mPoints;
    private LinearLayout counLayout, addrLayout;
    private EditText mInput;

    private DocumentReference userRef, itemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mCard = findViewById(R.id.cartCard);
        mCard.setVisibility(View.GONE);

        counLayout = findViewById(R.id.cartCountryLyt);
        addrLayout = findViewById(R.id.cartAddressLyt);

        mImage = findViewById(R.id.cartImage);
        mTitle = findViewById(R.id.cartTitle);
        mCountry = findViewById(R.id.cartCountry);
        mAddr = findViewById(R.id.cartAddress);
        mPoints = findViewById(R.id.cartPoints);

        mErr = findViewById(R.id.cartError);
        mBtn = findViewById(R.id.cartButton);
        mRtn = findViewById(R.id.cartReturn);
        mInput = findViewById(R.id.cartAddressInput);

        mContext = CartActivity.this;
        helper = new UserHelper(mContext);
        db = FirebaseFirestore.getInstance();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mCode = extras.getString("CODE");
                mType = extras.getString("TYPE");
            }
        } else {
            mCode = (String) savedInstanceState.getSerializable("CODE");
            mType = (String) savedInstanceState.getSerializable("TYPE");
        }

        if (mType != null) {
            switch (mType) {
                case "VOUCHER":
                    counLayout.setVisibility(View.GONE);
                    addrLayout.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.bottomMargin = 120;
                    mPoints.setLayoutParams(params);
                    break;
                case "CONSUMABLE":
                    break;
                case "HOME":
                    counLayout.setVisibility(View.GONE);
            }

            mBtn.setEnabled(false);
            initCursor();
        }

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.update("points", FieldValue.increment(-itemsPts));
                finish();
            }
        });

        mRtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mErr.setText("");
                    if (userPts >= itemsPts) {
                        mBtn.setEnabled(true);
                    } else {
                        mErr.setText("Insufficient Points!");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
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
                    if (documentSnapshot.get("address") != null) {
                        ArrayList<String> userAddr = (ArrayList<String>) documentSnapshot.get("address");
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < userAddr.size(); i++) {
                            builder.append(userAddr.get(i));
                            if (i < userAddr.size()-1) {
                                builder.append("\n");
                            }
                        }
                        addrStr = builder.toString();
                    }

                    if (documentSnapshot.get("points") != null) {
                        userPts = (long) documentSnapshot.get("points");
                    }

                    initFstore();
                }
            });
        }
    }

    private void initFstore() {
        itemsRef = db.collection("data").document("items");
        itemsRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    Map<Object, Object> itemMap = (Map<Object, Object>) document.get(mCode);

                    Glide.with(mContext)
                            .load(itemMap.get("url"))
                            .into(mImage);

                    String itemTitle = (String) itemMap.get("name");
                    mTitle.setText(itemTitle.replace("[n]", "\n"));
                    itemsPts = (long) itemMap.get("points");

                    if (itemMap.get("country") != null) {
                        counStr = itemMap.get("country").toString();
                    }

                    cardDisplay();
                }
            }
        });
    }

    private void cardDisplay() {
        if (!counStr.trim().equals("")) {
            mCountry.setText(counStr);
        }

        if (!addrStr.trim().equals("")) {
            mAddr.setText(addrStr);
            if (userPts >= itemsPts) {
                mBtn.setEnabled(true);
            } else {
                mErr.setText("Insufficient Points!");
            }
        } else {
            mAddr.setVisibility(View.GONE);
            mInput.setVisibility(View.VISIBLE);
            mInput.requestFocus();
            mErr.setText("Please provide an address!");
        }

        mPoints.setText(String.format("%d (%d left)", itemsPts, userPts));//String.valueOf(itemsPts) + " (" + String.valueOf(userPts) + " left)");

        mCard.setVisibility(View.VISIBLE);
    }
}