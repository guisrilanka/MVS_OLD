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
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
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
import com.gui.mdt.thongsieknavclient.dbhandler.PaymentHeaderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.CustomerSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.GSTPostingSetupSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.InitSync;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemBalancePdaSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemCategorySyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemUomSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.PaymentUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderDownloadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesPricesSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.UserSetupRunningNoUploadTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MsoHomeActivity extends AppCompatActivity implements View.OnClickListener,
        AsyncResponse {

    NavClientApp mApp;
    Logger mLog;
    InitSync mInitSync;

    FrameLayout mBtnSalesOrder, mBtnPaymentCollection, mBtnCustomerInfo, BtnStockInfo, mBtnPrint,
            mBtnCustomerPriceList;
    Toolbar myToolbar;
    Button mBtnSignOut;
    Button mBtnSync;
    Drawable mBtnSyncDrawableGreen, mBtnSyncDrawableGray, mBtnSignOutDrawable;
    AlertDialog.Builder mAlertDialogBuilder;
    AlertDialog mAlertDialog;
    ProgressDialog mProgressDialog;
    TextView mTxtSoPendingCount,mTxtPaymentPendingCount;

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
    String logSyncStatus;
    //boolean isInitialSyncRun;
    boolean isPrintButtonClicked = false;
    Calendar calender;
    SimpleDateFormat simpledateformat;
    int soPendingCount=0, paymentPendingCount=0;
    String mFilterCreatedDate="";

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mso_home);

        mApp = (NavClientApp) getApplication();
        logSyncStatus = "";
        this.mLog = Logger.getLogger(UserSetupRunningNoUploadTask.class);
        //isInitialSyncRun=false;
        mInitSync = new InitSync(getApplicationContext());



        mProgressDialog = new ProgressDialog(MsoHomeActivity.this);
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
        //getSupportActionBar().setTitle("Sales Order List");
        mBtnSignOut = (Button) findViewById(R.id.btnSignOut);

        mBtnSignOut.setBackgroundDrawable(mBtnSignOutDrawable);
        mBtnSync = (Button) findViewById(R.id.btnSync);
        mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGreen);

        mBtnSalesOrder = (FrameLayout) findViewById(R.id.btnSalesOrder);
        mBtnPaymentCollection = (FrameLayout) findViewById(R.id.btnPaymentCollection);
        mBtnCustomerInfo = (FrameLayout) findViewById(R.id.btnCustomerInfo);
        BtnStockInfo = (FrameLayout) findViewById(R.id.btnStockInfo);
        mBtnPrint = (FrameLayout) findViewById(R.id.btnPrint);
        mBtnCustomerPriceList = (FrameLayout) findViewById(R.id.btnCustomerPriceList);

        mTxtSoPendingCount = (TextView) findViewById(R.id.txtSoPendingCount);
        mTxtPaymentPendingCount = (TextView) findViewById(R.id.txtPaymentPendingCount);

        mBtnSalesOrder.setOnClickListener(this);
        mBtnPaymentCollection.setOnClickListener(this);
        mBtnCustomerInfo.setOnClickListener(this);
        BtnStockInfo.setOnClickListener(this);
        mBtnPrint.setOnClickListener(this);
        mBtnSignOut.setOnClickListener(this);
        mBtnSync.setOnClickListener(this);
        mBtnCustomerPriceList.setOnClickListener(this);

        calender = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("yyyy-MM-dd");


        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //action when dialog is dismissed goes here
                if(salesPricesSyncTask!=null){
                    stopAsyncTasks();
                    mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGreen);
                }
            }
        });

        setPendingCount();
    }

    public void setPendingCount()
    {
        GetPendingCountTask pdTask = new GetPendingCountTask();
        pdTask.execute((Void) null);
    }

    @Override
    public void onClick(View v) {
        onButtonClick(v);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MsoHomeActivity.this);
        builder.setTitle(getResources().getString(R.string.message_title_alert));
        builder.setMessage(getResources().getString(R.string.message_ask_log_out));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() { //when click on DELETE
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        }).setNegativeButton(getResources().getString(R.string.button_text_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    // this method will auto fire when each sync task complete(onPostExecute).
    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

        //end customer sync
        if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeDownloadCustomer))) {

            if (syncStatus.isStatus()) {
                logSyncStatus = String.format("%-10s %s", "Customers", "(Done)");
            } else {
                logSyncStatus = String.format("%-22s %s", "Customers", "Failed");
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
                        String.format("%-21s %s", "Sale Prices", "(Failed)");
            }

            startSalesOrdersDownload();
        }

        //end sales order download sync
        else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeDownLoadSO))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Download Sales Orders", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Download Sales Orders", "(Failed)");
            }

            startSalesOrdersUpload();
        }

        //end sales order upload sync
        else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeUploadSO))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Upload Sales Orders", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Upload Sales Orders", "(Failed)");
            }

            startItemBalanceDownload();
        }

        //end Item Balance
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeDownItemBalance))) {
            /*if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Item Balances", "(Done)");
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Item Balances", "(Failed)");
            }*/

            startUploadRunningNos();
        }

        //end Running No Upload
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeUploadRunningNos))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Running Nos'", "(Done)");
                updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Running Nos'", "(Failed)");
            }

            startUploadPayments();
        }

        //end Payment Upload
        else if (syncStatus.getScope().equals(getResources().getString(R.string
                .SyncScopeUploadPayment))) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Payments Upload", "(Done)");
                updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Payments Upload", "(Failed)");
            }


            mAlertDialogBuilder = new AlertDialog.Builder(MsoHomeActivity.this);
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

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setPendingCount();

    }

    private void onButtonClick(View v) {
        if (findViewById(R.id.btnSalesOrder) == v) {
            Intent intent = new Intent(this, MsoSalesOrderListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (findViewById(R.id.btnPaymentCollection) == v) {
            Intent intent = new Intent(this, MsoPaymentListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (findViewById(R.id.btnCustomerInfo) == v) {
            Intent intent = new Intent(this, SalesCustomerListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("IsPopupNeeded", false);
            intent.putExtra("IsFromMso", true);
            intent.putExtra("IsFromMvs", false);
            intent.putExtra("formName", "MsoHome");
            startActivity(intent);

        } else if (findViewById(R.id.btnStockInfo) == v) {
            Intent intent = new Intent(this, SalesItemSearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("formName", "MsoHome");
            startActivity(intent);

        } else if (findViewById(R.id.btnPrint) == v) {
            if (!isPrintButtonClicked) {
                isPrintButtonClicked = true;
                showPrintDialog();
            }

        } else if (findViewById(R.id.btnSignOut) == v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MsoHomeActivity.this);
            builder.setTitle(getResources().getString(R.string.message_title_alert));
            builder.setMessage(getResources().getString(R.string.message_ask_log_out));
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() { //when click on DELETE
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mInitSync != null) {
                        mInitSync.stopAsyncTasks();
                        mInitSync.stopTimers();
                    }

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    // set the new task and clear flags
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    //finish();

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
                                //startUploadPayments();
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
        } else if (findViewById(R.id.btnCustomerPriceList) == v) {
            Intent intent = new Intent(this, MsoCustomerPriceListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    }

    private void showPrintDialog() {

        FrameLayout btnCalender;
        ImageButton btnClose;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style
                .Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams
        // .WRAP_CONTENT);
        dialog.getWindow()
                .setLayout((int) (getScreenWidth(this) * .9), ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_print);
        btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);

        final Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
        String[] items = new String[]{"Payment Summary"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout
                .simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        btnCalender = (FrameLayout) dialog.findViewById(R.id.btncalender);
        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickDeliveryDate(view, dialog);
            }
        });

        Button btnPrint = (Button) dialog.findViewById(R.id.btnprint);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final TextView editFilterCreatedDate = (TextView) dialog.findViewById(R.id.txtDeliveryDate);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterCreatedDate = "";
                try {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = format.parse(editFilterCreatedDate.getText().toString());
                    mFilterCreatedDate = format.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(MsoHomeActivity.this, PrintSummaryActivity.class);
                intent.putExtra("filterCreatedDate", mFilterCreatedDate);
                intent.putExtra("selectedItem", dropdown.getSelectedItem().toString());
                intent.putExtra("isPrint",true);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        Button btnemail = (Button) dialog.findViewById(R.id.btnemail);
        btnemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFilterCreatedDate = "";

                try {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = format.parse(editFilterCreatedDate.getText().toString());
                    mFilterCreatedDate = format.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(MsoHomeActivity.this, PrintSummaryActivity.class);
                intent.putExtra("filterCreatedDate", mFilterCreatedDate);
                intent.putExtra("selectedItem", dropdown.getSelectedItem().toString());
                intent.putExtra("isEmail",true);
                startActivity(intent);

                dialog.dismiss();

            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isPrintButtonClicked = false;
            }
        });

        editFilterCreatedDate.setText(simpledateformat.format(calender.getTime()));
        dialog.show();
    }

    private void OnClickDeliveryDate(View view, Dialog dialog) {
        Calendar cl, clNow;
        int nowYear, nowMonth, nowDay;
        cl = Calendar.getInstance();
        int _printDate = cl.get(Calendar.DAY_OF_MONTH);
        int _printMonth = cl.get(Calendar.MONTH);
        int _printYear = cl.get(Calendar.YEAR);
        final TextView txtcalender;

        clNow = Calendar.getInstance();
        nowYear = clNow.get(Calendar.YEAR);
        nowMonth = clNow.get(Calendar.MONTH);
        nowDay = clNow.get(Calendar.DAY_OF_MONTH);
        txtcalender = (TextView) dialog.findViewById(R.id.txtDeliveryDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtcalender.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, _printDate, _printMonth, _printYear);

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
        customerSyncTask.delegate = MsoHomeActivity.this;
        customerSyncTask.execute((Void) null);
    }

    private void startItemDownload() {
        itemSyncTask = new ItemSyncTask(getApplicationContext(), true);
        itemSyncTask.delegate = MsoHomeActivity.this;
        itemSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Items...");
    }

    private void startItemCategoryDownload() {
        itemCategorySyncTask = new ItemCategorySyncTask(getApplicationContext(), true);
        itemCategorySyncTask.delegate = MsoHomeActivity.this;
        itemCategorySyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Item Categories...");
    }

    private void startItemUomDownload() {
        itemUomSyncTask = new ItemUomSyncTask(getApplicationContext(), true);
        itemUomSyncTask.delegate = MsoHomeActivity.this;
        itemUomSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Item UOMs...");
    }

    private void startGstPercentageDownload() {

        gSTPostingSetupSyncTask = new GSTPostingSetupSyncTask(getApplicationContext(), true);
        gSTPostingSetupSyncTask.delegate = MsoHomeActivity.this;
        gSTPostingSetupSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading GST tables...");
    }

    private void startSalesPriceDownload() {
        salesPricesSyncTask = new SalesPricesSyncTask(getApplicationContext(), true, isInitialSyncRun());
        salesPricesSyncTask.delegate = MsoHomeActivity.this;
        salesPricesSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Sale Prices...");
    }

    private void startSalesOrdersDownload() {
        salesOrderDownloadSyncTask = new SalesOrderDownloadSyncTask(getApplicationContext(), true, isInitialSyncRun());
        salesOrderDownloadSyncTask.delegate = MsoHomeActivity.this;
        salesOrderDownloadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Sales Orders...");
    }

    private void startSalesOrdersUpload() {
        salesOrderUploadSyncTask = new SalesOrderUploadSyncTask(getApplicationContext(), true);
        salesOrderUploadSyncTask.delegate = MsoHomeActivity.this;
        salesOrderUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Sales Orders...");
    }

    private void startItemBalanceDownload() {
        itemBalancePdaSyncTask = new ItemBalancePdaSyncTask(getApplicationContext(), true);
        itemBalancePdaSyncTask.delegate = MsoHomeActivity.this;
        itemBalancePdaSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Downloading Item Balances...");
    }

    private void startUploadRunningNos() {
        userSetupRunningNoUploadTask = new UserSetupRunningNoUploadTask(getApplicationContext(), true);
        userSetupRunningNoUploadTask.delegate = MsoHomeActivity.this;
        userSetupRunningNoUploadTask.execute((Void) null);
        mProgressDialog.setMessage("Updating Running Numbers...");
    }

    private void startUploadPayments() {
        paymentUploadSyncTask = new PaymentUploadSyncTask(getApplicationContext(), true);
        paymentUploadSyncTask.delegate = MsoHomeActivity.this;
        paymentUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Payments...");
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

    public void stopAsyncTasks() {

        if (customerSyncTask != null) {
            customerSyncTask.cancel(true);
        }
        if (itemSyncTask != null) {
            itemSyncTask.cancel(true);
        }
        if (itemCategorySyncTask != null) {
            itemCategorySyncTask.cancel(true);
        }
        if (itemUomSyncTask != null) {
            itemUomSyncTask.cancel(true);
        }
        if (salesOrderDownloadSyncTask != null) {
            salesOrderDownloadSyncTask.cancel(true);
        }
        if (salesPricesSyncTask != null) {
            salesPricesSyncTask.cancel(true);
        }
        if (itemBalancePdaSyncTask != null) {
            itemBalancePdaSyncTask.cancel(true);
        }
        if (salesOrderUploadSyncTask != null) {
            salesOrderUploadSyncTask.cancel(true);
        }
        if (gSTPostingSetupSyncTask != null) {
            gSTPostingSetupSyncTask.cancel(true);
        }
        if (userSetupRunningNoUploadTask != null) {
            userSetupRunningNoUploadTask.cancel(true);
        }
        if (paymentUploadSyncTask != null) {
            paymentUploadSyncTask.cancel(true);
        }
    }

    private class GetPendingCountTask extends AsyncTask<Void, Void, Boolean> {

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
        protected void onCancelled() {}
    }

    public void getPendingCounts()
    {
        SalesOrderDbHandler soDb = new SalesOrderDbHandler(getApplicationContext());
        soDb.open();
        soPendingCount = soDb.getSoPendingCount(mApp.getmCurrentSalesPersonCode());
        soDb.close();

        PaymentHeaderDbHandler pmDb = new PaymentHeaderDbHandler(getApplicationContext());
        pmDb.open();
        paymentPendingCount = pmDb.getPendingPaymentCount(mApp.getmCurrentSalesPersonCode());
        pmDb.close();
    }

    public void updatePendingCount()
    {
        if(soPendingCount>0)
        {
            mTxtSoPendingCount.setVisibility(View.VISIBLE);
            mTxtSoPendingCount.setText(String.valueOf(soPendingCount));
        }
        else
        {
            mTxtSoPendingCount.setVisibility(View.GONE);
        }

        if(paymentPendingCount>0)
        {
            mTxtPaymentPendingCount.setVisibility(View.VISIBLE);
            mTxtPaymentPendingCount.setText(String.valueOf(paymentPendingCount));
        }
        else
        {
            mTxtPaymentPendingCount.setVisibility(View.GONE);
        }
    }


}