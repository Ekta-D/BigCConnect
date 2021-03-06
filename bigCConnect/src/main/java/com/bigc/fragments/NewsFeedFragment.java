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
import android.widget.Toast;

import com.bigc.adapters.NewsFeedsAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.PostManager;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc.interfaces.UploadPostObserver;
import com.bigc.models.ConnectionsModel;
import com.bigc.models.Messages;
import com.bigc.models.Posts;
import com.bigc.models.Tributes;
import com.bigc_connect.R;
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;/*
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;*/
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.bigc.adapters.NewsfeedAdapter;

public class NewsFeedFragment extends BaseFragment implements
       /* OnRefreshListener,*/ UploadPostObserver, OnLoadMoreListener,
        PopupOptionHandler {
    public static Posts currentObject = null;
    private PullAndLoadListView listView;
    //	private NewsfeedAdapter adapter;
    private NewsFeedsAdapter adapter;
    private MoPubAdAdapter mAdAdapter;
    private RequestParameters mRequestParameters;
    private TextView messageView;
    private LinearLayout progressParent;
    private ProgressBar progressView;
    private boolean isPremium;
    Posts post;

    //private List<ParseObject> posts = new ArrayList<ParseObject>();
    List<Posts> posts = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds_layout, container,
                false);

        view.findViewById(R.id.postParent).setOnClickListener(this);
        view.findViewById(R.id.photoParent).setOnClickListener(this);
        messageView = (TextView) view.findViewById(R.id.messageView);
        progressView = (ProgressBar) view.findViewById(R.id.progressView);
        progressParent = (LinearLayout) view
                .findViewById(R.id.messageViewParent);
        listView = (PullAndLoadListView) view.findViewById(R.id.listview);

        adapter = new NewsFeedsAdapter(this, posts);
        listView.setAdapter(adapter);
        if (posts.size() == 0) {
            startProgress();
            loadData(true);
        }


        isPremium = Preferences.getInstance(getActivity()).getBoolean(
                Constants.PREMIUM);
        if (!isPremium) {

            ViewBinder viewBinder = new ViewBinder.Builder(
                    R.layout.list_item_ad).mainImageId(R.id.newsFeedPicView)
                    .iconImageId(R.id.newsFeedRibbonView)
                    .titleId(R.id.newsFeedHeading1)
                    .textId(R.id.newsFeedMessageView).build();

            MoPubNativeAdPositioning.MoPubServerPositioning adPositioning = MoPubNativeAdPositioning
                    .serverPositioning();
            MoPubStaticNativeAdRenderer adRenderer = new MoPubStaticNativeAdRenderer(
                    viewBinder);

            mAdAdapter = new MoPubAdAdapter(getActivity(), adapter,
                    adPositioning);
            mAdAdapter.registerAdRenderer(adRenderer);

            listView.setAdapter(mAdAdapter);
        } else {
            listView.setAdapter(adapter);
        }


        /*FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_CONNECTIONS).orderByChild(DbConstants.FROM).equalTo(Preferences.getInstance(getActivity()).getString(DbConstants.ID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ConnectionsModel connectionsModel = dataSnapshot1.getValue(ConnectionsModel.class);
                    System.out.println("check pending status: "+connectionsModel.getStatus());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isPremium) {
            // Set up your request parameters
            mRequestParameters = new RequestParameters.Builder().keywords(
                    "medical, health, cancer, ad").build();

            // Request ads when the user returns to this activity.
            if (mAdAdapter != null)
                mAdAdapter.loadAds(Constants.MOPUB_UNIT_ID, mRequestParameters);
        }
        loadData(true);
        if (NewsFeedFragment.currentObject != null) {
            //   Log.i("post", post.toString());
//            statusView.setText(post.getMessage() == null ? ""
//                    : post.getMessage());

            Utils.updatePost(NewsFeedFragment.currentObject);
            //adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!isPremium) {
            if (mAdAdapter != null)
                mAdAdapter.destroy();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "NewsFeed Screen");

        if (!Preferences.getInstance(getActivity()).getBoolean(
                Constants.SPLASHES, true)) {
            view.findViewById(R.id.splashTextView).setVisibility(View.GONE);
        }

        //listView.setOnRefreshListener(this);
        listView.setOnLoadMoreListener(this);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Log.e("Clicked on", position + "--");
                if (!isPremium) {
                    int temp = mAdAdapter.getOriginalPosition(position);
                    if (temp >= 0) {
                        position = temp;
                    }
                    Log.e("original Position", position + "--");
                }


                if (position - 1 < adapter.getCount())
                    ((FragmentHolder) getActivity())
                            .replaceFragment(new PostDetailFragment(
                                    NewsFeedFragment.this, position,
                                    (Posts) adapter.getItem(position), true));

            }
        });

        Log.e("Posts", posts.size() + "--");
        if (posts.size() == 0) {
            startProgress();
            loadData(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photoParent:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Add Photo Button");
                Utils.launchPostView(getActivity(), Constants.OPERATION_PHOTO);
                break;
            case R.id.postParent:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Add Status Button");
                Utils.launchPostView(getActivity(), Constants.OPERATION_STATUS);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        PostManager.getInstance().addObserver(this);
    }

    @Override
    public void onDestroy() {
        PostManager.getInstance().removePostObserver();
        super.onDestroy();
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_NEWSFEED;
    }

    @Override
    public int getTab() {
        return 1;
    }

/*    @Override
    public void onRefresh() {
        //loadData(false);

    }*/

    @Override
    public void onLoadMore() {

        Log.e("LoadMore", "Request");
        // loadPosts(adapter.getLastItemDate(), false);
    }

    HashMap<String, Posts> feedHashMap = new HashMap<>();

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            if (dataSnapshot.exists()) {
                feedHashMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(Posts.class));

                populateList(feedHashMap.values());
            }
            //if (isMoreLoading) {
            // Log.e("new posts", posts.size() + "--");
                /*boolean isRecent = true;
                adapter.addItems(tributesHashMap.values(), isRecent);
                long count = dataSnapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    //Posts posts = dataSnapshot1.getValue(Posts.class);
                    Map<Object, Object> posts = (Map<Object, Object>) dataSnapshot1.getValue();
                    Posts post = new Posts();
                    post.setMessage(String.valueOf(posts.get(DbConstants.MESSAGE)));
                    post.setMedia(String.valueOf(posts.get(DbConstants.MEDIA)));
                    post.setLikes((ArrayList<String>) posts.get(DbConstants.LIKES));
                    post.setCreatedAt(String.valueOf(posts.get(DbConstants.CREATED_AT)));
                    post.setUpdatedAt(String.valueOf(posts.get(DbConstants.UPDATED_AT)));
                    post.setComments(Integer.parseInt(String.valueOf(posts.get(DbConstants.COMMENTS))));
                    post.setUser(String.valueOf(posts.get(DbConstants.USER)));
                    post.setObjectId(String.valueOf(posts.get(DbConstants.ID)));
                    posts_arraylist.add(post);
                }

                populateList(posts_arraylist);
                Log.i("datasnap", dataSnapshot.toString());*/
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            posts.clear();

            if (dataSnapshot.exists()) {
                feedHashMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(Posts.class));

                populateList(feedHashMap.values());
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                feedHashMap.remove(dataSnapshot.getKey());

                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void loadData(final boolean fromCache) {

//        final List<Posts> posts_arraylist = new ArrayList<>();
        final List<Posts> posts_arraylist = new ArrayList<>();
        Query query = null;
        if (fromCache) {
            query = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_POST);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isRecent = true;
                //adapter.addItems(feedHashMap.values(), isRecent);
                long count = dataSnapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    feedHashMap.put(dataSnapshot1.getKey(), dataSnapshot1.getValue(Posts.class));
                    String key = dataSnapshot1.getKey();
                    //Posts posts = dataSnapshot1.getValue(Posts.class);
                    Map<Object, Object> posts = (Map<Object, Object>) dataSnapshot1.getValue();
                    Posts post = new Posts();
                    post.setMessage(String.valueOf(posts.get(DbConstants.MESSAGE)));
                    post.setMedia(String.valueOf(posts.get(DbConstants.MEDIA)));
                    post.setLikes((ArrayList<String>) posts.get(DbConstants.LIKES));
                    post.setCreatedAt(String.valueOf(posts.get(DbConstants.CREATED_AT)));
                    post.setUpdatedAt(String.valueOf(posts.get(DbConstants.UPDATED_AT)));
                    post.setComments(Integer.parseInt(String.valueOf(posts.get(DbConstants.COMMENTS))));
                    post.setUser(String.valueOf(posts.get(DbConstants.USER)));
                    post.setObjectId(String.valueOf(posts.get(DbConstants.ID)));
                    posts_arraylist.add(post);
                }

                populateList(feedHashMap.values());
                Log.i("datasnap", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // query.addChildEventListener(childEventListener);

//
//        ParseQuery<ParseObject> query = Queries.getFeedsQuery(fromCache);
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//
//            @Override
//            public void done(final List<ParseObject> posts, ParseException e) {
//
//                if (e == null) {
//
//                    Log.e("Posts", posts.size() + "--");
//
//                    if (posts.size() == 0 && fromCache) {
//                        // Load data from network
//                        loadData(false);
//                        return;
//                    }
//
//                    new completePostLoadingsTask(posts, false, false).execute();
//                } else {
//                    if (!fromCache) {
//                        if (listView != null) {
//                            listView.onRefreshComplete();
//                            if (e.getCode() == ParseException.CONNECTION_FAILED) {
//                                populateList(null);
//                            } else {
//                                populateList(new ArrayList<ParseObject>());
//                            }
//                            // listView.onRefreshComplete();
//                            // Toast.makeText(
//                            // getActivity(),
//                            // Utils.loadString(getActivity(),
//                            // R.string.networkFailureMessage),
//                            // Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        populateList(null);
//                    }
//                }
//            }
//        });


    }



    //    private class completePostLoadingsTask extends
