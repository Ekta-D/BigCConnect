package com.bigc.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigc.adapters.NewsAdapter;
import com.bigc.adapters.NewsAdapter.SOURCE;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.RoboSpiceService;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.models.Feed;
import com.bigc.models.Feeds;
import com.bigc.requests.FetchBBCFeedsRequest;
import com.bigc.requests.FetchCNNFeedsRequest;
import com.bigc.requests.FetchNYTFeedsRequest;
import com.bigc_connect.R;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class RssFeedFragment extends BaseFragment implements OnLoadMoreListener {

	private SpiceManager spiceManager;
	private LoadMoreListView listView;
	private NewsAdapter adapter;
	private TextView messageView;
	private LinearLayout progressParent;
	private ProgressBar progressView;

	private List<Feed> feeds = new ArrayList<Feed>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		spiceManager = new SpiceManager(RoboSpiceService.class);
		spiceManager.start(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_rssfeeds, container, false);

		messageView = (TextView) view.findViewById(R.id.messageView);
		progressView = (ProgressBar) view.findViewById(R.id.progressView);
		progressParent = (LinearLayout) view
				.findViewById(R.id.messageViewParent);
		listView = (LoadMoreListView) view.findViewById(R.id.listview);
		adapter = new NewsAdapter(getActivity(), feeds);
		listView.setAdapter(adapter);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"RSS-News Screen");

		listView.setOnLoadMoreListener(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Utils.openUrl(getActivity(), adapter.getItem(position).link);

			}
		});

		if (feeds.size() == 0) {
			startProgress();
			loadBBCFeeds();
		} else if (feeds.size() < 4) {
			listView.onLoadMore();
		}
	}

	@Override
	public void onLoadMore() {
		SOURCE s = adapter.getItem(adapter.getCount() - 1) == null ? SOURCE.BBC
				: adapter.getItem(adapter.getCount() - 1).source;

		if (s == SOURCE.BBC) {
			loadNYTFeeds();
		} else if (s == SOURCE.NYT) {
			loadCNNFeeds();
		} else {
			listView.onLoadMoreComplete();
		}

	}

	private void populateList(List<Feed> feeds) {

		this.feeds.clear();
		if (listView != null) {
			if (feeds == null) {
				showError(Utils.loadString(getActivity(),
						R.string.networkFailureMessage));
			} else if (feeds.size() == 0) {
				showError(Utils.loadString(getActivity(),
						R.string.noFeedMessage));
			} else {
				adapter.setData(feeds);
				listView.setVisibility(View.VISIBLE);
				progressParent.setVisibility(View.GONE);
				this.feeds.addAll(feeds);
				listView.onLoadMoreComplete();
			}
		}
	}

	private void startProgress() {
		try {
			messageView.setText(R.string.loadingNews);
			progressParent.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void showError(String message) {
		listView.setVisibility(View.GONE);
		progressParent.setVisibility(View.VISIBLE);
		progressView.setVisibility(View.GONE);
		messageView.setText(message);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_RSSFEEDS;
	}

	@Override
	public int getTab() {
		return 0;
	}

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity()).replaceFragment(new ExploreFragment());
		return true;
	}

	private void loadBBCFeeds() {
		spiceManager.execute(new FetchBBCFeedsRequest(getActivity()),
				new LoadNewsReqListener(SOURCE.BBC));
	}

	private void loadNYTFeeds() {
		spiceManager.execute(new FetchNYTFeedsRequest(getActivity()),
				new LoadNewsReqListener(SOURCE.NYT));
	}

	private void loadCNNFeeds() {
		spiceManager.execute(new FetchCNNFeedsRequest(getActivity()),
				new LoadNewsReqListener(SOURCE.CNN));
	}

	public class LoadNewsReqListener implements RequestListener<Feeds> {

		private SOURCE source;

		public LoadNewsReqListener(SOURCE source) {
			this.source = source;
		}

		@Override
		public void onRequestFailure(SpiceException e) {
			if (source == SOURCE.NYT && adapter.getCount() == 0) {
				showError("Failed to load Feeds");
			} else {
				onLoadMore();
			}
		}

		@Override
		public void onRequestSuccess(Feeds result) {
			try {
				if (source == SOURCE.BBC) {
					populateList(result.getFeeds());
				} else {
					adapter.addItems(result.getFeeds(), false);
					feeds.addAll(result.getFeeds());
					listView.onLoadMoreComplete();
				}

				if (source != SOURCE.CNN && feeds.size() < 4)
					onLoadMore();

			} catch (Exception e) {

			}
		}
	}
}
