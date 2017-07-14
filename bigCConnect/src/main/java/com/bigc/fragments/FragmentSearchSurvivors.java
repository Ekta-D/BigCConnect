package com.bigc.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import com.bigc.adapters.RibbonsGridAdapter;
import com.bigc.adapters.SearchResultAdapter;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.UserConnections;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FragmentSearchSurvivors extends BaseFragment {

	public static String SEARCH_KEY = "";
	private ListView listview;
	private GridView categoryGridView;
	private RibbonsGridAdapter categoryAdapter;
	private SearchResultAdapter adapter;
	private EditText inputBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		adapter.processUserSettings();
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_search_survivors,
				container, false);

		listview = (ListView) view.findViewById(R.id.listview);
		categoryGridView = (GridView) view.findViewById(R.id.categoryGridView);
		categoryAdapter = new RibbonsGridAdapter(getActivity());
		categoryGridView.setAdapter(categoryAdapter);

		adapter = new SearchResultAdapter(getActivity(), null, null);
		listview.setAdapter(adapter);
		inputBox = (EditText) view.findViewById(R.id.searchBox);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Search Survivor Screen");

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
				((FragmentHolder) getActivity())
						.replaceFragment(new ProfileFragment(
								FragmentSearchSurvivors.this, adapter
										.getItem(position)));
			}
		});

		categoryGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				((FragmentHolder) getActivity())
						.replaceFragment(new CategorySurvivorsFragment(
								position, adapter.getUserConnections()));
			}
		});

		new loadSurvivorsTask().execute();
	}

	private void searchSurvivors(String SEARCH_KEY) {
		Utils.showProgress(getActivity());
		ArrayList<Users> searchUsers = Queries.getSearchSurvivorQuery(SEARCH_KEY);
		if (searchUsers!=null) {
			showError("Unable to reach server, Please check your connect and try again");
		} else if(searchUsers.size() == 0) {
			showError("No survivor found");
		} else {
			showResult(searchUsers);
		}

		/*ParseQuery<ParseUser> query = Queries
				.getSearchSurvivorQuery(SEARCH_KEY);
		Utils.showProgress(getActivity());

		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> users, ParseException e) {
				if (e == null) {
					if (users.size() == 0) {
						showError("No survivor found");
					} else {
						showResult(users);
					}
				} else {
					showError("Unable to reach server, Please check your connect and try again");
				}
				Utils.hideProgress();
			}
		});*/
	}

	private void showError(String message) {
		if (categoryGridView == null || listview == null)
			return;

		Utils.showToast(getActivity(), message);
		listview.setVisibility(View.GONE);
		categoryGridView.setVisibility(View.VISIBLE);
	}

	private void showResult(List<Users> result) {
		if (categoryGridView == null || listview == null)
			return;
		categoryGridView.setVisibility(View.GONE);
		adapter.updateData(result);
		listview.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.continueButton:
			break;
		}
	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_SEARCH_SURVIVOR;
	}

	private class loadSurvivorsTask extends
			AsyncTask<Void, Void, UserConnections> {

		@Override
		public void onPreExecute() {
		//	Utils.showProgress(getActivity());
		}

		@Override
		protected UserConnections doInBackground(Void... params) {
			return loadConnections(false);
		}

		@Override
		public void onPostExecute(UserConnections connections) {
			if (adapter != null)
				adapter.updateData(null, connections.activeConnections,
						connections.pendingConnections);
			Utils.hideProgress();
		}
	}

	private UserConnections loadConnections(final boolean fromCache) {

		ParseQuery<ParseObject> mQuery = Queries.getUserConnectionsQuery(
				ParseUser.getCurrentUser(), fromCache);


		UserConnections connections = new UserConnections();

		// TODO: 7/13/2017 Load connections here
		/*try {
			List<ParseObject> survivorConnections = mQuery.find();
			Log.e("Result", survivorConnections.size() + " - ");
			
			if (fromCache && survivorConnections.size() == 0)
				return loadConnections(false);

			String currentId = ParseUser.getCurrentUser().getObjectId();
			for (ParseObject obj : survivorConnections)
				if (obj.getParseUser(DbConstants.TO).getObjectId()
						.equals(currentId)) {
					if (obj.getParseUser(DbConstants.FROM).fetchIfNeeded()
							.getInt(DbConstants.TYPE) != Constants.USER_TYPE.SUPPORTER
							.ordinal()) {
						if (obj.getBoolean(DbConstants.STATUS))
							connections.activeConnections.add(obj
									.getParseUser(DbConstants.FROM));
						else
							connections.pendingConnections.add(obj
									.getParseUser(DbConstants.FROM));
					}
				} else {
					if (obj.getParseUser(DbConstants.TO).fetchIfNeeded()
							.getInt(DbConstants.TYPE) != Constants.USER_TYPE.SUPPORTER
							.ordinal()) {
						if (obj.getBoolean(DbConstants.STATUS))
							connections.activeConnections.add(obj
									.getParseUser(DbConstants.TO));
						else
							connections.pendingConnections.add(obj
									.getParseUser(DbConstants.TO));
					}
				}

			ParseObject.unpinAll(Constants.TAG_CONNECTIONS);
			ParseObject.pinAll(Constants.TAG_CONNECTIONS, survivorConnections);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/

		return connections;
	}

	@Override
	public int getTab() {
		return 2;
	}

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity())
				.replaceFragment(new ConnectionsFragment());
		return true;
	}
}