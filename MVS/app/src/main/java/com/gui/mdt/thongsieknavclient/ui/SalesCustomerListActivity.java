package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.SalesCustomerListAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

public class SalesCustomerListActivity extends AppCompatActivity {

    NavClientApp mApp;
    List<Customer> customerPriceGroupList;
    List<Customer> customers;

    private SalesCustomerListAdapter salesCustomerListAdapter;

    Toolbar myToolbar;
    Button btnSearch;
    boolean IsMso, IsMvs;
    private RecyclerView recyclerViewSalesCustomers;
    private Bundle extras;
    private String filterCustomerName = "",
                    filterCustomerCode = "",
                    filterCustomerPriceGroup = "",
                    filterSalesPersonCode="",
                    formName ="",
                    searchText="";
    private GetCustomersTask getCustomersTask = null;
    private ProgressDialog xProgressDialog;
    SwipyRefreshLayout swipyrefreshlayoutSalesCustomers;
    boolean isSearchButtonClicked = false;
    private EditText txtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_customer_list);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Customer List");

        mApp = (NavClientApp) getApplicationContext();

        filterSalesPersonCode = mApp.getmCurrentSalesPersonCode();
        txtSearch=(EditText)findViewById(R.id.txtSearch);

        extras = getIntent().getExtras();
        IsMso = extras.getBoolean("IsFromMso");
        IsMvs = extras.getBoolean("IsFromMvs");
        formName = getIntent().getStringExtra("formName");

        xProgressDialog = new ProgressDialog(SalesCustomerListActivity.this);
        xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        Drawable searchDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_search)
                .color(Color.WHITE)
                .sizeDp(25);
        btnSearch.setBackgroundDrawable(searchDrawable);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSearchButtonClicked)
                {
                    isSearchButtonClicked = true;
                    showSearchDialog();
                }
            }
        });



        txtSearch.addTextChangedListener(searchTextWatcher());

        recyclerViewSalesCustomers = (RecyclerView) findViewById(R.id.recyclerViewSalesCustomers);
        recyclerViewSalesCustomers.setHasFixedSize(true);
        recyclerViewSalesCustomers.setLayoutManager(new LinearLayoutManager(this));

        swipyrefreshlayoutSalesCustomers = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayoutSalesCustomers);
        swipyrefreshlayoutSalesCustomers.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    filterCustomerName = "";
                    filterCustomerCode = "";
                    filterCustomerPriceGroup = "";
                    txtSearch.setText("");

                    getCustomersTask = new GetCustomersTask();
                    getCustomersTask.execute((Void) null);
                    swipyrefreshlayoutSalesCustomers.setRefreshing(false);
                }
            }
        });
        // Configure the refreshing colors
        swipyrefreshlayoutSalesCustomers.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        salesCustomerListAdapter = new SalesCustomerListAdapter(new ArrayList<Customer>(), extras,
                                    R.layout.item_sales_customer_card,SalesCustomerListActivity.this);
        recyclerViewSalesCustomers.setAdapter(salesCustomerListAdapter);

        getCustomersTask = new GetCustomersTask();
        getCustomersTask.execute((Void) null);

        if (getIntent().getBooleanExtra(getResources().getString(R.string.is_search_pop_up_need), false)) {
            txtSearch.requestFocus();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private TextWatcher searchTextWatcher()
    {

        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                searchText = txtSearch.getText().toString();
                xProgressDialog.setMessage("Loading...");

                if (!searchText.isEmpty()) {
                    xProgressDialog.show();
                    salesCustomerListAdapter = new SalesCustomerListAdapter(searchCustomer(), extras, R
                            .layout.item_sales_customer_card, SalesCustomerListActivity.this);
                    recyclerViewSalesCustomers.setAdapter(salesCustomerListAdapter);
                    salesCustomerListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                }
                else {
                    xProgressDialog.show();
                    salesCustomerListAdapter = new SalesCustomerListAdapter(customers, extras, R
                            .layout.item_sales_customer_card, SalesCustomerListActivity.this);
                    recyclerViewSalesCustomers.setAdapter(salesCustomerListAdapter);
                    salesCustomerListAdapter.notifyDataSetChanged();

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
    public List<Customer> searchCustomer()
    {
        List<Customer> customerSearchResultList = new ArrayList<Customer>();

        String text = searchText.toUpperCase();

        if (customers != null) {
            for (Customer customer : customers) {
                if (customer.getCode().contains(text)) {
                    customerSearchResultList.add(customer);
                }
                else if (customer.getName().contains(text)) {
                    customerSearchResultList.add(customer);
                }
            }
        }
        return customerSearchResultList;
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    //search customer
    private void getCustomerList(String filterCustomerName,
                                 String filterCustomerCode,
                                 String filterCustomerPriceGroup,
                                 String filterSalesPersonCode) {

        CustomerDbHandler dbAdapter = new CustomerDbHandler(this);
        dbAdapter.open();
        Customer  valuePassCustomer=new Customer();
        //add by sajith
        valuePassCustomer.setName(filterCustomerName);
        valuePassCustomer.setCode(filterCustomerCode);
        valuePassCustomer.setCustomerPriceGroup(filterCustomerPriceGroup);
        valuePassCustomer.setSalespersonCode(filterSalesPersonCode);
        customers = dbAdapter.getAllCustomer(valuePassCustomer);
        dbAdapter.close();
    }

    //add by Sajith
    private void getCustomerListForLDSMVS(String filterCustomerName,
                                          String filterCustomerCode,
                                          String filterCustomerPriceGroup) {

        CustomerDbHandler dbAdapter = new CustomerDbHandler(this);
        dbAdapter.open();
        Customer  valuePassCustomer=new Customer();
        //add by sajith
        valuePassCustomer.setName(filterCustomerName);
        valuePassCustomer.setCode(filterCustomerCode);
        valuePassCustomer.setCustomerPriceGroup(filterCustomerPriceGroup);

        valuePassCustomer.setDriverCode(mApp.getmCurrentDriverCode());
        customers = dbAdapter.getAllCustomerListForLdsMvs(valuePassCustomer);
        dbAdapter.close();
    }

    private void getCustomerListForMvsStockRequest(String filterCustomerName,
                                                   String filterCustomerCode,
                                                   String filterCustomerPriceGroup){

        CustomerDbHandler dbAdapter = new CustomerDbHandler(this);
        dbAdapter.open();

        Customer  valuePassCustomer = new Customer();

        valuePassCustomer.setName(filterCustomerName);
        valuePassCustomer.setCode(filterCustomerCode);
        valuePassCustomer.setCustomerPriceGroup(filterCustomerPriceGroup);
        valuePassCustomer.setSalespersonCode(mApp.getmCurrentSalesPersonCode());
        valuePassCustomer.setDriverCode(mApp.getmCurrentDriverCode());

        customers = dbAdapter.getAllCustomerForMVSStockRequest(valuePassCustomer);
        dbAdapter.close();
    }


    private void showSearchDialog() {
        String filterSalesPersonCode = "", filterDriverCode = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                                        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();

        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.getWindow().setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_sales_customer_list_search);
        dialog.setCancelable(false);

        final EditText editFilterCustomerName = (EditText) dialog.findViewById(R.id.editFilterCustomerName);
        final EditText editFilterCustomerCode = (EditText) dialog.findViewById(R.id.editFilterCustomerCode);
        final AutoCompleteTextView actvPriceGroup = (AutoCompleteTextView) dialog.findViewById(R.id.actvPriceGroup);

        //final Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerPriceGroup);
        final TextView lblDialogHeader=(TextView)dialog.findViewById(R.id.lblDialogHeader) ;

        lblDialogHeader.setText("Customer Search");

        filterSalesPersonCode = mApp.getmCurrentSalesPersonCode();
        filterDriverCode = mApp.getmCurrentDriverCode();

        GetCustomerPriceGroupList(filterSalesPersonCode, filterDriverCode);

        ArrayList<String> _customerPriceGroupList = new ArrayList<String>();

        if (!customerPriceGroupList.isEmpty()) {
            for (int i = 0; i < customerPriceGroupList.size(); i++) {
                _customerPriceGroupList.add(customerPriceGroupList.get(i).getCustomerPriceGroup());
            }
        }
        if(_customerPriceGroupList.isEmpty())
        {
            _customerPriceGroupList.add("");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                        android.R.layout.simple_list_item_1, _customerPriceGroupList);
        actvPriceGroup.setAdapter(adapter);
        actvPriceGroup.setThreshold(1);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filterCustomerName = editFilterCustomerName.getText().toString();
                filterCustomerCode = editFilterCustomerCode.getText().toString();
                filterCustomerPriceGroup =  actvPriceGroup.getText().toString();

                txtSearch.setText("");

                getCustomersTask = new GetCustomersTask();
                getCustomersTask.execute((Void) null);
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

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isSearchButtonClicked = false;
            }
        });

        editFilterCustomerName.setText(filterCustomerName);
        editFilterCustomerCode.setText(filterCustomerCode);
        //dropdown.setSelection(getIndex(dropdown, filterCustomerPriceGroup));
        actvPriceGroup.setText(filterCustomerPriceGroup);

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private int getIndex(Spinner spinner, String myString){
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    //get customer price groups
    private void GetCustomerPriceGroupList(String salesPersonCode, String driverCode) {
        CustomerDbHandler dbAdapter = new CustomerDbHandler(this);
        dbAdapter.open();
        customerPriceGroupList = dbAdapter.getAllCustomerPriceGroups(salesPersonCode, driverCode);
        dbAdapter.close();
    }

    //Normal search
    public class GetCustomersTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if(formName.equals("LdsHome") || formName.equals("MvsHome")){
                    getCustomerListForLDSMVS(filterCustomerName, filterCustomerCode, filterCustomerPriceGroup);
                }
                else if(formName.equals(getResources().getString(R.string.form_name_mvs_stock_Request)) || formName.equals(getResources().getString(R.string.form_name_mvs_sales_order))) {
                    getCustomerListForMvsStockRequest(filterCustomerName, filterCustomerCode, filterCustomerPriceGroup);
                }
                else{
                    getCustomerList(filterCustomerName, filterCustomerCode, filterCustomerPriceGroup,
                            filterSalesPersonCode);
                }


            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            xProgressDialog = new ProgressDialog(SalesCustomerListActivity.this);
            xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            xProgressDialog.setMessage("Loading...");
            xProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    //customerList
                    salesCustomerListAdapter = new SalesCustomerListAdapter(customers, extras,
                                    R.layout.item_sales_customer_card, SalesCustomerListActivity.this);
                    recyclerViewSalesCustomers.setAdapter(salesCustomerListAdapter);
                    salesCustomerListAdapter.notifyDataSetChanged();

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
}
