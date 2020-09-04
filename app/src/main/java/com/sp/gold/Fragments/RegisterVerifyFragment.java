package com.sp.gold.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.sp.gold.Activities.HomeActivity;
import com.sp.gold.Activities.RegisterActivity;
import com.sp.gold.R;
import com.sp.gold.Utils.SharedViewModel;
import com.sp.gold.Utils.UserHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class RegisterVerifyFragment extends Fragment {
    private static final String TAG = "RegisterNRICFragment";
    private boolean isOpened = false;

    //private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private RegisterActivity registerActivity;
    private UserHelper helper = null;
    private SharedViewModel model;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private PinEntryEditText codeStr;
    private ImageView codeIcon;
    private Button nextBtn;
    private String phoneNum;
    private String verifyId;
    private LinearLayout authLayout;
    private ProgressBar authBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_verify, container, false);
        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        codeStr = view.findViewById(R.id.authStr);
        codeIcon = view.findViewById(R.id.authIcon);
        nextBtn = view.findViewById(R.id.nextAuthBtn);
        authLayout = view.findViewById(R.id.authLayout);
        authBar = view.findViewById(R.id.authBar);

        helper = new UserHelper(getContext());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("en");

        registerActivity = new RegisterActivity();

        codeStr.addTextChangedListener(onAuth);
        nextBtn.setOnClickListener(onNext);
        setListenerToRootView();

        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public void onResume(){
        super.onResume();
        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        phoneNum = model.getPhone();
        if (!phoneNum.trim().equals("")) {
            if (!model.isPhoneVerified()) {
                verifyNumber(phoneNum);
            }
        } else {
            errorReturn("Please enter a valid phone number!");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    private void setListenerToRootView() {
        final View activityRootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    if (!isOpened) {
                        codeIcon.setVisibility(View.GONE);
                    }
                    isOpened = true;
                } else if (isOpened) {
                    codeIcon.setVisibility(View.VISIBLE);
                    isOpened = false;
                }
            }
        });
    }

    private View.OnClickListener onNext = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: onNext");
            String code = codeStr.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyId, code);
            signInWithPhoneAuthCredential(credential);

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

            RegisterActivity registerActivity = new RegisterActivity();
            registerActivity.onNextPage();
        }
    };

    private TextWatcher onAuth = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() >= 6) {
                String code = s.toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyId, code);
                signInWithPhoneAuthCredential(credential);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void errorMessage(String err) {
        authLayout.setVisibility(View.VISIBLE);
        authBar.setVisibility(View.INVISIBLE);
        codeStr.getText().clear();
        codeStr.focus();
        Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
    }

    private void errorReturn(String err) {
        registerActivity.onReturnPage();
        Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
    }

    private void verifyNumber(String number) {
        String phone = "+65" + number;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, getActivity(), mCallbacks);
        codeStr.focus();
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks  = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Log.d(TAG, "onVerificationCompleted:" + credential);
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.w(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                errorReturn("Please enter a valid phone number!");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(getContext(), "We have blocked all requests from this device due to unusual activity. Please try again later!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d(TAG, "onCodeSent:" + verificationId);
            model.setPhoneVerified(true);
            verifyId = verificationId;
            // mResendToken = token;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        authLayout.setVisibility(View.INVISIBLE);
        authBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = task.getResult().getUser();

                            DocumentReference docRef = db.collection("users").document(model.getNric());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            updateDatabase();
                                        } else {
                                            Log.d(TAG, "Document not found, adding new document");
                                            addDatabase();
                                        }
                                    } else {
                                        Log.d(TAG, "Failed: ", task.getException());
                                    }
                                }
                            });
                            Intent myIntent = new Intent(getActivity(), HomeActivity.class);
                            getActivity().startActivity(myIntent);
                            getActivity().finish();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                errorMessage("Please enter the correct verification code!");
                            }
                        }
                    }
                });
    }

    private void addDatabase() {
        Map<String, Object> user = new HashMap<>();
        user.put("uinfin", model.getNric());
        user.put("name", model.getName());
        user.put("phone", model.getPhone());
        if (!model.getAddress().isEmpty())
            user.put("address", model.getAddress());

        db.collection("users").document(model.getNric()).set(user);
        helper.insert(model.getNric(), model.getName(), model.getPhone());
    }

    private void updateDatabase() {
        Map<String, Object> data = new HashMap<>();
        data.put("uinfin", model.getNric());
        data.put("name", model.getName());
        data.put("phone", model.getPhone());
        if (!model.getAddress().isEmpty())
            data.put("address", model.getAddress());

        db.collection("users").document(model.getNric()).set(data, SetOptions.merge());
        helper.insert(model.getNric(), model.getName(), model.getPhone());
    }
}