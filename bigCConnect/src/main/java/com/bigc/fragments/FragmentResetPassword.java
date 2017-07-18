package com.bigc.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bigc.activities.HomeScreen;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc_connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentResetPassword extends BaseFragment {

    private EditText oldPasswordView;
    private EditText newPasswordView;
    private EditText retypePasswordView;
    private static BaseFragment caller = null;
    FirebaseAuth auth;

    public FragmentResetPassword(BaseFragment caller) {
        FragmentResetPassword.caller = caller;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_resetpassword, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "Reset Password Screen");

        auth = FirebaseAuth.getInstance();
        oldPasswordView = (EditText) view.findViewById(R.id.oldPasswordInput);
        newPasswordView = (EditText) view.findViewById(R.id.newPasswordInput);
        retypePasswordView = (EditText) view.findViewById(R.id.retypePassword);

        view.findViewById(R.id.resetBtn).setOnClickListener(this);
        view.findViewById(R.id.cancelBtn).setOnClickListener(this);
        view.findViewById(R.id.forgotPasswordOption).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.resetBtn:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Reset Password Button");
                resetPassword();
                break;
            case R.id.forgotPasswordOption:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Forgot Password Button");
//			Utils.sendPasswordRecoverEmail(ParseUser.getCurrentUser()
//					.getEmail());
                Utils.sendPasswordToEmail(Preferences.getInstance(getActivity()).getString(DbConstants.EMAIL), getActivity(), auth);

//                Toast.makeText(getActivity(),
//                        "Reset link has been sent to your email", Toast.LENGTH_LONG)
//                        .show();
                break;
            case R.id.cancelBtn:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Cancel Button");
                ((HomeScreen) getActivity()).onBackPressed();
                break;
        }
    }

    @Override
    public String getName() {
        return null;
    }

    private void resetPassword() {
        final String pass = oldPasswordView.getText().toString();
        final String nPass = newPasswordView.getText().toString();
        String rPass = retypePasswordView.getText().toString();
        if (pass.length() == 0) {
            oldPasswordView.setError(Utils.loadString(getActivity(),
                    R.string.passwordEmpty));
            return;
        } else if (pass.length() < 6) {
            Toast.makeText(getActivity(),
                    Utils.loadString(getActivity(), R.string.wrongOldPassword),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (nPass.length() == 0) {
            oldPasswordView.setError(Utils.loadString(getActivity(),
                    R.string.passwordEmpty));
            return;
        } else if (nPass.length() < 6) {
            newPasswordView.setError(Utils.loadString(getActivity(),
                    R.string.tooShortPassword));
            return;
        } else if (!nPass.equals(rPass)) {
            newPasswordView.setError(Utils.loadString(getActivity(),
                    R.string.passwordMismatch));
            newPasswordView.setText("");
            retypePasswordView.setText("");
            return;
        }

        Utils.showProgress(getActivity());
//        ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(),
//                pass, new LogInCallback() {
//
//                    @Override
//                    public void done(ParseUser user, ParseException e) {
//                        if (getActivity() != null) {
//                            if (e == null) {
//                                resetPasswordOnClouds(nPass);
//                            } else {
//                                Utils.showToast(getActivity(), Utils
//                                        .loadString(getActivity(),
//                                                R.string.wrongOldPassword));
//                                Utils.hideProgress();
//                            }
//                        }
//                    }
//                });

        final FirebaseUser user = auth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(Preferences.getInstance(getActivity()).getString(DbConstants.EMAIL), pass);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(nPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Utils.showToast(getActivity(), Utils.loadString(
                                        getActivity(),
                                        R.string.passwordChangedSuccessful));
                            } else if (!task.isSuccessful()) {
//                                Utils.showToast(getActivity(), Utils.loadString(
//                                        getActivity(), R.string.operationFailed));
                                Utils.showToast(getActivity(), task.getException().toString());
                            }
                            Utils.hideProgress();
                        }
                    });
                }
            }
        });

    }

    private void resetPasswordOnClouds(final String nPassword) {
        /*ParseUser.getCurrentUser().setPassword(nPassword);
        ParseUser.getCurrentUser().put(DbConstants.KEY, nPassword);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {

                if (getActivity() != null) {
                    Utils.hideProgress();
                    if (e == null) {
                        Utils.showToast(getActivity(), Utils.loadString(
                                getActivity(),
                                R.string.passwordChangedSuccessful));
                    } else {
                        Utils.showToast(getActivity(), Utils.loadString(
                                getActivity(), R.string.operationFailed));
                    }
                }
            }
        });*/
    }

    @Override
    public int getTab() {
        return 0;
    }

    @Override
    public boolean onBackPressed() {

        if (caller != null)
            ((FragmentHolder) getActivity()).replaceFragment(caller);
        else
            ((FragmentHolder) getActivity())
                    .replaceFragment(new NewsFeedFragment());

        return true;
    }

}
