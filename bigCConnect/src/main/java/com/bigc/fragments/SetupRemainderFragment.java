package com.bigc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import com.bigc.activities.SignupActivity;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SetupRemainderFragment extends Fragment implements
        View.OnClickListener {

    private EditText stageView;
    private EditText typeView;
    private EditText locationView;
    private RadioButton yesBtn;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    public static SetupRemainderFragment newInstance() {
        return new SetupRemainderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setupremainder,
                container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
//		if (ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//				.ordinal())
        if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 1) //1 for supporter
        {
            view.findViewById(R.id.stageParent).setVisibility(View.GONE);
            view.findViewById(R.id.typeParent).setVisibility(View.GONE);
            view.findViewById(R.id.publicProfileParent)
                    .setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "SignUp Set-Up-Remainder Screen");

        view.findViewById(R.id.skipOption).setOnClickListener(this);
        view.findViewById(R.id.continueButton).setOnClickListener(this);

        typeView = (EditText) view.findViewById(R.id.typeInputView);
        stageView = (EditText) view.findViewById(R.id.stageInputView);
        locationView = (EditText) view.findViewById(R.id.locationInputView);
        yesBtn = (RadioButton) view.findViewById(R.id.yesCheckbox);

        System.gc();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.continueButton:
                saveSettings();
            case R.id.skipOption:
                gotoNextStep();
                break;
        }
    }

    private void saveSettings() {
//        ParseUser.getCurrentUser().put(DbConstants.STAGE,
//                stageView.getText().toString());
//        ParseUser.getCurrentUser().put(DbConstants.CANCER_TYPE,
//                typeView.getText().toString());
//        ParseUser.getCurrentUser().put(DbConstants.VISIBILITY,
//                yesBtn.isChecked() ? Constants.PUBLIC : Constants.PRIVATE);
//        ParseUser.getCurrentUser().put(DbConstants.LOCATION,
//                locationView.getText().toString());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = currentUser.getUid();
        Map<String, Object> map = new HashMap<>();
        map.put(DbConstants.STAGE, stageView.getText().toString().trim());
        map.put(DbConstants.CANCER_TYPE, typeView.getText().toString().trim());
        map.put(DbConstants.VISIBILITY, yesBtn.isChecked() ? Constants.PUBLIC : Constants.PRIVATE);
        map.put(DbConstants.LOCATION, locationView.getText().toString().trim());

        Preferences.getInstance(getActivity()).save(DbConstants.STAGE, stageView.getText().toString().trim());
        Preferences.getInstance(getActivity()).save(DbConstants.CANCER_TYPE, typeView.getText().toString().trim());
        Preferences.getInstance(getActivity()).save(DbConstants.VISIBILITY, yesBtn.isChecked() ? Constants.PUBLIC : Constants.PRIVATE);
        Preferences.getInstance(getActivity()).save(DbConstants.LOCATION, locationView.getText().toString().trim());
        databaseReference.child(DbConstants.USERS).child(currentUid).updateChildren(map);

    }

    private void gotoNextStep() {
        Utils.hideKeyboard(getActivity());
        ((SignupActivity) getActivity()).replaceFragment(InviteFragment
                .newInstance());
    }
}