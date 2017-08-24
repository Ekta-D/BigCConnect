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

import com.bigc.adapters.TributesAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.dialogs.AddTributeDialog;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.PostManager;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc.interfaces.UploadPostObserver;
import com.bigc.models.Posts;
import com.bigc.models.Tributes;
import com.bigc_connect.R;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//public class FragmentTributes extends BaseFragment implements
//		OnLoadMoreListener, UploadPostObserver, PopupOptionHandler  //// TODO: 14-07-2017
public class FragmentTributes extends BaseFragment implements
        OnLoadMoreListener, UploadPostObserver, PopupOptionHandler {

    private LoadMoreListView listView;
    private TributesAdapter adapter;
    private TextView messageView;
    private LinearLayout progressParent;
    private ProgressBar progressView;
    private List<Tributes> tributes;
    HashMap<String, Tributes> tributesHashMap = new HashMap<>();

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
        tributes = new ArrayList<>();
        adapter = new TributesAdapter(this, tributes);
        listView.setAdapter(adapter);

        if (tributes.size() == 0) {
            startProgress();
            loadData();
        }

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

				Log.e("Clicked on", position + "--");
				((FragmentHolder) getActivity())
						.replaceFragment(new FragmentTributeDetail(
								FragmentTributes.this, adapter
										.getItem(position), position));
            }
        });


