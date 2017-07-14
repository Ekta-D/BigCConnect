package com.bigc.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.PostManager;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc_connect.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsfeedAdapter extends ArrayAdapter<ParseObject>
	 {

	private LayoutInflater inflater;
	private static ImageLoaderConfiguration config;
	private BaseFragment context;

	private static DisplayImageOptions imgDisplayOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(true).cacheOnDisk(true).build();

	private static ImageLoader imageLoader = ImageLoader.getInstance();
	private List<ParseObject> data;
	private boolean isClickable = true;

	public NewsfeedAdapter(BaseFragment context, List<ParseObject> posts) {
		super(context.getActivity(), R.layout.newsfeed_item_layout);

		this.context = context;
		data = new ArrayList<ParseObject>();
		if (posts != null)
			data.addAll(posts);

		inflater = (LayoutInflater) context.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

		config = new ImageLoaderConfiguration.Builder(context.getActivity())
				.memoryCacheSize(41943040).diskCacheSize(104857600)
				.threadPoolSize(10).build();

		imageLoader.init(config);
	}

	public void setClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}

	@Override
	public ParseObject getItem(int position) {
		if (position < 0 || position >= data.size())
			return null;
		return data.get(position);
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		final ParseObject post = data.get(position);
		final ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.newsfeed_item_layout, parent,
					false);
			holder = new ViewHolder();
			holder.headingOne = (TextView) view
					.findViewById(R.id.newsFeedHeading1);
			holder.dateView = (TextView) view.findViewById(R.id.dateView);
			holder.ribbonView = (ImageView) view
					.findViewById(R.id.newsFeedRibbonView);
			holder.statusView = (TextView) view
					.findViewById(R.id.newsFeedMessageView);
			holder.commentCountView = (TextView) view
					.findViewById(R.id.commentCountView);
			holder.commentIconView = (ImageView) view
					.findViewById(R.id.commentImage);
			holder.loveCountView = (TextView) view.findViewById(R.id.loveCount);
			holder.loveIconView = (ImageView) view.findViewById(R.id.loveImage);
			holder.picView = (ImageView) view
					.findViewById(R.id.newsFeedPicView);
			holder.optionView = (ImageView) view.findViewById(R.id.optionView);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ParseUser owner = post.getParseUser(DbConstants.USER);
		holder.headingOne.setText(owner.getString(DbConstants.NAME));

		if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
				.ordinal()) {
			holder.ribbonView.setImageResource(R.drawable.ribbon_supporter);
		} else if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
				.ordinal()) {
			holder.ribbonView
					.setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
							: Utils.fighter_ribbons[owner
									.getInt(DbConstants.RIBBON)]);
		} else {
			holder.ribbonView
					.setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
							: Utils.survivor_ribbons[owner
									.getInt(DbConstants.RIBBON)]);
		}

		holder.statusView.setText(post.getString(DbConstants.MESSAGE));

		holder.loveCountView.setText(String.valueOf(post
				.getList(DbConstants.LIKES) == null ? 0 : post.getList(
				DbConstants.LIKES).size()));

		holder.commentCountView.setText(String.valueOf(post
				.getInt(DbConstants.COMMENTS)));

		if (post.getParseFile(DbConstants.MEDIA) == null) {
			holder.picView.setVisibility(View.GONE);
		} else {
			holder.picView.setImageResource(R.drawable.loading_img);
			holder.picView.setVisibility(View.VISIBLE);

			imageLoader.displayImage(post.getParseFile(DbConstants.MEDIA)
					.getUrl(), holder.picView, imgDisplayOptions,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.picView
									.setImageResource(R.drawable.loading_img);
							super.onLoadingStarted(imageUri, view);
						}
					});

		}

		holder.dateView.setText(Utils.getTimeStringForFeed(
				context.getActivity(), post.getCreatedAt()));

		holder.loveCountView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickLove(post, holder.loveCountView);
			}
		});

		holder.loveIconView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickLove(post, holder.loveCountView);
			}
		});

		// if () {
		holder.optionView.setVisibility(View.VISIBLE);
		holder.optionView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Utils.showQuickActionMenu(
//						NewsfeedAdapter.this,
//						context.getActivity(),
//						position,
//						null,
//						v,
//						post.getParseUser(DbConstants.USER)
//								.getObjectId()
//								.equals(ParseUser.getCurrentUser()
//										.getObjectId()),
//						DbConstants.Flags.NewsFeed);
			}
		});
		// } else {
		// holder.optionView.setVisibility(View.INVISIBLE);
		// }

		return view;
	}

	private void onClickLove(ParseObject post, TextView countView) {
		if (!isClickable)
			return;

		if (!isLiked(post)) {
			countView
					.setText(String
							.valueOf(post.getList(DbConstants.LIKES) == null ? 1
									: post.getList(DbConstants.LIKES).size() + 1));
			post.add(DbConstants.LIKES, ParseUser.getCurrentUser()
					.getObjectId());
			Log.e("Likes", post.getList(DbConstants.LIKES).size() + "--");
			PostManager.getInstance().likePost(post);
		}

	}

	private boolean isLiked(ParseObject post) {

		List<String> likes = post.getList(DbConstants.LIKES);
		if (likes == null)
			return false;

		return likes.contains(ParseUser.getCurrentUser().getObjectId());
	}

	public void setData(List<ParseObject> posts) {
		this.data.clear();
		if (posts == null)
			return;
		this.data.addAll(posts);
		notifyDataSetChanged();
	}

	public Date getLastItemDate() {
		if (data.size() == 0)
			return null;
		return data.get(data.size() - 1).getCreatedAt();
	}

	public void addItems(List<ParseObject> posts, boolean atStart) {

		if (posts == null)
			return;

		if (atStart)
			this.data.addAll(0, posts);
		else
			this.data.addAll(posts);

		notifyDataSetChanged();
	}

	public void addItem(ParseObject post) {
		if (post == null)
			return;
		this.data.add(0, post);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	public static class ViewHolder {

		public TextView headingOne;
		public TextView dateView;
		public ImageView ribbonView;
		public TextView statusView;
		public ImageView picView;
		public TextView commentCountView;
		public ImageView commentIconView;
		public ImageView loveIconView;
		public TextView loveCountView;
		public ImageView optionView;
	}
		 //need to check
//	@Override
//	public void onDelete(int position, ParseObject post) {
//		Log.e("Deleting", position + "--");
//		if (position < data.size()) {
//			PostManager.getInstance().deletePost(data.get(position));
//			data.remove(position);
//			((PopupOptionHandler) context).onDelete(position, post);
//			notifyDataSetChanged();
//			Log.e("Deleted", position + "--");
//		}
//	}
		 //need to check
//	@Override
//	public void onEditClicked(int position, ParseObject post) {
//		((PopupOptionHandler) context).onEditClicked(position, post);
//	}

	public void updateItem(int position, ParseObject item) {
		if (position >= 0 && position < data.size()) {
			data.set(position, item);
			notifyDataSetChanged();
		}
	}

		 //need to check
//	@Override
//	public void onFlagClicked(int position, ParseObject post) {
//		((PopupOptionHandler) context).onFlagClicked(position, post);
//	}
}
