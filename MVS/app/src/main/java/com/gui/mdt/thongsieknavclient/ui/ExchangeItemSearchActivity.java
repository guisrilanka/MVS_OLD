package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.seanzor.prefhelper.SharedPrefHelper;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.ExchangeItemSearchAdapter;
import com.gui.mdt.thongsieknavclient.adapters.SalesItemSearchAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.ExchangeItem;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;
import com.gui.mdt.thongsieknavclient.datamodel.ItemCategory;
import com.gui.mdt.thongsieknavclient.datamodel.StockStatus;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ExchangeItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemBalancePdaDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemCategoryDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockStatusDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.model.PostDbBackupResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.CreateStockBalanceSummaryPdfSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemBalanceVehicleSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemImageSync;
import com.gui.mdt.thongsieknavclient.syncTasks.PdfUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SendEmailSyncTask;
import com.gui.mdt.thongsieknavclient.utils.ZipManager;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeItemSearchActivity extends AppCompatActivity implements View.OnClickListener,
        AsyncResponse {

    private RecyclerView recyclerViewSalesItem;
    private ExchangeItemSearchAdapter salesItemSearchAdapter;
    List<ExchangeItem> itemList;

    private ProgressDialog mProgressDialog;
    SharedPrefHelper mPrefHelper;

    Toolbar myToolbar;

    String formName = "",
            searchItemCode = "",
            searchCategoryCode = "",
            searchItemDescription = "",
            categoryDescription = "",
            customerPriceGroup = "",
            itemSearchText = "",
            details = "",
            mDateToday = "";
    Bundle extras;
    private GetSalesItemTask xGetSalesItemTask = null;
    private Customer tempCustomer;
    private EditText txtItemSearch;

    SwipyRefreshLayout swipyRefreshLayoutSalesItem;
    private NavClientApp mApp;

    Logger mLog;

    SharedPreferences mDefaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_exchange_item_search);

        mApp = (NavClientApp) getApplicationContext();
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefHelper = new SharedPrefHelper(getResources(), mDefaultSharedPreferences);
        recyclerViewSalesItem = (RecyclerView) findViewById(R.id.recyclerViewExchangeItem);
        final EditText textScanCode = (EditText) findViewById(R.id.textScanCode);
        txtItemSearch = (EditText) findViewById(R.id.txtItemSearch);

        mProgressDialog = new ProgressDialog(ExchangeItemSearchActivity.this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCanceledOnTouchOutside(false);

        extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(getResources().getString(R.string.intent_extra_form_name))) {
                formName = getIntent().getStringExtra(getResources().getString(R.string.intent_extra_form_name));
                if (extras.containsKey(getResources().getString(R.string.customer_json_obj))) {
                    tempCustomer = Customer.fromJson(getIntent().getStringExtra(getResources().getString(R.string.customer_json_obj)));
                    customerPriceGroup = tempCustomer.getCustomerPriceGroup();
                }
                if (extras.containsKey(getResources().getString(R.string.intent_extra_details))) {
                    details = getIntent().getStringExtra(getResources().getString(R.string.intent_extra_details));
                }
            }
        }

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Order List");

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        textScanCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    textScanCode.requestFocus();
                }
                return true;
            }
        });

        txtItemSearch.addTextChangedListener(searchTextWatcher());

            recyclerViewSalesItem.setLayoutManager(new LinearLayoutManager(this));



        swipyRefreshLayoutSalesItem = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayoutSalesItem);
        swipyRefreshLayoutSalesItem.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    //customerPriceGroup = "";
                    searchItemCode = "";
                    searchCategoryCode = "";
                    searchItemDescription = "";
                    categoryDescription = "";
                    txtItemSearch.setText("");

                    xGetSalesItemTask = new GetSalesItemTask();
                    xGetSalesItemTask.execute((Void) null);
                    swipyRefreshLayoutSalesItem.setRefreshing(false);
                }
            }
        });
        // Configure the refreshing colors
        swipyRefreshLayoutSalesItem.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


            salesItemSearchAdapter = new ExchangeItemSearchAdapter(new ArrayList<ExchangeItem>(),
                    extras,
                    R.layout.list_exchange_item_search_detail_card,
                    ExchangeItemSearchActivity.this);
            recyclerViewSalesItem.setAdapter(salesItemSearchAdapter);


        //Calling GetItemTask (Task) to Get All Items from DB
        xGetSalesItemTask = new GetSalesItemTask();
        xGetSalesItemTask.execute((Void) null);


        if (formName.equals(getResources().getString(R.string.form_name_mvs_sales_order)) &&
                details.equals(getResources().getString(R.string.intent_extra_add_new_item))) {
            textScanCode.setEnabled(false);
            textScanCode.setVisibility(View.GONE);
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj = new Date();
        mDateToday = df.format(dateObj).toString();

        this.mLog = Logger.getLogger(ExchangeItemSearchActivity.class);

    }

    private TextWatcher searchTextWatcher() {

        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                mProgressDialog.setMessage("Loading...");
                itemSearchText = txtItemSearch.getText().toString();

                if (!itemSearchText.isEmpty()) {

                        salesItemSearchAdapter = new ExchangeItemSearchAdapter(getSearchResult()
                                , extras
                                , R.layout.list_exchange_item_search_detail_card
                                , ExchangeItemSearchActivity.this);
                        recyclerViewSalesItem.setAdapter(salesItemSearchAdapter);

                    mProgressDialog.dismiss();
                } else {
                    mProgressDialog.show();

                        salesItemSearchAdapter = new ExchangeItemSearchAdapter(itemList
                                , extras
                                , R.layout.list_exchange_item_search_detail_card
                                , ExchangeItemSearchActivity.this);
                        recyclerViewSalesItem.setAdapter(salesItemSearchAdapter);

                    mProgressDialog.dismiss();
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

    public List<ExchangeItem> getSearchResult() {
        List<ExchangeItem> itemListResult = new ArrayList<ExchangeItem>();

        String searchText = itemSearchText.toUpperCase();

        if (itemList != null) {
            for (ExchangeItem item : itemList) {
                if (item.getItemCode().contains(searchText)) {
                    itemListResult.add(item);
                } else if (item.getDescription().contains(searchText)) {
                    itemListResult.add(item);
                }
            }
        }
        return itemListResult;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

    }


    public void showMessageBox(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private String getItemImageUrl(String itemCode) {
        String url = "";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", getApplicationContext().MODE_PRIVATE);
        Resources res = getApplicationContext().getResources();

        try {
            File f = new File(directory, itemCode + ".png");
            Bitmap temp = BitmapFactory.decodeStream(new FileInputStream(f));
            url = f.getPath().toString();
        } catch (FileNotFoundException e) {

            url = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.item_no_image)
                    + '/' + getResources().getResourceTypeName(R.drawable.item_no_image)
                    + '/' + getResources().getResourceEntryName(R.drawable.item_no_image)).toString();
        }
        return url;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public void searchItems() {

        ExchangeItemDbHandler dbAdapter = new ExchangeItemDbHandler(this);
        try {
            dbAdapter.open();
            itemList = new ArrayList<ExchangeItem>();
            itemList = dbAdapter.getAllItems();



            if (itemList != null && !itemList.isEmpty()) {
                for (int i = 0; i < itemList.size(); i++) {
                    ExchangeItem tempItem = itemList.get(i);
                    tempItem.setItemImageUrl(getItemImageUrl(tempItem.getItemCode()));
                    itemList.set(i, tempItem);
                }
            } else {

            }
        } catch (Exception e) {

        } finally {
            if (dbAdapter != null) {
                dbAdapter.close();
            }
        }

    }

    public class GetSalesItemTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                    searchItems();


            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {

            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();

                salesItemSearchAdapter = new ExchangeItemSearchAdapter(new ArrayList<ExchangeItem>()
                        , extras
                        , R.layout.list_exchange_item_search_detail_card
                        , ExchangeItemSearchActivity.this);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {

                        salesItemSearchAdapter = new ExchangeItemSearchAdapter(itemList
                                , extras
                                , R.layout.list_exchange_item_search_detail_card
                                , ExchangeItemSearchActivity.this);
                        recyclerViewSalesItem.setAdapter(salesItemSearchAdapter);


                    salesItemSearchAdapter.notifyDataSetChanged();

                    if (itemList.isEmpty()) {
                        Toast.makeText(ExchangeItemSearchActivity.this, "No Items To Show!", Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialog.dismiss();
                } catch (Exception ex) {
                }
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }
    }

}