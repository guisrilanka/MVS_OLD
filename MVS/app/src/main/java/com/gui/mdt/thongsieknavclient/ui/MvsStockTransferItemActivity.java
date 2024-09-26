package com.gui.mdt.thongsieknavclient.ui;

/**
 * Created by BhanukaBandara on 7/19/17.
 */

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.MsoSalesOrder;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;


public class MvsStockTransferItemActivity extends AppCompatActivity {

    Toolbar myToolbar;
    ArrayList<MsoSalesOrder> dataModels;
    String description, uom, unitprice, QTy, warehouseqty, Total, itemNo,vehicleQty;
    EditText txtItemDesc, txtUOM, txtUnitPrice, txtQTY, txtWarehouseQTY, txtTotalPrice,txtVehicleQty;
    TextView tvSalesitem;
    Button btnadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvs_stock_transfer_order_item);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Item Details");

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);


        myToolbar.hideOverflowMenu();

        txtItemDesc = (EditText) findViewById(R.id.txtItemDesc);
        txtUOM = (EditText) findViewById(R.id.txtUOM);
        txtUnitPrice = (EditText) findViewById(R.id.txtUnitPrice);
        txtQTY = (EditText) findViewById(R.id.txtQTY);
        txtWarehouseQTY = (EditText) findViewById(R.id.txtWarehouseQTY);
        txtVehicleQty = (EditText) findViewById(R.id.txtVehicleQty);
        txtTotalPrice = (EditText) findViewById(R.id.txtTotalPrice);
        tvSalesitem = (TextView) findViewById(R.id.tvSalesitem);
        btnadd = (Button) findViewById(R.id.btnadd);

        Bundle extras = getIntent().getExtras();

        itemNo = extras.getString("b_itemNo");
        description = extras.getString("itemDescroption");
        uom = extras.getString("uom");
        unitprice = extras.getString("price");
        QTy = extras.getString("QTY");
        warehouseqty = extras.getString("QTY");
        Total = extras.getString("total");

        txtItemDesc.setText(description);
        txtUOM.setText(uom);
        txtUnitPrice.setText(unitprice);
        txtQTY.setText(QTy);
        txtWarehouseQTY.setText(warehouseqty);
        txtTotalPrice.setText(Total);
        tvSalesitem.setText(itemNo);

        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MvsStockTransferItemActivity.this,"Item Added",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sales_order_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
}
