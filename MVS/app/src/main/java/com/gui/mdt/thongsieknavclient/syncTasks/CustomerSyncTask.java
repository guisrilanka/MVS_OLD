package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerListParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerListResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMobileSalesOrderHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMobileSalesOrderHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 07/19/2017.
 */


public class CustomerSyncTask extends AsyncTask<Void, Void, Boolean> {


    Context context;
    private NavClientApp mApp;
    public AsyncResponse delegate = null;
    SyncConfiguration syncConfig;
    private boolean isForcedSync = false;
    private boolean isInitialSyncRun = false;
    private String customerCode = "";
    Logger mLog;

    List<ApiCustomerListResult.ApiCustomerResultResponse> customerList;
    ApiCustomerListResult apiCustomerListResult;
    ApiCustomerListParameter apiCustomerListParameter;

    public CustomerSyncTask(Context context, boolean isForcedSync, boolean isInitialSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.isInitialSyncRun = isInitialSync;
        this.mLog = Logger.getLogger(CustomerSyncTask.class);
    }

    public CustomerSyncTask(Context context, boolean isForcedSync, boolean isInitialSync, String cusCode) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.isInitialSyncRun = isInitialSync;
        this.mLog = Logger.getLogger(CustomerSyncTask.class);
        this.customerCode = cusCode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //set parameter for request
        apiCustomerListParameter = new ApiCustomerListParameter();
        apiCustomerListParameter.setFilterCustomerCode(this.customerCode);
        apiCustomerListParameter.setFilterCustomerName("");
        apiCustomerListParameter.setUserCompany(mApp.getmUserCompany());
        apiCustomerListParameter.setUserName(mApp.getCurrentUserName());
        apiCustomerListParameter.setPassword(mApp.getCurrentUserPassword());
        apiCustomerListParameter.setFilterDriverCode(mApp.getmCurrentDriverCode());
        // 2018-02-23 - as nav. change driver in some blanket orders.
        // api will filter Gen_Bus_Posting_group to "SUPER"
        apiCustomerListParameter.setFilterSalesPersonCode("");
        String lastModified = getSyncConfiguration().getLastSyncDateTime();
        lastModified = isInitialSyncRun ? lastModified : "";
        apiCustomerListParameter.setFilterLastModifiedDate(lastModified == null ? "" : "");
        logParams(apiCustomerListParameter);

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeDownloadCustomer));

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (isNetworkAvailable()) {
                Call<ApiCustomerListResult> call = mApp.getNavBrokerService()
                        .GetCustomers(apiCustomerListParameter);
                apiCustomerListResult = call.execute().body();
                addRecords(true, false);
                addNonAssignedCustomers();
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

                if (apiCustomerListResult.getCustomerListResultResultData().size() > 0) {

                    syncStatus.setMessage(String.format(
                            "%.2f"
                            , apiCustomerListResult.getCustomerListResultResultData().get(0).getBalance_LCY())
                    );
                }

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
        mLog.info("SYNC_CUSTOMER_RESULT :" + json);
    }

    private void addRecords(final boolean success, final boolean isNeedDeleteAllCusData) {

        if (success) {

            try {
                customerList = apiCustomerListResult.CustomerListResultData;

                //set current sync status.
                syncConfig.setLastSyncDateTime(apiCustomerListResult.getServerDate());
                syncConfig.setDataCount(customerList.size());
                syncConfig.setSuccess(true);

                //delete if customer exist and insert newly downloaded customer.
                CustomerDbHandler dbAdapter = new CustomerDbHandler(context);
                dbAdapter.open();
                if (isNeedDeleteAllCusData) {
                    dbAdapter.deleteAllCustomer();
                }
                for (ApiCustomerListResult.ApiCustomerResultResponse cus : customerList) {
                    if (dbAdapter.deleteCustomer(cus.getNo())) {

                    Customer customer = new Customer();
                    customer.setKey((cus.getKey()));
                    customer.setName(cus.getName());
                    customer.setCode(cus.getNo());
                    customer.setAddress(cus.getAddress());
                    customer.setPostalCode(cus.getPost_Code());
                    customer.setPhoneNo(cus.getPhone_No());
                    customer.setContact(cus.getContact());
                    customer.setDriverCode(cus.getDriver_Code());
                    customer.setSalespersonCode(cus.getSalesperson_Code());
                    customer.setMinimumSalesAmount(cus.getMinimum_Sales_Amount_LCY());
                    customer.setBalance(cus.getBalance());
                    customer.setCreditLimit(cus.getCredit_Limit_LCY());
                    customer.setDueDateGracePeriod(cus.getDue_Date_Grace_Period());
                    customer.setPaymentTerms(cus.getPayment_Terms_Code());
                    customer.setBlocked(cus.getBlocked() > 0 ? true : false);
                    customer.setEmail(cus.getE_Mail());
                    customer.setCustomerPriceGroup(cus.getCustomer_Price_Group());
                    customer.setVatBusPostingGroup(cus.getVAT_Bus_Posting_Group());
                    customer.setBillToCustomerNo(cus.getBill_to_Customer_No());
                    customer.setBillToCustomerName(cus.getBill_to_Customer_Name());
                    customer.setCustomerReferenceNo(cus.getCustomer_Reference_No());
                    customer.setNtucBuyerCode(cus.getNTUC_Store_Code());

                    if (mApp.getmCurrentModule().equals(context.getResources().getString(R.string.module_mvs))) {
                        if (!customer.isBlocked()) {
                            dbAdapter.addCustomer(customer);
                            Log.d("SYNC_CUSTOMER_ADDED: ", cus.getName());
                        }
                    } else {
                        dbAdapter.addCustomer(customer);
                        Log.d("SYNC_CUSTOMER_ADDED: ", cus.getName());
                    }


                    }
                }

                dbAdapter.close();
            } catch (Exception ex) {
                syncConfig.setSuccess(false);
            }
        } else {
            syncConfig.setSuccess(false);
            Log.d("SYNC_CUSTOMER: ", "Error");
        }

        //Log sync information to Sync Configuration table.
        setSyncConfiguration(syncConfig);
    }

    private void addNonAssignedCustomers() {


        try {

            ApiMobileSalesOrderHeaderParameter apiSalesOrderHeaderParameter = new ApiMobileSalesOrderHeaderParameter();
            apiSalesOrderHeaderParameter.setFilterDriverCode(mApp.getmCurrentDriverCode());
            apiSalesOrderHeaderParameter.setUserName(mApp.getCurrentUserName());
            apiSalesOrderHeaderParameter.setPassword(mApp.getCurrentUserPassword());
            apiSalesOrderHeaderParameter.setUserCompany(mApp.getmUserCompany());
            apiSalesOrderHeaderParameter.setFilterSONumber("");
            Call<ApiMobileSalesOrderHeaderResponse> call = mApp.getNavBrokerService().GetMvsSalesOrders
                    (apiSalesOrderHeaderParameter);
            ApiMobileSalesOrderHeaderResponse apiSalesOrderResult = call.execute().body();


            apiCustomerListParameter.setFilterLastModifiedDate("");
            Call<ApiCustomerListResult> callAllCus = mApp.getNavBrokerService()
                    .GetCustomers(apiCustomerListParameter);
            apiCustomerListResult = callAllCus.execute().body();


            if (apiSalesOrderResult != null && apiSalesOrderResult.getMobileSalesOrderHeaderResultData().size() > 0) {

                String cusNos = "";



                   /* boolean x= apiCustomerListResult.getCustomerListResultResultData().stream().anyMatch(
                          o -> o.getNo().equals(cus.getNo()));*/
                for (ApiMobileSalesOrderHeaderResponse.ApiMobileSalesOrder mso :
                        apiSalesOrderResult.getMobileSalesOrderHeaderResultData()) {
                    boolean isExist = false;
                    for (ApiCustomerListResult.ApiCustomerResultResponse cus :
                            apiCustomerListResult.getCustomerListResultResultData()) {

                        if (mso.getCustomer_Number().equals(cus.getNo())) {
                            isExist = true;
                        }
                    }

                    if (!isExist) {
                        cusNos = cusNos + mso.getCustomer_Number() + "|";
                    }
                }


                if (!cusNos.equals("")) {
                    /*StringBuffer buffer = new StringBuffer(cusNos);
                    cusNos = buffer.reverse().toString().replaceFirst("|","");
                    cusNos = new StringBuffer(cusNos).reverse().toString();*/

                    cusNos = cusNos.substring(0, cusNos.length() - 1);

                    apiCustomerListParameter.setFilterDriverCode("");
                    apiCustomerListParameter.setFilterCustomerCode(cusNos);
                    Call<ApiCustomerListResult> callCus = mApp.getNavBrokerService()
                            .GetCustomers(apiCustomerListParameter);
                    apiCustomerListResult = callCus.execute().body();
                    addRecords(true, false);
                }
            }


        } catch (Exception ex) {

        }

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void logParams(ApiCustomerListParameter params) {

        params.setPassword("*****");
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params);
        mLog.info("SYNC_CUSTOMER_PARAMS :" + json);
        params.setPassword(mApp.getCurrentUserPassword());


    }

}

