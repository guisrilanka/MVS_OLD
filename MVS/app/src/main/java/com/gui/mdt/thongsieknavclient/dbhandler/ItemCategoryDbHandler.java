package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.datamodel.ItemCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhanuka on 02/08/2017.
 */

public class ItemCategoryDbHandler {

    private Context context;
    private DatabaseHandler dbHelper;
    SQLiteDatabase db;

    public ItemCategoryDbHandler(Context context) {
        this.context = context;
    }

    public ItemCategoryDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new item
    public void addItemCategory(ItemCategory itemCategory) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_ITEM_CATEGORY_CODE, itemCategory.getItemCategoryCode());
        values.put(dbHelper.KEY_ITEM_CATEGORY_DESCRIPTION, itemCategory.getDescription());

        // Inserting Row
        db=dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_ITEM_CATEGORY, null, values);
    }

    public boolean isItemExist(String code) {

        db=dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_CATEGORY + " WHERE "+ dbHelper.KEY_ITEM_CATEGORY_CODE+" = ?";
        Cursor cursor = db.rawQuery(query, new String[] {code});
        int count = cursor.getCount();
        cursor.close();
        return count>0?true:false;
    }

    // Getting All Items
    public List<ItemCategory> getAllCategories() {

        List<ItemCategory> itemCategoriesList = new ArrayList<ItemCategory>();
        db=dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM_CATEGORY;
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ItemCategory itemcategory = new ItemCategory();
                itemcategory.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CATEGORY_ID))));
                itemcategory.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CATEGORY_CODE)));
                itemcategory.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITEM_CATEGORY_DESCRIPTION)));

                itemCategoriesList.add(itemcategory);
            } while (c.moveToNext());
        }

        c.close();
        return itemCategoriesList;
    }

    // Getting item Count
    public int getItemCategoryCount() {
        db=dbHelper.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM_CATEGORY;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public boolean deleteItem(String code) {
        boolean success=false;
        if( isItemExist(code)){
            db=dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_ITEM_CATEGORY, dbHelper.KEY_ITEM_CATEGORY_CODE +
                    "=?", new String[]{String.valueOf(code)});

            success = !isItemExist(code);
        }
        else{
            success=true;
        }
        return success;
    }

    public String getCategoryByCategoryCode(String categoryCode) {
        String categoryDescription = "";
        db = dbHelper.getReadableDatabase();

        if (isItemExist(categoryCode)) {
            String query = "SELECT * FROM " + dbHelper.TABLE_ITEM_CATEGORY + " WHERE " + dbHelper.KEY_ITEM_CATEGORY_CODE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{categoryCode});

            cursor.moveToFirst();

            categoryDescription = cursor.getString(cursor.getColumnIndex(dbHelper.KEY_ITEM_CATEGORY_DESCRIPTION));

            cursor.close();
            return categoryDescription;
        }
        return categoryDescription;
    }
}
