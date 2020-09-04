package com.sp.gold.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.gold.Activities.HomeActivity;
import com.sp.gold.Adpaters.HomeAdapter;
import com.sp.gold.Adpaters.VoucherAdapter;
import com.sp.gold.R;

import java.util.ArrayList;
import java.util.Arrays;

public class RewardVoucherFragment extends Fragment {
    RecyclerView recyclerView;

    private String[] voucherUrls = {
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/vouchers%2Fkoufu_2.jpg?alt=media&token=66fcc211-59cd-413f-a5e8-ebf5093dd407",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/vouchers%2Fguardian_5.png?alt=media&token=bf4411ae-2f77-4f22-97f0-eea08c108a17",
            "https://firebasestorage.googleapis.com/v0/b/gold-b1fea.appspot.com/o/vouchers%2Ffairprice_10.jpeg?alt=media&token=a069b248-8703-4aee-a71f-1ffb757d0d77"
    };


    private String[] voucherText = {
            "Koufu $4 Voucher",
            "Guardian $5 Voucher",
            "FairPrice $10 Voucher",
    };

    private Integer[] voucherPoints = {
            5,
            5,
            10
    };

    private String[] voucherCode = {
            "VC001",
            "VC002",
            "VC003"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reward_voucher, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ArrayList<String> mImages = new ArrayList(Arrays.asList(voucherUrls));
        final ArrayList<String> mNames = new ArrayList(Arrays.asList(voucherText));
        final ArrayList<Integer> mPoints = new ArrayList(Arrays.asList(voucherPoints));
        final ArrayList<String> mCodes = new ArrayList(Arrays.asList(voucherCode));

        recyclerView = view.findViewById(R.id.voucher_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        final VoucherAdapter adapter = new VoucherAdapter(getContext(), mNames, mImages, mPoints, mCodes);
        recyclerView.setAdapter(adapter);
    }
}
