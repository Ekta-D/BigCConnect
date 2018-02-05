package com.bigc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class Splash extends Activity {

    private volatile boolean sync = false;
    Runnable r;
    Handler handler = new Handler();
    boolean isFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isFirstTime = Preferences.getInstance(Splash.this).getBoolean(Constants.ISFIRST_TIME);
        //ParseAnalytics.trackAppOpenedInBackground(getIntent());
        sync = false;


        handler = new Handler();
        r = new Runnable() {

            @Override
            public void run() {

                if (sync) {
                    startApplication();
                } else {
                    // Wait More
                    handler.postDelayed(r, 1000);
                }
            }
        };

        handler.postDelayed(r, 2000);
        sync = true;

        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
                "Splash Screen");
    }

    @Override
    public void onNewIntent(Intent data) {
        super.onNewIntent(data);

    }

    @Override
    public void onBackPressed() {

    }

    private void startApplication() {
        if (!isFirstTime) {
                startActivity(new Intent(Splash.this, LoginActivity.class));
        } else {
            sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());

            //Utils.registerDeviceForNotifications();
            Intent i = new Intent(Splash.this, HomeScreen.class);
            if (getIntent() != null) {

                if (getIntent().getExtras() != null) {
                    i.putExtras(getIntent().getExtras());
                }

                if (getIntent().getAction() != null) {
                    i.setAction(getIntent().getAction());
                }
            }
            startActivity(i);
        }
        finish();
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        HashMap<String, Object> map = new HashMap();
        map.put(DbConstants.TOKEN, token);

        String uid = "";
        if(Preferences.getInstance(Splash.this).getString(DbConstants.ID)!=null && !Preferences.getInstance(Splash.this).getString(DbConstants.ID).equalsIgnoreCase(""))
            uid= Preferences.getInstance(Splash.this).getString(DbConstants.ID);
        else
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

       /* FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS).child(Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID)).updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d("", "Refreshed token saved");
            }
        });*/
        FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS).child(uid).updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d("", "Refreshed token saved");
            }
        });
    }

}
