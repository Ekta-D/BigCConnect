package com.bigc.interfaces;

import com.parse.ParseObject;

public interface UploadPostObserver {

    	public void onNotify(ParseObject post);
    //public void onNotify(Posts postObject);

//    public void onEditDone(int position, Posts postObject);
	public void onEditDone(int position, ParseObject post);
}
