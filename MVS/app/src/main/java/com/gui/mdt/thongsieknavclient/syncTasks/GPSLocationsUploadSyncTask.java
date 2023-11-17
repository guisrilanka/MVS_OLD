package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.ApiGPSLocationLineParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiGPSLocationParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiGPSLocationResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceLineResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceWithMediaParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesInvoiceWithMediaResponse;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.GPSLocation;
import com.gui.mdt.thongsieknavclient.dbhandler.GPSDbHandler;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;


public class GPSLocationsUploadSyncTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private NavClientApp mApp;
    private Logger mLog;
    private Bundle locationData;
    String currentDateTime = "";
    GPSLocation gpsLocation;
    Customer mTempCustomer;
    List<GPSLocation> gpsLocationList=
            new ArrayList<GPSLocation>();
    ApiGPSLocationResponse apiGPSLocationResponse;
    DateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date dateObj = new Date();

    public GPSLocationsUploadSyncTask(Context context, Bundle locationData, boolean isForcedSync) {
        this.context = context;
        this.locationData = locationData;
        this.mLog = Logger.getLogger(GPSLocationsUploadSyncTask.class);
    }

    @Override
    protected void onPreExecute() {
        this.mApp = (NavClientApp) context.getApplicationContext();
        super.onPreExecute();
        // Pre-execution code
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            if (isNetworkAvailable()) {
                loadSavedGPSLocationsData();
              boolean isSuccess= uploadGPSLocations();
                if(isSuccess){

                }
            } else {
                saveGPSLocation();
            }
        } catch (Exception e) {
            Log.e("GPSLocationsUploadSync", "Error", e);
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success) {
            deleteUploadedGPSLocations();
        } else {

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean saveGPSLocation() {
        currentDateTime = df3.format(dateObj).toString();
        double latitude = locationData.getDouble("lat", 0.0);
        double longitude = locationData.getDouble("long", 0.0);

        GPSDbHandler dbAdapter = new GPSDbHandler(context);
        dbAdapter.open();
        try {
            GPSLocation gpsLocation = new GPSLocation();
            gpsLocation.setDate(currentDateTime);
            gpsLocation.setLatitude(latitude);
            gpsLocation.setLongitude(longitude);
            gpsLocation.setStatus("0");
            dbAdapter.addGpsLocation(gpsLocation);
            return true;
        } catch (Exception e) {
            Log.e("GPSLocationsUploadSyncTask", "Error saving GPS location", e);
        } finally {
            dbAdapter.close();
        }
        return false;
    }

    private int loadSavedGPSLocationsData() {
        GPSDbHandler db = new GPSDbHandler(context);
        db.open();
        gpsLocationList = db.getGPSLocationList();
        db.close();
        return gpsLocationList.size();
    }
    private boolean uploadGPSLocations() {
        try {
            ApiGPSLocationParameter apiGPSLocationParameter=new ApiGPSLocationParameter();
            List<ApiGPSLocationLineParameter> gpsList = new ArrayList<>();
            apiGPSLocationParameter.setDriverCode(mApp.getmCurrentDriverCode());
            for (GPSLocation gps : gpsLocationList) {
                ApiGPSLocationLineParameter apiParameter = new ApiGPSLocationLineParameter();
                apiParameter.setLatitude(gps.getLatitude());
                apiParameter.setLongitude(gps.getLongitude());
                apiParameter.setDateTime(gps.getDate());
                gpsList.add(apiParameter);
            }
            ApiGPSLocationLineParameter singleGps = createSingleLineGPSLocation();
            gpsList.add(singleGps);
            apiGPSLocationParameter.setDriverLocations(gpsList);
            sendGPSLocations(apiGPSLocationParameter);
            markGPSLocationsAsUploaded();
            return true;
        }catch (Exception e){
            mLog.error("Error", e);
            return false;
        }

    }

    private ApiGPSLocationLineParameter createSingleLineGPSLocation() {
        currentDateTime = df3.format(dateObj).toString();
        double latitude = locationData.getDouble("lat", 0.0);
        double longitude = locationData.getDouble("long", 0.0);

        ApiGPSLocationLineParameter locationParameter = new ApiGPSLocationLineParameter();
        locationParameter.setLatitude(latitude);
        locationParameter.setLongitude(longitude);
        locationParameter.setDateTime(currentDateTime);

        return locationParameter;
    }
    private void sendGPSLocations(ApiGPSLocationParameter updateMVSDriverLocation) {

        try {
            if (isNetworkAvailable()) {
                Call<ApiGPSLocationResponse> call = mApp.getWhNavBrokerService()
                        .updateMVSDriverLocation(updateMVSDriverLocation);

                apiGPSLocationResponse = call.execute().body();
//                 if(apiGPSLocationResponse.getStatus()==200) {
//
//                 }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void deleteUploadedGPSLocations() {
        GPSDbHandler dbAdapter = new GPSDbHandler(context);
        dbAdapter.open();
        dbAdapter.deleteUploadedGPSLocations();
        dbAdapter.close();
    }
    private void markGPSLocationsAsUploaded() {
        GPSDbHandler dbAdapter = new GPSDbHandler(context);
        dbAdapter.open();
        try {
            for (GPSLocation gps : gpsLocationList) {
                gps.setStatus("1");
                dbAdapter.updateGpsLocation(gps);
            }
        } catch (Exception e) {
            Log.e("GPSLocationsUploadSyncTask", "Error marking GPS locations as uploaded", e);
        } finally {
            dbAdapter.close();
        }
    }
}
