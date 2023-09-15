package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesPrices;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bhanuka on 09/08/2017.
 */

public class SalesPricesDbHandler {

    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;

    public SalesPricesDbHandler(Context context) {
        this.context = context;
    }

    public SalesPricesDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new item
    public void addSalesPrices(SalesPrices salesPrices) {
        try {
            ContentValues values = new ContentValues();
            values.put(dbHelper.KEY_SALES_PRICES_KEY, salesPrices.getKey());
            //values.put(dbHelper.KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL, salesPrices.getSalesCodeFilterCtrl());
            //values.put(dbHelper.KEY_SALES_PRICES_ITEM_NO_FILTER_CTRL, salesPrices.getItemNoFilterCtrl());
            //values.put(dbHelper.KEY_SALES_PRICES_STARTING_DATE_FILTER, salesPrices.getStartingDateFilter());
            //values.put(dbHelper.KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL2, salesPrices.getSalesCodeFilterCtrl2());
            //values.put(dbHelper.KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL, salesPrices.getSalesCodeFilterCtrl());
            values.put(dbHelper.KEY_SALES_PRICES_SALES_TYPE, salesPrices.getSalesType());
            values.put(dbHelper.KEY_SALES_PRICES_SALES_CODE, salesPrices.getSalesCode());
            values.put(dbHelper.KEY_SALES_PRICES_CUSTOMER_NAME, salesPrices.getCustomerName());
            values.put(dbHelper.KEY_SALES_PRICES_ITEM_NO, salesPrices.getItemNo());
            values.put(dbHelper.KEY_SALES_PRICES_ITEM_DESCRIPTION, salesPrices.getItemDescription());
            values.put(dbHelper.KEY_SALES_PRICES_VARIANT_CODE, salesPrices.getVariantCode());
            values.put(dbHelper.KEY_SALES_PRICES_CURRENCY_CODE, salesPrices.getCurrencyCode());
            values.put(dbHelper.KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE, salesPrices.getUnitofMeasureCode());
            values.put(dbHelper.KEY_SALES_PRICES_MINIMUM_QUANTITY, salesPrices.getMinimumQuantity());
            values.put(dbHelper.KEY_SALES_PRICES_PUBLISHED_PRICE, salesPrices.getPublishedPrice());
            values.put(dbHelper.KEY_SALES_PRICES_COST, salesPrices.getCost());
            values.put(dbHelper.KEY_SALES_PRICES_COST_PLUS_PERCENT, salesPrices.getCostPlusPercent());
            values.put(dbHelper.KEY_SALES_PRICES_DISCOUNT_AMOUNT, salesPrices.getDiscountAmount());
            values.put(dbHelper.KEY_SALES_PRICES_UNIT_PRICE, salesPrices.getUnitPrice());
            values.put(dbHelper.KEY_SALES_PRICES_STARTING_DATE, salesPrices.getStartingDate());
            values.put(dbHelper.KEY_SALES_PRICES_ENDING_DATE, salesPrices.getEndingDate());
            values.put(dbHelper.KEY_SALES_PRICES_PRICE_INCLUDES_VAT, salesPrices.getPriceIncludesVAT());
            values.put(dbHelper.KEY_SALES_PRICES_ALLOW_LINE_DISC, salesPrices.getAllowLineDisc());
            values.put(dbHelper.KEY_SALES_PRICES_ALLOW_INVOICE_DISC, salesPrices.getAllowInvoiceDisc());
            values.put(dbHelper.KEY_SALES_PRICES_VAT_BUS_POSTING_GR_PRICE, salesPrices.getVATBusPostingGrPrice());
            values.put(dbHelper.KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE, salesPrices.getItemTemplateSequence());

            // Inserting Row
            db = dbHelper.getWritableDatabase();
            db.insert(dbHelper.TABLE_SALES_PRICES, null, values);
        }catch (Exception ex){
            Log.d("ERROR SALE.PRI ADD",ex.toString());
        }
    }

