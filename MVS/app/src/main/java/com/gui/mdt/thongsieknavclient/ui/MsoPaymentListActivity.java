package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MsoPaymentListAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentHeader;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentLine;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.PaymentHeaderDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.PaymentUploadSyncTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MsoPaymentListActivity extends AppCompatActivity implements android.view.View.OnClickListener,
        AsyncResponse {

    Toolbar myToolbar;
    Button btnSearch;
    FrameLayout flCalendar;
    int year, month, date;

    private MsoPaymentListAdapter msoPaymentListAdapter;
    private SwipyRefreshLayout swipyrefreshlayoutPaymentItem;
    private RecyclerView recyclerViewPaymentItem;
    private ProgressDialog xProgressDialog;
    private List<PaymentHeader> paymentHeaderList, checkedPaymentHeaderList;

    private GetPaymentListTask xGetPaymentListTask;
    private PaymentUploadSyncTask paymentUploadSyncTask;

    private Drawable backArrow, searchDrawable, menuDrawable;
    private FloatingActionButton fabAddNewPayment;
    private String searchCustomerName = "", searchPaymentDate = "", searchStatus = "", searchCustomerCode = "", selectedDate = "", formName = "",mcurrentModule="",searchText="";
    private Customer tempCustomer;
    NavClientApp mApp;
    boolean isSearchButtonClicked = false;

    Calendar calander;
    SimpleDateFormat simpledateformat;
    private EditText txtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mso_payment_list);
        mApp = (NavClientApp) getApplicationContext();
        Bundle extras = getIntent().getExtras();
        mcurrentModule= mApp.getmCurrentModule();

        txtSearch=(EditText)findViewById(R.id.txtSearch);

        if (extras != null) {
            if (extras.containsKey("formName")) {
                formName = extras.getString("formName");
            }
            if (extras.containsKey("_customerDetailObject")) {
                String objAsJson = extras.getString("_customerDetailObject");
                tempCustomer = Customer.fromJson(objAsJson);

                if (tempCustomer.getCode() != null) {
                    SimpleDateFormat dBFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat uIFormat = new SimpleDateFormat("dd-MM-yyyy");
                    uIFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                    String todayDate = dBFormat.format(new Date());
                    searchPaymentDate = todayDate;
                    selectedDate = uIFormat.format(new Date());

                    searchCustomerName = tempCustomer.getName();
                } else {
                    searchCustomerName = "";
                }
            }
        }

        paymentHeaderList = new ArrayList<PaymentHeader>();
        checkedPaymentHeaderList = new ArrayList<PaymentHeader>();

        recyclerViewPaymentItem = (RecyclerView) findViewById(R.id.recyclerViewPaymentItem);
        swipyrefreshlayoutPaymentItem = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayoutPaymentItem);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        fabAddNewPayment = (FloatingActionButton) findViewById(R.id.fabAddNewPayment);

        xProgressDialog = new ProgressDialog(this);
        xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Order List");

        backArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left).color(Color.WHITE).sizeDp(30);
        searchDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_search).color(Color.WHITE).sizeDp(25);
        menuDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_ellipsis_v).color(Color.WHITE).sizeDp(25);

        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        btnSearch.setBackgroundDrawable(searchDrawable);

        myToolbar.setOverflowIcon(menuDrawable);

        recyclerViewPaymentItem.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewPaymentItem.setLayoutManager(mLayoutManager);

        swipyrefreshlayoutPaymentItem.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    selectedDate = "";
                    searchCustomerName = "";
                    searchPaymentDate = "";
                    searchStatus = "";
                    searchCustomerCode = "";
                    txtSearch.setText("");

                    xGetPaymentListTask = new GetPaymentListTask();
                    xGetPaymentListTask.execute();
                    swipyrefreshlayoutPaymentItem.setRefreshing(false);
                }
            }
        });

        txtSearch.addTextChangedListener(searchTextWatcher());

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSearchButtonClicked)
                {
                    isSearchButtonClicked = true;
                    showSearchDialog();
                }

            }
        });

        if(mcurrentModule.equals(getResources().getString(R.string.module_lds)))
        {
            fabAddNewPayment.setVisibility(View.GONE);
            fabAddNewPayment.setEnabled(false);
            fabAddNewPayment.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

        }

        fabAddNewPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (formName.equals("MsoSalesOrder")) {
                    String jsonCustomerObj = tempCustomer.toJson();
                    Intent intent = new Intent(MsoPaymentListActivity.this, MsoPaymentActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("details", "NewPayment");
                    intent.putExtra("formName", "MsoSalesOrder");
                    intent.putExtra("_customerDetailObject", jsonCustomerObj);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MsoPaymentListActivity.this, MsoPaymentActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("details", "NewPayment");
                    startActivity(intent);
                }
            }
        });

        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("yyyy-MM-dd");


        selectedDate=simpledateformat.format(calander.getTime());
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

    }

    private class GetPaymentListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getPaymentHeaderList(searchCustomerName, searchPaymentDate, searchStatus, searchCustomerCode);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            xProgressDialog = new ProgressDialog(MsoPaymentListActivity.this);
            xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            xProgressDialog.setMessage("Loading...");
            xProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {

                    msoPaymentListAdapter = new MsoPaymentListAdapter(paymentHeaderList, R.layout.item_sales_payment_card, MsoPaymentListActivity.this);
                    recyclerViewPaymentItem.setAdapter(msoPaymentListAdapter);
                    msoPaymentListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();

                    if (paymentHeaderList.size() == 0) {
                        if (formName.equals("MsoSalesOrder")) {
                            finish();

                            String jsonCustomerObj = tempCustomer.toJson();
                            Intent intent = new Intent(MsoPaymentListActivity.this, MsoPaymentActivity.class);
                            intent.putExtra("details", "NewPayment");
                            intent.putExtra("formName", "MsoSalesOrder");
                            intent.putExtra("_customerDetailObject", jsonCustomerObj);
                            startActivity(intent);
                        }
                    }
                } catch (Exception ex) {
                }
            } else {
            }
        }

        @Override
        protected void onCancelled() {
            xProgressDialog.dismiss();
        }
    }

    public void getPaymentHeaderList(String customerName, String paymentDate, String status, String customerCode) {
        PaymentHeaderDbHandler dbAdapter = new PaymentHeaderDbHandler(getApplicationContext());
        dbAdapter.open();

        if(mcurrentModule.equals(getResources().getString(R.string.module_lds))){

            paymentHeaderList = dbAdapter.getAllPaymentHeadersForLDS(customerName, paymentDate, status, customerCode);
        }
        else{
            paymentHeaderList = dbAdapter.getAllPaymentHeaders(customerName, paymentDate, status, customerCode);
        }


        dbAdapter.close();
    }

    //----------------Menu Bar----------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sales_order_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_selectAll:
                updateAllListStatus(true);
                msoPaymentListAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_cancelSelect:
                updateAllListStatus(false);
                msoPaymentListAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_confirm:
                if (getListCheckedCount() > 0) {
                    confirmPaymentListTask phTask = new confirmPaymentListTask();
                    phTask.execute();
                } else {
                    Toast.makeText(mApp, "Please select at least one item to confirm", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class confirmPaymentListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            xProgressDialog = new ProgressDialog(MsoPaymentListActivity.this);
            xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            xProgressDialog.setMessage("Loading...");
            xProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                saveCheckedPaymentListToMobileDb();

                if (isNetworkConnected()) {
                    uploadConfirmedPaymentsAndSaveToMobileDb();
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    /*if(checkedPaymentHeaderList.size() > 0)
                    {*/
                        msoPaymentListAdapter.notifyDataSetChanged();
                        xProgressDialog.dismiss();
                        Toast.makeText(mApp, "Successfully confirmed!", Toast.LENGTH_SHORT).show();
                    /*}
                    else {
                        xProgressDialog.dismiss();
                        Toast.makeText(mApp, "Please select at least one item to confirm", Toast.LENGTH_SHORT).show();
                    }*/

                } catch (Exception e) {
                    Log.d("Exception: ",e.getMessage().toString());
                    xProgressDialog.dismiss();
                }
            }
            else {
                xProgressDialog.dismiss();
                Toast.makeText(mApp, "Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadConfirmedPaymentsAndSaveToMobileDb() {
        paymentUploadSyncTask = new PaymentUploadSyncTask(getApplicationContext(), true);
        paymentUploadSyncTask.delegate = MsoPaymentListActivity.this;
        paymentUploadSyncTask.execute((Void) null);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public int getListCheckedCount()
    {
        int count = 0;
        for(PaymentHeader ph : paymentHeaderList)
        {
            if(ph.isChecked() && ph.getStatus().equals(getResources().getString(R.string.SalesPaymentStatusPending)))
            {
                count++;
            }
        }
        return count;
    }

    public void saveCheckedPaymentListToMobileDb() {
        updateStatusList();
        PaymentHeaderDbHandler dbAdapter = new PaymentHeaderDbHandler(this);
        dbAdapter.open();

        if (checkedPaymentHeaderList != null && checkedPaymentHeaderList.size() > 0) {
            for (PaymentHeader ph : checkedPaymentHeaderList) {
                try {
                    dbAdapter.updatePaymentHeader(ph);
                    Log.d("Payment_UPDATED: ", ph.getPaymentNo() == null ? "" : ph.getPaymentNo());
                } catch (Exception ee) {
                    Log.d("Exception: ", ee.getMessage().toString());
                    dbAdapter.close();
                }
            }
        }
        dbAdapter.close();
    }

    public void updateStatusList() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date today = new Date();

        for (PaymentHeader ph : paymentHeaderList) {
            if (ph.isChecked()) {
                ph.setLastModifiedBy(mApp.getCurrentUserName());
                ph.setLastModifiedDateTime(df.format(today).toString());
                ph.setStatus(getResources().getString(R.string.SalesOrderStatusConfirmed));
                checkedPaymentHeaderList.add(ph);
            }
        }
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

    public void updateAllListStatus(boolean status) {
        for (PaymentHeader ph : paymentHeaderList) {
            if(ph.getStatus().equals(getResources().getString(R.string.SalesPaymentStatusPending)))
            {
                ph.setChecked(status);
            }
        }
    }

    private void showSearchDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();

        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.getWindow()
                .setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_sales_payment_list_search);
        dialog.setCancelable(false);

        final Spinner dropdown = (Spinner) dialog.findViewById(R.id.spnStatus);
        String[] items = new String[]{"", "Pending", "Confirm"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);

        final EditText txtCustomerName = (EditText) dialog.findViewById(R.id.txtCustomerName);
        final TextView txtPaymentDate = (TextView) dialog.findViewById(R.id.txtPaymentDate);
        final EditText txtCustomerCode = (EditText) dialog.findViewById(R.id.txtCustomerCode);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCustomerName = txtCustomerName.getText().toString();
                searchStatus = dropdown.getSelectedItem().toString();
                searchCustomerCode = txtCustomerCode.getText().toString();

                if (!searchPaymentDate.equals("")) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
                    try {

                        Date initDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(searchPaymentDate);
                        searchPaymentDate = df.format(initDate);
                        selectedDate = df2.format(initDate);

                    } catch (Exception e) {
                        Log.e("Exception :", e.getMessage().toString());
                    }
                }

                xGetPaymentListTask = new GetPaymentListTask();
                xGetPaymentListTask.execute();

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

        flCalendar = (FrameLayout) dialog.findViewById(R.id.flCalendar);

        flCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDatePicker(view, dialog);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isSearchButtonClicked = false;
            }
        });

        txtCustomerName.setText(searchCustomerName);
        txtPaymentDate.setText(selectedDate);
        txtCustomerCode.setText(searchCustomerCode);

        int spinnerPosition = adapter.getPosition(searchStatus);
        dropdown.setSelection(spinnerPosition);

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void getDatePicker(View view, Dialog dialog) {
        Calendar cl, clNow;
        int nowYear, nowMonth, nowDay;
        cl = Calendar.getInstance();
        date = cl.get(Calendar.DAY_OF_MONTH);
        month = cl.get(Calendar.MONTH);
        year = cl.get(Calendar.YEAR);
        final TextView txtPaymentDate;

        clNow = Calendar.getInstance();
        nowYear = clNow.get(Calendar.YEAR);
        nowMonth = clNow.get(Calendar.MONTH);
        nowDay = clNow.get(Calendar.DAY_OF_MONTH);
        txtPaymentDate = (TextView) dialog.findViewById(R.id.txtPaymentDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtPaymentDate.setText(formatDate(year, month, dayOfMonth));
                //selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                searchPaymentDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            }
        }, date, month, year);

        datePickerDialog.updateDate(nowYear, nowMonth, nowDay);
        datePickerDialog.show();
    }

    private static String formatDate(int year, int month, int day) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        return sdf.format(date);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        xGetPaymentListTask = new GetPaymentListTask();
        xGetPaymentListTask.execute();
    }

    private TextWatcher searchTextWatcher()
    {
        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                searchText = txtSearch.getText().toString();
                xProgressDialog.setMessage("Loading...");

                if (!searchText.isEmpty()) {
                    xProgressDialog.show();
                    msoPaymentListAdapter = new MsoPaymentListAdapter(searchPayment(),
                            R.layout.item_sales_payment_card, MsoPaymentListActivity.this);
                    recyclerViewPaymentItem.setAdapter(msoPaymentListAdapter);
                    msoPaymentListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
                }
                else {
                    xProgressDialog.show();
                    msoPaymentListAdapter = new MsoPaymentListAdapter(paymentHeaderList,
                            R.layout.item_sales_payment_card, MsoPaymentListActivity.this);
                    recyclerViewPaymentItem.setAdapter(msoPaymentListAdapter);
                    msoPaymentListAdapter.notifyDataSetChanged();

                    xProgressDialog.dismiss();
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

    private List<PaymentHeader> searchPayment() {
        List<PaymentHeader> paymentHeaderResultList = new ArrayList<PaymentHeader>();

        String text = searchText.toUpperCase();

        if (paymentHeaderList != null) {
            for (PaymentHeader pay : paymentHeaderList) {
                if (pay.getPaymentNo().contains(text)) {
                    paymentHeaderResultList.add(pay);
                }
                else if (pay.getCustomerName().contains(text)) {
                    paymentHeaderResultList.add(pay);
                }
                else if (pay.getCustomerNo().contains(text)) {
                    paymentHeaderResultList.add(pay);
                }
            }
        }
        return paymentHeaderResultList;
    }
}
