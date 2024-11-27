package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nelin_000 on 07/31/2017.
 */

public class SalesOrderLineDbHandler {
    private Context context;
    private DatabaseHandler dbHelper;
    SQLiteDatabase db;

    public SalesOrderLineDbHandler(Context context) {
        this.context = context;
    }

    public SalesOrderLineDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new So Lines
    public void addSalesOrderLine(SalesOrderLine soLine) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_SO_LINE_KEY, soLine.getKey());
        values.put(dbHelper.KEY_SO_LINE_TYPE, soLine.getType());
        values.put(dbHelper.KEY_SO_LINE_NO, soLine.getNo());
        values.put(dbHelper.KEY_SO_LINE_CROSS_REF_NO, soLine.getCrossReferenceNo());
        values.put(dbHelper.KEY_SO_LINE_DRIVER_CODE, soLine.getDriverCode());
        values.put(dbHelper.KEY_SO_LINE_DESCRIPTION, soLine.getDescription());
        values.put(dbHelper.KEY_SO_LINE_QTY, soLine.getQuantity());
        values.put(dbHelper.KEY_SO_LINE_UOM, soLine.getUnitofMeasure());
        values.put(dbHelper.KEY_SO_LINE_SALE_PRICE_EXIST, soLine.isSalesLineDiscExists());
        values.put(dbHelper.KEY_SO_LINE_UNIT_PRICE, String.format("%.2f", soLine.getUnitPrice()));
        values.put(dbHelper.KEY_SO_LINE_AMT, String.format("%.2f", soLine.getLineAmount()));
        values.put(dbHelper.KEY_SO_LINE_DISC_EXIST, soLine.isSalesLineDiscExists());
        values.put(dbHelper.KEY_SO_LINE_DISC_PERCENTAGE, soLine.getLineDiscountPercent());
        values.put(dbHelper.KEY_SO_LINE_DISC_AMT, soLine.getLineDiscountAmount());
        values.put(dbHelper.KEY_SO_LINE_QTY_TO_INVOICE, soLine.getQtytoInvoice());
        values.put(dbHelper.KEY_SO_LINE_QTY_INVOICED, soLine.getQuantityInvoiced());
        values.put(dbHelper.KEY_SO_LINE_DOCUMENT_NO, soLine.getDocumentNo());
        values.put(dbHelper.KEY_SO_LINE_LINE_NO, soLine.getLineNo());
        values.put(dbHelper.KEY_SO_LINE_TOT_AMT_EXCL_VAT, String.format("%.2f", soLine.getTotalAmountExclVAT()));
        values.put(dbHelper.KEY_SO_LINE_TOT_VAT_AMOUNT, String.format("%.2f", soLine.getTotalVATAmount()));
        values.put(dbHelper.KEY_SO_LINE_TOT_AMT_INCL_VAT, String.format("%.2f", soLine.getTotalAmountInclVAT()));
        values.put(dbHelper.KEY_SO_LINE_TAX_PERCENTAGE, soLine.getTaxPercentage());
        values.put(dbHelper.KEY_SO_LINE_EXCHANGED_QTY, soLine.getExchangedQty());
        values.put(dbHelper.KEY_SO_LINE_IS_EXCHANGE_ITEM, soLine.isExchangeItem());

        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_SO_LINE, null, values);
    }

    public boolean isSoLineExist(String soKey) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SO_LINE + " WHERE " + dbHelper.KEY_SO_LINE_KEY + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{soKey});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public boolean isSoLineExistForDocumentNo(String documetNo) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SO_LINE + " WHERE " + dbHelper.KEY_SO_LINE_DOCUMENT_NO + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{documetNo});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public List<SalesOrderLine> getAllSalesOrderLinesByDocumentNo(String soKey) {

        List<SalesOrderLine> salesOrderLines = new ArrayList<SalesOrderLine>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO_LINE
                + " WHERE " + dbHelper.KEY_SO_LINE_DOCUMENT_NO + " = ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{soKey});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesOrderLine soLine = new SalesOrderLine();
                soLine.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_ID))));
                soLine.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_KEY)));
                soLine.setType(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_LINE_TYPE)));
                soLine.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_NO)));
                soLine.setCrossReferenceNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_CROSS_REF_NO)));
                soLine.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_DRIVER_CODE)));
                soLine.setDescription(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_DESCRIPTION)));
                soLine.setQuantity(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_QTY)));
                soLine.setUnitofMeasure(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_UOM)));
                soLine.setSalesPriceExist(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_LINE_SALE_PRICE_EXIST)) > 0);
                soLine.setUnitPrice(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_UNIT_PRICE)));
                soLine.setLineAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_AMT)));
                soLine.setSalesLineDiscExists(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_LINE_DISC_EXIST)) > 0);
                soLine.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_DRIVER_CODE)));
                soLine.setLineDiscountPercent(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_DISC_PERCENTAGE)));
                soLine.setLineDiscountAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_DISC_AMT)));
                soLine.setQtytoInvoice(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_QTY_TO_INVOICE)));
                soLine.setQuantityInvoiced(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_QTY_INVOICED)));
                soLine.setDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_DOCUMENT_NO)));
                soLine.setLineNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_LINE_NO)));
                soLine.setTotalAmountExclVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_TOT_AMT_EXCL_VAT)));
                soLine.setTotalVATAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_TOT_VAT_AMOUNT)));
                soLine.setTotalAmountInclVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_TOT_AMT_INCL_VAT)));
                soLine.setTaxPercentage(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LINE_TAX_PERCENTAGE)));
                soLine.setExchangedQty(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_EXCHANGED_QTY)));
                soLine.setExchangeItem(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_LINE_IS_EXCHANGE_ITEM)) > 0);
                salesOrderLines.add(soLine);
            } while (c.moveToNext());
        }

        c.close();
        return salesOrderLines;
    }

    public boolean deleteSalesOrderLine(String key) {
        boolean success = false;

        if (isSoLineExist(key)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_SO_LINE, dbHelper.KEY_SO_LINE_KEY + "=?", new String[]{String.valueOf(key)});
            success = !isSoLineExist(key);
        } else {
            success = true;
        }
        return success;
    }

    public void deleteSOLineByDocumentNo(String documentNo) {
        db = dbHelper.getWritableDatabase();
        int affectedRows = db.delete(dbHelper.TABLE_SO_LINE, dbHelper.KEY_SO_LINE_DOCUMENT_NO + " = ? ", new String[]{documentNo});
        System.out.println(affectedRows);
    }

    public boolean deleteSalesOrderLineByDocumentNo(String documentNo) {
        boolean success = false;

        if (isSoLineExistForDocumentNo(documentNo)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_SO_LINE, dbHelper.KEY_SO_LINE_DOCUMENT_NO + "=?", new String[]{String.valueOf(documentNo)});
            success = !isSoLineExistForDocumentNo(documentNo);
        } else {
            success = true;
        }
        return success;
    }

    public int getInvoiceLineCount(String documetNo) {
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SO_LINE + " WHERE " + dbHelper.KEY_SO_LINE_DOCUMENT_NO + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{documetNo});
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getTotalBillQtyByDocNo(String soKey) {

        int totalBilQty = 0;
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO_LINE
                + " WHERE " + dbHelper.KEY_SO_LINE_DOCUMENT_NO + " = ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{soKey});

        if (c.moveToFirst()) {
            do {
                totalBilQty += Math.round(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_LINE_QTY)));

            } while (c.moveToNext());
        }

        c.close();
        return totalBilQty;
    }

}
