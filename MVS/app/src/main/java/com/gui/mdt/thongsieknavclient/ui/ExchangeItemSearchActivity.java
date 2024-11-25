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
//    List<ItemCategory> itemCategoryList;

//    AlertDialog.Builder mAlertDialogBuilder;
//    AlertDialog mAlertDialog;
    private ProgressDialog mProgressDialog;
    SharedPrefHelper mPrefHelper;

    Toolbar myToolbar;
    Button btnSearchMain;
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
    Boolean isStarted = false, isSearchButtonClicked = false, isItemBalPdaReset = false;
    private Customer tempCustomer;
    private EditText txtItemSearch;


    Item tempItem;
    SwipyRefreshLayout swipyRefreshLayoutSalesItem;
    private NavClientApp mApp;
//    ResetItemBalancePdaTask mResetItemBalancePdaTask;
//    ItemBalanceVehicleSyncTask mItemBalanceVehicleSyncTask;
    Logger mLog;
//    CreateStockBalanceSummaryPdfSyncTask mCreateStockBalanceSummaryPdfSyncTask;
//    PdfUploadSyncTask mPdfUploadSyncTask;
//    SendEmailSyncTask mSendEmailSyncTask;
//    String mReportDate = "", mReportFileName = "";
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
//        Drawable myToolbarDrawable = new IconicsDrawable(this)
//                .icon(FontAwesome.Icon.faw_ellipsis_v)
//                .color(Color.WHITE).sizeDp(30);
//        myToolbar.setOverflowIcon(myToolbarDrawable);
//
//        Drawable drawable = new IconicsDrawable(this)
//                .icon(FontAwesome.Icon.faw_ellipsis_v)
//                .color(Color.WHITE)
//                .sizeDp(30);
//        myToolbar.setOverflowIcon(drawable);

//        btnSearchMain = (Button) findViewById(R.id.btnSearch);
//        Drawable searchDrawable = new IconicsDrawable(this)
//                .icon(FontAwesome.Icon.faw_search)
//                .color(Color.WHITE)
//                .sizeDp(30);
//        btnSearchMain.setBackgroundDrawable(searchDrawable);
//        btnSearchMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isSearchButtonClicked) {
//                    isSearchButtonClicked = true;
//                    showSearchDialog();
//                }
//            }
//        });
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

//        if (mApp.getmCurrentModule().equals(getResources().getString(R.string.module_mso))) {
//            recyclerViewSalesItem.setHasFixedSize(true);
//            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
//            recyclerViewSalesItem.setLayoutManager(mLayoutManager);
//            recyclerViewSalesItem.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(4), true));
//            recyclerViewSalesItem.setItemAnimator(new DefaultItemAnimator());
//        } else {
            recyclerViewSalesItem.setLayoutManager(new LinearLayoutManager(this));
//            recyclerViewSalesItem.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
//        }


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


//        getCategoryList();
        //Calling GetItemTask (Task) to Get All Items from DB
        xGetSalesItemTask = new GetSalesItemTask();
        xGetSalesItemTask.execute((Void) null);

