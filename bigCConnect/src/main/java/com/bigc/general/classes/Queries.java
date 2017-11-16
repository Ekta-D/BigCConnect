package com.bigc.general.classes;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.bigc.datastorage.Preferences;
import com.bigc.interfaces.ConnectionExist;
import com.bigc.interfaces.GetConnectionCompletion;
import com.bigc.interfaces.SignupInterface;
import com.bigc.models.ConnectionsModel;
import com.bigc.models.Posts;
import com.bigc.models.Stories;
import com.bigc.models.Tributes;
import com.bigc.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.janmuller.android.simplecropimage.Util;

public class Queries {

//    public static ParseQuery<ParseObject> getUserConnectionStatusQuery(
//            ParseUser user) {
//        List<ParseUser> users = new ArrayList<ParseUser>();
//        users.add(ParseUser.getCurrentUser());
//        users.add(user);
//        ParseQuery<ParseObject> mQuery = ParseQuery
//                .getQuery(DbConstants.TABLE_CONNECTIONS);
//        mQuery.whereContainedIn(DbConstants.TO, users);
//        mQuery.whereContainedIn(DbConstants.FROM, users);
//
//        mQuery.include("User");
//        return mQuery;
//    }


    public static Query getAllUsers() {
        Query query = FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS);
        return query;
    }

    public static Query getStoriesQuery(boolean fromCache) {
        Query query = FirebaseDatabase.getInstance().getReference().
                child(DbConstants.TABLE_STORIES).limitToFirst(30).orderByChild(DbConstants.UPDATED_AT);
        return query;
    }

    public static Query getStoryCommentQuery(Stories story) {
        Query query = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_STORY_COMMENT).
                orderByChild(DbConstants.POST).equalTo(story.getObjectId());
        return query;
    }

