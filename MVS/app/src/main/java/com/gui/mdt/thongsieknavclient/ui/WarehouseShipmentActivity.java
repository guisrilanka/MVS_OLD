package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.gui.mdt.thongsieknavclient.adapters.warehouseshipment.WarehouseShipmentHeaderArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentListResult;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentListResultData;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentListSearchParameter;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class WarehouseShipmentActivity extends BaseActivity {

    private Context mContext;
    private GetWarehouseShipmentListTask mGetWarehouseShipmentListTask = null;

    ListView historyList;
    WarehouseShipmentHeaderArrayAdapter warehouseShipmentHeaderArrayAdapter;

    private String mWarehouseShipNo;

    private NavClientApp mApp;

    protected int getLayoutResource() {
        return R.layout.activity_warehouse_shipment;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_shipment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;

        mApp = (NavClientApp) getApplication();

        historyList = (ListView) findViewById(R.id.orderList);

        mGetWarehouseShipmentListTask = new GetWarehouseShipmentListTask(this);
        mGetWarehouseShipmentListTask.execute((Void) null);
    }

    public class GetWarehouseShipmentListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        WarehouseShipmentListResult mWarehouseShipmentListResult;
        WarehouseShipmentListSearchParameter mWarehouseShipmentListSearchParameter;

        GetWarehouseShipmentListTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mWarehouseShipmentListSearchParameter = new WarehouseShipmentListSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword());
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<WarehouseShipmentListResult> call = mApp.getNavBrokerService().GetWarehouseShipmentList(mWarehouseShipmentListSearchParameter);

                mWarehouseShipmentListResult = call.execute().body();

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
                    if (mWarehouseShipmentListResult != null) {

                        if (mWarehouseShipmentListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                            Gson gson = new Gson();
                            String json = gson.toJson(mWarehouseShipmentListResult);
                            Log.d("WAREHOUSE", json);
                            historyList.setAdapter(null);

                            ArrayList<WarehouseShipmentListResultData> temp_list = new ArrayList<WarehouseShipmentListResultData>();

                            if (mWarehouseShipmentListResult.getWarehouseShipmentListResultData() != null) {
                                temp_list.addAll(mWarehouseShipmentListResult.getWarehouseShipmentListResultData());
                            }

                            warehouseShipmentHeaderArrayAdapter = new WarehouseShipmentHeaderArrayAdapter(mContext, temp_list);

                            historyList.setAdapter(warehouseShipmentHeaderArrayAdapter);
                            historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    mWarehouseShipNo = warehouseShipmentHeaderArrayAdapter.getItem(i).getWarehouseShipmentNo();
                                    Log.d("WAREHOUSESHIP NO", mWarehouseShipNo);

                                    Intent intent = new Intent(getBaseContext(), WarehouseShipmentLineActivity.class);
                                    intent.putExtra("WAREHOUSE_SHIP_NO", mWarehouseShipNo);
                                    startActivity(intent);

                                }
                            });

                            DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_WAREHOUSE_SHIPMENT);
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mWarehouseShipmentListResult.getMessage());
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
