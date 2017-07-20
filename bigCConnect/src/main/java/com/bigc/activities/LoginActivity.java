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
import com.bigc.models.ConnectionsModel;
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
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends Activity implements OnClickListener {

    private EditText emailView;
    private EditText passwordView;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        emailView = (EditText) findViewById(R.id.inputBoxEmail);
        passwordView = (EditText) findViewById(R.id.inputBoxPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        boolean isFirstTime = Preferences.getInstance(LoginActivity.this).getBoolean(Constants.ISFIRST_TIME);

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
                            fetchUserTask();
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


    private void fetchUserTask(){

        // TODO: 7/18/2017 fetch user task
        List<ConnectionsModel> connectionsModels = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //ref.child(DbConstants.TABLE_CONNECTIONS)
        Queries.getUserConnectionsQuery(Preferences.getInstance(getBaseContext()).getUserFromPreference(), false, getApplicationContext());
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
        String current_uid = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS).child(current_uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.getValue() == null) {
                    Utils.showPrompt(LoginActivity.this, "This user may not exists in database");
                    Utils.hideProgress();
                } else {
                    String key = dataSnapshot.getKey();
                    Map<Object, Object> values = (Map<Object, Object>) dataSnapshot.getValue();

                    boolean deactivated= (boolean) values.get(DbConstants.DEACTIVATED);

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
                    Utils.hideProgress();

                    if (deactivated)
                    {
                        passwordView.setText("");
                        showDeactivatedDialog();
                    }else{
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