/*
public static ParseQuery<ParseObject> getUserConnectionStatusQuery(
        ParseUser user) {
    List<ParseUser> users = new ArrayList<ParseUser>();
    users.add(ParseUser.getCurrentUser());
    users.add(user);
    ParseQuery<ParseObject> mQuery = ParseQuery
            .getQuery(DbConstants.TABLE_CONNECTIONS);
    mQuery.whereContainedIn(DbConstants.TO, users);
    mQuery.whereContainedIn(DbConstants.FROM, users);

    mQuery.include("User");
    return mQuery;
}
*/

    public static ArrayList<Users> getSearchSurvivorQuery(String keyword) {
        final ArrayList<Users> searchUsers = null;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(DbConstants.USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        searchUsers.add(data.getValue(Users.class));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*ParseQuery<ParseUser> query1 = ParseUser.getQuery();
        query1.whereContains(DbConstants.NAME, keyword);
        query1.whereNotEqualTo(DbConstants.TYPE,
                Constants.USER_TYPE.SUPPORTER.ordinal());
        query1.whereNotEqualTo(DbConstants.ID, ParseUser.getCurrentUser()
                .getObjectId());
        query1.whereNotEqualTo(DbConstants.VISIBILITY, Constants.PRIVATE);
        query1.whereNotEqualTo(DbConstants.DEACTIVATED, true);

        ParseQuery<ParseUser> query2 = ParseUser.getQuery();
        query2.whereContains(DbConstants.NAME_LOWERCASE, keyword.toLowerCase());
        // query2.whereEqualTo(DbConstants.TYPE,
        // Constants.USER_TYPE.SURVIVOR.ordinal());
        query1.whereNotEqualTo(DbConstants.TYPE,
                Constants.USER_TYPE.SUPPORTER.ordinal());
        query2.whereNotEqualTo(DbConstants.ID, ParseUser.getCurrentUser()
                .getObjectId());
        query2.whereNotEqualTo(DbConstants.VISIBILITY, Constants.PRIVATE);
        query2.whereNotEqualTo(DbConstants.DEACTIVATED, true);

        List<ParseQuery<ParseUser>> queries = new ArrayList<ParseQuery<ParseUser>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseUser> mQuery = ParseQuery.or(queries);

        return mQuery;*/
        return searchUsers;
    }

//    public static Query getSearchSurvivorQuery1(String keyword) {
//        List<Query> queries = new ArrayList<>();
//        Query query1, query2 = null;
//        query1 = FirebaseDatabase.getInstance().getReference().child("users").equalTo(keyword).orderByChild(DbConstants.TYPE)
//.equalTo(DbConstants.DEACTIVATED=false)
//
//    }

    /*public static ParseQuery<ParseObject> getPendingRequestsQuery() {
        ParseQuery<ParseObject> query = ParseQuery
                .getQuery(DbConstants.TABLE_CONNECTIONS);
        query.whereEqualTo(DbConstants.TO, ParseUser.getCurrentUser());
        query.whereEqualTo(DbConstants.STATUS, false);

        return query;
    }*/
    public static Query getCategorizedUsersQuery(int ribbon) {
        final ArrayList<Users> categoryUsers = null;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child(DbConstants.USERS).orderByChild(DbConstants.RIBBON).equalTo(ribbon);
/*
        mDatabase.child(DbConstants.USERS).startAt(DbConstants.RIBBON, String.valueOf(ribbon)).orderByChild(DbConstants.CREATED_AT).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                categoryUsers.add(data.getValue(Users.class));
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
*/

       /* ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo(DbConstants.TYPE,
                Constants.USER_TYPE.SUPPORTER.ordinal());
        query.whereEqualTo(DbConstants.RIBBON, ribbon);
        query.whereNotEqualTo(DbConstants.VISIBILITY, Constants.PRIVATE);
        query.orderByDescending(DbConstants.CREATED_AT);
        query.whereNotEqualTo(DbConstants.DEACTIVATED, true);*/

        return query;
    }
//    public static ParseQuery<ParseUser> getCategorizedUsersQuery(int ribbon) {
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.whereNotEqualTo(DbConstants.TYPE,
//                Constants.USER_TYPE.SUPPORTER.ordinal());
//        query.whereEqualTo(DbConstants.RIBBON, ribbon);
//        query.whereNotEqualTo(DbConstants.VISIBILITY, Constants.PRIVATE);
//        query.orderByDescending(DbConstants.CREATED_AT);
//        query.whereNotEqualTo(DbConstants.DEACTIVATED, true);
//
//        return query;
//    }

   /* public static ParseQuery<ParseObject> getUserFeedsQuery(ParseUser user) {
        ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_POST);
        mQuery.whereEqualTo(DbConstants.USER, user);
        mQuery.addDescendingOrder(DbConstants.CREATED_AT);
        mQuery.include("User");

        return mQuery;
    }

    public static ParseQuery<ParseObject> getUserPictureFeedsQuery(
            ParseUser user) {
        ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_POST);
        mQuery.whereEqualTo(DbConstants.USER, user);
        mQuery.whereExists(DbConstants.MEDIA);
        mQuery.addDescendingOrder(DbConstants.CREATED_AT);
        mQuery.include("User");

        return mQuery;
    }

    public static ParseQuery<ParseObject> getStoriesQuery(boolean fromCache) {
        ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_STORIES);
        ParseQuery<ParseUser> blockedusersQuery = ParseUser.getQuery();
        blockedusersQuery.whereEqualTo(DbConstants.DEACTIVATED, true);
        mQuery.whereDoesNotMatchQuery(DbConstants.USER, blockedusersQuery);
        mQuery.addDescendingOrder(DbConstants.CREATED_AT);

        if (fromCache)
            mQuery.fromLocalDatastore();
        else
            mQuery.setLimit(30);

        return mQuery;
    }

    public static ParseQuery<ParseObject> getGroupMessagesQuery(ParseUser user,
                                                                boolean fromCache) {
        ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_MESSAGE);

        ParseQuery<ParseObject> sentMessagesQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_MESSAGE);
        sentMessagesQuery.whereEqualTo(DbConstants.USER1,
                ParseUser.getCurrentUser());
        sentMessagesQuery.whereEqualTo(DbConstants.USER2, user);

        ParseQuery<ParseObject> receivedMessagesQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_MESSAGE);
        receivedMessagesQuery.whereEqualTo(DbConstants.USER1, user);
        receivedMessagesQuery.whereEqualTo(DbConstants.USER2,
                ParseUser.getCurrentUser());

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(receivedMessagesQuery);
        queries.add(sentMessagesQuery);

        mQuery = ParseQuery.or(queries);
        mQuery.setLimit(1000);

        mQuery.orderByAscending(DbConstants.CREATED_AT);
        return mQuery;
    }*/

//    public static Query getGroupMessagesQuery(boolean fromCache) {
//        String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Query sentMessage = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_MESSAGE);
//        sentMessage.orderByChild(DbConstants.USER1).equalTo(current_user);
//        return sentMessage
//
//    }

//    public static List<Query> getConversationsQuery(
//            boolean fromCache) {
//
//        Query mQuery = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_CONVERSATION);
//        Query sentMessagesQuery = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_CONVERSATION).
//                orderByChild(DbConstants.USER1).equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        Query receivedMessagesQuery = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_CONVERSATION).
//                orderByChild(DbConstants.USER2).equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        List<Query> conversationQueries = new ArrayList<>();
//        conversationQueries.add(mQuery);
//        conversationQueries.add(sentMessagesQuery);
//        conversationQueries.add(receivedMessagesQuery);
//
//        /*ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
//                DbConstants.TABLE_CONVERSATION);
//
//        ParseQuery<ParseObject> sentMessagesQuery = new ParseQuery<ParseObject>(
//                DbConstants.TABLE_CONVERSATION);
//        sentMessagesQuery.whereEqualTo(DbConstants.USER1,
//                ParseUser.getCurrentUser());
//
//        ParseQuery<ParseObject> recievedMessagesQuery = new ParseQuery<ParseObject>(
//                DbConstants.TABLE_CONVERSATION);
//        recievedMessagesQuery.whereEqualTo(DbConstants.USER2,
//                ParseUser.getCurrentUser());
//
//        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
//        queries.add(recievedMessagesQuery);
//        queries.add(sentMessagesQuery);
//
//        mQuery = ParseQuery.or(queries);
//
//        mQuery.orderByDescending(DbConstants.UPDATED_AT);
//
//        mQuery.include("User");
//        mQuery.setLimit(1000);
//
//        return mQuery;*/
//        return conversationQueries;
//    }

   /* public static ParseQuery<ParseObject> getTributesQuery() {
        ParseQuery<ParseObject> mQuery = ParseQuery
                .getQuery(DbConstants.TABLE_TRIBUTE);
        mQuery.setLimit(30);
        mQuery.addDescendingOrder(DbConstants.CREATED_AT);
        mQuery.include("User");

        return mQuery;
    }

    public static ParseQuery<ParseObject> getFeedsQuery(boolean fromCache) {
        ParseQuery<ParseObject> mQuery;
        if (fromCache) {

            mQuery = new ParseQuery<ParseObject>(DbConstants.TABLE_POST);
            mQuery.fromLocalDatastore();
        } else {

            ParseQuery<ParseUser> blockedusersQuery = ParseUser.getQuery();
            blockedusersQuery.whereEqualTo(DbConstants.DEACTIVATED, true);

            // My Connections
            ParseQuery<ParseObject> fromQuery = ParseQuery
                    .getQuery(DbConstants.TABLE_CONNECTIONS);
            fromQuery
                    .whereEqualTo(DbConstants.FROM, ParseUser.getCurrentUser());
            fromQuery.whereEqualTo(DbConstants.STATUS, true);
            fromQuery.whereDoesNotMatchQuery(DbConstants.TO, blockedusersQuery);

            ParseQuery<ParseObject> toQuery = ParseQuery
                    .getQuery(DbConstants.TABLE_CONNECTIONS);
            toQuery.whereEqualTo(DbConstants.TO, ParseUser.getCurrentUser());
            toQuery.whereEqualTo(DbConstants.STATUS, true);
            toQuery.whereDoesNotMatchQuery(DbConstants.FROM, blockedusersQuery);

            ParseQuery<ParseObject> myPostsQuery = new ParseQuery<ParseObject>(
                    DbConstants.TABLE_POST);
            myPostsQuery.whereEqualTo(DbConstants.USER,
                    ParseUser.getCurrentUser());

            ParseQuery<ParseObject> sharedWithMeQuery = new ParseQuery<ParseObject>(
                    DbConstants.TABLE_POST);
            sharedWithMeQuery.whereEqualTo(DbConstants.SHARE_WITH,
                    ParseUser.getCurrentUser());

            ParseQuery<ParseObject> fromFriendsPublicPostsQuery = new ParseQuery<ParseObject>(
                    DbConstants.TABLE_POST);
            fromFriendsPublicPostsQuery.whereMatchesKeyInQuery(
                    DbConstants.USER, DbConstants.TO, fromQuery);
            fromFriendsPublicPostsQuery
                    .whereDoesNotExist(DbConstants.SHARE_WITH);

            ParseQuery<ParseObject> toFriendsPublicPostsQuery = new ParseQuery<ParseObject>(
                    DbConstants.TABLE_POST);
            toFriendsPublicPostsQuery.whereMatchesKeyInQuery(DbConstants.USER,
                    DbConstants.FROM, toQuery);
            fromFriendsPublicPostsQuery
                    .whereDoesNotExist(DbConstants.SHARE_WITH);

            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
            queries.add(myPostsQuery);
            queries.add(sharedWithMeQuery);
            queries.add(toFriendsPublicPostsQuery);
            queries.add(fromFriendsPublicPostsQuery);

            mQuery = ParseQuery.or(queries);
            mQuery.setLimit(30);
        }

        mQuery.addDescendingOrder(DbConstants.CREATED_AT);
        mQuery.include("User");

        return mQuery;
    }*/

    //    public static ParseQuery<ParseObject> getPostCommentsQuery(ParseObject post) {
//        ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
//                DbConstants.TABLE_COMMENT);
//        mQuery.whereEqualTo(DbConstants.POST, post);
//        mQuery.addDescendingOrder(DbConstants.CREATED_AT);
//        return mQuery;
//    }
    public static Query getPostCommentSQuery(Posts post) {
        Query query = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_COMMENT).
                orderByChild("post").equalTo(post.getObjectId());
        Log.i("getPostCommentSQuery", query.toString());

        return query;

    }

   /* public static ParseQuery<ParseObject> getStoryCommentsQuery(
            ParseObject story) {
        ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_STORY_COMMENT);
        mQuery.whereEqualTo(DbConstants.POST, story);
        mQuery.addDescendingOrder(DbConstants.CREATED_AT);
        return mQuery;
    }*/

    public static Query getTributeCommentsQuery(
            Tributes tribute) {
        Query query = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_TRIBUTE_COMMENT).
                orderByChild(DbConstants.POST).equalTo(tribute.getObjectId());
        return query;
        /*ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_TRIBUTE_COMMENT);
        mQuery.whereEqualTo(DbConstants.POST, tribute);
        mQuery.addDescendingOrder(DbConstants.CREATED_AT);
        return mQuery;*/
    }

