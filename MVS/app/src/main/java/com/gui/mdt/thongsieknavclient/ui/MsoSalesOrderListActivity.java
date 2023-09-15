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
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MsoSalesOrderListAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderClearAndDownloadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.UserSetupRunningNoUploadTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MsoSalesOrderListActivity extends AppCompatActivity implements android.view.View.OnClickListener,
        AsyncResponse {

    //Data related member variables
    private static MsoSalesOrderListAdapter salesOrderListAdapter;
    //UI related member variables
    Toolbar mMyToolbar;
    SwipyRefreshLayout mSwipyrefreshlayoutSalesOrders;
    //API related member variables
    NavClientApp mApp;
    Logger mLog;
    ApiPostMobileSalesInvoiceHeaderResponse salesInvoiceHeaderResponse;
    List<SalesOrder> salesOrderList;
    List<SalesOrder> comfirmedSalesOrderList;
    UserSetupRunningNoUploadTask userSetupRunningNoUploadTask;
    SalesOrderUploadSyncTask salesOrderUploadSyncTask;
    Customer mTempCustomer;

    String transfferFailedSalesOrderNo;
    int mConfirmedCount = 0
            , mCreditLimitFaildCount=0
            , mOverDueInvoiceCount=0
            , mMinSaleAmtNotExceedCount=0;
    private Button mBtnSalesOrderListSearch,mBtnSync;
    private Drawable mBtnSyncDrawableGreen, mBtnSyncDrawableGray;
    private FloatingActionButton mFabTopUpQuantity;
    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerViewSalesOrders;
    //Task related member variables
    private getSalesOrderListTask getSalesOrderListTask = null;
    private String
            mFilterCustomerCode = "",
            mFilterCustomerName = "",
            mFilterSalesOrderNo = "",
            mFilterCreatedDate = "",
            mFilterStatus = "",
            mFilterSalesPersonCode = "",
            searchText="";
    private boolean mIsUpdated = false, isSearchButtonClicked = false;
    private EditText txtSearch;
    //private Bundle extras;
    private boolean isFormSearch=false;
    String logSyncStatus, mCreditLimitFaildList="",mDueDateFaildList = "", mMinSaleAmtNotExceedList = "";
    SalesOrderClearAndDownloadSyncTask soDownloadSyncTask;
    AlertDialog.Builder mAlertDialogBuilder;
    AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mso_sales_order_list);

        txtSearch=(EditText)findViewById(R.id.txtSearch);

        mFabTopUpQuantity = (FloatingActionButton) findViewById(R.id.fabTopUpQuantity);
        mFabTopUpQuantity.bringToFront();

        mFabTopUpQuantity.setOnClickListener(this);
        mMyToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mMyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Order List");

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        mBtnSalesOrderListSearch = (Button) findViewById(R.id.btnSearch);
        Drawable searchDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_search)
                .color(Color.WHITE)
                .sizeDp(30);

        mBtnSalesOrderListSearch.setBackgroundDrawable(searchDrawable);
        mBtnSalesOrderListSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSearchButtonClicked)
                {
                    isSearchButtonClicked = true;
                    showSearchDialog();
                }

            }
        });

        mBtnSyncDrawableGreen = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_refresh)
                .color(Color.GREEN)
                .sizeDp(30);
        mBtnSyncDrawableGray = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_refresh)
                .color(Color.GRAY)
                .sizeDp(30);
        mBtnSync = (Button) findViewById(R.id.btnSync);
        mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGreen);
        mBtnSync.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(MsoSalesOrderListActivity.this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle("Sync Data");

        Drawable drawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_ellipsis_v)
                .color(Color.WHITE)
                .sizeDp(25);
        txtSearch.addTextChangedListener(searchTextWatcher());
        mMyToolbar.setOverflowIcon(drawable);
        mApp = (NavClientApp) getApplicationContext();
        this.mLog = Logger.getLogger(MsoSalesOrderListActivity.class);

        mFilterSalesPersonCode = mApp.getmCurrentSalesPersonCode();

        mRecyclerViewSalesOrders = (RecyclerView) findViewById(R.id.recyclerViewSalesOrders);
        mRecyclerViewSalesOrders.setHasFixedSize(true);
        mRecyclerViewSalesOrders.setLayoutManager(new LinearLayoutManager(this));

        mSwipyrefreshlayoutSalesOrders = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayoutSalesOrders);

        mSwipyrefreshlayoutSalesOrders.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    mFilterCustomerName = "";
                    mFilterCustomerCode = "";
                    mFilterSalesOrderNo = "";
                    mFilterCreatedDate = "";
                    mFilterStatus = "";
                    txtSearch.setText("");

                    getSalesOrderListTask = new getSalesOrderListTask();
                    getSalesOrderListTask.execute((Void) null);
                    mSwipyrefreshlayoutSalesOrders.setRefreshing(false);

                    //mFabTopUpQuantity.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_end_color));
                }

            }
        });

        mSwipyrefreshlayoutSalesOrders.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        salesOrderListAdapter = new MsoSalesOrderListAdapter(new ArrayList<SalesOrder>(),
                R.layout.item_sales_order_card, getApplicationContext());
        mRecyclerViewSalesOrders.setAdapter(salesOrderListAdapter);

        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //action when dialog is dismissed goes here
                if(soDownloadSyncTask!=null){
                    soDownloadSyncTask.cancel(true);
                    mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGreen);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sales_order_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (findViewById(R.id.fabTopUpQuantity) == v) {
            Intent intent = new Intent(this, MsoSalesOrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("listType", "MsoNewSalesOrder");
            this.startActivity(intent);
        }else if (findViewById(R.id.btnSync) == v) {
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

                                startSalesOrderDownload();
                                //startUploadPayments();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            } else {
                new android.support.v7.app.AlertDialog.Builder(this)
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

    @Override
    protected void onResume() {
        super.onResume();

        if (mFilterCreatedDate.isEmpty()) {
            isFormSearch = false;
        } else {
            isFormSearch = true;
        }

        getSalesOrderListTask = new getSalesOrderListTask();
        getSalesOrderListTask.execute((Void) null);
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {
        if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeUploadSO))
        {
            if (syncStatus.isStatus()) {
                if(syncStatus.getMessage().equals("")){
                    mProgressDialog.dismiss();
                }
                else {
                    mProgressDialog.dismiss();
                    Toast.makeText(this,syncStatus.getMessage(),Toast.LENGTH_LONG);
                }

            } else {

            }

            refreshList();
        }
        if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeDownLoadSO)) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Sales Order Download", "(Done)");
                //updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Sales Order Download", "(Failed)");
            }


            mAlertDialogBuilder = new AlertDialog.Builder(MsoSalesOrderListActivity.this);
            mAlertDialogBuilder
                    .setTitle("Sync Completed")
                    .setMessage(logSyncStatus)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                            mLog.info("SYNC_STATUS : " + logSyncStatus);
                            mSwipyrefreshlayoutSalesOrders.post(new Runnable() {
                                @Override public void run() {
                                    mSwipyrefreshlayoutSalesOrders.setRefreshing(true);

                                    mFilterCustomerName = "";
                                    mFilterCustomerCode = "";
                                    mFilterSalesOrderNo = "";
                                    mFilterCreatedDate = "";
                                    mFilterStatus = "";
                                    txtSearch.setText("");

                                    getSalesOrderListTask = new getSalesOrderListTask();
                                    getSalesOrderListTask.execute((Void) null);
                                    mSwipyrefreshlayoutSalesOrders.setRefreshing(false);

                                }
                            });
                        }
                    });
            mAlertDialog = mAlertDialogBuilder.create();
            mProgressDialog.dismiss();
            mAlertDialog.show();
            mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGreen);



        }


    }

    public static int getScreenWidth(Activity activity) {
        Point _size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(_size);
        return _size.x;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_selectAll:
                salesOrderList = getModel(true);
                salesOrderListAdapter = new MsoSalesOrderListAdapter(salesOrderList,
                        R.layout.item_sales_order_card, getApplicationContext());
                mRecyclerViewSalesOrders.setAdapter(salesOrderListAdapter);
                return true;
            case R.id.action_cancelSelect:
                salesOrderList = getModel(false);
                salesOrderListAdapter = new MsoSalesOrderListAdapter(salesOrderList,
                        R.layout.item_sales_order_card, getApplicationContext());
                mRecyclerViewSalesOrders.setAdapter(salesOrderListAdapter);
                return true;
            case R.id.action_confirm:
            try {
                if(isNetworkConnected()){
                    getConfirmSalesOrders();
                    saveConfirmSalesOrdersSqlLite();
                    if (mIsUpdated) {
                        salesOrderListAdapter = new MsoSalesOrderListAdapter(salesOrderList,
                                R.layout.item_sales_order_card, getApplicationContext());
                        mRecyclerViewSalesOrders.setAdapter(salesOrderListAdapter);
                        salesOrderListAdapter.notifyDataSetChanged();
                        mProgressDialog.dismiss();
                        Toast.makeText(MsoSalesOrderListActivity.this, "Successfully Saved.",
                                Toast.LENGTH_SHORT).show();

                        comfirmedSalesOrderList = new ArrayList<>();
                        mIsUpdated = false;

                        if (isNetworkAvailable()) {
                            startSalesOrdersUpload();
                            startUploadRunningNos();
                        }

                    } else {
                        mProgressDialog.dismiss();
                        if (mConfirmedCount == 0) {
                            //Toast.makeText(MsoSalesOrderListActivity.this, "Please select at least one item to confirm",
                            //Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MsoSalesOrderListActivity.this, "Update failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(MsoSalesOrderListActivity.this, "No network available. Please retry.",
                            Toast.LENGTH_SHORT).show();
                }

                }catch (Exception ex){

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startUploadRunningNos() {
        userSetupRunningNoUploadTask = new UserSetupRunningNoUploadTask(getApplicationContext(), true);
        userSetupRunningNoUploadTask.delegate = MsoSalesOrderListActivity.this;
        userSetupRunningNoUploadTask.execute((Void) null);
        //mProgressDialog.setMessage("Updating Running Numbers...");
    }

    private void startSalesOrdersUpload() {
        salesOrderUploadSyncTask = new SalesOrderUploadSyncTask(getApplicationContext(), true);
        salesOrderUploadSyncTask.delegate = MsoSalesOrderListActivity.this;
        salesOrderUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Sales Orders...");
        mProgressDialog.show();
    }

    private void refreshList(){
        mFilterCustomerName = "";
        mFilterCustomerCode = "";
        mFilterSalesOrderNo = "";
        mFilterCreatedDate = "";
        mFilterStatus = "";
        txtSearch.setText("");

        getSalesOrderListTask = new getSalesOrderListTask();
        getSalesOrderListTask.execute((Void) null);
        //mSwipyrefreshlayoutSalesOrders.setRefreshing(false);
    }

    private void getSalesOrderList(String mFilterSalesPersonCode,
                                   String mFilterCustomerCode,
                                   String mFilterCustomerName,
                                   String mFilterSalesOrderNo,
                                   String mFilterCreatedDate,
                                   String mFilterStatus){
        salesOrderList=new ArrayList<SalesOrder>();
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(this);
        dbAdapter.open();
        salesOrderList = dbAdapter.getSalesOrderList(
                mFilterSalesPersonCode,
                mFilterCustomerCode,
                mFilterCustomerName,
                mFilterSalesOrderNo,
                mFilterCreatedDate,
                mFilterStatus,isFormSearch);

        isFormSearch=false;
        dbAdapter.close();
    }

    private List<SalesOrder> getModel(boolean status) {
        for (SalesOrder so : salesOrderList) {
            so.setConfirmedSo(status);
        }
        return salesOrderList;
    }

    private void showSearchDialog() {
        FrameLayout _btncalender;

        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();

        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.getWindow().setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);

        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_sales_order_list_search);
        dialog.setCancelable(false);

        final Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
        String[] items = new String[]{"", "Pending", "Confirm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);

        final EditText editFilterCustomerCode = (EditText) dialog.findViewById(R.id.txtCustomerCode);
        final EditText editFilterCustomerName = (EditText) dialog.findViewById(R.id.txtCustomerName);
        final EditText editFilterSalesOrderNo = (EditText) dialog.findViewById(R.id.txtSalesOrderNo);
        final TextView editFilterCreatedDate = (TextView) dialog.findViewById(R.id.txtDeliveryDate);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterCustomerCode = editFilterCustomerCode.getText().toString();
                mFilterCustomerName = editFilterCustomerName.getText().toString();
                mFilterSalesOrderNo = editFilterSalesOrderNo.getText().toString();
                mFilterStatus = dropdown.getSelectedItem().toString();
                String date = editFilterCreatedDate.getText().toString();

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date startDate = df.parse(date);
                    mFilterCreatedDate = df.format(startDate);
                    isFormSearch=true;

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                getSalesOrderListTask = new getSalesOrderListTask();
                getSalesOrderListTask.execute((Void) null);
                dialog.dismiss();
            }
        });
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        _btncalender = (FrameLayout) dialog.findViewById(R.id.btncalender);

        _btncalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickCreatedDate(dialog);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isSearchButtonClicked = false;
            }
        });

        editFilterCustomerCode.setText(mFilterCustomerCode);
        editFilterCustomerName.setText(mFilterCustomerName);
        editFilterSalesOrderNo.setText(mFilterSalesOrderNo);
        editFilterCreatedDate.setText(mFilterCreatedDate);
        int spinnerPosition = adapter.getPosition(mFilterStatus);
        dropdown.setSelection(spinnerPosition);

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void OnClickCreatedDate(Dialog dialog) {
        Calendar _cl, _clNow;
        final int _date, _month, _year, _nowYear, _nowMonth, _nowDay;
        _cl = Calendar.getInstance();
        _date = _cl.get(Calendar.DAY_OF_MONTH);
        _month = _cl.get(Calendar.MONTH);
        _year = _cl.get(Calendar.YEAR);
        final TextView _txtcalender;

        _clNow = Calendar.getInstance();
        _nowYear = _clNow.get(Calendar.YEAR);
        _nowMonth = _clNow.get(Calendar.MONTH);
        _nowDay = _clNow.get(Calendar.DAY_OF_MONTH);
        _txtcalender = (TextView) dialog.findViewById(R.id.txtDeliveryDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                _txtcalender.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, _date, _month, _year);

        datePickerDialog.updateDate(_nowYear, _nowMonth, _nowDay);
        datePickerDialog.show();
    }

    private void getConfirmSalesOrders() {
        comfirmedSalesOrderList = new ArrayList<>();
        DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date _dateobj = new Date();

        for (SalesOrder so :
                salesOrderList) {
            String status = getResources().getString(R.string.SalesOrderStatusPending);
            if (so.getStatus().equals(status) && so.isTransferred() == false && so.isConfirmedSo()) {
                    so.setLastModifiedBy(mApp.getCurrentUserName());
                    so.setLastModifiedDateTime(_dateFormat.format(_dateobj).toString());
                    so.setLastTransferredBy(mApp.getCurrentUserName());
                    so.setLastTransferredDateTime(_dateFormat.format(_dateobj).toString());

                    comfirmedSalesOrderList.add(so);
            }
        }
    }

    public float getAllSoTotalAmtInclVatByCustomer(String cusCode, String deliveryDate,String invoiceNo)
    {
        float totalAmount=0;

        SalesOrderDbHandler soDb=new SalesOrderDbHandler(getApplicationContext());
        soDb.open();

        totalAmount=soDb.getAllSoTotalAmtInclVat(cusCode,deliveryDate, invoiceNo);

        soDb.close();

        return totalAmount;
    }

    private void getCustomer(String Code) {
        mTempCustomer = new Customer();
        boolean status = false;
        CustomerDbHandler dbAdapter = new CustomerDbHandler(this);
        dbAdapter.open();

        mTempCustomer = dbAdapter.getCustomerByCustomerCode(Code);

        dbAdapter.close();
    }

    private void saveConfirmSalesOrdersSqlLite() {
        setSalesOrderStatus(comfirmedSalesOrderList);
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(this);
        dbAdapter.open();
        if (mConfirmedCount > 0) {
            mIsUpdated = dbAdapter.saveConfirmSalesOrders(comfirmedSalesOrderList);
        }
        dbAdapter.close();
        
        if(mCreditLimitFaildCount > 0 || mOverDueInvoiceCount > 0 || mMinSaleAmtNotExceedCount > 0)
        {
            if(mMinSaleAmtNotExceedCount>0)
            {
                String msgItems =  mMinSaleAmtNotExceedList;
                mMinSaleAmtNotExceedList = "Minimum sales amount validation failed: "+'\n' + msgItems;
            }

            AlertDialog.Builder builder1 = new AlertDialog.Builder(MsoSalesOrderListActivity.this);
            builder1.setTitle("Following sales orders are not confirmed.");
            builder1.setMessage(mCreditLimitFaildList + mDueDateFaildList + mMinSaleAmtNotExceedList);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void setSalesOrderStatus(List<SalesOrder> comfirmedSalesOrderList) {
        try {
            mConfirmedCount = 0;
            transfferFailedSalesOrderNo = "";
            mCreditLimitFaildCount=0;
            mCreditLimitFaildList="";
            mDueDateFaildList = "";
            mOverDueInvoiceCount=0;
            mMinSaleAmtNotExceedList = "";
            mMinSaleAmtNotExceedCount = 0;

            if (salesInvoiceHeaderResponse == null) {
                for (SalesOrder so : comfirmedSalesOrderList) {
                    if (so.isConfirmedSo()) {

                        if(validateCusCreditLimit(so.getAmountIncludingVAT()
                                                  ,so.getSelltoCustomerNo()
                                                  ,so.getNo()))
                        {
                            if(validateDueDate(so.getSelltoCustomerNo()))
                            {
                                float totalOfAllAmtInclVat = so.getAmountIncludingVAT() +
                                        getAllSoTotalAmtInclVatByCustomer(so.getSelltoCustomerNo()
                                                ,so.getShipmentDate()
                                                ,so.getNo());
                                
                                //get customer
                                getCustomer(so.getSelltoCustomerNo());

                                if(Double.parseDouble(String.valueOf(totalOfAllAmtInclVat))> mTempCustomer.getMinimumSalesAmount())
                                {
                                    so.setStatus(getResources().getString(R.string.SalesOrderStatusConfirmed));
                                    so.setTransferred(false);
                                    mConfirmedCount++;
                                }
                                else
                                {
                                    mMinSaleAmtNotExceedList = mMinSaleAmtNotExceedList
                                            + so.getNo() + '\n';
                                    mMinSaleAmtNotExceedCount++;
                                }
                            }
                            else
                            {
                                mDueDateFaildList = mDueDateFaildList
                                        +"Overdue invoice: "
                                        + so.getNo() + '\n';
                                mOverDueInvoiceCount++;
                            }
                        }
                        else
                        {
                            mCreditLimitFaildList = mCreditLimitFaildList
                                    +"Credit limit exceeded: "
                                    + so.getNo() + '\n';
                            mCreditLimitFaildCount++;
                        }

                    } else {
                        so.setStatus(getResources().getString(R.string.SalesOrderStatusPending));
                        so.setTransferred(false);
                    }
                }
            } else {
                for (SalesOrder so : comfirmedSalesOrderList) {
                    for (ApiPostMobileSalesInvoiceHeaderResponse.SalesInvoiceHeaderResponse res
                            : salesInvoiceHeaderResponse.getTrStatusList()) {
                        if (res.getDocumentNo().equals(so.getNo())) {
                            if (res.isTransferred() == true && so.isConfirmedSo()) {
                                so.setStatus(getResources().getString(R.string.SalesOrderStatusConfirmed));
                                so.setTransferred(res.isTransferred());
                                mConfirmedCount++;
                            } else if (res.isTransferred() == false && so.isConfirmedSo()) {
                                so.setStatus(getResources().getString(R.string.SalesOrderStatusConfirmed));
                                so.setTransferred(res.isTransferred());
                                if(transfferFailedSalesOrderNo=="")
                                    transfferFailedSalesOrderNo= res.getDocumentNo();
                                else
                                    transfferFailedSalesOrderNo = transfferFailedSalesOrderNo +" / "+ res.getDocumentNo();
                                mConfirmedCount++;
                            } else {
                                so.setStatus(getResources().getString(R.string.SalesOrderStatusPending));
                                so.setTransferred(res.isTransferred());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public boolean validateCusCreditLimit(float grandTotal, String cusCode, String invoiceNo) {
        boolean isNotExceededCusCreditLimit = false;

        //double grandTotal_ = Double.valueOf(Float.valueOf(h_grandTotal).toString()).doubleValue();
        CustomerDbHandler cusDb = new CustomerDbHandler(getApplicationContext());
        cusDb.open();

        isNotExceededCusCreditLimit =
                cusDb.validateCustomerCreditLimit(cusCode, grandTotal, invoiceNo);

        cusDb.close();
        return isNotExceededCusCreditLimit;
    }

    public boolean validateDueDate(String cusCode)
    {
        boolean isValidDueDate = true;
        CustomerDbHandler cusDb = new CustomerDbHandler(getApplicationContext());
        cusDb.open();

        isValidDueDate = cusDb.validateCustomerDueDate(cusCode,true);
        cusDb.close();

        return isValidDueDate;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class getSalesOrderListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getSalesOrderList(mFilterSalesPersonCode, mFilterCustomerCode,
                        mFilterCustomerName, mFilterSalesOrderNo, mFilterCreatedDate, mFilterStatus);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MsoSalesOrderListActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    salesOrderListAdapter = new MsoSalesOrderListAdapter(salesOrderList,
                            R.layout.item_sales_order_card, getApplicationContext());
                    mRecyclerViewSalesOrders.setAdapter(salesOrderListAdapter);
                    salesOrderListAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();
                } catch (Exception ex) {
                }
                mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(MsoSalesOrderListActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }


    }

    private TextWatcher searchTextWatcher(){
        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                searchText = txtSearch.getText().toString();
                mProgressDialog.setMessage("Loading...");

                if (!searchText.isEmpty()) {
                    mProgressDialog.show();
                    salesOrderListAdapter = new MsoSalesOrderListAdapter(searchSalesOrder(),
                            R.layout.item_sales_order_card, MsoSalesOrderListActivity.this);
                    mRecyclerViewSalesOrders.setAdapter(salesOrderListAdapter);
                    salesOrderListAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();
                }
                else {
                    mProgressDialog.show();
                    salesOrderListAdapter = new MsoSalesOrderListAdapter(salesOrderList,
                            R.layout.item_sales_order_card, MsoSalesOrderListActivity.this);
                    mRecyclerViewSalesOrders.setAdapter(salesOrderListAdapter);
                    salesOrderListAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        return tw;
    }

    public List<SalesOrder> searchSalesOrder(){
        List<SalesOrder> salesOrderSearchResultList = new ArrayList<SalesOrder>();

        String text = searchText.toUpperCase();

        if (salesOrderList != null) {
            for (SalesOrder so : salesOrderList) {
                if (so.getNo().contains(text)) {
                    salesOrderSearchResultList.add(so);
                }
                else if (so.getSelltoCustomerName().contains(text)) {
                    salesOrderSearchResultList.add(so);
                }
                else if (so.getSelltoCustomerNo().contains(text)) {
                    salesOrderSearchResultList.add(so);
                }
                else if (so.getDocumentDate().contains(text)) {
                    salesOrderSearchResultList.add(so);
                }
            }
        }
        return salesOrderSearchResultList;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void startSalesOrderDownload() {

        if(deleteAllHqDownloadedSalesOrders()) {
            soDownloadSyncTask = new SalesOrderClearAndDownloadSyncTask(getApplicationContext(), true, false);
            soDownloadSyncTask.delegate = MsoSalesOrderListActivity.this;
            soDownloadSyncTask.execute((Void) null);
            mProgressDialog.setMessage("Downloading Sale Orders...");
        }
    }

    private boolean deleteAllHqDownloadedSalesOrders(){

        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(getApplicationContext());
        dbAdapter.open();
        boolean deleted = dbAdapter.deleteAllHqDownloadedSalesOrders();
        dbAdapter.close();

        return deleted;
    }
}
