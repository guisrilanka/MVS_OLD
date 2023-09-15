package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nelin_000 on 07/25/2017.
 */

public class ItemDbHandler {

    private Context context;
    private DatabaseHandler dbHelper;
    SQLiteDatabase db;

    public ItemDbHandler(Context context) {
        this.context = context;
    }

    public ItemDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new item
    public void addItems(Item item) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_ITM_CATEGORY_CODE, item.getItemCategoryCode());
        values.put(dbHelper.KEY_ITM_DESCRIPTION, item.getDescription());
        values.put(dbHelper.KEY_ITM_CODE, item.getItemCode());
        values.put(dbHelper.KEY_ITM_BASE_UOM, item.getItemBaseUom());

        values.put(dbHelper.KEY_ITM_PRODUCT_GROUP_CODE, item.getProductGroupCode());
        values.put(dbHelper.KEY_ITM_QTY_ON_PURCH_ORDER, item.getQtyOnPurchOrder());
        values.put(dbHelper.KEY_ITM_QTY_ON_SALES_ORDER, item.getQtyOnSalesOrder());
        values.put(dbHelper.KEY_ITM_BLOCKED, item.getBlocked());
        values.put(dbHelper.KEY_ITM_UNIT_PRICE, item.getUnitPrice());
        values.put(dbHelper.KEY_ITM_NET_INVOICED_QTY, item.getNetInvoicedQty());
        values.put(dbHelper.KEY_ITM_IDENTIFIER_CODE, item.getIdentifierCode());
        values.put(dbHelper.KEY_ITM_VAT_PROD_GROUP, item.getVatProdPostingGroup());


        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_ITEM, null, values);
    }

    public boolean isItemExist(String code) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM + " WHERE " + dbHelper.KEY_ITM_CODE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{code});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public Item getItemByItemCode(String code) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM + " WHERE " + dbHelper.KEY_ITM_CODE + " = ?";
        Cursor c = db.rawQuery(query, new String[]{code});

        Item item = new Item();
