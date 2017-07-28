package com.bigc.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigc.adapters.VideosAdapter;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.RoboSpiceService;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.requests.FetchVideosRequest;
import com.bigc_connect.R;
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;/*
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;*/
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.model.youtube.Item;
import com.model.youtube.SearchResults;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class VideosFragment extends BaseFragment implements/* OnRefreshListener,*/
		OnLoadMoreListener {

	private static final int REQ_START_STANDALONE_PLAYER = 1;
	private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

	private SpiceManager spiceManager;
	private VideosAdapter adapter;
	private PullAndLoadListView listView;
	private TextView messageView;
	private LinearLayout progressParent;
	private ProgressBar progressView;
	private List<Item> videos = new ArrayList<Item>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		spiceManager = new SpiceManager(RoboSpiceService.class);
		spiceManager.start(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_videos, container, false);
		messageView = (TextView) view.findViewById(R.id.messageView);
		progressView = (ProgressBar) view.findViewById(R.id.progressView);
		progressParent = (LinearLayout) view
				.findViewById(R.id.messageViewParent);
		listView = (PullAndLoadListView) view.findViewById(R.id.listview);
		adapter = new VideosAdapter(getActivity(), videos);
		listView.setAdapter(adapter);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Videos Screen");
		
		
		//listView.setOnRefreshListener(this);
		listView.setOnLoadMoreListener(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				position--;
				Log.e("Clicked on", position + "--");
				playVideo(adapter.getItem(position));
				// startActivity(new Intent(getActivity(),
				// PlayerActivity.class));
			}
		});

		Log.e("Videos", videos.size() + "--");
		if (videos.size() == 0) {
			startProgress();
			makeSearchRequest("");
		}
	}

	private void playVideo(Item video) {
		Intent intent = YouTubeStandalonePlayer.createVideoIntent(
				getActivity(), Constants.YOUTUBE_DEVELOPER_KEY, video.getId()
						.getVideoId(), 0, true, true);
		if (intent != null) {
			if (canResolveIntent(intent)) {
				startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
			} else {
				// Could not resolve the intent - must need to install or update
				// the YouTube API service.
				YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(
						getActivity(), REQ_RESOLVE_SERVICE_MISSING).show();
			}
		}
	}

	private boolean canResolveIntent(Intent intent) {
		List<ResolveInfo> resolveInfo = getActivity().getPackageManager()
				.queryIntentActivities(intent, 0);
		return resolveInfo != null && !resolveInfo.isEmpty();
	}

	private void startProgress() {
		try {
			messageView.setText(R.string.loadingVideos);
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

	private void makeSearchRequest(String lastItemDate) {
		spiceManager.execute(new FetchVideosRequest(lastItemDate),
				new SearchVideosReqListener(lastItemDate.length() != 0));
	}

	public class SearchVideosReqListener implements
			RequestListener<SearchResults> {

		private boolean isMoreLoading;

		private SearchVideosReqListener(boolean isMoreLoading) {
			this.isMoreLoading = isMoreLoading;
		}

		@Override
		public void onRequestFailure(SpiceException e) {
			e.printStackTrace();

		}

		@Override
		public void onRequestSuccess(SearchResults result) {

			List<Item> videos = result.getItems();
			Log.e("Videos", videos.size() + "--");

			if (isMoreLoading) {
				adapter.addItems(videos);
				listView.onLoadMoreComplete();
			} else {
				populateList(videos);
			}
		}
	}

	private void populateList(List<Item> videos) {

		this.videos.clear();
		if (listView != null) {
			if (videos == null) {
				showError(Utils.loadString(getActivity(),
						R.string.networkFailureMessage));
			} else if (videos.size() == 0) {
				showError(Utils.loadString(getActivity(),
						R.string.noFeedMessage));
			} else {
				adapter.setData(videos);
				listView.setVisibility(View.VISIBLE);
				progressParent.setVisibility(View.GONE);
				this.videos.addAll(videos);
			}
			listView.onRefreshComplete();
		}
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_VIDEO;
	}

	@Override
	public int getTab() {
		return 0;
	}

	@Override
	public void onLoadMore() {
		makeSearchRequest(adapter.getLastItemDate());
	}

/*	@Override
	public void onRefresh() {
		makeSearchRequest("");
	}*/

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity()).replaceFragment(new ExploreFragment());
		return true;
	}
}
