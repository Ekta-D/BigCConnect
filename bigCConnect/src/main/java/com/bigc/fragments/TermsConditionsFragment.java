package com.bigc.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigc.general.classes.Constants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc_connect.R;

public class TermsConditionsFragment extends BaseFragment {

	private static BaseFragment caller = null;

	public TermsConditionsFragment(BaseFragment caller) {
		TermsConditionsFragment.caller = caller;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_terms_conditions,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
				"Terms-Conditions Screen");

		TextView textview = (TextView) view.findViewById(R.id.textView);
		// textview.setMovementMethod(new ScrollingMovementMethod());
		textview.setText(Html.fromHtml(Utils.loadString(getActivity(),
				R.string.termsConditions)));
		view.findViewById(R.id.okayButton).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.okayButton:
			onBackPressed();
			break;
		}
	}

	@Override
	public String getName() {
		return Constants.FRAGMENT_TERMS;
	}

	@Override
	public int getTab() {
		return 5;
	}

	@Override
	public boolean onBackPressed() {
		if (caller != null)
			((FragmentHolder) getActivity()).replaceFragment(caller);
		else
			((FragmentHolder) getActivity())
					.replaceFragment(new ProfileFragment(null, null));
		return true;
	}

}