//        c.moveToFirst();
        if (c != null && c.moveToFirst()) {

            item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
            item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
            item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
            item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
            item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
            item.setIdentifierCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_IDENTIFIER_CODE)));
            item.setVatProdPostingGroup(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_VAT_PROD_GROUP)));
        }
        c.close();
        return item;
    }

    // Getting All Items
    public List<Item> getAllItems() {

        List<Item> itemList = new ArrayList<Item>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM;
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
                item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
                item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
                item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
                item.setVatProdPostingGroup(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_VAT_PROD_GROUP)));

                itemList.add(item);
            } while (c.moveToNext());
        }

        c.close();
        return itemList;
    }

    // Getting item Count
    public int getItemCount() {
        db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }


    public boolean deleteItem(String code) {
        boolean success = false;

        if (isItemExist(code)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_ITEM, dbHelper.KEY_ITM_CODE + "=?", new String[]{String.valueOf(code)});

            success = !isItemExist(code);
        } else {
            success = true;
        }
        return success;
    }

    public List<Item> getSearchItems(String searchCategory, String searchItemCode, String searchDescription) {
        String searchCategory_ = "", searchItemCode_ = "", searchDescription_ = "", itemStatus = "";

        searchCategory_ = searchCategory.isEmpty() ? "" : searchCategory;
        searchItemCode_ = searchItemCode.isEmpty() ? "" : searchItemCode;
        searchDescription_ = searchDescription.isEmpty() ? "" : searchDescription;
        itemStatus = context.getResources().getString(R.string.sales_item_status_unblocked);

        List<Item> itemList = new ArrayList<Item>();
        db = dbHelper.getReadableDatabase();

        //String[] selectionArgs = new String[]{ searchItemCode_ };

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM + " WHERE "
                + dbHelper.KEY_ITM_BLOCKED + "= " + itemStatus + " AND "
                + dbHelper.KEY_ITM_CODE + " LIKE ? AND "
                + dbHelper.KEY_ITM_CATEGORY_CODE + " LIKE ? AND "
                + dbHelper.KEY_ITM_DESCRIPTION + " LIKE ?"
                + " ORDER BY " + dbHelper.KEY_ITM_CODE;
        Cursor c = db.rawQuery(selectQuery, new String[]{"%" + searchItemCode_ + "%", "%" + searchCategory_ + "%", "%" + searchDescription_ + "%"});

        if (c.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
                item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
                item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
                item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
                item.setProductGroupCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_PRODUCT_GROUP_CODE)));
                item.setVatProdPostingGroup(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_VAT_PROD_GROUP)));
                itemList.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return itemList;
    }

    public boolean isItemExistByIdentifierCode(String code) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM + " WHERE " + dbHelper.KEY_ITM_IDENTIFIER_CODE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{code});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public Item getItemByIdentifierCode(String code) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM + " WHERE " + dbHelper.KEY_ITM_IDENTIFIER_CODE + " = ?";
        Cursor c = db.rawQuery(query, new String[]{code});

        c.moveToFirst();

        Item item = new Item();
        item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
        item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
        item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
        item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
        item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
        item.setProductGroupCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_PRODUCT_GROUP_CODE)));

        c.close();
        return item;
    }

    public Item getItemByCode(String itemCode) {
        db = dbHelper.getReadableDatabase();
        Item item = new Item();
        String query = "SELECT * FROM " + dbHelper.TABLE_ITEM + " WHERE " + dbHelper.KEY_ITM_CODE + " = ?";
        Cursor c = db.rawQuery(query, new String[]{itemCode});

        c.moveToFirst();

        item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
        item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
        item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
        item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
        item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
        item.setProductGroupCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_PRODUCT_GROUP_CODE)));

        c.close();
        return item;
    }

    public String getItemIdentifierByCode(String itemCode) {
        db = dbHelper.getReadableDatabase();
        String query = "SELECT " + dbHelper.KEY_ITM_IDENTIFIER_CODE + " FROM " + dbHelper.TABLE_ITEM + " WHERE " + dbHelper.KEY_ITM_CODE + " = ?";
        Cursor c = db.rawQuery(query, new String[]{itemCode});

        c.moveToFirst();
        String itemIdentifier = c.getString(c.getColumnIndex(dbHelper.KEY_ITM_IDENTIFIER_CODE));

        c.close();
        return itemIdentifier;
    }


    public String getItemPriceByItemCode(String code, String itemUom) {

        db = dbHelper.getReadableDatabase();
        String itemUnitPrice = "";

        if (isItemExist(code)) {
            String query = "SELECT * FROM " + dbHelper.TABLE_ITEM + " WHERE "
                    + dbHelper.KEY_ITM_CODE + " = ? AND "
                    + dbHelper.KEY_ITM_BASE_UOM + " = ? ";
            Cursor c = db.rawQuery(query, new String[]{code, itemUom});

            if (c.getCount() > 0) {
                c.moveToFirst();
                itemUnitPrice = c.getString(c.getColumnIndex(dbHelper.KEY_ITM_UNIT_PRICE));
            }
            c.close();
        } else {
            return itemUnitPrice;
        }
        return itemUnitPrice;
    }

    public List<Item> getItemsByCustomerPriceGroup(String searchCustomerPriceGroup,
                                                   String categoryCode,
                                                   String itemCode,
                                                   String itemDescription) {

        String searchCustomerPriceGroup_, selectQuery, searchCategory_ = "", searchItemCode_ = "", searchDescription_ = "", itemStatus = "";
        Cursor c;
        List<Item> itemList = new ArrayList<Item>();

        searchCustomerPriceGroup_ = searchCustomerPriceGroup.isEmpty() ? "" : searchCustomerPriceGroup;
        searchCategory_ = categoryCode.isEmpty() ? "" : categoryCode;
        searchItemCode_ = itemCode.isEmpty() ? "" : itemCode;
        searchDescription_ = itemDescription.isEmpty() ? "" : itemDescription;
        itemStatus = context.getResources().getString(R.string.sales_item_status_unblocked);

        db = dbHelper.getReadableDatabase();

        selectQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM + " WHERE "
                + dbHelper.KEY_ITM_BLOCKED + "= " + itemStatus + " AND "
                + dbHelper.KEY_ITM_CODE + " LIKE ? AND "
                + dbHelper.KEY_ITM_CATEGORY_CODE + " LIKE ? AND "
                + dbHelper.KEY_ITM_DESCRIPTION + " LIKE ? AND "
                + dbHelper.KEY_ITM_CODE
                + " IN (SELECT DISTINCT "
                + dbHelper.KEY_SALES_PRICES_ITEM_NO + " FROM "
                + dbHelper.TABLE_SALES_PRICES + " WHERE "
                + dbHelper.KEY_SALES_PRICES_SALES_CODE
                + " = ?)"
                + " ORDER BY " + dbHelper.KEY_ITM_CODE;

        c = db.rawQuery(selectQuery, new String[]{
                "%" + searchItemCode_ + "%"
                , "%" + searchCategory_ + "%"
                , "%" + searchDescription_ + "%"
                , searchCustomerPriceGroup_});

        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
                    item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                    item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
                    item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
                    item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
                    item.setProductGroupCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_PRODUCT_GROUP_CODE)));
                    item.setIdentifierCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_IDENTIFIER_CODE)));

                    itemList.add(item);
                } while (c.moveToNext());
            }
        }
        c.close();
        return itemList;
    }


    public List<Item> getItemsBySalesPrice(String categoryCode,
                                           String itemCode,
                                           String itemDescription) {
        String selectQuery, searchCategory_ = "", searchItemCode_ = "", searchDescription_ = "", itemStatus = "";
        Cursor c;

        searchCategory_ = categoryCode.isEmpty() ? "" : categoryCode;
        searchItemCode_ = itemCode.isEmpty() ? "" : itemCode;
        searchDescription_ = itemDescription.isEmpty() ? "" : itemDescription;
        itemStatus = context.getResources().getString(R.string.sales_item_status_unblocked);

        List<Item> itemList = new ArrayList<Item>();
        db = dbHelper.getReadableDatabase();

        selectQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM + " WHERE "
                + dbHelper.KEY_ITM_BLOCKED + "= " + itemStatus + " AND "
                + dbHelper.KEY_ITM_CODE + " LIKE ? AND "
                + dbHelper.KEY_ITM_CATEGORY_CODE + " LIKE ? AND "
                + dbHelper.KEY_ITM_DESCRIPTION + " LIKE ? AND "
                + dbHelper.KEY_ITM_CODE
                + " IN (SELECT DISTINCT "
                + dbHelper.KEY_SALES_PRICES_ITEM_NO + " FROM "
                + dbHelper.TABLE_SALES_PRICES
                + " )"
                + " ORDER BY " + dbHelper.KEY_ITM_CODE;

        c = db.rawQuery(selectQuery, new String[]{
                "%" + searchItemCode_ + "%", "%" + searchCategory_ + "%", "%" + searchDescription_ + "%"});

        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
                    item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                    item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
                    item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
                    item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
                    item.setProductGroupCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_PRODUCT_GROUP_CODE)));
                    item.setIdentifierCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_IDENTIFIER_CODE)));

                    itemList.add(item);
                } while (c.moveToNext());
            }
        }
        c.close();
        return itemList;
    }


    public List<Item> getItemTemplateByCustomerPriceGroup(String searchCustomerPriceGroup,
                                                          String categoryCode,
                                                          String itemCode,
                                                          String itemDescription,
                                                          String customerCode) {
        String searchCustomerPriceGroup_,
                selectQuery,
                searchCategory_ = "",
                searchItemCode_ = "",
                searchDescription_ = "",
                itemStatus = "",
                cusCode_ = "";
        Cursor c;

        searchCustomerPriceGroup_ = searchCustomerPriceGroup.isEmpty() ? "" : searchCustomerPriceGroup;
        searchCategory_ = categoryCode.isEmpty() ? "" : categoryCode;
        searchItemCode_ = itemCode.isEmpty() ? "" : itemCode;
        searchDescription_ = itemDescription.isEmpty() ? "" : itemDescription;
        itemStatus = context.getResources().getString(R.string.sales_item_status_unblocked);
        cusCode_ = customerCode.isEmpty() ? "" : customerCode;


        List<Item> itemList = new ArrayList<Item>();
        db = dbHelper.getReadableDatabase();

        // change by sajith // 2017-10-03
        if (!searchCustomerPriceGroup_.equals("")) {

            selectQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM + " WHERE "
                    + dbHelper.KEY_ITM_BLOCKED + "= " + itemStatus + " AND "
                    + dbHelper.KEY_ITM_CODE + " LIKE ? AND "
                    + dbHelper.KEY_ITM_CATEGORY_CODE + " LIKE ? AND "
                    + dbHelper.KEY_ITM_DESCRIPTION + " LIKE ? AND "
                    + dbHelper.KEY_ITM_CODE
                    + " IN (SELECT DISTINCT "
                    + dbHelper.KEY_SALES_PRICES_ITEM_NO + " FROM "
                    + dbHelper.TABLE_SALES_PRICES + " WHERE "
                    + dbHelper.KEY_SALES_PRICES_SALES_CODE + "= ? " + " AND "
                    + dbHelper.KEY_SALES_PRICES_SALES_TYPE + "= ? " + " AND "
                    + dbHelper.KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE + " = ?)"
                    + " ORDER BY " + dbHelper.KEY_ITM_CODE;

            c = db.rawQuery(selectQuery, new String[]{
                    "%" + searchItemCode_ + "%", "%" + searchCategory_ + "%", "%" + searchDescription_ + "%", cusCode_, "1", "0"});
        } else {

            selectQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM + " WHERE "
                    + dbHelper.KEY_ITM_BLOCKED + "= " + itemStatus + " AND "
                    + dbHelper.KEY_ITM_CODE + " LIKE ? AND "
                    + dbHelper.KEY_ITM_CATEGORY_CODE + " LIKE ? AND "
                    + dbHelper.KEY_ITM_DESCRIPTION + " LIKE ? AND "
                    + dbHelper.KEY_ITM_CODE
                    + " IN (SELECT DISTINCT "
                    + dbHelper.KEY_SALES_PRICES_ITEM_NO + " FROM "
                    + dbHelper.TABLE_SALES_PRICES + " WHERE "
                    + dbHelper.KEY_SALES_PRICES_SALES_CODE + " AND "
                    + dbHelper.KEY_SALES_PRICES_SALES_TYPE
                    + " = ?)"
                    + " ORDER BY " + dbHelper.KEY_ITM_CODE;

            c = db.rawQuery(selectQuery, new String[]{
                    "%" + searchItemCode_ + "%", "%" + searchCategory_ + "%", "%" + searchDescription_ + "%", searchCustomerPriceGroup_});
        }


        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
                    item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                    item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
                    item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
                    item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
                    item.setProductGroupCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_PRODUCT_GROUP_CODE)));

                    itemList.add(item);
                } while (c.moveToNext());
            }
        }
        c.close();
        return itemList;
    }

    public Item getItemByIdentifierCodeAndTemplate(String code, String customerPriceGroup) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT  * FROM " + dbHelper.TABLE_ITEM + " WHERE "
                + dbHelper.KEY_ITM_BLOCKED + "= " + 0 + " AND "
                + dbHelper.KEY_ITM_IDENTIFIER_CODE + " = ? AND "
                + dbHelper.KEY_ITM_CODE
                + " IN (SELECT DISTINCT " + dbHelper.KEY_SALES_PRICES_ITEM_NO
                + " FROM " + dbHelper.TABLE_SALES_PRICES
                + " WHERE " + dbHelper.KEY_SALES_PRICES_SALES_CODE
                + " = ? AND " + dbHelper.KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE + " != " + 0 + " )"
                + " ORDER BY " + dbHelper.KEY_ITM_CODE;

        Cursor c = db.rawQuery(query, new String[]{code, customerPriceGroup});

        Item item = new Item();
        if (c.getCount() > 0) {

            if (c.moveToFirst()) {

                item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
                item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
                item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
                item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
                item.setProductGroupCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_PRODUCT_GROUP_CODE)));

            }
        }
        c.close();
        return item;
    }

    //nwely added
    public Item getItemByIdentifierCodeAndPriceGroup(String code, String customerPriceGroup) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT  * FROM " + dbHelper.TABLE_ITEM + " WHERE "
                + dbHelper.KEY_ITM_BLOCKED + "= " + 0 + " AND "
                + dbHelper.KEY_ITM_IDENTIFIER_CODE + " = ? AND "
                + dbHelper.KEY_ITM_CODE
                + " IN (SELECT DISTINCT " + dbHelper.KEY_SALES_PRICES_ITEM_NO
                + " FROM " + dbHelper.TABLE_SALES_PRICES
                + " WHERE " + dbHelper.KEY_SALES_PRICES_SALES_CODE
                + " = ? )"
                + " ORDER BY " + dbHelper.KEY_ITM_CODE;

        Cursor c = db.rawQuery(query, new String[]{code, customerPriceGroup});

        Item item = new Item();
        if (c.getCount() > 0) {

            if (c.moveToFirst()) {

                item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
                item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
                item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
                item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
                item.setProductGroupCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_PRODUCT_GROUP_CODE)));

            }
        }
        c.close();
        return item;
    }

    public List<Item> getItemsByCustomerTemplate(String searchCustomerPriceGroup,
                                                 String categoryCode,
                                                 String itemCode,
                                                 String itemDescription,
                                                 String customerCode) {
        String searchCustomerPriceGroup_,
                selectQuery,
                searchCategory_ = "",
                searchItemCode_ = "",
                searchDescription_ = "",
                itemStatus = "",
                cusCode_ = "";
        Cursor c;

        searchCustomerPriceGroup_ = searchCustomerPriceGroup.isEmpty() ? "" : searchCustomerPriceGroup;
        searchCategory_ = categoryCode.isEmpty() ? "" : categoryCode;
        searchItemCode_ = itemCode.isEmpty() ? "" : itemCode;
        searchDescription_ = itemDescription.isEmpty() ? "" : itemDescription;
        itemStatus = context.getResources().getString(R.string.sales_item_status_unblocked);

        List<Item> itemList = new ArrayList<Item>();
        db = dbHelper.getReadableDatabase();

        selectQuery = "SELECT  * FROM " + dbHelper.TABLE_ITEM + " WHERE "
                + dbHelper.KEY_ITM_BLOCKED + "= " + itemStatus + " AND "
                + dbHelper.KEY_ITM_CODE + " LIKE ? AND "
                + dbHelper.KEY_ITM_CATEGORY_CODE + " LIKE ? AND "
                + dbHelper.KEY_ITM_DESCRIPTION + " LIKE ? AND "
                + dbHelper.KEY_ITM_CODE
                + " IN (SELECT DISTINCT " + dbHelper.KEY_SALES_PRICES_ITEM_NO + " FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper.KEY_SALES_PRICES_SALES_CODE
                + " = ? AND " + dbHelper.KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE + " != " + 0 + " )"
                + " ORDER BY " + dbHelper.KEY_ITM_CODE;

        c = db.rawQuery(selectQuery, new String[]{
                "%" + searchItemCode_ + "%", "%" + searchCategory_ + "%", "%" + searchDescription_ + "%", searchCustomerPriceGroup_});

        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_ID))));
                    item.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                    item.setItemBaseUom(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_BASE_UOM)));
                    item.setItemCategoryCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CATEGORY_CODE)));
                    item.setItemCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_CODE)));
                    item.setProductGroupCode(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_PRODUCT_GROUP_CODE)));

                    itemList.add(item);
                } while (c.moveToNext());
            }
        }
        c.close();
        return itemList;
    }

    public int getVehicleQtyItemCount(String driverCode) {
        int pendingCount = 0;
        db = dbHelper.getReadableDatabase();

        String itemStatus = context.getResources().getString(R.string.sales_item_status_unblocked);
        String driverCode_ = driverCode == null ? "" : driverCode;

        String query = "SELECT * " +
                "FROM " + dbHelper.TABLE_ITEM + " i " +
                " LEFT JOIN " + dbHelper.TABLE_ITEM_BALANCE_PDA + " ibp " +
                "ON i." + dbHelper.KEY_ITM_CODE + " = ibp." + dbHelper.KEY_ITEM_BAL_PDA_ITEM_NO +
                " WHERE ibp." + dbHelper.KEY_ITEM_BAL_PDA_LOC_CODE + " = ? AND i." + dbHelper.KEY_ITM_BLOCKED + " = ? ";

        Cursor c = db.rawQuery(query, new String[]{driverCode_, itemStatus});

        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    int qty = 0, openQty = 0, vQty = 0;
                    qty = c.getInt(c.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_QTY));
                    openQty = c.getInt(c.getColumnIndex(dbHelper.KEY_ITEM_BAL_PDA_OPEN_QTY));
                    vQty = openQty - qty;

                    if (vQty > 0) {
                        pendingCount++;
                    }

                } while (c.moveToNext());
            }
        }
        c.close();
        return pendingCount;
    }

}
