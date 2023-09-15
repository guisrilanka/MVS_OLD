package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.datamodel.StockRequestLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10/24/2017.
 */

public class StockRequestLineDbHandler {
    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;

    public StockRequestLineDbHandler(Context context) {
        this.context = context;
    }

    public StockRequestLineDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new Sr Line's
    public void addStockRequestLine(StockRequestLine srl) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_SR_LINE_KEY, srl.getKey());
        values.put(dbHelper.KEY_SR_LINE_SELL_TO_CUSTOMER_NO, srl.getSelltoCustomerNo());
        values.put(dbHelper.KEY_SR_LINE_SHIP_TO_CODE, srl.getShiptoCode());
        values.put(dbHelper.KEY_SR_LINE_SALES_PERSON_CODE, srl.getSalespersonCode());
        values.put(dbHelper.KEY_SR_LINE_ORDER_DATE, srl.getOrderDate());
        values.put(dbHelper.KEY_SR_LINE_SHIPMENT_DATE, srl.getShipmentDate());
        values.put(dbHelper.KEY_SR_LINE_DUE_DATE, srl.getDueDate());
        values.put(dbHelper.KEY_SR_LINE_GRS_SALES_HEADER_EXTERNAL_DOCUMENT_NO, srl.getGrsSalesHeaderExternalDocumentNo());
        values.put(dbHelper.KEY_SR_LINE_CREATED_BY, srl.getCreatedBy());
        values.put(dbHelper.KEY_SR_LINE_CREATED_DATETIME, srl.getCreatedDataTime());
        values.put(dbHelper.KEY_SR_LINE_LAST_MODIFIED_BY, srl.getLastModifiedBy());
        values.put(dbHelper.KEY_SR_LINE_LAST_MODIFIED_DATETIME, srl.getLastModifiedDateTime());
        values.put(dbHelper.KEY_SR_LINE_DOCUMENT_NO, srl.getDocumentNo());
        values.put(dbHelper.KEY_SR_LINE_LINE_NO, srl.getLineNo());
        values.put(dbHelper.KEY_SR_LINE_TYPE, srl.getType());
        values.put(dbHelper.KEY_SR_LINE_ITEM_NO, srl.getItemNo());
        values.put(dbHelper.KEY_SR_LINE_DESCRIPTION, srl.getDescription());
        values.put(dbHelper.KEY_SR_LINE_LOCATION_CODE, srl.getLocationCode());
        values.put(dbHelper.KEY_SR_LINE_QUANTITY, String.format("%.2f", srl.getQuantity()));
        values.put(dbHelper.KEY_SR_LINE_UNIT_OF_MEASURE_CODE, srl.getUnitofMeasureCode());
        values.put(dbHelper.KEY_SR_LINE_UNIT_PRICE, String.format("%.2f", srl.getUnitPrice()));
        values.put(dbHelper.KEY_SR_LINE_AMOUNT, String.format("%.2f", srl.getAmount()));
        values.put(dbHelper.KEY_SR_LINE_LINE_AMOUNT, String.format("%.2f", srl.getLineAmount()));
        values.put(dbHelper.KEY_SR_LINE_LINE_DISCOUNT_AMOUNT, String.format("%.2f", srl.getLineDiscountAmount()));
        values.put(dbHelper.KEY_SR_LINE_LINE_DISCOUNT_PERCENT, srl.getLineDiscountPercent());
        values.put(dbHelper.KEY_SR_LINE_DRIVER_CODE, srl.getDriverCode());

        values.put(dbHelper.KEY_SR_LINE_TOTAL_VAT_AMOUNT, String.format("%.2f", srl.getTotalVATAmount()));
        values.put(dbHelper.KEY_SR_LINE_TOTAL_AMOUNT_INCL_VAT, String.format("%.2f", srl.getTotalAmountInclVAT()));
        values.put(dbHelper.KEY_SR_LINE_TAX_PERCENTAGE, srl.getTaxPercentage());

        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_SR_LINE, null, values);
    }

    public boolean isSrLineExist(String srDucumentNo, String lineNo) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SR_LINE + " WHERE "
                + dbHelper.KEY_SR_LINE_DOCUMENT_NO + " = ?"
                + " AND " + dbHelper.KEY_SR_LINE_LINE_NO + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{srDucumentNo, lineNo});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public boolean isSrLineExistByKey(String key) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SR_LINE + " WHERE "
                + dbHelper.KEY_SR_LINE_KEY + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{key});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public List<StockRequestLine> getAllStockRequestLines() {

        List<StockRequestLine> stockRequestLines = new ArrayList<StockRequestLine>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SR_LINE;
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                StockRequestLine srl = new StockRequestLine();

                srl.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_KEY)));
                srl.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SELL_TO_CUSTOMER_NO)));
                srl.setShiptoCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SHIP_TO_CODE)));
                srl.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SALES_PERSON_CODE)));
                srl.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_ORDER_DATE)));
                srl.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SHIPMENT_DATE)));
                srl.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_DUE_DATE)));
                srl.setGrsSalesHeaderExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_GRS_SALES_HEADER_EXTERNAL_DOCUMENT_NO)));
                srl.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_CREATED_BY)));
                srl.setCreatedDataTime(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_CREATED_DATETIME)));
                srl.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_LAST_MODIFIED_BY)));
                srl.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_LAST_MODIFIED_DATETIME)));
                srl.setDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_DOCUMENT_NO)));
                srl.setLineNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_LINE_NO)));
                srl.setType(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_TYPE)));
                srl.setItemNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_ITEM_NO)));
                srl.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_DESCRIPTION)));
                srl.setLocationCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_LOCATION_CODE)));
                srl.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SELL_TO_CUSTOMER_NO)));
                srl.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SELL_TO_CUSTOMER_NO)));
                srl.setQuantity(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_QUANTITY)));
                srl.setUnitofMeasureCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_UNIT_OF_MEASURE_CODE)));
                srl.setUnitPrice(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_UNIT_PRICE)));
                srl.setAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_AMOUNT)));
                srl.setLineAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_LINE_AMOUNT)));
                srl.setLineDiscountAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_LINE_DISCOUNT_AMOUNT)));
                srl.setLineDiscountPercent(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_LINE_DISCOUNT_PERCENT)));
                srl.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_DRIVER_CODE)));

                stockRequestLines.add(srl);
            } while (c.moveToNext());
        }
        c.close();
        return stockRequestLines;
    }

    public boolean deleteStockRequestLine(String documentNo, String lineNo) {
        boolean success = false;

        if (isSrLineExist(documentNo, lineNo)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_SR_LINE, dbHelper.KEY_SR_LINE_DOCUMENT_NO + " = ? AND "
                            + dbHelper.KEY_SR_LINE_LINE_NO + " = ?"
                    , new String[]{documentNo, lineNo});
            success = !isSrLineExist(documentNo, lineNo);
        } else {
            success = true;
        }
        return success;
    }

    public boolean deleteStockRequestLineByKey(String key) {
        boolean success = false;

        if (isSrLineExistByKey(key)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_SR_LINE, dbHelper.KEY_SR_LINE_KEY + " = ?"
                    , new String[]{key});
            success = !isSrLineExistByKey(key);
        } else {
            success = true;
        }
        return success;
    }


    public void deleteStockRequestLinesByDate(String lastModifyDate) {

        db = dbHelper.getWritableDatabase();
        int affectedRow = db.delete(dbHelper.TABLE_SR_LINE, dbHelper.KEY_SR_LINE_ORDER_DATE + "< ?", new String[]{lastModifyDate});

    }


    public boolean deleteStockRequestLinesByDocumentNo(String documentNo) {
        boolean success=false;

        try{
            db=dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_SR_LINE, dbHelper.KEY_SR_LINE_DOCUMENT_NO+ "=?", new String[]{documentNo});
            success=true;
        }
        catch (Exception ex){
            success=false;
        }
           /* db=dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_SO_LINE, dbHelper.KEY_SO_LINE_DOCUMENT_NO+ "=?", new String[]{String.valueOf(documentNo)});
*/
        return success;
    }

    public List<StockRequestLine> getAllSRLinesByDocumentNo(String documentNo)
    {
        List<StockRequestLine> stockRequestLines = new ArrayList<StockRequestLine>();
        db=dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SR_LINE
                + " WHERE "+ dbHelper.KEY_SR_LINE_DOCUMENT_NO+" = ?";
        Cursor c = db.rawQuery(selectQuery,  new String[] {documentNo});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                StockRequestLine srl = new StockRequestLine();

                srl.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_KEY)));
                srl.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SELL_TO_CUSTOMER_NO)));
                srl.setShiptoCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SHIP_TO_CODE)));
                srl.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SALES_PERSON_CODE)));
                srl.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_ORDER_DATE)));
                srl.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SHIPMENT_DATE)));
                srl.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_DUE_DATE)));
                srl.setGrsSalesHeaderExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_GRS_SALES_HEADER_EXTERNAL_DOCUMENT_NO)));
                srl.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_CREATED_BY)));
                srl.setCreatedDataTime(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_CREATED_DATETIME)));
                srl.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_LAST_MODIFIED_BY)));
                srl.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_LAST_MODIFIED_DATETIME)));
                srl.setDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_DOCUMENT_NO)));
                srl.setLineNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_LINE_NO)));
                srl.setType(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_TYPE)));
                srl.setItemNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_ITEM_NO)));
                srl.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_DESCRIPTION)));
                srl.setLocationCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_LOCATION_CODE)));
                srl.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SELL_TO_CUSTOMER_NO)));
                srl.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_SELL_TO_CUSTOMER_NO)));
                srl.setQuantity(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_QUANTITY)));
                srl.setUnitofMeasureCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_UNIT_OF_MEASURE_CODE)));
                srl.setUnitPrice(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_UNIT_PRICE)));
                srl.setAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_AMOUNT)));
                srl.setLineAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_LINE_AMOUNT)));
                srl.setLineDiscountAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_LINE_DISCOUNT_AMOUNT)));
                srl.setLineDiscountPercent(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_LINE_DISCOUNT_PERCENT)));
                srl.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_DRIVER_CODE)));

                srl.setTotalVATAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_TOTAL_VAT_AMOUNT)));
                srl.setTotalAmountInclVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_TOTAL_AMOUNT_INCL_VAT)));
                srl.setTaxPercentage(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LINE_TAX_PERCENTAGE)));

                stockRequestLines.add(srl);
            } while (c.moveToNext());
        }

        c.close();
        return stockRequestLines;
    }

    public int getTotalQtyByDocNo(String srKey) {

        int totalQty = 0;
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SR_LINE
                + " WHERE "+ dbHelper.KEY_SR_LINE_DOCUMENT_NO+" = ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{srKey});

        if (c.moveToFirst()) {
            do {
                totalQty += Math.round(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_LINE_QUANTITY)));

            } while (c.moveToNext());
        }

        c.close();
        return totalQty;
    }
}
