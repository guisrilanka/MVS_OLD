package com.gui.mdt.thongsieknavclient.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MvsScanQrListAdapter;
import com.gui.mdt.thongsieknavclient.interfaces.ItemOnClickListener;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

public class ScanQrActivity extends AppCompatActivity implements ItemOnClickListener {

    RecyclerView mRecyclerViewScanQr;
    LinearLayoutManager mLayoutManager;
    MvsScanQrListAdapter mvsScanQrListAdapter;

    TextView tvSalesOrder;
    Toolbar myToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSalesOrder = (TextView) findViewById(R.id.tvSalesOrder);
        tvSalesOrder.setText(MvsSalesOrderActivity.qrScanHeadModels.get(0).b_invoiceNo);
        mRecyclerViewScanQr = (RecyclerView) findViewById(R.id.recyclerViewScannedQr);
//        btnDone = (Button) findViewById(R.id.btnDone);
//        btnDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });

        loadScanQrToRecycle();

    }


    private void loadScanQrToRecycle() {
        if (mvsScanQrListAdapter != null) {
            mvsScanQrListAdapter.notifyDataSetChanged();
        } else {
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerViewScanQr.setLayoutManager(mLayoutManager);
            mvsScanQrListAdapter = new MvsScanQrListAdapter(MvsSalesOrderActivity.qrScanHeadModels, this);
            mRecyclerViewScanQr.setAdapter(mvsScanQrListAdapter);
            mvsScanQrListAdapter.setItemOnClickListener(this);
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerViewScanQr);

        }
    }


    @Override
    public void itemOnClicked(String type, int position) {
        if (type.equals("next")) {
            mRecyclerViewScanQr.scrollToPosition(position + 1);
        } else {
            mRecyclerViewScanQr.scrollToPosition(position - 1);
        }
    }
}
