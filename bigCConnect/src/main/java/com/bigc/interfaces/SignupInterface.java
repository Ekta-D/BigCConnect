package com.bigc.interfaces;

import android.support.v4.app.Fragment;

public interface SignupInterface {

	public void replaceFragment(Fragment fragment);

	public void launchLogin();

	public boolean isStatusUpdate();
	
	public void finishWithAnimation(int Result_Code);
}
