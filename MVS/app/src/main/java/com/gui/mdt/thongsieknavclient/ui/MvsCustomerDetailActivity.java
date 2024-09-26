package com.gui.mdt.thongsieknavclient.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.gui.mdt.thongsieknavclient.R;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

public class MvsCustomerDetailActivity extends AppCompatActivity {

    Toolbar myToolbar;
    Button btnSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvs_customer_detail);

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
                //showSearchDialog();
                Intent intent=new Intent(MvsCustomerDetailActivity.this, SalesCustomerListActivity.class);
                intent.putExtra("IsPopupNeeded",true);
                startActivity(intent);
                finish();
            }
        });
        Drawable drawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_ellipsis_v)
                .color(Color.WHITE)
                .sizeDp(30);
        myToolbar.setOverflowIcon(drawable);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mvs_sales_customer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_cusInvoice:
                Intent invoice = new Intent(this, SalesCustomerInvoiceActivity.class);
                this.startActivity(invoice);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
