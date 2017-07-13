package com.bigc.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.general.classes.Utils;
import com.bigc_connect.R;

public class RibbonsGridAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflater;
	private static List<Integer> colors;
	private Resources res;

	static {
		colors = new ArrayList<Integer>();
		colors.add(R.color.bg_lung);
		colors.add(R.color.bg_breast);
		colors.add(R.color.bg_breast);
		colors.add(R.color.bg_prostate);
		colors.add(R.color.bg_colon);
		colors.add(R.color.bg_leukemia);
		colors.add(R.color.bg_liver);
		colors.add(R.color.bg_nh_lymphoma);
		colors.add(R.color.bg_testicular);
		colors.add(R.color.bg_stomach);
		colors.add(R.color.bg_brain);
		colors.add(R.color.bg_kidney);
		colors.add(R.color.bg_melanoma);
		colors.add(R.color.bg_cervical);
		colors.add(R.color.bg_h_lymphoma);
		colors.add(R.color.bg_bone);
		colors.add(R.color.bg_childhood);
		colors.add(R.color.bg_bladder);
		colors.add(R.color.bg_thyroid);
		colors.add(R.color.bg_pancreatic);
	}

	public RibbonsGridAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		res = context.getResources();
	}

	@Override
	public int getCount() {
		return Utils.ribbonNames.length;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.grid_item_ribbons_layout, parent,
					false);
			holder = new ViewHolder();
			holder.textView = (TextView) view.findViewById(R.id.textView);
			holder.imageView = (ImageView) view.findViewById(R.id.imageView);
			view.setTag(holder);
		} else
			holder = (ViewHolder) view.getTag();

		holder.textView.setText(Utils.ribbonNames[position]
				.concat(" Fighters & Survivors"));
		holder.imageView.setImageResource(Utils.survivor_ribbons[position]);
		view.setBackgroundColor(res.getColor(colors.get(position)));

		return view;
	}

	static class ViewHolder {
		public TextView textView;
		public ImageView imageView;
	}
}
