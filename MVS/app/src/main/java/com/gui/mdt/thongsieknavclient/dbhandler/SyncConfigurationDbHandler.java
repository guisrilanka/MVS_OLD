package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;

import org.joda.time.DateTime;

/**
 * Created by nelin_000 on 07/20/2017.
 */

public class SyncConfigurationDbHandler {
    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;
    private NavClientApp mApp;

    public SyncConfigurationDbHandler(Context context) {
        this.context = context;
        this.mApp = (NavClientApp) context;
    }

    public SyncConfigurationDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        //database = dbHelper.getWritableDatabase();
        //db = dbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new configuration
    public void addSyncConfiguration(SyncConfiguration config) {

        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_SYNC_SCOPE, config.getScope());
        values.put(dbHelper.KEY_SYNC_LAST_SYNC_BY, config.getLastSyncBy());
        values.put(dbHelper.KEY_SYNC_LAST_SYNC__DATE, config.getLastSyncDateTime()==null? DateTime.now().toString():
                config.getLastSyncDateTime().toString());
        values.put(dbHelper.KEY_SYNC_IS_SUCCESS, config.getSuccess());
        values.put(dbHelper.KEY_SYNC_DATA_COUNT,config.getDataCount());
        //values.put(dbHelper.KEY_SYNC_USER,mApp.getCurrentUserName());

        // Inserting Row
        db.insert(dbHelper.TABLE_SYNC_CONFIG, null, values);

    }

    public boolean isConfigurationExist(String scope) {
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SYNC_CONFIG + " WHERE " + dbHelper.KEY_SYNC_SCOPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{scope});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public SyncConfiguration getLastSyncInfoByScope(String scope) {

        SyncConfiguration syncConfiguration = new SyncConfiguration();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SYNC_CONFIG + " WHERE " + dbHelper.KEY_SYNC_SCOPE + " = ?"
                + " AND " + dbHelper.KEY_SYNC_LAST_SYNC_BY + " =?";
        Cursor c = db.rawQuery(query, new String[]{scope,mApp.getCurrentUserName()});

        if (c.moveToFirst()) {
            do {
                syncConfiguration.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SYNC_ID))));
                syncConfiguration.setScope(c.getString(c.getColumnIndex(dbHelper.KEY_SYNC_SCOPE)));
                syncConfiguration.setLastSyncBy(c.getString(c.getColumnIndex(dbHelper.KEY_SYNC_LAST_SYNC_BY)));
                syncConfiguration.setLastSyncDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SYNC_LAST_SYNC__DATE)));
                syncConfiguration.setSuccess(c.getInt(c.getColumnIndex(dbHelper.KEY_SYNC_IS_SUCCESS)) > 0);
            } while (c.moveToNext());
        }

        return syncConfiguration;
    }


}
