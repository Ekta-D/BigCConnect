package com.bigc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bigc.activities.SignupActivity;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc_connect.R;

public class SurvivorSearch extends Fragment implements View.OnClickListener {

	public static SurvivorSearch newInstance() {
		return new SurvivorSearch();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_survivor_search, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"SignUp Search-Survivors Screen");

		view.findViewById(R.id.skipOption).setOnClickListener(this);
		final EditText searchBox = (EditText) view.findViewById(R.id.inputBox);
		searchBox.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if ((event.getAction() == KeyEvent.ACTION_DOWN)
//						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
//					searchSurvivors(searchBox.getText().toString());
//					return true;
//				}
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
					searchSurvivors(searchBox.getText().toString());
					return true;
				}
				return false;
			}
		});

		System.gc();
	}

	private void searchSurvivors(String name) {
		if (name.length() == 0) {
			Toast.makeText(getActivity(), "Enter survivor name",
					Toast.LENGTH_LONG).show();
			return;
		}
		((SignupActivity) getActivity())
				.replaceFragment(SurvivorSearchResultFragment.newInstance(name));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.skipOption:
			showNextStep();
			break;
		}
	}

	private void showNextStep() {
		((SignupActivity) getActivity()).replaceFragment(SetupRemainderFragment
				.newInstance());
	}
}