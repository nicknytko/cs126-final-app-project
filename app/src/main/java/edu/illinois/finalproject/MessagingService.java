package edu.illinois.finalproject;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Service that handles the receiving of push notifications from Firebase.  When receives a message
 * from "downstream", it creates a local notification on the android device.
 */
public class MessagingService extends FirebaseMessagingService {
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher);

        if (message.getNotification() != null) {
            builder.setContentTitle(message.getNotification().getTitle())
                    .setContentText(message.getNotification().getBody());
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
