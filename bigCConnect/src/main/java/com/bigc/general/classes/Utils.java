package com.bigc.general.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.ex.chips.RecipientEntry;
import com.bigc.activities.LoginActivity;
import com.bigc.activities.PostActivity;
import com.bigc.activities.Splash;
import com.bigc.activities.ZoomActivity;
import com.bigc.datastorage.Preferences;
import com.bigc.gallery.CustomGalleryActivity;
import com.bigc.interfaces.ConnectionExist;
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc.interfaces.StoryPopupOptionHandler;
import com.bigc.models.ConnectionsModel;
import com.bigc.models.Posts;
import com.bigc.models.Stories;
import com.bigc.models.Tributes;
import com.bigc.models.Users;
import com.bigc.views.PopupHelper;
import com.bigc.views.ProgressDialogue;
import com.bigc_connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.janmuller.android.simplecropimage.CropImage;
import eu.janmuller.android.simplecropimage.Util;

public class Utils {

    private static final long YEAR = 31104000000L;
    private static final long MONTH = 2592000000L;
    private static final long WEEK = 604800000L;
    private static final long DAY = 86400000L;
    private static final long HOUR = 3600000L;
    private static final long MINUTE = 60000L;

    private static ProgressDialogue progress;
    // public static List<String> videoQueries = new ArrayList<String>();
    public static String[] ribbonNames = {"Lung Cancer", "Breast Cancer",
            "Male Breast Cancer", "Prostate Cancer", "Colon Cancer",
            "Leukemia", "Liver Cancer", "Non-Hodgkin Lymphoma",
            "Testicular Cancer", "Stomach Cancer", "Brain Cancer",
            "Kidney Cancer", "Melanoma", "Cervical Cancer",
            "Hodgkin's Lymphoma", "Bone Cancer", "Childhood Cancer",
            "Bladder Cancer", "Thyroid Cancer", "Pancreatic Cancer"};

    public static Integer[] survivor_ribbons = {R.drawable.ribbon_lung,
            R.drawable.ribbon_breast, R.drawable.ribbon_breast_male,
            R.drawable.ribbon_prostate, R.drawable.ribbon_colon,
            R.drawable.ribbon_leukemia, R.drawable.ribbon_liver,
            R.drawable.ribbon_nh_lymphoma, R.drawable.ribbon_testicular,
            R.drawable.ribbon_stomach, R.drawable.ribbon_brain,
            R.drawable.ribbon_kidney, R.drawable.ribbon_melanoma,
            R.drawable.ribbon_cervical, R.drawable.ribbon_h_lymphoma,
            R.drawable.ribbon_bone, R.drawable.ribbon_childhood,
            R.drawable.ribbon_bladder, R.drawable.ribbon_thyroid,
            R.drawable.ribbon_pancreatic};

    public static Integer[] fighter_ribbons = {R.drawable.ribbon_lung_fighter,
            R.drawable.ribbon_breast_fighter,
            R.drawable.ribbon_breast_male_fighter,
            R.drawable.ribbon_prostate_fighter,
            R.drawable.ribbon_colon_fighter,
            R.drawable.ribbon_leukemia_fighter,
            R.drawable.ribbon_liver_fighter,
            R.drawable.ribbon_nh_lymphoma_fighter,
            R.drawable.ribbon_testicular_fighter,
            R.drawable.ribbon_stomach_fighter, R.drawable.ribbon_brain_fighter,
            R.drawable.ribbon_kidney_fighter,
            R.drawable.ribbon_melanoma_fighter,
            R.drawable.ribbon_cervical_fighter,
            R.drawable.ribbon_h_lymphoma_fighter,
            R.drawable.ribbon_bone_fighter,
            R.drawable.ribbon_childhood_fighter,
            R.drawable.ribbon_bladder_fighter,
            R.drawable.ribbon_thyroid_fighter,
            R.drawable.ribbon_pancreatic_fighter};

