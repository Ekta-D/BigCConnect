package com.bigc.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigc.adapters.PhotoAdapter;
import com.bigc.adapters.PhotosAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.models.Posts;
import com.bigc_connect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotosFragment extends BaseFragment {

    // private PhotosAdapter adapter;
    private PhotoAdapter adapter;
    private GridView gridview;
    private TextView messageView;
    private LinearLayout messageViewParent;
    private ProgressBar progressView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_grid, container, false);
        gridview = (GridView) view.findViewById(R.id.gridview);
        messageView = (TextView) view.findViewById(R.id.messageView);
        messageViewParent = (LinearLayout) view
                .findViewById(R.id.messageViewParent);
        progressView = (ProgressBar) view.findViewById(R.id.progressView);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "User-Photos Screen");

//        adapter = new PhotosAdapter(getActivity(), null);
//        gridview.setAdapter(adapter);

//        gridview.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Utils.openImageZoomView(getActivity(), adapter
//                        .getItem(position).getParseFile(DbConstants.MEDIA)
//                        .getUrl());
//            }
//        });

        messageView.setText(R.string.loadingPhotos);
        loadUserData();
    }

    private void loadUserData() {
        // ParseQuery<ParseObject> mQuery = Queries
        // .getUserConnectionStatusQuery(ProfileFragment.getUser());
        // mQuery.fromPin(Constants.TAG_CONNECTIONS);
        // mQuery.getFirstInBackground(new GetCallback<ParseObject>() {
        //
        // @Override
        // public void done(ParseObject object, ParseException e) {
        // boolean isConnected = false;
        // if (e == null && object != null
        // && object.getBoolean(DbConstants.STATUS))
        // isConnected = true;
        //
        // Log.e("isConnected", isConnected + "--");
        // adapter.setClickable(isConnected);
        loadData();
        // }
        // });

    }

    Map<String, Object> media;
    ArrayList<Posts> posts_array;

    private void loadData() {
        //    Utils.showProgress(getActivity());
        Log.e("Loading", "data");
//        ParseQuery<ParseObject> query = Queries
//                .getUserPictureFeedsQuery(ProfileFragment.getUser());
        // if (ProfileFragment.getUser().getObjectId()
        // .equals(ParseUser.getCurrentUser().getObjectId())) {
        // query.fromLocalDatastore();
        // }

        media = new HashMap<>();
        posts_array = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_POST).orderByChild("user")
                .equalTo(Preferences.getInstance(getActivity()).getString(DbConstants.ID));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long children_count = dataSnapshot.getChildrenCount();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    Posts posts=new Posts();
                    posts = child.getValue(Posts.class);
                    posts_array.add(posts);


                    //   Utils.hideProgress();
                }
                setAdapter(posts_array);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Utils.hideProgress();
            }
        });


//		query.findInBackground(new FindCallback<ParseObject>() {
//
//			@Override
//			public void done(final List<ParseObject> posts, ParseException e) {
//
//				if (e == null) {
//
//					new completePostLoadingsTask(posts).execute();
//
//				} else {
//					populateList(null);
//				}
//
//			}
//		});
    }

    public void setAdapter(ArrayList<Posts> posts) {
        gridview.setVisibility(View.VISIBLE);
        messageViewParent.setVisibility(View.GONE);
        adapter = new PhotoAdapter(posts, getActivity());
        gridview.setAdapter(adapter);
    }

    private class completePostLoadingsTask extends
            AsyncTask<Void, Void, List<ParseObject>> {

        List<ParseObject> posts;

        public completePostLoadingsTask(List<ParseObject> objects) {
            this.posts = new ArrayList<ParseObject>();
            if (objects != null)
                this.posts.addAll(objects);

        }

        @Override
        public List<ParseObject> doInBackground(Void... params) {
            try {
                for (ParseObject p : posts)
                    p.getParseUser(DbConstants.USER).fetchIfNeeded();
                return posts;
            } catch (ParseException e2) {
                e2.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(List<ParseObject> posts) {
            //   populateList(posts);
        }
    }

//    private void populateList(List<ParseObject> posts) {
//
//        if (gridview != null) {
//            if (posts == null) {
//                showError(Utils.loadString(getActivity(),
//                        R.string.networkFailureMessage));
//            } else if (posts.size() == 0) {
//                showError(Utils.loadString(getActivity(),
//                        R.string.noPhotosPostedYet));
//            } else {
//                adapter.setData(posts);
//                gridview.setVisibility(View.VISIBLE);
//                messageViewParent.setVisibility(View.GONE);
//            }
//        }
//    }

    private void showError(String message) {
        gridview.setVisibility(View.GONE);
        messageViewParent.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
        messageView.setText(message);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_PHOTOS;
    }

    @Override
    public int getTab() {
        return 0;
    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity()).replaceFragment(new ProfileFragment(
                null, null));
        return true;
    }
}