//		if (posts.size() == 0) {
//			startProgress();
//			loadData();
//		}
    }

	/*@Override
    public void onStart() {
		super.onStart();
		PostManager.getInstance().addTributeObserver(this);
	}

	@Override
	public void onDestroy() {
		PostManager.getInstance().removeTributeObserver();
		super.onDestroy();
	}*/

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

		/*ParseQuery<ParseObject> query = Queries.getTributesQuery();

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
		});*/


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_TRIBUTE);
        Query query = databaseReference.orderByChild(DbConstants.FROM).equalTo(Preferences.getInstance(getActivity()).getString(DbConstants.ID));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        tributesHashMap.put(snapshot.getKey(), snapshot.getValue(Tributes.class));
                    }
                   /* adapter.notifyDataSetChanged();

                    //if (isMoreLoading) {
                    // Log.e("new posts", posts.size() + "--");
                    boolean isRecent = true;
                    adapter.addItems(tributesHashMap.values(), isRecent);
                    if (!isRecent) {
                        listView.onLoadMoreComplete();
                    } else {
                        populateList(tributesHashMap.values());
                    }*/
                } else {
                    /*if (listView != null) {
                        listView.onLoadMoreComplete();
                        *//*Toast.makeText(
                                getActivity(),
                                Utils.loadString(getActivity(),
                                        R.string.networkFailureMessage),
                                Toast.LENGTH_LONG).show();*//*
                    }
                    showError(Utils.loadString(getActivity(),
                            R.string.noTributeMessage));*/
                }
                checkToTributes(databaseReference,  Preferences.getInstance(getActivity()).getString(DbConstants.ID));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        query.addChildEventListener(childEventListener);

    }

    private void checkToTributes(DatabaseReference databaseReference, String uid) {
        Query query = databaseReference.orderByChild(DbConstants.TO).equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        tributesHashMap.put(snapshot.getKey(), snapshot.getValue(Tributes.class));
                    }
                    adapter.notifyDataSetChanged();

                    //if (isMoreLoading) {
                    // Log.e("new posts", posts.size() + "--");
                    boolean isRecent = true;
                    adapter.addItems(tributesHashMap.values(), isRecent);
                    if (!isRecent) {
                        listView.onLoadMoreComplete();
                    } else {
                        populateList(tributesHashMap.values());
                    }
                } else {
                    if (listView != null) {
                        populateList(tributesHashMap.values());
                        listView.onLoadMoreComplete();
                        /*Toast.makeText(
                                getActivity(),
                                Utils.loadString(getActivity(),
                                        R.string.networkFailureMessage),
                                Toast.LENGTH_LONG).show();*/
                    } else
                        showError(Utils.loadString(getActivity(),
                            R.string.noTributeMessage));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        query.addChildEventListener(childEventListener);
    }

    private void populateList(Collection<Tributes> tributes) {

        this.tributes.clear();
        if (listView != null) {
            if (tributes == null) {
                showError(Utils.loadString(getActivity(),
                        R.string.networkFailureMessage));
            } else if (tributes.size() == 0) {
                showError(Utils.loadString(getActivity(),
                        R.string.noTributeMessage));
            } else {
                adapter.setData(tributes);
                listView.setVisibility(View.VISIBLE);
                progressParent.setVisibility(View.GONE);
                this.tributes.addAll(tributes);


            }
            listView.onLoadMoreComplete();
        }
    }

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("added_tributes", dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    //for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    tributesHashMap.put(dataSnapshot.getKey(),dataSnapshot.getValue(Tributes.class));
                    //tributesTemp.add(dataSnapshot.getValue(Tributes.class));
                    //}
                    /*adapter.notifyDataSetChanged();
                    stopProgress();*/

                    //if (isMoreLoading) {
                    // Log.e("new posts", posts.size() + "--");
                    boolean isRecent = true;
                    adapter.addItems( tributesHashMap.values(), isRecent);
                    if (!isRecent) {
                        listView.onLoadMoreComplete();
                    } else {
                        populateList(tributesHashMap.values());
                    }
                } else {
                    /*if (listView != null) {
                        listView.onLoadMoreComplete();
                        Toast.makeText(
                                getActivity(),
                                Utils.loadString(getActivity(),
                                        R.string.networkFailureMessage),
                                Toast.LENGTH_LONG).show();
                    }*/
                    showError(Utils.loadString(getActivity(),
                            R.string.noTributeMessage));
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i("update_tributes", dataSnapshot.toString());

                if (dataSnapshot.exists()) {
                    tributes.clear();
                    //for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //tributesTemp.add(dataSnapshot.getValue(Tributes.class));
                    tributesHashMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(Tributes.class));

                    //}
                    /*adapter.notifyDataSetChanged();
                    stopProgress();*/

                    //if (isMoreLoading) {
                    // Log.e("new posts", posts.size() + "--");
                    boolean isRecent = true;
                    adapter.addItems( tributesHashMap.values(), isRecent);
                    if (!isRecent) {
                        listView.onLoadMoreComplete();
                    } else {
                        populateList( tributesHashMap.values());
                    }
                } else {
                    /*if (listView != null) {
                        listView.onLoadMoreComplete();
                        Toast.makeText(
                                getActivity(),
                                Utils.loadString(getActivity(),
                                        R.string.networkFailureMessage),
                                Toast.LENGTH_LONG).show();
                    }*/
                    showError(Utils.loadString(getActivity(),
                            R.string.noTributeMessage));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    tributesHashMap.remove(dataSnapshot.getKey());
                    adapter.notifyDataSetChanged();
                }
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
    public void onNotify(final Tributes post) {
        if (post == null) {
            Toast.makeText(getActivity(), "Upload status is failed, try again",
                    Toast.LENGTH_LONG).show();
            return;
        }

		/*try {
			post.getParseUser(DbConstants.USER).fetchIfNeeded();
			post.pin(Constants.TAG_TRIBUTES);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/

        //posts.add(0, post);

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
	/*	ParseQuery<ParseObject> query = Queries.getTributesQuery();

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
		});*/
    }

	/*private class completePostLoadingsTask extends
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
*/
/*	private void populateList(List<ParseObject> posts) {

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
	}*/
    //// TODO: 14-07-2017

	@Override
	public void onDelete(int position, Object post) {
		if (position >= 0 && position < tributes.size())
			tributes.remove(position);
	}

	@Override
	public void onEditClicked(int position, Object post) {
		Log.e("onEditClicked", "Done");
		Tributes obj = post == null ? adapter.getItem(position) : (Tributes) post;
		Utils.launchTributeEditView(getActivity(), Constants.OPERATION_TRIBUTE, false,
				position, obj);
	}

    @Override
    public void onEditDone(int position, Object post) {
        Tributes tributeObj = (Tributes) post;
        Log.e(FragmentTributes.class.getSimpleName(), "onEditDone");
        adapter.updateItem(position, tributeObj);//// TODO: 14-07-2017
    }

// TODO: 14-07-2017
	@Override
	public void onFlagClicked(int position, Object post) {
		if (post == null) {
			post = adapter.getItem(position);
		}
		if (post != null) {
			Utils.flagTribute(post);
		}
		Toast.makeText(getActivity(),
				getResources().getString(R.string.tributeFlagMessage),
				Toast.LENGTH_SHORT).show();
	}
}