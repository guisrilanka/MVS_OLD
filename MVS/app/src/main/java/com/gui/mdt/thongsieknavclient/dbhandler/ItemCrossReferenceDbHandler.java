package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.datamodel.ItemCrossReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhanuka on 09/08/2017.
 */

public class ItemCrossReferenceDbHandler {


    private Context context;
    private DatabaseHandler dbHelper;
    SQLiteDatabase db;

    public ItemCrossReferenceDbHandler(Context context) {
        this.context = context;
    }

    public ItemCrossReferenceDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding New Item Cross Reference
    public void addItemCrossReference(ItemCrossReference itemCrossReference) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_KEY, itemCrossReference.getKey());
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_TYPE, itemCrossReference.getItemCrossReferenceType());
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_TYPE_NO, itemCrossReference.getItemCrossReferenceTypeNo());
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_NO, itemCrossReference.getItemCrossReferenceNo());
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_ITEM_NO, itemCrossReference.getItemNo());
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_VARIANT_CODE, itemCrossReference.getVariantCode());
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_UNIT_OF_MEASURE, itemCrossReference.getUnitOfMeasure());
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_UOM, itemCrossReference.getItemCrossReferenceUOM());
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_DESCRIPTION, itemCrossReference.getDescription());
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_DISCONTINUE_BAR_CODE, itemCrossReference.getDiscontinueBarCode());
        values.put(dbHelper.KEY_ITEM_CROSS_REFERENCE_ITEM_NO,itemCrossReference.getItemNo());

        // Inserting Row
        db=dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_ITEM_CROSS_REFERENCE, null, values);
    }

    public boolean isItemExist(String code) {

        db=dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_CROSS_REFERENCE + " WHERE "+ dbHelper.KEY_ITEM_CROSS_REFERENCE_KEY+" = ?";
        Cursor cursor = db.rawQuery(query, new String[] {code});
        int count = cursor.getCount();
        cursor.close();
        return count>0?true:false;
    }

    // Getting All Item Cross References
    public List<ItemCrossReference> getAllItemCrossReferences() {

        List<ItemCrossReference> itemCrossReferenceList = new ArrayList<ItemCrossReference>();
        db=dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM_CROSS_REFERENCE;
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ItemCrossReference itemcrossreference = new ItemCrossReference();

                itemcrossreference.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_KEY)));
                itemcrossreference.setItemCrossReferenceType(Integer.valueOf(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_TYPE))));
                itemcrossreference.setItemCrossReferenceTypeNo(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_TYPE_NO)));
                itemcrossreference.setItemCrossReferenceNo(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_NO)));
                itemcrossreference.setItemNo(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_ITEM_NO)));
                itemcrossreference.setVariantCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_VARIANT_CODE)));
                itemcrossreference.setUnitOfMeasure(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_UNIT_OF_MEASURE)));
                itemcrossreference.setItemCrossReferenceUOM(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_UOM)));
                itemcrossreference.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_DESCRIPTION)));
                itemcrossreference.setDiscontinueBarCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_DISCONTINUE_BAR_CODE)));

                itemCrossReferenceList.add(itemcrossreference);
            } while (c.moveToNext());
        }

        c.close();
        return itemCrossReferenceList;
    }

    // Getting Item Cross Reference Count
    public int getItemCrossReferenceCount() {
        db=dbHelper.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM_CROSS_REFERENCE;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }


//    public boolean deleteItem(String itemNo,String cusCode, String key) {
    public boolean deleteItem() {
        boolean deleted =false;

        try {
            db = dbHelper.getWritableDatabase();


//            deleted = db.delete(
//                    dbHelper.TABLE_ITEM_CROSS_REFERENCE,
//                    dbHelper.KEY_ITEM_CROSS_REFERENCE_KEY + " = ?"
//                    , new String[]{key})>0;
//
//
//            if(!deleted){
            //2021/06/01  changed by lasith . every item coudn't delete by key .
                deleted = db.delete(
                        dbHelper.TABLE_ITEM_CROSS_REFERENCE
                        ,null,null)>=0;
//            }

//            deleted = true;
        } catch (Exception ex) {
            deleted = false;
        }

        return deleted;
    }

    public ItemCrossReference getItemCrossreference(String itemNo_, String itemUom_, String customerCode_)
    {
        String cusCode="", itemCode = "", itemUom;

        cusCode = customerCode_ == null ? "" : customerCode_;
        itemCode = itemNo_ == null ? "" : itemNo_;
        itemUom = itemUom_ == null ? "" : itemUom_;

        ItemCrossReference itemCrossReference = new ItemCrossReference();
        db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM_CROSS_REFERENCE +
                            " WHERE " + dbHelper.KEY_ITEM_CROSS_REFERENCE_ITEM_NO + " = ? AND "
                                    + dbHelper.KEY_ITEM_CROSS_REFERENCE_UNIT_OF_MEASURE + " = ? AND "
                                    + dbHelper.KEY_ITEM_CROSS_REFERENCE_TYPE_NO + " = ? ";

        Cursor cursor = db.rawQuery(countQuery, new String[]{itemCode, itemUom , cusCode});

        if(cursor.moveToFirst())
        {
            itemCrossReference = getItemCrossReferenceFromCursor(cursor, itemCrossReference);
        }
        cursor.close();

        return  itemCrossReference;
    }

    public ItemCrossReference getItemCrossReferenceFromCursor(Cursor c, ItemCrossReference itemcrossreference)
    {
        itemcrossreference.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_KEY)));
        itemcrossreference.setItemCrossReferenceType(Integer.valueOf(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_TYPE))));
        itemcrossreference.setItemCrossReferenceTypeNo(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_TYPE_NO)));
        itemcrossreference.setItemCrossReferenceNo(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_NO)));
        itemcrossreference.setItemNo(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_ITEM_NO)));
        itemcrossreference.setVariantCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_VARIANT_CODE)));
        itemcrossreference.setUnitOfMeasure(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_UNIT_OF_MEASURE)));
        itemcrossreference.setItemCrossReferenceUOM(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_UOM)));
        itemcrossreference.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_DESCRIPTION)));
        itemcrossreference.setDiscontinueBarCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CROSS_REFERENCE_DISCONTINUE_BAR_CODE)));

        return itemcrossreference;
    }

}
