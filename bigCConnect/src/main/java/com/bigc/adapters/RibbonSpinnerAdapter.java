package com.bigc.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.Utils;
import com.bigc_connect.R;
import com.parse.ParseUser;

public class RibbonSpinnerAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;
    private boolean isFighter = false;

    public RibbonSpinnerAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		isFighter = ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//				.ordinal();
        isFighter = Preferences.getInstance(context).getInt(DbConstants.TYPE) == 2;

    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getCount() {
        return Utils.ribbonNames.length;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = inflater.inflate(R.layout.ribbon_nothing_selected, parent,
                    false);

        ((TextView) view.findViewById(R.id.text1))
                .setText(Utils.ribbonNames[position]);
        return view;
    }

    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = inflater.inflate(R.layout.ribbon_selection_item_layout,
                    parent, false);

        ((TextView) view.findViewById(R.id.ribbonSelectionText))
                .setText(Utils.ribbonNames[position]);

        if (isFighter) {
            ((ImageView) view.findViewById(R.id.ribbonSelectionImage))
                    .setImageResource(Utils.fighter_ribbons[position]);
        } else {
            ((ImageView) view.findViewById(R.id.ribbonSelectionImage))
                    .setImageResource(Utils.survivor_ribbons[position]);
        }

        return view;

    }
}