//            AsyncTask<Void, Void, List<ParseObject>> {
//
//        private List<ParseObject> posts;
//        private boolean isMoreLoading;
//        private boolean isRecent;
//
//        public completePostLoadingsTask(List<ParseObject> objects,
//                                        boolean isMoreLoading, boolean isRecent) {
//            this.posts = new ArrayList<ParseObject>();
//            if (objects != null)
//                this.posts.addAll(objects);
//
//            this.isMoreLoading = isMoreLoading;
//            this.isRecent = isRecent;
//        }
//
//        @Override
//        public List<ParseObject> doInBackground(Void... params) {
//            try {
//                ParseObject.unpinAll(Constants.TAG_POSTS);
//                for (ParseObject p : posts)
//                    p.getParseUser(DbConstants.USER).fetchIfNeeded();
//                ParseObject.pinAll(Constants.TAG_POSTS, posts);
//                return posts;
//            } catch (ParseException e2) {
//                e2.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        public void onPostExecute(List<ParseObject> posts) {
//            if (isMoreLoading) {
//                Log.e("new posts", posts.size() + "--");
//                adapter.addItems(posts, isRecent);
//                if (!isRecent)
//                    listView.onLoadMoreComplete();
//            } else {
//                populateList(posts);
//            }
//        }
//    }
    ArrayList<Posts> postsArrayList;

    private void populateList(Collection<Posts> posts) {


        this.posts.clear();
        if (listView != null) {
            if (posts == null) {
                showError(Utils.loadString(getActivity(),
                        R.string.networkFailureMessage));
            } else if (posts.size() == 0) {
                showError(Utils.loadString(getActivity(),
                        R.string.noFeedMessage));
            } else {
                /*listView.setVisibility(View.VISIBLE);
                adapter.setData(posts);
                listView.setVisibility(View.VISIBLE);
                progressParent.setVisibility(View.GONE);
                this.posts.addAll(posts);*//*
                //adapter = new NewsFeedsAdapter(getActivity(), this.posts);
                listView.setAdapter(adapter);
                progressParent.setVisibility(View.GONE);*/
            }

        }


        if (posts != null && posts.size() > 0) {
            postsArrayList = new ArrayList<>();
            postsArrayList.addAll(posts);
            Collections.sort(postsArrayList, new Comparator<Posts>() {
                @Override
                public int compare(Posts comments, Posts t1) {
                /*if (Utils.convertStringToDate(comments.getUpdatedAt()) == null || Utils.convertStringToDate(t1.getUpdatedAt()) == null)
                    return 0;
                return Utils.convertStringToDate(comments.getUpdatedAt()).compareTo(Utils.convertStringToDate(t1.getUpdatedAt()));*/
                    try {
                        SimpleDateFormat dateFormatlhs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date convertedDatelhs = dateFormatlhs.parse(comments.getCreatedAt());
                        Calendar calendarlhs = Calendar.getInstance();
                        calendarlhs.setTime(convertedDatelhs);

                        SimpleDateFormat dateFormatrhs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date convertedDaterhs = dateFormatrhs.parse(t1.getCreatedAt());
                        Calendar calendarrhs = Calendar.getInstance();
                        calendarrhs.setTime(convertedDaterhs);

                        if (calendarlhs.getTimeInMillis() < calendarrhs.getTimeInMillis()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }


                    return 0;
                }
            });
            Collections.reverse(postsArrayList);
            listView.setVisibility(View.VISIBLE);
            adapter.setData(postsArrayList);
            listView.setVisibility(View.VISIBLE);
            progressParent.setVisibility(View.GONE);
            this.posts.addAll(posts);
            listView.setSelectionAfterHeaderView();
        }


        if (adapter != null && getActivity() != null) {
            if (!isPremium) {

                ViewBinder viewBinder = new ViewBinder.Builder(
                        R.layout.list_item_ad).mainImageId(R.id.newsFeedPicView)
                        .iconImageId(R.id.newsFeedRibbonView)
                        .titleId(R.id.newsFeedHeading1)
                        .textId(R.id.newsFeedMessageView).build();

                MoPubNativeAdPositioning.MoPubServerPositioning adPositioning = MoPubNativeAdPositioning
                        .serverPositioning();
                MoPubStaticNativeAdRenderer adRenderer = new MoPubStaticNativeAdRenderer(
                        viewBinder);


                mAdAdapter = new MoPubAdAdapter(getActivity(), adapter,
                        adPositioning);
                mAdAdapter.registerAdRenderer(adRenderer);
                listView.setAdapter(mAdAdapter);
            } else {
                listView.setAdapter(adapter);
            }
        }

    }

    public void sortArray(List<Posts> postses) {

        Collections.sort(postses, new Comparator<Posts>() {
            @Override
            public int compare(Posts comments, Posts t1) {
                int result = 0;
                if (comments == null || t1 == null) {
                    result = 0;
//                    return 0;
                } else {
                    try {
                        SimpleDateFormat dateFormatlhs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date convertedDatelhs = dateFormatlhs.parse(comments.getCreatedAt());
                        Calendar calendarlhs = Calendar.getInstance();
                        calendarlhs.setTime(convertedDatelhs);

                        SimpleDateFormat dateFormatrhs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date convertedDaterhs = dateFormatrhs.parse(t1.getCreatedAt());
                        Calendar calendarrhs = Calendar.getInstance();
                        calendarrhs.setTime(convertedDaterhs);

                        if (calendarlhs.getTimeInMillis() > calendarrhs.getTimeInMillis()) {

                            result = -1;
//                            return -1;
                        } else {

                            result = 1;
//                            return 1;

                        }
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }

                }
                return result;

            }
        });
    }

//    private void populateList(List<ParseObject> posts) {
//
//        this.posts.clear();
//        if (listView != null) {
//            if (posts == null) {
//                showError(Utils.loadString(getActivity(),
//                        R.string.networkFailureMessage));
//            } else if (posts.size() == 0) {
//                showError(Utils.loadString(getActivity(),
//                        R.string.noFeedMessage));
//            } else {
//                adapter.setData(posts);
//                listView.setVisibility(View.VISIBLE);
//                progressParent.setVisibility(View.GONE);
//                this.posts.addAll(posts);
//            }
//            listView.onRefreshComplete();
//        }
//    }

    private void startProgress() {
        try {
            messageView.setText(R.string.loadingFeeds);
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

    @Override
    public void onNotify(final Tributes post) {
        if (post == null) {
            Toast.makeText(getActivity(), "Upload status is failed, try again",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            //post.getUser();
//            post.getParseUser(DbConstants.USER).fetchIfNeeded();
//            post.pin(Constants.TAG_POSTS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (posts != null)
            // posts.add(0, post);

            if (listView == null)
                return;

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    //  adapter.addItem(post);
                    if (listView.getVisibility() == View.GONE) {
                        listView.setVisibility(View.VISIBLE);
                        progressParent.setVisibility(View.GONE);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    private void loadPosts(Date from, final boolean recent) {
//        ParseQuery<ParseObject> query = Queries.getFeedsQuery(false);
//
//        if (recent)
//            query.whereGreaterThan(DbConstants.CREATED_AT, from);
//        else
//            query.whereLessThan(DbConstants.CREATED_AT, from);
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//
//            @Override
//            public void done(final List<ParseObject> posts, ParseException e) {
//
//                if (e == null) {
//
//                    new completePostLoadingsTask(posts, true, recent).execute();
//                } else {
//                    listView.onLoadMoreComplete();
//                }
//
//            }
//        });
//    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDelete(int position, Object post) {
        if (position >= 0 && position < posts.size())
            posts.remove(position);
    }

    @Override
    public void onEditClicked(int position, Object post) {
        Log.e("onEditClicked", "Done");

        //   ParseObject obj = post == null ? adapter.getItem(position) : post;
//        Utils.launchEditView(
//                getActivity(),
//                obj.getParseFile(DbConstants.MEDIA) == null ? Constants.OPERATION_STATUS
//                        : Constants.OPERATION_PHOTO, position, obj);
    }

    @Override
    public void onEditDone(int position, Object post) {
        Log.e(NewsFeedFragment.class.getSimpleName(), "onEditDone");
        adapter.updateItem(position, (Posts) post);
    }

    @Override
    public void onFlagClicked(int position, Object post) {
        if (post == null) {
            //  post = adapter.getItem(position);
        }
        if (post != null) {
            Utils.flagFeed(post);
        }
        Toast.makeText(getActivity(),
                getResources().getString(R.string.postFlagMessage),
                Toast.LENGTH_SHORT).show();
    }
}