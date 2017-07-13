package com.bigc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.views.TouchImageView;
import com.bigc_connect.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ZoomActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zoom_layout);

		TouchImageView imageView = (TouchImageView) findViewById(R.id.img);
		String path = getIntent() == null ? "" : getIntent().getStringExtra(
				"path");
		ImageLoader.getInstance().displayImage(path, imageView);
		findViewById(R.id.doneButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
		
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
				"Image-Zoom Screen");
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		finish();
		overridePendingTransition(R.anim.remains_same, R.anim.pull_down);
	}
}
