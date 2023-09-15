package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;


import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.PdfUploadResponse;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.model.BaseResult;

import org.apache.log4j.Logger;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by GuiUser on 11/16/2017.
 */

public class PdfUploadSyncTask extends AsyncTask<Void, Void, Boolean> {

    Context context;
    private NavClientApp mApp;
    public AsyncResponse delegate = null;
    SyncConfiguration syncConfig;
    private boolean isForcedSync = false;
    Logger mLog;
    PdfUploadResponse pdfResposeBody;
    File pdfFile;
    String fileName,mReportName="";


    public PdfUploadSyncTask(Context context, boolean isForcedSync,String fileNmae) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.fileName=fileNmae;
        this.mLog = Logger.getLogger(PdfUploadSyncTask.class);
    }

    public PdfUploadSyncTask(Context context, boolean isForcedSync,String fileNmae, String reportName_) {
        this.context = context;
        this.isForcedSync = isForcedSync;
        this.mApp = (NavClientApp) context;
        this.fileName=fileNmae;
        this.mLog = Logger.getLogger(PdfUploadSyncTask.class);
        this.mReportName = reportName_;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //set parameters for request
        getFiles();

        // get previous sync details
        syncConfig = new SyncConfiguration();
        syncConfig.setLastSyncBy(mApp.getCurrentUserName());
        syncConfig.setScope(context.getResources().getString(R.string.SyncScopePdf));

    }
    public File getFiles()
    {
        ContextWrapper cw = new ContextWrapper(mApp);
        if(mReportName.equals("InvoiceSummaryReport"))
        {
            pdfFile = new File(cw.getExternalFilesDir("MyInvoiceSummaryReports"), fileName);
        }
        else {
            pdfFile = new File(cw.getExternalFilesDir("MyStockBalanceReports"), fileName);
        }


        if(pdfFile.canRead())
        {
            return pdfFile;
        }
        else {
            Log.v("Failed", "File can not found!");
            return null;
        }

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            if (isNetworkAvailable()) {


                RequestBody requestFile = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("variable", "printPdf",
                                RequestBody.create(MediaType.parse("pdf"), pdfFile))
                        .addFormDataPart("key", "")
                        .build();

                logParams(requestFile);

                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("pdf", pdfFile.getName(), requestFile);

                Call<PdfUploadResponse> call = mApp.getNavBrokerService().UploadPDF(body);

                pdfResposeBody = call.execute().body();
                logResponse(pdfResposeBody);
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

        if (success) {
            if (isForcedSync) {
                if (pdfResposeBody!=null && pdfResposeBody.getStatus()== BaseResult.BaseResultStatusOk) {
                    syncStatus.setStatus(true);
                    delegate.onAsyncTaskFinished(syncStatus);
                } else {
                    syncStatus.setStatus(false);
                    syncStatus.setMessage(pdfResposeBody.getMessage());
                    delegate.onAsyncTaskFinished(syncStatus);
                }
            }
        }
        else {
            syncStatus.setStatus(false);
            delegate.onAsyncTaskFinished(syncStatus);
        }
    }

    @Override
    protected void onCancelled() {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void logResponse(PdfUploadResponse respose) {

        Gson gson = new Gson();
        String json = gson.toJson(respose);
        mLog.info("SYNC_PDF_UPLOAD_RESPONSE :" + json);

    }

    private void logParams(RequestBody params) {

        Gson gson = new Gson();
        String json = gson.toJson(params);
        mLog.info("SYNC_PDF_UPLOAD_PARAMS :" + json);

    }

}