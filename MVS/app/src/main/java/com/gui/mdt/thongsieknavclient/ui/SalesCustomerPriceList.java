package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.SalesCustomerPriceListAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesPrices;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesPricesDbHandler;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BhanukaBandara on 7/15/17.
 */


public class SalesCustomerPriceList extends AppCompatActivity implements View.OnClickListener {


    Toolbar myToolbar;
    EditText mItemSearch;
    TextView tvCustomerName;
    List<SalesPrices> customerPriceList;
    private String customerNo, customerName;
    private String commonSearchText = "";
    private RecyclerView recyclerViewSalesCustomerPriceList;
    private SalesCustomerPriceListAdapter salesCustomerPriceListAdapter;
    private Bundle extras;
    private GetCustomersPriceListTask getCustomersPriceListTask = null;
    private ProgressDialog xProgressDialog;
    private ImageButton ibSearch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_customer_price_list);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer Price List");

        customerNo = getIntent().getStringExtra("cusNo");
        customerName = getIntent().getStringExtra("cusName");

        mItemSearch = (EditText) findViewById(R.id.itemSearch);
        tvCustomerName = (TextView) findViewById(R.id.tvcustomerName);
        ibSearch = (ImageButton) findViewById(R.id.ibSearch);

        if (!(customerName == null || customerName == "")) {
            tvCustomerName.setText(customerName);
        }

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonSearchText = mItemSearch.getText().toString();
                if (!commonSearchText.isEmpty()) {
                    xProgressDialog.show();
                    salesCustomerPriceListAdapter = new SalesCustomerPriceListAdapter(searchItemByItemCode(), extras, R
                            .layout.item_sales_customer_price_list_card, getApplicationContext());
                    recyclerViewSalesCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);
                    salesCustomerPriceListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                }
                else {
                    xProgressDialog.show();
                    salesCustomerPriceListAdapter = new SalesCustomerPriceListAdapter(customerPriceList, extras, R
                            .layout.item_sales_customer_price_list_card, getApplicationContext());
                    recyclerViewSalesCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);
                    salesCustomerPriceListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                }
            }
        });

        mItemSearch.addTextChangedListener(searchTextWatcher());

        recyclerViewSalesCustomerPriceList = (RecyclerView) findViewById(R.id.recyclerViewSalesCustomersPriceList);
        recyclerViewSalesCustomerPriceList.setHasFixedSize(true);
        recyclerViewSalesCustomerPriceList.setLayoutManager(new LinearLayoutManager(this));
        salesCustomerPriceListAdapter = new SalesCustomerPriceListAdapter(new ArrayList<SalesPrices>(), extras, R
                .layout.item_sales_customer_price_list_card, getApplicationContext());
        recyclerViewSalesCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewSalesCustomerPriceList.setLayoutManager(mLayoutManager);
        recyclerViewSalesCustomerPriceList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSalesCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);

        getCustomersPriceListTask = new GetCustomersPriceListTask();
        getCustomersPriceListTask.execute((Void) null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onClick(View v) {

    }

    private TextWatcher searchTextWatcher()
    {

        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                commonSearchText = mItemSearch.getText().toString();
                if (!commonSearchText.isEmpty()) {
                    xProgressDialog.show();
                    salesCustomerPriceListAdapter = new SalesCustomerPriceListAdapter(searchItemByItemCode(), extras, R
                            .layout.item_sales_customer_price_list_card, getApplicationContext());
                    recyclerViewSalesCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);
                    salesCustomerPriceListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                }
                else {
                    xProgressDialog.show();
                    salesCustomerPriceListAdapter = new SalesCustomerPriceListAdapter(customerPriceList, extras, R
                            .layout.item_sales_customer_price_list_card, getApplicationContext());
                    recyclerViewSalesCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);
                    salesCustomerPriceListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
               /* if(s.length() != 0) {

                }
                else {
                }*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
               /* if(s.length() != 0) {

                }
                else {

                }*/
            }
        };

        return tw;
    }

    private List<SalesPrices> searchItemByItemCode() {
        List<SalesPrices> sp = new ArrayList<SalesPrices>();

        String searchText = commonSearchText.toUpperCase();

        if (customerPriceList != null) {
            for (SalesPrices price : customerPriceList) {
                if (price.getItemNo().contains(searchText)) {
                    sp.add(price);
                }
                else if (price.getItemDescription().contains(searchText)) {
                    sp.add(price);
                }
                else if (price.getPublishedPrice().startsWith(searchText)) {
                    sp.add(price);
                }
            }
        }

        return sp;
    }

    private void getCustomerPriceList() {

        SalesPricesDbHandler dbAdapter = new SalesPricesDbHandler(this);
        CustomerDbHandler cusDBAdapter = new CustomerDbHandler(this);
        dbAdapter.open();
        cusDBAdapter.open();

        Customer customer = cusDBAdapter.getCustomerByCustomerCode(customerNo);

        customerPriceList = dbAdapter.getCustomerPriceListByCustomer(customer);
        dbAdapter.close();
        cusDBAdapter.close();
    }

    private class GetCustomersPriceListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getCustomerPriceList();
            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            xProgressDialog = new ProgressDialog(SalesCustomerPriceList.this);
            xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            xProgressDialog.setMessage("Loading...");
            xProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    salesCustomerPriceListAdapter = new SalesCustomerPriceListAdapter(customerPriceList, extras, R
                            .layout.item_sales_customer_price_list_card, getApplicationContext());
                    recyclerViewSalesCustomerPriceList.setAdapter(salesCustomerPriceListAdapter);
                    salesCustomerPriceListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                } catch (Exception ex) {
                    xProgressDialog.dismiss();
                }
            } else {
                xProgressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            xProgressDialog.dismiss();
        }
    }
}