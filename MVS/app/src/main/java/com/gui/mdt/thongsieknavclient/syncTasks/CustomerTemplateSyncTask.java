package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerTemplateParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerTemplateResponse;
import com.gui.mdt.thongsieknavclient.datamodel.CustomerTemplate;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerTemplateDbHandler;
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

public class CustomerTemplateSyncTask extends AsyncTask<Void, Void, Boolean> {
    Context context;
    private NavClientApp mApp;
    public AsyncResponse delegate = null;
    SyncConfiguration syncConfig;
    private boolean isForcedSync = false;
    private boolean isInitialSyncRun = false;
    private String customerCode = "";
    Logger mLog;

    List<ApiCustomerTemplateResponse.ApiStandardSalesCodeResultData> customerSalesTemplateList;
    ApiCustomerTemplateResponse apiCustomerSalesTemplateResponse;
    ApiCustomerTemplateParameter apiCustomerTemplateParameter;

    public CustomerTemplateSyncTask(Context context, boolean isForcedSync,boolean isInitialSync ) {
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
        apiCustomerTemplateParameter = new ApiCustomerTemplateParameter();
        apiCustomerTemplateParameter.setFilterSalesCode(this.customerCode);
        apiCustomerTemplateParameter.setUserCompany(mApp.getmUserCompany());
        apiCustomerTemplateParameter.setUserName(mApp.getCurrentUserName());
        apiCustomerTemplateParameter.setPassword(mApp.getCurrentUserPassword());
        logParams(apiCustomerTemplateParameter);

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownloadCustomerTemplate));

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if(isNetworkAvailable()) {
                Call<ApiCustomerTemplateResponse> call = mApp.getNavBrokerService()
                        .GetCustomerTemplates(apiCustomerTemplateParameter);
                apiCustomerSalesTemplateResponse = call.execute().body();

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
                customerSalesTemplateList = apiCustomerSalesTemplateResponse.getStandardSalesCodeResultData();

                //set current sync status.
                syncConfig.setLastSyncDateTime(apiCustomerSalesTemplateResponse.getServerDate());
                syncConfig.setDataCount(customerSalesTemplateList.size());
                syncConfig.setSuccess(true);

                //delete if customer exist and insert newly downloaded customer.
                CustomerTemplateDbHandler dbAdapter = new CustomerTemplateDbHandler(context);
                dbAdapter.open();
                if(dbAdapter.deleteCustomerTemplate()){
                    for (ApiCustomerTemplateResponse.ApiStandardSalesCodeResultData ct : customerSalesTemplateList) {
                        //if (dbAdapter.deleteCustomerTemplate(ct.getStandard_Sales_Code(),ct.getNo())) {

                            CustomerTemplate customerTemplate = new CustomerTemplate();
                            customerTemplate.setSalesCode(ct.getStandard_Sales_Code());
                            customerTemplate.setItemNo(ct.getNo());
                            customerTemplate.setDescription(ct.getDescription());
                            customerTemplate.setQuantity(ct.getQuantity());
                            customerTemplate.setItemUom(ct.getUnit_of_Measure_Code());

                            dbAdapter.addCustomerTemplate(customerTemplate);
                            Log.d("SYNC_CUS_TEMP_ADDED: ", ct.getStandard_Sales_Code()+" - "+ct.getNo());
                        //}
                    }
                }


                dbAdapter.close();
            } catch (Exception ex) {
                syncConfig.setSuccess(false);
            }
        } else {
            syncConfig.setSuccess(false);
            Log.d("SYNC_CUS_TEMP_ADDED: ", "Error");
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

    private void logParams(ApiCustomerTemplateParameter params) {

        params.setPassword("*****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        mLog.info("SYNC_CUS_TEMP_PARAMS :" + json);
        params.setPassword(mApp.getCurrentUserPassword());


    }
}

