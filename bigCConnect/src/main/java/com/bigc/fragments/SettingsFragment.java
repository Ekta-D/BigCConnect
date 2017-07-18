package com.bigc.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.activities.SignupActivity;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends BaseFragment {

    private EditText nameView;
    private EditText stageView;
    private EditText livesView;
    private TextView statusView;
    private EditText emailView;
    private RadioGroup privacyButtons;
    private RadioGroup notificationsButtons;
    private LinearLayout privacyParent;
    private LinearLayout stageParent;
    DatabaseReference databaseReference;
    Map<String, Object> updated_values;
    String current_userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);

        updated_values = new HashMap<>();
        current_userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        nameView = (EditText) view.findViewById(R.id.nameInputView);
        stageView = (EditText) view.findViewById(R.id.stageInputView);
        emailView = (EditText) view.findViewById(R.id.emailInputView);
        statusView = (TextView) view.findViewById(R.id.statusView);
        livesView = (EditText) view.findViewById(R.id.livesInputView);
        privacyButtons = (RadioGroup) view.findViewById(R.id.privacyGroup);
        privacyParent = (LinearLayout) view
                .findViewById(R.id.privacyViewParent);
        stageParent = (LinearLayout) view
                .findViewById(R.id.stageViewParent);
        notificationsButtons = (RadioGroup) view
                .findViewById(R.id.notificationsGroup);

        view.findViewById(R.id.nameViewParent).setOnClickListener(this);
        view.findViewById(R.id.emailViewParent).setOnClickListener(this);
        view.findViewById(R.id.statusViewParent).setOnClickListener(this);
        view.findViewById(R.id.LiveViewParent).setOnClickListener(this);
        view.findViewById(R.id.changePasswordParent).setOnClickListener(this);
        view.findViewById(R.id.accountDeativateParent).setOnClickListener(this);
        view.findViewById(R.id.notebookIcon).setOnClickListener(this);
        view.findViewById(R.id.stageViewParent).setOnClickListener(this);


        nameView.setOnClickListener(this);
        emailView.setOnClickListener(this);
        livesView.setOnClickListener(this);
        stageView.setOnClickListener(this);

        nameView.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Utils.hideKeyboard(getActivity());
                    updateName();
                    return true;
                }
                return false;
            }
        });

        stageView.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Utils.hideKeyboard(getActivity());
                    updateStage();
                    return true;
                }
                return false;
            }
        });

        privacyButtons.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.noCheckBox) {
//                            ParseUser.getCurrentUser().put(
//                                    DbConstants.VISIBILITY, Constants.PRIVATE);
                    Preferences.getInstance(getActivity()).save(DbConstants.VISIBILITY, Constants.PRIVATE);
                    updated_values.put(DbConstants.VISIBILITY, Constants.PRIVATE);
                    Preferences.getInstance(getActivity()).save(DbConstants.VISIBILITY, Constants.PRIVATE);

                    Log.e("Profile", "Private");
                } else {
//                    ParseUser.getCurrentUser().put(
//                            DbConstants.VISIBILITY, Constants.PUBLIC);
                    Preferences.getInstance(getActivity()).save(DbConstants.VISIBILITY, Constants.PUBLIC);
                    updated_values.put(DbConstants.VISIBILITY, Constants.PUBLIC);
                    Preferences.getInstance(getActivity()).save(DbConstants.VISIBILITY, Constants.PUBLIC);
                    Log.e("Profile", "Public");
                }
                databaseReference.child(DbConstants.USERS).child(current_userId).updateChildren(updated_values);

                //  ParseUser.getCurrentUser().saveInBackground();
            }
        });

        /*if (ParseInstallation.getCurrentInstallation().containsKey(
                Constants.NOTIFICATIONS)
                && ParseInstallation.getCurrentInstallation().getBoolean(
                Constants.NOTIFICATIONS) == false) {
            notificationsButtons.check(R.id.offCheckBox);
        } else {
            notificationsButtons.check(R.id.onCheckbox);
        }
*/
        notificationsButtons
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.offCheckBox) {
                            Log.e("Notifications", "Off");
                            Utils.disablePushes();
                        } else {
                            Log.e("Notifications", "On");
                            Utils.enablePushes();
                        }
                    }
                });

        emailView.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Utils.hideKeyboard(getActivity());
                    updateEmail();
                    return true;
                }
                return false;
            }
        });

        livesView.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Utils.hideKeyboard(getActivity());
                    updateLocation();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "Profile-Settings Screen");

    }

    private void updateEmail() {
        String email = emailView.getText().toString();
        if (email.length() == 0) {
            Toast.makeText(getActivity(), "Please enter email",
                    Toast.LENGTH_LONG).show();
            return;
        } else if (!Utils.validateEmail(email)) {
            Toast.makeText(getActivity(),
                    "Invalid email address, Please re-enter.",
                    Toast.LENGTH_LONG).show();
            return;
        }
//        ParseUser.getCurrentUser().setEmail(email);
//        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    switchToDisableMode(emailView);
//                }
//            }
//        });
        Preferences.getInstance(getActivity()).save(DbConstants.EMAIL, email);
        updated_values.put(DbConstants.EMAIL, email);
        databaseReference.child(DbConstants.USERS).child(current_userId).updateChildren(updated_values);
    }

    private void updateName() {
        String name = nameView.getText().toString();
        if (name.length() == 0) {
            Toast.makeText(getActivity(), "Please enter name",
                    Toast.LENGTH_LONG).show();
            return;
        }
        updated_values.put(DbConstants.NAME, name);
        updated_values.put(DbConstants.NAME_LOWERCASE, name.toLowerCase());
        Preferences.getInstance(getActivity()).save(DbConstants.NAME, name);
        Preferences.getInstance(getActivity()).save(DbConstants.NAME_LOWERCASE, name.toLowerCase());
        databaseReference.child(DbConstants.USERS).child(current_userId).updateChildren(updated_values);

//        ParseUser.getCurrentUser().put(DbConstants.NAME, name);
//        ParseUser.getCurrentUser().put(DbConstants.NAME_LOWERCASE,
//                name.toLowerCase());
//        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    switchToDisableMode(nameView);
//                }
//            }
//        });
    }

    private void updateStage() {
        String stage = stageView.getText().toString();
        if (stage.length() == 0) {
            Toast.makeText(getActivity(), "Please enter stage",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Preferences.getInstance(getActivity()).save(DbConstants.STAGE, stage);
        updated_values.put(DbConstants.STAGE, stage);
        databaseReference.child(DbConstants.USERS).child(current_userId).updateChildren(updated_values);

//        ParseUser.getCurrentUser().put(DbConstants.STAGE, stage);
//        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    switchToDisableMode(stageView);
//                }
//            }
//        });

    }

    private void updateLocation() {
        String location = livesView.getText().toString();
        if (location.length() == 0) {
            Toast.makeText(getActivity(), "Please enter your location",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Preferences.getInstance(getActivity()).save(DbConstants.LOCATION, location);
        updated_values.put(DbConstants.LOCATION, location);
        databaseReference.child(DbConstants.USERS).child(current_userId).updateChildren(updated_values);
//        ParseUser.getCurrentUser().put(DbConstants.LOCATION, location);
//        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    switchToDisableMode(livesView);
//                }
//            }
//        });

    }

    @Override
    public void onPause() {
        super.onPause();

        Utils.hideKeyboard(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
//		nameView.setText(ParseUser.getCurrentUser().getString(DbConstants.NAME));
//		emailView.setText(ParseUser.getCurrentUser().getString(
//				DbConstants.EMAIL));
        nameView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.NAME));
        emailView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.EMAIL));

//        if (ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                .ordinal())
        if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 1) {
            statusView.setText("Supporter");
        }
//        else if (ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//                .ordinal())
        else if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 2) {
            statusView.setText("Fighter");
        } else {
            statusView.setText("Survivor");
        }
