package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 10/24/2017.
 */

public class StockRequestDbHandler {
    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;

    public StockRequestDbHandler(Context context) {
        this.context = context;
    }

    public StockRequestDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new Sr's
    public void addStockRequest(StockRequest sr) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_SR_NO, sr.getNo());
        values.put(dbHelper.KEY_SR_SELL_TO_CUSTOMER_NO, sr.getSelltoCustomerNo());
        values.put(dbHelper.KEY_SR_SELL_TO_CUSTOMER_NAME, sr.getSelltoCustomerName());
        values.put(dbHelper.KEY_SR_EXTERNAL_DOCUMENT_NO, sr.getExternalDocumentNo());
        values.put(dbHelper.KEY_SR_SELL_TO_POST_CODE, sr.getSelltoPostCode());
        values.put(dbHelper.KEY_SR_SELL_TO_COUNTRY_REGION_CODE, sr.getSelltoCountryRegionCode());
        values.put(dbHelper.KEY_SR_SELL_TO_CONTRACT, sr.getSelltoContact());
        values.put(dbHelper.KEY_SR_BILL_TO_CUSTOMER_NO, sr.getBilltoCustomerNo());
        values.put(dbHelper.KEY_SR_BILL_TO_NAME, sr.getBilltoName());
        values.put(dbHelper.KEY_SR_BILL_TO_POST_CODE, sr.getBilltoPostCode());
        values.put(dbHelper.KEY_SR_BILL_TO_COUNTRY_REGION_CODE, sr.getBilltoCountryRegionCode());
        values.put(dbHelper.KEY_SR_BILL_TO_CONTACT, sr.getBilltoContact());
        values.put(dbHelper.KEY_SR_SHIP_TO_CODE, sr.getShiptoCode());
        values.put(dbHelper.KEY_SR_SHIP_TO_NAME, sr.getShiptoName());
        values.put(dbHelper.KEY_SR_SHIP_TO_POST_CODE, sr.getShiptoPostCode());
        values.put(dbHelper.KEY_SR_SHIP_TO_COUNTRY_REGION_CODE, sr.getShiptoCountryRegionCode());
        values.put(dbHelper.KEY_SR_SHIP_TO_CONTRACT, sr.getShiptoContact());
        values.put(dbHelper.KEY_SR_POSTING_DATE, sr.getPostingDate());
        values.put(dbHelper.KEY_SR_ORDER_DATE, sr.getOrderDate());
        values.put(dbHelper.KEY_SR_SHIPMENT_DATE, sr.getShipmentDate());
        values.put(dbHelper.KEY_SR_LOCATION_CODE, sr.getLocationCode());
        values.put(dbHelper.KEY_SR_SALES_PERSON_CODE, sr.getSalespersonCode());
        values.put(dbHelper.KEY_SR_DRIVER_CODE, sr.getDriverCode());
        values.put(dbHelper.KEY_SR_STATUS, sr.getStatus());

        values.put(dbHelper.KEY_SR_AMOUNT_INCL_VAT, String.format("%.2f", sr.getAmountInclVAT()));
        values.put(dbHelper.KEY_SR_VAT_AMOUNT, String.format("%.2f", sr.getVATAmount()));
        values.put(dbHelper.KEY_SR_TRANSFERRED, sr.getTransferred());
        values.put(dbHelper.KEY_SR_LAST_TRANSFERRED_BY, sr.getLastTransferredBy());
        values.put(dbHelper.KEY_SR_LAST_TRANSFERRED_DATETIME, sr.getLastTransferredDateTime());
        values.put(dbHelper.KEY_SR_CREATED_BY, sr.getCreatedBy());
        values.put(dbHelper.KEY_SR_CREATED_DATE, sr.getCreatedDateTime());
        values.put(dbHelper.KEY_SR_LAST_MODIFIED_BY, sr.getLastModifiedBy());
        values.put(dbHelper.KEY_SR_LAST_MODIFIED_DATE, sr.getLastModifiedDateTime());
        values.put(dbHelper.KEY_SR_IS_DOWNLOADED, sr.isDownloaded());

        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_SR, null, values);
    }

    public boolean isSrExist(String srNo) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_SR + " WHERE " + dbHelper.KEY_SR_NO + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{srNo});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public List<String> getAllStockRequestNoByDate(String date) {
        db = dbHelper.getReadableDatabase();
        List<String> orderNoList = new ArrayList<>();
        String selectQuery = "SELECT " + dbHelper.KEY_SR_NO + " FROM " + dbHelper.TABLE_SR + " WHERE " + dbHelper.KEY_SR_LAST_TRANSFERRED_DATETIME + " < ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{date});
        if (c.moveToFirst()) {
            do {
                orderNoList.add(c.getString(c.getColumnIndex(dbHelper.KEY_SR_NO)));
            } while (c.moveToNext());
        }
        return orderNoList;
    }


    public List<StockRequest> getAllStockRequest() {

        List<StockRequest> salesOrders = new ArrayList<StockRequest>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SR;
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                StockRequest sr = new StockRequest();

                sr.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_NO)));
                sr.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_CUSTOMER_NO)));
                sr.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_CUSTOMER_NAME)));
                sr.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_EXTERNAL_DOCUMENT_NO)));
                sr.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_POST_CODE)));
                sr.setSelltoCountryRegionCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_COUNTRY_REGION_CODE)));
                sr.setSelltoContact(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_CONTRACT)));
                sr.setBilltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_CUSTOMER_NO)));
                sr.setBilltoName(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_NAME)));
                sr.setBilltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_POST_CODE)));
                sr.setBilltoCountryRegionCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_COUNTRY_REGION_CODE)));
                sr.setBilltoContact(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_CONTACT)));
                sr.setShiptoCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_CODE)));
                sr.setShiptoName(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_NAME)));
                sr.setShiptoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_POST_CODE)));
                sr.setShiptoCountryRegionCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_COUNTRY_REGION_CODE)));
                sr.setShiptoContact(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_CONTRACT)));
                sr.setPostingDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_POSTING_DATE)));
                sr.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_ORDER_DATE)));
                sr.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIPMENT_DATE)));
                sr.setLocationCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LOCATION_CODE)));
                sr.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SALES_PERSON_CODE)));
                sr.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_DRIVER_CODE)));
                sr.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SR_STATUS)));

                salesOrders.add(sr);
            } while (c.moveToNext());
        }

        c.close();
        return salesOrders;
    }


    public void deleteStockRequestByDate(String lastModifyDate) {

        db = dbHelper.getWritableDatabase();
        int affectedRow = db.delete(dbHelper.TABLE_SR, dbHelper.KEY_SR_ORDER_DATE + "< ?", new String[]{lastModifyDate});

    }

    public boolean deleteStockRequest(String srNo) {
        boolean success = false;

        if (isSrExist(srNo)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_SR, dbHelper.KEY_SR_NO + "=?", new String[]{String.valueOf(srNo)});
            success = !isSrExist(srNo);
        } else {
            success = true;
        }
        return success;
    }

    public List<StockRequest> getStockRequestList(
            String mFilterDriverCode,
            String mFilterCustomerCode,
            String mFilterCustomerName,
            String mFilterStockRequestNo,
            String mFilterDeliveryDate,
            String mFilterStatus)
    {
        List<StockRequest> stockRequestList = new ArrayList<StockRequest>(),
                            mFinalSrList=new ArrayList<StockRequest>();
        Cursor c;
        db = dbHelper.getReadableDatabase();

        String dateString = "", dateToday = "", _filterStatus = "", _filterDeliveryDate = "";
        String _filterDriverCode = mFilterDriverCode.isEmpty() ? "" : mFilterDriverCode;
        String _filterCustomerCode = mFilterCustomerCode.isEmpty() ? "" : mFilterCustomerCode;
        String _filterCustomerName = mFilterCustomerName.isEmpty() ? "" : mFilterCustomerName;
        String _filterStockRequestNo = mFilterStockRequestNo.isEmpty() ? "" : mFilterStockRequestNo;

        if (mFilterStatus == "Pending") {
            _filterStatus = "0";
        } else if (mFilterStatus =="Confirm") {
            _filterStatus = "1";
        } else {
            _filterStatus = "";
        }

        //get today date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj = new Date();
        dateToday = df.format(dateObj).toString();

        if (mFilterDeliveryDate.isEmpty()) {

            _filterDeliveryDate = dateToday;
            dateString = "date(" + dbHelper.TABLE_SR + "."
                    + dbHelper.KEY_SR_CREATED_DATE + ") = date( "+dateToday+" ) OR "+
                    "date(" + dbHelper.TABLE_SR + "."
                    + dbHelper.KEY_SR_ORDER_DATE + ") > date( '" + _filterDeliveryDate + "' )";
        } else {

            _filterDeliveryDate = mFilterDeliveryDate;
            dateString = "date(" + dbHelper.TABLE_SR + "."
                    + dbHelper.KEY_SR_ORDER_DATE + ") = date( '" + _filterDeliveryDate + "' )";
        }

        //Note. delivery date as Order date or shipment date

        String selectQuery = "SELECT  " + dbHelper.TABLE_SR + ".*," + dbHelper.TABLE_CUSTOMER + "."
                + dbHelper.KEY_CUS_ADDRESS + " FROM " + dbHelper.TABLE_SR +
                " INNER JOIN " + dbHelper.TABLE_CUSTOMER + " ON "
                + dbHelper.TABLE_SR + "." + dbHelper.KEY_SR_SELL_TO_CUSTOMER_NO + " = " + dbHelper.TABLE_CUSTOMER
                + "." + dbHelper.KEY_CUS_CODE
                + " WHERE "
                + dbHelper.TABLE_SR + "." + dbHelper.KEY_SR_DRIVER_CODE + " = ? AND "
                + dbHelper.TABLE_SR + "." + dbHelper.KEY_SR_SELL_TO_CUSTOMER_NO + " LIKE ? AND "
                + dbHelper.TABLE_SR + "." + dbHelper.KEY_SR_SELL_TO_CUSTOMER_NAME + " LIKE ? AND "
                + dbHelper.TABLE_SR + "." + dbHelper.KEY_SR_NO + " LIKE ? AND "
                + dateString + " AND "
                + dbHelper.TABLE_SR + "." + dbHelper.KEY_SR_STATUS + " LIKE ? " +
                " ORDER BY datetime(" + dbHelper.TABLE_SR + "." + dbHelper.KEY_SR_ORDER_DATE + ") DESC";

        c = db.rawQuery(selectQuery, new String[]{_filterDriverCode,
                "%" + _filterCustomerCode + "%",
                "%" + _filterCustomerName + "%",
                "%" + _filterStockRequestNo + "%",
                "%" + _filterStatus + "%"});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                StockRequest sr = new StockRequest();

                sr.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_NO)));
                sr.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_CUSTOMER_NO)));
                sr.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_CUSTOMER_NAME)));
                sr.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_EXTERNAL_DOCUMENT_NO)));
                sr.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_POST_CODE)));
                sr.setSelltoCountryRegionCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_COUNTRY_REGION_CODE)));
                sr.setSelltoContact(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_CONTRACT)));
                sr.setBilltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_CUSTOMER_NO)));
                sr.setBilltoName(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_NAME)));
                sr.setBilltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_POST_CODE)));
                sr.setBilltoCountryRegionCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_COUNTRY_REGION_CODE)));
                sr.setBilltoContact(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_CONTACT)));
                sr.setShiptoCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_CODE)));
                sr.setShiptoName(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_NAME)));
                sr.setShiptoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_POST_CODE)));
                sr.setShiptoCountryRegionCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_COUNTRY_REGION_CODE)));
                sr.setShiptoContact(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_CONTRACT)));
                sr.setPostingDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_POSTING_DATE)));
                sr.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_ORDER_DATE)));
                sr.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIPMENT_DATE)));
                sr.setLocationCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LOCATION_CODE)));
                sr.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SALES_PERSON_CODE)));
                sr.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_DRIVER_CODE)));
                sr.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SR_STATUS)));

                sr.setAmountInclVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_AMOUNT_INCL_VAT)));
                sr.setVATAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_VAT_AMOUNT)));
                sr.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SR_TRANSFERRED)) > 0);
                sr.setLastTransferredBy(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LAST_TRANSFERRED_BY)));
                sr.setLastTransferredDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LAST_TRANSFERRED_DATETIME)));
                sr.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SR_CREATED_BY)));
                sr.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SR_CREATED_DATE)));
                sr.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LAST_MODIFIED_BY)));
                sr.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LAST_MODIFIED_DATE)));

                //setCustomer Address
                sr.setSellToCustomerAddress(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_ADDRESS)));

                String _statusPending = context.getResources().getString(R.string.StockRequestStatusPending),
                        _statusComplete = context.getResources().getString(R.string.StockRequestStatusComplete);

                if (c.getInt(c.getColumnIndex(dbHelper.KEY_SR_STATUS)) == Integer.parseInt(_statusPending)) {
                    sr.setConfirmedSr(false);
                } else if (c.getInt(c.getColumnIndex(dbHelper.KEY_SR_STATUS)) == Integer.parseInt(_statusComplete)) {
                    sr.setConfirmedSr(false);
                } else {
                    sr.setConfirmedSr(true);
                }

                if(sr.getNo() != null)
                {
                    StockRequestLineDbHandler srlDb=new StockRequestLineDbHandler(context);
                    srlDb.open();
                    sr.setTotalQty(srlDb.getTotalQtyByDocNo(sr.getNo()));
                    srlDb.close();
                }

                stockRequestList.add(sr);
            } while (c.moveToNext());
        }

        c.close();

        //sorting list by Status (pending, completed, confirmed)
        if (!stockRequestList.isEmpty()) {
            List<StockRequest> mPendingSrList = new ArrayList<StockRequest>(),
                    mConfirmedSrList = new ArrayList<StockRequest>(),
                    mCompletedSrList = new ArrayList<StockRequest>();

            for (StockRequest sr : stockRequestList) {
                if (sr.getStatus().equals(context.getResources().getString(R.string.StockRequestStatusPending))) {
                    mPendingSrList.add(sr);
                } else if (sr.getStatus().equals(context.getResources().getString(R.string.StockRequestStatusComplete))) {
                    mCompletedSrList.add(sr);
                } else if (sr.getStatus().equals(context.getResources().getString(R.string.StockRequestStatusConfirmed))) {
                    mConfirmedSrList.add(sr);
                }
            }

            if (!mPendingSrList.isEmpty())
                mFinalSrList.addAll(mPendingSrList);

            if (!mCompletedSrList.isEmpty())
                mFinalSrList.addAll(mCompletedSrList);

            if (!mConfirmedSrList.isEmpty())
                mFinalSrList.addAll(mConfirmedSrList);
        }

        return mFinalSrList;
    }

    public List<StockRequest> getSalesOrdersForUploading() {

        List<StockRequest> stockRequests = new ArrayList<StockRequest>();
        db = dbHelper.getReadableDatabase();

        String _filterStatus = context.getResources().getString(R.string.StockRequestStatusConfirmed);
        String _isTransfered = "0";

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_SR
                + " WHERE "
                + dbHelper.KEY_SR_STATUS + " = ? AND "
                + dbHelper.KEY_SR_TRANSFERRED + " = ? ";


        Cursor c = db.rawQuery(selectQuery, new String[]{_filterStatus, _isTransfered});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                StockRequest sr = new StockRequest();

                sr.setNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_NO)));
                sr.setSelltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_CUSTOMER_NO)));
                sr.setSelltoCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_CUSTOMER_NAME)));
                sr.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_EXTERNAL_DOCUMENT_NO)));
                sr.setSelltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_POST_CODE)));
                sr.setSelltoCountryRegionCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_COUNTRY_REGION_CODE)));
                sr.setSelltoContact(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SELL_TO_CONTRACT)));
                sr.setBilltoCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_CUSTOMER_NO)));
                sr.setBilltoName(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_NAME)));
                sr.setBilltoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_POST_CODE)));
                sr.setBilltoCountryRegionCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_COUNTRY_REGION_CODE)));
                sr.setBilltoContact(c.getString(c.getColumnIndex(dbHelper.KEY_SR_BILL_TO_CONTACT)));
                sr.setShiptoCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_CODE)));
                sr.setShiptoName(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_NAME)));
                sr.setShiptoPostCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_POST_CODE)));
                sr.setShiptoCountryRegionCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_COUNTRY_REGION_CODE)));
                sr.setShiptoContact(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIP_TO_CONTRACT)));
                sr.setPostingDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_POSTING_DATE)));
                sr.setOrderDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_ORDER_DATE)));
                sr.setShipmentDate(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SHIPMENT_DATE)));
                sr.setLocationCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LOCATION_CODE)));
                sr.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_SALES_PERSON_CODE)));
                sr.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_SR_DRIVER_CODE)));
                sr.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_SR_STATUS)));

                sr.setAmountInclVAT(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_AMOUNT_INCL_VAT)));
                sr.setVATAmount(c.getFloat(c.getColumnIndex(dbHelper.KEY_SR_VAT_AMOUNT)));
                sr.setTransferred(c.getInt(c.getColumnIndex(dbHelper.KEY_SR_TRANSFERRED)) > 0);
                sr.setLastTransferredBy(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LAST_TRANSFERRED_BY)));
                sr.setLastTransferredDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_SR_LAST_TRANSFERRED_DATETIME)));

                sr.setConfirmedSr(true);

                stockRequests.add(sr);
            } while (c.moveToNext());
        }

        c.close();

        return stockRequests;
    }

    public boolean saveConfirmStockRequests(List<StockRequest> srList) {
        List<StockRequest> comfirmedSrList = new ArrayList<>();
        boolean success = false;
        for (StockRequest sr : srList) {
            if (sr.isConfirmedSr()) {
                db = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(dbHelper.KEY_SR_TRANSFERRED, sr.getTransferred());
                contentValues.put(dbHelper.KEY_SR_STATUS, sr.getStatus());
                contentValues.put(dbHelper.KEY_SR_LAST_TRANSFERRED_BY, sr.getLastTransferredBy());
                contentValues.put(dbHelper.KEY_SR_LAST_TRANSFERRED_DATETIME, sr.getLastTransferredDateTime());
                //contentValues.put(dbHelper.KEY_SR_LAST_MODIFIED_BY, sr.getLastModifiedBy());
                //contentValues.put(dbHelper.KEY_SR_LAST_MODIFIED_DATE, sr.getLastModifiedDateTime());

                comfirmedSrList.add(sr);

                String id = String.valueOf(sr.getNo());

                if (db.update(dbHelper.TABLE_SR, contentValues, "" + dbHelper.KEY_SR_NO + " = ?", new String[]{id}) == 1)
                    success = true;
                else
                    success = false;
            }
        }
        return success;
    }

    public int getSrPendingCount(String driverCode) {
        int pendingCount = 0;
        db = dbHelper.getReadableDatabase();
        //String pendingStatus = context.getResources().getString(R.string.StockRequestStatusPending);
        String confirmedStatus = context.getResources().getString(R.string.StockRequestStatusConfirmed);

        String driverCode_ = driverCode == null ? "" : driverCode;

        String query = "SELECT * FROM " + dbHelper.TABLE_SR + " WHERE " + dbHelper.KEY_SR_STATUS + " != ?"
                + " AND " + dbHelper.KEY_SR_DRIVER_CODE + "=?";

        Cursor c = db.rawQuery(query, new String[]{confirmedStatus, driverCode_});

        pendingCount = c.getCount();
        c.close();
        return pendingCount;

    }

    // this method will delete SRs which are downloaded but not changed in mobile.
    public boolean deleteAllHqDownloadedSStockRequests() {

        String status = context.getResources().getString(R.string.SalesOrderStatusComplete);
        String downloaded = "1";

        db = dbHelper.getReadableDatabase();
        int affectedRows = db.delete(dbHelper.TABLE_SR
                , "" + dbHelper.KEY_SR_STATUS+ " = ?  "
                        + " AND "+ dbHelper.KEY_SR_IS_DOWNLOADED +" = ?"
                ,new String[]{status,downloaded});
        return affectedRows >= 0;
    }


}
