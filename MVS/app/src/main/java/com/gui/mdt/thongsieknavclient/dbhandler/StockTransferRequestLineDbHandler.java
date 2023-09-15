package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.datamodel.StockTransferRequestLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GUI-NB03 on 2017-09-20.
 */

public class StockTransferRequestLineDbHandler {
    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;

    public StockTransferRequestLineDbHandler(Context context) {
        this.context = context;
    }

    public StockTransferRequestLineDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public List<StockTransferRequestLine> getAllTransferRequestInLinesByNo(String no) {
        List<StockTransferRequestLine> transferRequestInLines = new ArrayList<StockTransferRequestLine>();
        db=dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_STOCK_TRANSFER_LINE
                + " WHERE "+ dbHelper.KEY_ST_LINE_ST_NO +" = ?";
        Cursor c = db.rawQuery(selectQuery,  new String[] {no});

        if (c.moveToFirst()) {
            do {
                StockTransferRequestLine transferInLines = new StockTransferRequestLine();
                transferInLines.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LINE_KEY)));
                transferInLines.setStockTransferLineNo(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LINE_NO)));
                transferInLines.setStockTransferNo(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LINE_ST_NO)));
                transferInLines.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LINE_DRIVER_CODE)));
                transferInLines.setQuantity(c.getFloat(c.getColumnIndex(dbHelper.KEY_ST_LINE_QTY)));
                transferInLines.setUnitofMeasure(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LINE_UOM)));
                transferInLines.setItemNo(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LINE_ITEM_NO)));
                transferInLines.setItemDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LINE_ITEM_DESCRIPTION)));

                transferRequestInLines.add(transferInLines);
            }while (c.moveToNext());
        }
        c.close();
        return transferRequestInLines;
    }

    public boolean deleteStockTransferLine(String key) {
        boolean success=false;

        if( isSoLineExist(key)){
            db=dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_STOCK_TRANSFER_LINE, dbHelper.KEY_ST_LINE_KEY + "=?", new String[]{String.valueOf(key)});
            success = !isSoLineExist(key);
        }
        else{
            success=true;
        }
        return success;
    }

    private boolean isSoLineExist(String key) {
        db=dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_STOCK_TRANSFER_LINE + " WHERE "+ dbHelper.KEY_ST_LINE_KEY+" = ?";
        Cursor cursor = db.rawQuery(query, new String[] {key});
        int count = cursor.getCount();
        cursor.close();
        return count>0?true:false;
    }

    public void addStockTransferLine(StockTransferRequestLine stl) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_ST_LINE_KEY, stl.getKey());
        values.put(dbHelper.KEY_ST_LINE_NO, stl.getStockTransferLineNo());
        values.put(dbHelper.KEY_ST_LINE_ST_NO, stl.getStockTransferNo());
        values.put(dbHelper.KEY_ST_LINE_DRIVER_CODE, stl.getDriverCode());
        values.put(dbHelper.KEY_ST_LINE_QTY, stl.getQuantity());
        values.put(dbHelper.KEY_ST_LINE_UOM, stl.getUnitofMeasure());
        values.put(dbHelper.KEY_ST_LINE_ITEM_NO, stl.getItemNo());
        values.put(dbHelper.KEY_ST_LINE_ITEM_DESCRIPTION, stl.getItemDescription());
        // Inserting Row
        db=dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_STOCK_TRANSFER_LINE, null, values);
    }
}
