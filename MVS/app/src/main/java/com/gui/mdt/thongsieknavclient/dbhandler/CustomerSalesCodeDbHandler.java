package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.CustomerSalesCode;

/**
 * Created by nelin_000 on 12/21/2017.
 */

public class CustomerSalesCodeDbHandler {

    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;
    private NavClientApp mApp;


    public CustomerSalesCodeDbHandler(Context context) {
        this.context = context;
    }

    public CustomerSalesCodeDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        this.mApp = (NavClientApp) context.getApplicationContext();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new customer
    public void addCustomerSalesCode(CustomerSalesCode csd) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_CSD_CODE, csd.getCode());
        values.put(dbHelper.KEY_CSD_CUSTOMER_NO, csd.getCustomerNo());
        values.put(dbHelper.KEY_CSD_DESCRIPTION,csd.getDescription());
        values.put(dbHelper.KEY_CSD_VALID_FROM_DATE,csd.getValidFromDate());
        values.put(dbHelper.KEY_CSD_VALID_TO_DATE,csd.getValidToDate());
        values.put(dbHelper.KEY_CSD_BLOCKED, csd.isBlocked());

        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_CUSTOMER_SALES_CODE, null, values);
    }

    public boolean isCustomerSalesCodeExist(String code) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_CUSTOMER_SALES_CODE + " WHERE " + dbHelper.KEY_CSD_CODE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{code});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public boolean deleteCustomerSalesCode(String code) {
        boolean success = false;

        if (isCustomerSalesCodeExist(code)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_CUSTOMER_SALES_CODE, dbHelper.KEY_CSD_CODE + "=?", new String[]{String.valueOf(code)});
            success = !isCustomerSalesCodeExist(code);
        } else {
            success = true;
        }
        return success;
    }

    public boolean deleteCustomerSalesCode() {
        boolean success = false;

        //if (isCustomerSalesCodeExist(code)) {
            db = dbHelper.getWritableDatabase();
            if(db.delete(dbHelper.TABLE_CUSTOMER_SALES_CODE,null,null)>=0){
                success=true;
            }
            //success = !isCustomerSalesCodeExist(code);
        //} else {
            //success = true;
        //}
        return success;
    }

    public String getCusSalesCodeByCusNo(String cusNo) {
        String cusSalesCode = "";
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM "
                + dbHelper.TABLE_CUSTOMER_SALES_CODE
                + " WHERE " + dbHelper.KEY_CSD_CUSTOMER_NO + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{cusNo});

        if (cursor.moveToFirst()) {
            cusSalesCode = cursor.getString(cursor.getColumnIndex(dbHelper.KEY_CSD_CODE));
        }

        cursor.close();
        return cusSalesCode;
    }


}
