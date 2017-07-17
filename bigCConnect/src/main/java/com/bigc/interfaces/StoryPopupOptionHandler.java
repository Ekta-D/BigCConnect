package com.bigc.interfaces;

import com.bigc.models.Posts;
import com.bigc.models.Stories;
import com.parse.ParseObject;

/**
 * Created by ENTER on 17-07-2017.
 */
public interface StoryPopupOptionHandler {

    public abstract void onDelete(int position, Stories story);
    public abstract void onEditClicked(int position, Stories story);
    public abstract void  onFlagClicked(int position, Stories story);
}
