package com.bigc.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bigc.fragments.ProfileFragment;
import com.bigc.fragments.SearchableFragment;
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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements
		FragmentHolder, SearchView.OnQueryTextListener {

	private int mContainerId;
	private FragmentTransaction fragmentTransaction;
	private FragmentManager fragmentManager;
	private BaseFragment currentFragment;
	private Users profileFragmentCurrentState;
	private ProgressBar progressBar;

	public UserConnections connections = new UserConnections();
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
				R.color.background)));

		bar.setTitle(Html
				.fromHtml("<font color='#ffffff'>Search Survivors</font>"));

		setContentView(R.layout.activity_search);

		mContainerId = android.R.id.tabcontent;
		fragmentManager = getSupportFragmentManager();

		profileFragmentCurrentState = ProfileFragment.getUser();
		progressBar = (ProgressBar) findViewById(R.id.progressView);
		new loadConnectionsTask().execute();
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
				"Top-Bar-Search Parent Screen");
	}

	public String getSearchViewText() {
		return searchView.getQuery().toString();
	}

	private void handleIntent(Intent intent) {
		Log.e("HandleIntent", "Called");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String searchedQuery = intent.getStringExtra(SearchManager.QUERY);
			if (searchView != null)
				searchView.setQuery(searchedQuery, true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_activity, menu);

		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) searchItem.getActionView();

		searchView.setIconified(false);
		searchView.setQueryHint("Search for Survivors");
		searchView.setOnQueryTextListener(this);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		handleIntent(getIntent());
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Utils.hideKeyboard(this);
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if (currentFragment == null) {
			// Wait here, User connections are still loading.
		} else if (Constants.FRAGMENT_SEARCHABLE.equals(currentFragment
				.getName())) {
			((SearchableFragment) currentFragment).executeSearch(query);
		} else {
			replaceFragment(new SearchableFragment(query));
		}
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public void replaceFragment(BaseFragment fragment) {

		if (currentFragment != null) {
			if (Constants.FRAGMENT_SEARCHABLE.equals(currentFragment.getName())) {
				replaceFragment(fragment, R.anim.slide_in_right,
						R.anim.slide_out_left);
				return;
			} else if (Constants.FRAGMENT_PROFILE.equals(currentFragment
					.getName())) {
				if (Constants.FRAGMENT_SEARCHABLE.equals(fragment.getName())) {
					replaceFragment(fragment, android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				} else {
					replaceFragment(fragment, R.anim.slide_in_right,
							R.anim.slide_out_left);
				}
				return;
			} else {
				replaceFragment(fragment, android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);
			}

		}
		replaceFragment(fragment, -1, -1);
	}

	public void replaceFragment(BaseFragment fragment, int inAnim, int outAnim) {

		try {
			final String tag = fragment.getName();
			BaseFragment f = (BaseFragment) fragmentManager
					.findFragmentByTag(tag);

			if (f != null)
				fragment = f;

			fragmentTransaction = fragmentManager.beginTransaction();
			if (inAnim > 0)
				fragmentTransaction.setCustomAnimations(inAnim, outAnim);

			fragmentTransaction.replace(mContainerId, fragment, tag);
			fragmentTransaction.addToBackStack(tag);
			fragmentTransaction.commit();
			currentFragment = fragment;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void finishActivity() {
		ProfileFragment.setUser(profileFragmentCurrentState);
		finish();
		overridePendingTransition(R.anim.remains_same, R.anim.pull_down);
	}

	private class loadConnectionsTask extends
			AsyncTask<Void, Void, UserConnections> {

		@Override
		protected UserConnections doInBackground(Void... params) {
			return loadConnections(true);
		}

		@Override
		public void onPostExecute(UserConnections connections) {

			SearchActivity.this.connections = connections;
			if (SearchActivity.this != null) {
				progressBar.setVisibility(View.GONE);
				replaceFragment(new SearchableFragment(searchView == null ? ""
						: searchView.getQuery().toString()));

			}
		}
	}

	private UserConnections loadConnections(final boolean fromCache) {
		// TODO: 7/14/2017 load user connections here

		UserConnections connections = new UserConnections();
		/*ParseQuery<ParseObject> mQuery = Queries.getUserConnectionsQuery(
				ParseUser.getCurrentUser(), fromCache);

		UserConnections connections = new UserConnections();

		try {
			List<ParseObject> survivorConnections = mQuery.find();

			if (fromCache && survivorConnections.size() == 0)
				return loadConnections(false);

			for (ParseObject obj : survivorConnections)
				if (obj.getParseUser(DbConstants.TO).getObjectId()
						.equals(ParseUser.getCurrentUser().getObjectId())) {

					if (obj.getParseUser(DbConstants.FROM).fetchIfNeeded()
							.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SURVIVOR
							.ordinal())
						if (obj.getBoolean(DbConstants.STATUS))
							connections.activeConnections.add(obj
									.getParseUser(DbConstants.FROM));
						else
							connections.pendingConnections.add(obj
									.getParseUser(DbConstants.FROM));

				} else {
					if (obj.getParseUser(DbConstants.TO).fetchIfNeeded()
							.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SURVIVOR
							.ordinal())
						if (obj.getBoolean(DbConstants.STATUS))
							connections.activeConnections.add(obj
									.getParseUser(DbConstants.TO));
						else
							connections.pendingConnections.add(obj
									.getParseUser(DbConstants.TO));
				}

			ParseObject.unpinAll(Constants.TAG_CONNECTIONS);
			ParseObject.pinAll(Constants.TAG_CONNECTIONS, survivorConnections);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/

		return connections;
	}

	@Override
	public void onBackPressed() {
		if (currentFragment == null
				|| Constants.FRAGMENT_SEARCHABLE.equals(currentFragment
						.getName())) {
			finishActivity();
		} else if (Constants.FRAGMENT_PROFILE.equals(currentFragment.getName())) {
			replaceFragment(new SearchableFragment(searchView.getQuery()
					.toString()));
		} else {
			replaceFragment(new ProfileFragment(null, ProfileFragment.getUser()));
		}
	}

	@Override
	public void logoutUser() {

	}

	@Override
	public boolean showProfileSettings() {
		return false;
	}

}
