package com.bigc.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.RecipientEntry;
import com.bigc.dialogs.AddTributeDialog;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.PostManager;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.ProgressHandler;
import com.bigc.models.Posts;
import com.bigc.models.Stories;
import com.bigc_connect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import eu.janmuller.android.simplecropimage.CropImage;
import eu.janmuller.android.simplecropimage.Util;

public class PostActivity extends Activity implements OnClickListener,
        ProgressHandler {

    private LinearLayout allSupportersParent;
    private EditText statusInputView;
    private ImageView pictureInputView;
    private Bitmap selectedPicture;
    private RecipientEditTextView shareUsers;
    private EditText titleInputView;

    private LinearLayout indicatorParentView;
    private TextView indicatorTextView;
    private ProgressBar progressIndicator;

    private boolean isPublicShare = true;
    private int operation;
    private boolean isEdit = false;
    //    private static ParseObject currentObject = null;
    public static Posts currentObject = null;
    private static int currentObjectIndex = -1;
    public static Stories currentstoryObject = null;
    DatabaseReference databaseReference;
    private File mFileTemp;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    public static void setCurrentObject(int position, Posts object, Stories storyObject) {
        //  currentObject = object;
        currentObjectIndex = position;
        currentstoryObject = storyObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_layout);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STRAGE_URL);
        if (getIntent() == null) {
            operation = 1;
            isEdit = false;
        } else {
            operation = getIntent().getIntExtra(Constants.OPERATION,
                    Constants.OPERATION_STATUS);
            isEdit = getIntent().getBooleanExtra(Constants.EDIT_MODE, false);
            if (isEdit && currentObject == null) {
                isEdit = false;
            }
        }

        progressIndicator = (ProgressBar) findViewById(R.id.progressIndicator);

        indicatorParentView = (LinearLayout) findViewById(R.id.indicatorParent);
        indicatorTextView = (TextView) findViewById(R.id.indicatorTextView);

        allSupportersParent = (LinearLayout) findViewById(R.id.allSupportersParent);
        statusInputView = (EditText) findViewById(R.id.statusInputView);
        pictureInputView = (ImageView) findViewById(R.id.picInputView);
        titleInputView = (EditText) findViewById(R.id.titleInputView);

        if (operation != Constants.OPERATION_STORY) {
            findViewById(R.id.titleViewParent).setVisibility(View.GONE);
        }

        findViewById(R.id.postOption).setOnClickListener(this);
        if (isEdit) {
            ((TextView) findViewById(R.id.postOption)).setText("Done");
            findViewById(R.id.attachPicView).setVisibility(View.GONE);
            findViewById(R.id.allSupportersView).setVisibility(View.GONE);
        } else {
            findViewById(R.id.attachPicView).setOnClickListener(this);
            findViewById(R.id.allSupportersView).setOnClickListener(this);
        }

        if (operation == Constants.OPERATION_TRIBUTE) {

            pictureInputView.setImageResource(R.drawable.default_profile_pic);
            statusInputView.setHint(R.string.shareYourTribute);
            GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
                    "Compose New Tribute Screen");
        } else if (operation == Constants.OPERATION_STORY) {

            pictureInputView.setImageResource(R.drawable.default_profile_pic);
            findViewById(R.id.shareUserParent).setVisibility(View.GONE);
            findViewById(R.id.allSupportersView).setVisibility(View.GONE);
            statusInputView.setHint(R.string.shareYourStory);
            GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
                    "Compose New Survivor-Story Screen");
        } else if (operation == Constants.OPERATION_MESSAGE) {

            ((TextView) findViewById(R.id.postOption)).setText(R.string.send);
            statusInputView.setHint(R.string.sendPersonalMessage);
            shareUsers = (RecipientEditTextView) findViewById(R.id.UsersInputView);

            shareUsers.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            shareUsers.setThreshold(1);
            shareUsers.setAdapter(new BaseRecipientAdapter(this, 4, Utils
                    .loadConnectionChips()) {
            });
            GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
                    "Compose New Message Screen");
            switchToSelectionMode(findViewById(R.id.allSupportersView));
        } else if (operation == Constants.OPERATION_PHOTO) {

            statusInputView.setHint(R.string.commentOnThisPhoto);
            pictureInputView.setImageResource(R.drawable.default_profile_pic);
            GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
                    "Post New Photo Screen");
        } else {

            Log.e("Post", "Status");
            statusInputView.setHint(R.string.shareYourThoughts);
            GoogleAnalyticsHelper.sendScreenViewGoogleAnalytics(this,
                    "Post New Status Screen");
        }

        if (isEdit) {

            if (operation == Constants.OPERATION_STORY) {
//                titleInputView.setText(currentObject
//                        .getString(DbConstants.TITLE) == null ? ""
//                        : currentObject.getString(DbConstants.TITLE));
                //// TODO: 14-07-2017  
            }

//            String text = currentObject.getString(DbConstants.MESSAGE) == null ? ""
//                    : currentObject.getString(DbConstants.MESSAGE);
            String text = currentObject.getMessage() == null ? ""
                    : currentObject.getMessage();
            statusInputView.setText(text);
            statusInputView.setSelection(text.length());
//            if (currentObject.getParseFile(DbConstants.MEDIA) != null) {
//                ImageLoader.getInstance().displayImage(
//                        currentObject.getParseFile(DbConstants.MEDIA).getUrl(),
//                        pictureInputView, Utils.normalDisplayOptions,
//                        new SimpleImageLoadingListener() {
//                            @Override
//                            public void onLoadingStarted(String url, View view) {
//                                ((ImageView) view)
//                                        .setImageResource(android.R.color.white);
//                            }
//                        });
            if (currentObject.getMedia() != null) {
                ImageLoader.getInstance().displayImage(
                        currentObject.getMedia(),
                        pictureInputView, Utils.normalDisplayOptions,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String url, View view) {
                                ((ImageView) view)
                                        .setImageResource(android.R.color.white);
                            }
                        });
            }
        }

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    Constants.TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), Constants.TEMP_PHOTO_FILE_NAME);
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void finishActivity() {
        finish();
        overridePendingTransition(R.anim.remains_same, R.anim.pull_down);
    }

    @Override
    public void onClick(View v) {
        v.setClickable(false);
        switch (v.getId()) {
            case R.id.postOption:
                String message = statusInputView.getText().toString().trim();
                if (isEdit) {
                    if (Constants.OPERATION_TRIBUTE == operation) {

                        if (message.length() == 0 && selectedPicture == null) {
                            Toast.makeText(this,
                                    "Empty tribute, Enter data to update it",
                                    Toast.LENGTH_LONG).show();
                            v.setClickable(true);
                        } else {
                            currentObject.setMessage(message);
                            // currentObject.put(DbConstants.MESSAGE, message);

                            finishActivity();
                            Toast.makeText(this, "Tribute has been updated.",
                                    Toast.LENGTH_LONG).show();
//                            PostManager.getInstance().editTribute(
//                                    currentObjectIndex, currentObject);//// TODO: 14-07-2017  
                        }
                    } else if (Constants.OPERATION_STORY == operation) {

                        String title = titleInputView.getText().toString();
                        if (title.length() == 0) {
                            Toast.makeText(this,
                                    "Title is empty, Please give any title",
                                    Toast.LENGTH_LONG).show();
                            v.setClickable(true);
                            return;
                        }

                        if (message.length() == 0) {
                            Toast.makeText(this,
                                    "Empty story, Enter story to post it",
                                    Toast.LENGTH_LONG).show();
                            v.setClickable(true);
                            return;
                        }
                        // addStory(message,
                        // selectedPicture);
                        //   currentObject.put(DbConstants.MESSAGE, message);
                        currentObject.setMessage(message);
                        //// TODO: 14-07-2017            //   currentObject.put(DbConstants.TITLE, title);

                        finishActivity();
                        Toast.makeText(this, "Story has been updated.",
                                Toast.LENGTH_LONG).show();
//                        PostManager.getInstance().editStory(currentObjectIndex,
//                                currentObject);//// TODO: 14-07-2017  
                    } else {
                        if (message.length() == 0 && selectedPicture == null) {
                            Toast.makeText(this,
                                    "Empty post, Enter data to update it",
                                    Toast.LENGTH_LONG).show();
                            v.setClickable(true);
                        } else {
                            //currentObject.put(DbConstants.MESSAGE, message);
                            currentObject.setMessage(message);

                            currentObject.setUpdatedAt(Utils.getCurrentDate());
                            finishActivity();
                            Toast.makeText(this, "Post has been updated.",
                                    Toast.LENGTH_LONG).show();
//                            PostManager.getInstance().editPost(currentObjectIndex,
//                                    currentObject);//// TODO: 14-07-2017  
                        }
                    }

                } else {
                    if (Constants.OPERATION_TRIBUTE == operation) {

                        if (message.length() == 0 && selectedPicture == null) {
                            Toast.makeText(this,
                                    "Empty tribute, Enter data to post it",
                                    Toast.LENGTH_LONG).show();
                            v.setClickable(true);
                        } else {
                            addTribute(message, selectedPicture,
                                    AddTributeDialog.getTargetUser(),
                                    AddTributeDialog.getTargetUserAge());

                        }
                    } else if (Constants.OPERATION_STORY == operation) {

                        String title = titleInputView.getText().toString();
                        if (title.length() == 0) {
                            Toast.makeText(this,
                                    "Title is empty, Please give any title",
                                    Toast.LENGTH_LONG).show();
                            v.setClickable(true);
                            return;
                        }

                        if (message.length() == 0) {
                            Toast.makeText(this,
                                    "Empty story, Enter story to post it",
                                    Toast.LENGTH_LONG).show();
                            v.setClickable(true);
                            return;
                        }
                        addStory(title, message, selectedPicture);
                    } else if (Constants.OPERATION_MESSAGE == operation) {
                        if (message.length() == 0) {
                            Toast.makeText(this,
                                    "Empty message, Type message to send it",
                                    Toast.LENGTH_LONG).show();
                            v.setClickable(true);
                            return;
                        }
                        if (isPublicShare) {
                            sendMessage(message, selectedPicture, getAllUsers());
                        } else {
                            sendMessage(message, selectedPicture,
                                    getAllSelectedUsers());
                        }

                        Toast.makeText(this, "Sending your message, Please wait",
                                Toast.LENGTH_LONG).show();

                        finishActivity();
                    } else {
                        if (message.length() == 0 && selectedPicture == null) {
                            Toast.makeText(this,
                                    "Empty post, Enter data to post it",
                                    Toast.LENGTH_LONG).show();
                            v.setClickable(true);
                        } else {
                            addPost(message, selectedPicture);
                        }
                    }
                }
                break;

            case R.id.attachPicView:
                if (v.getContentDescription().equals("0")) {
                    v.setContentDescription("1");
                    ((ImageView) v).setImageResource(R.drawable.ic_zigzag_colored);
                }
                Utils.choosePicFromGallery(this, true);
                v.setClickable(true);
                break;

            case R.id.allSupportersView:
                if (operation == Constants.OPERATION_MESSAGE) {
                    if (v.getContentDescription().equals("0")) {
                        switchToPublicMode(v);
                    } else {
                        switchToSelectionMode(v);
                    }
                }
                v.setClickable(true);
                break;
        }
    }

    private void switchToSelectionMode(View v) {
        isPublicShare = false;
        v.setContentDescription("0");
        ((ImageView) v).setImageResource(R.drawable.icon_connections);
        allSupportersParent.setVisibility(View.GONE);
    }

    private void switchToPublicMode(View v) {
        isPublicShare = true;
        v.setContentDescription("1");
        ((ImageView) v).setImageResource(R.drawable.ic_all_supporters);
        allSupportersParent.setVisibility(View.VISIBLE);
    }

