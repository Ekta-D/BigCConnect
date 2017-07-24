package com.bigc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.Utils;
import com.bigc.models.Messages;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.janmuller.android.simplecropimage.Util;

public class MessagesAdapter extends ArrayAdapter<Messages> {

    private List<Messages> messages;
    private LayoutInflater inflater;
    private Users currentUser;
    private Context context;
    List<Users> userList = new ArrayList<>();

    public MessagesAdapter(Context context, List<Messages> messages, List<Users> userList) {
        super(context, R.layout.listitem_message);
        this.messages = new ArrayList<>();
        if (messages != null)
            this.messages.addAll(messages);
        this.userList = userList;
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        currentUser = Preferences.getInstance(context).getUserFromPreference();

    }

    @Override
    public Messages getItem(int position) {
        return messages.get(position);
    }

    public void setData(List<Messages> messages) {
        this.messages.clear();
        if (messages == null)
            return;
        this.messages.addAll(messages);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return messages.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listitem_message, parent, false);
            holder = new ViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.nameView);
            holder.messageView = (TextView) view
                    .findViewById(R.id.descriptionView);
            holder.ribbonView = (ImageView) view.findViewById(R.id.ribbonView);
            holder.dateView = (TextView) view.findViewById(R.id.dateView);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Messages message = messages.get(position);
        String user2 = message.getUser2();

        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getObjectId().equalsIgnoreCase(user2)) {
                Users selected_user = userList.get(i);
                setValues(selected_user, message, holder);
            }
        }
      /*  String owner = message.getUser1().equals(currentUser.getObjectId()) ? message.getUser2() : message.getUser1();

        for (Users user : userList) {
            if (user.getObjectId().equalsIgnoreCase(owner)) {
                setValues(user, message, holder);
            }
        }*/
    /*	ParseObject message = messages.get(position);



		ParseUser owner = message.getParseUser(DbConstants.USER1).getObjectId()
				.equals(currentUser.getObjectId()) ? message
				.getParseUser(DbConstants.USER2) : message
				.getParseUser(DbConstants.USER1);

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

		holder.nameView.setText(owner.getString(DbConstants.NAME));
		holder.dateView.setText(Utils.getTimeStringForFeed(context,
				message.getUpdatedAt()));
		holder.messageView.setText(message.getString(DbConstants.MESSAGE));*/
        return view;
    }

    private void setValues(Users users, Messages messages, ViewHolder holder) {
        if (users.getType() == Constants.IS_SUPPORTER) {
            holder.ribbonView.setImageResource(R.drawable.ribbon_supporter);
        } else if (users.getType() == Constants.IS_FIGHTER) {
            holder.ribbonView
                    .setImageResource(users.getRibbon() < 0 ? R.drawable.ic_launcher
                            : Utils.fighter_ribbons[users.getRibbon()]);
        } else {
            holder.ribbonView
                    .setImageResource(users.getRibbon() < 0 ? R.drawable.ic_launcher
                            : Utils.survivor_ribbons[users.getRibbon()]);
        }
        holder.nameView.setText(users.getName());
        holder.dateView.setText(Utils.getTimeStringForFeed(context,
                Utils.convertStringToDate(messages.getCreatedAt())));
        holder.messageView.setText(messages.getMessage());
    }

    public Date getLastItemDate() {
        if (messages.size() == 0)
            return null;
        return new Date();// messages.get(messages.size() - 1).getCreatedAt();
    }

	/*public List<ParseObject> getData() {
        return this.messages;
	}

	public void addItems(List<ParseObject> messages, boolean atStart) {

		if (messages == null)
			return;

		if (atStart)
			this.messages.addAll(0, messages);
		else
			this.messages.addAll(messages);

		notifyDataSetChanged();
	}

	public void addItem(ParseObject message) {
		if (message == null)
			return;
		this.messages.add(0, message);
		notifyDataSetChanged();
	}*/

    private static class ViewHolder {
        public TextView nameView;
        public TextView dateView;
        public TextView messageView;
        public ImageView ribbonView;
    }

}
