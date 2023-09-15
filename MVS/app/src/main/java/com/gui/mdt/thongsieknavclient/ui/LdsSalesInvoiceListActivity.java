package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.LdsSalesInvoiceListAdapter;
import com.gui.mdt.thongsieknavclient.adapters.MsoSalesOrderListAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.model.MsoSalesOrderList;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.gui.mdt.thongsieknavclient.R.id.btnSearch;

public class LdsSalesInvoiceListActivity extends AppCompatActivity implements android.view.View.OnClickListener{

    //UI related member variables
    Toolbar tb_lds;
    ListView lvLdsSalesOvder;
    private Button btnBack,btnSalesOrderListSearch;
    private FloatingActionButton fabTopUpQuantity;
    int date, month, year;
    SwipyRefreshLayout mSwipyrefreshlayoutSalesInvoices;
    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerViewSalesInvoices;

    //API related member variables
    NavClientApp mApp;

    //Data related member variables
    private static LdsSalesInvoiceListAdapter SalesInvoiceListAdapter;
    ArrayList<MsoSalesOrderList> dataModels;
    List<Customer> customers ;
    List<SalesOrder> salesInvoiceList;
    List<SalesOrder> comfirmedSalesInvoiceList;

    //Task related member variables
    private getSalesInvoiceListTask getSalesInvoiceListTask=null;

    private String mFilterCustomerCode = "", mFilterCustomerName = "", mFilterSalesOrderNo = "",
            mFilterCreatedDate = "", mFilterStatus = "", mFilterDriverCode = "",searchText="";
    private EditText txtSearch;

    Calendar calander;
    SimpleDateFormat simpledateformat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lds_sales_invoice_list);

        txtSearch=(EditText)findViewById(R.id.txtSearch);

        tb_lds = (Toolbar) findViewById(R.id.tb_lds);
        setSupportActionBar(tb_lds);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Order List");

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);


        btnSalesOrderListSearch = (Button)findViewById(btnSearch);
        Drawable searchDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_search)
                .color(Color.WHITE)
                .sizeDp(30);
        btnSalesOrderListSearch.setBackgroundDrawable(searchDrawable);
        btnSalesOrderListSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });

        txtSearch.addTextChangedListener(searchTextWatcher());
