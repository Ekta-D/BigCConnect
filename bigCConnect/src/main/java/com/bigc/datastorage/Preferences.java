package com.bigc.datastorage;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.models.Connection;
import com.bigc.models.ConnectionsModel;
import com.bigc.models.Feeds;
import com.bigc.models.Users;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Preferences {

    private static Preferences instance;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private Preferences(Context context) {

        prefs = context.getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void clearPreferences(Context context) {
        context.getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE).edit().clear().commit();
//        editor.clear();
//        editor.commit();
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

    public void save_list(String key, ArrayList<Users> user_value) {
        Gson gson = new Gson();
        String all_users = gson.toJson(user_value);
        editor.putString(key, all_users);

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

    public Users getUserFromPreference() {
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

    public void saveConnectionsLocally(List<Users> activeConnections, List<Users> pendingConnections) {
        Gson gson = new Gson();
        String acJson = gson.toJson(activeConnections);
        String pcJson = gson.toJson(pendingConnections);
        editor.putString("activeConnections", acJson);
        editor.putString("pendingConnections", pcJson);
        editor.commit();
    }

    public ArrayList<Users> getAllUsers(String key) {
        Gson gson = new Gson();
        String users = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Users>>() {
        }.getType();
        ArrayList<Users> all_users = gson.fromJson(users, type);
        return all_users;
    }

    public List<ArrayList<Users>> getLocalConnections() {
        Gson gson = new Gson();
        String acJson = prefs.getString("activeConnections", null);
        String pcJson = prefs.getString("pendingConnections", null);
        Type type = new TypeToken<ArrayList<Users>>() {
        }.getType();
        ArrayList<Users> activeConnections = gson.fromJson(acJson, type);
        ArrayList<Users> pendingConnections = gson.fromJson(pcJson, type);

        ArrayList<ArrayList<Users>> connections = new ArrayList<ArrayList<Users>>();
        connections.add(activeConnections);
        connections.add(pendingConnections);
        return connections;
    }
}
