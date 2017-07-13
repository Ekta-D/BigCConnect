package com.bigc.models;

import com.bigc.general.classes.DbConstants;
import com.parse.ParseUser;

public class Connection {

	ParseUser obj;

	public Connection(ParseUser usr) {
		this.obj = usr;
	}

	@Override
	public String toString() {
		return obj.getString(DbConstants.NAME);
	}
}
