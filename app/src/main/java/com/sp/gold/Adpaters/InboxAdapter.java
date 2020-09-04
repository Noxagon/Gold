package com.sp.gold.Adpaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.gold.Activities.ProfileActivity;
import com.sp.gold.Activities.ProgramActivity;
import com.sp.gold.Activities.ScannerActivity;
import com.sp.gold.R;

import java.util.ArrayList;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {
    private static final String TAG = "InboxAdapter";

    private Context mContext;
    private ArrayList<String> mTitle;
    private ArrayList<String> mMessage;
    private ArrayList<String> mCode;
    private ArrayList<String> mType;

    public InboxAdapter(Context mContext, ArrayList<String> mTitle, ArrayList<String> mMessage, ArrayList<String> mCode, ArrayList<String> mType) {
        this.mContext = mContext;
        this.mTitle = mTitle;
        this.mMessage = mMessage;
        this.mCode = mCode;
        this.mType = mType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InboxAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_inbox, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.title.setText(mMessage.get(position));
        holder.message.setText(mTitle.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType.get(position).equals("FB")) {
                    return;
                }

                if (holder.btnView.getVisibility() == View.GONE) {
                    holder.btnView.setVisibility(View.VISIBLE);
                    if (mCode.get(position) != null) {
                        switch (mType.get(position)) {
                            case "PG":
                                holder.acceptBtn.setText("View");
                                break;
                            case "FR":
                                holder.acceptBtn.setText("Accept");
                                break;
                        }
                    }
                } else {
                    holder.btnView.setVisibility(View.GONE);
                }
            }
        });

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCode.get(position) != null) {
                    switch (mType.get(position)) {
                        case "PG":
                            Intent intent = new Intent(mContext, ProgramActivity.class);
                            intent.putExtra("CODE", mCode.get(position));
                            mContext.startActivity(intent);
                            break;
                        case "FR":
                            if (mContext instanceof ProfileActivity) {
                                ((ProfileActivity)mContext).addFriend(mCode.get(position), mMessage.get(position));
                            }
                            break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, title;
        LinearLayout btnView;
        Button cancelBtn;
        Button acceptBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.inbox_title);
            message = itemView.findViewById(R.id.inbox_message);
            btnView = itemView.findViewById(R.id.inbox_buttons_view);
            cancelBtn = itemView.findViewById(R.id.inbox_cancel);
            acceptBtn = itemView.findViewById(R.id.inbox_accept);
        }
    }
}
