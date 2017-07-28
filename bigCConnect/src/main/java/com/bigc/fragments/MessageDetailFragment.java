package com.bigc.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import com.bigc.activities.PostActivity;
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
import com.bigc.models.Comments;
import com.bigc.models.Messages;
import com.bigc.models.Users;
import com.bigc.receivers.NotificationReceiver;
import com.bigc_connect.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.janmuller.android.simplecropimage.Util;

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
       /* adapter = new ChatAdapter(getActivity(), messageList, Preferences.
                getInstance(getActivity()).getAllUsers(DbConstants.FETCH_USER));
        //	adapter = new ChatAdapter(getActivity(),	MessageDetailFragment.conversation);

        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(adapter);
        messageViewParent.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);*/
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

        // loadData(false);
        loadData(false);
    }

    ArrayList<Messages> chat_conversation;
    boolean isCompleted = false;

    public void loadData(boolean fromCache) {

        Utils.showProgress(getActivity());

        final String senderID = MessageDetailFragment.conversation.getUser1();
        final String receiver = MessageDetailFragment.conversation.getUser2();
//        final String currentuserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_MESSAGE).orderByKey().addChildEventListener(childEventListener);
        FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_MESSAGE).orderByKey().
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chat_conversation = new ArrayList<Messages>();

                        long count = dataSnapshot.getChildrenCount();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String key = data.getKey();
                            Messages messages = data.getValue(Messages.class);
                            Log.i("data", data.toString());
                            boolean toadd = false;


//                            if ((messages.getUser1().equals(userID) || messages.getUser2().equals(userID))
//                                    && (messages.getUser1().equals(currentuserID) || messages.getUser2().equals(currentuserID))
//                                    && (messages.getUser2().equalsIgnoreCase(receiver))
//                                    )

                            System.out.println("condition" + messages.getUser1().equals(senderID) +
                                    messages.getUser2().equals(senderID) + messages.getUser2().equalsIgnoreCase(receiver) +
                                    messages.getUser1().equalsIgnoreCase(receiver));

                            if ((messages.getUser1().equals(senderID) || messages.getUser2().equals(senderID))
                                    && (messages.getUser2().equalsIgnoreCase(receiver) || messages.getUser1().equalsIgnoreCase(receiver))) {
                                toadd = true;
                            }
                            String user1 = messages.getUser1().equals(senderID) ? senderID : FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String user2 = messages.getUser2().equals(senderID) ? senderID : FirebaseAuth.getInstance().getCurrentUser().getUid();
                            messages.setUser2(user2);
                            messages.setUser1(user1);
                            messages.setSender(user1);

                            if (toadd) {
                                chat_conversation.add(messages);
                            }
                            isCompleted = true;
                        }
                        if (isCompleted) {
                            if (senderID.equals(MessageDetailFragment.conversation.getSender())) {
                                Collections.sort(chat_conversation, new Comparator<Messages>() {
                                    @Override
                                    public int compare(Messages comments, Messages t1) {
                                        return Integer.parseInt(String.valueOf(Utils.convertStringToDate(comments.getCreatedAt()).
                                                compareTo(Utils.convertStringToDate(t1.getCreatedAt()))));
                                    }
                                });

                                Utils.hideProgress();
                            }

                            populateList(chat_conversation);
                            Utils.hideProgress();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Log.i("added_chat", dataSnapshot.toString());
            if (dataSnapshot.exists()) {

            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Log.i("update_chat", dataSnapshot.toString());
            if (dataSnapshot.exists()) {
                chat_conversation.clear();

            }
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

                    Messages message_reply = new Messages();
                    Messages conversation = new Messages();
                    message_reply.setCreatedAt(MessageDetailFragment.conversation.getCreatedAt());
                    message_reply.setMessage(reply);
                    message_reply.setUser1(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    message_reply.setSender(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    String receiver = MessageDetailFragment.conversation.getUser2();
                    if (!receiver.equalsIgnoreCase(message_reply.getSender())) {
                        message_reply.setUser2(receiver);
                    }
                    message_reply.setUpdatedAt(Utils.getCurrentDate());
                    message_reply.setMedia(MessageDetailFragment.conversation.getMedia());
                    message_reply.setObjectId(UUID.randomUUID().toString());


                    FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_MESSAGE).child(message_reply.getObjectId())
                            .setValue(message_reply);

                    conversation = message_reply;
                    conversation.setObjectId(MessageDetailFragment.conversation.getObjectId());


                    Map<String, Object> update_conversation = new HashMap<>();
                    update_conversation.put(DbConstants.CREATED_AT, conversation.getCreatedAt());
                    update_conversation.put(DbConstants.MESSAGE, conversation.getMessage());
                    update_conversation.put(DbConstants.ID, conversation.getObjectId());
                    update_conversation.put(DbConstants.UPDATED_AT, conversation.getUpdatedAt());
                    update_conversation.put(DbConstants.USER1, conversation.getUser1());
                    update_conversation.put(DbConstants.USER2, conversation.getUser2());
                    update_conversation.put(DbConstants.MEDIA, conversation.getMedia());


                    FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_CONVERSATION).child(conversation.getObjectId())
                            .updateChildren(update_conversation);

                    ArrayList<Messages> updateList = new ArrayList<>();
                    updateList.add(conversation);
                    adapter.setData(updateList);
                    //   populateList(updateList);
                    adapter.notifyDataSetChanged();

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

//    private void loadData(final boolean fromCache) {
//
//		/*ParseQuery<ParseObject> query = Queries.getGroupMessagesQuery(user,
//                fromCache);
//
//		query.findInBackground(new FindCallback<ParseObject>() {
//
//			@Override
//			public void done(final List<ParseObject> messages, ParseException e) {
//
//				if (e == null) {
//
//					Log.e("Messages", messages.size() + "--");
//
//					if (messages.size() == 0 && fromCache) {
//						// Load data from network
//						loadData(false);
//						return;
//					}
//
//					new completeMessageLoadingsTask(messages).execute();
//
//				} else {
//					if (!fromCache) {
//						if (listView != null) {
//							// listView.onRefreshComplete();
//							Toast.makeText(
//									getActivity(),
//									Utils.loadString(getActivity(),
//											R.string.networkFailureMessage),
//									Toast.LENGTH_LONG).show();
//						}
//					} else {
//						populateList(null);
//					}
//				}
//
//			}
//		});*/
//    }

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

    private void populateList(List<Messages> messageList) {
        if (listView != null) {
            if (messageList == null) {
                showError(Utils.loadString(getActivity(),
                        R.string.networkFailureMessage));
            } else if (messageList.size() == 0) {
                showError(Utils.loadString(getActivity(),
                        R.string.noFeedMessage));
            } else {
                adapter = new ChatAdapter(getActivity(), messageList, Preferences.
                        getInstance(getActivity()).getAllUsers(DbConstants.FETCH_USER));
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

//    @Override
//    public boolean onMessageReceive(final Object message, Users user) {
//      if (listView != null || adapter != null) {
//            getActivity().runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					adapter.addItem(message);
//				}
//			});
//			return true;
//		}
//        return false;
//    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity())
                .replaceFragment(new MessagesFragment());
        return true;
    }

    @Override
    public boolean onMessageReceive(final Messages message, Users sender) {
        if (listView != null || adapter != null) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    adapter.addItem(message);
                }
            });
            return true;
        }
        return false;
    }
}