//        Drawable drawable = new IconicsDrawable(this)
//                .icon(FontAwesome.Icon.faw_ellipsis_v)
//                .color(Color.WHITE)
//                .sizeDp(30);
//
//        tb_lds.setOverflowIcon(drawable);
        mApp = (NavClientApp) getApplicationContext();

        mFilterDriverCode = mApp.getmCurrentDriverCode();

        mRecyclerViewSalesInvoices=(RecyclerView)findViewById(R.id.recyclerViewSalesInvoices);
        mRecyclerViewSalesInvoices.setHasFixedSize(true);
        mRecyclerViewSalesInvoices.setLayoutManager(new LinearLayoutManager(this));

        mSwipyrefreshlayoutSalesInvoices=(SwipyRefreshLayout)findViewById(R.id.swipyrefreshlayoutSalesInvoices);
        mSwipyrefreshlayoutSalesInvoices.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    mFilterCustomerName = "";
                    mFilterCustomerCode = "";
                    mFilterSalesOrderNo = "";
                    mFilterCreatedDate = "";
                    mFilterStatus = "";
                    txtSearch.setText("");

                    getSalesInvoiceListTask = new getSalesInvoiceListTask();
                    getSalesInvoiceListTask.execute((Void) null);
                    mSwipyrefreshlayoutSalesInvoices.setRefreshing(false);
                }

                }
            });

        mSwipyrefreshlayoutSalesInvoices.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        SalesInvoiceListAdapter=new LdsSalesInvoiceListAdapter(new ArrayList<SalesOrder>(),
                R.layout.item_lds_sales_invoice_card,getApplicationContext());

        mRecyclerViewSalesInvoices.setAdapter(SalesInvoiceListAdapter);

        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("yyyy-MM-dd");


        mFilterCreatedDate=simpledateformat.format(calander.getTime());

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (findViewById(R.id.fabTopUpQuantity) == v) {
            Intent intent = new Intent(getApplication(), LdsSalesInvoiceActivity.class);
            intent.putExtra("customerName","");
            intent.putExtra("deliveryDate","");
            intent.putExtra("listType","newOrder");
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSalesInvoiceListTask = new getSalesInvoiceListTask();
        getSalesInvoiceListTask.execute((Void) null);
    }


    private class getSalesInvoiceListTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getSalesInvoiceList(mFilterDriverCode, mFilterCustomerCode,
                        mFilterCustomerName, mFilterSalesOrderNo, mFilterCreatedDate, mFilterStatus);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(LdsSalesInvoiceListActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    SalesInvoiceListAdapter = new LdsSalesInvoiceListAdapter(salesInvoiceList,
                            R.layout.item_lds_sales_invoice_card, getApplicationContext());
                    mRecyclerViewSalesInvoices.setAdapter(SalesInvoiceListAdapter);
                    SalesInvoiceListAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();
                } catch (Exception ex) {
                }
                mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(LdsSalesInvoiceListActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }

    }


    private void getSalesInvoiceList(String mFilterDriverCode, String mFilterCustomerCode, String mFilterCustomerName,
                                   String mFilterSalesOrderNo, String mFilterCreatedDate, String mFilterStatus) {
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(this);
        dbAdapter.open();

        salesInvoiceList = dbAdapter.getSalesOrderListForLDS(mFilterDriverCode, mFilterCustomerCode, mFilterCustomerName,
                mFilterSalesOrderNo, mFilterCreatedDate, mFilterStatus);
        dbAdapter.close();
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
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.getWindow().setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);

        window.setDimAmount(0.2f);


        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_sales_invoice_list_search);
        dialog.setCancelable(false);



        final EditText editFilterCustomerCode = (EditText) dialog.findViewById(R.id.txtCustomerCode);
        final EditText editFilterCustomerName = (EditText) dialog.findViewById(R.id.txtCustomerName);
        final EditText editFilterSalesInvoiceNo = (EditText) dialog.findViewById(R.id.txtSalesInvoiceNo);
        final TextView editFilterDeliverDate = (TextView) dialog.findViewById(R.id.txtDeliveryDate);




        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterCustomerCode = editFilterCustomerCode.getText().toString();
                mFilterCustomerName = editFilterCustomerName.getText().toString();
                mFilterSalesOrderNo = editFilterSalesInvoiceNo.getText().toString();
                String date = editFilterDeliverDate.getText().toString();

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date startDate = df.parse(date);
                    mFilterCreatedDate = df.format(startDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                getSalesInvoiceListTask = new getSalesInvoiceListTask();
                getSalesInvoiceListTask.execute((Void) null);
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

        btncalender = (FrameLayout) dialog.findViewById(R.id.btncalender);

        btncalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickDeliveryDate(dialog);
            }
        });


        editFilterCustomerCode.setText(mFilterCustomerCode);
        editFilterCustomerName.setText(mFilterCustomerName);
        editFilterSalesInvoiceNo.setText(mFilterSalesOrderNo);
        editFilterDeliverDate.setText(mFilterCreatedDate);

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void OnClickDeliveryDate(Dialog dialog) {
        Calendar _cl, _clNow;
        final int _date,_month,_year,_nowYear, _nowMonth, _nowDay;
        _cl = Calendar.getInstance();
        _date = _cl.get(Calendar.DAY_OF_MONTH);
        _month = _cl.get(Calendar.MONTH);
        _year = _cl.get(Calendar.YEAR);
        final TextView _txtcalender;

        _clNow = Calendar.getInstance();
        _nowYear = _clNow.get(Calendar.YEAR);
        _nowMonth = _clNow.get(Calendar.MONTH);
        _nowDay = _clNow.get(Calendar.DAY_OF_MONTH);
        _txtcalender = (TextView) dialog.findViewById(R.id.txtDeliveryDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                _txtcalender.setText( year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, _date, _month, _year);

        datePickerDialog.updateDate(_nowYear, _nowMonth, _nowDay);
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
                    SalesInvoiceListAdapter = new LdsSalesInvoiceListAdapter(searchSalesInvoice(),
                            R.layout.item_lds_sales_invoice_card, LdsSalesInvoiceListActivity.this);
                    mRecyclerViewSalesInvoices.setAdapter(SalesInvoiceListAdapter);
                    SalesInvoiceListAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();
                }
                else {
                    mProgressDialog.show();
                    SalesInvoiceListAdapter = new LdsSalesInvoiceListAdapter(salesInvoiceList,
                            R.layout.item_lds_sales_invoice_card, LdsSalesInvoiceListActivity.this);
                    mRecyclerViewSalesInvoices.setAdapter(SalesInvoiceListAdapter);
                    SalesInvoiceListAdapter.notifyDataSetChanged();

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

    private List<SalesOrder> searchSalesInvoice() {
        List<SalesOrder> salesOrderSearchResultList = new ArrayList<SalesOrder>();

        String text = searchText.toUpperCase();

        if (salesInvoiceList != null) {
            for (SalesOrder so : salesInvoiceList) {
                if (so.getNo().contains(text)) {
                    salesOrderSearchResultList.add(so);
                }
                else if (so.getSelltoCustomerName().contains(text)) {
                    salesOrderSearchResultList.add(so);
                }
                else if (so.getSelltoCustomerNo().contains(text)) {
                    salesOrderSearchResultList.add(so);
                }
                else if (so.getDocumentDate().contains(text)) {
                    salesOrderSearchResultList.add(so);
                }
            }
        }
        return salesOrderSearchResultList;
    }
}
