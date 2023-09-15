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
import com.gui.mdt.thongsieknavclient.datamodel.ApiMobileSalesOrderLineParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMobileSalesOrderLineResponse;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 01/02/2018.
 */

public class SalesOrderClearAndDownloadForMvsSyncTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    Logger mLog;
    String mSoNumbersToDownloadLines = "";
    List<ApiMobileSalesOrderHeaderResponse.ApiMobileSalesOrder> soList;
    List<ApiMobileSalesOrderLineResponse.MobileSalesOrderLineListResultData> soLineList;
    ApiMobileSalesOrderHeaderResponse apiSalesOrderResult;
    ApiMobileSalesOrderHeaderParameter apiSalesOrderHeaderParameter;
    ApiMobileSalesOrderLineParameter apiMobileSalesOrderLineParameter;
    ApiMobileSalesOrderLineResponse apiMobileSalesOrderLineResponse;
    private NavClientApp mApp;
    private boolean isForcedSync = false;
    private boolean isInitialSyncCompleted = false;

    public SalesOrderClearAndDownloadForMvsSyncTask(Context context, boolean isForcedSync, boolean isInitialSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.isInitialSyncCompleted = isInitialSync;
        this.mLog = Logger.getLogger(SalesOrderDownloadSyncTask.class);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        apiSalesOrderHeaderParameter = new ApiMobileSalesOrderHeaderParameter();
       /* apiSalesOrderHeaderParameter.setFilterSalesOrder("");
        apiSalesOrderHeaderParameter.setFilterSalePersonNumber("");
        apiSalesOrderHeaderParameter.setFilterSalePersonNumber(mApp.getmCurrentSalesPersonCode());*/
        apiSalesOrderHeaderParameter.setFilterDriverCode(mApp.getmCurrentDriverCode());
        //apiSalesOrderHeaderParameter.setFilterCustomerNumber("");
        apiSalesOrderHeaderParameter.setUserName(mApp.getCurrentUserName());
        apiSalesOrderHeaderParameter.setPassword(mApp.getCurrentUserPassword());
        apiSalesOrderHeaderParameter.setUserCompany(mApp.getmUserCompany());
        /*String lastModified = getSyncConfiguration().getLastSyncDateTime();
        lastModified= isInitialSyncCompleted ?lastModified:getInitialSyncDate();
        //String lastModified = "";
        apiSalesOrderHeaderParameter.setFilterLastModifiedDate("");*/
        apiSalesOrderHeaderParameter.setFilterSONumber("");

        logParams(apiSalesOrderHeaderParameter);


        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownLoadSO));

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (isNetworkAvailable()) {
                Call<ApiMobileSalesOrderHeaderResponse> call = mApp.getNavBrokerService().GetMvsSalesOrders
                        (apiSalesOrderHeaderParameter);

                apiSalesOrderResult = call.execute().body();
                logResponse(apiSalesOrderResult);

                deleteAllHqDownloadedSalesOrdersForMvs(); //2018-02-28 navi. will change some fields in blanket orders. need to download latest.
                removeNonAssignedSo();// 2018-02-26  some order's assigned driver will change from navi.
                addHeaderRecords(true);
                addLineRecords();
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

    private void addHeaderRecords(final boolean success) {

        if (success) {
            SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(context);
            dbAdapter.open();
            SalesOrderLineDbHandler dbLineAdapter = new SalesOrderLineDbHandler(context);
            dbLineAdapter.open();

            try {
                Date dt = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(dt);
                c.add(Calendar.MONTH, -1);
                dt = c.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                List<String> soNoList = dbAdapter.getAllSalesOrderNoByDate(dateFormat.format(dt));
                for (String no : soNoList) {
                    dbLineAdapter.deleteSOLineByDocumentNo(no);
                }

                dbAdapter.deleteSalesOrderByDate(dateFormat.format(dt));

                soList = apiSalesOrderResult.getMobileSalesOrderHeaderResultData();

                //set current sync status.
                syncConfig.setLastSyncDateTime(apiSalesOrderResult.getServerDate());
                syncConfig.setDataCount(soList.size());
                syncConfig.setSuccess(true);

                for (ApiMobileSalesOrderHeaderResponse.ApiMobileSalesOrder so : soList) {
                    if (so.getSales_Order_No() != null) {
                        if (!dbAdapter.isSoExist(so.getSales_Order_No())) {

                            SalesOrder soHeader = new SalesOrder();
                            soHeader.setKey(so.getKey());
                            soHeader.setNo(so.getSales_Order_No());
                            setCustomerInfo(soHeader, so);
                            soHeader.setDueDate(so.getDue_Date());
                            soHeader.setOrderDate(so.getSales_Order_Date());
                            soHeader.setDocumentDate(so.getCreated_DateTime());
                            soHeader.setRequestedDeliveryDate(so.getDue_Date());
                            soHeader.setShipmentDate(so.getDue_Date());
                            soHeader.setExternalDocumentNo(so.getComment());
                            soHeader.setSalespersonCode(so.getSaleperson_Number());
                            soHeader.setDriverCode(so.getDriver_Code());
                            soHeader.setStatus(context.getResources().getString(R.string.SalesOrderStatusComplete));
                            soHeader.setCreatedBy(so.getCreated_By());
                            soHeader.setCreatedDateTime(so.getCreated_DateTime());
                            soHeader.setLastModifiedBy(so.getLast_Modified_By());
                            soHeader.setLastModifiedDateTime(so.getLast_Modified_DateTime());
                            soHeader.setAmountIncludingVAT(Float.valueOf(so.getTotal_Amt_after_Tax()));
                            soHeader.setAmountExcludingVAT(Float.valueOf(so.getTotal_Amt_after_Discount()));
                            soHeader.setVATAmount(Float.valueOf(so.getTotal_Amt_after_Tax()) - Float.valueOf(so
                                    .getTotal_Amt_after_Discount()));
                            soHeader.setDueDate(so.getDue_Date());
                            soHeader.setSelltoCustomerNo(so.getCustomer_Number());
                            soHeader.setComment(so.getComment());

                            if (mApp.getmCurrentModule().equals(context.getResources()
                                    .getString(R.string.module_mvs))) {
                                soHeader.setTransferred(false);
                            } else {
                                soHeader.setTransferred(true);
                            }

                            //soHeader.setCreatedFrom(so.getCreated_From());
                            soHeader.setDelivered(false);

                            soHeader.setDownloaded(true);

                            if (!so.isVoid()) //*so not exist and not void, add so
                            {
                                dbAdapter.addSalesOrder(soHeader);
                                Log.d("SYNC_SO_ADDED: ", so.getSales_Order_No());

                                if (dbLineAdapter.deleteSalesOrderLineByDocumentNo(so.getSales_Order_No())) {
                                    mSoNumbersToDownloadLines = mSoNumbersToDownloadLines + soHeader.getNo() + "|";
                                }
                            }
                        } else {
                            //*so exist and void, remove so and lines
                            if (so.isVoid()) {
                                dbAdapter.deleteSO(so.getSales_Order_No());
                                dbLineAdapter.deleteSalesOrderLineByDocumentNo(so.getSales_Order_No());
                            }
                        }
                    } else {
                        Log.e("SYNC_SO_ERROR:", "SO NO IS NULL");
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

    private void addLineRecords() {

        SalesOrderLineDbHandler dbLineAdapter = new SalesOrderLineDbHandler(context);
        dbLineAdapter.open();
        try {
            if (mSoNumbersToDownloadLines.length() > 0 && mSoNumbersToDownloadLines.lastIndexOf("|") > 0) {


                mSoNumbersToDownloadLines = mSoNumbersToDownloadLines.substring(0, mSoNumbersToDownloadLines.length() - 1);
                /*char[] Chars = mSoNumbersToDownloadLines.toCharArray();
                Chars[]
                Chars[mSoNumbersToDownloadLines.lastIndexOf("|")] = "''";
                mSoNumbersToDownloadLines = String.valueOf(Chars);*/

                apiMobileSalesOrderLineParameter = new ApiMobileSalesOrderLineParameter();
                apiMobileSalesOrderLineParameter.setDocument_No(mSoNumbersToDownloadLines);
                apiMobileSalesOrderLineParameter.setUserName(mApp.getCurrentUserName());
                apiMobileSalesOrderLineParameter.setPassword(mApp.getCurrentUserPassword());
                apiMobileSalesOrderLineParameter.setUserCompany(mApp.getmUserCompany());

                Call<ApiMobileSalesOrderLineResponse> call = mApp.getNavBrokerService()
                        .GetMvsSalesOrderLines(apiMobileSalesOrderLineParameter);

                ApiMobileSalesOrderLineResponse res = call.execute().body();
                if (res != null && res.getMobileSalesOrderLineListResultData() != null) {
                    soLineList = res.getMobileSalesOrderLineListResultData();
                }

                if (soLineList.size() > 0) {
                    for (ApiMobileSalesOrderLineResponse.MobileSalesOrderLineListResultData line : soLineList) {
                        SalesOrderLine soLine = new SalesOrderLine();
                        soLine.setKey(line.getKey());
                        soLine.setType(2);
                        soLine.setNo(line.getItem_Number());
                        soLine.setCrossReferenceNo("");
                        soLine.setDriverCode(line.getDriver_Code());
                        //soLine.setDescription(line.getDescription());
                        soLine.setQuantity(Float.valueOf(line.getQuantity()));
                        soLine.setUnitofMeasure(line.getUOM_number());
                        soLine.setSalesLineDiscExists(false);
                        soLine.setUnitPrice(Float.valueOf(line.getUnit_Price()));
                        soLine.setLineAmount(Float.valueOf(line.getLine_Amt_before_Discount()));
                        soLine.setSalesLineDiscExists(false);
                        soLine.setQtytoInvoice(Float.valueOf(line.getQuantity()));
                        soLine.setQuantityInvoiced(Float.valueOf(0));
                        soLine.setDocumentNo(line.getDocument_No());
                        soLine.setLineNo(line.getLine_Number());
                        soLine.setTotalAmountExclVAT(Float.valueOf(line.getLine_Amt_before_Discount()));
                        soLine.setTotalVATAmount(Float.valueOf(
                                line.getLine_Amt_after_Discount()) - Float.valueOf(line.getLine_Amt_before_Discount()));
                        soLine.setTotalAmountInclVAT(Float.valueOf(line.getLine_Amt_after_Discount()));

                        dbLineAdapter.addSalesOrderLine(soLine);
                        Log.d("SYNC_SO_LINE ADDED: ", soLine.getNo() == null ? "" : soLine.getNo());
                    }

                }
            }

        } catch (Exception ex) {
            syncConfig.setSuccess(false);
            Log.d("SYNC_SO_ERROR: ", ex.toString());
            dbLineAdapter.close();
        }
        dbLineAdapter.close();
    }

    private void removeNonAssignedSo() {
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(context);
        dbAdapter.open();
        SalesOrderLineDbHandler dbLineAdapter = new SalesOrderLineDbHandler(context);
        dbLineAdapter.open();

        List<SalesOrder> pendingSalesOrderLIst = new ArrayList<SalesOrder>();
        pendingSalesOrderLIst = dbAdapter.getDownloadedPendingSalesOrder();

        ApiMobileSalesOrderHeaderParameter params = new ApiMobileSalesOrderHeaderParameter();
        params.setFilterDriverCode("");
        params.setUserName(mApp.getCurrentUserName());
        params.setPassword(mApp.getCurrentUserPassword());
        params.setUserCompany(mApp.getmUserCompany());
        String soNos = getSoNos(pendingSalesOrderLIst);
        params.setFilterSONumber(soNos);

        try {
            Call<ApiMobileSalesOrderHeaderResponse> call = mApp.getNavBrokerService().GetMvsSalesOrders
                    (params);
            ApiMobileSalesOrderHeaderResponse respnose = null;
            if (!soNos.equals("")) {
                respnose = call.execute().body();
            }


            if (respnose != null && respnose.getMobileSalesOrderHeaderResultData().size() > 0) {

                for (ApiMobileSalesOrderHeaderResponse.ApiMobileSalesOrder so
                        : respnose.getMobileSalesOrderHeaderResultData()) {

                    if (!so.getDriver_Code().equals(mApp.getmCurrentDriverCode())) { //delete if driver changed in navi.
                        if (dbAdapter.deleteSalesOrder(so.getSales_Order_No())) {
                            dbLineAdapter.deleteSalesOrderLineByDocumentNo(so.getDocument_No());
                        }
                    } else if (so.isVoid()) { //delete if void from navi.
                        if (dbAdapter.deleteSalesOrder(so.getSales_Order_No())) {
                            dbLineAdapter.deleteSalesOrderLineByDocumentNo(so.getDocument_No());
                        }
                    } else { //delete if deleted from navi
                        String no = "";
                        for (SalesOrder soExist :
                                pendingSalesOrderLIst) {
                            if (so.getSales_Order_No().equals(soExist.getNo())) {
                                no = so.getSales_Order_No();
                            }
                        }

                        if (no.equals("")) {
                            if (dbAdapter.deleteSalesOrder(so.getSales_Order_No())) {
                                dbLineAdapter.deleteSalesOrderLineByDocumentNo(so.getSales_Order_No());
                            }
                        }
                    }
                }
            }


        } catch (Exception ex) {
            Log.d("error", ex.toString());
            dbLineAdapter.close();
            dbAdapter.close();
        }

        dbAdapter.close();
        dbLineAdapter.close();


    }

    private void setCustomerInfo(SalesOrder soHeader, ApiMobileSalesOrderHeaderResponse.ApiMobileSalesOrder so) {

        CustomerDbHandler cusDbAdapter = new CustomerDbHandler(context);
        cusDbAdapter.open();
        Customer customer = cusDbAdapter.getCustomerByCustomerCode(so.getCustomer_Number());
        if (customer != null || customer.getCode() != "") {
            soHeader.setSelltoCustomerName(customer.getName());
            soHeader.setSelltoAddress(customer.getAddress());
            soHeader.setSelltoContactNo(customer.getContact());
            soHeader.setSelltoPostCode(customer.getPostalCode());
            soHeader.setSelltoCity("");
        }
        cusDbAdapter.close();
    }

    private String getInitialSyncDate() {
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

    private void logParams(ApiMobileSalesOrderHeaderParameter params) {

        params.setPassword("****");

        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_SO_DOWN_PARAMS :" + json);

        params.setPassword(mApp.getCurrentUserPassword());

    }

    private void logResponse(ApiMobileSalesOrderHeaderResponse response) {
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(response);
        //Log.d("SYNC_SO_DOWN_RES", json);
        mLog.info("SYNC_SO_DOWN_RES :" + json);
    }

    private String getSoNos(List<SalesOrder> pendingSalesOrderLIst) {

        List<SalesOrder> soList;
        int count = 0;
        SalesOrder so;
        String soNos = "";

        if (pendingSalesOrderLIst.size() > 0) {
            for (int i = count; count < pendingSalesOrderLIst.size(); count++) {

                so = pendingSalesOrderLIst.get(count);
                soNos = pendingSalesOrderLIst.size() == (count + 1) ? soNos + so.getNo()
                        : soNos + so.getNo() + "|";
            }
        }

        return soNos;
    }

    private boolean deleteAllHqDownloadedSalesOrdersForMvs() {

        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(mApp.getApplicationContext());
        dbAdapter.open();
        boolean deleted = dbAdapter.deleteAllHqDownloadedSalesOrdersForMvs();
        dbAdapter.close();

        return deleted;
    }
}