package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.SalesCustomerInvoiceAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceResponse;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by BhanukaBandara on 7/11/17.
 */

public class SalesCustomerInvoiceActivity extends AppCompatActivity {

    NavClientApp mApp;
    ApiPostedSalesInvoiceParameter apiPostedSalesInvoiceParameter;
    ApiPostedSalesInvoiceResponse apiPostedSalesInvoiceResponse;
    SalesCustomerInvoiceAdapter salesCustomerInvoiceAdapter;

    Toolbar mToolbar;
    TextView tvCusName;
    SwipyRefreshLayout mSwipyRefreshLayout;
    RecyclerView mCustomerInvoiceRecyclerView;
    String mCustomerNo,mCustomerName;
    String mBookMark;
    private ProgressDialog xProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sales_customer_invoice);

        //tool bar
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer Invoice");
        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        mApp = (NavClientApp) getApplicationContext();
        mCustomerNo = getIntent().getStringExtra("cusNo");
        mBookMark = "";

        tvCusName = (TextView) findViewById(R.id.tvCusName);
        mCustomerName = getIntent().getStringExtra("cusName");

        if (!(mCustomerName == null || mCustomerName == "")) {
            tvCusName.setText(getIntent().getExtras().getString("cusName"));
        }

        mCustomerInvoiceRecyclerView = (RecyclerView) findViewById(R.id.invoice_recycler_view);
        mCustomerInvoiceRecyclerView.setHasFixedSize(true);
        mCustomerInvoiceRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.BOTH) {

                } else {
                    if (direction == SwipyRefreshLayoutDirection.TOP) {
                        mBookMark = ""; //set empty to get first page
                        salesCustomerInvoiceAdapter.clear();
                        getInvoiceList();
                        mSwipyRefreshLayout.setRefreshing(false);
                    } else {
                        getInvoiceList();
                        mSwipyRefreshLayout.setRefreshing(false);
                    }
                }

            }
        });
        // Configure the refreshing colors
        mSwipyRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        salesCustomerInvoiceAdapter = new SalesCustomerInvoiceAdapter(
                new ArrayList<ApiPostedSalesInvoiceResponse.ApiPostedSalesInvoice>(),
                R.layout.item_sales_customer_invoice_card,
                getApplication());
        mCustomerInvoiceRecyclerView.setAdapter(salesCustomerInvoiceAdapter);

        initializeActivity();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initializeActivity() {
        checkConnection();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void checkConnection() {
        if (!isNetworkConnected()) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.notification_title_no_connection))
                    .setMessage(getResources().getString(R.string.notification_msg_no_connection))
                    .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkConnection();
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
        } else {
            Log.d("LOGIN", "PASSED");

            if (mApp.getNavBrokerService() != null) {
                SalesCustomerInvoiceActivity.GetCustomersInvoiceTask cusInvoiceTask =
                        new SalesCustomerInvoiceActivity.GetCustomersInvoiceTask();
                cusInvoiceTask.execute((Void) null);
            }
        }
    }

    private void loadListView(List<ApiPostedSalesInvoiceResponse.ApiPostedSalesInvoice> invoiceList) {

        if (!mBookMark.equals("")) {
            salesCustomerInvoiceAdapter.addAll(invoiceList);
            salesCustomerInvoiceAdapter.notifyDataSetChanged();
        } else {
            salesCustomerInvoiceAdapter = new SalesCustomerInvoiceAdapter(
                    invoiceList,
                    R.layout.item_sales_customer_invoice_card,
                    getApplication());
            mCustomerInvoiceRecyclerView.setAdapter(salesCustomerInvoiceAdapter);
            salesCustomerInvoiceAdapter.notifyDataSetChanged();
        }
    }

    private void getInvoiceList() {
        GetCustomersInvoiceTask invoiceListTask = new GetCustomersInvoiceTask();
        invoiceListTask.execute((Void) null);
    }

    public class GetCustomersInvoiceTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                apiPostedSalesInvoiceParameter = new ApiPostedSalesInvoiceParameter();
                apiPostedSalesInvoiceParameter.setUserName(mApp.getCurrentUserName());
                apiPostedSalesInvoiceParameter.setPassword(mApp.getCurrentUserPassword());
                apiPostedSalesInvoiceParameter.setUserCompany(mApp.getmUserCompany());
                apiPostedSalesInvoiceParameter.setCustomerName("");
                apiPostedSalesInvoiceParameter.setCustomerNo(mCustomerNo);
                apiPostedSalesInvoiceParameter.setBookmarkKey(mBookMark);
                apiPostedSalesInvoiceParameter.setPageSize(10);
                apiPostedSalesInvoiceParameter.setItemCode("");
                apiPostedSalesInvoiceParameter.setItemName("");
                Gson gson = new Gson();
                String json = gson.toJson(apiPostedSalesInvoiceParameter);
                Log.d("INVOICE_PARAMS ", json);

                Call<ApiPostedSalesInvoiceResponse> call = mApp.getNavBrokerService()
                        .GetPostedSalesInvoice(apiPostedSalesInvoiceParameter);

                apiPostedSalesInvoiceResponse = call.execute().body();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            xProgressDialog = new ProgressDialog(SalesCustomerInvoiceActivity.this);
            xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            xProgressDialog.setMessage("Loading...");
            xProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    if( apiPostedSalesInvoiceResponse.getBookMarkKey()!=null) {
                        mBookMark = apiPostedSalesInvoiceResponse.getBookMarkKey();
                    }
                    loadListView(apiPostedSalesInvoiceResponse.getPostedSalesInvoiceListResultData());
                    xProgressDialog.dismiss();
                } catch (Exception ex) {
                    xProgressDialog.dismiss();
                }
            } else {
                Toast.makeText(SalesCustomerInvoiceActivity.this, "No Customer Invoices",Toast.LENGTH_SHORT).show();
                xProgressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
             xProgressDialog.dismiss();
        }
    }
}
