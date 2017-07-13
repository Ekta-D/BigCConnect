package com.bigc.requests;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.Utils;
import com.model.youtube.SearchResults;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.parse.ParseUser;

public class FetchVideosRequest extends
		SpringAndroidSpiceRequest<SearchResults> {

	private String date;

	private final static List<String> restrictedWords = new ArrayList<String>();

	static {
		restrictedWords.add("sex");
		restrictedWords.add("nude");
		restrictedWords.add("porn");
		restrictedWords.add("funny");
		restrictedWords.add("comedy");
		restrictedWords.add("kidding");
	}

	public FetchVideosRequest(String publishedAfter) {
		super(SearchResults.class);

		this.date = publishedAfter;
	}

	@Override
	public SearchResults loadDataFromNetwork() throws Exception {

		String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="
				+ getQuery()
				+ "&type=video&videoCaption=any&order=date&"
				+ (date.length() > 0 ? "publishedBefore=".concat(date).concat(
						"&") : "")
				+ "maxResults=20&key="
				+ Constants.GOOGLE_BROWSER_KEY;

		Log.e("URL", url);
		SearchResults value = getRestTemplate().getForObject(url,
				SearchResults.class);

		if (date.length() > 0)
			value.getItems().remove(0);

		for (int i = 0; i < value.getItems().size(); i++) {
			for (String type : restrictedWords) {
				if (value.getItems().get(i).getSnippet().getTitle()
						.contains(type)
						|| value.getItems().get(i).getSnippet()
								.getDescription().contains(type)
						|| value.getItems().get(i).getSnippet()
								.getChannelTitle().contains(type)) {
					value.getItems().remove(i);
					i--;
					continue;
				}
			}
		}
		return value;
	}

	private String getQuery() {
		int ribbon = ParseUser.getCurrentUser().getInt(DbConstants.RIBBON);
		if (ribbon < 0)
			return "cancer news";
		return Utils.ribbonNames[ribbon].concat(" news");
	}
}
