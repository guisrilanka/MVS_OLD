package com.gui.mdt.thongsieknavclient.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.Date;
import java.util.List;

public class PrintStockBalanceSummaryActivity extends AppCompatActivity {

    private Bundle mExtras;
    private String mFilterDate = "", mDetails="";
    private List<ItemBalancePda> mItemBalancePdaList;
    NavClientApp mApp;
    GetItemBalances mGetItemBalances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExtras = getIntent().getExtras();
        if(mExtras.containsKey("details"))
        {
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

                Toast.makeText(PrintStockBalanceSummaryActivity.this, "No data", Toast.LENGTH_SHORT).show();
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
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter!=null && btAdapter.getBondedDevices()!=null && btAdapter.getBondedDevices().size()>0) {
            BluetoothDevice mBtDevice = btAdapter.getBondedDevices().iterator().next();   // Get first paired device
            final BluetoothPrinter mPrinter = new BluetoothPrinter(mBtDevice);
            mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {

                @Override
                public void onConnected() {

                    try {

                        String date = "", tableColumnSpacing = "", printCommand = "";

                        DateFormat df = new SimpleDateFormat("dd  MMM  yyyy");
                        Date dateObj = new Date();
                        date = "for " + df.format(dateObj).toString();

                        int esc = 27;
                        int L = 69;
                        int g = 90;
                        mPrinter.btOutputStream.write(esc);
                        mPrinter.btOutputStream.write(L);
                        mPrinter.btOutputStream.write(g);

                        //title
                        mPrinter.btOutputStream.write("{PRINT,:@40,200:MF107|ITEM BALANCE SUMMARY|}".getBytes());

                        //date
                        printCommand = "{PRINT,:@30,250:MF107|" + date + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //salesMan
                        printCommand = "{PRINT,:@30,20:MF185|Vansales : " + mApp.getCurrentUserDisplayName() + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //table upper line
                        printCommand = "{PRINT,:@30,20:MF185|"
                                + "..........................................................................." + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //description ch
                        //printCommand = "{PRINT,:@10,20:MF185|" + "Description" + "|}";
                        //mPrinter.btOutputStream.write(printCommand.getBytes());

                        //table header
                        tableColumnSpacing = "%12s %-13s %-13s %15s %15s";
                        printCommand = "{PRINT,:@10,20:MF185|" +
                                String.format(tableColumnSpacing
                                        , "Description"
                                        , "ItemCode"
                                        , "UOM"
                                        , "Vehicle Qty"
                                        , "Exch. Qty")+"|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //table lower line
                        printCommand = "{PRINT,:@10,20:MF185|"
                                + "..........................................................................." + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //table content
                        if (mItemBalancePdaList.size() > 0) {
                            for (ItemBalancePda ib : mItemBalancePdaList) {

                                float qty = ib.getOpenQty() - ib.getQuantity();

                            /*int maxDescriptionSize = 35;
                            String itemDescription = "";

                            if (ib.getItemDescription().length() > maxDescriptionSize) {
                                itemDescription = ib.getItemDescription().substring(0, maxDescriptionSize);
                            } else if (ib.getItemDescription().length() == maxDescriptionSize) {
                                itemDescription = ib.getItemDescription();
                            } else {
                                int needCount = maxDescriptionSize - ib.getItemDescription().length();
                                itemDescription = ib.getItemDescription()
                                        + String.format("%-" + String.valueOf(needCount) + "s", "");
                            }*/

                                printCommand = "{PRINT,:@10,20:MF185|" + ib.getItemDescription() +"|}";
                                mPrinter.btOutputStream.write(printCommand.getBytes(StandardCharsets.UTF_8));

                                printCommand = "{PRINT,:@10,20:MF185|" + String.format(tableColumnSpacing
                                        , ""
                                        , ib.getItemNo()
                                        , ib.getUnitofMeasureCode()
                                        , String.valueOf(Math.round(qty))
                                        , String.valueOf(Math.round(ib.getExchangedQty()))) + "|}";
                                mPrinter.btOutputStream.write(printCommand.getBytes(StandardCharsets.UTF_8));

                            }
                        }

                        //table end line
                        printCommand = "{PRINT,:@10,20:MF185|"
                                + "..........................................................................." + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        printCommand = "{PRINT,:@30,20:MF185|"
                                + " " + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //received by line
                        printCommand = "{PRINT,:@30,20:MF185|"
                                + "--------------------" + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());
                        printCommand = "{PRINT,:@10,20:MF185|"
                                + "      Received by" + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        printCommand = "{PRINT,:@30,20:MF185|"
                                + " " + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());

                        //Change printer mode back to line print
                        mPrinter.printText("{LP}");
                        Thread.sleep(5000);
                        mPrinter.finish();

                    } catch (Exception ex) {
                        Toast.makeText(PrintStockBalanceSummaryActivity.this,
                                mPrinter.getDevice().getName().toString(), Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onFailed() {
                    Log.d("BluetoothPrinter", "Connection failed");
                }

            });
        }
        else {
            return;
        }

    }
}
