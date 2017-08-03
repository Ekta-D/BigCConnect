package com.bigc.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import eu.janmuller.android.simplecropimage.Util;

public class SupportersFragment extends BaseFragment {

    private SearchResultAdapter adapter;
    private ListView listview;
    private ProgressBar progressView;
    private LinearLayout messageViewParent;
    private TextView messageView;
    private static boolean supporters;

    public SupportersFragment(boolean supporters) {

        SupportersFragment.supporters = supporters;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new SearchResultAdapter(getActivity(), null, null);
        return inflater.inflate(R.layout.layout_list_with_margin, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "User-Supporters/Survivors Screen");

        listview = (ListView) view.findViewById(R.id.listview);
        messageView = (TextView) view.findViewById(R.id.messageView);
        messageViewParent = (LinearLayout) view
                .findViewById(R.id.messageViewParent);
        progressView = (ProgressBar) view.findViewById(R.id.progressView);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ((FragmentHolder) getActivity())
                        .replaceFragment(new ProfileFragment(null, adapter
                                .getItem(position)));
                ProfileFragment.showProfileOnBack();
            }
        });
        if (supporters)
            messageView.setText(R.string.loadingSupporters);
        else
            messageView.setText(R.string.loadingSupporting);

        //	new loadUserConnectionsTask().execute(ProfileFragment.getUser());
        loadUserConnectionsTask();
    }


    public void loadUserConnectionsTask() {
        ArrayList<Users> active = Preferences.getInstance(getActivity()).getLocalConnections().get(0);
        ArrayList<Users> pending = Preferences.getInstance(getActivity()).getLocalConnections().get(1);
//        ArrayList<Users> connections = new ArrayList<>();
//        connections.addAll(active);
//        connections.addAll(pending);
        showData(active, pending);
        //  Log.i("connections", connections.toString());
//        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        boolean isCurrentUser=currentUser.equals(users.getObjectId());
//         UserConnections userConnections = new UserConnections();

    }
    //	private class loadUserConnectionsTask extends
