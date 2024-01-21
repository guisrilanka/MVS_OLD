package com.gui.mdt.thongsieknavclient.services;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.syncTasks.GPSLocationsUploadSyncTask;

public class LocationUpdateAlarmReceiver extends BroadcastReceiver {
    private NavClientApp mApp;
    private double previousLatitude = 0.0;
    @Override
    public void onReceive(Context context, Intent intent) {
        mApp = (NavClientApp) context.getApplicationContext();
        LocationRequest request = new LocationRequest();
        request.setInterval(3 * 60 *1000);
        request.setFastestInterval(3 * 60 *1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        int permission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult.getLastLocation().getLatitude() == previousLatitude ) {
                        return;
                    }
                    previousLatitude = locationResult.getLastLocation().getLatitude();
                    sendLocationData(context,locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());

                }
            }, null);
        }
    }
   public void sendLocationData(Context context,double latitude,double longitude){

       Bundle locationData = new Bundle();
       locationData.putDouble("lat", latitude);
       locationData.putDouble("long", longitude);
       if(mApp.getCurrentUserName()!=null) {
           System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> latitude and longitude : "+ latitude+" / "+longitude );
           Log.d("LocationUpdateAlarmReceiver", "Latitude: " + latitude + ", Longitude: " + longitude);

           GPSLocationsUploadSyncTask task = new GPSLocationsUploadSyncTask(context, locationData, false);
           task.execute();
       }
    }

}
