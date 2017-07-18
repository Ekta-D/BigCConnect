package com.bigc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigc.activities.SignupActivity;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc_connect.R;

public class SupporterVisibilityFragment extends Fragment implements
		View.OnClickListener {

	public static SupporterVisibilityFragment newInstance() {
		return new SupporterVisibilityFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_supporter_visibility, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"SignUp Set-Visibility Screen");

		view.findViewById(R.id.skipOption).setOnClickListener(this);
		view.findViewById(R.id.yesButton).setOnClickListener(this);
		view.findViewById(R.id.noButton).setOnClickListener(this);

		System.gc();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.noButton:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Private Visibility");
			/*ParseUser.getCurrentUser().put(DbConstants.VISIBILITY,
					Constants.PRIVATE);*/
			showNextStep();
			break;
		case R.id.skipOption:
		case R.id.yesButton:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Public Visibility");
			/*ParseUser.getCurrentUser().put(DbConstants.VISIBILITY,
					Constants.PUBLIC);*/
			showNextStep();
			break;
		}
	}

	private void showNextStep() {
		((SignupActivity) getActivity()).replaceFragment(InviteFragment
				.newInstance());
	}
}