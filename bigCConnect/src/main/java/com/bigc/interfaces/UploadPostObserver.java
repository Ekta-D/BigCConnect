package com.bigc.interfaces;

import com.bigc.models.Posts;

public interface UploadPostObserver {

    	public void onNotify(Posts post);
    //public void onNotify(Posts postObject);

//    public void onEditDone(int position, Posts postObject);
	public void onEditDone(int position, Posts post);
}
