package com.bigc.fragments;

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

import com.bigc.adapters.SearchResultPictureAdapter;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.UserConnections;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.models.Comments;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CategorySurvivorsFragment extends BaseFragment implements
        OnLoadMoreListener {

    public static int ribbon;
    private LoadMoreListView listView;
    private SearchResultPictureAdapter adapter;
    private TextView messageView;
    private LinearLayout progressParent;
    private ProgressBar progressView;
    private TextView titleView;

    private List<Users> users = new ArrayList<>();
    private HashMap<String, Users> usersHashMap;
    private UserConnections connections = new UserConnections();
    DatabaseReference databaseReference;

    public CategorySurvivorsFragment() {
    }

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

        databaseReference = FirebaseDatabase.getInstance().getReference();

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
//        adapter.processUserSettings();
//        connections = adapter.getUserConnections();
        databaseReference.removeEventListener(valueEventListener);
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

        Query query = Queries.getCategorizedUsersQuery(ribbon);
        query.addValueEventListener(valueEventListener);


        //populateList(Queries.getCategorizedUsersQuery(ribbon));

		/*ParseQuery<ParseUser> query = Queries.getCategorizedUsersQuery(ribbon);

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
		});*/

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null) {
                Log.i("CategorySurvivors", dataSnapshot.toString());

                if (usersHashMap == null)
                    usersHashMap = new HashMap();
                usersHashMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    if (user.getType() != Constants.USER_TYPE.SUPPORTER.ordinal() && user.getVisibility() != Constants.PRIVATE && user.isDeactivated() != true)
                        usersHashMap.put(snapshot.getKey(), snapshot.getValue(Users.class));
                }
                List<Map.Entry<String, Users>> list = new LinkedList<>(usersHashMap.entrySet());
                Collections.sort(list, new Comparator<HashMap.Entry<String, Users>>() {
                    @Override
                    public int compare(HashMap.Entry<String, Users> usersHashMap1, HashMap.Entry<String, Users> usersHashMap2) {
                        return ((usersHashMap1.getValue()).getCreatedAt()).compareTo((usersHashMap2.getValue()).getCreatedAt());
                    }
                });
                usersHashMap = new LinkedHashMap<>();
                for (Map.Entry<String, Users> entry : list) {
                    usersHashMap.put(entry.getKey(), entry.getValue());
                }
                populateList(usersHashMap.values());
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void populateList(Collection<Users> users) {

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
        // TODO: 7/13/2017 Get posts of the user from a date
        /*ParseQuery<ParseUser> query = Queries.getCategorizedUsersQuery(ribbon);

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
		});*/
    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity())
                .replaceFragment(new FragmentSearchSurvivors());
        return true;
    }
}