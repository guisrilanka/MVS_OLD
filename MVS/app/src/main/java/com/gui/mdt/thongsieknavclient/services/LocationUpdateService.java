package com.gui.mdt.thongsieknavclient.services;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

import android.support.v4.app.ActivityCompat;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.syncTasks.GPSLocationsUploadSyncTask;
import com.gui.mdt.thongsieknavclient.R;
public class LocationUpdateService extends Service {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private PowerManager.WakeLock wakeLock;
    private NavClientApp mApp;
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create a wake lock to ensure the CPU stays awake during location updates
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationUpdateService:WakeLock");



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Check for location permissions before requesting updates
        if (checkLocationPermissions()) {
            // Set up your location request here
            locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Acquire a wake lock to keep the CPU awake during location updates
            if (wakeLock != null && !wakeLock.isHeld()) {
                wakeLock.acquire();
            }
            // Create a notification for the foreground service
            Notification notification = createNotification();

            // Start the service in the foreground
//            startForeground(1, notification);
            System.out.println(mApp);
                Task<Void> locationTask = fusedLocationClient.requestLocationUpdates(locationRequest, getPendingIntent());

                locationTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("LocationUpdateService", "Location updates requested successfully");
                    }
                });
        } else {
            Log.d("LocationUpdateService", "Location permissions not granted");
        }

        return START_STICKY;

    }
    private Notification createNotification() {


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp); // Change this to your notification icon
        builder.setContentTitle("Location Updates");
        builder.setContentText("Location updates are active.");
        builder.setContentIntent(getPendingIntent());

      return   builder.build();

    }
    private boolean checkLocationPermissions() {
        int fineLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Release the wake lock when the service is stopped
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        stopForeground(true);
        // Remove location updates
        System.out.println(mApp);

//        fusedLocationClient.removeLocationUpdates(getPendingIntent());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdateReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public void logLocation(Context context, double latitude, double longitude) {
//        if(mApp!=null) {
            Bundle locationData = new Bundle();
            locationData.putDouble("lat", latitude);
            locationData.putDouble("long", longitude);
            Log.d("LocationUpdateService", "Latitude: " + latitude + ", Longitude: " + longitude);

//        Toast.makeText(context, "Your message goes here : " + latitude + ", Longitude " + longitude, Toast.LENGTH_SHORT).show();
            GPSLocationsUploadSyncTask task = new GPSLocationsUploadSyncTask(context, locationData, false);
            task.execute();
//        }

    }

}
