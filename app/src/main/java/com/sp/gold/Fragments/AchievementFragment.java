package com.sp.gold.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.gold.Activities.ProgramActivity;
import com.sp.gold.Adpaters.InboxAdapter;
import com.sp.gold.Adpaters.ListAdapter;
import com.sp.gold.R;
import com.sp.gold.Utils.UserHelper;
import com.sp.gold.Utils.VersionModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class AchievementFragment extends Fragment {
    private static final String TAG = "AchievementFragment";
    private FirebaseFirestore db;
    private UserHelper helper = null;
    private RecyclerView recyclerView;
    private String uinFin;

    private String[] title = {
            "Friendly",
            "Active Ageing",
            "Spend Wisely"
    };

    private String[] info = {
            "Follow 5 Friends",
            "Participate in 5 Events",
            "Spend $300 on Events"
    };

    private Integer[] progress = {
            0,
            0,
            0
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievement, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        helper = new UserHelper(getContext());
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.achievement_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        initCursor();
    }

    private void initCursor() {
        final Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            uinFin = helper.getUIN(cursor);
            DocumentReference userRef = db.collection("users").document(uinFin);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.get("friends") != null) {
                            ArrayList<Object> friendsList = (ArrayList<Object>) documentSnapshot.get("friends");
                            progress[0] = friendsList.size() * ( 100 / 5 );
                        }

                        if (documentSnapshot.get("events") != null) {
                            ArrayList<Object> eventsList = (ArrayList<Object>) documentSnapshot.get("events");
                            progress[1] = eventsList.size() * ( 100 / 5 );
                        }

                        if (documentSnapshot.get("spending") != null) {
                            double spending = (double) documentSnapshot.get("spending");
                            Integer spendingInt = (int) Math.round(spending/3);
                            progress[2] = spendingInt;
                        }

                        ArrayList<String> titleList = new ArrayList(Arrays.asList(title));
                        ArrayList<String> infoList = new ArrayList(Arrays.asList(info));
                        ArrayList<Integer> progressList = new ArrayList(Arrays.asList(progress));

                        Log.d(TAG, "onSuccess: " + progressList);

                        ListAdapter adapter = new ListAdapter(getContext(), titleList, progressList, infoList);
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
        }
    }
}