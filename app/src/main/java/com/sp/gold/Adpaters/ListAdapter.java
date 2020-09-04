package com.sp.gold.Adpaters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.gold.Activities.FriendActivity;
import com.sp.gold.Activities.ScannerActivity;
import com.sp.gold.R;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ListAdapter";

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mInfo = new ArrayList<>();
    private ArrayList<Integer> mProgress = new ArrayList<>();
    private ArrayList<Integer> mPoints = new ArrayList<>();
    private int option = 0;
    private Context mContext;

//    public ListAdapter(Context mContext) {
//        this.mContext = mContext;
//
//        option = 0;
//    }

    public ListAdapter(Context mContext, ArrayList<String> mNames) {
        this.mContext = mContext;
        this.mNames = mNames;

        option = 0;
    }

    public ListAdapter(Context mContext, ArrayList<String> mNames, ArrayList<Integer> mPoints) {
        this.mContext = mContext;
        this.mNames = mNames;
        this.mPoints = mPoints;

        option = 1;
    }

    public ListAdapter(Context mContext, ArrayList<String> mNames, ArrayList<Integer> mProgress , ArrayList<String> mInfo) {
        this.mContext = mContext;
        this.mNames = mNames;
        this.mProgress = mProgress;
        this.mInfo = mInfo;

        option = 2;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: ");
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: called.");
        switch (option) {
            case 0: return new ListAdapter.ViewHolder0(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_users, viewGroup, false));
            case 1: return new ListAdapter.ViewHolder1(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_leaderboard, viewGroup, false));
            case 2: return new ListAdapter.ViewHolder2(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_achievements, viewGroup, false));
        }

        return null;
    }

//    public void addItem(Context mContext, String mName) {
//        this.mContext = mContext;
//        mNames.add(mName);
//    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");
        final int pos = i;

        switch (option) {
            case 0:
                final ListAdapter.ViewHolder0 viewHolder0 = (ListAdapter.ViewHolder0) viewHolder;

                viewHolder0.name.setText(mNames.get(i));
                viewHolder0.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder0.view.getVisibility() == View.GONE) {
                            viewHolder0.view.setVisibility(View.VISIBLE);
                            viewHolder0.name.setSingleLine(false);
                            viewHolder0.removeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomAlertDialog);
                                    ViewGroup viewGroup = view.findViewById(android.R.id.content);
                                    View dialogView = LayoutInflater.from(mContext).inflate(R.layout.card_dialog, viewGroup, false);
                                    builder.setView(dialogView);
                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.show();

                                    Button negBtn = (Button) dialogView.findViewById(R.id.negBtn);
                                    negBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alertDialog.cancel();
                                        }
                                    });

                                    Button posBtn = (Button) dialogView.findViewById(R.id.posBtn);
                                    posBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (mContext instanceof FriendActivity) {
                                                alertDialog.cancel();
                                                ((FriendActivity)mContext).removeFriend(pos);
                                            }
                                        }
                                    });
                                }
                            });

                            viewHolder0.inviteBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mContext instanceof FriendActivity) {
                                        ((FriendActivity)mContext).callFriend(pos);
                                    }
                                }
                            });
                        } else {
                            viewHolder0.view.setVisibility(View.GONE);
                            viewHolder0.name.setSingleLine();
                        }
                    }
                });
                break;

            case 1:
                ListAdapter.ViewHolder1 viewHolder1 = (ListAdapter.ViewHolder1) viewHolder;
                viewHolder1.name.setText(mNames.get(i));
                viewHolder1.points.setText(mPoints.get(i).toString());
                break;

            case 2:
                ListAdapter.ViewHolder2 viewHolder2 = (ListAdapter.ViewHolder2) viewHolder;
                viewHolder2.title.setText(mNames.get(i));
                viewHolder2.progress.setProgress(mProgress.get(i));
                viewHolder2.info.setText(mInfo.get(i));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    class ViewHolder0 extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout view;
        Button removeBtn;
        Button inviteBtn;

        public ViewHolder0(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.users_name);
            view = itemView.findViewById(R.id.users_btn_view);
            removeBtn = itemView.findViewById(R.id.removeBtn);
            inviteBtn = itemView.findViewById(R.id.inviteBtn);
        }
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        TextView name;
        TextView points;

        public ViewHolder1(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_name);
            points = itemView.findViewById(R.id.user_points);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        TextView title;
        TextView info;
        ProgressBar progress;

        public ViewHolder2(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.achievement_title);
            info = itemView.findViewById(R.id.achievement_text);
            progress = itemView.findViewById(R.id.achievement_progress);
        }
    }
}