//        textScanCode.setInputType(InputType.TYPE_NULL);
//
//        textScanCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    textScanCode.setBackgroundResource(R.drawable.border_red);
//                } else {
//                    textScanCode.setBackgroundResource(R.drawable.border_gray);
//                }
//            }
//        });
//
//        textScanCode.requestFocus();
//
//       /* textScanCode.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (editable.toString().length() == 1) {
//                    isStarted = true;
//                    Handler handler = new Handler();
//                    mProgressDialog.setMessage("Loading...");
//                    mProgressDialog.show();
//
//                    handler.postDelayed(
//                            new Runnable() {
//                                public void run() {
//                                    if (isStarted) {
//                                        if (isItemAvailable(textScanCode.getText().toString())) {
//
//                                            getItem(textScanCode.getText().toString());
//                                            String objAsJson = tempItem.toJson();
//
//                                            if (formName.equals("MsoHome")) //from MsoHome -> Stock Info -> SalesItemDetailActivity
//                                            {
//                                                Intent intent = new Intent(SalesItemSearchActivity.this, SalesItemDetailActivity.class);
//                                                intent.putExtra("formName", "MsoSalesItemSearch");
//                                                intent.putExtra("_itemObject", objAsJson);
//                                                startActivity(intent);
//
//                                                mProgressDialog.dismiss();
//                                            }
//                                            if (formName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_in))) //from MvsHome -> Stock Transfer In -> SalesItemDetailActivity
//                                            {
//                                                Intent intent = new Intent(SalesItemSearchActivity.this, SalesItemDetailActivity.class);
//                                                intent.putExtra("formName", "MvsTransferInItemSearch");
//                                                intent.putExtra("_itemObject", objAsJson);
//                                                startActivity(intent);
//
//                                                mProgressDialog.dismiss();
//                                            }
//                                        } else {
//                                            mProgressDialog.dismiss();
//                                            showMessageBox("Alert","Invalid barcode");
//                                        }
//                                        textScanCode.setText("");
//                                        textScanCode.requestFocus();
//                                        isStarted = false;
//                                    }
//                                }
//                            }, 1000);
//                }
//            }
//        });*/
//
//        textScanCode.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    switch (keyCode) {
//                        case KeyEvent.KEYCODE_DPAD_CENTER:
//                        case KeyEvent.KEYCODE_ENTER:
//                            isStarted = true;
//                            Handler handler = new Handler();
//                            mProgressDialog.setMessage("Loading...");
//                            mProgressDialog.show();
//
//                            handler.postDelayed(
//                                    new Runnable() {
//                                        public void run() {
//                                            if (isStarted) {
//                                                if (isItemAvailable(textScanCode.getText().toString())) {
//
//                                                    getItem(textScanCode.getText().toString());
//                                                    String objAsJson = tempItem.toJson();
//
////                                                    if (formName.equals("MsoHome")) //from MsoHome -> Stock Info -> SalesItemDetailActivity
////                                                    {
////                                                        Intent intent = new Intent(ExchangeItemSearchActivity.this, SalesItemDetailActivity.class);
////                                                        intent.putExtra("formName", "MsoSalesItemSearch");
////                                                        intent.putExtra("_itemObject", objAsJson);
////                                                        startActivity(intent);
////
////                                                        mProgressDialog.dismiss();
////                                                    }
////                                                    if (formName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_in))) //from MvsHome -> Stock Transfer In -> SalesItemDetailActivity
////                                                    {
////                                                        Intent intent = new Intent(ExchangeItemSearchActivity.this, SalesItemDetailActivity.class);
////                                                        intent.putExtra("formName", "MvsTransferInItemSearch");
////                                                        intent.putExtra("_itemObject", objAsJson);
////                                                        startActivity(intent);
////
////                                                        mProgressDialog.dismiss();
////                                                    }
//                                                     /*Intent intent = new Intent(SalesItemSearchActivity.this, SalesItemDetailActivity.class);
//                                                        intent.putExtra("formName", "MsoSalesItemSearch");
//                                                        intent.putExtra("_itemObject", objAsJson);
//                                                        startActivity(intent);*/
//
//                                                    Intent intent = new Intent(ExchangeItemSearchActivity.this, SalesItemDetailActivity.class);
//                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    intent.putExtra("_itemObject", objAsJson);
//                                                    startActivity(intent);
//
//                                                    mProgressDialog.dismiss();
//
//                                                } else {
//                                                    mProgressDialog.dismiss();
//                                                    showMessageBox("Alert", "Invalid barcode");
//                                                }
//
//                                                mProgressDialog.dismiss();
//
//                                                textScanCode.setText("");
//                                                textScanCode.requestFocus();
//                                                isStarted = false;
//                                            }
//                                        }
//                                    }, 1000);
//
//                            return true;
//                        default:
//                            break;
//                    }
//                }
//                return false;
//            }
//        });

//        if (formName.equals(getResources().getString(R.string.form_name_mso_sales_order)) &&
//                details.equals(getResources().getString(R.string.intent_extra_add_new_item))) {
//            textScanCode.setEnabled(false);
//            textScanCode.setVisibility(View.GONE);
//        }
//        if (formName.equals(getResources().getString(R.string.form_name_mvs_stock_Request)) &&
//                details.equals(getResources().getString(R.string.intent_extra_add_new_item))) {
//            textScanCode.setEnabled(false);
//            textScanCode.setVisibility(View.GONE);
//        }
//        if (formName.equals(getResources().getString(R.string.form_name_mvs_sales_order)) &&
//                details.equals(getResources().getString(R.string.intent_extra_add_new_item))) {
//            textScanCode.setEnabled(false);
//            textScanCode.setVisibility(View.GONE);
//        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj = new Date();
        mDateToday = df.format(dateObj).toString();

        this.mLog = Logger.getLogger(ExchangeItemSearchActivity.class);


    }

    //Create by lasith , Create ZIP file for sending backup
