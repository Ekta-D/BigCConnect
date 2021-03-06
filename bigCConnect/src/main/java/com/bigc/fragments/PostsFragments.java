package com.bigc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.adapters.NewsFeedsAdapter;
import com.bigc.adapters.NewsfeedAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc.models.Post;
import com.bigc.models.Posts;
import com.bigc.models.Tributes;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

//import com.bigc.adapters.NewsfeedAdapter;

public class PostsFragments extends BaseFragment implements PopupOptionHandler {

    //    private NewsfeedAdapter adapter;
    private NewsFeedsAdapter adapter;
    private ListView listview;
    private TextView messageView;
    private MoPubAdAdapter mAdAdapter;
    private RequestParameters mRequestParameters;
    private LinearLayout messageViewParent;
    private ProgressBar progressView;
    private boolean isPremium;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_list, container, false);

        listview = (ListView) view.findViewById(R.id.listview);
        messageView = (TextView) view.findViewById(R.id.messageView);
        messageViewParent = (LinearLayout) view
                .findViewById(R.id.messageViewParent);
        progressView = (ProgressBar) view.findViewById(R.id.progressView);
        adapter = new NewsFeedsAdapter(this, null);

        isPremium = Preferences.getInstance(getActivity()).getBoolean(
                Constants.PREMIUM);
        if (!isPremium) {

            ViewBinder viewBinder = new ViewBinder.Builder(
                    R.layout.list_item_ad).mainImageId(R.id.newsFeedPicView)
                    .iconImageId(R.id.newsFeedRibbonView)
                    .titleId(R.id.newsFeedHeading1)
                    .textId(R.id.newsFeedMessageView).build();

            // Set up the positioning behavior your ads should have.
            MoPubNativeAdPositioning.MoPubServerPositioning adPositioning = MoPubNativeAdPositioning
                    .serverPositioning();
            MoPubStaticNativeAdRenderer adRenderer = new MoPubStaticNativeAdRenderer(
                    viewBinder);
            // Set up the MoPubAdAdapter
            mAdAdapter = new MoPubAdAdapter(getActivity(), adapter,
                    adPositioning);
            mAdAdapter.registerAdRenderer(adRenderer);

            listview.setAdapter(mAdAdapter);


        } else {
            listview.setAdapter(adapter);
        }

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
            mAdAdapter.loadAds(Constants.MOPUB_UNIT_ID, mRequestParameters);
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!isPremium) {
            mAdAdapter.destroy();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "User-Posts Screen");

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!isPremium) {
                    int temp = mAdAdapter.getOriginalPosition(position);
                    if (temp >= 0) {
                        position = temp;
                    }
                }
                if (position - 1 < adapter.getCount())
                    ((FragmentHolder) getActivity())
                            .replaceFragment(new PostDetailFragment(
                                    PostsFragments.this, position,
                                    (Posts) adapter.getItem(position), true));

            }
        });


        messageView.setText(R.string.loadingFeeds);
        loadUserData();
    }

    private void loadUserData() {
        String user = Preferences.getInstance(getActivity()).getString(DbConstants.ID);

        FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_POST).orderByChild(DbConstants.USER).
                equalTo(user).addValueEventListener(postsListener);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child(DbConstants.TABLE_POST).orderByChild(DbConstants.USER).
                equalTo(Preferences.getInstance(getActivity()).getString(DbConstants.ID));
  /*ParseQuery<ParseObject> mQuery = Queries
    .getUserConnectionStatusQuery(ProfileFragment.getUser());
  mQuery.fromPin(Constants.TAG_CONNECTIONS);
  mQuery.getFirstInBackground(new GetCallback<ParseObject>() {

   @Override
   public void done(ParseObject object, ParseException e) {
    boolean isConnected = false;
    if (e == null && object != null
      && object.getBoolean(DbConstants.STATUS))
     isConnected = true;

    adapter.setClickable(isConnected);
    loadData();
   }
  });*/


    }

    ArrayList<Posts> posts;
    ValueEventListener postsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            long count = dataSnapshot.getChildrenCount();
            posts = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                String key = data.getKey();
                Posts post = data.getValue(Posts.class);
                posts.add(post);
            }
            populateList(posts);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    //// TODO: 14-07-2017  
//    private void loadData() {
//
//        ParseQuery<ParseObject> query = Queries
//                .getUserFeedsQuery(ProfileFragment.getUser());
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//
//            @Override
//            public void done(final List<ParseObject> posts, ParseException e) {
//
//                if (e == null) {
//
//                    new completePostLoadingsTask(posts).execute();
//
//                } else {
//                    populateList(null);
//                }
//
//            }
//        });
//    }

    /*private class completePostLoadingsTask extends
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
            populateList(posts);
        }
    }*/

    private void populateList(List<Posts> posts) {

        if (listview != null) {
            if (posts == null) {
                showError(Utils.loadString(getActivity(),
                        R.string.networkFailureMessage));
            } else if (posts.size() == 0) {
                showError(Utils.loadString(getActivity(),
                        R.string.noUpdatesPostedYet));
            } else {
                if (posts != null && posts.size() > 0) {
                    posts = new ArrayList<>();
                    posts.addAll(posts);
                    Collections.sort(posts, new Comparator<Posts>() {
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
                    Collections.reverse(posts);
                    listview.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                    this.posts.addAll(posts);
                    adapter.setData(this.posts);
                    adapter = new NewsFeedsAdapter(this, this.posts);
                    listview.setAdapter(adapter);
                    listview.setSelectionAfterHeaderView();
                }
            }
        }
    }

    private void showError(String message) {
        listview.setVisibility(View.GONE);
        messageViewParent.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
        messageView.setText(message);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_POSTS;
    }

    @Override
    public int getTab() {
        return 0;
    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity()).replaceFragment(new ProfileFragment(
                PostsFragments.this, Preferences.getInstance(getActivity()).getUserFromPreference(getActivity().getApplicationContext())));
        return true;
    }

    //	@Override
//	public void onDelete(int position, ParseObject post) {
//
//	}

    @Override
    public void onDelete(int position, Object post) {

    }

    //    @Override
//    public void onEditClicked(int position, ParseObject post) {
//        ParseObject obj = post == null ? adapter.getItem(position) : post;
//        Utils.launchEditView(
//                getActivity(),
//                obj.getParseFile(DbConstants.MEDIA) == null ? Constants.OPERATION_MESSAGE
//                        : Constants.OPERATION_PHOTO, position, obj);
//    }
    @Override
    public void onEditClicked(int position, Object post) {
        //  Posts obj = post == null ? adapter.getItem(position) : post;
        Utils.launchEditView(
                getActivity(),
                ((Posts) post).getMedia() == null ? Constants.OPERATION_MESSAGE
                        : Constants.OPERATION_PHOTO, true, position, (Posts) post);
    }

    @Override
    public void onFlagClicked(int position, Object post) {
        if (post == null) {
            post = adapter.getItem(position);
        }
        if (post != null) {
            Utils.flagFeed(post);
        }
        Toast.makeText(getActivity(),
                getResources().getString(R.string.postFlagMessage),
                Toast.LENGTH_SHORT).show();
    }

}
