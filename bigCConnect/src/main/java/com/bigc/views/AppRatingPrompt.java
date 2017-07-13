package com.bigc.views;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc_connect.R;

public class AppRatingPrompt {

	private final static int LAUNCHES_UNTIL_PROMPT = 20;
	private final static long DAYS_TILL_TO_SHOW = 7 * 24 * 60 * 60 * 1000;

	public static void app_launched(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
		if (prefs.getBoolean("dontshowagain", false)) {
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launch_count = prefs.getLong(Constants.LAUNCH_COUNT, 0) + 1;
		editor.putLong(Constants.LAUNCH_COUNT, launch_count);

		// Set date of first launch
		Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_firstlaunch", date_firstLaunch);
		}

		// Wait specified minimum number of days before opening
		if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
			Preferences.getInstance(mContext).save(Constants.SPLASHES, false);
			if (System.currentTimeMillis() >= date_firstLaunch
					+ DAYS_TILL_TO_SHOW) {
				showRateDialog(mContext, editor);
			}
		}

		editor.commit();
	}

	public static void showRateDialog(final Context mContext,
			final SharedPreferences.Editor editor) {
		final Builder builder = new AlertDialog.Builder(mContext)
				.setTitle(R.string.dlg_rate_title)
				.setIcon(R.drawable.ic_launcher)
				.setMessage(R.string.dlg_rate_message)
				.setPositiveButton(R.string.dlg_rate_pos,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mContext.startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse(mContext
												.getString(R.string.dlg_rate_marketurl))));
								if (editor != null) {
									editor.putBoolean("dontshowagain", true);
									editor.commit();
								}
								dialog.dismiss();
							}
						})
				.setNeutralButton(R.string.dlg_rate_neut,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (editor != null) {
									editor.putLong(Constants.LAUNCH_COUNT, 0);
									editor.commit();
								}
								dialog.dismiss();
							}
						})
				.setNegativeButton(R.string.dlg_rate_neg,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (editor != null) {
									editor.putBoolean("dontshowagain", true);
									editor.commit();
								}
								dialog.dismiss();
							}
						});
		builder.show();
	}

}