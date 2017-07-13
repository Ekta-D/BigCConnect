package com.bigc.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.models.Feed;
import com.bigc_connect.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class NewsAdapter extends ArrayAdapter<Feed> {

	public static enum SOURCE {
		BBC, CNN, NYT
	};

	private LayoutInflater inflater;
	private static ImageLoaderConfiguration config;

	private static DisplayImageOptions imgDisplayOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(true).cacheOnDisk(true).build();

	private static ImageLoader imageLoader = ImageLoader.getInstance();
	private List<Feed> data;

	public NewsAdapter(Activity context, List<Feed> posts) {
		super(context, R.layout.list_item_news);

		data = new ArrayList<Feed>();
		if (posts != null)
			data.addAll(posts);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		config = new ImageLoaderConfiguration.Builder(context)
				.memoryCacheSize(41943040).diskCacheSize(104857600)
				.threadPoolSize(10).build();

		imageLoader.init(config);
	}

	@Override
	public Feed getItem(int position) {
		if (position < 0 || position >= data.size())
			return null;
		return data.get(position);
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		final Feed feed = data.get(position);
		final ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item_news, parent, false);
			holder = new ViewHolder();
			holder.heading = (TextView) view.findViewById(R.id.titleView);
			holder.descriptionView = (TextView) view
					.findViewById(R.id.descriptionView);
			holder.picView = (ImageView) view.findViewById(R.id.imageView);
			holder.sourceView = (TextView) view.findViewById(R.id.sourceView);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.descriptionView.setText(feed.description);
		holder.heading.setText(feed.title);
		holder.sourceView.setText(feed.source.name());

		imageLoader.displayImage(feed.image, holder.picView, imgDisplayOptions,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						holder.picView.setImageResource(R.drawable.loading_img);
						super.onLoadingStarted(imageUri, view);
					}
				});

		return view;
	}

	public void setData(List<Feed> posts) {
		this.data.clear();
		if (posts == null)
			return;
		this.data.addAll(posts);
		notifyDataSetChanged();
	}

	public void addItems(List<Feed> posts, boolean atStart) {

		if (posts == null)
			return;

		if (atStart)
			this.data.addAll(0, posts);
		else
			this.data.addAll(posts);

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	public static class ViewHolder {

		public TextView heading;
		public TextView descriptionView;
		public ImageView picView;
		public TextView sourceView;
	}
}
