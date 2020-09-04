package com.sp.gold.Fragments;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sp.gold.Activities.ProfileActivity;
import com.sp.gold.Adpaters.InboxAdapter;
import com.sp.gold.Adpaters.ListAdapter;
import com.sp.gold.R;
import com.sp.gold.Utils.UserHelper;

import java.util.ArrayList;
import java.util.Map;

import static com.sp.gold.Fragments.InboxFragment.*;

public class InboxFragment extends Fragment {
    private static final String TAG = "InboxFragment";
    private UserHelper helper = null;
    private FirebaseFirestore db;

    private String uinFin = "", title = "", message = "", code = "";
    private boolean prompt = false;
    private ArrayList<String> messagesTitle = new ArrayList<>();
    private ArrayList<String> messagesList = new ArrayList<>();
    private ArrayList<String> messagesCode = new ArrayList<>();
    private ArrayList<String> messageType = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        helper = new UserHelper(getContext());
        db = FirebaseFirestore.getInstance();

        final RecyclerView recyclerView = view.findViewById(R.id.inbox_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Cursor cursor = helper.getAll();
        if (cursor != null && cursor.moveToFirst()) {
            uinFin = helper.getUIN(cursor);
            DocumentReference docRef = db.collection("users").document(uinFin);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.get("inbox") != null) {
                            ArrayList<Map<String, String>> inbox = (ArrayList<Map<String, String>>) documentSnapshot.get("inbox");
                            for (int i = 0; i < inbox.size(); i++) {
                                getMessage(inbox.get(i));
                            }

                            InboxAdapter adapter = new InboxAdapter(getContext(), messagesTitle, messagesList, messagesCode, messageType);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
            });
        }
    }

    private void getMessage(Map<String, String> map) {
        if (map.get("type") != null) {
            title = "";
            String type = map.get("type");
            switch (type) {
                case "PG":
                    title = "Joined a new event!";
                    break;
                case "FR":
                    title = "Sent you a Friend Request!";
                    break;
                case "FB":
                    title = "Accepted your Friend Request!";
                    if (getContext() instanceof ProfileActivity) {
                        ((ProfileActivity)getContext()).addFriend(map.get("code"), map.get("name"));
                    }
                    break;
            }

            messagesTitle.add(title);
            messagesList.add(map.get("name"));
            messagesCode.add(map.get("code"));
            messageType.add(type);
        }
    }

//    OnSuccessListener<DocumentSnapshot> onProgram = new OnSuccessListener<DocumentSnapshot>() {
//        @Override
//        public void onSuccess(DocumentSnapshot documentSnapshot) {
//            if (documentSnapshot.exists()) {
//                String program = documentSnapshot.getString("name");
//                message = "Joined: " + program;
//            }
//        }
//    };
}