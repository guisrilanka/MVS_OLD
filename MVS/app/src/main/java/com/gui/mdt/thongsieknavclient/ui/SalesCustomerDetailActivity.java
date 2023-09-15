package com.gui.mdt.thongsieknavclient.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.CustomerSyncTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

public class SalesCustomerDetailActivity extends AppCompatActivity implements AsyncResponse {

    private Customer customerObj;

    Toolbar myToolbar;
    Button btnSearch;
    String formName = "",mModule="";
    TextView txtCustomerDetails, txtCustomerCode, txtCustomerName, txtDriverCode, txtSalesPerson,
            txtCreditLimit, txtOutstanding, txtDueDatePeriod, txtPaymentTerms, txtMinimumSaleAmount,
            txtBlockedField, txtCustomerPriceGroup, txtAddress,txtSalesPersonCode, mTxtCusContract, mTxtCusPhoneNo;
    private LinearLayout layoutSalesPerson, layoutDriverCode,layoutOutstanding,layoutCreditlimit,layoutGrace,layoutPaymentTrems,layoutMinimunSaleAmt
            ,layoutBlocked,layoutPriceGroup,layoutSalesPersonCode;
    private NavClientApp mApp;
    CustomerSyncTask customerSyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sales_customer_detail);

        layoutDriverCode = (LinearLayout) findViewById(R.id.layoutDriverCode);
        layoutCreditlimit=(LinearLayout) findViewById(R.id.layoutCreditLimit);
        layoutOutstanding=(LinearLayout) findViewById(R.id.layoutOutstanding);
        layoutGrace=(LinearLayout) findViewById(R.id.layoutGrace);
        layoutPaymentTrems=(LinearLayout) findViewById(R.id.layoutPaymentTerms);
        layoutMinimunSaleAmt=(LinearLayout) findViewById(R.id.layoutMinimumSaleAmt);
        layoutBlocked=(LinearLayout) findViewById(R.id.layoutBlocked);
        layoutPriceGroup=(LinearLayout) findViewById(R.id.layoutPriceGroup);
        layoutSalesPersonCode=(LinearLayout) findViewById(R.id.layoutSalesPersonCode);

        txtCustomerDetails = (TextView) findViewById(R.id.tvCustomerDetails);
        txtCustomerCode = (TextView) findViewById(R.id.txtCustomerCode);
        txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        txtDriverCode = (TextView) findViewById(R.id.txtDriverCode);
        txtCreditLimit = (TextView) findViewById(R.id.txtCreditLimit);
        txtOutstanding = (TextView) findViewById(R.id.txtOutstanding);
        txtDueDatePeriod = (TextView) findViewById(R.id.txtDueDatePeriod);
        txtPaymentTerms = (TextView) findViewById(R.id.txtPaymentTerms);
        txtMinimumSaleAmount = (TextView) findViewById(R.id.txtMinimumSaleAmount);
        txtBlockedField = (TextView) findViewById(R.id.txtBlockedField);
        txtCustomerPriceGroup = (TextView) findViewById(R.id.txtCustomerPriceGroup);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtSalesPersonCode=(TextView) findViewById(R.id.txtSalesPersonCode);

        mTxtCusContract = (TextView) findViewById(R.id.txtCusContract);
        mTxtCusPhoneNo = (TextView) findViewById(R.id.txtCusPhoneNo);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("formName")) {
                formName = getIntent().getStringExtra("formName");
            }
            if (extras.containsKey("_customerDetailObject")) {
                String objAsJson = extras.getString("_customerDetailObject");
                customerObj = Customer.fromJson(objAsJson);
            }

        }

        mApp = (NavClientApp) getApplicationContext();

         mModule=mApp.getmCurrentModule();


        if(mModule.equals(getResources().getString(R.string.module_lds))){
//            layoutDriverCode.setVisibility(View.GONE);
            layoutCreditlimit.setVisibility(View.GONE);
            layoutOutstanding.setVisibility(View.GONE);
            layoutGrace.setVisibility(View.GONE);
            layoutPaymentTrems.setVisibility(View.GONE);
            layoutMinimunSaleAmt.setVisibility(View.GONE);
            layoutBlocked.setVisibility(View.GONE);
            layoutPriceGroup.setVisibility(View.GONE);
//            layoutSalesPersonCode.setVisibility(View.GONE);
        }


        if (formName.equals("LdsSalesCustomer")) {
//            layoutDriverCode.setVisibility(View.GONE);
        }
        if (formName.equals("IsFromLDSSalesInvoice")) {
//            layoutDriverCode.setVisibility(View.GONE);
        }

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Order List");

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        Drawable searchDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_search)
                .color(Color.WHITE)
                .sizeDp(30);
        btnSearch.setBackgroundDrawable(searchDrawable);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formName.equals("LdsSalesCustomer")) {
                    Intent intent = new Intent(SalesCustomerDetailActivity.this, SalesCustomerListActivity.class);
                    intent.putExtra("formName", "LdsSalesCustomer");
                    intent.putExtra("IsPopupNeeded", true);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SalesCustomerDetailActivity.this, SalesCustomerListActivity.class);
                    intent.putExtra("IsPopupNeeded", true);
                    startActivity(intent);
                    finish();
                }
            }
        });
        Drawable drawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_ellipsis_v)
                .color(Color.WHITE)
                .sizeDp(30);
        myToolbar.setOverflowIcon(drawable);

        SetData();
        startCustomerDownload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sales_customer_menu, menu);

        if(mModule.equals(getResources().getString(R.string.module_lds))){
            MenuItem itm_action_cusPriceList, itm_action_cusAr;
            itm_action_cusPriceList = menu.findItem(R.id.action_cusPriceList);
           // itm_action_cusAr = menu.findItem(R.id.action_cusAr);
            itm_action_cusPriceList.setVisible(false);
            //itm_action_cusAr.setVisible(false);
        }
        if (formName.equals("IsFromLDSSalesInvoice")) {
            MenuItem itm_action_cusPriceList, itm_action_cusAr;
            itm_action_cusPriceList = menu.findItem(R.id.action_cusPriceList);
            itm_action_cusAr = menu.findItem(R.id.action_cusAr);
            itm_action_cusPriceList.setVisible(false);
            itm_action_cusAr.setVisible(false);
        }
        if(formName.equals("MvsHome")
                || formName.equals(getResources().getString(R.string.form_name_mvs_sales_order))
                || formName.equals(getResources().getString(R.string.form_name_mvs_stock_Request)))
        {
            MenuItem  itm_action_cusAr;
            itm_action_cusAr = menu.findItem(R.id.action_cusAr);
            itm_action_cusAr.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(customerSyncTask!=null){
            customerSyncTask.cancel(true);
        }
        onBackPressed();
        return true;
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

        txtOutstanding.setText(syncStatus.getMessage());
    }

    private void startCustomerDownload() {

        if(isNetworkConnected()){
            if (this.customerObj.getCode().equals("") || this.customerObj.getCode() == null ) {
                customerSyncTask = new CustomerSyncTask(getApplicationContext(), true,false, this.customerObj.getCode());
                customerSyncTask.delegate = SalesCustomerDetailActivity.this;
                customerSyncTask.execute((Void) null);
            }
        }
        else {
            txtOutstanding.setText("Service Unavailable");
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void SetData() {
        Bundle extras = getIntent().getExtras();

        txtCustomerDetails.setText(customerObj.getCode() + "-" + customerObj.getName());
        txtCustomerCode.setText(customerObj.getCode());
        txtCustomerName.setText(customerObj.getName());
        txtDriverCode.setText(customerObj.getDriverCode());
        txtCreditLimit.setText(String.format("%.2f",customerObj.getCreditLimit()));
        txtOutstanding.setText(String.valueOf(customerObj.getBalance()));
        txtSalesPersonCode.setText(customerObj.getSalespersonCode());
        String dueDate =customerObj.getDueDateGracePeriod();
        if(dueDate=="")
        {
            txtDueDatePeriod.setText("");
        }
        else {
            txtDueDatePeriod.setText(customerObj.getDueDateGracePeriod().replace("D", " DAYS") );
        }
        String paymentTerms = customerObj.getPaymentTerms();
        if (paymentTerms == null)
            txtPaymentTerms.setText(extras.getString(""));
        else
            txtPaymentTerms.setText(paymentTerms);

        txtMinimumSaleAmount.setText(String.format("%.2f",customerObj.getMinimumSalesAmount()));
        if (customerObj.isBlocked())
            txtBlockedField.setText("Blocked");
        else
            txtBlockedField.setText("");
        txtCustomerPriceGroup.setText(customerObj.getCustomerPriceGroup());
        txtAddress.setText(customerObj.getAddress());

        mTxtCusContract.setText(customerObj.getContact());
        mTxtCusPhoneNo.setText(customerObj.getPhoneNo());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_cusAr:
                Intent actioncurAr = new Intent(this, SalesCustomerArActivity.class);
                actioncurAr.putExtra("cusNo", customerObj.getCode());
                actioncurAr.putExtra("cusName", customerObj.getName());
                actioncurAr.putExtra("outstanding", String.valueOf(customerObj.getBalance()));
                this.startActivity(actioncurAr);
                return true;

            case R.id.action_cusInvoice:
                Intent actioncurInv = new Intent(this, SalesCustomerInvoiceActivity.class);
                actioncurInv.putExtra("cusNo", customerObj.getCode());
                actioncurInv.putExtra("cusName", customerObj.getName());
                this.startActivity(actioncurInv);
                return true;

            case R.id.action_cusPriceList:
                Intent actionPriceList = new Intent(this, SalesCustomerPriceList.class);
                actionPriceList.putExtra("cusNo", customerObj.getCode());
                actionPriceList.putExtra("cusName", customerObj.getName());
                this.startActivity(actionPriceList);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
