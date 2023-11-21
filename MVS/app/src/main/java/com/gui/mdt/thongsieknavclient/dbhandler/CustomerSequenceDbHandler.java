package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;

public class CustomerSequenceDbHandler {
    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;
    private NavClientApp mApp;

    public CustomerSequenceDbHandler(Context context){
        this.context=context;
    }
    public CustomerSequenceDbHandler open()throws SQLException{
        dbHelper=new DatabaseHandler(context);
        this.mApp= (NavClientApp) context.getApplicationContext();
        return this;
    }
    public void addCustomer(Customer customer){
        ContentValues values=new ContentValues();
        values.put(dbHelper.KEY_CUS_CODE,customer.getCode());
        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_CUSTOMER_SEQUENCE, null, values);
    }
    public  void close(){
        dbHelper.close();
    }
    public boolean deleteAllDownloadedCustomerSequence() {


        db = dbHelper.getReadableDatabase();
        int affectedRows =  db.delete(dbHelper.TABLE_CUSTOMER_SEQUENCE, null, null);
        return affectedRows >= 0;
    }
    
//    public
}
