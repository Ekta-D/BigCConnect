package com.bigc.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.activities.HomeScreen;
import com.bigc.adapters.CommentsAdapter;
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
import com.bigc.models.Comments;
import com.bigc.models.Posts;
import com.bigc.models.Tributes;
import com.bigc.models.Users;
import com.bigc.views.NestedListView;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostDetailFragment extends BaseFragment implements
        PopupOptionHandler, UploadPostObserver {

    private CommentsAdapter adapter;
    private static boolean isConnected;
    //    private static ParseObject post = null;
    private static Posts post = null;
    private static int postPosition = -1;
    private static PopupOptionHandler handler = null;

    private TextView headingOne;
    private TextView dateView;
    private ImageView ribbonView;
    private ImageView picView;
    // private ImageView commentIconView;
    private ImageView loveIconView;
    private TextView statusView;
    private TextView commentCountView;
    private TextView loveCountView;
    private LinearLayout progressParent;
    private ProgressBar progressView;
    private TextView messageView;
    private NestedListView listView;
    private EditText commentInputView;
    private TextView postButton;
    private ImageView optionView;

    //	public PostDetailFragment(PopupOptionHandler handler, int position,
//			ParseObject post, boolean isConnected)
    public PostDetailFragment(PopupOptionHandler handler, int position,
                              Posts post, boolean isConnected) {
        PostDetailFragment.post = post;
        PostDetailFragment.postPosition = position;
        PostDetailFragment.handler = handler;
        PostDetailFragment.isConnected = isConnected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_detail, container,
                false);

        headingOne = (TextView) view.findViewById(R.id.newsFeedHeading1);
        dateView = (TextView) view.findViewById(R.id.dateView);
        ribbonView = (ImageView) view.findViewById(R.id.newsFeedRibbonView);
        statusView = (TextView) view.findViewById(R.id.newsFeedMessageView);
        commentCountView = (TextView) view.findViewById(R.id.commentCountView);
        // commentIconView = (ImageView) view.findViewById(R.id.commentImage);
        loveCountView = (TextView) view.findViewById(R.id.loveCount);
        loveIconView = (ImageView) view.findViewById(R.id.loveImage);
        picView = (ImageView) view.findViewById(R.id.newsFeedPicView);
        listView = (NestedListView) view.findViewById(R.id.listview);
        progressParent = (LinearLayout) view
                .findViewById(R.id.messageViewParent);
        progressView = (ProgressBar) view.findViewById(R.id.progressView);
        messageView = (TextView) view.findViewById(R.id.messageView);
        commentInputView = (EditText) view.findViewById(R.id.commentInput);
        postButton = (TextView) view.findViewById(R.id.postButton);
        optionView = (ImageView) view.findViewById(R.id.optionView);
        picView.setVisibility(View.GONE);

        picView.setOnClickListener(this);
        postButton.setOnClickListener(this);
        optionView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "Post-Detail Screen");

//        adapter = new CommentsAdapter(getActivity());
//        listView.setAdapter(adapter);

