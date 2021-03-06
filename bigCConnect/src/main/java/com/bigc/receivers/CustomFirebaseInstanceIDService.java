package com.bigc.receivers;

import android.util.Log;

import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.DbConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;

/**
 * Created by beesolver on 7/27/2017.
 */

public class CustomFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            String uid = "";
            if (Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID) != null && !Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID).equalsIgnoreCase(""))
                uid = Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID);
            else
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            HashMap<String, Object> map = new HashMap();
            map.put(DbConstants.TOKEN, token);
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS).
                        child(uid).
                        updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Log.d(TAG, "Refreshed token saved");
                            }
                        });
        }
    }
}
