package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerSalesCodeParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerSalesCodeResponse;
import com.gui.mdt.thongsieknavclient.datamodel.CustomerSalesCode;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerSalesCodeDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 12/21/2017.
 */

public class CustomerSalesCodeSyncTask extends AsyncTask<Void, Void, Boolean> {
    Context context;
    private NavClientApp mApp;
    public AsyncResponse delegate = null;
    SyncConfiguration syncConfig;
    private boolean isForcedSync = false;
    private boolean isInitialSyncRun = false;
    private String customerCode = "";
    Logger mLog;

    List<ApiCustomerSalesCodeResponse.ApiSalesCodeResultData> customerSalesCodeList;
    ApiCustomerSalesCodeResponse apiCustomerSalesCodeResponse;
    ApiCustomerSalesCodeParameter apiCustomerSalesCodeParameter;

    public CustomerSalesCodeSyncTask(Context context, boolean isForcedSync,boolean isInitialSync ) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.isInitialSyncRun = isInitialSync;
        this.mLog= Logger.getLogger(CustomerSyncTask.class);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //set parameter for request
        apiCustomerSalesCodeParameter = new ApiCustomerSalesCodeParameter();
        apiCustomerSalesCodeParameter.setFilterCustomerCode(this.customerCode);
        apiCustomerSalesCodeParameter.setUserCompany(mApp.getmUserCompany());
        apiCustomerSalesCodeParameter.setUserName(mApp.getCurrentUserName());
        apiCustomerSalesCodeParameter.setPassword(mApp.getCurrentUserPassword());
        //logParams(apiCustomerSalesCodeParameter);

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownloadCustomerSalesCodes));

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if(isNetworkAvailable()) {
                Call<ApiCustomerSalesCodeResponse> call = mApp.getNavBrokerService()
                        .GetCustomerSalesCodes(apiCustomerSalesCodeParameter);
                apiCustomerSalesCodeResponse = call.execute().body();
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
                    .getString(R.string.SyncScopeDownloadCustomerSalesCodes));
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
            //Log.d("SYNC_CUSTOMER_RESULT ", json);
            //mLog.info("SYNC_CUSTOMER_RESULT :" + json);

        } catch (Exception ex) {
            //
            mLog.error("Error",ex);
        }
        syncDbHandler.close();

        Gson gson = new Gson();
        String json = gson.toJson(syncConfig);
        mLog.info("SYNC_CUSTOMER_RESULT :" + json);
    }

    private void addRecords(final boolean success) {

        if (success) {

            try {
                customerSalesCodeList = apiCustomerSalesCodeResponse.getSalesCodeResultData();

                //set current sync status.
                syncConfig.setLastSyncDateTime(apiCustomerSalesCodeResponse.getServerDate());
                syncConfig.setDataCount(customerSalesCodeList.size());
                syncConfig.setSuccess(true);

                //delete if customer exist and insert newly downloaded customer.
                CustomerSalesCodeDbHandler dbAdapter = new CustomerSalesCodeDbHandler(context);
                dbAdapter.open();
                if(dbAdapter.deleteCustomerSalesCode()){
                    for (ApiCustomerSalesCodeResponse.ApiSalesCodeResultData csd : customerSalesCodeList) {
                        //if (dbAdapter.deleteCustomerSalesCode(csd.getCode())) {

                            CustomerSalesCode customerSalesCode = new CustomerSalesCode();
                            customerSalesCode.setCode(csd.getCode());
                            customerSalesCode.setCustomerNo(csd.getCustomer_No());
                            customerSalesCode.setDescription(csd.getDescription());
                            customerSalesCode.setValidFromDate(csd.getValid_From_Date());
                            customerSalesCode.setValidToDate(csd.getValid_To_date());
                            customerSalesCode.setBlocked(csd.isBlocked());

                            dbAdapter.addCustomerSalesCode(customerSalesCode);
                            Log.d("SYNC_SALES_CODE_ADDED: ", csd.getCode());
                        //}
                    }
                }


                dbAdapter.close();
            } catch (Exception ex) {
                syncConfig.setSuccess(false);
            }
        } else {
            syncConfig.setSuccess(false);
            Log.d("SYNC_SALES_CODE: ", "Error");
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

    private void logParams(ApiCustomerSalesCodeParameter params) {

        params.setPassword("*****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        mLog.info("SYNC_SALES_CODE_PARAMS :" + json);
        params.setPassword(mApp.getCurrentUserPassword());


    }
}
