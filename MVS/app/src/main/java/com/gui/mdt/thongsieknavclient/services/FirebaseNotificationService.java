package com.gui.mdt.thongsieknavclient.services;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import android.support.v4.app.NotificationManagerCompat;
import android.os.Bundle;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.gui.mdt.thongsieknavclient.R;

import java.util.Map;


public class FirebaseNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Display the notification
            sendNotification(title, body);
        }
    }

//    private void sendNotification(String title, String messageBody) {
//        Context context = getApplicationContext();
////        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//       String channelId = "FirebaseChannel";
////        String channelId = null;
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            channelId = "FirebaseChannel";
////            NotificationChannel channel = new NotificationChannel(channelId, "Firebase Channel", NotificationManager.IMPORTANCE_HIGH);
////            notificationManager.createNotificationChannel(channel);
////        }
////        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
////                .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Change this to your notification icon
////                .setContentTitle(title)
////                .setContentText(messageBody)
////                .setAutoCancel(true);
////
////        Log.d("NotificationService", "Title: " + title);
////        Log.d("NotificationService", "Message: " + messageBody);
//////
////        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////        notificationManager.notify(101, builder.build());
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Change this to your notification icon
//                .setContentTitle(title)
//                .setContentText(messageBody)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(101, builder.build());
//    }





        private void sendNotification(String title, String messageBody) {
            NotificationCompat.Builder builder;
            String channelId = "default_channel_id";
            String channelName = "Default Channel";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);

                builder = new NotificationCompat.Builder(this);
            } else {
                builder = new NotificationCompat.Builder(this);
            }

            builder
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(101, builder.build());
        }
}
