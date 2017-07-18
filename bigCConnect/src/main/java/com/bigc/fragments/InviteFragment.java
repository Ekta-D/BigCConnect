package com.bigc.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigc.activities.LoginActivity;
import com.bigc.activities.SignupActivity;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.LoadImageObserver;
import com.bigc_connect.R;

public class InviteFragment extends Fragment implements View.OnClickListener,
        LoadImageObserver {

    public static InviteFragment newInstance() {
        return new InviteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_invite, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "SignUp Invite Screen");

        view.findViewById(R.id.skipOption).setOnClickListener(this);
        view.findViewById(R.id.yesButton).setOnClickListener(this);
        view.findViewById(R.id.noButton).setOnClickListener(this);

//		if (ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//				.ordinal())
        if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 1) {
            ((TextView) view
                    .findViewById(R.id.inviteSupporterDescriptionTextView))
                    .setText(R.string.inviteSupportersDescription);
        } else {
            ((TextView) view
                    .findViewById(R.id.inviteSupporterDescriptionTextView))
                    .setText(R.string.inviteSurvivorsDescription);
        }

        System.gc();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skipOption:
            case R.id.noButton:
                finishSignup();
                break;
            case R.id.yesButton:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Invite Supporters Button");
                Utils.inviteSupporters(getActivity(), true);
                break;
        }
    }

    // private void showInviteByDialog() {
    // new AlertDialog.Builder(getActivity())
    // .setTitle(R.string.inviteSupporter)
    // .setMessage(R.string.selectMedium)
    // .setPositiveButton(R.string.bySMS,
    // new DialogInterface.OnClickListener() {
    //
    // @Override
    // public void onClick(DialogInterface dialog,
    // int which) {
    // inviteBySMS();
    // }
    // })
    // .setNegativeButton(R.string.byEmail,
    // new DialogInterface.OnClickListener() {
    //
    // @Override
    // public void onClick(DialogInterface dialog,
    // int which) {
    // inviteByEmail();
    // }
    // }).create().show();
    // }

    // private void inviteBySMS() {
    // Uri uri = Uri.parse("smsto:");
    // Intent it = new Intent(Intent.ACTION_SENDTO, uri);
    // it.putExtra(
    // "sms_body",
    // "Hi,\nI would like you to invite on BigC-Connect application. It is a awesome platform for Cancer's survivors & supporters.");
    // startActivity(it);
    // }

    private void finishSignup() {
        Utils.showProgress(getActivity());
        //  Utils.showToast(getActivity(), "Setting your profile, please wait");
//        Toast.makeText(getActivity(), "Setting your profile, please wait",
//                Toast.LENGTH_LONG).show();

        goToProfile();
//        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//
//            @Override
//            public void done(ParseException e) {
//                Utils.hideProgress();
//                if (e == null) {
//                    if (getActivity() != null) {
//                        goToProfile();
//                    }
//                } else {
//                    e.printStackTrace();
//                    if (getActivity() != null) {
//                        Toast.makeText(getActivity(),
//                                "Error in saving settings, try again",
//                                Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });
    }

    private void goToProfile() {
//        Intent i = new Intent(getActivity(), HomeScreen.class);
//        i.putExtra("fragment", Constants.FRAGMENT_PROFILE);
//        startActivity(i);
//        getActivity().finish();
        Utils.showAlert(getActivity(), "Account created successfully!" + " " + "you can login now");
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
        Utils.hideProgress();

    }

    // private void inviteByEmail() {
    // Intent intent = new Intent(Intent.ACTION_SEND);
    // intent.setType("text/plain");
    // intent.putExtra(Intent.EXTRA_EMAIL, "");
    // intent.putExtra(Intent.EXTRA_SUBJECT, "Invitation to BigC-Connect");
    // intent.putExtra(
    // Intent.EXTRA_TEXT,
    // "Hi,\nI would like you to invite on BigC-Connect application. It is a awesome platform for Cancer's survivors & supporters.");
    // getActivity().startActivityForResult(
    // Intent.createChooser(intent, "Invite Supporter/Survivor"),
    // ((SignupActivity) getActivity()).INVITE_SUPPORTERS_CODE);
    // }

    @Override
    public void onResume() {
        super.onResume();
        ((SignupActivity) getActivity()).setObserver(this);
    }

    @Override
    public void onNotify() {
        finishSignup();
    }

}