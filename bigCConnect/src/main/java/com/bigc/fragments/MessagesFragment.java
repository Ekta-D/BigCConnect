package com.bigc.fragments;

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

import com.bigc.activities.PostActivity;
import com.bigc.adapters.MessagesAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.PostManager;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.interfaces.MessageObservable;
import com.bigc.interfaces.MessageObserver;
import com.bigc.interfaces.UploadPostObserver;
import com.bigc.models.Messages;
import com.bigc.models.Posts;
import com.bigc.models.Tributes;
import com.bigc.models.Users;
import com.bigc.receivers.NotificationReceiver;
import com.bigc_connect.R;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends BaseFragment implements
        OnRefreshListener, UploadPostObserver, OnLoadMoreListener,
        MessageObserver {

    private AdView adView;
//    private MessagesAdapter adapter;
    private PullToRefreshListView listView;
    private TextView messageView;
    private LinearLayout progressParent;
    private ProgressBar progressView;

    private List<Messages> messages = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_layout,
                container, false);
        adView = (AdView) view.findViewById(R.id.adView);
        view.findViewById(R.id.leftBottomOption).setOnClickListener(this);

        messageView = (TextView) view.findViewById(R.id.messageView);
        progressView = (ProgressBar) view.findViewById(R.id.progressView);
        progressParent = (LinearLayout) view
                .findViewById(R.id.messageViewParent);
        listView = (PullToRefreshListView) view.findViewById(R.id.listview);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "Messages Screen");

        if (!Preferences.getInstance(getActivity()).getBoolean(
                Constants.SPLASHES, true)) {
            view.findViewById(R.id.splashTextView).setVisibility(View.GONE);
        }

        listView.setOnRefreshListener(this);
        // listView.setOnLoadMoreListener(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				position--;
//				((FragmentHolder) getActivity())
//						.replaceFragment(new MessageDetailFragment(adapter
//								.getItem(position)));
//			}
//		});


        Log.e("Posts", messages.size() + "--");
        if (messages.size() == 0) {
            startProgress();
            loadData(true);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (Preferences.getInstance(getActivity())
                .getBoolean(Constants.PREMIUM)) {
            adView.setVisibility(View.GONE);
        }
        if (PostActivity.selectedUsers_array != null) {
            Log.i("uses's message", PostActivity.selectedUsers_array.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        PostManager.getInstance().bindObserver(this);
        PostManager.getInstance().addObserver(this);
        //  ((MessageObservable) new NotificationReceiver()).bindObserver(this); //// TODO: 7/18/2017
    }

    @Override
    public void onStop() {
        //  ((MessageObservable) new NotificationReceiver()).freeObserver();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        PostManager.getInstance().freeObserver();
        PostManager.getInstance().removePostObserver();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.leftBottomOption:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "New Message Button");
                Utils.launchPostView(getActivity(), Constants.OPERATION_MESSAGE);
                break;
        }
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_MESSAGES;
    }

    @Override
    public int getTab() {
        return 3;
    }

    @Override
    public void onRefresh() {
        /*loadData(false);*/
    }

    @Override
    public void onLoadMore() {

        Log.e("LoadMore", "Request");
        // loadPosts(adapter.getLastItemDate(), false);
    }

    List<Messages> messages_list;

    private void loadData(final boolean fromCache) {

    /*    final List<Query> queries = Queries.getConversationsQuery(fromCache);

        queries.get(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        Messages message = messageSnapshot.getValue(Messages.class);
                        messages.add(message);
                    }

                    executeReceiveQuery(queries.get(2));
                } else {
                    Toast.makeText(
                            getActivity(),
                            Utils.loadString(getActivity(),
                                    R.string.networkFailureMessage),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/ // TODO: 21-07-2017 need to work on Converation table

        Query query = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_MESSAGE);
        query.orderByChild(DbConstants.USER1).equalTo(Preferences.getInstance(getActivity()).getString(DbConstants.ID));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long count = dataSnapshot.getChildrenCount();
                messages_list = new ArrayList<Messages>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    Messages message = data.getValue(Messages.class);
                    messages_list.add(message);
                }

                populateList(messages_list);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showError(databaseError.toString());
            }
        });

		/*ParseQuery<ParseObject> query = Queries
                .getConversationsQuery(fromCache);

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

					new completeMessageLoadingsTask(messages, false, false)
							.execute();

				} else {
					if (!fromCache) {
						if (listView != null) {
							listView.onRefreshComplete();
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

    private void executeReceiveQuery(Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        Messages message = messageSnapshot.getValue(Messages.class);
                        messages.add(message);
                    }

                }
                /*if (messages.size() == 0) {
                    // Load data from network
					loadData(false);
					return;
				}*/

				/*new completeMessageLoadingsTask(messages, false, false)
                        .execute();*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

	/*private class completeMessageLoadingsTask extends
            AsyncTask<Void, Void, List<ParseObject>> {

		List<ParseObject> messages;
		boolean isMoreLoading;
		boolean isRecent;

		public completeMessageLoadingsTask(List<ParseObject> objects,
				boolean isMoreLoading, boolean isRecent) {
			this.messages = new ArrayList<ParseObject>();
			if (objects != null)
				this.messages.addAll(objects);

			this.isMoreLoading = isMoreLoading;
			this.isRecent = isRecent;
		}

		@Override
		public List<ParseObject> doInBackground(Void... params) {
			try {
				ParseObject.unpinAll(Constants.TAG_MESSAGES, messages);

				for (ParseObject m : messages) {
					if (ParseUser
							.getCurrentUser()
							.getObjectId()
							.equals(m.getParseUser(DbConstants.USER1)
									.getObjectId())) {
						m.getParseUser(DbConstants.USER2).fetchIfNeeded();
					} else {
						m.getParseUser(DbConstants.USER1).fetchIfNeeded();
					}
				}

				ParseObject.pinAll(Constants.TAG_MESSAGES, messages);
				return messages;
			} catch (ParseException e2) {
				e2.printStackTrace();
				return null;
			}
		}

		@Override
		public void onPostExecute(List<ParseObject> messages) {
			if (isMoreLoading) {
				Log.e("new messages", messages.size() + "--");
				adapter.addItems(messages, isRecent);
				// if (!isRecent)
				// listView.onLoadMoreComplete();
			} else {
				populateList(messages);
			}
		}
	}*/

    private void populateList(List<Messages> messages) {

        if (messages == null) {
            showError(Utils.loadString(getActivity(),
                    R.string.networkFailureMessage));
        } else if (messages.size() == 0) {
            showError("No messages sent or received yet.");
        } else {

            final MessagesAdapter message_adapter = new MessagesAdapter(getActivity(), messages, Preferences.getInstance(getActivity()).getAllUsers(DbConstants.FETCH_USER));
            listView.setAdapter(message_adapter);
            listView.setVisibility(View.VISIBLE);
            progressParent.setVisibility(View.GONE);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    position--;
                    ((FragmentHolder) getActivity())
                            .replaceFragment(new MessageDetailFragment(message_adapter
                                    .getItem(position), Preferences.getInstance(getActivity()).getAllUsers(DbConstants.FETCH_USER)));
                }
            });
        }

