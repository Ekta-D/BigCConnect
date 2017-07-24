package com.bigc.interfaces;

import com.bigc.models.Posts;
import com.bigc.models.Tributes;

public interface PopupOptionHandler {

    	public abstract void onDelete(int position, Object post);
	public abstract void onEditClicked(int position, Object post);
	public abstract void  onFlagClicked(int position, Object post);

}
