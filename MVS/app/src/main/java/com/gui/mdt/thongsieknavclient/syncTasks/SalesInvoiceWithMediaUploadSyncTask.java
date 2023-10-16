package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceWithMediaParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSoImageUploadParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSoMediaUploadParameter;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderImageUploadStatus;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SalesInvoiceWithMediaUploadSyncTask extends AsyncTask<Void, Void, Boolean>  {
    public AsyncResponse delegate = null;
    NavClientApp mApp;
    boolean isForcedSync = false;
    Context context;
    SyncConfiguration syncConfig;
    Logger mLog;
    int mLineNo = 1;


    List<SalesOrderImageUploadStatus> salesOrderImageUploadStatusList=
            new ArrayList<SalesOrderImageUploadStatus>();

    ApiSoMediaUploadParameter mediaParameter;
    List<SalesOrder> salesOrdersToBeSync;
    public SalesInvoiceWithMediaUploadSyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog = Logger.getLogger(SalesInvoiceUploadSyncTask.class);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected Boolean doInBackground(Void... voids) {

         uploadSalesInvoiceWithMedia();
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    private boolean uploadSalesInvoiceWithMedia() {
        try {
            System.out.println(salesOrdersToBeSync.size());
            for (SalesOrder confirmedSalesOrder : salesOrdersToBeSync) {

                ApiPostMobileSalesInvoiceWithMediaParameter SoHeaderAndMedaParams = new
                        ApiPostMobileSalesInvoiceWithMediaParameter();

                SoHeaderAndMedaParams.setDocumentNo(confirmedSalesOrder.getSINo());
                if (confirmedSalesOrder.getComments() == null)
                    SoHeaderAndMedaParams.setRemark("");
                else
                    SoHeaderAndMedaParams.setRemark(confirmedSalesOrder.getComments());
                SoHeaderAndMedaParams.setDeliverLongitude(103.8121867);
                SoHeaderAndMedaParams.setDeliverLongitude(1.4687649);
                SoHeaderAndMedaParams.setStatus(Integer.parseInt(confirmedSalesOrder.getStatus()));
                SoHeaderAndMedaParams.setSignature("");


                List<ApiSoMediaUploadParameter> mediaParameterList = new ArrayList<>();
                for (SalesOrderImageUploadStatus soius : salesOrderImageUploadStatusList) {
                    ApiSoMediaUploadParameter mediaParameter = new ApiSoMediaUploadParameter();
                    String base64ImageString = convertImageToBase64(soius.getImageUrl());
                    mediaParameter.setPath(base64ImageString);
                    mediaParameterList.add(mediaParameter);
                }

                SoHeaderAndMedaParams.setMediaList(mediaParameterList);

            }

        } catch (Exception e) {
            mLog.error("Error", e);
            return false;
        }

        return true;
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
}
