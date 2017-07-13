package com.bigc.fragments;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bigc.activities.SignupActivity;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.SignupInterface;
import com.bigc_connect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import eu.janmuller.android.simplecropimage.Util;

public class EmailInputFragment extends Fragment implements
        View.OnClickListener {

    private EditText emailView;
    DatabaseReference databaseReference;

    public static EmailInputFragment newInstance() {
        return new EmailInputFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_emailinput, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users"); // reference of users node.

        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "SignUp Email-Input Screen");

        view.findViewById(R.id.continueButton).setOnClickListener(this);
        view.findViewById(R.id.signInOption).setOnClickListener(this);
        emailView = (EditText) view.findViewById(R.id.inputBox);

        System.gc();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continueButton:
                Utils.hideKeyboard(getActivity());
                verifyEmail();
                break;
            case R.id.signInOption:
                ((SignupInterface) getActivity()).launchLogin();
                break;
        }
    }

    boolean isExist = false;
    boolean isCompleted = false;

    private void verifyEmail() {
        final String email = emailView.getText().toString().trim().toLowerCase();
        ((SignupActivity) getActivity()).email = email;
        if (email.length() == 0) {
            emailView.setError(Utils.loadString(getActivity(),
                    R.string.emailEmpty));
            return;
        } else if (!Utils.validateEmail(email)) {
            emailView.setError(Utils.loadString(getActivity(),
                    R.string.invalidEmail));
            return;
        }
        Utils.showProgress(getActivity());

        Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    isExist = true;
                } else {
                    isExist = false;
                }
                isCompleted = true;


                if (isCompleted) {
                    if (isExist) {
                        showNextStep();
                    } else {
//                        Utils.showToast(getActivity(), "Email already registered.");
                        Utils.showPrompt(getActivity(), "Email already registered.");
                        Utils.hideProgress();
                        return;
                    }

                    Utils.hideProgress();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.i("databaseError", databaseError.toString());
            }
        });


        //	Utils.showProgress(getActivity());
        /********/
        //
        /********/
        // check query if user already exiss in our database

//		ParseQuery<ParseUser> query = ParseUser.getQuery();
//		query.whereEqualTo(DbConstants.EMAIL, email);
//		query.findInBackground(new FindCallback<ParseUser>() {
//
//			@Override
//			public void done(List<ParseUser> users, ParseException e) {
//				Log.i("ParseException",e.toString());
//				Utils.hideProgress();
//				if (e == null) {
//					if (users.size() == 0) {
//						((SignupActivity) getActivity()).email = email;
//						showNextStep();
//					} else {
//						if (getActivity() != null)
//							Toast.makeText(getActivity(),
//									"Email already registered.",
//									Toast.LENGTH_LONG).show();
//					}
//				} else {
//					if (getActivity() != null)
//						Toast.makeText(
//								getActivity(),
//								"We're unable to reach to server, Please try later",
//								Toast.LENGTH_LONG).show();
//				}
//
//			}
//		});


    }

    private void showNextStep() {
        ((SignupInterface) getActivity()).replaceFragment(NameInputFragment
                .newInstance());
    }
}
