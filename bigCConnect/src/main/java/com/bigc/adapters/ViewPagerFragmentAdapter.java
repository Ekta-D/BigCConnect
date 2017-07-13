package com.bigc.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.bigc.fragments.EmailInputFragment;
import com.bigc.fragments.NameInputFragment;
import com.bigc.fragments.PasswordFragment;
import com.bigc.fragments.ProfilePicFragment;
import com.bigc.fragments.SelectionFragment;
import com.bigc_connect.R;
import com.viewpagerindicator.IconPagerAdapter;

public class ViewPagerFragmentAdapter extends FragmentPagerAdapter implements
		IconPagerAdapter {
	private final int LENGTH = 5;

	public ViewPagerFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	public Fragment getItem(int position) {
		position = position % LENGTH;
		Log.e("Position", position + "-");
		switch (position) {
		case 0:
			return EmailInputFragment.newInstance();
		case 1:
			return NameInputFragment.newInstance();
		case 2:
			return PasswordFragment.newInstance();
		case 3:
			return SelectionFragment.newInstance();
		case 4:
			return ProfilePicFragment.newInstance();
		}
		return null;
	}

	@Override
	public int getCount() {
		return LENGTH;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "";
	}

	@Override
	public int getIconResId(int index) {
		return R.drawable.ic_launcher;
	}
}