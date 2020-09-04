package com.sp.gold.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.sp.gold.Adpaters.ListAdapter;
import com.sp.gold.R;
import com.sp.gold.Utils.UserHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LeaderboardFragment extends Fragment {
    private static final String TAG = "LeaderboardFragment";
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = view.findViewById(R.id.leaderboard_recycler_view);
        final ProgressBar friendsBar = view.findViewById(R.id.leaderboard_bar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        db = FirebaseFirestore.getInstance();
        DocumentReference dataRef = db.collection("data").document("leaderboard");
        dataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    friendsBar.setVisibility(View.GONE);
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document found.");
                        HashMap<String, HashMap<String, String>> leaderboard = (HashMap<String, HashMap<String, String>>) document.get("users");
                        ArrayList<String> nameArray = new ArrayList<>();
                        ArrayList<Integer> pointsArray = new ArrayList<>();

                        for (int i = 0; i < leaderboard.size(); i++) {
                            nameArray.add((leaderboard.get(String.valueOf(i))).get("name"));
                            pointsArray.add(Integer.valueOf((leaderboard.get(String.valueOf(i))).get("points")));
                        }

                        if (leaderboard != null) {
                            Log.d(TAG, "onComplete: " + nameArray);
                            ListAdapter adapter = new ListAdapter(getContext(), nameArray, pointsArray);
                            recyclerView.setAdapter(adapter);
                        }
                    } else {
                        Log.d(TAG, "Document not found.");
                    }
                } else {
                    Log.d(TAG, "Failed: ", task.getException());
                }
            }
        });
    }
}