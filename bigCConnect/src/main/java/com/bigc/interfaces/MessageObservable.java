package com.bigc.interfaces;

import com.parse.ParseObject;
import com.parse.ParseUser;

public interface MessageObservable {

	public void bindObserver(MessageObserver ob);

	public void freeObserver();

	public boolean notifyObserver(ParseObject message, ParseUser sender);
}
