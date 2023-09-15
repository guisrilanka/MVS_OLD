package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.ConfigurationManager;
import com.gui.mdt.thongsieknavclient.DataConverter;
import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.purchaseorder.PurchaseOrderLotEntryListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderItemTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderLineLotEntriesResultData;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderLineLotEntriesSearchParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseReceiveEntry;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.RemovePurchaseOrderLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialRecords;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.PurchaseOrderItemLineEntries;
import com.orm.SugarRecord;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class PurchaseOrderLotEntryActivity extends BaseActivity {

    Context mContext;
    private NavClientApp mApp;

    ListView historyList;
    PurchaseOrderLotEntryListArrayAdapter historyAdapter;
    ArrayList<PurchaseReceiveEntry> temp_list;

    GetPurchaseOrderLineLotEntriesTask mGetPurchaseOrderLineLotEntriesTask;
    PostPurchaseOrderItemEntriesTask mPostPurchaseOrderItemTask;
    RemovePurchaseOrderLineLotEntriesTask mRemovePurchaseOrderLineLotEntriesTask;
    BaseResult mReturnBaseResult;

    private boolean isScanned = false;
    private String scannedString;
    private String scannedLotNo;
    private String scannedSerialNo;

    Calendar calProductionDate;
    Calendar calExpiryDate;

    EditText textProductionDate;
    EditText textExpiryDate;
    EditText textQuantity;
    EditText textLotNumber;
    EditText scanText;
    Button btnAddEntry;
    Button btnScan;
    LinearLayout mTopLayout;

    private String mPurchaseOrderNo;
    private String mItemNo;
    private int mLineNo;
    private float mPurchaseOrderQuantityBase;
    private float mPurchaseOrderBalanceQuantityBase;

    private String mProductionDate;
    private String mExpiryDate;
    private String mQuantity;
    private String mLotNumber;

    private boolean isInterfaceScanning = false;
    LinearLayout mLinearLayoutManual;
    LinearLayout mLinearLayoutScan;

    private boolean isStarted = false;
    private boolean isEnded = false;

    private int entryIndexCounter;

    protected int getLayoutResource() {
        return R.layout.activity_purchase_order_lot_entry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order_lot_entry);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Item No.: " + DataManager.getInstance().transitionData.getItemNo());

        textProductionDate = (EditText) findViewById(R.id.editProductionDate);
        textExpiryDate = (EditText) findViewById(R.id.editExpiryDate);
        textQuantity = (EditText) findViewById(R.id.editQtyReceived);
        textLotNumber = (EditText) findViewById(R.id.editLotNumber);

        mTopLayout = (LinearLayout) findViewById(R.id.topLayout);
        mLinearLayoutManual = (LinearLayout) findViewById(R.id.linearLayoutManual);
        mLinearLayoutScan = (LinearLayout) findViewById(R.id.linearLayoutScan);

        entryIndexCounter = 0;
        scannedLotNo = "";
        scannedSerialNo = "";

        historyList = (ListView) findViewById(R.id.itemList);
        temp_list = new ArrayList<PurchaseReceiveEntry>();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String isScanning = extras.getString("IS_SCANNING");
            //The key argument here must match that used in the other activity

            mPurchaseOrderNo = DataManager.getInstance().PurchaseOrderNo;

            if (isScanning.contains("YES")) {
                isScanned = true;
                scannedString = extras.getString("SCAN_CONTENT");
            }

            if (DataManager.getInstance().transitionData.getQuantityBase() != null) {
                mPurchaseOrderQuantityBase = Float.parseFloat(DataManager.getInstance().transitionData.getQuantityBase());
            }

            if (DataManager.getInstance().transitionData.getOutstandingQuantityBase() != null) {
                mPurchaseOrderBalanceQuantityBase = Float.parseFloat(DataManager.getInstance().transitionData.getOutstandingQuantityBase());
            }

            mLineNo = Integer.parseInt(DataManager.getInstance().transitionData.getLineNo());
            mItemNo = DataManager.getInstance().transitionData.getItemNo();

            Log.d("PO ITEM DETAILS", "PO: " + mPurchaseOrderNo.toString());
            Log.d("PO ITEM DETAILS", Float.toString(mPurchaseOrderQuantityBase));
            Log.d("PO ITEM DETAILS", Float.toString(mPurchaseOrderBalanceQuantityBase));
            Log.d("PO ITEM DETAILS", Integer.toString(mLineNo));
        }

        calProductionDate = Calendar.getInstance();
        calExpiryDate = Calendar.getInstance();

        disableTextFields();
        initializeDisplayHeader();
        initializeDataEntry();
        initializeDateFields();


        btnAddEntry = (Button) findViewById(R.id.btnAddEntry);
        btnAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prepareAddEntryParameters();

                Log.d("ADD ENTRY", mProductionDate);
                Log.d("ADD ENTRY", mExpiryDate);
                Log.d("ADD ENTRY", scannedLotNo);
                Log.d("ADD ENTRY", scannedSerialNo);

                if (validateEntry()) {
                    if (mergeSameLotEntryQuantity(mLotNumber, mProductionDate, mQuantity)) {
                        DataManager.getInstance().AddSerialRecord(mPurchaseOrderNo, ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);

                        Toast.makeText(mContext, "Lot No exists! Quantity merged!", Toast.LENGTH_LONG).show();
                    } else {
                        temp_list.add(new PurchaseReceiveEntry(temp_list.size() + 1, mProductionDate, mExpiryDate, mQuantity, mLotNumber, scannedSerialNo, isScanned, entryIndexCounter, -1, ConfigurationManager.DATA_UPLOADED_FALSE));

                        PurchaseOrderItemLineEntries temp_entry = new PurchaseOrderItemLineEntries(mPurchaseOrderNo, mItemNo, mLineNo,
                                mProductionDate, mExpiryDate, mQuantity, mLotNumber, scannedSerialNo, "", isScanned, entryIndexCounter, ConfigurationManager.DATA_UPLOADED_FALSE, System.currentTimeMillis());
                        temp_entry.save();

                        DataManager.getInstance().AddSerialRecord(mPurchaseOrderNo, ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);

                        entryIndexCounter++;


                        //List<PurchaseOrderItemLineEntries> entryList = SugarRecord.listAll(PurchaseOrderItemLineEntries.class);
                        Log.d("SUGARORM", "ItemNo: " + mItemNo);
                        List<PurchaseOrderItemLineEntries> entryList = SugarRecord.find(PurchaseOrderItemLineEntries.class, "item_no = ?", mItemNo);
                        //List<PurchaseOrderItemLineEntries> entryList = SugarRecord.findWithQuery(PurchaseOrderItemLineEntries.class, "SELECT * FROM PurchaseOrderItemLineEntries WHERE ItemNo = ?", mItemNo);
                        for (int i = 0; i < entryList.size(); i++) {
                            //Toast.makeText(mContext, i + ": " + entryList.toString(), Toast.LENGTH_LONG).show();
                            Log.d("SUGARORM", i + ": " + entryList.get(i).toString());
                        }

                        List<BarcodeSerialRecords> newEntryList = DataManager.getInstance().GetSerialRecords(mPurchaseOrderNo, ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER, mItemNo, Integer.toString(mLineNo), mLotNumber);

                        for (int i = 0; i < newEntryList.size(); i++) {
                            Log.d("SUGARORM NEW", i + ": " + newEntryList.get(i).toString());
                        }
                    }

                    historyAdapter.notifyDataSetChanged();
                    UpdateDisplayQuantity();
                    clearInputFields();

                    //Toast.makeText(mContext, "Total Entries: " + entryList.size(), Toast.LENGTH_LONG).show();
                }

            }
        });

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isRequiredSave = false;
                for (int i = 0; i < temp_list.size(); i++)   // CHECK WHETHER ANY NEW ENTRIES
                {
                    if (!temp_list.get(i).IsUploaded) {
                        isRequiredSave = true;
                        break;
                    }
                }

                if (!isRequiredSave) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_INFORMATION, "No entries to save!");
                } else {
                    mPostPurchaseOrderItemTask = new PostPurchaseOrderItemEntriesTask((Activity) mContext);
                    mPostPurchaseOrderItemTask.execute((Void) null);
                }

            }
        });

        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInterfaceScanning = !isInterfaceScanning;

                if (isInterfaceScanning) {
                    clearInputFields();

                    mLinearLayoutScan.setVisibility(View.VISIBLE);
                    mLinearLayoutManual.setVisibility(View.INVISIBLE);

                    scanText.setText("");
                    scanText.requestFocus();

                    btnScan.setText("Manual");
                } else {
                    clearInputFields();

                    mLinearLayoutScan.setVisibility(View.INVISIBLE);
                    mLinearLayoutManual.setVisibility(View.VISIBLE);

                    btnScan.setText("Scan");
                }
            }
        });

        scanText = (EditText) findViewById(R.id.textScanCode);
        scanText.setInputType(InputType.TYPE_NULL);

        scanText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scanText.setBackgroundResource(R.drawable.border_red);
                } else {
                    scanText.setBackgroundResource(R.drawable.border_gray);
                }
            }
        });
        scanText.requestFocus();

        scanText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 1 && !isStarted && !isScanned) {
                    if (charSequence.toString().length() > 4 && !charSequence.toString().startsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL) && !charSequence.toString().endsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL)) {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
                        scanText.setText("");
                        scanText.requestFocus();
                    } else if (charSequence.toString().length() > 4 && charSequence.toString().startsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL) && charSequence.toString().endsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL)) {
                        isStarted = false;

                        String local_scannedString = charSequence.toString().replace(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL, "");
                        String[] splitString = local_scannedString.split("\\|");

                        if (splitString[0].contains(mItemNo)) {
                            isScanned = true;

                            scannedString = local_scannedString;
                            initializeDataEntry();
                            prepareAddEntryParameters();

                            if (validateEntry()) {
                                btnAddEntry.callOnClick();
                            } else {
                                clearInputFields();
                            }
                            scanText.setText("");
                            scanText.requestFocus();
                        } else {
                            isScanned = false;
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_PO, NotificationManager.ALERT_MSG_INVALID_PO);
                            scanText.setText("");
                            scanText.requestFocus();
                        }

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isStarted && !isScanned && editable.toString().length() == 1) {
                    isStarted = true;
                    Handler handler = new Handler();
                    handler.postDelayed(
                            new Runnable() {
                                public void run() {
                                    if (isStarted)       // still havent ended after timeout
                                    {
                                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
                                        scanText.setText("");
                                        scanText.requestFocus();
                                        isStarted = false;
                                    }
                                }
                            }, ConfigurationManager.BARCODE_CHECKING_TIMEOUT_VALUE);
                }

                if (isStarted && editable.toString().length() > 4 && editable.toString().startsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL) && editable.toString().endsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL)) {
                    isStarted = false;

                    String local_scannedString = editable.toString().replace(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL, "");
                    String[] splitString = local_scannedString.split("\\|");

                    if (splitString[0].contains(mItemNo)) {
                        isScanned = true;

                        scannedString = local_scannedString;
                        initializeDataEntry();
                        prepareAddEntryParameters();

                        if (validateEntry()) {
                            btnAddEntry.callOnClick();
                        } else {
                            clearInputFields();
                        }
                        editable.clear();
                        scanText.requestFocus();
                    } else {
                        isScanned = false;
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_PO, NotificationManager.ALERT_MSG_INVALID_PO);
                        editable.clear();
                        scanText.requestFocus();
                    }
                }
            }
        });

        mGetPurchaseOrderLineLotEntriesTask = new GetPurchaseOrderLineLotEntriesTask(this);
        mGetPurchaseOrderLineLotEntriesTask.execute((Void) null);
    }

    private void prepareAddEntryParameters() {
        mQuantity = textQuantity.getText().toString();

        if (DataManager.getInstance().transitionData.getIsItemProductionDateRequired()) {
            mProductionDate = textProductionDate.getText().toString();
        } else {
            mProductionDate = textProductionDate.getText().toString();
            //mProductionDate = "";
        }

        if (DataManager.getInstance().transitionData.getIsItemExpireDateRequired()) {
            mExpiryDate = textExpiryDate.getText().toString();
        } else {
            mExpiryDate = textExpiryDate.getText().toString();
            //mExpiryDate = "";
        }

        mLotNumber = textLotNumber.getText().toString().toUpperCase();

    }

    private boolean mergeSameLotEntryQuantity(String lotNo, String prodDate, String quantity) {
        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(lotNo) && temp_list.get(i).ProductionDate.toString().equals(prodDate)) {
                if (!temp_list.get(i).IsUploaded) {
                    temp_list.get(i).QtyToReceive = Float.toString(Float.parseFloat(temp_list.get(i).QtyToReceive) + Float.parseFloat(quantity));

                    List<PurchaseOrderItemLineEntries> entryList = SugarRecord.find(PurchaseOrderItemLineEntries.class, "lot_number = ? and production_date = ?", lotNo, prodDate);

                    if (entryList.size() > 0) {
                        entryList.get(0).setQtyToReceive(temp_list.get(i).QtyToReceive);
                        entryList.get(0).save();
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private boolean validateEntry() {
        if (!checkSerialExist()) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_SERIALNO, NotificationManager.ALERT_MSG_INVALID_SERIALNO);
            return false;
        }
        if (!checkRequiredFields()) {
            return false;
        }
        if (!checkQuantity(mQuantity)) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_EXCEEDS_QUANTITY, NotificationManager.ALERT_MSG_EXCEEDS_QUANTITY);
            return false;
        }

        return true;
    }

    private boolean checkRequiredFields() {
        /*if(DataManager.getInstance().transitionData.getIsItemProductionDateRequired())
        {
            if(mProductionDate.length() == 0 || mProductionDate.length() < 10)
            {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_PRODUCTION_DATE, NotificationManager.ALERT_MSG_INVALID_PRODUCTION_DATE);
                return false;
            }
        }*/
        if (DataManager.getInstance().transitionData.getIsItemExpireDateRequired()) {
            if (mExpiryDate.length() == 0 || mExpiryDate.length() < 10) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_EXPIRY_DATE, NotificationManager.ALERT_MSG_INVALID_EXPIRY_DATE);
                return false;
            }
        }
        if (mQuantity.length() == 0 || mQuantity.equals("0") || mQuantity.equals("0.0")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
            return false;
        }
        if (DataManager.getInstance().transitionData.getIsItemLotTrackingRequired()) {
            if (!DataManager.getInstance().transitionData.getIsItemLotNoAutoAssigned())      // if lot is not auto-assign, does not allow empty
            {
                if (mLotNumber.length() == 0) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOT_NO, NotificationManager.ALERT_MSG_INVALID_LOT_NO);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkQuantity(String scannedQuantity) {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            total += Float.parseFloat(temp_list.get(i).QtyToReceive);
        }

        total += Float.parseFloat(scannedQuantity);

        if (total > mPurchaseOrderBalanceQuantityBase) {
            Log.d("VALIDATION", Float.toString(mPurchaseOrderQuantityBase));
            Log.d("VALIDATION", Float.toString(total));

            return false;
        } else {
            return true;
        }
    }

    private boolean checkSerialExist() {
        if (scannedSerialNo.isEmpty()) {
            return true;
        }

        List<BarcodeSerialRecords> tempSerialList = DataManager.getInstance().GetSerialRecords(mPurchaseOrderNo, ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER, mItemNo, Integer.toString(mLineNo), mLotNumber);
        boolean serialExist = false;

        for (int i = 0; i < tempSerialList.size(); i++) {
            if (tempSerialList.get(i).getBarcodeSerialNo().length() > 0) {
                if (tempSerialList.get(i).getBarcodeSerialNo().contains(scannedSerialNo)) {
                    serialExist = true;
                    break;
                }
            }
        }

        if (serialExist) {
            return false;
        } else {
            return true;
        }
    }

    private void clearInputFields() {
        textProductionDate.setText("");
        textExpiryDate.setText("");
        textQuantity.setText("");
        textLotNumber.setText("");

        scannedString = "";
        scannedLotNo = "";
        scannedSerialNo = "";

        isScanned = false;
    }

    /*private void enableTextFieldsEditing(boolean isEnable)
    {
        if(isEnable)
        {
            textExpiryDate.setEnabled(true);
            textProductionDate.setEnabled(true);
            textLotNumber.setEnabled(true);
            textQuantity.setEnabled(true);
        }
        else
        {
            textExpiryDate.setEnabled(false);
            textProductionDate.setEnabled(false);
            textLotNumber.setEnabled(false);
            textQuantity.setEnabled(false);
        }

    }*/

    private void disableTextFields() {
        textExpiryDate.setEnabled(true);
        textProductionDate.setEnabled(true);
        textLotNumber.setEnabled(true);


        Log.d("DISABLE", String.valueOf(DataManager.getInstance().transitionData.getIsItemExpireDateRequired()));
        Log.d("DISABLE", String.valueOf(DataManager.getInstance().transitionData.getIsItemProductionDateRequired()));
        Log.d("DISABLE", String.valueOf(DataManager.getInstance().transitionData.getIsItemLotTrackingRequired()));
        Log.d("DISABLE", String.valueOf(DataManager.getInstance().transitionData.getIsItemLotNoAutoAssigned()));

        /*if(!DataManager.getInstance().transitionData.getIsItemExpireDateRequired())
        {
            textExpiryDate.setBackgroundColor(Color.GRAY);
            textExpiryDate.setEnabled(false);
        }
        if(!DataManager.getInstance().transitionData.getIsItemProductionDateRequired())
        {
            textProductionDate.setBackgroundColor(Color.GRAY);
            textProductionDate.setEnabled(false);
        }

        if(!DataManager.getInstance().transitionData.getIsItemLotTrackingRequired())
        {
            textLotNumber.setBackgroundColor(Color.GRAY);
            textLotNumber.setEnabled(false);
        }*/

        if (DataManager.getInstance().transitionData.getIsItemLotNoAutoAssigned())   // if it is auto-assign, still allow to type in lot number
        {
            textLotNumber.setBackgroundColor(Color.WHITE);
            textLotNumber.setEnabled(true);
        }
    }

    private void initializeDisplayHeader() {
        TextView tempTextLabel = (TextView) findViewById(R.id.lblItemDescription);
        tempTextLabel.setText(DataManager.getInstance().transitionData.getItemDescription());

        tempTextLabel = (TextView) findViewById(R.id.lblItemIDUom);
        tempTextLabel.setText("Item No: " + DataManager.getInstance().transitionData.getItemNo() + DataConverter.CheckNullString(DataManager.getInstance().transitionData.getBaseUom(), " / "));

        tempTextLabel = (TextView) findViewById(R.id.lblPurchaseQuantity);
        tempTextLabel.setText("Ord Qty: " + DataManager.getInstance().transitionData.getQuantityBase());

        tempTextLabel = (TextView) findViewById(R.id.lblBalanceQuantity);
        tempTextLabel.setText("Bal Qty: " + DataManager.getInstance().transitionData.getOutstandingQuantityBase());

        tempTextLabel = (TextView) findViewById(R.id.lblReceivedQuantity);
        tempTextLabel.setText("Qty to Rec: " + DataManager.getInstance().transitionData.getQuantityToReceiveBase());

    }

    private void initializeDateFields() {
        textProductionDate = (EditText) findViewById(R.id.editProductionDate);
        textProductionDate.setInputType(InputType.TYPE_NULL);

        final DatePickerDialog.OnDateSetListener dateOfProductionPicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calProductionDate.set(Calendar.YEAR, year);
                calProductionDate.set(Calendar.MONTH, monthOfYear);
                calProductionDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
                textProductionDate.setText(simpleDateFormat.format(calProductionDate.getTime()));
            }
        };

        textProductionDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    new DatePickerDialog(mContext, dateOfProductionPicker, calProductionDate.get(Calendar.YEAR), calProductionDate.get(Calendar.MONTH), calProductionDate.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        textProductionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(mContext, dateOfProductionPicker, calProductionDate.get(Calendar.YEAR), calProductionDate.get(Calendar.MONTH), calProductionDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        textExpiryDate = (EditText) findViewById(R.id.editExpiryDate);
        textExpiryDate.setInputType(InputType.TYPE_NULL);

        final DatePickerDialog.OnDateSetListener dateOfExpirationPicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calExpiryDate.set(Calendar.YEAR, year);
                calExpiryDate.set(Calendar.MONTH, monthOfYear);
                calExpiryDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
                textExpiryDate.setText(simpleDateFormat.format(calExpiryDate.getTime()));
            }
        };

        textExpiryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    new DatePickerDialog(mContext, dateOfExpirationPicker, calExpiryDate.get(Calendar.YEAR), calExpiryDate.get(Calendar.MONTH), calExpiryDate.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        textExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(mContext, dateOfExpirationPicker, calExpiryDate.get(Calendar.YEAR), calExpiryDate.get(Calendar.MONTH), calExpiryDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        textQuantity.requestFocus();
    }

    //For only have production date and expiry date
    private void initializeDataEntry() {
        if (isScanned) {
            String[] splitString = scannedString.split("\\|");

            if (!splitString[4].isEmpty()) {
                if (DataManager.getInstance().transitionData.getIsItemExpireDateRequired() && !splitString[4].isEmpty()) {
                    calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                    textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));
                }
                calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));
            }

            /*if(!splitString[3].isEmpty()) {
                if (DataManager.getInstance().transitionData.getIsItemProductionDateRequired() && !splitString[3].isEmpty()) {
                    calProductionDate.set(Integer.parseInt(splitString[3].substring(0, 4)), Integer.parseInt(splitString[3].substring(4, 6)), Integer.parseInt(splitString[3].substring(6, 8)));

                    textProductionDate.setText(splitString[3].substring(6, 8) + "/" + splitString[3].substring(4, 6) + "/" + splitString[3].substring(0, 4));
                }
                calProductionDate.set(Integer.parseInt(splitString[3].substring(0, 4)), Integer.parseInt(splitString[3].substring(4, 6)), Integer.parseInt(splitString[3].substring(6, 8)));

                textProductionDate.setText(splitString[3].substring(6, 8) + "/" + splitString[3].substring(4, 6) + "/" + splitString[3].substring(0, 4));
            }*/
            if (DataManager.getInstance().transitionData.getIsItemLotTrackingRequired()) {
                textLotNumber.setText(splitString[3]);
            }

            textQuantity.setText(splitString[1]);

            scannedLotNo = splitString[3];
            if (scannedLotNo != null) {
                textLotNumber.setText(scannedLotNo);
            }
            scannedSerialNo = splitString[5];
        }

        Log.d("PO ITEM DETAILS", calProductionDate.toString());
        Log.d("PO ITEM DETAILS", calExpiryDate.toString());
    }

    public class PostPurchaseOrderItemEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        List<PurchaseOrderItemTrackingLineParameter> mPurchaseOrderItemTrackingLineParameter;

        PostPurchaseOrderItemEntriesTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mPurchaseOrderItemTrackingLineParameter = new ArrayList<PurchaseOrderItemTrackingLineParameter>();

            for (int i = 0; i < temp_list.size(); i++) {
                if (!temp_list.get(i).IsUploaded) {
                    String production_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ProductionDate);
                    String expiry_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ExpiryDate);

                    PurchaseOrderItemTrackingLineParameter temp_PurchaseOrderItemTrackingLineParameter = new PurchaseOrderItemTrackingLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mPurchaseOrderNo, mLineNo, production_date, expiry_date, temp_list.get(i).LotNo, "", Float.parseFloat(temp_list.get(i).QtyToReceive), temp_list.get(i).IsScanned);
                    mPurchaseOrderItemTrackingLineParameter.add(temp_PurchaseOrderItemTrackingLineParameter);
                }
            }

            Gson gson = new Gson();
            String json = gson.toJson(mPurchaseOrderItemTrackingLineParameter);
            Log.d("JSON", json);

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().PostPurchaseOrderItemTrackingLine(mPurchaseOrderItemTrackingLineParameter);

                mReturnBaseResult = call.execute().body();

            } catch (IOException e) {
                Log.d("NAV_Client_Exception", e.toString());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            NotificationManager.HideProgressDialog();

            if (success) {
                try {
                    Log.d("RETURN STATUS", String.valueOf(mReturnBaseResult.getStatus()));

                    if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_SUCCESSFUL, "Data has been saved!");

                        List<PurchaseOrderItemLineEntries> dataList = SugarRecord.find(PurchaseOrderItemLineEntries.class, "line_no = ?", String.valueOf(mLineNo));
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).delete();
                        }

                        // REMOVE BARCODE SERIAL LIST
                        DataManager.getInstance().DeleteSerialRecord(mPurchaseOrderNo, ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER, mItemNo, Integer.toString(mLineNo));

                        Intent intent = new Intent(getBaseContext(), PurchaseOrderLineActivity.class);
                        intent.putExtra("PURCHASE_ORDER_NO", mPurchaseOrderNo);
                        startActivity(intent);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_FAILED, mReturnBaseResult.getMessage());
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_FAILED, getResources().getString(R.string.notification_msg_server_no_response));
            }

        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class GetPurchaseOrderLineLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PurchaseOrderLineLotEntriesSearchParameter mPurchaseOrderLineLotEntriesSearchParameter;
        PurchaseOrderLineLotEntriesResult mPurchaseOrderLineLotEntriesResult;

        GetPurchaseOrderLineLotEntriesTask(Activity activity) {

            mActivity = activity;

            mPurchaseOrderLineLotEntriesSearchParameter = new PurchaseOrderLineLotEntriesSearchParameter(mApp.getmUserCompany(), mPurchaseOrderNo, mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mLineNo);

            Gson gson = new Gson();
            String json = gson.toJson(mPurchaseOrderLineLotEntriesSearchParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PurchaseOrderLineLotEntriesResult> call = mApp.getNavBrokerService().GetPurchaseOrderLineLotEntries(mPurchaseOrderLineLotEntriesSearchParameter);

                mPurchaseOrderLineLotEntriesResult = call.execute().body();

            } catch (IOException e) {
                Log.d("NAV_Client_Exception", e.toString());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            NotificationManager.HideProgressDialog();

            if (success) {
                try {
                    if (mPurchaseOrderLineLotEntriesResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        if (mPurchaseOrderLineLotEntriesResult != null) {
                            if (mPurchaseOrderLineLotEntriesResult.getPurchaseOrderLineLotEntriesResultData() != null
                                    && mPurchaseOrderLineLotEntriesResult.getPurchaseOrderLineLotEntriesResultData().size() != 0) {
                                for (int i = 0; i < mPurchaseOrderLineLotEntriesResult.getPurchaseOrderLineLotEntriesResultData().size(); i++) {
                                    PurchaseOrderLineLotEntriesResultData temp_result = mPurchaseOrderLineLotEntriesResult.getPurchaseOrderLineLotEntriesResultData().get(i);
                                    String temp_prod_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_result.getProductionDate());
                                    String temp_expire_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_result.getExpireDate());
                                    PurchaseReceiveEntry temp_entry = new PurchaseReceiveEntry(temp_list.size() + 1, temp_prod_date, temp_expire_date, Float.toString(temp_result.getReceivedQuantity()), temp_result.getLotNo().toUpperCase(), "", false, ConfigurationManager.DATA_NO_LOCAL_ENTRY_INDEX, temp_result.getEntryNo(), ConfigurationManager.DATA_UPLOADED_TRUE);
                                    temp_list.add(temp_entry);

                                }
                            }
                        }

                        //check whether there are entries in the local database and append them to the list
                        List<PurchaseOrderItemLineEntries> entryList = SugarRecord.find(PurchaseOrderItemLineEntries.class, "line_no = ?", Integer.toString(mLineNo));

                        for (int i = 0; i < entryList.size(); i++) {
                            PurchaseReceiveEntry temp_entry = new PurchaseReceiveEntry(temp_list.size() + 1, entryList.get(i).getProductionDate(), entryList.get(i).getExpiryDate(), entryList.get(i).getQtyToReceive(), entryList.get(i).getLotNumber(), entryList.get(i).getBarcodeSerialNo(), false, entryList.get(i).getEntryIndexNo(), ConfigurationManager.DATA_NO_SERVER_ENTRY_INDEX, ConfigurationManager.DATA_UPLOADED_FALSE);
                            temp_list.add(temp_entry);
                        }

                        historyAdapter = new PurchaseOrderLotEntryListArrayAdapter(mContext, temp_list);

                        historyList.setAdapter(historyAdapter);

                        UpdateDisplayQuantity();
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mReturnBaseResult.getMessage());
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }

            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, getResources().getString(R.string.notification_msg_server_no_response));
            }


            if (isScanned) {
                prepareAddEntryParameters();

                if (validateEntry()) {
                    btnAddEntry.callOnClick();
                } else {
                    clearInputFields();
                }
                btnScan.callOnClick();
            }

            NotificationManager.HideProgressDialog();
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class RemovePurchaseOrderLineLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        RemovePurchaseOrderLotEntryParameter mRemovePurchaseOrderLotEntryParameter;
        BaseResult mReturnBaseResult;

        int mPositionDelete;

        RemovePurchaseOrderLineLotEntriesTask(Activity activity, String PurchaseOrderNo, int LineNo, int EntryNo, int position, String lotSerialNo, float quantityToDeduct) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_DELETE_DATA, NotificationManager.DIALOG_MSG_DELETE);

            mActivity = activity;

            int SerialOrLotFlag = 0;
            if (DataManager.getInstance().transitionData.getIsItemLotTrackingRequired()) {
                SerialOrLotFlag = 1;
            }

            mRemovePurchaseOrderLotEntryParameter = new RemovePurchaseOrderLotEntryParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), PurchaseOrderNo, LineNo, SerialOrLotFlag, lotSerialNo, quantityToDeduct);

            mPositionDelete = position;

            Gson gson = new Gson();
            String json = gson.toJson(mRemovePurchaseOrderLotEntryParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().RemovePurchaseOrderLotEntry(mRemovePurchaseOrderLotEntryParameter);

                mReturnBaseResult = call.execute().body();

            } catch (IOException e) {
                Log.d("NAV_Client_Exception", e.toString());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            NotificationManager.HideProgressDialog();

            if (success) {
                try {
                    if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        temp_list.remove(mPositionDelete);
                        historyAdapter.notifyDataSetChanged();

                        UpdateDisplayQuantity();

                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_REMOVE_SUCCESSFUL, NotificationManager.MSG_REMOVE_MESSAGE);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_REMOVE_FAILED, mReturnBaseResult.getMessage());
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, getResources().getString(R.string.notification_msg_server_no_response));
            }

        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public void RemoveEntry(int position, int entryNo, String lotSerialNo, float quantityToDeduct) {
        //Log.d("SERVER REMOVE", "PO: " + mPurchaseOrderNo + " LineNo: " + mLineNo + " position: " + position + " EntryNO: " + entryNo);
        mRemovePurchaseOrderLineLotEntriesTask = new RemovePurchaseOrderLineLotEntriesTask((Activity) mContext, mPurchaseOrderNo, mLineNo, entryNo, position, lotSerialNo, quantityToDeduct);
        mRemovePurchaseOrderLineLotEntriesTask.execute((Void) null);
    }

    protected void handleHomeAsUpEvent() {
        //same as back pressed event
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (temp_list.size() > 0) {
            boolean hasNotUploaded = false;
            for (int i = 0; i < temp_list.size(); i++) {
                Log.d("BACK", temp_list.get(i).toString());
                if (!temp_list.get(i).IsUploaded) {
                    hasNotUploaded = true;
                    break;
                }
            }

            if (hasNotUploaded) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_REQUEST, NotificationManager.DIALOG_MSG_SAVE_REQUEST);
            } else {
                Intent intent = new Intent(getBaseContext(), PurchaseOrderLineActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("PURCHASE_ORDER_NO", mPurchaseOrderNo);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getBaseContext(), PurchaseOrderLineActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("PURCHASE_ORDER_NO", mPurchaseOrderNo);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sales_order_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_clearEntry:
                clearAllEntries();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clearAllEntries() {
        List<PurchaseOrderItemLineEntries> entryList = SugarRecord.find(PurchaseOrderItemLineEntries.class, "line_no = ?", Integer.toString(mLineNo));
        if (entryList.size() > 0) {
            for (int i = 0; i < entryList.size(); i++) {
                entryList.get(i).delete();
            }
        }

        // REMOVE BARCODE SERIAL LIST
        DataManager.getInstance().DeleteSerialRecord(mPurchaseOrderNo, ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER, mItemNo, Integer.toString(mLineNo));

        for (int i = temp_list.size() - 1; i >= 0; i--) {
            if (!temp_list.get(i).IsUploaded) {
                temp_list.remove(i);
            }
        }

        historyAdapter.notifyDataSetChanged();
        UpdateDisplayQuantity();
    }

    // CALLED BY ADAPTER TO REMOVE LOCAL ENTRY FROM SERIAL LIST
    public void RemoveSerialEntry(String mLotNo, String mProductionDate) {
        DataManager.getInstance().DeleteSerialRecord(mPurchaseOrderNo, ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER, mItemNo, Integer.toString(mLineNo), mLotNo);

        UpdateDisplayQuantity();
    }

    public void UpdateDisplayQuantity() {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            total += Float.parseFloat(temp_list.get(i).QtyToReceive);
        }

        TextView tempTextLabel = (TextView) findViewById(R.id.lblReceivedQuantity);
        tempTextLabel.setText("Qty to Rec: " + Float.toString(total));
    }
}
