package com.gui.mdt.thongsieknavclient.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.os.Bundle;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.gui.mdt.thongsieknavclient.R;

import java.util.Map;


public class FirebaseNotificationService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "2";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Display the notification
//            sendNotification(title,body);
        }
    }


}
