package com.bigc.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.activities.LoginActivity;
import com.bigc.adapters.SearchResultPictureAdapter.SurvivorSearchViewHolder;
import com.bigc.datastorage.Preferences;
import com.bigc.fragments.SupportersFragment;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.SearchResultBaseAdapter;
import com.bigc.models.ConnectionsModel;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        if (activeConnections.size() > 0) {
            holder.indexInActiveConnections = Utils.getUserIndex(user,
                    activeConnections);
        } else {
            holder.indexInActiveConnections = Utils.getUserIndex(user, data);
        }
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
        boolean supporter = user.getType() == 1; // supporter

        if (supporter)
            holder.ribbonView.setImageResource(R.drawable.ribbon_supporter);
        else {
//			if (user.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//					.ordinal())
            if (user.getRibbon() == Constants.USER_TYPE.FIGHTER.ordinal()) {
//				holder.ribbonView
//						.setImageResource(user.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
//								: Utils.fighter_ribbons[user
//										.getInt(DbConstants.RIBBON)]);
                holder.ribbonView.setImageResource(
                        Preferences.getInstance(getContext()).getInt(DbConstants.RIBBON) < 0 ?
                                R.drawable.ribbon_supporter : Utils.fighter_ribbons[user
                                .getRibbon()]);

            } else {
//                holder.ribbonView
//                        .setImageResource(user.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
//                                : Utils.survivor_ribbons[user
//                                .getInt(DbConstants.RIBBON)]);
                holder.ribbonView
                        .setImageResource(user.getRibbon() < 0 ? R.drawable.ribbon_supporter
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


        if (/*(isSupporterUser && supporter)
                    ||  now commented by Ekta   FirebaseAuth.getInstance().getCurrentUser().getUid()*/
                Preferences.getInstance(getContext()).getString(DbConstants.ID)
                        .equals(user.getObjectId())) {
            holder.addOption.setVisibility(View.GONE);
        } else {
            holder.addOption.setVisibility(View.VISIBLE);
                /*ArrayList<Users> active = Preferences.getInstance(getContext()).getLocalConnections().get(0);
                ArrayList<Users> pending = Preferences.getInstance(getContext()).getLocalConnections().get(0);
                for(Users auser: active)
                    System.out.println("active: "+auser.getName());
                for(Users auser: pending)
                    System.out.println("pending: "+auser.getName());
                if(Preferences.getInstance(getContext()).getLocalConnections().get(0).contains(user)){
                    holder.addOption.setImageResource(R.drawable.ic_connected);
                    holder.addOption.setContentDescription(TAG_CONNECTED);
                } else if (Preferences.getInstance(getContext()).getLocalConnections().get(1).contains(user)) {
                    holder.addOption.setImageResource(R.drawable.ic_connect_pending);
                    holder.addOption.setContentDescription(TAG_WAITING);
                } else {
                    holder.addOption.setImageResource(R.drawable.ic_connect);
                    holder.addOption.setContentDescription(TAG_NOT_CONNECTED);
                }*/
            if (holder.indexInNewAddedConnections >= 0) {
                holder.addOption.setImageResource(R.drawable.ic_connect_pending);
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
                        // Completed: 7/17/2017 Send add connection request
                        checkAddConnectionRequest(user, Preferences.getInstance(getContext().getApplicationContext()).getString(DbConstants.ID));

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
                    } else if (v.getContentDescription().toString()
                            .equals(TAG_WAITING)) {
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
                    } else if (v.getContentDescription().toString()
                            .equals(TAG_CONNECTED)) {

                        // TODO: 7/24/2017 remove connection
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

    private void checkAddConnectionRequest(final Users user, final String currentUid) {
        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT);
        String date = format.format(new Date(System.currentTimeMillis()));

        final String objectID = currentUid + "_" + user.getObjectId();
        final ConnectionsModel connection = new ConnectionsModel(date, currentUid, objectID, false, user.getObjectId(), date);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        // TODO: 7/19/2017 Check if there's a pending connection
        ref.child(DbConstants.TABLE_CONNECTIONS).orderByKey().equalTo(objectID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    /*boolean sendRequest = true;
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        ConnectionsModel connectionsModel = dataSnapshot1.getValue(ConnectionsModel.class);
                        if(connectionsModel.getFrom().equalsIgnoreCase(user.getObjectId()) || connectionsModel.getTo().equalsIgnoreCase(user.getObjectId())){
                            if(connectionsModel.getStatus() == true){
                                //exists and is a connection.. dont send connection request

                            } else {
                                //exists and pending.. no need to send connection request
                            }
                            sendRequest = false;
                            break;
                        }
                    }*/
                    /*if(sendRequest){
                        //send add connection request
                        checkReverse(ref, user.getObjectId()+"_"+currentUid, connection, user);
                        //sendConnectionRequest(ref, objectID, connection);
                    }*/
                } else {
                    //send add connection request
                    checkReverse(ref, currentUid, user.getObjectId(), connection, user);
                    //sendConnectionRequest(ref, objectID, connection);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



       /* DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String objectID= ref.child(DbConstants.TABLE_CONNECTIONS).push().getKey();
        ConnectionsModel connection = new ConnectionsModel(date, uid, objectID, false, user.getObjectId(), date);
        ref.child(DbConstants.TABLE_CONNECTIONS).child(objectID).setValue(connection);*/

    }

    private void checkReverse(final DatabaseReference ref, final String currentUid, final String userId, final ConnectionsModel connection, final Users user) {
        ref.child(DbConstants.TABLE_CONNECTIONS).orderByKey().equalTo(userId + "_" + currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    /*boolean sendRequest = true;
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        ConnectionsModel connectionsModel = dataSnapshot1.getValue(ConnectionsModel.class);
                        if(connectionsModel.getFrom().equalsIgnoreCase(user.getObjectId()) || connectionsModel.getTo().equalsIgnoreCase(user.getObjectId())){
                            if(connectionsModel.getStatus() == true){
                                //exists and is a connection.. dont send connection request

                            } else {
                                //exists and pending.. no need to send connection request
                            }
                            sendRequest = false;
                            break;
                        }
                    }
                    if(sendRequest){
                        //send add connection request
                        sendConnectionRequest(ref, objectID, connection);
                    }*/
                } else {
                    //send add connection request
                    sendConnectionRequest(ref, currentUid + "_" + userId, connection, user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendConnectionRequest(DatabaseReference ref, String objectID, ConnectionsModel connection, final Users user) {
        // TODO: 7/19/2017 send add connection request
        ref.child(DbConstants.TABLE_CONNECTIONS).child(objectID).setValue(connection).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //show success
                ArrayList sendToken = new ArrayList();
                sendToken.add(user.getToken());
                Utils.sendNotification(sendToken, Constants.ACTION_FRIEND_REQUEST, "Connection Request", Preferences.getInstance(getContext()).getString(DbConstants.NAME) + " has sent you a connection request");
            }
        });
    }


}
