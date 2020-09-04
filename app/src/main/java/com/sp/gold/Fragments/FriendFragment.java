package com.sp.gold.Fragments;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sp.gold.Activities.ProfileActivity;
import com.sp.gold.Activities.RegisterActivity;
import com.sp.gold.Adpaters.ListAdapter;
import com.sp.gold.R;
import com.sp.gold.Utils.UserHelper;
import com.sp.gold.Utils.VersionModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FriendFragment extends Fragment {
    private static final String TAG = "FriendFragment";
    private UserHelper helper = null;
    private FirebaseFirestore db;
    //private DocumentReference userRef;
    //private ListAdapter adapter = new ListAdapter(getContext());

    private String uinFin = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = view.findViewById(R.id.friend_recycler_view);
        final ProgressBar friendsBar = view.findViewById(R.id.friend_bar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setAdapter(adapter);

        helper = new UserHelper(getContext());
        db = FirebaseFirestore.getInstance();

        Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            uinFin = helper.getUIN(cursor);
            DocumentReference docRef = db.collection("users").document(uinFin);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        friendsBar.setVisibility(View.GONE);
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "Document found.");
                            if (document.get("friends") != null) {
//                                ArrayList<String> friends = (ArrayList<String>) document.get("friends");
//                                for (int i = 0; i < friends.size(); i++) {
//                                    userRef = db.collection("users").document(friends.get(i));
//                                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                            if (documentSnapshot.exists()) {
//                                                adapter.addItem(getContext(), documentSnapshot.getString("name"));
//                                                adapter.notifyDataSetChanged();
//                                            }
//                                        }
//                                    });
//                                }

                                ArrayList<Map<String, String>> friends = (ArrayList<Map<String, String>>) document.get("friends");
                                ArrayList<String> friendsName = new ArrayList<>();
                                for (int i = 0; i < friends.size(); i++) {
                                    friendsName.add(friends.get(i).get("name"));
                                }

                                ListAdapter adapter = new ListAdapter(getContext(), friendsName);
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
        } else {
            Log.d("TAG", "User not registered.");
        }
    }
}