package com.bigc.interfaces;

import com.bigc.models.Post;
import com.bigc.models.Posts;
import com.parse.ParseObject;

public interface PopupOptionHandler {

    	public abstract void onDelete(int position, Posts post);
	public abstract void onEditClicked(int position, Posts post);
	public abstract void  onFlagClicked(int position, ParseObject post);
//    public abstract void onDelete(int position, Posts posts);
//
//    public abstract void onEditClicked(int position, Posts posts);
//
//    public abstract void onFlagClicked(int position, Posts posts);

}
