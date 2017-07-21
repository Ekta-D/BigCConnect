package com.bigc.fragments;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Layout;
import android.text.style.LeadingMarginSpan.LeadingMarginSpan2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.adapters.CommentsAdapter;
import com.bigc.dialogs.AddTributeDialog;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc.interfaces.UploadPostObserver;
import com.bigc.models.Posts;
import com.bigc.models.Tributes;
import com.bigc.models.Users;
import com.bigc.views.NestedListView;
import com.bigc_connect.R;

//public class FragmentTributeDetail extends BaseFragment implements
//		PopupOptionHandler, UploadPostObserver //// TODO: 14-07-2017  
public class FragmentTributeDetail extends BaseFragment implements
		 UploadPostObserver
{

	private CommentsAdapter adapter;
	private static Object tribute = null;
	private static PopupOptionHandler handler = null;
	private static int position = -1;

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

	public FragmentTributeDetail(PopupOptionHandler handler,
			Tributes tribute, int position) {
		FragmentTributeDetail.tribute = tribute;
		FragmentTributeDetail.handler = handler;
		FragmentTributeDetail.position = position;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_tribute_detail,
				container, false);

		headingOne = (TextView) view.findViewById(R.id.newsFeedHeading1);
		dateView = (TextView) view.findViewById(R.id.dateView);
		ribbonView = (ImageView) view.findViewById(R.id.newsFeedRibbonView);
		statusView = (TextView) view.findViewById(R.id.newsFeedMessageView);

		picView = (ImageView) view.findViewById(R.id.newsFeedPicView);
		ribbonView = (ImageView) view.findViewById(R.id.newsFeedRibbonView);

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
		picView.setVisibility(View.GONE);

		optionView.setOnClickListener(this);
		picView.setOnClickListener(this);
		postButton.setOnClickListener(this);

		view.findViewById(R.id.leftOptionParent).setOnClickListener(this);
		return view;
	}

/*	@Override
	public void onStart() {
		super.onStart();
		PostManager.getInstance().addTributeObserver(this);
	}

	@Override
	public void onDestroy() {
		PostManager.getInstance().removeTributeObserver();
		super.onDestroy();
	}*/

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Tribute Detail Screen");
//
//		adapter = new CommentsAdapter(getActivity());
//		listView.setAdapter(adapter);

		/*if (tribute.isDataAvailable()) {
			populateData();
		} else {
			tribute.fetchIfNeededInBackground(new GetCallback<ParseObject>() {

				@Override
				public void done(ParseObject object, ParseException e) {
					if (e == null) {
						tribute = object;
						populateData();
					}
				}
			});
		}*/
	}

	private void populateOwnerFields(Users owner) {
		/*headingOne.setText(owner.getString(DbConstants.NAME));
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
*/
		// else {
		// ribbonView
		// .setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ?
		// R.drawable.ic_launcher
		// : Utils.survivor_ribbons[owner
		// .getInt(DbConstants.RIBBON)]);
		// }
	}

	private void populateData() {
		/*ParseUser owner = tribute.getParseUser(DbConstants.TO);
		if (!owner.isDataAvailable()) {
			owner.fetchIfNeededInBackground(new GetCallback<ParseUser>() {

				@Override
				public void done(ParseUser object, ParseException e) {
					populateOwnerFields(object);
				}
			});
		} else {
			populateOwnerFields(owner);
		}

		if (tribute.getParseFile(DbConstants.MEDIA) != null) {
			picView.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					tribute.getParseFile(DbConstants.MEDIA).getUrl(), picView,
					Utils.normalDisplayOptions,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							picView.setImageResource(R.drawable.loading_img);
							super.onLoadingStarted(imageUri, view);
						}
					});
		} else {
			picView.setVisibility(View.GONE);
		}

		loveCountView
				.setText(String
						.valueOf(tribute.getList(DbConstants.LIKES) == null ? 0
								: tribute.getList(DbConstants.LIKES).size()));

		commentCountView.setText(String.valueOf(tribute
				.getInt(DbConstants.COMMENTS)));

		statusView.setText(tribute.getString(DbConstants.MESSAGE));
		dateView.setText(Utils.getTimeStringForFeed(getActivity(),
				tribute.getCreatedAt()));

		ribbonView.setOnClickListener(this);
		headingOne.setOnClickListener(this);
		loveCountView.setOnClickListener(this);

		loadComments();*/
	}

	/*private void loadComments() {
		ParseQuery<ParseObject> mQuery = Queries
				.getTributeCommentsQuery(tribute);
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
*/
/*
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
			showComments(comments);
		}
	}
*/

	private void showLoadingError() {
		try {
			progressView.setVisibility(View.GONE);
			messageView.setText(R.string.networkFailureMessage);
		} catch (NullPointerException e) {

		}
	}

	/*private void showComments(List<ParseObject> objects) {
		try {
			progressParent.setVisibility(View.GONE);
			//adapter.setData(objects);
			listView.setVisibility(View.VISIBLE);
		} catch (NullPointerException e) {

		}
	}*/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.optionView:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Tribute 3-Dots Options");
			//// TODO: 14-07-2017  
