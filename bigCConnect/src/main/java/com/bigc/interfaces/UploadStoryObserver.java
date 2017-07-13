package com.bigc.interfaces;

import com.parse.ParseObject;

public interface UploadStoryObserver {

	public void onNotify(ParseObject story);
	
	public void onEditDone(int position, ParseObject post);
}
