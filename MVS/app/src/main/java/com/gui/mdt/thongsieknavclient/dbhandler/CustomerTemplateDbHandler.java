package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.CustomerTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nelin_000 on 12/21/2017.
 */

public class CustomerTemplateDbHandler {
    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;
    private NavClientApp mApp;


    public CustomerTemplateDbHandler(Context context) {
        this.context = context;
    }

    public CustomerTemplateDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        this.mApp = (NavClientApp) context.getApplicationContext();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new customer template
    public void addCustomerTemplate(CustomerTemplate customerTemplate) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_CUS_TEMP_SALES_CODE, customerTemplate.getSalesCode());
        values.put(dbHelper.KEY_CUS_TEMP_ITEM_NO, customerTemplate.getItemNo());
        values.put(dbHelper.KEY_CUS_TEMP_DESCRIPTION,customerTemplate.getDescription());
        values.put(dbHelper.KEY_CUS_TEMP_QUANTITY,customerTemplate.getQuantity());
        values.put(dbHelper.KEY_CUS_TEMP_ITEM_UOM, customerTemplate.getItemUom());

        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_CUSTOMER_TEMPLATE, null, values);
    }

    public boolean isCustomerTemplateExist(String code,String no) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_CUSTOMER_TEMPLATE
                + " WHERE " + dbHelper.KEY_CUS_TEMP_SALES_CODE + " = ? "
                +" AND " +  dbHelper.KEY_CUS_TEMP_ITEM_NO + " = ? ";
        Cursor cursor = db.rawQuery(query, new String[]{code,no});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public boolean deleteCustomerTemplate(String code, String no) {
        boolean success = false;

        if (isCustomerTemplateExist(code,no)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_CUSTOMER_TEMPLATE,
                    dbHelper.KEY_CUS_TEMP_SALES_CODE + " = ? " + " AND " +  dbHelper.KEY_CUS_TEMP_ITEM_NO + " = ? "  ,
                    new String[]{code,no});
            success = !isCustomerTemplateExist(code,no);
        } else {
            success = true;
        }
        return success;
    }

    public boolean deleteCustomerTemplate() {
        boolean success = false;

        //if (isCustomerTemplateExist(code,no)) {
            db = dbHelper.getWritableDatabase();
            success =db.delete(dbHelper.TABLE_CUSTOMER_TEMPLATE,null,null)>=0;
            //success = !isCustomerTemplateExist(code,no);
        //} else {
            //success = true;
        //}
        return success;
    }

    public List<CustomerTemplate> getCusTemplateItemsByCusCode(String customerCode) {

        List<CustomerTemplate> itemList
                = new ArrayList<CustomerTemplate>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + dbHelper.TABLE_CUSTOMER_TEMPLATE + " ct" + " WHERE "
                + "ct." + dbHelper.KEY_CUS_TEMP_SALES_CODE
                + " = ( SELECT csc." + dbHelper.KEY_CSD_CODE + " FROM "
                + dbHelper.TABLE_CUSTOMER_SALES_CODE + " csc " +
                " WHERE " + "csc." + dbHelper.KEY_CSD_CUSTOMER_NO + " = ? LIMIT 1)";

        Cursor c = db.rawQuery(selectQuery, new String[]{customerCode});

        // looping through all rows and adding to list
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    CustomerTemplate cusTemplate = new CustomerTemplate();
                    cusTemplate = getCusTemplateFromCursor(cusTemplate, c);
                    itemList.add(cusTemplate);
                } while (c.moveToNext());
            }
        }
        c.close();
        return itemList;
    }

    private CustomerTemplate getCusTemplateFromCursor(CustomerTemplate customerTemplate, Cursor c) {
        customerTemplate.setSalesCode(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_TEMP_SALES_CODE)));
        customerTemplate.setItemNo(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_TEMP_ITEM_NO)));
        customerTemplate.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_TEMP_DESCRIPTION)));
        customerTemplate.setQuantity(c.getFloat(c.getColumnIndex(dbHelper.KEY_CUS_TEMP_QUANTITY)));
        customerTemplate.setItemUom(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_TEMP_ITEM_UOM)));

        return customerTemplate;
    }

    public boolean isCusTempExistByCusNoAndItemNo(String cusCode, String itemNo) {

        String cusSalesCode = "";

        CustomerSalesCodeDbHandler cscDb = new CustomerSalesCodeDbHandler(context);
        cscDb.open();
        cusSalesCode = cscDb.getCusSalesCodeByCusNo(cusCode);
        cscDb.close();

        db = dbHelper.getReadableDatabase();
        return isCustomerTemplateExist(cusSalesCode, itemNo);
    }


}