//    private void createBackupClass() {
//        mProgressDialog.setMessage("Uploading Backup to Server...");
//        mProgressDialog.show();
//
//        String android_id = Settings.Secure.getString(getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//        dbBackupPath = getExternalFilesDir(null).getAbsolutePath() + "/";
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//        String todayDate = format.format(new Date());
//        dbName = mApp.getmCurrentDriverCode() + "_" + android_id + "_" + todayDate + ".zip";
//        zipManager = new ZipManager(dbBackupPath, dbName);
////        callBackupDB();
//    }

    private TextWatcher searchTextWatcher() {

        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                mProgressDialog.setMessage("Loading...");
                itemSearchText = txtItemSearch.getText().toString();

                if (!itemSearchText.isEmpty()) {

                    //mProgressDialog.show();





//                    if (mApp.getmCurrentModule().equals(getResources().getString(R.string.module_mso))) {
//                        salesItemSearchAdapter = new SalesItemSearchAdapter(getSearchResult()
//                                , extras
//                                , R.layout.sales_item_search_detail_card
//                                , ExchangeItemSearchActivity.this);
//                        recyclerViewSalesItem.setAdapter(salesItemSearchAdapter);
//                    } else {
                        salesItemSearchAdapter = new ExchangeItemSearchAdapter(getSearchResult()
                                , extras
                                , R.layout.list_exchange_item_search_detail_card
                                , ExchangeItemSearchActivity.this);
                        recyclerViewSalesItem.setAdapter(salesItemSearchAdapter);
//                    }

                    mProgressDialog.dismiss();
                } else {
                    mProgressDialog.show();

//                    if (mApp.getmCurrentModule().equals(getResources().getString(R.string.module_mso))) {
//                        salesItemSearchAdapter = new SalesItemSearchAdapter(itemList
//                                , extras
//                                , R.layout.sales_item_search_detail_card
//                                , ExchangeItemSearchActivity.this);
//                        recyclerViewSalesItem.setAdapter(salesItemSearchAdapter);
//                    } else {
                        salesItemSearchAdapter = new ExchangeItemSearchAdapter(itemList
                                , extras
                                , R.layout.list_exchange_item_search_detail_card
                                , ExchangeItemSearchActivity.this);
                        recyclerViewSalesItem.setAdapter(salesItemSearchAdapter);
//                    }

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.sales_item_search_menu, menu);
//
//        MenuItem mMITransferStock = menu.getItem(1);
//
//        SpannableString mSpannableStringTransferStock = new SpannableString(mMITransferStock.getTitle().toString());
//
//        mSpannableStringTransferStock.setSpan(new ForegroundColorSpan(Color.GRAY)
//                , 0
//                , mSpannableStringTransferStock.length()
//                , 0);
//
//        //getStock status
//        StockStatusDbHandler ssDb = new StockStatusDbHandler(getApplicationContext());
//        ssDb.open();
//        StockStatus stockStatus = ssDb.getStockStatus(mDateToday);
//        ssDb.close();
//
//        if (stockStatus.isDispatched() != null) {
//            if (!stockStatus.isDispatched()) {
//                mMITransferStock.setEnabled(false);
//                mMITransferStock.setTitle(mSpannableStringTransferStock);
//            }
//        }
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {


        //end Item Balance
//        if (syncStatus.getScope().equals(getResources().getString(R.string
//                .SyncScopeDownItemBalance))) {
//            mProgressDialog.dismiss();
//
//            //refresh menu
//            getFragmentManager().invalidateOptionsMenu();
//
//            //refresh itemList
//            searchItemCode = "";
//            searchCategoryCode = "";
//            searchItemDescription = "";
//            categoryDescription = "";
//            txtItemSearch.setText("");
//
//            xGetSalesItemTask = new GetSalesItemTask();
//            xGetSalesItemTask.execute((Void) null);
//            swipyRefreshLayoutSalesItem.setRefreshing(false);
//
//            Toast.makeText(mApp, "Updated!", Toast.LENGTH_SHORT).show();
//
//            //email stock balance
//            Intent intentEmailReport
//                    = new Intent(ExchangeItemSearchActivity.this
//                    , EmailStockBalanceSummaryActivity.class);
//            intentEmailReport.putExtra("details", "Load");
//            startActivity(intentEmailReport);
//
//
//        } else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeCreateStockBalSumPdf))) {
//            if (syncStatus.isStatus()) {
//                String value = new String(syncStatus.getMessage());
//
//                String[] parts = value.split("#");
//
//                mReportDate = parts[1];
//                mReportFileName = parts[0];
//
//                startUploadPdf(mReportFileName);
//            } else {
//                Toast.makeText(mApp, syncStatus.getMessage(), Toast.LENGTH_SHORT).show();
//                mProgressDialog.dismiss();
//            }
//
//        } else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopePdf))) {
//
//            if (syncStatus.isStatus()) {
//                startSendEmail("UnLoad", mReportFileName, mReportDate);
//            } else {
//                Toast.makeText(mApp, syncStatus.getMessage(), Toast.LENGTH_SHORT).show();
//                mProgressDialog.dismiss();
//            }
//            createBackupClass();
//        } else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeSendEmail))) {
//
//            mReportFileName = "";
//            mReportDate = "";
//
//            if (syncStatus.isStatus()) {
//                Toast.makeText(mApp, "Email Sent.", Toast.LENGTH_SHORT).show();
//                if (!isFinishing()) {
//                    mProgressDialog.setMessage("Resetting...");
//                    mProgressDialog.show();
//                }
//                //reset item balance in db
//                mResetItemBalancePdaTask = new ResetItemBalancePdaTask();
//                mResetItemBalancePdaTask.execute((Void) null);
//
//            } else {
//                Toast.makeText(mApp, "Email Sending Failed.", Toast.LENGTH_SHORT).show();
//                mProgressDialog.dismiss();
//            }
//        } else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeDownLoadItemImage))) {
//            mProgressDialog.dismiss();
//            mAlertDialogBuilder = new AlertDialog.Builder(ExchangeItemSearchActivity.this);
//            mAlertDialogBuilder
//                    .setTitle("Item Image Download Finished!")
//                    //.setMessage("Item Image Download Finished!")
//                    .setCancelable(false)
//                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//
//                            dialog.cancel();
//
//                        }
//                    });
//            mAlertDialog = mAlertDialogBuilder.create();
//            mProgressDialog.dismiss();
//            mAlertDialog.show();
//        }
    }

//    private void startSendEmail(String details, String fileName, String reportDate) {
//
//        String subjectParam = "", bodyParam = "";
//        if (details.equals("")) {
//            subjectParam = " - ";
//            bodyParam = "";
//        } else {
//            subjectParam = " - " + details + " - ";
//            bodyParam = " - " + details;
//        }
//
//        String mSubject = ""
//                + "Stock Summary" + subjectParam
//                + mApp.getmCurrentDriverCode() + " - "
//                + reportDate;
//
//        String mBody = ""
//                + "Report: Stock Summary" + bodyParam + '\n'
//                + "Driver: " + mApp.getmCurrentDriverCode() + "\n"
//                + "Date :" + reportDate;
//
//        mSendEmailSyncTask = new SendEmailSyncTask(getApplicationContext(), true, reportDate, fileName, mBody, mSubject);
//        mSendEmailSyncTask.delegate = ExchangeItemSearchActivity.this;
//        mSendEmailSyncTask.execute((Void) null);
//    }
//
//    private void startUploadPdf(String fileName) {
//        mPdfUploadSyncTask = new PdfUploadSyncTask(getApplicationContext(), true, fileName);
//        mPdfUploadSyncTask.delegate = ExchangeItemSearchActivity.this;
//        mPdfUploadSyncTask.execute((Void) null);
//    }

    private boolean isItemAvailable(String itemIdentifireCode) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        status = dbAdapter.isItemExistByIdentifierCode(itemIdentifireCode);

        dbAdapter.close();
        return status;
    }

    private void getItem(String itemCode) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        tempItem = dbAdapter.getItemByIdentifierCode(itemCode);

        dbAdapter.close();
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

