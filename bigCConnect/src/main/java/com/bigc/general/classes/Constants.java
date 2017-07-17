package com.bigc.general.classes;

public class Constants {

    public static enum USER_TYPE {
        SURVIVOR, SUPPORTER, FIGHTER
    }

    public static final int IS_SURVIVO = 0;
    public static final int IS_SUPPORTER = 1;
    public static final int IS_FIGHTER = 2;

    public static final String FIREBASE_STRAGE_URL = "gs://bigc-e2fb5.appspot.com";
    public static final String CURRENT_USERID = "Current_user_id";
    public static final String ISFIRST_TIME = "Isfirst_time";
    public static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyBV2lb1moknZjNmJPtXFznF9OLOQ7mKU_8"; // Test:

    // AIzaSyD21uHXMjHabLdZLm0t625dSBKpCmIOIMU
    // &
    // Production:
    // AIzaSyBV2lb1moknZjNmJPtXFznF9OLOQ7mKU_8
    public static final String TIME = "timestamp";
    public static final String MOPUB_UNIT_ID = "861ddf6490b640b281dc0867f3a9d54d";
    public static final String GOOGLE_BROWSER_KEY = "AIzaSyBCXkLpK6oUcEYUVleJXgDy24MhjaM69cA";
    public static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int PIC_CROP = 1;

    public static final String PREFS_NAME = "prefs";

    public static final String LOGIN = "login";

    // public static final int SURVIVOR = 0;
    // public static final int SUPPORTER = 1;

    public static final int PUBLIC = 0;
    public static final int PRIVATE = 1;

    public static final String NOTIFICATIONS = "notifications";
    public static final String SINGLE_SELECTION = "single_path";

    public static final String FRAGMENT_NEWSFEED = "1";
    public static final String FRAGMENT_POST_DETAIL = "11";

    public static final String FRAGMENT_CONNECTIONS = "2";
    public static final String FRAGMENT_CATEGORY_SURVIVORS = "22";

    public static final String FRAGMENT_MESSAGES = "3";
    public static final String FRAGMENT_MESSAGE_DETAIL = "31";

    public static final String FRAGMENT_EXPLORE = "4";
    public static final String FRAGMENT_RSSFEEDS = "41";
    public static final String FRAGMENT_BOOKS = "42";
    public static final String FRAGMENT_VIDEO = "43";
    public static final String FRAGMENT_TRIBUTES = "44";
    public static final String FRAGMENT_STORIES = "45";
    public static final String FRAGMENT_VIDEO_PLAYER = "46";
    public static final String FRAGMENT_BOOK_DETAIL = "47";
    public static final String FRAGMENT_TRIBUTE_DETAIL = "48";
    public static final String FRAGMENT_STORY_DETAIL = "49";

    public static final String FRAGMENT_PROFILE = "5";
    public static final String FRAGMENT_SUPPORTERS = "51";
    public static final String FRAGMENT_PHOTOS = "52";
    public static final String FRAGMENT_SEARCH_SURVIVOR = "53";
    public static final String FRAGMENT_RESET_PASSWORD = "54";
    public static final String FRAGMENT_POSTS = "55";
    public static final String FRAGMENT_TERMS = "56";
    public static final String FRAGMENT_SETTINGS = "57";
    public static final String FRAGMENT_REPORT_PROBLEM = "58";
    public static final String FRAGMENT_DEACTIVATE_SECURITY = "59";
    public static final String FRAGMENT_DEACTIVATE = "60";

    public static final String FRAGMENT_SEARCHABLE = "61";

    public static final String TAG_CONNECTIONS = "connections";
    public static final String TAG_MESSAGES = "messages";
    public static final String TAG_POSTS = "posts";
    public static final String TAG_STORIES = "stories";
    public static final String TAG_TRIBUTES = "tributes";

    // Cache Queries
    public static final String QUERY_USER_SUPPORTERS = "queryUserSupporters";

    public static final String ACTION_FRIEND_REQUEST = "com.bigc.FRIENDREQUEST";
    public static final String ACTION_NEWS_FEED = "com.bigc.NEWSFEED";
    public static final String ACTION_MESSAGE = "com.bigc.MESSAGE";
    public static final String ACTION_TRIBUTE = "com.bigc.TRIBUTE";

    public static final int PROFILE_IMAGE_HEIGHT = 500;

    public static String currentCameraIntentURI = "";
    public static final int CODE_SELECT_PICTURE = 101;
    public static final int CODE_TAKE_PHOTO_CODE = 102;
    public static final int CODE_INVITE_SUPPORTERS = 103;

    public static final String SENDER_EMAIL = "hello@time-away.com";
    public static final String PD = "ahmadtamara";
    public static final String USER = "timeaway";

    public static final String NOTIFICATION_ID = "notification_id";

    public static final int OPERATION_STATUS = 1;
    public static final int OPERATION_PHOTO = 2;
    public static final int OPERATION_STORY = 3;
    public static final int OPERATION_MESSAGE = 4;
    public static final int OPERATION_TRIBUTE = 5;
    public static final String EDIT_MODE = "editOperation";
    public static final String OPERATION = "operation";
    public static final String FROM_NEWSFEEDS = "from_newsfeeds";

    public static final String CLOUD_ID = "rueeOaJe3KEn1JEt8Nj2zH2MJ2encom4PFBiwbWf";
    public static final String CLOUD_KEY = "KosqGC4f2xjDLWlPKC0zcA96Z4QLZPoqYIiYXy4D";

    public static final String TEST_CLOUD_ID = "cZSlU01mX3rT6ZMJdsTccSwz87dz96zgLfawxRbK";
    public static final String TEST_CLOUD_KEY = "cJRhiDyjIWgBY7OahsyL4C11gfmnDstM5lvRnn9p";

    public static final String LAUNCH_COUNT = "launch_count";
    public static final String SPLASHES = "splashes";

    // Modes
    public static final String PREMIUM = "premium";
    public static final String FIREBASE_URL = "https://bigc-connect.firebaseio.com/";
}
