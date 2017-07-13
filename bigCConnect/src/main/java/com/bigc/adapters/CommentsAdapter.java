package com.bigc.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.Utils;
import com.bigc_connect.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class CommentsAdapter extends ArrayAdapter<ParseObject> {

	private List<ParseObject> comments;
	private LayoutInflater inflater;
	private Context context;

	public CommentsAdapter(Context context) {
		super(context, R.layout.list_item_comment);
		this.comments = new ArrayList<ParseObject>();
		this.context = context;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public void setData(List<ParseObject> comments) {
		this.comments.clear();
		if (comments == null)
			return;
		this.comments.addAll(comments);
		notifyDataSetChanged();
	}

	public void addItem(ParseObject comment) {
		this.comments.add(0, comment);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item_comment, parent, false);
			holder = new ViewHolder();
			holder.nameView = (TextView) view.findViewById(R.id.nameView);
			holder.ribbonView = (ImageView) view.findViewById(R.id.ribbonView);
			holder.commentView = (TextView) view.findViewById(R.id.commentView);
			holder.dateView = (TextView) view.findViewById(R.id.dateView);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ParseObject comment = comments.get(position);
		ParseUser owner = comment.getParseUser(DbConstants.USER);

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
				comment.getCreatedAt()));
		holder.nameView.setText(owner.getString(DbConstants.NAME));
		holder.commentView.setText(comment.getString(DbConstants.MESSAGE));
		return view;
	}

	private static class ViewHolder {
		public TextView nameView;
		public TextView dateView;
		public TextView commentView;
		public ImageView ribbonView;
	}
}
