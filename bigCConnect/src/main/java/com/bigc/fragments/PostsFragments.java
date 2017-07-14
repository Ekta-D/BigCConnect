package com.bigc.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.adapters.NewsfeedAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc_connect.R;
import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragments extends BaseFragment implements PopupOptionHandler {

	private NewsfeedAdapter adapter;
	private ListView listview;
	private TextView messageView;
	private MoPubAdAdapter mAdAdapter;
	private RequestParameters mRequestParameters;
	private LinearLayout messageViewParent;
	private ProgressBar progressView;
	private boolean isPremium;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_list, container, false);

		listview = (ListView) view.findViewById(R.id.listview);
		messageView = (TextView) view.findViewById(R.id.messageView);
		messageViewParent = (LinearLayout) view
				.findViewById(R.id.messageViewParent);
		progressView = (ProgressBar) view.findViewById(R.id.progressView);
		adapter = new NewsfeedAdapter(this, null);

		isPremium = Preferences.getInstance(getActivity()).getBoolean(
				Constants.PREMIUM);
		if (!isPremium) {

			ViewBinder viewBinder = new ViewBinder.Builder(
					R.layout.list_item_ad).mainImageId(R.id.newsFeedPicView)
					.iconImageId(R.id.newsFeedRibbonView)
					.titleId(R.id.newsFeedHeading1)
					.textId(R.id.newsFeedMessageView).build();

			// Set up the positioning behavior your ads should have.
			MoPubNativeAdPositioning.MoPubServerPositioning adPositioning = MoPubNativeAdPositioning
					.serverPositioning();
			MoPubStaticNativeAdRenderer adRenderer = new MoPubStaticNativeAdRenderer(
					viewBinder);
			// Set up the MoPubAdAdapter
			mAdAdapter = new MoPubAdAdapter(getActivity(), adapter,
					adPositioning);
			mAdAdapter.registerAdRenderer(adRenderer);

			listview.setAdapter(mAdAdapter);
		} else {
			listview.setAdapter(adapter);
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!isPremium) {

			// Set up your request parameters
			mRequestParameters = new RequestParameters.Builder().keywords(
					"medical, health, cancer, ad").build();

			// Request ads when the user returns to this activity.
			mAdAdapter.loadAds(Constants.MOPUB_UNIT_ID, mRequestParameters);
		}
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (!isPremium) {
			mAdAdapter.destroy();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"User-Posts Screen");

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!isPremium) {
					int temp = mAdAdapter.getOriginalPosition(position);
					if (temp >= 0) {
						position = temp;
					}
				}
			}
		});
		messageView.setText(R.string.loadingFeeds);
		loadUserData();
	}

	private void loadUserData() {
		// TODO: 7/14/2017 loadUserData 
		/*ParseQuery<ParseObject> mQuery = Queries
				.getUserConnectionStatusQuery(ProfileFragment.getUser());
		mQuery.fromPin(Constants.TAG_CONNECTIONS);
		mQuery.getFirstInBackground(new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				boolean isConnected = false;
				if (e == null && object != null
						&& object.getBoolean(DbConstants.STATUS))
					isConnected = true;

				adapter.setClickable(isConnected);
				loadData();
			}
		});*/

	}

	private void loadData() {
		// TODO: 7/14/2017 loadData 
		/*ParseQuery<ParseObject> query = Queries
				.getUserFeedsQuery(ProfileFragment.getUser());

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(final List<ParseObject> posts, ParseException e) {

				if (e == null) {

					new completePostLoadingsTask(posts).execute();

				} else {
					populateList(null);
				}

			}
		});*/
	}

	private class completePostLoadingsTask extends
			AsyncTask<Void, Void, List<ParseObject>> {

		List<ParseObject> posts;

		public completePostLoadingsTask(List<ParseObject> objects) {
			this.posts = new ArrayList<ParseObject>();
			if (objects != null)
				this.posts.addAll(objects);

		}

		@Override
		public List<ParseObject> doInBackground(Void... params) {
			try {
				for (ParseObject p : posts)
					p.getParseUser(DbConstants.USER).fetchIfNeeded();
				return posts;
			} catch (ParseException e2) {
				e2.printStackTrace();
				return null;
			}
		}

		@Override
		public void onPostExecute(List<ParseObject> posts) {
			populateList(posts);
		}
	}

	private void populateList(List<ParseObject> posts) {

		if (listview != null) {
			if (posts == null) {
				showError(Utils.loadString(getActivity(),
						R.string.networkFailureMessage));
			} else if (posts.size() == 0) {
				showError(Utils.loadString(getActivity(),
						R.string.noUpdatesPostedYet));
			} else {
				adapter.setData(posts);
				listview.setVisibility(View.VISIBLE);
				messageViewParent.setVisibility(View.GONE);
			}
		}
	}

	private void showError(String message) {
		listview.setVisibility(View.GONE);
		messageViewParent.setVisibility(View.VISIBLE);
		progressView.setVisibility(View.GONE);
		messageView.setText(message);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_POSTS;
	}

	@Override
	public int getTab() {
		return 0;
	}

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity()).replaceFragment(new ProfileFragment(
				null, null));
		return true;
	}

	@Override
	public void onDelete(int position, ParseObject post) {

	}

	@Override
	public void onEditClicked(int position, ParseObject post) {
		ParseObject obj = post == null ? adapter.getItem(position) : post;
		Utils.launchEditView(
				getActivity(),
				obj.getParseFile(DbConstants.MEDIA) == null ? Constants.OPERATION_MESSAGE
						: Constants.OPERATION_PHOTO, position, obj);
	}

	@Override
	public void onFlagClicked(int position, ParseObject post) {
		if (post == null) {
			post = adapter.getItem(position);
		}
		if (post != null) {
			Utils.flagFeed(post);
		}
		Toast.makeText(getActivity(),
				getResources().getString(R.string.postFlagMessage),
				Toast.LENGTH_SHORT).show();
	}
}
