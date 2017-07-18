package com.bigc.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bigc.general.classes.DbConstants;
import com.bigc_connect.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PhotosAdapter extends ArrayAdapter<Object> {

	private List<Object> data;
	private LayoutInflater inflater;
	private static ImageLoaderConfiguration config;

	private static DisplayImageOptions imgDisplayOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(true).cacheOnDisk(true).build();

	private static ImageLoader imageLoader = ImageLoader.getInstance();

	public PhotosAdapter(Context context, ArrayList<Object> data) {
		super(context, R.layout.row_grid);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.data = new ArrayList<Object>();
		if (data != null)
			this.data.addAll(data);

		config = new ImageLoaderConfiguration.Builder(context)
				.memoryCacheSize(41943040).diskCacheSize(104857600)
				.threadPoolSize(10).build();

		imageLoader.init(config);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	public void setData(List<Object> posts) {
		this.data.clear();
		if (posts == null)
			return;
		this.data.addAll(posts);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		final ViewHolder holder;

		if (row == null) {
			row = inflater.inflate(R.layout.row_grid, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) row.findViewById(R.id.image);
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		/*imageLoader.displayImage(
				data.get(position).getParseFile(DbConstants.MEDIA).getUrl(),
				holder.image, imgDisplayOptions,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						holder.image.setImageResource(R.drawable.loading_img);
						super.onLoadingStarted(imageUri, view);
					}
				});*/

		return row;
	}

	static class ViewHolder {
		ImageView image;
	}
}