//        if (!post.isDataAvailable()) {
//            post.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//
//                @Override
//                public void done(ParseObject object, ParseException e) {
//                    if (e == null) {
//                        post = object;
//                        populateData();
//                    } else {
//                        Log.e("Loading", "failed");
//                    }
//                }
//            });
//        }
//        else {
//            populateData();
//        }

        if (post != null) {
            populateData();
        }
    }

    private void populateOwnerData(Users owner) {
        headingOne.setText(owner.getName());
//        if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                .ordinal())
        if (owner.getType() == 1) {
            ribbonView.setImageResource(R.drawable.ribbon_supporter);
        }
//        else if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//                .ordinal())
        else if (owner.getType() == 2) {
            ribbonView
                    .setImageResource(owner.getRibbon() < 0 ? R.drawable.ic_launcher
                            : Utils.fighter_ribbons[owner.getRibbon()]);
        } else {
            ribbonView
                    .setImageResource(owner.getRibbon() < 0 ? R.drawable.ic_launcher
                            : Utils.survivor_ribbons[owner.getRibbon()]);
        }

        // else {
        //
        // ribbonView
        // .setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ?
        // R.drawable.ic_launcher
        // : Utils.survivor_ribbons[owner
        // .getInt(DbConstants.RIBBON)]);
        // }
    }

    private void populateData() {
        //   ParseUser owner = post.getParseUser(DbConstants.USER);

        String user = post.getUser();
        if (!user.equals("")) {

            FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS).child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (dataSnapshot.getValue() == null) {

                    } else {
                        String key = dataSnapshot.getKey();
                        Map<Object, Object> user_values = (Map<Object, Object>) dataSnapshot.getValue();
                        Users user = dataSnapshot.getValue(Users.class);
                        populateOwnerData(user);
                    }


                    Utils.hideProgress();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
//        if (!owner.isDataAvailable()) {
//            owner.fetchInBackground(new GetCallback<ParseUser>() {
//
//                @Override
//                public void done(ParseUser object, ParseException e) {
//                    if (e == null)
//                        populateOwnerData(object);
//                }
//            });
//        } else {
//            populateOwnerData(owner);
//        }

//        loveCountView
//                .setText(String.valueOf(post.getList(DbConstants.LIKES) == null ? 0
//                        : post.getList(DbConstants.LIKES).size()));

        loveCountView
                .setText(String.valueOf(post.getLikes() == null ? 0
                        : post.getLikes().size()));
//        commentCountView.setText(String.valueOf(post
//                .getInt(DbConstants.COMMENTS)));


//        if (post.getParseFile(DbConstants.MEDIA) != null) {
//            picView.setVisibility(View.VISIBLE);
//            picView.setImageResource(R.drawable.loading_img);
//            picView.setVisibility(View.VISIBLE);
//            ImageLoader.getInstance().displayImage(
//                    post.getParseFile(DbConstants.MEDIA).getUrl(), picView,
//                    Utils.normalDisplayOptions,
//                    new SimpleImageLoadingListener() {
//                        @Override
//                        public void onLoadingStarted(String imageUri, View view) {
//                            picView.setImageResource(R.drawable.loading_img);
//                            super.onLoadingStarted(imageUri, view);
//                        }
//                    });
//        }
        if (!post.getMedia().equals("")) {
            picView.setVisibility(View.VISIBLE);
            picView.setImageResource(R.drawable.loading_img);

            ImageLoader.getInstance().displayImage(
                    post.getMedia(), picView,
                    Utils.normalDisplayOptions,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            picView.setImageResource(R.drawable.loading_img);
                            super.onLoadingStarted(imageUri, view);
                        }
                    });
        } else {
            if (post.getMedia().equals(""))
                picView.setVisibility(View.GONE);
        }

        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT, Locale.getDefault());
        Date createdDate = null;

        try {
            if (post.getCreatedAt().contains("T") && post.getCreatedAt().contains("Z")) {
                post.getCreatedAt().replace("T", "");
                post.getCreatedAt().replace("Z", "");
            }
            createdDate = format.parse(post.getCreatedAt());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        dateView.setText(Utils.getTimeStringForFeed(getActivity(),
                createdDate));
        statusView.setText(post.getMessage() == null ? ""
                : post.getMessage());

        ribbonView.setOnClickListener(this);
        headingOne.setOnClickListener(this);
        loveCountView.setOnClickListener(this);
        loveIconView.setOnClickListener(this);
        loadComments(post);
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
    public void onResume() {
        super.onResume();
        if (post != null)
            statusView.setText(post.getMessage() == null ? ""
                    : post.getMessage());

//        Utils.updatePost(post);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.newsFeedRibbonView:
            case R.id.newsFeedHeading1:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Post-User-Info View");
//                ((FragmentHolder) getActivity())
//                        .replaceFragment(new ProfileFragment(
//                                PostDetailFragment.this, post
//                                .getParseUser(DbConstants.USER)));
                break;
            case R.id.optionView:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Post 3-Dots Option");
//                Utils.showQuickActionMenu(
//                        PostDetailFragment.this,
//                        getActivity(),
//                        postPosition,
//                        post,
//                        v,
//                        post.getParseUser(DbConstants.USER).getObjectId()
//                                .equals(ParseUser.getCurrentUser().getObjectId()),
//                        DbConstants.Flags.NewsFeed);
                boolean isOwner = post.getUser().equalsIgnoreCase(Preferences.getInstance(getActivity()).getString(DbConstants.ID));
                Utils.showQuickActionMenu(
                        PostDetailFragment.this,
                        getActivity(),
                        postPosition,
                        post,
                        v,
                        isOwner,
                        DbConstants.Flags.NewsFeed);
                break;
            case R.id.newsFeedPicView:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Post Picture");
                if (post.getMedia() != null)
                    Utils.openImageZoomView(getActivity(),
                            post.getMedia());
                break;
            case R.id.postButton:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Post-Comment Button");
                if (commentInputView.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Write comment",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Utils.hideKeyboard(getActivity());
                    String comment = commentInputView.getText().toString();
                    commentInputView.setText("");
                    NewsFeedFragment.currentObject = post;
                    Comments user_comment = new Comments();
                    user_comment.setMessage(comment);
                    user_comment.setPost(post.getObjectId());
//                    user_comment.setCreatedAt();
//                    adapter.addItem(PostManager.getInstance().commentOnPost(getActivity(),
//                            comment, post));


                    Toast.makeText(getActivity(), "Comment posted",
                            Toast.LENGTH_SHORT).show();
                    commentCountView.setText(String.valueOf(Integer
                            .valueOf(commentCountView.getText().toString()) + 1));
                    int comments_count = Integer.parseInt(commentCountView.getText().toString());
                    Preferences.getInstance(getActivity()).save(DbConstants.COMMENT_COUNT, (int) comments_count);
                    NewsFeedFragment.currentObject.setComments(comments_count);
                    adapter.addItem(PostManager.getInstance().commentOnPost(getActivity(),
                            comment, post));
                }
                break;
            case R.id.loveCount:
            case R.id.loveImage:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Post-Love Button");
                if (!isConnected)
                    return;

                if (!isLiked(post)) {
                    loveCountView.setText(String.valueOf(post
                            .getLikes() == null ? 1 : post.getLikes().size() + 1));
                    ArrayList<String> like = new ArrayList<>();
                    like.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    post.setLikes(like);
                    PostManager.getInstance().likePost(like, post);
                }

        }
    }

    private boolean isLiked(Posts post) {

        List<String> likes = post.getLikes();
        if (likes == null)
            return false;

        return likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_POST_DETAIL;
    }

    @Override
    public int getTab() {
        return 0;
    }

    List<Comments> commentsList;

    private void loadComments(Posts post) {
        //ParseQuery<ParseObject> mQuery = Queries.getPostCommentsQuery(post);

//        mQuery.findInBackground(new FindCallback<ParseObject>() {
//
//            @Override
//            public void done(List<ParseObject> comments, ParseException e) {
//                if (e == null) {
//                    new completeCommentLoadingsTask(comments).execute();
//                } else {
//                    showLoadingError();
//                }
//            }
//        });

        Query query = Queries.getPostCommentSQuery(post);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentsList = new ArrayList<>();
                long count = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    Comments comments = dataSnapshot1.getValue(Comments.class);
                    commentsList.add(comments);
                }
                setCommentsData(commentsList, count);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showLoadingError();
            }
        });


    }

    public void setCommentsData(List<Comments> comments, long comments_count) {
        commentCountView.setText(String.valueOf(comments_count));
        Preferences.getInstance(getActivity()).save(DbConstants.COMMENT_COUNT, (int) comments_count);
        showComments(comments);
    }

    /*private class completeCommentLoadingsTask extends
            AsyncTask<Void, Void, List<ParseObject>> {

        List<ParseObject> comments;

        public completeCommentLoadingsTask(List<ParseObject> comments) {
            this.comments = new ArrayList<ParseObject>();
            if (comments != null)
                this.comments.addAll(comments);

        }

        @Override
        public List<ParseObject> doInBackground(Void... params) {
            try {
                // ParseObject.unpinAll(Constants.TAG_POSTS);
                for (ParseObject p : comments)
                    p.getParseUser(DbConstants.USER).fetchIfNeeded();
                // ParseObject.pinAll(Constants.TAG_POSTS, comments);
                return comments;
            } catch (ParseException e2) {
                e2.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(List<ParseObject> comments) {
            // showComments(comments);
        }
    }*/

    private void showLoadingError() {
        try {
            progressView.setVisibility(View.GONE);
            messageView.setText(R.string.networkFailureMessage);
        } catch (NullPointerException e) {

        }
    }

    //    private void showComments(List<ParseObject> objects) {
