package com.bigc.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.bigc.adapters.AutoCompleteTextViewAdapter;
import com.bigc.datastorage.Preferences;
import com.bigc.dialogs.AddTributeDialog;
import com.bigc.fragments.NewsFeedFragment;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.GoogleAnalyticsHelper;
import com.bigc.general.classes.PostManager;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.ProgressHandler;
import com.bigc.models.Messages;
import com.bigc.models.Post;
import com.bigc.models.Posts;
import com.bigc.models.Stories;
import com.bigc.models.Tributes;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.android.gms.nearby.connection.dev.Strategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import eu.janmuller.android.simplecropimage.CropImage;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends Activity implements OnClickListener,
        ProgressHandler {

    private LinearLayout allSupportersParent;
    private EditText statusInputView;
    private ImageView pictureInputView;
    private Bitmap selectedPicture;
    //    private RecipientEditTextView shareUsers;
    MultiAutoCompleteTextView shareUsers;
    private EditText titleInputView;

    private LinearLayout indicatorParentView;
    private TextView indicatorTextView;
    private ProgressBar progressIndicator;

    private boolean isPublicShare = true;
    private int operation;
    private boolean isEdit = false;
    private boolean fromNewsfeeds = false;
    //    private static ParseObject currentObject = null;
//    public static Posts currentObject = null;
    public static Stories currentstoryObject = null;
    public static Tributes currentTributeObject = null;
    public static Messages currentMessageObject = null;
    private static int currentObjectIndex = -1;
    DatabaseReference databaseReference;
    private File mFileTemp;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Users user, selected_user;
    AutoCompleteTextViewAdapter autoCompleteTextViewAdapter;
    public static String message = "";

    public static void setCurrentObject(int position, Posts object, Stories storyObject, Tributes tribute) {
        //  currentObject = object;
        currentObjectIndex = position;
        currentstoryObject = storyObject;
        currentTributeObject = tribute;
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
            fromNewsfeeds = getIntent().getBooleanExtra(Constants.FROM_NEWSFEEDS, false);
            if (!fromNewsfeeds) {
                NewsFeedFragment.currentObject = null;
            }
            if (isEdit && NewsFeedFragment.currentObject == null && currentstoryObject == null && currentTributeObject == null) {
                isEdit = false;
            }
            user = (Users) getIntent().getSerializableExtra(DbConstants.USER_INFO);
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
            shareUsers = (MultiAutoCompleteTextView) findViewById(R.id.UsersInputView);

//            fetchUser();
            //  shareUsers = (RecipientEditTextView) findViewById(R.id.UsersInputView);
            shareUsers = (MultiAutoCompleteTextView) findViewById(R.id.UsersInputView);
            shareUsers.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            shareUsers.setThreshold(1);

            /*shareUsers.setAdapter(new BaseRecipientAdapter(this, 4, Utils
                    .loadConnectionChips()) {
            });*/

            getAllUsers(Preferences.getInstance(this).getAllUsers(DbConstants.FETCH_USER));
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
            String text;
            if (operation == Constants.OPERATION_STORY) {
                titleInputView.setText(currentstoryObject.getTitle() == null ? "" : currentstoryObject.getTitle());
//                titleInputView.setText(currentObject
//                        .getString(DbConstants.TITLE) == null ? ""
//                        : currentObject.getString(DbConstants.TITLE));
                ////
                text = currentstoryObject == null ? ""
                        : currentstoryObject.getMessage();
                statusInputView.setText(text);
                statusInputView.setSelection(text.length());
                if (currentstoryObject != null && currentstoryObject.getMedia() != null) {
                    ImageLoader.getInstance().displayImage(
                            currentstoryObject.getMedia(),
                            pictureInputView, Utils.normalDisplayOptions,
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String url, View view) {
                                    ((ImageView) view)
                                            .setImageResource(android.R.color.white);
                                }
                            });
                }


            } else if (operation == Constants.OPERATION_TRIBUTE) {
                text = currentTributeObject == null ? ""
                        : currentTributeObject.getMessage();
                statusInputView.setText(text);
                statusInputView.setSelection(text.length());
                if (currentTributeObject != null && currentTributeObject.getMedia() != null) {
                    ImageLoader.getInstance().displayImage(
                            currentTributeObject.getMedia(),
                            pictureInputView, Utils.normalDisplayOptions,
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String url, View view) {
                                    ((ImageView) view).setImageResource(android.R.color.white);
                                }
                            });
                }
            } else {
                text = NewsFeedFragment.currentObject == null ? ""
                        : NewsFeedFragment.currentObject.getMessage();
                statusInputView.setText(text);
                statusInputView.setSelection(text.length());
                if (NewsFeedFragment.currentObject != null && NewsFeedFragment.currentObject.getMedia() != null) {
                    ImageLoader.getInstance().displayImage(
                            NewsFeedFragment.currentObject.getMedia(),
                            pictureInputView, Utils.normalDisplayOptions,
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String url, View view) {
                                    ((ImageView) view).setImageResource(android.R.color.white);
                                }
                            });
                }
            }

