package com.bigc.requests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

import com.bigc.adapters.NewsAdapter.SOURCE;
import com.bigc.datastorage.Preferences;
import com.bigc.models.Feed;
import com.bigc.models.Feeds;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class FetchBBCFeedsRequest extends SpringAndroidSpiceRequest<Feeds> {

	private Context context;

	public FetchBBCFeedsRequest(Context context) {
		super(Feeds.class);

		this.context = context;
	}

	@Override
	public Feeds loadDataFromNetwork() throws Exception {

		String url = "http://feeds.bbci.co.uk/news/health/rss.xml";

		List<Feed> feeds = new ArrayList<Feed>();
		try {

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String value = null;
			response = httpclient.execute(new HttpGet(url));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				value = out.toString();

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();

				InputStream is = new ByteArrayInputStream(value.getBytes());
				xpp.setInput(is, HTTP.UTF_8);
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {

					if (eventType == XmlPullParser.START_TAG
							&& xpp.getName().equals("item")) {

						eventType = xpp.next();
						Feed feed = new Feed(SOURCE.BBC);
						while (eventType != XmlPullParser.END_TAG
								|| !"item".equals(xpp.getName())) {
							if (eventType == XmlPullParser.START_TAG
									&& xpp.getName().equals("title")) {
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
									feed.title = xpp.getText();

							} else if (eventType == XmlPullParser.START_TAG
									&& xpp.getName().equals("link")) {
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
									feed.link = xpp.getText();

							} else if (eventType == XmlPullParser.START_TAG
									&& xpp.getName().equals("description")) {
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
									feed.description = xpp.getText();

							} else if (eventType == XmlPullParser.START_TAG
									&& "thumbnail".equals(xpp.getName())) {
								feed.image = xpp.getAttributeValue(2);

							}
							eventType = xpp.next();
						}
						if ((feed.title != null && feed.title.toLowerCase()
								.contains("cancer"))
								|| (feed.description != null && feed.description
										.toLowerCase().contains("cancer")))
							feeds.add(feed);
					} else {
						eventType = xpp.next();
					}
				}
			} else {
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Feeds oldData = Preferences.getInstance(context).getFeeds("bbc");
		Log.e("BBC Feeds Size", feeds.size() + " -- "
				+ (oldData == null ? "-1" : oldData.getFeeds().size()));
		for (int i = 0; i < feeds.size(); i++) {
			if (oldData.getFeeds().contains(feeds.get(i))) {
				feeds.remove(i);
				i--;
				Log.e("Removed", "Feed");
			}
		}
		if (feeds.size() > 0) {
			oldData.getFeeds().addAll(0, feeds);
			feeds.clear();
			feeds.addAll(oldData.getFeeds());
			oldData.setFeeds(feeds.size() > 20 ? feeds.subList(0, 20) : feeds);
			Preferences.getInstance(context).saveFeeds(oldData, "bbc");
		}
		return oldData;
	}
}