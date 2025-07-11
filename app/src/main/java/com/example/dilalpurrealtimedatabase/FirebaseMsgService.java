package com.example.dilalpurrealtimedatabase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMsgService extends FirebaseMessagingService{

    public static final int REQUEST_CODE = 100;
    public static final String CHANNEL_ID = "Channel 1";
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("Refreshed Token: ", token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if(message.getNotification() != null)
        {
            pushNotification(message.getNotification().getTitle(), message.getNotification().getBody());
        }
    }

    private void pushNotification(String title, String msg) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "My Channel 1";
            String description = "Channel for Push Notification";
            int inportance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, inportance);
            notificationChannel.setDescription(description);

            if(notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);

            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.face_1)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setSubText(msg)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .build();
        }
        else
        {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.face_1)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setSubText(msg)
                    .setAutoCancel(true)
                    .build();
        }

        if(notificationManager != null)
            notificationManager.notify(1, notification);

    }
}
