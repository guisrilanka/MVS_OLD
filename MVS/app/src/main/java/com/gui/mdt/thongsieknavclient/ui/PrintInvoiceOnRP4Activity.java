package com.gui.mdt.thongsieknavclient.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.seanzor.prefhelper.SharedPrefHelper;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.rp4.ConnectionBase;
import com.gui.mdt.thongsieknavclient.rp4.Connection_Bluetooth;
import com.gui.mdt.thongsieknavclient.rp4.DocumentDPL;
import com.gui.mdt.thongsieknavclient.rp4.ParametersDPL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import android.graphics.Color;

public class PrintInvoiceOnRP4Activity extends AppCompatActivity implements Runnable {

    NavClientApp mApp;
    SalesOrder mTempSalesOrder;
    List<SalesOrderLine> mSalesOrderLineList;
    private String mDeliveryDate = "", siNo = "";
    private Bundle mExtras;
    int mTotalNoOfItems = 0, mNoOfPrintouts = 0;
    private ProgressDialog mProgressDialog;

    GenerateInvoiceReportTask generateInvoiceReportTask;
    Customer mCustomer;

    //Document and Parameter Objects
    private DocumentDPL docDPL;
    private ParametersDPL paramDPL;
//    private DocumentLP docLP;

    byte[] printData = {0};

    ConnectionBase conn = null;
    SharedPreferences mDefaultSharedPreferences;
    SharedPrefHelper mPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (NavClientApp) getApplicationContext();

        mExtras = getIntent().getExtras();
        if (mExtras.containsKey(getResources().getString(R.string.sales_order_jason_obj))) {
            String mObjAsJson = mExtras.getString(getResources().getString(R.string.sales_order_jason_obj));
            mTempSalesOrder = SalesOrder.fromJson(mObjAsJson);

            mSalesOrderLineList = mTempSalesOrder.getLineItems();
            mDeliveryDate = mTempSalesOrder.getOrderDate();

            mCustomer = getCustomer(mTempSalesOrder.getSelltoCustomerNo());
        }
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefHelper = new SharedPrefHelper(getResources(), mDefaultSharedPreferences);
        mProgressDialog = new ProgressDialog(PrintInvoiceOnRP4Activity.this);

        mNoOfPrintouts = getCountOfCopies();

