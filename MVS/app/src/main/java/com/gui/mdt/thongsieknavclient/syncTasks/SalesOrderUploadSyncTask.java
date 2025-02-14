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
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nelin_000 on 08/24/2017.
 */

public class SalesOrderUploadSyncTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    NavClientApp mApp;
    boolean isForcedSync = false;
    Context context;
    SyncConfiguration syncConfig;
    Logger mLog;
    String mLocationName;
    ApiPostMobileSalesInvoiceHeaderResponse apiSalesOrderHeaderResponse;
    List<ApiPostMobileSalesInvoiceHeaderParameter> apiSalesOrderHeaderParameterList;
    ApiPostMobileSalesInvoiceLineResponse apiSalesOrderLineResponse;
    List<ApiPostMobileSalesInvoiceLineParameter> apiSalesOrderLineParameterList;
    List<SalesOrder> salesOrdersToBeSync;
    List<SalesOrderLine> salesOrdersLinesToBeSync;


    public SalesOrderUploadSyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog = Log4jHelper.getLogger();
        mLocationName = SalesOrderUploadSyncTask.class.getSimpleName();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeUploadSO));
        syncConfig.setLastSyncDateTime(DateTime.now().toString());

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(context);
        dbAdapter.open();
        boolean isSuccess;
        try {

            salesOrdersToBeSync = dbAdapter.getSalesOrdersForUploading();
            dbAdapter.close();

            if (salesOrdersToBeSync.size() > 0 && isNetworkAvailable()) {
                uploadConfirmedSalesOrderHeaders();
                isSuccess = saveUploadedSalesOrderHeaders();

                if (isSuccess) {
                    isSuccess = uploadConfirmedSalesOrderLines();

                    if (isSuccess) {
                        syncConfig.setSuccess(true);
                    } else {
                        syncConfig.setSuccess(false);
                    }

                }
            } else {
                return true;
            }

            syncConfig.setDataCount(salesOrdersLinesToBeSync.size());
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
                if(apiSalesOrderHeaderResponse!=null && apiSalesOrderHeaderResponse.getTrStatusList().size()>0){
                    for (ApiPostMobileSalesInvoiceHeaderResponse.SalesInvoiceHeaderResponse res:
                            apiSalesOrderHeaderResponse.getTrStatusList() ) {
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
                    .getString(R.string.SyncScopeUploadSO));
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
            mLog.info(mLocationName +":-"+"SYNC_SO_UP_RESULT :" + json);
        } catch (Exception e) {
            mLog.error("Error",e);
        }
        syncDbHandler.close();
    }

    private void uploadConfirmedSalesOrderHeaders() {

        apiSalesOrderHeaderParameterList = new ArrayList<ApiPostMobileSalesInvoiceHeaderParameter>();

        try {

            for (SalesOrder confirmedSalesOrder : salesOrdersToBeSync) {

                ApiPostMobileSalesInvoiceHeaderParameter SoHeaderParams = new
                        ApiPostMobileSalesInvoiceHeaderParameter();

                SoHeaderParams.setSales_Order_Date(confirmedSalesOrder.getShipmentDate());
                SoHeaderParams.setKey(confirmedSalesOrder.getKey());
                SoHeaderParams.setDocument_No(confirmedSalesOrder.getNo());
                SoHeaderParams.setCustomer_Number(confirmedSalesOrder.getSelltoCustomerNo());
                SoHeaderParams.setShip_To_Number("");
                SoHeaderParams.setSaleperson_Number(confirmedSalesOrder.getSalespersonCode());
                SoHeaderParams.setDetail_Ordinal(getInvoiceLineCount(confirmedSalesOrder.getNo()));//Jason: its total number (Count) of Invoice line
                //SoHeaderParams.setSales_Order_Date(confirmedSalesOrder.getDocumentDate());
                if (confirmedSalesOrder.getDueDate() == null) {
                    SoHeaderParams.setDue_Date("");
                } else {
                    SoHeaderParams.setDue_Date(confirmedSalesOrder.getDueDate());
                }

                if (confirmedSalesOrder.getExternalDocumentNo() == null)
                    SoHeaderParams.setPurchase_Order_Number("");
                else
                    SoHeaderParams.setPurchase_Order_Number(confirmedSalesOrder.getExternalDocumentNo());

                SoHeaderParams.setTotal_Amt_after_Discount(String.valueOf(confirmedSalesOrder.getAmountExcludingVAT()));//Jason: leave it zero if no i_discount.
                SoHeaderParams.setDiscount_Amt("0");
                SoHeaderParams.setTax_Percentage("0");//Jason: leave it zero
                SoHeaderParams.setTotal_Amt_after_Tax(String.valueOf(confirmedSalesOrder.getAmountIncludingVAT()));
                SoHeaderParams.setDriver_Code(confirmedSalesOrder.getDriverCode());

                if (confirmedSalesOrder.getComments() == null)
                    SoHeaderParams.setComment("");
                else
                    SoHeaderParams.setComment(confirmedSalesOrder.getComments());

                SoHeaderParams.setStock_Request_No("");
                SoHeaderParams.setCreated_By("");
                SoHeaderParams.setCreated_DateTime("");
                SoHeaderParams.setLast_Modified_By("");
                SoHeaderParams.setLast_Modified_DateTime("");
                SoHeaderParams.setLast_Transferred_By("");
                SoHeaderParams.setLast_Transferred_DateTime("");
                SoHeaderParams.setUserName(mApp.getCurrentUserName());
                SoHeaderParams.setPassword(mApp.getCurrentUserPassword());
                SoHeaderParams.setUserCompany(mApp.getmUserCompany());

                apiSalesOrderHeaderParameterList.add(SoHeaderParams);
            }

            logHeaderParams(apiSalesOrderHeaderParameterList);

            // no need in mvs
           /* Call<ApiPostMobileSalesInvoiceHeaderResponse> call = mApp.getNavBrokerService()
                    .PostMobileSalesOrderHeaderMSO(apiSalesOrderHeaderParameterList);

            apiSalesOrderHeaderResponse = call.execute().body();
            */
            logHeaderResponse(apiSalesOrderHeaderResponse);

        } catch (Exception e) {
            mLog.error("Error",e);
        }


    }

    private boolean saveUploadedSalesOrderHeaders() {
        setSalesOrderStatus(salesOrdersToBeSync);

        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(context);
        dbAdapter.open();
        boolean pass = dbAdapter.saveConfirmSalesOrders(salesOrdersToBeSync);
        dbAdapter.close();

        return pass;
    }

    private void setSalesOrderStatus(List<SalesOrder> comfirmedSalesOrderList) {

        try {
            for (SalesOrder so :
                    comfirmedSalesOrderList) {
                for (ApiPostMobileSalesInvoiceHeaderResponse.SalesInvoiceHeaderResponse res :
                        apiSalesOrderHeaderResponse.getTrStatusList()) {
                    if (res.getDocumentNo().equals(so.getNo())) {
                        if (res.isTransferred() == true && so.isConfirmedSo()) {
                            so.setStatus(context.getResources().getString(R.string.SalesOrderStatusConfirmed));
                            so.setTransferred(res.isTransferred());
                        } else if (res.isTransferred() == false && so.isConfirmedSo()) {
                            so.setStatus(context.getResources().getString(R.string.SalesOrderStatusConfirmed));
                        } else {
                            so.setStatus(context.getResources().getString(R.string.SalesOrderStatusPending));
                        }

                    }
                }
            }
        } catch (Exception e) {
            mLog.error("Error",e);
        }
    }

    private boolean uploadConfirmedSalesOrderLines() {
        apiSalesOrderLineParameterList = new ArrayList<ApiPostMobileSalesInvoiceLineParameter>();
        salesOrdersLinesToBeSync = new ArrayList<>();
        try {
            for (SalesOrder confirmedSalesOrder : salesOrdersToBeSync) {
                SalesOrderLineDbHandler dbAdapter = new SalesOrderLineDbHandler(context);
                dbAdapter.open();
                /*if(confirmedSalesOrder.isTransferred()) {
                    salesOrdersLinesToBeSync = dbAdapter.getAllSalesOrderLinesByDocumentNo(confirmedSalesOrder.getNo());
                }else {

                }*/

                salesOrdersLinesToBeSync = dbAdapter.getAllSalesOrderLinesByDocumentNo(confirmedSalesOrder.getNo());
                dbAdapter.close();

                for (SalesOrderLine soLine : salesOrdersLinesToBeSync) {

                    String transferred = "";
                    ApiPostMobileSalesInvoiceLineParameter soLineParams = new ApiPostMobileSalesInvoiceLineParameter();

                    soLineParams.setUserName(mApp.getCurrentUserName());
                    soLineParams.setPassword(mApp.getCurrentUserPassword());
                    soLineParams.setUserCompany(mApp.getmUserCompany());
                    //postMobileSalesInvoiceLineParameter.setKey("");
                    soLineParams.setKey(soLine.getKey());
                    soLineParams.setDocument_No(confirmedSalesOrder.getNo());
                    soLineParams.setLine_Number(soLine.getLineNo());
                    soLineParams.setItem_Number(soLine.getNo());
                    soLineParams.setLocation_Code(""); // not confirmed. will set from API
                    soLineParams.setUOM_number(soLine.getUnitofMeasure());
                    soLineParams.setQuantity(String.valueOf(soLine.getQuantity()));
                    soLineParams.setUnit_Price(String.valueOf(soLine.getUnitPrice()));
                    soLineParams.setLine_Amt_before_Discount(String.valueOf(soLine.getLineAmount())); //need to
                    // confrim the correct column to get data
                    soLineParams.setDiscount_Amt(String.valueOf(soLine.getLineDiscountAmount()));
                    soLineParams.setLine_Amt_after_Discount(String.valueOf(soLine.getLineAmount() - soLine
                            .getLineDiscountAmount())); //need to confrim the correct column to get data
                    soLineParams.setLine_Comment("");
                    soLineParams.setDriver_Code(soLine.getDriverCode());
                    soLineParams.setCreated_DateTime(confirmedSalesOrder.getCreatedDateTime());
                    soLineParams.setLast_Modified_DateTime(confirmedSalesOrder.getLastModifiedDateTime());
//soLineParams.setGen_Prod_Posting_Group(soLine.getTaxPercentage());

                    if (confirmedSalesOrder.isTransferred())
                        transferred = "1";
                    else
                        transferred = "0";

                    soLineParams.setTransferred(transferred);
                    soLineParams.setLast_Transferred_By(confirmedSalesOrder.getLastTransferredBy());
                    soLineParams.setLast_Transferred_DateTime(confirmedSalesOrder.getLastTransferredDateTime());
                    soLineParams.setNav_Document_No(""); //need to confirm the correct
                    // column to get data
                    soLineParams.setNav_Document_Line_No("");//need to confirm the correct column to
                    // get data

                    apiSalesOrderLineParameterList.add(soLineParams);
                }
            }


            logLineParams(apiSalesOrderLineParameterList);
            //no need in mvs
          /*  Call<ApiPostMobileSalesInvoiceLineResponse> call = mApp.getNavBrokerService()
                    .PostMobileSalesOrderLineMSO(apiSalesOrderLineParameterList);

            apiSalesOrderLineResponse = call.execute().body();*/

            logLineResponse(apiSalesOrderLineResponse);


        } catch (Exception e) {
            mLog.error("Error",e);
            return false;
        }

        return true;
    }

    private void logHeaderResponse() {

        if (apiSalesOrderHeaderResponse != null) {
            Gson gson = new Gson();
            String json = gson.toJson(apiSalesOrderHeaderResponse);
            //Log.d("SYNC_RES_SO_UP_HEAD", json);
            mLog.info("SYNC_RES_SO_UP_HEAD : " + json);
        } else {

            //Log.d("SYNC_RES_SO_UP_HEAD", "null");
            mLog.info("SYNC_RES_SO_UP_HEAD : " + "null");

        }
    }

    private void logLineResponse() {

        if (apiSalesOrderLineResponse != null) {
            Gson gson = new Gson();
            String json = gson.toJson(apiSalesOrderLineResponse);
            //Log.d("SYNC_RES_SO_UP_LINE", json);
            mLog.info("SYNC_RES_SO_UP_LINE : " + json);
        } else {

            //Log.d("SYNC_RES_SO_UP_LINE", "null");
            mLog.info("SYNC_RES_SO_UP_LINE : " + "null");

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
        mLog.info(mLocationName +":-"+"SYNC_SO_UP_PARAMS :" + json);

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
        mLog.info(mLocationName +":-"+"SYNC_SO_LINE_UP_PARAMS :" + json);

        for (ApiPostMobileSalesInvoiceLineParameter p:params) {
            p.setPassword(mApp.getCurrentUserPassword());
        }
    }

    private void logHeaderResponse(ApiPostMobileSalesInvoiceHeaderResponse response){


        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(response);
        //Log.d("SYNC_RES_SO_UP_HEAD", json);
        mLog.info(mLocationName +":-"+"SYNC_RES_SO_UP_HEAD :" + json);


    }

    private void logLineResponse(ApiPostMobileSalesInvoiceLineResponse response){
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(response);
        //Log.d("SYNC_RES_SO_UP_LINE", json);
        mLog.info(mLocationName +":-"+"SYNC_RES_SO_UP_LINE :" + json);
    }


}
