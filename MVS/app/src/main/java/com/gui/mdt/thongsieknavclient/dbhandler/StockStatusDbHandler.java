package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.datamodel.StockStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GuiUser on 2/2/2018.
 */

public class StockStatusDbHandler {

    private Context context;
    private DatabaseHandler dbHelper;
    SQLiteDatabase db;

    public StockStatusDbHandler(Context context) {
        this.context = context;
    }

    public StockStatusDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new StockStatus
    public void addItems(StockStatus stockStatus) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_SS_LOAD_DATE, stockStatus.getLoadDate());
        values.put(dbHelper.KEY_SS_IS_DISPATCHED, stockStatus.isDispatched());
        values.put(dbHelper.KEY_SS_DISPATCHED_TIME, stockStatus.getDispatchedTime());

        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_STOCK_STATUS, null, values);
    }

    public boolean isStockStatusExist(String loadDate) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_STOCK_STATUS + " WHERE " + dbHelper.KEY_SS_LOAD_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{loadDate});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public StockStatus getStockStatus(String loadDate) {
        StockStatus ssItem = new StockStatus();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_STOCK_STATUS
                + " WHERE date(" + dbHelper.KEY_SS_LOAD_DATE + ") = date( ? )";
        Cursor c = db.rawQuery(query, new String[]{loadDate});

        if (c.moveToFirst()) {
            ssItem = getStockStatusFromCursor(ssItem, c);
        }

        c.close();
        return ssItem;
    }

    public boolean updateStockStatus(String date, boolean status)
    {
        boolean success = false;
        db = dbHelper.getWritableDatabase();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date dateObj = new Date();
        String dispatchTime = df.format(dateObj).toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_SS_IS_DISPATCHED, status);
        contentValues.put(dbHelper.KEY_SS_DISPATCHED_TIME, dispatchTime);

        if (db.update(dbHelper.TABLE_STOCK_STATUS, contentValues, ""
                + dbHelper.KEY_SS_LOAD_DATE + " = ?", new String[]{date}) == 1)
            success = true;
        else
            success = false;

        return success;
    }

    private StockStatus getStockStatusFromCursor(StockStatus ss, Cursor c) {
        ss.setId(c.getInt(c.getColumnIndex(dbHelper.KEY_SS_ID)));
        ss.setLoadDate(c.getString(c.getColumnIndex(dbHelper.KEY_SS_LOAD_DATE)));
        ss.setDispatchedTime(c.getString(c.getColumnIndex(dbHelper.KEY_SS_DISPATCHED_TIME)));
        ss.setDispatched(c.getInt(c.getColumnIndex(dbHelper.KEY_SS_IS_DISPATCHED)) > 0);

        return ss;
    }

    public void clearTable()
    {
        db = dbHelper.getWritableDatabase();
        db.delete(dbHelper.TABLE_STOCK_STATUS, null, null);
    }

}
