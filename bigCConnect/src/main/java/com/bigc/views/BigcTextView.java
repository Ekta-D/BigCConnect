package com.bigc.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class BigcTextView extends TextView {

	public BigcTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BigcTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BigcTextView(Context context) {
		super(context);
		init();
	}

	public void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"font/marker_felt.ttf");
		setTypeface(tf);
	}

}