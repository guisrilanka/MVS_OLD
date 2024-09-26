package com.gui.mdt.thongsieknavclient.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gui.mdt.thongsieknavclient.rp4.ConnectionBase;
import com.gui.mdt.thongsieknavclient.rp4.Connection_Bluetooth;
import com.gui.mdt.thongsieknavclient.rp4.DocumentDPL;
import com.gui.mdt.thongsieknavclient.rp4.ParametersDPL;

public class PrintSalesOrderDraftRP4Activity extends AppCompatActivity implements Runnable  {

    NavClientApp mApp;
    SalesOrder mTempSalesOrder;
    List<SalesOrderLine> mSalesOrderLineList;
    private Bundle mExtras;
    int mTotalNoOfItems = 0;

    GenerateDraftReportTask mGenerateDraftReportTask;
    Customer mCustomer;

    //Document and Parameter Objects
    private DocumentDPL docDPL;
    private ParametersDPL paramDPL;
    byte[] printData = {0};

    ConnectionBase conn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (NavClientApp) getApplicationContext();

        mExtras = getIntent().getExtras();
        if (mExtras.containsKey(getResources().getString(R.string.sales_order_jason_obj))) {
            String mObjAsJson = mExtras.getString(getResources().getString(R.string.sales_order_jason_obj));
            mTempSalesOrder = SalesOrder.fromJson(mObjAsJson);

            mSalesOrderLineList = mTempSalesOrder.getLineItems();
            mCustomer = getCustomer(mTempSalesOrder.getSelltoCustomerNo());
        }

