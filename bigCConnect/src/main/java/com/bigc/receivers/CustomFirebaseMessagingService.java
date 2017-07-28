package com.bigc.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bigc.activities.HomeScreen;
import com.bigc.activities.Splash;
import com.bigc.general.classes.Constants;
import com.bigc_connect.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by beesolver on 7/27/2017.
 */

public class CustomFirebaseMessagingService extends FirebaseMessagingService{

    private static final String TAG = "FbMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


                sendNotification(remoteMessage);

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

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

    private void sendNotification(RemoteMessage messageBody) {
        int icon;
        Intent intent = new Intent(this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Constants.ACTION_FRIEND_REQUEST.equals(messageBody.getData().values().toArray()[1])) {
            icon = R.drawable.ic_alert_invite;
        } else if (Constants.ACTION_MESSAGE.equals("")) {
            icon = R.drawable.ic_alert_feed;
        } else {
            icon = R.drawable.ic_alert_feed;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(messageBody.getData().values().toArray()[1].toString())
                .setContentText(messageBody.getData().values().toArray()[0].toString())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
