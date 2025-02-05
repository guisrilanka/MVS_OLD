package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.seanzor.prefhelper.SharedPrefHelper;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.datamodel.UserSetup;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockRequestDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.CustomerSalesCodeSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.CustomerSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.CustomerTemplateSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.GSTPostingSetupSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemBalanceVehicleSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemCategorySyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemCrossReferenceSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemUomSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesInvoiceUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderClearAndDownloadForMvsSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderImageUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesPricesSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.StockRequestDownloadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.StockRequestUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.UserSetupRunningNoUploadTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MvsHomeActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse {

    NavClientApp mApp;

    FrameLayout mBtnStockRequest, mBtnSalesOrderInvoice, mBtnStockTranferRequestIn, mBtnMvsCustomerInfo,
            mBtnMvsStockInfo, mBtnPrint, mBtnStockTranferRequestOut;
    Toolbar myToolbar;
    Button mBtnSignOut;
    Button mBtnSync;
    Drawable mBtnSyncDrawableGreen, mBtnSyncDrawableGray, mBtnSignOutDrawable;

    UserSetup userSetup;
    ProgressDialog mProgressDialog;
    AlertDialog.Builder mAlertDialogBuilder;
    AlertDialog mAlertDialog;
    CustomerSyncTask customerSyncTask;
    ItemSyncTask itemSyncTask;
    ItemCrossReferenceSyncTask itemCrossRefSyncTask;
    ItemCategorySyncTask itemCategorySyncTask;
    ItemUomSyncTask itemUomSyncTask;
    SalesPricesSyncTask salesPricesSyncTask;
    SalesOrderClearAndDownloadForMvsSyncTask salesOrderDownloadSyncTask;
    SalesInvoiceUploadSyncTask salesInvoiceUploadSyncTask;
    //ItemBalancePdaSyncTask itemBalancePdaSyncTask;
    ItemBalanceVehicleSyncTask itemBalanceVehicleSyncTask;
    GSTPostingSetupSyncTask gSTPostingSetupSyncTask;
    UserSetupRunningNoUploadTask userSetupRunningNoUploadTask;
    StockRequestDownloadSyncTask stockRequestDownloadSyncTask;
    StockRequestUploadSyncTask stockRequestUploadSyncTask;
    CustomerSalesCodeSyncTask customerSalesCodeSyncTask;
    CustomerTemplateSyncTask customerTemplateSyncTask;
    SalesOrderImageUploadSyncTask salesOrderImageUploadSyncTask;

    String logSyncStatus;
    int soPendingCount = 0, srPendingCount = 0, mVehicleQtyItemCount = 0;
    TextView mTxtSrPendingCount, mTxtSoPendingCount, mTxtVclQtyItemCount;
    String mFilterCreatedDate = "";
    String mSelectedPrintOption = "";

    GetPendingCountTask mGetPendingCountTask;

    SharedPrefHelper mPrefHelper;
    SharedPreferences mDefaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvs_home);

        mApp = (NavClientApp) getApplication();
        logSyncStatus = "";


        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefHelper = new SharedPrefHelper(getResources(), mDefaultSharedPreferences);
        mProgressDialog = new ProgressDialog(MvsHomeActivity.this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle("Sync Data");

        mBtnSyncDrawableGreen = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_refresh)
                .color(Color.GREEN)
                .sizeDp(30);
        mBtnSyncDrawableGray = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_refresh)
                .color(Color.GRAY)
                .sizeDp(30);
        mBtnSignOutDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_sign_out)
                .color(Color.WHITE)
                .sizeDp(30);

        //tool bar
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mBtnSignOut = (Button) findViewById(R.id.btnSignOut);
        mBtnSignOut.setBackgroundDrawable(mBtnSignOutDrawable);
        mBtnSync = (Button) findViewById(R.id.btnSync);
        mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGreen);

        /*//UserSetup
        userSetup = new UserSetup();
        userSetup.setEnableCustomer(true);
        userSetup.setEnableItems(false);
        userSetup.setEnableTransferRequestIn(true);
        userSetup.setEnableTransferRequestOut(true);*/

        mTxtSrPendingCount = (TextView) findViewById(R.id.txtSrPendingCount);
        mTxtSoPendingCount = (TextView) findViewById(R.id.txtSoPendingCount);
        mTxtVclQtyItemCount = (TextView) findViewById(R.id.txtVclQtyItemCount);

        mBtnStockRequest = (FrameLayout) findViewById(R.id.btnstockreqest);
        mBtnSalesOrderInvoice = (FrameLayout) findViewById(R.id.btnsalesorderinvoice);
        mBtnMvsCustomerInfo = (FrameLayout) findViewById(R.id.btnmvscustomerinfo);
        mBtnMvsStockInfo = (FrameLayout) findViewById(R.id.btnmvsstockinfo);
        mBtnPrint = (FrameLayout) findViewById(R.id.btnPrintInvoiceSummary);
        mBtnStockTranferRequestIn = (FrameLayout) findViewById(R.id.btnstocktranferrequestin);
        mBtnStockTranferRequestOut = (FrameLayout) findViewById(R.id.btnstocktranferrequestout);

        mBtnStockTranferRequestIn.setOnClickListener(this);
        mBtnStockTranferRequestOut.setOnClickListener(this);
        mBtnStockRequest.setOnClickListener(this);
        mBtnSalesOrderInvoice.setOnClickListener(this);
        mBtnMvsCustomerInfo.setOnClickListener(this);
        mBtnMvsStockInfo.setOnClickListener(this);
        mBtnPrint.setOnClickListener(this);
        mBtnSignOut.setOnClickListener(this);
        mBtnSync.setOnClickListener(this);

        setPendingCount();


