package com.bigc.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.activities.LoginActivity;
import com.bigc.adapters.SearchResultPictureAdapter.SurvivorSearchViewHolder;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.SearchResultBaseAdapter;
import com.bigc.models.ConnectionsModel;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.janmuller.android.simplecropimage.Util;

public class SearchResultAdapter extends SearchResultBaseAdapter {

    public SearchResultAdapter(Context context,
                               List<Users> activeConnection, List<Users> pendingConnection) {
        super(context, R.layout.listitem_search_result, activeConnection,
                pendingConnection, null);
        // init(context, activeConnection, pendingConnections, null);
    }

    public SearchResultAdapter(Context context,
                               List<Users> activeConnections,
                               List<Users> pendingConnections, List<Users> data) {
        super(context, R.layout.listitem_search_result, activeConnections,
                pendingConnections, data);
        // init(context, userConnections, pendingConnections, data);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final SurvivorSearchViewHolder holder;
        final Users user = data.get(position);

            if (view == null) {
                view = inflater.inflate(R.layout.listitem_search_result, parent,
                        false);
                holder = new SurvivorSearchViewHolder();
                holder.addOption = (ImageView) view.findViewById(R.id.addOption);
                holder.ribbonView = (ImageView) view.findViewById(R.id.ribbonView);
                holder.nameView = (TextView) view.findViewById(R.id.nameView);
                holder.descView = (TextView) view
                        .findViewById(R.id.descriptionView);
                view.setTag(holder);
            } else {
                holder = (SurvivorSearchViewHolder) view.getTag();
            }

            holder.indexInNewAddedConnections = Utils.getUserIndex(user,
                    newAddedConnections);
            holder.indexInActiveConnections = Utils.getUserIndex(user,
                    activeConnections);
            holder.indexInPendingConnections = Utils.getUserIndex(user,
                    pendingConnections);
            holder.indexInRemovedConnections = Utils.getUserIndex(user,
                    removedConnections);

            holder.nameView.setText(user.getName());
            String stage = user.getStage();
            stage = stage == null ? "" : stage;
            String loc = user.getLocation();

            String desc = (stage.length() > 0 ? stage.concat(", ") : "")
                    .concat(loc == null ? "" : loc);
            holder.descView.setText(desc);
//		boolean supporter = user.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//				.ordinal();
            boolean supporter = Preferences.getInstance(getContext()).getInt(DbConstants.TYPE) == 1; // supporter

            if (supporter)
                holder.ribbonView.setImageResource(R.drawable.ribbon_supporter);
            else {
//			if (user.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//					.ordinal())
                if (Preferences.getInstance(getContext()).getInt(DbConstants.TYPE) == 2) {
//				holder.ribbonView
//						.setImageResource(user.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
//								: Utils.fighter_ribbons[user
//										.getInt(DbConstants.RIBBON)]);
                    holder.ribbonView.setImageResource(
                            Preferences.getInstance(getContext()).getInt(DbConstants.RIBBON) < 0 ?
                                    R.drawable.ic_launcher : Utils.fighter_ribbons[user
                                    .getRibbon()]);

                } else {
//                holder.ribbonView
//                        .setImageResource(user.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
//                                : Utils.survivor_ribbons[user
//                                .getInt(DbConstants.RIBBON)]);
                    holder.ribbonView
                            .setImageResource(Preferences.getInstance(getContext()).getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
                                    : Utils.survivor_ribbons[user
                                    .getRibbon()]);
                }

                // int ribbon = user.getInt(DbConstants.RIBBON);
                // if (ribbon >= 0)
                // holder.ribbonView
                // .setImageResource(Utils.survivor_ribbons[ribbon]);
                // else
                // holder.ribbonView.setImageResource(R.drawable.ic_launcher);
            }

            if ((isSupporterUser && supporter)
                    || FirebaseAuth.getInstance().getCurrentUser().getUid()
                    .equals(user.getObjectId())) {
                holder.addOption.setVisibility(View.GONE);
            } else {
                holder.addOption.setVisibility(View.VISIBLE);
                if (holder.indexInNewAddedConnections >= 0) {
                    holder.addOption
                            .setImageResource(R.drawable.ic_connect_pending);
                    holder.addOption.setContentDescription(TAG_WAITING);

                } else if (holder.indexInActiveConnections >= 0
                        && holder.indexInRemovedConnections < 0) {
                    holder.addOption.setImageResource(R.drawable.ic_connected);
                    holder.addOption.setContentDescription(TAG_CONNECTED);

                } else if (holder.indexInPendingConnections >= 0
                        && holder.indexInRemovedConnections < 0) {
                    holder.addOption
                            .setImageResource(R.drawable.ic_connect_pending);
                    holder.addOption.setContentDescription(TAG_WAITING);

                } else {
                    holder.addOption.setImageResource(R.drawable.ic_connect);
                    holder.addOption.setContentDescription(TAG_NOT_CONNECTED);
                }

                holder.addOption.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (v.getContentDescription().toString()
                                .equals(TAG_NOT_CONNECTED)) {
                            // TODO: 7/17/2017 Send add connection request
                            sendAddConnectionRequest(user, FirebaseAuth.getInstance().getCurrentUser().getUid());

                            if (holder.indexInRemovedConnections < 0) {
                                newAddedConnections.add(user);
                                holder.indexInNewAddedConnections = newAddedConnections
                                        .size() - 1;
                            } else {
                                removedConnections
                                        .remove(holder.indexInRemovedConnections);
                                holder.indexInRemovedConnections = -1;
                            }

                            holder.addOption.setContentDescription(TAG_WAITING);
                            holder.addOption
                                    .setImageResource(R.drawable.ic_connect_pending);
                        } else {
                            if (holder.indexInNewAddedConnections < 0) {
                                removedConnections.add(data.get(position));
                                holder.indexInRemovedConnections = removedConnections
                                        .size() - 1;
                            } else {
                                newAddedConnections
                                        .remove(holder.indexInNewAddedConnections);
                                holder.indexInNewAddedConnections = -1;
                            }

                            holder.addOption
                                    .setContentDescription(TAG_NOT_CONNECTED);
                            holder.addOption
                                    .setImageResource(R.drawable.ic_connect);
                        }
                    }
                });
            }
        return view;
    }

    private void sendAddConnectionRequest(Users user, String uid) {
        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT);
        final String date = format.format(new Date(System.currentTimeMillis()));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String objectID= ref.child(DbConstants.TABLE_CONNECTIONS).push().getKey();
        ConnectionsModel connection = new ConnectionsModel(date, uid, objectID, false, user.getObjectId(), date);
        ref.child(DbConstants.TABLE_CONNECTIONS).child(objectID).setValue(connection);
    }


}
