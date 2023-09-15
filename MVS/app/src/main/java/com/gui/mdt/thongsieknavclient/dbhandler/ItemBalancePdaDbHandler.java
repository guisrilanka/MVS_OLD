package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nelin_000 on 08/16/2017.
 */

public class ItemBalancePdaDbHandler {

    private Context context;
    private DatabaseHandler dbHelper;
    SQLiteDatabase db;

    public ItemBalancePdaDbHandler(Context context) {
        this.context = context;
    }

    public ItemBalancePdaDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new item pda
    public void addItemBalancePda(ItemBalancePda item) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_ITEM_BAL_PDA_KEY, item.getKey());
        values.put(dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO, item.getItemNo());
        values.put(dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE, item.getLocationCode());
        values.put(dbHelper.KEY_ITEM_BAL_PDA_BIN_CODE, item.getBinCode());
        values.put(dbHelper.KEY_ITEM_BAL_PDA_QTY, item.getQuantity());
        values.put(dbHelper.KEY_ITEM_BAL_PDA_UOM, item.getUnitofMeasureCode());
        values.put(dbHelper.KEY_ITEM_BAL_PDA_OPEN_QTY, item.getOpenQty());
        values.put(dbHelper.KEY_ITEM_BAL_PDA_EXCHANGED_QTY, item.getExchangedQty());

        // Inserting Row
        db=dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_ITEM_BALANCE_PDA, null, values);
    }




    public boolean isItemExist(String key) {

        db=dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_BALANCE_PDA
                + " WHERE "+ dbHelper.KEY_ITEM_BAL_PDA_KEY+" = ?";
        Cursor cursor = db.rawQuery(query, new String[] {key});
        int count = cursor.getCount();
        cursor.close();
        return count>0?true:false;
    }

    public boolean isItemExistbyItemNo(String itemNo) {

        db=dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_BALANCE_PDA
                + " WHERE "+ dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO+" = ?";
        Cursor cursor = db.rawQuery(query, new String[] {itemNo});
        int count = cursor.getCount();
        cursor.close();
        return count>0?true:false;
    }

    public boolean deleteItem(String key) {
        boolean success=false;

        if( isItemExist(key)){
            db=dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_ITEM_BALANCE_PDA, dbHelper.KEY_ITEM_BAL_PDA_KEY
                    + "=?", new String[]{String.valueOf(key)});

            success = !isItemExist(key);
        }
        else{
            success=true;
        }
        return success;
    }

    public boolean  deleteAllRecords(){
        db=dbHelper.getWritableDatabase();
        return db.delete(dbHelper.TABLE_ITEM_BALANCE_PDA, null, null)>=0;
    }

    public String getItemWarehouseQtyByItemCode(String code) {
        db=dbHelper.getReadableDatabase();
        String qty = "0";
        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_BALANCE_PDA
                + " WHERE "+ dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO+" = ?";
        Cursor cursor = db.rawQuery(query, new String[] {code});

        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();

            qty = cursor.getString(cursor.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_QTY));
        }
        cursor.close();
        return qty;
    }


    /*
    // use return object to get,
    // balance qty(quantity)
    //open qty (openQuantity)
    //exchanged qty (exchangedQuantity)
    */
    public ItemBalancePda getItemBalance(String itemNo,String locationCode) {

        ItemBalancePda item = new ItemBalancePda();
        db=dbHelper.getReadableDatabase();
        String qty = "0";
        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_BALANCE_PDA
                + " WHERE "+ dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE+" = ?"
                + " AND "+ dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO+" = ?";
        Cursor cursor = db.rawQuery(query, new String[] {locationCode,itemNo});


        if (cursor.moveToFirst()) {
            do {
                //Customer customer = new Customer();
                item = getItemBalanceFromCursor(item,cursor);

            } while (cursor.moveToNext());
        }


        cursor.close();
        return item;
    }

    /*
    use this method to update exchange qty and balance qty
    in the vehicle
     */
    public boolean updateItemBalance(String itemCode,String locationCode,float outQty,float inQty){

        boolean success = false;

        ItemBalancePda itemBalance = getItemBalance(itemCode,locationCode);
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_ITEM_BAL_PDA_QTY, (itemBalance.getQuantity()-outQty));
        contentValues.put(dbHelper.KEY_ITEM_BAL_PDA_EXCHANGED_QTY,inQty);


        if (db.update(dbHelper.TABLE_ITEM_BALANCE_PDA, contentValues,
                "" + dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE + " = ?" +" AND " + dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO + " =? ",
                new String[]{locationCode,itemCode}) == 1)
            success = true;
        else
            success = false;

        return success;
    }

    public boolean updateOpenQty(ItemBalancePda item){
        String itemNo = item.getItemNo();
        float qty = item.getOpenQty();
        boolean success = false;

        ItemBalancePda existingObj = getItemBalance(itemNo,item.getLocationCode());
        float newQty = existingObj.getOpenQty()+qty;

        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_ITEM_BAL_PDA_OPEN_QTY, (String.valueOf(newQty)));



        if (db.update(dbHelper.TABLE_ITEM_BALANCE_PDA, contentValues,
                " "+  dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO + " =? ",
                new String[]{itemNo}) == 1)
            success = true;
        else
            success = false;

        return success;
    }

    public boolean resetOpenQuantities(String driverCode){

        float resetQty = 0;
        boolean success = false;
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_ITEM_BAL_PDA_OPEN_QTY, resetQty);

        if (db.update(dbHelper.TABLE_ITEM_BALANCE_PDA, contentValues,
                "" + dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE + " = ?",
                new String[]{driverCode}) >= 0) {
            success = true;

      /*  if (db.update(dbHelper.TABLE_ITEM_BALANCE_PDA, contentValues,null,null) == 1)
            success = true;*/
        }else {
            success = false;
        }

        return success;

    }

    private ItemBalancePda getItemBalanceFromCursor(ItemBalancePda balance, Cursor c)
    {
        //customer.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_KEY)));
        balance.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_KEY)));
        balance.setItemNo(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO)));
        balance.setLocationCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE)));
        balance.setBinCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_BIN_CODE)));
        balance.setQuantity(c.getInt(c.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_QTY)));
        balance.setOpenQty(c.getInt(c.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_OPEN_QTY)));
        balance.setExchangedQty(c.getInt(c.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_EXCHANGED_QTY)));
        balance.setUnitofMeasureCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_UOM)));

        return balance;
    }

    public boolean isItemExistByItemNoAndItemUom(String itemCode,String itemUom, String locationCode) {

        db=dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_BALANCE_PDA
                + " WHERE "+ dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO+" = ? "
                + " AND " + dbHelper.KEY_ITEM_BAL_PDA_UOM + " = ? "
                + " AND " + dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[] {itemCode, itemUom, locationCode});
        int count = cursor.getCount();
        cursor.close();
        return count>0?true:false;
    }

    public ItemBalancePda getItemBalencePda(String itemCode, String itemUom,String locationCode)
    {
        ItemBalancePda item = new ItemBalancePda();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_BALANCE_PDA
                + " WHERE "+ dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO+" = ? "
                + " AND " + dbHelper.KEY_ITEM_BAL_PDA_UOM + " = ? "
                + " AND " + dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[] {itemCode, itemUom , locationCode});

        if(cursor.moveToFirst())
        {
            item = getItemBalanceFromCursor(item,cursor);
        }
        return  item;
    }

    public boolean updateItemBalencePdaByMvsSo(
            String itemCode
            , String itemUom
            , String locationCode
            , float qty
            , float exchQty
            , boolean isVoid) {

        boolean success = false;
        float totalQty = 0f, totalExchQty = 0f;
        db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        if (isItemExistByItemNoAndItemUom(itemCode, itemUom, locationCode)) //item exist.
        {
            ItemBalancePda item = getItemBalencePda(itemCode, itemUom, locationCode);

            if (isVoid) {
                totalQty = item.getQuantity() - qty;
                totalExchQty = item.getExchangedQty() - exchQty;
            } else {
                totalQty = item.getQuantity() + qty;
                totalExchQty = item.getExchangedQty() + exchQty;
            }

            contentValues.put(dbHelper.KEY_ITEM_BAL_PDA_QTY, totalQty);
            contentValues.put(dbHelper.KEY_ITEM_BAL_PDA_EXCHANGED_QTY, totalExchQty);

            if (db.update(dbHelper.TABLE_ITEM_BALANCE_PDA, contentValues,
                    "" + dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE + " = ?"
                            + " AND " + dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO + " = ?"
                            + " AND " + dbHelper.KEY_ITEM_BAL_PDA_UOM + " =? ",
                    new String[]{locationCode, itemCode, itemUom}) == 1)
                success = true;
            else
                success = false;
        } else {
            return false;
        }
        return success;
    }

    public boolean resetItemBalancePda(String driverCode) {

        String driverCode_ = driverCode == null ? "" : driverCode;

        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_ITEM_BAL_PDA_QTY, (String.valueOf(new Float(0))));
        contentValues.put(dbHelper.KEY_ITEM_BAL_PDA_OPEN_QTY, (String.valueOf(new Float(0))));
        contentValues.put(dbHelper.KEY_ITEM_BAL_PDA_EXCHANGED_QTY, (String.valueOf(new Float(0))));

        int result = db.update(dbHelper.TABLE_ITEM_BALANCE_PDA, contentValues,
                " " + dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE + " = ? ",
                new String[]{driverCode_});

        return result > 0;
    }

    public List<ItemBalancePda> getItemBalanceList(String locationCode) {

        List<ItemBalancePda> itemPdaList
                = new ArrayList<ItemBalancePda>();

        db=dbHelper.getReadableDatabase();
        /*String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_BALANCE_PDA
                + " WHERE "+ dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE+" = ?";*/


        String query =  "SELECT  " + dbHelper.TABLE_ITEM_BALANCE_PDA + ".*," + dbHelper.TABLE_ITEM + "."
                + dbHelper.KEY_ITM_DESCRIPTION + " FROM " +
                dbHelper.TABLE_ITEM_BALANCE_PDA +
                " INNER JOIN " + dbHelper.TABLE_ITEM + " ON "
                + dbHelper.TABLE_ITEM_BALANCE_PDA + "." + dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO + " = " + dbHelper.TABLE_ITEM
                + "." + dbHelper.KEY_ITM_CODE
                + " WHERE "+ dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE+" = ?"
                + " AND (" +
                "(CAST(" +dbHelper.TABLE_ITEM_BALANCE_PDA + "." + dbHelper.KEY_ITEM_BAL_PDA_OPEN_QTY
                + " as decimal) - CAST( " + dbHelper.TABLE_ITEM_BALANCE_PDA + "." + dbHelper.KEY_ITEM_BAL_PDA_QTY +" as decimal) )>0 OR "
                +" CAST("+dbHelper.TABLE_ITEM_BALANCE_PDA + "." + dbHelper.KEY_ITEM_BAL_PDA_EXCHANGED_QTY+" as decimal )"+">0"
                +")" ;


        Cursor c = db.rawQuery(query, new String[] {locationCode});

        if (c.moveToFirst()) {
            do {
                ItemBalancePda itemPda = new ItemBalancePda();
                itemPda = getItemBalanceFromCursor(itemPda,c);
                itemPda.setItemDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));

                itemPdaList.add(itemPda);
            } while (c.moveToNext());
        }

        c.close();

        //sort item list by vehicle qty - Descending
        Collections.sort(itemPdaList, ComparatorItemVclQty);
        Collections.sort(itemPdaList, ComparatorItemExchQty);


        return itemPdaList;
    }

    public static Comparator<ItemBalancePda> ComparatorItemVclQty = new Comparator<ItemBalancePda>() {

        public int compare(ItemBalancePda item1, ItemBalancePda item2) {

            int qty1 = Math.round(item1.getOpenQty()-item1.getQuantity());
            int qty2 = Math.round(item1.getOpenQty()-item1.getQuantity());

	        /*For descending order*/
            return qty2 - qty1;
        }
    };
    public static Comparator<ItemBalancePda> ComparatorItemExchQty = new Comparator<ItemBalancePda>() {

        public int compare(ItemBalancePda item1, ItemBalancePda item2) {

            int qty1 = Math.round(item1.getExchangedQty());
            int qty2 = Math.round(item2.getExchangedQty());

	        /*For descending order*/
            return qty2 - qty1;
        }
    };
}