//			AsyncTask<ParseUser, Void, List<ParseUser>> {
//		private UserConnections userConnections = new UserConnections();
//
//		@Override
//		protected List<ParseUser> doInBackground(ParseUser... params) {
//			if (params == null || params.length == 0 || params[0] == null)
//				return null;
//
//			boolean isCurrentUser = ParseUser.getCurrentUser().getObjectId()
//					.equals(params[0].getObjectId());
//			userConnections = loadUserConnections(!isCurrentUser);
//
//			return isCurrentUser ? userConnections.activeConnections
//					: loadConnections(params[0], false);
//		}
//
//		@Override
//		public void onPostExecute(List<ParseUser> supporters) {
//			if (supporters != null && supporters.size() > 0) {
//				showData(supporters, userConnections);
//			} else {
//				showNoDataMessage();
//			}
//		}
//	}
  /*  private void loadUserConnectionsTask() {
        String current_userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (current_userId.equalsIgnoreCase(Preferences.getInstance(getActivity()).getString(DbConstants.ID)))
        {

        }
    }*///// TODO: 25-07-2017  


    /* private UserConnections loadUserConnections(final boolean fromCache) {

         ParseQuery<ParseObject> mQuery = Queries.getUserActiveConnectionsQuery(
                 ParseUser.getCurrentUser(), fromCache);
         UserConnections connections = new UserConnections();

         try {
             List<ParseObject> survivorConnections = mQuery.find();
             Log.e("Result", survivorConnections.size() + " - ");

             if (fromCache && survivorConnections.size() == 0)
                 return loadUserConnections(false);

             String cOId = ParseUser.getCurrentUser().getObjectId();
             for (ParseObject obj : survivorConnections) {
                 if (SupportersFragment.supporters) {
                     if (obj.getParseUser(DbConstants.TO).getObjectId()
                             .equals(cOId)) {

                         if (obj.getParseUser(DbConstants.FROM).fetchIfNeeded()
                                 .getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
                                 .ordinal()) {
                             // TODO: 7/14/2017 show status
                             *//*if (obj.getBoolean(DbConstants.STATUS))
                                connections.activeConnections.add(obj
                                        .getParseUser(DbConstants.FROM));
                            else
                                connections.pendingConnections.add(obj
                                        .getParseUser(DbConstants.FROM));*//*
                        }

                    } else {
                        if (obj.getParseUser(DbConstants.TO).fetchIfNeeded()
                                .getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
                                .ordinal()) {
                            // TODO: 7/14/2017 show type 
                            *//*if (obj.getBoolean(DbConstants.STATUS))
                                connections.activeConnections.add(obj
                                        .getParseUser(DbConstants.TO));
                            else
                                connections.pendingConnections.add(obj
                                        .getParseUser(DbConstants.TO));*//*
                        }
                    }
                } else {
                    if (obj.getParseUser(DbConstants.TO).getObjectId()
                            .equals(cOId)) {

                        if (obj.getParseUser(DbConstants.FROM).fetchIfNeeded()
                                .getInt(DbConstants.TYPE) != Constants.USER_TYPE.SUPPORTER
                                .ordinal()) {
                            // TODO: 7/14/2017 get from user 
                            *//*if (obj.getBoolean(DbConstants.STATUS))
                                connections.activeConnections.add(obj
                                        .getParseUser(DbConstants.FROM));
                            else
                                connections.pendingConnections.add(obj
                                        .getParseUser(DbConstants.FROM));*//*
                        }

                    } else {
                        if (obj.getParseUser(DbConstants.TO).fetchIfNeeded()
                                .getInt(DbConstants.TYPE) != Constants.USER_TYPE.SUPPORTER
                                .ordinal()) {
                            // TODO: 7/14/2017 get to user 
                            *//*if (obj.getBoolean(DbConstants.STATUS))
                                connections.activeConnections.add(obj
                                        .getParseUser(DbConstants.TO));
                            else
                                connections.pendingConnections.add(obj
                                        .getParseUser(DbConstants.TO));*//*
                        }
                    }
                }
            }
            ParseObject.unpinAll(Constants.TAG_CONNECTIONS);
            ParseObject.pinAll(Constants.TAG_CONNECTIONS, survivorConnections);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return connections;
    }
*/
  /*  private List<ParseUser> loadConnections(ParseUser user, boolean searchLocal) {

        ParseQuery<ParseObject> mQuery = Queries.getUserActiveConnectionsQuery(
                user, searchLocal);

        List<ParseUser> data = new ArrayList<ParseUser>();

        try {
            final List<ParseObject> connections = mQuery.find();
            for (ParseObject obj : connections)
                if (obj.getBoolean(DbConstants.STATUS))
                    if (supporters) {

                        if (obj.getParseUser(DbConstants.TO).getObjectId()
                                .equals(user.getObjectId())) {
                            if (obj.getParseUser(DbConstants.FROM)
                                    .fetchIfNeeded().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
                                    .ordinal())
                                data.add(obj.getParseUser(DbConstants.FROM));
                        } else {
                            if (obj.getParseUser(DbConstants.TO)
                                    .fetchIfNeeded().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
                                    .ordinal())
                                data.add(obj.getParseUser(DbConstants.TO));
                        }

                    } else {

                        if (obj.getParseUser(DbConstants.TO).getObjectId()
                                .equals(user.getObjectId())) {
                            if (obj.getParseUser(DbConstants.FROM)
                                    .fetchIfNeeded().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SURVIVOR
                                    .ordinal())
                                data.add(obj.getParseUser(DbConstants.FROM));
                        } else {
                            if (obj.getParseUser(DbConstants.TO)
                                    .fetchIfNeeded().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SURVIVOR
                                    .ordinal())
                                data.add(obj.getParseUser(DbConstants.TO));
                        }
                    }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }
*/
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onStop() {
        //  adapter.processUserSettings();
        super.onStop();
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_SUPPORTERS;
    }

    @Override
    public int getTab() {
        return 0;
    }

    private void showNoDataMessage() {
        try {
            listview.setVisibility(View.GONE);
            /*if (supporters) {
                if (ProfileFragment.getUser().getObjectId()
                        .equals(ParseUser.getCurrentUser().getObjectId()))
                    messageView.setText(R.string.NoSupporterMessage);
                else
                    messageView.setText("User currently has no supporters.");
            } else {
                if (ProfileFragment.getUser().getObjectId()
                        .equals(ParseUser.getCurrentUser().getObjectId()))
                    messageView.setText(R.string.NoSupportingMessage);
                else
                    messageView
                            .setText("User is currently not supporting anyone.");
            }
*/
            messageViewParent.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showData(ArrayList<Users> active,
                          ArrayList<Users> pendingConnections) {
        try {

//            if (adapter != null)
                adapter = new SearchResultAdapter(getActivity(), null, null,
                        active);
//            adapter.updateData(null, active,
//                    pendingConnections);
//           adapter.updateData(connections,
//                    loggedInUserConnections.activeConnections,
//                    loggedInUserConnections.pendingConnections);
            listview.setVisibility(View.VISIBLE);
            listview.setAdapter(adapter);
            messageViewParent.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity()).replaceFragment(new ProfileFragment(
                null, null));
        return true;
    }

}