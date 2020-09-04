package com.sp.gold.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.kenilt.loopingviewpager.scroller.AutoScroller;
import com.kenilt.loopingviewpager.widget.LoopingViewPager;
import com.sp.gold.Activities.CartActivity;
import com.sp.gold.Activities.RewardActivity;
import com.sp.gold.Activities.ScannerActivity;
import com.sp.gold.Adpaters.RewardCardAdapter;
import com.sp.gold.R;
import com.sp.gold.Utils.UserHelper;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class RewardStoreFragment extends Fragment {
    private static final String TAG = "RewardStoreFragment";
    private LoopingViewPager voucherPager, itemPager;
    private RewardCardAdapter adapter;

    private View view;
    private Context mContext;
    private UserHelper helper = null;
    private FirebaseFirestore db;

    private String[] couponUrls = {
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/vouchers%2Fkoufu_2.jpg?alt=media&token=66fcc211-59cd-413f-a5e8-ebf5093dd407",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/vouchers%2Fguardian_5.png?alt=media&token=bf4411ae-2f77-4f22-97f0-eea08c108a17",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/vouchers%2Ffairprice_10.jpeg?alt=media&token=a069b248-8703-4aee-a71f-1ffb757d0d77"
    };

    private String[] couponStr = {
            "Foodcourt",
            "Pharmacy",
            "Supermarket"
    };

    private String[] itemUrls = {
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/items%2Fgroceries_item.png?alt=media&token=2d44c292-44ca-4313-8173-ff5b469c168a",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/items%2Fhealth_item.jpg?alt=media&token=bb66c064-6cea-4c72-8aa4-a128cd5681fa",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/items%2Fhome_item.jpg?alt=media&token=7911ad61-9670-412b-b429-9dad17b85698"
    };

    private String[] itemStr = {
            "Grocery",
            "Health",
            "Home"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reward_store, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        mContext = getContext();
        helper = new UserHelper(mContext);
        db = FirebaseFirestore.getInstance();

        initUserPoints();
        initVoucherPager();
        initItemPager();
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
                        TextView userPoints = (TextView) view.findViewById(R.id.user_points);
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

    private void initVoucherPager() {
        voucherPager = view.findViewById(R.id.voucher_pager);
        final ArrayList<String> mImages = new ArrayList(Arrays.asList(couponUrls));
        final ArrayList<String> mNames = new ArrayList(Arrays.asList(couponStr));
        adapter = new RewardCardAdapter(getContext(), mImages);
        voucherPager.setAdapter(adapter);

        WormDotsIndicator wormDotsIndicator = (WormDotsIndicator) view.findViewById(R.id.voucher_indicator);
        wormDotsIndicator.setViewPager(voucherPager);

        AutoScroller autoScroller = new AutoScroller(voucherPager, getLifecycle(), 5000);
        autoScroller.setAutoScroll(true);

        TextView textView = (TextView) view.findViewById(R.id.voucher_text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof RewardActivity) {
                    ((RewardActivity)getContext()).moveToVoucher();
                }
            }
        });
    }

    private void initItemPager() {
        itemPager = view.findViewById(R.id.items_pager);
        final ArrayList<String> mItems = new ArrayList(Arrays.asList(itemUrls));
        final ArrayList<String> mNames = new ArrayList(Arrays.asList(itemStr));
        adapter = new RewardCardAdapter(getContext(), mItems, mNames);
        itemPager.setAdapter(adapter);

        WormDotsIndicator wormDotsIndicator = (WormDotsIndicator) view.findViewById(R.id.items_indicator);
        wormDotsIndicator.setViewPager(itemPager);

        AutoScroller autoScroller = new AutoScroller(itemPager, getLifecycle(), 5000);
        autoScroller.setAutoScroll(true);

        TextView textView = (TextView) view.findViewById(R.id.items_text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof RewardActivity) {
                    ((RewardActivity)getContext()).moveToCategory();
                }
            }
        });
    }
}

//static class PopularAdapter extends PagerAdapter {
//    private Context mContext;
//    private ArrayList<String> mImages;
//    private ArrayList<String> mText;
//
//    public PopularAdapter(Context mContext, ArrayList<String> mImages, ArrayList<String> mText) {
//        this.mContext = mContext;
//        this.mImages = mImages;
//        this.mText = mText;
//    }
//
//    @Override
//    public Object instantiateItem(final ViewGroup container, int position) {
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.card_items_none, container, false);
//
//        ImageView image = (ImageView) layout.findViewById(R.id.items_image);
//        Glide.with(mContext)
//                .load(mImages.get(position))
//                .into(image);
//
//        if (mText != null) {
//            TextView text = (TextView) layout.findViewById(R.id.items_text);
//            text.setText(mText.get(position));
//            text.setVisibility(View.VISIBLE);
//        }
//
//        container.addView(layout);
//        return layout;
//    }
//
//    @Override
//    public int getCount() {
//        return mImages.size();
//    }
//
//    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return object == view;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
//    }
//}

//    private int pos = 0;
//    private Handler handler = new Handler();
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (viewPager.getCurrentItem() >= 2) {
//                pos = 0;
//            } else {
//                pos = viewPager.getCurrentItem() + 1;
//            }
//
//            Log.d(TAG, "run: " + pos);
//            viewPager.setCurrentItem(pos);
//            handler.postDelayed(this, 5000);
//        }
//    };


//        @NonNull
//        @Override
//        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_items_none, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            holder.image.setImageResource(mImages.get(position));
//        }
//
//        @Override
//        public int getItemCount() {
//            return mImages.size();
//        }

//        class ViewHolder extends RecyclerView.ViewHolder {
//            ImageView image;
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//                image = itemView.findViewById(R.id.popular_pager_image);
//            }
//        }