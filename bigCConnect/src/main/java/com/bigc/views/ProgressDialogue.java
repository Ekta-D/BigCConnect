package com.bigc.views;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.bigc_connect.R;

public class ProgressDialogue extends Dialog {

	public static ProgressDialogue show(final Context context,
			final CharSequence title, final CharSequence message) {
		return show(context, title, message, false);
	}

	public static ProgressDialogue show(final Context context,
			final CharSequence title, final CharSequence message,
			final boolean indeterminate) {
		return show(context, title, message, indeterminate, false, null);
	}

	public static ProgressDialogue show(final Context context,
			final CharSequence title, final CharSequence message,
			final boolean indeterminate, final boolean cancelable) {
		return show(context, title, message, indeterminate, cancelable, null);
	}

	public static ProgressDialogue show(final Context context,
			final CharSequence title, final CharSequence message,
			final boolean indeterminate, final boolean cancelable,
			final OnCancelListener cancelListener) {
		ProgressDialogue dialog = new ProgressDialogue(context);
		dialog.setTitle(title);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		/* The next line will add the ProgressBar to the dialog. */
		dialog.addContentView(new ProgressBar(context), new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		dialog.show();

		return dialog;
	}

	public ProgressDialogue(final Context context) {
		super(context, R.style.ProgressDialog);
	}
}
