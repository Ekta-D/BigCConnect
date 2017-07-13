package com.bigc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bigc.activities.SignupActivity;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.SignupInterface;
import com.bigc_connect.R;

public class NameInputFragment extends Fragment implements View.OnClickListener {

	private EditText nameView;

	public static NameInputFragment newInstance() {
		return new NameInputFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_nameinput, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"SignUp Name-Input Screen");

		view.findViewById(R.id.continueButton).setOnClickListener(this);
		view.findViewById(R.id.signInOption).setOnClickListener(this);
		nameView = (EditText) view.findViewById(R.id.inputBox);

		System.gc();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.continueButton:
			onContinueButtonClicked();
			break;
		case R.id.signInOption:
			((SignupInterface) getActivity()).launchLogin();
			break;
		}
	}

	private void onContinueButtonClicked() {

		Utils.hideKeyboard(getActivity());
		String name = nameView.getText().toString().trim();
		if (name.length() == 0) {
			nameView.setError("Name is empty");
			return;
		}

		((SignupActivity) getActivity()).name = name;
		((SignupInterface) getActivity()).replaceFragment(PasswordFragment
				.newInstance());
	}
}
