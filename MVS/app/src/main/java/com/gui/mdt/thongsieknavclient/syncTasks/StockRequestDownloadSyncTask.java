package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.DataConverter;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiStockRequestHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiStockRequestHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiStockRequestLineResponse;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequest;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequestLine;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.StockRequestDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockRequestLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 10/25/2017.
 */

public class StockRequestDownloadSyncTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    Logger mLog;
    List<ApiStockRequestHeaderResponse.StockRequestHeaderResultData> srList;
    List<ApiStockRequestLineResponse> srLineList;
    ApiStockRequestHeaderResponse apiStockRequestHeaderResponse;
    ApiStockRequestHeaderParameter apiStockRequestHeaderParameter;
    //List<ApiStockRequestLineResponse.StockRequestLineResultData> srLinesList;
    //ApiStockRequestLineResponse apiStockRequestLineResponse;
    //ApiStockRequestLineParameter apiStockRequestLineParameter;
    private NavClientApp mApp;
    private boolean isForcedSync = false;
    private boolean isInitialSyncCompleted = false;
    SimpleDateFormat dBDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public StockRequestDownloadSyncTask(Context context, boolean isForcedSync, boolean isInitialSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.isInitialSyncCompleted = isInitialSync;
        this.mLog = Logger.getLogger(SalesOrderDownloadSyncTask.class);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        apiStockRequestHeaderParameter = new ApiStockRequestHeaderParameter();

        apiStockRequestHeaderParameter.setFilterStockRequest("");
        apiStockRequestHeaderParameter.setFilterSalePersonNumber(mApp.getmCurrentSalesPersonCode());
        apiStockRequestHeaderParameter.setFilterDriverCode(mApp.getmCurrentDriverCode());
        apiStockRequestHeaderParameter.setUserName(mApp.getCurrentUserName());
        apiStockRequestHeaderParameter.setPassword(mApp.getCurrentUserPassword());
        apiStockRequestHeaderParameter.setUserCompany(mApp.getmUserCompany());
        apiStockRequestHeaderParameter.setFilterCustomerNumber("");
        apiStockRequestHeaderParameter.setFilterDelivered(false);
        apiStockRequestHeaderParameter.setFilterDeliveredSpecified(false);

        String lastModified = getSyncConfiguration().getLastSyncDateTime();
        lastModified = isInitialSyncCompleted ? lastModified : getInitialSyncDate();
        //String lastModified = "";
        //apiStockRequestHeaderParameter.setFilterLastModifiedDate(lastModified == null ? "" : lastModified);
        apiStockRequestHeaderParameter.setFilterLastModifiedDate(lastModified == null ? "" : "");

        logParams(apiStockRequestHeaderParameter);


        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownLoadSR));

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {

            if (isNetworkAvailable()) {
                Call<ApiStockRequestHeaderResponse> call =
                        mApp.getNavBrokerService().GetStockRequest(apiStockRequestHeaderParameter);
                apiStockRequestHeaderResponse = call.execute().body();
                //logResponse(apiStockRequestHeaderResponse);

                addSRRecords(true);
            }

        } catch (Exception e) {
            //Log.d("NAV_Client_Exception", e.toString());
            mLog.error("Error", e);
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        SyncStatus syncStatus = new SyncStatus();
        syncStatus.setScope(mApp.getResources().getString(R.string.SyncScopeDownLoadSR));
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
                    .getString(R.string.SyncScopeDownLoadSR));
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

    private void addSRRecords(final boolean success) {
        if (success) {
            StockRequestDbHandler dbAdapter = new StockRequestDbHandler(context);
            dbAdapter.open();
            StockRequestLineDbHandler dbLineAdapter = new StockRequestLineDbHandler(context);
            dbLineAdapter.open();

            try {
                Date dt = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(dt);
                c.add(Calendar.MONTH, -1);
                dt = c.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                List<String> srNoList = dbAdapter.getAllStockRequestNoByDate(dateFormat.format(dt));
//                for(String no : srNoList){
                dbLineAdapter.deleteStockRequestLinesByDate(dateFormat.format(dt));
//                }

                dbAdapter.deleteStockRequestByDate(dateFormat.format(dt));


                srList = apiStockRequestHeaderResponse.getStockRequestListResultData();

                //set current sync status.
                syncConfig.setLastSyncDateTime(apiStockRequestHeaderResponse.getServerDate());
                syncConfig.setDataCount(srList.size());
                syncConfig.setSuccess(true);

                for (ApiStockRequestHeaderResponse.StockRequestHeaderResultData sr : srList) {
                    //if (dbAdapter.deleteStockRequest(sr.getNo())) {
                    if (!dbAdapter.isSrExist(sr.getNo())) {
                        srLineList = sr.getStockRequestLine();

                        StockRequest srHeader = new StockRequest();

                        srHeader.setNo(sr.getNo());
                        srHeader.setSelltoCustomerNo(sr.getSell_to_Customer_No());
                        srHeader.setSelltoCustomerName(sr.getSell_to_Customer_Name());
                        srHeader.setExternalDocumentNo(sr.getExternal_Document_No());
                        srHeader.setSelltoPostCode(sr.getSell_to_Post_Code());
                        srHeader.setSelltoContact(sr.getSell_to_Contact_No());
                        srHeader.setAmountInclVAT(sr.getAmount_Including_VAT());
                        srHeader.setPostingDate(sr.getPosting_Date());
                        srHeader.setOrderDate(DataConverter.ConvertJsonDateToYearMonthDay(sr.getShipment_Date()));
                        srHeader.setShipmentDate(DataConverter.ConvertJsonDateToYearMonthDay(sr.getShipment_Date()));
                        srHeader.setLocationCode(sr.getLocation_Code());
                        srHeader.setSalespersonCode(sr.getSalesperson_Code());
                        srHeader.setDriverCode(sr.getDriver_Code());
                        srHeader.setStatus("2");
                        srHeader.setCreatedBy(sr.getCreated_By());
                        srHeader.setCreatedDateTime(sr.getCreated_DateTime());
                        srHeader.setLastModifiedBy(sr.getLast_Modified_By());
                        srHeader.setLastModifiedDateTime(sr.getLast_Modified_DateTime());
                        srHeader.setDownloaded(true);

                        dbAdapter.addStockRequest(srHeader);
                        Log.d("SYNC_SR_ADDED: ", sr.getNo());


                        if (srLineList != null && srLineList.size() > 0) {
                            if (dbLineAdapter.deleteStockRequestLinesByDocumentNo(sr.getNo())) {
                                for (ApiStockRequestLineResponse srLine : srLineList) {
                                    StockRequestLine newSrLine = new StockRequestLine();

                                    newSrLine.setKey(srLine.getKey());
                                    newSrLine.setDocumentNo(srLine.getDocument_No());
                                    newSrLine.setLineNo(srLine.getLine_No());
                                    newSrLine.setType(srLine.getType());
                                    newSrLine.setItemNo(srLine.getNo());
                                    newSrLine.setDescription(srLine.getDescription());
                                    newSrLine.setQuantity(srLine.getQuantity());
                                    newSrLine.setUnitofMeasureCode(srLine.getUnit_of_Measure_Code());
                                    newSrLine.setUnitPrice(srLine.getUnit_Price());
                                    newSrLine.setAmount(srLine.getAmount());
                                    newSrLine.setLineAmount(srLine.getLine_Amount());
                                    newSrLine.setLineDiscountAmount(srLine.getLine_Discount_Amount());
                                    newSrLine.setLineDiscountPercent(srLine.getLine_Discount_Percent());
                                    newSrLine.setDriverCode(srLine.getDriver_Code());
                                    newSrLine.setTotalVATAmount(srLine.getAmount_Incl_VAT() - srLine.getAmount());
                                    newSrLine.setTotalAmountInclVAT(srLine.getAmount_Incl_VAT());

                                    dbLineAdapter.addStockRequestLine(newSrLine);
                                    Log.d("SYNC_SR_LINE ADDED: ", newSrLine.getDocumentNo() == null ? "" : newSrLine.getDocumentNo());
                                }

                            }
                        }
                    }
                    dbAdapter.close();
                    dbLineAdapter.close();
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

    private String getInitialSyncDate() {
        String lastModifiedDate = "";

        DateTime currDate = DateTime.now().minusDays(4);
        lastModifiedDate = currDate.toString();

        return lastModifiedDate;
    }

    private void logParams(ApiStockRequestHeaderParameter params) {

        params.setPassword("****");

        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_SO_DOWN_PARAMS :" + json);

        params.setPassword(mApp.getCurrentUserPassword());

    }

    private void logResponse(ApiStockRequestHeaderResponse response) {
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(response);
        //Log.d("SYNC_SO_DOWN_RES", json);
        mLog.info("SYNC_SO_DOWN_RES :" + json);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
