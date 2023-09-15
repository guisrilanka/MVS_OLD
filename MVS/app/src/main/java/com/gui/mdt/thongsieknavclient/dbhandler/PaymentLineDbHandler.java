package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentLine;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/29/2017.
 */

public class PaymentLineDbHandler {


    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;
    NavClientApp mApp;

    public PaymentLineDbHandler(Context context) {
        this.context = context;
        this.mApp = (NavClientApp) context.getApplicationContext();
    }

    public PaymentLineDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void addNewPayment(PaymentLine paymentLine) {

        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(dbHelper.KEY_PAYMENT_LINE_NO, paymentLine.getLineNo());
        values.put(dbHelper.KEY_PAYMENT_LINE_PAYMENT_NO, paymentLine.getPaymentNo());
        values.put(dbHelper.KEY_PAYMENT_LINE_PAYMENT_TYPE, paymentLine.getPaymentType());
        values.put(dbHelper.KEY_PAYMENT_LINE_AMOUNT, String.format("%.2f", paymentLine.getAmount()));
        values.put(dbHelper.KEY_PAYMENT_LINE_REMARK, paymentLine.getRemark());
        values.put(dbHelper.KEY_PAYMENT_LINE_CHEQUE_NO, paymentLine.getChequeNo());
        values.put(dbHelper.KEY_PAYMENT_LINE_CHEQUE_DATE, paymentLine.getChequeDate());
        values.put(dbHelper.KEY_PAYMENT_LINE_CHEQUE_NAME, paymentLine.getChequeName());

        db.insert(dbHelper.TABLE_PAYMENT_LINE, null, values);
    }