/* might use this condition for getUserConnectionsQuery
   if (obj.getParseUser(DbConstants.TO).getObjectId()
                            .equals(currentId)) {
        if (obj.getParseUser(DbConstants.FROM).fetchIfNeeded()
                .getInt(DbConstants.TYPE) != Constants.USER_TYPE.SUPPORTER
                .ordinal()) {
            if (obj.getBoolean(DbConstants.STATUS))
                connections.activeConnections.add(obj
                        .getParseUser(DbConstants.FROM));
            else
                connections.pendingConnections.add(obj
                        .getParseUser(DbConstants.FROM));
        }
    } else {
        if (obj.getParseUser(DbConstants.TO).fetchIfNeeded()
                .getInt(DbConstants.TYPE) != Constants.USER_TYPE.SUPPORTER
                .ordinal()) {
            if (obj.getBoolean(DbConstants.STATUS))
                connections.activeConnections.add(obj
                        .getParseUser(DbConstants.TO));
            else
                connections.pendingConnections.add(obj
                        .getParseUser(DbConstants.TO));
        }
    }*/

    public static void getUserConnectionsQuery(
            final Users user, boolean fromCache, final Context context, final GetConnectionCompletion getConnectionCompletion) {


        final List<Users> activeConnections = new ArrayList<>();
        final List<Users> pendingConnections = new ArrayList<>();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(DbConstants.TABLE_CONNECTIONS).addListenerForSingleValueEvent(new ValueEventListener() {
            // mDatabase.child(DbConstants.TABLE_CONNECTIONS).orderByChild(DbConstants.FROM).equalTo(user.getObjectId()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {


                    new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
                        @Override
                        public void run() {

                            if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    ConnectionsModel connectionsModel = dataSnapshot1.getValue(ConnectionsModel.class);
                                    String currentId = Preferences.getInstance(context).getString(DbConstants.ID);
                                    if (connectionsModel.getFrom().equalsIgnoreCase(Preferences.getInstance(context).getString(DbConstants.ID)) ||
                                            connectionsModel.getTo().equalsIgnoreCase(Preferences.getInstance(context).getString(DbConstants.ID))) {

                                        ArrayList<Users> allusers = Preferences.getInstance(context).getAllUsers(DbConstants.FETCH_USER);

                                        if (connectionsModel.getStatus() == true) {
                                            //exists and is a connection.. dont send connection request
                                            if (connectionsModel.getFrom().equalsIgnoreCase(Preferences.getInstance(context).getString(DbConstants.ID))) {
                                                Users userExists = Utils.getUserIndexFromObjectId(connectionsModel.getTo(), allusers);
                                                if (userExists != null) {
                                                    activeConnections.add(userExists);
                                                }
                                            } else {
                                                Users userExists = Utils.getUserIndexFromObjectId(connectionsModel.getFrom(), allusers);
                                                if (userExists != null)
                                                    activeConnections.add(userExists);
                                            }
                                            //getConnectionDetails(connectionsModel.getFrom(), activeConnections, pendingConnections, true, context);


                                        } else {

                                            //exists and pending.. no need to send connection request
                                            if (connectionsModel.getFrom().equalsIgnoreCase(Preferences.getInstance(context).getString(DbConstants.ID))) {
                                                Users userExists = Utils.getUserIndexFromObjectId(connectionsModel.getTo(), allusers);
                                                if (userExists != null) {
                                                    pendingConnections.add(userExists);
                                                }
                                            } else {
                                                Users userExists = Utils.getUserIndexFromObjectId(connectionsModel.getFrom(), allusers);
                                                if (userExists != null)
                                                    pendingConnections.add(userExists);
                                            }
                                    /*getConnectionDetails(connectionsModel.getFrom(), activeConnections, pendingConnections, false, context);
                                    System.out.println("check pending status: " + connectionsModel.getStatus());*/

                                        }

                                    }


                                    //check for other connections
                                    // searchOtherConnections(user, activeConnections, pendingConnections, context);
                                }
                                getConnectionCompletion.isComplete(true);
                                Preferences.getInstance(context).saveConnectionsLocally(activeConnections, pendingConnections);
                            }
                            /*getConnectionCompletion.isComplete(true);*/
                            System.out.println("check other pending status: " + activeConnections.size() + " " + pendingConnections.size() + " " + dataSnapshot.getChildrenCount());
                           /* Preferences.getInstance(context).saveConnectionsLocally(activeConnections, pendingConnections);*/
                        }
                    });

