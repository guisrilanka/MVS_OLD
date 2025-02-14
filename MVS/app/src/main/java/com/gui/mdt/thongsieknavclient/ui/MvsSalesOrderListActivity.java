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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.gui.mdt.thongsieknavclient.adapters.MvsSalesOrderListAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderLineDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesInvoiceUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderClearAndDownloadForMvsSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderImageUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.UserSetupRunningNoUploadTask;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;
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

public class MvsSalesOrderListActivity extends AppCompatActivity implements android.view.View.OnClickListener
        , AsyncResponse {

    Toolbar mMvs_so_toolbar;
    MvsSalesOrderListAdapter mMvsSalesOrderListAdapter;
    int date, month, year, mCreditLimitFaildCount = 0, mOverDueInvoiceCount = 0, mConfirmedCount = 0;
    List<SalesOrder> mSalesOrderList, mCheckedSoList;
    FloatingActionButton mFabAddNewItem;
    NavClientApp mApp;
    String mFilterCustomerCode = "",
            mFilterCustomerName = "",
            mFilterSalesOrderNo = "",
            mFilterCreatedDate = "",
            mFilterStatus = "",
            mFilterDriverCode = "",
            mSearchText = "",
            mCreditLimitFaildList = "",
            mDueDateFaildList = "",
            mLogSyncStatus;
    RecyclerView mRecyclerViewSalesOrders;
    SwipyRefreshLayout mSwipyrefreshlayoutSalesOrders;
    getMvsSalesOrderListTask getMvsSalesOrderListTask = null;
    ProgressDialog mProgressDialog;
    boolean mIsUpdated = false, isSearchButtonClicked = false;
    Button btnSearch, mBtnSync;
    EditText mTxtSearch;

    Drawable mBtnSyncDrawableGreen, mBtnSyncDrawableGray;
    AlertDialog.Builder mAlertDialogBuilder;
    AlertDialog mAlertDialog;

    SalesOrderClearAndDownloadForMvsSyncTask soDownloadSyncTask;
    SalesInvoiceUploadSyncTask salesInvoiceUploadSyncTask;
    UserSetupRunningNoUploadTask userSetupRunningNoUploadTask;
    private Logger mLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvs_sales_order_list);

        mFabAddNewItem = (FloatingActionButton) findViewById(R.id.fabAddNewItem);
        mMvs_so_toolbar = (Toolbar) findViewById(R.id.mvs_so_toolbar);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        mRecyclerViewSalesOrders = (RecyclerView) findViewById(R.id.recyclerViewSalesOrders);
        mSwipyrefreshlayoutSalesOrders = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayoutSalesOrders);
        mTxtSearch = (EditText) findViewById(R.id.txtSearch);

        mSalesOrderList = new ArrayList<SalesOrder>();
        mCheckedSoList = new ArrayList<SalesOrder>();

        mFabAddNewItem.bringToFront();
        setSupportActionBar(mMvs_so_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.mLog = Log4jHelper.getLogger();

        Drawable backArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE).sizeDp(30);

        Drawable searchDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_search)
                .color(Color.WHITE).sizeDp(30);

        Drawable drawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_ellipsis_v)
                .color(Color.WHITE).sizeDp(30);

        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        btnSearch.setBackgroundDrawable(searchDrawable);
        mMvs_so_toolbar.setOverflowIcon(drawable);

        mFabAddNewItem.setOnClickListener(this);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearchButtonClicked) {
                    isSearchButtonClicked = true;
                    showSearchDialog();
                }
            }
        });

        mFabAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MvsSalesOrderListActivity.this
                        , MvsSalesOrderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(getResources().getString(R.string.intent_extra_details),
                        getResources().getString(R.string.mvs_new_sales_order));
                startActivity(intent);
            }
        });

        mApp = (NavClientApp) getApplicationContext();

        mFilterDriverCode = mApp.getmCurrentDriverCode();

        mRecyclerViewSalesOrders.setHasFixedSize(true);
        mRecyclerViewSalesOrders.setLayoutManager(new LinearLayoutManager(this));

        mSwipyrefreshlayoutSalesOrders.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    mFilterCustomerName = "";
                    mFilterCustomerCode = "";
                    mFilterSalesOrderNo = "";
                    mFilterCreatedDate = "";
                    mFilterStatus = "";

                    getMvsSalesOrderListTask = new getMvsSalesOrderListTask();
                    getMvsSalesOrderListTask.execute((Void) null);

                    mSwipyrefreshlayoutSalesOrders.setRefreshing(false);
                }
            }
        });

        mSwipyrefreshlayoutSalesOrders.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mMvsSalesOrderListAdapter = new MvsSalesOrderListAdapter(new ArrayList<SalesOrder>(),
                R.layout.item_mvs_sales_order_card, getApplicationContext());
        mRecyclerViewSalesOrders.setAdapter(mMvsSalesOrderListAdapter);

        mTxtSearch.addTextChangedListener(searchTextWatcher());

        //sync
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

        mProgressDialog = new ProgressDialog(MvsSalesOrderListActivity.this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle("Sync Data");

        initSwipeDelete();

        mBtnSync.setVisibility(View.GONE); //on req 2018-02-06
    }

    public void initSwipeDelete() {

        ItemTouchHelper.SimpleCallback simpleCallback
                = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.RIGHT) {

                    //if swipe Right
                    final SalesOrder so = mSalesOrderList.get(position);

                    if (so.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusPending))) {
                        AlertDialog.Builder builder
                                = new AlertDialog.Builder(MvsSalesOrderListActivity.this); //alert for confirm to delete
                        builder.setMessage(getResources().getString(R.string.message_ask_so_delete));    //set message
                        builder.setCancelable(false);
                        builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() { //when click on DELETE
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (deleteSO(so.getNo(), getApplicationContext())) {
                                    mSalesOrderList.remove(position);
                                    mMvsSalesOrderListAdapter.notifyDataSetChanged();
                                    Toast.makeText(mApp, getResources().getString(R.string.message_deleted)
                                            , Toast.LENGTH_SHORT).show();
                                } else {
                                    mMvsSalesOrderListAdapter.notifyDataSetChanged();
                                    Toast.makeText(mApp, getResources().getString(R.string.message_failed)
                                            , Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).setNegativeButton(getResources().getString(R.string.button_text_no), new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMvsSalesOrderListAdapter.notifyDataSetChanged();
                            }
                        }).show();
                    } else {
                        AlertDialog.Builder builder
                                = new AlertDialog.Builder(MvsSalesOrderListActivity.this);
                        builder.setTitle(getResources().getString(R.string.message_title_alert));
                        builder.setMessage(getResources().getString(R.string.message_not_allow_delete));
                        builder.setCancelable(false);
                        builder.setPositiveButton(getResources().getString(R.string.button_text_ok)
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mMvsSalesOrderListAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerViewSalesOrders);

    }

    private boolean deleteSO(String soNo, Context context) {
        boolean isSuccess = false;
        SalesOrderDbHandler soDb = new SalesOrderDbHandler(context);

        soDb.open();
        isSuccess = soDb.deleteSO(soNo);
        soDb.close();

        return isSuccess;
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
                                mLogSyncStatus = "";
                                mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGray);
                                mProgressDialog.setMessage("Initializing...");
                                mProgressDialog.show();

                                startSalesOrderDownload();
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMvsSalesOrderListTask = new getMvsSalesOrderListTask();
        getMvsSalesOrderListTask.execute((Void) null);
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {
        if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeUploadSI)) {
            if (syncStatus.isStatus()) {
                if (syncStatus.getMessage().equals("")) {
                    mProgressDialog.dismiss();
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(this, syncStatus.getMessage(), Toast.LENGTH_LONG);
                }

            } else {

            }

            refreshList();
        }
        if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeDownLoadSO)) {
            if (syncStatus.isStatus()) {
                mLogSyncStatus = mLogSyncStatus + '\n' +
                        String.format("%-10s %s", "Sales Order Download", "(Done)");
                //updateUserSetupInitialSync();
            } else {
                mLogSyncStatus = mLogSyncStatus + '\n' +
                        String.format("%-21s %s", "Sales Order Download", "(Failed)");
            }


            mAlertDialogBuilder = new AlertDialog.Builder(MvsSalesOrderListActivity.this);
            mAlertDialogBuilder
                    .setTitle("Sync Completed")
                    .setMessage(mLogSyncStatus)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            //mLog.info("SYNC_STATUS : " + logSyncStatus);
                            mSwipyrefreshlayoutSalesOrders.post(new Runnable() {
                                @Override
                                public void run() {
                                    mSwipyrefreshlayoutSalesOrders.setRefreshing(true);

                                    mFilterCustomerName = "";
                                    mFilterCustomerCode = "";
                                    mFilterSalesOrderNo = "";
                                    mFilterCreatedDate = "";
                                    mFilterStatus = "";
                                    mTxtSearch.setText("");

                                    getMvsSalesOrderListTask = new MvsSalesOrderListActivity.getMvsSalesOrderListTask();
                                    getMvsSalesOrderListTask.execute((Void) null);
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

    private void refreshList() {
        mFilterCustomerName = "";
        mFilterCustomerCode = "";
        mFilterSalesOrderNo = "";
        mFilterCreatedDate = "";
        mFilterStatus = "";
        mTxtSearch.setText("");

        getMvsSalesOrderListTask = new MvsSalesOrderListActivity.getMvsSalesOrderListTask();
        getMvsSalesOrderListTask.execute((Void) null);
    }

    private void startSalesOrderDownload() {

        if (deleteAllHqDownloadedSalesOrdersForMvs()) {
            soDownloadSyncTask = new SalesOrderClearAndDownloadForMvsSyncTask(getApplicationContext(), true, false);
            soDownloadSyncTask.delegate = MvsSalesOrderListActivity.this;
            soDownloadSyncTask.execute((Void) null);
            mProgressDialog.setMessage("Downloading Sale Orders...");
        }
    }

    private boolean deleteAllHqDownloadedSalesOrdersForMvs() {

        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(getApplicationContext());
        dbAdapter.open();
        boolean deleted = dbAdapter.deleteAllHqDownloadedSalesOrdersForMvs();
        dbAdapter.close();

        return deleted;
    }

    private TextWatcher searchTextWatcher() {
        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                mSearchText = mTxtSearch.getText().toString();
                mProgressDialog.setMessage("Loading...");

                if (!mSearchText.isEmpty()) {
                    mProgressDialog.show();
                    mMvsSalesOrderListAdapter = new MvsSalesOrderListAdapter(searchSalesOrder(),
                            R.layout.item_mvs_sales_order_card, getApplicationContext());
                    mRecyclerViewSalesOrders.setAdapter(mMvsSalesOrderListAdapter);
                    mMvsSalesOrderListAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();
                } else {
                    mProgressDialog.show();
                    mMvsSalesOrderListAdapter = new MvsSalesOrderListAdapter(mSalesOrderList,
                            R.layout.item_mvs_sales_order_card, getApplicationContext());
                    mRecyclerViewSalesOrders.setAdapter(mMvsSalesOrderListAdapter);
                    mMvsSalesOrderListAdapter.notifyDataSetChanged();

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

    public List<SalesOrder> searchSalesOrder() {
        List<SalesOrder> salesOrderSearchResultList = new ArrayList<SalesOrder>();

        String text = mSearchText.toUpperCase();

        if (mSalesOrderList != null) {
            for (SalesOrder so : mSalesOrderList) {
                if (so.getNo().contains(text)) {
                    salesOrderSearchResultList.add(so);
                } else if (so.getSelltoCustomerName().contains(text)) {
                    salesOrderSearchResultList.add(so);
                } else if (so.getSelltoCustomerNo().contains(text)) {
                    salesOrderSearchResultList.add(so);
                } else if (so.getDocumentDate().contains(text)) {
                    salesOrderSearchResultList.add(so);
                }
            }
        }
        return salesOrderSearchResultList;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_selectAll:
                mSalesOrderList = getModel(true);
                mMvsSalesOrderListAdapter = new MvsSalesOrderListAdapter(mSalesOrderList,
                        R.layout.item_mvs_sales_order_card, getApplicationContext());
                mRecyclerViewSalesOrders.setAdapter(mMvsSalesOrderListAdapter);
                return true;
            case R.id.action_cancelSelect:
                mSalesOrderList = getModel(false);
                mMvsSalesOrderListAdapter = new MvsSalesOrderListAdapter(mSalesOrderList,
                        R.layout.item_mvs_sales_order_card, getApplicationContext());
                mRecyclerViewSalesOrders.setAdapter(mMvsSalesOrderListAdapter);
                return true;
            case R.id.action_confirm:
                try {

                    if (isNetworkConnected()) {


                        ConfirmSalesOrderListTask task = new MvsSalesOrderListActivity.ConfirmSalesOrderListTask();
                        task.execute((Void) null);

                        /*if (getCheckedSoCountAndList() > 0) {
                            confirmSo();

                            if (mIsUpdated) {

                                mMvsSalesOrderListAdapter = new MvsSalesOrderListAdapter(mSalesOrderList,
                                        R.layout.item_mvs_sales_order_card, getApplicationContext());
                                mRecyclerViewSalesOrders.setAdapter(mMvsSalesOrderListAdapter);
                                mMvsSalesOrderListAdapter.notifyDataSetChanged();
                                //mProgressDialog.dismiss();

                               *//* Toast.makeText(MvsSalesOrderListActivity.this, "Successfully Saved.",
                                        Toast.LENGTH_SHORT).show();*//*

                                mCheckedSoList = new ArrayList<SalesOrder>();
                                mIsUpdated = false;

                                if (isNetworkConnected()) {
                                    startSalesInvoiceUpload();
                                    startUploadRunningNos();
                                }

                            } else {
                                mProgressDialog.dismiss();
                                if (mConfirmedCount == 0) {
                                } else {
                                    Toast.makeText(MvsSalesOrderListActivity.this, "Update failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(mApp, "Please select at least one sales invoice to confirm!", Toast.LENGTH_SHORT).show();
                        }
                        mProgressDialog.dismiss();*/

                    } else {
                        Toast.makeText(MvsSalesOrderListActivity.this, "No network available. Please retry.",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    logParams(getResources().getString(R.string.message_exception), e.getMessage());
                    mProgressDialog.dismiss();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private int getCheckedSoCountAndList() {

        DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date _dateobj = new Date();
        int checkedSoCount = 0;

        if (!mSalesOrderList.isEmpty()) {
            for (SalesOrder so : mSalesOrderList) {
                if (so.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted)) &&
                        so.isConfirmedSo() && so.isTransferred() == false) {
                    so.setLastModifiedBy(mApp.getCurrentUserName());
                    so.setLastModifiedDateTime(_dateFormat.format(_dateobj).toString());
                    so.setLastTransferredBy(mApp.getCurrentUserName());
                    so.setLastTransferredDateTime(_dateFormat.format(_dateobj).toString());
                    mCheckedSoList.add(so);

                    checkedSoCount++;
                }
            }
        }
        return checkedSoCount;
    }

    private List<SalesOrder> getModel(boolean status) {
        for (SalesOrder so : mSalesOrderList) {
            if (so.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))) {
                so.setConfirmedSo(status);
            }
        }
        return mSalesOrderList;
    }

    public void confirmSo() {

        //update list status
        updateSoListStatus();

        //update db
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(this);
        dbAdapter.open();

        if (mConfirmedCount > 0) {
            removeZeroQtySolItems(mCheckedSoList);
            mIsUpdated = dbAdapter.saveConfirmSalesOrders(mCheckedSoList);
        }

        dbAdapter.close();

        //si confirmed failed list
        if (mCreditLimitFaildCount > 0 || mOverDueInvoiceCount > 0) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MvsSalesOrderListActivity.this);
            builder1.setTitle("Following sales invoices are not posted");
            builder1.setMessage(mCreditLimitFaildList + mDueDateFaildList);
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

    public void removeZeroQtySolItems(List<SalesOrder> soList) {
        for (SalesOrder so : soList) {
            List<SalesOrderLine> solList
                    = new ArrayList<SalesOrderLine>();

            SalesOrderLineDbHandler solDb
                    = new SalesOrderLineDbHandler(getApplicationContext());

            solDb.open();
            solList = solDb.getAllSalesOrderLinesByDocumentNo(so.getNo());

            if (!solList.isEmpty()) {
                for (SalesOrderLine sol : solList) {
                    float salesQty = sol.getQuantity() + sol.getExchangedQty();

                    if (salesQty == new Float(0)) {
                        if (solDb.deleteSalesOrderLine(sol.getKey())) {
                            Log.d(getResources().getString(R.string.message_deleted_so_line)
                                    , sol.getNo() == null ? "" : sol.getNo());
                        }
                    }
                }
            }
            solDb.close();
        }
    }

    public void updateSoListStatus() {
        mConfirmedCount = 0;
        mCreditLimitFaildCount = 0;
        mCreditLimitFaildList = "";
        mDueDateFaildList = "";
        mOverDueInvoiceCount = 0;

        for (SalesOrder so : mCheckedSoList) {
            if (so.isConfirmedSo()) {
                so.setStatus(getResources().getString(R.string.SalesOrderStatusConfirmed));
                so.setTransferred(false);
                mConfirmedCount++;

                //removed by nalaka 2018-01-25, no such a requirement for MVS
               /* if (validateCusCreditLimit(so.getAmountIncludingVAT()
                        , so.getSelltoCustomerNo()
                        , so.getNo())) {
                    if (validateDueDate(so.getSelltoCustomerNo())) {
                        so.setStatus(getResources().getString(R.string.SalesOrderStatusConfirmed));
                        so.setTransferred(false);
                        mConfirmedCount++;
                    } else {
                        mDueDateFaildList = mDueDateFaildList
                                + "Overdue invoice: "
                                + so.getNo() + '\n';
                        mOverDueInvoiceCount++;
                    }
                } else {
                    mCreditLimitFaildList = mCreditLimitFaildList
                            + "Credit limit exceeded: "
                            + so.getNo() + '\n';
                    mCreditLimitFaildCount++;
                }*/

            } else {
                so.setStatus(getResources().getString(R.string.MVSSalesOrderStatusConverted));
                so.setTransferred(false);
            }
        }

    }

    public boolean validateCusCreditLimit(float grandTotal, String cusCode, String invoiceNo) {
        boolean isNotExceededCusCreditLimit = false;

        double totalAmtInclVat = Double.parseDouble(String.valueOf(grandTotal));

        CustomerDbHandler cusDb = new CustomerDbHandler(getApplicationContext());
        cusDb.open();

        isNotExceededCusCreditLimit =
                cusDb.validateCustomerCreditLimit(cusCode, totalAmtInclVat, invoiceNo);

        cusDb.close();
        return isNotExceededCusCreditLimit;
    }

    public boolean validateDueDate(String cusCode) {
        boolean isValidDueDate = true;
        CustomerDbHandler cusDb = new CustomerDbHandler(getApplicationContext());
        cusDb.open();

        isValidDueDate = cusDb.validateCustomerDueDate(cusCode, true);
        cusDb.close();

        return isValidDueDate;
    }

    private class getMvsSalesOrderListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getSalesOrderList(mFilterDriverCode, mFilterCustomerCode,
                        mFilterCustomerName, mFilterSalesOrderNo, mFilterCreatedDate, mFilterStatus);
            } catch (Exception e) {
                logParams(getResources().getString(R.string.message_exception), e.getMessage());
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            /*mProgressDialog = new ProgressDialog(MvsSalesOrderListActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);*/
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    mMvsSalesOrderListAdapter = new MvsSalesOrderListAdapter(mSalesOrderList,
                            R.layout.item_mvs_sales_order_card, getApplicationContext());
                    mRecyclerViewSalesOrders.setAdapter(mMvsSalesOrderListAdapter);
                    mMvsSalesOrderListAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();
                } catch (Exception e) {
                    logParams(getResources().getString(R.string.message_exception), e.getMessage());
                }
                mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(MvsSalesOrderListActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }
    }

    private void getSalesOrderList(String mFilterDriverCode,
                                   String mFilterCustomerCode,
                                   String mFilterCustomerName,
                                   String mFilterSalesOrderNo,
                                   String mFilterCreatedDate,
                                   String mFilterStatus) {
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(this);
        dbAdapter.open();

        mSalesOrderList = dbAdapter.getSalesOrderListForMVS(mFilterDriverCode, mFilterCustomerCode, mFilterCustomerName,
                mFilterSalesOrderNo, mFilterCreatedDate, mFilterStatus);
        dbAdapter.close();
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
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
        String[] items = new String[]{"", "Pending", "Confirm", "Completed", "Converted", "Void"};
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

                mFilterStatus = setStatus(dropdown.getSelectedItem().toString());

                String date = editFilterCreatedDate.getText().toString();

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date startDate = df.parse(date);
                    mFilterCreatedDate = df.format(startDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                    logParams(getResources().getString(R.string.message_exception), e.getMessage());
                }

                getMvsSalesOrderListTask = new getMvsSalesOrderListTask();
                getMvsSalesOrderListTask.execute((Void) null);
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
                OnClickDeliveryDate(dialog);
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
        int spinnerPosition = adapter.getPosition(getStatus(mFilterStatus));
        dropdown.setSelection(spinnerPosition);

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    public String setStatus(String status) {
        String status_ = "";

        if (status.equals("Pending")) {
            return getResources().getString(R.string.MVSSalesOrderStatusPending);
        } else if (status.equals("Confirm")) {
            return getResources().getString(R.string.MVSSalesOrderStatusConfirmed);
        } else if (status.equals("Completed")) {
            return getResources().getString(R.string.MVSSalesOrderStatusComplete);
        } else if (status.equals("Converted")) {
            return getResources().getString(R.string.MVSSalesOrderStatusConverted);
        } else if (status.equals("Void")) {
            return getResources().getString(R.string.MVSSalesOrderStatusVoid);
        } else {
            status_ = "";
        }

        return status_;
    }

    public String getStatus(String status) {
        String status_ = "";

        if (status.equals(getResources().getString(R.string.MVSSalesOrderStatusPending))) {
            return "Pending";

        } else if (status.equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {
            return "Confirm";

        } else if (status.equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {
            return "Completed";

        } else if (status.equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))) {
            return "Converted";

        } else if (status.equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
            return "Void";

        } else {
            status_ = "";
        }

        return status_;
    }

    private void OnClickDeliveryDate(Dialog dialog) {
        Calendar cl, clNow;
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

    private void startUploadRunningNos() {
        userSetupRunningNoUploadTask = new UserSetupRunningNoUploadTask(getApplicationContext(), true);
        userSetupRunningNoUploadTask.delegate = MvsSalesOrderListActivity.this;
        userSetupRunningNoUploadTask.execute((Void) null);
        //mProgressDialog.setMessage("Updating Running Numbers...");
    }

    private void startSalesInvoiceUpload() {
        salesInvoiceUploadSyncTask = new SalesInvoiceUploadSyncTask(getApplicationContext(), true);
        salesInvoiceUploadSyncTask.delegate = MvsSalesOrderListActivity.this;
        salesInvoiceUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Sales Invoices...");
        mProgressDialog.show();
    }

    public void uploadSoImages() {
        SalesOrderImageUploadSyncTask task = new SalesOrderImageUploadSyncTask(getApplicationContext(), false);
        task.execute();
    }

    private class ConfirmSalesOrderListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (getCheckedSoCountAndList() > 0) {
                    confirmSo();
                }

            } catch (Exception e) {
                Log.d("Exception", e.toString());
                logParams(getResources().getString(R.string.message_exception), e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {

            mProgressDialog.setMessage("Saving...");
            mProgressDialog.show();

            if (getCheckedSoCountAndList() > 0) {

            } else {
                Toast.makeText(mApp, "Please select at least one sales invoice to confirm!", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    if (mIsUpdated) {

                        //mMvsSalesOrderListAdapter = new MvsSalesOrderListAdapter(mSalesOrderList,
                        //R.layout.item_mvs_sales_order_card, getApplicationContext());
                        //mRecyclerViewSalesOrders.setAdapter(mMvsSalesOrderListAdapter);
                        // mMvsSalesOrderListAdapter.notifyDataSetChanged();
                        //mProgressDialog.dismiss();

                               /* Toast.makeText(MvsSalesOrderListActivity.this, "Successfully Saved.",
                                        Toast.LENGTH_SHORT).show();*/

                        mCheckedSoList = new ArrayList<SalesOrder>();
                        mIsUpdated = false;

                        if (isNetworkConnected()) {
                            startSalesInvoiceUpload();
                            startUploadRunningNos();
                            uploadSoImages();
                        }

                    } else {
                        mProgressDialog.dismiss();
                        if (mConfirmedCount == 0) {
                        } else {
                            Toast.makeText(MvsSalesOrderListActivity.this, "Update failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    logParams(getResources().getString(R.string.message_exception), e.getMessage());
                }
                //mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(MvsSalesOrderListActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }
    }

    private void logParams(String type, String params) {
        mLog.info(type + params);
    }

}
