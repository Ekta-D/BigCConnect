package com.bigc.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;

import com.bigc.activities.LoginActivity;
import com.bigc.datastorage.Preferences;
import com.bigc.fragments.ConnectionsFragment;
import com.bigc.fragments.ExploreFragment;
import com.bigc.fragments.FragmentResetPassword;
import com.bigc.fragments.FragmentTributeDetail;
import com.bigc.fragments.FragmentTributes;
import com.bigc.fragments.MessagesFragment;
import com.bigc.fragments.NewsFeedFragment;
import com.bigc.fragments.PostDetailFragment;
import com.bigc.fragments.ProfileFragment;
import com.bigc.fragments.ReportProblemFragment;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.DummyTabFactory;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.views.AppRatingPrompt;
import com.bigc.views.CustomTypefaceSpan;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
public class HomeScreen extends AppCompatActivity implements
        FragmentHolder, OnClickListener {

    private SearchView searchView;
    private TabHost mTabHost;
    private int mContainerId;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private BaseFragment currentFragment;

    private View tabIndicator1;
    private View tabIndicator2;
    private View tabIndicator3;
    private View tabIndicator4;
    private View tabIndicator5;

    private ImageView tabIcon1;
    private ImageView tabIcon2;
    private ImageView tabIcon3;
    private ImageView tabIcon4;
    private ImageView tabIcon5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
        SpannableString s = new SpannableString(Utils.loadString(this,
                R.string.app_name));
        s.setSpan(new CustomTypefaceSpan("", Typeface.createFromAsset(
                        getAssets(), "font/marker_felt.ttf")), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bar.setTitle(s);
        Preferences.getInstance(HomeScreen.this).save(Constants.ISFIRST_TIME, true);
        setContentView(R.layout.activity_home);

        AppRatingPrompt.app_launched(this);
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mContainerId = android.R.id.tabcontent;
        fragmentManager = getSupportFragmentManager();

        tabIndicator1 = LayoutInflater.from(this).inflate(R.layout.tab,
                mTabHost.getTabWidget(), false);
        tabIndicator2 = LayoutInflater.from(this).inflate(R.layout.tab,
                mTabHost.getTabWidget(), false);
        tabIndicator3 = LayoutInflater.from(this).inflate(R.layout.tab,
                mTabHost.getTabWidget(), false);
        tabIndicator4 = LayoutInflater.from(this).inflate(R.layout.tab,
                mTabHost.getTabWidget(), false);
        tabIndicator5 = LayoutInflater.from(this).inflate(R.layout.tab,
                mTabHost.getTabWidget(), false);

        tabIcon1 = (ImageView) tabIndicator1;
        tabIcon2 = (ImageView) tabIndicator2;
        tabIcon3 = (ImageView) tabIndicator3;
        tabIcon4 = (ImageView) tabIndicator4;
        tabIcon5 = (ImageView) tabIndicator5;

        tabIcon1.setImageResource(R.drawable.icon_feeds);
        tabIcon2.setImageResource(R.drawable.icon_connections);
        tabIcon3.setImageResource(R.drawable.icon_messages);
        tabIcon4.setImageResource(R.drawable.icon_explore);
        tabIcon5.setImageResource(R.drawable.icon_profile_selected);

        mTabHost.addTab(mTabHost.newTabSpec("1")
                .setContent(new DummyTabFactory(this))
                .setIndicator(tabIndicator1));

        mTabHost.addTab(mTabHost.newTabSpec("2")
                .setContent(new DummyTabFactory(this))
                .setIndicator(tabIndicator2));

        mTabHost.addTab(mTabHost.newTabSpec("3")
                .setContent(new DummyTabFactory(this))
                .setIndicator(tabIndicator3));

        mTabHost.addTab(mTabHost.newTabSpec("4")
                .setContent(new DummyTabFactory(this))
                .setIndicator(tabIndicator4));

        mTabHost.addTab(mTabHost.newTabSpec("5")
                .setContent(new DummyTabFactory(this))
                .setIndicator(tabIndicator5));

        setTabListener();

        handleNavigation(getIntent());

        GoogleAnalyticsHelper
                .sendScreenViewGoogleAnalytics(this, "Home Screen");
    }

    private void handleNavigation(Intent intent) {
        String fragment = intent == null ? null : intent
                .getStringExtra("fragment");

        if (fragment == null) {
            String objectId = intent.getStringExtra("object");
            String action = intent == null ? null : intent.getAction();
            if (Constants.ACTION_FRIEND_REQUEST.equals(action)) {

                replaceFragment(getFragment(Constants.FRAGMENT_CONNECTIONS));
            } else if (Constants.ACTION_MESSAGE.equals(action)) {
                    replaceFragment(getFragment(Constants.FRAGMENT_MESSAGES));
            } else if (Constants.ACTION_TRIBUTE.equals(action)) {
                if (objectId == null || objectId.length() == 0) {
                    replaceFragment(getFragment(Constants.FRAGMENT_TRIBUTES));
                } else {
                    // TODO: 7/14/2017 tribute work 
                    /*ParseObject tributeObject = ParseObject.createWithoutData(
                            DbConstants.TABLE_TRIBUTE, objectId);
                    replaceFragment(new FragmentTributeDetail(null,
                            tributeObject, -1));*/
                }
            } else if (Constants.ACTION_NEWS_FEED.equals(action)) {
                if (objectId == null || objectId.length() == 0) {
                    renderHomeFragment();
                } else {
                    // TODO: 7/14/2017 tribute work 
                    /*ParseObject postObject = ParseObject.createWithoutData(
                            DbConstants.TABLE_POST, objectId);
                    replaceFragment(new PostDetailFragment(null, -1,
                            postObject, true));*/
                }
            } else {

                renderHomeFragment();
            }
        } else {
            replaceFragment(getFragment(fragment));
        }
    }

    @Override
    public void onNewIntent(Intent data) {
        super.onNewIntent(data);
        handleNavigation(data);
    }

    private void setTabListener() {
        tabIndicator1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                GoogleAnalyticsHelper.setClickedAction(HomeScreen.this,
                        "NewsFeed Tab");
                tabClicked(1);
            }
        });
        tabIndicator2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                GoogleAnalyticsHelper.setClickedAction(HomeScreen.this,
                        "Connections Tab");
                tabClicked(2);
            }
        });
        tabIndicator3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                GoogleAnalyticsHelper.setClickedAction(HomeScreen.this,
                        "Message Tab");
                tabClicked(3);
            }
        });
        tabIndicator4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                GoogleAnalyticsHelper.setClickedAction(HomeScreen.this,
                        "Explore Tab");
                tabClicked(4);
            }
        });
        tabIndicator5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                GoogleAnalyticsHelper.setClickedAction(HomeScreen.this,
                        "Profile Tab");
                tabClicked(5);
            }
        });
    }

    private void tabClicked(final int tabIndex) {
        String selectedTabID = Constants.FRAGMENT_NEWSFEED;
        switch (tabIndex) {
            case 1:
                selectedTabID = Constants.FRAGMENT_NEWSFEED;
                break;
            case 2:
                selectedTabID = Constants.FRAGMENT_CONNECTIONS;
                break;
            case 3:
                selectedTabID = Constants.FRAGMENT_MESSAGES;
                break;
            case 4:
                selectedTabID = Constants.FRAGMENT_EXPLORE;
                break;
            case 5:
                selectedTabID = Constants.FRAGMENT_PROFILE;
                break;
        }

        if (currentFragment != null
                && selectedTabID.equals(currentFragment.getName())) {
            if (Constants.FRAGMENT_PROFILE.equals(selectedTabID)
                    && !FirebaseAuth.getInstance().getCurrentUser().getUid()
                    .equals(ProfileFragment.getUser().getObjectId()))
                showUserProfile();

            return;
        }
        currentFragment = null;
        Utils.hideKeyboard(this);
        replaceFragment(getFragment(selectedTabID));
    }

    private void showUserProfile() {
        ProfileFragment.setUser(Preferences.getInstance(this).getUserFromPreference());
        currentFragment.onViewCreated(currentFragment.getView(), null);
        setTabBar(currentFragment.getTab());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchView != null) {
            searchView.setIconified(true);
            searchView.setIconified(true);
        }
    }

    @Override
    public void onBackPressed() {

        if (!currentFragment.onBackPressed()) {
            HomeScreen.this.finish();
        }
    }

    private void renderHomeFragment() {
        replaceFragment(new NewsFeedFragment());
    }

    private BaseFragment getFragment(String tag) {

        if (Constants.FRAGMENT_NEWSFEED.equals(tag)) {
            return new NewsFeedFragment();
        } else if (Constants.FRAGMENT_CONNECTIONS.equals(tag)) {
            return new ConnectionsFragment();
        } else if (Constants.FRAGMENT_MESSAGES.equals(tag)) {
            return new MessagesFragment();
        } else if (Constants.FRAGMENT_EXPLORE.equals(tag)) {
            return new ExploreFragment();
        } else if (Constants.FRAGMENT_PROFILE.equals(tag)) {
            return new ProfileFragment(new NewsFeedFragment(),
                    Preferences.getInstance(this).getUserFromPreference());
        } else if (Constants.FRAGMENT_TRIBUTES.equals(tag))
            return new FragmentTributes();

        return new NewsFeedFragment();
    }

    private void setTabBar(int tabIndex) {
        Log.e("TabId", tabIndex + "-");
        if (tabIndex <= 0)
            return;

        tabIcon1.setImageResource(R.drawable.icon_feeds);
        tabIcon2.setImageResource(R.drawable.icon_connections);
        tabIcon3.setImageResource(R.drawable.icon_messages);
        tabIcon4.setImageResource(R.drawable.icon_explore);
        tabIcon5.setImageResource(R.drawable.icon_profile);
        switch (tabIndex) {
            case 1:
                tabIcon1.setImageResource(R.drawable.icon_feeds_selected);
                break;
            case 2:
                tabIcon2.setImageResource(R.drawable.icon_connections_selected);
                break;
            case 3:
                tabIcon3.setImageResource(R.drawable.icon_messages_selected);
                break;
            case 4:
                tabIcon4.setImageResource(R.drawable.icon_explore_selected);
                break;
            case 5:
                tabIcon5.setImageResource(R.drawable.icon_profile_selected);
                break;
        }
    }

    @Override
    public void onClick(View v) {
    }

