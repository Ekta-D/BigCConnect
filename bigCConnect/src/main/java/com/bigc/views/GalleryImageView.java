package com.bigc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GalleryImageView extends ImageView {
	public GalleryImageView(Context context) {
		super(context);
	}

	public GalleryImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GalleryImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), (int) (1.2 * getMeasuredWidth())); // Snap
		// to
		// width
	}
}