//        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(this);
//        dbAdapter.open();
//        SalesOrderLineDbHandler dbLineAdapter = new SalesOrderLineDbHandler(this);
//        dbLineAdapter.open();

//        try {
//
//            Date dt = new Date();
//            Calendar c = Calendar.getInstance();
//            c.setTime(dt);
//            c.add(Calendar.MONTH, -2);
//            dt = c.getTime();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            List<String> soNoList = dbAdapter.getAllSalesOrderNoByDate(dateFormat.format(dt));
//            for (String no : soNoList) {
//                dbLineAdapter.deleteSOLineByDocumentNo(no);
//            }
//
//            dbAdapter.deleteSalesOrderByDate(dateFormat.format(dt));
//
//            StockRequestDbHandler dbAdapter1 = new StockRequestDbHandler(this);
//            dbAdapter1.open();
//            StockRequestLineDbHandler dbLineAdapter2 = new StockRequestLineDbHandler(this);
//            dbLineAdapter2.open();
//
//            List<String> srNoList = dbAdapter1.getAllStockRequestNoByDate(dateFormat.format(dt));
//            for(String no : srNoList){
//                dbLineAdapter2.deleteStockRequestLinesByDocumentNo(no);
//            }
//
//            dbAdapter1.deleteStockRequestByDate(dateFormat.format(dt));
//        } catch (Exception e) {
//            System.out.println(e);
//        }

    }

    public void setPendingCount() {
        mGetPendingCountTask = new GetPendingCountTask();
        mGetPendingCountTask.execute((Void) null);
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

        //end customer sync
        if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeDownloadCustomer))) {

            if (syncStatus.isStatus()) {
                logSyncStatus = String.format("%-10s %s", "Customers", "(Done)");
            } else {
                logSyncStatus = String.format("%-22s %s", "Customers", "Failed");
            }
            startCustomerSalesCodeDownload();
        }

        //end sales code sync
        if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeDownloadCustomerSalesCodes))) {

            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' + String.format("%-10s %s", "Customer Sales Code", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' + String.format("%-22s %s", "Customer Sales Code", "Failed");
            }
            startCustomerTemplateDownload();
        }

        //end Customer template sync
        if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeDownloadCustomerTemplate))) {

            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' + String.format("%-10s %s", "Customer Template", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' + String.format("%-22s %s", "Customer Template", "Failed");
            }
            startItemDownload();
        }

        //end item sync
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeDownLoadItem))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Items", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-30s %s", "Items", "Failed");
            }
            startItemCrossRefDownload();
        }

        //end item cross ref. sync
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeDownLoadItemCrossRef))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Item Cross Ref.", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-30s %s", "Item Cross Ref.", "Failed");
            }
            startItemCategoryDownload();
        }

        //end item category sync
        else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeDownLoadItemCategory))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Item Categories", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-30s %s", "Item Categories", "Failed");
            }
            startItemUomDownload();
        }

        //end Item UOM sync
        else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeDownLoadItemUom))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Item UOMs", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Item UOMs", "Failed");
            }
            startGstPercentageDownload();
        }

        //end GST sync
        else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeDownLoadGst))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "GST tables", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "GST tables", "Failed");
            }
            startSalesPriceDownload();
        }

        //end sales price sync
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeDownLoadSalesPrice))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Sale Prices", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Sale Prices", "Failed");
            }

            startSalesOrdersDownload();
        }

        //end sales order download sync
        else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeDownLoadSO))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Downloading Sales Orders", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Downloading Sales Orders", "Failed");
            }

            startSalesOrdersUpload();
        }

        //end sales order upload sync
        else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeUploadSI))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Upload Sales Invoice", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Upload Sales Invoice", "Failed");
            }

            startItemBalanceDownload();
        }

        //end Item Balance
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeDownItemBalance))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Item Balances", "(Done)");
                updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Item Balances", "(Failed)");
            }

            startStockRequestDownload();
        }

        // //end SR Download
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeDownLoadSR))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Stock Request Download", "(Done)");
                updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Stock Request Download", "(Failed)");
            }

            startStockRequestUpload();
        }
        //Stock request upload ...
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeUploadSR))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Stock Request Upload", "(Done)");
                updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Stock Request Upload", "(Failed)");
            }

            startUploadRunningNos();
        }

        //Running No Upload ...
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeUploadRunningNos))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Running No. Upload", "(Done)");
                updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Running No. Upload", "(Failed)");
            }

            startSalesOrderImagesUpload();
        }

        //end So Image Upload
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeUploadSOImage))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Upload Sales Order Images", "(Done)");
                updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Upload Sales Order Images", "(Failed)");
            }

            mAlertDialogBuilder = new AlertDialog.Builder(MvsHomeActivity.this);
            mAlertDialogBuilder
                    .setTitle("Sync Completed")
                    .setMessage(logSyncStatus)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setPendingCount();
                            dialog.cancel();

                        }
                    });
            mAlertDialog = mAlertDialogBuilder.create();
            mProgressDialog.dismiss();
            mAlertDialog.show();
            mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGreen);
        }
    }

    @Override
    public void onClick(View v) {
        OnButtonClick(v);
    }

    private void OnButtonClick(View v) {

        if (mGetPendingCountTask != null) {
            mGetPendingCountTask.cancel(true);
        }

        if (findViewById(R.id.btnstockreqest) == v) {
            Intent intent = new Intent(this, MvsStockRequestListActivity.class);
            startActivity(intent);
        } else if (findViewById(R.id.btnsalesorderinvoice) == v) {
            Intent intent = new Intent(this, MvsSalesOrderListActivity.class);
            startActivity(intent);
        } else if (findViewById(R.id.btnstocktranferrequestin) == v) {
            Intent intent = new Intent(this, MvsStockTransferRequestListActivity.class);
            intent.putExtra(getResources().getString(R.string.intent_extra_form_name)
                    , getResources().getString(R.string.form_name_mvs_stock_transfer_in));
            startActivity(intent);
            //startActivity(new Intent(this, MvsStockTransferRequestListActivity.class));
        } else if (findViewById(R.id.btnmvscustomerinfo) == v) {
            Intent intent = new Intent(this, SalesCustomerListActivity.class);
            intent.putExtra("IsPopupNeeded", false);
            intent.putExtra("IsFromMvs", true);
            intent.putExtra("IsFromMso", false);
            intent.putExtra("formName", "MvsHome");
            startActivity(intent);
        } else if (findViewById(R.id.btnmvsstockinfo) == v) {
            Intent intent = new Intent(this, SalesItemSearchActivity.class);
            intent.putExtra("formName", "MvsHome");
            startActivity(intent);
        } else if (findViewById(R.id.btnPrintInvoiceSummary) == v) {
            showSearchDialog();
        } else if (findViewById(R.id.btnstocktranferrequestout) == v) {
            Intent intent = new Intent(this, MvsStockTransferRequestListActivity.class);
            intent.putExtra("formName", getResources().getString(R.string.form_name_mvs_stock_transfer_out));
            startActivity(intent);
        } else if (findViewById(R.id.btnSignOut) == v) {

//            Intent intentTakePic = new Intent(this, MsoTakePictureActivity.class);
//            intentTakePic.putExtra("soNo", " 0");
//            intentTakePic.putExtra("status", " 2");
//            this.startActivity(intentTakePic);

            AlertDialog.Builder builder = new AlertDialog.Builder(MvsHomeActivity.this);
            builder.setTitle(getResources().getString(R.string.message_title_alert));
            builder.setMessage(getResources().getString(R.string.message_ask_log_out));
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() { //when click on DELETE
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //cancel all running tasks
                    stopAllTasks();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    // set the new task and clear flags
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }).setNegativeButton(getResources().getString(R.string.button_text_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        } else if (findViewById(R.id.btnSync) == v) {
            if (isNetworkConnected()) {
                new AlertDialog.Builder(this)
                        .setTitle("Data Sync")
                        .setMessage(getResources().getString(R.string
                                .notification_msg_confirmation_force_sync))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                logSyncStatus = "";
                                mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGray);
                                mProgressDialog.setMessage("Initializing...");
                                mProgressDialog.show();
                                startCustomerDownload();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            } else {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string
                                .notification_title_no_connection))
                        .setMessage(getResources().getString(R.string
                                .notification_msg_no_connection))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }
        }
    }

    private void startCustomerDownload() {
        mProgressDialog.setMessage("Downloading Customers...");
        customerSyncTask = new CustomerSyncTask(getApplicationContext(), true, isInitialSyncRun());
        customerSyncTask.delegate = MvsHomeActivity.this;
        customerSyncTask.execute((Void) null);

    }

    private void startCustomerSalesCodeDownload() {

        mProgressDialog.setMessage("Downloading Customer Sales Codes...");
        customerSalesCodeSyncTask = new CustomerSalesCodeSyncTask(getApplicationContext(), true, isInitialSyncRun());
        customerSalesCodeSyncTask.delegate = MvsHomeActivity.this;
        customerSalesCodeSyncTask.execute((Void) null);
    }

    private void startCustomerTemplateDownload() {

        mProgressDialog.setMessage("Downloading Customer Templates...");
        customerTemplateSyncTask = new CustomerTemplateSyncTask(getApplicationContext(), true, isInitialSyncRun());
        customerTemplateSyncTask.delegate = MvsHomeActivity.this;
        customerTemplateSyncTask.execute((Void) null);
    }

    private void startItemDownload() {
        itemSyncTask = new ItemSyncTask(getApplicationContext(), true);
        itemSyncTask.delegate = MvsHomeActivity.this;
        itemSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Items...");
    }

    private void startItemCrossRefDownload() {
        mProgressDialog.setMessage("Downloading Item Cross Ref..");
        itemCrossRefSyncTask = new ItemCrossReferenceSyncTask(getApplicationContext(), true, isInitialSyncRun());
        itemCrossRefSyncTask.delegate = MvsHomeActivity.this;
        itemCrossRefSyncTask.execute((Void) null);

    }

    private void startItemCategoryDownload() {
        itemCategorySyncTask = new ItemCategorySyncTask(getApplicationContext(), true);
        itemCategorySyncTask.delegate = MvsHomeActivity.this;
        itemCategorySyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Item Categories...");
    }

    private void startItemUomDownload() {
        itemUomSyncTask = new ItemUomSyncTask(getApplicationContext(), true);
        itemUomSyncTask.delegate = MvsHomeActivity.this;
        itemUomSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Item UOMs...");
    }

    private void startGstPercentageDownload() {

        gSTPostingSetupSyncTask = new GSTPostingSetupSyncTask(getApplicationContext(), true);
        gSTPostingSetupSyncTask.delegate = MvsHomeActivity.this;
        gSTPostingSetupSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading GST tables...");
    }

    private void startSalesPriceDownload() {
        salesPricesSyncTask = new SalesPricesSyncTask(getApplicationContext(), true, isInitialSyncRun());
        salesPricesSyncTask.delegate = MvsHomeActivity.this;
        salesPricesSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Sale Prices...");
    }

    private void startSalesOrdersDownload() {
        salesOrderDownloadSyncTask = new SalesOrderClearAndDownloadForMvsSyncTask(getApplicationContext(), true, isInitialSyncRun());
        salesOrderDownloadSyncTask.delegate = MvsHomeActivity.this;
        salesOrderDownloadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Sales Orders...");
    }

    private void startSalesOrdersUpload() {
        salesInvoiceUploadSyncTask = new SalesInvoiceUploadSyncTask(getApplicationContext(), true);
        salesInvoiceUploadSyncTask.delegate = MvsHomeActivity.this;
        salesInvoiceUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Sales Orders...");
    }

    private void startItemBalanceDownload() {
        itemBalanceVehicleSyncTask = new ItemBalanceVehicleSyncTask(getApplicationContext(), true);
        itemBalanceVehicleSyncTask.delegate = MvsHomeActivity.this;
        itemBalanceVehicleSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Item Balances...");
    }

    private void updateUserSetupInitialSync() {
        UserSetupDbHandler db = new UserSetupDbHandler(getApplicationContext());
        db.open();

        UserSetup u = db.getUserSetUp(mApp.getCurrentUserName());
        u.setInitialSyncRun(true);
        if (db.deleteUser(mApp.getCurrentUserName())) {
            db.addUserSetup(u);
        }

        db.close();
    }

    private void startUploadRunningNos() {
        userSetupRunningNoUploadTask = new UserSetupRunningNoUploadTask(getApplicationContext(), true);
        userSetupRunningNoUploadTask.delegate = MvsHomeActivity.this;
        userSetupRunningNoUploadTask.execute((Void) null);
        mProgressDialog.setMessage("Updating Running Numbers...");
    }

    private void startStockRequestDownload() {
        stockRequestDownloadSyncTask = new StockRequestDownloadSyncTask(getApplicationContext(), true, isInitialSyncRun());
        stockRequestDownloadSyncTask.delegate = MvsHomeActivity.this;
        stockRequestDownloadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Stock Request...");
    }

    private void startStockRequestUpload() {
        stockRequestUploadSyncTask = new StockRequestUploadSyncTask(getApplicationContext(), true);
        stockRequestUploadSyncTask.delegate = MvsHomeActivity.this;
        stockRequestUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Stock Request...");
    }

    private void startSalesOrderImagesUpload() {
        salesOrderImageUploadSyncTask = new SalesOrderImageUploadSyncTask(getApplicationContext(), true);
        salesOrderImageUploadSyncTask.delegate = MvsHomeActivity.this;
        salesOrderImageUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Sales Order Images...");
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    private boolean isInitialSyncRun() {

        UserSetupDbHandler db = new UserSetupDbHandler(getApplicationContext());
        db.open();

        UserSetup u = db.getUserSetUp(mApp.getCurrentUserName());

        db.close();
        return u.isInitialSyncRun();
    }

    private void showSearchDialog() {
        FrameLayout btnCalender;
        ImageButton btnClose;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        ;
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow()
                .setLayout((int) (getScreenWidth(this) * .9), ViewGroup.LayoutParams.WRAP_CONTENT);

        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_mvs_print);
        btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);

        final Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
        String[] items = new String[]{"Sales Invoice Summary", "Stock Balance Summary","Exchange Item Summery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);


        final TextView editFilterCreatedDate = (TextView) dialog.findViewById(R.id.txtDeliveryDate);
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        try {
            String todayDate = DateTime.now().toString("YYYY-MM-dd");
            editFilterCreatedDate.setText(todayDate);
        } catch (Exception ex) {

        }

        Button btnprint = (Button) dialog.findViewById(R.id.btnprint);
        btnprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterCreatedDate = "";
                try {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = format.parse(editFilterCreatedDate.getText().toString());
                    mFilterCreatedDate = format.format(date);

                    mSelectedPrintOption = dropdown.getSelectedItem().toString();

                    if (mSelectedPrintOption == "Sales Invoice Summary") {
                        Intent intent;
                        String selectedPrinter = mPrefHelper.getString(R.string.pref_select_printer_key);
                        if (selectedPrinter.equals(getResources().getString(R.string.printer_honeywell))) {
                            intent = new Intent(MvsHomeActivity.this, PrintInvoiceSummaryActivity.class);
                        } else {
                            intent
                                    = new Intent(MvsHomeActivity.this, PrintInvoiceSummaryRP4Activity.class);
                        }
//                        intent = new Intent(MvsHomeActivity.this, PrintInvoiceSummaryActivity.class);
                        intent.putExtra("filterCreatedDate", mFilterCreatedDate);
                        startActivity(intent);
                        dialog.dismiss();
                    } else if (mSelectedPrintOption.equals("Stock Balance Summary")) {
                        Intent intent;
                        String selectedPrinter = mPrefHelper.getString(R.string.pref_select_printer_key);
                        if (selectedPrinter.equals(getResources().getString(R.string.printer_honeywell))) {
                            intent
                                    = new Intent(MvsHomeActivity.this, PrintStockBalanceSummaryActivity.class);

                        } else {
                            intent
                                    = new Intent(MvsHomeActivity.this, PrintStockBalanceSummaryRP4Activity.class);
                        }
//                        Intent intent = new Intent(MvsHomeActivity.this, PrintStockBalanceSummaryActivity.class);
                        //intent.putExtra("filterCreatedDate", mFilterCreatedDate);
                        intent.putExtra("details", "");
                        startActivity(intent);
                        dialog.dismiss();
                    }else if (mSelectedPrintOption.equals("Exchange Item Summery")) {

                        Intent intent;
                        String selectedPrinter = mPrefHelper.getString(R.string.pref_select_printer_key);
                        if (selectedPrinter.equals(getResources().getString(R.string.printer_honeywell))) {
                            intent
                                    = new Intent(MvsHomeActivity.this, PrintExchangeItemSummaryActivity.class);
                        } else {
                            intent
                                    = new Intent(MvsHomeActivity.this, PrintExchangeItemSummaryRP4Activity.class);
                        }
                        intent.putExtra("details", "");
                        startActivity(intent);
                        dialog.dismiss();
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

        final Button btnEmail = (Button) dialog.findViewById(R.id.btnemail);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = format.parse(editFilterCreatedDate.getText().toString());
                    mFilterCreatedDate = format.format(date);

                    Intent intent = new Intent(MvsHomeActivity.this
                            , EmailInvoiceSummaryActivity.class);
                    intent.putExtra("filterCreatedDate", mFilterCreatedDate);
                    startActivity(intent);
                    dialog.dismiss();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }

            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = dropdown.getSelectedItem().toString();

                if (selectedOption.equals("Sales Invoice Summary")) {
                    btnEmail.setVisibility(View.INVISIBLE);
                } else {
                    btnEmail.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        btnCalender = (FrameLayout) dialog.findViewById(R.id.btncalender);
        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickDeliveryDate(dialog);
            }
        });

        dialog.show();
    }

    private void OnClickDeliveryDate(Dialog dialog) {
        Calendar cl, clNow;
        int date, month, year;
        int nowYear, nowMonth, nowDay;
        cl = Calendar.getInstance();
        date = cl.get(Calendar.DAY_OF_MONTH);
        month = cl.get(Calendar.MONTH);
        year = cl.get(Calendar.YEAR);
        final TextView txtcalender;

        clNow = Calendar.getInstance();
        nowYear = clNow.get(Calendar.YEAR);
        nowMonth = clNow.get(Calendar.MONTH);
        nowDay = clNow.get(Calendar.DAY_OF_MONTH);
        txtcalender = (TextView) dialog.findViewById(R.id.txtDeliveryDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtcalender.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, date, month, year);

        datePickerDialog.updateDate(nowYear, nowMonth, nowDay);
        datePickerDialog.show();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private class GetPendingCountTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getPendingCounts();
            } catch (Exception e) {
                Log.d(getResources().getString(R.string.message_exception), e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    updatePendingCount();

                } catch (Exception ex) {
                    Log.d(getResources().getString(R.string.message_exception), ex.toString());
                }
            } else {
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    public void getPendingCounts() {
        SalesOrderDbHandler soDb = new SalesOrderDbHandler(getApplicationContext());
        soDb.open();
        soPendingCount = soDb.getMVSSoPendingCount(mApp.getmCurrentDriverCode());
        soDb.close();

        StockRequestDbHandler stDb = new StockRequestDbHandler(getApplicationContext());
        stDb.open();
        srPendingCount = stDb.getSrPendingCount(mApp.getmCurrentDriverCode());
        stDb.close();

        ItemDbHandler iDb = new ItemDbHandler(getApplicationContext());
        iDb.open();
        mVehicleQtyItemCount = iDb.getVehicleQtyItemCount(mApp.getmCurrentDriverCode());
        iDb.close();
    }

    public void updatePendingCount() {
        if (soPendingCount > 0) {
            mTxtSoPendingCount.setVisibility(View.VISIBLE);
            mTxtSoPendingCount.setText(String.valueOf(soPendingCount));
        } else {
            mTxtSoPendingCount.setVisibility(View.GONE);
        }

        if (srPendingCount > 0) {
            mTxtSrPendingCount.setVisibility(View.VISIBLE);
            mTxtSrPendingCount.setText(String.valueOf(srPendingCount));
        } else {
            mTxtSrPendingCount.setVisibility(View.GONE);
        }

        if (mVehicleQtyItemCount > 0) {
            mTxtVclQtyItemCount.setVisibility(View.VISIBLE);
            mTxtVclQtyItemCount.setText(String.valueOf(mVehicleQtyItemCount));
        } else {
            mTxtVclQtyItemCount.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPendingCount();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MvsHomeActivity.this);
        builder.setTitle(getResources().getString(R.string.message_title_alert));
        builder.setMessage(getResources().getString(R.string.message_ask_log_out));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() { //when click on DELETE
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //cancel all running tasks
                stopAllTasks();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                // set the new task and clear flags
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        }).setNegativeButton(getResources().getString(R.string.button_text_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void stopAllTasks() {
        if (customerSyncTask != null) {
            customerSyncTask.cancel(true);
        }
        if (itemSyncTask != null) {
            itemSyncTask.cancel(true);
        }
        if (itemCrossRefSyncTask != null) {
            itemCrossRefSyncTask.cancel(true);
        }
        if (itemCategorySyncTask != null) {
            itemCategorySyncTask.cancel(true);
        }
        if (itemUomSyncTask != null) {
            itemUomSyncTask.cancel(true);
        }
        if (salesPricesSyncTask != null) {
            salesPricesSyncTask.cancel(true);
        }
        if (salesOrderDownloadSyncTask != null) {
            salesOrderDownloadSyncTask.cancel(true);
        }
        if (salesInvoiceUploadSyncTask != null) {
            salesInvoiceUploadSyncTask.cancel(true);
        }
        if (itemBalanceVehicleSyncTask != null) {
            itemBalanceVehicleSyncTask.cancel(true);
        }
        if (gSTPostingSetupSyncTask != null) {
            gSTPostingSetupSyncTask.cancel(true);
        }
        if (userSetupRunningNoUploadTask != null) {
            userSetupRunningNoUploadTask.cancel(true);
        }
        if (stockRequestDownloadSyncTask != null) {
            stockRequestDownloadSyncTask.cancel(true);
        }
        if (stockRequestUploadSyncTask != null) {
            stockRequestUploadSyncTask.cancel(true);
        }
        if (customerSalesCodeSyncTask != null) {
            customerSalesCodeSyncTask.cancel(true);
        }
        if (customerTemplateSyncTask != null) {
            customerTemplateSyncTask.cancel(true);
        }
        if (salesOrderImageUploadSyncTask != null) {
            salesOrderImageUploadSyncTask.cancel(true);
        }
    }

}