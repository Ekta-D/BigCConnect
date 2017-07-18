package com.bigc.interfaces;

import com.bigc.models.Posts;
import com.bigc.models.Stories;

public interface UploadStoryObserver {

	public void onNotify(Stories story);
	
	public void onEditDone(int position, Posts post);
}
