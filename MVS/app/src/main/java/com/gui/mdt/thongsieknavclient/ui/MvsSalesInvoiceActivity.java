package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MsoSalesOrderAdapter;
import com.gui.mdt.thongsieknavclient.model.MsoSalesOrder;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

public class MvsSalesInvoiceActivity extends AppCompatActivity {
    private static MsoSalesOrderAdapter adapter;
    String customerName, deliveryDate;
    TextView tvCustomer, tvDeliveryDate;
    Toolbar myToolbar;
    ArrayList<MsoSalesOrder> dataModels;
    ListView lvSalesOrderDetail;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mvs_sales_invoice );
        tvCustomer = (TextView) findViewById(R.id.txtCustomer);
        tvDeliveryDate = (TextView) findViewById(R.id.txtDeliveryDate);

        Bundle extras = getIntent().getExtras();

        customerName = extras.getString("customerName");
        deliveryDate = extras.getString("deliveryDate");
        /*customerName = "Joho Doa";
        deliveryDate = "09/07/2017";*/

        tvCustomer.setText(customerName);
        tvDeliveryDate.setText(deliveryDate);

       /* Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

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
                showSearchDialog();
            }
        });
        Drawable drawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_ellipsis_v)
                .color(Color.WHITE)
                .sizeDp(30);
        myToolbar.setOverflowIcon(drawable);


        lvSalesOrderDetail = (ListView) findViewById(R.id.lvSalesOrderDetail);

        dataModels = new ArrayList<>();

        dataModels.add(new MsoSalesOrder("ITM001", "100.00", "1", "02", "100", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM002", "100.00", "1", "03", "200", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM003", "100.00", "1", "07", "300", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM004", "100.00", "1", "05", "400", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM005", "100.00", "1", "07", "500", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM006", "100.00", "1", "12", "600", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM007", "100.00", "1", "34", "700", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM008", "100.00", "1", "23", "300", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM009", "100.00", "1", "23", "400", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM0010", "100.00", "1", "45", "600", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM0011", "100.00", "1", "56", "600", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));
        dataModels.add(new MsoSalesOrder("ITM0011", "100.00", "1", "34", "200", "Cuttlefish Meat, Fish Meat, Potato Starch, Palm Oil 墨鱼丸肉、鱼肉、土豆淀粉、棕榈油"));

       /* adapter = new MsoSalesOrderAdapter(dataModels, getApplication());

        lvSalesOrderDetail.setAdapter(adapter);
        lvSalesOrderDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MsoSalesOrder dataModel = dataModels.get(position);
                //intent.putExtra("customerName",dataModel.getName());
                //intent.putExtra("deliveryDate",dataModel.getDeliverDate());
                //startActivity(intent);
                // Snackbar.make(view, dataModel.getName()+"\n"+dataModel.getName()+" API: "+dataModel.getSoDate(), Snackbar.LENGTH_LONG)
                //        .setAction("No action", null).show();
            }
        });*/
    }public static int getScreenWidth(Activity activity) {
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
