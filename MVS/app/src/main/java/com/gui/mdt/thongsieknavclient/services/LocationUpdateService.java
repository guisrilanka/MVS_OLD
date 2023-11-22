package com.gui.mdt.thongsieknavclient.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.gui.mdt.thongsieknavclient.R;

public class LocationUpdateService extends Service {

    private final IBinder mBinder = new MyBinder();
    private static final String CHANNEL_ID = "2";
    private static final long LOCATION_UPDATE_INTERVAL =3*60 * 1000;

    private AlarmManager alarmManager;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(this, LocationUpdateAlarmReceiver.class), 0);
        long triggerAtMillis = System.currentTimeMillis() + LOCATION_UPDATE_INTERVAL;
        alarmManager.set (AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // requestLocationUpdates();
        buildNotification();
        // Set up AlarmManager to trigger LocationUpdateReceiver periodically

    }

    private static final int NOTIFICATION_ID = 1;

    private void buildNotification() {

        Context context = getApplicationContext();
        String channelId = "FirebaseChannel";

        String stop = "stop";
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android Oreo and above
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Location Updates", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setContentTitle(getString(R.string.app_name))
                .setContentText("Location tracking is working")
                .setOngoing(true)
                .setContentIntent(broadcastIntent);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(180*1000);
        request.setFastestInterval(3000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    // String location = "Latitude : " +
                    // locationResult.getLastLocation().getLatitude() +
                    // "\nLongitude : " + locationResult.getLastLocation().getLongitude();
                    // Toast.makeText(LocationUpdateService.this, location,
                    // Toast.LENGTH_SHORT).show();
                }
            }, null);
        } else {

            stopSelf();
        }
    }

    public class MyBinder extends Binder {
        public LocationUpdateService getService() {
            return LocationUpdateService.this;
        }
    }
}