        generateInvoiceReportTask = new GenerateInvoiceReportTask();
        generateInvoiceReportTask.execute((Void) null);

    }

    private class GenerateInvoiceReportTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                int count = 0;
                print();
               /* while (count < mNoOfPrintouts) {
                    print();
                    count++;
                }*/
            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            finish();
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
            finish();
        }
    }

    private int getCountOfCopies() {
        int count = 2;
        try {
            SharedPreferences mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPrefHelper mPrefHelper = new SharedPrefHelper(getResources(), mDefaultSharedPreferences);
            String mCountString = mPrefHelper.getString(R.string.pref_no_of_si_printouts_key);

            if (mCountString.equals("")) {
                count = 2;
            } else {
                count = Integer.parseInt(mCountString);
            }

            if (count > 2) {
                count = 2;
            }

        } catch (Exception ee) {
            Log.e("SharedPreferences", "Error getting copies count!");
            count = 2;
        }
        return count;
    }


    private void print() {

        //check device type

        try {


            printData = new byte[]{0};
            docDPL = new DocumentDPL();
            paramDPL = new ParametersDPL();

//            docLP = new DocumentLP("!");

            String header,
                    summeryDetails,
                    clientSignature,
                    tableHeader,
                    tableContent = "",
                    tableRowSpaceing,
                    printCommand;
            int commanWidth = 15;
            int fontSize = 10;

            //setting si no
            String value = new String(mTempSalesOrder.getSINo());
            String[] siNoParts = value.split("-");
            if (siNoParts.length > 2) {
                siNo = siNoParts[0] + "-" + siNoParts[1];
            } else {
                siNo = mTempSalesOrder.getSINo();
            }

            mTotalNoOfItems = 0;
            int dyanamicMediaLength = 0;
            //item count
            if (mSalesOrderLineList != null) {

                if (mSalesOrderLineList.size() >= 0) {
//                    tableRowSpaceing = "%-10s %10s %11s %12s";
                    for (SalesOrderLine sol : mSalesOrderLineList) {

                        //check sales qty zero items
                        float salesQty = sol.getExchangedQty() + sol.getQuantity();

                        if (salesQty > new Float(0)) {
                            mTotalNoOfItems++;
                            dyanamicMediaLength = dyanamicMediaLength + 35;
                        }
                    }
                }
            }
            int concat = 0;

            //extra media length reserved for the invoice barcode (Code128)
            //TODO: gate on mCustomer.isShowBarcode() once NAV sends the flag
            int barcodeExtra = 100;

            String selectedPrinter = mPrefHelper.getString(R.string.pref_select_printer_key);
            if (selectedPrinter.equals(getResources().getString(R.string.printer_honeywell_rp4_old))) {
                concat = (0 + dyanamicMediaLength + barcodeExtra) * 1;
            }else{
                //900 base covers the redesigned layout without the bank remittance block
                //(header+logo ~1035 dots for a 2 item invoice); add ~200 when the bank block is enabled
                concat = (900 + dyanamicMediaLength + barcodeExtra) * 1;
            }


            int length = (int) (Math.log10(concat) + 1);

            String mediaLength;
            if (length == 3) {
                mediaLength = "0" + concat;
            } else {
                mediaLength = concat + "";
            }

            String sDPL = String.format("%cc" + mediaLength + "\r\n", 2);

            docDPL.setEnableAdvanceFormatAttribute(true);


            int count = 0;
            int row = 0;

//            while (count < mNoOfPrintouts) { //print no.of copies according to settings

//            docDPL.writeTextInternalSmooth("", fontSize, row, 100, paramDPL);

                if(count >= 1){
                    row += 180;
                    docDPL.writeTextInternalSmooth("", 24, row, 100, paramDPL);
                }


            row += 20;
            docDPL.writeTextInternalSmooth("Client Signature / Company Chop",
                    fontSize, row, 100, paramDPL);

            //signature
            row += 20;
            docDPL.writeTextInternalSmooth("-----------------------------------------------------",
                    fontSize, row, 100, paramDPL);

            //bank remittance details block (PayNow QR at left, account details at right)
            //disabled for now - uncomment when the client wants bank details + QR on the invoice
            //(also add paynow_qr.png to assets and increase the media length base by ~200)
            /*
            row += 60; //signing space above the signature line
            int bankBaseRow = row;
            boolean qrPrinted = false;
            try {
                Bitmap qrImage = BitmapFactory.decodeStream(getAssets().open("paynow_qr.png"));
                docDPL.writeImage(qrImage, bankBaseRow, 15, paramDPL);
                qrPrinted = true;
            } catch (Exception e) {
                Log.e("PRINT_RP4", "paynow_qr.png missing in assets - QR skipped: " + e);
            }
            docDPL.writeTextInternalSmooth("Swift Code       : UOVBSGSG",
                    fontSize, row, 130, paramDPL);
            row += 18;
            docDPL.writeTextInternalSmooth("Branch Code      : 001",
                    fontSize, row, 130, paramDPL);
            row += 18;
            docDPL.writeTextInternalSmooth("Account No.      : 4513064807",
                    fontSize, row, 130, paramDPL);
            row += 18;
            docDPL.writeTextInternalSmooth("Bank Name        : United Overseas Bank Limited",
                    fontSize, row, 130, paramDPL);
            row += 18;
            docDPL.writeTextInternalSmooth("Beneficiary Name : TSG Food Pte. Ltd.",
                    fontSize, row, 130, paramDPL);
            row += 22;
            paramDPL.setIsBold(true);
            docDPL.writeTextInternalSmooth("BANK REMITTANCE DETAILS",
                    12, row, 130, paramDPL);
            paramDPL.setIsBold(false);
            if (qrPrinted) {
                //make sure the next line clears the QR image height
                row = Math.max(row, bankBaseRow + 140);
            }
            */

            row += 200; //signing space above the signature line
            docDPL.writeTextInternalSmooth("The above mentioned goods in good order and condition.",
                    fontSize, row, commanWidth, paramDPL);

            //footnote
            row += 20;
            docDPL.writeTextInternalSmooth("Received from TSG Food Pte. Ltd.",
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

            paramDPL.setIsBold(false);
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
//                    tableRowSpaceing = "%-10s %10s %11s %12s";
                    for (SalesOrderLine sol : mSalesOrderLineList) {

                        //check sales qty zero items
                        float salesQty = sol.getExchangedQty() + sol.getQuantity();

                        if (salesQty > new Float(0) || sol.isExchangeItem()) {

                            String itemUOM = sol.getUnitofMeasure() == null ? "" : sol.getUnitofMeasure();
                            String exchQty = sol.getExchangedQty() == 0f ? ""
                                    : String.valueOf(Math.round(sol.getExchangedQty())) + " " + itemUOM;
//                            mTotalNoOfItems++;


                            row += 20;
                            docDPL.writeTextScalable(sol.getItemCrossReferenceNo(),
                                    "01", row, 40, paramDPL);
                            docDPL.writeTextScalable(Math.round(sol.getQuantity()) + " " + itemUOM,
                                    "01", row, 130, paramDPL);
                            docDPL.writeTextScalable(exchQty,
                                    "01", row, 210, paramDPL);
                            docDPL.writeTextScalable(String.format("%.2f", sol.getUnitPrice()),
                                    "01", row, 295, paramDPL);

                            docDPL.writeTextScalable(String.format("%.2f", sol.getLineAmount()),
                                    "01", row, 352, paramDPL);

                            row += 14;
                            docDPL.writeTextScalable(sol.getNo() + " " + sol.getItemCrossReferenceDescription(),
                                    "01", row, commanWidth, paramDPL);

                        }
                    }
                }
            }

            //table lower line
            row += 15;
            docDPL.writeTextInternalSmooth(
                    ".........................................................................................................",
                    fontSize, row, commanWidth, paramDPL);

            //table header column names, positioned to line up with the item row columns
            row += 15;
            docDPL.writeTextInternalSmooth("Item", fontSize, row, commanWidth, paramDPL);
            docDPL.writeTextInternalSmooth("Qty Billed", fontSize, row, 125, paramDPL);
            docDPL.writeTextInternalSmooth("Qty Exch.", fontSize, row, 205, paramDPL);
            docDPL.writeTextInternalSmooth("U.Price($)", fontSize, row, 275, paramDPL);
            docDPL.writeTextInternalSmooth("Total($)", fontSize, row, 350, paramDPL);

            //table upper line
            row += 20;
            docDPL.writeTextInternalSmooth(
                    "......................................................................................................",
                    fontSize, row, commanWidth, paramDPL);

            //delivery by + mobile no (mobile is embedded after the last '-' of the display name)
            String deliveryBy = mApp.getCurrentUserDisplayName() == null ? "" : mApp.getCurrentUserDisplayName();
            String driverMobile = "";
            int dashIdx = deliveryBy.lastIndexOf('-');
            if (dashIdx > 0 && deliveryBy.substring(dashIdx + 1).trim().matches("\\d{7,}")) {
                driverMobile = deliveryBy.substring(dashIdx + 1).trim();
                deliveryBy = deliveryBy.substring(0, dashIdx).trim();
            }

            paramDPL.setIsBold(true);
            if (!driverMobile.isEmpty()) {
                row += 20;
                docDPL.writeTextInternalSmooth("Mobile No : " + driverMobile,
                        fontSize, row, commanWidth, paramDPL);
            }

            row += 20;
            docDPL.writeTextInternalSmooth("Delivery By: " + deliveryBy,
                    fontSize, row, commanWidth, paramDPL);

            if (siNoParts.length == 3) {
                row += 20;
                docDPL.writeTextInternalSmooth("Revision No : " + "0" + siNoParts[2],
                        fontSize, row, commanWidth, paramDPL);
            }

            //Postal Code
            row += 20;
            docDPL.writeTextInternalSmooth(mCustomer.getPostalCode(),
                    fontSize, row, 50, paramDPL);

                List<String> arr = splitString(mTempSalesOrder.getSelltoAddress(), 40);
                Collections.reverse(arr);
                for (String s : arr) {
                    row += 20;
                    docDPL.writeTextScalable(s,
                            "00", row, 50, paramDPL);
                }

            row += 20;
            docDPL.writeTextInternalSmooth("Address :",
                    fontSize, row, commanWidth, paramDPL);

            //po no(External Document no)
            if (mTempSalesOrder.getExternalDocumentNo() != null) {
                row += 15;
                docDPL.writeTextInternalSmooth("PO No : " + mTempSalesOrder.getExternalDocumentNo(),
                        fontSize, row, commanWidth, paramDPL);
            }

            //Ship to customer (code, with customer name on the line below)
            row += 20;
            docDPL.writeTextInternalSmooth(mTempSalesOrder.getSelltoCustomerName(),
                    fontSize, row, 50, paramDPL);
            row += 20;
            docDPL.writeTextInternalSmooth("Ship To : " + mTempSalesOrder.getSelltoCustomerNo(),
                    fontSize, row, commanWidth, paramDPL);

            //Bill to customer (code, with customer name on the line below)
            String billToNo = mCustomer.getBillToCustomerNo() == null ? "" : mCustomer.getBillToCustomerNo();
            String billToName = mCustomer.getBillToCustomerName() == null ? "" : mCustomer.getBillToCustomerName();
            row += 20;
            docDPL.writeTextInternalSmooth(billToName,
                    fontSize, row, 50, paramDPL);
            row += 20;
            docDPL.writeTextInternalSmooth("Bill To : " + (billToNo.trim().isEmpty() ? billToName : billToNo),
                    fontSize, row, commanWidth, paramDPL);

            //delivery date
            row += 20;
            docDPL.writeTextInternalSmooth("Delivery Date : " + dateConverter(mTempSalesOrder.getShipmentDate()),
                    fontSize, row, commanWidth, paramDPL);

            // invoice no.
            row += 20;
            docDPL.writeTextInternalSmooth("Invoice No: " + siNo,
                    18, row, commanWidth, paramDPL);

            //suplier code (only when the customer has a reference no)
            String supplierCode = mCustomer.getCustomerReferenceNo() == null ? "" : mCustomer.getCustomerReferenceNo();
            if (!supplierCode.trim().isEmpty()) {
                row += 30;
                docDPL.writeTextInternalSmooth("Supplier Code : " + supplierCode,
                        fontSize, row, commanWidth, paramDPL);
            }

            //invoice barcode (Code128) - printed directly below the TAX INVOICE title when enabled.
            //uses the printer's native DPL barcode record ("e" = Code128, no human readable text);
            //image download (writeImage) is not rendered by these RP4 units, so a bitmap barcode never prints.
            //TODO: gate on mCustomer.isShowBarcode() once NAV sends the flag
            row += 25;
            paramDPL.setWideBarWidth(2);
            paramDPL.setNarrowBarWidth(2);
            paramDPL.setSymbolHeight(60); //barcode is 60 dots tall, drawn upwards from 'row'
            docDPL.writeBarCode("e", siNo, row, 130, paramDPL); //col 130 centers the ~310 dot wide barcode on 4in media
            Log.d("DPL_BARCODE", "Code128 record added: data=" + siNo + " row=" + row);
            row += 30; //clearance so the title clears the top of the barcode

            //title
            row += 40;

            docDPL.writeTextInternalSmooth("TAX INVOICE", 24, row, 100, paramDPL);

            //LOGO print
            row += 50;
            Bitmap anImage = BitmapFactory.decodeStream(getAssets().open("logo3.png"));
            docDPL.writeImage(anImage, row, 60, paramDPL);
//                row += 50;
//                if (MvsSalesOrderActivity.qrScanHeadModels != null && MvsSalesOrderActivity.qrScanHeadModels.size() > 0) {
//                    Gson gson = new Gson();
//                    for (QrScanHeadModel model : MvsSalesOrderActivity.qrScanHeadModels) {
//                        QrScanHeadModel headModel = model;
//                        String barcodeDetails = gson.toJson(headModel);
//                        QRGEncoder qrgEncoder = new QRGEncoder(barcodeDetails, null, QRGContents.Type.TEXT, 50);
//                        qrgEncoder.setColorBlack(Color.BLACK);
//                        qrgEncoder.setColorWhite(Color.WHITE);
//                        try {
//                            // Getting QR-Code as Bitmap
//                            Bitmap bitmap = qrgEncoder.getBitmap();
//                            // Setting Bitmap to ImageView
//                            docDPL.writeImage(bitmap, row, 60, paramDPL);
////                            mPrinter.printImage(bitmap, getApplicationContext());
//                        } catch (Exception e) {
//                            Log.v("BARCODE_GENERATE", e.toString());
//                            Toast.makeText(getApplicationContext(), "BARCODE_GENERATE Issue"+e.toString(),
//                                    Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                }


                        count++;

//                    }

            printData = new byte[docDPL.getDocumentData().length + sDPL.length()];

            System.arraycopy(sDPL.getBytes(), 0, printData, 0, sDPL.getBytes().length);
            System.arraycopy(docDPL.getDocumentData(), 0, printData, sDPL.getBytes().length, docDPL.getDocumentData().length);

//            printData =  docDPL.getDocumentData();

            logDplForDebug(mediaLength);

            new Thread(PrintInvoiceOnRP4Activity.this, "PrintingTask").start();


        } catch (Exception ex) {
            Toast.makeText(PrintInvoiceOnRP4Activity.this, ex.getMessage(), Toast.LENGTH_SHORT)
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
                    bytesToWrite = (remainingBytes);

                //Send data, 1024 bytes at a time until all data sent
                conn.write(printData, bytesWritten, bytesToWrite);
                bytesWritten += bytesToWrite;
                remainingBytes = remainingBytes - bytesToWrite;
                Thread.sleep(100);
            }

            //signals to close connection

            conn.close();

