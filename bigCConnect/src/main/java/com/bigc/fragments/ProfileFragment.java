package com.bigc.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.activities.LoginActivity;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Queries;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.FragmentHolder;
import com.bigc.interfaces.SignupInterface;
import com.bigc.models.ConnectionsModel;
import com.bigc.models.Users;
import com.bigc_connect.BigcConnect;
import com.bigc_connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import eu.janmuller.android.simplecropimage.CropImage;
import eu.janmuller.android.simplecropimage.Util;

public class ProfileFragment extends BaseFragment {

    private static BaseFragment caller = null;
    private static Users user = null;
    private static boolean showProfile = false;

    private TextView nameView;
    private TextView liveView;
    private TextView typeView;
    private TextView stageView;
    private ImageView ribbonView;
    private ImageView updateProfilePicView;
    private ImageView picView;
    private View supportingView;
    private ImageView connectionView;
    private View supportersView;
    private View settingsView;
    private View notebookView;
    private View typeParentView;
    private View stageParentView;

    private File mFileTemp;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    public ProfileFragment(BaseFragment caller, Users user) {
        if (caller != null)
            ProfileFragment.caller = caller;

        if (user != null)
            ProfileFragment.user = user;
        else if (ProfileFragment.user == null) {
            // TODO: 7/13/2017 Get user from preference
            ProfileFragment.user = Preferences.getInstance(getActivity()).getUserFromPreference();
            //ProfileFragment.user = ParseUser.getCurrentUser();
        }

    }

    public static void showProfileOnBack() {
        showProfile = true;
    }

    public static Users getUser() {
        return user;
    }

    public static void setUser(Users user) {
        if (user != null)
            ProfileFragment.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_layout,
                container, false);
        nameView = (TextView) view.findViewById(R.id.nameView);
        liveView = (TextView) view.findViewById(R.id.liveView);
        stageView = (TextView) view.findViewById(R.id.stageView);
        typeView = (TextView) view.findViewById(R.id.typeView);
        connectionView = (ImageView) view.findViewById(R.id.connectOption);
        settingsView = view.findViewById(R.id.settingsIcon);
        notebookView = view.findViewById(R.id.notebookIcon);
        supportingView = view.findViewById(R.id.supportingView);
        supportersView = view.findViewById(R.id.supportersOption);
        typeParentView = view.findViewById(R.id.typeParent);
        stageParentView = view.findViewById(R.id.stageParent);
        ribbonView = (ImageView) view.findViewById(R.id.ribbonView);
        picView = (ImageView) view.findViewById(R.id.profilePicture);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        updateProfilePicView = (ImageView) view
                .findViewById(R.id.selectProfilePictureView);
        updateProfilePicView.setOnClickListener(this);
        connectionView.setOnClickListener(this);
        notebookView.setOnClickListener(this);
        view.findViewById(R.id.photosOption).setOnClickListener(this);
        view.findViewById(R.id.postsOptions).setOnClickListener(this);
        supportersView.setOnClickListener(this);
        supportingView.setOnClickListener(this);
        picView.setOnClickListener(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STRAGE_URL);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(getActivity(),
                "Profile Screen");

//        nameView.setText(ProfileFragment.user.getString(DbConstants.NAME));
        nameView.setText(Preferences.getInstance(getActivity()).getString(DbConstants.NAME));
//        String location = ProfileFragment.user.getString(DbConstants.LOCATION);
        String location = Preferences.getInstance(getActivity()).getString(DbConstants.LOCATION);
        location = location == null ? "-" : location;

        liveView.setText(location);
        picView.setImageResource(R.drawable.default_profile_pic);