//    private int dpToPx(int dp) {
//        Resources r = getResources();
//        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
//    }

//    private void getCategoryList() {
//        ItemCategoryDbHandler dbAdapter = new ItemCategoryDbHandler(this);
//        dbAdapter.open();
//        //Log.d("Reading: ", "Reading all contacts..");
//
//        itemCategoryList = dbAdapter.getAllCategories();
//
//        //Set Spinner Top item Empty
//        ItemCategory emptyItem = new ItemCategory();
//        emptyItem.setDescription("");
//        emptyItem.setItemCategoryCode("");
//        itemCategoryList.add(0, emptyItem);
//
//
//        for (ItemCategory itemCategoryResult : itemCategoryList) {
//            Log.d("CATEGORY RECEIVED: ",
//                    itemCategoryResult.getDescription() + " \n");
//        }
//
//        dbAdapter.close();
//
//    }

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

//    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection


//        switch (item.getItemId()) {
//            case R.id.action__downloadImages:
//
//                if (isNetworkConnected()) {
//                    new AlertDialog.Builder(this)
//                            .setTitle("Data Sync")
//                            .setMessage(getResources().getString(R.string
//                                    .notification_msg_confirmation_image_sync))
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .setPositiveButton(android.R.string.yes, new DialogInterface
//                                    .OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    mProgressDialog.setMessage("Downloading...");
//                                    mProgressDialog.show();
//
//                                    //Toast.makeText(this,"dl images",Toast.LENGTH_LONG).show();
//                                    ItemImageSync itemSync = new ItemImageSync(getApplicationContext());
//                                    itemSync.delegate = ExchangeItemSearchActivity.this;
//                                    itemSync.execute((Void) null);
//                                }
//                            })
//                            .setNegativeButton(android.R.string.no, null).show();
//
//                } else {
//                    new androidx.appcompat.app.AlertDialog.Builder(this)
//                            .setTitle(getResources().getString(R.string
//                                    .notification_title_no_connection))
//                            .setMessage(getResources().getString(R.string
//                                    .notification_msg_no_connection))
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//
//                                }
//                            })
//                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .setCancelable(false)
//                            .show();
//                }
//
//                return true;
//            case R.id.action_resetQty:
//                new AlertDialog.Builder(this)
//                        .setTitle("Print Stock Balance")
//                        .setMessage("Do you want to print stock balance?")
//                        .setPositiveButton(getResources().getString(R.string.notification_btn_txt_print)
//                                , new DialogInterface
//                                        .OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                        dialog.dismiss();
//                                        printStockBalance();
//                                    }
//                                })
//                        .setNegativeButton(android.R.string.no, new DialogInterface
//                                .OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();
//                return true;
//
//            case R.id.action_transfer_stock:
//                new androidx.appcompat.app.AlertDialog.Builder(this)
//                        .setTitle("Stock Transfer")
//                        .setMessage("Do you want to transfer stock?")
//                        .setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                transferStock();
////                                callBackupDB();
//
//                            }
//                        })
//                        .setNegativeButton(getResources().getString(R.string.button_text_no), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                dialog.dismiss();
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setCancelable(false)
//                        .show();
//                return true;
//
//
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }

//    }