//            if (mProgressDialog != null && mProgressDialog.isShowing())
//            mProgressDialog.dismiss();

        } catch (Exception e) {
            //signals to close connection
//            if (mProgressDialog != null && mProgressDialog.isShowing())
//            mProgressDialog.dismiss();

            if (conn != null)
                conn.close();
            e.printStackTrace();

        }
    }


    //writes the generated DPL label commands (image binary excluded) to the app log file,
    //so remote test prints can be verified without adb access
    private void logDplForDebug(String mediaLength) {
        try {
            org.apache.log4j.Logger log = com.gui.mdt.thongsieknavclient.utils.Log4jHelper.getLogger();
            String dpl = new String(printData, "ISO-8859-1");
            int start = -1; //label section begins at STX (2) followed by L (76)
            for (int i = 0; i + 1 < dpl.length(); i++) { if (dpl.charAt(i) == 2 && dpl.charAt(i + 1) == 76) { start = i; break; } }
            int end = dpl.lastIndexOf("E\r\n");
            String label = (start >= 0 && end > start) ? dpl.substring(start, end + 3) : "(label section not found)";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < label.length(); i++) {
                char ch = label.charAt(i);
                if (ch == 2) sb.append("<STX>");
                else if (ch == '\r') continue;
                else if (ch == '\n') sb.append(" | ");
                else if (ch < 32 || ch > 126) sb.append(String.format(Locale.US, "<%02X>", (int) ch));
                else sb.append(ch);
            }
            log.info("PRINT_RP4_DPL: ver=native-barcode-v3 siNo=" + siNo
                    + " mediaLength=" + mediaLength
                    + " totalBytes=" + printData.length
                    + " label=" + sb);
        } catch (Exception e) {
            Log.e("PRINT_RP4_DPL", e.toString());
        }
    }

    public Customer getCustomer(String cusCode) {
        Customer customerObj;
        CustomerDbHandler cDb = new CustomerDbHandler(getApplicationContext());
        cDb.open();
        customerObj = cDb.getCustomerByCustomerCode(cusCode);
        cDb.close();

        return customerObj;
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

    @Override
    protected void onResume() {
        super.onResume();
        //finish();
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
}
