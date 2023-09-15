package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.adapters.MvsStockTransferRequestListAdapter;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockTransferHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockTransferHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockTransferLineParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockTransferLineResponse;
import com.gui.mdt.thongsieknavclient.datamodel.StockTransferRequest;
import com.gui.mdt.thongsieknavclient.datamodel.StockTransferRequestLine;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.StockTransferRequestDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockTransferRequestLineDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.UserSetupRunningNoUploadTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

public class MvsStockTransferRequestListActivity extends AppCompatActivity implements View.OnClickListener,
        AsyncResponse {

    private static MvsStockTransferRequestListAdapter stockTransferRequestListAdapter;

    Toolbar myToolbar;
    private FloatingActionButton mfabTopUpAddNewTransfer;
    private Button mbtnSearch;
    SwipyRefreshLayout mSwipyrefreshlayoutStockTransferRequestList;
    private RecyclerView mRecyclerViewStockTransferRequestList;
    private ProgressDialog mProgressDialog;
    TextView tvToolbarTitle;

    NavClientApp mApp;
    private getStockTransferRequestListInTask getStockTransferRequestListTask = null;
    private updateStockTransferRequestInListTask updateStockTransferRequestListTask =null;
    UserSetupRunningNoUploadTask userSetupRunningNoUploadTask;

    List<StockTransferRequest> stockTransferRequestList;
    List<StockTransferRequest> comfirmedStockTransferRequestList;
    List<StockTransferRequestLine> comfirmedStockTransferRequestLineList;
    List<ApiPostMobileStockTransferHeaderParameter> apiPostMobileStockTransferHeaderList;
    List<ApiPostMobileStockTransferLineParameter> apiPostMobileStockTransferLineList;
    ApiPostMobileStockTransferHeaderParameter postMobileStockTransferHeaderParameter;
    ApiPostMobileStockTransferLineParameter postMobileStockTransferLineParameter;
    ApiPostMobileStockTransferHeaderResponse stockTransferHeaderResponse;
    ApiPostMobileStockTransferLineResponse stockTransferLineResponse;

    private boolean mIsUpdated = false;
    int mConfirmedCount = 0;
    private EditText txtSearch;
    private String mFilterStockTransferInNo = "",mFilterStockTransferInDate = "",mFilterStockTransferType = "",mFormName,
            searchText="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvs_stock_transfer_request_list);

        txtSearch=(EditText)findViewById(R.id.txtSearch);

        mfabTopUpAddNewTransfer = (FloatingActionButton) findViewById(R.id.fabTopUpAddNewTransfer);
        mfabTopUpAddNewTransfer.bringToFront();

        mfabTopUpAddNewTransfer.setOnClickListener(this);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvToolbarTitle = (TextView)findViewById(R.id.tvToolbarTitle);
        Bundle extras = getIntent().getExtras();

        mApp = (NavClientApp) getApplicationContext();

        if(extras.containsKey(getResources().getString(R.string.intent_extra_form_name)))
        {
            mFormName = extras.getString(getResources().getString(R.string.intent_extra_form_name));
            if(mFormName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_in)))
            {
                mFilterStockTransferType = getResources().getString(R.string.StockTransferIn);
                getSupportActionBar().setTitle("Stock Transfer In");
                tvToolbarTitle.setText("Stock Transfer In");
            }else if(mFormName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_out)))
            {
                mFilterStockTransferType = getResources().getString(R.string.StockTransferOut);
                getSupportActionBar().setTitle("Stock Transfer Out");
                tvToolbarTitle.setText("Stock Transfer Out");
            }
        }

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        mbtnSearch = (Button) findViewById(R.id.btnSearch);
        Drawable searchDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_search)
                .color(Color.WHITE)
                .sizeDp(30);
        mbtnSearch.setBackgroundDrawable(searchDrawable);
        mbtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });
        Drawable drawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_ellipsis_v)
                .color(Color.WHITE)
                .sizeDp(30);

        myToolbar.setOverflowIcon(drawable);

        txtSearch.addTextChangedListener(searchTextWatcher());

        mRecyclerViewStockTransferRequestList = (RecyclerView) findViewById(R.id.recyclerViewStockTransferRequestListIn);
        mRecyclerViewStockTransferRequestList.setHasFixedSize(true);
        mRecyclerViewStockTransferRequestList.setLayoutManager(new LinearLayoutManager(this));

        mSwipyrefreshlayoutStockTransferRequestList = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayoutStockTransferRequestListIn);
        mSwipyrefreshlayoutStockTransferRequestList.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    mFilterStockTransferInNo = "";
                    mFilterStockTransferInDate = "";
                    txtSearch.setText("");

                    getStockTransferRequestListTask = new getStockTransferRequestListInTask();
                    getStockTransferRequestListTask.execute((Void) null);

                    mSwipyrefreshlayoutStockTransferRequestList.setRefreshing(false);
                }
            }
        });

        mSwipyrefreshlayoutStockTransferRequestList.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        stockTransferRequestListAdapter = new MvsStockTransferRequestListAdapter(new ArrayList<StockTransferRequest>(),
                R.layout.item_stock_transfer_request_list_card, getApplicationContext(),mFormName);
        mRecyclerViewStockTransferRequestList.setAdapter(stockTransferRequestListAdapter);
    }

    //----------------Menu Bar----------------------//
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
        if (findViewById(R.id.fabTopUpAddNewTransfer) == v) {
            if(mFormName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_in))) {
                Intent intent = new Intent(getApplication(), MvsStockTransferRequestActivity.class);
                intent.putExtra(getResources().getString(R.string.list_type), getResources().getString(R.string.mvs_new_stock_transfer));
                intent.putExtra(getResources().getString(R.string.intent_extra_form_name),
                        getResources().getString(R.string.form_name_mvs_stock_transfer_in));
                startActivity(intent);
            }
            else if(mFormName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_out))) {
                Intent intent = new Intent(getApplication(), MvsStockTransferRequestActivity.class);
                intent.putExtra(getResources().getString(R.string.list_type), getResources().getString(R.string.mvs_new_stock_transfer));
                intent.putExtra(getResources().getString(R.string.intent_extra_form_name),
                        getResources().getString(R.string.form_name_mvs_stock_transfer_out));
                startActivity(intent);
            }
        }
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getStockTransferRequestListTask = new getStockTransferRequestListInTask();
        getStockTransferRequestListTask.execute((Void) null);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_selectAll:
                stockTransferRequestList = getModel(true);
                stockTransferRequestListAdapter = new MvsStockTransferRequestListAdapter(stockTransferRequestList,
                        R.layout.item_stock_transfer_request_list_card, getApplicationContext(),mFormName);
                mRecyclerViewStockTransferRequestList.setAdapter(stockTransferRequestListAdapter);
                return true;
            case R.id.action_cancelSelect:
                stockTransferRequestList = getModel(false);
                stockTransferRequestListAdapter = new MvsStockTransferRequestListAdapter(stockTransferRequestList,
                        R.layout.item_stock_transfer_request_list_card, getApplicationContext(),mFormName);
                mRecyclerViewStockTransferRequestList.setAdapter(stockTransferRequestListAdapter);
                return true;
            case R.id.action_confirm:
                updateStockTransferRequestListTask = new updateStockTransferRequestInListTask();
                updateStockTransferRequestListTask.execute((Void) null);
                startUploadRunningNos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startUploadRunningNos() {
        userSetupRunningNoUploadTask = new UserSetupRunningNoUploadTask(getApplicationContext(), true);
        userSetupRunningNoUploadTask.delegate = MvsStockTransferRequestListActivity.this;
        userSetupRunningNoUploadTask.execute((Void) null);
    }

    private List<StockTransferRequest> getModel(boolean status) {
        for (StockTransferRequest st : stockTransferRequestList) {
            st.setConfirmedSt(status);
        }
        return stockTransferRequestList;
    }

    private class getStockTransferRequestListInTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getStockTransferRequestListInList(mFilterStockTransferInNo,mFilterStockTransferInDate,mFilterStockTransferType);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MvsStockTransferRequestListActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    stockTransferRequestListAdapter = new MvsStockTransferRequestListAdapter(stockTransferRequestList,
                            R.layout.item_stock_transfer_request_list_card, getApplicationContext(),mFormName);
                    mRecyclerViewStockTransferRequestList.setAdapter(stockTransferRequestListAdapter);
                    stockTransferRequestListAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();
                } catch (Exception ex) {
                }
                mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(MvsStockTransferRequestListActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }
    }

    private void getStockTransferRequestListInList(String filterStockTransferInNo,String filterStockTransferInDate,String filterStockTransferType) {
        StockTransferRequestDbHandler dbAdapter = new StockTransferRequestDbHandler(this);
        dbAdapter.open();
        stockTransferRequestList = dbAdapter.getStockTransferRequestListInList(filterStockTransferInNo,
                filterStockTransferInDate,filterStockTransferType);
        dbAdapter.close();
    }

    private class updateStockTransferRequestInListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getConfirmStockTransferRequest();

                if (isNetworkAvailable()) {
                    saveStockTransferRequestLive(comfirmedStockTransferRequestList);
                    saveStockTransferRequestLineLive(comfirmedStockTransferRequestLineList);
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MvsStockTransferRequestListActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    saveStockTransferRequestSqlLite();
                    if (mIsUpdated) {
                        stockTransferRequestListAdapter = new MvsStockTransferRequestListAdapter(stockTransferRequestList,
                                R.layout.item_stock_transfer_request_list_card, getApplicationContext(),mFormName);
                        mRecyclerViewStockTransferRequestList.setAdapter(stockTransferRequestListAdapter);
                        stockTransferRequestListAdapter.notifyDataSetChanged();
                        mProgressDialog.dismiss();
                        Toast.makeText(MvsStockTransferRequestListActivity.this, "Successfully Updated.",
                                Toast.LENGTH_SHORT).show();
                        /*if(transfferFailedSalesOrderNo!="")
                        {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MvsStockTransferRequestListActivity.this);
                            alertDialogBuilder.setTitle("Transfer Failed");
                            alertDialogBuilder.setMessage("Failed transfer Sales Orders - " + transfferFailedSalesOrderNo);
                            alertDialogBuilder.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                            *//*Toast.makeText(MsoSalesOrderListActivity.this, "Failed tranfer Sales Orders - " + transfferFailedSalesOrderNo,
                                    Toast.LENGTH_SHORT).show();*//*
                        }*/
                        comfirmedStockTransferRequestList = new ArrayList<>();
                        mIsUpdated = false;
                    } else {
                        mProgressDialog.dismiss();
                        if (mConfirmedCount == 0) {
                            Toast.makeText(MvsStockTransferRequestListActivity.this, "Please select at least one item to confirm",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MvsStockTransferRequestListActivity.this, "Update failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    mProgressDialog.dismiss();
                }
            }
        }
    }

    private void saveStockTransferRequestSqlLite() {
        setStockTransferRequestStatus(comfirmedStockTransferRequestList);
        StockTransferRequestDbHandler dbAdapter = new StockTransferRequestDbHandler(this);
        dbAdapter.open();
        if (mConfirmedCount > 0) {
            mIsUpdated = dbAdapter.saveConfirmStockTransferRequestIn(comfirmedStockTransferRequestList);
        }
        dbAdapter.close();
    }

    private void setStockTransferRequestStatus(List<StockTransferRequest> comfirmedStockTransferRequestList) {
        try {
            mConfirmedCount = 0;
            //transfferFailedSalesOrderNo = "";

            /*if (salesInvoiceHeaderResponse == null) {
                for (SalesOrder so : comfirmedSalesOrderList) {
                    if (so.isConfirmedSo()) {
                        so.setStatus(getResources().getString(R.string.SalesOrderStatusConfirmed));
                        so.setTransferred(false);
                        mConfirmedCount++;
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
            }*/
            for (StockTransferRequest st : comfirmedStockTransferRequestList) {
                if (st.isConfirmedSt()) {
                    st.setStatus(getResources().getString(R.string.StockTransferStatusConfirmed));
                    st.setTransferred("false");
                    mConfirmedCount++;
                } else {
                    st.setStatus(getResources().getString(R.string.StockTransferStatusPending));
                    st.setTransferred("false");
                }
            }
        } catch (Exception e) {
        }
    }

    private void saveStockTransferRequestLineLive(List<StockTransferRequestLine> comfirmedStockTransferRequestLineList) {
        apiPostMobileStockTransferLineList = new ArrayList<ApiPostMobileStockTransferLineParameter>();

        comfirmedStockTransferRequestLineList = new ArrayList<>();
        try {
            for (StockTransferRequest confirmedStockTransfer : comfirmedStockTransferRequestList) {
                StockTransferRequestLineDbHandler dbAdapter = new StockTransferRequestLineDbHandler(this);
                dbAdapter.open();
                comfirmedStockTransferRequestLineList = dbAdapter.getAllTransferRequestInLinesByNo(confirmedStockTransfer.getNo());
                dbAdapter.close();

                for (StockTransferRequestLine stLine : comfirmedStockTransferRequestLineList) {

                    String transferred = "";
                    postMobileStockTransferLineParameter = new ApiPostMobileStockTransferLineParameter();

                    postMobileStockTransferLineParameter.setUserName(mApp.getCurrentUserName());
                    postMobileStockTransferLineParameter.setPassword(mApp.getCurrentUserPassword());
                    postMobileStockTransferLineParameter.setUserCompany(mApp.getmUserCompany());
                    postMobileStockTransferLineParameter.setKey(stLine.getKey());
                    postMobileStockTransferLineParameter.setDocument_No(confirmedStockTransfer.getNo());
                    postMobileStockTransferLineParameter.setItem_Number(stLine.getItemNo());
                    postMobileStockTransferLineParameter.setUOM_number("");
                    postMobileStockTransferLineParameter.setQuantity(String.valueOf(stLine.getQuantity()));
                    postMobileStockTransferLineParameter.setDriver_Code(stLine.getDriverCode());
                    postMobileStockTransferLineParameter.setCreated_DateTime("");
                    postMobileStockTransferLineParameter.setLast_Modified_DateTime(confirmedStockTransfer.getLastModifiedDateTime());

                    if (confirmedStockTransfer.getTransferred()=="true")
                        transferred = "1";
                    else
                        transferred = "0";

                    postMobileStockTransferLineParameter.setTransferred(transferred);
                    postMobileStockTransferLineParameter.setLast_Transferred_By(confirmedStockTransfer.getLastTransferredBy());
                    postMobileStockTransferLineParameter.setLast_Transferred_DateTime(confirmedStockTransfer.getLastTransferredDateTime());

                    apiPostMobileStockTransferLineList.add(postMobileStockTransferLineParameter);
                }
            }

            Gson gson = new Gson();
            String json = gson.toJson(apiPostMobileStockTransferLineList);
            Log.d("SYNC_SO_LINE_UP_PARAMS", json);

            Call<ApiPostMobileStockTransferLineResponse> call = mApp.getNavBrokerService()
                    .UpdateStockTransferLineDetails(apiPostMobileStockTransferLineList);

            stockTransferLineResponse = call.execute().body();
        } catch (Exception e) {
        }
    }

    private void saveStockTransferRequestLive(List<StockTransferRequest> comfirmedStockTransferRequestList) {
        apiPostMobileStockTransferHeaderList = new ArrayList<ApiPostMobileStockTransferHeaderParameter>();
        try {
            for (StockTransferRequest confirmedTransfers : comfirmedStockTransferRequestList) {

                postMobileStockTransferHeaderParameter = new ApiPostMobileStockTransferHeaderParameter();

                postMobileStockTransferHeaderParameter.setKey(confirmedTransfers.getKey());
                postMobileStockTransferHeaderParameter.setDocument_No(confirmedTransfers.getNo());
                postMobileStockTransferHeaderParameter.setStock_Transfer_Date(confirmedTransfers.getStockTransferDate());
                postMobileStockTransferHeaderParameter.setTotal_Qty(String.valueOf(confirmedTransfers.getTotalQuantity()));
                postMobileStockTransferHeaderParameter.setNo_of_Items(confirmedTransfers.getNoOfItems());
                postMobileStockTransferHeaderParameter.setDriver_Code(confirmedTransfers.getDriverCode());
                postMobileStockTransferHeaderParameter.setStock_Transfer_No(confirmedTransfers.getNo());
                postMobileStockTransferHeaderParameter.setDriver_Code(confirmedTransfers.getDriverCode());
                postMobileStockTransferHeaderParameter.setCreated_By("");
                postMobileStockTransferHeaderParameter.setCreated_DateTime("");
                postMobileStockTransferHeaderParameter.setLast_Modified_By("");
                postMobileStockTransferHeaderParameter.setLast_Modified_DateTime("");
                postMobileStockTransferHeaderParameter.setLast_Transferred_By("");
                postMobileStockTransferHeaderParameter.setLast_Transferred_DateTime("");
                postMobileStockTransferHeaderParameter.setUserName(mApp.getCurrentUserName());
                postMobileStockTransferHeaderParameter.setPassword(mApp.getCurrentUserPassword());
                postMobileStockTransferHeaderParameter.setUserCompany(mApp.getmUserCompany());

                apiPostMobileStockTransferHeaderList.add(postMobileStockTransferHeaderParameter);
            }

            Gson gson = new Gson();
            String json = gson.toJson(apiPostMobileStockTransferHeaderList);
            Log.d("SYNC_ST_UP_PARAMS", json);

            Call<ApiPostMobileStockTransferHeaderResponse> call = mApp.getNavBrokerService()
                    .UpdateStockTransferHeaderDetails(apiPostMobileStockTransferHeaderList);

            stockTransferHeaderResponse = call.execute().body();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getConfirmStockTransferRequest() {
        comfirmedStockTransferRequestList = new ArrayList<>();
        DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date _dateObj = new Date();

        for (StockTransferRequest st :
                stockTransferRequestList) {
            String status = getResources().getString(R.string.StockTransferStatusPending);
            if (st.getStatus().equals(status) && st.getTransferred().equals("false") && st.isConfirmedSt()) {
                st.setLastModifiedBy(mApp.getCurrentUserName());
                st.setLastModifiedDateTime(_dateFormat.format(_dateObj).toString());
                st.setLastTransferredBy(mApp.getCurrentUserName());
                st.setLastTransferredDateTime(_dateFormat.format(_dateObj).toString());

                comfirmedStockTransferRequestList.add(st);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    private void showSearchDialog() {
        FrameLayout btncalender;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();;
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow()
                .setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);

        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_stock_transfer_request_search);
        dialog.setCancelable(false);

        final EditText editFilterStockTransferInNo = (EditText) dialog.findViewById(R.id.txtStockTransferNo);
        final TextView editFilterStockTransferInDate = (TextView) dialog.findViewById(R.id.txtStockTransferDate);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterStockTransferInNo = editFilterStockTransferInNo.getText().toString();
                String date = editFilterStockTransferInDate.getText().toString();

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date startDate = df.parse(date);
                    mFilterStockTransferInDate = df.format(startDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                getStockTransferRequestListTask = new getStockTransferRequestListInTask();
                getStockTransferRequestListTask.execute((Void) null);
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

        btncalender = (FrameLayout) dialog.findViewById(R.id.btnCalender);

        btncalender.setOnClickListener(new View.OnClickListener() {
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
            txtcalender = (TextView) dialog.findViewById(R.id.txtStockTransferDate);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    txtcalender.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                }
            }, date, month, year);

            datePickerDialog.updateDate(nowYear, nowMonth, nowDay);
            datePickerDialog.show();
    }

    private TextWatcher searchTextWatcher()
    {
        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                searchText = txtSearch.getText().toString();
                mProgressDialog.setMessage("Loading...");

                if (!searchText.isEmpty()) {
                    mProgressDialog.show();
                    stockTransferRequestListAdapter = new MvsStockTransferRequestListAdapter(searchStockTransferRequest(),
                            R.layout.item_stock_transfer_request_list_card, MvsStockTransferRequestListActivity.this,mFormName);
                    mRecyclerViewStockTransferRequestList.setAdapter(stockTransferRequestListAdapter);
                    stockTransferRequestListAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();
                }
                else {
                    mProgressDialog.show();
                    stockTransferRequestListAdapter = new MvsStockTransferRequestListAdapter(stockTransferRequestList,
                            R.layout.item_stock_transfer_request_list_card, MvsStockTransferRequestListActivity.this,mFormName);
                    mRecyclerViewStockTransferRequestList.setAdapter(stockTransferRequestListAdapter);
                    stockTransferRequestListAdapter.notifyDataSetChanged();

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

    public List<StockTransferRequest> searchStockTransferRequest()
    {
        List<StockTransferRequest> stockTransferSearchResultList = new ArrayList<StockTransferRequest>();

        String text = searchText.toUpperCase();

        if (stockTransferRequestList != null) {
            for (StockTransferRequest str : stockTransferRequestList) {
                if (str.getNo().contains(text)) {
                    stockTransferSearchResultList.add(str);
                }
                else if (str.getDocumentDate().contains(text)) {
                    stockTransferSearchResultList.add(str);
                }
            }
        }
        return stockTransferSearchResultList;
    }
}
