package com.bigc.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigc.activities.SearchActivity;
import com.bigc.adapters.SearchResultAdapter;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc_connect.R;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class SearchableFragment extends BaseFragment implements
		OnLoadMoreListener {

	private LoadMoreListView listView;
	private LinearLayout progressParent;
	private TextView messageView;
	private ProgressBar progressView;
	private SearchResultAdapter adapter;
	private static String query;
	private static SearchSurvivorsTask searchTask = null;

	public SearchableFragment(String query) {
		if (query != null && SearchableFragment.query != null
				&& SearchableFragment.query.equals(query))
			adapter = null;
		SearchableFragment.query = query;
	}

	public static String getQuery() {
		return query;
	}

	public void executeSearch(String query) {
		if (searchTask != null)
			searchTask.cancel(true);

		Utils.hideKeyboard(getActivity());
		searchTask = new SearchSurvivorsTask();
		searchTask.execute(query);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_search_layout,
				container, false);
		progressView = (ProgressBar) view.findViewById(R.id.progressView);
		messageView = (TextView) view.findViewById(R.id.messageView);
		progressParent = (LinearLayout) view
				.findViewById(R.id.messageViewParent);
		listView = (LoadMoreListView) view.findViewById(R.id.listview);
		if (adapter == null)
			adapter = new SearchResultAdapter(getActivity(), null, null, null);

		listView.setAdapter(adapter);
		listView.setOnLoadMoreListener(this);

		return view;
	}

	@Override
	public void onPause() {
		adapter.processUserSettings();
		super.onPause();
		Utils.hideKeyboard(getActivity());
	}

	@Override
	public void onLoadMore() {
		listView.onLoadMoreComplete();
	}

	private void startProgress() {
		try {
			messageView.setText("");
			progressView.setVisibility(View.VISIBLE);
			progressParent.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Top-Bar Search-Result Screen");

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				((FragmentHolder) getActivity())
						.replaceFragment(new ProfileFragment(null, adapter
								.getItem(position)));
			}
		});

		if (adapter.getConnections().size() == 0) {
			executeSearch(query);
		} else {
			adapter.notifyDataSetChanged();
			listView.setVisibility(View.VISIBLE);
			progressParent.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_SEARCHABLE;
	}

	@Override
	public int getTab() {
		return 0;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	private class SearchSurvivorsTask extends
			AsyncTask<String, Void, List<ParseUser>> {

		@Override
		public void onPreExecute() {
			startProgress();
		}

		@Override
		protected List<ParseUser> doInBackground(String... params) {
			if (params[0] == null || params[0].length() == 0)
				return new ArrayList<ParseUser>();

			if (params[0].toLowerCase().endsWith("survivors")) {
				for (int i = 0; i < Utils.ribbonNames.length; i++) {
					if (params[0].toLowerCase().startsWith(
							Utils.ribbonNames[i].toLowerCase())) {
						return loadRibbonSurvivors(i);
					}
				}
			}

			return searchSurvivors(params[0]);
		}

		@Override
		public void onPostExecute(List<ParseUser> users) {
			Log.e("onPostExecute", "users: " + users);
			if (users == null) {
				showError("Unable to reach server, Please check your connect and try again");
			} else if (users.size() == 0) {
				showError("No survivor found.");
			} else {
				showResult(users);
			}
		}

	}

	private List<ParseUser> loadRibbonSurvivors(int ribbon) {

		ParseQuery<ParseUser> query = Queries.getCategorizedUsersQuery(ribbon);

		try {
			return query.find();
		} catch (ParseException e) {
			return null;
		}
	}

	private List<ParseUser> searchSurvivors(String SEARCH_KEY) {
		ParseQuery<ParseUser> query = Queries
				.getSearchSurvivorQuery(SEARCH_KEY);

		try {
			return query.find();
		} catch (Exception e) {
			return null;
		}

	}

	private void showError(String message) {
		if (listView == null)
			return;

		messageView.setText(message);
		progressView.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);
		progressParent.setVisibility(View.VISIBLE);

	}

	private void showResult(List<ParseUser> result) {
		if (listView == null)
			return;

		adapter.updateData(result,
				((SearchActivity) getActivity()).connections.activeConnections,
				((SearchActivity) getActivity()).connections.pendingConnections);
		listView.setVisibility(View.VISIBLE);
		progressParent.setVisibility(View.GONE);
	}
}
