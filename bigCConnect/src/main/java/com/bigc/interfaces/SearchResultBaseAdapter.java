package com.bigc.interfaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.UserConnections;
import com.bigc.general.classes.Utils;
import com.bigc.models.Users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public abstract class SearchResultBaseAdapter extends ArrayAdapter<Users> {

    protected List<Users> data;
    protected LayoutInflater inflater;
    protected List<Users> activeConnections;
    protected List<Users> pendingConnections;
    protected List<Users> newAddedConnections;
    protected List<Users> removedConnections;
    protected boolean isSupporterUser;

    public static final String TAG_CONNECTED = "2";
    public static final String TAG_WAITING = "1";
    public static final String TAG_NOT_CONNECTED = "0";

    protected SearchResultBaseAdapter(Context context, int layout,
                                      List<Users> activeConnections,
                                      List<Users> pendingConnections, List<Users> data) {

        super(context, layout, data);
        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);

        if (activeConnections == null)
            this.activeConnections = new ArrayList<>();
        else
            this.activeConnections = new ArrayList<>(activeConnections);

        if (pendingConnections == null)
            this.pendingConnections = new ArrayList<>();
        else
            this.pendingConnections = new ArrayList<>(
                    pendingConnections);

//		isSupporterUser = ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//				.ordinal();
        isSupporterUser = Preferences.getInstance(context).getInt(DbConstants.TYPE) == Constants.IS_SUPPORTER;

        newAddedConnections = new ArrayList<>();
        removedConnections = new ArrayList<>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Users getItem(int position) {
        return data.get(position);
    }

    public List<Users> getConnections() {
        List<Users> cns = new ArrayList<>();
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
        return cns;
    }

    public List<Users> getData() {
        return data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public void updateData(List<Users> result,
                           List<Users> activeConnections,
                           List<Users> pendingConnections) {
        this.activeConnections.clear();
        this.pendingConnections.clear();

        if (activeConnections != null)
            this.activeConnections.addAll(activeConnections);

        if (pendingConnections != null)
            this.pendingConnections.addAll(pendingConnections);

        updateData(result);
    }

    public void updateConnections(List<Users> activeConnections,
                                  List<Users> pendingConnections) {
        this.activeConnections.clear();
        this.pendingConnections.clear();

        if (activeConnections != null)
            this.activeConnections.addAll(activeConnections);

        if (pendingConnections != null)
            this.pendingConnections.addAll(pendingConnections);

        notifyDataSetChanged();
    }

    public void updateData(Collection<Users> result) {
        data.clear();
        if (result == null)
            return;
        data.addAll(result);

        notifyDataSetChanged();
    }

    public void processUserSettings() {
     /*   Utils.addConnections(new ArrayList<>(newAddedConnections));
        Utils.removeConnections(new ArrayList<>(removedConnections));

        int temp;
        for (Users r : removedConnections) {
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
        removedConnections.clear();*/
    }

    public Date getLastItemDate() {
        if (data.size() == 0)
            return null;
        return Utils.convertStringToDate(data.get(data.size() - 1).getCreatedAt());
    }

    public void addItems(List<Users> results, boolean atStart) {

        if (results == null)
            return;

        if (atStart)
            this.data.addAll(0, results);
        else
            this.data.addAll(results);

        notifyDataSetChanged();
    }
}