//            String text = currentObject.getString(DbConstants.MESSAGE) == null ? ""
//                    : currentObject.getString(DbConstants.MESSAGE);


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
                message = statusInputView.getText().toString().trim();
                if (isEdit) {
                    if (Constants.OPERATION_TRIBUTE == operation) {

                        if (message.length() == 0 && selectedPicture == null) {
                            Toast.makeText(this,
                                    "Empty tribute, Enter data to update it",
                                    Toast.LENGTH_LONG).show();
                            v.setClickable(true);
                        } else {
                            currentTributeObject.setMessage(message);
                            // currentObject.put(DbConstants.MESSAGE, message);

                            finishActivity();
                            Toast.makeText(this, "Tribute has been updated.",
                                    Toast.LENGTH_LONG).show();
                            PostManager.getInstance().editTribute(
                                    currentObjectIndex, currentTributeObject);//// TODO: 14-07-2017
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
                        currentstoryObject.setTitle(title);
                        currentstoryObject.setMessage(message);
                        currentstoryObject.setUpdatedAt(Utils.getCurrentDate());


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
                            NewsFeedFragment.currentObject.setMessage(message);

                            NewsFeedFragment.currentObject.setUpdatedAt(Utils.getCurrentDate());
                            finishActivity();

                            Toast.makeText(this, "Post has been updated.",
                                    Toast.LENGTH_LONG).show();
//                            PostManager.getInstance().editPost(currentObjectIndex,
//                                    currentObject);
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
//                            addTribute(message, selectedPicture,
//                                    (Users) AddTributeDialog.getTargetUser(),
                            //     AddTributeDialog.getTargetUserAge());
//                            addTribute(message, selectedPicture, (Users) AddTributeDialog.getTargetUser(),
//                                    AddTributeDialog.getTargetUserAge());
                            addTribute(message, selectedPicture, user, AddTributeDialog.getTargetUserAge());

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
                        if (selectedUsers_array.size() == 0) {
                            Utils.showToast(this, "Please enter users");
                            v.setClickable(true);
                            return;
                        }

                        if (isPublicShare) {
                            //  fetchUser();
                            //sendMessage(message, selectedPicture, getAllUsers());
                        } else {
                            //   sendMessage(message, selectedPicture,
                            // getAllSelectedUsers());
                            sendMessage(PostActivity.this, message, selectedPicture, selectedUsers_array);
                        }

                        Toast.makeText(this, "Sending your message, Please wait",
                                Toast.LENGTH_LONG).show();

                        //  finishActivity();
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


    public static ArrayList<Users> selectedUsers_array;

    public void getAllUsers(final ArrayList<Users> userses) {
        Utils.hideProgress();
        final ArrayList<String> users_name = new ArrayList<>();
        for (Users user : userses) {
            users_name.add(user.getName());
        }
        selectedUsers_array = new ArrayList<>();
        autoCompleteTextViewAdapter = new AutoCompleteTextViewAdapter(this, R.layout.autotext_layout, R.id.auto_text, userses);
        shareUsers.setAdapter(autoCompleteTextViewAdapter);
        shareUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selected_user = new Users();
                selected_user = (Users) adapterView.getAdapter().getItem(position);
                selectedUsers_array.add(selected_user);
            }
        });


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

   /* private List<Object> getAllSelectedUsers() {
        final Collection<RecipientEntry> recipients = shareUsers.getChosenRecipients();
        List<Object> users = new ArrayList<>();
        for (final RecipientEntry entry : recipients)
            users.add(entry.getUser());
        return users;
    }*/

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

    public void sendMessage(final Context context, final String message, Bitmap image,
                            final List<Users> users) {
        //	new sendMessageTask(message, image, users).execute();

        Utils.showProgress(context);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STRAGE_URL);
        Uri uri = null;
        if (image != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "Title", null);
            uri = Uri.parse(path);

            StorageReference reference = storageReference.child("MessageImages/" + UUID.randomUUID().toString() + ".jpg");
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                    media = downloadUri.toString();

                    uploadMessage(media, users, message);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.showPrompt(context, e.toString().trim());
                    Utils.hideProgress();
                }
            });
        } else {
            uploadMessage(media, users, message);
            Utils.hideProgress();
        }

    }

    public void uploadMessage(String media, List<Users> users_list, String message) {
//        for (int i = 0; i < users_list.size(); i++) {
//        Users user = users_list.get(i);
        String current_user = Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID);
        //      String objectId = databaseReference.child(DbConstants.TABLE_MESSAGE).push().getKey();
        String conversationObjectId = databaseReference.child(DbConstants.TABLE_CONVERSATION).push().getKey();
        Messages message_model = new Messages();
        message_model.setCreatedAt(Utils.getCurrentDate());
        message_model.setUpdatedAt(Utils.getCurrentDate());
        message_model.setMessage(message);
        //  message_model.setObjectId(objectId);
