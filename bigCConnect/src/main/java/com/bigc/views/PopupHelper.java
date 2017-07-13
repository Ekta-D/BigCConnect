package com.bigc.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

public class PopupHelper {
	public static final int UPPER_HALF = 0;
	public static final int LOWER_HALF = 1;

	public static PopupWindow newBasicPopupWindow(Context context) {
		final PopupWindow window = new PopupWindow(context);

		// when a touch even happens outside of the window
		// make the window go away
		window.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					window.dismiss();
					return true;
				}
				return false;
			}
		});

		window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		window.setTouchable(true);
		window.setFocusable(true);
		window.setOutsideTouchable(true);

		window.setBackgroundDrawable(new ColorDrawable(
				android.R.color.darker_gray));
		return window;
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in the X direction
	 * @param yOffset
	 *            offset in the Y direction
	 */
	public static void showLikeQuickAction(PopupWindow window, View root,
			View anchor, DisplayMetrics metrics, int xOffset, int yOffset,
			int section) {

		// window.setAnimationStyle(R.style.Animations_GrowFromBottomRight);

		int[] location = new int[2];
		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());

		root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = root.getMeasuredWidth();
		int rootHeight = root.getMeasuredHeight();

		int yPos = anchorRect.top - rootHeight + yOffset;
		int[] viewLoc = new int[2];
		anchor.getLocationInWindow(viewLoc);

		int xPos = viewLoc[0] + anchor.getWidth() - rootWidth;
		if (section == UPPER_HALF) {
			yPos = anchorRect.top + anchor.getMeasuredHeight();
		} else {
			yPos = anchorRect.top - rootHeight;
		}
		window.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}
}