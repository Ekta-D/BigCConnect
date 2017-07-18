package com.bigc.interfaces;

import com.bigc.models.Posts;

public interface PopupOptionHandler {

    	public abstract void onDelete(int position, Posts post);
	public abstract void onEditClicked(int position, Posts post);
	public abstract void  onFlagClicked(int position, Posts post);
//    public abstract void onDelete(int position, Posts posts);
//
//    public abstract void onEditClicked(int position, Posts posts);
//
//    public abstract void onFlagClicked(int position, Posts posts);

}
