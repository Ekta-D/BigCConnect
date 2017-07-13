package com.bigc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.general.classes.Utils;
import com.bigc_connect.R;

public class RibbonsAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflater;

	public RibbonsAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return Utils.ribbonNames.length;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = inflater.inflate(R.layout.list_item_ribbons_layout, parent,
					false);

		((TextView) view.findViewById(R.id.ribbonSelectionText))
				.setText(Utils.ribbonNames[position]);
		((ImageView) view.findViewById(R.id.ribbonSelectionImage))
				.setImageResource(Utils.survivor_ribbons[position]);

		return view;

	}
}
