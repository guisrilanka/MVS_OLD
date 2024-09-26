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
import android.widget.Button;

import com.gui.mdt.thongsieknavclient.R;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

public class LdsPaymentActivity extends AppCompatActivity {

    Toolbar tb_lds;
    private Button btnSearch, btnCash, btnCheque,btnSave,btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lds_payment);

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
        Drawable searchDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_search)
                .color(Color.WHITE)
                .sizeDp(30);
        btnSearch.setBackgroundDrawable(searchDrawable);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showSearchDialog();
            }
        });
        Drawable drawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_ellipsis_v)
                .color(Color.WHITE)
                .sizeDp(30);
        tb_lds.setOverflowIcon(drawable);


        btnCash = (Button) findViewById(R.id.btnCash);
        btnCheque = (Button) findViewById(R.id.btnCheque);

        btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCashDialog();
            }
        });
        btnCheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChequeDialog();
            }
        });

        btnSave = (Button)findViewById(R.id.btnSave);
        btnCancel=(Button)findViewById(R.id.btnCancel);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }
    private void showCashDialog() {

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
        dialog.setContentView(R.layout.dialog_sales_payment_cash);
        dialog.setCancelable(false);

        /*Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
        String[] items = new String[]{"Pending", "Completed", "Confirm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);*/

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
    private void showChequeDialog() {
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
        dialog.setContentView(R.layout.dialog_sales_payment_cheque);
        dialog.setCancelable(false);

        /*Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
        String[] items = new String[]{"Pending", "Completed", "Confirm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);*/

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
