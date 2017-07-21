package com.bigc.interfaces;

import com.bigc.models.Posts;
import com.bigc.models.Tributes;

public interface UploadPostObserver {

    	public void onNotify(Tributes post);
    //public void onNotify(Posts postObject);

//    public void onEditDone(int position, Posts postObject);
	public void onEditDone(int position, Posts post);
}
