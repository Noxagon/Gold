package com.sp.gold.Adpaters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.gold.Activities.ScannerActivity;
import com.sp.gold.R;

public class ScannerAdapter extends RecyclerView.Adapter<ScannerAdapter.ViewHolder> {

    private static final String TAG = "ScannerAdapter";

    private String mName;
    private String mTitle;
    private Integer mType;
    private View view;
    private Context mContext;

    public ScannerAdapter(Context mContext, String mTitle, String mName, Integer mType) {
        this.mTitle = mTitle;
        this.mName = mName;
        this.mType = mType;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called. ");
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_scanner, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.title.setText(mTitle);
        holder.name.setText(mName);

        holder.negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof ScannerActivity) {
                    ((ScannerActivity)mContext).closeBottomSheet();
                }
            }
        });

        holder.posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof ScannerActivity) {
                    switch (mType) {
                        case 0: ((ScannerActivity)mContext).requestFriend(); break;
                        case 1: ((ScannerActivity)mContext).addEvent(); break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView name;
        Button posBtn;
        Button negBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bottom_title);
            name = itemView.findViewById(R.id.bottom_text);
            posBtn = itemView.findViewById(R.id.posBtn);
            negBtn = itemView.findViewById(R.id.negBtn);
        }
    }
}