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
import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.stocktake.StockTakeHeaderArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntriesListParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntriesListResultData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class StockTakeActivity extends BaseActivity {

    GetStockTakeListTask mGetStockTakeListTask = null;

    ListView historyList;
    StockTakeHeaderArrayAdapter historyAdapter;
    private Context mContext;
    private NavClientApp mApp;
    private String mFilterLocationCode;
    private String mFilterLocationLineNo;
    private String mDocNo;
    private boolean ShowBinCode;

    protected int getLayoutResource() {
        return R.layout.activity_stock_take;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        mApp = (NavClientApp) getApplication();
        historyList = (ListView) findViewById(R.id.orderList);

        mGetStockTakeListTask = new GetStockTakeListTask(this);
        mGetStockTakeListTask.execute((Void) null);
    }

    public class GetStockTakeListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        List<StockTakeEntriesListResultData> mStockTakeEntriesListResult;
        StockTakeEntriesListParameter mStockTakeEntriesListParameter;

        GetStockTakeListTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mStockTakeEntriesListParameter = new StockTakeEntriesListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword());

            Gson gson = new Gson();
            String json = gson.toJson(mStockTakeEntriesListParameter);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<StockTakeEntriesListResultData>> call = mApp.getNavBrokerService().StockTakeEntriesList(mStockTakeEntriesListParameter);

                mStockTakeEntriesListResult = call.execute().body();

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
                    if (mStockTakeEntriesListResult != null) {

                        if (mStockTakeEntriesListResult.size() != 0) {

                            historyList.setAdapter(null);

                            final ArrayList<StockTakeEntriesListResultData> temp_list = new ArrayList<StockTakeEntriesListResultData>();

                            if (mStockTakeEntriesListResult != null) {
                                temp_list.addAll(mStockTakeEntriesListResult);
                            }

                            historyAdapter = new StockTakeHeaderArrayAdapter(mContext, temp_list);
                            historyList.setAdapter(historyAdapter);
                            historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    ShowBinCode = false;
                                    ShowBinCode = temp_list.get(i).isShowBinCode();
                                    mFilterLocationCode = temp_list.get(i).getLocationCode();
                                    mFilterLocationLineNo = String.valueOf(temp_list.get(i).getStockTakeHeaderLineNo());
                                    mDocNo = temp_list.get(i).getDocumentNo();

                                    Intent intent = new Intent(getBaseContext(), StockTakeLineActivity.class);
                                    intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                                    intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                                    intent.putExtra("IS_SHOW_BINCODE", ShowBinCode);
                                    intent.putExtra("DOCUMENT_NO", mDocNo);
                                    startActivity(intent);

                                    DataManager.getInstance().clearTransitionStockTakeListResultData();
                                    DataManager.getInstance().setStockTakeListResultData(temp_list.get(i));

                                }
                            });
                            //DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_STOCK_TAKE);
                        } else {
                            TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                            lblNoData.setVisibility(View.VISIBLE);
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

