package com.bigc.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.adapters.ChatAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.interfaces.MessageObservable;
import com.bigc.interfaces.MessageObserver;
import com.bigc.models.Messages;
import com.bigc.models.Users;
import com.bigc.receivers.NotificationReceiver;
import com.bigc_connect.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class MessageDetailFragment extends BaseFragment implements
        MessageObserver {

    private static Users user = null;
    //	private static Object conversation = null;
    private static Messages conversation = null;
    List<Users> userses;
    private ListView listView;
    private LinearLayout messageViewParent;
    private TextView messageView;
    private ProgressBar progressView;
    private AdView adView;
    private List<Object> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private EditText replyView;
    private static volatile boolean clicked = false;

    //	public MessageDetailFragment(Object conversation) {
//		MessageDetailFragment.conversation = conversation;
//		/*MessageDetailFragment.user = conversation
//				.getParseUser(DbConstants.USER1).getObjectId()
//				.equals(ParseUser.getCurrentUser().getObjectId()) ? conversation
//				.getParseUser(DbConstants.USER2) : conversation
//				.getParseUser(DbConstants.USER1);*/
//	}
    //// TODO: 24-07-2017  full conversation will be shown here but for now only one message in detail showing
    public MessageDetailFragment(Messages conversation, List<Users> userses) {
        this.conversation = conversation;
        this.userses = userses;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        View view = inflater.inflate(R.layout.fragment_messages_detail,
                container, false);
        messageView = (TextView) view.findViewById(R.id.messageView);
        messageViewParent = (LinearLayout) view
                .findViewById(R.id.messageViewParent);
        progressView = (ProgressBar) view.findViewById(R.id.progressView);
        listView = (ListView) view.findViewById(R.id.listview);
        adView = (AdView) view.findViewById(R.id.adView);
        View footerView = ((LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.list_item_reply_layout, listView, false);
        footerView.findViewById(R.id.replyButton).setOnClickListener(this);
        replyView = (EditText) footerView.findViewById(R.id.replyView);
        listView.addFooterView(footerView);

        List<Messages> messageList = new ArrayList<>();
        messageList.add(MessageDetailFragment.conversation);
        adapter = new ChatAdapter(getActivity(),messageList , Preferences.
                getInstance(getActivity()).getAllUsers(DbConstants.FETCH_USER));
        //	adapter = new ChatAdapter(getActivity(),	MessageDetailFragment.conversation);

        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(adapter);
        messageViewParent.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Preferences.getInstance(getActivity())
                .getBoolean(Constants.PREMIUM)) {
            adView.setVisibility(View.GONE);
        }
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "User Message-Thread Screen");

        if (!Preferences.getInstance(getActivity()).getBoolean(
                Constants.PREMIUM)) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        loadData(false);
    }

    private void showError(String message) {
        progressView.setVisibility(View.GONE);
        messageView.setText(message);
    }

    @Override
    public void onClick(View v) {
        if (clicked)
            return;
        clicked = true;

        switch (v.getId()) {
            case R.id.replyButton:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Message-Reply Button");
                if (replyView.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Enter message to reply.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String reply = replyView.getText().toString();
                    replyView.setText("");

				/*ParseObject obj = new ParseObject(DbConstants.

				);
                obj.put(DbConstants.USER1, ParseUser.getCurrentUser());
				obj.put(DbConstants.USER2, user);
				obj.put(DbConstants.MESSAGE, reply);
				obj.put(DbConstants.SENDER, ParseUser.getCurrentUser());
				// TODO: 7/14/2017 send message 
				//PostManager.getInstance().sendMessage(obj, user);
				conversation.put(DbConstants.MESSAGE, reply);
				adapter.addItem(obj);*/
                }
        }
        clicked = false;
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_MESSAGE_DETAIL;
    }

    @Override
    public int getTab() {
        return 0;
    }

    private void loadData(final boolean fromCache) {

		/*ParseQuery<ParseObject> query = Queries.getGroupMessagesQuery(user,
                fromCache);

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(final List<ParseObject> messages, ParseException e) {

				if (e == null) {

					Log.e("Messages", messages.size() + "--");

					if (messages.size() == 0 && fromCache) {
						// Load data from network
						loadData(false);
						return;
					}

					new completeMessageLoadingsTask(messages).execute();

				} else {
					if (!fromCache) {
						if (listView != null) {
							// listView.onRefreshComplete();
							Toast.makeText(
									getActivity(),
									Utils.loadString(getActivity(),
											R.string.networkFailureMessage),
									Toast.LENGTH_LONG).show();
						}
					} else {
						populateList(null);
					}
				}

			}
		});*/
    }

	/*private class completeMessageLoadingsTask extends
            AsyncTask<Void, Void, List<ParseObject>> {

		List<ParseObject> messages;

		public completeMessageLoadingsTask(List<ParseObject> objects) {
			this.messages = new ArrayList<ParseObject>();
			if (objects != null)
				this.messages.addAll(objects);

		}

		@Override
		public List<ParseObject> doInBackground(Void... params) {
			try {
				ParseObject.unpinAll(Constants.TAG_MESSAGES, messages);
				ParseObject.pinAll(Constants.TAG_MESSAGES, messages);
				return messages;
			} catch (ParseException e2) {
				e2.printStackTrace();
				return null;
			}
		}

		@Override
		public void onPostExecute(List<ParseObject> messages) {
			populateList(messages);
		}
	}
*/
/*	private void populateList(List<ParseObject> messages) {

		this.messages.clear();
		if (listView != null) {
			if (messages == null) {
				showError(Utils.loadString(getActivity(),
						R.string.networkFailureMessage));
			} else if (messages.size() == 0) {
				showError(Utils.loadString(getActivity(),
						R.string.noFeedMessage));
			} else {
				adapter.setData(messages);
				listView.setVisibility(View.VISIBLE);
				messageViewParent.setVisibility(View.GONE);
				this.messages.addAll(messages);
			}
			// listView.onRefreshComplete();
		}
	}*/

    private void populateList(List<Messages> messages) {
        if (listView != null) {
            if (messages == null) {
                showError(Utils.loadString(getActivity(),
                        R.string.networkFailureMessage));
            } else if (messages.size() == 0) {
                showError(Utils.loadString(getActivity(),
                        R.string.noFeedMessage));
            } else {
                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
                messageViewParent.setVisibility(View.GONE);
                this.messages.addAll(messages);
            }
            // listView.onRefreshComplete();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //// TODO: 24-07-2017  
        // ((MessageObservable) new NotificationReceiver()).bindObserver(this);
    }

    @Override
    public void onStop() {
        //// TODO: 24-07-2017
        //  ((MessageObservable) new NotificationReceiver()).freeObserver();
        super.onStop();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public boolean onMessageReceive(final Object message, Users user) {
        /*if (listView != null || adapter != null) {
            getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					adapter.addItem(message);
				}
			});
			return true;
		}*/
        return false;
    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity())
                .replaceFragment(new MessagesFragment());
        return true;
    }
}