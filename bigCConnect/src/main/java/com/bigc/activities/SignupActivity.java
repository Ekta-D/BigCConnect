package com.bigc.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.bigc.datastorage.Preferences;
import com.bigc.fragments.EmailInputFragment;
import com.bigc.fragments.RibbonSelectionFragment;
import com.bigc.fragments.SelectionFragment;
import com.bigc.fragments.SurvivorSearch;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.LoadImageObservable;
import com.bigc.interfaces.LoadImageObserver;
import com.bigc.interfaces.SignupInterface;
import com.bigc_connect.R;
import com.google.android.gms.nearby.connection.dev.Strategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class SignupActivity extends FragmentActivity implements
        SignupInterface, LoadImageObservable {

    private LoadImageObserver ob;
    private volatile boolean mReturningWithResult = false;
    private boolean isStatusUpdate = false;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    public String email;
    public String password;
    public String name;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        Preferences.getInstance(SignupActivity.this).save(Constants.ISFIRST_TIME, true);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STRAGE_URL);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        try {
            isStatusUpdate = getIntent().getBooleanExtra("statusUpdate", false);
        } catch (Exception e) {
            isStatusUpdate = false;
        }
        fragmentManager = getSupportFragmentManager();

        if (isStatusUpdate) {
            replaceFragment(SelectionFragment.newInstance());
        } else {
            replaceFragment(EmailInputFragment.newInstance());
        }
        GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
                "SignUp Parent Screen");

    }

    @Override
    public boolean isStatusUpdate() {
        return isStatusUpdate;
    }

    @Override
    public void onBackPressed() {

        if (isStatusUpdate) {
            finishWithAnimation(fragmentManager.getBackStackEntryCount() == 1 ? Activity.RESULT_CANCELED
                    : Activity.RESULT_OK);
            return;
        }

        if (fragmentManager.getBackStackEntryCount() == 1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (fragmentManager.getBackStackEntryCount() == 5) {
            showExitDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this).setTitle("Exit")
                .setMessage("Do you want to quit sign up process?")
                .setPositiveButton("Yes", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SignupActivity.this.finish();
                    }
                }).setNegativeButton("No", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left);
