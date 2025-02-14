package com.gui.mdt.thongsieknavclient.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.SalesCustomerPriceListAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesPrices;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesPricesDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesPricesSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.UserSetupRunningNoUploadTask;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MsoCustomerPriceListActivity extends AppCompatActivity implements View.OnClickListener,AsyncResponse {

    private AutoCompleteTextView actvCustomerPriceGroup;
    private EditText txtItemSearch;
    private RecyclerView recyclerViewCustomerPriceList;
    private SalesCustomerPriceListAdapter salesCustomerPriceListAdapter;
    private Toolbar tbCustomerPriceList;
    private Drawable backArrow;
    private String filterSalesPersonCode="",filterDriverCode="", searchText="", searchPriceGroup="", selectedPriceGroup = "";
    private Drawable mBtnSyncDrawableGreen, mBtnSyncDrawableGray;
    private NavClientApp mApp;
    Logger mLog;
    private List<Customer> customerPriceGroupList;
    private ProgressDialog xProgressDialog;
    private List<SalesPrices> salesPricesList;
    private Bundle extras;
    private GetCustomersPriceListTask getCustomersPriceListTask = null;
    private Button btnSearch,mBtnSync;

    AlertDialog.Builder mAlertDialogBuilder;
    AlertDialog mAlertDialog;
    ProgressDialog mProgressDialog;
    String logSyncStatus;
    SalesPricesSyncTask salesPricesSyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mso_customer_price_list);

        mApp = (NavClientApp)getApplicationContext();
        this.mLog= Log4jHelper.getLogger();
        tbCustomerPriceList = (Toolbar) findViewById(R.id.tbCustomerPriceList);
        setSupportActionBar(tbCustomerPriceList);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left).color(Color.WHITE).sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

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
        logSyncStatus = "";
        mProgressDialog = new ProgressDialog(MsoCustomerPriceListActivity.this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle("Sync Data");

        actvCustomerPriceGroup = (AutoCompleteTextView)findViewById(R.id.actvPriceGroup);
        txtItemSearch = (EditText)findViewById(R.id.txtItemSearch);
        recyclerViewCustomerPriceList = (RecyclerView) findViewById(R.id.recyclerViewCustomerPriceList);
        btnSearch = (Button)findViewById(R.id.btnSearch);

        filterSalesPersonCode = mApp.getmCurrentSalesPersonCode();
        filterDriverCode = mApp.getmCurrentDriverCode();
        salesPricesList = new ArrayList<SalesPrices>();

        xProgressDialog = new ProgressDialog(MsoCustomerPriceListActivity.this);
        xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        GetCustomerPriceGroupList(filterSalesPersonCode, filterDriverCode);

        bindactvCustomerPriceGroup();

        txtItemSearch.addTextChangedListener(searchItemTextWatcher());
        actvCustomerPriceGroup.addTextChangedListener(searchPriceGroupTextWatcher());

        recyclerViewCustomerPriceList.setHasFixedSize(true);
        recyclerViewCustomerPriceList.setLayoutManager(new LinearLayoutManager(this));
        salesCustomerPriceListAdapter = new SalesCustomerPriceListAdapter(new ArrayList<SalesPrices>(), extras, R
                .layout.item_sales_customer_price_list_card, getApplicationContext());

        recyclerViewCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);

        actvCustomerPriceGroup.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                bindactvCustomerPriceGroup();
                actvCustomerPriceGroup.showDropDown();
                return false;
            }
        });

        btnSearch.setOnClickListener(this);
        mBtnSync.setOnClickListener(this);

        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //action when dialog is dismissed goes here
                if(salesPricesSyncTask!=null){
                    salesPricesSyncTask.cancel(true);
                    mBtnSync.setBackgroundDrawable(mBtnSyncDrawableGreen);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        onButtonClick(v);
    }

    private void onButtonClick(View v) {
        if (findViewById(R.id.btnSearch) == v) {
            txtItemSearch.setText("");
            selectedPriceGroup = actvCustomerPriceGroup.getText().toString();
            getCustomersPriceListTask = new GetCustomersPriceListTask();
            getCustomersPriceListTask.execute();

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

                                startSalesPriceDownload();
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


    private void GetCustomerPriceGroupList(String salesPersonCode, String driverCode) {
        customerPriceGroupList = new ArrayList<Customer>();

        CustomerDbHandler dbAdapter = new CustomerDbHandler(this);
        dbAdapter.open();
        customerPriceGroupList = dbAdapter.getAllCustomerPriceGroups(salesPersonCode, driverCode);
        dbAdapter.close();
    }

    public void bindactvCustomerPriceGroup()
    {
        ArrayList<String> _customerPriceGroupList = new ArrayList<String>();

        if (!customerPriceGroupList.isEmpty()) {
            for (int i = 0; i < customerPriceGroupList.size(); i++) {
                if(!customerPriceGroupList.get(i).getCustomerPriceGroup().equals(""))
                {
                    _customerPriceGroupList.add(customerPriceGroupList.get(i).getCustomerPriceGroup());
                }
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, _customerPriceGroupList);
        actvCustomerPriceGroup.setAdapter(adapter);
        actvCustomerPriceGroup.setThreshold(0);
    }

    private TextWatcher searchItemTextWatcher()
    {
        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                searchText = txtItemSearch.getText().toString();
                xProgressDialog.setMessage(getResources().getString(R.string.progress_dialog__status_loading));

                if (!searchText.isEmpty()) {

                    xProgressDialog.show();
                    salesCustomerPriceListAdapter = new SalesCustomerPriceListAdapter(searchItem(), extras, R
                            .layout.item_sales_customer_price_list_card, getApplicationContext());
                    recyclerViewCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);
                    salesCustomerPriceListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                }
                else {

                    xProgressDialog.show();
                    salesCustomerPriceListAdapter = new SalesCustomerPriceListAdapter(salesPricesList, extras, R
                            .layout.item_sales_customer_price_list_card, getApplicationContext());
                    recyclerViewCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);
                    salesCustomerPriceListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,  int before, int count) {
            }
        };

        return tw;
    }

    private TextWatcher searchPriceGroupTextWatcher()
    {
        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                searchPriceGroup = actvCustomerPriceGroup.getText().toString();

                if (!searchPriceGroup.isEmpty()) {
                    ArrayAdapter adapter1 = new ArrayAdapter(MsoCustomerPriceListActivity.this, android.R.layout.simple_list_item_1, getPriceGroups());
                    actvCustomerPriceGroup.setAdapter(null);
                    actvCustomerPriceGroup.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();
                    actvCustomerPriceGroup.showDropDown();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,  int before, int count) {
                searchPriceGroup = actvCustomerPriceGroup.getText().toString();

                if (!searchPriceGroup.isEmpty()) {
                    ArrayAdapter adapter1 = new ArrayAdapter(MsoCustomerPriceListActivity.this, android.R.layout.simple_list_item_1, getPriceGroups());
                    actvCustomerPriceGroup.setAdapter(null);
                    actvCustomerPriceGroup.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();
                    actvCustomerPriceGroup.showDropDown();
                }
            }
        };

        return tw;
    }

    public List<String> getPriceGroups()
    {
        List<String> resultPriceGroups = new ArrayList<String>();
        String text = searchPriceGroup.toUpperCase();

        if (customerPriceGroupList != null) {
            for (Customer customer : customerPriceGroupList) {
                if (customer.getCustomerPriceGroup().contains(text)) {
                    resultPriceGroups.add(customer.getCustomerPriceGroup());
                }
            }
        }
        return resultPriceGroups;
    }


    public List<SalesPrices> searchItem()
    {
        List<SalesPrices> resultSalesPriceList_ = new ArrayList<SalesPrices>();
        String text = searchText.toUpperCase();

        if (salesPricesList != null) {
            for (SalesPrices sp : salesPricesList) {
                if (sp.getItemNo().contains(text)) {
                    resultSalesPriceList_.add(sp);
                } else if (sp.getItemDescription().contains(text)) {
                    resultSalesPriceList_.add(sp);
                } else if (sp.getPublishedPrice().startsWith(text)) {
                    resultSalesPriceList_.add(sp);
                }
            }
        }
        return resultSalesPriceList_;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void startSalesPriceDownload() {

        if(deleteAllSalesPrices()) {
            salesPricesSyncTask = new SalesPricesSyncTask(getApplicationContext(), true, false);
            salesPricesSyncTask.delegate = MsoCustomerPriceListActivity.this;
            salesPricesSyncTask.execute((Void) null);
            mProgressDialog.setMessage("Downloading Sale Prices...");
        }
    }

    private boolean deleteAllSalesPrices(){

        SalesPricesDbHandler dbAdapter = new SalesPricesDbHandler(getApplicationContext());
        dbAdapter.open();
        boolean deleted = dbAdapter.deleteAllSalesPrices();
        dbAdapter.close();

        return deleted;
    }

    private class GetCustomersPriceListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getCustomerPriceList(selectedPriceGroup);
            } catch (Exception e) {
                Log.d(getResources().getString(R.string.message_exception), e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            xProgressDialog.setMessage(getResources().getString(R.string.progress_dialog__status_loading));
            xProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    salesCustomerPriceListAdapter = new SalesCustomerPriceListAdapter(salesPricesList, extras, R
                            .layout.item_sales_customer_price_list_card, getApplicationContext());
                    recyclerViewCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);
                    salesCustomerPriceListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                } catch (Exception ex) {
                }
            } else {
            }
        }

        @Override
        protected void onCancelled() {
            xProgressDialog.dismiss();
        }
    }
    private void getCustomerPriceList(String priceGroup_) {
        salesPricesList = new ArrayList<SalesPrices>();

        SalesPricesDbHandler dbAdapter = new SalesPricesDbHandler(this);
        dbAdapter.open();

        salesPricesList = dbAdapter.getAllCustomerPriceListBySalesType(1,priceGroup_);
        dbAdapter.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(salesPricesSyncTask!=null){
            salesPricesSyncTask.cancel(true);
        }
    }

    // this method will auto fire when each sync task complete(onPostExecute).
    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

        //end sales price download
        if (syncStatus.getScope() == getResources().getString(R.string
                .SyncScopeDownLoadSalesPrice)) {
            if (syncStatus.isStatus()) {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-10s %s", "Sales Price Download", "(Done)");
                //updateUserSetupInitialSync();
            } else {
                logSyncStatus = logSyncStatus + '\n' +
                        String.format("%-21s %s", "Sales Price Download", "(Failed)");
            }


            mAlertDialogBuilder = new AlertDialog.Builder(MsoCustomerPriceListActivity.this);
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
}
