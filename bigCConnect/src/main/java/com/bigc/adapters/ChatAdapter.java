package com.bigc.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.MyLeadingMarginSpan2;
import com.bigc.general.classes.Utils;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ChatAdapter extends ArrayAdapter<Users> {
	// TODO: 7/18/2017 fix this class had parse object previously
	//private List<ParseObject> messages;
	private LayoutInflater inflater;
	private MyLeadingMarginSpan2 span;
	private Activity context;

	public ChatAdapter(Activity context) {
		super(context, R.layout.listitem_chat);
		//this.messages = new ArrayList<ParseObject>();

		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		span = new MyLeadingMarginSpan2(2, (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources()
						.getDisplayMetrics()));
	}

	/*public void setData(List<ParseObject> messages) {
		this.messages.clear();
		if (messages == null)
			return;
		this.messages.addAll(messages);
		notifyDataSetChanged();
	}

	public void addItem(ParseObject message) {
		this.messages.add(message);
		notifyDataSetChanged();
	}*/

	@Override
	public int getCount() {

		return 0;//messages.size();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		/*if (view == null) {
			view = inflater.inflate(R.layout.listitem_chat, parent, false);
			holder = new ViewHolder();
			holder.nameView = (TextView) view.findViewById(R.id.nameView);
			holder.ribbonView = (ImageView) view.findViewById(R.id.ribbonView);
			holder.messageView = (TextView) view.findViewById(R.id.messageView);
			holder.dateView = (TextView) view.findViewById(R.id.dateView);
			holder.picView = (ImageView) view.findViewById(R.id.pictureView);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		final ParseObject message = messages.get(position);
		ParseUser owner = message.getParseUser(DbConstants.SENDER);

		if (message.getParseFile(DbConstants.MEDIA) != null) {
			holder.picView.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					message.getParseFile(DbConstants.MEDIA).getUrl(),
					holder.picView, Utils.normalDisplayOptions,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String uri, View view) {
							((ImageView) view)
									.setImageResource(R.drawable.loading_img);
						}
					});
		} else {
			holder.picView.setVisibility(View.GONE);
		}
		
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

		holder.dateView.setText(Utils.getTimeStringForFeed(context,
				message.getCreatedAt()));
		holder.nameView.setText(owner.getString(DbConstants.NAME));
		SpannableString ss = new SpannableString(
				message.getString(DbConstants.MESSAGE));
		ss.setSpan(span, 0, ss.length(), 0);

		holder.messageView.setText(ss);
		holder.picView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.openImageZoomView(context,
						message.getParseFile(DbConstants.MEDIA).getUrl());
			}
		});*/
		return view;
	}

	private static class ViewHolder {
		public TextView nameView;
		public TextView dateView;
		public ImageView picView;
		public TextView messageView;
		public ImageView ribbonView;
	}
}
