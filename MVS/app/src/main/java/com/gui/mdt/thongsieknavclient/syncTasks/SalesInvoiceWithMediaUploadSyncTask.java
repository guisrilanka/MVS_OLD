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
import com.google.gson.JsonObject;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceWithMediaParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesInvoiceWithMediaResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSoImageUploadListResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSoImageUploadParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSoMediaUploadParameter;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderImageUploadStatus;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderImageUploadStatusDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.model.BaseResult;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class SalesInvoiceWithMediaUploadSyncTask extends AsyncTask<Void, Void, Boolean>  {
    public AsyncResponse delegate = null;
    NavClientApp mApp;
    boolean isForcedSync = false;
    Context context;
    SyncConfiguration syncConfig;
    Logger mLog;
    int mLineNo = 1;
    List<SalesOrder> salesOrdersSynced;
    List<SalesOrderImageUploadStatus> salesOrderImageUploadStatusList=
            new ArrayList<SalesOrderImageUploadStatus>();
    ApiSoMediaUploadParameter mediaParameter;
    Map<String, SalesOrder> salesOrderMap;
    ApiSalesInvoiceWithMediaResponse apiSalesInvoiceWithMediaResponse;
    boolean isSuccess=false;
    public SalesInvoiceWithMediaUploadSyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog = Logger.getLogger(SalesInvoiceUploadSyncTask.class);
        this.salesOrderMap = new HashMap<String, SalesOrder>();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Pre-execution code
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(context);
            dbAdapter.open();

            if(isNetworkAvailable()) {
                salesOrdersSynced = dbAdapter.getSalesInvoiceForImageWithMediaUploading();
                loadSalesOrderImageUploadStatus();
                processSalesInvoiceWithMedia();
                uploadImages();

                isSuccess = changeUploadedSOImageStatus();

//                if (isSuccess) {
//                    syncConfig.setSuccess(true);
//                } else {
//                    syncConfig.setSuccess(false);
//                }
//
//                syncConfig.setDataCount(apiSoImageUploadParameterList.size());
//                setSyncConfiguration(syncConfig);
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

    private void loadSalesOrderImageUploadStatus() {
        SalesOrderImageUploadStatusDbHandler db = new SalesOrderImageUploadStatusDbHandler(context);
        db.open();
        salesOrderImageUploadStatusList = db.getAllItemsByTransferredWareHouse("0");
        db.close();
    }

    private void processSalesInvoiceWithMedia() {
        SalesOrderDbHandler db = new SalesOrderDbHandler(context);
        db.open();
        for(SalesOrder salesOrderSynced: salesOrdersSynced) {
            for (SalesOrderImageUploadStatus soius : salesOrderImageUploadStatusList) {
                String orderId = soius.getSoNo();
                if(salesOrderSynced.getNo().equals(orderId)) {
                    if (!salesOrderMap.containsKey(orderId)) {
                        SalesOrder salesOrder = db.getSalesOrderBySoNumber(orderId);
                        salesOrderMap.put(orderId, salesOrder);
                    }
                }
            }
        }



        db.close();
    }

    private void uploadImages() {
        List<ApiPostMobileSalesInvoiceWithMediaParameter> salesOrderWithMediaList = new ArrayList<>();

        for (Map.Entry<String, SalesOrder> entry : salesOrderMap.entrySet()) {
            String orderId = entry.getKey();
            SalesOrder salesOrder = entry.getValue();

            ApiPostMobileSalesInvoiceWithMediaParameter apiParameter = new ApiPostMobileSalesInvoiceWithMediaParameter();
            apiParameter.setDocumentNo(salesOrder.getNo());
            apiParameter.setRemark("");
            apiParameter.setDeliveryDateTime(salesOrder.getDocumentDate());
            apiParameter.setDeliverLatitude(salesOrder.getLatitude());
            apiParameter.setDeliverLongitude(salesOrder.getLongitude());

            apiParameter.setStatus(4);
//            apiParameter.setStatus(Integer.parseInt(salesOrder.getStatus()));
            apiParameter.setSignature("");

            List<ApiSoMediaUploadParameter> mediaList = new ArrayList<>();
            for (SalesOrderImageUploadStatus soius : salesOrderImageUploadStatusList) {
                    if (orderId.equals(soius.getSoNo())) {
                        String base64ImageString = convertImageToBase64(soius.getImageUrl());
                        ApiSoMediaUploadParameter mediaParameter = new ApiSoMediaUploadParameter();
                        mediaParameter.setPath(base64ImageString);
                        mediaList.add(mediaParameter);
                    }
            }
            apiParameter.setMediaList(mediaList);
            salesOrderWithMediaList.add(apiParameter);
        }

        sendSalesOrdersWithMedia(salesOrderWithMediaList);

    }
    private void sendSalesOrdersWithMedia(List<ApiPostMobileSalesInvoiceWithMediaParameter> updateMVSSaleOrderList) {

        try {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("updateMVSSaleOrderList", gson.toJsonTree(updateMVSSaleOrderList));
            String json = gson.toJson(jsonObject);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
            Call<ApiSalesInvoiceWithMediaResponse> call = mApp.getWhNavBrokerService()
                    .updateMVSSaleOrders(requestBody);
            Response<ApiSalesInvoiceWithMediaResponse> response = call.execute();

            if (response.isSuccessful()) {
                apiSalesInvoiceWithMediaResponse = response.body();
            } else {
                Log.e("API Error", "API call failed with response code: " + response.code());
                try {
                    String errorBody = response.errorBody().string();
                    Log.e("API Error", "Error Response Body: " + errorBody);
                } catch (IOException e) {
                    Log.e("API Error", "Error reading error response body", e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public interface AsyncResponse {
        void onTaskComplete(boolean success);
    }
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
    public boolean changeUploadedSOImageStatus() {
        boolean status = true;

        if (salesOrderImageUploadStatusList != null) {
            for (SalesOrder salesOrderSynced : salesOrdersSynced) {
                for (SalesOrderImageUploadStatus soius : salesOrderImageUploadStatusList) {
                    if (salesOrderSynced.getNo().equals(soius.getSoNo())) {
                        SalesOrderImageUploadStatusDbHandler db = new SalesOrderImageUploadStatusDbHandler(context);
                        db.open();

                        status = db.updateItemTransformWh(
                                soius.getSoNo()
                        );
                        db.close();
                    }
                }
            }
        }

        return status;
    }

}
