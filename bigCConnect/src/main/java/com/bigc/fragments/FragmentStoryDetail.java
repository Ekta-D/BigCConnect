package com.bigc.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Layout;
import android.text.style.LeadingMarginSpan.LeadingMarginSpan2;
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
import com.bigc.interfaces.StoryPopupOptionHandler;
import com.bigc.interfaces.UploadStoryObserver;
import com.bigc.models.Comments;
import com.bigc.models.Posts;
import com.bigc.models.Stories;
import com.bigc.views.NestedListView;
import com.bigc_connect.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

//public class FragmentStoryDetail extends BaseFragment implements
//		UploadStoryObserver, PopupOptionHandler //// TODO: 14-07-2017
public class FragmentStoryDetail extends BaseFragment implements
        UploadStoryObserver, StoryPopupOptionHandler {

    private CommentsAdapter adapter;
    //    private static ParseObject story = null;
    private static Stories story = null;
    private static StoryPopupOptionHandler handler = null;
    //    private static PopupOptionHandler handler = null;
    private static int position = -1;

    private AdView adView;
    private TextView headingOne;
    private TextView dateView;
    private ImageView ribbonView;
    private ImageView picView;
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
    private TextView titleView;
    DatabaseReference databaseReference;

    //	public FragmentStoryDetail(PopupOptionHandler handler, ParseObject story,
//			int position)
    public FragmentStoryDetail(StoryPopupOptionHandler handler, Stories story,
                               int position) {
        FragmentStoryDetail.story = story;
        FragmentStoryDetail.position = position;
        FragmentStoryDetail.handler = handler;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_story_detail, container,
                false);

        titleView = (TextView) view.findViewById(R.id.newsFeedTitleView);
        headingOne = (TextView) view.findViewById(R.id.newsFeedHeading1);
        dateView = (TextView) view.findViewById(R.id.dateView);
        ribbonView = (ImageView) view.findViewById(R.id.newsFeedRibbonView);
        statusView = (TextView) view.findViewById(R.id.newsFeedMessageView);

        picView = (ImageView) view.findViewById(R.id.newsFeedPicView);
        ribbonView = (ImageView) view.findViewById(R.id.newsFeedRibbonView);
        adView = (AdView) view.findViewById(R.id.adView);

        commentCountView = (TextView) view.findViewById(R.id.commentCountView);
        loveCountView = (TextView) view.findViewById(R.id.loveCount);
        view.findViewById(R.id.loveImage).setOnClickListener(this);

        listView = (NestedListView) view.findViewById(R.id.listview);
        progressParent = (LinearLayout) view
                .findViewById(R.id.messageViewParent);
        progressView = (ProgressBar) view.findViewById(R.id.progressView);
        messageView = (TextView) view.findViewById(R.id.messageView);
        commentInputView = (EditText) view.findViewById(R.id.commentInput);
        postButton = (TextView) view.findViewById(R.id.postButton);
        optionView = (ImageView) view.findViewById(R.id.optionView);

        optionView.setOnClickListener(this);
        picView.setOnClickListener(this);
        postButton.setOnClickListener(this);
        view.findViewById(R.id.addAStoryOptionImage).setOnClickListener(this);
        view.findViewById(R.id.addAStoryOptionText).setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "Survivor Story Detail Screen");

        //adapter = new CommentsAdapter(getActivity());
        //listView.setAdapter(adapter);

        if (!Preferences.getInstance(getActivity()).getBoolean(
                Constants.PREMIUM)) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        String user = story.getUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS).child(user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {

                } else {

                    String key = dataSnapshot.getKey();
                    Map<Object, Object> user_values = (Map<Object, Object>) dataSnapshot.getValue();
                    setValues(user_values, story);
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //     ParseUser owner = story.getParseUser(DbConstants.USER);
//        headingOne.setText(owner.getString(DbConstants.NAME));
//        if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                .ordinal()) {
//            ribbonView.setImageResource(R.drawable.ribbon_supporter);
//        } else if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//                .ordinal()) {
//            ribbonView
//                    .setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
//                            : Utils.fighter_ribbons[owner
//                            .getInt(DbConstants.RIBBON)]);
//        } else {
//            ribbonView
//                    .setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
//                            : Utils.survivor_ribbons[owner
//                            .getInt(DbConstants.RIBBON)]);
//        }
//
//        if (story.getParseFile(DbConstants.MEDIA) != null)
//            ImageLoader.getInstance().displayImage(
//                    story.getParseFile(DbConstants.MEDIA).getUrl(), picView,
//                    Utils.normalDisplayOptions,
//                    new SimpleImageLoadingListener() {
//                        @Override
//                        public void onLoadingStarted(String imageUri, View view) {
//                            picView.setImageResource(R.drawable.loading_img);
//                            super.onLoadingStarted(imageUri, view);
//                        }
//                    });
//        else
//            picView.setVisibility(View.GONE);
//
//        loveCountView
//                .setText(String
//                        .valueOf(story.getList(DbConstants.LIKES) == null ? 0
//                                : story.getList(DbConstants.LIKES).size()));
//
//        commentCountView.setText(String.valueOf(story
//                .getInt(DbConstants.COMMENTS)));
//
//        titleView.setText(story.getString(DbConstants.TITLE) == null ? ""
//                : story.getString(DbConstants.TITLE));
//        statusView.setText(story.getString(DbConstants.MESSAGE));
//        dateView.setText(Utils.getTimeStringForFeed(getActivity(),
//                story.getCreatedAt()));
//
//        ribbonView.setOnClickListener(this);
//        headingOne.setOnClickListener(this);
//        loveCountView.setOnClickListener(this);

        loadComments();
    }

    public void setValues(Map<Object, Object> user_values, Stories story) {

        headingOne.setText(String.valueOf(user_values.get(DbConstants.NAME)));
        if (Integer.parseInt(String.valueOf(user_values.get(DbConstants.TYPE))) == Constants.IS_SUPPORTER) {
            ribbonView.setImageResource(R.drawable.ribbon_supporter);
        } else if (Integer.parseInt(String.valueOf(user_values.get(DbConstants.TYPE))) == Constants.IS_FIGHTER) {
            ribbonView
                    .setImageResource(Integer.parseInt(String.valueOf(user_values.get(DbConstants.RIBBON))) < 0 ? R.drawable.ic_launcher
                            : Utils.fighter_ribbons[Integer.parseInt(String.valueOf(user_values.get(DbConstants.RIBBON)))]);
        } else {
            ribbonView
                    .setImageResource(Integer.parseInt(String.valueOf(user_values.get(DbConstants.RIBBON))) < 0 ? R.drawable.ic_launcher
                            : Utils.survivor_ribbons[Integer.parseInt(String.valueOf(user_values.get(DbConstants.RIBBON)))]);
        }

        if (String.valueOf(user_values.get(DbConstants.PROFILE_PICTURE)) != null)
        {
            ImageLoader.getInstance().displayImage(
                    String.valueOf(user_values.get(DbConstants.PROFILE_PICTURE)),
                    picView, Utils.normalDisplayOptions,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String uri, View imageView) {
                            picView.setImageResource(R.drawable.default_profile_pic);
                        }
                    });
        }

        else
        {
            picView.setVisibility(View.GONE);
        }


        loveCountView
                .setText(String
                        .valueOf(story.getLikes() == null ? 0
                                : story.getLikes().size()));

        commentCountView.setText(String.valueOf(story
                .getComments()));

        titleView.setText(story.getTitle() == null ? ""
                : story.getTitle());
        statusView.setText(story.getMessage());
        dateView.setText(Utils.getTimeStringForFeed(getActivity(),
                Utils.convertStringToDate(story.getCreatedAt())));

        ribbonView.setOnClickListener(this);
        headingOne.setOnClickListener(this);
        loveCountView.setOnClickListener(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (Preferences.getInstance(getActivity())
                .getBoolean(Constants.PREMIUM)) {
            adView.setVisibility(View.GONE);
        }
    }

    List<Comments> comment_list;
    long comment_count;

    private void loadComments() {

        Query query = Queries.getStoryCommentQuery(story);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("comments", dataSnapshot.toString());
                comment_count = dataSnapshot.getChildrenCount();
                comment_list = new ArrayList<Comments>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    Comments comments = data.getValue(Comments.class);
                    comment_list.add(comments);
                }
                showComments(comment_list, comment_count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // ParseQuery<ParseObject> mQuery = Queries.getStoryCommentsQuery(story);
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
    }


    @Override
    public void onDelete(int position, Stories story) {
        PostManager.getInstance().deleteStory(story);
        handler.onDelete(position, story);
        ((HomeScreen) getActivity()).onBackPressed();
    }

    @Override
    public void onEditClicked(int position, Stories story) {
        Log.e("onEditClicked", "Done");
        Utils.launchStoryEditView(getActivity(), Constants.OPERATION_STORY,
                position, story);
    }

    @Override
    public void onFlagClicked(int position, Stories story) {

    }

