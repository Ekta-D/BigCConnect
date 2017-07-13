package com.bigc.datastorage;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bigc.activities.LoginActivity;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.models.Feeds;
import com.bigc.models.Users;

public class Preferences {

	private static Preferences instance;

	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	private Preferences(Context context) {

		prefs = context.getSharedPreferences(Constants.PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = prefs.edit();
	}

	public static synchronized Preferences getInstance(Context context) {
		if (instance == null) {
			instance = new Preferences(context);
		}
		return instance;
	}

	public void save(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void save(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void save(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key) {
		return prefs.getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return prefs.getBoolean(key, defaultValue);
	}
	
	public String getString(String key) {
		return prefs.getString(key, "");
	}

	public int getInt(String key) {
		return prefs.getInt(key, 0);
	}

	public void saveInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void saveFeeds(Feeds feeds, String key) {
		ObjectMapper mapper = new ObjectMapper();
		Writer strWriter = new StringWriter();
		try {
			mapper.writeValue(strWriter, feeds);
			String jsonString = strWriter.toString();
			editor.putString(key, jsonString);
			editor.commit();

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Feeds getFeeds(String key) {
		String userString = prefs.getString(key, "");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		Feeds feeds = null;
		try {
			feeds = mapper.readValue(userString, Feeds.class);
			return feeds;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Ex", "Mapper-" + e.toString());
			return new Feeds();
		}
	}

	public Users getUserFromPreference(){
		Users user = new Users();
		user.setName(getString(DbConstants.NAME));
		user.setEmail(getString(DbConstants.EMAIL));
		user.setProfile_picture(getString(DbConstants.PROFILE_PICTURE));
		user.setRibbon(getInt(DbConstants.RIBBON));
		user.setLocation(getString(DbConstants.LOCATION));
		user.setStage(getString(DbConstants.STAGE));
		user.setCancertype(getString(DbConstants.CANCER_TYPE));
		user.setType(getInt(DbConstants.TYPE));
		user.setObjectId(getString(DbConstants.ID));
		user.setVisibility(getInt(DbConstants.VISIBILITY));
		return user;
	}


}
