package com.bigc.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc.models.Comments;
import com.bigc.models.ConnectionsModel;
import com.bigc.models.Post;
import com.bigc.models.Posts;
import com.bigc.models.Stories;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements OnClickListener {

    private EditText emailView;
    private EditText passwordView;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String storie_comment_json = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        emailView = (EditText) findViewById(R.id.inputBoxEmail);
        passwordView = (EditText) findViewById(R.id.inputBoxPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        boolean isFirstTime = Preferences.getInstance(LoginActivity.this).getBoolean(Constants.ISFIRST_TIME);

     /*   storie_comment_json = "{ \"results\": [\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-04-26T21:28:06.418Z\",\n" +
                "        \"message\": \"amen\",\n" +
                "        \"objectId\": \"17kael4vdq\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"mURbNyX6jL\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-04-26T21:28:06.418Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"CxE6c5KnDF\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-02-13T08:57:44.668Z\",\n" +
                "        \"message\": \"write the next chapter later\",\n" +
                "        \"objectId\": \"DJzi3oIVso\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"sWDj5BgJXX\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-02-13T08:57:44.668Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"Aq5fysNb0T\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2015-10-28T11:32:25.976Z\",\n" +
                "        \"message\": \"hello Jane i was so touched when i read your story your children are beautiful stay strong keep the faith you have made me feel that anything is possible thank you \",\n" +
                "        \"objectId\": \"DvGITugKzi\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"sm9rKlq32j\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2015-10-28T11:32:25.976Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"1gYaAnaPNn\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-02-13T04:03:55.020Z\",\n" +
                "        \"message\": \"I didn't know for about 2years and thanks to the lord I'm alive .I have a feeling your going to be fine and wish you the best of luck. I'm still getting tests as well it's a part of the journey and you choose your own path. xx\\n\",\n" +
                "        \"objectId\": \"JBHZ47YDcx\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"pcmIVt4Xoj\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-02-13T04:03:55.020Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"Aq5fysNb0T\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-11-30T03:13:06.088Z\",\n" +
                "        \"message\": \"I pray that you'd be ok.\",\n" +
                "        \"objectId\": \"OLAkQshjby\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"pcmIVt4Xoj\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-11-30T03:13:06.088Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"k4FK6ZS122\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-01-13T15:21:49.946Z\",\n" +
                "        \"message\": \"The waiting for tests can be an excruciating time that few understand. I know  when my 3 monthly CT scans cam around I used to be a stressed out bitch to everyone then I would get the all clear and life would go on.  You will be fine, I found by not focusing on the big C word my life was easier..I am a gastric cancer survivor....so I just thought OK I'm sick and I have to have check ups but didn't focus on all the medical terminology or the names of things and treatments and just doing that made it easier to cope. So many people get hung up on the disease and treatments they forget to live.  I didn't talk about it only when necessary.  I hope this helps.....stay strong and  enjoy the moment\",\n" +
                "        \"objectId\": \"QRA2Xa7BuF\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"pcmIVt4Xoj\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-01-13T15:21:49.946Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"WDk8WMsILq\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-03-08T19:27:56.947Z\",\n" +
                "        \"message\": \"I am a 10 year survivor of stage 3 her 2nue breast cancer.  I to this day I have never had reconstructive surgery.  My scar is my badge of honor.  I know we must move forward but I never want to forget what I went through because it was not only the worst thing but also the best thing that has happened to me.  It made me appreciate so much in my life.  My faith, my family, and my friends...\",\n" +
                "        \"objectId\": \"TggIciDu0M\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"9JrFkXgM26\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-03-08T19:27:56.947Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"FRMRtUEWTI\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-04-20T11:04:27.836Z\",\n" +
                "        \"message\": \"amen \",\n" +
                "        \"objectId\": \"UbbLwz6I22\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"mURbNyX6jL\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-04-20T11:04:27.836Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"qXTGhs0hlL\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2015-12-29T01:48:44.068Z\",\n" +
                "        \"message\": \"Hi Rose. Thanks for sharing your story. I was diagnosed with \\n breast cancer in March of this year. I had 5 operations to remove the cancer. The last one was a  mastectomy. I can also say it was Jesus who gave me the strength to endure and my family provided incredible strength also.\",\n" +
                "        \"objectId\": \"ZmWPkyCmly\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"nnLCSlRFZ5\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2015-12-29T01:48:44.068Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"bSO8rmIyst\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-01-22T15:33:49.488Z\",\n" +
                "        \"message\": \"reading your heart felt story has given me hope, I was diagnosed with breast cancer few mouths ago now, not sure how I should be feeling heads all over the place my moods are up and down, but I do believe positive thinking helps \",\n" +
                "        \"objectId\": \"b6iXB9pvZg\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"sm9rKlq32j\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-01-22T15:33:49.488Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"ErxVBmXY2N\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-04-12T21:27:55.965Z\",\n" +
                "        \"message\": \"Hi mine was 2012 2013.I was 38.Had lump big as a pea.Doc told me was cyst don't worry about. A yr later was a Mas.Had lumpectomy. Didn't get all.Then mastectomy. Chemo Radiation. My hair was gone by day 14.Head very sore.Lots water.8 glasses a day. And made my self walk..The end of Aug I had reconstruction surgery.  complications. A 9hr surgery. It didn't take.So I had a black boob for 5 days.They were waiting on expander.But was to infected.Had 3 blood transfusions. I'm not a smoker and I'm small.Just didn't take.6months now.On 22 I'm going for expander.my skin is thin.But I'm not giving up.The last resort is for skin of back with muscle.If you need to no anything ask.All try best I can to help.And may God bless.\",\n" +
                "        \"objectId\": \"hUHfrtjl6Y\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"orYoKOYSWu\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-04-12T21:27:55.965Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"J7h608xdhN\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2015-12-29T01:51:45.913Z\",\n" +
                "        \"message\": \"In Sept. I found out I was cancer free so I am so grateful to God for bringing me through this hard time. I will pray for you that the cancer will stay in remission and you will live a long life. God bless you!\",\n" +
                "        \"objectId\": \"kKZNbLibnb\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"nnLCSlRFZ5\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2015-12-29T01:51:45.913Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"bSO8rmIyst\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-01-13T22:37:33.657Z\",\n" +
                "        \"message\": \"if this helps in any way... I got diagnosed with Malignant Melanoma pretty much exactly the same time as your diagnosis, I was 19 in September so I'm sure we have similar factors in our lives to contend with alongside the big C! I've already had one operation to test some of my lymph nodes and see if the cancer had spread, just before Xmas.. and found out last week that it has so I, going back under the knife next week:/ literally just joined this forum today (13/01/16) because, as supportive as everyone in my life has been, I really feel like I just need to talk to someone who has even the slightest idea of what it's actually like at this age to go through stuff like this.  I was also like you in that they didn't detect mine as  cancer initially... but I've found that they deal with me a lot faster than most other patients when it comes to appointments and surgery. I really hope your results come back clear... and if they don't, mine weren't clear either! so if you need a chat, I know that'd make me feel a whole lot better! \",\n" +
                "        \"objectId\": \"ky9gkmvscm\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"pcmIVt4Xoj\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-01-13T22:37:33.657Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"HaR48Qtjj5\"\n" +
                "        }\n" +
                "    },\n" +
                "\t{\n" +
                "        \"createdAt\": \"2016-02-13T03:55:24.811Z\",\n" +
                "        \"message\": \"Amen xx\",\n" +
                "        \"objectId\": \"wWWxQeEdYK\",\n" +
                "        \"post\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"Stories\",\n" +
                "            \"objectId\": \"mURbNyX6jL\"\n" +
                "        },\n" +
                "        \"updatedAt\": \"2016-02-13T03:55:24.811Z\",\n" +
                "        \"user\": {\n" +
                "            \"__type\": \"Pointer\",\n" +
                "            \"className\": \"_User\",\n" +
                "            \"objectId\": \"Aq5fysNb0T\"\n" +
                "        }\n" +
                "    }\n" +
                "] }";


        try {
            JSONObject jsonObject = new JSONObject(storie_comment_json);
            Log.i("jsonObject", jsonObject.toString());
            insertStoriesToDatabase(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        if (!isFirstTime) {
            checkPermissions();
            //Go directly to main activity.
        }

        passwordView.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onLoginButtonClicked();
                }
                return false;
            }
        });


        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.forgotPasswordOption).setOnClickListener(this);
        findViewById(R.id.signUpOption).setOnClickListener(this);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
                "Login Screen");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                GoogleAnalyticsHelper.setClickedAction(this, "Login Button");
                onLoginButtonClicked();
                break;
            case R.id.signUpOption:
                GoogleAnalyticsHelper.setClickedAction(this, "SignUp Button");
                startActivity(new Intent(this, SignupActivity.class));
                finish();
                break;
            case R.id.forgotPasswordOption:
                GoogleAnalyticsHelper.setClickedAction(this,
                        "Forgot-Password Button");
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                overridePendingTransition(R.anim.pull_up, R.anim.remains_same);
                break;
        }
    }

    private void onLoginButtonClicked() {
        String email = emailView.getText().toString();
        String pass = passwordView.getText().toString();

        if (email.length() == 0) {
            emailView.setError(Utils.loadString(this, R.string.emailEmpty));
            return;
        } else if (!Utils.validateEmail(email)) {
            emailView.setError(Utils.loadString(this, R.string.invalidEmail));
            return;
        }

        if (pass.length() == 0) {
            passwordView.setError(Utils
                    .loadString(this, R.string.passwordEmpty));
            return;
        } else if (pass.length() < 5) {
            passwordView.setError(Utils.loadString(this,
                    R.string.tooShortPassword));
            return;
        }

        Utils.showProgress(this);
        loginUser(email, pass);
    }

   /* private void insertStoriesToDatabase(JSONObject jsonObject) {
        try {
            JSONArray result_array = jsonObject.getJSONArray("results");

            for (int i = 0; i < result_array.length(); i++) {
                JSONObject object = result_array.getJSONObject(i);
                String createdAt = object.optString("createdAt");
                String message = object.optString("message");
                String objectId = object.getString("objectId");
                JSONObject postJson = object.getJSONObject("post");
                String postId = postJson.getString("objectId");
                String updatedAt = object.getString("updatedAt");
                JSONObject userJson = object.getJSONObject("user");
                String userId = userJson.getString("objectId");


                Comments comment = new Comments();
                comment.setPost(postId);
                comment.setMessage(message);
                comment.setUser(userId);
                comment.setCreatedAt(createdAt);
                comment.setUpdatedAt(updatedAt);
                comment.setObjectId(objectId);

                FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_STORY_COMMENT).child(objectId).setValue(comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/


    private void loginUser(String email, String password) {

//		ParseUser.logInInBackground(email, password, new LogInCallback() {
//			@Override
//			public void done(ParseUser user, ParseException e) {
//				Log.e("Exception", e + "-");
//				if (e == null) {
//					Log.e("Login", "Success: " + user.getUsername());
//					if (LoginActivity.this != null) {
//						if (user.getBoolean(DbConstants.DEACTIVATED)) {
//							ParseUser.logOut();
//							Utils.hideProgress();
//							passwordView.setText("");
//							showDeactivatedDialog();
//						} else {
//							Utils.registerDeviceForNotifications();
//
//							ParseQuery<ParseObject> query = Queries
//									.getUserConnectionsQuery(
//											ParseUser.getCurrentUser(), false);
//							query.findInBackground(new FindCallback<ParseObject>() {
//
//								@Override
//								public void done(List<ParseObject> connections,
//										ParseException arg1) {
//									new fetchUsersTask(connections).execute();
//								}
//							});
//						}
//					}
//				} else {
//					Log.e("Login", "Failed");
//					if (LoginActivity.this != null)
//						Toast.makeText(LoginActivity.this,
//								R.string.invalidCredentials, Toast.LENGTH_LONG)
//								.show();
//					Utils.hideProgress();
//				}
//			}
//		});
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            gotoHomeScreen();

                            fetchUser();
                        } else {
                            String error = task.getException().toString();
                            String message = "";
                            if (error.equalsIgnoreCase("com.google.firebase.auth.FirebaseAuthInvalidUserException:" +
                                    " There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                message = "This is an Invalid User ID";
                            }
                            if (error.equalsIgnoreCase("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: " +
                                    "The password is invalid or the user does not have a password.")) {
                                message = "The password is invalid";
                            }

                            Log.i("login_error", task.getException().toString());
                            Utils.showPrompt(LoginActivity.this, message);
                            Utils.hideProgress();
                        }

                    }
                }
        );

    }

    private void showDeactivatedDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Deactivated Account")
                .setMessage(
                        "You've deactivated your account, Please email info@bigc-connect.com if you want to activate it.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }


    private void fetchUserTask() {

        // TODO: 7/18/2017 fetch user task
        List<ConnectionsModel> connectionsModels = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //ref.child(DbConstants.TABLE_CONNECTIONS)
        Queries.getUserConnectionsQuery(Preferences.getInstance(getBaseContext()).getUserFromPreference(), false, getApplicationContext());
    }

    PostActivity postActivity = new PostActivity();
    ArrayList<Users> usersArrayList;

    private void fetchUser() {
        Query query = Queries.getAllUsers();
        Utils.showProgress(LoginActivity.this);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                usersArrayList = new ArrayList<Users>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    Users user = data.getValue(Users.class);
                    usersArrayList.add(user);
                }
                Utils.hideProgress();
                Preferences.getInstance(LoginActivity.this).save_list(DbConstants.FETCH_USER, usersArrayList);
//               postActivity.getAllUsers(usersArrayList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


/*    private class fetchUsersTask extends AsyncTask<Void, Void, Void> {

        private List<ParseObject> objects;

        public fetchUsersTask(List<ParseObject> objects) {
            this.objects = new ArrayList<ParseObject>();
            if (objects != null)
                this.objects.addAll(objects);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                for (ParseObject obj : objects)
                    if (obj.getParseUser(DbConstants.TO).getObjectId()
                            .equals(ParseUser.getCurrentUser().getObjectId())) {
                        obj.getParseUser(DbConstants.FROM).fetchIfNeeded();
                    } else {
                        obj.getParseUser(DbConstants.TO).fetchIfNeeded();
                    }

                ParseObject.pinAll(Constants.TAG_CONNECTIONS, objects);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void objects) {
            Utils.hideProgress();
            gotoHomeScreen();
        }
    }*/

    private void gotoHomeScreen() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String current_uid = currentUser.getEmail();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS);
        databaseReference.orderByChild(DbConstants.EMAIL).equalTo(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println(dataSnapshot.toString());
                if (dataSnapshot.getValue() == null) {

                    Utils.showPrompt(LoginActivity.this, "This user may not exists in database");
                    Utils.hideProgress();
                } else {
                    String key = dataSnapshot.getKey();
                    Map<Object, Object> values1 = (Map<Object, Object>) dataSnapshot.getValue();
                    String keySet = (String) values1.keySet().toArray()[0];
                    Map<Object, Object> values = (Map<Object, Object>) values1.get(keySet);

                    boolean deactivated = (boolean) values.get(DbConstants.DEACTIVATED);

                    Preferences.getInstance(LoginActivity.this).save(DbConstants.NAME, String.valueOf(values.get("name")));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.EMAIL, String.valueOf(values.get("email")));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.PROFILE_PICTURE, String.valueOf(values.get("profile_picture")));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.RIBBON, Integer.parseInt(String.valueOf(values.get("ribbon"))));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.LOCATION, String.valueOf(values.get("location")));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.STAGE, String.valueOf(values.get("stage")));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.CANCER_TYPE, String.valueOf(values.get("cancertype")));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.TYPE, Integer.parseInt(String.valueOf(values.get("type"))));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.ID, String.valueOf(values.get("objectId")));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.VISIBILITY, Integer.parseInt(String.valueOf(values.get("visibility"))));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.TOKEN, String.valueOf(values.get("token")));
                    Preferences.getInstance(LoginActivity.this).save(DbConstants.RECEIVEPUSH, String.valueOf(values.get("recievePush")));

                    fetchUserTask();

                    Utils.hideProgress();

                    if (deactivated) {
                        passwordView.setText("");
                        showDeactivatedDialog();
                    } else {
                        startActivity(new Intent(com.bigc.activities.LoginActivity.this, HomeScreen.class));
                        finish();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public boolean checkPermissions() {
        int permissionWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int receive_boot = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED);
        int wake_lock = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK);
        int get_accounts = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        int camera_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (receive_boot != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        }
        if (wake_lock != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WAKE_LOCK);
        }
        if (get_accounts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
        }
        if (camera_permission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,

                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(LoginActivity.this, "permissions granted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "permissions not granted!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}