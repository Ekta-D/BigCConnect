package com.bigc.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bigc_connect.R;
import com.model.books.Item;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class BooksAdapter extends ArrayAdapter<Item> {

	private List<Item> books;
	private LayoutInflater inflater;

	private static ImageLoaderConfiguration config;
	private static DisplayImageOptions imgDisplayOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(true).cacheOnDisk(true).build();
	private static ImageLoader imageLoader = ImageLoader.getInstance();

	public BooksAdapter(Context context, List<Item> books) {
		super(context, R.layout.list_item_books);
		this.books = new ArrayList<Item>();
		if (books != null)
			this.books.addAll(books);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		config = new ImageLoaderConfiguration.Builder(context)
				.memoryCacheSize(41943040).diskCacheSize(104857600)
				.threadPoolSize(10).build();
		imageLoader.init(config);
	}

	@Override
	public Item getItem(int position) {
		return this.books.get(position);
	}

	public void addItems(List<Item> books) {

		if (books == null)
			return;

		this.books.addAll(books);
		notifyDataSetChanged();
	}

	public void setData(List<Item> books) {
		this.books.clear();
		if (books == null)
			return;
		this.books.addAll(books);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return books.size();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item_books, null);
			holder = new ViewHolder();
			holder.rateView = (RatingBar) view.findViewById(R.id.ratingView);
			holder.nameView = (TextView) view.findViewById(R.id.titleView);
			holder.descriptionView = (TextView) view
					.findViewById(R.id.descriptionView);
			holder.priceView = (TextView) view.findViewById(R.id.priceView);
			holder.iconView = (ImageView) view.findViewById(R.id.imageView);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Item book = books.get(position);

		if (book.getVolumeInfo().getImageLinks() != null)
			imageLoader.displayImage(book.getVolumeInfo().getImageLinks()
					.getThumbnail(), holder.iconView, imgDisplayOptions,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.iconView
									.setImageResource(android.R.color.white);
							super.onLoadingStarted(imageUri, view);
						}
					});

		holder.nameView.setText(book.getVolumeInfo().getTitle());
		StringBuilder authors = new StringBuilder();

		for (String a : book.getVolumeInfo().getAuthors())
			authors.append(a).append(", ");

		if (authors.length() > 2)
			authors.delete(authors.length() - 2, authors.length());

		holder.descriptionView.setText(authors.toString());

		if (book.getSaleInfo().getListPrice() != null)
			holder.priceView.setText(books.get(position).getSaleInfo()
					.getListPrice().getCurrencyCode()
					+ " " + book.getSaleInfo().getListPrice().getAmount());
		else
			holder.priceView.setText(book.getSaleInfo().getSaleability());

		holder.rateView.setRating(Float.valueOf(String.valueOf(books
				.get(position).getVolumeInfo().getAverageRating())));

		return view;
	}

	private static class ViewHolder {
		public RatingBar rateView;
		public TextView nameView;
		public TextView descriptionView;
		public TextView priceView;
		public ImageView iconView;
	}
}
