package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.StockTransferRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by GUI-NB03 on 2017-09-18.
 */

public class StockTransferRequestDbHandler {

    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;

    public StockTransferRequestDbHandler(Context context) {
        this.context = context;
    }

    public StockTransferRequestDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public List<StockTransferRequest> getStockTransferRequestListInList(String filterStockTransferInNo,
                                                                        String filterStockTransferInDate, String filterStockTransferType)
    {
        List<StockTransferRequest> stockTransferRequest = new ArrayList<StockTransferRequest>();
        db = dbHelper.getReadableDatabase();

        String _filterStockTransferInNo = filterStockTransferInNo.isEmpty() ? "" : filterStockTransferInNo;
        String _filterStockTransferInDate = "";
        String _filterStockTransferType = filterStockTransferType;

        if (filterStockTransferInDate.isEmpty()) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dateobj = new Date();
            _filterStockTransferInDate = df.format(dateobj).toString();
        } else {
            _filterStockTransferInDate = filterStockTransferInDate;
        }

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_STOCK_TRANSFER
                + " WHERE "
                + dbHelper.KEY_ST_NO + " LIKE ? AND date("
                + dbHelper.KEY_ST_DATE + ") LIKE ? AND "
                + dbHelper.KEY_ST_TYPE +"= ? "+ "ORDER BY datetime(" + dbHelper.KEY_ST_DATE + ") DESC";

        Cursor c = db.rawQuery(selectQuery, new String[]{"%" +_filterStockTransferInNo+ "%",
                "%" + _filterStockTransferInDate + "%",_filterStockTransferType});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                StockTransferRequest stockTransferIn = new StockTransferRequest();
                stockTransferIn.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_ST_KEY)));
                stockTransferIn.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_ST_NO)));
                stockTransferIn.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_ST_DRIVER_CODE))); 
                stockTransferIn.setStockTransferDate(c.getString(c.getColumnIndex(dbHelper.KEY_ST_DATE)));
                stockTransferIn.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_ST_DOCUMENT_DATE)));
                stockTransferIn.setStockTransferType(c.getString(c.getColumnIndex(dbHelper.KEY_ST_TYPE)));
                stockTransferIn.setTotalQuantity(c.getString(c.getColumnIndex(dbHelper.KEY_ST_TOTAL_QTY)));
                stockTransferIn.setNoOfItems(c.getString(c.getColumnIndex(dbHelper.KEY_ST_NO_OF_ITEMS)));
                stockTransferIn.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_ST_STATUS)));
                stockTransferIn.setTransferred(c.getString(c.getColumnIndex(dbHelper.KEY_ST_TRANSFERRED)));
                stockTransferIn.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_ST_CREATED_BY)));
                stockTransferIn.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_ST_CREATED_DATE)));
                stockTransferIn.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LAST_MODIFIED_BY)));
                stockTransferIn.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LAST_MODIFIED_DATE)));
                stockTransferIn.setLastTransferredBy(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LAST_TRANSFERRED_BY)));
                stockTransferIn.setLastTransferredDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_ST_LAST_TRANSFERRED_DATETIME)));

                String _pending = context.getResources().getString(R.string.StockTransferStatusPending);
                if (c.getInt(c.getColumnIndex(dbHelper.KEY_ST_STATUS)) == Integer.parseInt(_pending)) {
                    stockTransferIn.setConfirmedSt(false);
                } else {
                    stockTransferIn.setConfirmedSt(true);
                }

                stockTransferRequest.add(stockTransferIn);
            } while (c.moveToNext());
        }

        c.close();
        return stockTransferRequest;
    }

    public boolean saveConfirmStockTransferRequestIn(List<StockTransferRequest> comfirmedStockTransferRequestList) {
        List<StockTransferRequest> comfirmedSalesOrderList = new ArrayList<>();
        boolean success = false;
        for (StockTransferRequest st : comfirmedStockTransferRequestList) {
            if (st.isConfirmedSt()) {
                db = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(dbHelper.KEY_SO_TRANSFERRED, st.getTransferred());
                contentValues.put(dbHelper.KEY_SO_STATUS, st.getStatus());
                contentValues.put(dbHelper.KEY_SO_LAST_TRANSFERRED_BY, st.getLastTransferredBy());
                contentValues.put(dbHelper.KEY_SO_LAST_TRANSFERRED_DATETIME, st.getLastTransferredDateTime());
                contentValues.put(dbHelper.KEY_SO_LAST_MODIFIED_BY, st.getLastModifiedBy());
                contentValues.put(dbHelper.KEY_SO_LAST_MODIFIED_DATE, st.getLastModifiedDateTime());

                comfirmedSalesOrderList.add(st);

                String id = String.valueOf(st.getKey());

                if (db.update(dbHelper.TABLE_STOCK_TRANSFER, contentValues, "" + dbHelper.KEY_ST_KEY + " = ?", new String[]{id}) == 1)
                    success = true;
                else
                    success = false;
            }
        }
        return success;
    }

    public boolean deleteStockTransfer(String no)
    {
        boolean success = false;

        if (isSoExist(no)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_STOCK_TRANSFER, dbHelper.KEY_ST_NO + "=?", new String[]{String.valueOf(no)});
            success = !isSoExist(no);
        } else {
            success = true;
        }
        return success;
    }

    private boolean isSoExist(String no) {
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_STOCK_TRANSFER + " WHERE " + dbHelper.KEY_ST_NO + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{no});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public void addStockTransfer(StockTransferRequest tempTransferIn) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_ST_KEY, tempTransferIn.getKey());
        values.put(dbHelper.KEY_ST_NO, tempTransferIn.getNo());
        values.put(dbHelper.KEY_ST_DRIVER_CODE, tempTransferIn.getDriverCode());
        values.put(dbHelper.KEY_ST_DATE, tempTransferIn.getStockTransferDate());
        values.put(dbHelper.KEY_ST_DOCUMENT_DATE, tempTransferIn.getDocumentDate());
        values.put(dbHelper.KEY_ST_TYPE,tempTransferIn.getStockTransferType());
        values.put(dbHelper.KEY_ST_TOTAL_QTY,tempTransferIn.getTotalQuantity());
        values.put(dbHelper.KEY_ST_NO_OF_ITEMS,tempTransferIn.getNoOfItems());
        values.put(dbHelper.KEY_ST_STATUS, tempTransferIn.getStatus());
        values.put(dbHelper.KEY_ST_TRANSFERRED, tempTransferIn.getTransferred());
        values.put(dbHelper.KEY_ST_CREATED_BY, tempTransferIn.getCreatedBy());
        values.put(dbHelper.KEY_ST_CREATED_DATE, tempTransferIn.getCreatedDateTime());
        values.put(dbHelper.KEY_ST_LAST_MODIFIED_BY, tempTransferIn.getLastModifiedBy());
        values.put(dbHelper.KEY_ST_LAST_MODIFIED_DATE, tempTransferIn.getLastModifiedDateTime());
        values.put(dbHelper.KEY_ST_LAST_TRANSFERRED_BY,tempTransferIn.getLastTransferredBy());
        values.put(dbHelper.KEY_ST_LAST_TRANSFERRED_DATETIME,tempTransferIn.getLastTransferredDateTime());

        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_STOCK_TRANSFER, null, values);
    }
}
