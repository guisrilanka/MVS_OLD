package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostCashReceiptJournalParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostCashReceiptJournalResponse;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentLine;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.PaymentLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SyncConfigurationDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by nelin_000 on 09/15/2017.
 */

public class PaymentUploadSyncTask extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    NavClientApp mApp;
    boolean isForcedSync = false;
    Context context;
    SyncConfiguration syncConfig;
    Logger mLog;
    String mLocationName;
    List<ApiPostCashReceiptJournalParameter> apiPostCashReceiptJournalParameterList;
    ApiPostCashReceiptJournalResponse apiPostCashReceiptJournalResponse;
    List<PaymentLine> paymentsToBeSync;

    public PaymentUploadSyncTask(Context context, boolean isForcedSync) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.mLog = Log4jHelper.getLogger();
        mLocationName = PaymentUploadSyncTask.class.getSimpleName();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopeUploadPayment));
        syncConfig.setLastSyncDateTime(DateTime.now().toString());

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        PaymentLineDbHandler dbAdapter = new PaymentLineDbHandler(context);
        dbAdapter.open();
        boolean isSuccess;
        try {
            paymentsToBeSync = dbAdapter.getPaymentsForUploading();
            dbAdapter.close();

            ////
            Gson gson = new Gson();
            String json = gson.toJson(paymentsToBeSync);
            mLog.info("SYNC_PAYMENT_UP_PARAMS  " +json);
            /////////

            if (paymentsToBeSync.size() > 0 && isNetworkAvailable()) {
                uploadConfirmedPayments();
                isSuccess = saveUploadedPaymentHeaders();

                if (isSuccess) {
                    syncConfig.setSuccess(true);
                } else {
                    syncConfig.setSuccess(false);
                }


            } else {
                return true;
            }

            syncConfig.setDataCount(paymentsToBeSync.size());
            setSyncConfiguration(syncConfig);

        } catch (Exception e) {
            Log.d("NAV_Client_Exception", e.toString());
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
            //Log.d("SYNC_PAYMENT_UP_RESULT ", json);
            mLog.info("SYNC_PAYMENT_UP_RESULT :" + json);

        } catch (Exception ex) {
            //
        }
        syncDbHandler.close();
    }

    private void uploadConfirmedPayments() {

        apiPostCashReceiptJournalParameterList = new ArrayList<ApiPostCashReceiptJournalParameter>();

        try {

            for (PaymentLine confirmedPayment : paymentsToBeSync) {

                ApiPostCashReceiptJournalParameter param = new
                        ApiPostCashReceiptJournalParameter();

                //String docNo = confirmedPayment.getPaymentNo().substring(7);
                //String lineNo = confirmedPayment.getPaymentType();

                param.setAccount_No(confirmedPayment.getLineNo().toString());
                param.setPosting_Date(confirmedPayment.getChequeDate());
                param.setDocument_Date(confirmedPayment.getChequeDate());
                param.setLine_No(Integer.valueOf(confirmedPayment.getLineNo()).toString()); // this should insert from nav.
                param.setDocument_Type("Payment");
                param.setDocument_No(confirmedPayment.getPaymentNo());

                //UAT-2017-11-22
                if(confirmedPayment.getPaymentType()=="0"){ //for cash.
                    param.setExternal_Document_No("");
                }
                else {
                    param.setExternal_Document_No(confirmedPayment.getChequeNo());
                }

                //UAT-2017-11-22
                if(confirmedPayment.getPaymentType()=="0"){ //for cash.
                    param.setDescription(mApp.getmUserCompany());
                }
                else {
                    if(confirmedPayment.getChequeName().equals("")){
                        param.setDescription(mApp.getmUserCompany());
                    }
                    else {
                        param.setDescription((confirmedPayment.getChequeName()));
                    }
                }

                param.setAccount_Type("Customer");

                //account no should set to customer_code when customer's  bill_to_customer_code is empty.
                if(confirmedPayment.getBillToCustomerNo()==null || confirmedPayment.getBillToCustomerNo().equals("")){
                    param.setAccount_No(confirmedPayment.getCustomerNo());
                }
                else {
                    param.setAccount_No(confirmedPayment.getBillToCustomerNo());
                }

                param.setSalespers_Purch_Code("");
                param.setCampaign_No("");
                param.setAmount(confirmedPayment.getAmount());
                param.setApplies_to_Doc_No(confirmedPayment.getExternalDocumentNo());
                param.setGen_Posting_Type("");
                param.setBal_Account_Type(""); // set to bank account from API
                param.setBal_Account_No(confirmedPayment.getPaymentType()); // API will set, 0 to'MSD_CASH, 1 to
                                                                            // 'MSD_CHEQUE'
                param.setCurrency_Code("");
                param.setUserName(mApp.getCurrentUserName());
                param.setPassword(mApp.getCurrentUserPassword());
                param.setUserCompany(mApp.getmUserCompany());

                apiPostCashReceiptJournalParameterList.add(param);
            }

            logParams(apiPostCashReceiptJournalParameterList);

            Call<ApiPostCashReceiptJournalResponse> call = mApp.getNavBrokerService()
                    .PostCashReceiptJournal(apiPostCashReceiptJournalParameterList);

            apiPostCashReceiptJournalResponse = call.execute().body();

            logResponse(apiPostCashReceiptJournalResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private boolean saveUploadedPaymentHeaders() {

        PaymentLineDbHandler dbAdapter = new PaymentLineDbHandler(context);
        dbAdapter.open();


        if (apiPostCashReceiptJournalResponse != null && apiPostCashReceiptJournalResponse.getTrStatusList() != null) {
            for (ApiPostCashReceiptJournalResponse.PaymentTransactionStatusResponse
                    tr : apiPostCashReceiptJournalResponse.getTrStatusList()) {

                if (tr.isTransferred()) {
                    dbAdapter.saveConfirmPayments(
                            tr.getDocumentNo(),
                            apiPostCashReceiptJournalResponse.getServerDate(),
                            tr.getOriginalLineNo(),
                            tr.getNewLineNo(),tr.isTransferred());
                } else {

                }
            }
        }

        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void logParams(List<ApiPostCashReceiptJournalParameter> params){

        for (ApiPostCashReceiptJournalParameter p:params) {
            p.setPassword("****");
        }

        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Gson gson = new Gson();
        String json = gson.toJson(params);
        //Log.d("SYNC_PAYMENT_UP_PARAMS", json);
        mLog.info(mLocationName +":-"+"SYNC_PAYMENT_UP_PARAMS :" + json);

        for (ApiPostCashReceiptJournalParameter p:params) {
            p.setPassword(mApp.getCurrentUserPassword());
        }


    }

    private void logResponse(ApiPostCashReceiptJournalResponse response){
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(response);
        //Log.d("SYNC_PAYMENT_UP_RES", json);
        mLog.info(mLocationName +":-"+"SYNC_PAYMENT_UP_RES :" + json);
    }


}

