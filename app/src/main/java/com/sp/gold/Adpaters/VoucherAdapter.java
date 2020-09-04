package com.sp.gold.Adpaters;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sp.gold.Activities.CartActivity;
import com.sp.gold.R;

import java.util.ArrayList;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
    private static final String TAG = "VoucherAdapter";
    private ArrayList<String> mNames;
    private ArrayList<String> mImages;
    private ArrayList<Integer> mPoints;
    private ArrayList<String> mCodes;
    private Context mContext;
    private TextToSpeech tts;

    public VoucherAdapter(Context mContext, ArrayList<String> mNames, ArrayList<String> mImages, ArrayList<Integer> mPoints, ArrayList<String> mCodes) {
        this.mNames = mNames;
        this.mImages = mImages;
        this.mPoints = mPoints;
        this.mCodes = mCodes;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_vouchers, parent, false);
        return new VoucherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.name.setText(mNames.get(position));
        holder.points.setText(mPoints.get(position).toString());
        Glide.with(mContext)
                .load(mImages.get(position))
                .into(holder.image);

        final String code = mCodes.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CartActivity.class);
                intent.putExtra("CODE", code);
                intent.putExtra("TYPE", "VOUCHER");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name, points;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.vouchers_image);
            name = itemView.findViewById(R.id.vouchers_title);
            points = itemView.findViewById(R.id.vouchers_points);
        }
    }
}
