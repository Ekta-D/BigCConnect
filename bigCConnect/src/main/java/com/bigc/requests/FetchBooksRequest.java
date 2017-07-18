package com.bigc.requests;

import android.util.Log;

import com.model.books.BooksResponse;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class FetchBooksRequest extends SpringAndroidSpiceRequest<BooksResponse> {

	private int index;

	public FetchBooksRequest(int index) {
		super(BooksResponse.class);

		this.index = index;
	}

	@Override
	public BooksResponse loadDataFromNetwork() throws Exception {

		String url = "https://www.googleapis.com/books/v1/volumes?q="
				+ getQuery()
				+ "&filter=paid-ebooks"
				+ "&orderBy=relevance"
				+ "&startIndex="
				+ index
				+ "&maxResults=40"
				+ "&fields=items(accessInfo(country,viewability),id,kind,saleInfo(buyLink,country,isEbook,listPrice,retailPrice,saleability),volumeInfo(authors,averageRating,canonicalVolumeLink,categories,contentVersion,description,imageLinks,infoLink,language,mainCategory,pageCount,previewLink,printType,printedPageCount,publishedDate,publisher,ratingsCount,readingModes,subtitle,title))"
				+ "&key=AIzaSyBCXkLpK6oUcEYUVleJXgDy24MhjaM69cA";

		Log.e("URL", url);

		BooksResponse value = getRestTemplate().getForObject(url,
				BooksResponse.class);

		return value;
	}

	private String getQuery() {
		/*int ribbon = ParseUser.getCurrentUser().getInt(DbConstants.RIBBON);
		if (ribbon < 0)*/
			return "cancer cure";
		//return Utils.ribbonNames[ribbon].concat(" cure");
	}
}