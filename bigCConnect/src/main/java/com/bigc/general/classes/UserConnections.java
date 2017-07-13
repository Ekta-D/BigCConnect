package com.bigc.general.classes;

import java.util.ArrayList;
import java.util.List;

import com.bigc.models.Users;
import com.parse.ParseUser;

public class UserConnections {

	public List<ParseUser> activeConnections = new ArrayList<ParseUser>();
	public List<ParseUser> pendingConnections = new ArrayList<ParseUser>();


}
