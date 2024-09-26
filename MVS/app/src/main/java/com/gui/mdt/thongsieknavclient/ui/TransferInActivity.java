package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.ConfigurationManager;
import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.transferreceipt.TransferReceiptHeaderArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptListResult;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptListResultData;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptListSearchParameter;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class TransferInActivity extends BaseActivity {

    private Context mContext;
    private GetTransferReceiptListTask mGetTransferReceiptListTask = null;

    ListView historyList;
    TransferReceiptHeaderArrayAdapter transferReceiptHeaderArrayAdapter;

    private String mTransferNo;

    private NavClientApp mApp;

    protected int getLayoutResource() {
        return R.layout.activity_transfer_in;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;

        mApp = (NavClientApp) getApplication();

        historyList = (ListView) findViewById(R.id.orderList);

        mGetTransferReceiptListTask = new GetTransferReceiptListTask(this);
        mGetTransferReceiptListTask.execute((Void) null);
    }

    public class GetTransferReceiptListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        TransferReceiptListResult mTransferReceiptListResult;
        TransferReceiptListSearchParameter mTransferReceiptListSearchParameter;

        GetTransferReceiptListTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mTransferReceiptListSearchParameter = new TransferReceiptListSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword());

            Gson gson = new Gson();
            String json = gson.toJson(mTransferReceiptListSearchParameter);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<TransferReceiptListResult> call = mApp.getNavBrokerService().GetTransferReceiptList(mTransferReceiptListSearchParameter);

                mTransferReceiptListResult = call.execute().body();

            } catch (IOException e) {
                Log.d("TransferReceiptList", e.toString());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            NotificationManager.HideProgressDialog();

            if (success) {

                try {

                    if (mTransferReceiptListResult != null) {

                        if (mTransferReceiptListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                            historyList.setAdapter(null);

                            ArrayList<TransferReceiptListResultData> temp_list = new ArrayList<TransferReceiptListResultData>();

                            if (mTransferReceiptListResult.getTransferReceiptListResultData() != null) {
                                temp_list.addAll(mTransferReceiptListResult.getTransferReceiptListResultData());
                            }

                            if (temp_list.size() == 0) {
                                TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                                lblNoData.setVisibility(View.VISIBLE);
                            } else {
                                TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                                lblNoData.setVisibility(View.INVISIBLE);
                            }

                            transferReceiptHeaderArrayAdapter = new TransferReceiptHeaderArrayAdapter(mContext, temp_list);

                            historyList.setAdapter(transferReceiptHeaderArrayAdapter);
                            historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    TextView transferText = (TextView) view.findViewById(R.id.lblTransferNo);

                                    mTransferNo = transferText.getText().toString();
                                    Log.d("TRANSFER NO", mTransferNo);
                                    Intent intent = new Intent(getBaseContext(), TransferInLineActivity.class);
                                    intent.putExtra("TRANSFER_ORDER_NO", mTransferNo);
                                    startActivity(intent);

                                }
                            });

                            DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_TRANSFER_RECEIPT);
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mTransferReceiptListResult.getMessage());
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
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
