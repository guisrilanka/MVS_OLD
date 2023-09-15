package com.gui.mdt.thongsieknavclient.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.SalesCustomerArAdapter;
import com.gui.mdt.thongsieknavclient.adapters.SalesCustomerInvoiceItemAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceItemParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceItemResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceResponse;
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

public class SalesCustomerInvoiceDetail extends AppCompatActivity {

    private ApiPostedSalesInvoiceResponse.ApiPostedSalesInvoice tempSalesOrderInvoice;
    NavClientApp mApp;
    ApiPostedSalesInvoiceItemParameter apiPostedSalesInvoiceItemParameter;
    ApiPostedSalesInvoiceItemResponse apiPostedSalesInvoiceItemResponse;
    SalesCustomerInvoiceItemAdapter salesCustomerInvoiceItemAdapter;

    Toolbar myToolbar;
    TextView mToolbarTitle;
    private ProgressDialog xProgressDialog;
    RecyclerView mCustomerInvoiceRecyclerView;

    String mBookMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_customer_invoice_detail);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);

        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        mApp = (NavClientApp) getApplicationContext();
        mBookMark = "";

        Bundle extras = getIntent().getExtras();

        mToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        if (extras.containsKey("cusSalesInvoiceObj")) {

            String objAsJson = extras.getString("cusSalesInvoiceObj");
            tempSalesOrderInvoice = ApiPostedSalesInvoiceResponse.ApiPostedSalesInvoice.fromJson(objAsJson);

            mToolbarTitle.setText(tempSalesOrderInvoice.getNo());
        }

        mCustomerInvoiceRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewSalesCustomersInviceItemList);
        mCustomerInvoiceRecyclerView.setHasFixedSize(true);
        mCustomerInvoiceRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        salesCustomerInvoiceItemAdapter = new SalesCustomerInvoiceItemAdapter(
                new ArrayList<ApiPostedSalesInvoiceItemResponse.ApiPostedSalesInvoiceItem>(),
                R.layout.item_customer_invoice_item_card,
                getApplication());
        mCustomerInvoiceRecyclerView.setAdapter(salesCustomerInvoiceItemAdapter);

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
                SalesCustomerInvoiceDetail.GetInvoiceItemListTask invoiceItemListTask =
                        new SalesCustomerInvoiceDetail.GetInvoiceItemListTask();
                invoiceItemListTask.execute((Void) null);
            }
        }
    }

    private class GetInvoiceItemListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                apiPostedSalesInvoiceItemParameter = new ApiPostedSalesInvoiceItemParameter();
                apiPostedSalesInvoiceItemParameter.setInvoiceNo(tempSalesOrderInvoice.getNo());
                apiPostedSalesInvoiceItemParameter.setPageIndex("1");
                apiPostedSalesInvoiceItemParameter.setPageSize("10");
                apiPostedSalesInvoiceItemParameter.setUserName(mApp.getCurrentUserName());
                apiPostedSalesInvoiceItemParameter.setPassword(mApp.getCurrentUserPassword());
                apiPostedSalesInvoiceItemParameter.setUserCompany(mApp.getmUserCompany());

                Call<ApiPostedSalesInvoiceItemResponse> call = mApp.getNavBrokerService()
                        .GetPostedSalesInvoiceLine(apiPostedSalesInvoiceItemParameter);

                apiPostedSalesInvoiceItemResponse = call.execute().body();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            xProgressDialog = new ProgressDialog(SalesCustomerInvoiceDetail.this);
            xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            xProgressDialog.setMessage("Loading...");
            xProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    loadListView(apiPostedSalesInvoiceItemResponse.getPostedSalesInvoiceLineListResultData());
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

    private void loadListView(List<ApiPostedSalesInvoiceItemResponse.ApiPostedSalesInvoiceItem> invoiceItemList) {
        if(invoiceItemList.size()>0) {
            salesCustomerInvoiceItemAdapter = new SalesCustomerInvoiceItemAdapter(
                    invoiceItemList,
                    R.layout.item_customer_invoice_item_card,
                    getApplication());
            mCustomerInvoiceRecyclerView.setAdapter(salesCustomerInvoiceItemAdapter);
            salesCustomerInvoiceItemAdapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }
}
