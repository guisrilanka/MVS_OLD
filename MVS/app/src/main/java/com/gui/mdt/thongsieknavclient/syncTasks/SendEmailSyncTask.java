package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostEmailMessageParameter;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.model.BaseResult;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;

/**
 * Created by nelin_000 on 11/21/2017.
 */

public class SendEmailSyncTask extends AsyncTask<Void, Void, Boolean> {

    Context context;
    private NavClientApp mApp;
    public AsyncResponse delegate = null;
    SyncConfiguration syncConfig;
    private boolean isForcedSync = false;
    Logger mLog;
    String mFilterCreatedDate;
    String fileName;
    String mEmailSubject = "", mEmailBody = "";

    ApiPostEmailMessageParameter apiPostEmailMessageParameter;
    BaseResult apiPostEmailMessageResponse;


    public SendEmailSyncTask(Context context
            , boolean isForcedSync
            ,String mFilterCreatedDate
            ,String fileName
            ,String emailBody
            ,String emailSubject) {

        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog = Logger.getLogger(SendEmailSyncTask.class);
        this.mFilterCreatedDate=mFilterCreatedDate;
        this.fileName=fileName;
        this.mEmailSubject = emailSubject;
        this.mEmailBody = emailBody;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //set parameters for request
        /*apiPostEmailMessageParameter = new ApiPostEmailMessageParameter();
        apiPostEmailMessageParameter.setUserName(mApp.getCurrentUserName());
        apiPostEmailMessageParameter.setPassword(mApp.getCurrentUserPassword());
        apiPostEmailMessageParameter.setUserCompany(mApp.getmUserCompany());
        apiPostEmailMessageParameter.setSubject( ""
                +"Payment Summery - "
                +  mApp.getmCurrentSalesPersonCode() +" - "
                + mFilterCreatedDate
        );
        apiPostEmailMessageParameter.setBody(""
                +"Report: Payment Summary \n"
                + "Salesperson: " + mApp.getmCurrentSalesPersonCode() + "\n"
                + "Date :" + mFilterCreatedDate);
        apiPostEmailMessageParameter.setFileName(fileName);
        apiPostEmailMessageParameter.setSalesPersonCode(mApp.getmCurrentSalesPersonCode());*/

        apiPostEmailMessageParameter = new ApiPostEmailMessageParameter();
        apiPostEmailMessageParameter.setUserName(mApp.getCurrentUserName());
        apiPostEmailMessageParameter.setPassword(mApp.getCurrentUserPassword());
        apiPostEmailMessageParameter.setUserCompany(mApp.getmUserCompany());
        apiPostEmailMessageParameter.setSubject(mEmailSubject);
        apiPostEmailMessageParameter.setBody(mEmailBody);
        apiPostEmailMessageParameter.setFileName(fileName);
        apiPostEmailMessageParameter.setSalesPersonCode(mApp.getmCurrentSalesPersonCode());

        logParams(apiPostEmailMessageParameter);

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeSendEmail));

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            if(isNetworkAvailable()) {
                Call<BaseResult> call = mApp.getNavBrokerService().PostEmailMessage(apiPostEmailMessageParameter);
                apiPostEmailMessageResponse = call.execute().body();
                logResponse(apiPostEmailMessageResponse);
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
            if (success
                    &&  apiPostEmailMessageResponse!=null
                    &&  apiPostEmailMessageResponse.getStatus()==BaseResult.BaseResultStatusOk) {
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

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void logParams(ApiPostEmailMessageParameter params) {

        params.setPassword("****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_SEND_EMAIL_PARAMS :" + json);
        params.setPassword(mApp.getCurrentUserPassword());


    }

    private void logResponse(BaseResult response) {

        Gson gson = new Gson();
        String json = gson.toJson(response);
        mLog.info("SYNC_SEND_EMAIL_RESPONSE :" + json);
    }

}