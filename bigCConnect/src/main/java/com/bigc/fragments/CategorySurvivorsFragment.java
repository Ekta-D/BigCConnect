package com.bigc.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.adapters.SearchResultPictureAdapter;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.UserConnections;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc_connect.R;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class CategorySurvivorsFragment extends BaseFragment implements
		OnLoadMoreListener {

	private static int ribbon;
	private LoadMoreListView listView;
	private SearchResultPictureAdapter adapter;
	private TextView messageView;
	private LinearLayout progressParent;
	private ProgressBar progressView;
	private TextView titleView;

	private List<ParseUser> users = new ArrayList<ParseUser>();
	private UserConnections connections = new UserConnections();

	public CategorySurvivorsFragment(int ribbon, UserConnections connections) {
		CategorySurvivorsFragment.ribbon = ribbon;
		if (connections != null)
			this.connections = connections;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_cateogory_survivors_layout, container, false);

		titleView = (TextView) view.findViewById(R.id.titleView);
		messageView = (TextView) view.findViewById(R.id.messageView);
		progressView = (ProgressBar) view.findViewById(R.id.progressView);
		progressParent = (LinearLayout) view
				.findViewById(R.id.messageViewParent);
		listView = (LoadMoreListView) view.findViewById(R.id.listview);
		adapter = new SearchResultPictureAdapter(getActivity(),
				connections.activeConnections, connections.pendingConnections,
				null);
		listView.setAdapter(adapter);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Categorized Survivors Screen");

		titleView.setText(Utils.ribbonNames[ribbon]
				.concat(" Fighters & Survivors"));
		listView.setOnLoadMoreListener(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				((FragmentHolder) getActivity())
						.replaceFragment(new ProfileFragment(
								CategorySurvivorsFragment.this, adapter
										.getItem(position)));
			}
		});

		startProgress();
		loadData();
	}

	@Override
	public void onPause() {
		adapter.processUserSettings();
		connections = adapter.getUserConnections();
		super.onPause();
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_CATEGORY_SURVIVORS;
	}

	@Override
	public int getTab() {
		return 0;
	}

	@Override
	public void onLoadMore() {

		loadPosts(adapter.getLastItemDate(), false);
	}

	private void loadData() {

		ParseQuery<ParseUser> query = Queries.getCategorizedUsersQuery(ribbon);

		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> users, ParseException e) {

				if (e == null) {
					populateList(users);
				} else {
					if (listView != null) {
						Toast.makeText(
								getActivity(),
								Utils.loadString(getActivity(),
										R.string.networkFailureMessage),
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});

	}

	private void populateList(List<ParseUser> users) {

		this.users.clear();
		if (listView != null) {
			if (users == null) {
				showError(Utils.loadString(getActivity(),
						R.string.networkFailureMessage));
			} else if (users.size() == 0) {
				showError("No survivors found");
			} else {
				adapter.updateData(users);
				listView.setVisibility(View.VISIBLE);
				progressParent.setVisibility(View.GONE);
				this.users.addAll(users);
			}
		}
	}

	private void startProgress() {
		try {
			messageView.setText(R.string.loadingSurvivors);
			progressParent.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void showError(String message) {
		listView.setVisibility(View.GONE);
		progressParent.setVisibility(View.VISIBLE);
		progressView.setVisibility(View.GONE);
		messageView.setText(message);
	}

	private void loadPosts(Date from, final boolean recent) {
		ParseQuery<ParseUser> query = Queries.getCategorizedUsersQuery(ribbon);

		if (recent)
			query.whereGreaterThan(DbConstants.CREATED_AT, from);
		else
			query.whereLessThan(DbConstants.CREATED_AT, from);

		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(final List<ParseUser> posts, ParseException e) {

				if (e == null) {

					adapter.addItems(posts, recent);
					if (!recent)
						listView.onLoadMoreComplete();
				} else {
					listView.onLoadMoreComplete();
				}
			}
		});
	}

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity())
				.replaceFragment(new FragmentSearchSurvivors());
		return true;
	}
}