    public boolean isPaymentLineExist(Integer code) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_PAYMENT_LINE + " WHERE " + dbHelper.KEY_PAYMENT_LINE_NO + " " +
                "= " + code;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    public List<PaymentLine> getAllPaymentLinesByPaymentNo(String paymentNo) {

        List<PaymentLine> paymentLineList = new ArrayList<PaymentLine>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_PAYMENT_LINE + " WHERE " + dbHelper
                .KEY_PAYMENT_LINE_PAYMENT_NO + " = ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{paymentNo});

        if (c.moveToFirst()) {
            do {
                PaymentLine paymentline = new PaymentLine();

                paymentline.setLineNo(Integer.valueOf(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_NO))));
                paymentline.setPaymentNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_PAYMENT_NO)));
                paymentline.setPaymentType(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_PAYMENT_TYPE)));
                paymentline.setAmount(Float.valueOf(Float.valueOf(c.getString(c.getColumnIndex(dbHelper
                        .KEY_PAYMENT_LINE_AMOUNT)))));
                paymentline.setRemark(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_REMARK)));
                paymentline.setChequeNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_CHEQUE_NO)));
                paymentline.setChequeDate(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_CHEQUE_DATE)));
                paymentline.setChequeName(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_CHEQUE_NAME)));

                paymentLineList.add(paymentline);
            } while (c.moveToNext());
        }

        c.close();
        return paymentLineList;
    }

    public boolean deletePaymentLine(Integer lineNo) {

        boolean success = false;

        if (isPaymentLineExist(lineNo)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_PAYMENT_LINE, dbHelper.KEY_PAYMENT_LINE_NO + "=?", new String[]{Integer.toString
                    (lineNo)});
            success = !isPaymentLineExist(lineNo);
        } else {
            success = true;
        }
        return success;
    }

    public Integer getLineNo(String paymentNo, String paymentType) {
        Integer lineNo = -1;
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_PAYMENT_LINE + " WHERE " + dbHelper
                .KEY_PAYMENT_LINE_PAYMENT_NO + " =?" +
                " AND " + dbHelper.KEY_PAYMENT_LINE_PAYMENT_TYPE + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{paymentNo, paymentType});

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                lineNo = cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_NO));
            }
        }
        cursor.close();
        return lineNo;
    }

    public List<PaymentLine> getPaymentsForUploading() {

        List<PaymentLine> paymentLineList = new ArrayList<PaymentLine>();
        db = dbHelper.getReadableDatabase();

        String confirmedStatus = "1";
        String TransferedStatus = "0";

        String selectQuery = "SELECT  PL.*"
                + " ,PH." + dbHelper.KEY_PAYMENT_DRIVER_CODE
                + " ,PH." + dbHelper.KEY_PAYMENT_SALES_PERSON_CODE
                + " ,PH." + dbHelper.KEY_PAYMENT_PAYMENT_DATE
                + " ,PH." + dbHelper.KEY_PAYMENT_EXTERNAL_DOCUMENT_NO
                + " ,PH." + dbHelper.KEY_PAYMENT_CUS_CODE
                + " ,CUS." + dbHelper.KEY_CUS_BILL_TO_NO
                + " FROM " + dbHelper.TABLE_PAYMENT_LINE + " PL "
                + " JOIN " + dbHelper.TABLE_PAYMENT_HEADER + " PH "
                + " ON " + " PL." + dbHelper.KEY_PAYMENT_LINE_PAYMENT_NO + "=" + " PH." + dbHelper.KEY_PAYMENT_NO
                + " JOIN " + dbHelper.TABLE_CUSTOMER + " CUS "
                + " ON " + " CUS." + dbHelper.KEY_CUS_CODE + "=" + " PH." + dbHelper.KEY_PAYMENT_CUS_CODE
                + " WHERE " + dbHelper.KEY_PAYMENT_STATUS + " = ?"
                + " AND " +" PL."+ dbHelper.KEY_PAYMENT_LINE_TRANSFERRED + " is null";
        Cursor c = db.rawQuery(selectQuery, new String[]{confirmedStatus});

        if (c.moveToFirst()) {
            do {
                PaymentLine paymentline = new PaymentLine();

                paymentline.setLineNo(Integer.valueOf(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_NO))));
                paymentline.setPaymentNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_PAYMENT_NO)));
                paymentline.setPaymentType(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_PAYMENT_TYPE)));
                paymentline.setAmount(Float.valueOf(Float.valueOf(c.getString(c.getColumnIndex(dbHelper
                        .KEY_PAYMENT_LINE_AMOUNT)))));
                paymentline.setRemark(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_REMARK)));
                paymentline.setChequeNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_CHEQUE_NO)));
                paymentline.setChequeDate(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_LINE_CHEQUE_DATE)));
                paymentline.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_DRIVER_CODE)));
                paymentline.setSalesPersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_SALES_PERSON_CODE)));
                paymentline.setDocumentDate(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_PAYMENT_DATE)));
                paymentline.setExternalDocumentNo(c.getString(c.getColumnIndex(dbHelper
                        .KEY_PAYMENT_EXTERNAL_DOCUMENT_NO)));
                paymentline.setCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_PAYMENT_CUS_CODE)));
                paymentline.setBillToCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_BILL_TO_NO)));

                paymentLineList.add(paymentline);
            } while (c.moveToNext());
        }

        c.close();
        return paymentLineList;

    }

    public boolean saveConfirmPayments(String paymentNo,
                                       String serverDate,
                                       String originalLineNo,
                                       String newLineNo,
                                       boolean isTransferred) {
        boolean success = false;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(dbHelper.KEY_PAYMENT_TRANSFERRED,isTransferred);
            contentValues.put(dbHelper.KEY_PAYMENT_LAST_TRANSFERRED_BY,mApp.getCurrentUserName());
            contentValues.put(dbHelper.KEY_PAYMENT_LAST_TRANSFERRED_DATE,serverDate );
            contentValues.put(dbHelper.KEY_PAYMENT_LAST_MODIFIED_BY, mApp.getCurrentUserName());
            contentValues.put(dbHelper.KEY_PAYMENT_LAST_MODIFIED_DATE, DateTime.now().toString());

            ContentValues lineContentValues = new ContentValues();
            lineContentValues.put(dbHelper.KEY_PAYMENT_LINE_NO,newLineNo);
            lineContentValues.put(dbHelper.KEY_PAYMENT_LINE_TRANSFERRED,isTransferred);
            lineContentValues.put(dbHelper.KEY_PAYMENT_LINE_LAST_TRANSFERRED_BY,mApp.getCurrentUserName());
            lineContentValues.put(dbHelper.KEY_PAYMENT_LINE_LAST_TRANSFERRED_DATE,serverDate);

            String id = String.valueOf(paymentNo);


            //update header
            if (db.update(dbHelper.TABLE_PAYMENT_HEADER, contentValues, "" + dbHelper.KEY_PAYMENT_NO + " = ?",
                    new String[]{id}) == 1)
                success = true;
            else
                success = false;

            //update lines
            if(success){
                if (db.update(dbHelper.TABLE_PAYMENT_LINE,
                        lineContentValues,
                        "" + dbHelper.KEY_PAYMENT_NO + " = ? " +" AND "+dbHelper.KEY_PAYMENT_LINE_NO+" = ?",
                        new String[]{id,originalLineNo}) == 1)
                    success = true;
                else
                    success = false;
            }
        }
        catch (Exception ex){
            success = false;
        }

        return success;
    }
}
