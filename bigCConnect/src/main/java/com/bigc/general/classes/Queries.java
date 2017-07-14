package com.bigc.general.classes;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.bigc.datastorage.Preferences;
import com.bigc.models.Posts;
import com.bigc.models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Queries {

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

    public static ArrayList<Users> getSearchSurvivorQuery(String keyword) {
        final ArrayList<Users> searchUsers = null;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(DbConstants.USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.hasChildren()){
                    for( DataSnapshot data: dataSnapshot.getChildren()) {
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

    public static ParseQuery<ParseObject> getPendingRequestsQuery() {
        ParseQuery<ParseObject> query = ParseQuery
                .getQuery(DbConstants.TABLE_CONNECTIONS);
        query.whereEqualTo(DbConstants.TO, ParseUser.getCurrentUser());
        query.whereEqualTo(DbConstants.STATUS, false);

        return query;
    }

    public static ArrayList<Users> getCategorizedUsersQuery(int ribbon) {
        final ArrayList<Users> categoryUsers = null;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(DbConstants.USERS).startAt(DbConstants.RIBBON,String.valueOf(ribbon)).orderByChild(DbConstants.CREATED_AT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.hasChildren()){
                    for( DataSnapshot data: dataSnapshot.getChildren()) {
                        categoryUsers.add(data.getValue(Users.class));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       /* ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo(DbConstants.TYPE,
                Constants.USER_TYPE.SUPPORTER.ordinal());
        query.whereEqualTo(DbConstants.RIBBON, ribbon);
        query.whereNotEqualTo(DbConstants.VISIBILITY, Constants.PRIVATE);
        query.orderByDescending(DbConstants.CREATED_AT);
        query.whereNotEqualTo(DbConstants.DEACTIVATED, true);*/

        return categoryUsers;
    }

/*
    public static ParseQuery<ParseUser> getCategorizedUsersQuery(int ribbon) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo(DbConstants.TYPE,
                Constants.USER_TYPE.SUPPORTER.ordinal());
        query.whereEqualTo(DbConstants.RIBBON, ribbon);
        query.whereNotEqualTo(DbConstants.VISIBILITY, Constants.PRIVATE);
        query.orderByDescending(DbConstants.CREATED_AT);
        query.whereNotEqualTo(DbConstants.DEACTIVATED, true);

        return query;
    }
*/

    public static ParseQuery<ParseObject> getUserFeedsQuery(ParseUser user) {
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
    }

    public static ParseQuery<ParseObject> getConversationsQuery(
            boolean fromCache) {
        ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_CONVERSATION);

        ParseQuery<ParseObject> sentMessagesQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_CONVERSATION);
        sentMessagesQuery.whereEqualTo(DbConstants.USER1,
                ParseUser.getCurrentUser());

        ParseQuery<ParseObject> recievedMessagesQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_CONVERSATION);
        recievedMessagesQuery.whereEqualTo(DbConstants.USER2,
                ParseUser.getCurrentUser());

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(recievedMessagesQuery);
        queries.add(sentMessagesQuery);

        mQuery = ParseQuery.or(queries);

        mQuery.orderByDescending(DbConstants.UPDATED_AT);

        mQuery.include("User");
        mQuery.setLimit(1000);

        return mQuery;
    }

    public static ParseQuery<ParseObject> getTributesQuery() {
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
    }

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

    public static ParseQuery<ParseObject> getStoryCommentsQuery(
            ParseObject story) {
        ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_STORY_COMMENT);
        mQuery.whereEqualTo(DbConstants.POST, story);
        mQuery.addDescendingOrder(DbConstants.CREATED_AT);
        return mQuery;
    }

    public static ParseQuery<ParseObject> getTributeCommentsQuery(
            ParseObject tribute) {
        ParseQuery<ParseObject> mQuery = new ParseQuery<ParseObject>(
                DbConstants.TABLE_TRIBUTE_COMMENT);
        mQuery.whereEqualTo(DbConstants.POST, tribute);
        mQuery.addDescendingOrder(DbConstants.CREATED_AT);
        return mQuery;
    }

    public static ParseQuery<ParseObject> getUserConnectionsQuery(
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
    }

    public static ParseQuery<ParseObject> getUserActiveConnectionsQuery(
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
    }
}
