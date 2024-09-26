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
import com.gui.mdt.thongsieknavclient.adapters.SalesCustomerArAdapter;
import com.gui.mdt.thongsieknavclient.model.MsoSalesOrderList;
import com.gui.mdt.thongsieknavclient.model.SalesCustomerArParameter;
import com.gui.mdt.thongsieknavclient.model.SalesCustomerArResponse;
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

public class SalesCustomerArActivity extends AppCompatActivity {

    ArrayList<MsoSalesOrderList> dataModels;
    NavClientApp mApp;
    SalesCustomerArParameter salesCustomerArParameter;
    SalesCustomerArResponse salesCustomerArResponse;
    SalesCustomerArAdapter salesCustomerArAdapter;

    Toolbar myToolbar;
    TextView mCusName, mOutstanding;
    SwipyRefreshLayout mSwipyRefreshLayout;
    RecyclerView mCustomerInvoiceRecyclerView;
    private String customerNo;
    private String bookMark;
    private ProgressDialog xProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sales_customer_ar);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer AR");

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        mApp = (NavClientApp) getApplicationContext();
        customerNo = getIntent().getStringExtra("cusNo");
        bookMark = "";

        mCustomerInvoiceRecyclerView = (RecyclerView) findViewById(R.id.customer_ar_recycler_view);
        mCustomerInvoiceRecyclerView.setHasFixedSize(true);
        mCustomerInvoiceRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.BOTH) {

                } else {
                    if (direction == SwipyRefreshLayoutDirection.TOP) {
                        bookMark = ""; //set empty to get first page
                        salesCustomerArAdapter.clear();
                        getCustomerArList();
                        mSwipyRefreshLayout.setRefreshing(false);
                    } else {
                        getCustomerArList();
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

        salesCustomerArAdapter = new SalesCustomerArAdapter(
                new ArrayList<SalesCustomerArResponse.SalesCustomerAr>(),
                R.layout.item_sales_order_ar_card,
                getApplication());
        mCustomerInvoiceRecyclerView.setAdapter(salesCustomerArAdapter);

        mCusName = (TextView) findViewById(R.id.tvCusName);
        String cusName = getIntent().getStringExtra("cusName");
        if (!(cusName == null || cusName == "")) {
            mCusName.setText(getIntent().getExtras().getString("cusName"));
        }

        mOutstanding = (TextView) findViewById(R.id.tvOutstanding);
        String outstanding = getIntent().getStringExtra("outstanding");
        if (!(outstanding == null || outstanding == "")) {
            mOutstanding.setText("S$ " + getIntent().getExtras().getString("outstanding"));
        }

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
                GetCustomersArTask cusArListTask = new GetCustomersArTask();
                cusArListTask.execute((Void) null);
            }
        }
    }

    private void getCustomerArList() {
        GetCustomersArTask cusArListTask = new GetCustomersArTask();
        cusArListTask.execute((Void) null);
    }

    private void loadListView(List<SalesCustomerArResponse.SalesCustomerAr> customerArList) {
        if (!bookMark.equals("")) {
            salesCustomerArAdapter.addAll(customerArList);
            salesCustomerArAdapter.notifyDataSetChanged();
        } else {
            salesCustomerArAdapter = new SalesCustomerArAdapter(
                    customerArList,
                    R.layout.item_sales_order_ar_card,
                    getApplication());
            mCustomerInvoiceRecyclerView.setAdapter(salesCustomerArAdapter);
            salesCustomerArAdapter.notifyDataSetChanged();
        }
    }

    public class GetCustomersArTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                salesCustomerArParameter = new SalesCustomerArParameter();
                salesCustomerArParameter.setUserName(mApp.getCurrentUserName());
                salesCustomerArParameter.setPassword(mApp.getCurrentUserPassword());
                salesCustomerArParameter.setUserCompany(mApp.getmUserCompany());
                salesCustomerArParameter.setInvoiceNo("");
                salesCustomerArParameter.setCustomerNo(customerNo);
                salesCustomerArParameter.setBookmarkKey(bookMark);
                salesCustomerArParameter.setPageSize("20");
                Gson gson = new Gson();
                String json = gson.toJson(salesCustomerArParameter);
                Log.d("AR_PARAMS ", json);

                Call<SalesCustomerArResponse> call = mApp.getNavBrokerService()
                        .GetSalesCustomerAr(salesCustomerArParameter);

                salesCustomerArResponse = call.execute().body();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            xProgressDialog = new ProgressDialog(SalesCustomerArActivity.this);
            xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            xProgressDialog.setMessage("Loading...");
            xProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    if (salesCustomerArResponse == null) {
                        Toast.makeText(SalesCustomerArActivity.this, getResources().getString(R.string.message_Customer_Ar), Toast.LENGTH_SHORT).show();
                        xProgressDialog.dismiss();
                    } else {
                        if(salesCustomerArResponse.getCustomerLedgerEntryListResultData().size()>0) {
                            bookMark = salesCustomerArResponse.getBookMarkKey();
                        }
                        loadListView(salesCustomerArResponse.getCustomerLedgerEntryListResultData());
                        xProgressDialog.dismiss();
                    }
                } catch (Exception ex) {
                    xProgressDialog.dismiss();
                }
            } else {
                Toast.makeText(SalesCustomerArActivity.this, getResources().getString(R.string.message_Customer_Ar), Toast.LENGTH_SHORT).show();
                xProgressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            xProgressDialog.dismiss();
        }
    }
}
