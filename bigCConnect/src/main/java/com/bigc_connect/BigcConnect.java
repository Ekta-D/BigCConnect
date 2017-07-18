package com.bigc_connect;

import android.app.Application;
import android.graphics.Bitmap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class BigcConnect extends Application {

	private Bitmap bitmap;
	private Tracker mTracker;

	@Override
	public void onCreate() {
		super.onCreate();

		// cloud integration would be here
		/*Parse.enableLocalDatastore(getApplicationContext());
		Parse.initialize(this, Constants.CLOUD_ID, Constants.CLOUD_KEY);

		// ONLY FOR PREMIUM VERSION
//		if (!Preferences.getInstance(this).getBoolean(Constants.PREMIUM))
//			Preferences.getInstance(this).save(Constants.PREMIUM, true);

		bitmap = null;
		ParsePush.subscribeInBackground("");*/

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).build();
		ImageLoader.getInstance().init(config);
	}

	public Bitmap getUpdatedBitmap() {
		return bitmap;
	}

	public void setUpdatedBitmap(Bitmap bm) {
		bitmap = bm;
	}

	public synchronized Tracker getTracker() {
		if (mTracker == null) {
			mTracker = GoogleAnalytics.getInstance(this).newTracker(
					"UA-57259386-1");
		}

		return mTracker;
	}
}