//    public void transferStock() {
//        if (isNetworkConnected()) {
//            mProgressDialog.setMessage("Downloading Item Balances...");
//            mProgressDialog.show();
//            StockStatusDbHandler ssDb = new StockStatusDbHandler(getApplicationContext());
//            ssDb.open();
//
//            //clear records
//            ssDb.clearTable();
//            Log.d("STOCK STATUS :", " CLEARED");
//
//            StockStatus stockStatus = new StockStatus();
//            stockStatus.setDispatched(false);
//            stockStatus.setLoadDate(mDateToday);
//
//            ssDb.addItems(stockStatus);
//            Log.d("STOCK STATUS :", " ADDED, " + mDateToday);
//
//            ssDb.close();
//            startItemBalanceDownload();
//        } else {
//            new androidx.appcompat.app.AlertDialog.Builder(this)
//                    .setTitle(getResources().getString(R.string
//                            .notification_title_no_connection))
//                    .setMessage(getResources().getString(R.string
//                            .notification_msg_no_connection))
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//
//                        }
//                    })
//                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            dialog.dismiss();
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setCancelable(false)
//                    .show();
//        }
//    }

//    private void startItemBalanceDownload() {
//        mItemBalanceVehicleSyncTask = new ItemBalanceVehicleSyncTask(getApplicationContext(), true);
//        mItemBalanceVehicleSyncTask.delegate = ExchangeItemSearchActivity.this;
//        mItemBalanceVehicleSyncTask.execute((Void) null);
//    }

//    private void printStockBalance() {
//        //print stock balance
//        StockStatus stockStatus = new StockStatus();
//        String selectedPrinter = mPrefHelper.getString(R.string.pref_select_printer_key);
////        Intent intent = new Intent(SalesItemSearchActivity.this
////                , PrintStockBalanceSummaryActivity.class);
//        Intent intent;
//        if (selectedPrinter.equals(getResources().getString(R.string.printer_honeywell))) {
//            intent
//                    = new Intent(ExchangeItemSearchActivity.this, PrintStockBalanceSummaryActivity.class);
//
//        } else {
//            intent
//                    = new Intent(ExchangeItemSearchActivity.this, PrintStockBalanceSummaryRP4Activity.class);
//        }
//        intent.putExtra("details", "EmailReport");
//        startActivity(intent);
//
//        //getStock status
//        StockStatusDbHandler ssDb = new StockStatusDbHandler(getApplicationContext());
//        ssDb.open();
//        stockStatus = ssDb.getStockStatus(mDateToday);
//        ssDb.close();
//
//        //if no records in ss
//        if (stockStatus.isDispatched() == null) {
//            Drawable warningIcon = new IconicsDrawable(this)
//                    .icon(FontAwesome.Icon.faw_exclamation_triangle)
//                    .color(Color.RED).sizeDp(30);
//
//            new AlertDialog.Builder(this)
//                    .setTitle(Html.fromHtml("<font color="
//                            + getResources().getColor(R.color.errorRed)
//                            + ">Reset Item Balances</font>"))
//                    .setMessage(getResources().getString(R.string
//                            .notification_msg_confirmation_item_reset))
//                    .setIcon(warningIcon)
//                    .setCancelable(false)
//                    .setPositiveButton(getResources().getString(R.string.notification_btn_txt_unload)
//                            , new DialogInterface
//                                    .OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//
//                                    createStockBalSumReportPdf();
//
//                                }
//                            })
//                    .setNegativeButton(android.R.string.no, null).show();
//        } else {
//            if (!stockStatus.isDispatched()) {
//                Drawable warningIcon = new IconicsDrawable(this)
//                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
//                        .color(Color.RED).sizeDp(30);
//
//                new AlertDialog.Builder(this)
//                        .setTitle(Html.fromHtml("<font color="
//                                + getResources().getColor(R.color.errorRed)
//                                + ">Reset Item Balances</font>"))
//                        .setMessage(getResources().getString(R.string
//                                .notification_msg_confirmation_item_reset))
//                        .setIcon(warningIcon)
//                        .setCancelable(false)
//                        .setPositiveButton(getResources().getString(R.string.notification_btn_txt_unload)
//                                , new DialogInterface
//                                        .OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                                        createStockBalSumReportPdf();
//                                    }
//                                })
//                        .setNegativeButton(android.R.string.no, null).show();
//            }
//        }
//    }

    //Changed 2018-05-31, Buddhika
//    public void createStockBalSumReportPdf() {
//        if (isNetworkConnected()) {
//            mProgressDialog.setMessage("Sending email...");
//            mProgressDialog.show();
//
//            mCreateStockBalanceSummaryPdfSyncTask = new CreateStockBalanceSummaryPdfSyncTask(getApplicationContext()
//                    , "Unload");
//            mCreateStockBalanceSummaryPdfSyncTask.mDelegate = ExchangeItemSearchActivity.this;
//            mCreateStockBalanceSummaryPdfSyncTask.execute((Void) null);
//        } else {
//            new androidx.appcompat.app.AlertDialog.Builder(this)
//                    .setTitle(getResources().getString(R.string
//                            .notification_title_no_connection))
//                    .setMessage(getResources().getString(R.string
//                            .notification_msg_no_connection))
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//
//                        }
//                    })
//                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            dialog.dismiss();
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setCancelable(false)
//                    .show();
//        }
//
//    }