        firebaseUser = firebaseAuth.getCurrentUser();
//        if (ParseUser.getCurrentUser().getObjectId().equals(user.getObjectId())) {
        if (firebaseUser.getUid().equalsIgnoreCase(Preferences.getInstance(getActivity()).getString(DbConstants.ID))) {
            connectionView.setVisibility(View.GONE);
            settingsView.setVisibility(View.VISIBLE);
            notebookView.setVisibility(View.VISIBLE);
            updateProfilePicView.setVisibility(View.VISIBLE);

            if (((BigcConnect) getActivity().getApplication())
                    .getUpdatedBitmap() != null) {
                picView.setImageBitmap(((BigcConnect) getActivity()
                        .getApplication()).getUpdatedBitmap());
            }
        } else

        {
            updateProfilePicView.setVisibility(View.GONE);
            connectionView.setVisibility(View.VISIBLE);
            settingsView.setVisibility(View.INVISIBLE);
            notebookView.setVisibility(View.INVISIBLE);

//            if (ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                    .ordinal()
//                    && user.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                    .ordinal())
            if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 1) {
                connectionView.setVisibility(View.GONE);
            } else {
                // TODO: 7/13/2017 Get if the user if a connection or not or is connection request is sent or received

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(DbConstants.TABLE_CONNECTIONS).equalTo(Preferences.getInstance(getActivity()).getString(DbConstants.ID)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            ConnectionsModel user = dataSnapshot.getValue(ConnectionsModel.class);
                            if (user.getStatus()) {
                                connectionView
                                        .setImageResource(R.drawable.ic_connected);
                                connectionView.setContentDescription("1");
                            } else {
                                connectionView
                                        .setImageResource(R.drawable.ic_connect_pending);
                                connectionView.setContentDescription("2");
                            }
                        } else {
                            connectionView
                                    .setImageResource(R.drawable.ic_connect);
                            connectionView.setContentDescription("0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

               /* ParseQuery<ParseObject> mQuery = Queries
                        .getUserConnectionStatusQuery(user);

                mQuery.fromPin(Constants.TAG_CONNECTIONS);
                mQuery.getFirstInBackground(new GetCallback<ParseObject>() {

                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null && object != null)
                            if (object.getBoolean(DbConstants.STATUS)) {
                                connectionView
                                        .setImageResource(R.drawable.ic_connected);
                                connectionView.setContentDescription("1");
                            } else {
                                connectionView
                                        .setImageResource(R.drawable.ic_connect_pending);
                                connectionView.setContentDescription("2");
                            }
                        else {
                            connectionView
                                    .setImageResource(R.drawable.ic_connect);
                            connectionView.setContentDescription("0");
                        }
                    }
                });*/
            }
        }

//        if (ProfileFragment.user.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                .ordinal())
        if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 1)

        {
            stageParentView.setVisibility(View.GONE);
            typeParentView.setVisibility(View.GONE);
            supportersView.setVisibility(View.INVISIBLE);

            ribbonView.setImageResource(R.drawable.ribbon_supporter);

        } else

        {
            stageParentView.setVisibility(View.VISIBLE);
            typeParentView.setVisibility(View.VISIBLE);
            supportersView.setVisibility(View.VISIBLE);

//            String stage = ProfileFragment.user.getString(DbConstants.STAGE);
            String stage = "";
            stage = Preferences.getInstance(getActivity()).getString(DbConstants.STAGE);
//            String type = ProfileFragment.user
//                    .getString(DbConstants.CANCER_TYPE);
            String type = "";
            type = Preferences.getInstance(getActivity()).getString(DbConstants.CANCER_TYPE);
            stage = stage == "" ? "-" : stage;

            type = type == "" ? "-" : type;

            stageView.setText(stage);
            typeView.setText(type);

//            int ribbon = ProfileFragment.user.getInt(DbConstants.RIBBON);
            int ribbon = Preferences.getInstance(getActivity()).getInt(DbConstants.RIBBON);
            if (ribbon >= 0) {

//                if (ProfileFragment.user.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//                        .ordinal())
                if (Preferences.getInstance(getActivity()).getInt(DbConstants.TYPE) == 2) {
                    ribbonView.setImageResource(Utils.fighter_ribbons[ribbon]);
                } else {
                    ribbonView.setImageResource(Utils.survivor_ribbons[ribbon]);
                }
                // ribbonView.setImageResource(Utils.survivor_ribbons[ribbon]);
            } else {
                ribbonView.setImageResource(R.drawable.ic_launcher);
            }
        }

