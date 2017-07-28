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
import com.bigc.models.Comments;
import com.bigc.models.Messages;
import com.bigc.models.Post;
import com.bigc.models.Posts;
import com.bigc.models.Tributes;
import com.bigc.models.Users;
import com.bigc.receivers.NotificationReceiver;
import com.bigc_connect.R;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView;/*
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;*/
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.janmuller.android.simplecropimage.Util;

public class MessagesFragment extends BaseFragment implements
        /*OnRefreshListener,*/ UploadPostObserver, OnLoadMoreListener,
        MessageObserver {

    private AdView adView;
    private MessagesAdapter adapter;
    private PullToRefreshListView listView;
    private TextView messageView;
    private LinearLayout progressParent;
    private ProgressBar progressView;
    DatabaseReference databaseReference;
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
        /*adapter = new MessagesAdapter(getActivity(), messages);
        listView.setAdapter(adapter);
*/
        databaseReference = FirebaseDatabase.getInstance().getReference();
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

        //listView.setOnRefreshListener(this);
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


        if (PostActivity.currentMessageObject != null &&
                PostActivity.selectedUsers_array.size() > 0 &&
                !PostActivity.message.equalsIgnoreCase("")) {
            Log.i("currentMessageObject", PostActivity.currentMessageObject.toString());
            uploadMessage(PostActivity.currentMessageObject, PostActivity.selectedUsers_array, messages_list);
        }
    }

    boolean found = false;

    private void uploadMessage(Messages messages, ArrayList<Users> selectedUsers, final List<Messages> messages_list) {
        String conversationObjectId;
        String messageId = "";

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentUserName = Preferences.getInstance(getContext()).getString(DbConstants.NAME);
        for (int i = 0; i < selectedUsers.size(); i++) {
            Users user = selectedUsers.get(i);
            //    String objectId = databaseReference.child(DbConstants.TABLE_MESSAGE).push().getKey();

            messageId = UUID.randomUUID().toString();
            conversationObjectId = UUID.randomUUID().toString();
            messages.setUser1(currentUserId);
            messages.setSender(currentUserId);
            messages.setUser2(user.getObjectId());
            messages.setObjectId(messageId);
            Messages mess = null;
            String senderId = messages.getSender() == null ? messages.getSender() : messages.getUser1();
            for (int j = 0; j < messages_list.size(); j++) {
                mess = messages_list.get(j);

                System.out.println("comdtidion: " + mess.getUser1().equalsIgnoreCase(senderId) + mess.getUser2().equalsIgnoreCase(senderId)
                        + mess.getUser1().equalsIgnoreCase(user.getObjectId()) + mess.getUser2().equalsIgnoreCase(user.getObjectId()));
                if ((mess.getUser1().equalsIgnoreCase(senderId) || mess.getUser2().equalsIgnoreCase(senderId))
                        && (mess.getUser1().equalsIgnoreCase(user.getObjectId()) || mess.getUser2().equalsIgnoreCase(user.getObjectId()))) {
                    found = true;
                    mess.setUser1(currentUserId);
                    mess.setUser2(user.getObjectId());
                    mess.setSender(currentUserId);
                    mess.setMessage(messages.getMessage());
                    messages_list.remove(i);
                    messages_list.add(0, mess);
                    break;
                }


            }


            if (!found)
            {
                Log.e("New", "Add");
                messages_list.add(messages);
                FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_MESSAGE).child(messages.getObjectId()).setValue(messages);
                Messages conversation_model = null;
                conversation_model = messages;
                conversation_model.setObjectId(conversationObjectId);
                conversation_model.setUpdatedAt(Utils.getCurrentDate());

                FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_CONVERSATION).child(conversationObjectId)
                        .setValue(conversation_model);

            } else
            {
                Map<String, Object> update_conversation = new HashMap<>();
                update_conversation.put(DbConstants.CREATED_AT, mess.getCreatedAt());
                update_conversation.put(DbConstants.MESSAGE, PostActivity.message);
                update_conversation.put(DbConstants.ID, mess.getObjectId());
                update_conversation.put(DbConstants.UPDATED_AT, Utils.getCurrentDate());
                update_conversation.put(DbConstants.USER1,FirebaseAuth.getInstance().getCurrentUser().getUid());
                update_conversation.put(DbConstants.USER2, mess.getUser2());
                update_conversation.put(DbConstants.MEDIA, mess.getMedia());
                update_conversation.put(DbConstants.SENDER,FirebaseAuth.getInstance().getCurrentUser().getUid());

                FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_CONVERSATION).child(mess.getObjectId())
                        .updateChildren(update_conversation);
                FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_MESSAGE).child(messageId).setValue(messages);
                found = false;
            }
            ArrayList<String> sendTokens =  new ArrayList<>();
            sendTokens.add(user.getToken());
            Utils.sendNotification(sendTokens, Constants.ACTION_MESSAGE, "Message from: "+currentUserName, messages.getMessage());

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    populateList(messages_list);

                    //empty model
                    PostActivity.selectedUsers_array.clear();

                }
            });
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

 /*   @Override
    public void onRefresh() {
        *//*loadData(false);*//*
    }*/

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


        Query query = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_CONVERSATION).orderByChild(DbConstants.UPDATED_AT);

        query.addChildEventListener(childEventListener);
        //     query.orderByChild(DbConstants.USER1).equalTo(Preferences.getInstance(getActivity()).getString(DbConstants.ID));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long count = dataSnapshot.getChildrenCount();
                messages_list = new ArrayList<Messages>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
