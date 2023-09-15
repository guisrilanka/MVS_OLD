package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.grn.PurchaseOrderReceiptLineArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PrintPurchaseOrderReceiptLineLabelParameter;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptDataCollection;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptRetrieveParameter;
import com.gui.mdt.thongsieknavclient.model.grnmodels.ReceiptItemEntry;
import com.gui.mdt.thongsieknavclient.model.grnmodels.ReceiptLineData;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class GRNOrderLineActivity extends BaseActivity {

    private Context mContext;
    private NavClientApp mApp;

    private RetrievePurchaseOrderReceiptDetailsTask mRetrievePurchaseOrderReceiptDetailsTask;
    private PrintPurchaseOrderReceiptLineLabelTask mPrintPurchaseOrderReceiptLineLabelTask;

    private PurchaseOrderReceiptDataCollection mPurchaseOrderReceiptDataCollection;

    private ListView orderList;
    private PurchaseOrderReceiptLineArrayAdapter orderReceiptAdapter;

    private String mPurchaseOrderReceiptNo;
    private String s = null;

    protected int getLayoutResource() {
        return R.layout.activity_grn_order_line;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grn_order_line);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        orderList = (ListView) findViewById(R.id.orderList);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPurchaseOrderReceiptNo = extras.getString("PURCHASE_ORDER_RECEIPT_NO");
            //The key argument here must match that used in the other activity

            DataManager.getInstance().mPurchaseOrderReceiptNo = mPurchaseOrderReceiptNo;

            mRetrievePurchaseOrderReceiptDetailsTask = new RetrievePurchaseOrderReceiptDetailsTask((Activity) mContext);
            mRetrievePurchaseOrderReceiptDetailsTask.execute((Void) null);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PR NO: " + mPurchaseOrderReceiptNo);
    }

    public class RetrievePurchaseOrderReceiptDetailsTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PurchaseOrderReceiptRetrieveParameter mPurchaseOrderReceiptRetrieveParameter;

        RetrievePurchaseOrderReceiptDetailsTask(Activity activity) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

            mActivity = activity;
            mPurchaseOrderReceiptRetrieveParameter = new PurchaseOrderReceiptRetrieveParameter(mApp.getmUserCompany(), mPurchaseOrderReceiptNo, mApp.getCurrentUserName(), mApp.getCurrentUserPassword());
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PurchaseOrderReceiptDataCollection> call = mApp.getNavBrokerService().GetPurchaseOrderReceiptDetails(mPurchaseOrderReceiptRetrieveParameter);

                mPurchaseOrderReceiptDataCollection = call.execute().body();

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
                    if (mPurchaseOrderReceiptDataCollection != null) {

                        if (mPurchaseOrderReceiptDataCollection.getStatus() == BaseResult.BaseResultStatusOk) {
                            orderList.setAdapter(null);


                            final ArrayList<ReceiptItemEntry> temp_list = new ArrayList<ReceiptItemEntry>();

                            if (mPurchaseOrderReceiptDataCollection != null && mPurchaseOrderReceiptDataCollection.getPurchaseOrderReceiptData() != null) {
                                for (int i = 0; i < mPurchaseOrderReceiptDataCollection.getPurchaseOrderReceiptData().getLinesData().size(); i++) {
                                    ReceiptLineData lineData = mPurchaseOrderReceiptDataCollection.getPurchaseOrderReceiptData().getLinesData().get(i);
                                    if (lineData.getLotTrackingLines() != null && lineData.getLotTrackingLines().size() != 0) {
                                        ReceiptItemEntry temp_entry = new ReceiptItemEntry(lineData.getItemDescription(), lineData.getItemNo(), lineData.getLineNo(), lineData.getQuantity(), lineData.getUom(), true);
                                        temp_list.add(temp_entry);
                                    } else {
                                        ReceiptItemEntry temp_entry = new ReceiptItemEntry(lineData.getItemDescription(), lineData.getItemNo(), lineData.getLineNo(), lineData.getQuantity(), lineData.getUom(), false);
                                        temp_list.add(temp_entry);
                                    }

                                }
                            }

                            orderReceiptAdapter = new PurchaseOrderReceiptLineArrayAdapter(mContext, temp_list);

                            orderList.setAdapter(orderReceiptAdapter);


                            orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    TextView lblLineNo = (TextView) view.findViewById(R.id.lblLineNo);

                                    String currLine = lblLineNo.getText().toString();
                                    String itemDescription = "";
                                    String itemNo = "";

                                    for (int index = 0; index < mPurchaseOrderReceiptDataCollection.getPurchaseOrderReceiptData().getLinesData().size(); index++) {

                                        ReceiptLineData temp_line = mPurchaseOrderReceiptDataCollection.getPurchaseOrderReceiptData().getLinesData().get(index);
                                        if (temp_line.getLineNo().contains(currLine)) {
                                            DataManager.getInstance().clearGRNTransitionData();
                                            DataManager.getInstance().mPurchaseOrderReceiptNo = mPurchaseOrderReceiptNo;
                                            DataManager.getInstance().mPurchaseOrderReceiptLineNo = currLine;
                                            DataManager.getInstance().mPurchaseOrderReceiptDescription = temp_line.getItemDescription();
                                            DataManager.getInstance().mPurchaseOrderReceiptItemIDUom = "Item No: " + temp_line.getItemNo() + " / " + temp_line.getUom();
                                            DataManager.getInstance().mPurchaseOrderReceiptQuantityBase = temp_line.getQuantityBase();
                                            DataManager.getInstance().CloneReceiptItemLotEntriesData(temp_line.getLotTrackingLines());
                                            itemDescription = temp_line.getItemDescription();
                                            itemNo = temp_line.getItemNo();
                                        }
                                    }

                                    if (DataManager.getInstance().mReceiptItemLotEntries.size() > 0) {
                                        Intent intent = new Intent(getBaseContext(), GRNOrderLotEntryActivity.class);
                                        intent.putExtra("ITEM_NO", itemNo);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(mContext, "Lot No. not required for this item.", Toast.LENGTH_LONG).show();
                                    }
                                }

                            });
                        } else {
//                        NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_error), mPurchaseOrderReceiptDataCollection.getMessage());
                            if (mPurchaseOrderReceiptDataCollection.getMessage() != null) {
                                NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_error), mPurchaseOrderReceiptDataCollection.getMessage());
                            } else {
                                TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                                lblNoData.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                        lblNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, getResources().getString(R.string.notification_msg_server_no_response));

                Intent intent = new Intent(getBaseContext(), GRNOrderActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class PrintPurchaseOrderReceiptLineLabelTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PrintPurchaseOrderReceiptLineLabelParameter mPrintPurchaseOrderReceiptLineLabelParameter;
        BaseResult mReturnBaseResult;
        String temp_str;

        PrintPurchaseOrderReceiptLineLabelTask(Activity activity, String PurchaseOrderReceiptNo, String LineNo) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_PRINT, NotificationManager.DIALOG_MSG_PRINT);

            mActivity = activity;

            mPrintPurchaseOrderReceiptLineLabelParameter = new PrintPurchaseOrderReceiptLineLabelParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), PurchaseOrderReceiptNo, Integer.parseInt(LineNo));

            Gson gson = new Gson();
            String json = gson.toJson(mPrintPurchaseOrderReceiptLineLabelParameter);
            Log.d("JSON POST", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().PrintPurchaseOrderReceiptLineLabel(mPrintPurchaseOrderReceiptLineLabelParameter);

                mReturnBaseResult = call.execute().body();
                //temp_str = call.execute().raw().toString();

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
                    if (mReturnBaseResult != null) {

                        if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {

                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_INFORMATION, NotificationManager.MSG_PRINT_REQUEST_SENT);
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mReturnBaseResult.getMessage());
                        }
                    } else {

                        if (mReturnBaseResult.getMessage() != null) {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mReturnBaseResult.getMessage());
                        } else {
                            TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                            lblNoData.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, NotificationManager.MSG_PRINT_REQUEST_FAILED);
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public void PrintEntry(String lineNo) {
        //Log.d("SERVER REMOVE", "PO: " + mPurchaseOrderNo + " LineNo: " + mLineNo + " position: " + position + " EntryNO: " + entryNo);
        mPrintPurchaseOrderReceiptLineLabelTask = new PrintPurchaseOrderReceiptLineLabelTask((Activity) mContext, mPurchaseOrderReceiptNo, lineNo);
        mPrintPurchaseOrderReceiptLineLabelTask.execute((Void) null);
    }
}
