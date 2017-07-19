package com.bigc.interfaces;

import com.bigc.models.Posts;

public interface UploadPostObserver {

    	public void onNotify(Object post);
    //public void onNotify(Posts postObject);

//    public void onEditDone(int position, Posts postObject);
	public void onEditDone(int position, Posts post);
}
