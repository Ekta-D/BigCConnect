package com.bigc.general.classes;

import android.app.Activity;

import com.bigc_connect.BigcConnect;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class GoogleAnalyticsHelper {
	public static void sendScreenViewGoogleAnalytics(Activity activity,
			String screenName) {

		Tracker t = ((BigcConnect) activity.getApplication()).getTracker();
		t.setScreenName(screenName);
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	public static void setClickedAction(Activity activity, String category) {
		sendActionToGoogleAnalytics(activity, category, "clicked");
	}

	private static void sendActionToGoogleAnalytics(Activity activity,
			String category, String action) {

		Tracker t = ((BigcConnect) activity.getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder().setCategory(category)
				.setAction(action).build());
	}
}