    public boolean isItemExist(SalesPrices sp) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE "
                + dbHelper.KEY_SALES_PRICES_ITEM_NO + " = ?"
                + " AND " + dbHelper.KEY_SALES_PRICES_SALES_CODE + " = ?"
                + " AND " + dbHelper.KEY_SALES_PRICES_SALES_TYPE + " = ?"
                + " AND " + dbHelper.KEY_SALES_PRICES_STARTING_DATE + " = ?"
                + " AND " + dbHelper.KEY_SALES_PRICES_CURRENCY_CODE + " = ?"
                + " AND " + dbHelper.KEY_SALES_PRICES_VARIANT_CODE + " = ?"
                + " AND " + dbHelper.KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE + " = ?"
                + " AND " + dbHelper.KEY_SALES_PRICES_MINIMUM_QUANTITY + " = ?";
        Cursor cursor = db.rawQuery(query,
                new String[]{
                        sp.getItemNo(),
                        sp.getSalesCode(),
                        sp.getSalesType().toString(),
                        sp.getStartingDate(),
                        sp.getCurrencyCode(),
                        sp.getVariantCode(),
                        sp.getUnitOfMeasureCode(),
                        sp.getMinimumQuantity(),
                });
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    // Getting All Items
    public List<SalesPrices> getAllSalesPrices() {

        List<SalesPrices> salesPricesList = new ArrayList<SalesPrices>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SALES_PRICES;
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesPrices salesprices = new SalesPrices();

                salesprices.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_KEY)));
               /* salesprices.setSalesCodeFilterCtrl(String.valueOf(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL)));
                salesprices.setItemNoFilterCtrl(c.getString(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_ITEM_NO_FILTER_CTRL)));
                salesprices.setStartingDateFilter(c.getString(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_STARTING_DATE_FILTER)));
                salesprices.setSalesCodeFilterCtrl2(c.getString(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL2)));*/
                salesprices.setSalesType(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_SALES_TYPE));
                salesprices.setSalesCode(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_SALES_CODE)));
                salesprices.setCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_CUSTOMER_NAME)));
                salesprices.setItemNo(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_ITEM_NO)));
                salesprices.setItemDescription(c.getString(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_ITEM_DESCRIPTION)));
                salesprices.setVariantCode(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_VARIANT_CODE)));
                salesprices.setCurrencyCode(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_CURRENCY_CODE)));
                salesprices.setUnitofMeasureCode(c.getString(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE)));
                salesprices.setMinimumQuantity(c.getString(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_MINIMUM_QUANTITY)));
                salesprices.setPublishedPrice(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_PUBLISHED_PRICE)));
                salesprices.setCost(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_COST)));
                salesprices.setCostPlusPercent(c.getString(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_COST_PLUS_PERCENT)));
                salesprices.setDiscountAmount(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_DISCOUNT_AMOUNT)));
                salesprices.setUnitPrice(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_UNIT_PRICE)));
                salesprices.setStartingDate(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_STARTING_DATE)));
                salesprices.setEndingDate(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_ENDING_DATE)));
                salesprices.setPriceIncludesVAT(c.getString(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_PRICE_INCLUDES_VAT)));
                salesprices.setAllowLineDisc(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_ALLOW_LINE_DISC)));
                salesprices.setAllowInvoiceDisc(c.getString(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_ALLOW_INVOICE_DISC)));
                salesprices.setVATBusPostingGrPrice(c.getString(c.getColumnIndex(dbHelper
                        .KEY_SALES_PRICES_VAT_BUS_POSTING_GR_PRICE)));

                salesPricesList.add(salesprices);
            } while (c.moveToNext());
        }

        c.close();
        return salesPricesList;
    }

    // Getting sales prices count
    public int getSalesPricesCount() {
        db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + dbHelper.TABLE_SALES_PRICES;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public boolean deleteItem(SalesPrices sp) {
        boolean success = false;

        try {
            db = dbHelper.getWritableDatabase();
            db.delete(
                    dbHelper.TABLE_SALES_PRICES,
                    dbHelper.KEY_SALES_PRICES_ITEM_NO + " = ?"
                            + " AND " + dbHelper.KEY_SALES_PRICES_SALES_CODE + " = ?"
                            + " AND " + dbHelper.KEY_SALES_PRICES_SALES_TYPE + " = ?"
                            + " AND " + dbHelper.KEY_SALES_PRICES_STARTING_DATE + " = ?"
                            + " AND " + dbHelper.KEY_SALES_PRICES_CURRENCY_CODE + " = ?"
                            + " AND " + dbHelper.KEY_SALES_PRICES_VARIANT_CODE + " = ?"
                            + " AND " + dbHelper.KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE + " = ?"
                            + " AND " + dbHelper.KEY_SALES_PRICES_MINIMUM_QUANTITY + " = ?"
                    , new String[]{
                            sp.getItemNo(),
                            sp.getSalesCode(),
                            sp.getSalesType().toString(),
                            sp.getStartingDate(),
                            sp.getCurrencyCode(),
                            sp.getVariantCode(),
                            sp.getUnitOfMeasureCode(),
                            sp.getMinimumQuantity(),
                    });

            success = true;
        } catch (Exception ex) {
            success = false;
        }

        return success;
    }

    public String getSalesPricesBySalesPricesKey(SalesPrices sp) {
        db = dbHelper.getReadableDatabase();

        if (isItemExist(sp)) {
            String query = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper.KEY_SALES_PRICES_KEY
                    + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{sp.getKey()});

            cursor.moveToFirst();

            cursor.close();
        }
        return sp.getKey();
    }

    public List<SalesPrices> getCustomerPriceListByCustomer(Customer customer) {

        List<SalesPrices> salesPricesList = new ArrayList<SalesPrices>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  " + dbHelper.TABLE_SALES_PRICES + ".*," + dbHelper.TABLE_ITEM + "."
                + dbHelper.KEY_ITM_DESCRIPTION + " FROM " +
                dbHelper.TABLE_SALES_PRICES +
                " INNER JOIN " + dbHelper.TABLE_ITEM + " ON "
                + dbHelper.TABLE_SALES_PRICES + "." + dbHelper.KEY_SALES_PRICES_ITEM_NO + " = " + dbHelper.TABLE_ITEM
                + "."
                + dbHelper.KEY_ITM_CODE +
                " WHERE (" + dbHelper.KEY_SALES_PRICES_SALES_CODE + " = ? OR "
                + dbHelper.KEY_SALES_PRICES_SALES_CODE + " = ?) AND "
                + " date(" + dbHelper.KEY_SALES_PRICES_ENDING_DATE + ") >= date('" + DateTime.now().toString("YYYY-MM-dd") + "')"
                +" ORDER BY " + dbHelper.KEY_SALES_PRICES_ITEM_NO;
        Cursor c = db.rawQuery(selectQuery, new String[]{customer.getCode(), customer.getCustomerPriceGroup()});

        // looping through all rows and adding to list
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    SalesPrices salesprices = new SalesPrices();
                    salesprices = getSalesPricesFromCursor(salesprices, c);
                    salesprices.setItemDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));
                    salesprices.setSalesType(c.getInt(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_SALES_TYPE)));

                    salesPricesList.add(salesprices);
                } while (c.moveToNext());
            }
        }

        c.close();
        return salesPricesList;

    }

    public List<SalesPrices> getAllCustomerPriceList(String cutomerName, String itemCode) {

        List<SalesPrices> salesPricesList = new ArrayList<SalesPrices>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper
                .KEY_SALES_PRICES_CUSTOMER_NAME + " = ? AND "
                + dbHelper.KEY_SALES_PRICES_ITEM_NO + " LIKE ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{cutomerName, "%" + itemCode + "%"});

        // looping through all rows and adding to list
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    SalesPrices salesprices = new SalesPrices();
                    salesprices = getSalesPricesFromCursor(salesprices, c);

                    salesPricesList.add(salesprices);
                } while (c.moveToNext());
            }
        }

        c.close();
        return salesPricesList;

    }

    //added by buddhika
    public List<SalesPrices> getAllPriceList(String customerCode, String itemCode) {

        List<SalesPrices> salesPricesList = new ArrayList<SalesPrices>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper
                .KEY_SALES_PRICES_SALES_CODE + " = ? AND "
                + dbHelper.KEY_SALES_PRICES_ITEM_NO + " = ?"
                + " GROUP BY " + dbHelper.KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE;
        Cursor c = db.rawQuery(selectQuery, new String[]{customerCode, itemCode});

        // looping through all rows and adding to list
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    SalesPrices salesprices = new SalesPrices();
                    salesprices = getSalesPricesFromCursor(salesprices, c);
                    salesPricesList.add(salesprices);
                } while (c.moveToNext());
            }
        }

        c.close();
        return salesPricesList;

    }

    public String getGroupItemPriceByCustomePriceGroup(String customerPriceGroup, int salesType, String itemCode,
                                                       String deliveryDate, String itemUom) {
        db = dbHelper.getReadableDatabase();
        ArrayList<Float> itemPriceList = new ArrayList<Float>();
        String itemUnitPrice = "0";

        /*String query = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper
        .KEY_SALES_PRICES_SALES_CODE + " = ? AND " +
                dbHelper.KEY_SALES_PRICES_SALES_TYPE + " = " + salesType+" AND "
                + dbHelper.KEY_SALES_PRICES_ITEM_NO + " = ? AND "
                + dbHelper.KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE+" = ?"
                +" AND DATE('" + deliveryDate + "') BETWEEN " + dbHelper.KEY_SALES_PRICES_STARTING_DATE + " AND "
                + dbHelper.KEY_SALES_PRICES_ENDING_DATE;*/

        String query = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper
                .KEY_SALES_PRICES_SALES_CODE + " = ? AND " +
                dbHelper.KEY_SALES_PRICES_SALES_TYPE + " = " + salesType + " AND "
                + dbHelper.KEY_SALES_PRICES_ITEM_NO + " = ? AND "
                + dbHelper.KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE + " = ? AND "
                + " date(" + dbHelper.KEY_SALES_PRICES_STARTING_DATE + ") <= date('" + deliveryDate + "') AND "
                + " date(" + dbHelper.KEY_SALES_PRICES_ENDING_DATE + ") >= date('" + deliveryDate + "')";

        Cursor c = db.rawQuery(query, new String[]{customerPriceGroup, itemCode, itemUom});

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String price = c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_PUBLISHED_PRICE));
                    itemPriceList.add(Float.valueOf(price));
                } while (c.moveToNext());
            }
        }
        c.close();

        if (itemPriceList.isEmpty()) {
            return "0";
        }
        if (itemPriceList.size() == 1) {
            return String.valueOf(itemPriceList.get(0));
        }
        if (itemPriceList.size() > 1) {
            Collections.sort(itemPriceList);
            return String.valueOf(itemPriceList.get(0));
        }
        return itemUnitPrice;
    }

    public String getCustomerItemUnitPriceByCustomerCode(String customerCode, int salesType, String itemCode, String
            deliveryDate, String itemUom) {
        ArrayList<Float> itemPriceList = new ArrayList<Float>();
        db = dbHelper.getReadableDatabase();
        String itemUnitPrice = "";

        String query = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper
                .KEY_SALES_PRICES_SALES_CODE + " = ? AND " +
                dbHelper.KEY_SALES_PRICES_SALES_TYPE + " = " + salesType + " AND " +
                dbHelper.KEY_SALES_PRICES_ITEM_NO
                + " = ? AND "
                + dbHelper.KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE + " = ? AND "
                + " date(" + dbHelper.KEY_SALES_PRICES_STARTING_DATE + ") <= date('" + deliveryDate + "') AND "
                + "date(" + dbHelper.KEY_SALES_PRICES_ENDING_DATE + ") >= date('" + deliveryDate + "')";

        Cursor c = db.rawQuery(query, new String[]{customerCode, itemCode, itemUom});
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String price = c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_PUBLISHED_PRICE));
                    itemPriceList.add(Float.valueOf(price));
                } while (c.moveToNext());
            }
        }
        c.close();

        if (itemPriceList.isEmpty()) {
            return "0";
        }
        if (itemPriceList.size() == 1) {
            return String.valueOf(itemPriceList.get(0));
        }
        if (itemPriceList.size() > 1) {
            Collections.sort(itemPriceList);
            return String.valueOf(itemPriceList.get(0));
        }
        return itemUnitPrice;
    }

    private SalesPrices getSalesPricesFromCursor(SalesPrices salesPrices, Cursor c) {
        salesPrices.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_KEY)));
       /* salesPrices.setSalesCodeFilterCtrl(String.valueOf(c.getColumnIndex(dbHelper
                .KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL)));
        salesPrices.setItemNoFilterCtrl(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_ITEM_NO_FILTER_CTRL)));
        salesPrices.setStartingDateFilter(c.getString(c.getColumnIndex(dbHelper
                .KEY_SALES_PRICES_STARTING_DATE_FILTER)));
        salesPrices.setSalesCodeFilterCtrl2(c.getString(c.getColumnIndex(dbHelper
                .KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL2)));*/
        salesPrices.setSalesType(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_SALES_TYPE));
        salesPrices.setSalesCode(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_SALES_CODE)));
        salesPrices.setCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_CUSTOMER_NAME)));
        salesPrices.setItemNo(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_ITEM_NO)));
        salesPrices.setItemDescription(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_ITEM_DESCRIPTION)));
        salesPrices.setVariantCode(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_VARIANT_CODE)));
        salesPrices.setCurrencyCode(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_CURRENCY_CODE)));
        salesPrices.setUnitofMeasureCode(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE)));
        salesPrices.setMinimumQuantity(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_MINIMUM_QUANTITY)));
        salesPrices.setPublishedPrice(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_PUBLISHED_PRICE)));
        salesPrices.setCost(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_COST)));
        salesPrices.setCostPlusPercent(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_COST_PLUS_PERCENT)));
        salesPrices.setDiscountAmount(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_DISCOUNT_AMOUNT)));
        salesPrices.setUnitPrice(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_UNIT_PRICE)));
        salesPrices.setStartingDate(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_STARTING_DATE)));
        salesPrices.setEndingDate(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_ENDING_DATE)));
        salesPrices.setPriceIncludesVAT(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_PRICE_INCLUDES_VAT)));
        salesPrices.setAllowLineDisc(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_ALLOW_LINE_DISC)));
        salesPrices.setAllowInvoiceDisc(c.getString(c.getColumnIndex(dbHelper.KEY_SALES_PRICES_ALLOW_INVOICE_DISC)));
        salesPrices.setVATBusPostingGrPrice(c.getString(c.getColumnIndex(dbHelper
                .KEY_SALES_PRICES_VAT_BUS_POSTING_GR_PRICE)));
        salesPrices.setItemTemplateSequence(c.getInt(c.getColumnIndex(dbHelper
                .KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE)));

        return salesPrices;
    }

    public List<SalesPrices> getAllCustomerPriceListBySalesType(int salesType, String priceGroup) {

        String priceGroup_ = priceGroup == null ? "" : priceGroup;
        List<SalesPrices> salesPricesList = new ArrayList<SalesPrices>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  " + dbHelper.TABLE_SALES_PRICES + ".*," + dbHelper.TABLE_ITEM + "."
                + dbHelper.KEY_ITM_DESCRIPTION + " FROM " +
                dbHelper.TABLE_SALES_PRICES +
                " INNER JOIN " + dbHelper.TABLE_ITEM + " ON "
                + dbHelper.TABLE_SALES_PRICES + "." + dbHelper.KEY_SALES_PRICES_ITEM_NO + " = " + dbHelper.TABLE_ITEM
                + "."
                + dbHelper.KEY_ITM_CODE +
                " WHERE " + dbHelper.KEY_SALES_PRICES_SALES_TYPE + " =" + salesType
                + " AND " + dbHelper.KEY_SALES_PRICES_SALES_CODE + " = ?"
                + " AND " + "date(" + dbHelper.KEY_SALES_PRICES_ENDING_DATE + ") >= date('" + DateTime.now().toString("YYYY-MM-dd") + "')"
                + " ORDER BY " + dbHelper.KEY_SALES_PRICES_ITEM_NO;

        Cursor c = db.rawQuery(selectQuery, new String[]{priceGroup_});
        // looping through all rows and adding to list
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    SalesPrices salesprices = new SalesPrices();
                    salesprices = getSalesPricesFromCursor(salesprices, c);
                    salesprices.setItemDescription(c.getString(c.getColumnIndex(dbHelper.KEY_ITM_DESCRIPTION)));

                    salesPricesList.add(salesprices);
                } while (c.moveToNext());
            }
        }
        c.close();
        return salesPricesList;
    }

    public boolean isSalesExistByPriceGroupAndItemCode(String priceGroup, String itemCode) {
        db = dbHelper.getReadableDatabase();
        String priceGroup_ = "", itemCode_ = "";
        priceGroup_ = priceGroup == null ? "" : priceGroup;
        itemCode_ = itemCode == null ? "" : itemCode;

        String query = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper.KEY_SALES_PRICES_SALES_CODE
                + " = ?"
                + " AND " + dbHelper.KEY_SALES_PRICES_ITEM_NO + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{priceGroup_, itemCode_});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public boolean deleteAllSalesPrices() {
        db = dbHelper.getReadableDatabase();
        int affectedRows = db.delete(dbHelper.TABLE_SALES_PRICES, null, null);
        return affectedRows >= 0;
    }

    public List<SalesPrices> getAllPriceListByItemTemplateSeq(String customerPriceGroup) {

        List<SalesPrices> salesPricesList = new ArrayList<SalesPrices>();
        db = dbHelper.getReadableDatabase();

        /*String selectQuery = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper
                .KEY_SALES_PRICES_SALES_CODE + " = ? AND "
                + dbHelper.KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE + " != " + 0
                + " ORDER BY " + dbHelper.KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE;*/

        String selectQuery = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper
                .KEY_SALES_PRICES_SALES_CODE + " = ? AND "
                + dbHelper.KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE + " != " + 0
                +" AND "+ dbHelper.KEY_SALES_PRICES_ITEM_NO
                +" IN ( SELECT "+dbHelper.KEY_ITM_CODE
                        +" FROM "+dbHelper.TABLE_ITEM
                        +" WHERE "+ dbHelper.KEY_ITM_BLOCKED +" = "+0+")"
                +" ORDER BY " + dbHelper.KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE;

        Cursor c = db.rawQuery(selectQuery, new String[]{customerPriceGroup});

        // looping through all rows and adding to list
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    SalesPrices salesprices = new SalesPrices();
                    salesprices = getSalesPricesFromCursor(salesprices, c);
                    salesPricesList.add(salesprices);
                } while (c.moveToNext());
            }
        }
        c.close();
        return salesPricesList;
    }

    public List<SalesPrices> getGroupPriceList(
            String customerPriceGroup
            , int salesType
            , String itemCode
            , String deliveryDate
            , String itemUom){

        db = dbHelper.getReadableDatabase();
        List<SalesPrices> salesPricesList = new ArrayList<SalesPrices>();

        String query = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper
                .KEY_SALES_PRICES_SALES_CODE + " = ? AND " +
                dbHelper.KEY_SALES_PRICES_SALES_TYPE + " = " + salesType + " AND "
                + dbHelper.KEY_SALES_PRICES_ITEM_NO + " = ? AND "
                + dbHelper.KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE + " = ? AND "
                + " date(" + dbHelper.KEY_SALES_PRICES_STARTING_DATE + ") <= date('" + deliveryDate + "') AND "
                + " date(" + dbHelper.KEY_SALES_PRICES_ENDING_DATE + ") >= date('" + deliveryDate + "')";

        Cursor c = db.rawQuery(query, new String[]{customerPriceGroup, itemCode, itemUom});

        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    SalesPrices salesprices = new SalesPrices();
                    salesprices = getSalesPricesFromCursor(salesprices, c);

                    salesPricesList.add(salesprices);
                } while (c.moveToNext());
            }
        }
        c.close();

        return salesPricesList;
    }

    public List<SalesPrices> getCustomerPriceList(
            String customerCode
            , int salesType
            , String itemCode
            , String deliveryDate
            , String itemUom){

        List<SalesPrices> salesPricesList = new ArrayList<SalesPrices>();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SALES_PRICES + " WHERE " + dbHelper
                .KEY_SALES_PRICES_SALES_CODE + " = ? AND " +
                dbHelper.KEY_SALES_PRICES_SALES_TYPE + " = " + salesType + " AND " +
                dbHelper.KEY_SALES_PRICES_ITEM_NO
                + " = ? AND "
                + dbHelper.KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE + " = ? AND "
                + " date(" + dbHelper.KEY_SALES_PRICES_STARTING_DATE + ") <= date('" + deliveryDate + "') AND "
                + "date(" + dbHelper.KEY_SALES_PRICES_ENDING_DATE + ") >= date('" + deliveryDate + "')";

        Cursor c = db.rawQuery(query, new String[]{customerCode, itemCode, itemUom});
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    SalesPrices salesprices = new SalesPrices();
                    salesprices = getSalesPricesFromCursor(salesprices, c);

                    salesPricesList.add(salesprices);
                } while (c.moveToNext());
            }
        }
        c.close();

        return salesPricesList;
    }

    //Get unit price start--------------------------------------------------------------------------
    public float getUnitPriceWithQuantity(
            String itemCode
            , String customerPriceGroup
            , String customerCode
            , String itemUom
            , int quantity
            , String deliveryDate) {

        float itemMasterUnitPrice = 0, groupItemPrice = 0, customerItemPrice = 0;

        List<SalesPrices> customerGroupPriceList = new ArrayList<SalesPrices>(),
                customerPriceList = new ArrayList<SalesPrices>();

        //1 ItemMaster UnitPrice
        itemMasterUnitPrice = Float.parseFloat(getItemUnitPriceFromItemMaster(itemCode
                , itemUom) == "" ? "0" : getItemUnitPriceFromItemMaster(itemCode, itemUom));


        customerGroupPriceList = getGroupItemPriceList(customerPriceGroup
                , 1
                , itemCode
                , deliveryDate
                , itemUom);

        customerPriceList = getCustomerItemPriceList(customerCode
                , 0
                , itemCode
                , deliveryDate
                , itemUom);

        //2 Customer group unit price
        if (!customerGroupPriceList.isEmpty()) {
            List<Float> priceList = new ArrayList<Float>();

            for (int i = 0; i < customerGroupPriceList.size(); i++) {
                SalesPrices sp = customerGroupPriceList.get(i);
                int minimumQty = Math.round(Float.parseFloat(sp.getMinimumQuantity()));

                if (minimumQty <= quantity) {
                    priceList.add(Float.parseFloat(sp.getPublishedPrice()));
                }
            }

            if (priceList.size() == 0) {
                groupItemPrice = 0;
            } else if (priceList.size() == 1) {
                groupItemPrice = priceList.get(0);
            } else {
                Collections.sort(priceList);
                groupItemPrice = priceList.get(0);
            }
        } else {
            groupItemPrice = 0;
        }

        //3 Customer unit price
        if (!customerPriceList.isEmpty()) {
            List<Float> priceList = new ArrayList<Float>();

            for (int i = 0; i < customerPriceList.size(); i++) {
                SalesPrices sp = customerPriceList.get(i);
                int minimumQty = Math.round(Float.parseFloat(sp.getMinimumQuantity()));

                if (minimumQty <= quantity) {
                    priceList.add(Float.parseFloat(sp.getPublishedPrice()));
                }
            }

            if (priceList.size() == 0) {
                customerItemPrice = 0;
            } else if (priceList.size() == 1) {
                customerItemPrice = priceList.get(0);
            } else {
                Collections.sort(priceList);
                customerItemPrice = priceList.get(0);
            }
        } else {
            customerItemPrice = 0;
        }


        float minimunItemPrice = getMinItemPrice(itemMasterUnitPrice
                , groupItemPrice
                , customerItemPrice);

        return minimunItemPrice;
    }

    public List<SalesPrices> getCustomerItemPriceList(String customerCode
            , int salesType
            , String itemCode
            , String deliveryDate
            , String itemUom) {
        SalesPricesDbHandler db = new SalesPricesDbHandler(context);
        db.open();

        List<SalesPrices> list = db.getCustomerPriceList(customerCode
                , salesType
                , itemCode
                , deliveryDate
                , itemUom);

        db.close();

        return list;
    }

    public List<SalesPrices> getGroupItemPriceList(String customerGroup
            , int salesType
            , String itemCode
            , String deliveryDate
            , String itemUom) {

        SalesPricesDbHandler db = new SalesPricesDbHandler(context);
        db.open();

        List<SalesPrices> list = db.getGroupPriceList(customerGroup
                , salesType
                , itemCode
                , deliveryDate
                , itemUom);

        db.close();

        return list;
    }

    public String getItemUnitPriceFromItemMaster(String itemCode, String itemUom) {
        ItemDbHandler db = new ItemDbHandler(context);
        db.open();

        String unitprice = db.getItemPriceByItemCode(itemCode, itemUom);
        db.close();

        return unitprice;
    }

    public float getMinItemPrice(float a_, float b_, float c_) {
        float a = a_, b = b_, c = c_;
        boolean isAZero = false, isBZero = false, isCZero = false;

        if (a == 0) {
            isAZero = true;
        }

        if (b == 0) {
            isBZero = true;
        }

        if (c == 0) {
            isCZero = true;
        }

        if (isAZero && isBZero && isCZero) {
            return 0;
        } else {
            if (isAZero && isBZero && !isCZero) {
                return c;
            }
            if (isAZero && !isBZero && isCZero) {
                return b;
            }
            if (!isAZero && isBZero && isCZero) {
                return a;
            }

            if (isAZero && !isBZero && !isCZero) {
                return Math.min(b, c);
            }
            if (!isAZero && !isBZero && isCZero) {
                return Math.min(a, b);
            }
            if (!isAZero && isBZero && !isCZero) {
                return Math.min(a, c);
            }
            if (!isAZero && !isBZero && !isCZero) {
                return Math.min(a, Math.min(b, c));
            }
        }
        return 0;
    }
    //Get unit price end----------------------------------------------------------------------------
}
