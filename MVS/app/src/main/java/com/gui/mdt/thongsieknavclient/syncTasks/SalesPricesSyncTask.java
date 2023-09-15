package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesPricesListParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesPricesResponse;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesPrices;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesPricesDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

/**
 * Created by bhanuka on 09/08/2017.
 */

public class SalesPricesSyncTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    ApiSalesPricesListParameter mApiSalesPricesListParameter;
    List<ApiSalesPricesResponse.ApiSalesPricesListResultData> apiSalesPricesListResultData;
    ApiSalesPricesResponse apiSalesPricesResponse;
    Logger mLog;
    private boolean isInitialSyncRun = false;
    private NavClientApp mApp;
    private boolean isForcedSync = false;

    public SalesPricesSyncTask(Context context, boolean isForcedSync, boolean isInitialSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.isInitialSyncRun = isInitialSync;
        this.mLog = Logger.getLogger(SalesPricesSyncTask.class);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String endingDate = dateFormat.format(date);

        //set parameters for request
        mApiSalesPricesListParameter = new ApiSalesPricesListParameter();
        mApiSalesPricesListParameter.setSalesCode(getSalesCodes());
        //mApiSalesPricesListParameter.setSalesCode("VRS31");
        mApiSalesPricesListParameter.setCustomerName("");
        mApiSalesPricesListParameter.setStarting_Date("");
        //mApiSalesPricesListParameter.setEnding_Date(DateTime.now().minusDays(1).toString());
        mApiSalesPricesListParameter.setEnding_Date(endingDate); //update on 2018-04-02 (two days before today)
        mApiSalesPricesListParameter.setSalesType("0|1");
        mApiSalesPricesListParameter.setUserName(mApp.getCurrentUserName());
        mApiSalesPricesListParameter.setPassword(mApp.getCurrentUserPassword());
        mApiSalesPricesListParameter.setUserCompany(mApp.getmUserCompany());

        String lastModified = getSyncConfiguration().getLastSyncDateTime();
        lastModified = isInitialSyncRun ? lastModified : "";
        //mApiSalesPricesListParameter.setFilterLastModifiedDate(lastModified == null ? "" : lastModified);
        mApiSalesPricesListParameter.setFilterLastModifiedDate(""); //update on 2018-04-02

        logParams(mApiSalesPricesListParameter);

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownLoadSalesPrice));


    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            if (isNetworkAvailable()) {
                Call<ApiSalesPricesResponse> call = mApp.getNavBrokerService().GetSalesPrices
                        (mApiSalesPricesListParameter);
                apiSalesPricesResponse = call.execute().body();
                addRecords(true);
            }

        } catch (IOException e) {
            //Log.d("NAV_Client_Exception", e.toString());
            mLog.error("Error", e);
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
        this.cancel(true);
    }

    private SyncConfiguration getSyncConfiguration() {
        SyncConfigurationDbHandler syncDbHandler = new SyncConfigurationDbHandler(context);
        SyncConfiguration sc = new SyncConfiguration();

        try {
            syncDbHandler.open();
            sc = syncDbHandler.getLastSyncInfoByScope(context.getResources()
                    .getString(R.string.SyncScopeDownLoadSalesPrice));
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
            //Log.d("SYNC_SALE_PRICE_RESULT ", json);
            mLog.info("SYNC_SALE_PRICE_RESULT :" + json);
        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }

    private void addRecords(final boolean success) {

        if (success) {
            SalesPricesDbHandler dbAdapter = new SalesPricesDbHandler(context);
            dbAdapter.open();
            try {

                if (apiSalesPricesResponse != null) {
                    apiSalesPricesListResultData = apiSalesPricesResponse.getSalesPriceListResultData();

                    //set current sync status.
                    syncConfig.setLastSyncDateTime(apiSalesPricesResponse.getServerDate());
                    syncConfig.setDataCount(apiSalesPricesListResultData.size());
                    syncConfig.setSuccess(true);
                } else {
                    syncConfig.setSuccess(false);
                }
                //boolean isDeleteNeeded=true;
                //isDeleteNeeded = dbAdapter.getSalesPricesCount()<0;

                //clear sales price table
                dbAdapter.deleteAllSalesPrices();
                Log.d("SALES_PRICES_TABLE: ","CLEARED");

                for (ApiSalesPricesResponse.ApiSalesPricesListResultData salesPricesResult :
                        apiSalesPricesListResultData) {

                    SalesPrices salesPrices = new SalesPrices();


                    salesPrices.setKey(salesPricesResult.getKey());
                    //salesPrices.setSalesCodeFilterCtrl(salesPricesResult.getSalesCodeFilterCtrl());
                    //salesPrices.setItemNoFilterCtrl(salesPricesResult.getItemNoFilterCtrl());
                    //salesPrices.setStartingDate(salesPricesResult.getStartingDateFilter());
                    //salesPrices.setSalesCodeFilterCtrl2(salesPricesResult.getSalesCodeFilterCtrl2());
                    salesPrices.setSalesType(salesPricesResult.getSalesType());
                    salesPrices.setSalesCode(salesPricesResult.getSalesCode());
                    salesPrices.setCustomerName(salesPricesResult.getCustomerName());
                    salesPrices.setItemNo(salesPricesResult.getItemNo());
                    salesPrices.setItemDescription(salesPricesResult.getItemDescription());
                    salesPrices.setVariantCode(salesPricesResult.getVariantCode() == null ? "" : salesPricesResult
                            .getVariantCode());
                    salesPrices.setCurrencyCode(salesPricesResult.getCurrencyCode() == null ? "" : salesPricesResult
                            .getCurrencyCode());
                    salesPrices.setUnitofMeasureCode(salesPricesResult.getUnitOfMeasureCode());
                    salesPrices.setMinimumQuantity(salesPricesResult.getMinimumQuantity());
                    salesPrices.setPublishedPrice(salesPricesResult.getUnit_Price());
                    salesPrices.setCost(salesPricesResult.getCost());
                    salesPrices.setCostPlusPercent(salesPricesResult.getCostPlusPercent());
                    salesPrices.setDiscountAmount(salesPricesResult.getDiscountAmount());
                    salesPrices.setUnitPrice(salesPricesResult.getUnit_Price());
                    salesPrices.setStartingDate(salesPricesResult.getStartingDate());
                    salesPrices.setEndingDate(salesPricesResult.getEndingDate());
                    salesPrices.setPriceIncludesVAT(salesPricesResult.getPriceIncludesVAT());
                    salesPrices.setAllowLineDisc(salesPricesResult.getAllowLineDisc());
                    salesPrices.setAllowInvoiceDisc(salesPricesResult.getAllowInvoiceDisc());
                    salesPrices.setVATBusPostingGrPrice(salesPricesResult.getVATBusPostingGrPrice());
                    salesPrices.setItemTemplateSequence(Integer.parseInt(salesPricesResult
                            .getItem_Template_Sequence()));

                    //if (dbAdapter.deleteItem(salesPrices)) {
                        dbAdapter.addSalesPrices(salesPrices);
                        Log.d("SYNC_SALES_PRICES_ADD: ", salesPricesResult.getItemNo());
                    //}
                    //Log.d("SYNC_SALES_PRICES_ADD: ", salesPricesResult.getItemNo());

                       /* if(!isDeleteNeeded) {
                            dbAdapter.addSalesPrices(salesPrices);
                            Log.d("SYNC_SALES_PRICES_ADD: ", salesPricesResult.getItemNo());
                        }else if(dbAdapter.deleteItem(salesPrices)){
                            dbAdapter.addSalesPrices(salesPrices);
                            Log.d("SYNC_SALES_PRICES_ADD: ", salesPricesResult.getItemNo());
                        }*/

                }
                dbAdapter.close();

            } catch (Exception ex) {
                dbAdapter.close();
                syncConfig.setSuccess(false);
            }
        } else {
            syncConfig.setSuccess(false);
        }

        //Log sync information to Sync Configuration table.
        setSyncConfiguration(syncConfig);
    }

    private String getSalesCodes() {

        List<Customer> customerList;
        int count = 0;
        Customer tempCustomer;
        String itemCodes = "";

        CustomerDbHandler dbAdapter = new CustomerDbHandler(context);
        dbAdapter.open();
        customerList = dbAdapter.getAllCustomer();
        dbAdapter.close();

        if (customerList.size() > 0) {
            for (int i = count; count < customerList.size(); count++) {

                tempCustomer = customerList.get(count);
                String cusCode = tempCustomer.getCode();
                String cusGroup = tempCustomer.getCustomerPriceGroup();

                if (customerList.size() == (count + 1)) {
                    itemCodes = itemCodes + cusCode;

                    String [] salesTypesArray=itemCodes.split("\\|");
                    if(
                            !Arrays.asList(salesTypesArray).contains(cusCode) ||
                                    !Arrays.asList(salesTypesArray).contains(cusGroup) )
                    {
                        if (!(cusGroup == null || cusGroup.equals(""))) {
                            itemCodes = itemCodes + "|" + cusGroup;
                        }
                    }

                    /*if (!itemCodes.contains(cusCode) || !itemCodes.contains(cusGroup)) {
                        if (!(cusGroup == null || cusGroup.equals(""))) {
                            itemCodes = itemCodes + "|" + cusGroup;
                        }
                    }*/

                } else {
                    itemCodes = itemCodes + cusCode + "|";

                    String [] salesTypesArray=itemCodes.split("\\|");
                    if(
                            !Arrays.asList(salesTypesArray).contains(cusCode) ||
                                    !Arrays.asList(salesTypesArray).contains(cusGroup) )
                    {
                        if (!(cusGroup == null || cusGroup.equals(""))) {
                            itemCodes = itemCodes + cusGroup + "|";
                        }
                    }

                    /*if (!itemCodes.contains(cusCode) || !itemCodes.contains(cusGroup)) {
                        if (!(cusGroup == null || cusGroup.equals(""))) {
                            itemCodes = itemCodes + cusGroup + "|";
                        }
                    }*/

                }

            }
        }

        return itemCodes;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void logParams(ApiSalesPricesListParameter params) {

        params.setPassword("****");

        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_S_PRICE_PARAMS :" + json);

        params.setPassword(mApp.getCurrentUserPassword());

    }


}

