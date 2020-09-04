package com.sp.gold.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.gold.Activities.ExploreActivity;
import com.sp.gold.Adpaters.ProgramAdapter;
import com.sp.gold.Adpaters.VoucherAdapter;
import com.sp.gold.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ProgramFragment extends Fragment {
    RecyclerView recyclerView;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_program, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.program_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DocumentReference userRef = db.collection("data").document("programs");
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    ArrayList<String> names = (ArrayList<String>) document.get("name");
                    ArrayList<String> urls = (ArrayList<String>) document.get("urls");
                    ArrayList<Double> cost = (ArrayList<Double>) document.get("cost");
                    ArrayList<Long> points = (ArrayList<Long>) document.get("points");
                    ArrayList<String> addr = (ArrayList<String>) document.get("location");
                    ArrayList<String> code = (ArrayList<String>) document.get("code");

                    ProgramAdapter adapter = new ProgramAdapter(getContext(), names, urls, cost, points, addr, code);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }
}