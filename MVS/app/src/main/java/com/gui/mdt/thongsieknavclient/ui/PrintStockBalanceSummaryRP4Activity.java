package com.gui.mdt.thongsieknavclient.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemBalancePdaDbHandler;
import com.gui.mdt.thongsieknavclient.utils.BluetoothPrinter;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.gui.mdt.thongsieknavclient.rp4.ConnectionBase;
import com.gui.mdt.thongsieknavclient.rp4.Connection_Bluetooth;
import com.gui.mdt.thongsieknavclient.rp4.DocumentDPL;
import com.gui.mdt.thongsieknavclient.rp4.ParametersDPL;

public class PrintStockBalanceSummaryRP4Activity extends AppCompatActivity implements Runnable {

    private Bundle mExtras;
    private String mFilterDate = "", mDetails = "";
    private List<ItemBalancePda> mItemBalancePdaList;
    NavClientApp mApp;
    GetItemBalances mGetItemBalances;

    //Document and Parameter Objects
    private DocumentDPL docDPL;
    private ParametersDPL paramDPL;
    byte[] printData = {0};

    ConnectionBase conn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExtras = getIntent().getExtras();
        if (mExtras.containsKey("details")) {
            mDetails = mExtras.getString("details");
        }
        //mFilterDate = mExtras.getString("filterCreatedDate");
        mApp = (NavClientApp) getApplication();

