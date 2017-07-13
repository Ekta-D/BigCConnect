package com.bigc.interfaces;

import com.parse.ParseObject;

public interface UploadPostObservable {

	public void addObserver(UploadPostObserver o);

	public void addTributeObserver(UploadPostObserver o);

	public void addObserver(UploadStoryObserver o);

	public void removePostObserver();

	public void removeStoryObserver();

	public void removeTributeObserver();

	public void notifyFeedObservers(ParseObject post);

	public void notifyStoryObservers(ParseObject post);

	public void notifyTributeObservers(ParseObject tribute);

	public void notifyEditFeedObservers(int position, ParseObject post);

	public void notifyEditStoryObservers(int position, ParseObject post);

	public void notifyEditTributeObservers(int position, ParseObject tribute);
}
