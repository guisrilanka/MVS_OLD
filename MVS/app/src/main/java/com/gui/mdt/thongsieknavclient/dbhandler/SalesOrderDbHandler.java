package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nelin_000 on 07/27/2017.
 */

public class SalesOrderDbHandler {

    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;

    public SalesOrderDbHandler(Context context) {
        this.context = context;
    }

    public SalesOrderDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new So's
    public void addSalesOrder(SalesOrder so) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_SO_KEY, so.getKey());
        values.put(dbHelper.KEY_SO_NO, so.getNo());
        values.put(dbHelper.KEY_SO_SELL_TO_CUS_NO, so.getSelltoCustomerNo());
        values.put(dbHelper.KEY_SO_SELL_TO_CUS_NAME, so.getSelltoCustomerName());
        values.put(dbHelper.KEY_SO_SELL_TO_ADDRESS, so.getSelltoAddress());
        values.put(dbHelper.KEY_SO_SELL_TO_CONTACT_NO, so.getSelltoContactNo());
        values.put(dbHelper.KEY_SO_SELL_TO_POST_CODE, so.getSelltoPostCode());
        values.put(dbHelper.KEY_SO_DUEDATE, so.getDueDate());
        values.put(dbHelper.KEY_SO_SELL_TO_CITY, so.getSelltoCity());
        values.put(dbHelper.KEY_SO_ORDER_DATE, so.getOrderDate());
        values.put(dbHelper.KEY_SO_DOCUMENT_DATE, so.getDocumentDate());
        values.put(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE, so.getRequestedDeliveryDate());
        values.put(dbHelper.KEY_SO_SHIPMENT_DATE, so.getShipmentDate());
        values.put(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO, so.getExternalDocumentNo());
        values.put(dbHelper.KEY_SO_SALESPERSON_CODE, so.getSalespersonCode());
        values.put(dbHelper.KEY_SO_DRIVER_CODE, so.getDriverCode());
        values.put(dbHelper.KEY_SO_STATUS, so.getStatus());
        values.put(dbHelper.KEY_SO_CREATED_BY, so.getCreatedBy());
        values.put(dbHelper.KEY_SO_CREATED_DATE, so.getCreatedDateTime());
        values.put(dbHelper.KEY_SO_LAST_MODIFIED_BY, so.getLastModifiedBy());
        values.put(dbHelper.KEY_SO_LAST_MODIFIED_DATE, so.getLastModifiedDateTime());
        values.put(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT, String.format("%.2f", so.getAmountIncludingVAT()));
        values.put(dbHelper.KEY_SO_TRANSFERRED, so.isTransferred());
        values.put(dbHelper.KEY_SO_COMMENTS, so.getComment());
        values.put(dbHelper.KEY_SO_CREATED_FROM, so.getCreatedFrom());

        values.put(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT, String.format("%.2f", so.getAmountExcludingVAT()));
        values.put(dbHelper.KEY_SO_VAT_AMOUNT, String.format("%.2f", so.getVATAmount()));
        values.put(dbHelper.KEY_DOCUMENT_TYPE, so.getDocumentType());
        values.put(dbHelper.KEY_SO_IS_DOWNLOADED, so.isDownloaded());
        values.put(dbHelper.KEY_SO_SI_DATE, so.getSIDate());
        values.put(dbHelper.KEY_SO_SI_NO, so.getSINo());
        values.put(dbHelper.KEY_SO_IS_DELETED, so.isDeleted());
        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_SO, null, values);

    }

    public boolean isSoExist(String soNo) {
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SO + " WHERE " + dbHelper.KEY_SO_NO + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{soNo});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public List<String> getAllSalesOrderNoByDate(String date) {
        db = dbHelper.getReadableDatabase();
        List<String> orderNoList = new ArrayList<>();
        String selectQuery = "SELECT " + dbHelper.KEY_SO_NO + " FROM " + dbHelper.TABLE_SO + " WHERE " + dbHelper.KEY_SO_LAST_MODIFIED_DATE + " < ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{date});
        if (c.moveToFirst()) {
            do {
                orderNoList.add(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
            } while (c.moveToNext());
        }
        return orderNoList;
    }

    public List<SalesOrder> getAllSalesOrders() {

        List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO;
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesOrder so = new SalesOrder();
                so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                so.setCreatedFrom(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_FROM)));
                so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));

                salesOrders.add(so);
            } while (c.moveToNext());
        }

        c.close();
        return salesOrders;
    }

    public int getSalesOrderCount() {
        db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + dbHelper.TABLE_SO;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public boolean deleteSalesOrder(String soNo) {
        boolean success = false;

        if (isSoExist(soNo)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_SO, dbHelper.KEY_SO_NO + "=?", new String[]{String.valueOf(soNo)});
            success = !isSoExist(soNo);
        } else {
            success = true;
        }
        return success;
    }

    public void deleteSalesOrderByDate(String lastModifyDate) {

        db = dbHelper.getWritableDatabase();
        int affectedRows = db.delete(dbHelper.TABLE_SO, dbHelper.KEY_SO_LAST_MODIFIED_DATE + "< ?", new String[]{lastModifyDate});


    }

    public List<SalesOrder> getSalesOrderList(String filterSalesPersonCode,
                                              String filterCustomerCode,
                                              String filterCustomerName,
                                              String filterSalesOrderNo,
                                              String filterCreatedDate,
                                              String filterStatus,
                                              boolean isFromSearch) {
        List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
        Cursor c;
        db = dbHelper.getReadableDatabase();

        String _filterCreatedDate = "";
        String _filterStatus = "";
        String _filterSalesPersonCode = "";
        String _filterCustomerCode = "";
        String _filterCustomerName = "";
        String _filterSalesOrderNo = "";


        _filterCreatedDate = "";
        _filterStatus = "";
        _filterSalesPersonCode = filterSalesPersonCode.isEmpty() ? "" : filterSalesPersonCode;
        _filterCustomerCode = filterCustomerCode.isEmpty() ? "" : filterCustomerCode;
        _filterCustomerName = filterCustomerName.isEmpty() ? "" : filterCustomerName;
        _filterSalesOrderNo = filterSalesOrderNo.isEmpty() ? "" : filterSalesOrderNo;


        if (filterStatus == "Pending") {
            _filterStatus = "0";
        } else if (filterStatus == "Confirm") {
            _filterStatus = "1";
        } else {
            _filterStatus = "";
        }

        if (filterCreatedDate.isEmpty()) {

            try {
                String str_date = "1900-01-01";
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date date = (Date) df.parse(str_date);
                _filterCreatedDate = df.format(date).toString();

            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date dateobj = new Date();
                _filterCreatedDate = df.format(dateobj).toString();
            }


        } else {
            _filterCreatedDate = filterCreatedDate;
        }

        if (!isFromSearch) {
            String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO
                    + " WHERE "
                    + dbHelper.KEY_SO_SALESPERSON_CODE + " = ? AND "
                    + dbHelper.KEY_SO_SELL_TO_CUS_NO + " LIKE ? AND "
                    + dbHelper.KEY_SO_SELL_TO_CUS_NAME + " LIKE ? AND "
                    + dbHelper.KEY_SO_NO + " LIKE ? AND date("
                    + dbHelper.KEY_SO_SHIPMENT_DATE + ") >= ? AND "
                    + dbHelper.KEY_SO_STATUS + " LIKE ? "
                    + " ORDER BY "
                    + dbHelper.KEY_SO_STATUS + " ASC "
                    + " ,datetime(" + dbHelper.KEY_SO_CREATED_DATE + ") DESC ";

            c = db.rawQuery(selectQuery, new String[]{_filterSalesPersonCode,
                    "%" + _filterCustomerCode + "%",
                    "%" + _filterCustomerName + "%",
                    "%" + _filterSalesOrderNo + "%",
                    _filterCreatedDate,
                    "%" + _filterStatus + "%"});


        } else {
            String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO
                    + " WHERE "
                    + dbHelper.KEY_SO_SALESPERSON_CODE + " = ? AND "
                    + dbHelper.KEY_SO_SELL_TO_CUS_NO + " LIKE ? AND "
                    + dbHelper.KEY_SO_SELL_TO_CUS_NAME + " LIKE ? AND "
                    + dbHelper.KEY_SO_NO + " LIKE ? AND date("
                    + dbHelper.KEY_SO_SHIPMENT_DATE + ") LIKE ? AND "
                    + dbHelper.KEY_SO_STATUS + " LIKE ? "
                    + " ORDER BY "
                    + dbHelper.KEY_SO_STATUS + " ASC "
                    + " ,datetime(" + dbHelper.KEY_SO_CREATED_DATE + ") DESC";

            c = db.rawQuery(selectQuery, new String[]{_filterSalesPersonCode,
                    "%" + _filterCustomerCode + "%",
                    "%" + _filterCustomerName + "%",
                    "%" + _filterSalesOrderNo + "%",
                    "%" + _filterCreatedDate + "%",
                    "%" + _filterStatus + "%"});
        }


        try {

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    SalesOrder so = new SalesOrder();
                    so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                    so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                    so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                    so.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                    so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                    so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                    so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                    so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                    so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                    so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                    so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                    so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                    so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                    so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                    so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                    so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                    so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                    so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                    so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                    so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                    so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                    so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                    so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                    so.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_TRANSFERRED)) > 0);
                    so.setCreatedFrom(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_FROM)));
                    so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));

                    String _confirm = context.getResources().getString(R.string.SalesOrderStatusPending);
                    if (c.getInt(c.getColumnIndex(dbHelper.KEY_SO_STATUS)) == Integer.parseInt(_confirm)) {
                        so.setConfirmedSo(false);
                    } else {
                        so.setConfirmedSo(true);
                    }

                    salesOrders.add(so);
                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.d("Error", "getSalesOrderList: " + ex.toString());
        }

        c.close();
        return salesOrders;
    }

    public List<SalesOrder> getSalesOrderListForLDS(String filterDriverCode,
                                                    String filterCustomerCode,
                                                    String filterCustomerName,
                                                    String filterSalesOrderNo,
                                                    String filterCreatedDate,
                                                    String filterStatus) {
        List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
        db = dbHelper.getReadableDatabase();

        String _filterCreatedDate = "";
        String _filterStatus = "";
        String _filterSecoundStatus = "";
        String _filterDriverCode = filterDriverCode.isEmpty() ? "" : filterDriverCode;
        String _filterCustomerCode = filterCustomerCode.isEmpty() ? "" : filterCustomerCode;
        String _filterCustomerName = filterCustomerName.isEmpty() ? "" : filterCustomerName;
        String _filterSalesOrderNo = filterSalesOrderNo.isEmpty() ? "" : filterSalesOrderNo;

//        if (filterStatus == "Pending") {
//            _filterStatus = "0";
//        } else if (filterStatus =="Confirm") {
//            _filterStatus = "1";
//        } else {
//            _filterStatus = "";
//        }

        _filterStatus = "2";
        _filterSecoundStatus = "3";

        if (filterCreatedDate.isEmpty()) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dateobj = new Date();
            _filterCreatedDate = df.format(dateobj).toString();
        } else {
            _filterCreatedDate = filterCreatedDate;
        }

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO
                + " WHERE "
                + dbHelper.KEY_SO_DRIVER_CODE + " = ? AND "
                + dbHelper.KEY_SO_SELL_TO_CUS_NO + " LIKE ? AND "
                + dbHelper.KEY_SO_SELL_TO_CUS_NAME + " LIKE ? AND "
                + dbHelper.KEY_SO_NO + " LIKE ? AND date("
                + dbHelper.KEY_SO_SHIPMENT_DATE + ") LIKE ? AND "
                + "(" + dbHelper.KEY_SO_STATUS + " = ? OR "
                + dbHelper.KEY_SO_STATUS + " = ? ) ORDER BY datetime(" + dbHelper.KEY_SO_CREATED_DATE + ") DESC";


        Cursor c = db.rawQuery(selectQuery, new String[]{_filterDriverCode,
                "%" + _filterCustomerCode + "%",
                "%" + _filterCustomerName + "%",
                "%" + _filterSalesOrderNo + "%",
                "%" + _filterCreatedDate + "%",
                _filterStatus,
                _filterSecoundStatus});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesOrder so = new SalesOrder();
                so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                so.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                so.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_TRANSFERRED)) > 0);
                so.setCreatedFrom(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_FROM)));
                so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));
                so.setDelivered(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_DELIVERED)) > 0);

                String _confirm = context.getResources().getString(R.string.SalesOrderStatusPending);
                if (c.getInt(c.getColumnIndex(dbHelper.KEY_SO_STATUS)) == Integer.parseInt(_confirm)) {
                    so.setConfirmedSo(false);
                } else {
                    so.setConfirmedSo(true);
                }

                salesOrders.add(so);
            } while (c.moveToNext());
        }

        c.close();
        return salesOrders;
    }


    public List<SalesOrder> getSalesOrderListForMVS(String filterDriverCode,
                                                    String filterCustomerCode,
                                                    String filterCustomerName,
                                                    String filterSalesOrderNo,
                                                    String filterCreatedDate,
                                                    String filterStatus) {
        List<SalesOrder> salesOrders = new ArrayList<SalesOrder>(),
                finalSoList = new ArrayList<SalesOrder>();
        db = dbHelper.getReadableDatabase();

        String _filterCreatedDate = "";
        String _filterStatus = filterStatus.isEmpty() ? "" : filterStatus;
        ;
        String _filterDriverCode = filterDriverCode.isEmpty() ? "" : filterDriverCode;
        String _filterCustomerCode = filterCustomerCode.isEmpty() ? "" : filterCustomerCode;
        String _filterCustomerName = filterCustomerName.isEmpty() ? "" : filterCustomerName;
        String _filterSalesOrderNo = filterSalesOrderNo.isEmpty() ? "" : filterSalesOrderNo;
        String _filterSODeleted = "0";

        if (filterCreatedDate.isEmpty()) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dateObj = new Date();
            _filterCreatedDate = df.format(dateObj).toString();
        } else {
            _filterCreatedDate = filterCreatedDate;
        }

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO
                + " WHERE "
                + dbHelper.KEY_SO_DRIVER_CODE + " = ? AND "
                + dbHelper.KEY_SO_SELL_TO_CUS_NO + " LIKE ? AND "
                + dbHelper.KEY_SO_SELL_TO_CUS_NAME + " LIKE ? AND "
                + dbHelper.KEY_SO_NO + " LIKE ? AND "
                + "date(" + dbHelper.KEY_SO_SHIPMENT_DATE + ") = date( ? ) AND "
                + dbHelper.KEY_SO_STATUS + " LIKE ? AND "
                + dbHelper.KEY_SO_IS_DELETED + " = ? "
                + " ORDER BY " + dbHelper.KEY_SO_STATUS + " ASC "
                + " ,datetime(" + dbHelper.KEY_SO_CREATED_DATE + ") DESC";

        Cursor c = db.rawQuery(selectQuery, new String[]{_filterDriverCode,
                "%" + _filterCustomerCode + "%",
                "%" + _filterCustomerName + "%",
                "%" + _filterSalesOrderNo + "%",
                _filterCreatedDate,
                "%" + _filterStatus + "%",
                _filterSODeleted
        });

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesOrder so = new SalesOrder();
                so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                so.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                so.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_TRANSFERRED)) > 0);
                so.setCreatedFrom(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_FROM)));
                so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));
                so.setDelivered(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_DELIVERED)) > 0);
                so.setSIDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SI_DATE)));
                so.setDownloaded(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_IS_DOWNLOADED)) > 0);
                so.setSINo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SI_NO)));
                so.setVATAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_VAT_AMOUNT)));
                so.setDeleted(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_IS_DELETED)) > 0);

                String _confirm = context.getResources().getString(R.string.MVSSalesOrderStatusConfirmed);
                if (c.getInt(c.getColumnIndex(dbHelper.KEY_SO_STATUS)) == Integer.parseInt(_confirm)) {
                    so.setConfirmedSo(true);
                } else {
                    so.setConfirmedSo(false);
                }

                if (so.getNo() != null) {
                    SalesOrderLineDbHandler solDb = new SalesOrderLineDbHandler(context);
                    solDb.open();
                    so.setTotalBillQty(solDb.getTotalBillQtyByDocNo(so.getNo()));
                    solDb.close();
                }

                salesOrders.add(so);
            } while (c.moveToNext());
        }

        c.close();

        //sort so list
        if (!salesOrders.isEmpty()) {
            List<SalesOrder> listSoPending = new ArrayList<SalesOrder>(),
                    listSoDownloaded = new ArrayList<SalesOrder>(),
                    listSoConvert = new ArrayList<SalesOrder>(),
                    listSoVoid = new ArrayList<SalesOrder>(),
                    listSoConfirm = new ArrayList<SalesOrder>();

            for (SalesOrder so : salesOrders) {
                if (so.getStatus().equals(context.getResources().getString(R.string.MVSSalesOrderStatusPending))) {
                    listSoPending.add(so);
                } else if (so.getStatus().equals(context.getResources().getString(R.string
                        .MVSSalesOrderStatusComplete))) {
                    listSoDownloaded.add(so);
                } else if (so.getStatus().equals(context.getResources().getString(R.string
                        .MVSSalesOrderStatusConverted))) {
                    listSoConvert.add(so);
                } else if (so.getStatus().equals(context.getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
                    listSoVoid.add(so);
                } else if (so.getStatus().equals(context.getResources().getString(R.string
                        .MVSSalesOrderStatusConfirmed))) {
                    listSoConfirm.add(so);
                }
            }
            if (!listSoPending.isEmpty()) {
                finalSoList.addAll(listSoPending);
            }
            if (!listSoDownloaded.isEmpty()) {
                finalSoList.addAll(listSoDownloaded);
            }
            if (!listSoConvert.isEmpty()) {
                finalSoList.addAll(listSoConvert);
            }
            if (!listSoVoid.isEmpty()) {
                finalSoList.addAll(listSoVoid);
            }
            if (!listSoConfirm.isEmpty()) {
                finalSoList.addAll(listSoConfirm);
            }
        }
        return finalSoList;
    }

    public boolean saveConfirmSalesOrders(List<SalesOrder> salesOrderList) {
        List<SalesOrder> comfirmedSalesOrderList = new ArrayList<>();
        boolean success = false;
        for (SalesOrder so : salesOrderList) {
            if (so.isConfirmedSo()) {
                db = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(dbHelper.KEY_SO_TRANSFERRED, so.isTransferred());
                contentValues.put(dbHelper.KEY_SO_STATUS, so.getStatus());
                contentValues.put(dbHelper.KEY_SO_LAST_TRANSFERRED_BY, so.getLastTransferredBy());
                contentValues.put(dbHelper.KEY_SO_LAST_TRANSFERRED_DATETIME, so.getLastTransferredDateTime());
                contentValues.put(dbHelper.KEY_SO_LAST_MODIFIED_BY, so.getLastModifiedBy());
                contentValues.put(dbHelper.KEY_SO_LAST_MODIFIED_DATE, so.getLastModifiedDateTime());

                comfirmedSalesOrderList.add(so);

                String id = String.valueOf(so.getKey());

                if (db.update(dbHelper.TABLE_SO, contentValues, "" + dbHelper.KEY_SO_KEY + " = ?", new String[]{id})
                        == 1)
                    success = true;
                else
                    success = false;
            }
        }
        return success;
    }

    public List<SalesOrder> getSalesOrdersForUploading() {

        List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
        db = dbHelper.getReadableDatabase();

        String _filterStatus = context.getResources().getString(R.string.SalesOrderStatusConfirmed);
        String _isTransfered = "0";

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO
                + " WHERE "
                + dbHelper.KEY_SO_STATUS + " = ? AND "
                + dbHelper.KEY_SO_TRANSFERRED + " = ? ORDER BY datetime(" + dbHelper.KEY_SO_CREATED_DATE + ") DESC";


        Cursor c = db.rawQuery(selectQuery, new String[]{_filterStatus, _isTransfered});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesOrder so = new SalesOrder();
                so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                so.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                so.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_TRANSFERRED)) > 0);
                so.setConfirmedSo(true);
                so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));


                salesOrders.add(so);
            } while (c.moveToNext());
        }

        c.close();

        return salesOrders;
    }

    public List<SalesOrder> getSalesInvoiceForUploading() {

        List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
        db = dbHelper.getReadableDatabase();

        String _filterStatus = context.getResources().getString(R.string.SalesOrderStatusConfirmed);
        String _isTransfered = "0";

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO
                + " WHERE "
                + dbHelper.KEY_SO_STATUS + " = ? AND "
                + dbHelper.KEY_SO_TRANSFERRED + " = ? ORDER BY datetime(" + dbHelper.KEY_SO_CREATED_DATE + ") DESC";


        Cursor c = db.rawQuery(selectQuery, new String[]{_filterStatus, _isTransfered});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesOrder so = new SalesOrder();
                so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                so.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                so.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_TRANSFERRED)) > 0);
                so.setConfirmedSo(true);
                so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));
                so.setSINo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SI_NO)));
                so.setSIDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SI_DATE)));


                salesOrders.add(so);
            } while (c.moveToNext());
        }

        c.close();

        return salesOrders;
    }

    public List<SalesOrder> getMobileSalesOrderForCustomer(String salesPerson, String customer) {

        List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
        db = dbHelper.getReadableDatabase();

        String _filterStatusConfirmed = context.getResources().getString(R.string.SalesOrderStatusConfirmed);
        String _filterStatusPending = context.getResources().getString(R.string.SalesOrderStatusPending);

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO
                + " WHERE "
                + dbHelper.KEY_SO_SELL_TO_CUS_NO + " = ?  AND "
                + dbHelper.KEY_SO_SALESPERSON_CODE + " = ? AND"
                + " ( " + dbHelper.KEY_SO_STATUS + " = ? OR " + dbHelper.KEY_SO_STATUS + " = ? " + " ) "
                + "ORDER BY datetime(" + dbHelper.KEY_SO_CREATED_DATE + ") DESC";


        Cursor c = db.rawQuery(selectQuery,
                new String[]{customer, salesPerson, _filterStatusConfirmed, _filterStatusPending});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesOrder so = new SalesOrder();
                so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                so.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                so.setConfirmedSo(true);
                so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));


                salesOrders.add(so);
            } while (c.moveToNext());
        }

        c.close();

        return salesOrders;
    }


    public boolean UpdateSalesInvoiceStatus(String salesInvoiceNo) {

        boolean success = false;
        SalesOrder salesInvoice = getSalesOrder(salesInvoiceNo);
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_SO_DELIVERED, true);
        contentValues.put(dbHelper.KEY_SO_STATUS, context.getResources().getString(R.string.SalesOrderStatusDelivery));

        String id = String.valueOf(salesInvoice.getKey());

        if (db.update(dbHelper.TABLE_SO, contentValues, "" + dbHelper.KEY_SO_KEY + " = ?", new String[]{id}) == 1)
            success = true;
        else
            success = false;

        return success;
    }

    // Getting SalesOrder
    public SalesOrder getSalesOrder(String salesInvoiceNo) {

        List<SalesOrder> soList = new ArrayList<SalesOrder>();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SO + " WHERE " + dbHelper.KEY_SO_NO + " = ?";
        Cursor c = db.rawQuery(query, new String[]{salesInvoiceNo});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SalesOrder so = new SalesOrder();
                so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                so.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                so.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_TRANSFERRED)) > 0);
                so.setCreatedFrom(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_FROM)));
                so.setDelivered(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_DELIVERED)) > 0);
                so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));

                soList.add(so);
            } while (c.moveToNext());
        }

        c.close();
        if (soList.size() > 0) {
            return soList.get(0);
        } else {
            return new SalesOrder();
        }

    }

    public List<SalesOrder> getSalesOrderForUploading(String documentNo) {
        List<SalesOrder> soReturnList = new ArrayList<SalesOrder>();
        db = dbHelper.getReadableDatabase();
        String deliveredStatus = "1";

        String query = "SELECT * FROM " + dbHelper.TABLE_SO + " WHERE " + dbHelper.KEY_SO_NO + " = ?";
        Cursor c = db.rawQuery(query, new String[]{documentNo});

        if (c.moveToFirst()) {
            do {
                SalesOrder so = new SalesOrder();
                so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                so.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                so.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_TRANSFERRED)) > 0);
                so.setCreatedFrom(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_FROM)));
                so.setDelivered(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_DELIVERED)) > 0);
                so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));

                soReturnList.add(so);
            } while (c.moveToNext());
        }

        c.close();
        return soReturnList;

    }

    public List<SalesOrder> getDownloadedPendingSalesOrder() {
        List<SalesOrder> soReturnList = new ArrayList<SalesOrder>();
        db = dbHelper.getReadableDatabase();

        String _filterPendingStatus = context.getResources().getString(R.string.SalesOrderStatusPending);
        String _filterCompleteStatus = context.getResources().getString(R.string.SalesOrderStatusComplete);
        String _filterDownloadStatus = "1";

        String query = "SELECT * FROM " + dbHelper.TABLE_SO + " WHERE "
                + " ( " + dbHelper.KEY_SO_STATUS + " =? OR "
                + dbHelper.KEY_SO_STATUS + " =? )"
                + " AND " + dbHelper.KEY_SO_IS_DOWNLOADED + " =?";
        Cursor c = db.rawQuery(query, new String[]{_filterPendingStatus, _filterCompleteStatus, _filterDownloadStatus});

        if (c.moveToFirst()) {
            do {
                SalesOrder so = new SalesOrder();
                so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                so.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                so.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_TRANSFERRED)) > 0);
                so.setCreatedFrom(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_FROM)));
                so.setDelivered(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_DELIVERED)) > 0);
                so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));

                soReturnList.add(so);
            } while (c.moveToNext());
        }

        c.close();
        return soReturnList;

    }

    public boolean updateSalesInvoiceNo(String soNo, String salesInvoiceNo, boolean isVoid) {

        boolean success = false;

        SimpleDateFormat soNumberFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = soNumberFormat.format(new Date());

        SalesOrder salesOrder = getSalesOrder(soNo);

        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (isVoid) {
            contentValues.put(dbHelper.KEY_SO_STATUS, context.getResources().getString(R.string
                    .MVSSalesOrderStatusVoid));
            contentValues.put(dbHelper.KEY_SO_SI_NO, salesInvoiceNo);
        } else {
            contentValues.put(dbHelper.KEY_SO_STATUS, context.getResources().getString(R.string
                    .MVSSalesOrderStatusConverted));
            contentValues.put(dbHelper.KEY_SO_SI_DATE, today);
            contentValues.put(dbHelper.KEY_SO_SI_NO, salesInvoiceNo);
        }

        String id = String.valueOf(salesOrder.getKey());

        if (db.update(dbHelper.TABLE_SO, contentValues, "" + dbHelper.KEY_SO_KEY + " = ?", new String[]{id}) == 1)
            success = true;
        else
            success = false;

        return success;
    }

    public boolean deleteAllHqDownloadedSalesOrders() {

        //2017-12-07 if so not transferred to navision from mobile so table do not delete from mobile.(2012)
        // 2017-12-12 if delete if mobile uploaded to mobilesalesorder table in navision.(2013)

        String status = context.getResources().getString(R.string.SalesOrderStatusComplete);
        //String transferred = context.getResources().getString(R.string.status_transfered); //2012
        String transferred = context.getResources().getString(R.string.status_transfered); //2013

        db = dbHelper.getReadableDatabase();
        int affectedRows = db.delete(dbHelper.TABLE_SO
                , "" + dbHelper.KEY_SO_STATUS + " = ?  "
                        //+ " OR "+ dbHelper.KEY_SO_TRANSFERRED +" = ?" //2012
                        + " OR " + dbHelper.KEY_SO_TRANSFERRED + " = ?" //2013
                , new String[]{status, transferred});
        return affectedRows >= 0;
    }

    public boolean deleteAllHqDownloadedSalesOrdersForMvs() {

        //2018-02-28 - arangala request to delete and re download if SO not confirmed. PDA changes will lost in this case.
        String statusComplete = context.getResources().getString(R.string.SalesOrderStatusComplete);
        String statusPending = context.getResources().getString(R.string.SalesOrderStatusPending);
        String isDownloaded = "1";
        //String transferred = context.getResources().getString(R.string.status_transfered);

        db = dbHelper.getReadableDatabase();
        int affectedRows = db.delete(dbHelper.TABLE_SO
                , "" + " ( " + dbHelper.KEY_SO_STATUS + " = ?   OR " + dbHelper.KEY_SO_STATUS + " = ?   )"
                        + " AND " + dbHelper.KEY_SO_IS_DOWNLOADED + " = ? "
                , new String[]{statusComplete, statusPending, isDownloaded});
        return affectedRows >= 0;
    }

    public int getSoPendingCount(String salesPerson) {
        int pendingCount = 0;
        db = dbHelper.getReadableDatabase();
        String pendingStatus = context.getResources().getString(R.string.SalesOrderStatusPending);
        String salesPersonCode_ = salesPerson == null ? "" : salesPerson;

        String query = "SELECT * FROM " + dbHelper.TABLE_SO + " WHERE " + dbHelper.KEY_SO_STATUS + " = ?"
                + " AND " + dbHelper.KEY_SO_SALESPERSON_CODE + "=?";

        Cursor c = db.rawQuery(query, new String[]{pendingStatus, salesPersonCode_});

        pendingCount = c.getCount();
        c.close();
        return pendingCount;

    }

    public float getAllSoTotalAmtInclVat(String customerNo, String shipmentDate, String
            invoiceNo) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //String deliveryDate = df.format(shipmentDate).toString();
        String deliveryDate = shipmentDate;
        String customerCode = customerNo.isEmpty() ? "" : customerNo;

        float totalAmt = 0;
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SO + " WHERE "
                + "date(" + dbHelper.KEY_SO_SHIPMENT_DATE + ") = date( ? )"
                + " AND " + dbHelper.KEY_SO_SELL_TO_CUS_NO + " = ? ";

        Cursor c = db.rawQuery(query, new String[]{deliveryDate, customerCode});

        if (c.moveToFirst()) {
            do {
                String soNo = c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO));
                if (!soNo.equals(invoiceNo)) {
                    totalAmt = totalAmt + c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT));
                }

            } while (c.moveToNext());
        }
        c.close();
        return totalAmt;
    }

    public int getMVSSoPendingCount(String driverCode) {
        int pendingCount = 0;

        //get date today
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj = new Date();
        String dateToday = df.format(dateObj).toString();

        db = dbHelper.getReadableDatabase();
        String confirmedStatus = context.getResources().getString(R.string.SalesOrderStatusConfirmed);
        String driverCode_ = driverCode == null ? "" : driverCode;
        String isSoDeleted = "0";

        String query = "SELECT * FROM " + dbHelper.TABLE_SO + " WHERE " + dbHelper.KEY_SO_STATUS + " != ?"
                + " AND " + dbHelper.KEY_SO_DRIVER_CODE + "=?"
                //+ " AND " + dbHelper.KEY_SO_SHIPMENT_DATE+" = ?"
                + " AND " + "date(" + dbHelper.KEY_SO_SHIPMENT_DATE + ") = date( ? )"
                + " AND " + dbHelper.KEY_SO_IS_DELETED + " = ? ";

        Cursor c = db.rawQuery(query, new String[]{confirmedStatus, driverCode_, dateToday, isSoDeleted});

        pendingCount = c.getCount();
        c.close();
        return pendingCount;

    }

    public List<SalesOrder> getSalesInvoiceSummaryList(String filterCreatedDate) {
        List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
        Cursor c;
        db = dbHelper.getReadableDatabase();

        String _filterCreatedDate = "";
        String _filterStatusConverted = "";
        String _filterStatusConfirmed = "";

        _filterStatusConverted = "3";
        _filterStatusConfirmed = "1";


        if (filterCreatedDate.isEmpty()) {

            try {
                String str_date = "1900-01-01";
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date date = (Date) df.parse(str_date);
                _filterCreatedDate = df.format(date).toString();

            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date dateobj = new Date();
                _filterCreatedDate = df.format(dateobj).toString();
            }


        } else {
            _filterCreatedDate = filterCreatedDate;
        }

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SO
                + " WHERE "
                + dbHelper.KEY_SO_SI_DATE + " = ? AND "
                + "(" + dbHelper.KEY_SO_STATUS + " = ?  " + " OR " + dbHelper.KEY_SO_STATUS + " = ?" + ")"
                + " ORDER BY "
                + dbHelper.KEY_SO_SI_NO + " ASC ";

        c = db.rawQuery(selectQuery, new String[]{_filterCreatedDate, _filterStatusConverted, _filterStatusConfirmed});


        try {

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    SalesOrder so = new SalesOrder();
                    so.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ID))));
                    so.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_SO_KEY)));
                    so.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO)));
                    so.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NO)));
                    so.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CUS_NAME)));
                    so.setSelltoAddress(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_ADDRESS)));
                    so.setSelltoContactNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CONTACT_NO)));
                    so.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_POST_CODE)));
                    so.setDueDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DUEDATE)));
                    so.setSelltoCity(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SELL_TO_CITY)));
                    so.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_ORDER_DATE)));
                    so.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DOCUMENT_DATE)));
                    so.setRequestedDeliveryDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_REQUESTED_DELIVERY_DATE)));
                    so.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SHIPMENT_DATE)));
                    so.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO)));
                    so.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SALESPERSON_CODE)));
                    so.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SO_DRIVER_CODE)));
                    so.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SO_STATUS)));
                    so.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_BY)));
                    so.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_DATE)));
                    so.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_BY)));
                    so.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SO_LAST_MODIFIED_DATE)));
                    so.setAmountIncludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_INCLUDING_VAT)));
                    so.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SO_TRANSFERRED)) > 0);
                    so.setCreatedFrom(c.getString(c.getColumnIndex(dbHelper.KEY_SO_CREATED_FROM)));
                    so.setAmountExcludingVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SO_AMOUNT_EXCLUDING_VAT)));
                    so.setSINo(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SI_NO)));
                    so.setSIDate(c.getString(c.getColumnIndex(dbHelper.KEY_SO_SI_DATE)));

                    String _confirm = context.getResources().getString(R.string.SalesOrderStatusPending);
                    if (c.getInt(c.getColumnIndex(dbHelper.KEY_SO_STATUS)) == Integer.parseInt(_confirm)) {
                        so.setConfirmedSo(false);
                    } else {
                        so.setConfirmedSo(true);
                    }

                    salesOrders.add(so);
                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.d("Error", "getSalesOrderList: " + ex.toString());
        }

        c.close();
        return salesOrders;
    }

    public boolean deleteSO(String soNo) {

        boolean success = false;

        SalesOrder salesOrder = getSalesOrder(soNo);

        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.KEY_SO_IS_DELETED, true);

        String id = String.valueOf(salesOrder.getKey());

        if (db.update(dbHelper.TABLE_SO
                , contentValues
                , "" + dbHelper.KEY_SO_KEY + " = ?"
                , new String[]{id}) == 1)
            success = true;
        else
            success = false;

        return success;
    }

    public List<String> getConfirmedSoNoList() {
        List<String> soNoList = new ArrayList<String>();
        db = dbHelper.getReadableDatabase();
        String soConfirmedStatus
                = context.getResources().getString(R.string.SalesOrderStatusConfirmed);

        String query = "SELECT * FROM " + dbHelper.TABLE_SO
                + " WHERE " + dbHelper.KEY_SO_STATUS + " = ?";

        Cursor c = db.rawQuery(query, new String[]{soConfirmedStatus});

        if (c.moveToFirst()) {
            do {
                String soNo = c.getString(c.getColumnIndex(dbHelper.KEY_SO_NO));
                if (soNo != null) {
                    if (!soNo.equals("")) {
                        soNoList.add(soNo);
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return soNoList;
    }
}
