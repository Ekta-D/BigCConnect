package com.bigc.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.adapters.NothingSelectedSpinnerAdapter;
import com.bigc.adapters.RibbonSpinnerAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.interfaces.SignupInterface;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RibbonSelectionFragment extends Fragment implements
        View.OnClickListener {

    private Spinner spinner;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    public static RibbonSelectionFragment newInstance() {
        return new RibbonSelectionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_ribbonselection, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "SignUp Ribbon-Selection Screen");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        view.findViewById(R.id.skipOption).setOnClickListener(this);
        view.findViewById(R.id.continueButton).setOnClickListener(this);

        if (((SignupInterface) getActivity()).isStatusUpdate())
            ((TextView) view.findViewById(R.id.continueButton)).setText("Save");

        spinner = (Spinner) view.findViewById(R.id.selectRibbonSpinner);
        spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                new RibbonSpinnerAdapter(getActivity()),
                R.layout.ribbon_nothing_selected, getActivity(), null));

        System.gc();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skipOption:
                if (((SignupInterface) getActivity()).isStatusUpdate()) {
                    ((SignupInterface) getActivity())
                            .finishWithAnimation(Activity.RESULT_OK);
                } else {
//				ParseUser.getCurrentUser().put(DbConstants.RIBBON, -1);
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String uid = firebaseUser.getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("ribbon", -1);
                    databaseReference.child(DbConstants.USERS).child(uid).updateChildren(map);
                    showNextStep();
                }
                break;
            case R.id.continueButton:
                if (spinner.getSelectedItemPosition() <= 0) {
                    Toast.makeText(getActivity(), "Please select your ribbon",
                            Toast.LENGTH_LONG).show();
                } else {
//				ParseUser.getCurrentUser().put(DbConstants.RIBBON,
//						spinner.getSelectedItemPosition() - 1);
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String uid = firebaseUser.getUid();
                    Preferences.getInstance(getActivity()).save(Constants.CURRENT_USERID, uid);
                    Map<String, Object> map = new HashMap<>();
                    map.put("ribbon", spinner.getSelectedItemPosition());
                    databaseReference.child(DbConstants.USERS).child(uid).updateChildren(map);

                    if (((SignupInterface) getActivity()).isStatusUpdate()) {
                        ((SignupInterface) getActivity())
                                .finishWithAnimation(Activity.RESULT_OK);
                    } else {
                        showNextStep();
                    }
                }
        }
    }

    private void showNextStep() {
        ((SignupInterface) getActivity())
                .replaceFragment(SetupRemainderFragment.newInstance());
    }
}