package com.bigc.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.ParseObject;
import com.parse.ParseUser;

//public class StoriesAdapter extends ArrayAdapter<ParseObject> implements
//		PopupOptionHandler //// TODO: 14-07-2017
public class StoriesAdapter extends ArrayAdapter<ParseObject>
{

	private LayoutInflater inflater;
	private List<ParseObject> data;
	private BaseFragment context;

	public StoriesAdapter(BaseFragment context, List<ParseObject> stories) {
		super(context.getActivity(), R.layout.list_item_stories);

		inflater = (LayoutInflater) context.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;

		data = new ArrayList<ParseObject>();
		if (stories != null)
			data.addAll(stories);

	}

	@Override
	public ParseObject getItem(int position) {
		if (position < 0 || position >= data.size())
			return null;
		return data.get(position);
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ParseObject story = data.get(position);
		final ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item_stories, parent, false);
			holder = new ViewHolder();
			holder.profileView = (ImageView) view
					.findViewById(R.id.profilePictureView);
			holder.iconView = (ImageView) view.findViewById(R.id.iconView);
			holder.nameView = (TextView) view.findViewById(R.id.nameView);
			holder.descView = (TextView) view
					.findViewById(R.id.descriptionView);
			holder.dateView = (TextView) view.findViewById(R.id.dateView);
			holder.titleView = (TextView) view.findViewById(R.id.titleView);
			holder.optionView = (ImageView) view.findViewById(R.id.optionView);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ParseUser owner = story.getParseUser(DbConstants.USER);
		holder.nameView.setText(owner.getString(DbConstants.NAME));
		holder.titleView
				.setText(story.getString(DbConstants.TITLE) == null ? ""
						: story.getString(DbConstants.TITLE));
		// Setting icon
		if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
				.ordinal()) {
			holder.iconView.setImageResource(R.drawable.ribbon_supporter);
		} else if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
				.ordinal()) {
			holder.iconView
					.setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
							: Utils.fighter_ribbons[owner
									.getInt(DbConstants.RIBBON)]);
		} else {
			holder.iconView
					.setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
							: Utils.survivor_ribbons[owner
									.getInt(DbConstants.RIBBON)]);
		}

		if (owner.getParseFile(DbConstants.PROFILE_PICTURE) != null)
			ImageLoader.getInstance().displayImage(
					owner.getParseFile(DbConstants.PROFILE_PICTURE).getUrl(),
					holder.profileView, Utils.normalDisplayOptions,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String uri, View imageView) {
							holder.profileView
									.setImageResource(R.drawable.default_profile_pic);
						}
					});
		else
			holder.profileView.setImageResource(R.drawable.default_profile_pic);

		holder.optionView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//// TODO: 14-07-2017
//				Utils.showQuickActionMenu(StoriesAdapter.this, context
//						.getActivity(), position, null, v,
//						(story.getParseUser(DbConstants.USER).getObjectId()
//								.equals(ParseUser.getCurrentUser()
//										.getObjectId())),
//						DbConstants.Flags.Story);
			}
		});

		holder.dateView.setText(Utils.getTimeStringForFeed(
				context.getActivity(), story.getCreatedAt()));
		holder.descView.setText(story.getString(DbConstants.MESSAGE));

		return view;
	}

	public void setData(List<ParseObject> stories) {
		this.data.clear();
		if (stories == null)
			return;
		this.data.addAll(stories);
		notifyDataSetChanged();
	}

	public Date getLastItemDate() {
		if (data.size() == 0)
			return null;
		return data.get(data.size() - 1).getCreatedAt();
	}

	public void addItems(List<ParseObject> stories, boolean atStart) {

		if (stories == null)
			return;

		if (atStart)
			this.data.addAll(0, stories);
		else
			this.data.addAll(stories);

		notifyDataSetChanged();
	}

	public void addItem(ParseObject story) {
		if (story == null)
			return;
		this.data.add(0, story);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	public static class ViewHolder {

		public ImageView profileView;
		public ImageView iconView;
		public TextView nameView;
		public TextView dateView;
		public TextView descView;
		public TextView titleView;
		public ImageView optionView;
	}
	//// TODO: 14-07-2017
//
//	@Override
//	public void onDelete(int position, ParseObject tribute) {
//		Log.e("Deleting", position + "--");
//		if (position < data.size()) {
//			PostManager.getInstance().deletePost(data.get(position));
//			data.remove(position);
//			((PopupOptionHandler) context).onDelete(position, tribute);
//			notifyDataSetChanged();
//			Log.e("Deleted", position + "--");
//		}
//	}
//
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
//// TODO: 14-07-2017
//	@Override
//	public void onFlagClicked(int position, ParseObject post) {
//		((PopupOptionHandler) context).onFlagClicked(position, post);
//	}
}