//        livesView.setText(ParseUser.getCurrentUser().getString(
//                DbConstants.LOCATION) == null ? "" : ParseUser.getCurrentUser()
//                .getString(DbConstants.LOCATION));
        livesView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.LOCATION)
                == null ? "" : Preferences.getInstance(getActivity()).getString(DbConstants.LOCATION));

//        if (ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                .ordinal())
        if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 1) {
            privacyParent.setVisibility(View.GONE);
            stageParent.setVisibility(View.GONE);

        } else {
            privacyParent.setVisibility(View.VISIBLE);
            stageParent.setVisibility(View.VISIBLE);
//           if (ParseUser.getCurrentUser().getInt(DbConstants.VISIBILITY) == Constants.PRIVATE)
            if (Preferences.getInstance(getActivity()).getInt(DbConstants.VISIBILITY) == Constants.PRIVATE) {
                privacyButtons.check(R.id.noCheckBox);
            } else {
                privacyButtons.check(R.id.yesCheckbox);
            }

            // stageView.setText(ParseUser.getCurrentUser().getString(DbConstants.STAGE) == null ? "" : ParseUser.getCurrentUser().getString(DbConstants.STAGE));
            stageView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.STAGE) == null ? "" : Preferences.getInstance(getActivity()).getString(DbConstants.STAGE));
        }
    }

    @Override
    public void onClick(View v) {
//        ParseUser user = ParseUser.getCurrentUser();
        switch (v.getId()) {
            case R.id.notebookIcon:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Term&Conditions Button");
                ((FragmentHolder) getActivity())
                        .replaceFragment(new TermsConditionsFragment(this));
                break;
            case R.id.emailViewParent:
            case R.id.emailInputView:
//                nameView.setText(user.getString(DbConstants.NAME));
                nameView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.NAME));
                switchToDisableMode(nameView);
