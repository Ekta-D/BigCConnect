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
import android.widget.Toast;

import com.bigc.adapters.BooksAdapter;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.RoboSpiceService;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.requests.FetchBooksRequest;
import com.bigc_connect.R;
import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.model.books.BooksResponse;
import com.model.books.Item;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class BooksFragment extends BaseFragment implements OnRefreshListener,
		OnLoadMoreListener {

	private SpiceManager spiceManager;
	private BooksAdapter adapter;
	private PullAndLoadListView listView;
	private TextView messageView;
	private LinearLayout progressParent;
	private ProgressBar progressView;
	private List<Item> books = new ArrayList<Item>();

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
		Toast.makeText(getActivity(), "books frag called", Toast.LENGTH_SHORT).show();
		messageView = (TextView) view.findViewById(R.id.messageView);
		progressView = (ProgressBar) view.findViewById(R.id.progressView);
		progressParent = (LinearLayout) view
				.findViewById(R.id.messageViewParent);
		listView = (PullAndLoadListView) view.findViewById(R.id.listview);
		adapter = new BooksAdapter(getActivity(), books);
		listView.setAdapter(adapter);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Books Screen");

		listView.setOnRefreshListener(this);
		listView.setOnLoadMoreListener(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				((FragmentHolder) getActivity())
						.replaceFragment(new BookDetailFragment(adapter
								.getItem(position - 1)));
			}
		});

		if (books.size() == 0) {
			startProgress();
			makeSearchRequest(0);
		}
	}

	private void startProgress() {
		try {
			messageView.setText(R.string.loadingBooks);
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

	private void makeSearchRequest(int lastItemIndex) {
		spiceManager.execute(new FetchBooksRequest(lastItemIndex),
				new SearchBooksReqListener(lastItemIndex > 0));
	}

	public class SearchBooksReqListener implements
			RequestListener<BooksResponse> {

		private boolean isMoreLoading;

		private SearchBooksReqListener(boolean isMoreLoading) {
			this.isMoreLoading = isMoreLoading;
		}

		@Override
		public void onRequestFailure(SpiceException e) {
			e.printStackTrace();
			if (!isMoreLoading)
				showError("Unable to load books");
			else
				listView.onLoadMoreComplete();
		}

		@Override
		public void onRequestSuccess(BooksResponse result) {

			List<Item> videos = result.getItems();

			if (isMoreLoading) {
				adapter.addItems(videos);
				listView.onLoadMoreComplete();
			} else {
				populateList(videos);
			}
		}
	}

	private void populateList(List<Item> books) {

		this.books.clear();
		if (listView != null) {
			if (books == null) {
				showError(Utils.loadString(getActivity(),
						R.string.networkFailureMessage));
			} else if (books.size() == 0) {
				showError(Utils.loadString(getActivity(),
						R.string.noFeedMessage));
			} else {
				adapter.setData(books);
				listView.setVisibility(View.VISIBLE);
				progressParent.setVisibility(View.GONE);
				this.books.addAll(books);
			}
			listView.onRefreshComplete();
		}
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_BOOKS;
	}

	@Override
	public int getTab() {
		return 0;
	}

	@Override
	public void onLoadMore() {
		makeSearchRequest(adapter.getCount());
	}

	@Override
	public void onRefresh() {
		makeSearchRequest(0);
	}

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity()).replaceFragment(new ExploreFragment());
		return true;
	}

}