//        message_model.setSender(current_user);
        //   message_model.setUser1(current_user);
//        message_model.setUser2(user.getObjectId());
        message_model.setMedia(media);


        currentMessageObject = new Messages();
        currentMessageObject = message_model;

//            FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_MESSAGE)
//                    .child(objectId).setValue(message_model);


//            FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_CONVERSATION)
//                    .child(conversationObjectId).setValue(message_model);
//        }
        Utils.hideProgress();
        finishActivity();
    }

    //    private void sendMessage(String message, Bitmap picture,
//                             List<ParseUser> users)
//    private void sendMessage(String message, Bitmap picture,
//                             List<Users> users) {
//
//        //PostManager.getInstance().sendMessage(PostActivity.this, message, picture, users);
//    }

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
                    uploadPost(message, 0, date, date, media, objectId, Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID));

                    Utils.hideProgress();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.showPrompt(PostActivity.this, e.toString().trim());

                }
            });
        } else {
            uploadPost(message, 0, date, date, media, objectId, Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID));

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
        post.setUser(Preferences.getInstance(PostActivity.this).getString(DbConstants.ID));
        Utils.hideProgress();
        databaseReference.child(DbConstants.TABLE_POST).child(objectId).setValue(post, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                ArrayList<String> sendTokens = new ArrayList<>();
                ArrayList<Users> activeConnections = new ArrayList<>();
                activeConnections = Preferences.getInstance(getBaseContext()).getLocalConnections().get(0);

                if (activeConnections==null||activeConnections.size()==0)
                {
                   // Utils.sendNotification(sendTokens, Constants.ACTION_NEWS_FEED, "Newsfeed", "A post has been added.");
                }
                else if (activeConnections.size() > 0) {
                    for (Users activeConnection : activeConnections) {
                        //if (!activeConnection.getToken().equalsIgnoreCase(Preferences.getInstance(getBaseContext()).getString(DbConstants.TOKEN)))
                        sendTokens.add(activeConnection.getToken());
                    }
                    Utils.sendNotification(sendTokens, Constants.ACTION_NEWS_FEED, "Newsfeed", "A post has been added.");
                }
            }
        });
        Utils.showToast(PostActivity.this, "Your post has been uploaded!");
        finishActivity();
    }

    //    private void addTribute(String message, Bitmap bitmap,