//    private List<ParseUser> getAllUsers() {
//        List<RecipientEntry> recipients = shareUsers.getAdapter().getEntries();
////        List<ParseUser> users = new ArrayList<ParseUser>();
//        if (recipients == null)
//            return users;
//
//        for (final RecipientEntry entry : recipients)
////            users.add(entry.getUser());
//
//        return users;
//    }

    private List<Object> getAllUsers() {
        List<RecipientEntry> recipients = shareUsers.getAdapter().getEntries();
        List<Object> users = new ArrayList<>();
        if (recipients == null)
            return users;
        for (final RecipientEntry entry : recipients)
            users.add(entry.getUser().toString());
        return users;
    }

//    private List<ParseUser> getAllSelectedUsers() {
//        final Collection<RecipientEntry> recipients = shareUsers
//                .getChosenRecipients();
//        List<ParseUser> users = new ArrayList<ParseUser>();
//        for (final RecipientEntry entry : recipients)
//            users.add(entry.getUser());
//
//        return users;
//    }

    private List<Object> getAllSelectedUsers() {
        final Collection<RecipientEntry> recipients = shareUsers.getChosenRecipients();
        List<Object> users = new ArrayList<>();
        for (final RecipientEntry entry : recipients)
            users.add(entry.getUser());
        return users;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("onActivityResult", "called--" + requestCode);

        if (resultCode == RESULT_OK)

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
                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (path == null) {
                        return;
                    }
                    selectedPicture = BitmapFactory.decodeFile(path);
                    pictureInputView.setImageBitmap(selectedPicture);
                    break;
            }
    }

    //    private void sendMessage(String message, Bitmap picture,
