package com.bigc.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.general.classes.Utils;
import com.bigc_connect.R;
import com.model.youtube.Item;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class VideosAdapter extends ArrayAdapter<Item> {

	private List<Item> videos;
	private LayoutInflater inflater;
	private Context context;

	private static ImageLoaderConfiguration config;
	private static DisplayImageOptions imgDisplayOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(true).cacheOnDisk(true).build();
	private static ImageLoader imageLoader = ImageLoader.getInstance();
	private SimpleDateFormat formater;

	public VideosAdapter(Context context, List<Item> videos) {
		super(context, R.layout.list_item_videos);
		this.videos = new ArrayList<Item>();
		this.context = context;
		if (videos != null)
			this.videos.addAll(videos);

		formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		config = new ImageLoaderConfiguration.Builder(context)
				.memoryCacheSize(41943040).diskCacheSize(104857600)
				.threadPoolSize(10).build();
		imageLoader.init(config);
	}

	@Override
	public Item getItem(int position) {
		return this.videos.get(position);
	}

	public void addItems(List<Item> videos) {

		if (videos == null)
			return;

		this.videos.addAll(videos);
		notifyDataSetChanged();
	}

	public void setData(List<Item> videos) {
		this.videos.clear();
		if (videos == null)
			return;
		this.videos.addAll(videos);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return videos.size();
	}

	public String getLastItemDate() {
		return videos.get(videos.size() - 1).getSnippet().getPublishedAt();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item_videos, null);
			holder = new ViewHolder();
			holder.nameView = (TextView) view.findViewById(R.id.titleView);
			holder.descriptionView = (TextView) view
					.findViewById(R.id.descriptionView);
			holder.dateView = (TextView) view.findViewById(R.id.dateView);
			holder.iconView = (ImageView) view.findViewById(R.id.imageView);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		imageLoader.displayImage(videos.get(position).getSnippet()
				.getThumbnails().getHigh().getUrl(), holder.iconView,
				imgDisplayOptions, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						holder.iconView.setImageResource(android.R.color.white);
						super.onLoadingStarted(imageUri, view);
					}
				});

		holder.nameView.setText(videos.get(position).getSnippet().getTitle());
		holder.descriptionView.setText(videos.get(position).getSnippet()
				.getDescription());
		try {
			holder.dateView.setText(Utils.getTimeStringForFeed(
					context,
					formater.parse(videos.get(position).getSnippet()
							.getPublishedAt().replaceAll("Z$", "+0000"))));
		} catch (ParseException e) {
			holder.dateView.setText("");
			e.printStackTrace();
		}
		return view;
	}

	private static class ViewHolder {
		public TextView nameView;
		public TextView descriptionView;
		public TextView dateView;
		public ImageView iconView;
	}
}