//                            Users targetUser, int age)
    private void addTribute(final String message, Bitmap bitmap,
                            final Users targetUser, final int age) {
       /* ParseObject post = new ParseObject(DbConstants.TABLE_TRIBUTE);
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
        PostManager.getInstance().addTribute(post, this);*/

        Utils.showProgress(PostActivity.this);
        final String objectId = databaseReference.child(DbConstants.TABLE_TRIBUTE).push().getKey();
        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT);
        final String date = format.format(new Date(System.currentTimeMillis()));

        Uri uri = null;
        if (bitmap != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            String path = MediaStore.Images.Media.insertImage(PostActivity.this.getContentResolver(), bitmap, "Title", null);
            uri = Uri.parse(path);

            StorageReference reference = storageReference.child("TributeImages/" + objectId + ".jpg");
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                    //    String objectId = databaseReference.child(DbConstants.TABLE_POST).push().getKey();

                    media = downloadUri.toString();
                    uploadTribute(targetUser, message, 0, date, date, media,
                            objectId, Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID), age);

                    Utils.hideProgress();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.showPrompt(PostActivity.this, e.toString().trim());

                }
            });
        } else {
            uploadTribute(targetUser, message, 0, date, date, media, objectId,
                    Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID), age);
        }

    }

    public void uploadTribute(Users targetUser, String message, int comments, String createdAt,
                              String updatedAt, String mediaUrl, String objectId,
                              String user, int age) {


        Tributes tributes = new Tributes();
        tributes.setAge(age);
        tributes.setComments(comments);
        tributes.setCreatedAt(createdAt);
        tributes.setUpdatedAt(updatedAt);
        tributes.setObjectId(objectId);
        tributes.setMedia(mediaUrl);
        tributes.setMessage(message);
        tributes.setTo(targetUser.getObjectId());
        tributes.setFrom(Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID));
        tributes.setUser(user);
        tributes.setLocation(targetUser.getLocation());
        tributes.setSurvivor(targetUser.getName());

        databaseReference.child(DbConstants.TABLE_TRIBUTE).child(objectId).setValue(tributes);
        Utils.hideProgress();
        ArrayList<String> sendTokens = new ArrayList<>();
        sendTokens.add(targetUser.getToken());
        Utils.sendNotification(sendTokens, Constants.ACTION_TRIBUTE, "Tribute", "A tribute has been added");
        finishActivity();
    }

    private void addStory(final String title, final String message, Bitmap bitmap) {
//        ParseObject story = new ParseObject(DbConstants.TABLE_STORIES);
//        story.put(DbConstants.MESSAGE, message);
//        story.put(DbConstants.TITLE, title);
//        if (bitmap != null) {
//            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//            byte[] bitmapdata = outStream.toByteArray();
//            try {
//                outStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ParseFile file = new ParseFile("media.png", bitmapdata);
//            story.put(DbConstants.MEDIA, file);
//        }
//
//        story.put(DbConstants.USER, ParseUser.getCurrentUser());
//        PostManager.getInstance().addStory(story, this);
        Utils.showProgress(PostActivity.this);
        final String objectId = databaseReference.child(DbConstants.TABLE_STORIES).push().getKey();
        Uri uri = null;
        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT);
        final String date = format.format(new Date(System.currentTimeMillis()));
        if (bitmap != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            String path = MediaStore.Images.Media.insertImage(PostActivity.this.getContentResolver(), bitmap, "Title", null);
            uri = Uri.parse(path);

            StorageReference reference = storageReference.child("StoryImages/" + objectId + ".jpg");
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();

                    media = downloadUri.toString();

                    uploadStory(message, title, date, date, objectId, media, 0, Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID));
                    Utils.hideProgress();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.showPrompt(PostActivity.this, e.toString().trim());

                }
            });
        } else {
            uploadStory(message, title, date, date, objectId, media, 0, Preferences.getInstance(getApplicationContext()).getString(DbConstants.ID));
        }

    }

    public void uploadStory(String message, String title, String createdAt, String updatedAt, String objectId, String media
            , int comments, String user) {
        Stories story = new Stories();
        story.setComments(comments);
        story.setCreatedAt(createdAt);
        story.setMedia(media);
        story.setMessage(message);
        story.setObjectId(objectId);
        story.setTitle(title);
        story.setUpdatedAt(updatedAt);
        story.setUser(user);
        Utils.hideProgress();
        databaseReference.child(DbConstants.TABLE_STORIES).child(objectId).setValue(story);
        Utils.showToast(PostActivity.this, "Your story has been uploaded!");
        finishActivity();
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
