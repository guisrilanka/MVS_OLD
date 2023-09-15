package com.gui.mdt.thongsieknavclient.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.utils.BluetoothPrinter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrintSalesOrderDraftActivity extends AppCompatActivity {

    NavClientApp mApp;
    SalesOrder mTempSalesOrder;
    List<SalesOrderLine> mSalesOrderLineList;
    private Bundle mExtras;
    int mTotalNoOfItems = 0;

    GenerateDraftReportTask mGenerateDraftReportTask;
    Customer mCustomer;

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
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice mBtDevice = btAdapter.getBondedDevices().iterator().next();   // Get first paired device

        final BluetoothPrinter mPrinter = new BluetoothPrinter(mBtDevice);
        mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {

            @Override
            public void onConnected() {

                try {

                    //Change Printer Mode to EZ print Mode
                    int esc = 27;
                    int L = 69;
                    int g = 90;
                    mPrinter.btOutputStream.write(esc);
                    mPrinter.btOutputStream.write(L);
                    mPrinter.btOutputStream.write(g);


                    String tableRowSpaceing,
                            printCommand;


                    //LOGO print
                    mPrinter.printUnicode("{PRINT:@10,100:G|}".getBytes());

                    //title
                    //mPrinter.btOutputStream.write("{PRINT,:@30,200:MF107,HMULT2,VMULT2|TAX INVOICE|}".getBytes());
                    mPrinter.btOutputStream.write("{PRINT,:@30,200:MF107,HMULT2,VMULT2|*** DRAFT ***|}".getBytes());

                    //suplier code
                    printCommand = "{PRINT,:@30,20:MF107|Supplier Code : " + mCustomer.getCustomerReferenceNo()
                            + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());


                    //delivery date
                    printCommand = "{PRINT,:@20,20:MF107|"
                            + "Delivery Date : " + dateConverter(mTempSalesOrder.getShipmentDate()) + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    //Bill to customer
                    printCommand = "{PRINT,:@20,20:MF107|" + "Bill To : " + mCustomer.getBillToCustomerName() + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    //Ship to customer
                    printCommand = "{PRINT,:@20,20:MF107|Ship To : " + mTempSalesOrder.getSelltoCustomerNo() + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    //po no(External Document no)
                    if (mTempSalesOrder.getExternalDocumentNo() != null) {
                        printCommand = "{PRINT,:@20,20:MF107|PO No : " + mTempSalesOrder.getExternalDocumentNo() + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());
                    }
                    List<String> arr = splitString(mTempSalesOrder.getSelltoCustomerName() + " , "
                            + mTempSalesOrder.getSelltoAddress(), 40);
                    for (String s : arr) {
                        printCommand = "{PRINT,:@20,50:MF107|" + s + "|}";
                        mPrinter.btOutputStream.write(printCommand.getBytes());
                    }

                    //Postal Code
                    printCommand = "{PRINT,:@20,50:MF107|" + mCustomer.getPostalCode() + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    printCommand = "{PRINT,:@30,20:MF185|" + "Delivery By: " + mApp.getCurrentUserDisplayName() + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());


                    //table upper line
                    printCommand = "{PRINT,:@30,20:MF185|"
                            + "................................................................................" + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    tableRowSpaceing = "%-20s %12s %12s %12s %12s";

                    //table header column names
                    printCommand = "{PRINT,:@10,20:MF185|" +
                            String.format(tableRowSpaceing
                                    , "Item", "Qty Billed", "Qty Exch.", "U.Price($)", "Total($)") + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    //table lower line
                    printCommand = "{PRINT,:@10,20:MF185|"
                            + "..............................................................................." + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    if (mSalesOrderLineList != null) {

                        if (mSalesOrderLineList.size() >= 0) {
                            for (SalesOrderLine sol : mSalesOrderLineList) {

                                //check sales qty zero items
                                float salesQty = sol.getExchangedQty() + sol.getQuantity();

                                if (salesQty > new Float(0)) {
                                    String itemUOM = sol.getUnitofMeasure() == null ? "" : sol.getUnitofMeasure();
                                    String exchQty = sol.getExchangedQty() == 0f ? ""
                                            : String.valueOf(Math.round(sol.getExchangedQty())) + " " + itemUOM;
                                    mTotalNoOfItems++;

                                    printCommand = "{PRINT,:@20,20:MF185|"
                                            + sol.getNo() + " " + sol.getItemCrossReferenceDescription() + "|}";
                                    mPrinter.btOutputStream.write(printCommand.getBytes());

                                    printCommand = "{PRINT,:@8,20:MF185|"
                                            + String.format(tableRowSpaceing
                                            , "     " + sol.getItemCrossReferenceNo()
                                            , String.valueOf(Math.round(sol.getQuantity())) + " " + itemUOM
                                            , exchQty
                                            , String.format("%.2f", sol.getUnitPrice())
                                            , String.format("%.2f", sol.getLineAmount())) + "|}";
                                    mPrinter.btOutputStream.write(printCommand.getBytes());
                                }
                            }
                        }
                    }
                    printCommand = "{PRINT,:@10,20:MF185|"
                            + "................................................................................" + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    //show no. items
                    String totalItem = "* total of " + String.valueOf(mTotalNoOfItems) + " item(s) on invoice";
                    printCommand = "{PRINT,:@8,20:MF185|"
                            + totalItem + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    //summery details
                    String summuryFormat = "%10s %20s %20s ";
                    printCommand = "{PRINT,:@40,20:MF107|"
                            + "              Subtotal:       $"
                            + String.format("%10.2f", mTempSalesOrder.getAmountExcludingVAT()) + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

//                    printCommand = "{PRINT,:@20,20:MF107|"
//                            + "              GST :            $"
//                            + String.format("%10.2f", mTempSalesOrder.getVATAmount()) + "|}";
                    printCommand = "{PRINT,:@20,20:MF107|"
                            + "              GST ("+mTempSalesOrder.getVatPercentage()+"%):       $"
                            + String.format("%10.2f", mTempSalesOrder.getVATAmount()) + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    printCommand = "{PRINT,:@20,20:MF107|"
                            + "              Grand Total:    $"
                            + String.format("%10.2f", mTempSalesOrder.getAmountIncludingVAT()) + "|}";

                    mPrinter.btOutputStream.write(printCommand.getBytes());


                    //footnote
                    printCommand = "{PRINT,:@30,20:MF185|"
                            + "Received from DODO Marketing Pte Ltd"
                            + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    printCommand = "{PRINT,:@10,20:MF185|"
                            + "The above mentioned goods in good order and condition."
                            + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    //signature

                    printCommand = "{PRINT,:@100,100:MF107|"
                            + "-----------------------------------------------------" + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    printCommand = "{PRINT,:@10,100:MF107|"
                            + "Client Signature / Company Chop" + "|}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    printCommand = "{PRINT,:@40,100:MF107|  |}";
                    mPrinter.btOutputStream.write(printCommand.getBytes());

                    //Change printer mode back to line print
                    mPrinter.printText("{LP}");
                    Thread.sleep(5000);
                    mPrinter.finish();

                } catch (Exception ex) {
                    Toast.makeText(PrintSalesOrderDraftActivity.this
                            , mPrinter.getDevice().getName().toString(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailed() {
                Log.d("BluetoothPrinter", "Connection failed");
            }

        });
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
