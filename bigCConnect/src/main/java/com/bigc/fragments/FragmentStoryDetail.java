package com.bigc.fragments;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
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
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc.interfaces.UploadStoryObserver;
import com.bigc.views.NestedListView;
import com.bigc_connect.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FragmentStoryDetail extends BaseFragment implements
		UploadStoryObserver, PopupOptionHandler {

	private CommentsAdapter adapter;
	private static ParseObject story = null;
	private static PopupOptionHandler handler = null;
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

	public FragmentStoryDetail(PopupOptionHandler handler, ParseObject story,
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

		adapter = new CommentsAdapter(getActivity());
		listView.setAdapter(adapter);

		if (!Preferences.getInstance(getActivity()).getBoolean(
				Constants.PREMIUM)) {
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
		}

		ParseUser owner = story.getParseUser(DbConstants.USER);
		headingOne.setText(owner.getString(DbConstants.NAME));
		if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
				.ordinal()) {
			ribbonView.setImageResource(R.drawable.ribbon_supporter);
		} else if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
				.ordinal()) {
			ribbonView
					.setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
							: Utils.fighter_ribbons[owner
									.getInt(DbConstants.RIBBON)]);
		} else {
			ribbonView
					.setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
							: Utils.survivor_ribbons[owner
									.getInt(DbConstants.RIBBON)]);
		}

		if (story.getParseFile(DbConstants.MEDIA) != null)
			ImageLoader.getInstance().displayImage(
					story.getParseFile(DbConstants.MEDIA).getUrl(), picView,
					Utils.normalDisplayOptions,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							picView.setImageResource(R.drawable.loading_img);
							super.onLoadingStarted(imageUri, view);
						}
					});
		else
			picView.setVisibility(View.GONE);

		loveCountView
				.setText(String
						.valueOf(story.getList(DbConstants.LIKES) == null ? 0
								: story.getList(DbConstants.LIKES).size()));

		commentCountView.setText(String.valueOf(story
				.getInt(DbConstants.COMMENTS)));

		titleView.setText(story.getString(DbConstants.TITLE) == null ? ""
				: story.getString(DbConstants.TITLE));
		statusView.setText(story.getString(DbConstants.MESSAGE));
		dateView.setText(Utils.getTimeStringForFeed(getActivity(),
				story.getCreatedAt()));

		ribbonView.setOnClickListener(this);
		headingOne.setOnClickListener(this);
		loveCountView.setOnClickListener(this);

		loadComments();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Preferences.getInstance(getActivity())
				.getBoolean(Constants.PREMIUM)) {
			adView.setVisibility(View.GONE);
		}
	}

	private void loadComments() {
		ParseQuery<ParseObject> mQuery = Queries.getStoryCommentsQuery(story);
		mQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> comments, ParseException e) {
				if (e == null) {
					new completeCommentLoadingsTask(comments).execute();
				} else {
					showLoadingError();
				}
			}
		});
	}

	private class completeCommentLoadingsTask extends
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
				for (ParseObject p : comments)
					p.getParseUser(DbConstants.USER).fetchIfNeeded();
				return comments;
			} catch (ParseException e2) {
				e2.printStackTrace();
				return null;
			}
		}

		@Override
		public void onPostExecute(List<ParseObject> comments) {
			showComments(comments);
		}
	}

	private void showLoadingError() {
		try {
			progressView.setVisibility(View.GONE);
			messageView.setText(R.string.networkFailureMessage);
		} catch (NullPointerException e) {

		}
	}

	private void showComments(List<ParseObject> objects) {
		try {
			progressParent.setVisibility(View.GONE);
			adapter.setData(objects);
			listView.setVisibility(View.VISIBLE);
		} catch (NullPointerException e) {

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.optionView:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Story 3-Dots Options");
			Utils.showQuickActionMenu(
					FragmentStoryDetail.this,
					getActivity(),
					position,
					story,
					v,
					story.getParseUser(DbConstants.USER).getObjectId()
							.equals(ParseUser.getCurrentUser().getObjectId()),
					DbConstants.Flags.Story);
			break;
		case R.id.newsFeedRibbonView:
		case R.id.newsFeedHeading1:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"User-Info View");
			((FragmentHolder) getActivity())
					.replaceFragment(new ProfileFragment(
							FragmentStoryDetail.this, story
									.getParseUser(DbConstants.USER)));
			break;
		case R.id.addAStoryOptionImage:
		case R.id.addAStoryOptionText:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Add A Story Button");
			Utils.launchPostView(getActivity(), Constants.OPERATION_STORY);
			break;
		case R.id.nameView:
		case R.id.ribbonView:
			((FragmentHolder) getActivity())
					.replaceFragment(new ProfileFragment(
							FragmentStoryDetail.this, story
									.getParseUser(DbConstants.USER)));
			break;
		case R.id.newsFeedPicView:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Story Picture");
			if (story.getParseFile(DbConstants.MEDIA) != null)
				Utils.openImageZoomView(getActivity(),
						story.getParseFile(DbConstants.MEDIA).getUrl());
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
				adapter.addItem(PostManager.getInstance().commentOnStory(
						comment, story));
				Toast.makeText(getActivity(), "Comment posted",
						Toast.LENGTH_SHORT).show();
				commentCountView.setText(String.valueOf(Integer
						.valueOf(commentCountView.getText().toString()) + 1));
			}
			break;
		case R.id.loveCount:
		case R.id.loveImage:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Story-Love Button");
			if (!isLiked(story)) {
				loveCountView.setText(String.valueOf(story
						.getList(DbConstants.LIKES) == null ? 1 : story
						.getList(DbConstants.LIKES).size() + 1));
				story.add(DbConstants.LIKES, ParseUser.getCurrentUser()
						.getObjectId());
				PostManager.getInstance().likeStory(story);
			}

		}
	}

	private boolean isLiked(ParseObject post) {

		List<String> likes = post.getList(DbConstants.LIKES);
		if (likes == null)
			return false;

		return likes.contains(ParseUser.getCurrentUser().getObjectId());
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
	};

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity())
				.replaceFragment(new FragmentSurvivorStories());
		return true;
	}

	@Override
	public void onNotify(ParseObject story) {

	}

	@Override
	public void onEditDone(int position, ParseObject post) {
		Log.e(FragmentStoryDetail.class.getSimpleName(), "onEditDone");
		FragmentStoryDetail.story = post;
		statusView.setText(story.getString(DbConstants.MESSAGE) == null ? ""
				: story.getString(DbConstants.MESSAGE));
	}

	@Override
	public void onFlagClicked(int position, ParseObject post) {
		if (post == null) {
			post = story;
		}
		if (post != null) {
			Utils.flagStory(post);
		}

		Toast.makeText(getActivity(),
				getResources().getString(R.string.storyFlagMessage),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDelete(int position, ParseObject story) {
		PostManager.getInstance().deletePost(story);
		handler.onDelete(position, story);
		((HomeScreen) getActivity()).onBackPressed();
	}

	@Override
	public void onEditClicked(int position, ParseObject post) {
		Log.e("onEditClicked", "Done");
		Utils.launchEditView(getActivity(), Constants.OPERATION_STORY,
				position, post);
	}
}
