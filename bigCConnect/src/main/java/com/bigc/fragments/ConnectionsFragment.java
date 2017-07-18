package com.bigc.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigc.adapters.PendingRequestsAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.models.ConnectionsModel;
import com.bigc_connect.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ConnectionsFragment extends BaseFragment {

	private ProgressBar progressView;
	private LinearLayout messageViewParent;
	private TextView messageView;
	private ListView listView;
	private PendingRequestsAdapter adapter;
	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_connections_layout,
				container, false);
		view.findViewById(R.id.inviteSupportersParent).setOnClickListener(this);
		view.findViewById(R.id.findSurvivorsParent).setOnClickListener(this);
		view.findViewById(R.id.inviteSurvivorsParent).setOnClickListener(this);
		listView = (ListView) view.findViewById(R.id.listview);
		messageView = (TextView) view.findViewById(R.id.messageView);
		adView = (AdView) view.findViewById(R.id.adView);
		messageViewParent = (LinearLayout) view
				.findViewById(R.id.messageViewParent);
		progressView = (ProgressBar) view.findViewById(R.id.progressView);
		adapter = new PendingRequestsAdapter(getActivity());
		listView.setAdapter(adapter);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Connections Screen");
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		loadPendingRequest();
		//new loadPendingRequestTask().execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		View view = this.getView();
		if (Preferences.getInstance(getActivity())
				.getBoolean(Constants.PREMIUM)) {
			adView.setVisibility(View.GONE);
			if (!Preferences.getInstance(getActivity()).getBoolean(
					Constants.SPLASHES, true)) {
				view.findViewById(R.id.splashTextView).setVisibility(View.GONE);
			}
		} else {
			if (Preferences.getInstance(getActivity()).getBoolean(
					Constants.SPLASHES, true)) {
				adView.setVisibility(View.GONE);
			} else {
				view.findViewById(R.id.splashTextView).setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.inviteSurvivorsParent:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Invite-Survivors Button");
			Utils.inviteSupporters(getActivity(), false);

			break;
		case R.id.findSurvivorsParent:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Find-Survivors Button");
			((FragmentHolder) getActivity())
					.replaceFragment(new FragmentSearchSurvivors());
			break;
		case R.id.inviteSupportersParent:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Invite-Supporters Button");
			Utils.inviteSupporters(getActivity(), true);
			break;
		}
	}
	List<ConnectionsModel> requests;
	private void loadPendingRequest(){
		DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
		mDatabase.child(DbConstants.CONNECTIONS).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				requests = new ArrayList<>();
				if(dataSnapshot!=null && dataSnapshot.hasChildren()) {
					for( DataSnapshot data: dataSnapshot.getChildren()) {
						ConnectionsModel connection = data.getValue(ConnectionsModel.class);
						requests.add(connection);
						System.out.println("ddata conenction "+data.toString());
					}
					showRequests(requests);
				} else {
					showNoRequestsMessage();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

/*	private class loadPendingRequestTask extends
			AsyncTask<Void, Void, List<ParseObject>> {

		@Override
		protected List<ParseObject> doInBackground(Void... params) {




			ParseQuery<ParseObject> query = Queries.getPendingRequestsQuery();
			try {
				List<ParseObject> requests = query.find();
				Log.e("Objects", requests.size() + "-");
				for (ParseObject c : requests)
					c.getParseUser(DbConstants.FROM).fetchIfNeeded();
				ParseObject.pinAll(Constants.TAG_CONNECTIONS, requests);
				return requests;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void onPostExecute(List<ParseObject> requests) {
			if (requests == null || requests.size() == 0) {
				showNoRequestsMessage();
			} else {
				showRequests(requests);
			}
		}

	}*/

	@Override
	public String getName() {
		return Constants.FRAGMENT_CONNECTIONS;
	}

	private void showRequests(List<ConnectionsModel> requests) {
		try {
			adapter.updateData(requests);
			listView.setVisibility(View.VISIBLE);
			messageViewParent.setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showNoRequestsMessage() {
		try {
			listView.setVisibility(View.GONE);
			messageViewParent.setVisibility(View.VISIBLE);
			progressView.setVisibility(View.GONE);
			messageView.setText("No pending requests");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getTab() {
		return 2;
	}

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity())
				.replaceFragment(new NewsFeedFragment());
		return true;
	}
}
