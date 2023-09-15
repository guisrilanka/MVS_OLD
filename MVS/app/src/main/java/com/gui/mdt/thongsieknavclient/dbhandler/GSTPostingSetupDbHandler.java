package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.datamodel.GSTPostingSetup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GUI-NB03 on 2017-08-30.
 */

public class GSTPostingSetupDbHandler {

    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;

    public GSTPostingSetupDbHandler(Context context) {
        this.context = context;
    }

    public GSTPostingSetupDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new GSTPostingSetup
    public void addGSTPostingSetup(GSTPostingSetup gstPostingSetup) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_GST_POSTING_SETUP_KEY, gstPostingSetup.getKey()); // key
        values.put(dbHelper.KEY_VAT_BUS_POSTING_GROUP, gstPostingSetup.getVATBusPostingGroup()); // VATBusPostingGroup
        values.put(dbHelper.KEY_VAT_PROD_POSTING_GROUP, gstPostingSetup.getVATProdPostingGroup()); // VATProdPostingGroup
        values.put(dbHelper.KEY_VAT_PERCENT, gstPostingSetup.getVATPercent()); // VATPercent

        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_GST_POSTING_SETUP, null, values);
    }

    public boolean deleteGSTPostingSetup(String key) {
        boolean success = false;

        if (isGSTPostingSetupExist(key)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_GST_POSTING_SETUP, dbHelper.KEY_GST_POSTING_SETUP_KEY + " == ? ", new String[]{key});
            success = !isGSTPostingSetupExist(key);
        } else {
            success = true;
        }
        return success;
    }

    private boolean isGSTPostingSetupExist(String key) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_GST_POSTING_SETUP + " WHERE " + dbHelper.KEY_GST_POSTING_SETUP_KEY + " == ?";
        Cursor cursor = db.rawQuery(query, new String[]{key});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public List<GSTPostingSetup> getAllGSTPostingSetup() {

        List<GSTPostingSetup> postingSetupList = new ArrayList<GSTPostingSetup>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_GST_POSTING_SETUP;
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                GSTPostingSetup postingSetup = new GSTPostingSetup();
                postingSetup.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_GST_POSTING_SETUP_KEY)));
                postingSetup.setVATBusPostingGroup(c.getString(c.getColumnIndex(dbHelper.KEY_VAT_BUS_POSTING_GROUP)));
                postingSetup.setVATProdPostingGroup(c.getString(c.getColumnIndex(dbHelper.KEY_VAT_PROD_POSTING_GROUP)));
                postingSetup.setVATPercent(c.getDouble(c.getColumnIndex(dbHelper.KEY_VAT_PERCENT)));

                postingSetupList.add(postingSetup);
            } while (c.moveToNext());
        }

        c.close();
        return postingSetupList;
    }

    public String getGSTPrecent(String _busPostingPrecent, String _productPostingPrecent)
    {
        String _gSTPrecent="0.0", busPostingPrecent = "" , productPosting = "" ;

        busPostingPrecent = _busPostingPrecent == null ? "" : _busPostingPrecent;
        productPosting  = _productPostingPrecent == null ? "" : _productPostingPrecent;

        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_GST_POSTING_SETUP + " WHERE "
                + dbHelper.KEY_VAT_BUS_POSTING_GROUP + " = ? AND "
                + dbHelper.KEY_VAT_PROD_POSTING_GROUP + " = ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{busPostingPrecent, productPosting});

        int count_ = c.getCount();
        if (c.moveToFirst()) {
            _gSTPrecent = String.valueOf(c.getDouble(c.getColumnIndex(dbHelper.KEY_VAT_PERCENT)));
        }

        c.close();

        return _gSTPrecent;
    }

    public String getGSTPrecentage(String _prodPostingPrecent)
    {
        String _gSTPrecent="0.0", prodPostingGroup = ""  ;

        prodPostingGroup = _prodPostingPrecent == null ? "" : _prodPostingPrecent;
        String busPostingGroup = "GST";

        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_GST_POSTING_SETUP + " WHERE "
                + dbHelper.KEY_VAT_BUS_POSTING_GROUP + " = ? AND "
                + dbHelper.KEY_VAT_PROD_POSTING_GROUP + " = ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{prodPostingGroup, prodPostingGroup});


        if (c.moveToFirst()) {
            _gSTPrecent = String.valueOf((int)c.getDouble(c.getColumnIndex(dbHelper.KEY_VAT_PERCENT)));
        }

        c.close();

        return _gSTPrecent;
    }
}
