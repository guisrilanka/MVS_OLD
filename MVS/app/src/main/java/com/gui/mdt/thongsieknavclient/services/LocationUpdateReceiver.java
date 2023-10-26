package com.gui.mdt.thongsieknavclient.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

public class LocationUpdateReceiver extends BroadcastReceiver {
    private static Location lastLocation;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                if (lastLocation == null || !areLocationsEqual(lastLocation, location)) {
                    // Store the new location as the last known location
                    lastLocation = location;

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LocationUpdateService locationUpdateService = new LocationUpdateService();
                    locationUpdateService.logLocation(context,latitude, longitude);
                }
                }

            }
        }

    private boolean areLocationsEqual(Location location1, Location location2) {
        return location1.getLatitude() == location2.getLatitude() && location1.getLongitude() == location2.getLongitude();
    }
}
