package com.sp.gold.Adpaters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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

import com.sp.gold.Activities.ExploreActivity;
import com.sp.gold.Activities.FriendActivity;
import com.sp.gold.Activities.ProfileActivity;
import com.sp.gold.Activities.RewardActivity;
import com.sp.gold.Activities.ScannerActivity;
import com.sp.gold.R;

import java.util.ArrayList;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mNames;
    private ArrayList<Integer> mImages;
    private ArrayList<String> mCards;
    private Context mContext;
    private TextToSpeech tts;

    public HomeAdapter(Context mContext, ArrayList<String> mNames, ArrayList<Integer> mImages, ArrayList<String> mCards) {
        this.mNames = mNames;
        this.mImages = mImages;
        this.mCards = mCards;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.name.setText(mNames.get(position));
        holder.image.setImageResource(mImages.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        Intent intent0 = new Intent(v.getContext(), ExploreActivity.class);
                        mContext.startActivity(intent0);
                        break;
                    case 1:
                        Intent intent1 = new Intent(v.getContext(), RewardActivity.class);
                        mContext.startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(v.getContext(), ScannerActivity.class);
                        mContext.startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(v.getContext(), FriendActivity.class);
                        mContext.startActivity(intent3);
                        break;
                    case 4:
                        Intent intent4 = new Intent(v.getContext(), ProfileActivity.class);
                        mContext.startActivity(intent4);
                        break;
                }
            }
        });

        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            tts.setLanguage(Locale.ENGLISH);
                            tts.setSpeechRate(0.7f);
                            tts.speak(mNames.get(position),TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                    }
                });

                return true;
            }
        });

        ViewGroup.LayoutParams params = holder.card.getLayoutParams();
        params.height = getScreenHeight() * 65 / 100;
        params.width =  getScreenWidth() * 75 / 100;
        holder.card.setLayoutParams(params);
        holder.card.setCardBackgroundColor(Color.parseColor(mCards.get(position)));
    }

    public int getImage(String imageName) {
        int drawableResourceId = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
        return drawableResourceId;
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cardImage);
            name = itemView.findViewById(R.id.cardText);
            card = itemView.findViewById(R.id.card);
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
