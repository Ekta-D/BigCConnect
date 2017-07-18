package com.bigc.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.activities.SignupActivity;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.SignupInterface;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SelectionFragment extends Fragment implements View.OnClickListener {

    private ImageView survivorView;
    private ImageView supporterView;
    private ImageView fighterView;
    private TextView continueButton;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    Map<String, Object> updated_values;
//    String current_userId;

    public static SelectionFragment newInstance() {
        return new SelectionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "SignUp User-Type-Selection Screen");

        continueButton = (TextView) view.findViewById(R.id.continueButton);
//        current_userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        updated_values = new HashMap<>();
        continueButton.setOnClickListener(this);

        supporterView = (ImageView) view.findViewById(R.id.supporterView);
        survivorView = (ImageView) view.findViewById(R.id.survivorView);
        fighterView = (ImageView) view.findViewById(R.id.fighterView);

        fighterView.setOnClickListener(this);
        supporterView.setOnClickListener(this);
        survivorView.setOnClickListener(this);

        if (((SignupInterface) getActivity()).isStatusUpdate()) {
            ((TextView) view.findViewById(R.id.signInOption)).setText(Html
                    .fromHtml("<u>Cancel</u>"));
            view.findViewById(R.id.signInOption).setOnClickListener(this);

//            if (ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                    .ordinal())
            if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 1) {
                survivorView.setImageResource(R.drawable.survivor_img);
                survivorView.setContentDescription("0");

                fighterView.setImageResource(R.drawable.fighter_img);
                fighterView.setContentDescription("0");

                supporterView.setImageResource(R.drawable.supporter_selected_img);
                supporterView.setContentDescription("1");
            }
//            else if (ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//                    .ordinal())
            else if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 2) {
                survivorView.setImageResource(R.drawable.survivor_img);
                survivorView.setContentDescription("0");

                fighterView.setImageResource(R.drawable.fighter_selected_img);
                fighterView.setContentDescription("1");

                supporterView.setImageResource(R.drawable.supporter_img);
                supporterView.setContentDescription("0");
            } else {
                survivorView.setImageResource(R.drawable.survivor_selected_img);
                survivorView.setContentDescription("1");

                fighterView.setImageResource(R.drawable.fighter_img);
                fighterView.setContentDescription("0");

                supporterView.setImageResource(R.drawable.supporter_img);
                supporterView.setContentDescription("0");
            }
        } else {
            view.findViewById(R.id.signInOption).setOnClickListener(this);
            survivorView.setImageResource(R.drawable.survivor_selected_img);
            survivorView.setContentDescription("1");
        }

        System.gc();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continueButton:
                continueButton.setClickable(false);
                if (supporterView.getContentDescription().equals("0")
                        && fighterView.getContentDescription().equals("0")
                        && survivorView.getContentDescription().equals("0")) {
                    Toast.makeText(getActivity(), "Please make any selection",
                            Toast.LENGTH_LONG).show();

                } else {
                    if (((SignupInterface) getActivity()).isStatusUpdate()) {
                        if (survivorView.getContentDescription().equals("1")) {
//                            ParseUser.getCurrentUser().put(DbConstants.TYPE,
//                                    Constants.USER_TYPE.SURVIVOR.ordinal());
                            Preferences.getInstance(getActivity()).save(DbConstants.TYPE, 0);
                            updated_values.put(DbConstants.TYPE, 0);
                            //   mDatabase.child(DbConstants.USERS).child(current_userId).updateChildren(updated_values);

                            ((SignupInterface) getActivity())
                                    .replaceFragment(RibbonSelectionFragment
                                            .newInstance());
                        } else if (fighterView.getContentDescription().equals("1")) {
//                            ParseUser.getCurrentUser().put(DbConstants.TYPE,
//                                    Constants.USER_TYPE.FIGHTER.ordinal());
                            if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 2)
                                ((SignupInterface) getActivity())
                                        .replaceFragment(RibbonSelectionFragment
                                                .newInstance());
                        } else {
//                            ParseUser.getCurrentUser().put(DbConstants.TYPE,
//                                    Constants.USER_TYPE.SUPPORTER.ordinal());
                            if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 1)
                                ((SignupInterface) getActivity())
                                        .finishWithAnimation(Activity.RESULT_OK);
                        }
                    } else {
                        createUser();
                    }
                }
                continueButton.setClickable(true);
                break;
            case R.id.signInOption:
                if (((SignupInterface) getActivity()).isStatusUpdate()) {
                    ((SignupInterface) getActivity())
                            .finishWithAnimation(Activity.RESULT_CANCELED);
                } else {
                    ((SignupInterface) getActivity()).launchLogin();
                }
                break;
            case R.id.supporterView:

                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Supporter Option");
                if (supporterView.getContentDescription().equals("0")) {
                    survivorView.setImageResource(R.drawable.survivor_img);
                    survivorView.setContentDescription("0");

                    fighterView.setImageResource(R.drawable.fighter_img);
                    fighterView.setContentDescription("0");

                    supporterView
                            .setImageResource(R.drawable.supporter_selected_img);
                    supporterView.setContentDescription("1");
                }
                break;
            case R.id.survivorView:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Survivor Option");
                if (survivorView.getContentDescription().equals("0")) {
                    survivorView.setImageResource(R.drawable.survivor_selected_img);
                    survivorView.setContentDescription("1");

                    fighterView.setImageResource(R.drawable.fighter_img);
                    fighterView.setContentDescription("0");

                    supporterView.setImageResource(R.drawable.supporter_img);
                    supporterView.setContentDescription("0");
                }
                break;
            case R.id.fighterView:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Fighter Option");
                if (fighterView.getContentDescription().equals("0")) {
                    fighterView.setImageResource(R.drawable.fighter_selected_img);
                    fighterView.setContentDescription("1");

                    survivorView.setImageResource(R.drawable.survivor_img);
                    survivorView.setContentDescription("0");

                    supporterView.setImageResource(R.drawable.supporter_img);
                    supporterView.setContentDescription("0");
                }
                break;
        }
    }

    private void createUser() {
        Utils.showProgress(getActivity());
        int type = Constants.USER_TYPE.SUPPORTER.ordinal();
        if (survivorView.getContentDescription().equals("1"))
            type = Constants.USER_TYPE.SURVIVOR.ordinal();
        else if (fighterView.getContentDescription().equals("1"))
            type = Constants.USER_TYPE.FIGHTER.ordinal();

//        ParseUser user = new ParseUser();
//        user.setUsername(((SignupActivity) getActivity()).email);
//        user.setEmail(((SignupActivity) getActivity()).email);
//        user.setPassword(((SignupActivity) getActivity()).password);
//        user.put(DbConstants.KEY, ((SignupActivity) getActivity()).password);
//        user.put(DbConstants.NAME, ((SignupActivity) getActivity()).name);
//        user.put(DbConstants.NAME_LOWERCASE,
//                ((SignupActivity) getActivity()).name.toLowerCase());
//        user.put(DbConstants.TYPE, type);
//        user.put(DbConstants.RIBBON, -1);
//        user.signUpInBackground(new SignUpCallback() {
//
//            @Override
//            public void done(ParseException e) {
//                Utils.hideProgress();
//                if (e == null) {
//                    userCreated();
//                } else {
//                    if (getActivity() != null) {
//                        Toast.makeText(getActivity(),
//                                "Something goes wrong, Please try again",
//                                Toast.LENGTH_LONG).show();
//                        getActivity().onBackPressed();
//                        getActivity().onBackPressed();
//                        getActivity().onBackPressed();
//                    }
//                }
//            }
//        });
        final int finalType = type;
        String email_user = ((SignupActivity) getActivity()).email;
        String password_user = ((SignupActivity) getActivity()).password;
        firebaseAuth.createUserWithEmailAndPassword(email_user, password_user)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Users users = new Users();
                            SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT);
                            String date = format.format(new Date(System.currentTimeMillis()));
                            FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                            String uid = firebaseUser.getUid();
                            users.setEmail(((SignupActivity) getActivity()).email);
                            users.setName(((SignupActivity) getActivity()).name);
                            users.setType(finalType);
                            users.setName_lowercase(((SignupActivity) getActivity()).name.toLowerCase());

                            users.setCreatedAt(date);

                            mDatabase.child(DbConstants.USERS).child(uid).setValue(users);

                            Preferences.getInstance(getActivity()).save(DbConstants.NAME, users.getName());
                            Preferences.getInstance(getActivity()).save(DbConstants.EMAIL, users.getEmail());
                            Preferences.getInstance(getActivity()).save(DbConstants.TYPE, users.getType());
                            Preferences.getInstance(getActivity()).save(DbConstants.NAME_LOWERCASE, users.getName_lowercase());
                            Preferences.getInstance(getActivity()).save(DbConstants.CREATED_AT, date);
                            userCreated();
                        }
                        if (!task.isSuccessful())

                        {
                            Log.i("firebase_signup", task.getException().toString());
                            String error = task.getException().toString().trim();

                            //   Utils.showAlert(getActivity(), task.getException().toString());
                        }
                    }
                });
    }

    private void userCreated() {
        //  Utils.registerDeviceForNotifications();
        //  Utils.sendWelcomeEmail();
        if (getActivity() != null) {
//            Toast.makeText(getActivity(), "Account is created",
//                    Toast.LENGTH_LONG).show();
            Utils.showAlert(getActivity(), "Account is created");
            ((SignupInterface) getActivity())
                    .replaceFragment(ProfilePicFragment.newInstance());

            Utils.hideProgress();
        }
    }

}