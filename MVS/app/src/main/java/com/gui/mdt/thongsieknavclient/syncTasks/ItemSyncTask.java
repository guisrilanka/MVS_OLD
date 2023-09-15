package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemListParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemListResult;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 07/19/2017.
 */


public class ItemSyncTask extends AsyncTask<Void, Void, Boolean> {

    Context context;
    private NavClientApp mApp;
    public AsyncResponse delegate = null;
    SyncConfiguration syncConfig;
    private boolean isForcedSync = false;
    Logger mLog;

    ApiItemListParameter apiItemListParameter;
    ApiItemListResult apiItemListResult;
    List<ApiItemListResult.ApiItem> itemListResult;


    public ItemSyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog = Logger.getLogger(ItemSyncTask.class);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //set parameters for request
        apiItemListParameter = new ApiItemListParameter();
        apiItemListParameter.setUserName(mApp.getCurrentUserName());
        apiItemListParameter.setPassword(mApp.getCurrentUserPassword());
        apiItemListParameter.setUserCompany(mApp.getmUserCompany());
        apiItemListParameter.setItemCode("");
        //2019/07/17 changed by lasith . with this last sync date .not download all items . so we discuss to send empty date
//        String lastModified = getSyncConfiguration().getLastSyncDateTime();
//        apiItemListParameter.setFilterLastModifiedDate(lastModified == null ? "" : lastModified);
        apiItemListParameter.setFilterLastModifiedDate("");

        logParams(apiItemListParameter);

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownLoadItem));

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            if (isNetworkAvailable()) {
                Call<ApiItemListResult> call = mApp.getNavBrokerService().GetItems(apiItemListParameter);
                apiItemListResult = call.execute().body();
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
            //Log.d("SYNC_ITEM_RESULT ", json);
            mLog.info("SYNC_ITEM_RESULT :" + json);

        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }

    private void addRecords(final boolean success) {

        if (success) {
            ItemDbHandler dbAdapter = new ItemDbHandler(context);
            dbAdapter.open();

            if (apiItemListResult != null) {
                itemListResult = apiItemListResult.getItemListResultData();

                //set current sync status.
                syncConfig.setLastSyncDateTime(apiItemListResult.getServerDate());
                syncConfig.setDataCount(itemListResult.size());
                syncConfig.setSuccess(true);
            } else {
                syncConfig.setSuccess(false);
            }

            try {

                for (ApiItemListResult.ApiItem itemResult : itemListResult) {
                    //Log.d("SYNC_CUSTOMER: ",cus.getNo());
                    if (dbAdapter.deleteItem(itemResult.getNo())) {

                        Item item = new Item();
                        item.setItemCode(itemResult.getNo());
                        item.setDescription(itemResult.getDescription());
                        item.setItemCategoryCode(itemResult.getItem_Category_Code());
                        item.setItemBaseUom(itemResult.getBase_Unit_of_Measure());

                        item.setProductGroupCode(itemResult.getProduct_Group_Code());
                        item.setQtyOnPurchOrder(itemResult.getQty_on_Purch_Order());
                        item.setQtyOnSalesOrder(itemResult.getQty_on_Sales_Order());
                        item.setBlocked(itemResult.getBlocked());
                        item.setUnitPrice(itemResult.getUnit_Price());
                        item.setNetInvoicedQty(itemResult.getNet_Invoiced_Qty());
                        item.setIdentifierCode(itemResult.getIdentifier_Code());
                        item.setVatProdPostingGroup(itemResult.getVAT_Prod_Posting_Group());

                        dbAdapter.addItems(item);
                        Log.d("SYNC_ITEM_ADDED: ", itemResult.getNo());
                        //delegate.onAsyncTaskFinished(itemResult.getNo() + " --DOWNLOADED-- ");

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

    private void logParams(ApiItemListParameter params) {

        params.setPassword("****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_ITEM_PARAMS :" + json);
        params.setPassword(mApp.getCurrentUserPassword());


    }

}


