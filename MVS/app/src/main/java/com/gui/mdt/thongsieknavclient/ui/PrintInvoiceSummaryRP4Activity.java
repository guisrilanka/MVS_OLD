package com.gui.mdt.thongsieknavclient.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.rp4.ConnectionBase;
import com.gui.mdt.thongsieknavclient.rp4.Connection_Bluetooth;
import com.gui.mdt.thongsieknavclient.rp4.DocumentDPL;
import com.gui.mdt.thongsieknavclient.rp4.ParametersDPL;
import com.itextpdf.text.Font;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GUISL-NB05 on 12/29/2017.
 */

public class PrintInvoiceSummaryRP4Activity extends AppCompatActivity implements Runnable {

    NavClientApp mApp;
    List<SalesOrder> salesOrderList;
    private String
            mFileName = "Invoice",
            mFilePath = "MyInvoices";
    private Bundle mExtras;
    Font mPlainFont,
            mPlainFontCompanyName,
            mPlainFontCompanyDetail,
            mPlainFontReportTitle,
            mPlainFontFilterDetail,
            mPlainFontColumnHeader,
            mPlainFontTableRegistry,
            mPlainFontInvoiceNo;
    private String mFilterDate, mFilterSalesPersonCode;
    private GetSalesOrderListTask getSalesOrderListTask = null;


    //Document and Parameter Objects
    private DocumentDPL docDPL;
    private ParametersDPL paramDPL;
    byte[] printData = {0};

    ConnectionBase conn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (NavClientApp) getApplicationContext();

        mPlainFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
        mPlainFontCompanyName = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
        mPlainFontCompanyDetail = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        mPlainFontReportTitle = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD | Font.UNDERLINE);
        mPlainFontFilterDetail = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
        mPlainFontColumnHeader = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
        mPlainFontTableRegistry = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        mPlainFontInvoiceNo = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);


        mFileName = mFileName + "-"
                + mApp.getmCurrentSalesPersonCode() + "-"
                + DateTime.now().toString()
                .replace("-", "")
                .replace(":", "")
                .replace(".", "")
                .replace("+", "")
                + ".pdf";

        //mPdfFile = new File(getExternalFilesDir(mFilePath), mFileName);

        mExtras = getIntent().getExtras();
        mFilterDate = mExtras.getString("filterCreatedDate");
        mFilterSalesPersonCode = mApp.getmCurrentSalesPersonCode();

        getSalesOrderListTask = new GetSalesOrderListTask();
        //getSalesOrderListTask.delegate=PrintInvoiceSummaryActivity.this;
        getSalesOrderListTask.execute((Void) null);


    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    private void printInvoiceSummaryReport() {

        try {
            String date = "", tableColumnSpacing = "", printCommand = "";
            float totalSalesAmt = 0f;

            DateFormat df = new SimpleDateFormat("dd  MMM  yyyy");

            if (mFilterDate != null) {
                if (!mFilterDate.equals("")) {
                    Date initDate
                            = new SimpleDateFormat(getApplicationContext().getResources().getString(R.string.date_format_yyyy_MM_dd)
                            , Locale.ENGLISH).parse(mFilterDate);
                    date = "for " + df.format(initDate);
                } else {
                    Date dateObj = new Date();
                    date = "for " + df.format(dateObj).toString();
                }
            } else {
                Date dateObj = new Date();
                date = "for " + df.format(dateObj).toString();
            }

            printData = new byte[]{0};
            docDPL = new DocumentDPL();
            paramDPL = new ParametersDPL();

            int commanWidth = 0;
            int fontSize = 8;
            int row = 0;

            int dyanamicMediaLength = 0;
            int mTotalNoOfItems = 0;
            if (salesOrderList.size() > 0) {
                for (SalesOrder so :
                        salesOrderList) {

                    if (so.getAmountIncludingVAT() > 0f) {
                        totalSalesAmt = totalSalesAmt + so.getAmountIncludingVAT();
                        mTotalNoOfItems++;

                    }
                }
            }
            dyanamicMediaLength = mTotalNoOfItems * 20;
            int concat = 350 + dyanamicMediaLength;

            int length = (int) (Math.log10(concat) + 1);

            String mediaLength;
            if (length == 3) {
                mediaLength = "0" + concat;
            } else {
                mediaLength = concat + "";
            }

            String sDPL = String.format("%cc" + mediaLength + "\r\n", 2);


            //total lower line
            row += 20;
            docDPL.writeTextInternalSmooth("===========================================================================",
                    fontSize, row, commanWidth, paramDPL);


            //total content
            row += 20;
            docDPL.writeTextInternalSmooth("Total Sales:",
                    fontSize, row, commanWidth, paramDPL);
            docDPL.writeTextInternalSmooth("$ "
                            + String.format("%12.2f", totalSalesAmt),
                    fontSize, row, 335, paramDPL);
            //total upper line
            row += 20;
            docDPL.writeTextInternalSmooth("===========================================================================",
                    fontSize, row, commanWidth, paramDPL);

            paramDPL.setIsUnicode(true);
            paramDPL.setDBSymbolSet(ParametersDPL.DoubleByteSymbolSet.Unicode);

            //table content
            if (salesOrderList.size() > 0) {
                Collections.reverse(salesOrderList);
                for (SalesOrder so :
                        salesOrderList) {

                    if (so.getAmountIncludingVAT() > 0f) {
                        List<String> arr = splitString(so.getSelltoCustomerName(), 45);
//                        Collections.reverse(arr);
                        int nameLineCount = 1;
                        int currentRow = row;
                        if (arr.size() == 1) {
                            row += 20;
                        }else{
                            row += 40;
                        }

                        docDPL.writeTextInternalSmooth(so.getSINo(),
                                8, row, 0, paramDPL);


                        for (String s : arr) {
                            if (nameLineCount == 1) {
                                docDPL.writeTextInternalSmooth(s,
                                        8, row, 80, paramDPL);
                            } else {
                                currentRow += 15;
                                docDPL.writeTextInternalSmooth(s,
                                        8, currentRow, 80, paramDPL);
                            }
                            nameLineCount++;

                        }

                        docDPL.writeTextInternalSmooth(String.format("%.2f", so.getAmountIncludingVAT()),
                                8, row, 365, paramDPL);

                        totalSalesAmt = totalSalesAmt + so.getAmountIncludingVAT();
                    }
                }
            }

            //table lower line
            row += 15;
            docDPL.writeTextInternalSmooth(".............................................................................................................................................",
                    fontSize, row, commanWidth, paramDPL);

            //table header
//            tableColumnSpacing = "%-14s %-45s %10s";
            row += 15;
            docDPL.writeTextInternalSmooth(
                    "Invoice",
                    fontSize, row, 5, paramDPL);
            docDPL.writeTextInternalSmooth("Customer",
                    fontSize, row, 80, paramDPL);
            docDPL.writeTextInternalSmooth("Sales($)",
                    fontSize, row, 360, paramDPL);

            //table upper line
            row += 15;
            docDPL.writeTextInternalSmooth(".............................................................................................................................................",
                    fontSize, row, commanWidth, paramDPL);

            //salesMan
            row += 15;
            docDPL.writeTextInternalSmooth("Vansales : " + mApp.getCurrentUserDisplayName(),
                    fontSize, row, commanWidth, paramDPL);

            //date
            row += 20;
            paramDPL.setIsBold(true);
            paramDPL.setFontHeight(12);
            paramDPL.setFontWidth(12);
            docDPL.writeTextScalable(date,
                    "00", row, 140, paramDPL);

            //title
            row += 20;
            docDPL.writeTextScalable("INVOICE SUMMARY",
                    "00", row, 130, paramDPL);


            printData = new byte[docDPL.getDocumentData().length + sDPL.length()];

            System.arraycopy(sDPL.getBytes(), 0, printData, 0, sDPL.getBytes().length);
            System.arraycopy(docDPL.getDocumentData(), 0, printData, sDPL.getBytes().length, docDPL.getDocumentData().length);


            new Thread(PrintInvoiceSummaryRP4Activity.this, "PrintingTask").start();

        } catch (Exception ee) {
            Toast.makeText(PrintInvoiceSummaryRP4Activity.this, ee.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }


    }

    @Override
    public void run() {
        //Connection
        try {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice mBtDevice = btAdapter.getBondedDevices().iterator().next();

            conn = null;
            Looper.prepare();
            conn = Connection_Bluetooth.createClient(mBtDevice.getAddress(), false);

            //Open bluetooth socket
            if (!conn.getIsOpen()) {
                conn.open();
            }

            int bytesWritten = 0;
            int bytesToWrite = 1024;
            int totalBytes = printData.length;
            int remainingBytes = totalBytes;
            while (bytesWritten < totalBytes) {
                if (remainingBytes < bytesToWrite)
                    bytesToWrite = remainingBytes;

                //Send data, 1024 bytes at a time until all data sent
                conn.write(printData, bytesWritten, bytesToWrite);
                bytesWritten += bytesToWrite;
                remainingBytes = remainingBytes - bytesToWrite;
                Thread.sleep(100);
            }

            //signals to close connection
            conn.close();

        } catch (Exception e) {
            //signals to close connection
            if (conn != null)
                conn.close();
            e.printStackTrace();

        }
    }


    private void getSalesInvoiceSummaryList(String mFilterDate) {
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(PrintInvoiceSummaryRP4Activity.this);
        dbAdapter.open();
        salesOrderList = dbAdapter.getSalesInvoiceSummaryList(mFilterDate);
        dbAdapter.close();
    }

    private class GetSalesOrderListTask extends AsyncTask<Void, Void, Boolean> {

        public AsyncResponse delegate = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getSalesInvoiceSummaryList(mFilterDate);


            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            //generateInvoiceSummaryPDF();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            SyncStatus syncStatus = new SyncStatus();
            syncStatus.setScope("");

            if (success) {
                try {

                    printInvoiceSummaryReport();
                    emailInvoiceSummaryReport();
                    //mProgressDialog.dismiss();
                } catch (Exception ex) {
                }
                //mProgressDialog.dismiss();
            } else {
                //mProgressDialog.dismiss();
                Toast.makeText(PrintInvoiceSummaryRP4Activity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            //mProgressDialog.dismiss();
        }
    }

    public void emailInvoiceSummaryReport() {
        if (isNetworkConnected()) {
            try {
                Intent intent = new Intent(PrintInvoiceSummaryRP4Activity.this
                        , EmailInvoiceSummaryActivity.class);
                intent.putExtra("filterCreatedDate", mFilterDate);
                startActivity(intent);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        } else {
            Toast.makeText(mApp, getResources().getString(R.string
                    .notification_msg_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static List<String> splitString(String msg, int lineSize) {
        List<String> res = new ArrayList<>();

        Pattern p = Pattern.compile("\\b.{1," + (lineSize - 1) + "}\\b\\W?");
        Matcher m = p.matcher(msg);

        while (m.find()) {
            System.out.println(m.group().trim());   // Debug
            res.add(m.group());
        }
        return res;
    }


}


