package com.bigc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.Map;

public class DeactivateFragment extends BaseFragment {

    private EditText inputView;
    DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deactivate, container,
                false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        inputView = (EditText) view.findViewById(R.id.editText);
        view.findViewById(R.id.deactivateBtn).setOnClickListener(this);
        view.findViewById(R.id.cancelBtn).setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "Deactivate-Account Screen");

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deactivateBtn:
                if (inputView.getText().length() == 0) {
                    Utils.showToast(getActivity(), "Please enter feedback");
                    return;
                }
                deactivateAccount(inputView.getText().toString().trim());
                break;
            case R.id.cancelBtn:
                onBackPressed();
                break;
            case R.id.forgotPasswordOption:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Forgot Password Button");
                Utils.sendPasswordRecoverEmail(ParseUser.getCurrentUser()
                        .getEmail());
                Toast.makeText(getActivity(),
                        "Reset link has been sent to your email", Toast.LENGTH_LONG)
                        .show();
                break;
        }
    }

    private void deactivateAccount(String feedback) {
        Utils.showProgress(getActivity());
//		ParseUser.getCurrentUser().put(DbConstants.DEACTIVATED, true);
//		ParseUser.getCurrentUser()
//				.put(DbConstants.DEACTIVATED_REASON, feedback);
//		ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//
//			@Override
//			public void done(ParseException e) {
//				if (e == null) {
//					((FragmentHolder) getActivity()).logoutUser();
//				}
//			}
//		});

        String current_usrId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> update_values = new HashMap<>();
        update_values.put(DbConstants.DEACTIVATED, true);
        update_values.put(DbConstants.DEACTIVATED_REASON, feedback);
        databaseReference.child(DbConstants.USERS).child(current_usrId).updateChildren(update_values);
        ((FragmentHolder) getActivity()).logoutUser();
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_DEACTIVATE;
    }

    @Override
    public int getTab() {
        return 5;
    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity())
                .replaceFragment(new SettingsFragment());
        return true;
    }
}
