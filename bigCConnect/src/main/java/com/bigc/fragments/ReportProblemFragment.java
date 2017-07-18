package com.bigc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bigc.adapters.NothingSelectedSpinnerAdapter;
import com.bigc.adapters.SimpleSpinnerAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc_connect.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportProblemFragment extends BaseFragment {

    private Spinner spinner;
    private EditText inputView;
    DatabaseReference databaseReference;
    private final static String[] TYPES = {"Posts", "Photos", "Profile",
            "NewsFeed", "Books", "Videos", "Messages", "Search", "Explore",
            "Notifications", "Login", "Privacy", "Ads"};

    private static BaseFragment caller = null;

    public ReportProblemFragment(BaseFragment caller) {
        ReportProblemFragment.caller = caller;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_problem,
                container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        spinner = (Spinner) view.findViewById(R.id.spinner);
        inputView = (EditText) view.findViewById(R.id.editText);
        view.findViewById(R.id.sendButton).setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        spinner.setSelection(0);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "Report-a-Problem Screen");

        spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                new SimpleSpinnerAdapter(getActivity(), Arrays.asList(TYPES)),
                android.R.layout.simple_list_item_1, getActivity(),
                "Please select"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                if (spinner.getSelectedItemPosition() <= 0) {
                    Toast.makeText(getActivity(), "Please select any product.",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if (inputView.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please enter your feedback.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

//			ParseObject obj = new ParseObject(DbConstants.TABLE_PROBLEM);
                //	obj.put(DbConstants.USER, ParseUser.getCurrentUser());
                //		obj.put(DbConstants.MESSAGE, inputView.getText().toString());
                //		obj.put(DbConstants.TYPE, spinner.getSelectedItemPosition());
//
//			PostManager.getInstance().reportProblem(obj);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DbConstants.TABLE_PROBLEM);
                String objectId = myRef.push().getKey();

                Map<String, Object> report_prob = new HashMap<>();
                report_prob.put(DbConstants.NAME, Preferences.getInstance(getActivity()).getString(DbConstants.NAME));
                report_prob.put(DbConstants.CREATED_AT, DateFormat.getDateTimeInstance().format(new Date()));
                report_prob.put(DbConstants.MESSAGE, inputView.getText().toString().trim());
                report_prob.put(DbConstants.PROBLEM_TYPE, spinner.getSelectedItemPosition());
                report_prob.put(DbConstants.ID,objectId );
                report_prob.put(DbConstants.USER,Preferences.getInstance(getActivity()).getString(DbConstants.ID));
                databaseReference.child(DbConstants.TABLE_PROBLEM).
                        child(objectId).setValue(report_prob);
                Toast.makeText(getActivity(),
                        "Your feedback has been submitted. Thank you!",
                        Toast.LENGTH_LONG).show();
                spinner.setSelection(0);
                inputView.setText("");
                break;
        }
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_REPORT_PROBLEM;
    }

    @Override
    public int getTab() {
        return 5;
    }

    @Override
    public boolean onBackPressed() {
        if (caller != null)
            ((FragmentHolder) getActivity()).replaceFragment(caller);
        else
            ((FragmentHolder) getActivity())
                    .replaceFragment(new NewsFeedFragment());
        return true;
    }
}
