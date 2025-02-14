package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostRunningNumbersParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostRunningNumbersResponse;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.datamodel.UserSetup;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by nelin_000 on 09/12/2017.
 */

public class UserSetupRunningNoUploadTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    ApiPostRunningNumbersParameter mApiPostRunningNumbersParameter;
    ApiPostRunningNumbersResponse mApiPostRunningNumbersResponse;
    private NavClientApp mApp;
    private boolean isForcedSync = false;
    private UserSetup mUserSetup;
    Logger mLog;

    String mLocationName;

    public UserSetupRunningNoUploadTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog= Log4jHelper.getLogger();
        mLocationName = StockRequestUploadSyncTask.class.getSimpleName();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mUserSetup = getUserSetup();
        mApiPostRunningNumbersParameter= new ApiPostRunningNumbersParameter();

        //set parameters for request
        mApiPostRunningNumbersParameter.setUserName(mApp.getCurrentUserName());
        mApiPostRunningNumbersParameter.setPassword(mApp.getCurrentUserPassword());
        mApiPostRunningNumbersParameter.setUserCompany(mApp.getmUserCompany());
        mApiPostRunningNumbersParameter.setSONumberMSO(mUserSetup.getSoRunningNoMso());
        mApiPostRunningNumbersParameter.setSONumberMVS(mUserSetup.getSoRunningNoMvs());
        mApiPostRunningNumbersParameter.setPaymentNumber(mUserSetup.getPaymentRunningNo());
        mApiPostRunningNumbersParameter.setSINumber(mUserSetup.getSiRunningNo());
        mApiPostRunningNumbersParameter.setSRNumber(mUserSetup.getSrRunningNoMvs());

        logParams(mApiPostRunningNumbersParameter);

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeUploadRunningNos));

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            if(isNetworkAvailable()) {
                Call<ApiPostRunningNumbersResponse> call = mApp.getNavBrokerService().PostRunningNumbers
                        (mApiPostRunningNumbersParameter);
                mApiPostRunningNumbersResponse = call.execute().body();
            }


        } catch (IOException e) {
            //Log.d("NAV_Client_Exception", e.toString());
            mLog.error("NAV_Client_Exception", e);
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        SyncStatus syncStatus = new SyncStatus();
        syncStatus.setScope(syncConfig.getScope());

        if (mApiPostRunningNumbersResponse != null && mApiPostRunningNumbersResponse.getStatus() == BaseResult
                .BaseResultStatusOk) {

            //Log sync information to Sync Configuration table.
            syncConfig.setLastSyncDateTime(DateTime.now().toString());
            syncConfig.setSuccess(true);
            setSyncConfiguration(syncConfig);

            if (isForcedSync) {
                syncStatus.setStatus(true);
                if(delegate!=null) {
                    delegate.onAsyncTaskFinished(syncStatus);
                }
            }

        } else {
            syncConfig.setLastSyncDateTime(DateTime.now().toString());
            syncConfig.setSuccess(false);
            setSyncConfiguration(syncConfig);

            syncStatus.setStatus(false);
            if(delegate!=null) {
                delegate.onAsyncTaskFinished(syncStatus);
            }

        }



    }

    @Override
    protected void onCancelled() {

    }

    private void setSyncConfiguration(SyncConfiguration syncConfig) {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);

        try {
            syncDbHandler.open();
            syncDbHandler.addSyncConfiguration(syncConfig);

            Gson gson = new Gson();
            String json = gson.toJson(syncConfig);
            //Log.d("SYNC_RUNNING_NO_RESULT ", json);
            mLog.info(mLocationName +":-"+"SYNC_RUNNING_NO_RESULT :" + json);
        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }

    private UserSetup getUserSetup() {

        UserSetupDbHandler dbAdapter = new UserSetupDbHandler(context);
        dbAdapter.open();

        mUserSetup = dbAdapter.getUserSetUp(mApp.getCurrentUserName());

        dbAdapter.close();

        return mUserSetup;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void logParams(ApiPostRunningNumbersParameter params) {

        params.setPassword("****");

        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info(mLocationName +":-"+"SYNC_RUNNING_NO_PARAMS :" + json);

        params.setPassword(mApp.getCurrentUserPassword());

    }


}