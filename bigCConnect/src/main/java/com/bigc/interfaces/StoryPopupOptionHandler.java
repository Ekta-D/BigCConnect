package com.bigc.interfaces;

import com.bigc.models.Stories;

/**
 * Created by beesolver on 7/18/2017.
 */

public interface StoryPopupOptionHandler {
    public abstract void onDelete(int position, Stories story);
    public abstract void onEditClicked(int position, Stories story);
    public abstract void  onFlagClicked(int position, Stories story);
}
