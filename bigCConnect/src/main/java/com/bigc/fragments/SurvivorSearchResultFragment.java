package com.bigc.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bigc.activities.SignupActivity;
import com.bigc.adapters.SearchResultAdapter;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc_connect.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class SurvivorSearchResultFragment extends Fragment implements
		View.OnClickListener {

	public static String SEARCH_KEY = "";
	private ListView listview;
	private TextView errorMessageView;
	private SearchResultAdapter adapter;
	private LinearLayout searchBarParent;
	private LinearLayout normalBarParent;
	private EditText inputBox;

	public static SurvivorSearchResultFragment newInstance(String name) {
		SEARCH_KEY = name == null ? "" : name;
		return new SurvivorSearchResultFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new SearchResultAdapter(getActivity(), null, null);
	}

	@Override
	public void onPause() {
		adapter.processUserSettings();
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_survivor_search_result,
				container, false);
		normalBarParent = (LinearLayout) view
				.findViewById(R.id.normalBarParent);
		searchBarParent = (LinearLayout) view
				.findViewById(R.id.searchBarParent);

		listview = (ListView) view.findViewById(R.id.survivorSearchList);
		listview.setAdapter(adapter);
		errorMessageView = (TextView) view
				.findViewById(R.id.survivorSearchErrorMessageView);
		inputBox = (EditText) view.findViewById(R.id.inputBox);
		view.findViewById(R.id.continueButton).setOnClickListener(this);
		view.findViewById(R.id.searchOption).setOnClickListener(this);
		view.findViewById(R.id.endSearchOption).setOnClickListener(this);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Survivor Search Result Screen");

		searchSurvivors(SEARCH_KEY);
		inputBox.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (inputBox.getText().length() > 0)
						searchSurvivors(inputBox.getText().toString());
					return true;
				}
				return false;
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.e("Survivor", "Clicked");
			}
		});

	}

	private void searchSurvivors(String SEARCH_KEY) {
		ParseQuery<ParseUser> query = Queries
				.getSearchSurvivorQuery(SEARCH_KEY);
		Utils.showProgress(getActivity());
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> users, ParseException e) {
				if (e == null) {
					if (users.size() == 0) {
						showError("No survivor found.");
					} else {
						showResult(users);
					}
				} else {
					showError("Unable to reach server, Please check your connect and try again");
				}
				Utils.hideProgress();
			}
		});
	}

	private void showError(String message) {
		if (errorMessageView == null || listview == null)
			return;
		errorMessageView.setText(message);
		listview.setVisibility(View.GONE);
		errorMessageView.setVisibility(View.VISIBLE);

	}

	private void showResult(List<ParseUser> result) {
		if (errorMessageView == null || listview == null)
			return;
		errorMessageView.setVisibility(View.GONE);
		adapter.updateData(result);
		listview.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.endSearchOption:
			normalBarParent.setVisibility(View.VISIBLE);
			searchBarParent.setVisibility(View.GONE);
			break;
		case R.id.searchOption:
			normalBarParent.setVisibility(View.GONE);
			searchBarParent.setVisibility(View.VISIBLE);
			break;
		case R.id.continueButton:
			showNextStep();
			break;
		}
	}

	private void showNextStep() {
		((SignupActivity) getActivity()).replaceFragment(SetupRemainderFragment
				.newInstance());
	}
}