//                             List<ParseUser> users)
    private void sendMessage(String message, Bitmap picture,
                             List<Object> users) {

        PostManager.getInstance().sendMessage(PostActivity.this, message, picture, users);
    }

    String media = "";

    private void addPost(final String message, Bitmap bitmap) {
        Utils.showProgress(PostActivity.this);
//		ParseObject post = new ParseObject(DbConstants.TABLE_POST);
//		post.put(DbConstants.MESSAGE, message);
//		if (bitmap != null) {
//			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//			byte[] bitmapdata = outStream.toByteArray();
//			try {
//				outStream.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			ParseFile file = new ParseFile("media.png", bitmapdata);
        //		post.put(DbConstants.MEDIA, file);
//		}
//
//		post.put(DbConstants.USER, ParseUser.getCurrentUser());
//		PostManager.getInstance().addPost(post, this);

        final String objectId = databaseReference.child(DbConstants.TABLE_POST).push().getKey();
        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT);
        final String date = format.format(new Date(System.currentTimeMillis()));

        Uri uri = null;
        if (bitmap != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            String path = MediaStore.Images.Media.insertImage(PostActivity.this.getContentResolver(), bitmap, "Title", null);
            uri = Uri.parse(path);

            StorageReference reference = storageReference.child("PostImages/" + objectId + ".jpg");
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                    //    String objectId = databaseReference.child(DbConstants.TABLE_POST).push().getKey();

                    media = downloadUri.toString();
                    uploadPost(message, 0, date, date, media, objectId, FirebaseAuth.getInstance().getCurrentUser().getUid());

                    Utils.hideProgress();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.showPrompt(PostActivity.this, e.toString().trim());

                }
            });
        } else {
            uploadPost(message, 0, date, date, media, objectId, FirebaseAuth.getInstance().getCurrentUser().getUid());

        }
        Utils.hideProgress();


    }

    public void uploadPost(String message, int comment, String
            createdAt, String updatedAt, String media, String objectId, String user) {
        Posts post = new Posts();
        post.setMessage(message);
        post.setComments(comment);
        post.setCreatedAt(createdAt);
        post.setUpdatedAt(updatedAt);
        post.setMedia(media);
        post.setObjectId(objectId);
        post.setUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Utils.hideProgress();
        databaseReference.child(DbConstants.TABLE_POST).child(objectId).setValue(post);
        Utils.showToast(PostActivity.this, "Your post has been uploaded!");
        finishActivity();
    }

    private void addTribute(String message, Bitmap bitmap,
                            ParseUser targetUser, int age) {
        ParseObject post = new ParseObject(DbConstants.TABLE_TRIBUTE);
        post.put(DbConstants.MESSAGE, message);
        post.put(DbConstants.AGE, age);
        if (bitmap != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            byte[] bitmapdata = outStream.toByteArray();
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ParseFile file = new ParseFile("media.png", bitmapdata);
            post.put(DbConstants.MEDIA, file);
        }

        post.put(DbConstants.USER, ParseUser.getCurrentUser());
        post.put(DbConstants.TO, targetUser);
        PostManager.getInstance().addTribute(post, this);
    }

    private void addStory(String title, String message, Bitmap bitmap) {
        ParseObject story = new ParseObject(DbConstants.TABLE_STORIES);
        story.put(DbConstants.MESSAGE, message);
        story.put(DbConstants.TITLE, title);
        if (bitmap != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            byte[] bitmapdata = outStream.toByteArray();
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ParseFile file = new ParseFile("media.png", bitmapdata);
            story.put(DbConstants.MEDIA, file);
        }

        story.put(DbConstants.USER, ParseUser.getCurrentUser());
        PostManager.getInstance().addStory(story, this);
    }

    @Override
    public void switchToProgressMode() {
        indicatorParentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateProgress(final int progress) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressIndicator.setProgress(progress);
            }
        });
    }

    @Override
    public void updateLabelToProcessingMessage() {
        indicatorTextView.setText("Saving, Please wait...");
    }

    @Override
    public void onUploadComplete() {
        finishActivity();
    }

}
