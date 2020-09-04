package com.sp.gold.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sp.gold.Activities.RegisterActivity;
import com.sp.gold.R;
import com.sp.gold.Utils.SharedViewModel;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class RegisterNRICFragment extends Fragment {

    private static final String TAG = "RegisterNRICFragment";
    private boolean isOpened = false;

    private SharedViewModel model;
    private EditText nricStr;
    private ImageView nricIcon;
    private Button nextBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_nric, container, false);
        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nricStr = view.findViewById(R.id.nricStr);
        nricIcon = view.findViewById(R.id.nricIcon);
        nextBtn = view.findViewById(R.id.nextNricBtn);

        nextBtn.setOnClickListener(onNext);
        nricStr.addTextChangedListener(onNRIC);
        setListenerToRootView();

        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    private void setListenerToRootView() {
        final View activityRootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    if (!isOpened) {
                        nricIcon.setVisibility(View.GONE);
                    }
                    isOpened = true;
                } else if (isOpened) {
                    nricIcon.setVisibility(View.VISIBLE);
                    isOpened = false;
                }
            }
        });
    }

    private View.OnClickListener onNext = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

            RegisterActivity registerActivity = new RegisterActivity();
            registerActivity.onNextPage();
        }
    };

    private TextWatcher onNRIC = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() > 0) {
                model.setNric(s.toString());
            }
        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            model.setNricVerified(false);
        }
    };
}