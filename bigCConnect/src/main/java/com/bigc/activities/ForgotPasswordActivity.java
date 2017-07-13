package com.bigc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc_connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgotPasswordActivity extends Activity implements OnClickListener {

    private EditText emailView;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Preferences.getInstance(ForgotPasswordActivity.this).save(Constants.ISFIRST_TIME, true);
        auth = FirebaseAuth.getInstance();
        emailView = (EditText) findViewById(R.id.inputBox);
        findViewById(R.id.signInOption).setOnClickListener(this);
        findViewById(R.id.sendButton).setOnClickListener(this);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
                "Forgot-Password Screen");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signInOption:
                finishActivity();
                break;
            case R.id.sendButton:
                GoogleAnalyticsHelper.setClickedAction(this,
                        "Recover Password Button");
                verifyEmail();
                break;
        }
    }

    private void verifyEmail() {
        final String email = emailView.getText().toString();
        if (email.length() == 0) {
            emailView.setError(Utils.loadString(ForgotPasswordActivity.this,
                    R.string.emailEmpty));
            return;
        } else if (!Utils.validateEmail(email)) {
            emailView.setError(Utils.loadString(ForgotPasswordActivity.this,
                    R.string.invalidEmail));
            return;
        }

        Utils.showProgress(ForgotPasswordActivity.this);
        Utils.sendPasswordToEmail(email,ForgotPasswordActivity.this,auth);
//		ParseQuery<ParseUser> query = ParseUser.getQuery();
//		query.whereEqualTo(DbConstants.EMAIL, email);
//		query.getFirstInBackground(new GetCallback<ParseUser>() {
//
//			@Override
//			public void done(ParseUser user, ParseException e) {
//				Utils.hideProgress();
//				if (e == null && user != null) {
//					sendPasswordToEmail(user.getString(DbConstants.NAME),
//							user.getString(DbConstants.KEY), email);
//				} else {
//					Utils.showToast(ForgotPasswordActivity.this,
//							"Email does not exist");
//				}
//			}
//		});

    }



//	private void sendPasswordToEmail(String name, String password, String email) {
//
//		Utils.showToast(ForgotPasswordActivity.this,
//				"Reset link has been sent to your email");
//		ParseUser.requestPasswordResetInBackground(email,
//				new RequestPasswordResetCallback() {
//
//					@Override
//					public void done(ParseException e) {
//						Log.e("Done", "ResetLink");
//					}
//				});
//		// Utils.sendPasswordRecoverEmail(name, password, email);
//	}

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    public void finishActivity() {
        finish();
        overridePendingTransition(R.anim.remains_same, R.anim.pull_down);
    }
}
