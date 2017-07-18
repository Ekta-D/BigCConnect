package com.bigc.interfaces;

import com.bigc.models.Users;

public interface MessageObserver {

	public boolean onMessageReceive(Object message, Users sender);
}
