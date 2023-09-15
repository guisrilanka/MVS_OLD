package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MsoSalesOrderAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentHeader;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.datamodel.SalesPrices;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.GSTPostingSetupDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.PaymentHeaderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesPricesDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderDeliveryUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderImageUploadSyncTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MsoSalesOrderActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,AsyncResponse {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE = 1;
    private static final int MSO_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE = 2;
    private static final int SALES_ITEM_ACTIVITY_RESULT_CODE = 3;
    String listType = "", soNo, deliveryDate = "",mModule="";
    Toolbar myToolbar;
    ArrayList<SalesOrderLine> dataModels;
    RecyclerView recyclerViewSalesOrderDetails;
    Button btnSearch, btnItem, btnSummary, btnSearchCus, btnDatePicker, btnContactCus, btnAddressCus, btnSave, btnClear;
    LinearLayout llItem, llSummary;
    FloatingActionButton fabAddNewItem;
    int date, month, year;
    boolean isStarted = false, isCustomerChanged = false, isSaved = true, invalidDate = false;
    Item tempItem;
    TextView tvToolbarTitle, txtNumberOfItems, txtTotalQty;
    EditText txtCustomerCode, txtDriverCode, txtCustomer, txtContact, txtAddress, txtGroup, txtDeliveryDate, txtMinimumSalesAmount, txtPoComments, textScanCode,
            txtSubTotal, txtGst, txtGrandTotal;
    List<SalesOrderLine> salesOrderLineList,removeSoList, removeSoListType2;
    Customer tempCustomer, customerObj;
    SalesOrderLine tempSalesOrderLine;

    private MsoSalesOrderAdapter msoSalesOrderAdapter;
    private String details = "";
    private SalesOrder tempSalesOrder;
    private float subTotal = 0, totalVatAmount = 0, grandTotal = 0;
    private int totalQty = 0;
    private NavClientApp mApp;
    private ProgressDialog xProgressDialog;

    //google map
    GoogleApiClient mGoogleApiClient;
    String mylocation, destination;
    private Location mLastLocation;
    private Double mLatitudeText, mLongitudeText, dLatitudeText, dLongideText;
    private boolean mLocationPermissionGranted;

    private SalesOrderDeliveryUploadSyncTask salesOrderDeliveryUploadSyncTask;


    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View view = View.inflate(this, R.layout.activity_mso_sales_order, null);
        setContentView(view);
        createView(view);

        mApp = (NavClientApp) getApplicationContext();

        txtCustomer.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        txtAddress.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        txtGroup.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        txtContact.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        txtPoComments.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });

        xProgressDialog = new ProgressDialog(this);
        xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingButtonDrawables();
        setSummaryTab();
        fabAddNewItem.bringToFront();

        fabAddNewItem.setOnClickListener(this);//
        btnSearch.setOnClickListener(this);//
        btnItem.setOnClickListener(this);//
        btnSummary.setOnClickListener(this);//
        btnSave.setOnClickListener(this);//
        btnClear.setOnClickListener(this);//
        btnSearchCus.setOnClickListener(this);//
        btnDatePicker.setOnClickListener(this);//
        btnAddressCus.setOnClickListener(this);//

        //Setting White color for editable fields
        txtCustomer.setBackgroundColor(getResources().getColor(R.color.white));
        txtDeliveryDate.setBackgroundColor(getResources().getColor(R.color.white));
        txtPoComments.setBackgroundColor(getResources().getColor(R.color.white));

        recyclerViewSalesOrderDetails.setHasFixedSize(true);
        recyclerViewSalesOrderDetails.setLayoutManager(new LinearLayoutManager(this));

        removeSoList = new ArrayList<SalesOrderLine>();
        removeSoListType2 = new ArrayList<SalesOrderLine>();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(getResources().getString(R.string.list_type))) {
                listType = extras.getString(getResources().getString(R.string.list_type));
            }
            if(extras.containsKey(getResources().getString(R.string.module))){
                mModule = extras.getString(getResources().getString(R.string.module));
            }

            if (extras.containsKey(getResources().getString(R.string.sales_order_jason_obj))) {

                String objAsJson = extras.getString(getResources().getString(R.string.sales_order_jason_obj));
                tempSalesOrder = SalesOrder.fromJson(objAsJson);
                deliveryDate = tempSalesOrder.getShipmentDate();
                //Get Salesorderline List
                getSalesOrderLineList(tempSalesOrder.getNo());

                //Get Customer Details
                getCustomer(tempSalesOrder.getSelltoCustomerNo());

                tvToolbarTitle.setText(tempSalesOrder.getNo());
                txtCustomer.setText(tempSalesOrder.getSelltoCustomerName());
                txtCustomerCode.setText(tempSalesOrder.getSelltoCustomerNo());
                txtContact.setText(tempSalesOrder.getSelltoContactNo());
                txtAddress.setText(tempSalesOrder.getSelltoAddress());
                txtGroup.setText(tempCustomer.getCustomerPriceGroup());
                txtPoComments.setText(tempSalesOrder.getExternalDocumentNo());

                //date conversion
                if (tempSalesOrder.getShipmentDate() != null) {
                    if (!tempSalesOrder.getShipmentDate().equals("")) {
                        String date_ = tempSalesOrder.getShipmentDate().toString();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_dd_MM_yyyy));
                        try {

                            Date initDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(tempSalesOrder.getShipmentDate().toString());
                            date_ = simpleDateFormat.format(initDate);

                        } catch (Exception e) {
                            Log.e(getResources().getString(R.string.message_exception), e.getMessage().toString());
                        }

                        txtDeliveryDate.setText(date_);
                    }
                }

                txtMinimumSalesAmount.setText(String.valueOf(tempCustomer.getMinimumSalesAmount()));
                txtContact.setText(tempCustomer.getPhoneNo());
                txtDriverCode.setText(tempCustomer.getDriverCode());

                updateSummeryValues(salesOrderLineList);

                //---------------------------------  Item List (Sales Order Line) -----------------------------------
                clearRecyclerView();
                setItemTouchHelper();
                if (salesOrderLineList.size() > 0) {
                    msoSalesOrderAdapter = new MsoSalesOrderAdapter(salesOrderLineList, R.layout.item_sales_order_detail_card, MsoSalesOrderActivity.this, tempSalesOrder.getStatus(), tempCustomer, deliveryDate);
                    recyclerViewSalesOrderDetails.setAdapter(msoSalesOrderAdapter);
                    msoSalesOrderAdapter.notifyDataSetChanged();
                }
            }
            if (listType.equals(getResources().getString(R.string.mso_new_sales_order)))  //New Sales Order
            {
                SimpleDateFormat dBFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));
                SimpleDateFormat uIFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_dd_MM_yyyy));
                //uIFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                clearFields();


                Date dt = new Date();
                DateTime dtOrg = new DateTime(dt);
                DateTime dtPlusOne = dtOrg.plusDays(1);

                Date pasd=dtPlusOne.toDate();

                String todayDate = uIFormat.format(pasd);
                txtDeliveryDate.setText(todayDate);
                deliveryDate = dBFormat.format(pasd);

                /*SimpleDateFormat soNumberFormat = new SimpleDateFormat("yyyy-MM-ddhh:mm:ss");
                soNo = "SO"+soNumberFormat.format(new Date());
                tvToolbarTitle.setText(soNo);*/

                soNo = getLatestSoId(txtDeliveryDate.getText().toString());
                tvToolbarTitle.setText(soNo);
                tempSalesOrder.setNo(soNo);

                tempSalesOrder.setStatus(getResources().getString(R.string.SalesOrderStatusPending));
                clearRecyclerView();
                setItemTouchHelper();
            }
        }
        if (tempSalesOrder.getStatus().equals(getResources().getString(R.string.SalesOrderStatusConfirmed))
                || tempSalesOrder.getStatus().equals(getResources().getString(R.string.SalesOrderStatusComplete))) {
            fabAddNewItem.setVisibility(View.GONE);
            btnClear.setEnabled(false);
            btnSave.setEnabled(false);
            // btnSave.setVisibility(View.GONE);
            fabAddNewItem.setEnabled(false);
            btnSearchCus.setEnabled(false);
            btnDatePicker.setEnabled(false);
            txtPoComments.setEnabled(false);
            textScanCode.setEnabled(false);
            fabAddNewItem.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

            if (mModule.equals(getResources().getString(R.string.module_lds)))
            {
                btnClear.setVisibility(View.INVISIBLE);
                btnSave .setVisibility(View.INVISIBLE);
                fabAddNewItem.setVisibility(View.INVISIBLE);
            }


            Toast.makeText(mApp, getResources().getString(R.string.not_allowed_to_edit), Toast.LENGTH_LONG).show();
        }
        //------------------------  BarCode Scanner  -----------------------------------------------
        textScanCode.setInputType(InputType.TYPE_NULL);
        textScanCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textScanCode.setBackgroundResource(R.drawable.border_red);
                } else {
                    textScanCode.setBackgroundResource(R.drawable.border_gray);
                }
            }
        });

        textScanCode.requestFocus();

        textScanCode.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            isStarted = true;
                            Handler handler = new Handler();
                            xProgressDialog.setMessage(getResources().getString(R.string.progress_dialog__status_loading));
                            xProgressDialog.show();

                            handler.postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            if (isStarted) {
                                                if (isItemAvailable(textScanCode.getText().toString())) {
                                                    if (tempCustomer.getCode() == null) {
                                                        showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                                                    } else {

                                                        getItem(textScanCode.getText().toString());

                                                if (getUnitPrice(tempItem.getItemCode(), tempCustomer.getCustomerPriceGroup(), tempCustomer.getCode(), tempItem.getItemBaseUom()) == 0f) {
                                                    showMessageBox(getResources().getString(R.string.message_title_alert),
                                                            "Item can not be added!, Item unit price is zero");
                                                } else {
                                                }
                                                        addItem();
                                                    }
                                                    xProgressDialog.dismiss();
                                                } else {
                                                    xProgressDialog.dismiss();
                                                    showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_invalid_barcode));
                                                }
                                                textScanCode.setText("");
                                                textScanCode.requestFocus();
                                                isStarted = false;
                                            }
                                        }
                                    }, 1000);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

       /* textScanCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 13) {
                    isStarted = true;
                    Handler handler = new Handler();
                    xProgressDialog.setMessage(getResources().getString(R.string.progress_dialog__status_loading));
                    xProgressDialog.show();

                    handler.postDelayed(
                            new Runnable() {
                                public void run() {
                                    if (isStarted) {
                                        if (isItemAvailable(textScanCode.getText().toString())) {
                                            if (tempCustomer.getCode() == null) {
                                                showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                                            } else {

                                                getItem(textScanCode.getText().toString());

                                                if (getUnitPrice(tempItem.getItemCode(), tempCustomer.getCustomerPriceGroup(), tempCustomer.getCode(), tempItem.getItemBaseUom()) == 0f) {
                                                    showMessageBox(getResources().getString(R.string.message_title_alert),
                                                            "Item can not be added!, Item unit price is zero");
                                                } else {
                                                }
                                                addItem();
                                            }
                                            xProgressDialog.dismiss();
                                        } else {
                                            xProgressDialog.dismiss();
                                            showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_invalid_barcode));
                                        }
                                        textScanCode.setText("");
                                        textScanCode.requestFocus();
                                        isStarted = false;
                                    }
                                }
                            }, 2000);
                }
            }
        });*/

    }

    @Override
    public void onClick(View v) {
        if (findViewById(R.id.fabAddNewItem) == v) {
            if (tempCustomer.getCode() == null || txtDeliveryDate.getText().toString().equals("")) {
                showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                if (tempCustomer.getCode() == null) {
                    txtCustomer.setError(getResources().getString(R.string.message_body_select_customer));
                }
                if (txtDeliveryDate.getText().toString().equals("")) {
                    txtDeliveryDate.setError(getResources().getString(R.string.message_body_select_delivery_date));
                }
            } else {
                String customerJsonObj = tempCustomer.toJson();
                Intent intent = new Intent(getApplication(), SalesItemSearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(getResources().getString(R.string.intent_extra_form_name), getResources().getString(R.string.form_name_mso_sales_order));
                intent.putExtra(getResources().getString(R.string.customer_json_obj), customerJsonObj);
                intent.putExtra("deliveryDate", deliveryDate);
                intent.putExtra(getResources().getString(R.string.is_search_pop_up_need), true);
                intent.putExtra(getResources().getString(R.string.intent_extra_details), getResources().getString(R.string.intent_extra_add_new_item));
                startActivityForResult(intent, SALES_ITEM_ACTIVITY_RESULT_CODE);
            }
        }
        if (findViewById(R.id.btnSearch) == v) {
            showSearchDialog();
        }
        if (findViewById(R.id.btnItem) == v) {
            setItemTab();
        }
        if (findViewById(R.id.btnSummary) == v) {
            setSummaryTab();
        }
        if (findViewById(R.id.btnSave) == v) {

            validateDeliveryDate(deliveryDate);

            if(!invalidDate)
            {
                if(tempCustomer.getCode() != null)
                {
                    if(salesOrderLineList.size()>0)
                    {
                        updateSummeryValues(salesOrderLineList);
                        /*float totalOfAllAmtInclVat = h_grandTotal +
                                getAllSoTotalAmtInclVatByCustomer(tempCustomer.getCode(),deliveryDate , tempSalesOrder.getNo());

                        if(Double.parseDouble(String.valueOf(totalOfAllAmtInclVat))> tempCustomer.getMinimumSalesAmount())
                        {*/
                            if (totalQty != 0) {
                                if (saveSalesOrder()) {
                                    SalesOrder salesOrderObj = tempSalesOrder;
                                    String objAsJson = salesOrderObj.toJson();

                                    Toast.makeText(mApp, getResources().getString(R.string.message_updated_successfully), Toast.LENGTH_SHORT).show();
                                    finish();

                                    Intent intent = new Intent(MsoSalesOrderActivity.this, MsoSalesOrderActivity.class);
                                    intent.putExtra(getResources().getString(R.string.sales_order_jason_obj), objAsJson);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(mApp, getResources().getString(R.string.message_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(mApp, "Total QTY can not be zero!", Toast.LENGTH_SHORT).show();
                            }

                        /*}
                        else
                        {
                            Toast.makeText(mApp, getResources().getString(R.string.message_Minimum_sales_amount_validation), Toast.LENGTH_SHORT).show();
                        }*/


                    }
                    else
                    {
                        Toast.makeText(mApp, getResources().getString(R.string.message_add_at_least_one_item), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    txtCustomer.setError("");
                    Toast.makeText(mApp, getResources().getString(R.string.message_body_select_customer), Toast.LENGTH_SHORT).show();
                }

            }
        }
        if (findViewById(R.id.btnClear) == v) {
            if (salesOrderLineList.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MsoSalesOrderActivity.this);

                builder.setTitle(getResources().getString(R.string.message_title_alert));
                builder.setMessage(getResources().getString(R.string.message_item_qty_will_be_cleared));

                builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        tempSalesOrder.setStatus(getResources().getString(R.string.SalesOrderStatusPending));
                        for (SalesOrderLine sol : salesOrderLineList) {
                            sol.setQuantity(0f);
                            sol.setQtytoInvoice(0f);
                            sol.setTotalVATAmount(0f);
                            sol.setLineAmount(0f);
                            sol.setTotalAmountExclVAT(0f);
                            sol.setTotalAmountInclVAT(0f);
                        }
                        updateSummeryValues(salesOrderLineList);
                        msoSalesOrderAdapter.notifyDataSetChanged();
                        Toast.makeText(mApp, getResources().getString(R.string.message_cleared), Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.button_text_no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        if (findViewById(R.id.btnSearchCus) == v) {
            xProgressDialog.setMessage(getResources().getString(R.string.progress_dialog__status_loading));
            xProgressDialog.show();
            if (findViewById(R.id.btnSearchCus) == v) {
                Intent intent = new Intent(getApplication(), SalesCustomerListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(mApp.getmCurrentModule().equals(getResources().getString(R.string.module_mvs)))
                {
                    intent.putExtra(getResources().getString(R.string.intent_extra_form_name),"MvsHome");
                }
                else{
                    intent.putExtra(getResources().getString(R.string.intent_extra_form_name), getResources().getString(R.string.form_name_mso_sales_order));
                }


                intent.putExtra(getResources().getString(R.string.intent_extra_details), getResources().getString(R.string.intent_extra_add_customer));
                intent.putExtra(getResources().getString(R.string.is_search_pop_up_need), true);
                startActivityForResult(intent, SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE);
                xProgressDialog.dismiss();
            }
        }
        if (findViewById(R.id.btnDatePicker) == v) {
            if(!salesOrderLineList.isEmpty())
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MsoSalesOrderActivity.this);

                builder.setTitle(getResources().getString(R.string.message_title_alert));
                builder.setMessage(getResources().getString(R.string.message_item_price_change));

                builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        showDatePickerDialog();
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.button_text_no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
            else
            {
                showDatePickerDialog();
            }
        }
        if (findViewById(R.id.btnAddressCus) == v) {   //google map
            GetConnectGoogleApiClient();
            GetCurrentLocation();
            GetDestination();
            /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("https://maps.google.com/maps?f=d&daddr=" + mDestination));
            startActivity(intent);*/
            mLatitudeText =1.332814;
            mLongitudeText=103.708018;

            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr="+mLatitudeText+","+mLongitudeText
                            +"&daddr="+ destination));
            startActivity(i);
        }
    }

    public float getAllSoTotalAmtInclVatByCustomer(String cusCode, String deliveryDate,String invoiceNo)
    {
        float totalAmount=0;

        SalesOrderDbHandler soDb=new SalesOrderDbHandler(getApplicationContext());
        soDb.open();

        totalAmount=soDb.getAllSoTotalAmtInclVat(cusCode,deliveryDate, invoiceNo);

        soDb.close();

        return totalAmount;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SALES_CUSTOMER_LIST_ACTIVITY with an OK result , Customer Change
        if (requestCode == SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {

                Bundle extraData = data.getExtras();

                if (extraData != null) {
                    if (extraData.containsKey(getResources().getString(R.string.customer_json_obj))) {
                        String objAsJson = extraData.getString(getResources().getString(R.string.customer_json_obj));
                        customerObj = Customer.fromJson(objAsJson);

                        tempCustomer = customerObj;

                        txtCustomer.setText(customerObj.getName());
                        txtCustomerCode     .setText(customerObj.getCode());
                        txtContact.setText(customerObj.getPhoneNo());
                        txtAddress.setText(customerObj.getAddress());
                        txtGroup.setText(customerObj.getCustomerPriceGroup());
                        txtMinimumSalesAmount.setText(String.valueOf(customerObj.getMinimumSalesAmount()));
                        txtContact.setText(customerObj.getPhoneNo());
                        txtDriverCode.setText(customerObj.getDriverCode());

                        tempSalesOrder.setStatus(getResources().getString(R.string.SalesOrderStatusPending));

                        SalesPricesDbHandler spDB=new SalesPricesDbHandler(getApplicationContext());
                        spDB.open();

                        if(salesOrderLineList.size()>0)
                        {
                            //removeSoList = new ArrayList<SalesOrderLine>();
                            for(SalesOrderLine sol:salesOrderLineList)
                            {
                                if(!spDB.isSalesExistByPriceGroupAndItemCode(tempCustomer.getCustomerPriceGroup()
                                        ,sol.getNo()))
                                {
                                    removeSoList.add(sol);
                                }
                            }
                            if(!removeSoList.isEmpty())
                            {
                                for(SalesOrderLine solR : removeSoList)
                                {
                                    salesOrderLineList.remove(solR);
                                }
                            }
                        }
                        spDB.close();

                        if(!salesOrderLineList.isEmpty())
                        {
                            for (int i = 0; i < salesOrderLineList.size(); i++) {
                                SalesOrderLine sol = salesOrderLineList.get(i);

                                float gstPresentage = 0, itemUnitePrice = 0, lineAmount = 0, totalVatAmount = 0, totalAmountInclVAT=0;

                                gstPresentage = getGstPercentage(tempCustomer.getCode(), sol.getNo());

                                //itemUnitePrice = getUnitPrice(sol.getNo(), customerObj.getCustomerPriceGroup(), customerObj.getCode(), sol.getUnitofMeasure());
                                itemUnitePrice = getUnitPriceWithQuantity(sol.getNo()
                                                                        , customerObj.getCustomerPriceGroup()
                                                                        , customerObj.getCode()
                                                                        , sol.getUnitofMeasure()
                                                                        , Math.round(sol.getQuantity()));

                                lineAmount = sol.getQuantity() * itemUnitePrice;

                                totalVatAmount = (lineAmount * gstPresentage) / 100;

                                totalAmountInclVAT = lineAmount + totalVatAmount;

                                sol.setUnitPrice(itemUnitePrice);
                                sol.setLineAmount(Float.parseFloat(String.format("%.2f", lineAmount)));
                                sol.setTotalAmountExclVAT(Float.parseFloat(String.format("%.2f", lineAmount)));
                                sol.setTotalVATAmount(Float.parseFloat(String.format("%.2f", totalVatAmount)));
                                sol.setTotalAmountInclVAT(Float.parseFloat(String.format("%.2f", totalAmountInclVAT)));
                                sol.setTaxPercentage(String.valueOf(gstPresentage));
                            }
                        }
                        updateSummeryValues(salesOrderLineList);
                        msoSalesOrderAdapter.notifyDataSetChanged();

                        isCustomerChanged = true;
                        Toast.makeText(mApp, getResources().getString(R.string.message_customer_added), Toast.LENGTH_SHORT).show();
                        txtCustomer.setError(null);
                        isSaved = false;
                    }
                }
            }
        }
        if (requestCode == MSO_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE) { //Updating Qty on specific item
            if (resultCode == RESULT_OK) {

                Bundle extraData = data.getExtras();

                if (extraData != null) {
                    if (extraData.containsKey(getResources().getString(R.string.sales_order_line_obj))) {

                        String objAsJson = extraData.getString(getResources().getString(R.string.sales_order_line_obj));
                        float gstPresentage = 0, lineAmount = 0, totalVatAmount = 0, totalAmountInclVAT = 0, itemUnitePrice = 0;

                        int position = extraData.getInt(getResources().getString(R.string.adapter_position));
                        tempSalesOrderLine = SalesOrderLine.fromJson(objAsJson);
                        tempSalesOrder.setStatus(getResources().getString(R.string.SalesOrderStatusPending));
                        boolean isExist=false;
                        int index = 0;
                        String timeStamp = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd_HH_mm_ss)).format(new Date());
                        String key = timeStamp + tempCustomer.getCode() + tempSalesOrderLine.getNo() +"#"+ tempSalesOrderLine.getUnitofMeasure() ;

                        //itemUnitePrice = getUnitPrice(tempSalesOrderLine.getNo(), tempCustomer.getCustomerPriceGroup(), tempCustomer.getCode(), tempSalesOrderLine.getUnitofMeasure());
                        itemUnitePrice = getUnitPriceWithQuantity(tempSalesOrderLine.getNo()
                                                                , tempCustomer.getCustomerPriceGroup()
                                                                , tempCustomer.getCode()
                                                                , tempSalesOrderLine.getUnitofMeasure()
                                                                , Math.round(tempSalesOrderLine.getQuantity()));

                        if(salesOrderLineList.size()==0)
                        {
                            tempSalesOrderLine.setUnitPrice(itemUnitePrice);

                            gstPresentage = getGstPercentage(tempCustomer.getCode(), tempSalesOrderLine.getNo());

                            lineAmount = tempSalesOrderLine.getQuantity() * tempSalesOrderLine.getUnitPrice();

                            totalVatAmount = (lineAmount * gstPresentage) / 100;

                            totalAmountInclVAT = lineAmount + totalVatAmount;

                            tempSalesOrderLine.setLineAmount(Float.parseFloat(String.format("%.2f", lineAmount)));
                            tempSalesOrderLine.setTotalAmountExclVAT(Float.parseFloat(String.format("%.2f", lineAmount)));
                            tempSalesOrderLine.setTotalVATAmount(Float.parseFloat(String.format("%.2f", totalVatAmount)));
                            tempSalesOrderLine.setTotalAmountInclVAT(Float.parseFloat(String.format("%.2f", totalAmountInclVAT)));
                            tempSalesOrderLine.setKey(key);

                            salesOrderLineList.add(tempSalesOrderLine);
                            tempSalesOrderLine.setLineNo(String.valueOf(salesOrderLineList.size()));

                            if (salesOrderLineList.size() == 1) {
                                msoSalesOrderAdapter = new MsoSalesOrderAdapter(salesOrderLineList, R.layout.item_sales_order_detail_card, MsoSalesOrderActivity.this, tempSalesOrder.getStatus(), tempCustomer, deliveryDate);
                                recyclerViewSalesOrderDetails.setAdapter(msoSalesOrderAdapter);
                            }
                            updateSummeryValues(salesOrderLineList);
                            msoSalesOrderAdapter.notifyDataSetChanged();
                            recyclerViewSalesOrderDetails.smoothScrollToPosition(salesOrderLineList.size());
                        }
                        else
                        {
                            ArrayList<Integer> indexArray = new ArrayList<Integer>();
                            for (int i = 0; i < salesOrderLineList.size(); i++) {
                                SalesOrderLine sol = salesOrderLineList.get(i);
                                //&& sol.getUnitofMeasure().equals(tempSalesOrderLine.getUnitofMeasure())
                                if (sol.getNo().equals(tempSalesOrderLine.getNo())) {
                                    isExist = true;
                                    index = i;
                                    indexArray.add(i);
                                }
                            }

                            if(position > -1) //for Old Items
                            {
                                if(indexArray.size()==1) {
                                    SalesOrderLine sol = salesOrderLineList.get(position);

                                    List<SalesOrderLine> clonedSalesOrderLineList = new ArrayList<SalesOrderLine>();
                                    clonedSalesOrderLineList = cloneList(salesOrderLineList);
                                    final SalesOrderLine fSol = clonedSalesOrderLineList.get(position);
                                    removeSoListType2.add(fSol);


                                    sol.setUnitPrice(itemUnitePrice);

                                    gstPresentage = getGstPercentage(tempCustomer.getCode(), tempSalesOrderLine.getNo());

                                    lineAmount = tempSalesOrderLine.getQuantity() * sol.getUnitPrice();

                                    totalVatAmount = (lineAmount * gstPresentage) / 100;

                                    totalAmountInclVAT = lineAmount + totalVatAmount;

                                    sol.setUnitofMeasure(tempSalesOrderLine.getUnitofMeasure());
                                    sol.setQuantity(tempSalesOrderLine.getQuantity());
                                    sol.setQtytoInvoice(tempSalesOrderLine.getQuantity());

                                    sol.setLineAmount(Float.parseFloat(String.format("%.2f", lineAmount)));
                                    sol.setTotalAmountExclVAT(Float.parseFloat(String.format("%.2f", lineAmount)));
                                    sol.setTotalVATAmount(Float.parseFloat(String.format("%.2f", totalVatAmount)));
                                    sol.setTotalAmountInclVAT(Float.parseFloat(String.format("%.2f", totalAmountInclVAT)));
                                    sol.setKey(key);

                                    //Setting SalesOrderLine item to the list
                                    salesOrderLineList.set(position, sol);

                                    updateSummeryValues(salesOrderLineList);
                                    msoSalesOrderAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    if(indexArray.size() == 2)
                                    {
                                        boolean itemAvailable=false;
                                        for (int i = 0; i < salesOrderLineList.size(); i++) {
                                            SalesOrderLine sol = salesOrderLineList.get(i);
                                            //&& sol.getUnitofMeasure().equals(tempSalesOrderLine.getUnitofMeasure())
                                            if (sol.getNo().equals(tempSalesOrderLine.getNo()) && sol.getUnitofMeasure().equals(tempSalesOrderLine.getUnitofMeasure())) {
                                                itemAvailable = true;
                                            }
                                        }
                                        List<String> items = Arrays.asList(tempSalesOrderLine.getKey().split("#"));

                                        if(items.get(1).equals(tempSalesOrderLine.getUnitofMeasure())) // only update qty
                                        {
                                            //
                                            SalesOrderLine sol = salesOrderLineList.get(position);

                                            List<SalesOrderLine> clonedSalesOrderLineList = new ArrayList<SalesOrderLine>();
                                            clonedSalesOrderLineList = cloneList(salesOrderLineList);
                                            final SalesOrderLine fSol = clonedSalesOrderLineList.get(position);
                                            removeSoListType2.add(fSol);


                                            sol.setUnitPrice(itemUnitePrice);

                                            gstPresentage = getGstPercentage(tempCustomer.getCode(), tempSalesOrderLine.getNo());

                                            lineAmount = tempSalesOrderLine.getQuantity() * sol.getUnitPrice();

                                            totalVatAmount = (lineAmount * gstPresentage) / 100;

                                            totalAmountInclVAT = lineAmount + totalVatAmount;

                                            sol.setUnitofMeasure(tempSalesOrderLine.getUnitofMeasure());
                                            sol.setQuantity(tempSalesOrderLine.getQuantity());
                                            sol.setQtytoInvoice(tempSalesOrderLine.getQuantity());

                                            sol.setLineAmount(Float.parseFloat(String.format("%.2f", lineAmount)));
                                            sol.setTotalAmountExclVAT(Float.parseFloat(String.format("%.2f", lineAmount)));
                                            sol.setTotalVATAmount(Float.parseFloat(String.format("%.2f", totalVatAmount)));
                                            sol.setTotalAmountInclVAT(Float.parseFloat(String.format("%.2f", totalAmountInclVAT)));
                                            sol.setKey(key);

                                            //Setting SalesOrderLine item to the list
                                            salesOrderLineList.set(position, sol);

                                            updateSummeryValues(salesOrderLineList);
                                            msoSalesOrderAdapter.notifyDataSetChanged();
                                            //

                                        }
                                        else
                                        {
                                            if(itemAvailable)
                                            {
                                                //removeSoListType2 = new ArrayList<SalesOrderLine>();
                                                for(SalesOrderLine sol:salesOrderLineList)
                                                {
                                                    if(sol.getNo().equals(tempSalesOrderLine.getNo()))
                                                    {
                                                        removeSoListType2.add(sol);
                                                    }
                                                }
                                                if(!removeSoListType2.isEmpty())
                                                {
                                                    for(SalesOrderLine solR : removeSoListType2)
                                                    {
                                                        salesOrderLineList.remove(solR);
                                                    }
                                                }

                                                tempSalesOrderLine.setUnitPrice(itemUnitePrice);

                                                gstPresentage = getGstPercentage(tempCustomer.getCode(), tempSalesOrderLine.getNo());

                                                lineAmount = tempSalesOrderLine.getQuantity() * tempSalesOrderLine.getUnitPrice();

                                                totalVatAmount = (lineAmount * gstPresentage) / 100;

                                                totalAmountInclVAT = lineAmount + totalVatAmount;

                                                tempSalesOrderLine.setLineAmount(Float.parseFloat(String.format("%.2f", lineAmount)));
                                                tempSalesOrderLine.setTotalAmountExclVAT(Float.parseFloat(String.format("%.2f", lineAmount)));
                                                tempSalesOrderLine.setTotalVATAmount(Float.parseFloat(String.format("%.2f", totalVatAmount)));
                                                tempSalesOrderLine.setTotalAmountInclVAT(Float.parseFloat(String.format("%.2f", totalAmountInclVAT)));

                                                salesOrderLineList.add(tempSalesOrderLine);
                                                tempSalesOrderLine.setLineNo(String.valueOf(salesOrderLineList.size()));
                                                tempSalesOrderLine.setKey(key);

                                                updateSummeryValues(salesOrderLineList);
                                                msoSalesOrderAdapter.notifyDataSetChanged();
                                                recyclerViewSalesOrderDetails.smoothScrollToPosition(salesOrderLineList.size());
                                            }
                                        }

                                    }
                                }

                            }
                            else {  //  For New items
                                isExist = false;
                                for (int i = 0; i < salesOrderLineList.size(); i++) {
                                    SalesOrderLine sol = salesOrderLineList.get(i);
                                    //&& sol.getUnitofMeasure().equals(tempSalesOrderLine.getUnitofMeasure())
                                    if (sol.getNo().equals(tempSalesOrderLine.getNo()) && sol.getUnitofMeasure().equals(tempSalesOrderLine.getUnitofMeasure())) {
                                        isExist = true;
                                        index = i;
                                        // indexArray.add(i);
                                    }
                                }

                                if (isExist) {
                                    SalesOrderLine sol = salesOrderLineList.get(index);

                                    List<SalesOrderLine> clonedSalesOrderLineList = new ArrayList<SalesOrderLine>();
                                    clonedSalesOrderLineList = cloneList(salesOrderLineList);
                                    final SalesOrderLine fSol = clonedSalesOrderLineList.get(index);
                                    removeSoListType2.add(fSol);

                                    sol.setUnitPrice(itemUnitePrice);

                                    gstPresentage = getGstPercentage(tempCustomer.getCode(), tempSalesOrderLine.getNo());

                                    lineAmount = tempSalesOrderLine.getQuantity() * sol.getUnitPrice();

                                    totalVatAmount = (lineAmount * gstPresentage) / 100;

                                    totalAmountInclVAT = lineAmount + totalVatAmount;

                                    sol.setUnitofMeasure(tempSalesOrderLine.getUnitofMeasure());
                                    sol.setQuantity(tempSalesOrderLine.getQuantity());
                                    sol.setQtytoInvoice(tempSalesOrderLine.getQuantity());

                                    sol.setLineAmount(Float.parseFloat(String.format("%.2f", lineAmount)));
                                    sol.setTotalAmountExclVAT(Float.parseFloat(String.format("%.2f", lineAmount)));
                                    sol.setTotalVATAmount(Float.parseFloat(String.format("%.2f", totalVatAmount)));
                                    sol.setTotalAmountInclVAT(Float.parseFloat(String.format("%.2f", totalAmountInclVAT)));
                                    sol.setKey(key);
                                    //Setting SalesOrderLine item to the list
                                    salesOrderLineList.set(index, sol);
                                    updateSummeryValues(salesOrderLineList);

                                    //Updating the recyclerView
                                    msoSalesOrderAdapter.notifyDataSetChanged();

                                    Toast.makeText(mApp, getResources().getString(R.string.message_qty_updated), Toast.LENGTH_SHORT).show();
                                    isSaved = false;
                                } else {
                                    tempSalesOrderLine.setUnitPrice(itemUnitePrice);

                                    gstPresentage = getGstPercentage(tempCustomer.getCode(), tempSalesOrderLine.getNo());

                                    lineAmount = tempSalesOrderLine.getQuantity() * tempSalesOrderLine.getUnitPrice();

                                    totalVatAmount = (lineAmount * gstPresentage) / 100;

                                    totalAmountInclVAT = lineAmount + totalVatAmount;

                                    tempSalesOrderLine.setLineAmount(Float.parseFloat(String.format("%.2f", lineAmount)));
                                    tempSalesOrderLine.setTotalAmountExclVAT(Float.parseFloat(String.format("%.2f", lineAmount)));
                                    tempSalesOrderLine.setTotalVATAmount(Float.parseFloat(String.format("%.2f", totalVatAmount)));
                                    tempSalesOrderLine.setTotalAmountInclVAT(Float.parseFloat(String.format("%.2f", totalAmountInclVAT)));

                                    salesOrderLineList.add(tempSalesOrderLine);
                                    tempSalesOrderLine.setLineNo(String.valueOf(salesOrderLineList.size()));
                                    tempSalesOrderLine.setKey(key);

                                    updateSummeryValues(salesOrderLineList);
                                    msoSalesOrderAdapter.notifyDataSetChanged();
                                    recyclerViewSalesOrderDetails.smoothScrollToPosition(salesOrderLineList.size());
                                }
                            }

                        }
                        Toast.makeText(mApp, "updated!", Toast.LENGTH_SHORT).show();
                        isSaved = false;
                    }
                }
            }
        }
        if (requestCode == SALES_ITEM_ACTIVITY_RESULT_CODE) {  //Add new item to sales line
            if (resultCode == RESULT_OK) {

                Bundle extraData = data.getExtras();

                if (extraData != null) {
                    if (extraData.containsKey(getResources().getString(R.string.item_json_obj))) {

                        String objAsJson = extraData.getString(getResources().getString(R.string.item_json_obj));
                        tempItem = Item.fromJson(objAsJson);

                        addItem();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(mModule.equals(getResources().getString(R.string.module_lds))){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.lds_sales_invoice_menu, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else{
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.sales_order_menu, menu);

            MenuItem itm_action_TakePic;
            itm_action_TakePic = menu.findItem(R.id.action_takePicture);
            itm_action_TakePic.setVisible(false);
            return super.onCreateOptionsMenu(menu);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        //finish();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (!txtPoComments.getText().toString().equals(tempSalesOrder.getExternalDocumentNo() == null ? "" : tempSalesOrder.getExternalDocumentNo())) {
            isSaved = false;
        }
        if (isSaved == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MsoSalesOrderActivity.this);

            builder.setTitle("Alert");
            builder.setMessage("Changes are not saved. Do you want to exit?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } else {
            finish();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        GetCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

        if(syncStatus.isStatus())
        {
            Toast.makeText(MsoSalesOrderActivity.this, "Successfully Confirmed Delivery Status", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(MsoSalesOrderActivity.this, "Error Updating Delivery Status", Toast.LENGTH_LONG).show();
        }

    }

    public void setItemTouchHelper() {

        if (tempSalesOrder.getStatus().equals(getResources().getString(R.string.SalesOrderStatusPending))) {
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                    if (direction == ItemTouchHelper.RIGHT) {    //if swipe Right

                        AlertDialog.Builder builder = new AlertDialog.Builder(MsoSalesOrderActivity.this); //alert for confirm to delete
                        builder.setMessage("Do you want to Remove this item?");    //set message
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() { //when click on DELETE
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SalesOrderLine sol = salesOrderLineList.get(position);
                                removeSoList.add(sol);

                                salesOrderLineList.remove(position);
                                updateSummeryValues(salesOrderLineList);
                                msoSalesOrderAdapter.notifyDataSetChanged();
                                isSaved = false;
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                msoSalesOrderAdapter.notifyDataSetChanged();
                            }
                        }).show();
                    }
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerViewSalesOrderDetails);
        }
    }

    public String getLatestSoId(String deliveryDate) {
        UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
        dbHandler.open();
        String soId="";

        soId = dbHandler.getLatestSoRunningNo(mApp.getCurrentUserName(),deliveryDate);

        dbHandler.close();
        return soId;
    }

    public void createView(View view) {
        myToolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        tvToolbarTitle = (TextView) view.findViewById(R.id.tvToolbarTitle);

        textScanCode = (EditText) view.findViewById(R.id.textScanCode);
        txtNumberOfItems = (TextView) view.findViewById(R.id.tvToolbarTitle);
        txtTotalQty = (TextView) view.findViewById(R.id.tvToolbarTitle);
        txtCustomerCode = (EditText) view.findViewById(R.id.txtCustomerCode);
        txtDriverCode = (EditText) view.findViewById(R.id.txtDriverCode);
        txtCustomer = (EditText) view.findViewById(R.id.txtCustomer);
        txtContact = (EditText) view.findViewById(R.id.txtContact);
        txtAddress = (EditText) view.findViewById(R.id.txtAddress);
        txtGroup = (EditText) view.findViewById(R.id.txtGroup);
        txtDeliveryDate = (EditText) view.findViewById(R.id.txtDeliveryDate);
        txtMinimumSalesAmount = (EditText) view.findViewById(R.id.txtMinimumSalesAmount);
        txtPoComments = (EditText) view.findViewById(R.id.txtPoComments);
        txtSubTotal = (EditText) view.findViewById(R.id.txtSubTotal);
        txtGst = (EditText) view.findViewById(R.id.txtGst);
        txtGrandTotal = (EditText) view.findViewById(R.id.txtGrandTotal);
        txtNumberOfItems = (TextView) view.findViewById(R.id.txtNumberOfItems);
        txtTotalQty = (TextView) view.findViewById(R.id.txtTotalQty);
        txtAddress = (EditText) view.findViewById(R.id.txtAddress);

        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnSearchCus = (Button) view.findViewById(R.id.btnSearchCus);
        btnDatePicker = (Button) view.findViewById(R.id.btnDatePicker);
        btnContactCus = (Button) view.findViewById(R.id.btnContactCus);
        btnAddressCus = (Button) view.findViewById(R.id.btnAddressCus);
        btnItem = (Button) view.findViewById(R.id.btnItem);
        btnSummary = (Button) view.findViewById(R.id.btnSummary);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnClear = (Button) view.findViewById(R.id.btnClear);
        fabAddNewItem = (FloatingActionButton) view.findViewById(R.id.fabAddNewItem);

        llItem = (LinearLayout) view.findViewById(R.id.llItem);
        llSummary = (LinearLayout) view.findViewById(R.id.llSummary);

        recyclerViewSalesOrderDetails = (RecyclerView) view.findViewById(R.id.recyclerViewSalesOrderDetails);
    }

    public void settingButtonDrawables() {
        Drawable backArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left).color(Color.WHITE).sizeDp(30);
        Drawable myToolbarDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_ellipsis_v).color(Color.WHITE).sizeDp(30);
        Drawable searchDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_search).color(getResources().getColor(R.color.white)).sizeDp(30);
        Drawable cusSearchDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_search).color(getResources().getColor(R.color.colorPrimary)).sizeDp(25);
        Drawable dtPickerDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_calendar).color(getResources().getColor(R.color.colorPrimary)).sizeDp(25);
        Drawable contactCusDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_phone).color(getResources().getColor(R.color.colorPrimary)).sizeDp(25);
        Drawable addressCusDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_location_arrow).color(getResources().getColor(R.color.colorPrimary)).sizeDp(25);
        Drawable fabAddDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_plus).color(getResources().getColor(R.color.colorPrimary)).sizeDp(80);

        myToolbar.setOverflowIcon(myToolbarDrawable);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        btnSearch.setBackgroundDrawable(searchDrawable);
        btnSearchCus.setBackgroundDrawable(cusSearchDrawable);
        btnDatePicker.setBackgroundDrawable(dtPickerDrawable);
        btnContactCus.setBackgroundDrawable(contactCusDrawable);
        btnAddressCus.setBackgroundDrawable(addressCusDrawable);
        fabAddNewItem.setBackgroundDrawable(fabAddDrawable);
    }

    public void clearFields() {
        tempSalesOrder = new SalesOrder();
        tempCustomer = new Customer();
        tempSalesOrderLine = new SalesOrderLine();
        tempItem = new Item();
        salesOrderLineList = new ArrayList<SalesOrderLine>();

        tvToolbarTitle.setText("");
        txtCustomer.setText("");
        txtCustomerCode.setText("");
        txtContact.setText("");
        txtAddress.setText("");
        txtGroup.setText("");
        txtPoComments.setText("");
        txtDeliveryDate.setText("");
        txtMinimumSalesAmount.setText("");
        txtContact.setText("");
        txtDriverCode.setText("");
        txtNumberOfItems.setText("");
        txtTotalQty.setText("");
        txtSubTotal.setText("");
        txtGst.setText("");
        txtGrandTotal.setText("");
    }

    public void clearRecyclerView() {
        msoSalesOrderAdapter = new MsoSalesOrderAdapter(new ArrayList<SalesOrderLine>(), R.layout.item_sales_order_detail_card, MsoSalesOrderActivity.this, tempSalesOrder.getStatus(), new Customer(), deliveryDate);
        recyclerViewSalesOrderDetails.setAdapter(msoSalesOrderAdapter);
    }

    public void updateSummeryValues(List<SalesOrderLine> salesOrderLineList_) {
        totalQty = 0;
        subTotal = 0;
        totalVatAmount = 0;
        grandTotal = 0;
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        if (salesOrderLineList_.size() > 0) {
            txtNumberOfItems.setText(String.valueOf(salesOrderLineList_.size()));
            for (SalesOrderLine salesOrderLineObj : salesOrderLineList_) {
                totalQty = totalQty + Math.round(salesOrderLineObj.getQuantity());
                subTotal = subTotal + salesOrderLineObj.getTotalAmountExclVAT();
                totalVatAmount = totalVatAmount + salesOrderLineObj.getTotalVATAmount();
                grandTotal = grandTotal + salesOrderLineObj.getTotalAmountInclVAT();
            }
            txtTotalQty.setText(String.valueOf(totalQty));
            txtSubTotal.setText(formatter.format(subTotal));
            txtGst.setText(formatter.format(totalVatAmount));
            txtGrandTotal.setText(formatter.format(grandTotal));
        } else {
            txtNumberOfItems.setText("");
            txtTotalQty.setText("");
            txtSubTotal.setText("");
            txtGst.setText("");
            txtGrandTotal.setText("");
        }
    }

    public void removeZeroTotalAmtItems() {
        List<SalesOrderLine> temparyRemoveSalesOrderLineList = new ArrayList<SalesOrderLine>();
        if (!salesOrderLineList.isEmpty()) {
            for (int i = 0; i < salesOrderLineList.size(); i++) {
                SalesOrderLine sol = salesOrderLineList.get(i);
                if (sol.getTotalAmountExclVAT().equals(new Float(0))) {
                    removeSoList.add(sol);
                    temparyRemoveSalesOrderLineList.add(sol);
                }
            }
        }
        if(!temparyRemoveSalesOrderLineList.isEmpty())
        {
            for(SalesOrderLine solR : temparyRemoveSalesOrderLineList)
            {
                salesOrderLineList.remove(solR);
            }
        }
    }

    public boolean saveSalesOrder() {
        boolean status = false;
        String poComments = txtPoComments.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(getResources().getString(R.string.time_zone_gmt)));
        String todayDate = sdf.format(new Date());

        updateSummeryValues(salesOrderLineList);
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(getApplicationContext());
        dbAdapter.open();
        SalesOrderLineDbHandler dbLineAdapter = new SalesOrderLineDbHandler(getApplicationContext());
        dbLineAdapter.open();

        removeZeroTotalAmtItems();

        if (listType.equals(getResources().getString(R.string.mso_new_sales_order))) {

            if (tempCustomer.getCode() == null) {
                txtCustomer.setError(getResources().getString(R.string.message_body_select_customer));
            } else {
                txtCustomer.setError(null);
            }

            if (txtDeliveryDate.getText().toString().equals("")) {
                txtDeliveryDate.setError(getResources().getString(R.string.message_body_select_delivery_date));
            } else {
                txtDeliveryDate.setError(null);
            }

            if (tempCustomer.getCode() != null && !txtDeliveryDate.getText().toString().equals("")) {
                tempSalesOrder.setKey(todayDate + getResources().getString(R.string.key_so) + tempCustomer.getCode());
                tempSalesOrder.setNo(soNo);
                tempSalesOrder.setOrderDate(todayDate);
                tempSalesOrder.setDocumentDate(todayDate);
                tempSalesOrder.setRequestedDeliveryDate(deliveryDate);
                tempSalesOrder.setShipmentDate(deliveryDate);
                tempSalesOrder.setExternalDocumentNo(poComments);
                tempSalesOrder.setSalespersonCode(mApp.getmCurrentSalesPersonCode());
                tempSalesOrder.setStatus(getResources().getString(R.string.SalesOrderStatusPending));
                tempSalesOrder.setCreatedBy(mApp.getCurrentUserName());
                tempSalesOrder.setCreatedDateTime(todayDate);
                tempSalesOrder.setCreatedFrom(getResources().getString(R.string.mobile));
                //tempSalesOrder.setLastModifiedBy(mApp.getCurrentUserName());
                //tempSalesOrder.setLastModifiedDateTime(todayDate);
                tempSalesOrder.setAmountIncludingVAT(totalVatAmount + subTotal);
                tempSalesOrder.setAmountExcludingVAT(subTotal);
                tempSalesOrder.setVATAmount(totalVatAmount);
                tempSalesOrder.setDocumentType(0);

                if (isCustomerChanged) {
                    tempSalesOrder.setSelltoCustomerNo(tempCustomer.getCode());
                    tempSalesOrder.setSelltoCustomerName(tempCustomer.getName());
                    tempSalesOrder.setSelltoAddress(tempCustomer.getAddress());
                    tempSalesOrder.setSelltoContactNo(tempCustomer.getPhoneNo());
                    tempSalesOrder.setSelltoPostCode(tempCustomer.getPostalCode());
                    tempSalesOrder.setDriverCode(tempCustomer.getDriverCode());
                    tempSalesOrder.setSelltoCity("");
                    tempSalesOrder.setDueDate(deliveryDate); //client request on 2017-10-03

                    //Setting Due Date
/*                    if (!tempCustomer.getDueDateGracePeriod().equals("")) {
                        int gracePeriod = 10;// Integer.parseInt(tempCustomer.getDueDateGracePeriod());
                        String dueDate = "";
                        String salesOrderCreatedDate_ = tempSalesOrder.getCreatedDateTime();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));

                        String date_ = salesOrderCreatedDate_.substring(0, 10);

                        try {
                            Date createdDate = simpleDateFormat.parse(date_);

                            Calendar c = Calendar.getInstance();
                            c.setTime(createdDate);
                            c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + gracePeriod);
                            Date newDate = c.getTime();
                            dueDate = simpleDateFormat.format(newDate);
                        } catch (Exception ee) {
                            Log.d(getResources().getString(R.string.message_exception), ee.getMessage().toString());
                        }
                        tempSalesOrder.setDueDate("");// temporally changed by nalaka
                    } else {
                        tempSalesOrder.setDueDate("");
                    }*/
                }
                tempSalesOrder.setComment(poComments);
                tempSalesOrder.setStatus(getResources().getString(R.string.SalesOrderStatusPending));
                updateLineNo();
                try {
                    if (dbAdapter.deleteSalesOrder(tempSalesOrder.getNo())) {
                        dbAdapter.addSalesOrder(tempSalesOrder);
                        //Updating new so running no
                        saveSoRunningNumber(tempSalesOrder.getNo());
                        Log.d(getResources().getString(R.string.message_so_added), tempSalesOrder.getNo() == null ? "" : tempSalesOrder.getNo());

                        if (salesOrderLineList != null && salesOrderLineList.size() > 0) {
                            for (SalesOrderLine sol : salesOrderLineList) {
                                if (dbLineAdapter.deleteSalesOrderLine(sol.getKey())) {

                                    dbLineAdapter.addSalesOrderLine(sol);
                                    Log.d(getResources().getString(R.string.message_so_line_added), sol.getNo() == null ? "" : sol.getNo());
                                }
                            }
                        }
                    }
                    status = true;
                    listType = "";
                    isCustomerChanged = false;
                } catch (Exception ee) {
                    Log.d(getResources().getString(R.string.message_exception), ee.getMessage().toString());
                    dbAdapter.close();
                    dbLineAdapter.close();
                }
                dbAdapter.close();
                dbLineAdapter.close();
            }
        } else {
            tempSalesOrder.setRequestedDeliveryDate(deliveryDate);
            tempSalesOrder.setShipmentDate(deliveryDate);
            tempSalesOrder.setExternalDocumentNo(poComments); //
            tempSalesOrder.setSalespersonCode(mApp.getmCurrentSalesPersonCode());
            tempSalesOrder.setStatus(getResources().getString(R.string.SalesOrderStatusPending));
            tempSalesOrder.setLastModifiedBy(mApp.getCurrentUserName());
            tempSalesOrder.setLastModifiedDateTime(todayDate);
            tempSalesOrder.setAmountIncludingVAT(totalVatAmount + subTotal);

            tempSalesOrder.setAmountExcludingVAT(subTotal);
            tempSalesOrder.setVATAmount(totalVatAmount);
            tempSalesOrder.setDocumentType(0);

            if (isCustomerChanged) {
                tempSalesOrder.setSelltoCustomerNo(tempCustomer.getCode());
                tempSalesOrder.setSelltoCustomerName(tempCustomer.getName());
                tempSalesOrder.setSelltoAddress(tempCustomer.getAddress());
                tempSalesOrder.setSelltoContactNo(tempCustomer.getPhoneNo());
                tempSalesOrder.setSelltoPostCode(tempCustomer.getPostalCode());
                tempSalesOrder.setDriverCode(tempCustomer.getDriverCode());
                tempSalesOrder.setSelltoCity("");

                //Setting Due Date
                /*if (!tempCustomer.getDueDateGracePeriod().equals("")) {
                    int gracePeriod = Integer.parseInt(tempCustomer.getDueDateGracePeriod());
                    String dueDate = "";
                    String salesOrderCreatedDate_ = tempSalesOrder.getCreatedDateTime();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));
                    String date_ = salesOrderCreatedDate_.substring(0, 10);

                    try {
                        Date createdDate = simpleDateFormat.parse(date_);

                        Calendar c = Calendar.getInstance();
                        c.setTime(createdDate);
                        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + gracePeriod);
                        Date newDate = c.getTime();
                        dueDate = simpleDateFormat.format(newDate);
                    } catch (Exception ee) {
                        Log.d(getResources().getString(R.string.message_exception), ee.getMessage().toString());
                    }
                    tempSalesOrder.setDueDate(dueDate);
                }
                else
                {
                    tempSalesOrder.setDueDate("");
                }*/
                //tempSalesOrder.setDueDate("");
            }
            tempSalesOrder.setDueDate(deliveryDate);
            tempSalesOrder.setComment(poComments);
            updateLineNo();
            try {
                if (dbAdapter.deleteSalesOrder(tempSalesOrder.getNo())) {
                    dbAdapter.addSalesOrder(tempSalesOrder);
                    Log.d(getResources().getString(R.string.message_so_added), tempSalesOrder.getNo() == null ? "" : tempSalesOrder.getNo());

                    if(removeSoListType2 != null && removeSoListType2.size() > 0)
                    {
                        for (SalesOrderLine sol : removeSoListType2) {
                            if (dbLineAdapter.deleteSalesOrderLine(sol.getKey())) {
                                Log.d("DELETED SOLINE:", sol.getNo() == null ? "" : sol.getNo());
                            }
                        }
                    }
                    if(removeSoList != null && removeSoList.size() > 0)
                    {
                        for (SalesOrderLine sol : removeSoList) {
                            if (dbLineAdapter.deleteSalesOrderLine(sol.getKey())) {
                                Log.d("DELETED SOLINE:", sol.getNo() == null ? "" : sol.getNo());
                            }
                        }
                    }
                    if (salesOrderLineList != null && salesOrderLineList.size() > 0) {
                        for (SalesOrderLine sol : salesOrderLineList) {
                            if (dbLineAdapter.deleteSalesOrderLine(sol.getKey())) {
                                dbLineAdapter.addSalesOrderLine(sol);
                                Log.d(getResources().getString(R.string.message_so_line_added), sol.getNo() == null ? "" : sol.getNo());
                            }
                        }
                    }
                }
                status = true;
                isCustomerChanged = false;
            } catch (Exception ee) {
                Log.d(getResources().getString(R.string.message_exception), ee.getMessage().toString());
                dbAdapter.close();
                dbLineAdapter.close();
            }
        }
        dbAdapter.close();
        dbLineAdapter.close();
        return status;
    }

    public void updateLineNo() {
        if(!salesOrderLineList.isEmpty())
        {
            int lineNo = 1;
            for(int i=0; i<salesOrderLineList.size(); i++)
            {
                SalesOrderLine sol = salesOrderLineList.get(i);
                sol.setLineNo(String.valueOf(lineNo));
                salesOrderLineList.set(i,sol);
                lineNo++;
            }
        }
    }

    public void saveSoRunningNumber(String newSoId) {
        UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
        dbHandler.open();
        dbHandler.setLatestSoRunningNo(newSoId, mApp.getCurrentUserName());
        dbHandler.close();
    }

    private void showDatePickerDialog() {
        Calendar cl, clNow;
        int nowYear, nowMonth, nowDay;
        cl = Calendar.getInstance();
        date = cl.get(Calendar.DAY_OF_MONTH);
        month = cl.get(Calendar.MONTH);
        year = cl.get(Calendar.YEAR);

        clNow = Calendar.getInstance();
        nowYear = clNow.get(Calendar.YEAR);
        nowMonth = clNow.get(Calendar.MONTH);
        nowDay = clNow.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtDeliveryDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                setDeliveryDate(year + "-" + (month + 1) + "-" + dayOfMonth);
                validateDeliveryDate(year + "-" + (month + 1) + "-" + dayOfMonth);
                //deliveryDate = dayOfMonth + "-" + (month + 1) + "-" + year;

                //Refresh all item price, details according to the delivery date;

                setAllItems();


                isSaved = false;
            }
        }, date, month, year);

        datePickerDialog.updateDate(nowYear, nowMonth, nowDay);
        datePickerDialog.show();
    }

    public void validateDeliveryDate(String date){
        SimpleDateFormat dfDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));

        try {
            if (dfDate.parse(date).before(dfDate.parse(dfDate.format(new Date()))))
            {
                invalidDate = true;
                txtDeliveryDate.setError("");
                Toast.makeText(this, getResources().getString(R.string.message_invalid_date), Toast.LENGTH_SHORT).show();
            }
            else
            {
                invalidDate = false;
                txtDeliveryDate.setError(null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setAllItems(){
        if(!salesOrderLineList.isEmpty()) {
            for (SalesOrderLine sol : salesOrderLineList) {
                float gstPresentage = 0, itemUnitePrice = 0, lineAmount = 0, totalVatAmount = 0, totalAmountInclVAT = 0;

                gstPresentage = getGstPercentage(tempCustomer.getCode(), sol.getNo());

                //itemUnitePrice = getUnitPrice(sol.getNo(), tempCustomer.getCustomerPriceGroup(), tempCustomer.getCode(), sol.getUnitofMeasure());
                itemUnitePrice = getUnitPriceWithQuantity(sol.getNo()
                                                        , tempCustomer.getCustomerPriceGroup()
                                                        , tempCustomer.getCode()
                                                        , sol.getUnitofMeasure()
                                                        , Math.round(sol.getQuantity()));

                lineAmount = sol.getQuantity() * itemUnitePrice;

                totalVatAmount = (lineAmount * gstPresentage) / 100;

                totalAmountInclVAT = lineAmount + totalVatAmount;

                sol.setUnitPrice(itemUnitePrice);
                sol.setLineAmount(Float.parseFloat(String.format("%.2f", lineAmount)));
                sol.setTotalAmountExclVAT(Float.parseFloat(String.format("%.2f", lineAmount)));
                sol.setTotalVATAmount(Float.parseFloat(String.format("%.2f", totalVatAmount)));
                sol.setTotalAmountInclVAT(Float.parseFloat(String.format("%.2f", totalAmountInclVAT)));
                sol.setTaxPercentage(String.valueOf(gstPresentage));
            }
            updateSummeryValues(salesOrderLineList);
            msoSalesOrderAdapter.notifyDataSetChanged();
        }
    }

    public void setDeliveryDate(String date) {
        if (!date.equals("")) {
            DateFormat df = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));
            try {

                Date initDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(date);
                deliveryDate = df.format(initDate);

            } catch (Exception e) {
                Log.e(getResources().getString(R.string.message_exception), e.getMessage().toString());
            }
        }
    }

    public float getGstPercentage(String customerNo, String itemNo) {
        String busPostingPrecent = null, productPostingPrecent = null;
        float gSTPercentage = 0;

        GSTPostingSetupDbHandler gstDb = new GSTPostingSetupDbHandler(getApplicationContext());
        CustomerDbHandler customerDb = new CustomerDbHandler(getApplicationContext());
        ItemDbHandler itemDb = new ItemDbHandler(getApplicationContext());

        customerDb.open();
        Customer cusObj = customerDb.getCustomerByCustomerCode(customerNo);
        customerDb.close();

        itemDb.open();
        Item itemObj = itemDb.getItemByItemCode(itemNo);
        itemDb.close();

        if (cusObj.getCode() != null) {
            busPostingPrecent = cusObj.getVatBusPostingGroup();
        }

        if (itemObj.getItemCode() != null) {
            productPostingPrecent = itemObj.getVatProdPostingGroup();
        }

        gstDb.open();

        gSTPercentage = Float.valueOf(gstDb.getGSTPrecent(busPostingPrecent, productPostingPrecent));

        gstDb.close();

        return gSTPercentage;
    }

    public List<SalesOrderLine> cloneList(List<SalesOrderLine> list) {
        List<SalesOrderLine> clonedSalesOrderLineList = new ArrayList<SalesOrderLine>();
        for (int i = 0; i < list.size(); i++) {
            clonedSalesOrderLineList.add(new SalesOrderLine(list.get(i)));
        }
        return clonedSalesOrderLineList;
    }

    public void addItem() {

        SalesOrderLine existSalesOrderLine = new SalesOrderLine();

        // if item exist
        if(!salesOrderLineList.isEmpty()) {

            for (SalesOrderLine sol : salesOrderLineList) {
                if(sol.getNo().equals(tempItem.getItemCode()))
                {
                    existSalesOrderLine = sol;
                    break;
                }
            }
        }
        String existSalesOrderLineJasonObj = existSalesOrderLine.toJson();

        SalesOrderLine salesOrderLine = new SalesOrderLine();
        String timeStamp = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd_HH_mm_ss)).format(new Date());
        String key = timeStamp + tempCustomer.getCode() + tempItem.getItemCode();
        int adapterPosition = -1;
        //float itemUnitePrice = getUnitPrice(tempItem.getItemCode(), tempCustomer.getCustomerPriceGroup(), tempCustomer.getCode(), tempItem.getItemBaseUom());

        salesOrderLine.setKey(key);
        salesOrderLine.setType(2);
        salesOrderLine.setNo(tempItem.getItemCode());
        salesOrderLine.setDriverCode(tempCustomer.getDriverCode());
        salesOrderLine.setDescription(tempItem.getDescription());
        salesOrderLine.setQuantity(0f);  //New item
        salesOrderLine.setUnitofMeasure(tempItem.getItemBaseUom());
        salesOrderLine.setSalesPriceExist(false);

        salesOrderLine.setUnitPrice(0f);
        salesOrderLine.setLineAmount(0);  //Quantity is 0  (new Item)
        salesOrderLine.setSalesLineDiscExists(false);
        salesOrderLine.setLineDiscountPercent(getResources().getString(R.string.double_amount_zero));
        salesOrderLine.setLineDiscountAmount(0.0f);
        salesOrderLine.setQtytoInvoice(0f);
        salesOrderLine.setQuantityInvoiced(0f);

        if (listType.equals(getResources().getString(R.string.mso_new_sales_order))) {
            salesOrderLine.setDocumentNo(soNo);
        } else {
            salesOrderLine.setDocumentNo(tempSalesOrder.getNo());
        }

        salesOrderLine.setTotalAmountExclVAT(0f); //line Amount
        salesOrderLine.setTotalVATAmount(0f);   //line amount X 7%(Prescentage)
        salesOrderLine.setTotalAmountInclVAT(0f); //line amount + line amount X 7%(Prescentage)

        float gstPresentage = getGstPercentage(tempCustomer.getCode(), tempItem.getItemCode());

        salesOrderLine.setTaxPercentage(String.valueOf(gstPresentage));

        tempSalesOrder.setStatus(getResources().getString(R.string.SalesOrderStatusPending));

        String jasonObj = salesOrderLine.toJson();
        String customerJsonObj = tempCustomer.toJson();

        Intent intent = new Intent(this, MsoSalesOrderItemActivity.class);
        intent.putExtra(getResources().getString(R.string.sales_order_line_obj), jasonObj);
        intent.putExtra(getResources().getString(R.string.customer_json_obj), customerJsonObj);
        intent.putExtra(getResources().getString(R.string.adapter_position), adapterPosition);
        intent.putExtra("deliveryDate",deliveryDate);
        intent.putExtra( "existSalesOrderLineJasonObj", existSalesOrderLineJasonObj);
        startActivityForResult(intent, MSO_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE);
    }

    public float getUnitPriceWithQuantity(
            String itemCode
            , String customerPriceGroup
            , String customerCode
            , String itemUom
            , int quantity) {

        float itemMasterUnitPrice = 0, groupItemPrice = 0, customerItemPrice = 0;

        List<SalesPrices> customerGroupPriceList = new ArrayList<SalesPrices>(),
                customerPriceList = new ArrayList<SalesPrices>() ;

        //1 ItemMaster UnitPrice
        itemMasterUnitPrice = Float.parseFloat(getItemUnitPriceFromItemMaster(itemCode
                , itemUom) == "" ? "0" : getItemUnitPriceFromItemMaster(itemCode, itemUom));


        customerGroupPriceList = getGroupItemPriceList(customerPriceGroup
                , 1
                , itemCode
                , deliveryDate
                , itemUom);

        customerPriceList = getCustomerItemPriceList(customerCode
                , 0
                , itemCode
                , deliveryDate
                , itemUom);

        //2 Customer group unit price
        if(!customerGroupPriceList.isEmpty())
        {
            List<Float> priceList = new ArrayList<Float>();

            for(int i=0; i<customerGroupPriceList.size(); i++)
            {
                SalesPrices sp = customerGroupPriceList.get(i);
                int minimumQty = Math.round(Float.parseFloat(sp.getMinimumQuantity()));

                if(minimumQty <= quantity)
                {
                    priceList.add(Float.parseFloat(sp.getPublishedPrice()));
                }
            }

            if(priceList.size()==0)
            {
                groupItemPrice=0;
            }
            else if(priceList.size() == 1)
            {
                groupItemPrice = priceList.get(0);
            }
            else
            {
                Collections.sort(priceList);
                groupItemPrice = priceList.get(0);
            }
        }
        else
        {
            groupItemPrice = 0;
        }

        //3 Customer unit price
        if(!customerPriceList.isEmpty())
        {
            List<Float> priceList = new ArrayList<Float>();

            for(int i=0; i<customerPriceList.size(); i++)
            {
                SalesPrices sp = customerPriceList.get(i);
                int minimumQty = Math.round(Float.parseFloat(sp.getMinimumQuantity()));

                if(minimumQty <= quantity)
                {
                    priceList.add(Float.parseFloat(sp.getPublishedPrice()));
                }
            }

            if(priceList.size()==0)
            {
                customerItemPrice=0;
            }
            else if(priceList.size() == 1)
            {
                customerItemPrice = priceList.get(0);
            }
            else
            {
                Collections.sort(priceList);
                customerItemPrice = priceList.get(0);
            }
        }
        else
        {
            customerItemPrice = 0;
        }


        float minimunItemPrice = getMinItemPrice(itemMasterUnitPrice
                , groupItemPrice
                , customerItemPrice);

        return minimunItemPrice;
    }

    public List<SalesPrices> getCustomerItemPriceList(String customerCode
                                                        , int salesType
                                                        , String itemCode
                                                        , String deliveryDate
                                                        , String itemUom) {
        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
        db.open();

        List<SalesPrices> list= db.getCustomerPriceList(customerCode
                                                        , salesType
                                                        , itemCode
                                                        , deliveryDate
                                                        , itemUom);

        db.close();

        return list;
    }

    public List<SalesPrices> getGroupItemPriceList(String customerGroup
                                                    , int salesType
                                                    , String itemCode
                                                    , String deliveryDate
                                                    , String itemUom) {

        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
        db.open();

        List<SalesPrices> list = db.getGroupPriceList(customerGroup
                                                        , salesType
                                                        , itemCode
                                                        , deliveryDate
                                                        , itemUom);

        db.close();

        return list;
    }

    public float getUnitPrice(String itemCode, String customerPriceGroup, String customerCode, String itemUom) {
        float itemMasterUnitPrice = 0, groupItemPrice = 0, customerItemPrice = 0;

        //1 ItemMaster UnitPrice
        itemMasterUnitPrice = Float.parseFloat(getItemUnitPriceFromItemMaster(itemCode, itemUom) == "" ? "0" : getItemUnitPriceFromItemMaster(itemCode, itemUom));

        //2 Group ItemPrice
        groupItemPrice = Float.parseFloat(getGroupItemPrice(customerPriceGroup, 1, itemCode, deliveryDate, itemUom) == "" ? "0" : getGroupItemPrice(customerPriceGroup, 1, itemCode, deliveryDate, itemUom));

        //3 customer ItemPrice
        customerItemPrice = Float.parseFloat(getCustomerItemPrice(customerCode, 0, itemCode, deliveryDate, itemUom) == "" ? "0" : getCustomerItemPrice(customerCode, 0, itemCode, deliveryDate, itemUom));

        float minimunItemPrice = getMinItemPrice(itemMasterUnitPrice, groupItemPrice, customerItemPrice);

        return minimunItemPrice;
    }

    public float getMinItemPrice(float a_, float b_, float c_) {
        float a = a_, b = b_, c = c_;
        boolean isAZero = false, isBZero = false, isCZero = false;

        if (a == 0) {
            isAZero = true;
        }

        if (b == 0) {
            isBZero = true;
        }

        if (c == 0) {
            isCZero = true;
        }

        if (isAZero && isBZero && isCZero) {
            return 0;
        } else {
            if (isAZero && isBZero && !isCZero) {
                return c;
            }
            if (isAZero && !isBZero && isCZero) {
                return b;
            }
            if (!isAZero && isBZero && isCZero) {
                return a;
            }

            if (isAZero && !isBZero && !isCZero) {
                return Math.min(b, c);
            }
            if (!isAZero && !isBZero && isCZero) {
                return Math.min(a, b);
            }
            if (!isAZero && isBZero && !isCZero) {
                return Math.min(a, c);
            }
            if (!isAZero && !isBZero && !isCZero) {
                return Math.min(a, Math.min(b, c));
            }
        }
        return 0;
    }

    public String getCustomerItemPrice(String customerCode, int salesType, String itemCode, String deliveryDate, String itemUom) {
        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
        db.open();

        String itemPrice = db.getCustomerItemUnitPriceByCustomerCode(customerCode, salesType, itemCode, deliveryDate, itemUom);

        db.close();

        return itemPrice;
    }

    public String getGroupItemPrice(String customerGroup, int salesType, String itemCode, String deliveryDate, String itemUom) {
        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
        db.open();

        String itemPrice = db.getGroupItemPriceByCustomePriceGroup(customerGroup, salesType, itemCode, deliveryDate, itemUom);

        db.close();

        return itemPrice;
    }

    public String getItemUnitPriceFromItemMaster(String itemCode, String itemUom) {
        ItemDbHandler db = new ItemDbHandler(getApplicationContext());
        db.open();

        String unitprice = db.getItemPriceByItemCode(itemCode, itemUom);
        db.close();

        return unitprice;
    }

    private void getSalesOrderLineList(String No) {
        boolean status = false;
        SalesOrderLineDbHandler dbAdapter = new SalesOrderLineDbHandler(this);
        dbAdapter.open();

        salesOrderLineList = dbAdapter.getAllSalesOrderLinesByDocumentNo(No);

        dbAdapter.close();
    }

    private void getCustomer(String Code) {
        boolean status = false;
        CustomerDbHandler dbAdapter = new CustomerDbHandler(this);
        dbAdapter.open();

        tempCustomer = dbAdapter.getCustomerByCustomerCode(Code);

        dbAdapter.close();
    }

    private boolean isItemAvailable(String barCode) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        status = dbAdapter.isItemExistByIdentifierCode(barCode);

        dbAdapter.close();
        return status;
    }

    private void getItem(String barCode) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        tempItem = dbAdapter.getItemByIdentifierCode(barCode);

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

    private void showSearchDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        ;
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow()
                .setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);

        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_sales_order_list_search);
        dialog.setCancelable(false);

        Spinner dropdown = (Spinner) dialog.findViewById(R.id.spinnerStatus);
        String[] items = new String[]{"Pending", "Completed", "Confirm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

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

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_cusInfo:
                if (tempCustomer.getCode() == null) {
                    showMessageBox("Alert", "Please select a customer first");
                } else {
                    String objAsJson = tempCustomer.toJson();
                    Intent intent = new Intent(this, SalesCustomerDetailActivity.class);
                    intent.putExtra("_customerDetailObject", objAsJson);
                    intent.putExtra("IsPopupNeeded", true);
                    this.startActivity(intent);
                }
                return true;

            case R.id.action_cusAr:
                if (tempCustomer.getCode() == null) {
                    showMessageBox("Alert", "Please select a customer first");
                } else {
                    Intent intentCusAr = new Intent(this, SalesCustomerArActivity.class);
                    intentCusAr.putExtra("cusNo", tempCustomer.getCode());
                    intentCusAr.putExtra("cusName", tempCustomer.getName());
                    intentCusAr.putExtra("outstanding", String.valueOf(tempCustomer.getBalance()));
                    this.startActivity(intentCusAr);
                }
                return true;

            case R.id.action_cusInvoice:
                if (tempCustomer.getCode() == null) {
                    showMessageBox("Alert", "Please select a customer first");
                } else {
                    Intent intentCusInvoice = new Intent(this, SalesCustomerInvoiceActivity.class);
                    intentCusInvoice.putExtra("cusNo", tempCustomer.getCode());
                    intentCusInvoice.putExtra("cusName", tempCustomer.getName());
                    this.startActivity(intentCusInvoice);
                }
                return true;

            case R.id.action_takePicture:
                if (tempSalesOrder.getStatus().equals(getResources().getString(R.string.SalesOrderStatusConfirmed))) {
                    if (mModule.equals(getResources().getString(R.string.module_lds))) {

                        Intent intentTakePic = new Intent(this, MsoTakePictureActivity.class);
                        intentTakePic.putExtra("soNo", tvToolbarTitle.getText());
                        intentTakePic.putExtra("status", tempSalesOrder.getStatus());
                        this.startActivity(intentTakePic);
                    } else {
                        Toast.makeText(MsoSalesOrderActivity.this, "Not allowed to add pictures.", Toast.LENGTH_LONG).show();
                    }


                } else {
                    if (tempSalesOrder.getCreatedDateTime() == null) {
                        Toast.makeText(MsoSalesOrderActivity.this, "Please save sales order before add pictures.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intentTakePic = new Intent(this, MsoTakePictureActivity.class);
                        intentTakePic.putExtra("soNo", tvToolbarTitle.getText());
                        intentTakePic.putExtra("status", tempSalesOrder.getStatus());
                        this.startActivity(intentTakePic);
                    }
                }
                return true;

            case R.id.action_collectPayment:

                if (tempCustomer.getCode() == null) {
                    showMessageBox("Alert", "Please select a customer first");
                } else {
                    if (mModule.equals(getResources().getString(R.string.module_lds))) {
                        PaymentHeader payment = new PaymentHeader();
                        PaymentHeaderDbHandler dbAdapter = new PaymentHeaderDbHandler(getApplicationContext());
                        dbAdapter.open();
                        payment = dbAdapter.getPaymentByDocumentNo(tempSalesOrder.getNo());
                        dbAdapter.close();

                        if (payment.getPaymentNo() == null) {
                            String jsonCustomerObj = tempCustomer.toJson();
                            Intent intent = new Intent(this, MsoPaymentActivity.class);
                            intent.putExtra("details", "NewPayment");
                            intent.putExtra("module", getResources().getString(R.string.module_lds));
                            intent.putExtra("_customerDetailObject", jsonCustomerObj);
                            intent.putExtra("DocumentNo", tempSalesOrder.getNo());
                            intent.putExtra("ReferenceAmount", Float.toString(tempSalesOrder.getAmountIncludingVAT()));
                            intent.putExtra("formName", "LdsSalesInvoice");
                            startActivity(intent);
                        } else {

                            // payment.setStatus("0"); // comment by S.L
                            String jsonCustomerObj = tempCustomer.toJson();
                            String jsonpaymentHeaderObj = payment.toJson();
                            Intent intent = new Intent(this, MsoPaymentActivity.class);
                            intent.putExtra("paymentHeaderObj", jsonpaymentHeaderObj);
                            intent.putExtra("module", getResources().getString(R.string.module_lds));
                            intent.putExtra("_customerDetailObject", jsonCustomerObj);
                            intent.putExtra("DocumentNo", tempSalesOrder.getNo());
                            intent.putExtra("ReferenceAmount", Float.toString(tempSalesOrder.getAmountIncludingVAT()));
                            intent.putExtra("formName", "LdsSalesInvoice");
                            startActivity(intent);
                        }


                    } else {
                        String objAsJson = tempCustomer.toJson();
                        Intent intentMsoPayment = new Intent(this, MsoPaymentListActivity.class);
                        intentMsoPayment.putExtra("_customerDetailObject", objAsJson);
                        intentMsoPayment.putExtra("formName", "MsoSalesOrder");
                        this.startActivity(intentMsoPayment);
                    }


                }


                return true;

            case R.id.action_Print:
                return true;

            case R.id.action_Deliver:
                if (tempSalesOrder != null) {
                    //add by sajith // 2017-10-13
                    UpdateSalesInvoiceStatus(tempSalesOrder.getNo());
                    if (isNetworkConnected()) {
                        uploadDeliverdSalesOrderStatus();
                        uploadSoImages();
                    }
                }

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void uploadSoImages() {
        SalesOrderImageUploadSyncTask task = new SalesOrderImageUploadSyncTask(getApplicationContext(), false);
        task.execute();
    }

    private void setItemTab() {
        btnItem.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnSummary.setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
        llItem.setVisibility(View.VISIBLE);
        llSummary.setVisibility(View.GONE);
        fabAddNewItem.setVisibility(View.VISIBLE);
    }

    private void setSummaryTab() {
        btnSummary.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnItem.setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
        llSummary.setVisibility(View.VISIBLE);
        llItem.setVisibility(View.GONE);
        fabAddNewItem.setVisibility(View.GONE);
    }

    private void GetConnectGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void GetCurrentLocation() {
        if (ContextCompat.checkSelfPermission(MsoSalesOrderActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(MsoSalesOrderActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            Geocoder coder = new Geocoder(MsoSalesOrderActivity.this);
            List<Address> adr;

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitudeText = mLastLocation.getLatitude();
                mLongitudeText = mLastLocation.getLongitude();

                try {
                    adr = coder.getFromLocation(mLatitudeText, mLongitudeText, 1);
                    String country = adr.get(0).getCountryName();
                    String postalCode = adr.get(0).getPostalCode();
                    mylocation = country + "," + postalCode;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void GetDestination() {
        try {
            String country = getResources().getString(R.string.Country);
            String postalCode = tempCustomer.getPostalCode();
            destination = country + "," + postalCode;

        } catch (Exception e) {

        }
    }

    public void UpdateSalesInvoiceStatus(String salesInvoiceNo){

        SalesOrderDbHandler dbHandler=new SalesOrderDbHandler(this);
        dbHandler.open();
        dbHandler.UpdateSalesInvoiceStatus(salesInvoiceNo);
        dbHandler.close();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void uploadDeliverdSalesOrderStatus() {
        salesOrderDeliveryUploadSyncTask = new SalesOrderDeliveryUploadSyncTask(getApplicationContext(), true,tempSalesOrder.getNo());
        salesOrderDeliveryUploadSyncTask.delegate = MsoSalesOrderActivity.this;
        salesOrderDeliveryUploadSyncTask.execute((Void) null);
    }

}
