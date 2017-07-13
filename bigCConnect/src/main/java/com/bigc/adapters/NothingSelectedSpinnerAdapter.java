package com.bigc.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class NothingSelectedSpinnerAdapter implements SpinnerAdapter,
		ListAdapter {

	protected static final int EXTRA = 0;
	protected SpinnerAdapter adapter;
	protected Context context;
	protected int nothingSelectedLayout;
	protected int nothingSelectedDropdownLayout;
	protected LayoutInflater layoutInflater;
	private String default_text;

	public NothingSelectedSpinnerAdapter(SpinnerAdapter spinnerAdapter,
			int nothingSelectedLayout, Context context, String defaultText) {

		this(spinnerAdapter, nothingSelectedLayout, -1, context, defaultText);
	}

	public NothingSelectedSpinnerAdapter(SpinnerAdapter spinnerAdapter,
			int nothingSelectedLayout, int nothingSelectedDropdownLayout,
			Context context, String defaultText) {

		this.default_text = defaultText;
		this.adapter = spinnerAdapter;
		this.context = context;
		this.nothingSelectedLayout = nothingSelectedLayout;
		this.nothingSelectedDropdownLayout = nothingSelectedDropdownLayout;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			convertView = getNothingSelectedView(parent);
			if (default_text != null)
				((TextView) convertView).setText(default_text);
			return convertView;
		}
		return adapter.getView(position - EXTRA, null, parent); // Could re-use
		// the convertView if possible.
	}

	protected View getNothingSelectedView(ViewGroup parent) {
		return layoutInflater.inflate(nothingSelectedLayout, parent, false);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			return nothingSelectedDropdownLayout == -1 ? new View(context)
					: getNothingSelectedDropdownView(parent);
		}

		// Could re-use the convertView if possible, use setTag...
		return adapter.getDropDownView(position - EXTRA, null, parent);
	}

	protected View getNothingSelectedDropdownView(ViewGroup parent) {
		return layoutInflater.inflate(nothingSelectedDropdownLayout, parent,
				false);
	}

	@Override
	public int getCount() {
		int count = adapter.getCount();
		return count == 0 ? 0 : count + EXTRA;
	}

	@Override
	public Object getItem(int position) {
		return position == 0 ? 0 : adapter.getItem(position - EXTRA);
	}

	@Override
	public int getItemViewType(int position) {
		return position == 0 ? getViewTypeCount() - EXTRA : adapter
				.getItemViewType(position - EXTRA);
	}

	@Override
	public int getViewTypeCount() {
		return adapter.getViewTypeCount() + EXTRA;
	}

	@Override
	public long getItemId(int position) {
		return adapter.getItemId(position - EXTRA);
	}

	@Override
	public boolean hasStableIds() {
		return adapter.hasStableIds();
	}

	@Override
	public boolean isEmpty() {
		return adapter.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		adapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		adapter.unregisterDataSetObserver(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return position == 0 ? false : true;
	}

}