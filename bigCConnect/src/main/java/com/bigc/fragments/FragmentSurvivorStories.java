package com.bigc.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.bigc.adapters.StoriesAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.PostManager;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc.interfaces.StoryPopupOptionHandler;
import com.bigc.interfaces.UploadStoryObserver;
import com.bigc.models.Posts;
import com.bigc.models.Stories;
import com.bigc_connect.R;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


//public class FragmentSurvivorStories extends BaseFragment implements
//		OnLoadMoreListener, UploadStoryObserver, PopupOptionHandler //// TODO: 14-07-2017  
public class FragmentSurvivorStories extends BaseFragment implements
        OnLoadMoreListener, UploadStoryObserver, StoryPopupOptionHandler

{

    private LoadMoreListView listView;
    private StoriesAdapter adapter;
    private AdView adView;
    private TextView messageView;
    private LinearLayout progressParent;
    private ProgressBar progressView;

    //private List<ParseObject> stories = new ArrayList<ParseObject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stories_layout,
                container, false);

        listView = (LoadMoreListView) view.findViewById(R.id.listview);
        adView = (AdView) view.findViewById(R.id.adView);
        messageView = (TextView) view.findViewById(R.id.messageView);
        progressView = (ProgressBar) view.findViewById(R.id.progressView);
        progressParent = (LinearLayout) view
                .findViewById(R.id.messageViewParent);

        view.findViewById(R.id.addAStoryOptionImage).setOnClickListener(this);
        view.findViewById(R.id.addAStoryOptionText).setOnClickListener(this);
        //	adapter = new StoriesAdapter(this, stories);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "Survivor Stories Screen");

        if (!Preferences.getInstance(getActivity()).getBoolean(
                Constants.PREMIUM)) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        // listView.setOnRefreshListener(this);
//		listView.setOnLoadMoreListener(this);
//
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //// TODO: 14-07-2017
//				((FragmentHolder) getActivity())
//						.replaceFragment(new FragmentStoryDetail(
//								FragmentSurvivorStories.this, adapter
//										.getItem(position), position));

                ((FragmentHolder) getActivity()).replaceFragment(new
                        FragmentStoryDetail(FragmentSurvivorStories.this,
                        adapter.getItem(position), position));

            }
        });

