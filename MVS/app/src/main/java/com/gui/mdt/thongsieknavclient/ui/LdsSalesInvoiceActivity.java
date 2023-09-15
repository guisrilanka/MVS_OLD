package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MsoSalesOrderAdapter;
import com.gui.mdt.thongsieknavclient.model.MsoSalesOrder;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

public class LdsSalesInvoiceActivity extends AppCompatActivity implements View.OnClickListener{

    private static MsoSalesOrderAdapter adapter;
    String customerName, deliveryDate, listType;
    TextView tvCustomer, tvDeliveryDate,tvToolbarTitle;
    Toolbar tb_lds;
    ArrayList<MsoSalesOrder> dataModels;
    ListView lvSalesOrderDetail;
    Button btnSearch, btnItem, btnSummary, btnSearchCus, btnDtPicker,btnContactCus,btnAddressCus;
    LinearLayout llItem, llSummary;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lds_sales_invoice);
        tvCustomer = (TextView) findViewById(R.id.txtCustomer);
        tvDeliveryDate = (TextView) findViewById(R.id.txtDeliveryDate);

        Bundle extras = getIntent().getExtras();

        customerName = extras.getString("customerName");
        deliveryDate = extras.getString("deliveryDate");
        listType = extras.getString("listType");
        /*customerName = "Joho Doa";
        deliveryDate = "09/07/2017";*/

        tvCustomer.setText(customerName);
        tvDeliveryDate.setText(deliveryDate);

       /* Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        tb_lds = (Toolbar) findViewById(R.id.tb_lds);
        setSupportActionBar(tb_lds);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Order List");

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearchCus = (Button) findViewById(R.id.btnSearchCus);
        btnDtPicker = (Button) findViewById(R.id.btnDtpicker);
        btnContactCus=(Button)findViewById(R.id.btnContactCus);
        btnAddressCus=(Button)findViewById(R.id.btnAddressCus);
        Drawable searchDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_search)
                .color(Color.WHITE)
                .sizeDp(30);
        Drawable cusSearchDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_search)
                .color(getResources().getColor(R.color.colorPrimary))
                .sizeDp(25);
        Drawable dtPickerDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_calendar)
                .color(getResources().getColor(R.color.colorPrimary))
                .sizeDp(25);
        Drawable contactCusDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_phone)
                .color(getResources().getColor(R.color.colorPrimary))
                .sizeDp(25);
        Drawable addressCusDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_location_arrow)
                .color(getResources().getColor(R.color.colorPrimary))
                .sizeDp(25);
        btnSearch.setBackgroundDrawable(searchDrawable);
        btnSearchCus.setBackgroundDrawable(cusSearchDrawable);
        btnDtPicker.setBackgroundDrawable(dtPickerDrawable);
        btnContactCus.setBackgroundDrawable(contactCusDrawable);
        btnAddressCus.setBackgroundDrawable(addressCusDrawable);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });
        Drawable drawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_ellipsis_v)
                .color(Color.WHITE)
                .sizeDp(30);
        tb_lds.setOverflowIcon(drawable);

        btnItem = (Button) findViewById(R.id.btnItem);
        btnSummary = (Button) findViewById(R.id.btnSummary);
        btnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemTab();
            }
        });
        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSummaryTab();
            }
        });
        llItem = (LinearLayout) findViewById(R.id.llItem);
        llSummary = (LinearLayout) findViewById(R.id.llSummary);
        fab = (FloatingActionButton) findViewById(R.id.fabTopUpQuantity);
        fab.bringToFront();
        fab.setOnClickListener(this);
        setSummaryTab();

        btnSearchCus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                showCustomerSearchDialog();
            }
        });

        lvSalesOrderDetail = (ListView) findViewById(R.id.lvSalesOrderDetail);

        dataModels = new ArrayList<>();

        if(listType.equals("newOrder"))
        {

        }
        else
        {
            dataModels.add(new MsoSalesOrder("DV5700", "25.00", "1", "2", "50.00", "Fish Balls     鱼丸"));
            dataModels.add(new MsoSalesOrder("DV5704", "10.00", "1", "3", "30.00", "Mini Chikuwa   迷你"));
            dataModels.add(new MsoSalesOrder("DV5706", "5.00", "1", "7", "35.00", "Cuttlefish Balls    墨鱼丸"));
            dataModels.add(new MsoSalesOrder("DV5712", "2.00", "1", "5", "10.00", "Fish Ball Seafood Fill  鱼球海鲜补"));
            dataModels.add(new MsoSalesOrder("DV5714", "8.00", "1", "7", "56.00", "Vegetable Fish Balls    蔬菜鱼丸"));
            dataModels.add(new MsoSalesOrder("DV5718", "3.00", "1", "12", "36.00", "Fish Cake Rectangle     鱼糕的矩形"));
            dataModels.add(new MsoSalesOrder("DV5722", "10.00", "1", "2", "20.00", "Black Pepp.Tofu Fishroll    黑胡椒豆腐"));
            dataModels.add(new MsoSalesOrder("DV5724", "12.00", "1", "2", "24.00", "Hot Pot Mix     热锅混合"));
            dataModels.add(new MsoSalesOrder("DV5716", "12.00", "1", "1", "12.00", "Tofu Fish Cake  豆腐鱼饼"));
            dataModels.add(new MsoSalesOrder("DV5700", "5.00", "1", "2", "10.00", "Fish Balls   鱼丸"));
            dataModels.add(new MsoSalesOrder("DV5704", "2.00", "1", "10", "20.00", "Mini Chikuwa    迷你"));
            dataModels.add(new MsoSalesOrder("DV5706", "6.00", "1", "7", "42.00", "Cuttlefish Balls    墨鱼丸"));
            dataModels.add(new MsoSalesOrder("DV5712", "8.00", "1", "5", "40.00", "Fish Ball Seafood Fill  鱼球海鲜补"));
            dataModels.add(new MsoSalesOrder("DV5714", "2.00", "1", "7", "14.00", "Vegetable Fish Balls    蔬菜鱼丸"));
            dataModels.add(new MsoSalesOrder("DV5718", "5.00", "1", "12", "60.00", "Fish Cake Rectangle     鱼糕的矩形"));
            dataModels.add(new MsoSalesOrder("DV5704", "2.00", "1", "10", "20.00", "Mini Chikuwa    迷你"));
            dataModels.add(new MsoSalesOrder("DV5706", "6.00", "1", "7", "42.00", "Cuttlefish Balls    墨鱼丸"));
            dataModels.add(new MsoSalesOrder("DV5712", "8.00", "1", "5", "40.00", "Fish Ball Seafood Fill  鱼球海鲜补"));
            dataModels.add(new MsoSalesOrder("DV5714", "2.00", "1", "7", "14.00", "Vegetable Fish Balls    蔬菜鱼丸"));
            dataModels.add(new MsoSalesOrder("DV5718", "5.00", "1", "12", "60.00", "Fish Cake Rectangle     鱼糕的矩形"));
        }



        /*adapter = new MsoSalesOrderAdapter(dataModels, getApplication());

        lvSalesOrderDetail.setAdapter(adapter);
        lvSalesOrderDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                *//*MsoSalesOrder dataModel = dataModels.get(position);
                Intent intent = new Intent(getApplication(), MsoSalesOrderItemActivity.class);
                intent.putExtra("b_itemNo", dataModel.getItemNo());
                intent.putExtra("itemDescroption", dataModel.getItemDescroption());
                intent.putExtra("uom", dataModel.getUom());
                intent.putExtra("price", dataModel.getPrice());
                intent.putExtra("QTY", dataModel.getQTY());
                intent.putExtra("total", dataModel.getTotal());
                startActivity(intent);*//*
            }
        });*/
    }
    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }
    private void showCustomerSearchDialog() {
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
        dialog.setContentView(R.layout.dialog_sales_customer_list_search);
        dialog.setCancelable(false);

        Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
        String[] items = new String[]{"Group A", "Group B", "Group C"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        dialog.show();
    }


    private void showSearchDialog() {

        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_sales_order_list_search);
        dialog.setCancelable(false);

        Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
        String[] items = new String[]{"Pending", "Completed", "Confirm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        dialog.show();
    }

    //----------------Menu Bar----------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lds_sales_invoice_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_cusInfo:
                Intent actioncusInfo = new Intent(this, SalesCustomerDetailActivity.class);
                actioncusInfo.putExtra("cusName",tvCustomer.getText());
                actioncusInfo.putExtra("formName","IsFromLDSSalesInvoice");
                this.startActivity(actioncusInfo);
                return true;

            case R.id.action_takePicture:
                /*Intent actioncurInv = new Intent(this, SalesCustomerInvoiceActivity.class);
                actioncurInv.putExtra("cusName",tvCustomer.getText());
                this.startActivity(actioncurInv);*/
                return true;

            case R.id.action_collectPayment:
                Intent intentLdsPayment = new Intent(this, LdsPaymentListActivity.class);
                this.startActivity(intentLdsPayment);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    private void setItemTab() {
        btnItem.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnSummary.setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
        llItem.setVisibility(View.VISIBLE);
        llSummary.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);
    }

    private void setSummaryTab() {
        btnSummary.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnItem.setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
        llSummary.setVisibility(View.VISIBLE);
        llItem.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (findViewById(R.id.fabTopUpQuantity) == v) {
            Intent intent = new Intent(getApplication(), SalesItemSearchActivity.class);
            intent.putExtra("IsPopupNeeded", true);
            intent.putExtra("IsMso", true);
            startActivity(intent);
        }
    }
}
