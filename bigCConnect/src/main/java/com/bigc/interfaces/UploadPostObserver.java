package com.bigc.interfaces;

import com.parse.ParseObject;

public interface UploadPostObserver {

	public void onNotify(ParseObject post);
	
	public void onEditDone(int position, ParseObject post);
}
