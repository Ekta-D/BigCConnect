package com.bigc.interfaces;


public interface MessageObservable {

	public void bindObserver(MessageObserver ob);

	public void freeObserver();
/*
	public boolean notifyObserver(ParseObject message, ParseUser sender);*/
}
