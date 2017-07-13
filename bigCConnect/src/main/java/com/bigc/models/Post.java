package com.bigc.models;

import android.graphics.Bitmap;

import com.bigc.general.classes.DbConstants;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName(DbConstants.TABLE_POST)
public class Post extends ParseObject {

	public Post() {
		super();
	}

	public Post(String message, Bitmap media) {

	}

	public String getStatus() {
		return null;
	}

	public String getMediaUrl() {
		return null;
	}

}
