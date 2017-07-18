package com.bigc.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bigc.general.classes.Constants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc_connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class DeactivateSecurityFragment extends BaseFragment {

    private EditText passwordView;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deactivate_security,
                container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        passwordView = (EditText) view.findViewById(R.id.passwordInput);
        view.findViewById(R.id.continueBtn).setOnClickListener(this);
        view.findViewById(R.id.cancelBtn).setOnClickListener(this);
        view.findViewById(R.id.forgotPasswordOption).setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "Deactivate-Password-Input Screen");

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueBtn:
                if (passwordView.getText().length() == 0) {
                    Utils.showToast(getActivity(), "Enter password");
                    return;
                }
                validatePassword(passwordView.getText().toString());
                break;
            case R.id.cancelBtn:
                onBackPressed();
                break;
            case R.id.forgotPasswordOption:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Forgot Password Button");
                Utils.sendPasswordRecoverEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                Toast.makeText(getActivity(),
                        "Reset link has been sent to your email", Toast.LENGTH_LONG)
                        .show();
                break;
        }
    }

    private void validatePassword(String password) {
        Utils.showProgress(getActivity());
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (DeactivateSecurityFragment.this != null && getActivity() != null) {
                                Utils.hideProgress();
                                ((FragmentHolder) getActivity())
                                        .replaceFragment(new DeactivateFragment());
                            }
                        } else {
                            String error = task.getException().toString();
                            String message = "";
                            if (error.equalsIgnoreCase("com.google.firebase.auth.FirebaseAuthInvalidUserException:" +
                                    " There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                message = "Incorrect User ID";
                            }
                            if (error.equalsIgnoreCase("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: " +
                                    "The password is invalid or the user does not have a password.")) {
                                message = "Incorrect password";
                            }

                            Log.i("deactivate_error", task.getException().toString());
                            Utils.showPrompt(getActivity(), message);
                            Utils.hideProgress();
                        }

                    }
                }
        );
//		ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(),
//				password, new LogInCallback() {
//
//					@Override
//					public void done(ParseUser user, ParseException e) {
//						if (DeactivateSecurityFragment.this != null
//								&& getActivity() != null) {
//							Utils.hideProgress();
//							if (e == null) {
//								((FragmentHolder) getActivity())
//										.replaceFragment(new DeactivateFragment());
//
//							} else {
//								Utils.showToast(getActivity(),
//										"Incorrect password");
//							}
//						}
//					}
//				});
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_DEACTIVATE_SECURITY;
    }

    @Override
    public int getTab() {
        return 5;
    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity())
                .replaceFragment(new SettingsFragment());
        return true;
    }
}
