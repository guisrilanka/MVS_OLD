package com.gui.mdt.thongsieknavclient.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.SalesCustomerArAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.model.SalesCustomerArParameter;
import com.gui.mdt.thongsieknavclient.model.SalesCustomerArResponse;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class SalesCustomerArSelectActivity extends AppCompatActivity {


    NavClientApp mApp;
    SalesCustomerArParameter salesCustomerArParameter;
    SalesCustomerArResponse salesCustomerArResponse;
    SalesCustomerArAdapter salesCustomerArAdapter;

    Toolbar myToolbar;
    TextView mCusName, mOutstanding;
    SwipyRefreshLayout mSwipyRefreshLayout;
    RecyclerView mCustomerInvoiceRecyclerView;
    private String customerNo;
    private String bookMark;
    private ProgressDialog xProgressDialog;
    LinearLayout mLayoutAddButton;
    Button mBtnAdd;
    List<SalesCustomerArResponse.SalesCustomerAr> mCustomerArList=
            new ArrayList<SalesCustomerArResponse.SalesCustomerAr>()
            ,mCheckedCustomerArList = new ArrayList<SalesCustomerArResponse.SalesCustomerAr>();
    String mDetails = "";
    Bundle mExtras;
    Customer customerObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sales_customer_ar);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mCusName = (TextView) findViewById(R.id.tvCusName);
        mOutstanding = (TextView) findViewById(R.id.tvOutstanding);
        mExtras = getIntent().getExtras();

        if (mExtras != null) {

            if (mExtras.containsKey(getResources().getString(R.string.intent_extra_details))) {
                mDetails =  mExtras.getString(getResources().getString(R.string.intent_extra_details));
            }

            if (mExtras.containsKey(getResources().getString(R.string.customer_json_obj))) {
                customerObj = Customer.fromJson(mExtras.getString(getResources().getString(R.string.customer_json_obj)));

                if(customerObj != null)
                {
                    if (!(customerObj.getName() == null || customerObj.getName() == "")) {
                        mCusName.setText(customerObj.getName());
                    }

                    String balence = String.valueOf(customerObj.getBalance());

                    if (!(balence == null|| balence == "")) {
                        mOutstanding.setText("S$ " + balence);
                    }
                }
                customerNo = customerObj.getCode();
            }
        }

        mLayoutAddButton = (LinearLayout) findViewById(R.id.layout_add_button);
        mBtnAdd = (Button) findViewById(R.id.btnAddItem);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Customer AR");

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        mApp = (NavClientApp) getApplicationContext();
        bookMark = "";

        mCustomerInvoiceRecyclerView = (RecyclerView) findViewById(R.id.customer_ar_recycler_view);
        mCustomerInvoiceRecyclerView.setHasFixedSize(true);
        mCustomerInvoiceRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.BOTH) {

                } else {
                    if (direction == SwipyRefreshLayoutDirection.TOP) {
                        bookMark = ""; //set empty to get first page
                        salesCustomerArAdapter.clear();
                        getCustomerArList();
                        mSwipyRefreshLayout.setRefreshing(false);
                    } else {
                        getCustomerArList();
                        mSwipyRefreshLayout.setRefreshing(false);
                    }
                }

            }
        });
        // Configure the refreshing colors
        mSwipyRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        salesCustomerArAdapter = new SalesCustomerArAdapter(
                new ArrayList<SalesCustomerArResponse.SalesCustomerAr>(),
                R.layout.item_sales_order_ar_card,
                getApplication(),
                true, mCheckedCustomerArList);
        mCustomerInvoiceRecyclerView.setAdapter(salesCustomerArAdapter);

        mLayoutAddButton.setVisibility(View.VISIBLE);

        initializeActivity();

        mBtnAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String mArNoList = "";
                int selectCount=0;
                float totalBalence = 0f;

                if(!mCheckedCustomerArList.isEmpty())
                {
                    for(int i=0; i<mCheckedCustomerArList.size(); i++)
                    {
                        SalesCustomerArResponse.SalesCustomerAr arObj = mCheckedCustomerArList.get(i);
                        if(arObj.getArSelected() == true)
                        {
                            float balence = mCheckedCustomerArList.get(i).getBalance()==""
                                    ?0f:Float.parseFloat(mCheckedCustomerArList.get(i).getBalance());

                            mArNoList = mArNoList +  arObj.getInvoiceNo()+"/"+dateConverter(arObj.getDocumentDate())+"," +"\n";
                            totalBalence = totalBalence + balence;
                            selectCount++;
                        }
                    }
                    if(selectCount==0)
                    {
                        Toast.makeText(mApp, "Please Check at least one Invoice!", Toast.LENGTH_SHORT).show();
                    }
                    else if(selectCount > 0)
                    {
                        //remove last comma
                        StringBuilder str = new StringBuilder(mArNoList);
                        int index = str.lastIndexOf(",");
                        str.setCharAt(index,' ');
                        mArNoList=str.toString();

                        String totalBalence_=String.format("%.2f", totalBalence);

                        Intent intent = new Intent();
                        intent.putExtra(getResources().getString(R.string.intent_extra_details),mDetails);
                        intent.putExtra("arNoList",mArNoList);
                        intent.putExtra("totalBalence", totalBalence_);
                        SalesCustomerArSelectActivity.this.setResult(RESULT_OK, intent);
                        SalesCustomerArSelectActivity.this.finish();
                    }
                }
            }
        });
    }

    public String dateConverter(String date_)
    {
        String date="";

        if (date_ != null) {
            if (!date_.equals("")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mApp.getResources().getString(R.string.date_format_dd_MM_yyyy));
                try {

                    Date initDate = new SimpleDateFormat(mApp.getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(date_);
                    date = simpleDateFormat.format(initDate);

                } catch (Exception e) {
                    Log.e(mApp.getResources().getString(R.string.message_exception), e.getMessage().toString());
                }

            }
        }
        return date;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initializeActivity() {
        checkConnection();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void checkConnection() {
        if (!isNetworkConnected()) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.notification_title_no_connection))
                    .setMessage(getResources().getString(R.string.notification_msg_no_connection))
                    .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkConnection();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        } else {
            Log.d("LOGIN", "PASSED");

            if (mApp.getNavBrokerService() != null) {
                GetCustomersArTask cusArListTask = new GetCustomersArTask();
                cusArListTask.execute((Void) null);
            }
        }
    }

    private void getCustomerArList() {
        GetCustomersArTask cusArListTask = new GetCustomersArTask();
        cusArListTask.execute((Void) null);
    }

    private void loadListView(List<SalesCustomerArResponse.SalesCustomerAr> customerArList) {
        if (!bookMark.equals("")) {
            salesCustomerArAdapter.addAll(customerArList);
            salesCustomerArAdapter.notifyDataSetChanged();
        } else {
            salesCustomerArAdapter = new SalesCustomerArAdapter(
                    customerArList,
                    R.layout.item_sales_order_ar_card,
                    getApplication(),
                    true,
                    mCheckedCustomerArList);
            mCustomerInvoiceRecyclerView.setAdapter(salesCustomerArAdapter);
            salesCustomerArAdapter.notifyDataSetChanged();
        }
    }

    public class GetCustomersArTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                salesCustomerArParameter = new SalesCustomerArParameter();
                salesCustomerArParameter.setUserName(mApp.getCurrentUserName());
                salesCustomerArParameter.setPassword(mApp.getCurrentUserPassword());
                salesCustomerArParameter.setUserCompany(mApp.getmUserCompany());
                salesCustomerArParameter.setInvoiceNo("");
                salesCustomerArParameter.setCustomerNo(customerNo);
                salesCustomerArParameter.setBookmarkKey(bookMark);
                salesCustomerArParameter.setPageSize("20");
                Gson gson = new Gson();
                String json = gson.toJson(salesCustomerArParameter);
                Log.d("AR_PARAMS ", json);

                Call<SalesCustomerArResponse> call = mApp.getNavBrokerService()
                        .GetSalesCustomerAr(salesCustomerArParameter);

                salesCustomerArResponse = call.execute().body();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            xProgressDialog = new ProgressDialog(SalesCustomerArSelectActivity.this);
            xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            xProgressDialog.setMessage("Loading...");
            xProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    if (salesCustomerArResponse == null) {
                        Toast.makeText(mApp, getResources().getString(R.string.message_Customer_Ar), Toast.LENGTH_SHORT).show();
                        xProgressDialog.dismiss();
                    } else {
                        if(salesCustomerArResponse.getCustomerLedgerEntryListResultData().size()>0) {
                            bookMark = salesCustomerArResponse.getBookMarkKey();
                        }
                        mCustomerArList = salesCustomerArResponse.getCustomerLedgerEntryListResultData();
                        loadListView(mCustomerArList);
                        xProgressDialog.dismiss();
                    }
                } catch (Exception ex) {
                    xProgressDialog.dismiss();
                }
            } else {
                Toast.makeText(mApp, getResources().getString(R.string.message_Customer_Ar), Toast.LENGTH_SHORT).show();
                xProgressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            xProgressDialog.dismiss();
        }
    }
}
