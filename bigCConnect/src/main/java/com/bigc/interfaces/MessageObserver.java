package com.bigc.interfaces;

import com.bigc.models.Messages;
import com.bigc.models.Users;

public interface MessageObserver {

    //	public boolean onMessageReceive(Object message, Users sender);
    public boolean onMessageReceive(Messages message, Users sender);
}