        mGetItemBalances = new GetItemBalances();
        mGetItemBalances.execute((Void) null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    private class GetItemBalances extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getItemBalancePdaList();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                try {

                    print();

                    /*if(mDetails.equals("EmailReport"))
                    {
                        Intent intentEmailReport
                                = new Intent(PrintStockBalanceSummaryActivity.this
                                , EmailStockBalanceSummaryActivity.class);
                        intentEmailReport.putExtra("details", "Unload");
                        startActivity(intentEmailReport);
                    }*/

                } catch (Exception ex) {
                    Log.d("Exception", ex.toString());
                }

            } else {

                Toast.makeText(PrintStockBalanceSummaryRP4Activity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    private void getItemBalancePdaList() {
        mItemBalancePdaList = new ArrayList<ItemBalancePda>();
        ItemBalancePdaDbHandler ibpDb = new ItemBalancePdaDbHandler(getApplicationContext());
        ibpDb.open();

        mItemBalancePdaList = ibpDb.getItemBalanceList(mApp.getmCurrentDriverCode());

        ibpDb.close();
    }

    private void print() {


        try {

            printData = new byte[]{0};
            docDPL = new DocumentDPL();
            paramDPL = new ParametersDPL();

            int commanWidth = 25;
            int fontSize = 8;
            int row = 0;

            String date = "", tableColumnSpacing = "", printCommand = "";

            DateFormat df = new SimpleDateFormat("dd  MMM  yyyy");
            Date dateObj = new Date();
            date = "for " + df.format(dateObj).toString();

            int dyanamicMediaLength = 0;
            int mTotalNoOfItems = 0;
            if (mItemBalancePdaList.size() > 0) {
                for (ItemBalancePda ib : mItemBalancePdaList) {

                    mTotalNoOfItems++;
                }
            }

            dyanamicMediaLength = mTotalNoOfItems * 40;
            int concat = 300 + dyanamicMediaLength;

            int length = (int) (Math.log10(concat) + 1);

            String mediaLength;
            if (length == 3) {
                mediaLength = "0" + concat;
            } else {
                mediaLength = concat + "";
            }

            String sDPL = String.format("%cc" + mediaLength + "\r\n", 2);

            row += 20;
            paramDPL.setIsBold(false);
            docDPL.writeTextInternalSmooth(" ",
                    fontSize, row, commanWidth, paramDPL);

            row += 20;
            docDPL.writeTextInternalSmooth("Received by",
                    fontSize, row, 35, paramDPL);

            //received by line
            row += 20;
            docDPL.writeTextScalable("---------------------------",
                    "00", row, commanWidth, paramDPL);

            row += 20;
            docDPL.writeTextInternalSmooth(" ",
                    fontSize, row, commanWidth, paramDPL);
            docDPL.writeTextInternalSmooth(" ",
                    fontSize, row, commanWidth, paramDPL);

            //table end line
            row += 40;
            docDPL.writeTextInternalSmooth("......................................................................................................................",
                    fontSize, row, commanWidth, paramDPL);
//            System.out.println(paramDPL.getFontHeight());
//            System.out.println(paramDPL.getFontWidth());
            paramDPL.setFontHeight(10);
            paramDPL.setFontWidth(10);
            paramDPL.setIsBold(false);
            //table content
            if (mItemBalancePdaList.size() > 0) {
                Collections.reverse(mItemBalancePdaList);
                for (ItemBalancePda ib : mItemBalancePdaList) {

                    float qty = ib.getOpenQty() - ib.getQuantity();

                    row += 20;
//                  docDPL.writeTextInternalSmooth(String.format(tableColumnSpacing
//                            , ""
//                            , ib.getItemNo()
//                            , ib.getUnitofMeasureCode()
//                            , String.valueOf(Math.round(qty))
//                            , String.valueOf(Math.round(ib.getExchangedQty()))),
//                            fontSize, row, commanWidth, paramDPL);



                    docDPL.writeTextScalable(ib.getItemNo(),
                            "01", row, 80, paramDPL);
                    docDPL.writeTextScalable(ib.getUnitofMeasureCode(),
                            "01", row, 160, paramDPL);
                    docDPL.writeTextScalable(String.valueOf(Math.round(qty)),
                            "01", row, 240, paramDPL);
                    docDPL.writeTextScalable(String.valueOf(Math.round(ib.getExchangedQty())),
                            "01", row, 320, paramDPL);

                    row += 14;
                    paramDPL.setIsUnicode(true);
                    paramDPL.setDBSymbolSet(ParametersDPL.DoubleByteSymbolSet.Unicode);


                    docDPL.writeTextScalable(ib.getItemDescription(),
                            "01", row, commanWidth, paramDPL);
                }
            }
            paramDPL.setFontHeight(8);
            paramDPL.setFontWidth(8);

            //table lower line
            row += 20;
            docDPL.writeTextInternalSmooth(
                    ".....................................................................................................................",
                    fontSize, row, commanWidth, paramDPL);

            tableColumnSpacing = "%12s %-15s %-15s %15s %15s";
            //table header
            row += 20;

            docDPL.writeTextInternalSmooth(String.format(tableColumnSpacing
                    , "Description"
                    , "ItemCode"
                    , "UOM"
                    , "Vehicle Qty"
                    , "Exch. Qty"),
                    fontSize, row, commanWidth, paramDPL);

            //table upper line
            row += 20;
            docDPL.writeTextInternalSmooth(
                    ".....................................................................................................................",
                    fontSize, row, commanWidth, paramDPL);

            //salesMan
            row += 40;
            docDPL.writeTextInternalSmooth("Vansales : " + mApp.getCurrentUserDisplayName(),
                    fontSize, row, commanWidth, paramDPL);

            //date
            row += 20;
            paramDPL.setIsBold(true);
            docDPL.writeTextScalable(date,
                    "00", row, 160, paramDPL);

            row += 20;
            paramDPL.setIsBold(true);
            paramDPL.setFontHeight(14);
            paramDPL.setFontWidth(14);
            docDPL.writeTextScalable("ITEM BALANCE SUMMARY",
                    "00", row, 100, paramDPL);

            printData = new byte[docDPL.getDocumentData().length + sDPL.length()];

            System.arraycopy(sDPL.getBytes(), 0, printData, 0, sDPL.getBytes().length);
            System.arraycopy(docDPL.getDocumentData(), 0, printData, sDPL.getBytes().length, docDPL.getDocumentData().length);

            new Thread(PrintStockBalanceSummaryRP4Activity.this, "PrintingTask").start();
        } catch (Exception ex) {
            Toast.makeText(PrintStockBalanceSummaryRP4Activity.this, ex.getMessage(), Toast.LENGTH_LONG)
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

}