//    private class completeCommentLoadingsTask extends
//            AsyncTask<Void, Void, List<ParseObject>> {
//
//        List<ParseObject> comments;
//
//        public completeCommentLoadingsTask(List<ParseObject> comments) {
//            this.comments = new ArrayList<ParseObject>();
//            if (comments != null)
//                this.comments.addAll(comments);
//
//        }
//
//        @Override
//        public List<ParseObject> doInBackground(Void... params) {
//            try {
//                for (ParseObject p : comments)
//                    p.getParseUser(DbConstants.USER).fetchIfNeeded();
//                return comments;
//            } catch (ParseException e2) {
//                e2.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        public void onPostExecute(List<ParseObject> comments) {
//            showComments(comments);
//        }
//    }

    private void showLoadingError() {
        try {
            progressView.setVisibility(View.GONE);
            messageView.setText(R.string.networkFailureMessage);
        } catch (NullPointerException e) {

        }
    }

    private void showComments(List<Comments> objects, long comment_count) {
//        try {
//            progressParent.setVisibility(View.GONE);
//            //adapter.setData(objects);
//            listView.setVisibility(View.VISIBLE);
//        } catch (NullPointerException e) {
//
//        }
        if (objects != null) {
            progressParent.setVisibility(View.GONE);
            Collections.sort(objects, new Comparator<Comments>() {
                @Override
                public int compare(Comments comments, Comments t1) {
                    return Integer.parseInt(String.valueOf(Utils.convertStringToDate(comments.getCreatedAt()).
                            compareTo(Utils.convertStringToDate(t1.getCreatedAt()))));
                }
            });
            adapter = new CommentsAdapter(getActivity(), objects);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
            commentCountView.setText(String.valueOf(comment_count));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.optionView:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Story 3-Dots Options");

                boolean isOwner = story.getUser().equals(Preferences.getInstance(getActivity()).getString(DbConstants.ID));
                Utils.storyQuickActionMenu(FragmentStoryDetail.this, getActivity(),
                        position, story, v, isOwner, DbConstants.Flags.Story);
//			Utils.showQuickActionMenu(
//					FragmentStoryDetail.this,
//					getActivity(),
//					position,
//					story,
//					v,
//					story.getParseUser(DbConstants.USER).getObjectId()
//							.equals(ParseUser.getCurrentUser().getObjectId()),
//					DbConstants.Flags.Story);
                break;
            case R.id.newsFeedRibbonView:
            case R.id.newsFeedHeading1:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "User-Info View");
                //// TODO: 14-07-2017
//			((FragmentHolder) getActivity())
//					.replaceFragment(new ProfileFragment(
//							FragmentStoryDetail.this, story
//									.getParseUser(DbConstants.USER)));
                break;
            case R.id.addAStoryOptionImage:
            case R.id.addAStoryOptionText:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Add A Story Button");
                Utils.launchPostView(getActivity(), Constants.OPERATION_STORY);
                break;
            case R.id.nameView:
            case R.id.ribbonView:
                //// TODO: 14-07-2017
//			((FragmentHolder) getActivity())
//					.replaceFragment(new ProfileFragment(
//							FragmentStoryDetail.this, story
//									.getParseUser(DbConstants.USER)));
                break;
            case R.id.newsFeedPicView:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Story Picture");
//                if (story.getParseFile(DbConstants.MEDIA) != null)
//                    Utils.openImageZoomView(getActivity(),
//                            story.getParseFile(DbConstants.MEDIA).getUrl());
                if (story.getMedia() != null)
                    Utils.openImageZoomView(getActivity(),
                            story.getMedia());
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
//				adapter.addItem(PostManager.getInstance().commentOnStory(
//						comment, story));
                    adapter.addItem(commentOnStory(getActivity(), comment, story));
                    Toast.makeText(getActivity(), "Comment posted",
                            Toast.LENGTH_SHORT).show();
//                    commentCountView.setText(String.valueOf(Integer
//                            .valueOf(commentCountView.getText().toString()) + 1));
                    commentCountView.setText(String.valueOf(story.getComments() + 1));

                    story.setComments(Integer.parseInt(commentCountView.getText().toString()));

                }
                break;
            case R.id.loveCount:
            case R.id.loveImage:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Story-Love Button");
                if (!isLiked(story)) {
                    loveCountView.setText(String.valueOf(story.getLikes() == null ? 1 : story.getLikes().size() + 1));
                    ArrayList<String> likes = new ArrayList<>();
                    likes.add(Preferences.getInstance(getActivity()).getString(DbConstants.ID));
                    story.setLikes(likes);
                    PostManager.getInstance().likeStory(likes, story);
                }


        }
    }

    private boolean isLiked(Stories story) {
// TODO: 7/18/2017 work on likes
        List<String> likes = story.getLikes();
        if (likes == null)
            return false;
        else
            return likes.contains(Preferences.getInstance(getActivity()).getString(DbConstants.ID));

    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_STORY_DETAIL;
    }

    @Override
    public int getTab() {
        return 0;
    }

    class MyLeadingMarginSpan2 implements LeadingMarginSpan2 {
        private int margin;
        private int lines;

        MyLeadingMarginSpan2(int lines, int margin) {
            this.margin = margin;
            this.lines = lines;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            if (first) {
                return margin;
            } else {
                return 0;
            }
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                      int top, int baseline, int bottom, CharSequence text,
                                      int start, int end, boolean first, Layout layout) {
        }

        @Override
        public int getLeadingMarginLineCount() {
            return lines;
        }
    }

    ;

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity())
                .replaceFragment(new FragmentSurvivorStories());
        return true;
    }

    @Override
    public void onNotify(Stories story) {

    }

    @Override
    public void onEditDone(int position, Posts post) {
//        Log.e(FragmentStoryDetail.class.getSimpleName(), "onEditDone");
//        FragmentStoryDetail.story = post;
//        statusView.setText(story.getString(DbConstants.MESSAGE) == null ? ""
//                : story.getString(DbConstants.MESSAGE));//// TODO: 17-07-2017


    }
    //// TODO: 14-07-2017


