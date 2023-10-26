package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.datamodel.GPSLocation;

import java.util.ArrayList;
import java.util.List;

public class GPSDbHandler {
    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;

    public GPSDbHandler(Context context) {
        this.context = context;
    }

    public GPSDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new gps's
    public void addGpsLocation(GPSLocation gps) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_GPS_LATITUDE, gps.getLatitude());
        values.put(dbHelper.KEY_GPS_LONGITUDE, gps.getLongitude());
        values.put(dbHelper.KEY_GPS_DATE, gps.getDate());

        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_GPS_LOCATION, null, values);

    }
    public List<GPSLocation> getGPSLocationList() {
        List<GPSLocation> gpsLocations = new ArrayList<GPSLocation>();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_GPS_LOCATION;
        Cursor c = db.rawQuery(selectQuery, new String[]{});
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                GPSLocation gps = new GPSLocation();
                gps.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_GPS_ID))));
                gps.setLatitude(c.getDouble(c.getColumnIndex(dbHelper.KEY_GPS_LATITUDE)));
                gps.setLongitude(c.getDouble(c.getColumnIndex(dbHelper.KEY_GPS_LONGITUDE)));
                gps.setDate(c.getString(c.getColumnIndex(dbHelper.KEY_GPS_DATE)));
                gpsLocations.add(gps);
            } while (c.moveToNext());
        }
        c.close();
        return gpsLocations;
    }
    public void deleteUploadedGPSLocations() {
        db = dbHelper.getWritableDatabase();
        db.delete(dbHelper.TABLE_GPS_LOCATION, dbHelper.KEY_GPS_STATUS + "=?", new String[]{"1"});
    }

    public void updateGpsLocation(GPSLocation gps) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_GPS_STATUS, gps.getStatus());

        db = dbHelper.getWritableDatabase();
        db.update(dbHelper.TABLE_GPS_LOCATION, values, dbHelper.KEY_GPS_ID + "=?", new String[]{String.valueOf(gps.getId())});
    }

}
