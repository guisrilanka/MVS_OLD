package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceLineParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceLineResponse;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequest;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequestLine;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockRequestDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockRequestLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 11/10/2017.
 */

public class StockRequestUploadSyncTask  extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    NavClientApp mApp;
    boolean isForcedSync = false;
    Context context;
    SyncConfiguration syncConfig;
    Logger mLog;
    String mLocationName;
    ApiPostMobileSalesInvoiceHeaderResponse apiStockRequestHeaderResponse;
    List<ApiPostMobileSalesInvoiceHeaderParameter> apiStockRequestHeaderParameterList;
    ApiPostMobileSalesInvoiceLineResponse apiStockRequestLineResponse;
    List<ApiPostMobileSalesInvoiceLineParameter> apiStockRequestParameterList;
    List<StockRequest> stockRequestsToBeSync;
    List<StockRequestLine> stockRequestLinesToBeSync;


    public StockRequestUploadSyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog = Log4jHelper.getLogger();
        mLocationName = StockRequestUploadSyncTask.class.getSimpleName();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeUploadSR));
        syncConfig.setLastSyncDateTime(DateTime.now().toString());

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        StockRequestDbHandler dbAdapter = new StockRequestDbHandler(context);
        dbAdapter.open();
        boolean isSuccess;
        try {

            stockRequestsToBeSync = dbAdapter.getSalesOrdersForUploading();
            dbAdapter.close();

            if (stockRequestsToBeSync.size() > 0 && isNetworkAvailable()) {
                /*uploadConfirmedStockRequestHeaders();
                isSuccess = saveUploadedSalesOrderHeaders();

                if (isSuccess) {
                    isSuccess = uploadConfirmedSalesOrderLines();

                    if (isSuccess) {
                        syncConfig.setSuccess(true);
                    } else {
                        syncConfig.setSuccess(false);
                    }

                }*/
                isSuccess = uploadStockRequest();

                if (isSuccess) {
                    syncConfig.setSuccess(true);
                } else {
                    syncConfig.setSuccess(false);
                }

            } else {
                return true;
            }

            syncConfig.setDataCount(stockRequestLinesToBeSync.size());
            setSyncConfiguration(syncConfig);

        } catch (Exception e) {
            mLog.error("Error",e);
            //Log.d("NAV_Client_Exception", e.toString());
            dbAdapter.close();
            return false;
        }

        return isSuccess;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        SyncStatus syncStatus = new SyncStatus();
        syncStatus.setScope(syncConfig.getScope());
        if (isForcedSync) {
            if (success) {

                String message = "";
                if(apiStockRequestHeaderResponse !=null && apiStockRequestHeaderResponse.getTrStatusList().size()>0){
                    for (ApiPostMobileSalesInvoiceHeaderResponse.SalesInvoiceHeaderResponse res:
                            apiStockRequestHeaderResponse.getTrStatusList() ) {
                        if(!res.isTransferred){
                            message = message + res.getDocumentNo()+" - Failed"+"\n";
                        }
                    }
                }

                syncStatus.setMessage(message);
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
                    .getString(R.string.SyncScopeUploadSR));
        } catch (Exception ex) {
            sc.setLastSyncDateTime(Calendar.getInstance().getTime().toString());
            mLog.error("Error",ex);
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
            //Log.d("SYNC_SO_UP_RESULT ", json);
            mLog.info(mLocationName +":-"+"SYNC_SR_UP_RESULT :" + json);
        } catch (Exception e) {
            mLog.error("Error",e);
        }
        syncDbHandler.close();
    }

    //new sr upload - one sr at a time(sr & sr line) 2018-03-13
    //----------------------------------------------------------------------------------------------
    private boolean uploadStockRequest() {

        try {

            for (StockRequest confirmedStockRequest : stockRequestsToBeSync) {

                //seting sr header upload parameter-------------------------------------------------
                apiStockRequestHeaderParameterList
                        = new ArrayList<ApiPostMobileSalesInvoiceHeaderParameter>();

                ApiPostMobileSalesInvoiceHeaderParameter SrHeaderParams = new
                        ApiPostMobileSalesInvoiceHeaderParameter();

                SrHeaderParams.setSales_Order_Date(confirmedStockRequest.getShipmentDate());
                SrHeaderParams.setKey(confirmedStockRequest.getNo());
                SrHeaderParams.setDocument_No(confirmedStockRequest.getNo());
                SrHeaderParams.setCustomer_Number(confirmedStockRequest.getSelltoCustomerNo());
                SrHeaderParams.setShip_To_Number("");
                SrHeaderParams.setSaleperson_Number(confirmedStockRequest.getSalespersonCode());
                SrHeaderParams.setDetail_Ordinal(getInvoiceLineCount(confirmedStockRequest.getNo()));//Jason: its total number (Count) of Invoice line
                //SoHeaderParams.setSales_Order_Date(confirmedSalesOrder.getDocumentDate());
                if (confirmedStockRequest.getShipmentDate() == null) {
                    SrHeaderParams.setDue_Date("");
                } else {
                    SrHeaderParams.setDue_Date(confirmedStockRequest.getShipmentDate());
                }

                if (confirmedStockRequest.getExternalDocumentNo() == null)
                    SrHeaderParams.setPurchase_Order_Number("");
                else
                    SrHeaderParams.setPurchase_Order_Number(confirmedStockRequest.getExternalDocumentNo());

                SrHeaderParams.setTotal_Amt_after_Discount(String.valueOf(confirmedStockRequest.getAmountInclVAT()
                        - confirmedStockRequest.getVATAmount()));//Jason: leave it zero if no i_discount.
                SrHeaderParams.setDiscount_Amt("0");
                SrHeaderParams.setTax_Percentage("0");//Jason: leave it zero
                SrHeaderParams.setTotal_Amt_after_Tax(
                        String.valueOf(confirmedStockRequest.getAmountInclVAT())
                );
                SrHeaderParams.setDriver_Code(confirmedStockRequest.getDriverCode());

                if (confirmedStockRequest.getExternalDocumentNo() == null)
                    SrHeaderParams.setComment("");
                else
                    SrHeaderParams.setComment(confirmedStockRequest.getExternalDocumentNo());

                SrHeaderParams.setCreated_By("");
                SrHeaderParams.setCreated_DateTime("");
                SrHeaderParams.setLast_Modified_By("");
                SrHeaderParams.setLast_Modified_DateTime("");
                SrHeaderParams.setLast_Transferred_By("");
                SrHeaderParams.setLast_Transferred_DateTime("");
                SrHeaderParams.setUserName(mApp.getCurrentUserName());
                SrHeaderParams.setPassword(mApp.getCurrentUserPassword());
                SrHeaderParams.setUserCompany(mApp.getmUserCompany());

                SrHeaderParams.setSales_Order_No(confirmedStockRequest.getNo());

                apiStockRequestHeaderParameterList.add(SrHeaderParams);


                //setting sr line parameter list ---------------------------------------------------
                apiStockRequestParameterList = new ArrayList<ApiPostMobileSalesInvoiceLineParameter>();
                stockRequestLinesToBeSync = new ArrayList<>();

                StockRequestLineDbHandler dbAdapter = new StockRequestLineDbHandler(context);
                dbAdapter.open();

                stockRequestLinesToBeSync = dbAdapter.getAllSRLinesByDocumentNo(confirmedStockRequest.getNo());

                dbAdapter.close();

                for (StockRequestLine srLine : stockRequestLinesToBeSync) {

                    ApiPostMobileSalesInvoiceLineParameter srLineParams = new ApiPostMobileSalesInvoiceLineParameter();

                    srLineParams.setUserName(mApp.getCurrentUserName());
                    srLineParams.setPassword(mApp.getCurrentUserPassword());
                    srLineParams.setUserCompany(mApp.getmUserCompany());
                    srLineParams.setKey(srLine.getKey());
                    srLineParams.setDocument_No(confirmedStockRequest.getNo());
                    srLineParams.setLine_Number(srLine.getLineNo());
                    srLineParams.setItem_Number(srLine.getItemNo());
                    srLineParams.setLocation_Code("");
                    srLineParams.setUOM_number(srLine.getUnitofMeasureCode());
                    srLineParams.setQuantity(String.valueOf(srLine.getQuantity()));
                    srLineParams.setUnit_Price(String.valueOf(srLine.getUnitPrice()));
                    srLineParams.setLine_Amt_before_Discount(String.valueOf(srLine.getLineAmount()));
                    srLineParams.setDiscount_Amt(String.valueOf(srLine.getLineDiscountAmount()));
                    srLineParams.setLine_Amt_after_Discount(
                            String.valueOf(srLine.getLineAmount() - srLine.getLineDiscountAmount()));
                    srLineParams.setLine_Comment("");
                    srLineParams.setDriver_Code(srLine.getDriverCode());
                    srLineParams.setCreated_DateTime(confirmedStockRequest.getCreatedDateTime());
                    srLineParams.setLast_Modified_DateTime(confirmedStockRequest.getLastModifiedDateTime());
                    srLineParams.setTransferred("0");
                    srLineParams.setLast_Transferred_By(confirmedStockRequest.getLastTransferredBy());
                    srLineParams.setLast_Transferred_DateTime(confirmedStockRequest.getLastTransferredDateTime());
                    srLineParams.setNav_Document_No("");
                    srLineParams.setNav_Document_Line_No("");

                    srLineParams.setLast_Modified_DateTime(srLine.getLastModifiedDateTime());
                    srLineParams.setUOM_number(srLine.getUnitofMeasureCode());
                    apiStockRequestParameterList.add(srLineParams);
                }


                //upload sr header ***
                logHeaderParams(apiStockRequestHeaderParameterList);

                Call<ApiPostMobileSalesInvoiceHeaderResponse> srHeaderUploadCall = mApp.getNavBrokerService()
                        .PostBlanketOrderHeaderMVS(apiStockRequestHeaderParameterList);

                apiStockRequestHeaderResponse = srHeaderUploadCall.execute().body();
                logHeaderResponse(apiStockRequestHeaderResponse);

                //upload sr line list ***
                logLineParams(apiStockRequestParameterList);

                Call<ApiPostMobileSalesInvoiceLineResponse> srLineUploadCall = mApp.getNavBrokerService()
                        .PostBlanketOrderLineMVS(apiStockRequestParameterList);

                apiStockRequestLineResponse = srLineUploadCall.execute().body();
                logLineResponse(apiStockRequestLineResponse);

                //update sr db
                //saveUploadedSalesOrderHeaders(); OLD nalaka 2018-05-25

                saveUploadedSalesOrderHeader(confirmedStockRequest
                ,apiStockRequestHeaderResponse
                ,apiStockRequestLineResponse);

            }


        } catch (Exception e) {
            mLog.error("Error", e);
            return false;
        }

        return true;
    }
    //----------------------------------------------------------------------------------------------

    private boolean saveUploadedSalesOrderHeaders() {
        setSalesOrderStatus(stockRequestsToBeSync);

        StockRequestDbHandler dbAdapter = new StockRequestDbHandler(context);
        dbAdapter.open();
        boolean pass = dbAdapter.saveConfirmStockRequests(stockRequestsToBeSync);
        dbAdapter.close();

        return pass;
    }

    private boolean saveUploadedSalesOrderHeader(StockRequest confirmedStockRequest
            , ApiPostMobileSalesInvoiceHeaderResponse headerResponse
            ,ApiPostMobileSalesInvoiceLineResponse linesResponse) {

        List<StockRequest> uploadedStockRequestList =  new ArrayList<StockRequest>();

        //Header need to be transferred or already exist.
        for (ApiPostMobileSalesInvoiceHeaderResponse.SalesInvoiceHeaderResponse hdResponse:headerResponse.trStatusList) {

            if(hdResponse.isTransferred || hdResponse.BaseResult.getMessage().equals("HEADER_ALREADY_EXIST")){ //already exist.

                confirmedStockRequest.setTransferred(true);
                confirmedStockRequest.setStatus(context.getResources().getString(R.string.SalesOrderStatusConfirmed));
            }else {
                confirmedStockRequest.setStatus(context.getResources().getString(R.string.SalesOrderStatusPending));
                //isUploaded=false;
            }
        }

            //all lines need to be transferred.
            for (ApiPostMobileSalesInvoiceLineResponse.SalesInvoiceLineResponse lineResponse:linesResponse.trStatusList) {

                if( !(lineResponse.isTransferred || (lineResponse.BaseResult.getMessage().equals("LINE_ALREADY_EXIST")))){ //already exist.
                    //isUploaded =false;
                    confirmedStockRequest.setTransferred(false);
                    confirmedStockRequest.setStatus(context.getResources().getString(R.string.SalesOrderStatusPending));
                }
            }

            uploadedStockRequestList.add(confirmedStockRequest);

        StockRequestDbHandler dbAdapter = new StockRequestDbHandler(context);
        dbAdapter.open();
        boolean pass = dbAdapter.saveConfirmStockRequests(uploadedStockRequestList);
        dbAdapter.close();

        return pass;

    }

    private void setSalesOrderStatus(List<StockRequest> comfirmedSalesOrderList) {

        try {
            for (StockRequest sr :
                    comfirmedSalesOrderList) {
                for (ApiPostMobileSalesInvoiceHeaderResponse.SalesInvoiceHeaderResponse res :
                        apiStockRequestHeaderResponse.getTrStatusList()) {
                    if (res.getDocumentNo().equals(sr.getNo())) {
                        if (res.isTransferred() == true && sr.isConfirmedSr()) {
                            sr.setStatus(context.getResources().getString(R.string.SalesOrderStatusConfirmed));
                            sr.setTransferred(res.isTransferred());
                        }
                        /*else if (res.isTransferred() == false && sr.isConfirmedSr()) {
                            sr.setStatus(context.getResources().getString(R.string.SalesOrderStatusConfirmed));
                        }*/
                        else {
                            sr.setStatus(context.getResources().getString(R.string.SalesOrderStatusPending));
                        }

                    }
                }
            }
        } catch (Exception e) {
            mLog.error("Error",e);
        }
    }


    private String getInvoiceLineCount(String soNo){

        SalesOrderLineDbHandler dbAdapter = new SalesOrderLineDbHandler(context);
        dbAdapter.open();
        int count = dbAdapter.getInvoiceLineCount(soNo);
        dbAdapter.close();
        String strCount = Integer.valueOf(count).toString();

        return strCount;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void logHeaderParams(List<ApiPostMobileSalesInvoiceHeaderParameter> params){

        for (ApiPostMobileSalesInvoiceHeaderParameter p:params) {
            p.setPassword("****");
        }

        Gson gson= new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_UP_PARAMS", json);
        mLog.info(mLocationName +":-"+"SYNC_SR_UP_PARAMS :" + json);

        for (ApiPostMobileSalesInvoiceHeaderParameter p:params) {
            p.setPassword(mApp.getCurrentUserPassword());
        }

    }

    private void logLineParams(List<ApiPostMobileSalesInvoiceLineParameter> params){

        for (ApiPostMobileSalesInvoiceLineParameter p:params) {
            p.setPassword("****");
        }

        Gson gson= new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_LINE_UP_PARAMS", json);
        mLog.info(mLocationName +":-"+"SYNC_SR_LINE_UP_PARAMS :" + json);

        for (ApiPostMobileSalesInvoiceLineParameter p:params) {
            p.setPassword(mApp.getCurrentUserPassword());
        }
    }

    private void logHeaderResponse(ApiPostMobileSalesInvoiceHeaderResponse response){

        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(response);
        //Log.d("SYNC_RES_SO_UP_HEAD", json);
        mLog.info(mLocationName +":-"+"SYNC_RES_SR_UP_HEAD :" + json);

    }

    private void logLineResponse(ApiPostMobileSalesInvoiceLineResponse response){
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(response);
        //Log.d("SYNC_RES_SO_UP_LINE", json);
        mLog.info(mLocationName +":-"+"SYNC_RES_SR_UP_LINE :" + json);
    }


}