//    private void showSearchDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
//        final Dialog dialog = builder.create();
//
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        dialog.show();
//        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//
//        Window window = dialog.getWindow();
//        ;
//        window.setGravity(Gravity.CENTER);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        dialog.getWindow()
//                .setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        window.setDimAmount(0.2f);
//
//        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
//        dialog.setContentView(R.layout.dialog_stock_order_list_search);
//        dialog.setCancelable(false);
//
//        final EditText txtItemCode = (EditText) dialog.findViewById(R.id.editItemCode);
//        final Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
//        final EditText txtItemDescription = (EditText) dialog.findViewById(R.id.txtItemDescription);
//        final TextView lblDialogHeader = (TextView) dialog.findViewById(R.id.lblDialogHeader);
//
//        lblDialogHeader.setText("Item Search");
//        String[] categoryItems = new String[]{};
//
//        if (!itemCategoryList.isEmpty()) {
//            categoryItems = new String[itemCategoryList.size()];
//
//            for (int i = 0; i < itemCategoryList.size(); i++) {
//                categoryItems[i] = itemCategoryList.get(i).getDescription().toString();
//            }
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryItems);
//        dropdown.setAdapter(adapter);
//
//        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //Setting Paramters for GetSalesItemTask
//                categoryDescription = dropdown.getSelectedItem().toString();
//                searchItemCode = txtItemCode.getText().toString();
//                searchItemDescription = txtItemDescription.getText().toString();
//
//                for (int i = 0; i < itemCategoryList.size(); i++) {
//                    if (itemCategoryList.get(i).getDescription().equals(categoryDescription)) {
//                        searchCategoryCode = itemCategoryList.get(i).getItemCategoryCode();
//                    }
//                }
//                //customerNo = "";
//
//                xGetSalesItemTask = new GetSalesItemTask();
//                xGetSalesItemTask.execute((Void) null);
//                dialog.dismiss();
//            }
//        });
//        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                isSearchButtonClicked = false;
//            }
//        });
//
//        dropdown.setSelection(getIndex(dropdown, categoryDescription));
//        txtItemCode.setText(searchItemCode);
//        txtItemDescription.setText(searchItemDescription);
//
//        dialog.show();
//    }

//    private int getIndex(Spinner spinner, String myString) {
//
//        int index = 0;
//
//        int count = spinner.getCount();
//
//        for (int i = 0; i < spinner.getCount(); i++) {
//            if (spinner.getItemAtPosition(i).equals(myString)) {
//                index = i;
//            }
//        }
//        return index;
//    }

//    public void searchItemsByCustomerPriceGroup(String searchCustomerPriceGroup,
//                                                String categoryCode,
//                                                String itemCode,
//                                                String itemDescription) {
//        ItemDbHandler dbAdapter = new ItemDbHandler(this);
//        dbAdapter.open();
//
//        itemList = new ArrayList<Item>();
//        itemList = dbAdapter.getItemsByCustomerPriceGroup(searchCustomerPriceGroup, categoryCode, itemCode, itemDescription);
//
//        //if item count is 0 then get all items
//        /*if(itemList.size() == 0)
//        {
//            itemList = dbAdapter.getSearchItems("","","");
//        }*/
//
//        //set image url
//
//        for (int i = 0; i < itemList.size(); i++) {
//            Item tempItem = itemList.get(i);
//            tempItem.setItemImageUrl(getItemImageUrl(tempItem.getItemCode().toString()));
//            itemList.set(i, tempItem);
//        }
//
//        /*for (Item itemResult : itemList) {
//            Log.d("Searched Item: ",
//                    itemResult.getDescription() + " \n");
//        }*/
//
//        dbAdapter.close();
////        updateItemBalencePda();
//    }
//
//    public void searchItemTemplateByCustomerPriceGroup(String searchCustomerPriceGroup,
//                                                       String categoryCode,
//                                                       String itemCode,
//                                                       String itemDescription) {
//
//        ItemDbHandler dbAdapter = new ItemDbHandler(this);
//        dbAdapter.open();
//
//        itemList = new ArrayList<Item>();
//        itemList = dbAdapter.getItemTemplateByCustomerPriceGroup(
//                tempCustomer.getCustomerPriceGroup(),
//                categoryCode,
//                itemCode,
//                itemDescription,
//                tempCustomer.getCode());
//
//
//        for (int i = 0; i < itemList.size(); i++) {
//            Item tempItem = itemList.get(i);
//            tempItem.setItemImageUrl(getItemImageUrl(tempItem.getItemCode().toString()));
//            itemList.set(i, tempItem);
//        }
//
//        dbAdapter.close();
//    }
//
//    //added newly
//    public void searchItemsByCustomerTemplate(String searchCustomerPriceGroup,
//                                              String categoryCode,
//                                              String itemCode,
//                                              String itemDescription) {
//        ItemDbHandler dbAdapter = new ItemDbHandler(this);
//        dbAdapter.open();
//
//        itemList = new ArrayList<Item>();
//        itemList = dbAdapter.getItemsByCustomerTemplate(searchCustomerPriceGroup,
//                categoryCode,
//                itemCode,
//                itemDescription,
//                tempCustomer.getCode());
//
//        //if item count is 0 then get all items
//        /*if(itemList.size() == 0)
//        {
//            itemList = dbAdapter.getSearchItems("","","");
//        }*/
//
//        //set image url
//
//        for (int i = 0; i < itemList.size(); i++) {
//            Item tempItem = itemList.get(i);
//            tempItem.setItemImageUrl(getItemImageUrl(tempItem.getItemCode().toString()));
//            itemList.set(i, tempItem);
//        }
//
//        /*for (Item itemResult : itemList) {
//            Log.d("Searched Item: ",
//                    itemResult.getDescription() + " \n");
//        }*/
//
//        dbAdapter.close();
//    }


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

