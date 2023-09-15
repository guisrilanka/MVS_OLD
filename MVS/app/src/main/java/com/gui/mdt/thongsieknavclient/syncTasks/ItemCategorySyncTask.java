package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ItemCategory;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemCategoryDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemCategoryListParameter;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemCategoryListResultData;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by bhanuka on 03/08/2017.
 */


public class ItemCategorySyncTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    ItemCategoryListParameter itemCategoryParameter;
    List<ItemCategoryListResultData> itemCategoryListResult;
    private NavClientApp mApp;
    private boolean isForcedSync = false;
    Logger mLog;

    public ItemCategorySyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog= Logger.getLogger(ItemCategorySyncTask.class);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //set parameter for request.
        itemCategoryParameter = new ItemCategoryListParameter();
        itemCategoryParameter.setUserCompany(mApp.getmUserCompany());
        itemCategoryParameter.setPassword(mApp.getCurrentUserPassword());
        itemCategoryParameter.setUserName(mApp.getCurrentUserName());

        logParams(itemCategoryParameter);


        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownLoadItemCategory));

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            if(isNetworkAvailable()) {
                Call<List<ItemCategoryListResultData>> call = mApp.getNavBrokerService().ItemCategoryList(itemCategoryParameter);

                itemCategoryListResult = call.execute().body();
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
        if(isForcedSync){
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

    private SyncConfiguration getSyncConfiguration() {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);
        SyncConfiguration sc = new SyncConfiguration();

        try {
            syncDbHandler.open();
            sc = syncDbHandler.getLastSyncInfoByScope(context.getResources()
                    .getString(R.string.SyncScopeDownLoadItem));
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
            //Log.d("SYNC_CAT_RESULT ", json);
            mLog.info("SYNC_CAT_RESULT :" + json);
        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }

    private void addRecords(final boolean success) {

        if (success) {
            ItemCategoryDbHandler dbAdapter = new ItemCategoryDbHandler(context);
            dbAdapter.open();

            //set current sync status.
            syncConfig.setLastSyncDateTime(DateTime.now().toString());
            syncConfig.setDataCount(itemCategoryListResult.size());
            syncConfig.setSuccess(true);

            try {

                for (ItemCategoryListResultData itemCategoryResult : itemCategoryListResult) {

                    if (dbAdapter.deleteItem(itemCategoryResult.getCode())) {

                        ItemCategory itemCategory = new ItemCategory();
                        itemCategory.setItemCategoryCode(itemCategoryResult.getCode());
                        itemCategory.setDescription(itemCategoryResult.getDescription());

                        dbAdapter.addItemCategory(itemCategory);
                        Log.d("SYNC_CAT_ADDED: ", itemCategoryResult.getCode());
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

    private void logParams(ItemCategoryListParameter params) {

        params.setPassword("****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_CAT_PARAMS :" + json);
        params.setPassword(mApp.getCurrentUserPassword());

    }

}
