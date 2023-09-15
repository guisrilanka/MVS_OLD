package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemUomListParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemUomListResult;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.ItemUom;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemUomDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by bhanuka on 03/08/2017.
 */

public class ItemUomSyncTask extends AsyncTask<Void, Void, Boolean> {

    Context context;
    private NavClientApp mApp;
    public AsyncResponse delegate = null;
    SyncConfiguration syncConfig;
    private boolean isForcedSync = false;
    Logger mLog;

    ApiItemUomListParameter mItemUomListParameter;
    ApiItemUomListResult itemUomListResult;

    public ItemUomSyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog= Logger.getLogger(ItemUomSyncTask.class);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //set parameters for request
        mItemUomListParameter = new ApiItemUomListParameter();
        mItemUomListParameter.setUserCompany(mApp.getmUserCompany());
        mItemUomListParameter.setUserName(mApp.getCurrentUserName());
        mItemUomListParameter.setPassword(mApp.getCurrentUserPassword());
        mItemUomListParameter.setFilterItemCode(getItemCodes());
        String lastModified = getSyncConfiguration().getLastSyncDateTime();
        mItemUomListParameter.setFilterLastModifiedDate(lastModified == null ? "" : lastModified);

        logParams(mItemUomListParameter);


        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownLoadItemUom));

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            if(isNetworkAvailable()) {
                Call<ApiItemUomListResult> call = mApp.getNavBrokerService().GetItemUomList(mItemUomListParameter);
                itemUomListResult = call.execute().body();
                addRecords(true);
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
        syncStatus.setScope(syncConfig.getScope());
        if (isForcedSync) {
            if (success) {
                syncStatus.setStatus(true);
                delegate.onAsyncTaskFinished(syncStatus);
            } else {
                syncStatus.setStatus(false);
                delegate.onAsyncTaskFinished(syncStatus);
            }
        }
    }

    @Override
    protected void onCancelled() {
    }

    private String getItemCodes() {

        List<Item> itemList;
        int count = 0;
        Item tempItem;
        String itemCodes = "";

        ItemDbHandler dbAdapter = new ItemDbHandler(context);
        dbAdapter.open();
        itemList = dbAdapter.getAllItems();
        dbAdapter.close();

        if (itemList.size() > 0) {
            for (int i = count; count < itemList.size(); count++) {

                tempItem = itemList.get(count);
                itemCodes = itemList.size() == (count + 1) ? itemCodes + tempItem.getItemCode()
                        : itemCodes + tempItem.getItemCode() + "|";
            }
        }

        return itemCodes;
    }

    private SyncConfiguration getSyncConfiguration() {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);
        SyncConfiguration sc = new SyncConfiguration();

        try {
            syncDbHandler.open();
            sc = syncDbHandler.getLastSyncInfoByScope(context.getResources()
                    .getString(R.string.SyncScopeDownLoadItemUom));
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
            //Log.d("SYNC_ITEM_UOM_RESULT ", json);
            mLog.info("SYNC_ITEM_UOM_RESULT :" + json);

        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }

    private void addRecords(final boolean success) {

        if (success) {

            ItemUomDbHandler dbAdapter = new ItemUomDbHandler(context);
            dbAdapter.open();

            //set current sync status.
            syncConfig.setLastSyncDateTime(itemUomListResult.getServerDate());
            syncConfig.setDataCount(itemUomListResult.getItemUOMListResultData().size());
            syncConfig.setSuccess(true);

            try {

                for (ApiItemUomListResult.ItemUomListResultData
                        itemUomResult : itemUomListResult.getItemUOMListResultData()) {

                    if (dbAdapter.deleteItem(itemUomResult.getKey())) {

                        ItemUom itemUom = new ItemUom();

                        itemUom.setUomCode(itemUomResult.getCode());
                        itemUom.setItemCode(itemUomResult.getItem_No());
                        itemUom.setKey(itemUomResult.getKey());
                        itemUom.setConvertion(itemUomResult.getQty_per_Unit_of_Measure());

                        dbAdapter.addItemUom(itemUom);
                        Log.d("SYNC_ITEM_UOM_ADDED: ", itemUomResult.getItem_No());
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

        //Log sync information to Sync Configuration table.
        setSyncConfiguration(syncConfig);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void logParams(ApiItemUomListParameter params) {

        params.setPassword("****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_UOM_PARAMS :" + json);
        params.setPassword(mApp.getCurrentUserPassword());

    }

}