//    public void searchItemsBySalesPriceForMvsSI(String categoryCode,
//                                                String itemCode,
//                                                String itemDescription) {
//        ItemDbHandler dbAdapter = new ItemDbHandler(this);
//        dbAdapter.open();
//
//        itemList = new ArrayList<Item>();
//        itemList = dbAdapter.getItemsBySalesPrice(categoryCode, itemCode, itemDescription);
//
//        //if item count is 0 then get all items
//        /*if(itemList.size() == 0)
//        {
//            itemList = dbAdapter.getSearchItems("","","");
//        }*/
//
//        //set image url
//
//        for (int i = 0; i < itemList.size(); i++) {
//            Item tempItem = itemList.get(i);
//            tempItem.setItemImageUrl(getItemImageUrl(tempItem.getItemCode().toString()));
//            itemList.set(i, tempItem);
//        }
//
//        /*for (Item itemResult : itemList) {
//            Log.d("Searched Item: ",
//                    itemResult.getDescription() + " \n");
//        }*/
//
//        dbAdapter.close();
//        updateItemBalencePda();
//    }

//    public static int getScreenWidth(Activity activity) {
//        Point size = new Point();
//        activity.getWindowManager().getDefaultDisplay().getSize(size);
//        return size.x;
//    }

//    public void updateItemBalencePda() {
//        if (itemList != null) {
//            if (!itemList.isEmpty()) {
//                ItemBalancePda itemPdaObj;
//                for (Item item : itemList) {
//                    ItemBalancePdaDbHandler ibpDb
//                            = new ItemBalancePdaDbHandler(getApplicationContext());
//                    ibpDb.open();
//                    itemPdaObj = ibpDb.getItemBalencePda(item.getItemCode(), item.getItemBaseUom(), mApp.getmCurrentDriverCode());
//                    ibpDb.close();
//                    if (item.getItemCode().equals("IST02A") || item.getItemCode().equals("TBKFN02B")) {
//                        System.out.println("ok");
//                    }
//                    float qty = itemPdaObj.getOpenQty() - itemPdaObj.getQuantity();
//                    item.setQty(qty);
//                    item.setExchQty(itemPdaObj.getExchangedQty());
//                }
//
//                //sort item list by vehicle qty - Descending
//                Collections.sort(itemList, ComparatorItemExchQty);
//                Collections.sort(itemList, ComparatorItemVclQty);
//            }
//        }
//    }

//    public static Comparator<Item> ComparatorItemVclQty = new Comparator<Item>() {
//
//        public int compare(Item item1, Item item2) {
//
//            int qty1 = Math.round(item1.getQty());
//            int qty2 = Math.round(item2.getQty());
//
//            /*For descending order*/
//            return qty2 - qty1;
//        }
//    };
//    public static Comparator<Item> ComparatorItemExchQty = new Comparator<Item>() {
//
//        public int compare(Item item1, Item item2) {
//
//            int qty1 = Math.round(item1.getExchQty());
//            int qty2 = Math.round(item2.getExchQty());
//
//            /*For descending order*/
//            return qty2 - qty1;
//        }
//    };

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
//    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
//
//        private int spanCount;
//        private int spacing;
//        private boolean includeEdge;
//
//        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
//            this.spanCount = spanCount;
//            this.spacing = spacing;
//            this.includeEdge = includeEdge;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            int position = parent.getChildAdapterPosition(view); // item position
//            int column = position % spanCount; // item column
//
//            if (includeEdge) {
//                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
//                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
//
//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
//                outRect.bottom = spacing; // item bottom
//            } else {
//                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
//                if (position >= spanCount) {
//                    outRect.top = spacing; // item top
//                }
//            }
//        }
//    }
//
    public class GetSalesItemTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
//                if (!customerPriceGroup.equals("") && formName.equals("MsoSalesOrder")) {
//                    searchItemsByCustomerPriceGroup(customerPriceGroup, searchCategoryCode, searchItemCode, searchItemDescription);
//                } else if (customerPriceGroup.equals("") && formName.equals("MsoSalesOrder")) {
//                    searchItemsByCustomerPriceGroup(customerPriceGroup, searchCategoryCode, searchItemCode, searchItemDescription);
//                } else if (formName.equals(getResources().getString(R.string.form_name_mvs_stock_Request)) || formName.equals(getResources().getString(R.string.form_name_mvs_sales_order))) {
//                    searchItemsByCustomerPriceGroup(customerPriceGroup, searchCategoryCode, searchItemCode, searchItemDescription);
//                } else
//
//                 if (formName.equals("MvsHome")) {
//                    searchItemsBySalesPriceForMvsSI(searchCategoryCode, searchItemCode, searchItemDescription);
//                } else {
                    searchItems();