//        try {
//            progressParent.setVisibility(View.GONE);
//            adapter.setData(objects);
//            listView.setVisibility(View.VISIBLE);
//        } catch (NullPointerException e) {
//
//        }
//    }
    private void showComments(List<Comments> objects) {
        try {
            progressParent.setVisibility(View.GONE);
            adapter = new CommentsAdapter(getActivity(), objects);
            listView.setAdapter(adapter);
            //  adapter.setData(objects);
            listView.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onDelete(int position, Object post) {
        PostManager.getInstance().deletePost((Posts) post);
        if (handler != null)
            handler.onDelete(position, post);
        ((HomeScreen) getActivity()).onBackPressed();
    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity())
                .replaceFragment(new NewsFeedFragment());
        return true;
    }

    @Override
    public void onEditClicked(int position, Object post) {
        Log.e("onEditClicked", "Done");
//        Utils.launchEditView(
//                getActivity(),
//                post.getParseFile(DbConstants.MEDIA) == null ? Constants.OPERATION_STATUS
//                        : Constants.OPERATION_PHOTO, position, post);
        NewsFeedFragment.currentObject = (Posts) post;
        Utils.launchEditView(
                getActivity(),
                ((Posts) post).getMedia() == null ? Constants.OPERATION_STATUS
                        : Constants.OPERATION_PHOTO, true, position, (Posts) post);
    }

    @Override
    public void onNotify(Tributes post) {

    }

    @Override
    public void onEditDone(int position, Object post) {
        Log.e(PostDetailFragment.class.getSimpleName(), "onEditDone");
        //PostDetailFragment.post = post;
      /*  messageView.setText(post.getString(DbConstants.MESSAGE) == null ? ""
                : post.getString(DbConstants.MESSAGE));*/
    }

    @Override
    public void onFlagClicked(int position, Object post) {
        if (post == null) {
            // post = PostDetailFragment.post;
        }
        if (post != null) {
            Utils.flagFeed(post);
        }
        Toast.makeText(getActivity(),
                getResources().getString(R.string.postFlagMessage),
                Toast.LENGTH_SHORT).show();
    }

}