//                livesView.setText(user.getString(DbConstants.LOCATION) == null ? ""
//                        : user.getString(DbConstants.LOCATION));
                livesView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.LOCATION) == null ? ""
                        : Preferences.getInstance(getActivity()).getString(DbConstants.LOCATION));
                switchToDisableMode(livesView);
                switchToEditMode(emailView);
                switchToDisableMode(stageView);
                break;
            case R.id.nameViewParent:
            case R.id.nameInputView:
//                livesView.setText(user.getString(DbConstants.LOCATION) == null ? ""
//                        : user.getString(DbConstants.LOCATION));
                livesView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.LOCATION) == null ? ""
                        : Preferences.getInstance(getActivity()).getString(DbConstants.LOCATION));

                switchToDisableMode(livesView);
//                emailView.setText(user.getEmail());
                emailView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.EMAIL));
                switchToDisableMode(emailView);
                switchToEditMode(nameView);
                switchToDisableMode(stageView);
                break;
            case R.id.stageViewParent:
            case R.id.stageInputView:
//                livesView.setText(user.getString(DbConstants.LOCATION) == null ? ""
//                        : user.getString(DbConstants.LOCATION));
                livesView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.LOCATION) == null ? ""
                        : Preferences.getInstance(getActivity()).getString(DbConstants.LOCATION));

                switchToDisableMode(livesView);
                emailView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.EMAIL));
//                emailView.setText(user.getEmail());
                switchToDisableMode(emailView);
                switchToDisableMode(nameView);
                switchToEditMode(stageView);
                break;
            case R.id.LiveViewParent:
            case R.id.livesInputView:
                nameView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.NAME));
                //  nameView.setText(user.getString(DbConstants.NAME));
                switchToDisableMode(nameView);
                emailView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.EMAIL));
//                emailView.setText(user.getEmail());
                switchToDisableMode(emailView);
                switchToDisableMode(stageView);
                switchToEditMode(livesView);
                break;
            case R.id.changePasswordParent:
                ((FragmentHolder) getActivity())
                        .replaceFragment(new FragmentResetPassword(this));
                break;
            case R.id.accountDeativateParent:
                ((FragmentHolder) getActivity())
                        .replaceFragment(new DeactivateSecurityFragment());
                break;
            case R.id.statusViewParent:
                Intent i = new Intent(getActivity(), SignupActivity.class);
                i.putExtra("statusUpdate", true);
                startActivityForResult(i, 1);
                getActivity().overridePendingTransition(R.anim.pull_up,
                        R.anim.remains_same);
                break;
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        switch (reqCode) {
            case 1:
                if (resCode == Activity.RESULT_OK) {
                    Toast.makeText(getActivity(), "Applying your new settings",
                            Toast.LENGTH_LONG).show();
                    Utils.showProgress(getActivity());
//                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//
//                        @Override
//                        public void done(ParseException e) {
//                            Utils.hideProgress();
//                        }
//                    });
                }
                break;
        }
    }

    private void switchToEditMode(EditText editView) {
        editView.setTextColor(Color.BLACK);
        editView.setFocusableInTouchMode(true);
        editView.setFocusable(true);
        editView.requestFocus();
        editView.setBackgroundResource(R.drawable.settings_edittext_border);
    }

    private void switchToDisableMode(EditText editView) {
        editView.setTextColor(getResources().getColor(R.color.gray_1));
        editView.setBackgroundResource(R.drawable.settings_edittext_normal);
        editView.setFocusable(false);
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_SETTINGS;
    }

    @Override
    public int getTab() {
        return 5;
    }

    @Override
    public boolean onBackPressed() {
        ((FragmentHolder) getActivity()).replaceFragment(new ProfileFragment(
                null, null));
        return true;
    }
}
