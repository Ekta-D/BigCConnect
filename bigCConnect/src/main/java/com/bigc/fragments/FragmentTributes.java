package com.bigc.fragments;

import android.os.AsyncTask;
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
import android.widget.Toast;

import com.bigc.adapters.TributesAdapter;
import com.bigc.dialogs.AddTributeDialog;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.PostManager;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc.interfaces.UploadPostObserver;
import com.bigc_connect.R;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//public class FragmentTributes extends BaseFragment implements
//		OnLoadMoreListener, UploadPostObserver, PopupOptionHandler  //// TODO: 14-07-2017
public class FragmentTributes extends BaseFragment implements
		OnLoadMoreListener, UploadPostObserver
{

	private LoadMoreListView listView;
	private TributesAdapter adapter;
	private TextView messageView;
	private LinearLayout progressParent;
	private ProgressBar progressView;

	private List<ParseObject> posts = new ArrayList<ParseObject>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tribunes, container,
				false);

		view.findViewById(R.id.leftOptionParent).setOnClickListener(this);
		messageView = (TextView) view.findViewById(R.id.messageView);
		progressView = (ProgressBar) view.findViewById(R.id.progressView);
		progressParent = (LinearLayout) view
				.findViewById(R.id.messageViewParent);
		listView = (LoadMoreListView) view.findViewById(R.id.listview);
		adapter = new TributesAdapter(this, posts);
		listView.setAdapter(adapter);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Tributes Screen");

		listView.setOnLoadMoreListener(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//// TODO: 14-07-2017
//
//				Log.e("Clicked on", position + "--");
//				((FragmentHolder) getActivity())
//						.replaceFragment(new FragmentTributeDetail(
//								FragmentTributes.this, adapter
//										.getItem(position), position));
			}
		});

		Log.e("Posts", posts.size() + "--");
		if (posts.size() == 0) {
			startProgress();
			loadData();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		PostManager.getInstance().addTributeObserver(this);
	}

	@Override
	public void onDestroy() {
		PostManager.getInstance().removeTributeObserver();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftOptionParent:
			new AddTributeDialog(getActivity(), this).show();
			break;
		}

	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_TRIBUTES;
	}

	@Override
	public int getTab() {
		return 4;
	}

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity()).replaceFragment(new ExploreFragment());
		return true;
	}

	@Override
	public void onLoadMore() {

		Log.e("LoadMore", "Request");
		loadPosts(adapter.getLastItemDate(), false);
	}

	private void loadData() {

		ParseQuery<ParseObject> query = Queries.getTributesQuery();

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(final List<ParseObject> posts, ParseException e) {

				if (e == null) {

					Log.e("Posts", posts.size() + "--");

					new completePostLoadingsTask(posts, false, false).execute();

				} else {
					if (listView != null) {
						listView.onLoadMoreComplete();
						Toast.makeText(
								getActivity(),
								Utils.loadString(getActivity(),
										R.string.networkFailureMessage),
								Toast.LENGTH_LONG).show();
					}
				}

			}
		});
	}

	private void startProgress() {
		try {
			messageView.setText(R.string.loadingTributes);
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
	public void onNotify(final ParseObject post) {
		if (post == null) {
			Toast.makeText(getActivity(), "Upload status is failed, try again",
					Toast.LENGTH_LONG).show();
			return;
		}

		try {
			post.getParseUser(DbConstants.USER).fetchIfNeeded();
			post.pin(Constants.TAG_TRIBUTES);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		posts.add(0, post);

		if (listView == null)
			return;

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					adapter.addItem(post);
					if (listView.getVisibility() == View.GONE) {
						listView.setVisibility(View.VISIBLE);
						progressParent.setVisibility(View.GONE);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void loadPosts(Date from, final boolean recent) {
		ParseQuery<ParseObject> query = Queries.getTributesQuery();

		if (recent)
			query.whereGreaterThan(DbConstants.CREATED_AT, from);
		else
			query.whereLessThan(DbConstants.CREATED_AT, from);

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(final List<ParseObject> posts, ParseException e) {

				if (e == null) {

					new completePostLoadingsTask(posts, true, recent).execute();
				} else {
					listView.onLoadMoreComplete();
					if (e.getCode() == ParseException.CONNECTION_FAILED) {
						populateList(null);
					} else {
						populateList(new ArrayList<ParseObject>());
					}
				}

			}
		});
	}

	private class completePostLoadingsTask extends
			AsyncTask<Void, Void, List<ParseObject>> {

		private List<ParseObject> posts;
		private boolean isMoreLoading;
		private boolean isRecent;

		public completePostLoadingsTask(List<ParseObject> objects,
				boolean isMoreLoading, boolean isRecent) {
			this.posts = new ArrayList<ParseObject>();
			if (objects != null)
				this.posts.addAll(objects);

			this.isMoreLoading = isMoreLoading;
			this.isRecent = isRecent;
		}

		@Override
		public List<ParseObject> doInBackground(Void... params) {
			try {
				ParseObject.unpinAll(Constants.TAG_TRIBUTES, posts);
				for (ParseObject p : posts) {
					p.getParseUser(DbConstants.USER).fetchIfNeeded();
					p.getParseUser(DbConstants.TO).fetchIfNeeded();
				}
				ParseObject.pinAll(Constants.TAG_TRIBUTES, posts);
				return posts;
			} catch (ParseException e2) {
				e2.printStackTrace();
				return null;
			}
		}

		@Override
		public void onPostExecute(List<ParseObject> posts) {
			if (isMoreLoading) {
				Log.e("new posts", posts.size() + "--");
				adapter.addItems(posts, isRecent);
				if (!isRecent)
					listView.onLoadMoreComplete();
			} else {
				populateList(posts);
			}
		}
	}

	private void populateList(List<ParseObject> posts) {

		this.posts.clear();
		if (listView != null) {
			if (posts == null) {
				showError(Utils.loadString(getActivity(),
						R.string.networkFailureMessage));
			} else if (posts.size() == 0) {
				showError(Utils.loadString(getActivity(),
						R.string.noTributeMessage));
			} else {
				adapter.setData(posts);
				listView.setVisibility(View.VISIBLE);
				progressParent.setVisibility(View.GONE);
				this.posts.addAll(posts);
			}
			listView.onLoadMoreComplete();
		}
	}
	//// TODO: 14-07-2017

//	@Override
//	public void onDelete(int position, ParseObject post) {
//		if (position >= 0 && position < posts.size())
//			posts.remove(position);
//	}
//
//	@Override
//	public void onEditClicked(int position, ParseObject post) {
//		Log.e("onEditClicked", "Done");
//		ParseObject obj = post == null ? adapter.getItem(position) : post;
//		Utils.launchEditView(getActivity(), Constants.OPERATION_TRIBUTE,
//				position, obj);
//	}

	@Override
	public void onEditDone(int position, ParseObject post) {
		Log.e(FragmentTributes.class.getSimpleName(), "onEditDone");
		//adapter.updateItem(position, post);//// TODO: 14-07-2017
	}
//// TODO: 14-07-2017
//	@Override
//	public void onFlagClicked(int position, ParseObject post) {
//		if (post == null) {
//			post = adapter.getItem(position);
//		}
//		if (post != null) {
//			Utils.flagTribute(post);
//		}
//		Toast.makeText(getActivity(),
//				getResources().getString(R.string.tributeFlagMessage),
//				Toast.LENGTH_SHORT).show();
//	}
}