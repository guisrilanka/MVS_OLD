package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.datamodel.UserSetup;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.CustomerSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.GSTPostingSetupSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemBalancePdaSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemCategorySyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemUomSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.PaymentUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderDownloadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderImageUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesPricesSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.UserSetupRunningNoUploadTask;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LdsHomeActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse {

    NavClientApp mApp;

    FrameLayout btnSalesInvoice, btnLdsPaymentCollection, btnLdsCustomerInfo, btnLdsStockInfo,
            btnPrint, btnReport2;
    Toolbar myToolbar;
    Button mBtnSignOut;
    Button mBtnSync;
    Drawable mBtnSyncDrawableGreen, mBtnSyncDrawableGray, mBtnSignOutDrawable;

    CustomerSyncTask customerSyncTask;
    ItemSyncTask itemSyncTask;
    ItemCategorySyncTask itemCategorySyncTask;
    ItemUomSyncTask itemUomSyncTask;
    SalesPricesSyncTask salesPricesSyncTask;
    SalesOrderDownloadSyncTask salesOrderDownloadSyncTask;
    SalesOrderUploadSyncTask salesOrderUploadSyncTask;
    ItemBalancePdaSyncTask itemBalancePdaSyncTask;
    GSTPostingSetupSyncTask gSTPostingSetupSyncTask;
    UserSetupRunningNoUploadTask userSetupRunningNoUploadTask;
    PaymentUploadSyncTask paymentUploadSyncTask;
    SalesOrderImageUploadSyncTask salesOrderImageUploadSyncTask;

    String logSyncStatus;
    AlertDialog.Builder mAlertDialogBuilder;
    AlertDialog mAlertDialog;
    ProgressDialog mProgressDialog;
    private String mFilterDeliveryDate = "";
    Calendar calander;
    SimpleDateFormat simpledateformat;
    Logger mLog;

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lds_home);

        //tool bar
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Sales Order List");

        mApp = (NavClientApp) getApplication();
        logSyncStatus = "";
        this.mLog= Log4jHelper.getLogger();

        mProgressDialog = new ProgressDialog(LdsHomeActivity.this);
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

        mBtnSignOut = (Button) findViewById(R.id.btnSignOut);
        mBtnSync = (Button) findViewById(R.id.btnSync);
        mBtnSignOut.setBackgroundDrawable(mBtnSignOutDrawable);
        mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGreen);

        mBtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                // set the new task and clear flags
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        mBtnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    new AlertDialog.Builder(LdsHomeActivity.this)
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
                    new androidx.appcompat.app.AlertDialog.Builder(LdsHomeActivity.this)
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
        });

        btnSalesInvoice = (FrameLayout) findViewById(R.id.btnsalesinvoice);
        btnLdsPaymentCollection = (FrameLayout) findViewById(R.id.btnldspaymentcollection);
        btnLdsCustomerInfo = (FrameLayout) findViewById(R.id.btnldscustomerinfo);
        btnLdsStockInfo = (FrameLayout) findViewById(R.id.btnldsstockinfo);
        btnPrint = (FrameLayout) findViewById(R.id.btnPrint);
        btnReport2 = (FrameLayout) findViewById(R.id.btnReport2);

        btnSalesInvoice.setOnClickListener(this);
        btnLdsPaymentCollection.setOnClickListener(this);
        btnLdsCustomerInfo.setOnClickListener(this);
        btnLdsStockInfo.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        btnReport2.setOnClickListener(this);

        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("yyyy-MM-dd");


        mFilterDeliveryDate=simpledateformat.format(calander.getTime());
    }

    @Override
    public void onClick(View v) {
        OnButtonClick(v);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

        //end customer sync
        if (syncStatus.getScope() == getResources().getString(R.string.SyncScopeDownloadCustomer)) {

            if (syncStatus.isStatus()) {
                logSyncStatus = String.format("%-10s %s", "Customers", "(Done)");
            } else {
                logSyncStatus = String.format("%-22s %s", "Customers", "Failed");
            }
            startItemDownload();
        }

        //end item sync
        else if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeDownLoadItem)) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Items", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-30s %s", "Items", "Failed");
            }
            startItemCategoryDownload();
        }

        //end item category sync
        else if (syncStatus.getScope() == getResources().getString(R.string.SyncScopeDownLoadItemCategory)) {
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
        else if (syncStatus.getScope() == getResources().getString(R.string.SyncScopeDownLoadItemUom)) {
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
        else if (syncStatus.getScope() == getResources().getString(R.string.SyncScopeDownLoadGst)) {
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
        else if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeDownLoadSalesPrice)) {
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
        else if (syncStatus.getScope() == getResources().getString(R.string.SyncScopeDownLoadSO)) {
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
        else if (syncStatus.getScope() == getResources().getString(R.string.SyncScopeUploadSO)) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Upload Sales Orders", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Upload Sales Orders", "Failed");
            }

            startItemBalanceDownload();
        }

        //end Item Balance
        else if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeDownItemBalance)) {
            /*if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Item Balances", "(Done)");
                updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Item Balances", "(Failed)");
            }*/

            startUploadRunningNos();
        }

        //end Running No Upload
        else if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeUploadRunningNos)) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Running No. Upload", "(Done)");
                updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Running No. Upload", "(Failed)");
            }

            startUploadPayments();
        }
        
        //end of payment Upload
        else if (syncStatus.getScope() == getResources().getString(R.string.SyncScopeUploadPayment)) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Upload Payments", "(Done)");
                updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Upload Payments", "(Failed)");
            }

            startSalesOrderImagesUpload();
        }

        //end SO Image Upload
        else if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeUploadSOImage)) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Upload Sales Order Images", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Upload Sales Order Images", "Failed");
            }

            mAlertDialogBuilder = new AlertDialog.Builder(LdsHomeActivity.this);
            mAlertDialogBuilder
                    .setTitle("Sync Completed")
                    .setMessage(logSyncStatus)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                            mLog.info("SYNC_STATUS : " + logSyncStatus);
                        }
                    });
            mAlertDialog = mAlertDialogBuilder.create();
            mProgressDialog.dismiss();
            mAlertDialog.show();
            mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGreen);

            /*try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String currentDBPath = "//data//"+getPackageName()+"//databases//"+"thongsiek"+"";
                    String backupDBPath = "backup.db";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    //Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            }*/

        }
    }

    private void OnButtonClick(View v) {
        if (findViewById(R.id.btnsalesinvoice) == v) {
            Intent intent = new Intent(this, LdsSalesInvoiceListActivity.class);
            startActivity(intent);
        } else if (findViewById(R.id.btnldspaymentcollection) == v) {
            Intent intent = new Intent(this, MsoPaymentListActivity.class);
            intent.putExtra("formName", "LdsHome");
            startActivity(intent);
        } else if (findViewById(R.id.btnldscustomerinfo) == v) {
            Intent intent = new Intent(this, SalesCustomerListActivity.class);
            intent.putExtra("formName", "LdsHome");
            intent.putExtra("IsPopupNeeded", false);
            startActivity(intent);
        } else if (findViewById(R.id.btnldsstockinfo) == v) {
            Intent intent = new Intent(this, SalesItemSearchActivity.class);
            intent.putExtra("formName", "LdsHome");
            startActivity(intent);
        } else if (findViewById(R.id.btnPrint) == v) {
            showSearchDialog();
        } else if (findViewById(R.id.btnReport2) == v) {
        }
    }

    private void showSearchDialog() {
        FrameLayout btnCalender;
        ImageButton btnClose;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow()
                .setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_print);

        final TextView editFilterDeliveryDate = (TextView) dialog.findViewById(R.id.txtDeliveryDate);

        btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);

        Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
        String[] items = new String[]{"Sales Invoice Summary", "Payment Summary"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Button btnEmail = (Button) dialog.findViewById(R.id.btnemail);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button btnprint = (Button) dialog.findViewById(R.id.btnprint);
        btnprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnCalender = (FrameLayout) dialog.findViewById(R.id.btncalender);

        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickDeliveryDate(dialog);
            }
        });

        editFilterDeliveryDate.setText(mFilterDeliveryDate);

        dialog.show();
    }

    private void OnClickDeliveryDate(Dialog dialog) {
        Calendar cl, clNow;
        int nowYear, nowMonth, nowDay;
        int date, month, year;
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

    private void startCustomerDownload() {
        mProgressDialog.setMessage("Downloading Customers...");
        customerSyncTask = new CustomerSyncTask(getApplicationContext(), true, isInitialSyncRun());
        customerSyncTask.delegate = LdsHomeActivity.this;
        customerSyncTask.execute((Void) null);
    }

    private void startItemDownload() {
        itemSyncTask = new ItemSyncTask(getApplicationContext(), true);
        itemSyncTask.delegate = LdsHomeActivity.this;
        itemSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Items...");
    }

    private void startItemCategoryDownload() {
        itemCategorySyncTask = new ItemCategorySyncTask(getApplicationContext(), true);
        itemCategorySyncTask.delegate = LdsHomeActivity.this;
        itemCategorySyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Item Categories...");
    }

    private void startItemUomDownload() {
        itemUomSyncTask = new ItemUomSyncTask(getApplicationContext(), true);
        itemUomSyncTask.delegate = LdsHomeActivity.this;
        itemUomSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Item UOMs...");
    }

    private void startGstPercentageDownload() {

        gSTPostingSetupSyncTask = new GSTPostingSetupSyncTask(getApplicationContext(), true);
        gSTPostingSetupSyncTask.delegate = LdsHomeActivity.this;
        gSTPostingSetupSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading GST tables...");
    }

    private void startSalesPriceDownload() {
        salesPricesSyncTask = new SalesPricesSyncTask(getApplicationContext(), true,isInitialSyncRun());
        salesPricesSyncTask.delegate = LdsHomeActivity.this;
        salesPricesSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Sale Prices...");
    }

    private void startSalesOrdersDownload() {
        salesOrderDownloadSyncTask = new SalesOrderDownloadSyncTask(getApplicationContext(), true, isInitialSyncRun());
        salesOrderDownloadSyncTask.delegate = LdsHomeActivity.this;
        salesOrderDownloadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Sales Orders...");
    }

    private void startSalesOrdersUpload() {
        salesOrderUploadSyncTask = new SalesOrderUploadSyncTask(getApplicationContext(), true);
        salesOrderUploadSyncTask.delegate = LdsHomeActivity.this;
        salesOrderUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Sales Orders...");
    }

    private void startItemBalanceDownload() {
        itemBalancePdaSyncTask = new ItemBalancePdaSyncTask(getApplicationContext(), true);
        itemBalancePdaSyncTask.delegate = LdsHomeActivity.this;
        itemBalancePdaSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Item Balances...");
    }

    private void startUploadPayments() {
        paymentUploadSyncTask = new PaymentUploadSyncTask(getApplicationContext(), true);
        paymentUploadSyncTask.delegate = LdsHomeActivity.this;
        paymentUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Payments...");
    }

    private void startUploadRunningNos() {
        userSetupRunningNoUploadTask = new UserSetupRunningNoUploadTask(getApplicationContext(), true);
        userSetupRunningNoUploadTask.delegate = LdsHomeActivity.this;
        userSetupRunningNoUploadTask.execute((Void) null);
        mProgressDialog.setMessage("Updating Running Numbers...");
    }
    //added by buddhika
    private void startSalesOrderImagesUpload() {
        salesOrderImageUploadSyncTask = new SalesOrderImageUploadSyncTask(getApplicationContext(), true);
        salesOrderImageUploadSyncTask.delegate = LdsHomeActivity.this;
        salesOrderImageUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Sales Order Images...");
    }

    private boolean isInitialSyncRun() {

        UserSetupDbHandler db = new UserSetupDbHandler(getApplicationContext());
        db.open();

        UserSetup u = db.getUserSetUp(mApp.getCurrentUserName());

        db.close();
        return u.isInitialSyncRun();
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
}