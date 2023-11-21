package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MvsStockRequestItemAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.CustomerTemplate;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.ItemCrossReference;
import com.gui.mdt.thongsieknavclient.datamodel.SalesPrices;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequest;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequestLine;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerTemplateDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.GSTPostingSetupDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemCrossReferenceDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesPricesDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockRequestDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockRequestLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MvsStockRequestActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE = 1;
    private static final int SALES_ITEM_ACTIVITY_RESULT_CODE = 2;
    private static final int MVS_STOCK_REQUEST_ITEM_ACTIVITY_RESULT_CODE = 3;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    Toolbar myToolbar;
    Button btnItem, btnSummary, btnSearchCustomer, btnDatePicker, btnAddressCustomer, btnClear, btnSave, btnContactCustomer;
    FloatingActionButton fabAddNewItem;
    EditText txtPoComments, txtScanCode;
    LinearLayout llItem, llSummary;
    int date, month, year, mNoOfItems = 0;
    TextView txtCustomerCode, txtCustomerName, txtAddress, txtContact, txtGroup,
            txtSalesPerson, txtDeliveryDate, txtNumberOfItems, txtTotalQty,
            txtSubTotal, txtGst, txtGrandTotal, tvToolbarTitle, mTxtMinSalesAmt;
    Drawable cusSearchDrawable, dtPickerDrawable, addressCusDrawable, menu, backArrow, contactCusDrawable;
    Bundle extras;
    String details = "", deliveryDate = "", srNo = "", todayDate = "", mPoComments = "";
    Customer tempCustomer;