//		fragmentTransaction.replace(android.R.id.tabcontent, fragment,
//				fragment.getTag());
        fragmentTransaction.replace(R.id.tabcontent, fragment, fragment.getTag());
        fragmentTransaction.addToBackStack(fragment.getTag());
        try {
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void launchLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("onActivityResult", "called--" + requestCode);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case Constants.CODE_SELECT_PICTURE:
//                    new PictureResponseHandler()
//                            .execute(data.getStringExtra("uri"));
                    String uri = data.getStringExtra("uri");
                    File file = new File(uri);
                    Uri uri1 = Uri.fromFile(file);
                    Utils.saveObjectId(databaseReference);
                    Utils.showProgress(SignupActivity.this);

                    PictureResponseHandler(uri1);

                    break;
                case Constants.CODE_TAKE_PHOTO_CODE:
//                    new PictureResponseHandler()
//                            .execute(Constants.currentCameraIntentURI);
                    Utils.saveObjectId(databaseReference);
                    Utils.showProgress(SignupActivity.this);
                    PictureResponseHandler(Uri.parse(Constants.currentCameraIntentURI));
                    break;
                case Constants.CODE_INVITE_SUPPORTERS:
                    Log.e("Invite", "called");
                    notifyObserver();
                    break;
            }
    }

    public void PictureResponseHandler(Uri uri) {
        if (uri != null) {
            FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
            final String uid = firebaseUser.getUid();
            StorageReference reference = storageReference.child(uid + ".jpg");
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                    Map<String, Object> profilemp = new HashMap<String, Object>();
                    profilemp.put("profile_picture", downloadUri.toString());
                    databaseReference.child(DbConstants.USERS).child(uid).updateChildren(profilemp, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Utils.hideProgress();

                            //ekta
                            if (Preferences.getInstance(SignupActivity.this).getInt(DbConstants.TYPE)==1)
                            {
                                ((SignupInterface) SignupActivity.this).replaceFragment(SurvivorSearch
                                        .newInstance());
                            }
                            else{
                                ((SignupInterface) SignupActivity.this)
                                        .replaceFragment(RibbonSelectionFragment.newInstance());
                            }
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.showPrompt(SignupActivity.this, e.toString().trim());

                }
            });
        }

    }

    private class PictureResponseHandler extends
            AsyncTask<String, Void, Boolean> {

        @Override
        public void onPreExecute() {
            Utils.showProgress(SignupActivity.this);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (params == null || params.length == 0)
                return false;
            return handleBitmap(Utils.setRotation(params[0]));
        }

        @Override
        public void onPostExecute(Boolean status) {
            Utils.hideProgress();
            if (SignupActivity.this != null) {
                if (status)
                    notifyObserver();
                else
                    Toast.makeText(SignupActivity.this,
                            "Error in loading image, try again",
                            Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void setObserver(LoadImageObserver ob) {
        if (ob != null)
            this.ob = ob;
    }

    @Override
    public void notifyObserver() {
        if (ob != null)
            ob.onNotify();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mReturningWithResult) {
            notifyObserver();
        }
        mReturningWithResult = false;
    }

    public boolean handleBitmap(Bitmap bitmap) {
        try {
            Log.e("Handle Bitmap", (bitmap == null) + "");
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            byte[] bitmapdata = outStream.toByteArray();
            outStream.close();
            ParseFile file = new ParseFile("profilePic.png", bitmapdata);
            ParseUser.getCurrentUser().put(DbConstants.PROFILE_PICTURE, file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void finishWithAnimation(int resultCode) {
        setResult(resultCode);
        finish();
        overridePendingTransition(R.anim.remains_same, R.anim.pull_down);
    }

    // public boolean handleChooseImageResponse(Intent data) {
    // InputStream stream = null;
    // try {
    // return handleBitmap(setRotation(data.getStringExtra("uri")));//
    // BitmapFactory.decodeStream(stream));
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // if (stream != null)
    // try {
    // stream.close();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // return false;
    // }

    // private class HandleImageResponse extends AsyncTask<String, Void,
    // Boolean> {
    //
    // @Override
    // public void onPreExecute() {
    // Utils.showProgress(SignupActivity.this);
    // }
    //
    // @Override
    // protected Boolean doInBackground(String... params) {
    // if (params == null || params.length == 0)
    // return false;
    // return handleBitmap(setRotation(params[0]));
    // }
    //
    // @Override
    // public void onPostExecute(Boolean status) {
    // Utils.hideProgress();
    //
    // if (SignupActivity.this != null) {
    // if (status) {
    // notifyObserver();
    // } else {
    // Toast.makeText(SignupActivity.this,
    // "Error in loading image, try again",
    // Toast.LENGTH_LONG).show();
    // }
    // }
    // }
    // }
    //
    // private class SelectPictureResponseHandler extends
    // AsyncTask<Intent, Void, Boolean> {
    //
    // @Override
    // public void onPreExecute() {
    // Utils.showProgress(SignupActivity.this);
    // }
    //
    // @Override
    // protected Boolean doInBackground(Intent... params) {
    // if (params == null || params.length == 0)
    // return false;
    // return handleChooseImageResponse(params[0]);
    // }
    //
    // @Override
    // public void onPostExecute(Boolean status) {
    // Utils.hideProgress();
    //
    // if (SignupActivity.this != null) {
    // if (status) {
    // notifyObserver();
    // } else {
    // Toast.makeText(SignupActivity.this,
    // "Error in loading image, try again",
    // Toast.LENGTH_LONG).show();
    // }
    // }
    // }
    // }
}