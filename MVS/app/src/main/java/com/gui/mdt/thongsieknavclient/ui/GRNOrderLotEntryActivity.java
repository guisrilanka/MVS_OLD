package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.grn.PurchaseOrderReceiptLotEntryListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PrintPurchaseOrderReceiptLotNoLabelParameter;
import com.gui.mdt.thongsieknavclient.model.grnmodels.ReceiptItemLotEntry;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class GRNOrderLotEntryActivity extends BaseActivity {

    Context mContext;
    ListView itemList;

    PurchaseOrderReceiptLotEntryListArrayAdapter entryAdapter;

    PrintPurchaseOrderReceiptLotNoLabelTask mPrintPurchaseOrderReceiptLotNoLabelTask;

    NavClientApp mApp;

    private String mItemNo;
    private String mPurchaseOrderReceiptNo;
    private String mPurchaseOrderReceiptLineNo;

    protected int getLayoutResource() {
        return R.layout.activity_grn_order_lot_entry;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grn_order_lot_entry);

        mContext = this;
        mApp = (NavClientApp) getApplication();
        mPurchaseOrderReceiptNo = DataManager.getInstance().mPurchaseOrderReceiptNo;
        mPurchaseOrderReceiptLineNo = DataManager.getInstance().mPurchaseOrderReceiptLineNo;

        itemList = (ListView) findViewById(R.id.itemList);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mItemNo = extras.getString("ITEM_NO");
        }

        InitializeDisplayHeader();

        ArrayList<ReceiptItemLotEntry> temp_list = new ArrayList<ReceiptItemLotEntry>();
        temp_list.addAll(DataManager.getInstance().mReceiptItemLotEntries);

        entryAdapter = new PurchaseOrderReceiptLotEntryListArrayAdapter(mContext, temp_list);

        itemList.setAdapter(entryAdapter);
    }

    private void InitializeDisplayHeader() {
        TextView tempView = (TextView) findViewById(R.id.lblItemDescription);
        tempView.setText(DataManager.getInstance().mPurchaseOrderReceiptDescription);

        tempView = (TextView) findViewById(R.id.lblItemIDUom);
        tempView.setText(DataManager.getInstance().mPurchaseOrderReceiptItemIDUom);

        tempView = (TextView) findViewById(R.id.lblPurchaseQuantity);
        tempView.setText("Quantity: " + DataManager.getInstance().mPurchaseOrderReceiptQuantityBase);
    }

    public class PrintPurchaseOrderReceiptLotNoLabelTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PrintPurchaseOrderReceiptLotNoLabelParameter mPrintPurchaseOrderReceiptLotNoLabelParameter;
        BaseResult mReturnBaseResult;

        PrintPurchaseOrderReceiptLotNoLabelTask(Activity activity, String LotNo, String SerialNo, String Quantity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_PRINT, NotificationManager.DIALOG_MSG_PRINT);

            mActivity = activity;

            if (SerialNo == null) {
                SerialNo = "";
            }

            if (LotNo == null) {
                LotNo = "";
            }
            mPrintPurchaseOrderReceiptLotNoLabelParameter = new PrintPurchaseOrderReceiptLotNoLabelParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(),
                    mPurchaseOrderReceiptNo, Integer.parseInt(mPurchaseOrderReceiptLineNo), LotNo, SerialNo, Float.parseFloat(Quantity));

            Gson gson = new Gson();
            String json = gson.toJson(mPrintPurchaseOrderReceiptLotNoLabelParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().PrintPurchaseOrderReceiptLotNoLabel(mPrintPurchaseOrderReceiptLotNoLabelParameter);

                mReturnBaseResult = call.execute().body();

            } catch (IOException e) {
                Log.d("NAV_Client_Exception", e.toString());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            NotificationManager.HideProgressDialog();

            if (success) {
                try {
                    if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {

                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_INFORMATION, NotificationManager.MSG_PRINT_REQUEST_SENT);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mReturnBaseResult.getMessage());
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, getResources().getString(R.string.notification_msg_server_no_response));
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public void PrintLotEntry(String lotNo, String serialNo, String quantity) {
        //Log.d("SERVER REMOVE", "PO: " + mPurchaseOrderNo + " LineNo: " + mLineNo + " position: " + position + " EntryNO: " + entryNo);
        mPrintPurchaseOrderReceiptLotNoLabelTask = new PrintPurchaseOrderReceiptLotNoLabelTask((Activity) mContext, lotNo, serialNo, quantity);
        mPrintPurchaseOrderReceiptLotNoLabelTask.execute((Void) null);
    }
}
