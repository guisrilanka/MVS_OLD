package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MsoSalesOrderListAdapter;
import com.gui.mdt.thongsieknavclient.adapters.MvsSalesInvoicelistAdapter;
import com.gui.mdt.thongsieknavclient.model.MsoSalesOrderList;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

public class MvsSalesInvoiceListActivity extends AppCompatActivity {
    Toolbar myToolbar;
    ArrayList<MsoSalesOrderList> dataModels;
    ListView listView1;
    private static MvsSalesInvoicelistAdapter adapter;
    private Button btnBack,btnSalesOrderListSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvs_sales_invoice_list);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Order List");

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        btnSalesOrderListSearch = (Button)findViewById(R.id.btnSearch);
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
        Drawable drawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_ellipsis_v)
                .color(Color.WHITE)
                .sizeDp(30);
        myToolbar.setOverflowIcon(drawable);

        listView1=(ListView)findViewById(R.id.listView1);

        dataModels= new ArrayList<>();

        dataModels.add(new MsoSalesOrderList("Secrets Fine Food", "SO001", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("GroXers Inc", "SO002", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Giant Hypermarket - IMM", "SO003", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Four seasons organic market","SO004","07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("NTUC Fairprice", "SO005", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Brown Rice Paradise", "SO006", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("FairPrice Finest", "SO007", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Belmonte Latin Foods","SO008","07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Big Box", "SO009", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("The Cheese Shop", "SO010", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("J-Mart ","SO012","07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Cold Storage ", "SO013", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("GNC", "SO014", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("MyOutlets Global Halal Hub", "SO015", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Brown Rice Paradise","SO016","07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Cold Storage ", "SO017", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Big Box", "SO018", "07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Secrets Fine Food","SO019","07/05/2017","07/05/2017"));
        dataModels.add(new MsoSalesOrderList("Giant Hypermarket - IMM", "SO020", "07/05/2017","07/05/2017"));

        adapter = new MvsSalesInvoicelistAdapter(dataModels,getApplication());

        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MsoSalesOrderList dataModel= dataModels.get(position);

                Intent intent = new Intent(getApplication(), MvsSalesInvoiceActivity.class);
                intent.putExtra("customerName",dataModel.getName());
                intent.putExtra("deliveryDate",dataModel.getDeliverDate());
                startActivity(intent);
            }
        });
    }
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
    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }
    private void showSearchDialog() {

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
}
