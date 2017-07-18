package com.bigc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends Activity {

    private volatile boolean sync = false;
    Runnable r;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Utils.unregisterDeviceForNotifications();
            startActivity(new Intent(Splash.this, LoginActivity.class));
        } else {
            Utils.registerDeviceForNotifications();
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


}