//			Utils.showQuickActionMenu(
//					FragmentTributeDetail.this,
//					getActivity(),
//					position,
//					tribute,
//					v,
//					(tribute.getParseUser(DbConstants.USER).getObjectId()
//							.equals(ParseUser.getCurrentUser().getObjectId()) || tribute
//							.getParseUser(DbConstants.TO).getObjectId()
//							.equals(ParseUser.getCurrentUser().getObjectId())),
//					DbConstants.Flags.Tribute);
			break;
		case R.id.newsFeedRibbonView:
		case R.id.newsFeedHeading1:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Tribute-User-Info View");
			//// TODO: 14-07-2017
//			((FragmentHolder) getActivity())
//					.replaceFragment(new ProfileFragment(
//							FragmentTributeDetail.this, tribute
//									.getParseUser(DbConstants.TO)));
			break;
		case R.id.leftOptionParent:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Add A Tribute Button");
			new AddTributeDialog(getActivity(), this).show();
			break;
		case R.id.nameView:
		case R.id.ribbonView:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Tribute-User-Infor View");
			//// TODO: 14-07-2017
//			((FragmentHolder) getActivity())
//					.replaceFragment(new ProfileFragment(
//							FragmentTributeDetail.this, tribute
//									.getParseUser(DbConstants.TO)));
			break;
		case R.id.newsFeedPicView:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Tribute Picture");
			/*if (tribute.getParseFile(DbConstants.MEDIA) != null)
				Utils.openImageZoomView(getActivity(),
						tribute.getParseFile(DbConstants.MEDIA).getUrl());*/
			break;
		case R.id.postButton:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Post Tribute Comment Button");
			if (commentInputView.getText().length() == 0) {
				Toast.makeText(getActivity(), "Write comment",
						Toast.LENGTH_SHORT).show();
			} else {
				Utils.hideKeyboard(getActivity());
				String comment = commentInputView.getText().toString();
				commentInputView.setText("");
//				adapter.addItem(PostManager.getInstance().commentOnTribute(
//						comment, tribute));
				Toast.makeText(getActivity(), "Comment posted",
						Toast.LENGTH_SHORT).show();
				commentCountView.setText(String.valueOf(Integer
						.valueOf(commentCountView.getText().toString()) + 1));
			}
			break;
		case R.id.loveCount:
		case R.id.loveImage:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Love Tribute Button");
			/*if (!Utils.isLiked(tribute)) {
				loveCountView.setText(String.valueOf(tribute
						.getList(DbConstants.LIKES) == null ? 1 : tribute
						.getList(DbConstants.LIKES).size() + 1));
				tribute.add(DbConstants.LIKES, ParseUser.getCurrentUser()
						.getObjectId());
				PostManager.getInstance().likeStory(tribute);
			}*/

		}
	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_TRIBUTE_DETAIL;
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
				.replaceFragment(new FragmentTributes());
		return true;
	}
//// TODO: 14-07-2017  
//	@Override
//	public void onDelete(int position, ParseObject tribute) {
//		//PostManager.getInstance().deletePost(tribute);
//		if (handler != null)
//	//		handler.onDelete(position, tribute);
//		((HomeScreen) getActivity()).onBackPressed();
//	}
//
//	@Override
//	public void onEditClicked(int position, ParseObject post) {
//		Log.e("onEditClicked", "Done");
////		Utils.launchEditView(getActivity(), Constants.OPERATION_TRIBUTE,
////				position, post);
//	}

	@Override
	public void onNotify(Tributes post) {

	}

	@Override
	public void onEditDone(int position, Posts tribute) {
//		Log.e(FragmentTributeDetail.class.getSimpleName(), "onEditDone - "
//				+ tribute.getString(DbConstants.MESSAGE));
//		FragmentTributeDetail.tribute = tribute;
//		statusView.setText(tribute.getString(DbConstants.MESSAGE) == null ? ""
//				: tribute.getString(DbConstants.MESSAGE));
	}
	//// TODO: 14-07-2017

//	@Override
//	public void onFlagClicked(int position, ParseObject post) {
////		if (post == null) {
////			post = tribute;
////		}
////		if (post != null) {
////			Utils.flagTribute(post);
////		}
//		Toast.makeText(getActivity(),
//				getResources().getString(R.string.tributeFlagMessage),
//				Toast.LENGTH_SHORT).show();
//	}
}
