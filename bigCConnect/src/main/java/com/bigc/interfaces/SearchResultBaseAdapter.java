package com.bigc.interfaces;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.bigc.activities.LoginActivity;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.UserConnections;
import com.bigc.general.classes.Utils;
import com.parse.ParseUser;

public abstract class SearchResultBaseAdapter extends ArrayAdapter<ParseUser> {

    protected List<ParseUser> data;
    protected LayoutInflater inflater;
    protected List<ParseUser> activeConnections;
    protected List<ParseUser> pendingConnections;
    protected List<ParseUser> newAddedConnections;
    protected List<ParseUser> removedConnections;
    protected boolean isSupporterUser;

    public static final String TAG_CONNECTED = "2";
    public static final String TAG_WAITING = "1";
    public static final String TAG_NOT_CONNECTED = "0";

    protected SearchResultBaseAdapter(Context context, int layout,
                                      List<ParseUser> activeConnections,
                                      List<ParseUser> pendingConnections, List<ParseUser> data) {

        super(context, layout, data);
        if (data == null)
            this.data = new ArrayList<ParseUser>();
        else
            this.data = new ArrayList<ParseUser>(data);

        if (activeConnections == null)
            this.activeConnections = new ArrayList<ParseUser>();
        else
            this.activeConnections = new ArrayList<ParseUser>(activeConnections);

        if (pendingConnections == null)
            this.pendingConnections = new ArrayList<ParseUser>();
        else
            this.pendingConnections = new ArrayList<ParseUser>(
                    pendingConnections);

//		isSupporterUser = ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//				.ordinal();
        isSupporterUser = Preferences.getInstance(context).getInt(DbConstants.TYPE) == 1;

        newAddedConnections = new ArrayList<ParseUser>();
        removedConnections = new ArrayList<ParseUser>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ParseUser getItem(int position) {
        return data.get(position);
    }

    public List<ParseUser> getConnections() {
        List<ParseUser> cns = new ArrayList<ParseUser>();
        cns.addAll(activeConnections);
        cns.addAll(pendingConnections);
        cns.addAll(newAddedConnections);
        return cns;
    }

    public UserConnections getUserConnections() {
        UserConnections cns = new UserConnections();
        cns.activeConnections.addAll(activeConnections);
        cns.pendingConnections.addAll(pendingConnections);
        cns.pendingConnections.addAll(newAddedConnections);
        ;
        return cns;
    }

    public List<ParseUser> getData() {
        return data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public void updateData(List<ParseUser> result,
                           List<ParseUser> activeConnections,
                           List<ParseUser> pendingConnections) {
        this.activeConnections.clear();
        this.pendingConnections.clear();

        if (activeConnections != null)
            this.activeConnections.addAll(activeConnections);

        if (pendingConnections != null)
            this.pendingConnections.addAll(pendingConnections);

        updateData(result);
    }

    public void updateConnections(List<ParseUser> activeConnections,
                                  List<ParseUser> pendingConnections) {
        this.activeConnections.clear();
        this.pendingConnections.clear();

        if (activeConnections != null)
            this.activeConnections.addAll(activeConnections);

        if (pendingConnections != null)
            this.pendingConnections.addAll(pendingConnections);

        notifyDataSetChanged();
    }

    public void updateData(List<ParseUser> result) {
        data.clear();
        if (result == null)
            return;
        data.addAll(result);
        notifyDataSetChanged();
    }

    public void processUserSettings() {
        Utils.addConnections(new ArrayList<ParseUser>(newAddedConnections));
        Utils.removeConnections(new ArrayList<ParseUser>(removedConnections));

        int temp;
        for (ParseUser r : removedConnections) {
            temp = Utils.getUserIndex(r, activeConnections);
            if (temp >= 0) {
                activeConnections.remove(temp);
            } else {
                temp = Utils.getUserIndex(r, pendingConnections);
                if (temp >= 0)
                    pendingConnections.remove(temp);
            }
        }
        pendingConnections.addAll(newAddedConnections);

        newAddedConnections.clear();
        removedConnections.clear();
    }

    public Date getLastItemDate() {
        if (data.size() == 0)
            return null;
        return data.get(data.size() - 1).getCreatedAt();
    }

    public void addItems(List<ParseUser> results, boolean atStart) {

        if (results == null)
            return;

        if (atStart)
            this.data.addAll(0, results);
        else
            this.data.addAll(results);

        notifyDataSetChanged();
    }
}
