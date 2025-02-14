package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiGSTPostingSetupParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiGSTPostingSetupResponse;
import com.gui.mdt.thongsieknavclient.datamodel.GSTPostingSetup;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.GSTPostingSetupDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by GUI-NB03 on 2017-08-30.
 */

public class GSTPostingSetupSyncTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    private NavClientApp mApp;
    private boolean isForcedSync = false;
    Logger mLog;
    String mLocationName;
    ApiGSTPostingSetupParameter apiGSTPostingSetupParameter;
    ApiGSTPostingSetupResponse apiGSTPostingSetupResponse;
    List<ApiGSTPostingSetupResponse.GSTPostingSetupResultData> gSTPostingSetupResult;


    public GSTPostingSetupSyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog= Log4jHelper.getLogger();
        mLocationName = GSTPostingSetupSyncTask.class.getSimpleName();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //set parameters for request
        apiGSTPostingSetupParameter = new ApiGSTPostingSetupParameter();
        apiGSTPostingSetupParameter.setUserName(mApp.getCurrentUserName());
        apiGSTPostingSetupParameter.setPassword(mApp.getCurrentUserPassword());
        apiGSTPostingSetupParameter.setUserCompany(mApp.getmUserCompany());
        apiGSTPostingSetupParameter.setVAT_Bus_Posting_Group("");
        apiGSTPostingSetupParameter.setVAT_Prod_Posting_Group("");

   logParams(apiGSTPostingSetupParameter);


        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownLoadGst));

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {

            if(isNetworkAvailable()) {
                Call<ApiGSTPostingSetupResponse> call = mApp.getNavBrokerService().GetGSTPostingSetup
                        (apiGSTPostingSetupParameter);
                apiGSTPostingSetupResponse = call.execute().body();
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

    private void addRecords(boolean success) {
        if (success) {
            GSTPostingSetupDbHandler dbAdapter = new GSTPostingSetupDbHandler(context);
            dbAdapter.open();

            if (apiGSTPostingSetupResponse != null) {
                gSTPostingSetupResult = apiGSTPostingSetupResponse.getGSTPostingSetupResultData();

                //set current sync status.
                syncConfig.setLastSyncDateTime(apiGSTPostingSetupResponse.getServerDate());
                syncConfig.setDataCount(gSTPostingSetupResult.size());
                syncConfig.setSuccess(true);
            } else {
                syncConfig.setSuccess(false);
            }

            try {

                for (ApiGSTPostingSetupResponse.GSTPostingSetupResultData itemResult : gSTPostingSetupResult) {
                    //Log.d("SYNC_CUSTOMER: ",cus.getNo());
                    if (dbAdapter.deleteGSTPostingSetup(itemResult.getKey())) {

                        if (itemResult.getVAT_Bus_Posting_Group() == null) {
                            itemResult.setVAT_Bus_Posting_Group("");
                        }
                        if (itemResult.getVAT_Prod_Posting_Group() == null) {
                            itemResult.setVAT_Prod_Posting_Group("");
                        }

                        GSTPostingSetup item = new GSTPostingSetup();
                        item.setKey(itemResult.getKey());
                        item.setVATBusPostingGroup(itemResult.getVAT_Bus_Posting_Group());
                        item.setVATProdPostingGroup(itemResult.getVAT_Prod_Posting_Group());
                        item.setVATPercent(itemResult.getVAT_Percent());

                        dbAdapter.addGSTPostingSetup(item);
                        Log.d("SYNC_GST_ADDED: ", itemResult.getVAT_Bus_Posting_Group() + "-"
                                + itemResult.getVAT_Prod_Posting_Group());
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

    private void setSyncConfiguration(SyncConfiguration syncConfig) {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);

        try {
            syncDbHandler.open();
            syncDbHandler.addSyncConfiguration(syncConfig);

            Gson gson = new Gson();
            String json = gson.toJson(syncConfig);
            //Log.d("SYNC_GST_POST_RESULT ", json);
            mLog.info("SYNC_GST_POST_RESULT :" + json);
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

    private void logParams(ApiGSTPostingSetupParameter params) {

        params.setPassword("****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info(mLocationName +":-"+"SYNC_GST_POST_PARAMS :" + json);
        params.setPassword(mApp.getCurrentUserPassword());


    }
}
