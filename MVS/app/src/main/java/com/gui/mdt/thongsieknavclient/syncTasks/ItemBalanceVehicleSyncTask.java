package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiVehicleOpenQuantityResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;
import com.gui.mdt.thongsieknavclient.datamodel.StockStatus;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemBalancePdaDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockStatusDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.ui.ApiVehicleOpenQuantityParameters;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 11/06/2017.
 */

public class ItemBalanceVehicleSyncTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    ApiVehicleOpenQuantityParameters apiItemListParameter;
    ApiVehicleOpenQuantityResponse apiVehicleOpenQuantityResponse;
    List<ApiVehicleOpenQuantityResponse.ApiVehicleOpenQuantityListResultData> itemListResult;
    private NavClientApp mApp;
    private boolean mIsForcedSync = false;
    private String mItemNo ="", mDateToday="";
    private boolean mIsOnlineRequest;
    Logger mLog;

    public ItemBalanceVehicleSyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.mIsForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog= Logger.getLogger(ItemBalancePdaSyncTask.class);
        this.mIsOnlineRequest=true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj = new Date();
        mDateToday = df.format(dateObj).toString();

        //set parameters for request
        apiItemListParameter = new ApiVehicleOpenQuantityParameters();
        apiItemListParameter.setUserName(mApp.getCurrentUserName());
        apiItemListParameter.setPassword(mApp.getCurrentUserPassword());
        apiItemListParameter.setUserCompany(mApp.getmUserCompany());
        apiItemListParameter.setDriver_Code(mApp.getmCurrentDriverCode());
        apiItemListParameter.setReceipt_Date(mDateToday);


        //apiItemListParameter.setDriver_Code("V0018");
        //apiItemListParameter.setReceipt_Date("2018-01-24");

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
                Call<ApiVehicleOpenQuantityResponse> call =
                        mApp.getNavBrokerService().GetVehicleOpenQuantity(apiItemListParameter);
                apiVehicleOpenQuantityResponse = call.execute().body();

                if (apiVehicleOpenQuantityResponse != null &&
                        apiVehicleOpenQuantityResponse.getVehicleOpenQuantityListResultData().size()>0) {
                    addRecords(true);
                }
            }
        } catch(IOException e){
            Log.d("NAV_Client_Exception", e.toString());
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        SyncStatus syncStatus = new SyncStatus();
        //syncConfig.setSuccess(true);
        if(mIsOnlineRequest) {

            if (itemListResult != null && itemListResult.size()>0) {
                syncConfig.setLastSyncDateTime(apiVehicleOpenQuantityResponse.getServerDate());
                syncConfig.setDataCount(itemListResult.size());
                syncConfig.setSuccess(true);

                syncStatus.setScope(context.getResources().getString(R.string.SyncScopeDownItemBalance));
            } else {
                syncConfig.setLastSyncDateTime(DateTime.now().toString());
                syncConfig.setDataCount(0);
                syncConfig.setSuccess(true);
                syncStatus.setScope(context.getResources().getString(R.string.SyncScopeDownItemBalance));
            }

            if (mIsForcedSync) {
                if (syncConfig.getSuccess()) {
                    syncStatus.setStatus(true);
                    delegate.onAsyncTaskFinished(syncStatus);
                } else {
                    syncStatus.setStatus(true);
                    delegate.onAsyncTaskFinished(syncStatus);
                }
            }

        }
        else {
            if (mIsForcedSync) {
                syncConfig.setSuccess(true);
                syncStatus.setScope(context.getResources().getString(R.string.SyncScopeDownItemBalance));
                delegate.onAsyncTaskFinished(syncStatus);
            }
        }

        //setSyncConfiguration(syncConfig);
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
            mLog.info("SYNC_VEHICLE_BAL_RESULT :" + json);

        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }

    private void addRecords(final boolean success) {

        if (success) {
            ItemBalancePdaDbHandler dbAdapter = new ItemBalancePdaDbHandler(context);
            dbAdapter.open();

            itemListResult = apiVehicleOpenQuantityResponse.getVehicleOpenQuantityListResultData();

            StockStatus stockStatus = new StockStatus();

            StockStatusDbHandler ssDb = new StockStatusDbHandler(context);
            ssDb.open();
            stockStatus = ssDb.getStockStatus(mDateToday);
            ssDb.close();

            if(stockStatus.isDispatched()!= null)
            {
                if (!stockStatus.isDispatched()) {
                    try {
                        if (dbAdapter.resetOpenQuantities(mApp.getmCurrentDriverCode())) {
                            for (ApiVehicleOpenQuantityResponse.ApiVehicleOpenQuantityListResultData
                                    itemResult : itemListResult) {

                                ItemBalancePda item = new ItemBalancePda();
                                item.setKey(itemResult.getKey());
                                item.setItemNo(itemResult.getItem_No());
                                item.setOpenQty(itemResult.getQuantity());
                                //item.setOpenQty(100); // only for testing
                                //item.setExchangedQty(0);
                                //item.setQuantity(0);
                                item.setUnitofMeasureCode(itemResult.getUnit_of_Measure());
                                item.setLocationCode(itemResult.getTransfer_to_Code());


                                if (!dbAdapter.isItemExistbyItemNo(itemResult.getItem_No())) {
                                    dbAdapter.addItemBalancePda(item);
                                    Log.d("SYNC_VEHICL_BAL_ADDED: ", itemResult.getItem_No());
                                    mLog.info("SYNC_VEHICL_BAL_ADDED :" + itemResult.getItem_No()+"         "+ itemResult.getQuantity());
                                } else {
                                    dbAdapter.updateOpenQty(item);
                                    mLog.info("SYNC_VEHICL_BAL_UPDATED :" + itemResult.getItem_No()+"         "+ itemResult.getQuantity());
                                }

                            }
                        }
                        dbAdapter.close();

                        SyncStatus syncStatus = new SyncStatus();
                        syncConfig.setLastSyncDateTime(apiVehicleOpenQuantityResponse.getServerDate());
                        syncConfig.setDataCount(itemListResult.size());
                        syncConfig.setSuccess(true);
                        syncStatus.setScope(context.getResources().getString(R.string.SyncScopeDownItemBalance));
                        setSyncConfiguration(syncConfig);

                    } catch (Exception ex) {
                        dbAdapter.close();
                        syncConfig.setSuccess(false);
                    }
                }
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

    private void logParams(ApiVehicleOpenQuantityParameters params) {

        params.setPassword("****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_VEHICLE_BAL_PARAM :" + json);
        params.setPassword(mApp.getCurrentUserPassword());


    }
}