//	@Override
//	public void onFlagClicked(int position, ParseObject post) {
//		if (post == null) {
//			post = story;
//		}
//		if (post != null) {
//			Utils.flagStory(post);
//		}
//
//		Toast.makeText(getActivity(),
//				getResources().getString(R.string.storyFlagMessage),
//				Toast.LENGTH_SHORT).show();
//	}
//
//	@Override
//	public void onDelete(int position, ParseObject story) {
//		PostManager.getInstance().deletePost(story);
//		handler.onDelete(position, story);
//		((HomeScreen) getActivity()).onBackPressed();
//	}
//

    public Comments commentOnStory(Context context, String comment, final Stories story) {

//        final ParseObject obj = new ParseObject(DbConstants.TABLE_COMMENT);
        final Comments obj = new Comments();
        obj.setPost(story.getObjectId());
        obj.setMessage(comment);
        obj.setUser(Preferences.getInstance(context).getString(DbConstants.ID));
        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT);
        String date = format.format(new Date(System.currentTimeMillis()));
        obj.setCreatedAt(date);
        obj.setUpdatedAt(date);
        String objectId = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_STORY_COMMENT).push().getKey();
        obj.setObjectId(objectId);
        FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_STORY_COMMENT).child(objectId).setValue(obj);
        return obj;

    }
}
