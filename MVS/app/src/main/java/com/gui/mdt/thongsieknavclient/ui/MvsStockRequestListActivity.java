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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.gui.mdt.thongsieknavclient.adapters.MvsStockRequestListAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequest;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequestLine;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.StockRequestDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockRequestLineDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.StockRequestDownloadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.StockRequestUploadSyncTask;
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

public class MvsStockRequestListActivity extends AppCompatActivity implements android.view.View.OnClickListener,
        AsyncResponse {

    Toolbar myToolbar;
    int date, month, year;
    AlertDialog.Builder mAlertDialogBuilder;
    AlertDialog mAlertDialog;
    StockRequestDownloadSyncTask srDownloadSyncTask;
    private Button btnStockRequestListSearch, mBtnSync;
    private FloatingActionButton fabAddNewStockRequest;
    private Drawable backArrow, searchDrawable, menuDrawable;
    private SwipyRefreshLayout swipyrefreshlayoutStockRequest;
    private RecyclerView recyclerViewStockRequest;
    private MvsStockRequestListAdapter mvsStockRequestListAdapter;
    private List<StockRequest> stockRequestList, confirmStockRequestList;
    private ProgressDialog xProgressDialog;
    private String
            mFilterCustomerCode = "",
            mFilterCustomerName = "",
            mFilterStockRequestNo = "",
            mFilterCreatedDate = "",
            mFilterStatus = "",
            mFilterDriverCode = "",
            mSearchText = "",
            logSyncStatus = "";
    private GetStockRequestListTask getStockRequestListTask = null;

    private NavClientApp mApp;
    private int mConfirmedCount = 0;
    private boolean mIsUpdated = false;
    private StockRequestUploadSyncTask stockRequestUploadSyncTask;
    private UserSetupRunningNoUploadTask userSetupRunningNoUploadTask;
    private EditText mTxtSearch;
    private Drawable mBtnSyncDrawableGreen, mBtnSyncDrawableGray;
    private ProgressDialog mProgressDialog;

    private Logger mLog;

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvs_stock_request_list);

        mApp = (NavClientApp) getApplicationContext();

        //Tool Bar
        backArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left).color(Color.WHITE).sizeDp(30);
        searchDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_search).color(Color.WHITE).sizeDp(30);
        menuDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_ellipsis_v).color(Color.WHITE).sizeDp(30);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        myToolbar.setOverflowIcon(menuDrawable);

        this.mLog = Log4jHelper.getLogger();

        //Search
        btnStockRequestListSearch = (Button) findViewById(R.id.btnSearch);
        btnStockRequestListSearch.setBackgroundDrawable(searchDrawable);
        btnStockRequestListSearch.setOnClickListener(this);

        //force sync
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
        mProgressDialog = new ProgressDialog(MvsStockRequestListActivity.this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle("Sync Data");

        fabAddNewStockRequest = (FloatingActionButton) findViewById(R.id.fabAddNewStockRequest);
        fabAddNewStockRequest.bringToFront();
        fabAddNewStockRequest.setOnClickListener(this);

        mFilterDriverCode = mApp.getmCurrentDriverCode();

        //Swipy
        xProgressDialog = new ProgressDialog(MvsStockRequestListActivity.this);
        xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        swipyrefreshlayoutStockRequest = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayoutStockRequest);
        recyclerViewStockRequest = (RecyclerView) findViewById(R.id.recyclerViewStockRequest);
        recyclerViewStockRequest.setHasFixedSize(true);
        recyclerViewStockRequest.setLayoutManager(new LinearLayoutManager(this));
        swipyrefreshlayoutStockRequest.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    mFilterCustomerName = "";
                    mFilterCustomerCode = "";
                    mFilterStockRequestNo = "";
                    mFilterCreatedDate = "";
                    mFilterStatus = "";

                    getStockRequestListTask = new GetStockRequestListTask();
                    getStockRequestListTask.execute();
                    swipyrefreshlayoutStockRequest.setRefreshing(false);
                }
            }
        });
        swipyrefreshlayoutStockRequest.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mvsStockRequestListAdapter = new MvsStockRequestListAdapter(new ArrayList<StockRequest>(),
                R.layout.item_stock_request_card, getApplicationContext());
        recyclerViewStockRequest.setAdapter(mvsStockRequestListAdapter);

        //on screen search
        mTxtSearch = (EditText) findViewById(R.id.txtSearch);
        mTxtSearch.addTextChangedListener(searchTextWatcher());

        mBtnSync.setVisibility(View.GONE); //on req 2018-02-06
    }

    @Override
    public void onClick(View v) {
        if (findViewById(R.id.fabAddNewStockRequest) == v) {
            Intent intent = new Intent(getApplicationContext(), MvsStockRequestActivity.class);
            intent.putExtra(getResources().getString(R.string.intent_extra_details), getResources().getString(R
                    .string.mvs_new_stock_request));
            startActivity(intent);
        } else if (findViewById(R.id.btnSearch) == v) {
            showSearchDialog();
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

                                startStockRequestDownload();
                                startUploadRunningNos();
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
    protected void onResume() {
        super.onResume();
        getStockRequestListTask = new GetStockRequestListTask();
        getStockRequestListTask.execute();
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {
        if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeUploadSR)) {
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
        } else if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeDownLoadSR)) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Stock Request Download", "(Done)");
                //updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Stock Request Download", "(Failed)");
            }


            mAlertDialogBuilder = new AlertDialog.Builder(MvsStockRequestListActivity.this);
            mAlertDialogBuilder
                    .setTitle("Sync Completed")
                    .setMessage(logSyncStatus)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                            //mLog.info("SYNC_STATUS : " + logSyncStatus);
                            swipyrefreshlayoutStockRequest.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipyrefreshlayoutStockRequest.setRefreshing(true);

                                    mFilterCustomerName = "";
                                    mFilterCustomerCode = "";
                                    mFilterStockRequestNo = "";
                                    mFilterCreatedDate = "";
                                    mFilterStatus = "";
                                    mTxtSearch.setText("");

                                    getStockRequestListTask = new GetStockRequestListTask();
                                    getStockRequestListTask.execute();
                                    swipyrefreshlayoutStockRequest.setRefreshing(false);

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

    private TextWatcher searchTextWatcher() {

        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                mSearchText = mTxtSearch.getText().toString();
                xProgressDialog.setMessage("Loading...");

                if (!mSearchText.isEmpty()) {
                    xProgressDialog.show();

                    mvsStockRequestListAdapter = new MvsStockRequestListAdapter(searchStockRequest(),
                            R.layout.item_stock_request_card, getApplicationContext());
                    recyclerViewStockRequest.setAdapter(mvsStockRequestListAdapter);
                    mvsStockRequestListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                } else {
                    xProgressDialog.show();

                    mvsStockRequestListAdapter = new MvsStockRequestListAdapter(stockRequestList,
                            R.layout.item_stock_request_card, getApplicationContext());
                    recyclerViewStockRequest.setAdapter(mvsStockRequestListAdapter);
                    mvsStockRequestListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
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

    public List<StockRequest> searchStockRequest() {

        List<StockRequest> mTempSRlist = new ArrayList<StockRequest>();

        String text = mSearchText.toUpperCase();

        if (stockRequestList != null) {
            for (StockRequest sr : stockRequestList) {
                if (sr.getNo().contains(text)) {
                    mTempSRlist.add(sr);
                } else if (sr.getSelltoCustomerName().contains(text)) {
                    mTempSRlist.add(sr);
                } else if (sr.getSelltoCustomerNo().contains(text)) {
                    mTempSRlist.add(sr);
                }
            }
        }
        return mTempSRlist;
    }

    private void getStockRequestList(String mFilterDriverCode,
                                     String mFilterCustomerCode,
                                     String mFilterCustomerName,
                                     String mFilterStockRequestNo,
                                     String mFilterCreatedDate,
                                     String mFilterStatus) {

        stockRequestList = new ArrayList<StockRequest>();
        StockRequestDbHandler dbAdapter = new StockRequestDbHandler(this);
        dbAdapter.open();

        stockRequestList = dbAdapter.getStockRequestList(mFilterDriverCode, mFilterCustomerCode, mFilterCustomerName,
                mFilterStockRequestNo, mFilterCreatedDate, mFilterStatus);
        dbAdapter.close();
    }

    private List<StockRequest> upldateStatus(boolean status) {
        for (StockRequest sr : stockRequestList) {
            if (sr.getStatus().equals(getResources().getString(R.string.StockRequestStatusPending))
                    || sr.getStatus().equals(getResources().getString(R.string.StockRequestStatusComplete))) {
                sr.setConfirmedSr(status);
            }

        }
        return stockRequestList;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_selectAll:
                stockRequestList = upldateStatus(true);
                mvsStockRequestListAdapter = new MvsStockRequestListAdapter(stockRequestList,
                        R.layout.item_stock_request_card, getApplicationContext());
                recyclerViewStockRequest.setAdapter(mvsStockRequestListAdapter);
                return true;
            case R.id.action_cancelSelect:
                stockRequestList = upldateStatus(false);
                mvsStockRequestListAdapter = new MvsStockRequestListAdapter(stockRequestList,
                        R.layout.item_stock_request_card, getApplicationContext());
                recyclerViewStockRequest.setAdapter(mvsStockRequestListAdapter);
                return true;
            case R.id.action_confirm:
                try {

                    if (isNetworkConnected()) {

                        ConfirmStockRequestListTask confirmStockRequestListTask =
                                new MvsStockRequestListActivity.ConfirmStockRequestListTask();
                        confirmStockRequestListTask.execute((Void) null);

                        /*getConfirmStockRequests();
                        saveConfirmStockRequestToDb();
                        if (mIsUpdated) {
                            mvsStockRequestListAdapter = new MvsStockRequestListAdapter(stockRequestList,
                                    R.layout.item_stock_request_card, getApplicationContext());
                            recyclerViewStockRequest.setAdapter(mvsStockRequestListAdapter);
                            mvsStockRequestListAdapter.notifyDataSetChanged();

                            Toast.makeText(MvsStockRequestListActivity.this, "Successfully Saved.",
                                    Toast.LENGTH_SHORT).show();

                            confirmStockRequestList = new ArrayList<StockRequest>();
                            mIsUpdated = false;

                            if (isNetworkAvailable()) {
                                startStockRequestUpload();
                                startUploadRunningNos();
                            }

                        } else {
                            //mProgressDialog.dismiss();
                            if (mConfirmedCount == 0) {
                                Toast.makeText(MvsStockRequestListActivity.this, "Please select at least one item to " +
                                                "confirm",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MvsStockRequestListActivity.this, "Update failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }*/
                    } else {
                        Toast.makeText(MvsStockRequestListActivity.this, "No network available. Please retry.",
                                Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception ex) {
                    Toast.makeText(MvsStockRequestListActivity.this, ex.toString(),
                            Toast.LENGTH_SHORT).show();
                    logParams(getResources().getString(R.string.message_exception), ex.getMessage());
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void startStockRequestUpload() {
        stockRequestUploadSyncTask = new StockRequestUploadSyncTask(getApplicationContext(), true);
        stockRequestUploadSyncTask.delegate = MvsStockRequestListActivity.this;
        stockRequestUploadSyncTask.execute((Void) null);
        mProgressDialog.setMessage("Uploading Stock Requests...");
        mProgressDialog.show();
    }

    private void startUploadRunningNos() {
        userSetupRunningNoUploadTask = new UserSetupRunningNoUploadTask(getApplicationContext(), true);
        userSetupRunningNoUploadTask.delegate = MvsStockRequestListActivity.this;
        userSetupRunningNoUploadTask.execute((Void) null);
        //mProgressDialog.setMessage("Updating Running Numbers...");
    }

    private void refreshList() {
        mFilterCustomerName = "";
        mFilterCustomerCode = "";
        mFilterStockRequestNo = "";
        mFilterCreatedDate = "";
        mFilterStatus = "";

        getStockRequestListTask = new GetStockRequestListTask();
        getStockRequestListTask.execute();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getConfirmStockRequests() {
        confirmStockRequestList = new ArrayList<StockRequest>();
        DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date _dateobj = new Date();

        for (StockRequest sr :
                stockRequestList) {

            String pendingStatus = getResources().getString(R.string.StockRequestStatusPending),
                    completeStatus = getResources().getString(R.string.StockRequestStatusComplete);

            if (sr.getStatus().equals(completeStatus)
                    || sr.getStatus().equals(pendingStatus)
                    && sr.getTransferred() == false
                    && sr.isConfirmedSr()) {

                sr.setLastModifiedBy(mApp.getCurrentUserName());
                sr.setLastModifiedDateTime(_dateFormat.format(_dateobj).toString());
                sr.setLastTransferredBy(mApp.getCurrentUserName());
                sr.setLastTransferredDateTime(_dateFormat.format(_dateobj).toString());

                confirmStockRequestList.add(sr);
            }
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void saveConfirmStockRequestToDb() {
        getConfirmedCount(confirmStockRequestList);
        StockRequestDbHandler dbAdapter = new StockRequestDbHandler(this);
        dbAdapter.open();
        if (mConfirmedCount > 0) {
            removeZeroQtySrlItems(confirmStockRequestList);
            mIsUpdated = dbAdapter.saveConfirmStockRequests(confirmStockRequestList);
        }
        dbAdapter.close();
    }

    public void removeZeroQtySrlItems(List<StockRequest> srList) {
        for (StockRequest sr : srList) {
            List<StockRequestLine> srlList
                    = new ArrayList<StockRequestLine>();

            StockRequestLineDbHandler srlDb
                    = new StockRequestLineDbHandler(getApplicationContext());

            srlDb.open();
            srlList = srlDb.getAllSRLinesByDocumentNo(sr.getNo());

            if (!srlList.isEmpty()) {
                for (StockRequestLine srl : srlList) {
                    //changed zero price implementation
//                    if (srl.getQuantity() == 0f || srl.getUnitPrice() == 0f) {
                    if (srl.getQuantity() == 0f) {
                        if (srlDb.deleteStockRequestLineByKey(srl.getKey())) {
                            Log.d("DELETED SRLINE:", srl.getItemNo() == null ? "" : srl.getItemNo());
                        }
                    }
                }
            }
            srlDb.close();
        }
    }

    private void getConfirmedCount(List<StockRequest> confirmedSalesOrderList) {
        for (StockRequest sr : confirmedSalesOrderList) {
            if (sr.isConfirmedSr()) {
                sr.setStatus(getResources().getString(R.string.StockRequestStatusConfirmed));
                sr.setTransferred(false);
                mConfirmedCount++;
            } else {
                //sr.setStatus(getResources().getString(R.string.StockRequestStatusPending));
                sr.setTransferred(false);
            }
        }
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
        dialog.setContentView(R.layout.dialog_stock_request_list_search);
        dialog.setCancelable(false);

        final Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
        String[] items = new String[]{"", "Pending", "Confirm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);

        final EditText editFilterCustomerCode = (EditText) dialog.findViewById(R.id.txtCustomerCode);
        final EditText editFilterCustomerName = (EditText) dialog.findViewById(R.id.txtCustomerName);
        final EditText editFilterStockRequestNo = (EditText) dialog.findViewById(R.id.txtStockRequestNo);
        final TextView editFilterCreatedDate = (TextView) dialog.findViewById(R.id.txtDeliveryDate);

        //2018-02-28 /set default date on search dialog (tomorrow) 
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        mFilterCreatedDate = String.valueOf(calendar.get(Calendar.YEAR))
                + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1)
                + "-" + String.valueOf(calendar.get(Calendar.DATE));
        editFilterCreatedDate.setText(mFilterCreatedDate);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterCustomerCode = editFilterCustomerCode.getText().toString();
                mFilterCustomerName = editFilterCustomerName.getText().toString();
                mFilterStockRequestNo = editFilterStockRequestNo.getText().toString();
                mFilterStatus = dropdown.getSelectedItem().toString();
                String date = editFilterCreatedDate.getText().toString();

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date startDate = df.parse(date);
                    mFilterCreatedDate = df.format(startDate);
                    //isFormSearch=true;

                } catch (ParseException e) {
                    e.printStackTrace();
                    logParams(getResources().getString(R.string.message_exception), e.getMessage());
                }

                getStockRequestListTask = new GetStockRequestListTask();
                getStockRequestListTask.execute();
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
                openDatePicker(dialog);
            }
        });

        /*dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isSearchButtonClicked = false;
            }
        });*/

        editFilterCustomerCode.setText(mFilterCustomerCode);
        editFilterCustomerName.setText(mFilterCustomerName);
        editFilterStockRequestNo.setText(mFilterStockRequestNo);
        editFilterCreatedDate.setText(mFilterCreatedDate);
        int spinnerPosition = adapter.getPosition(mFilterStatus);
        dropdown.setSelection(spinnerPosition);

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams
                .FLAG_ALT_FOCUSABLE_IM);

    }

    private void openDatePicker(Dialog dialog) {
        Calendar cl, clNow;
        int nowYear, nowMonth, nowDay;
        cl = Calendar.getInstance();
        date = cl.get(Calendar.DAY_OF_MONTH);
        month = cl.get(Calendar.MONTH);
        year = cl.get(Calendar.YEAR);
        final TextView txtDeliveryDate;

        clNow = Calendar.getInstance();
        nowYear = clNow.get(Calendar.YEAR);
        nowMonth = clNow.get(Calendar.MONTH);
        nowDay = clNow.get(Calendar.DAY_OF_MONTH);
        txtDeliveryDate = (TextView) dialog.findViewById(R.id.txtDeliveryDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtDeliveryDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, date, month, year);

        datePickerDialog.updateDate(nowYear, nowMonth, nowDay);
        datePickerDialog.show();
    }

    private void startStockRequestDownload() {

        if (deleteAllHqDownloadedStockRequests()) {
            srDownloadSyncTask = new StockRequestDownloadSyncTask(getApplicationContext(), true, false);
            srDownloadSyncTask.delegate = MvsStockRequestListActivity.this;
            srDownloadSyncTask.execute((Void) null);
            mProgressDialog.setMessage("Downloading Stock Requests...");
        }
    }

    private boolean deleteAllHqDownloadedStockRequests() {

        StockRequestDbHandler dbAdapter = new StockRequestDbHandler(getApplicationContext());
        dbAdapter.open();
        boolean deleted = dbAdapter.deleteAllHqDownloadedSStockRequests();
        dbAdapter.close();

        return deleted;
    }

    private class GetStockRequestListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getStockRequestList(mFilterDriverCode, mFilterCustomerCode,
                        mFilterCustomerName, mFilterStockRequestNo, mFilterCreatedDate, mFilterStatus);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
                logParams(getResources().getString(R.string.message_exception), e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            xProgressDialog.setMessage("Loading...");
            xProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    mvsStockRequestListAdapter = new MvsStockRequestListAdapter(stockRequestList,
                            R.layout.item_stock_request_card, getApplicationContext());
                    recyclerViewStockRequest.setAdapter(mvsStockRequestListAdapter);
                    mvsStockRequestListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                } catch (Exception ex) {
                    logParams(getResources().getString(R.string.message_exception), ex.getMessage());
                }
                xProgressDialog.dismiss();
            } else {
                xProgressDialog.dismiss();
                Toast.makeText(MvsStockRequestListActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            xProgressDialog.dismiss();
        }
    }

    private class ConfirmStockRequestListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getConfirmStockRequests();
                saveConfirmStockRequestToDb();

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
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    if (mIsUpdated) {
                        mvsStockRequestListAdapter = new MvsStockRequestListAdapter(stockRequestList,
                                R.layout.item_stock_request_card, getApplicationContext());
                        recyclerViewStockRequest.setAdapter(mvsStockRequestListAdapter);
                        mvsStockRequestListAdapter.notifyDataSetChanged();

                        /*Toast.makeText(MvsStockRequestListActivity.this, "Successfully Saved.",
                                Toast.LENGTH_SHORT).show();*/

                        confirmStockRequestList = new ArrayList<StockRequest>();
                        mIsUpdated = false;

                        if (isNetworkAvailable()) {
                            startStockRequestUpload();
                            startUploadRunningNos();
                        }
                    } else {
                        mProgressDialog.dismiss();
                        if (mConfirmedCount == 0) {
                            Toast.makeText(MvsStockRequestListActivity.this, "Please select at least one item to " +
                                            "confirm",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MvsStockRequestListActivity.this, "Update failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    logParams(getResources().getString(R.string.message_exception), ex.getMessage());
                }

            } else {
                mProgressDialog.dismiss();
                Toast.makeText(MvsStockRequestListActivity.this, "No data", Toast.LENGTH_SHORT).show();
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