//		listView.setAdapter(adapter);
//		if (stories.size() == 0) {
//			startProgress();
//			loadData(true);
//		} else {
//			adapter.notifyDataSetChanged();
//		}

        loadData(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Preferences.getInstance(getActivity())
                .getBoolean(Constants.PREMIUM)) {
            adView.setVisibility(View.GONE);
        }

        loadData(true);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadMore() {

        // loadStories(adapter.getLastItemDate(), false);
    }

    ArrayList<Stories> stories_list;

    private void loadData(final boolean fromCache) {

        //ParseQuery<ParseObject> query = Queries.getStoriesQuery(fromCache);

//		query.findInBackground(new FindCallback<ParseObject>() {
//
//			@Override
//			public void done(final List<ParseObject> stories, ParseException e) {
//
//				if (e == null) {
//
//					if (stories.size() == 0 && fromCache) {
//						loadData(false);
//						return;
//					}
//// TODO: 17-07-2017
////					new completePostLoadingsTask(stories, false, false)
////							.execute();
//
//				} else {
//					if (!fromCache) {
//						if (listView != null) {
//							// listView.onRefreshComplete();
//							if (e.getCode() == ParseException.CONNECTION_FAILED) {
//								populateList(null);
//							} else {
//								populateList(new ArrayList<ParseObject>());
//							}
//						}
//					} else {
//						populateList(null);
//					}
//				}
//
//			}
//		});


        Query query = Queries.getStoriesQuery(fromCache);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long story_count = dataSnapshot.getChildrenCount();

                stories_list = new ArrayList<Stories>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    Stories stories = data.getValue(Stories.class);
                    stories_list.add(stories);
                }
                populateList(stories_list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //// TODO: 17-07-2017

//	private class completePostLoadingsTask extends
//			AsyncTask<Void, Void, List<ParseObject>> {
//
//		List<ParseObject> stories;
//		boolean isMoreLoading;
//		boolean isRecent;
//
//		public completePostLoadingsTask(List<ParseObject> objects,
//				boolean isMoreLoading, boolean isRecent) {
//			this.stories = new ArrayList<ParseObject>();
//			if (objects != null)
//				this.stories.addAll(objects);
//
//			this.isMoreLoading = isMoreLoading;
//			this.isRecent = isRecent;
//		}
//
//		@Override
//		public List<ParseObject> doInBackground(Void... params) {
//			try {
//				ParseObject.unpinAll(Constants.TAG_STORIES, stories);
//				for (ParseObject p : stories)
//					p.getParseUser(DbConstants.USER).fetchIfNeeded();
//				ParseObject.pinAll(Constants.TAG_STORIES, stories);
//				return stories;
//			} catch (ParseException e2) {
//				e2.printStackTrace();
//				return null;
//			}
//		}
//
//		@Override
//		public void onPostExecute(List<ParseObject> stories) {
//			if (isMoreLoading) {
//				Log.e("new stories", stories.size() + "--");
//				adapter.addItems(stories, isRecent);
//				if (!isRecent)
//					listView.onLoadMoreComplete();
//			} else {
//				populateList(stories);
//			}
//		}
//	}

    private void populateList(List<Stories> stories) {

        //this.stories.clear();
        if (listView != null) {
            if (stories == null) {
                showError(Utils.loadString(getActivity(),
                        R.string.networkFailureMessage));
            } else if (stories.size() == 0) {
                showError(Utils.loadString(getActivity(),
                        R.string.noStoriesMessage));
            } else {
                // TODO: 17-07-2017
//				adapter.setData(stories);
//				listView.setVisibility(View.VISIBLE);
//				progressParent.setVisibility(View.GONE);
//				this.stories.addAll(stories);
                adapter = new StoriesAdapter(FragmentSurvivorStories.this, stories);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(adapter);
                progressParent.setVisibility(View.GONE);

            }
            // listView.onRefreshComplete();
        }
    }
//	private void populateList(List<ParseObject> stories) {
//
//		//this.stories.clear();
//		if (listView != null) {
//			if (stories == null) {
//				showError(Utils.loadString(getActivity(),
//						R.string.networkFailureMessage));
//			} else if (stories.size() == 0) {
//				showError(Utils.loadString(getActivity(),
//						R.string.noStoriesMessage));
//			} else {
//				// TODO: 17-07-2017
////				adapter.setData(stories);
////				listView.setVisibility(View.VISIBLE);
////				progressParent.setVisibility(View.GONE);
////				this.stories.addAll(stories);
//			}
//			 listView.onRefreshComplete();
//		}
//	}

    private void startProgress() {
        try {
            messageView.setText(R.string.loadingStories);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAStoryOptionImage:
            case R.id.addAStoryOptionText:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Add A Story Button");
                Utils.launchPostView(getActivity(), Constants.OPERATION_STORY);
                break;
        }
    }

    public void onStart() {
        super.onStart();
        PostManager.getInstance().addObserver(this);
    }

    @Override
    public void onDestroy() {
        PostManager.getInstance().removeStoryObserver();
        super.onDestroy();
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_STORIES;
    }

    @Override
    public int getTab() {
        return 0;
    }

//    private void loadStories(Date from, final boolean recent) {
//        ParseQuery<ParseObject> query = Queries.getStoriesQuery(false);
//
//        if (recent)
//            query.whereGreaterThan(DbConstants.CREATED_AT, from);
//        else
//            query.whereLessThan(DbConstants.CREATED_AT, from);
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//
//            @Override
//            public void done(final List<ParseObject> stories, ParseException e) {
//
//                if (e == null) {
//// TODO: 17-07-2017
////					new completePostLoadingsTask(stories, true, recent)
////							.execute();
//                } else {
//                    listView.onLoadMoreComplete();
//                }
//
//            }
//        });
//
//    }

//    @Override
//    public void onNotify(final ParseObject story) {
//        Log.e(FragmentSurvivorStories.class.getSimpleName(), "onNotify");
//        if (story == null) {
//            Toast.makeText(getActivity(), "Publish story is failed, try again",
//                    Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        try {
//            story.getParseUser(DbConstants.USER).fetchIfNeeded();
//            story.pin(Constants.TAG_STORIES);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
////        if (stories != null)
////            stories.add(0, story);
//
//        if (listView == null)
//            return;
//
//        getActivity().runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    // TODO: 17-07-2017
//                    //	adapter.addItem(story);
//                    if (listView.getVisibility() == View.GONE) {
//                        listView.setVisibility(View.VISIBLE);
//                        progressParent.setVisibility(View.GONE);
//                    }
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
    @Override
public boolean onBackPressed() {
    ((FragmentHolder) getActivity()).replaceFragment(new ExploreFragment());
    return true;
}

    @Override
    public void onNotify(Stories story) {

    }

    @Override
    public void onEditDone(int position, Posts post) {

    }

    @Override
    public void onDelete(int position, Stories story) {

    }

    @Override
    public void onEditClicked(int position, Stories story) {

    }

    @Override
    public void onFlagClicked(int position, Stories story) {

    }
//
//    @Override
//    public void onEditDone(int position, ParseObject post) {
//        Log.e(FragmentSurvivorStories.class.getSimpleName(), "onEditDone");
//    }
//
//    @Override
//    public void onDelete(int position, Stories story) {
//
//    }
//
//    @Override
//    public void onEditClicked(int position, Stories story) {
//
//    }
//
//    @Override
//    public void onFlagClicked(int position, Stories story) {
//
//    }
//// TODO: 14-07-2017
//	@Override
//	public void onDelete(int position, ParseObject post) {
//		if (position >= 0 && position < stories.size())
//			stories.remove(position);
//	}
//
//	@Override
//	public void onEditClicked(int position, ParseObject post) {
//		Log.e("onEditClicked", "Done");
//		ParseObject obj = post == null ? adapter.getItem(position) : post;
//		Utils.launchEditView(getActivity(), Constants.OPERATION_STORY,
//				position, obj);
//	}
//
//	@Override
//	public void onFlagClicked(int position, ParseObject post) {
//		if (post == null) {
//			post = adapter.getItem(position);
//		}
//		if (post != null) {
//			Utils.flagStory(post);
//		}
//		Toast.makeText(getActivity(),
//				getResources().getString(R.string.storyFlagMessage),
//				Toast.LENGTH_SHORT).show();
//	}
}
