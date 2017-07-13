package com.bigc.interfaces;

import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;


public abstract class BaseFragment extends Fragment implements
		OnClickListener {

	public abstract String getName();

	public abstract int getTab();

	public abstract boolean onBackPressed();
}