    public static final DisplayImageOptions normalDisplayOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true).build();

    public static boolean isLiked(Object post) {

        List<String> likes = ((Tributes)post).getLikes();
        if (likes == null)
            return false;

        return likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("Exception", e.getMessage(), e);
        }
        return null;
    }

    // static {
    //
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // ribbonNames.add();
    // ribbonIcons.add();
    // }

    public static void openImageZoomView(Activity activity, String image) {

        Intent intent = new Intent(activity, ZoomActivity.class);
        intent.putExtra("path", image);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up, R.anim.remains_same);
    }

    public static void flagTribute(Object tribute) {
        /*ParseObject flaggedStory = new ParseObject(DbConstants.TABLE_FLAGS);
        flaggedStory.put(DbConstants.TYPE, DbConstants.Flags.Tribute.ordinal());
        flaggedStory.put(DbConstants.TRIBUTE_OBJECT, tribute);
        flaggedStory.put(DbConstants.USER, ParseUser.getCurrentUser());
        flaggedStory.saveInBackground();*/
    }

    public static void flagStory(Object story) {
       /* ParseObject flaggedStory = new ParseObject(DbConstants.TABLE_FLAGS);
        flaggedStory.put(DbConstants.TYPE, DbConstants.Flags.Story.ordinal());
        flaggedStory.put(DbConstants.STORY_OBJECT, story);
        flaggedStory.put(DbConstants.USER, ParseUser.getCurrentUser());
        flaggedStory.saveInBackground();*/
    }

    public static void flagFeed(Object feed) {
        /*ParseObject flaggedStory = new ParseObject(DbConstants.TABLE_FLAGS);
        flaggedStory
                .put(DbConstants.TYPE, DbConstants.Flags.NewsFeed.ordinal());
        flaggedStory.put(DbConstants.FEED_OBJECT, feed);
        flaggedStory.put(DbConstants.USER, ParseUser.getCurrentUser());
        flaggedStory.saveInBackground();*/
    }

    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String loadString(Context context, int id) {

        try {
            return context.getResources().getString(id);
        } catch (Exception e) {

        }
        return "";
    }

    public static void showProgress(Context context) {
        if (context != null && (progress == null || !progress.isShowing())) {
            progress = ProgressDialogue.show(context, null, null);
        }
    }

    public static void showAlert(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void hideProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public static boolean validateEmail(String mail) {
        Pattern pattern;
        Matcher matcher;

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mail);
        return matcher.matches();
    }

    public static void addConnections(List<Users> newConnections) {
        Log.e("ParseUser", "Saving Connections: " + newConnections.size());
        if (newConnections == null || newConnections.size() == 0)
            return;

        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT);
        //List<ConnectionsModel> newConnectionObjects = new ArrayList<>();
        for (Users c : newConnections) {


            String date = format.format(new Date(System.currentTimeMillis()));
            String objectID = currentUid+"_"+c.getObjectId();
            ConnectionsModel connection = new ConnectionsModel(date, currentUid, objectID, false, c.getObjectId(), date);
            ref.child(DbConstants.TABLE_CONNECTIONS).child(objectID).setValue(connection).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //show success
                }
            });

            /*final ConnectionsModel o = new ConnectionsModel();
            o.setTo(c.getObjectId());
            o.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
            o.setStatus(false);*/
           // newConnectionObjects.add(connection);
        }

        //new saveConnectionTask(newConnectionObjects).execute();
        //updateConnectionTable(newConnectionObjects);
    }

    private static void updateConnectionTable(List<ConnectionsModel> newConnectionObjects) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        for (ConnectionsModel connectionReq : newConnectionObjects) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("objectId", user.getUid());
            databaseReference.child(DbConstants.CONNECTIONS).setValue(connectionReq, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    // TODO: 7/13/2017 show success
                }
            });
        }
    }

    private static class saveConnectionTask extends
            AsyncTask<Void, Void, Boolean> {

        List<ConnectionsModel> connections;

        private saveConnectionTask(List<ConnectionsModel> connections) {
            this.connections = new ArrayList<>();
            if (connections != null && connections.size() > 0) {
                this.connections.addAll(connections);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = true;
           /* for (ParseObject c : connections)
                try {
                    c.save();
                    c.pin(Constants.TAG_CONNECTIONS);
                } catch (ParseException e) {
                    Log.e("Save Ex:", e + "--" + e.getMessage());
                    String message = e.getMessage();
                    if (message != null
                            && message.startsWith("object already exist")) {
                        try {
                            c.pin(Constants.TAG_CONNECTIONS);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        result = false;
                        break;
                    }
                }*/
            return result;
        }

        @Override
        public void onPostExecute(Boolean status) {
            Log.e("Save Status:", status + "-");
        }

    }

    public static void removeConnections(
            final List<Users> removedConnections) {
        Log.e("ParseUser", "Removing : " + removedConnections.size());
        if (removedConnections == null || removedConnections.size() == 0)
            return;

        List<ConnectionsModel> removeConnectionObjects = new ArrayList<>();
        for (Users c : removedConnections) {
            final ConnectionsModel o = new ConnectionsModel();
            // TODO: 7/13/2017 discuss how to remove
            o.setTo("");
            o.setFrom("");
            o.setStatus(false);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
           // ConnectionsModel connection = new ConnectionsModel(date, uid, objectID, false, user.getObjectId(), date);
            /*ref.child(DbConstants.TABLE_CONNECTIONS).orderByChild(DbConstants.TO).equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null && dataSnapshot.hasChildren()){
                        ConnectionsModel connection1  = dataSnapshot.getValue(ConnectionsModel.class);
                        if(connection1.getFrom())

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

            removeConnectionObjects.add(o);
        }
        // TODO: 7/13/2017 remove for both from and to requests
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        for (ConnectionsModel connectionReq : removeConnectionObjects) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("objectId", user.getUid());
            databaseReference.child(DbConstants.CONNECTIONS).setValue(connectionReq, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    // TODO: 7/13/2017 show success
                }
            });
        }


       /* ParseQuery<ParseObject> sQuery1 = ParseQuery
                .getQuery(DbConstants.TABLE_CONNECTIONS);
        sQuery1.whereEqualTo(DbConstants.FROM, ParseUser.getCurrentUser());
        sQuery1.whereContainedIn(DbConstants.TO, removedConnections);

        ParseQuery<ParseObject> sQuery2 = ParseQuery
                .getQuery(DbConstants.TABLE_CONNECTIONS);
        sQuery2.whereEqualTo(DbConstants.TO, ParseUser.getCurrentUser());
        sQuery2.whereContainedIn(DbConstants.FROM, removedConnections);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(sQuery1);
        queries.add(sQuery2);

        ParseQuery<ParseObject> mQuery = ParseQuery.or(queries);
        mQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                Log.e("DeleteObjects", objects.size() + "--");
                if (e == null) {
                    if (objects.size() == 0) {
                        ParseObject.unpinAllInBackground(
                                Constants.TAG_CONNECTIONS, removedConnections,
                                new DeleteCallback() {

                                    @Override
                                    public void done(ParseException e2) {
                                        Log.e("Unpinned", e2 + "--");
                                        ParseObject
                                                .unpinAllInBackground(removedConnections);
                                    }
                                });

                    } else {
                        ParseObject.deleteAllInBackground(objects,
                                new DeleteCallback() {

                                    @Override
                                    public void done(ParseException e) {
                                        Log.e("Connection", "Deleted - " + e);
                                        ParseObject.unpinAllInBackground(
                                                Constants.TAG_CONNECTIONS,
                                                objects, new DeleteCallback() {

                                                    @Override
                                                    public void done(
                                                            ParseException e2) {
                                                        Log.e("Unpinned", e2
                                                                + "--");
                                                        ParseObject
                                                                .unpinAllInBackground(
                                                                        Constants.TAG_CONNECTIONS,
                                                                        objects);
                                                    }
                                                });
                                    }
                                });
                    }
                }
            }
        });*/
    }

    public static void inviteSupporters(Activity activity, boolean supporter) {
        PackageManager packageManager = activity.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        List<ResolveInfo> resolveInfoList = packageManager
                .queryIntentActivities(sendIntent, 0);

        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        Resources resources = activity.getResources();

        for (int j = 0; j < resolveInfoList.size(); j++) {
            ResolveInfo resolveInfo = resolveInfoList.get(j);
            String packageName = resolveInfo.activityInfo.packageName;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setComponent(new ComponentName(packageName,
                    resolveInfo.activityInfo.name));
            intent.setType("text/plain");

            // skip android mail and gmail to avoid adding to the list twice
            if (packageName.contains("android.email")
                    || packageName.contains("android.gm")) {
                continue;
            }

            // intentList.add(new LabeledIntent(intent, packageName, resolveInfo
            // .loadLabel(packageManager), resolveInfo.icon));
        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                resources.getString(R.string.invitationSubject));

        String body = resources
                .getString(R.string.invitationSurvivorToSupporterBody1)
                .concat(" ")
//				.concat("\'"
//						+ ParseUser.getCurrentUser()
//								.getString(DbConstants.NAME) + "\'")
                .concat("\'"
                        + Preferences.getInstance(activity).getString(DbConstants.NAME) + "\'")
                .concat(". ")
                .concat(resources
                        .getString(R.string.invitationSurvivorToSupporterBody2));

        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        activity.startActivity(Intent.createChooser(
                emailIntent,
                resources.getString(supporter ? R.string.inviteIntentSupporter
                        : R.string.inviteIntentSurvivor)).putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                intentList.toArray(new LabeledIntent[intentList.size()])));
    }

    public static void buildRequestNotification(Context context,
                                                String objectId, String message, String action) {
        int icon;
        if (Constants.ACTION_FRIEND_REQUEST.equals(action)) {
            icon = R.drawable.ic_alert_invite;
        } else if (Constants.ACTION_MESSAGE.equals(action)) {
            icon = R.drawable.ic_alert_feed;
        } else {
            icon = R.drawable.ic_alert_feed;
        }

        Intent intent = new Intent(context, Splash.class);
        intent.setAction(action);

        if (!message.contains("accepted your support request.")) {
            intent.putExtra("object", objectId);
        }

        PendingIntent pIntent = PendingIntent.getActivity(context,
                (int) System.currentTimeMillis(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n = new Notification.Builder(context)
                .setContentTitle(
                        context.getResources().getString(R.string.app_name))
                .setContentText(message).setSmallIcon(icon)
                .setContentIntent(pIntent).setAutoCancel(true).build();
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), n);

        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(
                    context.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerDeviceForNotifications() {
        /*if (ParseUser.getCurrentUser() == null)
            return;
        ParseInstallation.getCurrentInstallation().put("users",
                ParseUser.getCurrentUser());
        ParseInstallation.getCurrentInstallation().saveInBackground();*/
    }

    public static void unregisterDeviceForNotifications() {
        /*ParseInstallation.getCurrentInstallation().remove("users");
        ParseInstallation.getCurrentInstallation().remove(
                Constants.NOTIFICATIONS);
        ParseInstallation.getCurrentInstallation().saveInBackground();*/
    }

    public static void enablePushes() {
        /*ParseInstallation.getCurrentInstallation().put(Constants.NOTIFICATIONS,
                true);
        ParseInstallation.getCurrentInstallation().saveInBackground();*/
    }

    public static void disablePushes() {
        /*ParseInstallation.getCurrentInstallation().put(Constants.NOTIFICATIONS,
                false);
        ParseInstallation.getCurrentInstallation().saveInBackground();*/
    }

    public static void choosePicFromGallery(Activity activity,
                                            boolean showCameraOption) {

        Intent intent = new Intent(activity, CustomGalleryActivity.class);
        intent.putExtra("camera", showCameraOption);
        activity.startActivityForResult(intent, Constants.CODE_SELECT_PICTURE);
    }

    public static void choosePicFromGallery(Fragment fragment,
                                            boolean showCameraOption) {

        Intent intent = new Intent(fragment.getActivity(),
                CustomGalleryActivity.class);
        intent.putExtra("camera", true);
        fragment.startActivityForResult(intent, Constants.CODE_SELECT_PICTURE);
    }

    public static void launchCamera(Activity activity) {
        File pictureFileDir = new File(Environment
                .getExternalStorageDirectory().getPath()
                + File.separator
                + "BigcConnect");

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.e(PhotoHandler.class.getSimpleName(),
                    "Can't create directory to save image.");
            Toast.makeText(activity, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" + date + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File newfile = new File(filename);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        }

        Uri outputFileUri = Uri.fromFile(newfile);
        Constants.currentCameraIntentURI = outputFileUri.toString();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        activity.startActivityForResult(cameraIntent,
                Constants.CODE_TAKE_PHOTO_CODE);
    }

    public static byte[] getBitmapData(Bitmap bitmap) {
        try {
            Log.e("Handle Bitmap", (bitmap == null) + "");
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            byte[] bitmapdata = outStream.toByteArray();
            outStream.close();
            return bitmapdata;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap setRotation(String uri) {

        Log.e("uri", uri);
        Bitmap sourceBitmap = ImageLoader.getInstance().loadImageSync(uri);
        try {
            ExifInterface exif = new ExifInterface(uri);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);

            Matrix matrix = new Matrix();
            Log.e("rotation", rotation + "-");
            if (rotation != 0f) {
                matrix.preRotate(rotationInDegrees);
            }
            int newWidth = Constants.PROFILE_IMAGE_HEIGHT
                    * sourceBitmap.getWidth() / sourceBitmap.getHeight();
            matrix.postScale(
                    (float) newWidth / sourceBitmap.getWidth(),
                    (float) Constants.PROFILE_IMAGE_HEIGHT
                            / sourceBitmap.getHeight());
            Bitmap adjustedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                    sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix,
                    true);
            return adjustedBitmap;
        } catch (Exception e) {
            return sourceBitmap;
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

/*    public static void sendWelcomeEmail() {

        String body = EmailComposer.composeSupporterWelcomeEmail(ParseUser
                .getCurrentUser().getString(DbConstants.NAME));
        String email = ParseUser.getCurrentUser().getEmail();
        new EmailSenderTask().execute(email,
                EmailComposer.WELCOME_EMAIL_SUBJECT, body);
    }*/

    public static void sendPasswordRecoverEmail(String email) {

       /* ParseUser.requestPasswordResetInBackground(email,
                new RequestPasswordResetCallback() {

                    @Override
                    public void done(ParseException e) {
                        Log.e("Done", "ResetLink");
                    }
                });*/

        // String body = EmailComposer.ComposeForgotPasswordMail(name,
        // password);
        // new EmailSenderTask().execute(email,
        // EmailComposer.WELCOME_EMAIL_SUBJECT, body);
    }

    private static class EmailSenderTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (params == null || params.length < 3)
                return null;
            // GMailSender sender = new GMailSender(Constants.USER,
            // Constants.PD);
            // sender.sendMail(params[1], params[2], Constants.SENDER_EMAIL,
            // params[0]);
            Log.e("Email", "Sent");
            return null;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void storyQuickActionMenu(final StoryPopupOptionHandler handler,
                                            final Activity activity, final int pos, final Stories story,
                                            View v, boolean isOwner, final DbConstants.Flags flag) {

        // This is just a view with buttons that act as a menu.
        View popupView = ((LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.layout_popup, null);

        if (isOwner) {
            popupView.findViewById(R.id.editOption).setVisibility(View.VISIBLE);
            popupView.findViewById(R.id.editOptionShadow).setVisibility(
                    View.VISIBLE);
            popupView.findViewById(R.id.deleteOption).setVisibility(
                    View.VISIBLE);
            popupView.findViewById(R.id.deleteOptionShadow).setVisibility(
                    View.VISIBLE);
        }
        final PopupWindow window = PopupHelper.newBasicPopupWindow(activity);
        window.setContentView(popupView);

        popupView.findViewById(R.id.editOption).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        GoogleAnalyticsHelper.setClickedAction(activity,
                                "3-Dots Edit Button");
                        handler.onEditClicked(pos, story);
                    }
                });

        popupView.findViewById(R.id.deleteOption).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        GoogleAnalyticsHelper.setClickedAction(activity,
                                "3-Dots Delete Button");
                        storyDeleteConfirmationDialog(handler, activity, pos,
                                story, flag);
                    }
                });

        popupView.findViewById(R.id.flagOption).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GoogleAnalyticsHelper.setClickedAction(activity,
                                "3-Dots FlagsAsInappropriate Option");
                        window.dismiss();
                    }
                });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        int[] location = new int[2];
        v.getLocationOnScreen(location);

        if (location[1] < (displaymetrics.heightPixels / 2.0)) {
            PopupHelper.showLikeQuickAction(window, popupView, v,
                    displaymetrics, 0, 0, PopupHelper.UPPER_HALF);
        } else {
            PopupHelper.showLikeQuickAction(window, popupView, v,
                    displaymetrics, 0, 0, PopupHelper.LOWER_HALF);
        }
    }

    public static void showQuickActionMenu(final PopupOptionHandler handler,
                                           final Activity activity, final int pos, final Object post,
                                           View v, boolean isOwner, final DbConstants.Flags flag) {

        // This is just a view with buttons that act as a menu.
        View popupView = ((LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.layout_popup, null);

        if (isOwner) {
            popupView.findViewById(R.id.editOption).setVisibility(View.VISIBLE);
            popupView.findViewById(R.id.editOptionShadow).setVisibility(
                    View.VISIBLE);
            popupView.findViewById(R.id.deleteOption).setVisibility(
                    View.VISIBLE);
            popupView.findViewById(R.id.deleteOptionShadow).setVisibility(
                    View.VISIBLE);
        }
        final PopupWindow window = PopupHelper.newBasicPopupWindow(activity);
        window.setContentView(popupView);

        popupView.findViewById(R.id.editOption).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        GoogleAnalyticsHelper.setClickedAction(activity,
                                "3-Dots Edit Button");
                        handler.onEditClicked(pos, post);
                    }
                });

        popupView.findViewById(R.id.deleteOption).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        GoogleAnalyticsHelper.setClickedAction(activity,
                                "3-Dots Delete Button");
                        showDeleteConfirmationDialog(handler, activity, pos,
                                post, flag);
                    }
                });

        popupView.findViewById(R.id.flagOption).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GoogleAnalyticsHelper.setClickedAction(activity,
                                "3-Dots FlagsAsInappropriate Option");
                        window.dismiss();
                    }
                });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        int[] location = new int[2];
        v.getLocationOnScreen(location);

        if (location[1] < (displaymetrics.heightPixels / 2.0)) {
            PopupHelper.showLikeQuickAction(window, popupView, v,
                    displaymetrics, 0, 0, PopupHelper.UPPER_HALF);
        } else {
            PopupHelper.showLikeQuickAction(window, popupView, v,
                    displaymetrics, 0, 0, PopupHelper.LOWER_HALF);
        }
    }

    private static void storyDeleteConfirmationDialog(final StoryPopupOptionHandler handler, Activity activity,
                                                      final int position, final Stories post, DbConstants.Flags flag) {
        int title;
        int message;
        if (flag == DbConstants.Flags.Story) {
            title = R.string.deleteStory;
            message = R.string.deleteStoryMessage;
            new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.delete,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    handler.onDelete(position, post);
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
        }
    }

    private static void showDeleteConfirmationDialog(
            final PopupOptionHandler handler, Activity activity,
            final int position, final Object post, DbConstants.Flags flag) {
        int title;
        int message;
        if (flag == DbConstants.Flags.Story) {
            title = R.string.deleteStory;
            message = R.string.deleteStoryMessage;
        } else if (flag == DbConstants.Flags.Tribute) {
            title = R.string.deleteTribute;
            message = R.string.deleteTribtueMessage;
        } else {
            title = R.string.deletePost;
            message = R.string.deletePostMessage;
        }

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.delete,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                handler.onDelete(position, post);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    public static void launchPostView(Activity activity, int operation) {
        Intent intent = new Intent(activity, PostActivity.class);
        intent.putExtra(Constants.OPERATION, operation);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up, R.anim.remains_same);
    }

    public static void launchPostViewFromTribute(Activity activity, int operation, Users users) {
        Intent intent = new Intent(activity, PostActivity.class);
        intent.putExtra(Constants.OPERATION, operation);
        intent.putExtra(DbConstants.USER_INFO, users);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up, R.anim.remains_same);
    }

    public static void updatePost(Posts post) {

        Map<String, Object> updated_post = new HashMap<>();
        updated_post.put(DbConstants.CREATED_AT, post.getCreatedAt());
        updated_post.put(DbConstants.UPDATED_AT, post.getUpdatedAt());
        updated_post.put(DbConstants.MEDIA, post.getMedia());
        updated_post.put(DbConstants.LIKES, post.getLikes());
        updated_post.put(DbConstants.COMMENTS, post.getComments());
        updated_post.put(DbConstants.USER, post.getUser());
        updated_post.put(DbConstants.ID, post.getObjectId());
        updated_post.put(DbConstants.MESSAGE, post.getMessage());

        FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_POST).
                child(post.getObjectId()).updateChildren(updated_post);
    }

    public static void updateStory(Stories story) {
        Map<String, Object> updated_story = new HashMap<>();
        updated_story.put(DbConstants.CREATED_AT, story.getCreatedAt());
        updated_story.put(DbConstants.UPDATED_AT, story.getUpdatedAt());
        updated_story.put(DbConstants.MEDIA, story.getMedia());
        updated_story.put(DbConstants.LIKES, story.getLikes());
        updated_story.put(DbConstants.COMMENTS, story.getComments());
        updated_story.put(DbConstants.USER, story.getUser());
        updated_story.put(DbConstants.ID, story.getObjectId());
        updated_story.put(DbConstants.MESSAGE, story.getMessage());
        updated_story.put(DbConstants.TITLE, story.getTitle());

        FirebaseDatabase.getInstance().getReference().child(DbConstants.TABLE_STORIES).
                child(story.getObjectId()).updateChildren(updated_story);
    }


    public static void launchEditView(Activity activity, int operation, boolean fromNewsfeeds,
                                      int position, Posts post) {
        PostActivity.setCurrentObject(position, post,null, null);
        Intent i = new Intent(activity, PostActivity.class);
        i.putExtra(Constants.OPERATION, operation);
        i.putExtra(Constants.EDIT_MODE, true);
        i.putExtra(Constants.FROM_NEWSFEEDS, fromNewsfeeds);
        activity.startActivity(i);
    }

    public static void launchTributeEditView(Activity activity, int operation, boolean fromNewsfeeds,
                                      int position, Tributes post) {
        PostActivity.setCurrentObject(position, null,null, post);
        Intent i = new Intent(activity, PostActivity.class);
        i.putExtra(Constants.OPERATION, operation);
        i.putExtra(Constants.EDIT_MODE, true);
        i.putExtra(Constants.FROM_NEWSFEEDS, fromNewsfeeds);
        activity.startActivity(i);
    }

    public static void launchStoryEditView(Activity activity, int operation,
                                           int position, Stories story) {
        PostActivity.setCurrentObject(position, null, story, null);
        Intent i = new Intent(activity, PostActivity.class);
        i.putExtra(Constants.OPERATION, operation);
        i.putExtra(Constants.EDIT_MODE, true);
        i.putExtra(Constants.FROM_NEWSFEEDS, false);
        activity.startActivity(i);
    }

    public static ArrayList<Users> loadConnectionChips(Context context) {
        ArrayList<Users> active = Preferences.getInstance(context).getLocalConnections().get(0);
        //Queries.getUserActiveConnectionsQuery(context, name, connectionExist);
       /* ParseQuery<ParseObject> query = Queries.getUserActiveConnectionsQuery(
                ParseUser.getCurrentUser(), true);
        try {
            List<ParseObject> objects = query.find();
            ArrayList<RecipientEntry> usr = new ArrayList<RecipientEntry>();
            for (int i = 0; i < objects.size(); i++) {
                ParseObject o = objects.get(i);
                ParseUser u = ParseUser.getCurrentUser().getObjectId()
                        .equals(o.getParseUser(DbConstants.FROM).getObjectId()) ? o
                        .getParseUser(DbConstants.TO).fetchIfNeeded() : o
                        .getParseUser(DbConstants.FROM).fetchIfNeeded();
                int resId;
                if (u.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
                        .ordinal()) {
                    resId = R.drawable.ribbon_supporter;
                } else {
                    resId = u.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
                            : Utils.survivor_ribbons[u
                            .getInt(DbConstants.RIBBON)];
                }
                usr.add(RecipientEntry.constructTopLevelEntry(
                        u.getString(DbConstants.NAME),
                        DisplayNameSources.NICKNAME,
                        u.getString(DbConstants.LOCATION), 0, null, i, i, resId
                                + "", true, false, u));
            }
            return usr;
        } catch (ParseException e) {
            e.printStackTrace();
            return new ArrayList<RecipientEntry>();
        }*/
        return active;
    }

    public static String getTimeStringForFeed(Context context, Date date) {

        try {
            long diff = Calendar.getInstance().getTime().getTime()
                    - date.getTime();

            if (diff < 0) {
                return "Now";
            }

            long temp = diff / YEAR;
            if (temp > 0)
                return temp
                        + " ".concat(loadString(context,
                        temp > 1 ? R.string.years : R.string.year));

            temp = diff / MONTH;
            if (temp > 0)
                return temp
                        + " ".concat(loadString(context,
                        temp > 1 ? R.string.months : R.string.month));

            temp = diff / WEEK;
            if (temp > 0)
                return temp
                        + " ".concat(loadString(context,
                        temp > 1 ? R.string.weeks : R.string.week));

            temp = diff / DAY;
            if (temp > 0)
                return temp
                        + " ".concat(loadString(context,
                        temp > 1 ? R.string.days : R.string.day));

            temp = diff / HOUR;
            if (temp > 0)
                return temp
                        + " ".concat(loadString(context,
                        temp > 1 ? R.string.hours : R.string.hour));

            temp = diff / MINUTE;
            if (temp > 0)
                return temp
                        + " ".concat(loadString(context,
                        temp > 1 ? R.string.minutes : R.string.minute));

            temp = diff / 1000;
            return temp + " ".concat(loadString(context, R.string.seconds));
        } catch (Exception e) {
            return "--";
        }
    }

    public static void openUrl(Context context, String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (Exception e) {
            Toast.makeText(context, "Sorry, App is unable to open",
                    Toast.LENGTH_LONG).show();
        }
    }

    public static int getUserIndex(Users user, List<Users> list) {
        if (user == null)
            return -1;

        String id = user.getObjectId();
        if (id == null || id.length() == 0)
            return -1;

        for (int i = 0; i < list.size(); i++) {
            if (id.equals(list.get(i).getObjectId()))
                return i;
        }

        return -1;
    }

    public static String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT);
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }

    public static Date convertStringToDate(String string_date) {

        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT, Locale.getDefault());
        Date createdDate = null;

        try {
            if (string_date.contains("T") && string_date.contains("Z")) {
                string_date.replace("T", "");
                string_date.replace("Z", "");
            }
            createdDate = format.parse(string_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return createdDate;
    }

    /*public static void refreshLoggedInUser() {
        ParseUser.getCurrentUser().fetchInBackground(
                new GetCallback<ParseUser>() {

                    @Override
                    public void done(ParseUser object, ParseException e) {
                        Log.e("User Refreshed", "Ex: " + e);
                    }
                });
    }*/

    public static void cropImage(Activity activity, String imgPath) {
        Intent intent = new Intent(activity, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, imgPath);
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 4);
        intent.putExtra(CropImage.ASPECT_Y, 3);

        activity.startActivityForResult(intent, Constants.PIC_CROP);
    }

    public static void cropImage(Fragment fragment, String imgPath) {
        Intent intent = new Intent(fragment.getActivity(), CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, imgPath);
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 4);
        intent.putExtra(CropImage.ASPECT_Y, 3);

        fragment.startActivityForResult(intent, Constants.PIC_CROP);
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static void showPrompt(Context context, String message) {
        new AlertDialog.Builder(context).setTitle("Please check!").setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

    }

    public static void errorAlerts(Context context, String message) {
        new AlertDialog.Builder(context).setTitle("Please check!").setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

    }

    public static void saveObjectId(DatabaseReference databaseReference) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("objectId", user.getUid());
        databaseReference.child(DbConstants.USERS).child(user.getUid()).updateChildren(objectMap);
    }

    public static void sendPasswordToEmail(String email, final Context context, FirebaseAuth firebaseAuth) {
        firebaseAuth.sendPasswordResetEmail(email).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Utils.showToast(context, task.getException().toString());
                        } else {

                            Utils.showToast(context,
                                    "Reset link has been sent to your email");
                            context.startActivity(new Intent(context, LoginActivity.class));
                        }

                    }
                });
    }
}
