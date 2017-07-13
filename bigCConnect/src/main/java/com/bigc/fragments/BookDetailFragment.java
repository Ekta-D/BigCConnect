package com.bigc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bigc.general.classes.Constants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc_connect.R;
import com.model.books.Item;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class BookDetailFragment extends BaseFragment {

	private static Item book = null;

	private ImageView thumbnailView;
	private RatingBar ratingView;
	private TextView descriptionView;
	private TextView nameView;
	private TextView authorsView;
	private TextView priceView;

	public BookDetailFragment(Item book) {
		BookDetailFragment.book = book;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_book_detail, container,
				false);

		nameView = (TextView) view.findViewById(R.id.nameView);
		descriptionView = (TextView) view.findViewById(R.id.descriptionView);
		ratingView = (RatingBar) view.findViewById(R.id.ratingView);
		authorsView = (TextView) view.findViewById(R.id.authorsView);
		thumbnailView = (ImageView) view.findViewById(R.id.thumbnailView);
		priceView = (TextView) view.findViewById(R.id.priceView);

		view.findViewById(R.id.viewOnGoogleView).setOnClickListener(this);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Book-Detail Screen");

		if (book.getVolumeInfo().getImageLinks() != null)
			ImageLoader.getInstance().displayImage(
					book.getVolumeInfo().getImageLinks().getThumbnail(),
					thumbnailView, Utils.normalDisplayOptions,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							thumbnailView
									.setImageResource(android.R.color.white);
							super.onLoadingStarted(imageUri, view);
						}
					});

		if (book.getSaleInfo().getListPrice() != null)
			priceView.setText(book.getSaleInfo().getListPrice()
					.getCurrencyCode()
					+ " " + book.getSaleInfo().getListPrice().getAmount());
		else
			priceView.setText(book.getSaleInfo().getSaleability());

		nameView.setText(book.getVolumeInfo().getTitle());

		StringBuilder authors = new StringBuilder();
		for (String a : book.getVolumeInfo().getAuthors())
			authors.append(a).append(", ");

		if (authors.length() > 2)
			authors.delete(authors.length() - 2, authors.length());

		authorsView.setText(authors.toString());

		descriptionView.setText(book.getVolumeInfo().getDescription());
		ratingView.setRating(Float.valueOf(String.valueOf(book.getVolumeInfo()
				.getAverageRating())));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.viewOnGoogleView:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"View-Book-On-Google Button");
			Utils.openUrl(getActivity(), book.getVolumeInfo().getInfoLink());
			break;
		}

	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_BOOK_DETAIL;
	}

	@Override
	public int getTab() {
		return 0;
	}

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity()).replaceFragment(new BooksFragment());
		return true;
	}
}