//    Item mTempItem;

    SimpleDateFormat dBDateFormat, uIDateFormat;
    private NavClientApp mApp;
    private boolean invalidDeliveryDate = false, isStarted = false, isCustomerChanged = false,
            isSaveSuccess = false;
    public static boolean isSaved = true;
    private Item tempItem;

    private float totalQty = 0, subTotal = 0, totalVatAmount = 0, grandTotal = 0;

    private RecyclerView recyclerViewStockRequestItem;
    private ProgressDialog mProgressDialog;
    private List<SalesPrices> salesPricesListBaseOnItemTemSeq;

    private MvsStockRequestItemAdapter mvsStockRequestItemAdapter;
    private StockRequest tempStockRequest;
    List<StockRequestLine> stockRequestLineList, removeSrLineListType2, removeSrLineList, tempStockRequestLineList;
    private StockRequestLine tempStockRequestLine;

    //google map
    GoogleApiClient mGoogleApiClient;
    String mylocation, destination;
    private Location mLastLocation;
    private Double mLatitudeText, mLongitudeText, dLatitudeText, dLongideText;
    private boolean mLocationPermissionGranted;
    private UpdateCustomerTask mUpdateCustomerTask;
    private SaveStockRequestTask mSaveStockRequestTask;

    private Logger mLog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(this, R.layout.activity_mvs_stock_request, null);
        setContentView(view);
        createView(view);

        mApp = (NavClientApp) getApplicationContext();
        this.mLog = Logger.getLogger(MvsStockRequestActivity.class);

        initComponents();
        settingBtnDrawables();

        setSummaryTab();
        setClickListners();
        setBarcodeReader();

        extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(getResources().getString(R.string.intent_extra_details))) {
                details = extras.getString(getResources().getString(R.string.intent_extra_details));
            }
        }
        if (details.equals(getResources().getString(R.string.mvs_new_stock_request))) {
            newStockRequest();
        } else {
            oldStockRequest();
        }
    }

    public String getLatestSrId() {
        UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
        dbHandler.open();
        String srId = "";
        try {


            if (mApp.getmCurrentModule().equals(getResources().getString(R.string.module_mvs))) {
                srId = dbHandler.getLatestSrRunningNo(mApp.getCurrentUserName());
            }
        } catch (Exception e) {
            logParams(getResources().getString(R.string.message_exception), e.getMessage());
        }

        dbHandler.close();
        return srId;
    }

    public void initComponents() {
        dBDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));
        uIDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_dd_MM_yyyy));

        //Setting White color for editable fields
        txtCustomerCode.setBackgroundColor(getResources().getColor(R.color.white));
        txtDeliveryDate.setBackgroundColor(getResources().getColor(R.color.white));
        txtPoComments.setBackgroundColor(getResources().getColor(R.color.white));

        txtPoComments.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        recyclerViewStockRequestItem.setHasFixedSize(true);
        recyclerViewStockRequestItem.setLayoutManager(new LinearLayoutManager(this));

        removeSrLineListType2 = new ArrayList<StockRequestLine>();
        removeSrLineList = new ArrayList<StockRequestLine>();
        salesPricesListBaseOnItemTemSeq = new ArrayList<SalesPrices>();
        tempStockRequestLineList = new ArrayList<StockRequestLine>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(getResources().getString(R.string.time_zone_gmt)));
        todayDate = sdf.format(new Date());

        /*txtContact.setMovementMethod(new ScrollingMovementMethod());
        txtAddress.setMovementMethod(new ScrollingMovementMethod());
        txtCustomerName.setMovementMethod(new ScrollingMovementMethod());
        txtPoComments.setMovementMethod(new ScrollingMovementMethod());*/
    }

    private void newStockRequest() {
        clearFields();

        /*//Default SR NO
        SimpleDateFormat soNumberFormat = new SimpleDateFormat("yyyy-MM-ddhh:mm:ss");
        srNo = "RQ"+soNumberFormat.format(new Date());
        tvToolbarTitle.setText(srNo);*/

        //SR no
        srNo = getLatestSrId();
        tvToolbarTitle.setText(srNo);

        //Setting Delivery Date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        String dateTomorrow = uIDateFormat.format(tomorrow);
        txtDeliveryDate.setText(dateTomorrow);
        deliveryDate = dBDateFormat.format(tomorrow);
        clearRecyclerView();

        tempStockRequest.setStatus(getResources().getString(R.string.StockRequestStatusPending));
        setItemTouchHelper();
    }

    public void oldStockRequest() {
        if (extras.containsKey(getResources().getString(R.string.stock_request_jason_obj))) {

            String objAsJson = extras.getString(getResources().getString(R.string.stock_request_jason_obj));
            tempStockRequest = StockRequest.fromJson(objAsJson);
            deliveryDate = tempStockRequest.getOrderDate();

            //Get StockRequestline List
            getStockRequestLineList(tempStockRequest.getNo());
            srNo = tempStockRequest.getNo();

            //Get Customer Details
            getCustomer(tempStockRequest.getSelltoCustomerNo());

            tvToolbarTitle.setText(tempStockRequest.getNo());
            txtCustomerName.setText(tempStockRequest.getSelltoCustomerName());
            txtCustomerCode.setText(tempStockRequest.getSelltoCustomerNo());
            txtAddress.setText(tempCustomer.getAddress());
            txtGroup.setText(tempCustomer.getCustomerPriceGroup());
            txtPoComments.setText(tempStockRequest.getExternalDocumentNo());
            txtSalesPerson.setText(tempStockRequest.getSalespersonCode());
            mTxtMinSalesAmt.setText(String.valueOf(tempCustomer.getMinimumSalesAmount()));
            //date conversion
            if (tempStockRequest.getOrderDate() != null) {
                if (!tempStockRequest.getOrderDate().equals("")) {
                    String date_ = tempStockRequest.getOrderDate().toString();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_dd_MM_yyyy));
                    try {

                        Date initDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(tempStockRequest.getOrderDate().toString());
                        date_ = simpleDateFormat.format(initDate);

                    } catch (Exception e) {
                        logParams(getResources().getString(R.string.message_exception), e.getMessage());
                        Log.e(getResources().getString(R.string.message_exception), e.getMessage().toString());
                    }

                    txtDeliveryDate.setText(date_);
                }
            }

            txtContact.setText(tempCustomer.getPhoneNo());

            updateItemCrossRefDetails();

            updateSummeryValues(stockRequestLineList);

            //---------------------------------  Item List (Stock Request) -----------------------------------
            clearRecyclerView();
            setItemTouchHelper();
            if (stockRequestLineList.size() > 0) {
                mvsStockRequestItemAdapter = new MvsStockRequestItemAdapter(stockRequestLineList
                        , R.layout.item_mvs_stock_request_detail_card
                        , MvsStockRequestActivity.this
                        , tempStockRequest.getStatus()
                        , tempCustomer
                        , deliveryDate,
                        getResources().getString(R.string.form_name_mvs_stock_Request));
                recyclerViewStockRequestItem.setAdapter(mvsStockRequestItemAdapter);
                mvsStockRequestItemAdapter.notifyDataSetChanged();
            }

            //update item pricess in list
            setItemList();

            if (tempStockRequest.getStatus().equals(
                    getResources().getString(R.string.StockRequestStatusConfirmed))) {
                btnSearchCustomer.setEnabled(false);
                btnDatePicker.setEnabled(false);
                btnClear.setEnabled(false);
                btnSave.setEnabled(false);
                fabAddNewItem.setEnabled(false);
                fabAddNewItem.setVisibility(View.GONE);
                txtScanCode.setEnabled(false);
                txtScanCode.setFocusable(false);
                txtPoComments.setEnabled(false);
                fabAddNewItem.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

                Toast.makeText(mApp, getResources().getString(R.string.not_allowed_to_edit), Toast.LENGTH_LONG).show();
            } else if (tempStockRequest.getStatus().equals(
                    getResources().getString(R.string.StockRequestStatusComplete))) {
                btnSearchCustomer.setEnabled(false);
                btnDatePicker.setEnabled(false);
            }
        }
    }

    public void clearRecyclerView() {
        mvsStockRequestItemAdapter = new MvsStockRequestItemAdapter(new ArrayList<StockRequestLine>()
                , R.layout.item_mvs_stock_request_detail_card
                , MvsStockRequestActivity.this
                , tempStockRequest.getStatus()
                , new Customer()
                , deliveryDate,
                getResources().getString(R.string.form_name_mvs_stock_Request));
        recyclerViewStockRequestItem.setAdapter(mvsStockRequestItemAdapter);
    }

    public void setBarcodeReader() {
        txtScanCode.setInputType(InputType.TYPE_NULL);

        txtScanCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtScanCode.setBackgroundResource(R.drawable.border_red);
                } else {
                    txtScanCode.setBackgroundResource(R.drawable.border_gray);
                }
            }
        });

        txtScanCode.requestFocus();

        txtScanCode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            isStarted = true;
                            Handler handler = new Handler();
                            mProgressDialog.setMessage("Loading...");
                            mProgressDialog.show();

                            handler.postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            if (isStarted) {
                                                if (isItemAvailable(txtScanCode.getText().toString())) {
                                                    if (tempCustomer.getCode() == null) {
                                                        showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                                                    } else {

                                                        getItemByCode(txtScanCode.getText().toString());
                                                        if (tempItem.getItemCode() != null) {
                                                            addItem();
                                                        } else {
                                                            showMessageBox("Item not found!", getResources().getString(R.string.message_body_invalid_barcode));
                                                        }
                                                    }
                                                    mProgressDialog.dismiss();
                                                } else {
                                                    mProgressDialog.dismiss();
                                                    showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_invalid_barcode));
                                                }
                                                txtScanCode.setText("");
                                                txtScanCode.requestFocus();
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

        /*txtScanCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 1) {
                    isStarted = true;
                    Handler handler = new Handler();
                    mProgressDialog.setMessage(getResources().getString(R.string.progress_dialog__status_loading));
                    mProgressDialog.show();

                    handler.postDelayed(
                            new Runnable() {
                                public void run() {
                                    if (isStarted) {
                                        if (isItemAvailable(txtScanCode.getText().toString())) {
                                            if (tempCustomer.getCode() == null) {
                                                showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                                            } else {

                                                getItem(txtScanCode.getText().toString());
                                                if(tempItem.getItemCode() != null)
                                                {
                                                    addItem();
                                                }
                                                else
                                                {
                                                    showMessageBox("Item not found!", getResources().getString(R.string.message_body_invalid_barcode));
                                                }
                                            }
                                            mProgressDialog.dismiss();
                                        } else {
                                            mProgressDialog.dismiss();
                                            showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_invalid_barcode));
                                        }
                                        txtScanCode.setText("");
                                        txtScanCode.requestFocus();
                                        isStarted = false;
                                    }
                                }
                            }, 1000);
                }
            }
        });*/
    }

    public float floatRound(float value_, int places) {

        double value = Double.parseDouble(String.valueOf(value_));
        float result = 0f;

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        result = Float.parseFloat(String.valueOf((double) tmp / factor));

        return result;
    }

    private void getItem(String barCode) {
        tempItem = new Item();
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        //tempItem = dbAdapter.getItemByIdentifierCode(barCode);
        //tempItem = dbAdapter.getItemByIdentifierCodeAndTemplate(barCode, tempCustomer.getCustomerPriceGroup());
        tempItem = dbAdapter.getItemByIdentifierCodeAndPriceGroup(barCode, tempCustomer.getCustomerPriceGroup());
        dbAdapter.close();
    }

    public void setItemTouchHelper() {

        if (tempStockRequest.getStatus().equals(getResources().getString(R.string.StockRequestStatusPending))
                || tempStockRequest.getStatus().equals(getResources().getString(R.string.StockRequestStatusComplete))) {
            ItemTouchHelper.SimpleCallback simpleCallback
                    = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView
                        , RecyclerView.ViewHolder viewHolder
                        , RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                    if (direction == ItemTouchHelper.RIGHT) {    //if swipe Right

                        AlertDialog.Builder builder
                                = new AlertDialog.Builder(MvsStockRequestActivity.this); //alert for confirm to delete
                        builder.setMessage("Do you want to Remove this item?");    //set message
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() { //when click on DELETE
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StockRequestLine srl = stockRequestLineList.get(position);
                                removeSrLineList.add(srl);

                                stockRequestLineList.remove(position);
                                updateSummeryValues(stockRequestLineList);
                                mvsStockRequestItemAdapter.notifyDataSetChanged();
                                isSaved = false;
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mvsStockRequestItemAdapter.notifyDataSetChanged();
                            }
                        }).show();
                    }
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerViewStockRequestItem);
        }
    }

    private boolean isItemAvailable(String barCode) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        status = dbAdapter.isItemExistByIdentifierCode(barCode);

        dbAdapter.close();
        return status;
    }

    private void getItemByCode(String itemCode) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        tempItem = dbAdapter.getItemByCode(itemCode);

        dbAdapter.close();
    }

    private void getStockRequestLineList(String No) {
        boolean status = false;
        StockRequestLineDbHandler dbAdapter = new StockRequestLineDbHandler(this);
        dbAdapter.open();

        stockRequestLineList = dbAdapter.getAllSRLinesByDocumentNo(No);

        dbAdapter.close();
    }

    private void getCustomer(String Code) {
        boolean status = false;
        CustomerDbHandler dbAdapter = new CustomerDbHandler(this);
        dbAdapter.open();

        tempCustomer = dbAdapter.getCustomerByCustomerCode(Code);

        dbAdapter.close();
    }

    public void clearFields() {
        float emptyFloat = 0f;

        txtPoComments.setText("");
        txtCustomerCode.setText("");
        txtCustomerName.setText("");
        txtAddress.setText("");
        txtContact.setText("");
        txtGroup.setText("");
        txtSalesPerson.setText("");
        txtDeliveryDate.setText("");
        txtNumberOfItems.setText("");
        txtTotalQty.setText("");
        txtSubTotal.setText("");
        txtGst.setText("");
        txtGrandTotal.setText("");
        mTxtMinSalesAmt.setText("");

        tempCustomer = new Customer();
        tempStockRequest = new StockRequest();
        stockRequestLineList = new ArrayList<StockRequestLine>();

        txtNumberOfItems.setText(String.valueOf(Math.round(emptyFloat)));
        txtTotalQty.setText(String.valueOf(Math.round(emptyFloat)));
        txtSubTotal.setText(String.valueOf(emptyFloat));
        txtGst.setText(String.valueOf(emptyFloat));
        txtGrandTotal.setText(String.valueOf(emptyFloat));

        removeSrLineListType2 = new ArrayList<StockRequestLine>();
        removeSrLineList = new ArrayList<StockRequestLine>();
        salesPricesListBaseOnItemTemSeq = new ArrayList<SalesPrices>();
        tempStockRequestLineList = new ArrayList<StockRequestLine>();
    }

    public void createView(View view) {
        myToolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        llItem = (LinearLayout) view.findViewById(R.id.llItem);
        llSummary = (LinearLayout) view.findViewById(R.id.llSummary);

        txtPoComments = (EditText) view.findViewById(R.id.txtPoComments);

        txtCustomerCode = (TextView) view.findViewById(R.id.txtCustomerCode);
        txtCustomerName = (TextView) view.findViewById(R.id.txtCustomerName);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtContact = (TextView) view.findViewById(R.id.txtContact);
        txtGroup = (TextView) view.findViewById(R.id.txtGroup);
        txtSalesPerson = (TextView) view.findViewById(R.id.txtSalesPerson);
        txtDeliveryDate = (TextView) view.findViewById(R.id.txtDeliveryDate);
        txtNumberOfItems = (TextView) view.findViewById(R.id.txtNumberOfItems);
        txtTotalQty = (TextView) view.findViewById(R.id.txtTotalQty);
        txtSubTotal = (TextView) view.findViewById(R.id.txtSubTotal);
        txtGst = (TextView) view.findViewById(R.id.txtGst);
        txtGrandTotal = (TextView) view.findViewById(R.id.txtGrandTotal);
        mTxtMinSalesAmt = (TextView) view.findViewById(R.id.txtMinSalesAmt);

        tvToolbarTitle = (TextView) view.findViewById(R.id.tvToolbarTitle);

        btnSearchCustomer = (Button) view.findViewById(R.id.btnSearchCustomer);
        btnDatePicker = (Button) view.findViewById(R.id.btnDatePicker);
        btnAddressCustomer = (Button) view.findViewById(R.id.btnAddressCustomer);
        btnItem = (Button) view.findViewById(R.id.btnItem);
        btnSummary = (Button) view.findViewById(R.id.btnSummary);
        btnClear = (Button) view.findViewById(R.id.btnClear);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnContactCustomer = (Button) view.findViewById(R.id.btnContactCustomer);
        fabAddNewItem = (FloatingActionButton) view.findViewById(R.id.fabAddNewItem);
        recyclerViewStockRequestItem = (RecyclerView) view.findViewById(R.id.recyclerViewStockRequestItem);
        txtScanCode = (EditText) view.findViewById(R.id.txtScanCode);
    }

    public void settingBtnDrawables() {
        cusSearchDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_search).color(getResources().getColor(R.color.colorPrimary)).sizeDp(25);
        dtPickerDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_calendar).color(getResources().getColor(R.color.colorPrimary)).sizeDp(25);
        addressCusDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_location_arrow).color(getResources().getColor(R.color.colorPrimary)).sizeDp(25);
        menu = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_ellipsis_v).color(Color.WHITE).sizeDp(30);
        backArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left).color(Color.WHITE).sizeDp(30);
        contactCusDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_phone).color(getResources().getColor(R.color.colorPrimary)).sizeDp(25);

        btnSearchCustomer.setBackgroundDrawable(cusSearchDrawable);
        btnDatePicker.setBackgroundDrawable(dtPickerDrawable);
        btnAddressCustomer.setBackgroundDrawable(addressCusDrawable);
        btnContactCustomer.setBackgroundDrawable(contactCusDrawable);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        myToolbar.setOverflowIcon(menu);
    }

    public void setClickListners() {
        btnItem.setOnClickListener(this);
        btnSummary.setOnClickListener(this);
        btnSearchCustomer.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnAddressCustomer.setOnClickListener(this);
        fabAddNewItem.setOnClickListener(this);
    }

    //--------------------- Click Events  ----------------------------------
    @Override
    public void onClick(View v) {

        if (findViewById(R.id.btnItem) == v) {
            setItemTab();
        }
        if (findViewById(R.id.btnSummary) == v) {
            setSummaryTab();
        }
        if (findViewById(R.id.btnSearchCustomer) == v) {
            //
            if (stockRequestLineList.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MvsStockRequestActivity.this);

                builder.setTitle(getResources().getString(R.string.message_title_alert));
                builder.setMessage("Changing the customer will refresh stock request items. Are you sure?");

                builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getApplication(), SalesCustomerListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(getResources().getString(R.string.intent_extra_form_name), getResources().getString(R.string.form_name_mvs_stock_Request));
                        intent.putExtra(getResources().getString(R.string.intent_extra_details), getResources().getString(R.string.intent_extra_add_customer));
                        startActivityForResult(intent, SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE);
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
            } else {
                Intent intent = new Intent(getApplication(), SalesCustomerListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(getResources().getString(R.string.intent_extra_form_name), getResources().getString(R.string.form_name_mvs_stock_Request));
                intent.putExtra(getResources().getString(R.string.intent_extra_details), getResources().getString(R.string.intent_extra_add_customer));
                startActivityForResult(intent, SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE);
            }
        }
        if (findViewById(R.id.btnDatePicker) == v) {

            if (!stockRequestLineList.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MvsStockRequestActivity.this);

                builder.setTitle(getResources().getString(R.string.message_title_alert));
                builder.setMessage(getResources().getString(R.string.message_item_price_change));

                builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        showCalenderSearchDialog();
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
            } else {
                showCalenderSearchDialog();
            }
        }
        if (findViewById(R.id.btnClear) == v) {
            if (stockRequestLineList.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MvsStockRequestActivity.this);

                builder.setTitle(getResources().getString(R.string.message_title_alert));
                builder.setMessage(getResources().getString(R.string.message_item_qty_will_be_cleared));

                builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        for (StockRequestLine srl : stockRequestLineList) {
                            srl.setQuantity(0f);
                            srl.setLineAmount(0f);
                        }
                        updateSummeryValues(stockRequestLineList);
                        mvsStockRequestItemAdapter.notifyDataSetChanged();
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
        if (findViewById(R.id.btnSave) == v) {

            //not validate delivery date on hq sr
            if (tempStockRequest.getStatus().equals(getResources().getString(R.string.StockRequestStatusComplete))) {
                invalidDeliveryDate = false;
            } else {
                validateDeliveryDate(deliveryDate);
            }

            if (!invalidDeliveryDate) {
                if (tempCustomer.getCode() != null) {
                    if (stockRequestLineList.size() > 0) {
                        updateSummeryValues(stockRequestLineList);

                        /*if(Double.parseDouble(String.valueOf(f_subTotal))> tempCustomer.getMinimumSalesAmount())
                        {*/
                        if (subTotal > 0f) {
                            mSaveStockRequestTask = new SaveStockRequestTask();
                            mSaveStockRequestTask.execute((Void) null);
                        } else {
                            Toast.makeText(mApp, "Total amount should be greater than zero", Toast.LENGTH_SHORT).show();
                        }


                            /*if (saveStockRequest()) {
                                StockRequest stockRequestObj = tempStockRequest;
                                String objAsJson = stockRequestObj.toJson();

                                Toast.makeText(mApp, getResources().getString(R.string.message_updated_successfully), Toast.LENGTH_SHORT).show();
                                finish();

                                Intent intent = new Intent(MvsStockRequestActivity.this, MvsStockRequestActivity.class);
                                intent.putExtra(getResources().getString(R.string.stock_request_jason_obj), objAsJson);
                                startActivity(intent);
                            } else {
                                Toast.makeText(mApp, getResources().getString(R.string.message_failed), Toast.LENGTH_SHORT).show();
                            }*/
                        /*}
                        else
                        {
                            Toast.makeText(mApp, "Total amount should be grater than Minimum sales Amount", Toast.LENGTH_SHORT).show();
                        }*/
                    } else {
                        Toast.makeText(mApp, getResources().getString(R.string.message_add_at_least_one_item), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    txtCustomerCode.setError("");
                    Toast.makeText(mApp, getResources().getString(R.string.message_body_select_customer), Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (findViewById(R.id.btnAddressCustomer) == v) {
            if (tempCustomer.getCode() == null) {
                showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                if (tempCustomer.getCode() == null) {
                    txtCustomerCode.setError(getResources().getString(R.string.message_body_select_customer));
                }
            } else {
                GetConnectGoogleApiClient();
                GetCurrentLoaction();
                GetDestination();
                /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("https://maps.google.com/maps?f=d&daddr=" + mDestination));
                startActivity(intent);*/
                mLatitudeText = 1.332814;
                mLongitudeText = 103.708018;

                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + mLatitudeText + "," + mLongitudeText
                                + "&daddr=" + destination));
                startActivity(i);
            }
        }
        if (findViewById(R.id.fabAddNewItem) == v) {
            if (tempCustomer.getCode() == null) {
                showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                if (tempCustomer.getCode() == null) {
                    txtCustomerCode.setError(getResources().getString(R.string.message_body_select_customer));
                }

            } else {
                String customerJsonObj = tempCustomer.toJson();
                Intent intent = new Intent(getApplication(), SalesItemSearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(getResources().getString(R.string.intent_extra_form_name), getResources().getString(R.string.form_name_mvs_stock_Request));
                intent.putExtra(getResources().getString(R.string.customer_json_obj), customerJsonObj);
                intent.putExtra("deliveryDate", deliveryDate);
                intent.putExtra(getResources().getString(R.string.intent_extra_details), getResources().getString(R.string.intent_extra_add_new_item));
                startActivityForResult(intent, SALES_ITEM_ACTIVITY_RESULT_CODE);
            }
        }
    }

    //
    private class SaveStockRequestTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage("Saving...");
            mProgressDialog.show();

            mPoComments = txtPoComments.getText().toString();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                saveStockRequest();
            } catch (Exception e) {
                Log.d(getResources().getString(R.string.message_exception), e.toString());
                logParams(getResources().getString(R.string.message_exception), e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    mProgressDialog.dismiss();

                    if (isSaveSuccess) {
                        details = "";
                        Toast.makeText(mApp, getResources().getString(R.string.message_updated_successfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mApp, getResources().getString(R.string.message_failed), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.d(getResources().getString(R.string.message_exception), e.toString());
                    logParams(getResources().getString(R.string.message_exception), e.getMessage());
                    mProgressDialog.dismiss();
                }
            } else {
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }
    }

    //
    public boolean saveStockRequest() {
        boolean status = false;

        StockRequestDbHandler dbAdapter = new StockRequestDbHandler(getApplicationContext());
        dbAdapter.open();
        StockRequestLineDbHandler dbLineAdapter = new StockRequestLineDbHandler(getApplicationContext());
        dbLineAdapter.open();

        //removeZeroTotalAmtItems();

        if (details.equals(getResources().getString(R.string.mvs_new_stock_request))) {

            if (tempCustomer.getCode() != null) {

                tempStockRequest.setNo(srNo);
                tempStockRequest.setSelltoCustomerNo(tempCustomer.getCode());
                tempStockRequest.setSelltoCustomerName(tempCustomer.getName());
                tempStockRequest.setExternalDocumentNo(mPoComments);
                tempStockRequest.setSelltoPostCode(tempCustomer.getPostalCode());
                tempStockRequest.setSelltoCountryRegionCode(""); // no data
                tempStockRequest.setSelltoContact(tempCustomer.getPhoneNo());
                tempStockRequest.setBilltoCustomerNo(tempCustomer.getBillToCustomerNo());

                tempStockRequest.setBilltoName(""); // no data
                tempStockRequest.setBilltoPostCode(""); // no data
                tempStockRequest.setBilltoCountryRegionCode(""); // no data
                tempStockRequest.setBilltoContact(""); // no data

                tempStockRequest.setShiptoCode(""); // no data
                tempStockRequest.setShiptoName(""); // no data
                tempStockRequest.setShiptoPostCode(""); // no data
                tempStockRequest.setShiptoCountryRegionCode(""); // no data
                tempStockRequest.setShiptoContact(""); // no data

                tempStockRequest.setPostingDate(""); //
                tempStockRequest.setOrderDate(deliveryDate);
                tempStockRequest.setShipmentDate(deliveryDate);

                tempStockRequest.setLocationCode(""); //
                tempStockRequest.setSalespersonCode(tempCustomer.getSalespersonCode());
                tempStockRequest.setDriverCode(mApp.getmCurrentDriverCode());
                tempStockRequest.setStatus(getResources().getString(R.string.StockRequestStatusPending));

                float amtInclVat_
                        = Float.parseFloat(String.format(getResources().getString(R.string.decimal_two_places), subTotal))
                        + Float.parseFloat(String.format(getResources().getString(R.string.decimal_two_places), totalVatAmount));
                float totalVat_
                        = Float.parseFloat(String.format(getResources().getString(R.string.decimal_two_places), totalVatAmount));

                tempStockRequest.setAmountInclVAT(amtInclVat_);
                tempStockRequest.setVATAmount(totalVat_);
                tempStockRequest.setTransferred(false);

                tempStockRequest.setCreatedBy(mApp.getCurrentUserName());
                tempStockRequest.setCreatedDateTime(todayDate);

                updateLineNo();
                try {
                    if (dbAdapter.deleteStockRequest(tempStockRequest.getNo())) {
                        dbAdapter.addStockRequest(tempStockRequest);

                        //Updating new sr running no
                        saveSrRunningNumber(tempStockRequest.getNo());
                        Log.d(getResources().getString(R.string.message_sr_added), tempStockRequest.getNo() == null ? "" : tempStockRequest.getNo());
                        logParams(getResources().getString(R.string.message_so_added),
                                tempStockRequest.getNo() == null ? "" : tempStockRequest.getNo());
                        if (stockRequestLineList != null && stockRequestLineList.size() > 0) {
                            for (StockRequestLine srl : stockRequestLineList) {
                                if (dbLineAdapter.deleteStockRequestLineByKey(srl.getKey())) {

                                    dbLineAdapter.addStockRequestLine(srl);
                                    Log.d(getResources().getString(R.string.message_so_line_added), srl.getItemNo() == null ? "" : srl.getItemNo());
                                    logParams(getResources().getString(R.string.message_so_line_added),
                                            srl.getItemNo() == null ? "" : srl.getItemNo());
                                }
                            }
                        }
                    }
                    status = true;
                    isCustomerChanged = false;
                    this.isSaved = true;
                    this.isSaveSuccess = true;
                } catch (Exception e) {
                    Log.d(getResources().getString(R.string.message_exception), e.getMessage().toString());
                    logParams(getResources().getString(R.string.message_exception), e.getMessage());
                    dbAdapter.close();
                    dbLineAdapter.close();
                }
                dbAdapter.close();
                dbLineAdapter.close();
            }
        } else {

            tempStockRequest.setShipmentDate(deliveryDate);
            tempStockRequest.setSalespersonCode(tempCustomer.getSalespersonCode());
            tempStockRequest.setDriverCode(mApp.getmCurrentDriverCode());
            //tempStockRequest.setStatus(getResources().getString(R.string.StockRequestStatusPending));
            tempStockRequest.setExternalDocumentNo(mPoComments);
            tempStockRequest.setOrderDate(deliveryDate);

            float amtInclVat_
                    = Float.parseFloat(String.format(getResources().getString(R.string.decimal_two_places), subTotal))
                    + Float.parseFloat(String.format(getResources().getString(R.string.decimal_two_places), totalVatAmount));
            float totalVat_
                    = Float.parseFloat(String.format(getResources().getString(R.string.decimal_two_places), totalVatAmount));

            tempStockRequest.setAmountInclVAT(amtInclVat_);
            tempStockRequest.setVATAmount(totalVat_);
            tempStockRequest.setTransferred(false);

            tempStockRequest.setLastModifiedBy(mApp.getCurrentUserName());
            tempStockRequest.setLastModifiedDateTime(todayDate);

            if (isCustomerChanged) {
                tempStockRequest.setSelltoCustomerNo(tempCustomer.getCode());
                tempStockRequest.setSelltoCustomerName(tempCustomer.getName());
                tempStockRequest.setExternalDocumentNo(mPoComments);
                tempStockRequest.setSelltoPostCode(tempCustomer.getPostalCode());
                tempStockRequest.setSelltoCountryRegionCode(""); // no data
                tempStockRequest.setSelltoContact(tempCustomer.getPhoneNo());
                tempStockRequest.setBilltoCustomerNo(tempCustomer.getBillToCustomerNo());

                //Setting Due Date  need to no types Ex:- 100d ,100m ,100y
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
                } else {
                    tempSalesOrder.setDueDate("");
                }*/
            }

            updateLineNo();
            try {
                if (dbAdapter.deleteStockRequest(tempStockRequest.getNo())) {
                    dbAdapter.addStockRequest(tempStockRequest);
                    Log.d(getResources().getString(R.string.message_sr_added), tempStockRequest.getNo() == null ? "" : tempStockRequest.getNo());

                    if (removeSrLineListType2 != null && removeSrLineListType2.size() > 0) {
                        for (StockRequestLine srl : removeSrLineListType2) {
                            if (dbLineAdapter.deleteStockRequestLineByKey(srl.getKey())) {
                                Log.d("DELETED SRLINE:", srl.getItemNo() == null ? "" : srl.getItemNo());
                            }
                        }
                    }
                    if (removeSrLineList != null && removeSrLineList.size() > 0) {
                        for (StockRequestLine srl : removeSrLineList) {
                            if (dbLineAdapter.deleteStockRequestLineByKey(srl.getKey())) {
                                Log.d("DELETED SRLINE:", srl.getItemNo() == null ? "" : srl.getItemNo());
                            }
                        }
                    }
                    if (stockRequestLineList != null && stockRequestLineList.size() > 0) {
                        for (StockRequestLine srl : stockRequestLineList) {
                            if (dbLineAdapter.deleteStockRequestLineByKey(srl.getKey())) {
                                dbLineAdapter.addStockRequestLine(srl);
                                Log.d(getResources().getString(R.string.message_sr_line_added), srl.getItemNo() == null ? "" : srl.getItemNo());
                            }
                        }
                    }
                }
                status = true;
                isCustomerChanged = false;
                this.isSaved = true;
                this.isSaveSuccess = true;
            } catch (Exception e) {
                Log.d(getResources().getString(R.string.message_exception), e.getMessage().toString());
                logParams(getResources().getString(R.string.message_exception), e.getMessage());
                dbAdapter.close();
                dbLineAdapter.close();
            }
        }
        dbAdapter.close();
        dbLineAdapter.close();
        return status;
    }

    public void saveSrRunningNumber(String newSrId) {
        UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
        dbHandler.open();
        dbHandler.setLatestSrRunningNo(newSrId, mApp.getCurrentUserName());
        dbHandler.close();
    }

    public void updateLineNo() {
        if (!stockRequestLineList.isEmpty()) {
            int lineNo = 1;
            for (int i = 0; i < stockRequestLineList.size(); i++) {
                StockRequestLine srl = stockRequestLineList.get(i);
                srl.setLineNo(String.valueOf(lineNo));
                stockRequestLineList.set(i, srl);
                lineNo++;
            }
        }
    }

    public void removeZeroTotalAmtItems() {
        List<StockRequestLine> temparysrLineRemoveList = new ArrayList<StockRequestLine>();
        if (!stockRequestLineList.isEmpty()) {
            for (int i = 0; i < stockRequestLineList.size(); i++) {
                StockRequestLine srl = stockRequestLineList.get(i);
                if (srl.getAmount().equals(new Float(0))) {
                    removeSrLineList.add(srl);
                    temparysrLineRemoveList.add(srl);
                }
            }
        }
        if (!temparysrLineRemoveList.isEmpty()) {
            for (StockRequestLine srlR : temparysrLineRemoveList) {
                stockRequestLineList.remove(srlR);
            }
        }
    }

    //google map methods start
    private void GetConnectGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void GetCurrentLoaction() {
        if (ContextCompat.checkSelfPermission(MvsStockRequestActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(MvsStockRequestActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            Geocoder coder = new Geocoder(MvsStockRequestActivity.this);
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
                    logParams(getResources().getString(R.string.message_exception), e.getMessage());
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
            logParams(getResources().getString(R.string.message_exception), e.getMessage());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        GetCurrentLoaction();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    //google map methods end

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Add, Change Customer
        if (requestCode == SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extraData = data.getExtras();
                if (extraData != null) {
                    if (extraData.containsKey(getResources().getString(R.string.customer_json_obj))) {
                        String objAsJson = extraData.getString(getResources().getString(R.string.customer_json_obj));
                        tempCustomer = Customer.fromJson(objAsJson);

                        mUpdateCustomerTask = new UpdateCustomerTask();
                        mUpdateCustomerTask.execute((Void) null);
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
        if (requestCode == MVS_STOCK_REQUEST_ITEM_ACTIVITY_RESULT_CODE) { //Updating Qty on specific item
            if (resultCode == RESULT_OK) {

                Bundle extraData = data.getExtras();

                if (extraData != null) {
                    if (extraData.containsKey(getResources().getString(R.string.stock_request_line_jason_obj))) {

                        float lineAmount = 0, itemUnitePrice = 0;
                        String objAsJson = "", timeStamp = "", key = "";
                        boolean isExist = false, isADuplicateRecord = false;

                        objAsJson = extraData.getString(getResources().getString(R.string.stock_request_line_jason_obj));

                        tempStockRequestLine = StockRequestLine.fromJson(objAsJson);

                        key = tempStockRequestLine.getItemNo() + srNo + "#" + tempStockRequestLine.getUnitofMeasureCode();

                        int position = extraData.getInt(getResources().getString(R.string.adapter_position));
                        int index = 0;
                        getItemByCode(tempStockRequestLine.getItemNo());
                        //itemUnitePrice = getUnitPrice(tempStockRequestLine.getItemNo(), tempCustomer.getCustomerPriceGroup(), tempCustomer.getCode(), tempStockRequestLine.getUnitofMeasureCode());
                        SalesPricesDbHandler spDb = new SalesPricesDbHandler(getApplicationContext());
                        spDb.open();

                        if(tempItem.isInventoryValueZero()){
                            itemUnitePrice = 0;
                        }else{
                        itemUnitePrice = spDb.getUnitPriceWithQuantity(tempStockRequestLine.getItemNo(),
                                tempCustomer.getCustomerPriceGroup(),
                                tempCustomer.getCode(),
                                tempStockRequestLine.getUnitofMeasureCode(),
                                Math.round(tempStockRequestLine.getQuantity()),
                                deliveryDate
                        );
                        }
                        spDb.close();

                        if (stockRequestLineList.size() == 0) {
                            tempStockRequestLine.setUnitPrice(itemUnitePrice);

                            tempStockRequestLine.setKey(key);
                            updateStockRequestLineObject(tempStockRequestLine);
                            stockRequestLineList.add(tempStockRequestLine);
                            tempStockRequestLine.setLineNo(String.valueOf(stockRequestLineList.size()));

                            if (stockRequestLineList.size() == 1) {

                                mvsStockRequestItemAdapter = new MvsStockRequestItemAdapter(stockRequestLineList
                                        , R.layout.item_mvs_stock_request_detail_card
                                        , MvsStockRequestActivity.this
                                        , tempStockRequest.getStatus()
                                        , tempCustomer
                                        , deliveryDate,
                                        getResources().getString(R.string.form_name_mvs_stock_Request));
                                recyclerViewStockRequestItem.setAdapter(mvsStockRequestItemAdapter);
                            }

                            updateSummeryValues(stockRequestLineList);
                            mvsStockRequestItemAdapter.notifyDataSetChanged();
                            recyclerViewStockRequestItem.smoothScrollToPosition(stockRequestLineList.size());

                        } else {
                            ArrayList<StockRequestLine> indexSRLArray = new ArrayList<StockRequestLine>();
                            for (int i = 0; i < stockRequestLineList.size(); i++) {
                                StockRequestLine srl = stockRequestLineList.get(i);

                                if (srl.getItemNo().equals(tempStockRequestLine.getItemNo())) {

                                    index = i;
                                    indexSRLArray.add(srl);
                                }
                            }

                            if (position > -1) //for Old Items
                            {
                                if (indexSRLArray.size() == 1) {
                                    StockRequestLine srl = stockRequestLineList.get(position);

                                    StockRequestLine tempSRLine = new StockRequestLine(srl);
                                    removeSrLineListType2.add(tempSRLine);

                                    srl.setUnitPrice(itemUnitePrice);
                                    srl.setKey(key); //new
                                    lineAmount = tempStockRequestLine.getQuantity() * tempStockRequestLine.getUnitPrice();

                                    srl.setUnitofMeasureCode(tempStockRequestLine.getUnitofMeasureCode());
                                    srl.setQuantity(tempStockRequestLine.getQuantity());

                                    updateStockRequestLineObject(srl);

                                    //Setting SalesOrderLine item to the list
                                    stockRequestLineList.set(position, srl);

                                    updateSummeryValues(stockRequestLineList);
                                    mvsStockRequestItemAdapter.notifyDataSetChanged();
                                }
                                if (indexSRLArray.size() > 1) {
                                    StockRequestLine duplicateSRLObject = new StockRequestLine(),
                                            existSRLObject = new StockRequestLine();

                                    boolean itemExist = false;
                                    for (int i = 0; i < indexSRLArray.size(); i++) {
                                        StockRequestLine srl = indexSRLArray.get(i);

                                        if (srl.getUnitofMeasureCode().equals(tempStockRequestLine.getUnitofMeasureCode())) {
                                            isADuplicateRecord = true;
                                            duplicateSRLObject = srl;
                                        }
                                        if (srl.getItemNo().equals(tempStockRequestLine.getItemNo()) &&
                                                srl.getUnitofMeasureCode().equals(tempStockRequestLine.getUnitofMeasureCode())) {

                                            itemExist = true;
                                            existSRLObject = srl;
                                        }
                                    }

                                    if (isADuplicateRecord) {
                                        for (int i = 0; i < indexSRLArray.size(); i++) {
                                            StockRequestLine srl = indexSRLArray.get(i);

                                            if (srl.getKey().equals(tempStockRequestLine.getKey())) {
                                                removeSrLineListType2.add(srl);
                                                stockRequestLineList.remove(srl);
                                            }
                                        }
                                        removeSrLineListType2.remove(duplicateSRLObject);
                                        stockRequestLineList.remove(duplicateSRLObject);

                                        tempStockRequestLine.setUnitPrice(itemUnitePrice);
                                        tempStockRequestLine.setKey(key); //new

                                        updateStockRequestLineObject(tempStockRequestLine);

                                        stockRequestLineList.add(tempStockRequestLine);
                                        mvsStockRequestItemAdapter.notifyDataSetChanged();
                                    } else {
                                        if (existSRLObject.getKey() != null) {
                                            existSRLObject.setUnitPrice(itemUnitePrice);
                                            existSRLObject.setKey(key); //new


                                            existSRLObject.setUnitofMeasureCode(tempStockRequestLine.getUnitofMeasureCode());
                                            existSRLObject.setQuantity(tempStockRequestLine.getQuantity());

                                            updateStockRequestLineObject(existSRLObject);
                                            //Setting SalesOrderLine item to the list
                                            stockRequestLineList.set(position, existSRLObject);

                                            updateSummeryValues(stockRequestLineList);
                                            mvsStockRequestItemAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                            } else {  //  For New items
                                isExist = false;
                                for (int i = 0; i < stockRequestLineList.size(); i++) {
                                    StockRequestLine srl = stockRequestLineList.get(i);
                                    if (srl.getItemNo().equals(tempStockRequestLine.getItemNo()) && srl.getUnitofMeasureCode().equals(tempStockRequestLine.getUnitofMeasureCode())) {
                                        isExist = true;
                                        index = i;
                                    }
                                }

                                if (isExist) {
                                    StockRequestLine srl = stockRequestLineList.get(index);

                                    /*List<SalesOrderLine> clonedSalesOrderLineList = new ArrayList<SalesOrderLine>();
                                    clonedSalesOrderLineList = cloneList(salesOrderLineList);*/
                                    final StockRequestLine fSrl = new StockRequestLine(srl);
                                    removeSrLineListType2.add(fSrl);

                                    srl.setUnitPrice(itemUnitePrice);
                                    srl.setKey(key); //new


                                    srl.setUnitofMeasureCode(tempStockRequestLine.getUnitofMeasureCode());
                                    srl.setQuantity(tempStockRequestLine.getQuantity());

                                    updateStockRequestLineObject(srl);

                                    //Setting SalesOrderLine item to the list
                                    stockRequestLineList.set(index, srl);

                                    updateSummeryValues(stockRequestLineList);
                                    mvsStockRequestItemAdapter.notifyDataSetChanged();

                                } else {
                                    tempStockRequestLine.setUnitPrice(itemUnitePrice);

                                    tempStockRequestLine.setKey(key);

                                    updateStockRequestLineObject(tempStockRequestLine);

                                    stockRequestLineList.add(tempStockRequestLine);

                                    updateSummeryValues(stockRequestLineList);
                                    mvsStockRequestItemAdapter.notifyDataSetChanged();
                                    recyclerViewStockRequestItem.smoothScrollToPosition(stockRequestLineList.size());
                                }
                            }
                        }
                        updateSummeryValues(stockRequestLineList);

                        updateItemCrossRefDetails();
                        mvsStockRequestItemAdapter.notifyDataSetChanged();

                        Toast.makeText(mApp, "updated!", Toast.LENGTH_SHORT).show();
                        isSaved = false;
                    }
                }
            }
        }
    }

    public class UpdateCustomerTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                updateCustomer();
            } catch (Exception e) {
                Log.d("Exception", e.toString());
                logParams(getResources().getString(R.string.message_exception), e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MvsStockRequestActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {

                    txtCustomerCode.setText(tempCustomer.getCode());
                    txtCustomerName.setText(tempCustomer.getName());
                    txtAddress.setText(tempCustomer.getAddress());
                    txtContact.setText(tempCustomer.getPhoneNo());
                    txtGroup.setText(tempCustomer.getCustomerPriceGroup());
                    txtSalesPerson.setText(tempCustomer.getSalespersonCode());
                    mTxtMinSalesAmt.setText(String.valueOf(tempCustomer.getMinimumSalesAmount()));

                    updateSummeryValues(stockRequestLineList);
                    txtCustomerCode.setError(null);

                    mvsStockRequestItemAdapter = new MvsStockRequestItemAdapter(stockRequestLineList
                            , R.layout.item_mvs_stock_request_detail_card
                            , MvsStockRequestActivity.this
                            , tempStockRequest.getStatus()
                            , tempCustomer
                            , deliveryDate,
                            getResources().getString(R.string.form_name_mvs_stock_Request));
                    recyclerViewStockRequestItem.setAdapter(mvsStockRequestItemAdapter);
                    mvsStockRequestItemAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();

                    Toast.makeText(mApp, getResources().getString(R.string.message_customer_added), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    logParams(getResources().getString(R.string.message_exception), e.getMessage());
                    mProgressDialog.dismiss();
                }
            } else {
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }
    }

    public void updateCustomer() {

        List<CustomerTemplate> cusTemplateSalesItems
                = new ArrayList<CustomerTemplate>();

        cusTemplateSalesItems = getSalesItemsBaseOnCusTemplate(tempCustomer.getCode());

        //remove items which are not in customer's template
        CustomerTemplateDbHandler ctDB
                = new CustomerTemplateDbHandler(getApplicationContext());
        ctDB.open();

        if (stockRequestLineList.size() > 0) {
            //removeSrLineList = new ArrayList<StockRequestLine>();

            List<StockRequestLine> temparysrLineRemoveList = new ArrayList<StockRequestLine>();
            for (StockRequestLine srl : stockRequestLineList) {
                if (!ctDB.isCusTempExistByCusNoAndItemNo(tempCustomer.getCode()
                        , srl.getItemNo())) {
                    removeSrLineList.add(srl);
                    temparysrLineRemoveList.add(srl);
                }
            }
            if (!temparysrLineRemoveList.isEmpty()) {
                for (StockRequestLine srlR : temparysrLineRemoveList) {
                    stockRequestLineList.remove(srlR);
                }
            }
        }
        ctDB.close();

        //cloned stock request line list
        tempStockRequestLineList = cloneList(stockRequestLineList);

        //clear main list
        stockRequestLineList = new ArrayList<StockRequestLine>();

        //Add new items to stockRequestLineList
        if (!cusTemplateSalesItems.isEmpty()) {
            for (CustomerTemplate ct : cusTemplateSalesItems) {
                //getItemByCode(ct.getItemNo());

                if (ct.getItemUom() == null) {
                    getItemByCode(ct.getItemNo());
                    if (tempItem.getItemBaseUom() != null) {
                        ct.setItemUom(tempItem.getItemBaseUom());
                    } else {
                        ct.setItemUom("");
                    }
                } else if (ct.getItemUom().equals("")) {
                    getItemByCode(ct.getItemNo());
                    if (tempItem.getItemBaseUom() != null) {
                        ct.setItemUom(tempItem.getItemBaseUom());
                    }
                }

                String key = ct.getItemNo() + srNo + "#" + ct.getItemUom();

                getItemByCode(ct.getItemNo());
                SalesPricesDbHandler spDb
                        = new SalesPricesDbHandler(getApplicationContext());
                spDb.open();
                float itemUnitePrice = 0;
                if(tempItem.isInventoryValueZero()){
                    itemUnitePrice = 0;
                }else {
                    itemUnitePrice = spDb.getUnitPriceWithQuantity(ct.getItemNo(),
                            tempCustomer.getCustomerPriceGroup(),
                            tempCustomer.getCode(),
                            ct.getItemUom(),
                            Math.round(ct.getQuantity()),
                            deliveryDate);

                }
                spDb.close();

                StockRequestLine stockRequestLine = new StockRequestLine();

                stockRequestLine.setKey(key);
                stockRequestLine.setSelltoCustomerNo(tempCustomer.getCode());
                stockRequestLine.setShiptoCode("");
                stockRequestLine.setSalespersonCode(tempCustomer.getSalespersonCode());
                stockRequestLine.setOrderDate(todayDate);
                stockRequestLine.setShipmentDate(deliveryDate);
                stockRequestLine.setDueDate("");
                stockRequestLine.setGrsSalesHeaderExternalDocumentNo("");
                stockRequestLine.setCreatedBy(mApp.getCurrentUserName());
                stockRequestLine.setCreatedDataTime(todayDate);
                //LastModifiedBy;
                //LastModifiedDateTime;
                stockRequestLine.setDocumentNo(srNo);

                if (stockRequestLineList.size() == 0) {
                    stockRequestLine.setLineNo(String.valueOf(1));
                } else {
                    stockRequestLine.setLineNo(String.valueOf(stockRequestLineList.size() + 1));
                }

                stockRequestLine.setType("2");
                stockRequestLine.setItemNo(ct.getItemNo());
                stockRequestLine.setDescription(ct.getDescription());
                stockRequestLine.setLocationCode("");
                stockRequestLine.setQuantity(ct.getQuantity());
                stockRequestLine.setUnitofMeasureCode(ct.getItemUom());
                stockRequestLine.setUnitPrice(itemUnitePrice);
                stockRequestLine.setAmount(0f);
                stockRequestLine.setLineAmount(0f);
                stockRequestLine.setLineDiscountAmount(0f);
                stockRequestLine.setLineDiscountPercent("");
                stockRequestLine.setDriverCode(mApp.getmCurrentDriverCode());

                stockRequestLine.setTotalVATAmount(0f);
                stockRequestLine.setTotalAmountInclVAT(0f);

                //String taxPercentage = String.valueOf(getGstPercentage(tempCustomer.getCode(),ct.getItemNo()));
                stockRequestLine.setTaxPercentage("");

                //updateStockRequestLineObject(stockRequestLine);
                stockRequestLineList.add(stockRequestLine);
            }
        }

        //update salesOrderLineList from old list(tempSalesOrderLineList)
        if (!tempStockRequestLineList.isEmpty()) {
            if (stockRequestLineList.isEmpty()) {
                stockRequestLineList = new ArrayList<StockRequestLine>(tempStockRequestLineList);
            } else {
                for (StockRequestLine tempSrl : tempStockRequestLineList) {
                    for (int i = 0; i < stockRequestLineList.size(); i++) {
                        StockRequestLine srl = stockRequestLineList.get(i);

                        if (tempSrl.getQuantity() > 0f) {
                            if (srl.getItemNo().equals(tempSrl.getItemNo()) && srl.getUnitofMeasureCode().equals(tempSrl.getUnitofMeasureCode())) {
                                srl.setQuantity(tempSrl.getQuantity());

                                //update price details unit price, total ext...
                                updateStockRequestLineObject(srl);
                                stockRequestLineList.set(i, srl);
                            }
                        }
                    }
                }
            }
        }

        if (!tempStockRequestLineList.isEmpty()) {
            for (StockRequestLine srl : tempStockRequestLineList) {
                removeSrLineList.add(srl);
            }
        }

        updateItemCrossRefDetails();

        isCustomerChanged = true;
        isSaved = false;
    }


    public void updateStockRequestLineObject(StockRequestLine srl) {
        float gstPresentage = 0, totalVatAmount = 0f, totalAmountInclVAT = 0f;
        float itemUnitePrice = 0f, lineAmount = 0f;

        getItemByCode(srl.getItemNo());
        SalesPricesDbHandler spDb = new SalesPricesDbHandler(getApplicationContext());
        spDb.open();

        if(tempItem.isInventoryValueZero()){
            itemUnitePrice = 0;
        }else {
            itemUnitePrice = spDb.getUnitPriceWithQuantity(srl.getItemNo(),
                    tempCustomer.getCustomerPriceGroup(),
                    tempCustomer.getCode(),
                    srl.getUnitofMeasureCode() == null ? ""
                            : srl.getUnitofMeasureCode(),
                    Math.round(srl.getQuantity()),
                    deliveryDate
            );
        }

        itemUnitePrice = floatRound(itemUnitePrice, 2);

        spDb.close();

        //line amount
        float lineAmount_ = Math.round(srl.getQuantity()) * itemUnitePrice;
        lineAmount = floatRound(lineAmount_, 2);

        //get Percentage
        gstPresentage = getGstPercentage(tempCustomer.getCode(), srl.getItemNo());

        //vat amount
        float totalVatAmount_ = (lineAmount * gstPresentage) / 100;
        totalVatAmount = floatRound(totalVatAmount_, 2);

        //grand total
        float totalAmountInclVAT_ = lineAmount + totalVatAmount;
        totalAmountInclVAT = floatRound(totalAmountInclVAT_, 2);

        srl.setUnitPrice(itemUnitePrice);
        srl.setLineAmount(lineAmount);
        srl.setAmount(lineAmount);
        srl.setTaxPercentage(String.format("%.2f", gstPresentage));
        srl.setTotalVATAmount(totalVatAmount);
        srl.setTotalAmountInclVAT(totalAmountInclVAT);
    }

    public List<SalesPrices> getItemsBaseOnItemTemplate(String customerPriceGroup) {
        List<SalesPrices> salesPricesList_ = new ArrayList<SalesPrices>();

        SalesPricesDbHandler spDB = new SalesPricesDbHandler(getApplicationContext());
        spDB.open();

        salesPricesList_ = spDB.getAllPriceListByItemTemplateSeq(customerPriceGroup);

        spDB.close();
        return salesPricesList_;
    }

    //sales items based on cus template
    public List<CustomerTemplate> getSalesItemsBaseOnCusTemplate(String cusCode) {
        List<CustomerTemplate> cusTemplateItemList_ = new ArrayList<CustomerTemplate>();

        CustomerTemplateDbHandler ctDB = new CustomerTemplateDbHandler(getApplicationContext());
        ctDB.open();

        cusTemplateItemList_ = ctDB.getCusTemplateItemsByCusCode(cusCode);

        ctDB.close();
        return cusTemplateItemList_;
    }

    public void addItem() {

        // if item exist
        StockRequestLine existStockRequestLine = new StockRequestLine();
        if (!stockRequestLineList.isEmpty()) {

            for (StockRequestLine srl : stockRequestLineList) {
                if (srl.getItemNo().equals(tempItem.getItemCode())) {
                    existStockRequestLine = srl;
                    break;
                }
            }
        }
        String existStockRequestLineJasonObj = existStockRequestLine.toJson();

        //-------------------------------------------//---------------------------------------
        String key = tempItem.getItemCode() + srNo + "#" + tempItem.getItemBaseUom();

        StockRequestLine stockRequestLine = new StockRequestLine();

        stockRequestLine.setKey(key);
        stockRequestLine.setSelltoCustomerNo(tempCustomer.getCode());
        stockRequestLine.setShiptoCode("");
        stockRequestLine.setSalespersonCode(tempCustomer.getSalespersonCode());
        stockRequestLine.setOrderDate(todayDate);
        stockRequestLine.setShipmentDate(deliveryDate);
        stockRequestLine.setDueDate("");
        stockRequestLine.setGrsSalesHeaderExternalDocumentNo("");
        stockRequestLine.setCreatedBy(mApp.getCurrentUserName());
        stockRequestLine.setCreatedDataTime(todayDate);
        //LastModifiedBy;
        //LastModifiedDateTime;
        stockRequestLine.setDocumentNo(srNo);

        if (stockRequestLineList.size() == 0) {
            stockRequestLine.setLineNo(String.valueOf(1));
        } else {
            stockRequestLine.setLineNo(String.valueOf(stockRequestLineList.size() + 1));
        }

        stockRequestLine.setType("2");
        stockRequestLine.setItemNo(tempItem.getItemCode());
        stockRequestLine.setDescription(tempItem.getDescription());
        stockRequestLine.setLocationCode("");
        stockRequestLine.setQuantity(0f);
        stockRequestLine.setUnitofMeasureCode(tempItem.getItemBaseUom());
        stockRequestLine.setUnitPrice(0f);
        stockRequestLine.setAmount(0f);
        stockRequestLine.setLineAmount(0f);
        stockRequestLine.setLineDiscountAmount(0f);
        stockRequestLine.setLineDiscountPercent("");
        stockRequestLine.setDriverCode(mApp.getmCurrentDriverCode());

        stockRequestLine.setTotalVATAmount(0f);
        stockRequestLine.setTotalAmountInclVAT(0f);

        String taxPercentage = String.valueOf(getGstPercentage(tempCustomer.getCode(), tempItem.getItemCode()));
        stockRequestLine.setTaxPercentage(taxPercentage);

        //update cross reference on item  ***********
        ItemCrossReferenceDbHandler icrDb =
                new ItemCrossReferenceDbHandler(getApplicationContext());

        icrDb.open();
        ItemCrossReference itemCrossReference_ = icrDb.getItemCrossreference(stockRequestLine.getItemNo()
                , stockRequestLine.getUnitofMeasureCode(), tempCustomer.getCode());

        icrDb.close();

        if (itemCrossReference_.getItemCrossReferenceNo() != null) {
            stockRequestLine.setItemCrossReferenceNo(itemCrossReference_.getItemCrossReferenceNo());
        } else {
            stockRequestLine.setItemCrossReferenceNo(stockRequestLine.getItemNo());
        }

        if (itemCrossReference_.getDescription() != null) {
            stockRequestLine.setItemCrossReferenceDescription(itemCrossReference_.getDescription());
        } else {
            stockRequestLine.setItemCrossReferenceDescription(stockRequestLine.getDescription());
        }
        //

        //tempStockRequest.setStatus(getResources().getString(R.string.StockRequestStatusPending));

        String jasonObj = stockRequestLine.toJson();
        String customerJsonObj = tempCustomer.toJson();

        Intent intent = new Intent(this, MsoSalesOrderItemActivity.class);
        intent.putExtra(getResources().getString(R.string.stock_request_line_jason_obj), jasonObj);
        intent.putExtra(getResources().getString(R.string.customer_json_obj), customerJsonObj);
        intent.putExtra(getResources().getString(R.string.adapter_position), -1);
        intent.putExtra("deliveryDate", deliveryDate);
        intent.putExtra("existStockRequestLineJasonObj", existStockRequestLineJasonObj);
        startActivityForResult(intent, MVS_STOCK_REQUEST_ITEM_ACTIVITY_RESULT_CODE);
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

    public void updateSummeryValues(List<StockRequestLine> stockRequestLineList_) {
        totalQty = 0;
        subTotal = 0f;
        totalVatAmount = 0f;
        grandTotal = 0f;
        mNoOfItems = 0;

        if (stockRequestLineList_.size() > 0) {
            boolean isCleard = false;
            for (StockRequestLine stockRequestLineObj : stockRequestLineList_) {
                totalQty = totalQty + Math.round(stockRequestLineObj.getQuantity());

                float objSubTotal_ = floatRound(stockRequestLineObj.getLineAmount(), 2);
                subTotal = subTotal + objSubTotal_;

                float objVatAmt_ = floatRound(stockRequestLineObj.getTotalVATAmount(), 2);
                totalVatAmount = totalVatAmount + objVatAmt_;

                float objGrandTotal_ = floatRound(stockRequestLineObj.getTotalAmountInclVAT(), 2);
                grandTotal = grandTotal + objGrandTotal_;

                if (stockRequestLineObj.getQuantity() > 0f) {
                    mNoOfItems++;

                }
            }
            txtTotalQty.setText(String.valueOf(Math.round(totalQty)));
            txtSubTotal.setText("$ " + String.format("%.2f", subTotal));
            txtGst.setText("$ " + String.format("%.2f", mNoOfItems == 0 ? 0 : totalVatAmount));
            txtGrandTotal.setText("$ " + String.format("%.2f", mNoOfItems == 0 ? 0 : grandTotal));
            txtNumberOfItems.setText(String.valueOf(mNoOfItems));
        } else {
            txtNumberOfItems.setText("");
            txtTotalQty.setText("");
            txtSubTotal.setText("");
            txtGst.setText("");
            txtGrandTotal.setText("");
        }
    }

    public List<StockRequestLine> cloneList(List<StockRequestLine> list) {
        List<StockRequestLine> clonedStockRequestLineList = new ArrayList<StockRequestLine>();
        for (int i = 0; i < list.size(); i++) {
            clonedStockRequestLineList.add(new StockRequestLine(list.get(i)));
        }
        return clonedStockRequestLineList;
    }

    public void validateDeliveryDate(String date) {
        SimpleDateFormat dfDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));

        /*Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();*/
        Date dateToday = new Date();

        try {
            if (dfDate.parse(date).before(dfDate.parse(dfDate.format(dateToday)))) {
                invalidDeliveryDate = true;
                txtDeliveryDate.setError("");
                Toast.makeText(this, getResources().getString(R.string.message_invalid_date), Toast.LENGTH_SHORT).show();
            } else {
                invalidDeliveryDate = false;
                txtDeliveryDate.setError(null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            logParams(getResources().getString(R.string.message_exception), e.getMessage());
        }
    }

    public void setDeliveryDate(String date) {
        if (!date.equals("")) {
            DateFormat df = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));
            try {

                Date initDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(date);
                deliveryDate = df.format(initDate);

                txtDeliveryDate.setText(uIDateFormat.format(initDate));

            } catch (Exception e) {
                Log.e(getResources().getString(R.string.message_exception), e.getMessage().toString());
                logParams(getResources().getString(R.string.message_exception), e.getMessage());
            }
        }
    }

    private void showCalenderSearchDialog() {
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

                setDeliveryDate(year + "-" + (month + 1) + "-" + dayOfMonth);
                validateDeliveryDate(year + "-" + (month + 1) + "-" + dayOfMonth);


                //Refresh all item price, details according to the delivery date;
                setItemList();

            }
        }, date, month, year);

        datePickerDialog.updateDate(nowYear, nowMonth, nowDay);
        datePickerDialog.show();
    }

    public void setItemList() {
        float gstPresentage = 0, totalVatAmount = 0f, totalAmountInclVAT = 0f;
        float itemUnitePrice = 0f, lineAmount = 0f;

        if (!stockRequestLineList.isEmpty()) {
            if (tempCustomer != null) {
                SalesPricesDbHandler spDb = new SalesPricesDbHandler(getApplicationContext());
                spDb.open();

                for (int i = 0; i < stockRequestLineList.size(); i++) {
                    StockRequestLine srl = stockRequestLineList.get(i);
                    getItemByCode(srl.getItemNo());
                    //get unit price
                    if(tempItem.isInventoryValueZero()){
                        itemUnitePrice = 0;
                    }else {
                        itemUnitePrice = spDb.getUnitPriceWithQuantity(
                                srl.getItemNo(),
                                tempCustomer.getCustomerPriceGroup(),
                                tempCustomer.getCode(),
                                srl.getUnitofMeasureCode() == null ? "" : srl.getUnitofMeasureCode(),
                                Math.round(srl.getQuantity()),
                                deliveryDate
                        );
                    }

                    itemUnitePrice = floatRound(itemUnitePrice, 2);

                    //line amount
                    float lineAmount_ = Math.round(srl.getQuantity()) * itemUnitePrice;
                    lineAmount = floatRound(lineAmount_, 2);

                    //gst Presentage
                    gstPresentage = getGstPercentage(tempCustomer.getCode(), srl.getItemNo());

                    //total vat
                    float totalVatAmount_ = (lineAmount * gstPresentage) / 100;
                    totalVatAmount = floatRound(totalVatAmount_, 2);

                    //garnd total
                    float totalAmountInclVAT_ = lineAmount + totalVatAmount;
                    totalAmountInclVAT = floatRound(totalAmountInclVAT_, 2);

                    srl.setUnitPrice(itemUnitePrice);
                    srl.setLineAmount(lineAmount);
                    srl.setAmount(lineAmount);
                    srl.setTaxPercentage(String.format("%.2f", gstPresentage));
                    srl.setTotalVATAmount(totalVatAmount);
                    srl.setTotalAmountInclVAT(totalAmountInclVAT);

                }

                spDb.close();
                updateSummeryValues(stockRequestLineList);
                mvsStockRequestItemAdapter.notifyDataSetChanged();
            }
        }
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    //----------------Menu Bar----------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mvs_stock_request_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
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

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.actionCustomerInfo:
                if (tempCustomer.getCode() == null) {
                    showMessageBox("Alert", "Please select a customer first");
                } else {
                    String objAsJson = tempCustomer.toJson();
                    Intent intent = new Intent(this, SalesCustomerDetailActivity.class);
                    intent.putExtra("_customerDetailObject", objAsJson);
                    intent.putExtra(getResources().getString(R.string.intent_extra_form_name),
                            getResources().getString(R.string.form_name_mvs_stock_Request));
                    this.startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

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

        if (!txtPoComments.getText().toString().equals(tempStockRequest.getExternalDocumentNo() == null ? "" : tempStockRequest.getExternalDocumentNo())) {
            isSaved = false;
        }
        if (isSaved == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MvsStockRequestActivity.this);

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

    public void updateItemCrossRefDetails() {
        if (stockRequestLineList != null) {
            if (!stockRequestLineList.isEmpty()) {
                ItemCrossReference itemCrossReference_ = new ItemCrossReference();
                ItemCrossReferenceDbHandler icrDb =
                        new ItemCrossReferenceDbHandler(getApplicationContext());

                icrDb.open();

                for (int s = 0; s < stockRequestLineList.size(); s++) {
                    StockRequestLine srl = stockRequestLineList.get(s);

                    itemCrossReference_ = icrDb.getItemCrossreference(srl.getItemNo()
                            , srl.getUnitofMeasureCode(), tempCustomer.getCode());

                    if (itemCrossReference_.getItemCrossReferenceNo() != null) {
                        srl.setItemCrossReferenceNo(itemCrossReference_.getItemCrossReferenceNo());
                    } else {
                        srl.setItemCrossReferenceNo(srl.getItemNo());
                    }

                    if (itemCrossReference_.getDescription() != null) {
                        srl.setItemCrossReferenceDescription(itemCrossReference_.getDescription());
                    } else {
                        srl.setItemCrossReferenceDescription(srl.getDescription());
                    }

                    stockRequestLineList.set(s, srl);
                }
                icrDb.close();
            }
        }
    }

    private void logParams(String type, String params) {
        mLog.info(type + params);
    }
}
