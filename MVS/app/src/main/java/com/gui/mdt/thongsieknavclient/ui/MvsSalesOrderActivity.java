package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

import com.github.seanzor.prefhelper.SharedPrefHelper;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MvsSalesOrderAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.CustomerTemplate;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;
import com.gui.mdt.thongsieknavclient.datamodel.ItemCrossReference;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.datamodel.SalesPrices;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerTemplateDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.GSTPostingSetupDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemBalancePdaDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemCrossReferenceDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesPricesDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.gui.mdt.thongsieknavclient.model.QrScanHeadModel;
import com.gui.mdt.thongsieknavclient.model.QrScanLineModel;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MvsSalesOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE = 1;
    private static final int SALES_ITEM_ACTIVITY_RESULT_CODE = 2;
    private static final int MVS_SALES_ORDER_ITEM_ACTIVITY_RESULT_CODE = 3;

    Toolbar mTbMVSSalesOrder;
    LinearLayout llItem, llSummary;
    TextView mTvToolbarTitle, mTxtCustomerCode, mTxtCustomerName, mTxtAddress, mTxtContact, mTxtCusPriceGroup, mTxtSalesPerson, mTxtDeliveryDate, mTxtNoOfItems, mTxtTotalQTY, mTxtSubTotal, mTxtGst, mTxtGrandTotal, mTxtMinSaleAmt;
    EditText mTxtScanCode, mTxtPoComments;
    RecyclerView mRecyclerViewSalesOrderDetails;
    Button mBtnSearchCus, mBtnAddressCus, mBtnContactCus, mBtnDtpicker, mBtnClear, mBtnSave, mBtnSummary, mBtnItem;

    FloatingActionButton mFabAddNewItem;

    Drawable mCusSearchDrawable, mDtPickerDrawable, mContactCusDrawable, mAddressCusDrawable, mMenuDots, mBackArrow;
    int date, month, year, mNoOfItems = 0;
    NavClientApp mApp;
    Bundle mExtras;
    String mDetails = "", todayDate = "", soNo = "", deliveryDate = "", mInValidItems = "", mPoComments = "", mTaskName = "";
    Boolean isStarted = false, isCustomerChanged = false, invalidDate = false, isSaveSuccess = false, isLoaded = false;
    public static boolean isSaved = true;
    ProgressDialog mProgressDialog;
    SimpleDateFormat mDBDateFormat, mUIDateFormat;
    Customer mTempCustomer;
    SalesOrder mTempSalesOrder;
    List<SalesOrderLine> mSalesOrderLineList, mRemoveSalesOrderLineList, mTempSalesOrderLineList;
    List<SalesPrices> salesPricesListBaseOnItemTemSeq;
    Item mTempItem;
    float totalQty = 0, subTotal = 0, totalVatAmount = 0, grandTotal = 0;
    MvsSalesOrderAdapter mMvsSalesOrderAdapter;
    SalesOrderLine mTempSalesOrderLine;
    private MenuItem mItemVoid, mItemPrint, mItemTakePicture, mItemDraftReport, mItemScan;
    UpdateCustomerTask mUpdateCustomerTask;
    SaveSalesOrderTask mSaveSalesOrderTask;
    OpenOldSOTask mOpenOldSOTask;
    private Logger mLog;

    public static List<QrScanHeadModel> qrScanHeadModels;

    //google map
    String mDestination, mAddress;

    SharedPreferences mDefaultSharedPreferences;
    SharedPrefHelper mPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(this, R.layout.activity_mvs_sales_order, null);
        setContentView(view);
        createView(view);

        mApp = (NavClientApp) getApplicationContext();
        this.mLog = Logger.getLogger(MvsSalesOrderActivity.class);

        initComponents();
        settingBtnDrawables();

        setSummaryTab();
        setClickListeners();
        setBarcodeReader();

        mExtras = getIntent().getExtras();

        if (mExtras != null) {
            if (mExtras.containsKey(getResources().getString(R.string.intent_extra_details))) {
                mDetails = mExtras.getString(getResources().getString(R.string.intent_extra_details));
            }
        }
        if (mDetails.equals(getResources().getString(R.string.mvs_new_sales_order))) {
            newSalesOrder();
        } else {
            mOpenOldSOTask = new OpenOldSOTask();
            mOpenOldSOTask.execute();
        }

        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefHelper = new SharedPrefHelper(getResources(), mDefaultSharedPreferences);

        List<String> arr = splitString("NTUC-GEYLANG LOR 38 (457)-(F51) , 612/620 GEYLANG LORONG 38"
                , 40);
        System.out.println(arr.size() + "");
    }

    public static List<String> splitString(String msg, int lineSize) {
        List<String> res = new ArrayList<>();

        Pattern p = Pattern.compile("\\b.{1," + (lineSize - 1) + "}\\b\\W?");
        Matcher m = p.matcher(msg);

        while (m.find()) {
            // System.out.println(m.group().trim());   // Debug
            res.add(m.group());
        }
        return res;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mvs_sales_order_menu, menu);

        //get menu item according to the position in menu layout
        mItemVoid = menu.getItem(0);
        mItemPrint = menu.getItem(5);
        mItemScan = menu.getItem(6);
        mItemTakePicture = menu.getItem(4);
        mItemDraftReport = menu.getItem(3);

        //change menu title to Re-Print When so is converted or confirmed
        try {


            if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))
                    || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {
                mItemPrint.setTitle("Re-Print");
            }
        } catch (NullPointerException e) {
            System.out.println(e);
            logParams(getResources().getString(R.string.message_exception), "192", e.getMessage());
        }
        //take so picture is removed  cus req
        // mItemTakePicture.setVisible(false);

        SpannableString mSpannableStringVoid = new SpannableString(mItemVoid.getTitle().toString());
        SpannableString mSpannableStringPrint = new SpannableString(mItemPrint.getTitle().toString());
        SpannableString mSpannableStringTakePic = new SpannableString(mItemTakePicture.getTitle().toString());
        SpannableString mSpannableStringPrintGreen = new SpannableString(mItemPrint.getTitle().toString());
        SpannableString mSpannableStringVoidRed = new SpannableString(mItemVoid.getTitle().toString());
        SpannableString mSpannableStringDraftReport = new SpannableString(mItemDraftReport.getTitle().toString());
        SpannableString mSpannableStringItemScan = new SpannableString(mItemScan.getTitle().toString());

        mSpannableStringVoid.setSpan(new ForegroundColorSpan(Color.GRAY), 0, mSpannableStringVoid.length(), 0);
        mSpannableStringPrint.setSpan(new ForegroundColorSpan(Color.GRAY), 0, mSpannableStringPrint.length(), 0);
        mSpannableStringTakePic.setSpan(new ForegroundColorSpan(Color.GRAY), 0, mSpannableStringTakePic.length(), 0);
        mSpannableStringDraftReport.setSpan(new ForegroundColorSpan(Color.GRAY), 0, mSpannableStringDraftReport.length(), 0);
        mSpannableStringItemScan.setSpan(new ForegroundColorSpan(Color.GRAY), 0, mSpannableStringItemScan.length(), 0);

        mSpannableStringPrintGreen.setSpan(new ForegroundColorSpan(Color.GREEN), 0, mSpannableStringPrint.length(), 0);
        mItemPrint.setTitle(mSpannableStringPrintGreen);

        mSpannableStringVoidRed.setSpan(new ForegroundColorSpan(Color.RED), 0, mSpannableStringVoidRed.length(), 0);
        mItemVoid.setTitle(mSpannableStringVoidRed);
        try {
            if (mDetails.equals(getResources().getString(R.string.mvs_new_sales_order))) {
                mItemVoid.setEnabled(false);
                mItemPrint.setEnabled(false);
                mItemTakePicture.setEnabled(false);
                mItemDraftReport.setEnabled(false);
                mItemScan.setEnabled(false);

                mItemVoid.setTitle(mSpannableStringVoid);
                mItemPrint.setTitle(mSpannableStringPrint);
                mItemTakePicture.setTitle(mSpannableStringTakePic);
                mItemDraftReport.setTitle(mSpannableStringDraftReport);
                mItemScan.setTitle(mSpannableStringItemScan);
            } else {
                if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusPending))
                        || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {
                    mItemVoid.setEnabled(false);
                    mItemVoid.setTitle(mSpannableStringVoid);
                    mItemTakePicture.setEnabled(true);
                    mItemScan.setEnabled(false);
                    mItemTakePicture.setTitle(mSpannableStringTakePic);
                    mItemScan.setTitle(mSpannableStringItemScan);
                } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))) {
                /*mItemPrint.setEnabled(false);
                mItemPrint.setTitle(mSpannableStringPrint);*/
                } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {
                    mItemVoid.setEnabled(false);
                    //mItemPrint.setEnabled(false);

                    mItemVoid.setTitle(mSpannableStringVoid);
                    //mItemPrint.setTitle(mSpannableStringPrint);
                    mItemTakePicture.setEnabled(false);
                    mItemTakePicture.setTitle(mSpannableStringTakePic);
                } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
                    mItemVoid.setEnabled(false);
                    mItemVoid.setTitle(mSpannableStringVoid);
                    mItemTakePicture.setEnabled(false);
                    mItemTakePicture.setTitle(mSpannableStringTakePic);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            logParams(getResources().getString(R.string.message_exception), "286", e.getMessage());
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        //finish();
        return true;
    }

    @Override
    public void onClick(View v) {

        if (findViewById(R.id.btnSearchCus) == v) {
            if (mSalesOrderLineList.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MvsSalesOrderActivity.this);

                builder.setTitle(getResources().getString(R.string.message_title_alert));
                builder.setMessage(getResources().getString(R.string.message_changing_customer_will_refresh_stock_items));

                builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getApplication(), SalesCustomerListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(getResources().getString(R.string.intent_extra_form_name), getResources().getString(R.string.form_name_mvs_sales_order));
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
                intent.putExtra(getResources().getString(R.string.intent_extra_form_name), getResources().getString(R.string.form_name_mvs_sales_order));
                intent.putExtra(getResources().getString(R.string.intent_extra_details), getResources().getString(R.string.intent_extra_add_customer));
                startActivityForResult(intent, SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE);
            }
        }
        if (findViewById(R.id.btnItem) == v) {
            setItemTab();
        }
        if (findViewById(R.id.btnSummary) == v) {
            setSummaryTab();
        }
        if (findViewById(R.id.btnDtpicker) == v) {

            if (!mSalesOrderLineList.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MvsSalesOrderActivity.this);

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
        if (findViewById(R.id.btnAddressCus) == v) {
            if (mTempCustomer.getCode() == null) {
                showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                if (mTempCustomer.getCode() == null) {
                    mTxtCustomerCode.setError(getResources().getString(R.string.message_body_select_customer));
                }
            } else {

                getDestination();
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra(MapActivity.SEARCH_TEXT, mDestination);
                intent.putExtra(MapActivity.ADDRESS, mAddress);
                startActivity(intent);


                //this code comment by lasith ,beacuse  we implement one map
                //direct fire google navigation from current location to mDestination address.
                //mDestination = "University of Kelaniya";//for test
//                Intent in = new Intent(android.content.Intent.ACTION_VIEW,
//                        Uri.parse("google.navigation:q=" + mDestination));
//                startActivity(in);

                //get route info in google map from current location.
                /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("https://maps.google.com/maps?daddr=" + mDestination));
                startActivity(intent);*/

                //test for singapore location as start point in google map.
               /* mLatitudeText = 1.332814;
                mLongitudeText = 103.708018;
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + mLatitudeText + "," + mLongitudeText
                                + "&daddr=" + mDestination));
                startActivity(i);*/
            }
        }

        if (findViewById(R.id.btnClear) == v) {
            if (mSalesOrderLineList.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MvsSalesOrderActivity.this);

                builder.setTitle(getResources().getString(R.string.message_title_alert));
                builder.setMessage(getResources().getString(R.string.message_item_qty_will_be_cleared));

                builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {
                            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusComplete));
                        } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
                            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusVoid));
                        } else {
                            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusPending));
                        }

                        for (SalesOrderLine sol : mSalesOrderLineList) {
                            sol.setQuantity(0f);
                            sol.setQtytoInvoice(0f);
                            sol.setTotalVATAmount(0f);
                            sol.setLineAmount(0f);
                            sol.setTotalAmountExclVAT(0f);
                            sol.setTotalAmountInclVAT(0f);
                            sol.setExchangedQty(0f);
                        }
                        updateSummeryValues(mSalesOrderLineList);
                        mMvsSalesOrderAdapter.notifyDataSetChanged();
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

            //not validate delivery date on hq so
            if (mTempSalesOrder.isDownloaded() == true) {
                invalidDate = false;
            } else {
                validateDeliveryDate(deliveryDate);
            }

            if (!invalidDate) {
                if (mTempCustomer.getCode() != null) {
                    if (mSalesOrderLineList.size() > 0) {
                        updateSummeryValues(mSalesOrderLineList);
                        /*if (Double.parseDouble(String.valueOf(h_grandTotal)) > mTempCustomer.getMinimumSalesAmount()) {*/
                        /*if (totalQty >= 0) {*/
                        if (validateGTAndSalesQty()) {
                            if (validateVehicleQtyOnList(mSalesOrderLineList,
                                    mApp.getmCurrentDriverCode(),
                                    getApplicationContext())) {
                                mTaskName = "SoSaveMain";
                                mSaveSalesOrderTask = new SaveSalesOrderTask();
                                mSaveSalesOrderTask.execute((Void) null);

                            } else {
                                showMessageBox(getResources().getString(R.string.message_title_alert)
                                        , getResources().getString(R.string.message_so_unable_to_save_vcl_bal_qty) + '\n'
                                                + mInValidItems);
                            }
                        } else {
                            Toast.makeText(mApp, getResources().getString(R.string.message_total_sale_qty_greater_then_zero), Toast.LENGTH_SHORT).show();
                        }

                            /*}
                            else {
                                Toast.makeText(mApp, getResources().getString(R.string.message_total_qty_cant_be_negative), Toast.LENGTH_SHORT).show();
                            }*/

                        /*}
                        else {
                            Toast.makeText(mApp, getResources().getString(R.string.message_Minimum_sales_amount_validation), Toast.LENGTH_SHORT).show();
                        }*/


                    } else {
                        Toast.makeText(mApp, getResources().getString(R.string.message_add_at_least_one_item), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mTxtCustomerCode.setError("");
                    Toast.makeText(mApp, getResources().getString(R.string.message_body_select_customer), Toast.LENGTH_SHORT).show();
                }

            }
        }
        if (findViewById(R.id.fabAddNewItem) == v) {
            if (mTempCustomer.getCode() == null) {
                showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                if (mTempCustomer.getCode() == null) {
                    mTxtCustomerCode.setError(getResources().getString(R.string.message_body_select_customer));
                }

            } else {
                String customerJsonObj = mTempCustomer.toJson();
                Intent intent = new Intent(getApplication(), SalesItemSearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(getResources().getString(R.string.intent_extra_form_name), getResources().getString(R.string.form_name_mvs_sales_order));
                intent.putExtra(getResources().getString(R.string.customer_json_obj), customerJsonObj);
                intent.putExtra(getResources().getString(R.string.delivery_date), deliveryDate);
                intent.putExtra(getResources().getString(R.string.intent_extra_details), getResources().getString(R.string.intent_extra_add_new_item));
                startActivityForResult(intent, SALES_ITEM_ACTIVITY_RESULT_CODE);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (!mTxtPoComments.getText().toString().equals(mTempSalesOrder.getExternalDocumentNo() == null ? "" : mTempSalesOrder.getExternalDocumentNo())) {
            isSaved = false;
        }
        if (isSaved == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MvsSalesOrderActivity.this);

            builder.setTitle(getResources().getString(R.string.message_title_alert));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Add, Change Customer
        if (requestCode == SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extraData = data.getExtras();
                if (extraData != null) {
                    if (extraData.containsKey(getResources().getString(R.string.customer_json_obj))) {
                        String objAsJson = extraData.getString(getResources().getString(R.string.customer_json_obj));
                        mTempCustomer = Customer.fromJson(objAsJson);

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
                        mTempItem = Item.fromJson(objAsJson);

                        addItem();
                    }
                }
            }
        }
        if (requestCode == MVS_SALES_ORDER_ITEM_ACTIVITY_RESULT_CODE) { //Updating Qty on specific item
            if (resultCode == RESULT_OK) {

                Bundle extraData = data.getExtras();

                if (extraData != null) {
                    if (extraData.containsKey(getResources().getString(R.string.sales_order_line_obj))) {

                        float lineAmount = 0, itemUnitePrice = 0;
                        String objAsJson = "", timeStamp = "", key = "";
                        boolean isExist = false, isADuplicateRecord = false;

                        objAsJson = extraData.getString(getResources().getString(R.string.sales_order_line_obj));

                        mTempSalesOrderLine = SalesOrderLine.fromJson(objAsJson);

                        if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {
                            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusComplete));
                        } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
                            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusVoid));
                        } else {
                            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusPending));
                        }

                        key = mTempSalesOrderLine.getNo() + soNo + "#" + mTempSalesOrderLine.getUnitofMeasure();

                        int position = extraData.getInt(getResources().getString(R.string.adapter_position));
                        int index = 0;
                        getItemByCode(mTempSalesOrderLine.getNo());
                        if(mTempItem.isInventoryValueZero()){
                            itemUnitePrice = 0;
                        }else{
                            itemUnitePrice = getUnitPrice(mTempSalesOrderLine.getNo(), mTempCustomer.getCustomerPriceGroup(), mTempCustomer.getCode(), mTempSalesOrderLine.getUnitofMeasure());
                        }


                        if (mSalesOrderLineList.size() == 0) {
                            mTempSalesOrderLine.setUnitPrice(itemUnitePrice);

                            mTempSalesOrderLine.setKey(key);
                            updateSalesOrderLineObject(mTempSalesOrderLine);
                            mSalesOrderLineList.add(mTempSalesOrderLine);
                            mTempSalesOrderLine.setLineNo(String.valueOf(mSalesOrderLineList.size()));

                            if (mSalesOrderLineList.size() == 1) {

                                mMvsSalesOrderAdapter = new MvsSalesOrderAdapter(mSalesOrderLineList
                                        , R.layout.item_mvs_sales_order_detail_card
                                        , MvsSalesOrderActivity.this
                                        , mTempSalesOrder.getStatus(), mTempCustomer, deliveryDate);

                                mRecyclerViewSalesOrderDetails.setAdapter(mMvsSalesOrderAdapter);
                            }

                            updateSummeryValues(mSalesOrderLineList);
                            mMvsSalesOrderAdapter.notifyDataSetChanged();
                            mRecyclerViewSalesOrderDetails.smoothScrollToPosition(mSalesOrderLineList.size());

                        } else {
                            ArrayList<SalesOrderLine> indexSOLArray = new ArrayList<SalesOrderLine>();
                            for (int i = 0; i < mSalesOrderLineList.size(); i++) {
                                SalesOrderLine sol = mSalesOrderLineList.get(i);

                                if (sol.getNo().equals(mTempSalesOrderLine.getNo())) {

                                    index = i;
                                    indexSOLArray.add(sol);
                                }
                            }

                            if (position > -1) //for Old Items
                            {
                                if (indexSOLArray.size() == 1) {
                                    SalesOrderLine sol = mSalesOrderLineList.get(position);

                                    SalesOrderLine tempSRLine = new SalesOrderLine(sol);
                                    mRemoveSalesOrderLineList.add(tempSRLine);

                                    sol.setUnitPrice(itemUnitePrice);
                                    sol.setKey(key); //new
                                    lineAmount = mTempSalesOrderLine.getQuantity() * mTempSalesOrderLine.getUnitPrice();

                                    sol.setUnitofMeasure(mTempSalesOrderLine.getUnitofMeasure());
                                    sol.setQuantity(mTempSalesOrderLine.getQuantity());
                                    sol.setExchangedQty(mTempSalesOrderLine.getExchangedQty());

                                    updateSalesOrderLineObject(sol);

                                    //Setting SalesOrderLine item to the list
                                    mSalesOrderLineList.set(position, sol);

                                    updateSummeryValues(mSalesOrderLineList);
                                    mMvsSalesOrderAdapter.notifyDataSetChanged();
                                }
                                if (indexSOLArray.size() > 1) {
                                    SalesOrderLine duplicateSOLObject = new SalesOrderLine(),
                                            existSRLObject = new SalesOrderLine();

                                    boolean itemExist = false;
                                    for (int i = 0; i < indexSOLArray.size(); i++) {
                                        SalesOrderLine sol = indexSOLArray.get(i);

                                        if (sol.getUnitofMeasure().equals(mTempSalesOrderLine.getUnitofMeasure())) {
                                            isADuplicateRecord = true;
                                            duplicateSOLObject = sol;
                                        }
                                        if (sol.getNo().equals(mTempSalesOrderLine.getNo()) &&
                                                sol.getUnitofMeasure().equals(mTempSalesOrderLine.getUnitofMeasure())) {

                                            itemExist = true;
                                            existSRLObject = sol;
                                        }
                                    }

                                    if (isADuplicateRecord) {
                                        for (int i = 0; i < indexSOLArray.size(); i++) {
                                            SalesOrderLine srl = indexSOLArray.get(i);

                                            if (srl.getKey().equals(mTempSalesOrderLine.getKey())) {
                                                mRemoveSalesOrderLineList.add(srl);
                                                mSalesOrderLineList.remove(srl);
                                            }
                                        }
                                        mRemoveSalesOrderLineList.add(duplicateSOLObject);// --
                                        mSalesOrderLineList.remove(duplicateSOLObject);

                                        mTempSalesOrderLine.setUnitPrice(itemUnitePrice);
                                        mTempSalesOrderLine.setKey(key); //new

                                        updateSalesOrderLineObject(mTempSalesOrderLine);

                                        mSalesOrderLineList.add(mTempSalesOrderLine);
                                        mMvsSalesOrderAdapter.notifyDataSetChanged();
                                    } else {
                                        if (existSRLObject.getKey() != null) {
                                            existSRLObject.setUnitPrice(itemUnitePrice);
                                            existSRLObject.setKey(key); //new


                                            existSRLObject.setUnitofMeasure(mTempSalesOrderLine.getUnitofMeasure());
                                            existSRLObject.setQuantity(mTempSalesOrderLine.getQuantity());
                                            existSRLObject.setExchangedQty(mTempSalesOrderLine.getExchangedQty());

                                            updateSalesOrderLineObject(existSRLObject);
                                            //Setting SalesOrderLine item to the list
                                            mSalesOrderLineList.set(position, existSRLObject);

                                            updateSummeryValues(mSalesOrderLineList);
                                            mMvsSalesOrderAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                            } else {  //  For New items
                                isExist = false;
                                for (int i = 0; i < mSalesOrderLineList.size(); i++) {
                                    SalesOrderLine sol = mSalesOrderLineList.get(i);
                                    if (sol.getNo().equals(mTempSalesOrderLine.getNo()) && sol.getUnitofMeasure().equals(mTempSalesOrderLine.getUnitofMeasure())) {
                                        isExist = true;
                                        index = i;
                                    }
                                }

                                if (isExist) {
                                    SalesOrderLine sol = mSalesOrderLineList.get(index);

                                    /*List<SalesOrderLine> clonedSalesOrderLineList = new ArrayList<SalesOrderLine>();
                                    clonedSalesOrderLineList = cloneList(salesOrderLineList);*/

                                    final SalesOrderLine fSol = new SalesOrderLine(sol);
                                    mRemoveSalesOrderLineList.add(fSol);

                                    sol.setUnitPrice(itemUnitePrice);
                                    sol.setKey(key); //new


                                    sol.setUnitofMeasure(mTempSalesOrderLine.getUnitofMeasure());
                                    sol.setQuantity(mTempSalesOrderLine.getQuantity());
                                    sol.setExchangedQty(mTempSalesOrderLine.getExchangedQty());

                                    updateSalesOrderLineObject(sol);

                                    //Setting SalesOrderLine item to the list
                                    mSalesOrderLineList.set(index, sol);

                                    updateSummeryValues(mSalesOrderLineList);
                                    mMvsSalesOrderAdapter.notifyDataSetChanged();

                                } else {
                                    mTempSalesOrderLine.setUnitPrice(itemUnitePrice);

                                    mTempSalesOrderLine.setKey(key);

                                    updateSalesOrderLineObject(mTempSalesOrderLine);

                                    mSalesOrderLineList.add(mTempSalesOrderLine);

                                    updateSummeryValues(mSalesOrderLineList);
                                    mMvsSalesOrderAdapter.notifyDataSetChanged();
                                    mRecyclerViewSalesOrderDetails.smoothScrollToPosition(mSalesOrderLineList.size());
                                }
                            }
                        }
                        updateSummeryValues(mSalesOrderLineList);

                        updateItemCrossRefDetails();
                        mMvsSalesOrderAdapter.notifyDataSetChanged();

                        Toast.makeText(mApp, getResources().getString(R.string.message_updated), Toast.LENGTH_SHORT).show();
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
                logParams(getResources().getString(R.string.message_exception), "795", e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MvsSalesOrderActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {

                    //update ui
                    mTxtCustomerCode.setText(mTempCustomer.getCode());
                    mTxtCustomerName.setText(mTempCustomer.getName());
                    mTxtAddress.setText(mTempCustomer.getAddress());
                    mTxtContact.setText(mTempCustomer.getPhoneNo());
                    mTxtCusPriceGroup.setText(mTempCustomer.getCustomerPriceGroup());
                    mTxtSalesPerson.setText(mTempCustomer.getSalespersonCode());
                    mTxtMinSaleAmt.setText(String.valueOf(mTempCustomer.getMinimumSalesAmount()));

                    updateSummeryValues(mSalesOrderLineList);
                    mTxtCustomerCode.setError(null);

                    mMvsSalesOrderAdapter = new MvsSalesOrderAdapter(mSalesOrderLineList,
                            R.layout.item_mvs_sales_order_detail_card,
                            MvsSalesOrderActivity.this, mTempSalesOrder.getStatus(),
                            mTempCustomer, deliveryDate);

                    mRecyclerViewSalesOrderDetails.setAdapter(mMvsSalesOrderAdapter);
                    mMvsSalesOrderAdapter.notifyDataSetChanged();

                    mProgressDialog.dismiss();

                    Toast.makeText(mApp, getResources().getString(R.string.message_customer_added), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    logParams(getResources().getString(R.string.message_exception), "841", e.getMessage());
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
        if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {
            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusComplete));
        } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusVoid));
        } else {
            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusPending));
        }

        List<CustomerTemplate> cusTemplateSalesItems
                = new ArrayList<CustomerTemplate>();

        cusTemplateSalesItems = getSalesItemsBaseOnCusTemplate(mTempCustomer.getCode());

        //remove items which are not in customer's template list

        CustomerTemplateDbHandler ctDB
                = new CustomerTemplateDbHandler(getApplicationContext());
        ctDB.open();

        if (mSalesOrderLineList.size() > 0) {
            //removeSrLineList = new ArrayList<StockRequestLine>();

            List<SalesOrderLine> temparysoLineRemoveList = new ArrayList<SalesOrderLine>();
            for (SalesOrderLine sol : mSalesOrderLineList) {
                if (!ctDB.isCusTempExistByCusNoAndItemNo(mTempCustomer.getCode()
                        , sol.getNo())) {
                    mRemoveSalesOrderLineList.add(sol);
                    temparysoLineRemoveList.add(sol);
                }
            }
            if (!temparysoLineRemoveList.isEmpty()) {
                for (SalesOrderLine solR : temparysoLineRemoveList) {
                    mSalesOrderLineList.remove(solR);
                }
            }
        }
        ctDB.close();

        //cloned sales order line list
        mTempSalesOrderLineList = cloneList(mSalesOrderLineList);

        //clear main list
        mSalesOrderLineList = new ArrayList<SalesOrderLine>();

        //Add new items to salesOrderLineList **************************************
        if (!cusTemplateSalesItems.isEmpty()) {
            for (CustomerTemplate ct : cusTemplateSalesItems) {

                getItemByCode(ct.getItemNo());

                if (ct.getItemUom() == null) {
                    if (mTempItem.getItemBaseUom() != null) {
                        ct.setItemUom(mTempItem.getItemBaseUom());
                    } else {
                        ct.setItemUom("");
                    }
                } else if (ct.getItemUom().equals("")) {
                    if (mTempItem.getItemBaseUom() != null) {
                        ct.setItemUom(mTempItem.getItemBaseUom());
                    }
                }

                String key = ct.getItemNo()
                        + soNo
                        + getResources().getString(R.string.hash_symbol)
                        + ct.getItemUom();

                SalesPricesDbHandler spDb = new SalesPricesDbHandler(getApplicationContext());
                spDb.open();
                float itemUnitePrice = 0f;
//                getItemByCode(ct.getItemNo());
                if(mTempItem.isInventoryValueZero()){
                    itemUnitePrice = 0f;
                }else {
                      itemUnitePrice = spDb.getUnitPriceWithQuantity(ct.getItemNo(),
                            mTempCustomer.getCustomerPriceGroup(),
                            mTempCustomer.getCode(),
                            ct.getItemUom(),
                            Math.round(ct.getQuantity()),
                            deliveryDate);
                }
                spDb.close();

                SalesOrderLine salesOrderLine = new SalesOrderLine();

                int adapterPosition = -1;

                salesOrderLine.setKey(key);
                salesOrderLine.setType(2);
                salesOrderLine.setNo(ct.getItemNo());
                salesOrderLine.setDriverCode(mApp.getmCurrentDriverCode());
                salesOrderLine.setDescription(mTempItem.getDescription());
                salesOrderLine.setQuantity(ct.getQuantity());  //New item
                salesOrderLine.setUnitofMeasure(ct.getItemUom());
                salesOrderLine.setSalesPriceExist(false);

                salesOrderLine.setUnitPrice(itemUnitePrice);
                salesOrderLine.setLineAmount(0);  //Quantity is 0  (new Item)
                salesOrderLine.setSalesLineDiscExists(false);
                salesOrderLine.setLineDiscountPercent(getResources().getString(R.string.double_amount_zero));
                salesOrderLine.setLineDiscountAmount(0.0f);
                salesOrderLine.setQtytoInvoice(ct.getQuantity());
                salesOrderLine.setQuantityInvoiced(0f);

                salesOrderLine.setDocumentNo(mTempSalesOrder.getNo());

                salesOrderLine.setTotalAmountExclVAT(0f); //line Amount
                salesOrderLine.setTotalVATAmount(0f);   //line amount X 7%(Prescentage)
                salesOrderLine.setTotalAmountInclVAT(0f); //line amount + line amount X 7%(Prescentage)

                float gstPresentage = getGstPercentage(mTempCustomer.getCode(), mTempItem.getItemCode());

                salesOrderLine.setTaxPercentage(String.valueOf(gstPresentage));
                salesOrderLine.setExchangedQty(0f);

                if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {
                    mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusComplete));
                } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
                    mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusVoid));
                } else {
                    mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusPending));
                }

                updateSalesOrderLineObject(salesOrderLine);

                mSalesOrderLineList.add(salesOrderLine);
            }
        }

        //update salesOrderLineList from old list(tempSalesOrderLineList)
        if (!mTempSalesOrderLineList.isEmpty()) {
            if (mSalesOrderLineList.isEmpty()) {
                mSalesOrderLineList = new ArrayList<SalesOrderLine>(mTempSalesOrderLineList);
            } else {
                for (SalesOrderLine tempSol : mTempSalesOrderLineList) {
                    for (int i = 0; i < mSalesOrderLineList.size(); i++) {
                        SalesOrderLine sol = mSalesOrderLineList.get(i);

                        if (sol.getNo().equals(tempSol.getNo()) && sol.getUnitofMeasure().equals(tempSol.getUnitofMeasure())) {
                            sol.setQuantity(tempSol.getQuantity());

                            //update price details unit price, total ext...
                            updateSalesOrderLineObject(sol);
                            mSalesOrderLineList.set(i, sol);
                        } else {
                            //updateSalesOrderLineObject(srl);
                            //stockRequestLineList.add(srl);
                        }
                    }
                }
            }
        }

        if (!mTempSalesOrderLineList.isEmpty()) {
            for (SalesOrderLine srl : mTempSalesOrderLineList) {
                mRemoveSalesOrderLineList.add(srl);
            }
        }

        updateItemCrossRefDetails();

        setAllItems();

        isCustomerChanged = true;
        isSaved = false;
    }

    public void newSalesOrder() {
        clearFields();

        //Default So NO
        /*SimpleDateFormat soNumberFormat = new SimpleDateFormat("yyyy-MM-ddhh:mm:ss");
        soNo = "SO" + soNumberFormat.format(new Date());
        mTvToolbarTitle.setText(soNo);*/


        //mTempSalesOrder.setNo(soNo);
        mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusPending));

        //Setting Delivery Date
        /*Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();*/
        Date dateToday = new Date();

        String tomorrowDate = mUIDateFormat.format(dateToday);
        mTxtDeliveryDate.setText(tomorrowDate);
        deliveryDate = mDBDateFormat.format(dateToday);

        //So no
        soNo = getLatestSoId(mTxtDeliveryDate.getText().toString());
        mTvToolbarTitle.setText(soNo);
        mTempSalesOrder.setNo(soNo);

        clearRecyclerView();
        initSwipeDelete();
    }

    private class OpenOldSOTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {

            mTvToolbarTitle.setText("Sales Order");
            mProgressDialog = new ProgressDialog(MvsSalesOrderActivity.this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();

            mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!isLoaded) {
                        if (mOpenOldSOTask != null) {
                            mOpenOldSOTask.cancel(true);
                        }
                        finish();
                    }
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                oldSalesOrder();

                //item refersh
                if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusPending))
                        || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))
                        || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
                    //setAllItems();
                    if (!mSalesOrderLineList.isEmpty()) {

                        if (mTempCustomer != null) {
                            for (SalesOrderLine sol : mSalesOrderLineList) {

                                // Escape early if cancel() is called
                                if (isCancelled()) break;

                                float gstPresentage = 0f, itemUnitePrice = 0f, lineAmount = 0f, totalVatAmount = 0f, totalAmountInclVAT = 0f;

                                gstPresentage = getGstPercentage(mTempCustomer.getCode(), sol.getNo());


                                getItemByCode(sol.getNo());
                                if(mTempItem.isInventoryValueZero()){
                                    itemUnitePrice = 0f;
                                }else{
                                    SalesPricesDbHandler spDb = new SalesPricesDbHandler(getApplicationContext());
                                    spDb.open();
                                    itemUnitePrice = spDb.getUnitPriceWithQuantity(sol.getNo(),
                                            mTempCustomer.getCustomerPriceGroup(),
                                            mTempCustomer.getCode(),
                                            sol.getUnitofMeasure(),
                                            Math.round(sol.getQuantity()),
                                            deliveryDate);
                                    spDb.close();
                                }
                                itemUnitePrice = floatRound(itemUnitePrice, 2);


                                //line amount
                                float lineAmount_ = Math.round(sol.getQuantity()) * itemUnitePrice;
                                lineAmount = floatRound(lineAmount_, 2);

                                //vat amount
                                float totalVatAmount_ = (lineAmount * gstPresentage) / 100;
                                totalVatAmount = floatRound(totalVatAmount_, 2);

                                float totalAmountInclVAT_ = lineAmount + totalVatAmount;
                                totalAmountInclVAT = floatRound(totalAmountInclVAT_, 2);

                                sol.setUnitPrice(itemUnitePrice);
                                sol.setLineAmount(lineAmount);
                                sol.setTotalAmountExclVAT(lineAmount);
                                sol.setTotalVATAmount(totalVatAmount);
                                sol.setTotalAmountInclVAT(totalAmountInclVAT);
                                sol.setTaxPercentage(String.valueOf(gstPresentage));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(getResources().getString(R.string.message_exception), e.toString());
                logParams(getResources().getString(R.string.message_exception), "1162", e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    isLoaded = true;
                    mProgressDialog.dismiss();

                    if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))
                            || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))
                            || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {
                        mTvToolbarTitle.setText(mTempSalesOrder.getSINo());
                    } else {
                        mTvToolbarTitle.setText(mTempSalesOrder.getNo());
                    }

                    mTxtCustomerName.setText(mTempSalesOrder.getSelltoCustomerName());
                    mTxtCustomerCode.setText(mTempSalesOrder.getSelltoCustomerNo());
                    mTxtContact.setText(mTempSalesOrder.getSelltoContactNo());
                    mTxtAddress.setText(mTempSalesOrder.getSelltoAddress());
                    mTxtCusPriceGroup.setText(mTempCustomer.getCustomerPriceGroup());
                    mTxtPoComments.setText(mTempSalesOrder.getExternalDocumentNo());
                    mTxtMinSaleAmt.setText(String.valueOf(mTempCustomer.getMinimumSalesAmount()));
                    mTxtSalesPerson.setText(mTempCustomer.getSalespersonCode());

                    if (mTempSalesOrder.getShipmentDate() != null) {
                        if (!mTempSalesOrder.getShipmentDate().equals("")) {
                            String date_ = mTempSalesOrder.getShipmentDate().toString();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_dd_MM_yyyy));
                            try {

                                Date initDate
                                        = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(mTempSalesOrder.getShipmentDate().toString());
                                date_ = simpleDateFormat.format(initDate);

                            } catch (Exception e) {
                                Log.e(getResources().getString(R.string.message_exception), e.getMessage().toString());
                                logParams(getResources().getString(R.string.message_exception), "1176", e.getMessage());
                            }

                            mTxtDeliveryDate.setText(date_);
                        }
                    }

                    mTxtContact.setText(mTempCustomer.getPhoneNo());

                    updateSummeryValues(mSalesOrderLineList);

                    clearRecyclerView();
                    initSwipeDelete();
                    if (mSalesOrderLineList.size() > 0) {
                        mMvsSalesOrderAdapter = new MvsSalesOrderAdapter(mSalesOrderLineList,
                                R.layout.item_mvs_sales_order_detail_card
                                , MvsSalesOrderActivity.this, mTempSalesOrder.getStatus()
                                , mTempCustomer, deliveryDate);
                        mRecyclerViewSalesOrderDetails.setAdapter(mMvsSalesOrderAdapter);
                        mMvsSalesOrderAdapter.notifyDataSetChanged();
                    }

                    applyStatus();

                } catch (Exception e) {
                    Log.d(getResources().getString(R.string.message_exception), e.toString());
                    logParams(getResources().getString(R.string.message_exception), "1202", e.getMessage());
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

    public void oldSalesOrder() {

        String objAsJson = mExtras.getString(getResources().getString(R.string.sales_order_jason_obj));
        mTempSalesOrder = SalesOrder.fromJson(objAsJson);
        deliveryDate = mTempSalesOrder.getShipmentDate();

        soNo = mTempSalesOrder.getNo();
        //Get Salesorderline List
        getSalesOrderLineList(mTempSalesOrder.getNo());

        //Get Customer Details
        getCustomer(mTempSalesOrder.getSelltoCustomerNo());

        // updates list,  item cross reference . b_itemNo , description
        updateItemCrossRefDetails();
    }

    public void applyStatus() {
        if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {
            mBtnSearchCus.setEnabled(false);
            mBtnDtpicker.setEnabled(false);
            mBtnClear.setEnabled(false);
            mBtnSave.setEnabled(false);
            mFabAddNewItem.setVisibility(View.GONE);
            mFabAddNewItem.setEnabled(false);
            mFabAddNewItem.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            mTxtPoComments.setEnabled(false);
            mTxtScanCode.setEnabled(false);

            Toast.makeText(mApp, getResources().getString(R.string.not_allowed_to_edit), Toast.LENGTH_SHORT).show();
        }
        if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {
            mBtnSearchCus.setEnabled(true); //2018-02-28 /Req Changed - Can change customer on hq down sr
            mBtnDtpicker.setEnabled(false);
            mBtnClear.setEnabled(true);
            mBtnSave.setEnabled(true);
            //mFabAddNewItem.setVisibility(View.GONE);
            mFabAddNewItem.setEnabled(true);
            mFabAddNewItem.setBackgroundTintList(
                    ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            mTxtPoComments.setEnabled(true);
            mTxtScanCode.setEnabled(true);
        }
        if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))) {
            mBtnSearchCus.setEnabled(false);
            mBtnDtpicker.setEnabled(false);
            mBtnClear.setEnabled(false);
            mBtnSave.setEnabled(false);
            mFabAddNewItem.setVisibility(View.GONE);
            mFabAddNewItem.setEnabled(false);
            mFabAddNewItem.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            mTxtPoComments.setEnabled(false);
            mTxtScanCode.setEnabled(false);
            Toast.makeText(mApp, getResources().getString(R.string.not_allowed_to_edit), Toast.LENGTH_SHORT).show();
        }
        if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
            if (mTempSalesOrder.isDownloaded()) {
                mBtnSearchCus.setEnabled(false);

                mBtnDtpicker.setEnabled(true);
                mBtnClear.setEnabled(true);
                mBtnSave.setEnabled(true);
                mFabAddNewItem.setEnabled(true);
                mFabAddNewItem.setBackgroundTintList(
                        ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                mTxtPoComments.setEnabled(true);
                mTxtScanCode.setEnabled(true);

            } else {
                mBtnSearchCus.setEnabled(true);
                mBtnDtpicker.setEnabled(true);
                mBtnClear.setEnabled(true);
                mBtnSave.setEnabled(true);
                mFabAddNewItem.setEnabled(true);
                mFabAddNewItem.setBackgroundTintList(
                        ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                mTxtPoComments.setEnabled(true);
                mTxtScanCode.setEnabled(true);
            }
        }
    }

    public String getLatestSoId(String deliveryDate) {
        UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
        dbHandler.open();
        String soId = "";

        soId = dbHandler.getLatestSoRunningNoMVS(mApp.getCurrentUserName(), deliveryDate);

        dbHandler.close();
        return soId;
    }

    public void initSwipeDelete() {

        if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusPending))
                || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))
                || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
            ItemTouchHelper.SimpleCallback simpleCallback
                    = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView,
                                      RecyclerView.ViewHolder viewHolder,
                                      RecyclerView.ViewHolder target) {
                    return false;
                }

                //avoid swipe delete on converted & confirmed so line items
                @Override
                public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))
                            || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {
                        if (viewHolder instanceof MvsSalesOrderAdapter.SalesItemViewHolder)
                            return 0;
                    }
                    return super.getSwipeDirs(recyclerView, viewHolder);
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                    if (direction == ItemTouchHelper.RIGHT) {    //if swipe Right

                        AlertDialog.Builder builder
                                = new AlertDialog.Builder(MvsSalesOrderActivity.this); //alert for confirm to delete
                        builder.setMessage(getResources().getString(R.string.message_remove_item));    //set message
                        builder.setCancelable(false);
                        builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() { //when click on DELETE
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SalesOrderLine sol = mSalesOrderLineList.get(position);
                                mRemoveSalesOrderLineList.add(sol);

                                mSalesOrderLineList.remove(position);
                                updateSummeryValues(mSalesOrderLineList);
                                mMvsSalesOrderAdapter.notifyDataSetChanged();
                                isSaved = false;
                            }
                        }).setNegativeButton(getResources().getString(R.string.button_text_no), new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMvsSalesOrderAdapter.notifyDataSetChanged();
                            }
                        }).show();
                    }
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(mRecyclerViewSalesOrderDetails);
        }
    }

    public void clearRecyclerView() {
        mMvsSalesOrderAdapter = new MvsSalesOrderAdapter(new ArrayList<SalesOrderLine>()
                , R.layout.item_mvs_sales_order_detail_card
                , MvsSalesOrderActivity.this
                , mTempSalesOrder.getStatus()
                , new Customer()
                , deliveryDate);
        mRecyclerViewSalesOrderDetails.setAdapter(mMvsSalesOrderAdapter);
    }

    private void getSalesOrderLineList(String No) {
        SalesOrderLineDbHandler dbAdapter = new SalesOrderLineDbHandler(this);
        dbAdapter.open();

        mSalesOrderLineList = dbAdapter.getAllSalesOrderLinesByDocumentNo(No);

        //remove item no empty items
        if (mSalesOrderLineList.size() > 0) {
            for (int index = 0; index < mSalesOrderLineList.size(); index++) {
                SalesOrderLine sol = mSalesOrderLineList.get(index);
                if (sol.getNo() == null) {
                    mSalesOrderLineList.remove(index);
                } else {
                    if (sol.getNo().equals("")) {
                        mSalesOrderLineList.remove(index);
                    }
                }
            }
        }

        dbAdapter.close();
    }

    private void getCustomer(String Code) {
        boolean status = false;
        CustomerDbHandler dbAdapter = new CustomerDbHandler(this);
        dbAdapter.open();

        mTempCustomer = dbAdapter.getCustomerByCustomerCode(Code);

        dbAdapter.close();
    }

    public void clearFields() {
        float emptyFloat = 0f;

        mTxtPoComments.setText("");
        mTxtCustomerCode.setText("");
        mTxtCustomerName.setText("");
        mTxtAddress.setText("");
        mTxtContact.setText("");
        mTxtCusPriceGroup.setText("");
        mTxtSalesPerson.setText("");
        mTxtDeliveryDate.setText("");
        mTxtNoOfItems.setText("");
        mTxtTotalQTY.setText("");
        mTxtSubTotal.setText("");
        mTxtGst.setText("");
        mTxtGrandTotal.setText("");

        mTempCustomer = new Customer();
        mTempSalesOrder = new SalesOrder();
        mSalesOrderLineList = new ArrayList<SalesOrderLine>();

        mTxtNoOfItems.setText(String.valueOf(Math.round(emptyFloat)));
        mTxtTotalQTY.setText(String.valueOf(Math.round(emptyFloat)));
        mTxtSubTotal.setText(String.valueOf(emptyFloat));
        mTxtGst.setText(String.valueOf(emptyFloat));
        mTxtGrandTotal.setText(String.valueOf(emptyFloat));


        mRemoveSalesOrderLineList = new ArrayList<SalesOrderLine>();
        salesPricesListBaseOnItemTemSeq = new ArrayList<SalesPrices>();
        mTempSalesOrderLineList = new ArrayList<SalesOrderLine>();
    }

    public void createView(View view) {

        mTbMVSSalesOrder = (Toolbar) view.findViewById(R.id.tbMVSSalesOrder);
        llItem = (LinearLayout) view.findViewById(R.id.llItem);
        llSummary = (LinearLayout) view.findViewById(R.id.llSummary);

        mTvToolbarTitle = (TextView) view.findViewById(R.id.tvToolbarTitle);
        mTxtCustomerCode = (TextView) view.findViewById(R.id.txtCustomerCode);
        mTxtCustomerName = (TextView) view.findViewById(R.id.txtCustomerName);
        mTxtAddress = (TextView) view.findViewById(R.id.txtAddress);
        mTxtContact = (TextView) view.findViewById(R.id.txtContact);
        mTxtCusPriceGroup = (TextView) view.findViewById(R.id.txtCusPriceGroup);
        mTxtSalesPerson = (TextView) view.findViewById(R.id.txtSalesPerson);
        mTxtDeliveryDate = (TextView) view.findViewById(R.id.txtDeliveryDate);
        mTxtNoOfItems = (TextView) view.findViewById(R.id.txtNoOfItems);
        mTxtTotalQTY = (TextView) view.findViewById(R.id.txtTotalQTY);
        mTxtSubTotal = (TextView) view.findViewById(R.id.txtSubTotal);
        mTxtGst = (TextView) view.findViewById(R.id.txtGst);
        mTxtGrandTotal = (TextView) view.findViewById(R.id.txtGrandTotal);
        mTxtMinSaleAmt = (TextView) view.findViewById(R.id.txtMinimumSalesAmount);

        mTxtScanCode = (EditText) view.findViewById(R.id.txtScanCode);
        mTxtPoComments = (EditText) view.findViewById(R.id.txtPoComments);

        mRecyclerViewSalesOrderDetails =
                (RecyclerView) view.findViewById(R.id.recyclerViewSalesOrderDetails);

        mBtnSearchCus = (Button) view.findViewById(R.id.btnSearchCus);
        mBtnAddressCus = (Button) view.findViewById(R.id.btnAddressCus);
        mBtnContactCus = (Button) view.findViewById(R.id.btnContactCus);
        mBtnDtpicker = (Button) view.findViewById(R.id.btnDtpicker);
        mBtnClear = (Button) view.findViewById(R.id.btnClear);
        mBtnSave = (Button) view.findViewById(R.id.btnSave);

        mBtnSummary = (Button) view.findViewById(R.id.btnSummary);
        mBtnItem = (Button) view.findViewById(R.id.btnItem);

        mFabAddNewItem = (FloatingActionButton) view.findViewById(R.id.fabAddNewItem);
    }

    public void initComponents() {
        mFabAddNewItem.bringToFront();

        mDBDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));
        mUIDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_dd_MM_yyyy));

        //Setting White color for editable fields
        mTxtCustomerCode.setBackgroundColor(getResources().getColor(R.color.white));
        mTxtDeliveryDate.setBackgroundColor(getResources().getColor(R.color.white));
        mTxtPoComments.setBackgroundColor(getResources().getColor(R.color.white));

        mTxtPoComments.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mRecyclerViewSalesOrderDetails.setHasFixedSize(true);
        mRecyclerViewSalesOrderDetails.setLayoutManager(new LinearLayoutManager(this));

        mRemoveSalesOrderLineList = new ArrayList<SalesOrderLine>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(getResources().getString(R.string.time_zone_gmt)));
        todayDate = sdf.format(new Date());
    }

    public void settingBtnDrawables() {

        mMenuDots = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_ellipsis_v).color(Color.WHITE).sizeDp(30);
        mBackArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE).sizeDp(30);
        mCusSearchDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_search)
                .color(getResources().getColor(R.color.colorPrimary)).sizeDp(25);
        mDtPickerDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_calendar)
                .color(getResources().getColor(R.color.colorPrimary))
                .sizeDp(25);
        mContactCusDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_phone)
                .color(getResources().getColor(R.color.colorPrimary))
                .sizeDp(25);
        mAddressCusDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_location_arrow)
                .color(getResources().getColor(R.color.colorPrimary))
                .sizeDp(25);

        setSupportActionBar(mTbMVSSalesOrder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTbMVSSalesOrder.setOverflowIcon(mMenuDots);

        getSupportActionBar().setHomeAsUpIndicator(mBackArrow);
        mBtnSearchCus.setBackgroundDrawable(mCusSearchDrawable);
        mBtnDtpicker.setBackgroundDrawable(mDtPickerDrawable);
        mBtnContactCus.setBackgroundDrawable(mContactCusDrawable);
        mBtnAddressCus.setBackgroundDrawable(mAddressCusDrawable);
    }

    public void setClickListeners() {
        mFabAddNewItem.setOnClickListener(this);
        mBtnSearchCus.setOnClickListener(this);
        mBtnAddressCus.setOnClickListener(this);
        mBtnDtpicker.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mBtnSummary.setOnClickListener(this);
        mBtnItem.setOnClickListener(this);
    }

    public void setBarcodeReader() {
        mTxtScanCode.setInputType(InputType.TYPE_NULL);

        mTxtScanCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mTxtScanCode.setBackgroundResource(R.drawable.border_red);
                } else {
                    mTxtScanCode.setBackgroundResource(R.drawable.border_gray);
                }
            }
        });

        mTxtScanCode.requestFocus();

        mTxtScanCode.setOnKeyListener(new View.OnKeyListener() {
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
                                                if (isItemAvailable(mTxtScanCode.getText().toString())) {
                                                    if (mTempCustomer.getCode() == null) {
                                                        showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                                                    } else {

                                                        getItem(mTxtScanCode.getText().toString());
                                                        if (mTempItem.getItemCode() != null) {
                                                            addItem();
                                                        } else {
                                                            showMessageBox(getResources().getString(R.string.message_item_not_found), getResources().getString(R.string.message_body_invalid_barcode));
                                                        }
                                                    }
                                                    mProgressDialog.dismiss();
                                                } else {
                                                    mProgressDialog.dismiss();
                                                    showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_invalid_barcode));
                                                }
                                                mTxtScanCode.setText("");
                                                mTxtScanCode.requestFocus();
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

        /*mTxtScanCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

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
                                        if (isItemAvailable(mTxtScanCode.getText().toString())) {
                                            if (mTempCustomer.getCode() == null) {
                                                showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                                            } else {

                                                getItem(mTxtScanCode.getText().toString());
                                                if (mTempItem.getItemCode() != null) {
                                                    addItem();
                                                } else {
                                                    showMessageBox(getResources().getString(R.string.message_item_not_found), getResources().getString(R.string.message_body_invalid_barcode));
                                                }
                                            }
                                            mProgressDialog.dismiss();
                                        } else {
                                            mProgressDialog.dismiss();
                                            showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_invalid_barcode));
                                        }
                                        mTxtScanCode.setText("");
                                        mTxtScanCode.requestFocus();
                                        isStarted = false;
                                    }
                                }
                            }, 1000);
                }
            }
        });*/
    }

    private void getItemByCode(String itemCode) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        mTempItem = dbAdapter.getItemByCode(itemCode);

        dbAdapter.close();
    }

    public void addItem() {

        SalesOrderLine existSalesOrderLine = new SalesOrderLine();

        // if item exist
        if (!mSalesOrderLineList.isEmpty()) {

            for (SalesOrderLine sol : mSalesOrderLineList) {
                if (sol.getNo().equals(mTempItem.getItemCode())) {
                    existSalesOrderLine = sol;
                    break;
                }
            }
        }
        String existSalesOrderLineJasonObj = existSalesOrderLine.toJson();

        SalesOrderLine salesOrderLine = new SalesOrderLine();
        String timeStamp = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd_HH_mm_ss)).format(new Date());
        String key = mTempItem.getItemCode()
                + soNo
                + getResources().getString(R.string.hash_symbol)
                + mTempItem.getItemBaseUom();
        int adapterPosition = -1;

        salesOrderLine.setKey(key);
        salesOrderLine.setType(2);
        salesOrderLine.setNo(mTempItem.getItemCode());
        salesOrderLine.setDriverCode(mApp.getmCurrentDriverCode());
        salesOrderLine.setDescription(mTempItem.getDescription());
        salesOrderLine.setQuantity(0f);  //New item
        salesOrderLine.setUnitofMeasure(mTempItem.getItemBaseUom());
        salesOrderLine.setSalesPriceExist(false);

        salesOrderLine.setUnitPrice(0f);
        salesOrderLine.setLineAmount(0);  //Quantity is 0  (new Item)
        salesOrderLine.setSalesLineDiscExists(false);
        salesOrderLine.setLineDiscountPercent(getResources().getString(R.string.double_amount_zero));
        salesOrderLine.setLineDiscountAmount(0.0f);
        salesOrderLine.setQtytoInvoice(0f);
        salesOrderLine.setQuantityInvoiced(0f);
        salesOrderLine.setDocumentNo(mTempSalesOrder.getNo());

        salesOrderLine.setTotalAmountExclVAT(0f); //line Amount
        salesOrderLine.setTotalVATAmount(0f);   //line amount X 7%(Prescentage)
        salesOrderLine.setTotalAmountInclVAT(0f); //line amount + line amount X 7%(Prescentage)

        float gstPresentage = getGstPercentage(mTempCustomer.getCode(), mTempItem.getItemCode());

        salesOrderLine.setTaxPercentage(String.valueOf(gstPresentage));
        salesOrderLine.setExchangedQty(0f);

        //update cross reference on item
        ItemCrossReferenceDbHandler icrDb =
                new ItemCrossReferenceDbHandler(getApplicationContext());

        icrDb.open();
        ItemCrossReference itemCrossReference_ = icrDb.getItemCrossreference(salesOrderLine.getNo()
                , salesOrderLine.getUnitofMeasure(), mTempCustomer.getCode());

        icrDb.close();

        if (itemCrossReference_.getItemCrossReferenceNo() != null) {
            salesOrderLine.setItemCrossReferenceNo(itemCrossReference_.getItemCrossReferenceNo());
        } else {
            salesOrderLine.setItemCrossReferenceNo(salesOrderLine.getNo());
        }

        if (itemCrossReference_.getDescription() != null) {
            salesOrderLine.setItemCrossReferenceDescription(itemCrossReference_.getDescription());
        } else {
            salesOrderLine.setItemCrossReferenceDescription(salesOrderLine.getDescription());
        }
        //

        if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {
            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusComplete));
        } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusVoid));
        } else {
            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusPending));
        }

        String jasonObj = salesOrderLine.toJson();
        String customerJsonObj = mTempCustomer.toJson();

        Intent intent = new Intent(this, MvsSalesOrderItemActivity.class);
        intent.putExtra(getResources().getString(R.string.sales_order_line_obj), jasonObj);
        intent.putExtra(getResources().getString(R.string.customer_json_obj), customerJsonObj);
        intent.putExtra(getResources().getString(R.string.adapter_position), adapterPosition);
        intent.putExtra(getResources().getString(R.string.delivery_date), deliveryDate);
        intent.putExtra(getResources().getString(R.string.exist_sales_order_line_jason_obj), existSalesOrderLineJasonObj);
        startActivityForResult(intent, MVS_SALES_ORDER_ITEM_ACTIVITY_RESULT_CODE);
    }

    public void updateSalesOrderLineObject(SalesOrderLine sol) {
        float gstPresentage = 0f, totalVatAmount = 0f, totalAmountInclVAT = 0f;
        float itemUnitePrice = 0f, lineAmount = 0f;
        getItemByCode(sol.getNo());

        if(mTempItem.isInventoryValueZero()){
            itemUnitePrice = floatRound(0, 2);
        }else{
            SalesPricesDbHandler spDb = new SalesPricesDbHandler(getApplicationContext());
            spDb.open();

            float itemUnitePrice_ = spDb.getUnitPriceWithQuantity(sol.getNo(),
                    mTempCustomer.getCustomerPriceGroup(),
                    mTempCustomer.getCode(),
                    sol.getUnitofMeasure(),
                    Math.round(sol.getQuantity()),
                    deliveryDate);

            spDb.close();
            itemUnitePrice = floatRound(itemUnitePrice_, 2);
        }


        //Line Amount
        float lineAmount_ = Math.round(sol.getQuantity()) * itemUnitePrice;

        lineAmount = floatRound(lineAmount_, 2);

        //gst
        gstPresentage = getGstPercentage(mTempCustomer.getCode(), sol.getNo());

        //total vat
        float totalVatAmount_ = (lineAmount * gstPresentage) / 100;
        totalVatAmount = floatRound(totalVatAmount_, 2);

        //grand total
        float totalAmountInclVAT_ = lineAmount + totalVatAmount;
        totalAmountInclVAT = floatRound(totalAmountInclVAT_, 2);

        sol.setUnitPrice(itemUnitePrice);
        sol.setLineAmount(lineAmount);
        sol.setQtytoInvoice(sol.getQuantity());
        sol.setTotalAmountExclVAT(lineAmount);
        sol.setTaxPercentage(String.format(getResources().getString(R.string.decimal_two_places), gstPresentage));
        sol.setTotalVATAmount(totalVatAmount);
        sol.setTotalAmountInclVAT(totalAmountInclVAT);
    }

    public float getUnitPrice(String itemCode,
                              String customerPriceGroup,
                              String customerCode,
                              String itemUom) {
        float itemMasterUnitPrice = 0, groupItemPrice = 0, customerItemPrice = 0;

        //1 ItemMaster UnitPrice
        itemMasterUnitPrice = Float.parseFloat(getItemUnitPriceFromItemMaster(itemCode, itemUom)
                == "" ? "0" : getItemUnitPriceFromItemMaster(itemCode, itemUom));

        //2 Group ItemPrice
        groupItemPrice = Float.parseFloat(
                getGroupItemPrice(customerPriceGroup, 1, itemCode, deliveryDate, itemUom)
                        == "" ? "0" : getGroupItemPrice(customerPriceGroup, 1, itemCode, deliveryDate, itemUom));

        //3 customer ItemPrice
        customerItemPrice = Float.parseFloat(
                getCustomerItemPrice(customerCode, 0, itemCode, deliveryDate, itemUom)
                        == "" ? "0" : getCustomerItemPrice(customerCode, 0, itemCode, deliveryDate, itemUom));

        float minimunItemPrice = getMinItemPrice(itemMasterUnitPrice, groupItemPrice, customerItemPrice);

        return minimunItemPrice;
    }

    public String getCustomerItemPrice(String customerCode,
                                       int salesType,
                                       String itemCode,
                                       String deliveryDate,
                                       String itemUom) {
        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
        db.open();

        String itemPrice = db.getCustomerItemUnitPriceByCustomerCode(customerCode, salesType, itemCode, deliveryDate, itemUom);

        db.close();

        return itemPrice;
    }

    public String getGroupItemPrice(String customerGroup,
                                    int salesType,
                                    String itemCode,
                                    String deliveryDate,
                                    String itemUom) {
        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
        db.open();

        String itemPrice = db.getGroupItemPriceByCustomePriceGroup(customerGroup, salesType, itemCode, deliveryDate, itemUom);

        db.close();

        return itemPrice;
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

    public void updateSummeryValues(List<SalesOrderLine> mSalesOrderLineList_) {
        totalQty = 0;
        subTotal = 0f;
        totalVatAmount = 0f;
        grandTotal = 0f;
        mNoOfItems = 0;
        float minusTotalValues = 0f, plusTotalValues = 0f, gstPresentage = 0f,
                plusGstTotal = 0f, minusGstTotal = 0f;

        if (mSalesOrderLineList_.size() > 0) {

            for (SalesOrderLine salesOrderLineObj : mSalesOrderLineList_) {

                if ((salesOrderLineObj.getQuantity() + salesOrderLineObj.getExchangedQty() > 0)) {
                    gstPresentage = getGstPercentage(mTempCustomer.getCode(), salesOrderLineObj.getNo());

                    totalQty = totalQty + Math.round(salesOrderLineObj.getQuantity());

                    //sub total
                    float objLineAmt_ = floatRound(salesOrderLineObj.getLineAmount(), 2);
                    subTotal = subTotal + objLineAmt_;

                    float bTotalPlus = 0f, eTotalPlus = 0f, eTotalMinus = 0f;

                    bTotalPlus = floatRound(salesOrderLineObj.getQuantity()
                            * salesOrderLineObj.getUnitPrice(), 2);

                    plusTotalValues += floatRound(bTotalPlus, 2);

                    /*if (salesOrderLineObj.getExchangedQty() > 0f) {
                        eTotalPlus = floatRound(salesOrderLineObj.getExchangedQty()
                                * salesOrderLineObj.getUnitPrice(), 2);
                        eTotalMinus = eTotalPlus;

                        plusTotalValues += floatRound(bTotalPlus, 2);
                        plusTotalValues += floatRound(eTotalPlus, 2);
                        minusTotalValues += floatRound(eTotalMinus, 2);
                    } else {
                        plusTotalValues += floatRound(bTotalPlus, 2);
                    }*/

                    float salesQty
                            = salesOrderLineObj.getQuantity() + salesOrderLineObj.getExchangedQty();
                    if (salesQty > 0f) {
                        mNoOfItems++;
                    }
                }
            }

            plusGstTotal = plusTotalValues * (gstPresentage / 100);
            //minusGstTotal = minusTotalValues * (gstPresentage / 100);

            //totalVatAmount = floatRound(plusGstTotal, 2) - floatRound(minusGstTotal, 2);
            totalVatAmount = floatRound(plusGstTotal, 2);
            grandTotal = floatRound(subTotal, 2) + floatRound(totalVatAmount, 2);

            mTxtTotalQTY.setText(String.valueOf(Math.round(totalQty)));
            mTxtSubTotal.setText(getResources().getString(R.string.dollar_mark) + " " + String.format("%.2f", floatRound(subTotal, 2)));
            mTxtGst.setText(getResources().getString(R.string.dollar_mark) + " " + String.format("%.2f", floatRound(totalVatAmount, 2)));
            mTxtGrandTotal.setText(getResources().getString(R.string.dollar_mark) + " " + String.format("%.2f", floatRound(grandTotal, 2)));

            mTxtNoOfItems.setText(String.valueOf(mNoOfItems));
        } else {
            mTxtNoOfItems.setText("");
            mTxtTotalQTY.setText("");
            mTxtSubTotal.setText("");
            mTxtGst.setText("");
            mTxtGrandTotal.setText("");
        }
    }

    public List<SalesOrderLine> cloneList(List<SalesOrderLine> list) {
        List<SalesOrderLine> clonedSalesOrderLineList = new ArrayList<SalesOrderLine>();
        for (int i = 0; i < list.size(); i++) {
            clonedSalesOrderLineList.add(new SalesOrderLine(list.get(i)));
        }
        return clonedSalesOrderLineList;
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

    private boolean isItemAvailable(String barCode) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        status = dbAdapter.isItemExistByIdentifierCode(barCode);

        dbAdapter.close();
        return status;
    }

    public void showMessageBox(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.button_text_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void getItem(String barCode) {
        mTempItem = new Item();
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        mTempItem = dbAdapter.getItemByIdentifierCodeAndPriceGroup(barCode, mTempCustomer.getCustomerPriceGroup());
        dbAdapter.close();
    }


    public void removeZeroTotalAmtItems() {
        List<SalesOrderLine> temparyRemoveSalesOrderLineList = new ArrayList<SalesOrderLine>();
        if (!mSalesOrderLineList.isEmpty()) {
            for (int i = 0; i < mSalesOrderLineList.size(); i++) {
                SalesOrderLine sol = mSalesOrderLineList.get(i);
                if (sol.getTotalAmountExclVAT().equals(new Float(0))) {
                    mRemoveSalesOrderLineList.add(sol);
                    temparyRemoveSalesOrderLineList.add(sol);
                }
            }
        }
        if (!temparyRemoveSalesOrderLineList.isEmpty()) {
            for (SalesOrderLine solR : temparyRemoveSalesOrderLineList) {
                mSalesOrderLineList.remove(solR);
            }
        }
    }

    private class SaveSalesOrderTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MvsSalesOrderActivity.this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage("Saving...");
            mProgressDialog.show();

            mPoComments = mTxtPoComments.getText().toString();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                saveSalesOrder();
            } catch (Exception e) {
                logParams(getResources().getString(R.string.message_exception), "2134", e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    mProgressDialog.dismiss();
                    if (mTaskName.equals("SoSaveMain")) {

                        Toast.makeText(mApp, getResources().getString(R.string.message_updated_successfully), Toast.LENGTH_SHORT).show();

                        if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))
                                || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))
                                || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {
                            mTvToolbarTitle.setText(mTempSalesOrder.getSINo());
                        } else {
                            mTvToolbarTitle.setText(mTempSalesOrder.getNo());
                        }

                        if (mSalesOrderLineList.size() > 0) {
                            mMvsSalesOrderAdapter = new MvsSalesOrderAdapter(mSalesOrderLineList,
                                    R.layout.item_mvs_sales_order_detail_card
                                    , MvsSalesOrderActivity.this, mTempSalesOrder.getStatus()
                                    , mTempCustomer, deliveryDate);
                            mRecyclerViewSalesOrderDetails.setAdapter(mMvsSalesOrderAdapter);
                            mMvsSalesOrderAdapter.notifyDataSetChanged();
                        }

                        applyStatus();
                        mDetails = "";
                        getFragmentManager().invalidateOptionsMenu();

                    } else if (mTaskName.equals("SoSaveBeforeConvert")) {
                        convertSo();
                    } else if (mTaskName.equals("SoSaveForDraftReport")) {

                        mTempSalesOrder.setLineItems(mSalesOrderLineList);
                        printDraftReport(mTempSalesOrder);

                    }

                } catch (Exception ex) {
//                    Log.d(getResources().getString(R.string.message_exception), ex.toString());
                    logParams(getResources().getString(R.string.message_exception), "2181", ex.getMessage());
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

    public boolean saveSalesOrder() {
        boolean status = false;
        this.isSaveSuccess = false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(getResources().getString(R.string.time_zone_gmt)));
        String todayDate = sdf.format(new Date());

        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(getApplicationContext());
        dbAdapter.open();
        SalesOrderLineDbHandler dbLineAdapter = new SalesOrderLineDbHandler(getApplicationContext());
        dbLineAdapter.open();

        if (mDetails.equals(getResources().getString(R.string.mvs_new_sales_order))) {

            if (mTempCustomer.getCode() != null) {
                mTempSalesOrder.setKey(todayDate + getResources().getString(R.string.key_so) + mTempCustomer.getCode());
                mTempSalesOrder.setNo(soNo);
                mTempSalesOrder.setOrderDate(todayDate);
                mTempSalesOrder.setDocumentDate(todayDate);
                mTempSalesOrder.setRequestedDeliveryDate(deliveryDate);
                mTempSalesOrder.setShipmentDate(deliveryDate);
                mTempSalesOrder.setExternalDocumentNo(mPoComments);
                mTempSalesOrder.setSalespersonCode(mTempCustomer.getSalespersonCode());
                mTempSalesOrder.setStatus(getResources().getString(R.string.SalesOrderStatusPending));
                mTempSalesOrder.setCreatedBy(mApp.getCurrentUserName());
                mTempSalesOrder.setCreatedDateTime(todayDate);
                mTempSalesOrder.setCreatedFrom(getResources().getString(R.string.mobile));

                float totalAmtInclVat_ = Float.parseFloat(String.format("%.2f", totalVatAmount))
                        + Float.parseFloat(String.format("%.2f", subTotal));

                float subTotal_ = Float.parseFloat(String.format("%.2f", subTotal));
                float totalVat_ = Float.parseFloat(String.format("%.2f", totalVatAmount));

                mTempSalesOrder.setAmountIncludingVAT(totalAmtInclVat_);
                mTempSalesOrder.setAmountExcludingVAT(subTotal_);
                mTempSalesOrder.setVATAmount(totalVat_);
                mTempSalesOrder.setDocumentType(0);
                mTempSalesOrder.setModule(mApp.getmCurrentModule());
                mTempSalesOrder.setDownloaded(false);

                if (isCustomerChanged) {
                    mTempSalesOrder.setSelltoCustomerNo(mTempCustomer.getCode());
                    mTempSalesOrder.setSelltoCustomerName(mTempCustomer.getName());
                    mTempSalesOrder.setSelltoAddress(mTempCustomer.getAddress());
                    mTempSalesOrder.setSelltoContactNo(mTempCustomer.getPhoneNo());
                    mTempSalesOrder.setSelltoPostCode(mTempCustomer.getPostalCode());
                    mTempSalesOrder.setDriverCode(mApp.getmCurrentDriverCode());
                    mTempSalesOrder.setSelltoCity("");
                    mTempSalesOrder.setDueDate(deliveryDate); //client request on 2017-10-03
                }
                mTempSalesOrder.setComment(mPoComments);
                updateLineNo();
                try {
                    if (dbAdapter.deleteSalesOrder(mTempSalesOrder.getNo())) {
                        dbAdapter.addSalesOrder(mTempSalesOrder);
                        //Updating new so running no
                        saveSoRunningNumber(mTempSalesOrder.getNo());
//                        Log.d(getResources().getString(R.string.message_so_added), mTempSalesOrder.getNo() == null ? "" : mTempSalesOrder.getNo());
                        logParams(getResources().getString(R.string.message_so_added), "2255", mTempSalesOrder.getNo() == null ? "" : mTempSalesOrder.getNo());

                        if (mSalesOrderLineList != null && mSalesOrderLineList.size() > 0) {
                            for (SalesOrderLine sol : mSalesOrderLineList) {
                                if (dbLineAdapter.deleteSalesOrderLine(sol.getKey())) {

                                    dbLineAdapter.addSalesOrderLine(sol);
//                                    Log.d(getResources().getString(R.string.message_so_line_added), sol.getNo() == null ? "" : sol.getNo());
                                    logParams(getResources().getString(R.string.message_so_line_added), "2263", sol.getNo() == null ? "" : sol.getNo());
                                }
                            }
                        }
                    }
                    status = true;
                    this.isSaveSuccess = true;
                    isCustomerChanged = false;
                    this.isSaved = true;
                } catch (Exception ee) {
                    Log.d(getResources().getString(R.string.message_exception), ee.getMessage().toString());
                    logParams(getResources().getString(R.string.message_exception), "2274", ee.getMessage());
                    dbAdapter.close();
                    dbLineAdapter.close();
                }
                dbAdapter.close();
                dbLineAdapter.close();
            }
        } else {
            mTempSalesOrder.setRequestedDeliveryDate(deliveryDate);
            mTempSalesOrder.setShipmentDate(deliveryDate);
            mTempSalesOrder.setExternalDocumentNo(mPoComments); //
            mTempSalesOrder.setSalespersonCode(mTempCustomer.getSalespersonCode());
            mTempSalesOrder.setLastModifiedBy(mApp.getCurrentUserName());
            mTempSalesOrder.setLastModifiedDateTime(todayDate);

            float totalAmtInclVat_ = Float.parseFloat(String.format("%.2f", totalVatAmount))
                    + Float.parseFloat(String.format("%.2f", subTotal));

            float subTotal_ = Float.parseFloat(String.format("%.2f", subTotal));
            float totalVat_ = Float.parseFloat(String.format("%.2f", totalVatAmount));

            mTempSalesOrder.setAmountIncludingVAT(totalAmtInclVat_);
            mTempSalesOrder.setAmountExcludingVAT(subTotal_);
            mTempSalesOrder.setVATAmount(totalVat_);
            mTempSalesOrder.setDocumentType(0);

            if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {
                mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusPending));
            } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
                mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusVoid));
            } else {
                mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusPending));
            }

            if (isCustomerChanged) {
                mTempSalesOrder.setSelltoCustomerNo(mTempCustomer.getCode());
                mTempSalesOrder.setSelltoCustomerName(mTempCustomer.getName());
                mTempSalesOrder.setSelltoAddress(mTempCustomer.getAddress());
                mTempSalesOrder.setSelltoContactNo(mTempCustomer.getPhoneNo());
                mTempSalesOrder.setSelltoPostCode(mTempCustomer.getPostalCode());
                mTempSalesOrder.setDriverCode(mApp.getmCurrentDriverCode());
                mTempSalesOrder.setSelltoCity("");
                //mTempSalesOrder.setDueDate("");
                mTempSalesOrder.setDueDate(deliveryDate);
            }
            mTempSalesOrder.setComment(mPoComments);
            updateLineNo();
            try {
                if (dbAdapter.deleteSalesOrder(mTempSalesOrder.getNo())) {
                    dbAdapter.addSalesOrder(mTempSalesOrder);
                    Log.d(getResources().getString(R.string.message_so_added), mTempSalesOrder.getNo() == null ? "" : mTempSalesOrder.getNo());

                    if (mRemoveSalesOrderLineList != null && mRemoveSalesOrderLineList.size() > 0) {
                        for (SalesOrderLine sol : mRemoveSalesOrderLineList) {
                            if (dbLineAdapter.deleteSalesOrderLine(sol.getKey())) {
                                Log.d(getResources().getString(R.string.message_deleted_so_line), sol.getNo() == null ? "" : sol.getNo());
                            }
                        }
                    }
                    if (mSalesOrderLineList != null && mSalesOrderLineList.size() > 0) {
                        for (SalesOrderLine sol : mSalesOrderLineList) {
                            if (dbLineAdapter.deleteSalesOrderLine(sol.getKey())) {
                                dbLineAdapter.addSalesOrderLine(sol);
                                Log.d(getResources().getString(R.string.message_so_line_added), sol.getNo() == null ? "" : sol.getNo());
                            }
                        }
                    }
                }
                status = true;
                this.isSaveSuccess = true;
                isCustomerChanged = false;
                this.isSaved = true;
            } catch (Exception e) {
                Log.d(getResources().getString(R.string.message_exception), e.getMessage().toString());
                logParams(getResources().getString(R.string.message_exception), "2348", e.getMessage());
                dbAdapter.close();
                dbLineAdapter.close();
            }
        }
        dbAdapter.close();
        dbLineAdapter.close();
        return status;
    }

    public void updateLineNo() {
        if (!mSalesOrderLineList.isEmpty()) {
            int lineNo = 1;
            for (int i = 0; i < mSalesOrderLineList.size(); i++) {
                SalesOrderLine sol = mSalesOrderLineList.get(i);
                sol.setLineNo(String.valueOf(lineNo));
                mSalesOrderLineList.set(i, sol);
                lineNo++;
            }
        }
    }

    public void saveSoRunningNumber(String newSoId) {
        UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
        dbHandler.open();
        dbHandler.setLatestMvsSoRunningNo(newSoId, mApp.getCurrentUserName());
        dbHandler.close();
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

                setDeliveryDate(year + getResources().getString(R.string.minus_symbol)
                        + (month + 1) + getResources().getString(R.string.minus_symbol) + dayOfMonth);
                validateDeliveryDate(year + getResources().getString(R.string.minus_symbol)
                        + (month + 1) + getResources().getString(R.string.minus_symbol) + dayOfMonth);

                //Refresh all item price, details according to the delivery date;
                setAllItems();
                if (!mSalesOrderLineList.isEmpty()) {
                    updateSummeryValues(mSalesOrderLineList);
                    mMvsSalesOrderAdapter.notifyDataSetChanged();
                }
            }
        }, date, month, year);

        datePickerDialog.updateDate(nowYear, nowMonth, nowDay);
        datePickerDialog.show();
    }

    public void setAllItems() {
        if (!mSalesOrderLineList.isEmpty()) {

            if (mTempCustomer != null) {
                for (SalesOrderLine sol : mSalesOrderLineList) {
                    float gstPresentage = 0f, itemUnitePrice = 0f, lineAmount = 0f, totalVatAmount = 0f, totalAmountInclVAT = 0f;

                    gstPresentage = getGstPercentage(mTempCustomer.getCode(), sol.getNo());

                    SalesPricesDbHandler spDb = new SalesPricesDbHandler(getApplicationContext());
                    spDb.open();

                    float itemUnitePrice_ = spDb.getUnitPriceWithQuantity(sol.getNo(),
                            mTempCustomer.getCustomerPriceGroup(),
                            mTempCustomer.getCode(),
                            sol.getUnitofMeasure(),
                            Math.round(sol.getQuantity()),
                            deliveryDate);

                    spDb.close();

                    //unit price
                    itemUnitePrice = floatRound(itemUnitePrice_, 2);

                    //line amount
                    float lineAmount_ = Math.round(sol.getQuantity()) * itemUnitePrice;
                    lineAmount = floatRound(lineAmount_, 2);

                    //vat amount
                    float totalVatAmount_ = (lineAmount * gstPresentage) / 100;
                    totalVatAmount = floatRound(totalVatAmount_, 2);

                    float totalAmountInclVAT_ = lineAmount + totalVatAmount;
                    totalAmountInclVAT = floatRound(totalAmountInclVAT_, 2);

                    sol.setUnitPrice(itemUnitePrice);
                    sol.setLineAmount(lineAmount);
                    sol.setTotalAmountExclVAT(lineAmount);
                    sol.setTotalVATAmount(totalVatAmount);
                    sol.setTotalAmountInclVAT(totalAmountInclVAT);
                    sol.setTaxPercentage(String.valueOf(gstPresentage));
                }
            }
        }
    }

    public void validateDeliveryDate(String date) {
        SimpleDateFormat dfDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));
        Date dateToday = new Date();

        try {
            if (dfDate.parse(date).before(dfDate.parse(dfDate.format(dateToday)))) {
                invalidDate = true;
                mTxtDeliveryDate.setError("");
                Toast.makeText(this, getResources().getString(R.string.message_invalid_date), Toast.LENGTH_SHORT).show();
            } else {
                invalidDate = false;
                mTxtDeliveryDate.setError(null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            logParams(getResources().getString(R.string.message_exception), "2473", e.getMessage());
        }
    }

    public void setDeliveryDate(String date) {
        if (!date.equals("")) {
            DateFormat df = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));
            try {

                Date initDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(date);
                deliveryDate = df.format(initDate);

                mTxtDeliveryDate.setText(mUIDateFormat.format(initDate));

            } catch (Exception e) {
                Log.e(getResources().getString(R.string.message_exception), e.getMessage().toString());
                logParams(getResources().getString(R.string.message_exception), "2489", e.getMessage());
            }
        }
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_cusInfo:
                if (mTempCustomer.getCode() == null) {
                    showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                } else {
                    String objAsJson = mTempCustomer.toJson();
                    Intent intent = new Intent(this, SalesCustomerDetailActivity.class);
                    intent.putExtra(getResources().getString(R.string.customer_detail_object), objAsJson);
                    intent.putExtra(getResources().getString(R.string.intent_extra_form_name), getResources().getString(R.string.form_name_mvs_sales_order));
                    //intent.putExtra("IsPopupNeeded", true);
                    this.startActivity(intent);
                }
                return true;

            case R.id.action_cusInvoice:
                if (mTempCustomer.getCode() == null) {
                    showMessageBox(getResources().getString(R.string.message_title_alert), getResources().getString(R.string.message_body_select_customer));
                } else {
                    Intent intentCusInvoice = new Intent(this, SalesCustomerInvoiceActivity.class);
                    intentCusInvoice.putExtra("cusNo", mTempCustomer.getCode());
                    intentCusInvoice.putExtra("cusName", mTempCustomer.getName());
                    this.startActivity(intentCusInvoice);
                }
                return true;

            case R.id.action_void:

                AlertDialog.Builder builder = new AlertDialog.Builder(MvsSalesOrderActivity.this);

                builder.setTitle(getResources().getString(R.string.message_title_alert));
                builder.setMessage("Do you want to void this sales invoice?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        voidSalesInvoice();
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

                return true;

            case R.id.action_cusPrint:
                if (!mDetails.equals(getResources().getString(R.string.mvs_new_sales_order))) {
                    if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusPending))
                            || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))
                            || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {

                        AlertDialog.Builder builder_ = new AlertDialog.Builder(MvsSalesOrderActivity.this);

                        builder_.setTitle(getResources().getString(R.string.message_title_alert));
                        builder_.setMessage("Do you want to convert this sales order to invoice?");

                        builder_.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                convertToInvoice();
                            }
                        });

                        builder_.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert_ = builder_.create();
                        alert_.show();
                    } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))
                            || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {
                        mTempSalesOrder.setLineItems(mSalesOrderLineList);

                        openReport(mTempSalesOrder);
                    }

                }

                return true;

            case R.id.action_takePhoto:
                if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))) {
                    Intent intentTakePic = new Intent(this, MsoTakePictureActivity.class);
                    intentTakePic.putExtra("soNo", mTempSalesOrder.getNo());
                    intentTakePic.putExtra("status", mTempSalesOrder.getStatus());
                    this.startActivity(intentTakePic);
                }
                return true;

            case R.id.action_draftPrint:
                if (!mDetails.equals(getResources().getString(R.string.mvs_new_sales_order))) {
                    if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusPending))
                            || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))
                            || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusComplete))) {

                        if (validateGTAndSalesQty()) {
                            mTaskName = "SoSaveForDraftReport";
                            mSaveSalesOrderTask = new SaveSalesOrderTask();
                            mSaveSalesOrderTask.execute((Void) null);
                        } else {
                            Toast.makeText(mApp
                                    , getResources().getString(R.string.message_total_sale_qty_greater_then_zero)
                                    , Toast.LENGTH_SHORT).show();
                        }

                    } else if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))
                            || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {

                        mTempSalesOrder.setLineItems(mSalesOrderLineList);
                        printDraftReport(mTempSalesOrder);

                    }
                }
                return true;
            case R.id.action_qr:
                addBarcodeReaderFragment();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void addBarcodeReaderFragment() {

        qrScanHeadModels = new ArrayList<>();

        List<SalesOrderLine> orderLines = new ArrayList<>();

        for (SalesOrderLine lineWithQty : mSalesOrderLineList) {
            if (lineWithQty.getQuantity() > 0) {
                orderLines.add(lineWithQty);
            }
        }

        int lineCountForOneQr = 20;
        int itemCount = orderLines.size();
        int barcodeCount = (itemCount / lineCountForOneQr);
        int devideCount = (orderLines.size() % lineCountForOneQr);

        if (devideCount != 0) {
            barcodeCount++;
        }

        int runningNo = 1;
        ItemDbHandler dbHandler = new ItemDbHandler(this);
        dbHandler.open();
        for (int barcodeC = 0; barcodeCount > barcodeC; barcodeC++) {
            QrScanHeadModel headModel = new QrScanHeadModel();
            headModel.a_supplierCode = mTempCustomer.getCustomerReferenceNo();
            headModel.b_invoiceNo = mTempSalesOrder.getSINo();

            //
            String dateFormatted = "";
            String[] date = mTempSalesOrder.getShipmentDate().split("T");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date newDate = null;
            try {
                newDate = format.parse(date[0]);
                format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                dateFormatted = format.format(newDate);

            } catch (ParseException e) {
                e.printStackTrace();

            }

            headModel.c_deliveryDate = dateFormatted;


            List<QrScanLineModel> lineModels = new ArrayList<>();
            for (int LineCount = runningNo; orderLines.size() >= LineCount; LineCount++) {
                SalesOrderLine line = orderLines.get(LineCount - 1);
                QrScanLineModel lineModel = new QrScanLineModel();

                lineModel.a_runningNo = String.valueOf(runningNo);
                String identifier = dbHandler.getItemIdentifierByCode(line.getNo());
                lineModel.b_itemNo = identifier;
                float totalIssueQty = (line.getQuantity() + line.getExchangedQty());
                lineModel.c_issuedQuantity = String.valueOf(totalIssueQty);
                lineModel.d_returnQuantity = String.valueOf(line.getExchangedQty());
                lineModel.e_unitPrice = String.valueOf(line.getUnitPrice());
                if (line.getUnitPrice() == 0) {
                    lineModel.f_freeGoods = "Y";
                } else {
                    lineModel.f_freeGoods = "N";
                }


                lineModels.add(lineModel);
                headModel.d_iT = lineModels;
                if (runningNo == lineCountForOneQr * (barcodeC + 1)) {
                    runningNo++;
                    break;
                }
                runningNo++;
            }
            headModel.e_totalNetQuantity = String.valueOf(mTempSalesOrder.getTotalBillQty());
            headModel.f_subTotal = String.valueOf(mTempSalesOrder.getAmountExcludingVAT());
            headModel.g_tax = String.valueOf(mTempSalesOrder.getVATAmount());
            headModel.h_grandTotal = String.valueOf(mTempSalesOrder.getAmountIncludingVAT());
            headModel.i_discount = "0.00";
            String displayName = mApp.getCurrentUserDisplayName();
            try {

                String displayNames[] = displayName.split(":");
                String name = displayNames[1].trim();
//                String names[] = name.split(" ");

                headModel.j_driverName = name;
            } catch (Exception e) {
                headModel.j_driverName = mApp.getCurrentUserDisplayName();
                logParams(getResources().getString(R.string.message_exception), "2729", e.getMessage());
            }
            int paragrpheIndicator = (barcodeC + 1);
            headModel.k_paragraphIndicator = String.valueOf(paragrpheIndicator);
            headModel.l_maxPargraph = String.valueOf(barcodeCount);
            headModel.m_storeCode = mTempCustomer.getNtucBuyerCode();
            qrScanHeadModels.add(headModel);
        }
        dbHandler.close();
        if (qrScanHeadModels.size() > 0) {
            Intent intent = new Intent(this, ScanQrActivity.class);
            startActivity(intent);
            Toast.makeText(this, "barcode created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_qr_code_to_scan), Toast.LENGTH_SHORT).show();
        }
    }

    public void openReport(SalesOrder so) {
//        addBarcodeReaderFragment();
//        GSTPostingSetupDbHandler gstDb = new GSTPostingSetupDbHandler(getApplicationContext());
//        gstDb.open();
//        String gstPresentage = gstDb.getGSTPrecentage(mTempCustomer.getVatBusPostingGroup());
        String gstPresentage = mPrefHelper.getInt(R.string.pref_gst_percentage_key, 7)+"";
        so.setVatPercentage(gstPresentage);
        String soJasonObj = so.toJson();

        String selectedPrinter = mPrefHelper.getString(R.string.pref_select_printer_key);
        Intent printInvoiceIntent;
        if (selectedPrinter.equals(getResources().getString(R.string.printer_honeywell))) {

            printInvoiceIntent = new Intent(MvsSalesOrderActivity.this, PrintInvoiceActivity.class);
        } else {
            printInvoiceIntent = new Intent(MvsSalesOrderActivity.this, PrintInvoiceOnRP4Activity.class);
        }
        printInvoiceIntent.putExtra(getResources().getString(R.string.sales_order_jason_obj), soJasonObj);

        startActivity(printInvoiceIntent);
    }

    public void printDraftReport(SalesOrder so) {
//        GSTPostingSetupDbHandler gstDb = new GSTPostingSetupDbHandler(getApplicationContext());
//        gstDb.open();
//        String gstPresentage = gstDb.getGSTPrecentage(mTempCustomer.getVatBusPostingGroup());
        String gstPresentage = mPrefHelper.getInt(R.string.pref_gst_percentage_key, 7)+"";
        so.setVatPercentage(gstPresentage);
        String soJasonObj = so.toJson();
        String selectedPrinter = mPrefHelper.getString(R.string.pref_select_printer_key);
        Intent printDraftIntent;

        if (selectedPrinter.equals(getResources().getString(R.string.printer_honeywell))) {
            printDraftIntent
                    = new Intent(MvsSalesOrderActivity.this, PrintSalesOrderDraftActivity.class);
        } else {
            printDraftIntent
                    = new Intent(MvsSalesOrderActivity.this, PrintSalesOrderDraftRP4Activity.class);
        }

        printDraftIntent.putExtra(
                getResources().getString(R.string.sales_order_jason_obj), soJasonObj);

        startActivity(printDraftIntent);
    }

    public void voidSalesInvoice() {

        SalesOrderDbHandler db = new SalesOrderDbHandler(getApplicationContext());
        db.open();
        db.updateSalesInvoiceNo(soNo, mTempSalesOrder.getSINo(), true);
        db.close();


        if (!mTempSalesOrder.getSINo().equals("")) {

            mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusVoid));

            updateItemBalencePda(mSalesOrderLineList, true);

            logParams(getResources().getString(R.string.message_void_sale_invoice), "2781", "SI_NO: "
                    + mTempSalesOrder.getSINo() + ", SO_NO: " + soNo);

            Toast.makeText(mApp, getResources().getString(R.string.message_voided_success), Toast.LENGTH_SHORT).show();
            if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))
                    || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))
                    || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {
                mTvToolbarTitle.setText(mTempSalesOrder.getSINo());
            } else {
                mTvToolbarTitle.setText(mTempSalesOrder.getNo());
            }

            if (mSalesOrderLineList.size() > 0) {
                mMvsSalesOrderAdapter = new MvsSalesOrderAdapter(mSalesOrderLineList,
                        R.layout.item_mvs_sales_order_detail_card
                        , MvsSalesOrderActivity.this, mTempSalesOrder.getStatus()
                        , mTempCustomer, deliveryDate);
                mRecyclerViewSalesOrderDetails.setAdapter(mMvsSalesOrderAdapter);
                mMvsSalesOrderAdapter.notifyDataSetChanged();
            }

            applyStatus();
            mDetails = "";
            getFragmentManager().invalidateOptionsMenu();
        }
    }

    public void convertToInvoice() {
        boolean isSoExist = false;

        SalesOrderDbHandler db = new SalesOrderDbHandler(getApplicationContext());
        db.open();
        isSoExist = db.isSoExist(soNo);
        db.close();

        if (isSoExist) {
            if (mTempCustomer.getCode() != null) {
                if (mSalesOrderLineList.size() > 0) {
                    updateSummeryValues(mSalesOrderLineList);
                    logParams(getResources().getString(R.string.message_confirm_and_print), "2820", "SO_NO: " + soNo);
                    if (validateGTAndSalesQty()) {
                        if (validateVehicleQtyOnList(mSalesOrderLineList,
                                mApp.getmCurrentDriverCode(),
                                getApplicationContext())) {

                            mTaskName = "SoSaveBeforeConvert";
                            mSaveSalesOrderTask = new SaveSalesOrderTask();
                            mSaveSalesOrderTask.execute((Void) null);
                        } else {
                            showMessageBox(getResources().getString(R.string.message_title_alert)
                                    , getResources().getString(R.string.message_so_unable_to_save_vcl_bal_qty) + '\n'
                                            + mInValidItems);
                        }
                    } else {
                        Toast.makeText(mApp, getResources().getString(R.string.message_total_sale_qty_greater_then_zero), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(mApp, getResources().getString(R.string.message_add_at_least_one_item), Toast.LENGTH_SHORT).show();
                }
            } else {
                mTxtCustomerCode.setError("");
                Toast.makeText(mApp, getResources().getString(R.string.message_body_select_customer), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mApp, "Please save sales order First", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validateGTAndSalesQty() {
        float totalSalesQty = 0f;
        if (grandTotal > 0f) {
            return true;
        } else {
            for (SalesOrderLine sol : mSalesOrderLineList) {
                float salesQyt = sol.getQuantity() + sol.getExchangedQty();
                totalSalesQty += salesQyt;
            }
            if (totalSalesQty > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean validateVehicleQtyOnList(List<SalesOrderLine> solList
            , String driverCode
            , Context context) {
        mInValidItems = "";
        boolean status = true;
        if (!solList.isEmpty()) {
            for (SalesOrderLine sol : solList) {
                ItemBalancePda itemPdaObj;
                ItemBalancePdaDbHandler ibpDb
                        = new ItemBalancePdaDbHandler(context);
                ibpDb.open();

                itemPdaObj = ibpDb.getItemBalencePda(sol.getNo()
                        , sol.getUnitofMeasure()
                        , driverCode);
                ibpDb.close();

                float vehicleAvailableQty = itemPdaObj.getOpenQty() - itemPdaObj.getQuantity();
                float salesQty = sol.getQuantity() + sol.getExchangedQty();

                if (salesQty > vehicleAvailableQty) {
                    status = false;
                    mInValidItems = mInValidItems
                            + sol.getItemCrossReferenceNo()
                            + " - " + sol.getNo()
                            + " - " + sol.getItemCrossReferenceDescription()
                            + '\n';
                }
            }
        }
        return status;
    }

    public void convertSo() {
        if (validateVehicleQtyOnList(mSalesOrderLineList,
                mApp.getmCurrentDriverCode(),
                getApplicationContext())) {
            UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
            dbHandler.open();
            String siNo = "";

            siNo = dbHandler.convertToInvoice(soNo, mTempSalesOrder.getSINo(), mApp.getCurrentUserName());
            logParams(getResources().getString(R.string.message_void_sale_invoice), "2955", "GEN SI_NO: "
                    + siNo +" mTempSINo:" + mTempSalesOrder.getSINo() + ", SO_NO: " + soNo);
            dbHandler.close();

            if (!siNo.equals("")) {
                mTempSalesOrder.setStatus(getResources().getString(R.string.MVSSalesOrderStatusConverted));
                mTempSalesOrder.setSINo(siNo);

                Toast.makeText(mApp, getResources().getString(R.string.message_converted_success), Toast.LENGTH_SHORT).show();

                if (mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConverted))
                        || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusVoid))
                        || mTempSalesOrder.getStatus().equals(getResources().getString(R.string.MVSSalesOrderStatusConfirmed))) {
                    mTvToolbarTitle.setText(mTempSalesOrder.getSINo());
                } else {
                    mTvToolbarTitle.setText(mTempSalesOrder.getNo());
                }

                if (mSalesOrderLineList.size() > 0) {
                    mMvsSalesOrderAdapter = new MvsSalesOrderAdapter(mSalesOrderLineList,
                            R.layout.item_mvs_sales_order_detail_card
                            , MvsSalesOrderActivity.this, mTempSalesOrder.getStatus()
                            , mTempCustomer, deliveryDate);
                    mRecyclerViewSalesOrderDetails.setAdapter(mMvsSalesOrderAdapter);
                    mMvsSalesOrderAdapter.notifyDataSetChanged();
                }

                applyStatus();
                mDetails = "";
                getFragmentManager().invalidateOptionsMenu();

                updateItemBalencePda(mSalesOrderLineList, false);
                mTempSalesOrder.setLineItems(mSalesOrderLineList);
                openReport(mTempSalesOrder);
            }
        } else {
            showMessageBox(getResources().getString(R.string.message_title_alert)
                    , getResources().getString(R.string.message_so_unable_to_convert_vcl_bal_qty) + '\n'
                            + mInValidItems);
        }
    }

    private void setItemTab() {
        mBtnItem.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mBtnSummary.setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
        llItem.setVisibility(View.VISIBLE);
        llSummary.setVisibility(View.GONE);
        mFabAddNewItem.setVisibility(View.VISIBLE);
    }

    private void setSummaryTab() {
        mBtnSummary.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mBtnItem.setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
        llSummary.setVisibility(View.VISIBLE);
        llItem.setVisibility(View.GONE);
        mFabAddNewItem.setVisibility(View.GONE);
    }

    private void getDestination() {
        try {
            String country = getResources().getString(R.string.Country);
            String postalCode = mTempCustomer.getPostalCode();
//            mDestination = country + "," + postalCode;
            mDestination = postalCode + "," + country;
            mAddress = mTempCustomer.getAddress() + "," + postalCode;

        } catch (Exception e) {
            logParams(getResources().getString(R.string.message_exception), "3023", e.getMessage());
        }
    }

    public void updateItemBalencePda(List<SalesOrderLine> lineList, boolean isVoid_) {
        if (lineList != null) {
            if (!lineList.isEmpty()) {
                for (SalesOrderLine sol : lineList) {
                    float qty_ = sol.getQuantity() + sol.getExchangedQty();
                    ItemBalancePdaDbHandler ibpDb
                            = new ItemBalancePdaDbHandler(getApplicationContext());
                    ibpDb.open();

                    ibpDb.updateItemBalencePdaByMvsSo(sol.getNo()
                            , sol.getUnitofMeasure()
                            , sol.getDriverCode()
                            , qty_
                            , sol.getExchangedQty()
                            , isVoid_);

                    ibpDb.close();
                }
            }
        }
    }

    public void updateItemCrossRefDetails() {
        if (mSalesOrderLineList != null) {
            if (!mSalesOrderLineList.isEmpty()) {
                ItemCrossReference itemCrossReference_ = new ItemCrossReference();
                ItemCrossReferenceDbHandler icrDb =
                        new ItemCrossReferenceDbHandler(getApplicationContext());

                icrDb.open();

                for (int s = 0; s < mSalesOrderLineList.size(); s++) {
                    SalesOrderLine sol = mSalesOrderLineList.get(s);

                    itemCrossReference_ = icrDb.getItemCrossreference(sol.getNo()
                            , sol.getUnitofMeasure(), mTempCustomer.getCode());

                    if (itemCrossReference_.getItemCrossReferenceNo() != null) {
                        sol.setItemCrossReferenceNo(itemCrossReference_.getItemCrossReferenceNo());
                    } else {
                        sol.setItemCrossReferenceNo(sol.getNo());
                    }

                    if (itemCrossReference_.getDescription() != null) {
                        sol.setItemCrossReferenceDescription(itemCrossReference_.getDescription());
                    } else {
                        sol.setItemCrossReferenceDescription(sol.getDescription());
                    }

                    mSalesOrderLineList.set(s, sol);
                }
                icrDb.close();
            }
        }
    }


    private void logParams(String type, String lineNo, String params) {
        mLog.info(type + "- " + lineNo + " -" + params);
    }
}
