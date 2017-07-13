package com.bigc.interfaces;

import com.parse.ParseObject;
import com.parse.ParseUser;

public interface MessageObserver {

	public boolean onMessageReceive(ParseObject message, ParseUser sender);
}
