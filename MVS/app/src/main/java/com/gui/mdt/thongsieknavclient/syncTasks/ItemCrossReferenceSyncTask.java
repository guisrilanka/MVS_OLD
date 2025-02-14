package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemCrossReferenceParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemCrossReferenceResponse;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.ItemCrossReference;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemCrossReferenceDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 12/28/2017.
 */

public class ItemCrossReferenceSyncTask extends AsyncTask<Void, Void, Boolean> {


    Context context;
    private NavClientApp mApp;
    public AsyncResponse delegate = null;
    SyncConfiguration syncConfig;
    private boolean isForcedSync = false;
    private boolean isInitialSyncRun = false;
    private String customerCode = "";
    Logger mLog;

    String mLocationName;

    List<ApiItemCrossReferenceResponse> itemList;
    ApiItemCrossReferenceParameter apiItemCrossReferenceParameter;

    public ItemCrossReferenceSyncTask(Context context, boolean isForcedSync, boolean isInitialSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.isInitialSyncRun = isInitialSync;
        this.mLog = Log4jHelper.getLogger();
        mLocationName = ItemCrossReferenceSyncTask.class.getSimpleName();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //set parameter for request
        apiItemCrossReferenceParameter = new ApiItemCrossReferenceParameter();
        apiItemCrossReferenceParameter.setCustomerCode(getSalesCodes());
        apiItemCrossReferenceParameter.setUserCompany(mApp.getmUserCompany());
        apiItemCrossReferenceParameter.setUserName(mApp.getCurrentUserName());
        apiItemCrossReferenceParameter.setPassword(mApp.getCurrentUserPassword());
        logParams(apiItemCrossReferenceParameter);

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownLoadItemCrossRef));

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (isNetworkAvailable()) {
                Call<List<ApiItemCrossReferenceResponse>> call = mApp.getNavBrokerService()
                        .GetItemCrossReference(apiItemCrossReferenceParameter);
                itemList = call.execute().body();
                addRecords(true);
            }

        } catch (IOException e) {
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
                    .getString(R.string.SyncScopeDownloadCustomer));
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
            //Log.d("SYNC_CUSTOMER_RESULT ", json);
            //mLog.info("SYNC_CUSTOMER_RESULT :" + json);

        } catch (Exception ex) {
            //
            mLog.error("Error", ex);
        }
        syncDbHandler.close();

        Gson gson = new Gson();
        String json = gson.toJson(syncConfig);
        mLog.info("SYNC_CROSS_REF_RESULT :" + json);
    }

    private void addRecords(final boolean success) {

        if (success) {

            try {
                //customerList = apiCustomerListResult.CustomerListResultData;

                //set current sync status.
                //syncConfig.setLastSyncDateTime(apiCustomerListResult.getServerDate());
                syncConfig.setDataCount(itemList.size());
                syncConfig.setSuccess(true);

                //delete if customer exist and insert newly downloaded customer.
                ItemCrossReferenceDbHandler dbAdapter = new ItemCrossReferenceDbHandler(context);
                dbAdapter.open();
                dbAdapter.deleteItem();
                for (ApiItemCrossReferenceResponse itemCrossReferenceResult : itemList) {
//                    if(dbAdapter.deleteItem(itemCrossReferenceResult.getItem_No(),
//                            itemCrossReferenceResult.getCross_Reference_Type_No(),
//                            itemCrossReferenceResult.getKey())){

                        ItemCrossReference itemCrossReference = new ItemCrossReference();

                        itemCrossReference.setKey(itemCrossReferenceResult.getKey());
                        itemCrossReference.setItemCrossReferenceType(Integer.parseInt(itemCrossReferenceResult.getCross_Reference_Type()) );
                        itemCrossReference.setItemCrossReferenceTypeNo(itemCrossReferenceResult.getCross_Reference_Type_No());
                        itemCrossReference.setItemCrossReferenceNo(itemCrossReferenceResult.getCross_Reference_No());
                        itemCrossReference.setItemNo(itemCrossReferenceResult.getItem_No());
                        itemCrossReference.setVariantCode(itemCrossReferenceResult.getVariant_Code());
                        itemCrossReference.setUnitOfMeasure(itemCrossReferenceResult.getUnit_of_Measure());
                        itemCrossReference.setItemCrossReferenceUOM(itemCrossReferenceResult.getCross_Reference_UOM());
                        itemCrossReference.setDescription(itemCrossReferenceResult.getDescription());
                        itemCrossReference.setDiscontinueBarCode(itemCrossReferenceResult.getDiscontinue_Bar_Code());

                        dbAdapter.addItemCrossReference(itemCrossReference);
                        Log.d("SYNC_ITEM_CROSS_REF: ",itemCrossReferenceResult.getCross_Reference_Type_No());
//                    }
                }

                dbAdapter.close();
            } catch (Exception ex) {
                syncConfig.setSuccess(false);
            }
        } else {
            syncConfig.setSuccess(false);
            Log.d("SYNC_ITEM_CROSS_REF: ", "Error");
        }

        //Log sync information to Sync Configuration table.
        setSyncConfiguration(syncConfig);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

                }

            }
        }

        return itemCodes;
    }

    private void logParams(ApiItemCrossReferenceParameter params) {

        params.setPassword("*****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        mLog.info(mLocationName +":-"+"SYNC_ITEM_CROSS_REF_PARAMS :" + json);
        params.setPassword(mApp.getCurrentUserPassword());


    }

}
