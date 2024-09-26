package com.gui.mdt.thongsieknavclient.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

/**
 * Created by BhanukaBandara on 7/11/17.
 */

public class SalesCustomerInvoice extends AppCompatActivity {

    Toolbar myToolbar;
    TextView tvCusName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_customer_invoice);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer Invoice");

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);

        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        tvCusName=(TextView)findViewById(R.id.tvCusName);
        String cusName = getIntent().getStringExtra("cusName");
        if(!(cusName==null || cusName==""))
        {
            tvCusName.setText(getIntent().getExtras().getString("cusName"));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
