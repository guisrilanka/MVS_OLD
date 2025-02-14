package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSoImageUploadListResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSoImageUploadParameter;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderImageUploadStatus;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderImageUploadStatusDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;


public class SalesOrderImageUploadSyncTask extends AsyncTask<Void, Void, Boolean> {

    Context context;
    private ApiSoImageUploadListResult apiSoImageUploadListResult;
    ApiSoImageUploadParameter apiSoImageUploadParameter;
    NavClientApp mApp;
    List<ApiSoImageUploadParameter> apiSoImageUploadParameterList=
            new ArrayList<ApiSoImageUploadParameter>();
    List<SalesOrderImageUploadStatus> salesOrderImageUploadStatusList=
            new ArrayList<SalesOrderImageUploadStatus>();

    List<ApiSoImageUploadListResult.ApiSoImageUploadResponse> apiSoImageUploadResponseList;

    public AsyncResponse delegate = null;
    boolean isForcedSync = false;
    SyncConfiguration syncConfig;
    Logger mLog;

    String mLocationName;
    public SalesOrderImageUploadSyncTask(Context context, boolean isForcedSync_) {

        mApp = (NavClientApp) context;
        this.context = context;
        this.isForcedSync = isForcedSync_;
        this.mLog = Log4jHelper.getLogger();
        mLocationName = SalesOrderDownloadSyncTask.class.getSimpleName();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeUploadSOImage));
        syncConfig.setLastSyncDateTime(DateTime.now().toString());
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean isSuccess = false;
        try {
            if(isNetworkAvailable()) {
                uploadSOImages();
                isSuccess = saveUplodedSOImageStatus();

                if (isSuccess) {
                    syncConfig.setSuccess(true);
                } else {
                    syncConfig.setSuccess(false);
                }

                syncConfig.setDataCount(apiSoImageUploadParameterList.size());
                setSyncConfiguration(syncConfig);
            }

        } catch (Exception ex) {
            mLog.error("Error",ex);
            Log.d("SYNC_SO_IMG_UP", " Exception: " + ex.getMessage().toString());
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
        //Runtime.getRuntime().gc();
    }

    @Override
    protected void onCancelled() {
        Runtime.getRuntime().gc();
    }

    public boolean saveUplodedSOImageStatus()
    {
        boolean status = true;
        if (apiSoImageUploadListResult != null) {

            apiSoImageUploadResponseList = apiSoImageUploadListResult.getTrStatusList();

            if (!apiSoImageUploadResponseList.isEmpty()) {
                for (ApiSoImageUploadListResult.ApiSoImageUploadResponse apiSoImageUploadResponse : apiSoImageUploadResponseList) {

                    BaseResult baseResult = apiSoImageUploadResponse.getBaseResult();

                    if (apiSoImageUploadResponse.isTransferred() && baseResult.getStatus() == 200) {
                        //update status in table

                        SimpleDateFormat dBFormat = new SimpleDateFormat("yyyy-MM-ddhh:mm:ss");
                        String toDay = dBFormat.format(new Date());

                        SalesOrderImageUploadStatusDbHandler db = new SalesOrderImageUploadStatusDbHandler(context);
                        db.open();

                        status = db.updateItem(
                                apiSoImageUploadResponse.getImageName()
                                , apiSoImageUploadResponse.isTransferred()
                                , mApp.getCurrentUserName()
                                , toDay);

                        db.close();
                    }
                }
            }
        }
        return status;
    }

    public boolean uploadSOImages()
    {
        boolean status = false;
        try {
            if(mApp.getmCurrentModule().equals(context.getResources().getString(R.string.module_mvs)))
            {
                getImagesForMvs();
            }
            else if(mApp.getmCurrentModule().equals(context.getResources().getString(R.string.module_lds)))
            {
                getImages();
            }


            if(!apiSoImageUploadParameterList.isEmpty())
            {
                Call<ApiSoImageUploadListResult> call = mApp.getNavBrokerService().PostSOImage(apiSoImageUploadParameterList);
                apiSoImageUploadListResult = call.execute().body();
            }
            else
            {
                mLog.info(mLocationName +":-"+ "SYNC_SO_IMG_UP :No Image to Upload");
                //Log.d("SYNC_SO_IMG_UP", "No Image to Upload");
            }
            status = true;
        }
        catch (Exception ee)
        {
            mLog.error("Error",ee);
            //Log.d("SYNC_SO_IMG_UP", "Exception :" + ee.getMessage().toString());
            status = false;
        }
        return status;
    }

    public void getImages()
    {
        //get not transfered list
        SalesOrderImageUploadStatusDbHandler db=new SalesOrderImageUploadStatusDbHandler(context);
        db.open();
        salesOrderImageUploadStatusList = db.getAllItemsByTransferred("0");
        db.close();

        //set parameter list
        if(!salesOrderImageUploadStatusList.isEmpty())
        {
            for (SalesOrderImageUploadStatus soius : salesOrderImageUploadStatusList){

                apiSoImageUploadParameter = new ApiSoImageUploadParameter();

                String base64ImageString = convertImageToBase64(soius.getImageUrl());

                //Log.e("Test Image U:",soius.getImageName());
                Log.e("Image upload : ",soius.getImageName() +" /n"+base64ImageString);

                apiSoImageUploadParameter.setUserName(mApp.getCurrentUserName());
                apiSoImageUploadParameter.setPassword(mApp.getCurrentUserPassword());
                apiSoImageUploadParameter.setUserCompany(mApp.getmUserCompany());
                apiSoImageUploadParameter.setSaleOrderNo(soius.getSoNo());
                apiSoImageUploadParameter.setEncodedImage(base64ImageString);
                apiSoImageUploadParameter.setImageName(soius.getImageName());

                apiSoImageUploadParameterList.add(apiSoImageUploadParameter);
            }
        }
    }

    public void getImagesForMvs()
    {
        //get not transfered list
        SalesOrderImageUploadStatusDbHandler db=new SalesOrderImageUploadStatusDbHandler(context);
        db.open();
        salesOrderImageUploadStatusList = db.getAllItemsByTransferredForMVS("0");
        db.close();

        //set parameter list
        if(!salesOrderImageUploadStatusList.isEmpty())
        {
            for (SalesOrderImageUploadStatus soius : salesOrderImageUploadStatusList){

                apiSoImageUploadParameter = new ApiSoImageUploadParameter();

                String base64ImageString = convertImageToBase64(soius.getImageUrl());

                //Log.e("Test Image U:",soius.getImageName());
                Log.e("Image upload : ",soius.getImageName() +" /n"+base64ImageString);

                apiSoImageUploadParameter.setUserName(mApp.getCurrentUserName());
                apiSoImageUploadParameter.setPassword(mApp.getCurrentUserPassword());
                apiSoImageUploadParameter.setUserCompany(mApp.getmUserCompany());
                apiSoImageUploadParameter.setSaleOrderNo(soius.getSoNo());
                apiSoImageUploadParameter.setEncodedImage(base64ImageString);
                apiSoImageUploadParameter.setImageName(soius.getImageName());

                apiSoImageUploadParameterList.add(apiSoImageUploadParameter);
            }
        }
    }

    private void setSyncConfiguration(SyncConfiguration syncConfig) {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);

        try {
            syncDbHandler.open();
            syncDbHandler.addSyncConfiguration(syncConfig);

            Gson gson = new Gson();
            String json = gson.toJson(syncConfig);
            //Log.d("SYNC_SO_IMG_UP_RESULT ", json);
            mLog.info(mLocationName +":-"+"SYNC_SO_IMG_UP_RESULT :" + json);
        } catch (Exception e) {
            mLog.error("Error",e);
        }
        syncDbHandler.close();
    }

    //added newly
    public String convertImageToBase64(String imageURL){
        String base64ImageString = "";
        try {

            //1 Create file from URL
            Bitmap bm_ = BitmapFactory.decodeFile(imageURL);

            //2.2 Resixe image
            float maxImageSize = 500;
            boolean filter = true;
            float ratio = Math.min(
                    (float) maxImageSize / bm_.getWidth(),
                    (float) maxImageSize / bm_.getHeight());
            int width = Math.round((float) ratio * bm_.getWidth());
            int height = Math.round((float) ratio * bm_.getHeight());

            Bitmap finalBitmap = Bitmap.createScaledBitmap(bm_, width,
                    height, filter);

             //3 Convert it to base64 image string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteArrayImage_ = baos.toByteArray();
            base64ImageString = Base64.encodeToString(byteArrayImage_, Base64.DEFAULT);


           return  base64ImageString;
        } catch (Exception e) {
            return base64ImageString;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