//                    else{
//                        //check for other connections
//                        //searchOtherConnections(user, activeConnections, pendingConnections, context);
//                    }



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //return mDatabase.child(DbConstants.TABLE_CONNECTIONS).equalTo(DbConstants.TO, user.getObjectId());
    }

    private static void searchOtherConnections(final Users user, final List<Users> activeConnections, final List<Users> pendingConnections, final Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(DbConstants.TABLE_CONNECTIONS).orderByChild(DbConstants.TO).equalTo(user.getObjectId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        ConnectionsModel connectionsModel = dataSnapshot1.getValue(ConnectionsModel.class);
                        if (connectionsModel.getStatus() == true) {
                            //exists and is a connection.. dont send connection request
                            getConnectionDetails(connectionsModel.getFrom(), activeConnections, pendingConnections, true, context);
                        } else {
                            //exists and pending.. no need to send connection request
                            getConnectionDetails(connectionsModel.getFrom(), activeConnections, pendingConnections, false, context);
                        }
                        System.out.println("check other pending status: " + connectionsModel.getStatus());
                    }

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void getConnectionDetails(String uid, final List<Users> active, final List<Users> pending, final boolean activeUser, final Context context) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS).child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.getValue() == null) {
                    //Utils.showPrompt(LoginActivity.this, "This user may not exists in database");
                    Utils.hideProgress();
                } else {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (activeUser)
                        active.add(user);
                    else
                        pending.add(user);
                    System.out.println("pending size" + pending.size());
                    //save to preferences
                    Preferences.getInstance(context).saveConnectionsLocally(active, pending);
                    System.out.println("connections saved locally");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //save to preferences
                Preferences.getInstance(context).saveConnectionsLocally(active, pending);
                System.out.println("connections saved locally");
            }
        });
    }

    /*public static ParseQuery<ParseObject> getUserConnectionsQuery(
            ParseUser user, boolean fromCache) {

        ParseQuery<ParseObject> fromQuery = ParseQuery
                .getQuery(DbConstants.TABLE_CONNECTIONS);
        fromQuery.whereEqualTo(DbConstants.FROM, user);

        ParseQuery<ParseObject> toQuery = ParseQuery
                .getQuery(DbConstants.TABLE_CONNECTIONS);
        toQuery.whereEqualTo(DbConstants.TO, user);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(toQuery);
        queries.add(fromQuery);

        ParseQuery<ParseObject> mQuery = ParseQuery.or(queries);
        mQuery.include("User");
        mQuery.setLimit(1000);

        if (fromCache)
            mQuery.fromPin(Constants.TAG_CONNECTIONS);

        return mQuery;
    }*/

    /*public static ParseQuery<ParseObject> getUserActiveConnectionsQuery(
            ParseUser user, boolean fromCache) {

        ParseQuery<ParseObject> fromQuery = ParseQuery
                .getQuery(DbConstants.TABLE_CONNECTIONS);
        fromQuery.whereEqualTo(DbConstants.FROM, user);
        fromQuery.whereEqualTo(DbConstants.STATUS, true);

        ParseQuery<ParseObject> toQuery = ParseQuery
                .getQuery(DbConstants.TABLE_CONNECTIONS);
        toQuery.whereEqualTo(DbConstants.TO, user);
        toQuery.whereEqualTo(DbConstants.STATUS, true);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(toQuery);
        queries.add(fromQuery);

        ParseQuery<ParseObject> mQuery = ParseQuery.or(queries);
        mQuery.include("User");
        mQuery.setLimit(1000);

        if (fromCache)
            mQuery.fromPin(Constants.TAG_CONNECTIONS);

           return mQuery;
       }*/

    static List<Users> user_list;
    static boolean isCompleted = false;

    public static Query getUserActiveConnectionsQuery(final Context context,
                                                      String name, final ConnectionExist connectionExist) {
        Query query = null;

        query = FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS).
                orderByChild(DbConstants.NAME).equalTo(name);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                user_list = new ArrayList<Users>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    Users users = dataSnapshot1.getValue(Users.class);

                    user_list.add(users);
                }
                isCompleted = true;
                afterCompletion(context, isCompleted, user_list, connectionExist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//        final String objectID = Preferences.getInstance(context).getString(DbConstants.ID) + "_" + users.getObjectId();
//        // TODO: 7/19/2017 Check if there's a pending connection
//        ref.child(DbConstants.TABLE_CONNECTIONS).orderByKey().equalTo(objectID).
//                addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
//                    boolean sendRequest = true;
//                    ConnectionsModel connectionsModel = dataSnapshot.getValue(ConnectionsModel.class);
//                    if (connectionsModel.getStatus() == true) {
//                        //exists and is a connection.. dont send connection request
//
//                    } else {
//                        //exists and pending.. no need to send connection request
//                        checkReverse(ref, users.getObjectId() + "_" + Preferences.getInstance(context).getString(DbConstants.ID), connectionsModel, users);
//                    }
//                } else {
//                    //send add connection request
//                    checkReverse(ref, users.getObjectId() + "_" + Preferences.getInstance(context).getString(DbConstants.ID), null, users);
//                    //sendConnectionRequest(ref, objectID, connection);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        return query;
    }

    public static void afterCompletion(Context context, boolean isCompleted, List<Users> user_list, final ConnectionExist connectionExist) {
        if (isCompleted && user_list.size() > 0) {
            for (int i = 0; i < user_list.size(); i++) {
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                final String objectID = Preferences.getInstance(context).
                        getString(DbConstants.ID) + "_" + user_list.get(i).getObjectId();

                final Users users = user_list.get(i);
                final String reversed_objectID = user_list.get(i).getObjectId() + "_" + Preferences.getInstance(context).
                        getString(DbConstants.ID);
                ref.child(DbConstants.TABLE_CONNECTIONS).orderByChild(DbConstants.ID).equalTo(objectID).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.i("datasnapshot", dataSnapshot.toString());
                                if (dataSnapshot.getValue() == null) {
                                    checkReverse(users, ref, reversed_objectID, connectionExist);
                                } else {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        //  String key = data.getKey();
                                        ConnectionsModel connection = data.getValue(ConnectionsModel.class);

                                        Log.i("connections_values", connection.toString());
                                        //Map<String, Object> values = (Map<String, Object>) data.getValue(Map.class);

                                        //Log.i("connections_values", values.toString());
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        }
    }

    static boolean isConnectionExist = false;

    private static void checkReverse(final Users users, final DatabaseReference ref, final String objectID, final ConnectionExist connectionExist) {
        ref.child(DbConstants.TABLE_CONNECTIONS).orderByChild(DbConstants.ID).equalTo(objectID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    ConnectionsModel connection = new ConnectionsModel();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String key = dataSnapshot1.getKey();
                        Map<String, Object> connections = (Map<String, Object>) dataSnapshot1.getValue();

                        connection.setStatus(Boolean.parseBoolean(String.valueOf(connections.get(DbConstants.STATUS))));
                        connection.setCreatedAt(String.valueOf(connections.get(DbConstants.CREATED_AT)));
                        connection.setFrom(String.valueOf(connections.get(DbConstants.FROM)));
                        connection.setObjectId(String.valueOf(connections.get(DbConstants.ID)));
                        connection.setTo(String.valueOf(connections.get(DbConstants.TO)));
                        connection.setUpdatedAt(String.valueOf(connections.get(DbConstants.UPDATED_AT)));
                    }

                    if (connection.getStatus() == true) {
                        //exists and is a connection.. dont send connection request

                        isConnectionExist = true;
                        connectionExist.isConnection(isConnectionExist, users);
                    } else {
                        //exists and pending.. no need to send connection request
                        isConnectionExist = false;
                        connectionExist.isConnection(isConnectionExist, users);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
