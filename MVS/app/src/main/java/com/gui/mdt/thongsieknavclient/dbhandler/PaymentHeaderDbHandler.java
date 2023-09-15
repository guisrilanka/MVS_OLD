package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentHeader;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentLine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 8/28/2017.
 */

public class PaymentHeaderDbHandler {
    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;

    public PaymentHeaderDbHandler(Context context) {
        this.context = context;
    }

    public PaymentHeaderDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new PaymentHeader
    public void addPaymentHeader(PaymentHeader paymentHeader) {
        ContentValues values = new ContentValues();

        values.put(dbHelper.KEY_PAYMENT_NO, paymentHeader.getPaymentNo());
        values.put(dbHelper.KEY_PAYMENT_MODULE_ID, paymentHeader.getModuleId());
        values.put(dbHelper.KEY_PAYMENT_CUS_CODE, paymentHeader.getCustomerNo());
        values.put(dbHelper.KEY_PAYMENT_CUS_NAME, paymentHeader.getCustomerName());
        values.put(dbHelper.KEY_PAYMENT_DRIVER_CODE, paymentHeader.getDriverCode());

        values.put(dbHelper.KEY_PAYMENT_SALES_PERSON_CODE, paymentHeader.getSalesPersonCode());
        values.put(dbHelper.KEY_PAYMENT_TOTAL_AMOUNT, paymentHeader.getTotalAmount());
        values.put(dbHelper.KEY_PAYMENT_PAYMENT_DATE, paymentHeader.getPaymentDate());
        values.put(dbHelper.KEY_PAYMENT_CREATED_BY, paymentHeader.getCreatedBy());

        values.put(dbHelper.KEY_PAYMENT_CREATED_DATE, paymentHeader.getCreatedDateTime());
        values.put(dbHelper.KEY_PAYMENT_TRANSFERRED, paymentHeader.getTransferred());
        values.put(dbHelper.KEY_PAYMENT_LAST_TRANSFERRED_BY, paymentHeader.getLastTransferredBy());
        values.put(dbHelper.KEY_PAYMENT_LAST_TRANSFERRED_DATE, paymentHeader.getLastTransferredDateTime());
        values.put(dbHelper.KEY_PAYMENT_EXTERNAL_DOCUMENT_NO, paymentHeader.getExternalDocumentNo());
        values.put(dbHelper.KEY_PAYMENT_STATUS, paymentHeader.getStatus());
        values.put(dbHelper.KEY_PAYMENT_REFERENCE_AMOUNT, paymentHeader.getReferenceAmount());


        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_PAYMENT_HEADER, null, values);
    }

    public void updatePaymentHeader(PaymentHeader paymentHeader) {
        ContentValues values = new ContentValues();

        //values.put(dbHelper.KEY_PAYMENT_NO, paymentHeader.getPaymentNo());
        values.put(dbHelper.KEY_PAYMENT_MODULE_ID, paymentHeader.getModuleId());
        values.put(dbHelper.KEY_PAYMENT_CUS_CODE, paymentHeader.getCustomerNo());
        values.put(dbHelper.KEY_PAYMENT_CUS_NAME, paymentHeader.getCustomerName());
        values.put(dbHelper.KEY_PAYMENT_DRIVER_CODE, paymentHeader.getDriverCode());

        values.put(dbHelper.KEY_PAYMENT_SALES_PERSON_CODE, paymentHeader.getSalesPersonCode());
        values.put(dbHelper.KEY_PAYMENT_TOTAL_AMOUNT, paymentHeader.getTotalAmount());
        values.put(dbHelper.KEY_PAYMENT_PAYMENT_DATE, paymentHeader.getPaymentDate());
        //values.put(dbHelper.KEY_PAYMENT_CREATED_BY, paymentHeader.getCreatedBy());

        //values.put(dbHelper.KEY_PAYMENT_CREATED_DATE, paymentHeader.getCreatedDateTime());
        values.put(dbHelper.KEY_PAYMENT_TRANSFERRED, paymentHeader.getTransferred());
        values.put(dbHelper.KEY_PAYMENT_LAST_TRANSFERRED_BY, paymentHeader.getLastTransferredBy());
        values.put(dbHelper.KEY_PAYMENT_LAST_TRANSFERRED_DATE, paymentHeader.getLastTransferredDateTime());

        values.put(dbHelper.KEY_PAYMENT_EXTERNAL_DOCUMENT_NO, paymentHeader.getExternalDocumentNo());
        values.put(dbHelper.KEY_PAYMENT_STATUS, paymentHeader.getStatus());
        values.put(dbHelper.KEY_PAYMENT_REFERENCE_AMOUNT,paymentHeader.getReferenceAmount());

        // Update Row
        db = dbHelper.getWritableDatabase();
        db.update(dbHelper.TABLE_PAYMENT_HEADER, values, dbHelper.KEY_PAYMENT_NO +" = '" +paymentHeader.getPaymentNo()+ "'", null);
    }

    public boolean isPaymentHeaderExist(String code) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_PAYMENT_HEADER + " WHERE " + dbHelper.KEY_PAYMENT_NO + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{code});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    // Getting All PaymentHeaders
    public List<PaymentHeader> getAllPaymentHeaders(String customerName,String paymentDate, String status, String customerCode) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String _filterCustomerName = customerName.isEmpty() ? "" : customerName;
        String _filterCustomerCode = customerCode.isEmpty() ? "" : customerCode;
        String _filterPaymentDate ="";
        String _filterStatus="";

        if (status.equals("Pending")) {
            _filterStatus = "0";
        } else if (status.equals("Confirm")) {
            _filterStatus = "1";
        } else {
            _filterStatus = "";
        }

        if (paymentDate.isEmpty()) {

            Date dateobj = new Date();
            _filterPaymentDate = df.format(dateobj).toString();
        } else {
            _filterPaymentDate = paymentDate;
        }

        List<PaymentHeader> paymentHeaderList = new ArrayList<PaymentHeader>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  PH.* FROM " + dbHelper.TABLE_PAYMENT_HEADER +" PH "
                +" WHERE "
                +"PH." +dbHelper.KEY_PAYMENT_CUS_NAME+" LIKE ? AND date("
                +"PH."+ dbHelper.KEY_PAYMENT_PAYMENT_DATE + ") LIKE ? AND "
                + "PH."+dbHelper.KEY_PAYMENT_STATUS + " LIKE ? AND "
                + "PH."+dbHelper.KEY_PAYMENT_CUS_CODE +" LIKE ? " +
                "ORDER BY datetime(" +"PH."+ dbHelper.KEY_PAYMENT_CREATED_DATE + ") DESC";

        Cursor c = db.rawQuery(selectQuery,  new String[]{"%" + _filterCustomerName + "%",
                                                            "%" + _filterPaymentDate + "%",
                                                            "%" + _filterStatus + "%",
                                                            "%" + _filterCustomerCode + "%"});

        if (c.moveToFirst()) {
            do {
                PaymentHeader paymentHeader = new PaymentHeader();
                paymentHeader.setPaymentNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_NO)));
                paymentHeader.setModuleId(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_MODULE_ID)));
                paymentHeader.setCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CUS_CODE)));
                paymentHeader.setCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CUS_NAME)));
                paymentHeader.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_DRIVER_CODE)));
                paymentHeader.setSalesPersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_SALES_PERSON_CODE)));
                paymentHeader.setTotalAmount(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_TOTAL_AMOUNT)));
                paymentHeader.setPaymentDate(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_PAYMENT_DATE)));
                paymentHeader.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CREATED_BY)));
                paymentHeader.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CREATED_DATE)));
                paymentHeader.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LAST_MODIFIED_BY)));
                paymentHeader.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LAST_MODIFIED_DATE)));

                paymentHeader.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_EXTERNAL_DOCUMENT_NO)));
                paymentHeader.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_STATUS)));
                paymentHeader.setReferenceAmount(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_REFERENCE_AMOUNT)));

                paymentHeaderList.add(paymentHeader);
            } while (c.moveToNext());
        }

        //update cash and check amount
        if(!paymentHeaderList.isEmpty())
        {
            for(int i=0; i<paymentHeaderList.size(); i++)
            {
               List<PaymentLine> paymnetLineList = new ArrayList<PaymentLine>();

                PaymentHeader ph = paymentHeaderList.get(i);

                PaymentLineDbHandler plDb = new PaymentLineDbHandler(context);
                plDb.open();
                paymnetLineList = plDb.getAllPaymentLinesByPaymentNo(ph.getPaymentNo());
                plDb.close();

                if(!paymnetLineList.isEmpty())
                {
                    for(PaymentLine pl:paymnetLineList)
                    {
                        if(pl.getPaymentType().equals(
                                context.getResources().getString(R.string.PaymentTypeCash)))
                        {
                            ph.setCashAmount(String.format("%.2f", pl.getAmount()));
                            paymentHeaderList.set(i,ph);
                        }
                        else if(pl.getPaymentType().equals(
                                context.getResources().getString(R.string.PaymentTypeCheque)))
                        {
                            ph.setChequeAmount(String.format("%.2f", pl.getAmount()));
                            paymentHeaderList.set(i,ph);
                        }
                    }
                }
            }
        }

        c.close();
        return paymentHeaderList;
    }

    public List<PaymentHeader> getAllPaymentHeadersForLDS(String customerName,String paymentDate, String status, String customerCode) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String _filterCustomerName = customerName.isEmpty() ? "" : customerName;
        String _filterCustomerCode = customerCode.isEmpty() ? "" : customerCode;
        String _filterPaymentDate ="";
        String _filterStatus="";

        if (status.equals("Pending")) {
            _filterStatus = "0";
        } else if (status.equals("Confirm")) {
            _filterStatus = "1";
        } else {
            _filterStatus = "";
        }

        if (paymentDate.isEmpty()) {

            Date dateobj = new Date();
            _filterPaymentDate = df.format(dateobj).toString();
        } else {
            _filterPaymentDate = paymentDate;
        }

        List<PaymentHeader> paymentHeaderList = new ArrayList<PaymentHeader>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_PAYMENT_HEADER
                +" WHERE "
                + dbHelper.KEY_PAYMENT_MODULE_ID + " = ? AND " +
                dbHelper.KEY_PAYMENT_CUS_NAME +" LIKE ? AND date("
                + dbHelper.KEY_PAYMENT_PAYMENT_DATE + ") LIKE ? AND "
                + dbHelper.KEY_PAYMENT_STATUS + " LIKE ? AND "
                + dbHelper.KEY_PAYMENT_CUS_CODE +" LIKE ? " +
                "ORDER BY datetime(" + dbHelper.KEY_PAYMENT_CREATED_DATE + ") DESC";

        Cursor c = db.rawQuery(selectQuery,  new String[]{"2",
                "%" + _filterCustomerName + "%",
                "%" + _filterPaymentDate + "%",
                "%" + _filterStatus + "%",
                "%" + _filterCustomerCode + "%"});

        if (c.moveToFirst()) {
            do {
                PaymentHeader paymentHeader = new PaymentHeader();
                paymentHeader.setPaymentNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_NO)));
                paymentHeader.setModuleId(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_MODULE_ID)));
                paymentHeader.setCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CUS_CODE)));
                paymentHeader.setCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CUS_NAME)));
                paymentHeader.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_DRIVER_CODE)));
                paymentHeader.setSalesPersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_SALES_PERSON_CODE)));
                paymentHeader.setTotalAmount(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_TOTAL_AMOUNT)));
                paymentHeader.setPaymentDate(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_PAYMENT_DATE)));
                paymentHeader.setCreatedBy(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CREATED_BY)));
                paymentHeader.setCreatedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CREATED_DATE)));
                paymentHeader.setLastModifiedBy(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LAST_MODIFIED_BY)));
                paymentHeader.setLastModifiedDateTime(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LAST_MODIFIED_DATE)));

                paymentHeader.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_EXTERNAL_DOCUMENT_NO)));
                paymentHeader.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_STATUS)));
                paymentHeader.setReferenceAmount(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_REFERENCE_AMOUNT)));

                paymentHeaderList.add(paymentHeader);
            } while (c.moveToNext());
        }

        c.close();
        return paymentHeaderList;
    }


    // Getting Payment Header Count
    public int getPaymentHeaderCount() {
        db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + dbHelper.TABLE_PAYMENT_HEADER;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public boolean deletePaymentHeader(String code) {
        boolean success = false;

        if (isPaymentHeaderExist(code)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_PAYMENT_HEADER, dbHelper.KEY_PAYMENT_NO + "=?", new String[]{String.valueOf(code)});
            success = !isPaymentHeaderExist(code);
        } else {
            success = true;
        }
        return success;
    }

    public PaymentHeader getPaymentByDocumentNo(String documentNo) {

        PaymentHeader payment = new PaymentHeader();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_PAYMENT_HEADER + " WHERE " + dbHelper.KEY_SO_EXTERNAL_DOCUMET_NO + " = ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{documentNo});

        if (c.moveToFirst()) {
            do {
                payment.setPaymentNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_NO)));
                payment.setModuleId(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_MODULE_ID)));
                payment.setCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CUS_CODE)));
                payment.setCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CUS_NAME)));
                payment.setTransferred(Boolean.valueOf(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_TRANSFERRED))));
                payment.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_DRIVER_CODE)));
                payment.setTotalAmount(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_TOTAL_AMOUNT)));
                payment.setReferenceAmount(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_REFERENCE_AMOUNT)));
                payment.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_EXTERNAL_DOCUMENT_NO)));
                payment.setStatus(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_STATUS)));

            } while (c.moveToNext());
        }

        c.close();
        return payment;
    }
    public int getPendingPaymentCount(String salsePersonCode) {
        int paymentPendingCount = 0;

        String salesPersonCode_= salsePersonCode==null?"":salsePersonCode;

        db = dbHelper.getReadableDatabase();

        String paymentPendingStatus= context.getResources().getString(R.string.SalesPaymentStatusPending);

        String countQuery = "SELECT  * FROM " + dbHelper.TABLE_PAYMENT_HEADER
                            + " WHERE "+dbHelper.KEY_PAYMENT_STATUS +" = ?"
                            + " AND "+dbHelper.KEY_PAYMENT_SALES_PERSON_CODE+" = ?";
        Cursor cursor = db.rawQuery(countQuery, new String[]{paymentPendingStatus, salesPersonCode_});
        paymentPendingCount = cursor.getCount();
        cursor.close();

        return paymentPendingCount;
    }

}
