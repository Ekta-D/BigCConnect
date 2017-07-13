package com.bigc.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SimpleSpinnerAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflater;
	private List<String> data;

	public SimpleSpinnerAdapter(Context context, List<String> data) {
		super(context, android.R.layout.simple_spinner_dropdown_item);

		this.data = new ArrayList<String>();
		if (data != null)
			this.data.addAll(data);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = inflater.inflate(android.R.layout.simple_list_item_1,
					parent, false);

		((TextView) view).setText(data.get(position));
		return view;
	}

	public View getDropDownView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = inflater.inflate(android.R.layout.simple_list_item_1,
					parent, false);

		((TextView) view).setText(data.get(position));
		return view;

	}
}
