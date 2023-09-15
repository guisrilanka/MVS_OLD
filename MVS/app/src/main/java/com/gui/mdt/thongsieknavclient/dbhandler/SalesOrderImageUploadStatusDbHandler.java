package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderImageUploadStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10/13/2017.
 */

public class SalesOrderImageUploadStatusDbHandler {
    private Context context;
    private DatabaseHandler dbHelper;
    SQLiteDatabase db;

    public SalesOrderImageUploadStatusDbHandler(Context context) {
        this.context = context;
    }

    public SalesOrderImageUploadStatusDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new item
    public void addItems(SalesOrderImageUploadStatus salesOrderImageUploadStatus) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_SOIUS_SO_No, salesOrderImageUploadStatus.getSoNo());
        values.put(dbHelper.KEY_SOIUS_IMAGE_NAME, salesOrderImageUploadStatus.getImageName());
        values.put(dbHelper.KEY_SOIUS_IMAGE_URL, salesOrderImageUploadStatus.getImageUrl());
        values.put(dbHelper.KEY_SOIUS_TRANSFERRED, salesOrderImageUploadStatus.isTransferred());
        values.put(dbHelper.KEY_SOIUS_LAST_TRANSFERRED_BY, salesOrderImageUploadStatus.getLastTransferredBy());
        values.put(dbHelper.KEY_SOIUS_LAST_TRANSFERRED_DATETIME, salesOrderImageUploadStatus.getLastTransferredDateTime());

        // Inserting Row
        db=dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_SO_IMAGE_UPLOAD_STATUS, null, values);
    }

    // update item
    public boolean updateItem( String iamgeName, boolean transfered, String lastTransferedBy, String lastTransferedDateTime) {
        boolean success = false;
        ContentValues values = new ContentValues();

        values.put(dbHelper.KEY_SOIUS_TRANSFERRED, transfered);
        values.put(dbHelper.KEY_SOIUS_LAST_TRANSFERRED_BY, lastTransferedBy);
        values.put(dbHelper.KEY_SOIUS_LAST_TRANSFERRED_DATETIME, lastTransferedDateTime);

        // Update Row
        db = dbHelper.getWritableDatabase();

        if (db.update(dbHelper.TABLE_SO_IMAGE_UPLOAD_STATUS, values, dbHelper.KEY_SOIUS_IMAGE_NAME +" = '" +iamgeName+ "'", null) == 1) {
            success = true;
        }
        else {
            success = false;
        }

        return success;
    }

    public List<SalesOrderImageUploadStatus> getAllItemsByTransferred(String transfered) {

        List<SalesOrderImageUploadStatus> itemList = new ArrayList<SalesOrderImageUploadStatus>();
        db=dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO_IMAGE_UPLOAD_STATUS
                            + " WHERE "+dbHelper.KEY_SOIUS_TRANSFERRED + " = " + transfered;
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesOrderImageUploadStatus item = new SalesOrderImageUploadStatus();
                item.setSoNo( c.getString(c.getColumnIndex(dbHelper.KEY_SOIUS_SO_No)));
                item.setImageName(c.getString(c.getColumnIndex(dbHelper.KEY_SOIUS_IMAGE_NAME)));
                item.setImageUrl(c.getString(c.getColumnIndex(dbHelper.KEY_SOIUS_IMAGE_URL)));
                item.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SOIUS_TRANSFERRED)) > 0);
                item.setLastTransferredBy(c.getString(c.getColumnIndex(dbHelper.KEY_SOIUS_LAST_TRANSFERRED_BY)));
                item.setLastTransferredDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SOIUS_LAST_TRANSFERRED_DATETIME)));

                itemList.add(item);
            } while (c.moveToNext());
        }

        c.close();
        return itemList;
    }

    public List<SalesOrderImageUploadStatus> getAllItemsByTransferredForMVS(String transfered) {

        List<SalesOrderImageUploadStatus> itemList = new ArrayList<SalesOrderImageUploadStatus>(),
                finalItemList = new ArrayList<SalesOrderImageUploadStatus>();
        db=dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO_IMAGE_UPLOAD_STATUS
                + " WHERE "+dbHelper.KEY_SOIUS_TRANSFERRED + " = " + transfered;
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesOrderImageUploadStatus item = new SalesOrderImageUploadStatus();
                item.setSoNo( c.getString(c.getColumnIndex(dbHelper.KEY_SOIUS_SO_No)));
                item.setImageName(c.getString(c.getColumnIndex(dbHelper.KEY_SOIUS_IMAGE_NAME)));
                item.setImageUrl(c.getString(c.getColumnIndex(dbHelper.KEY_SOIUS_IMAGE_URL)));
                item.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SOIUS_TRANSFERRED)) > 0);
                item.setLastTransferredBy(c.getString(c.getColumnIndex(dbHelper.KEY_SOIUS_LAST_TRANSFERRED_BY)));
                item.setLastTransferredDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SOIUS_LAST_TRANSFERRED_DATETIME)));

                itemList.add(item);
            } while (c.moveToNext());
        }

        if (!itemList.isEmpty()) {
            for (SalesOrderImageUploadStatus item : itemList) {
                SalesOrder so = new SalesOrder();

                SalesOrderDbHandler soDb = new SalesOrderDbHandler(context);
                soDb.open();
                so = soDb.getSalesOrder(item.getSoNo());
                soDb.close();

                String statusConfirmed
                        = context.getResources().getString(R.string.MVSSalesOrderStatusConfirmed);

                if (so.getNo() != null) {
                    if (so.getNo() != ""
                            && so.isTransferred()
                            && so.getStatus().equals(statusConfirmed))
                    {
                        finalItemList.add(item);
                    }
                }
            }
        }

        c.close();
        return finalItemList;
    }
}