//        this.messages.clear();
//        if (listView != null) {
//            if (messages == null) {
//                showError(Utils.loadString(getActivity(),
//                        R.string.networkFailureMessage));
//            } else if (messages.size() == 0) {
//                showError("No messages sent or received yet.");
//            } else {
//                //adapter.setData(messages);
//                listView.setVisibility(View.VISIBLE);
//                progressParent.setVisibility(View.GONE);
//                this.messages.addAll(messages);
//            }
//            listView.onRefreshComplete();
//        }


    }

    private void startProgress() {
        try {
            messageView.setText(R.string.loadingMessages);
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
    public void onNotify(final Tributes post) {
        if (post == null) {
            Toast.makeText(getActivity(), "Upload status is failed, try again",
                    Toast.LENGTH_LONG).show();
            return;
        }

		/*try {
            post.getParseUser(DbConstants.USER).fetchIfNeeded();
			post.pin(Constants.TAG_MESSAGES);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/

        if (messages != null)
            //messages.add(0, post);

            if (listView == null)
                return;

	/*	getActivity().runOnUiThread(new Runnable() {

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
		});*/
    }

    @Override
    public boolean onMessageReceive(Object message, Users sender) {
        Log.e("onMessageReceive", "Invoked");
        /*if (adapter != null && listView != null) {
            final List<ParseObject> data = new ArrayList<ParseObject>();
			data.addAll(adapter.getData());
			String senderId = sender == null ? message.getParseUser(
					DbConstants.SENDER).getObjectId() : sender.getObjectId();
			boolean found = false;
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getParseUser(DbConstants.USER1).getObjectId()
						.equals(senderId)
						|| data.get(i).getParseUser(DbConstants.USER2)
								.getObjectId().equals(senderId)) {
					Log.e("Object", "Found");
					found = true;
					ParseObject obj = data.get(i);
					obj.put(DbConstants.MESSAGE,
							message.getString(DbConstants.MESSAGE));
					obj.put(DbConstants.IS_READ, false);
					data.remove(i);
					data.add(0, obj);
				}
			}
			if (!found) {
				Log.e("New", "Add");
				ParseObject o = new ParseObject(DbConstants.TABLE_CONVERSATION);
				o.put(DbConstants.MESSAGE,
						message.getString(DbConstants.MESSAGE));

				o.put(DbConstants.USER1,
						message.getParseUser(DbConstants.USER1));
				o.put(DbConstants.USER2,
						message.getParseUser(DbConstants.USER2));
				data.add(0, o);
			}
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Log.e("Adapter", "Updated -- " + data.size());
					adapter.setData(data);
					if (data.size() == 1) {
						listView.setVisibility(View.VISIBLE);
						progressParent.setVisibility(View.GONE);
					}
				}
			});
			return true;
		}*/
        return false;
    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity())
                .replaceFragment(new NewsFeedFragment());
        return true;
    }

    @Override
    public void onEditDone(int position, Posts post) {
        Log.e(MessagesFragment.class.getSimpleName(), "onEditDone");
    }
}
