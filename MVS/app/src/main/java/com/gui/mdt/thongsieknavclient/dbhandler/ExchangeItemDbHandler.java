package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ExchangeItem;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;

import java.util.ArrayList;
import java.util.List;


public class ExchangeItemDbHandler {

    private Context context;
    private DatabaseHandler dbHelper;
    SQLiteDatabase db;

    public ExchangeItemDbHandler(Context context) {
        this.context = context;
    }

    public ExchangeItemDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new item
    public void addItems(ExchangeItem item) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_EXCHANGE_ITEM_CODE, item.getItemCode());
        values.put(dbHelper.KEY_EXCHANGE_ITEM_UOM, item.getUom());
        values.put(dbHelper.KEY_EXCHANGE_ITEM_QTY, item.getTotalQty());
        values.put(dbHelper.KEY_EXCHANGE_ITEM_ISSUE_QTY, item.getIssueQty());
        values.put(dbHelper.KEY_EXCHANGE_ITEM_BALANCE_QTY, item.getBalanceQty());


        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_EXCHANGE_ITEM, null, values);
    }

    public boolean isItemExist(String code, String uom) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_EXCHANGE_ITEM + " WHERE "
                + dbHelper.KEY_EXCHANGE_ITEM_CODE + " = ?"
                + " AND " + dbHelper.KEY_EXCHANGE_ITEM_UOM + " = ? ";
        Cursor cursor = db.rawQuery(query, new String[]{code,uom});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }
//    get exchange items by itemCode
    public List<ExchangeItem> getExchangeItemByItemCode(String code) {
        List<ExchangeItem> itemList = new ArrayList<ExchangeItem>();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_EXCHANGE_ITEM + " WHERE " + dbHelper.KEY_EXCHANGE_ITEM_CODE + " = ?";
        Cursor c = db.rawQuery(query, new String[]{code});


//        c.moveToFirst();
        if (c.moveToFirst()) {
            do {
                ExchangeItem item = new ExchangeItem();
                item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ID))));
                item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_CODE)));
//                item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                item.setUom(c.getString(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_UOM)));
                item.setTotalQty(c.getFloat(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_QTY)));
                item.setIssueQty(c.getFloat(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_ISSUE_QTY)));
                item.setBalanceQty(c.getFloat(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_BALANCE_QTY)));

                itemList.add(item);
            } while (c.moveToNext());
        }
            c.close();
            return itemList;
    }
    // Getting All Items
    public List<ExchangeItem> getAllItems() {

        List<ExchangeItem> itemList = new ArrayList<ExchangeItem>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + dbHelper.TABLE_EXCHANGE_ITEM + " ei"
                + " LEFT JOIN " + dbHelper.TABLE_ITEM + " i "
                + " ON ei." + dbHelper.KEY_EXCHANGE_ITEM_CODE + " = i." + dbHelper.KEY_ITM_CODE + " WHERE  ei." + dbHelper.KEY_EXCHANGE_ITEM_BALANCE_QTY +" > ?";

        Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(0.0)});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ExchangeItem item = new ExchangeItem();
                item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ID))));
                item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_CODE)));
                item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                item.setUom(c.getString(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_UOM)));
                item.setTotalQty(c.getFloat(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_QTY)));
                item.setIssueQty(c.getFloat(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_ISSUE_QTY)));
                item.setBalanceQty(c.getFloat(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_BALANCE_QTY)));

                itemList.add(item);
            } while (c.moveToNext());
        }

        c.close();
        return itemList;
    }

    public boolean  deleteAllRecords(){
        db=dbHelper.getWritableDatabase();
        return db.delete(dbHelper.TABLE_EXCHANGE_ITEM, null, null)>=0;
    }

    public boolean updateTotalAndBalanceQty(ExchangeItem item, boolean isVoid){
        String itemNo = item.getItemCode();
        float qty = item.getTotalQty();
        boolean success = false;

        ExchangeItem existingObj = getItemBalance(itemNo,item.getUom());
        float totalQty = 0f;
        float balanceQty = 0f;

        if(isVoid){
            totalQty = existingObj.getTotalQty()-qty;
            balanceQty = existingObj.getBalanceQty()-qty;
        }else{
            totalQty = existingObj.getTotalQty()+qty;
            balanceQty = existingObj.getBalanceQty()+qty;
        }


        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_EXCHANGE_ITEM_QTY, (String.valueOf(totalQty)));
        contentValues.put(dbHelper.KEY_EXCHANGE_ITEM_BALANCE_QTY, (String.valueOf(balanceQty)));



        if (db.update(dbHelper.TABLE_EXCHANGE_ITEM, contentValues,  // prevous table is TABLE_ITEM_BALANCE_PDA
                " "+  dbHelper.KEY_EXCHANGE_ITEM_CODE + " =? "
                + " AND " + dbHelper.KEY_EXCHANGE_ITEM_UOM + " = ?",
                new String[]{itemNo,item.getUom()}) == 1)
            success = true;
        else
            success = false;

        return success;
    }

    public boolean updateIssueQty(ExchangeItem item, boolean isVoid){
        String itemNo = item.getItemCode();

        boolean success = false;

        ExchangeItem existingObj = getItemBalance(itemNo,item.getUom());
        float issueQty = 0f;
        float balanceQty = 0f;


        if(isVoid){
            issueQty = item.getIssueQty()-item.getIssueQty();
            balanceQty = existingObj.getBalanceQty()+item.getIssueQty();
        } else {
            issueQty = existingObj.getIssueQty()+ item.getIssueQty();
            balanceQty = existingObj.getBalanceQty()-item.getIssueQty();
        }



        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_EXCHANGE_ITEM_ISSUE_QTY, (String.valueOf(issueQty)));
        contentValues.put(dbHelper.KEY_EXCHANGE_ITEM_BALANCE_QTY, (String.valueOf(balanceQty)));


        if (db.update(dbHelper.TABLE_EXCHANGE_ITEM, contentValues,
                " "+  dbHelper.KEY_EXCHANGE_ITEM_CODE + " =? "
                        + " AND " + dbHelper.KEY_EXCHANGE_ITEM_UOM + " = ?",
                new String[]{itemNo,item.getUom()}) == 1)
            success = true;
        else
            success = false;

        return success;
    }

    public ExchangeItem getItemBalance(String itemNo,String Uom) {

        ExchangeItem item = new ExchangeItem();
        db=dbHelper.getReadableDatabase();
        String qty = "0";
        String query = "SELECT * FROM " + dbHelper.TABLE_EXCHANGE_ITEM
                + " WHERE "+ dbHelper.KEY_EXCHANGE_ITEM_UOM+" = ?"
                + " AND "+ dbHelper.KEY_EXCHANGE_ITEM_CODE+" = ?";
        Cursor cursor = db.rawQuery(query, new String[] {Uom,itemNo});


        if (cursor.moveToFirst()) {
            do {
                item = getExchangeItemBalanceFromCursor(item,cursor);

            } while (cursor.moveToNext());
        }


        cursor.close();
        return item;
    }

    private ExchangeItem getExchangeItemBalanceFromCursor(ExchangeItem item, Cursor c)
    {
        item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ID))));
        item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_CODE)));
        item.setUom(c.getString(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_UOM)));
        item.setTotalQty(c.getFloat(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_QTY)));
        item.setIssueQty(c.getFloat(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_ISSUE_QTY)));
        item.setBalanceQty(c.getFloat(c.getColumnIndex(dbHelper.KEY_EXCHANGE_ITEM_BALANCE_QTY)));

        return item;
    }


}
