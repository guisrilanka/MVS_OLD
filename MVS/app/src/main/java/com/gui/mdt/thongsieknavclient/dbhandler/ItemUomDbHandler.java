package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.datamodel.ItemUom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhanuka on 02/08/2017.
 */

public class ItemUomDbHandler {

    private Context context;
    private DatabaseHandler dbHelper;
    SQLiteDatabase db;

    public ItemUomDbHandler(Context context) {
        this.context = context;
    }

    public ItemUomDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    // Adding new item_uom
    public void addItemUom(ItemUom item_uom) {

        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_UOM_ITEM_CODE, item_uom.getItemCode());
        values.put(dbHelper.KEY_UOM_CODE, item_uom.getUomCode());
        values.put(dbHelper.KEY_UOM_KEY, item_uom.getKey());
        values.put(dbHelper.KEY_CONVERTION, item_uom.getConvertion());

        // Inserting Row
        db=dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_ITEM_UOM, null, values);
    }

    // Check item exist in db
    public boolean isItemExist(String code) {

        db=dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_UOM + " WHERE "+ dbHelper.KEY_UOM_KEY +" = ?";
        Cursor cursor = db.rawQuery(query, new String[] {code});
        int count = cursor.getCount();
        cursor.close();
        return count>0?true:false;
    }


    public List<ItemUom> getUomListbyItemCode(String code){

        List<ItemUom> itemUomList = new ArrayList<ItemUom>();
        db=dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_UOM + " WHERE "+ dbHelper.KEY_UOM_ITEM_CODE +" = ?";
        Cursor c = db.rawQuery(query, new String[] {code});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ItemUom item_uom = new ItemUom();
                item_uom.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_UOM_ID))));
                item_uom.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_UOM_ITEM_CODE)));
                item_uom.setUomCode(c.getString(c.getColumnIndex(dbHelper.KEY_UOM_CODE)));
                item_uom.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_UOM_KEY)));
                item_uom.setConvertion(Float.parseFloat(c.getString(c.getColumnIndex(dbHelper.KEY_CONVERTION))));

                itemUomList.add(item_uom);
            } while (c.moveToNext());
        }

        c.close();
        return itemUomList;
    }

    public boolean deleteItem(String code) {
        boolean success=false;
        if( isItemExist(code)){
            db=dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_ITEM_UOM, dbHelper.KEY_UOM_KEY +
                    "=?", new String[]{String.valueOf(code)});

            success = !isItemExist(code);
        }
        else{
            success=true;
        }
        return success;
    }

}
