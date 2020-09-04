package com.sp.gold.Adpaters;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.sp.gold.Activities.CartActivity;
import com.sp.gold.Activities.RewardActivity;
import com.sp.gold.R;

import java.util.ArrayList;
import java.util.Locale;

public class RewardCardAdapter extends PagerAdapter {
    private TextToSpeech tts;
    private Context mContext;
    private Options mOptions;
    private ArrayList<String> mImages;
    private ArrayList<String> mText;
    private ArrayList<Integer> mPoints;
    private ArrayList<String> mCodes;
    public enum Options {
        NONE,
        TAG,
        SIDE
    };

    public RewardCardAdapter(Context mContext, ArrayList<String> mImages) {
        this.mContext = mContext;
        this.mImages = mImages;

        mOptions = Options.NONE;
    }

    public RewardCardAdapter(Context mContext, ArrayList<String> mImages, ArrayList<String> mText) {
        this.mContext = mContext;
        this.mImages = mImages;
        this.mText = mText;

        mOptions = Options.TAG;
    }

    public RewardCardAdapter(Context mContext, ArrayList<String> mImages, ArrayList<String> mText, ArrayList<Integer> mPoints, ArrayList<String> mCodes) {
        this.mContext = mContext;
        this.mImages = mImages;
        this.mText = mText;
        this.mPoints = mPoints;
        this.mCodes = mCodes;

        mOptions = Options.SIDE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = null;
        ImageView image = null;
        TextView text = null;
        TextView point = null;

        switch (mOptions) {
            case NONE:
                layout = (ViewGroup) inflater.inflate(R.layout.card_items_none, container, false);
                image = (ImageView) layout.findViewById(R.id.items_none_image);
                Glide.with(mContext)
                        .load(mImages.get(position))
                        .into(image);

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mContext instanceof RewardActivity) {
                            Intent intent = new Intent(mContext, CartActivity.class);
                            intent.putExtra("TYPE", "VOUCHER");
                            switch (position) {
                                case 0:
                                    intent.putExtra("CODE", "VC001");
                                    break;
                                case 1:
                                    intent.putExtra("CODE", "VC002");
                                    break;
                                case 2:
                                    intent.putExtra("CODE", "VC003");
                                    break;
                            }
                            mContext.startActivity(intent);
                        }
                    }
                });
                break;
            case TAG:
                layout = (ViewGroup) inflater.inflate(R.layout.card_items_tag, container, false);
                image = (ImageView) layout.findViewById(R.id.items_tag_image);
                Glide.with(mContext)
                        .load(mImages.get(position))
                        .into(image);
                text = (TextView) layout.findViewById(R.id.items_tag_title);
                text.setText(mText.get(position));

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mContext instanceof RewardActivity) {
                            ((RewardActivity)mContext).moveToCategory();
                        }
                    }
                });
                break;
            case SIDE:
                layout = (ViewGroup) inflater.inflate(R.layout.card_items_side, container, false);
                image = (ImageView) layout.findViewById(R.id.items_side_image);
                Glide.with(mContext)
                        .load(mImages.get(position))
                        .into(image);
                point = (TextView) layout.findViewById(R.id.items_side_points);
                point.setText(String.valueOf(mPoints.get(position)));
                break;
        }

        if (mText != null) {
            final String ttsText = mText.get(position);
            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                tts.setLanguage(Locale.ENGLISH);
                                tts.setSpeechRate(0.7f);
                                tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    });

                    return true;
                }
            });
        }

        if (mCodes != null) {
            final String code = mCodes.get(position);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CartActivity.class);
                    intent.putExtra("CODE", code);
                    if ( code.startsWith("GR") || code.startsWith("HL") ) {
                        intent.putExtra("TYPE", "CONSUMABLE");
                    } else if (code.startsWith("HM")) {
                        intent.putExtra("TYPE", "HOME");
                    }

                    mContext.startActivity(intent);
                }
            });
        }

        container.addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}