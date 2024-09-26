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

public class SalesCustomerAr extends AppCompatActivity {


    Toolbar myToolbar;
    TextView tvCusName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_customer_ar);


        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer AR");


        /*prevLayout = (LinearLayout)findViewById(R.id.prevLayout);
        nxtLayout = (LinearLayout)findViewById(R.id.nxtLayout);
        prevLayout.setClickable(true);
        nxtLayout.setClickable(true);*/


        /*pageNo = (TextView)findViewById(R.id.pageNo);*/

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        Drawable backArrow1 = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.DKGRAY)
                .sizeDp(10);

        Drawable forwardArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_right)
                .color(Color.DKGRAY)
                .sizeDp(10);

        tvCusName=(TextView)findViewById(R.id.tvCusName);
        String cusName = getIntent().getStringExtra("cusName");
        if(!(cusName==null || cusName==""))
        {
            tvCusName.setText(getIntent().getExtras().getString("cusName"));
        }
        /*btnPrevious = (ImageButton)findViewById(R.id.btnPrevious);
        btnForward = (ImageButton)findViewById(R.id.btnForward);

        btnPrevious.setBackgroundDrawable(backArrow1);
        btnForward.setBackgroundDrawable(forwardArrow);*/


        /*nxtLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String present_value_string = pageNo.getText().toString();
                int present_value_int = Integer.parseInt(present_value_string);
                present_value_int++;

                if(present_value_int != 11) {
                    pageNo.setText(String.valueOf(present_value_int));
                }
            }
        });

        prevLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String present_value_string = pageNo.getText().toString();
                int present_value_int = Integer.parseInt(present_value_string);
                present_value_int--;

                if(present_value_int != 0) {

                    pageNo.setText(String.valueOf(present_value_int));
                }
            }
        });*/


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