//                    Messages messages = new Messages();
//                    messages.setCreatedAt(data.child(DbConstants.CREATED_AT).getValue(String.class));
//                    messages.setMessage(data.child(DbConstants.MESSAGE).getValue(String.class));
//                    messages.setObjectId(data.child(DbConstants.ID).getValue(String.class));
//                    messages.setSender(data.child(DbConstants.SENDER).getValue(String.class));
//                    messages.setUpdatedAt(data.child(DbConstants.UPDATED_AT).getValue(String.class));
//                    messages.setUser1(data.child(DbConstants.USER1).getValue(String.class));
//                    messages.setUser2(data.child(DbConstants.USER2).getValue(String.class));

                    Messages message = data.getValue(Messages.class);

                    if (message.getUser1().equalsIgnoreCase(Preferences.getInstance(getActivity()).getString(DbConstants.ID))
                            || message.getUser2().equalsIgnoreCase(Preferences.getInstance(getActivity()).getString(DbConstants.ID))) {
                        messages_list.add(message);
                    }

                }

                // uploadMessage(null,PostActivity.selectedUsers_array,messages_list);

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

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            if (dataSnapshot.exists()) {

            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

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

//   @Override
//    public boolean onMessageReceive(Object message, Users sender) {
//        Log.e("onMessageReceive", "Invoked");
//       if (adapter != null && listView != null) {
//            final List<ParseObject> data = new ArrayList<ParseObject>();
//			data.addAll(adapter.getData());
//			String senderId = sender == null ? message.getParseUser(
//					DbConstants.SENDER).getObjectId() : sender.getObjectId();
//			boolean found = false;
//			for (int i = 0; i < data.size(); i++) {
//				if (data.get(i).getParseUser(DbConstants.USER1).getObjectId()
//						.equals(senderId)
//						|| data.get(i).getParseUser(DbConstants.USER2)
//								.getObjectId().equals(senderId)) {
//					Log.e("Object", "Found");
//					found = true;
//					ParseObject obj = data.get(i);
//					obj.put(DbConstants.MESSAGE,
//							message.getString(DbConstants.MESSAGE));
//					obj.put(DbConstants.IS_READ, false);
//					data.remove(i);
//					data.add(0, obj);
//				}
//			}
//			if (!found) {
//				Log.e("New", "Add");
//				ParseObject o = new ParseObject(DbConstants.TABLE_CONVERSATION);
//				o.put(DbConstants.MESSAGE,
//						message.getString(DbConstants.MESSAGE));
//
//				o.put(DbConstants.USER1,
//						message.getParseUser(DbConstants.USER1));
//				o.put(DbConstants.USER2,
//						message.getParseUser(DbConstants.USER2));
//				data.add(0, o);
//			}
//			getActivity().runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					Log.e("Adapter", "Updated -- " + data.size());
//					adapter.setData(data);
//					if (data.size() == 1) {
//						listView.setVisibility(View.VISIBLE);
//						progressParent.setVisibility(View.GONE);
//					}
//				}
//			});
//			return true;
//		}
//		return false;
//	}

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity())
                .replaceFragment(new NewsFeedFragment());
        return true;
    }

    @Override
    public void onEditDone(int position, Object post) {
        Log.e(MessagesFragment.class.getSimpleName(), "onEditDone");
    }

    @Override
    public boolean onMessageReceive(Messages message, Users sender) {

//        Log.e("onMessageReceive", "Invoked");
//        if (adapter != null && listView != null) {
//
//            final List<Messages> data = new ArrayList<Messages>();
//            data.addAll(adapter.getData());
//
//            String senderId = sender == null ? message.getSender() : sender.getObjectId();
//            boolean found = false;
//            for (int i = 0; i < data.size(); i++) {
//                if (data.get(i).getUser1()
//                        .equals(senderId)
//                        || data.get(i).getUser2().equals(senderId)) {
//                    Log.e("Object", "Found");
//                    found = true;
//                    Messages obj = data.get(i);
//                    obj.setMessage(message.getMessage());
//                    //obj.setIsread(false);
//                    data.remove(i);
//                    data.add(0, obj);
//                    data.remove(i);
//                    data.add(0, obj);
//                }
//            }
//
//            if (!found) {
//                Log.e("New", "Add");
//                Messages message_object = new Messages();
//                message_object.setMessage(message.getMessage());
//                message_object.setUser1(message.getUser1());
//                message_object.setUser2(message.getUser2());
//                data.add(0, message_object);
//
//                String objectId = databaseReference.child(DbConstants.TABLE_CONVERSATION).push().getKey();
//                databaseReference.child(DbConstants.TABLE_CONVERSATION).child(objectId).setValue(message_object);
//            }
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("Adapter", "Updated -- " + data.size());
//                    adapter.setData(data);
//                    if (data.size() == 1) {
//                        listView.setVisibility(View.VISIBLE);
//                        progressParent.setVisibility(View.GONE);
//
//                    }
//                }
//            });
//        }
        return false;
    }
}
