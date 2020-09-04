package com.sp.gold.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.sp.gold.Activities.RegisterActivity;
import com.sp.gold.R;
import com.sp.gold.Utils.HttpHandler;
import com.sp.gold.Utils.SharedViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class RegisterPhoneFragment extends Fragment {

    private static final String TAG = "RegisterPhoneFragment";
    private RegisterActivity registerActivity;
    private boolean isOpened = false;

    private SharedViewModel model;
    private EditText phoneStr;
    private ImageView phoneIcon;
    private LinearLayout phoneLayout;
    private ProgressBar phoneBar;
    private Button nextBtn;

    private static String url = "https://sandbox.api.myinfo.gov.sg/com/v3/person-sample/";
    private String uinFin = "";
    private String dobStr = "";
    private String nameStr = "";
    private String[] addLine = new String[3];
    private ArrayList<String> addressStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_phone, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneStr = view.findViewById(R.id.phoneStr);
        phoneIcon = view.findViewById(R.id.phoneIcon);
        phoneBar = view.findViewById(R.id.phoneBar);
        phoneLayout = view.findViewById(R.id.phoneLayout);
        nextBtn = view.findViewById(R.id.nextPhoneBtn);

        registerActivity = new RegisterActivity();
        nextBtn.setOnClickListener(onNext);
        phoneStr.addTextChangedListener(onPhone);
        setListenerToRootView();
    }

    @Override
    public void onResume(){
        super.onResume();

        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        uinFin = model.getNric();
        if (!uinFin.trim().equals("")) {
            if (!model.isNricVerified()) {
                checkNRIC(uinFin);
            }
        } else {
            errorMessage("NRIC/FIN not found, please try again!");
        }
    }

    public void setListenerToRootView() {
        final View activityRootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    if (!isOpened) {
                        phoneIcon.setVisibility(View.GONE);
                    }
                    isOpened = true;
                } else if (isOpened) {
                    phoneIcon.setVisibility(View.VISIBLE);
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
            registerActivity.onNextPage();
        }
    };

    private TextWatcher onPhone = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() > 0) {
                model.setPhone(s.toString());
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            model.setPhoneVerified(false);

            String text = phoneStr.getText().toString();
            int textLength = phoneStr.getText().length();

            if(text.endsWith(" "))
                return;

            if(textLength == 5)
            {
                phoneStr.setText(new StringBuilder(text).insert(text.length()-1, " ").toString());
                phoneStr.setSelection(phoneStr.getText().length());
            }
        }
    };

    public void checkNRIC(String data) {
        url = "https://sandbox.api.myinfo.gov.sg/com/v3/person-sample/" + data;
        new GetUser().execute();
    }

    private class GetUser extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            phoneBar.setVisibility(View.VISIBLE);
            phoneLayout.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            //Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject dob = jsonObj.getJSONObject("dob");
                    JSONObject name = jsonObj.getJSONObject("name");
                    JSONObject address = jsonObj.getJSONObject("regadd");

                    dobStr = dob.getString("value");
                    nameStr = name.getString("value");
                    addressStr = new ArrayList<>();
                    model.setName(nameStr);

                    if (address.getString("type").equals("SG") && address.getString("source").equals("1")) {
                        addLine[0] = "BLK " + address.getJSONObject("block").getString("value") + " " + address.getJSONObject("street").getString("value");
                        addLine[1] = "#" + address.getJSONObject("floor").getString("value") + "-" + address.getJSONObject("unit").getString("value") + " " + address.getJSONObject("building").getString("value");
                        addLine[2] = address.getJSONObject("country").getString("code") + " " + address.getJSONObject("postal").getString("value");

                        for (int i = 0; i < 3; i++) {
                            addressStr.add(addLine[i]);
                        }
                    }

                    model.setAddress(addressStr);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ageCheck(dobStr);
                        }
                    });
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorMessage("NRIC/FIN not found, please try again!");
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (phoneBar.isShown()) {
                phoneBar.setVisibility(View.INVISIBLE);
                phoneLayout.setVisibility(View.VISIBLE);
            }

            phoneStr.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(phoneStr, InputMethodManager.SHOW_IMPLICIT);
        }

    }

    private void ageCheck(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date d = sdf.parse(date);
            Calendar c = Calendar.getInstance();
            int now = c.get(Calendar.YEAR);

            c.setTime(d);
            int year = c.get(Calendar.YEAR);

            if (now - year >= 50) {
                model.setNricVerified(true);
            } else {
                errorMessage("Sorry, your age is not verified yet!");
            }
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void errorMessage(String err) {
        registerActivity.onReturnPage();
        Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
    }
}