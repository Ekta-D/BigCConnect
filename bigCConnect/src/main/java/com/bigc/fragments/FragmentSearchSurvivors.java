package com.bigc.fragments;

import android.os.Bundle;
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
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.UserConnections;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.models.ConnectionsModel;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

		loadConnections(false);

		//new loadSurvivorsTask().execute();
	}

	private void searchSurvivors(String SEARCH_KEY) {
		Utils.showProgress(getActivity());
		//ArrayList<Users> searchUsers = Queries.getSearchSurvivorQuery(SEARCH_KEY);

		final ArrayList<Users> searchUsers = new ArrayList<>();
		DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
		mDatabase.child(DbConstants.USERS).orderByChild(DbConstants.NAME_LOWERCASE).startAt(SEARCH_KEY).endAt(SEARCH_KEY+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Utils.hideProgress();
				if(dataSnapshot!=null && dataSnapshot.hasChildren()){
					for( DataSnapshot data: dataSnapshot.getChildren()) {
						if(data.getValue(Users.class).getObjectId()!= FirebaseAuth.getInstance().getCurrentUser().getUid() && data.getValue(Users.class).isDeactivated()==false)
							searchUsers.add(data.getValue(Users.class));
					}
					if(searchUsers.size() == 0) {
						showError("No survivor found");
					} else
						showResult(searchUsers);

				} else if(searchUsers.size() == 0) {
					showError("No survivor found");
				}  /*else {
						showError("Unable to reach server, Please check your connect and try again");
				}*/

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Utils.hideProgress();
				showError("Unable to reach server, Please check your connect and try again");
			}
		});



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

/*	private class loadSurvivorsTask extends
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
	}*/
	List<Users> activeConnections = new ArrayList<>();
	List<Users> pendingConnections = new ArrayList<>();
	UserConnections userConnections = new UserConnections();
	Users connectionUser = null;
	private UserConnections loadConnections(final boolean fromCache) {

		Queries.getUserConnectionsQuery(Preferences.getInstance(getActivity()).getUserFromPreference(), false, getActivity());


		/*Query connectionsQuery = Queries.getUserConnectionsQuery(Preferences.getInstance(getContext()).getUserFromPreference(),false);

		final String currentUid = Preferences.getInstance(getContext()).getString(DbConstants.ID);

		connectionsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot!=null && dataSnapshot.hasChildren()){
					for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
						// TODO: handle the post
						ConnectionsModel connection = postSnapshot.getValue(ConnectionsModel.class);

						if(currentUid.equalsIgnoreCase(connection.getTo())) {
							Users user = new Users();
							user.setObjectId(connection.getTo());
							if (connection.getStatus()) {
								activeConnections.add(user);
							} else {
								pendingConnections.add(user);
							}
						}

						//getUserDetail(connection);

					}

				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});*/


		//userConnections1.activeConnections = activeConnections;
		// TODO: 7/13/2017 Load connections here
		/*ParseQuery<ParseObject> mQuery = Queries.getUserConnectionsQuery(
				ParseUser.getCurrentUser(), fromCache);
		try {
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
		userConnections.activeConnections = Preferences.getInstance(getActivity()).getLocalConnections().get(0);
		userConnections.pendingConnections = Preferences.getInstance(getActivity()).getLocalConnections().get(1);
		if (adapter != null)
			adapter.updateData(null, userConnections.activeConnections,
					userConnections.pendingConnections);
		Utils.hideProgress();
		return userConnections;
	}

/*	private Users getUserDetail(final ConnectionsModel connection) {
		final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

		if(Preferences.getInstance(getContext()).getUserFromPreference().getObjectId().equalsIgnoreCase(connection.getTo())) {
			mReference.child(DbConstants.USERS).child(connection.getTo()).addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if (dataSnapshot != null && dataSnapshot.hasChildren()) {
						connectionUser = dataSnapshot.getValue(Users.class);
						if (connectionUser.getType() != Constants.USER_TYPE.SUPPORTER.ordinal()) {
							if (connectionUser.isStatus())
								activeConnections.add(connection.getFrom());
							else
								pendingConnections.add(obj
										.getParseUser(DbConstants.FROM));
						}
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		}

		return connectionUser;
	}*/

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