package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMobileSalesOrderHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMobileSalesOrderHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesOrderLineResultData;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesOrderResult;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 01/05/2018.
 */

public class MvsSalesOrderDownloadSyncTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    private NavClientApp mApp;
    private boolean isForcedSync = false;
    private boolean isInitialSyncCompleted = false;
    Logger mLog;

    List<ApiMobileSalesOrderHeaderResponse.ApiMobileSalesOrder> soList;
    List<ApiSalesOrderLineResultData> soLineList;
    ApiMobileSalesOrderHeaderResponse apiSalesOrderResult;
    ApiMobileSalesOrderHeaderParameter apiSalesOrderHeaderParameter;

    public MvsSalesOrderDownloadSyncTask(Context context, boolean isForcedSync,boolean isInitialSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.isInitialSyncCompleted = isInitialSync;
        this.mLog= Logger.getLogger(SalesOrderDownloadSyncTask.class);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        apiSalesOrderHeaderParameter = new ApiMobileSalesOrderHeaderParameter();
        apiSalesOrderHeaderParameter.setFilterDriverCode(mApp.getmCurrentDriverCode());
        apiSalesOrderHeaderParameter.setUserName(mApp.getCurrentUserName());
        apiSalesOrderHeaderParameter.setPassword(mApp.getCurrentUserPassword());
        apiSalesOrderHeaderParameter.setUserCompany(mApp.getmUserCompany());
        String lastModified = getSyncConfiguration().getLastSyncDateTime();
        lastModified= isInitialSyncCompleted ?lastModified:getInitialSyncDate();
        //String lastModified = "";
        //apiSalesOrderHeaderParameter.setFilterLastModifiedDate(lastModified == null ? "" : lastModified);
        //apiSalesOrderHeaderParameter.setFilterLastModifiedDate(lastModified == null ? "1900-01-01" : "1900-01-01");

        logParams(apiSalesOrderHeaderParameter);


        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownLoadSO));

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if(isNetworkAvailable()) {
                Call<ApiMobileSalesOrderHeaderResponse> call =
                        mApp.getNavBrokerService().GetMvsSalesOrders(apiSalesOrderHeaderParameter);

                apiSalesOrderResult = call.execute().body();
                //logResponse(apiSalesOrderResult);

                addRecords(true);
            }

        } catch (Exception e) {
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

    private SyncConfiguration getSyncConfiguration() {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);
        SyncConfiguration sc = new SyncConfiguration();

        try {
            syncDbHandler.open();
            sc = syncDbHandler.getLastSyncInfoByScope(context.getResources()
                    .getString(R.string.SyncScopeDownLoadSO));
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
            //Log.d("SYNC_SO_RESULT ", json);
            mLog.info("SYNC_SO_RESULT :" + json);

        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }

    private void addRecords(final boolean success) {

        if (success) {
            SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(context);
            dbAdapter.open();
            SalesOrderLineDbHandler dbLineAdapter = new SalesOrderLineDbHandler(context);
            dbLineAdapter.open();

            try {
                soList = apiSalesOrderResult.getMobileSalesOrderHeaderResultData();

                //set current sync status.
                syncConfig.setLastSyncDateTime(apiSalesOrderResult.getServerDate());
                syncConfig.setDataCount(soList.size());
                syncConfig.setSuccess(true);

                for (ApiMobileSalesOrderHeaderResponse.ApiMobileSalesOrder so : soList) {
                    if (dbAdapter.deleteSalesOrder(so.getDocument_No())) {

                        SalesOrder soHeader = new SalesOrder();
                        soHeader.setKey(so.getKey());
                        soHeader.setNo(so.getDocument_No());
                        //soHeader.setSelltoCustomerName(so.getSell_to_Customer_Name());
                        //soHeader.setSelltoAddress(so.getSell_to_Address());
                        //soHeader.setSelltoContactNo(so.getSell_to_Contact_No());
                        //soHeader.setSelltoPostCode(so.getSell_to_Post_Code());
                        //soHeader.setSelltoCity(so.getSell_to_City());
                        soHeader.setDueDate(so.getSales_Order_Date());
                        soHeader.setOrderDate(so.getSales_Order_Date());
                        soHeader.setDocumentDate(so.getCreated_DateTime());
                        soHeader.setRequestedDeliveryDate(so.getSales_Order_Date());
                        soHeader.setShipmentDate(so.getSales_Order_Date());
                        soHeader.setExternalDocumentNo(so.getComment());
                        soHeader.setSalespersonCode(so.getSaleperson_Number());
                        soHeader.setDriverCode(so.getDriver_Code());
                        soHeader.setStatus(context.getResources().getString(R.string.SalesOrderStatusComplete));
                        soHeader.setCreatedBy(so.getCreated_By());
                        soHeader.setCreatedDateTime(so.getCreated_DateTime());
                        soHeader.setLastModifiedBy(so.getLast_Modified_By());
                        soHeader.setLastModifiedDateTime(so.getLast_Modified_DateTime());
                        soHeader.setAmountIncludingVAT(Float.valueOf(so.getTotal_Amt_after_Tax()));
                        soHeader.setDueDate(so.getSales_Order_Date());
                        soHeader.setSelltoCustomerNo(so.getCustomer_Number());
                        soHeader.setComment(so.getComment());

                        if(mApp.getmCurrentModule().equals(context.getResources()
                                .getString(R.string.module_mvs))){
                            soHeader.setTransferred(false);
                        }else {
                            soHeader.setTransferred(true);
                        }

                        //soHeader.setCreatedFrom(so.getCreated_From());
                        soHeader.setDelivered(false);

                        soHeader.setDownloaded(true);


                        dbAdapter.addSalesOrder(soHeader);
                        Log.d("SYNC_SO_ADDED: ", so.getDocument_No());

                        //add lines
                        /*soLineList = so.getSalesLines();
                        if (soLineList != null && soLineList.size() > 0) {
                            if (dbLineAdapter.deleteSalesOrderLineByDocumentNo(so.getNo())) {
                                for (ApiSalesOrderLineResultData res : soLineList) {
                                    SalesOrderLine soLine = new SalesOrderLine();
                                    soLine.setKey(res.getKey());
                                    soLine.setType(res.getType());
                                    soLine.setNo(res.getNo());
                                    soLine.setCrossReferenceNo(res.getCross_Reference_No());
                                    soLine.setDriverCode(res.getDriver_Code());
                                    soLine.setDescription(res.getDescription());
                                    soLine.setQuantity(res.getQuantity());
                                    soLine.setUnitofMeasure(res.getUnit_of_Measure());
                                    soLine.setSalesLineDiscExists(res.isSalesLineDiscExists());
                                    soLine.setUnitPrice(res.getUnit_Price());
                                    soLine.setLineAmount(res.getLine_Amount());
                                    soLine.setSalesLineDiscExists(res.isSalesLineDiscExists());
                                    soLine.setLineDiscountPercent(res.getLine_Discount_Percent());
                                    soLine.setLineDiscountAmount(res.getLine_Discount_Amount());
                                    soLine.setQtytoInvoice(res.getQty_to_Invoice());
                                    soLine.setQuantityInvoiced(res.getQuantity_Invoiced());
                                    soLine.setDocumentNo(res.getDocument_No());
                                    soLine.setLineNo(res.getLine_No());
                                    soLine.setTotalAmountExclVAT(res.getAmount());
                                    soLine.setTotalVATAmount(res.getAmount_Incl_VAT()-res.getAmount());
                                    soLine.setTotalAmountInclVAT(res.getAmount_Incl_VAT());

                                    dbLineAdapter.addSalesOrderLine(soLine);
                                    Log.d("SYNC_SO_LINE ADDED: ", soLine.getNo() == null ? "" : soLine.getNo());
                                }

                            }
                        }*/
                    }

                }
            } catch (Exception ex) {
                syncConfig.setSuccess(false);
                Log.d("SYNC_SO_ERROR: ", ex.toString());
                dbAdapter.close();
                dbLineAdapter.close();
            }
            dbAdapter.close();
            dbLineAdapter.close();
        } else {
            syncConfig.setSuccess(false);
            Log.d("SYNC_SO_ERROR: ", "Api Calling Error");
        }

        //Log sync information to Sync Configuration table.
        setSyncConfiguration(syncConfig);
    }

    private String getInitialSyncDate(){
        String lastModifiedDate = "";

        DateTime currDate = DateTime.now().minusDays(4);
        lastModifiedDate = currDate.toString();

        return lastModifiedDate;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void logParams(ApiMobileSalesOrderHeaderParameter params){

        params.setPassword("****");

        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_SO_DOWN_PARAMS :" + json);

        params.setPassword(mApp.getCurrentUserPassword());

    }

    private void logResponse(ApiSalesOrderResult response){
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(response);
        //Log.d("SYNC_SO_DOWN_RES", json);
        mLog.info("SYNC_SO_DOWN_RES :" + json);
    }



}