//                }

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

//            if (mApp.getmCurrentModule().equals(getResources().getString(R.string.module_mso))) {
//                salesItemSearchAdapter = new SalesItemSearchAdapter(new ArrayList<Item>()
//                        , extras
//                        , R.layout.sales_item_search_detail_card
//                        , ExchangeItemSearchActivity.this);
//            } else {
                salesItemSearchAdapter = new ExchangeItemSearchAdapter(new ArrayList<ExchangeItem>()
                        , extras
                        , R.layout.list_exchange_item_search_detail_card
                        , ExchangeItemSearchActivity.this);
//            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {

//                    if (mApp.getmCurrentModule().equals(getResources().getString(R.string.module_mso))) {
//                        salesItemSearchAdapter = new SalesItemSearchAdapter(itemList
//                                , extras
//                                , R.layout.sales_item_search_detail_card
//                                , ExchangeItemSearchActivity.this);
//                        recyclerViewSalesItem.setAdapter(salesItemSearchAdapter);
//                    } else {
                        salesItemSearchAdapter = new ExchangeItemSearchAdapter(itemList
                                , extras
                                , R.layout.list_exchange_item_search_detail_card
                                , ExchangeItemSearchActivity.this);
                        recyclerViewSalesItem.setAdapter(salesItemSearchAdapter);
//                    }

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

//    public boolean resetItemBalances(Context context) {
//        boolean isSuccess = false;
//
//        ItemBalancePdaDbHandler ibpDb = new ItemBalancePdaDbHandler(context);
//        ibpDb.open();
//
//        isSuccess = ibpDb.resetItemBalancePda(mApp.getmCurrentDriverCode());
//
//        ibpDb.close();
//
//        if (isSuccess) {
//            if (!itemList.isEmpty()) {
//                for (int index = 0; index < itemList.size(); index++) {
//                    Item item = itemList.get(index);
//
//                    item.setQty(new Float(0));
//                    item.setExchQty(new Float(0));
//
//                    itemList.set(index, item);
//                }
//            }
//        }
//        return isSuccess;
//    }

//    public class ResetItemBalancePdaTask extends AsyncTask<Void, Void, Boolean> {
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            try {
//
//                resetItemBalances(getApplicationContext());
//                mLog.info("RESET_ITEM_BALANCES: SUCCESS");
//
//                isItemBalPdaReset = true;
//
//            } catch (Exception e) {
//                Log.d("Exception", e.toString());
//                mLog.info("RESET_ITEM_BALANCES: FAILED");
//                return false;
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            if (success) {
//
//                try {
//                    if (itemList.isEmpty()) {
//                        Toast.makeText(ExchangeItemSearchActivity.this, "No Items To Show!", Toast.LENGTH_SHORT).show();
//                        mProgressDialog.dismiss();
//                    } else {
//                        salesItemSearchAdapter.notifyDataSetChanged();
//                        Toast.makeText(ExchangeItemSearchActivity.this, "Completed!", Toast.LENGTH_SHORT).show();
//                        mProgressDialog.dismiss();
//                    }
//
//                    if (isItemBalPdaReset) {
//                        StockStatusDbHandler ssDb
//                                = new StockStatusDbHandler(getApplicationContext());
//                        ssDb.open();
//                        ssDb.updateStockStatus(mDateToday, isItemBalPdaReset);
//                        ssDb.close();
//                        isItemBalPdaReset = false;
//                        getFragmentManager().invalidateOptionsMenu();
//                    }
//
//                } catch (Exception ex) {
//                    mProgressDialog.dismiss();
//                }
//            } else {
//                mProgressDialog.dismiss();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mProgressDialog.dismiss();
//        }
//    }
//
//    //Create by lasith , When clear stock we upload backup to server
//    private void callBackupDB() {
//        final File file = new File(dbBackupPath + dbName);
//        RequestBody body =
//                RequestBody.create(MediaType.parse("application/octet-stream")
//                        , file);
//        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("File", file.getName(), body);
//
//        Call call = mApp.getNavBrokerService().uploadDbBackup(multipartBody);
//        call.enqueue(new Callback<PostDbBackupResponse>() {
//            @Override
//            public void onResponse(Call<PostDbBackupResponse> call
//                    , Response<PostDbBackupResponse> response) {
//                mProgressDialog.dismiss();
//                file.delete();
//
//                try {
//                    mLog.info("DB_BACKUP: SUCCESS");
//
//                    Toast.makeText(ExchangeItemSearchActivity.this, "Backup Successfully!", Toast.LENGTH_LONG).show();
//
//                } catch (Exception e) {
//                    Toast.makeText(ExchangeItemSearchActivity.this, "Backup Failed!", Toast.LENGTH_LONG).show();
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PostDbBackupResponse> call, Throwable t) {
//                file.delete();
//                mProgressDialog.dismiss();
//                Toast.makeText(ExchangeItemSearchActivity.this, "Backup Failed!", Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }

}