        String url = null;
//        if (ProfileFragment.user.getParseFile(DbConstants.PROFILE_PICTURE) != null)
//            url = ProfileFragment.user
//                    .getParseFile(DbConstants.PROFILE_PICTURE).
//
//                            getUrl();
//
        if (Preferences.getInstance(getActivity()).getString(DbConstants.PROFILE_PICTURE) != null)
            url = Preferences.getInstance(getActivity()).getString(DbConstants.PROFILE_PICTURE);

        if (url != null && url.length() > 0)
            ImageLoader.getInstance().displayImage(url, picView,
                    Utils.normalDisplayOptions);

        if (((FragmentHolder) getActivity()).showProfileSettings())
            settingsView.setOnClickListener(this);
        else
            settingsView.setVisibility(View.INVISIBLE);


    }

    private void initializeTempFile() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    Constants.TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getActivity().getFilesDir(),
                    Constants.TEMP_PHOTO_FILE_NAME);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectProfilePictureView:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Update Profile Picture Button");
                initializeTempFile();
                Utils.choosePicFromGallery(this, true);
                break;
            case R.id.postsOptions:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "User-Posts Button");//// TODO: 14-07-2017
                Utils.showPrompt(getActivity(), "Functionality is in progress");
//                ((FragmentHolder) getActivity())
//                        .replaceFragment(new PostsFragments());
                break;
            case R.id.photosOption:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "User-Photos Button");
                ((FragmentHolder) getActivity())
                        .replaceFragment(new PhotosFragment());
                break;
            case R.id.supportersOption:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "User-Supporters Button");
                Utils.showPrompt(getActivity(), "Functionality is in progress");
//                ((FragmentHolder) getActivity())
//                        .replaceFragment(new SupportersFragment(true));
                break;
            case R.id.supportingView:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "User-Supporting Button");
                ((FragmentHolder) getActivity())
                        .replaceFragment(new SupportersFragment(false));
                break;
            case R.id.settingsIcon:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Profile-Settings Button");
                ((FragmentHolder) getActivity())
                        .replaceFragment(new SettingsFragment());
                break;
            case R.id.notebookIcon:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Term&Conditions Button");
                ((FragmentHolder) getActivity())
                        .replaceFragment(new TermsConditionsFragment(this));
                break;
            case R.id.connectOption:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "User-Connection Icon");
                List<Users> req = new ArrayList<>();
                req.add(user);
                ImageView iView = (ImageView) view;
                if (iView.getContentDescription().equals("0")) {
                    iView.setContentDescription("2");
                    iView.setImageResource(R.drawable.ic_connect_pending);
                    Utils.addConnections(req);
                } else {
                    iView.setContentDescription("0");
                    iView.setImageResource(R.drawable.ic_connect);
                    Utils.removeConnections(req);
                }
                break;
            case R.id.profilePicture:
                GoogleAnalyticsHelper.setClickedAction(getActivity(),
                        "Profile Picture");
//                if (user.getParseFile(DbConstants.PROFILE_PICTURE) != null)
                //                    Utils.openImageZoomView(getActivity(), ProfileFragment.user
