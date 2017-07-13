package com.bigc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc_connect.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ExploreFragment extends BaseFragment {

	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_explore_layout,
				container, false);
		view.findViewById(R.id.newsParent).setOnClickListener(this);
		view.findViewById(R.id.booksParent).setOnClickListener(this);
		view.findViewById(R.id.videosParent).setOnClickListener(this);
		view.findViewById(R.id.storiesParent).setOnClickListener(this);
		view.findViewById(R.id.tributeParent).setOnClickListener(this);
		adView = (AdView) view.findViewById(R.id.adView);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Explore Screen");

		if (!Preferences.getInstance(getActivity()).getBoolean(
				Constants.SPLASHES, true)) {
			view.findViewById(R.id.splashTextView).setVisibility(View.GONE);
		}

		if (!Preferences.getInstance(getActivity()).getBoolean(
				Constants.PREMIUM)) {
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
		}

		System.gc();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Preferences.getInstance(getActivity())
				.getBoolean(Constants.PREMIUM)) {
			adView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.newsParent:
			GoogleAnalyticsHelper
					.setClickedAction(getActivity(), "News Option");
			((FragmentHolder) getActivity())
					.replaceFragment(new RssFeedFragment());
			break;

		case R.id.booksParent:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Books Option");
			((FragmentHolder) getActivity())
					.replaceFragment(new BooksFragment());
			break;

		case R.id.videosParent:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Videos Option");
			((FragmentHolder) getActivity())
					.replaceFragment(new VideosFragment());
			break;

		case R.id.storiesParent:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Stories Option");
			((FragmentHolder) getActivity())
					.replaceFragment(new FragmentSurvivorStories());
			break;

		case R.id.tributeParent:
			GoogleAnalyticsHelper.setClickedAction(getActivity(),
					"Tribute Option");
			((FragmentHolder) getActivity())
					.replaceFragment(new FragmentTributes());
			break;
		}

	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_EXPLORE;
	}

	@Override
	public int getTab() {
		return 4;
	}

	@Override
	public boolean onBackPressed() {
		((FragmentHolder) getActivity())
				.replaceFragment(new NewsFeedFragment());
		return true;
	}
}
