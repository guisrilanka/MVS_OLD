package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostSalesOrderStatusResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostSalesOrderStatusUpdateParameter;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by GUISL-NB05 on 9/29/2017.
 */

public class SalesOrderDeliveryUploadSyncTask extends AsyncTask<Void, Void, Boolean> {
    public AsyncResponse delegate = null;
    NavClientApp mApp;
    boolean isForcedSync = false;
    Context context;
    SyncConfiguration syncConfig;
    Logger mLog;
    List<SalesOrder> salesOrderToBeSync;
    String documentNo="";

    List<ApiPostSalesOrderStatusUpdateParameter>  apiPostSalesOrderStatusUpdateParameterList;
    ApiPostSalesOrderStatusResponse apiPostSalesOrderStatusResponse;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeSoDeliveryFlag));
        syncConfig.setLastSyncDateTime(DateTime.now().toString());

    }

    public SalesOrderDeliveryUploadSyncTask(Context context, boolean isForcedSync,String mdocumentNo) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog = Logger.getLogger(PaymentUploadSyncTask.class);
        this.documentNo=mdocumentNo;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(context);
        dbAdapter.open();
        boolean isSuccess=false;

        try
        {
            salesOrderToBeSync = dbAdapter.getSalesOrderForUploading(documentNo);
            dbAdapter.close();

            if (salesOrderToBeSync.size() > 0 && isNetworkAvailable())
            {
                upLoadSalesOrders();
                isSuccess=true;
            }
            else{
                return true;
            }

            syncConfig.setDataCount(salesOrderToBeSync.size());
            setSyncConfiguration(syncConfig);
        }
        catch (Exception e)
        {
            Log.d("NAV_Client_Exception", e.toString());
            dbAdapter.close();
            return false;
        }


        return isSuccess;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        SyncStatus syncStatus = new SyncStatus();
        syncStatus.setScope(syncConfig.getScope());
        if (isForcedSync) {
            if (success) {
                syncStatus.setStatus(apiPostSalesOrderStatusResponse.getTrStatusList().get(0).isTransferred());

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

    private void upLoadSalesOrders(){

        apiPostSalesOrderStatusUpdateParameterList = new ArrayList<ApiPostSalesOrderStatusUpdateParameter>();
        try
        {
            for (SalesOrder deliverdSo : salesOrderToBeSync) {

                ApiPostSalesOrderStatusUpdateParameter param = new
                        ApiPostSalesOrderStatusUpdateParameter();


                param.setKey(deliverdSo.getKey().toString());
                param.setNo(deliverdSo.getNo().toString());
                param.setDelivered(String.valueOf(deliverdSo.isDelivered()));
                param.setUserName(mApp.getCurrentUserName());
                param.setPassword(mApp.getCurrentUserPassword());
                param.setUserCompany(mApp.getmUserCompany());

                apiPostSalesOrderStatusUpdateParameterList.add(param);
            }


            logDeliveryParams(apiPostSalesOrderStatusUpdateParameterList);

            Call<ApiPostSalesOrderStatusResponse> call = mApp.getNavBrokerService()
                    .PostSalesOrderStatus(apiPostSalesOrderStatusUpdateParameterList);

            apiPostSalesOrderStatusResponse = call.execute().body();

            logDeliveryResponse(apiPostSalesOrderStatusResponse);

        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    private SyncConfiguration getSyncConfiguration() {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);
        SyncConfiguration sc = new SyncConfiguration();

        try {
            syncDbHandler.open();
            sc = syncDbHandler.getLastSyncInfoByScope(context.getResources()
                    .getString(R.string.SyncScopeUploadSO));
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
        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void logDeliveryParams(List<ApiPostSalesOrderStatusUpdateParameter> params){

        for (ApiPostSalesOrderStatusUpdateParameter p:params) {
            p.setPassword("****");
        }

        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DELIVERY_PARAMS", json);
        mLog.info("SYNC_SO_DELIVERY_PARAMS :" + json);

        for (ApiPostSalesOrderStatusUpdateParameter p:params) {
            p.setPassword(mApp.getCurrentUserPassword());
        }

    }

    private void logDeliveryResponse(ApiPostSalesOrderStatusResponse response){
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(response);
        //Log.d("SYNC_SO_DELIVER_UP_RES", json);
        mLog.info("SYNC_SO_DELIVER_UP_RES :" + json);
    }

}
