package com.gui.mdt.thongsieknavclient.syncTasks;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.ApiDeviceTokenParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiDeviceTokenResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiGPSLocationLineParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiGPSLocationParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiGPSLocationResponse;
import com.gui.mdt.thongsieknavclient.datamodel.GPSLocation;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class TokenUploadSyncTask extends AsyncTask<Void, Void, Boolean>  {
    public AsyncResponse delegate = null;
    NavClientApp mApp;
    boolean isForcedSync = false;
    Context context;
    Logger mLog;
    ApiDeviceTokenResponse apiGPSLocationResponse;
    boolean isSuccess=false;
    private Bundle driverData;
    public TokenUploadSyncTask(Context context, Bundle driverData) {
        this.context = context;
        this.mApp = (NavClientApp) context;
        this.mLog = Logger.getLogger(SalesInvoiceUploadSyncTask.class);
        this.driverData = driverData;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Pre-execution code
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            if (isNetworkAvailable()) {
                uploadDeviceToken();
            }
        } catch (Exception e) {
            mLog.error("Error", e);
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (delegate != null) {
            delegate.onTaskComplete(success);
        }
    }


    public interface AsyncResponse {
        void onTaskComplete(boolean success);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean uploadDeviceToken() {
        try {
            ApiDeviceTokenParameter apiDeviceTokenParameter=new ApiDeviceTokenParameter();
            List<ApiGPSLocationLineParameter> gpsList = new ArrayList<>();
            apiDeviceTokenParameter.setDriverCode(mApp.getmCurrentDriverCode());

            String driverCode= driverData.getString("driverCode","");
            String fcmToken= driverData.getString("fcmToken","");

            apiDeviceTokenParameter.setDriverCode(driverCode);
            apiDeviceTokenParameter.setFcmToken(fcmToken);
            syncDeviceToken(apiDeviceTokenParameter);
            return true;
        }catch (Exception e){
            mLog.error("Error", e);
            return false;
        }
    }
    private void syncDeviceToken(ApiDeviceTokenParameter uploadDeviceToken) {

        try {
            if (isNetworkAvailable()) {
                Call<ApiDeviceTokenResponse> call = mApp.getWhNavBrokerService()
                    .updateDeviceToken(uploadDeviceToken);

                apiGPSLocationResponse = call.execute().body();
//                 if(apiGPSLocationResponse.getStatus()==200) {
//
//                 }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
