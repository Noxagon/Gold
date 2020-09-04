package com.sp.gold.Adpaters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sp.gold.Activities.CartActivity;
import com.sp.gold.Activities.ExploreActivity;
import com.sp.gold.Activities.FriendActivity;
import com.sp.gold.Activities.ProgramActivity;
import com.sp.gold.R;
import com.sp.gold.Utils.MapViewModel;
import com.sp.gold.Utils.SharedViewModel;

import java.util.ArrayList;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ViewHolder> {
    private static final String TAG = "ProgramAdapter";
    private ArrayList<String> mNames;
    private ArrayList<String> mImages;
    private ArrayList<Double> mCosts;
    private ArrayList<Long> mPoints;
    private ArrayList<String> mAddr;
    private ArrayList<String> mCodes;
    private Context mContext;
    private TextToSpeech tts;

    private boolean isExpanded = false;

    public ProgramAdapter(Context mContext, ArrayList<String> mNames, ArrayList<String> mImages, ArrayList<Double> mCosts,
                          ArrayList<Long> mPoints, ArrayList<String> mAddr, ArrayList<String> mCodes) {
        this.mNames = mNames;
        this.mImages = mImages;
        this.mCosts = mCosts;
        this.mPoints = mPoints;
        this.mCodes = mCodes;
        this.mAddr = mAddr;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ProgramAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_programs, parent, false);
        return new ProgramAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.name.setText(mNames.get(position));
        holder.addr.setText(mAddr.get(position));
        holder.costs.setText(String.format("%.2f", mCosts.get(position)));
        holder.points.setText(mPoints.get(position).toString());
        Glide.with(mContext)
                .load(mImages.get(position))
                .into(holder.image);

        holder.addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof ExploreActivity) {
                    ((ExploreActivity)mContext).switchToMap(mAddr.get(position));
                }
            }
        });

        final String code = mCodes.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProgramActivity.class);
                intent.putExtra("CODE", code);
                mContext.startActivity(intent);
            }
        });

        holder.expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isExpanded) {
                    holder.expandLyt.setVisibility(View.VISIBLE);
                    holder.expandBtn.setImageResource(R.drawable.round_expand_less_24);
                    holder.name.setSingleLine(false);
                    isExpanded = true;
                } else {
                    holder.expandLyt.setVisibility(View.GONE);
                    holder.expandBtn.setImageResource(R.drawable.round_expand_more_24);
                    holder.name.setSingleLine(true);
                    isExpanded = false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout expandLyt;
        ImageView image, expandBtn;
        TextView name, costs, points, addr;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.program_image);
            name = itemView.findViewById(R.id.program_title);
            costs = itemView.findViewById(R.id.program_cost);
            points = itemView.findViewById(R.id.program_points);
            addr = itemView.findViewById(R.id.program_location);

            expandLyt = itemView.findViewById(R.id.expandLyt);
            expandBtn = itemView.findViewById(R.id.expandBtn);
        }
    }
}
