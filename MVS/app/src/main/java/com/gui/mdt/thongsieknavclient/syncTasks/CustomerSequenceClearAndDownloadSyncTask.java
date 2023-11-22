package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import retrofit2.Call;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerSequenceParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerSequenceResponse;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerSequenceDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.gui.mdt.thongsieknavclient.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CustomerSequenceClearAndDownloadSyncTask extends AsyncTask<Void,Void,Boolean> {
    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    Logger mLog;
    private NavClientApp mApp;
    private  boolean isForcedSync=false;
    private  boolean isInitialSyncCompleted=false;
    ApiCustomerSequenceParameter apiCustomerSequenceParameter;
    ApiCustomerSequenceResponse apiCustomerSequenceResponse;
    List<ApiCustomerSequenceResponse.ApiCustomer> customerList;

    @Override
    protected  void onPreExecute(){
        super.onPreExecute();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String deliveryDate = dateFormat.format(Calendar.getInstance().getTime());

        apiCustomerSequenceParameter=new ApiCustomerSequenceParameter();
        apiCustomerSequenceParameter.setDriverCode(mApp.getmCurrentDriverCode().toUpperCase());
        apiCustomerSequenceParameter.setDeliveryDate(deliveryDate);
        logParams(apiCustomerSequenceParameter);

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownLoadCustomerSequence));
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if(isNetworkAvailable()){
                Call<ApiCustomerSequenceResponse> call=mApp.getWhNavBrokerService().getCustomerSequence(apiCustomerSequenceParameter);
                apiCustomerSequenceResponse = call.execute().body();

                logResponse(apiCustomerSequenceResponse);
                deleteAllDownloadedCustomerSequence();
                addCustomerRecords(true);
            }
        }catch (Exception e){
            Log.d("NAV_Client_Exception", e.toString());
            return  false;
        }
        return true;
    }
    @Override
    protected void onPostExecute(final Boolean success){
        SyncStatus syncStatus=new SyncStatus();
        syncStatus.setScope(syncConfig.getScope());
        if(isForcedSync){
            if(success){
                syncStatus.setStatus(true);
                delegate.onAsyncTaskFinished(syncStatus);
            }else{
                syncStatus.setStatus(false);
                delegate.onAsyncTaskFinished(syncStatus);
            }
        }
    }

    public CustomerSequenceClearAndDownloadSyncTask(Context context, boolean isForcedSync, boolean isInitialSync) {
        this.context = context;
        this.isForcedSync=isForcedSync;
        this.mApp=(NavClientApp) context;
        this.isInitialSyncCompleted=isInitialSync;
        this.mLog = Logger.getLogger(CustomerSequenceClearAndDownloadSyncTask.class);

    }
    private  boolean isNetworkAvailable(){
        ConnectivityManager _cConnectivityManager= (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=_cConnectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null&&activeNetworkInfo.isConnected();

    }
    private  void addCustomerRecords(final boolean success){
        if (success){

            CustomerSequenceDbHandler dbAdapter=new CustomerSequenceDbHandler(context);
            dbAdapter.open();
            try {
                customerList=apiCustomerSequenceResponse.getRouteSequenceMvs();

                //set current sync status.
//                syncConfig.setLastSyncDateTime(apiCustomerSequenceResponse.getServerDate());
//                syncConfig.setDataCount(soList.size());
//                syncConfig.setSuccess(true);
                for (ApiCustomerSequenceResponse.ApiCustomer customer:customerList){
                    Customer cus =new Customer();
                    cus.setCode(customer.getCustomer_code());

                    dbAdapter.addCustomer(cus);
                    Log.d("SYNC_CUSTOMER_SEQUENCE_ADDED: ", customer.getCustomer_code());
                }
            }catch (Exception ex) {
                syncConfig.setSuccess(false);
                Log.d("SYNC_CUSTOMER_SEQUENCE_ERROR: ", ex.toString());
                dbAdapter.close();
            }
            dbAdapter.close();
        }else {
            syncConfig.setSuccess(false);
            Log.d("SYNC_CUSTOMER_SEQUENCE_ERROR: ", "Api Calling Error");
        }
    }
    private void logParams(ApiCustomerSequenceParameter params) {

        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_CUSTOMER_SEQUENCE_DOWN_PARAMS :" + json);
    }
    private SyncConfiguration getSyncConfiguration() {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);
        SyncConfiguration sc = new SyncConfiguration();

        try {
            syncDbHandler.open();
            sc = syncDbHandler.getLastSyncInfoByScope(context.getResources()
                    .getString(R.string.SyncScopeDownLoadCustomerSequence));
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
            //Log.d("SYNC_SO_RESULT ", json);
            mLog.info("SYNC_CUSTOMER_SEQUENCE_RESULT :" + json);

        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }
    private String getInitialSyncDate() {
        String lastModifiedDate = "";

        DateTime currDate = DateTime.now().minusDays(4);
        lastModifiedDate = currDate.toString();

        return lastModifiedDate;
    }
    private boolean deleteAllDownloadedCustomerSequence(){
        CustomerSequenceDbHandler dbAdapter = new CustomerSequenceDbHandler(mApp.getApplicationContext());
        dbAdapter.open();
        boolean deleted = dbAdapter.deleteAllDownloadedCustomerSequence();
        dbAdapter.close();

        return deleted;
    }
    private void logResponse(ApiCustomerSequenceResponse response) {
        Gson gson = new Gson();
        String json = gson.toJson(response);
        mLog.info("SYNC_CUSTOMER_SEQUENCE_DOWN_RES :" + json);
    }
}