//                            .getParseFile(DbConstants.PROFILE_PICTURE).getUrl());

                if (Preferences.getInstance(getActivity()).getString(DbConstants.PROFILE_PICTURE) != null)

                    Utils.openImageZoomView(getActivity(), Preferences.getInstance(getActivity()).getString(DbConstants.PROFILE_PICTURE));

                break;
        }
    }

    @Override
    public String getName() {
        return Constants.FRAGMENT_PROFILE;
    }

    @Override
    public int getTab() {
        return 5;
    }

    @Override
    public boolean onBackPressed() {
//        if (showProfile && !user.getObjectId().equals(
//                ParseUser.getCurrentUser().getObjectId()))
        if (showProfile && !Preferences.getInstance(getActivity()).getString(DbConstants.ID).equals(firebaseUser.getUid())) {
            user = Preferences.getInstance(getActivity()).getUserFromPreference();
            onViewCreated(getView(), null);
        } else {
            if (caller != null)
                ((FragmentHolder) getActivity()).replaceFragment(caller);
            else
                ((FragmentHolder) getActivity())
                        .replaceFragment(new NewsFeedFragment());
        }
        showProfile = false;
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK)

            switch (requestCode) {
                case Constants.CODE_SELECT_PICTURE:
                    if (data.getStringExtra("uri") != null) {
                        try {
                            String picPath = data.getStringExtra("uri");
                            Uri uri = Uri.parse(picPath);
                            InputStream inputStream = new FileInputStream(new File(
                                    uri.getPath()));
                            FileOutputStream fileOutputStream = new FileOutputStream(
                                    mFileTemp);
                            Utils.copyStream(inputStream, fileOutputStream);
                            fileOutputStream.close();
                            inputStream.close();

                            Utils.cropImage(this, mFileTemp.getPath());

                        } catch (Exception e) {

                            Log.e("SelectImage Error",
                                    "Error while creating temp file", e);
                        }
                    }
                    break;

                case Constants.PIC_CROP:
//                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    String path = data.getAction();
                    if (path == null) {
                        return;
                    }
                    Bitmap bm = BitmapFactory.decodeFile(path);

                    PictureResponseHandler(Uri.parse(path));
                    //    new PictureResponseHandler(bm).execute();


                    break;
            }
    }


    public void PictureResponseHandler(Uri uri) {
        if (uri != null) {
            Utils.showProgress(getActivity());
            FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
            final String uid = firebaseUser.getUid();
            StorageReference reference = storageReference.child(uid + ".jpg");
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                    Map<String, Object> profilemp = new HashMap<String, Object>();
                    profilemp.put(DbConstants.PROFILE_PICTURE, downloadUri.toString());
                    databaseReference.child(DbConstants.USERS).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(profilemp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ImageLoader.getInstance().displayImage(downloadUri.toString(), picView,
                                    Utils.normalDisplayOptions);
                            //    Utils.hideProgress();
                        }
                    });
                    Utils.hideProgress();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.showPrompt(getActivity(), e.toString().trim());

                }
            });
        }

    }

    private class PictureResponseHandler extends
            AsyncTask<String, Void, Bitmap> {

        private Bitmap bm;

        public PictureResponseHandler(Bitmap bitmap) {
            this.bm = bitmap;
        }

        @Override
        public void onPreExecute() {
            Utils.showProgress(ProfileFragment.this.getActivity());
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (bm == null)
                return null;

            byte[] bmData = Utils.getBitmapData(bm);
            if (bmData == null)
                return null;

            ParseFile file = new ParseFile("profilePic.png", bmData);
            ParseUser.getCurrentUser().put(DbConstants.PROFILE_PICTURE, file);
            final BigcConnect application = (BigcConnect) ProfileFragment.this
                    .getActivity().getApplication();
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    try {
                        if (application != null)
                            application.setUpdatedBitmap(null);
                        Log.e("Application", "Updated");
                    } catch (Exception e1) {

                    }
                }
            });
            return bm;
        }

        @Override
        public void onPostExecute(Bitmap img) {
            Utils.hideProgress();
            if (ProfileFragment.this.getActivity() != null) {
                if (img != null) {
                    ((BigcConnect) ProfileFragment.this.getActivity()
                            .getApplication()).setUpdatedBitmap(img);
                    if (picView != null)
                        picView.setImageBitmap(img);

                } else
                    Toast.makeText(ProfileFragment.this.getActivity(),
                            "Error in loading image, try again",
                            Toast.LENGTH_LONG).show();
            }
        }
    }

}
