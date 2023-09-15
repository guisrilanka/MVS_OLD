package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemBalancePDAListResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemBalancePdaParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemBalancePdaDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.model.BaseResult;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 08/16/2017.
 */


public class ItemBalancePdaSyncTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    ApiItemBalancePdaParameter apiItemListParameter;
    ApiItemBalancePDAListResult apiItemListResult;
    List<ApiItemBalancePDAListResult.ApiItemBalancePDAListResultData> itemListResult;
    Logger mLog;
    private NavClientApp mApp;
    private boolean mIsForcedSync = false;
    private String mItemNo = "";
    private boolean mIsOnlineRequest;

    public ItemBalancePdaSyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.mIsForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog = Logger.getLogger(ItemBalancePdaSyncTask.class);
        this.mIsOnlineRequest = false;
    }


    public ItemBalancePdaSyncTask(Context context, boolean isForcedSync, String itemNo) {
        this.context = context;
        this.mIsForcedSync = isForcedSync;
        this.mItemNo = itemNo;
        this.mApp = (NavClientApp) context;
        this.mLog = Logger.getLogger(ItemBalancePdaSyncTask.class);
        this.mIsOnlineRequest = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //set parameters for request
        apiItemListParameter = new ApiItemBalancePdaParameter();
        apiItemListParameter.setUserName(mApp.getCurrentUserName());
        apiItemListParameter.setPassword(mApp.getCurrentUserPassword());
        apiItemListParameter.setUserCompany(mApp.getmUserCompany());
        apiItemListParameter.setFilterLocationCode("");
        apiItemListParameter.setFilterItemCode(this.mItemNo);
        String lastModified = getSyncConfiguration().getLastSyncDateTime();
        apiItemListParameter.setFilterLastModifiedDate(lastModified == null ? "" : lastModified);

        logParams(apiItemListParameter);


        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownItemBalance));

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (mIsOnlineRequest && isNetworkConnected()) {
                Call<ApiItemBalancePDAListResult> call =
                        mApp.getNavBrokerService().GetItemBalancePDA(apiItemListParameter);
                apiItemListResult = call.execute().body();

                if (apiItemListResult != null && apiItemListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                    //addRecords(true);
                    itemListResult = apiItemListResult.getItemBalancePDAListResultData();
                }
            }
        } catch (IOException e) {
            Log.d("NAV_Client_Exception", e.toString());
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        SyncStatus syncStatus = new SyncStatus();

        if (mIsOnlineRequest) {

            if (apiItemListResult != null && apiItemListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                //syncConfig.setLastSyncDateTime(apiItemListResult.getServerDate());
                //syncConfig.setDataCount(itemListResult.size());
                syncConfig.setSuccess(true);
                String qty = itemListResult.size() > 0 ? (String.valueOf(itemListResult.get(0).getQuantity())) :
                        "Service Unavailable";

                syncStatus.setScope(qty);
            } else {
                //syncConfig.setLastSyncDateTime(DateTime.now().toString());
                //syncConfig.setDataCount(0);
                syncConfig.setSuccess(false);
                syncStatus.setScope("Service Unavailable");
            }

            if (mIsForcedSync) {
                if (syncConfig.getSuccess()) {
                    syncStatus.setStatus(true);
                    delegate.onAsyncTaskFinished(syncStatus);
                } else {
                    syncStatus.setStatus(false);
                    delegate.onAsyncTaskFinished(syncStatus);
                }
            }

        } else {
            if (mIsForcedSync) {
                syncConfig.setSuccess(true);
                syncStatus.setScope(context.getResources().getString(R.string.SyncScopeDownItemBalance));
                delegate.onAsyncTaskFinished(syncStatus);
            }
        }
    }

    @Override
    protected void onCancelled() {
        this.cancel(true);
    }

    private SyncConfiguration getSyncConfiguration() {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);
        SyncConfiguration sc = new SyncConfiguration();

        try {
            syncDbHandler.open();
            sc = syncDbHandler.getLastSyncInfoByScope(context.getResources()
                    .getString(R.string.SyncScopeDownItemBalance));
        } catch (Exception ex) {
            sc.setLastSyncDateTime(Calendar.getInstance().getTime().toString());
        }
        syncDbHandler.close();
        return sc;

    }

    private void setSyncConfiguration(SyncConfiguration syncConfig) {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);

        try {
            syncDbHandler.open();
            syncDbHandler.addSyncConfiguration(syncConfig);

            Gson gson = new Gson();
            String json = gson.toJson(syncConfig);
            //Log.d("SYNC_ITM_BAL_RESULT ", json);
            mLog.info("SYNC_ITM_BAL_RESULT :" + json);

        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }

    private void addRecords(final boolean success) {

        if (success) {
            ItemBalancePdaDbHandler dbAdapter = new ItemBalancePdaDbHandler(context);
            dbAdapter.open();

            itemListResult = new ArrayList<ApiItemBalancePDAListResult.ApiItemBalancePDAListResultData>();

            if (apiItemListResult != null && apiItemListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                itemListResult = apiItemListResult.getItemBalancePDAListResultData();
            }

            //set current sync status.


            try {

                for (ApiItemBalancePDAListResult.ApiItemBalancePDAListResultData
                        itemResult : itemListResult) {

                    if (dbAdapter.deleteItem(itemResult.getItem_No())) {

                        ItemBalancePda item = new ItemBalancePda();
                        item.setKey(itemResult.getKey());
                        item.setItemNo(itemResult.getItem_No());
                        item.setLocationCode(itemResult.getLocation_Code());
                        item.setBinCode(itemResult.getBin_Code());

                        item.setQuantity(itemResult.getQuantity());
                        item.setUnitofMeasureCode(itemResult.getUnit_of_Measure_Code());

                        dbAdapter.addItemBalancePda(item);
                        Log.d("SYNC_ITM_BAL_ADDED: ", itemResult.getItem_No());
                    }
                }
                dbAdapter.close();

            } catch (Exception ex) {
                dbAdapter.close();
                syncConfig.setSuccess(false);
            }

        } else {
            syncConfig.setSuccess(false);
        }


    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mApp.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void logParams(ApiItemBalancePdaParameter params) {
        params.setPassword("****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_ITM_BAL_PARAMS :" + json);
        params.setPassword(mApp.getCurrentUserPassword());


    }
}