//    private class LogoutTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        public void onPreExecute() {
//            Utils.showProgress(HomeScreen.this);
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            clearLocalData();
//            return null;
//        }
//
//        @Override
//        public void onPostExecute(Void unused) {
//            ParseUser.logOut();
//            Utils.unregisterDeviceForNotifications();
//            Utils.hideProgress();
//            if (HomeScreen.this != null)
//                gotoLogin();
//        }
//    }

    private void clearLocalData() {
        /*try {
            ParseObject.unpinAllInBackground(Constants.TAG_CONNECTIONS,
                    new DeleteCallback() {

                        @Override
                        public void done(ParseException e) {
                            ParseObject
                                    .unpinAllInBackground(Constants.TAG_CONNECTIONS);
                        }
                    });
            ParseObject.unpinAllInBackground(Constants.TAG_POSTS,
                    new DeleteCallback() {

                        @Override
                        public void done(ParseException e) {
                            ParseObject
                                    .unpinAllInBackground(Constants.TAG_POSTS);
                        }
                    });

            ParseObject.unpinAllInBackground(Constants.TAG_MESSAGES,
                    new DeleteCallback() {

                        @Override
                        public void done(ParseException e) {
                            ParseObject
                                    .unpinAllInBackground(Constants.TAG_MESSAGES);
                        }
                    });

            ParseObject.unpinAllInBackground(Constants.TAG_STORIES,
                    new DeleteCallback() {

                        @Override
                        public void done(ParseException e) {
                            ParseObject
                                    .unpinAllInBackground(Constants.TAG_STORIES);
                        }
                    });

            ParseObject.unpinAll();
        } catch (ParseException e) {
            e.printStackTrace();
            clearLocalData();
        }*/
    }

    private void gotoLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        if (Preferences.getInstance(this).getBoolean(Constants.PREMIUM))
            menu.findItem(R.id.action_upgrade_no_ads).setVisible(false);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void replaceFragment(BaseFragment fragment) {
        final String tag = fragment.getName();
        BaseFragment f = (BaseFragment) fragmentManager.findFragmentByTag(tag);

        if (f != null)
            fragment = f;

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(mContainerId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        currentFragment = fragment;
        fragmentTransaction.commit();
        setTabBar(fragment.getTab());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                showLogoutDialog();
                break;
            case R.id.action_resetPassword:
                if (!Constants.FRAGMENT_RESET_PASSWORD.equals(currentFragment
                        .getName()))
                    replaceFragment(new FragmentResetPassword(currentFragment));
                break;
            case R.id.action_upgrade_no_ads:
                startActivity(new Intent(this, PurchaseActivity.class));
                break;
            case R.id.action_report_a_problem:
                if (!Constants.FRAGMENT_REPORT_PROBLEM.equals(currentFragment
                        .getName()))
                    replaceFragment(new ReportProblemFragment(currentFragment));
                break;
        }
        return true;
    }

    @Override
    public void logoutUser() {
       // new LogoutTask().execute();
        FirebaseAuth.getInstance().signOut();
        Preferences.getInstance(getBaseContext()).clearPreferences();
        gotoLogin();

    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logoutNow)
                .setPositiveButton(R.string.logout,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                logoutUser();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    @Override
    public boolean showProfileSettings() {
        return true;
    }
}