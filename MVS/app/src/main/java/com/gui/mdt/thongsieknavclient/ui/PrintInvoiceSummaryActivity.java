package com.gui.mdt.thongsieknavclient.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.utils.BluetoothPrinter;
import com.itextpdf.text.Font;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by GUISL-NB05 on 12/29/2017.
 */

public class PrintInvoiceSummaryActivity extends AppCompatActivity {

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
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter != null && btAdapter.getBondedDevices() != null && btAdapter.getBondedDevices().size() > 0) {
            BluetoothDevice mBtDevice = btAdapter.getBondedDevices().iterator().next();   // Get first paired device

            final BluetoothPrinter mPrinter = new BluetoothPrinter(mBtDevice);

            mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {

                @Override
                public void onConnected() {
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

                        int esc = 27;
                        int L = 69;
                        int g = 90;
                        mPrinter.btOutputStream.write(esc);
                        mPrinter.btOutputStream.write(L);
                        mPrinter.btOutputStream.write(g);

                        //title
                        mPrinter.btOutputStream.write("{PRINT,:@40,300:MF107|INVOICE SUMMARY|}".getBytes());

                        //date
                        printCommand = "{PRINT,:@30,250:MF107|" + date + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //salesMan
                        printCommand = "{PRINT,:@20,20:MF185|Vansales : " + mApp.getCurrentUserDisplayName() + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //table upper line
                        printCommand = "{PRINT,:@30,20:MF185|"
                                + "..........................................................................." + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //table header
                        tableColumnSpacing = "%-14s %-45s %10s";
                        printCommand = "{PRINT,:@10,20:MF185|" +
                                String.format(tableColumnSpacing
                                        , "Invoice", "Customer", "Sales($)") + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //table lower line
                        printCommand = "{PRINT,:@10,20:MF185|"
                                + "..........................................................................." + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //table content
                        if (salesOrderList.size() > 0) {
                            for (SalesOrder so :
                                    salesOrderList) {

                                if(so.getAmountIncludingVAT() > 0f)
                                {
                                    printCommand = "{PRINT,:@10,20:MF185|" + String.format(tableColumnSpacing
                                            , so.getSINo()
                                            , so.getSelltoCustomerName()
                                            , String.format("%.2f", so.getAmountIncludingVAT())) + "|}";
                                    mPrinter.btOutputStream.write(printCommand.getBytes());

                                    totalSalesAmt = totalSalesAmt + so.getAmountIncludingVAT();
                                }
                            }
                        }

                        //total upper line
                        printCommand = "{PRINT,:@20,20:MF185|"
                                + "===========================================================================" + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //total content
                        printCommand = "{PRINT,:@8,20:MF107|"
                                + "Total Sales:                $"
                                + String.format("%12.2f", totalSalesAmt) + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //total lower line
                        printCommand = "{PRINT,:@8,20:MF185|"
                                + "===========================================================================" + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //Change printer mode back to line print
                        mPrinter.printText("{LP}");
                        Thread.sleep(5000);
                        mPrinter.finish();

                    } catch (Exception ee) {
                        Toast.makeText(PrintInvoiceSummaryActivity.this, mPrinter.getDevice().getName().toString(), Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onFailed() {
                    Log.d("BluetoothPrinter", "Conection failed");
                }
            });
        } else {
            return;
        }


    }

    private void getSalesInvoiceSummaryList(String mFilterDate) {
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(PrintInvoiceSummaryActivity.this);
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
                Toast.makeText(PrintInvoiceSummaryActivity.this, "No data", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(PrintInvoiceSummaryActivity.this
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

}