        mGenerateDraftReportTask = new GenerateDraftReportTask();
        mGenerateDraftReportTask.execute((Void) null);
    }

    private class GenerateDraftReportTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                printDraft();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        }

        @Override
        protected void onCancelled() {
        }
    }

    private void printDraft() {

                try {
                    printData = new byte[]{0};
                    docDPL = new DocumentDPL();
                    paramDPL = new ParametersDPL();

                    String tableRowSpaceing;
                    int commanWidth = 10;
                    int fontSize = 10;


                    int row = 0;
                    mTotalNoOfItems = 0;

                    int dyanamicMediaLength = 0;
                    if (mSalesOrderLineList != null) {

                        if (mSalesOrderLineList.size() >= 0) {

                            for (SalesOrderLine sol : mSalesOrderLineList) {

                                //check sales qty zero items
                                float salesQty = sol.getExchangedQty() + sol.getQuantity();

                                if (salesQty > new Float(0) && sol.getUnitPrice() > 0f) {

                                    mTotalNoOfItems++;

                                }
                            }
                        }
                    }
                    dyanamicMediaLength = mTotalNoOfItems * 40;

                    int concat = 550 + dyanamicMediaLength;

                    int length = (int) (Math.log10(concat) + 1);

                    String mediaLength;
                    if (length == 3) {
                        mediaLength = "0" + concat;
                    } else {
                        mediaLength = concat + "";
                    }

                    String sDPL = String.format("%cc" + mediaLength + "\r\n", 2);

                    docDPL.setEnableAdvanceFormatAttribute(true);


                    docDPL.writeTextInternalSmooth("", fontSize, row, 100, paramDPL);

                    row += 20;
                    docDPL.writeTextInternalSmooth("Client Signature / Company Chop",
                            fontSize, row, 100, paramDPL);

                    //signature
                    row += 20;
                    docDPL.writeTextInternalSmooth("-----------------------------------------------------",
                            fontSize, row, 100, paramDPL);

                    row += 35;
                    docDPL.writeTextInternalSmooth("The above mentioned goods in good order and condition.",
                            fontSize, row, commanWidth, paramDPL);

                    //footnote
                    row += 20;
                    docDPL.writeTextInternalSmooth("Received from DODO Marketing Pte Ltd",
                            fontSize, row, commanWidth, paramDPL);

                    paramDPL.setIsBold(true);
                    row += 30;
                    docDPL.writeTextInternalSmooth("              Grand Total:    $",
                            10, row, 150, paramDPL);

                    paramDPL.setIsBold(false);
                    docDPL.writeTextInternalSmooth(String.format("%10.2f", mTempSalesOrder.getAmountIncludingVAT()),
                            10, row, 330, paramDPL);

                    row += 20;
                    paramDPL.setIsBold(true);
                    docDPL.writeTextInternalSmooth("              GST ("+ mTempSalesOrder.getVatPercentage()+"%):       $",
                            10, row, 150, paramDPL);
                    paramDPL.setIsBold(false);
                    docDPL.writeTextInternalSmooth(String.format("%10.2f", mTempSalesOrder.getVATAmount()),
                            10, row, 330, paramDPL);

                    //summery details
                    row += 20;
                    paramDPL.setIsBold(true);
                    docDPL.writeTextInternalSmooth("              Subtotal:         $",
                            10, row, 150, paramDPL);

                    paramDPL.setIsBold(false);
                    docDPL.writeTextInternalSmooth(String.format("%10.2f", mTempSalesOrder.getAmountExcludingVAT()),
                            10, row, 330, paramDPL);

                    //show no. items
                    row += 30;
                    docDPL.writeTextInternalSmooth("* total of " + String.valueOf(mTotalNoOfItems) + " item(s) on invoice",
                            fontSize, row, commanWidth, paramDPL);

                    row += 20;
                    docDPL.writeTextInternalSmooth(
                            "...........................................................................................................",
                            fontSize, row, commanWidth, paramDPL);
                    paramDPL.setIsUnicode(true);
                    paramDPL.setDBSymbolSet(ParametersDPL.DoubleByteSymbolSet.Unicode);
                    if (mSalesOrderLineList != null) {
                        Collections.reverse(mSalesOrderLineList);
                        if (mSalesOrderLineList.size() >= 0) {
                            for (SalesOrderLine sol : mSalesOrderLineList) {

                                //check sales qty zero items
                                float salesQty = sol.getExchangedQty() + sol.getQuantity();

                                if (salesQty > new Float(0) && sol.getUnitPrice() > 0f) {

                                    String itemUOM = sol.getUnitofMeasure() == null ? "" : sol.getUnitofMeasure();
                                    String exchQty = sol.getExchangedQty() == 0f ? ""
                                            : String.valueOf(Math.round(sol.getExchangedQty())) + " " + itemUOM;
                                    mTotalNoOfItems++;



                                    row += 20;
//                                    docDPL.writeTextScalable(String.format(tableRowSpaceing
//                                            , "       " + sol.getItemCrossReferenceNo()
//                                            , String.valueOf(Math.round(sol.getQuantity())) + " " + itemUOM
//                                            , exchQty
//                                            , String.format("%.2f", sol.getUnitPrice())
//                                            , String.format("%.2f", sol.getLineAmount())),
//                                            "00", row, commanWidth, paramDPL);
                                    docDPL.writeTextScalable(sol.getItemCrossReferenceNo(),
                                            "01", row, 40, paramDPL);
                                    docDPL.writeTextScalable(Math.round(sol.getQuantity()) + " " + itemUOM,
                                            "01", row, 130, paramDPL);
                                    docDPL.writeTextScalable(exchQty,
                                            "01", row, 210, paramDPL);
                                    docDPL.writeTextScalable(String.format("%.2f", sol.getUnitPrice()),
                                            "01", row, 280, paramDPL);

                                    docDPL.writeTextScalable(String.format("%.2f", sol.getLineAmount()),
                                            "01", row, 350, paramDPL);

                                    row += 14;
                                    docDPL.writeTextScalable(sol.getNo() + " " + sol.getItemCrossReferenceDescription(),
                                            "01", row, commanWidth, paramDPL);

//                                    row += 20;
//                                    docDPL.writeTextScalable(sol.getNo() + " " + sol.getItemCrossReferenceDescription(),
//                                            "00", row, commanWidth, paramDPL);
                                }
                            }
                        }
                    }

                    //table lower line
                    row += 20;
                    docDPL.writeTextInternalSmooth(
                            ".........................................................................................................",
                            fontSize, row, commanWidth, paramDPL);

                    tableRowSpaceing = "%-20s %12s %12s %12s %12s";
                    //table header column names
                    row += 20;
                    docDPL.writeTextInternalSmooth(String.format(tableRowSpaceing
                            , "Item", "Qty Billed", "Qty Exch.", "U.Price($)", "Total($)"),
                            fontSize, row, commanWidth, paramDPL);

                    //table upper line
                    row += 30;
                    docDPL.writeTextInternalSmooth(
                            "......................................................................................................",
                            fontSize, row, commanWidth, paramDPL);

                    row += 20;
                    paramDPL.setIsBold(true);
                    docDPL.writeTextInternalSmooth("Delivery By: " + mApp.getCurrentUserDisplayName(),
                            fontSize, row, commanWidth, paramDPL);


                    //Postal Code
                    row += 20;
                    docDPL.writeTextInternalSmooth(mCustomer.getPostalCode(),
                            fontSize, row, 40, paramDPL);


                    List<String> arr = splitString(mTempSalesOrder.getSelltoAddress(), 40);
                    Collections.reverse(arr);
                    for (String s : arr) {
                        row += 20;
                        docDPL.writeTextScalable(s,
                                "00", row, 40, paramDPL);
                    }

                    row += 15;
                    docDPL.writeTextInternalSmooth(mTempSalesOrder.getSelltoCustomerName()+",",
                            8, row, 40, paramDPL);


                    //po no(External Document no)
                    if (mTempSalesOrder.getExternalDocumentNo() != null) {
                        row += 20;
                        docDPL.writeTextInternalSmooth("PO No : " + mTempSalesOrder.getExternalDocumentNo(),
                                fontSize, row, commanWidth, paramDPL);
                    }

                    //Ship to customer
                    row += 20;
                    docDPL.writeTextInternalSmooth("Ship To : " + mTempSalesOrder.getSelltoCustomerNo(),
                            fontSize, row, commanWidth, paramDPL);

                    //Bill to customer
                    row += 20;
                    docDPL.writeTextInternalSmooth("Bill To : " + mCustomer.getBillToCustomerName(),
                            fontSize, row, commanWidth, paramDPL);

                    //delivery date
                    row += 20;
                    docDPL.writeTextInternalSmooth("Delivery Date : " + dateConverter(mTempSalesOrder.getShipmentDate()),
                            fontSize, row, commanWidth, paramDPL);


                    //suplier code
                    row += 20;
                    docDPL.writeTextInternalSmooth("Supplier Code : " + mCustomer.getCustomerReferenceNo(),
                            fontSize, row, commanWidth, paramDPL);

                    //title
                    row += 40;
                    docDPL.writeTextInternalSmooth("*** DRAFT ***", 18, row, 115, paramDPL);

                    //LOGO print
//                    row += 50;
//                    Bitmap anImage = BitmapFactory.decodeStream(getAssets().open("logo.jpg"));
//                    docDPL.writeImage(anImage, row, 120, paramDPL);


                    printData = new byte[docDPL.getDocumentData().length + sDPL.length()];

                    System.arraycopy(sDPL.getBytes(), 0, printData, 0, sDPL.getBytes().length);
                    System.arraycopy(docDPL.getDocumentData(), 0, printData, sDPL.getBytes().length, docDPL.getDocumentData().length);


                    new Thread(PrintSalesOrderDraftRP4Activity.this, "PrintingTask").start();


                } catch (Exception ex) {
                    Toast.makeText(PrintSalesOrderDraftRP4Activity.this, ex.getMessage(), Toast.LENGTH_LONG)
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

    @Override
    protected void onResume() {
        super.onResume();
        finish();
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

    public String dateConverter(String mDate_) {
        String mDate = "";

        if (mDate_ != null) {
            if (!mDate_.equals("")) {
                SimpleDateFormat mSimpleDateFormat =
                        new SimpleDateFormat("EEE dd/MM/yyyy");
                try {

                    Date mInitDate
                            = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd)
                            , Locale.ENGLISH).parse(mDate_);
                    mDate = mSimpleDateFormat.format(mInitDate);

                } catch (Exception e) {
                    Log.e(getResources().getString(R.string.message_exception), e.getMessage().toString());
                }
            }
        }
        return mDate;
    }

    public Customer getCustomer(String cusCode) {
        Customer customerObj;
        CustomerDbHandler cDb = new CustomerDbHandler(getApplicationContext());
        cDb.open();
        customerObj = cDb.getCustomerByCustomerCode(cusCode);
        cDb.close();

        return customerObj;
    }
}
