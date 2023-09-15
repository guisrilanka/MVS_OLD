package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.gui.mdt.thongsieknavclient.adapters.LotNumberListArrayAdapter;
import com.gui.mdt.thongsieknavclient.adapters.productionpastegroup.ProductionPasteGroupBomLotEntryListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.AvailableLotNoResult;
import com.gui.mdt.thongsieknavclient.model.AvailableLotNoResultData;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderAvailableLotNoParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderEntry;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderItemTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderLineLotEntriesResultData;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderLineLotEntriesSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.RemoveProductionOrderLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialRecords;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.ProductionOrderBOMItemLineEntries;
import com.orm.SugarRecord;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class ProductionPasteGroupBOMLotEntryActivity extends BaseActivity {

    GetProductionOrderLineLotEntriesTask mGetProductionOrderLineLotEntriesTask = null;
    PostProductionOrderItemEntriesTask mPostProductionOrderItemEntriesTask = null;
    RemoveProductionOrderLineLotEntriesTask mRemoveProductionOrderPasteGroupLineLotEntriesTask = null;
    QueryProductionOrderAvailableLotNoParameter mQueryProductionOrderAvailableLotNoParameter = null;

    Context mContext;
    private NavClientApp mApp;

    AvailableLotNoResult mAvailableLotNoResult;

    ListView historyList;
    ProductionPasteGroupBomLotEntryListArrayAdapter historyAdapter;
    ArrayList<ProductionOrderEntry> temp_list;

    ListView lotNumberList;
    LotNumberListArrayAdapter lotNumberArrayAdapter;

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

    private String mProductionOrderNo;
    private String mItemNo;
    private int mLineNo;
    private int mProductionOrderLineNo;
    private float mProductionExpectedQuantityBase;
    private float mProductionRemainingQuantityBase;
    private float mProductionConsumeQuantityBase;

    private String mProductionDate;
    private String mExpiryDate;
    private String mQuantity;
    private String mLotNumber;

    private boolean isInterfaceScanning = false;
    LinearLayout mLinearLayoutManual;
    LinearLayout mLinearLayoutScan;

    private boolean isStarted = false;

    private int entryIndexCounter;

    protected int getLayoutResource() {
        return R.layout.activity_production_paste_group_bom_lot_entry;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_paste_group_bom_lot_entry);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Item No.: " + DataManager.getInstance().productionOrderBomListResultData.getItemNo());

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
        temp_list = new ArrayList<ProductionOrderEntry>();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String isScanning = extras.getString("IS_SCANNING");
            //The key argument here must match that used in the other activity

            mProductionOrderNo = DataManager.getInstance().ProductionOrderNo;

            if (isScanning.contains("YES")) {
                isScanned = true;
                scannedString = extras.getString("SCAN_CONTENT");
            }

            if (DataManager.getInstance().productionOrderBomListResultData.getExpectedQuantityBase() != null) {
                mProductionExpectedQuantityBase = Float.parseFloat(DataManager.getInstance().productionOrderBomListResultData.getExpectedQuantityBase());
            }

            if (DataManager.getInstance().productionOrderBomListResultData.getRemainingQuantityBase() != null) {
                mProductionRemainingQuantityBase = Float.parseFloat(DataManager.getInstance().productionOrderBomListResultData.getRemainingQuantityBase());
            }

            if (DataManager.getInstance().productionOrderBomListResultData.getQuantityToConsumedBase() != null) {
                mProductionConsumeQuantityBase = Float.parseFloat(DataManager.getInstance().productionOrderBomListResultData.getQuantityToConsumedBase());
            }

            mLineNo = Integer.parseInt(DataManager.getInstance().productionOrderBomListResultData.getLineNo());
            mProductionOrderLineNo = Integer.parseInt(DataManager.getInstance().pasteGroupProductionOrderListResultData.getLineNo());
            mItemNo = DataManager.getInstance().productionOrderBomListResultData.getItemNo();

            Log.d("PO ITEM DETAILS", "PO: " + mProductionOrderNo.toString());
            Log.d("PO ITEM DETAILS", Float.toString(mProductionExpectedQuantityBase));
            Log.d("PO ITEM DETAILS", Float.toString(mProductionRemainingQuantityBase));
            Log.d("PO ITEM DETAILS", Integer.toString(mLineNo));
        }

        calProductionDate = Calendar.getInstance();
        calExpiryDate = Calendar.getInstance();

        enableTextFieldsEditing(true);
        disableTextFields();
        initializeDisplayHeader();
        initializeDataEntry();      // for entries that are scanned from Layer 2
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

                    if (mergeSameLotEntryQuantity(mLotNumber, mQuantity, mProductionDate)) {
                        DataManager.getInstance().AddSerialRecord(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GROUP, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);
                        Toast.makeText(mContext, "Lot No exists! Quantity merged!", Toast.LENGTH_LONG).show();
                    } else {

                        if (mQuantity.length() == 0 || mQuantity.equals("0.0")) {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                        } else {

                            temp_list.add(new ProductionOrderEntry(temp_list.size() + 1, mProductionDate, mExpiryDate, mQuantity, mLotNumber, scannedSerialNo, entryIndexCounter, ConfigurationManager.DATA_NO_SERVER_ENTRY_INDEX, ConfigurationManager.DATA_UPLOADED_FALSE));

                            ProductionOrderBOMItemLineEntries temp_entry = new ProductionOrderBOMItemLineEntries(mProductionOrderNo, mItemNo, mLineNo,
                                    mProductionDate, mExpiryDate, mQuantity, mLotNumber, scannedSerialNo, "", entryIndexCounter, ConfigurationManager.DATA_UPLOADED_FALSE, System.currentTimeMillis());
                            temp_entry.save();

                            DataManager.getInstance().AddSerialRecord(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GROUP, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);


                            entryIndexCounter++;


                            Log.d("SUGARORM", "ItemNo: " + mItemNo);
                            List<ProductionOrderBOMItemLineEntries> entryList = SugarRecord.find(ProductionOrderBOMItemLineEntries.class, "item_no = ?", mItemNo);

                            for (int i = 0; i < entryList.size(); i++) {
                                Log.d("SUGARORM", i + ": " + entryList.get(i).toString());
                            }
                        }
                    }

                    historyAdapter.notifyDataSetChanged();
                    UpdateDisplayQuantity();
                    clearInputFields();
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
                    mPostProductionOrderItemEntriesTask = new PostProductionOrderItemEntriesTask((Activity) mContext);
                    mPostProductionOrderItemEntriesTask.execute((Void) null);
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
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
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
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                        editable.clear();
                        scanText.requestFocus();
                    }
                }
            }
        });

        Button btnShowLotDialog = (Button) findViewById(R.id.btnShowLotDialog);
        btnShowLotDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

                // Setting dialogview
                Window window = dialog.getWindow();
                window.setGravity(Gravity.CENTER);
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                window.setDimAmount(0.2f);

                dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                dialog.setContentView(R.layout.dialog_list_selection);
                dialog.setCancelable(false);

                EditText editFilterLotNumber = (EditText) dialog.findViewById(R.id.editFilterLotNumber);
                editFilterLotNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        lotNumberArrayAdapter.getFilter().filter(charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                ArrayList<AvailableLotNoResultData> temp_item_list = new ArrayList<AvailableLotNoResultData>(mAvailableLotNoResult.AvailableLotNoData);
                lotNumberArrayAdapter = new LotNumberListArrayAdapter(mContext, temp_item_list);

                lotNumberList = (ListView) dialog.findViewById(R.id.itemList);
                lotNumberList.setAdapter(lotNumberArrayAdapter);

                lotNumberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        isScanned = false;

                        AvailableLotNoResultData temp = lotNumberArrayAdapter.getItem(i);
                        textExpiryDate.setText(DataConverter.ConvertJsonDateToDayMonthYear(temp.getExpirationDate()));
                        textProductionDate.setText(DataConverter.ConvertJsonDateToDayMonthYear(temp.getProductionDate()));
                        textLotNumber.setText(temp.getLotNo());

                        Float currBalance = computeCurrentBalance();
                        if ((currBalance > Float.parseFloat(temp.getAvailableQuantity()))) {
                            textQuantity.setText(temp.getAvailableQuantity());
                        } else {
                            textQuantity.setText(Float.toString(currBalance));
                        }

                        dialog.dismiss();
                    }
                });

                Button btnClearText = (Button) dialog.findViewById(R.id.btnClearText);

                btnClearText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editFilterLotNumber = (EditText) dialog.findViewById(R.id.editFilterLotNumber);
                        editFilterLotNumber.setText("");
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
        });

        mGetProductionOrderLineLotEntriesTask = new GetProductionOrderLineLotEntriesTask(this);
        mGetProductionOrderLineLotEntriesTask.execute((Void) null);
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

    private void enableTextFieldsEditing(boolean isEnable) {
        if (isEnable) {
            textExpiryDate.setEnabled(true);
            textProductionDate.setEnabled(true);
            //textLotNumber.setEnabled(true);
            textQuantity.setEnabled(true);
        } else {
            textExpiryDate.setEnabled(false);
            textProductionDate.setEnabled(false);
            //textLotNumber.setEnabled(false);
            textQuantity.setEnabled(false);
        }

    }

    private void disableTextFields() {
        textExpiryDate.setEnabled(true);
        textProductionDate.setEnabled(true);
        textLotNumber.setEnabled(true);
        /*if (!DataManager.getInstance().productionOrderBomListResultData.isItemExpireDateRequired()) {
            textExpiryDate.setBackgroundColor(Color.GRAY);
        }*/

        /*if (!DataManager.getInstance().productionOrderBomListResultData.isItemLotTrackingRequired()) {
            textLotNumber.setBackgroundColor(Color.GRAY);
            //textLotNumber.setEnabled(false);  // For production, lot number will always be disabled. Can only select from list or scan
        }*/


        //textQuantity.setEnabled(DataManager.getInstance().productionOrderBomListResultData.isAllowChangeQty());

    }

    private float computeCurrentBalance() {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            if (!temp_list.get(i).IsUploaded)    // Those not uploaded are those that should be deducted from balance
            {
                total += Float.parseFloat(temp_list.get(i).QtyToConsume);
            }
        }

        return mProductionExpectedQuantityBase - total - mProductionConsumeQuantityBase;

    }

    private void prepareAddEntryParameters() {
        mQuantity = textQuantity.getText().toString();

        if (DataManager.getInstance().productionOrderBomListResultData.isItemProductionDateRequired()) {
            mProductionDate = textProductionDate.getText().toString();
        } else {
            mProductionDate = textProductionDate.getText().toString();
            //mProductionDate = "";
        }

        if (DataManager.getInstance().productionOrderBomListResultData.isItemExpireDateRequired()) {
            mExpiryDate = textExpiryDate.getText().toString();
        } else {
            mExpiryDate = textExpiryDate.getText().toString();
            //mExpiryDate = "";
        }

        mLotNumber = textLotNumber.getText().toString().toUpperCase();
        /*
        if(DataManager.getInstance().productionOrderBomListResultData.isItemLotTrackingRequired() && !DataManager.getInstance().productionOrderBomListResultData.isItemLotNoAutoAssigned())
        {

        }
        else
        {
            mLotNumber = "";
        }*/
    }

    private boolean mergeSameLotEntryQuantity(String lotNo, String quantity, String prodDate) {
        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(lotNo) && temp_list.get(i).ProductionDate.toString().equals(prodDate)) {
                if (!temp_list.get(i).IsUploaded) {
                    temp_list.get(i).QtyToConsume = Float.toString(Float.parseFloat(temp_list.get(i).QtyToConsume) + Float.parseFloat(quantity));

                    List<ProductionOrderBOMItemLineEntries> entryList = SugarRecord.find(ProductionOrderBOMItemLineEntries.class, "lot_number = ? and production_date = ?", lotNo, prodDate);
                    if (entryList.size() > 0) {
                        entryList.get(0).setQtyToConsume(temp_list.get(i).QtyToConsume);
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
        if (!checkLotNoExist()) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOTNO, NotificationManager.ALERT_MSG_INVALID_LOTNO);
            return false;
        }
        if (!checkLotQuantityOverflow(mQuantity)) {
            NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_quantity_exceeds), getResources().getString(R.string.notification_msg_quantity_exceeds));
            return false;
        }
        if (!checkQuantity(mQuantity)) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_EXCEEDS_QUANTITY, NotificationManager.ALERT_MSG_EXCEEDS_QUANTITY);
            return false;
        }

        return true;
    }

    /*private boolean checkLotNoExist() {
        if (!DataManager.getInstance().productionOrderBomListResultData.isItemLotNoAutoAssigned())     // if lot no is not auto-assigned, lot must exist within the available lot list
        {
            *//*for (int i = 0; i < mAvailableLotNoResult.AvailableLotNoData.size(); i++) {
                if (mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo().toUpperCase().equals(mLotNumber)) {
                    mProductionDate = DataConverter.ConvertJsonDateToDayMonthYear(mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate());
                    return true;
                }
            }

            return false;*//*

            for (int i = 0; i < mAvailableLotNoResult.AvailableLotNoData.size(); i++) {
                if (mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo().toUpperCase().equals(mLotNumber)) {
                    mProductionDate = DataConverter.ConvertJsonDateToDayMonthYear(mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate());
                    Log.d("LOT CORRECT", mLotNumber + " " + Integer.toString(mLotNumber.length()) + mProductionDate + " " + Integer.toString(mProductionDate.length()));
                    Log.d("LOT CORRECT", mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo() + " " + Integer.toString(mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo().length()) + mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate() + " " + Integer.toString(mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate().length()));
                    return true;
                }
                Log.d("LOT WRONG", mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo() + " " + Integer.toString(mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo().length()) + mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate() + " " + Integer.toString(mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate().length()));
            }
            Log.d("LOT WRONG", mLotNumber + " " + Integer.toString(mLotNumber.length()) + mProductionDate + " " + Integer.toString(mProductionDate.length()));
            return false;
        } else    // if it is auto-assigned, lot no can be empty. thus do not care whether lot exist in the list
        {
            return true;
        }
    }*/

    private boolean checkLotNoExist() {
        /*if (!DataManager.getInstance().productionOrderBomListResultData.isItemLotNoAutoAssigned())     // if lot no is not auto-assigned, lot must exist within the available lot list
        {*/
        for (int i = 0; i < mAvailableLotNoResult.AvailableLotNoData.size(); i++) {
            if (mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo().toUpperCase().equals(mLotNumber)) {
                mProductionDate = DataConverter.ConvertJsonDateToDayMonthYear(mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate());
                Log.d("LOT CORRECT", mLotNumber + " " + Integer.toString(mLotNumber.length()) + mProductionDate + " " + Integer.toString(mProductionDate.length()));
                Log.d("LOT CORRECT", mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo() + " " + Integer.toString(mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo().length()) + mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate() + " " + Integer.toString(mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate().length()));
                return true;
            }
            Log.d("LOT WRONG", mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo() + " " + Integer.toString(mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo().length()) + mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate() + " " + Integer.toString(mAvailableLotNoResult.AvailableLotNoData.get(i).getProductionDate().length()));
        }
        Log.d("LOT WRONG", mLotNumber + " " + Integer.toString(mLotNumber.length()) + mProductionDate + " " + Integer.toString(mProductionDate.length()));
        return false;
        /*}
        else    // if it is auto-assigned, lot no can be empty. thus do not care whether lot exist in the list
        {
            return true;
        }*/

    }

    private boolean checkLotQuantityOverflow(String scannedQuantity) {
        if (DataManager.getInstance().productionOrderBomListResultData.isItemLotNoAutoAssigned()) {
            return true;
        }

        float total = 0;
        float max_lot_qty = 0;

        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(mLotNumber) && !temp_list.get(i).IsUploaded)   // disregard those that have already been uploaded
            {
                total += Float.parseFloat(temp_list.get(i).QtyToConsume);
            }
        }

        total += Float.parseFloat(scannedQuantity);

        for (int i = 0; i < mAvailableLotNoResult.AvailableLotNoData.size(); i++) {
            if (mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo().toUpperCase().equals(mLotNumber)) {
                Log.d("QTY CHECK", "MATCHING LOT: " + mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo() + ", " + mAvailableLotNoResult.AvailableLotNoData.get(i).getAvailableQuantity());
                max_lot_qty = Float.parseFloat(mAvailableLotNoResult.AvailableLotNoData.get(i).getAvailableQuantity());
                break;
            }
        }

        Log.d("QTY CHECK", "CURRENT LOT: " + mLotNumber + ", " + Float.toString(total));
/*
        if (total > max_lot_qty) {
            Log.d("QTY CHECK", "OVERFLOW!!");
            return false;
        }
*/

        return true;
    }

    private boolean checkQuantity(String scannedQuantity) {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            total += Float.parseFloat(temp_list.get(i).QtyToConsume);
        }

        total += Float.parseFloat(scannedQuantity);

        Log.d("QTY CHECK", Float.toString(total) + " " + Float.toString(mProductionExpectedQuantityBase));
        /*if (total > mProductionExpectedQuantityBase) {
            Log.d("VALIDATION OVERFLOW", Float.toString(mProductionExpectedQuantityBase));
            Log.d("VALIDATION OVERFLOW", Float.toString(total));

            return false;
        } else {
            return true;
        }*/
        return true;
    }

    private boolean checkSerialExist() {
        if (scannedSerialNo.isEmpty()) {
            return true;
        }

        List<BarcodeSerialRecords> tempSerialList = DataManager.getInstance().GetSerialRecords(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GROUP, mItemNo, Integer.toString(mLineNo), mLotNumber);
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

    private boolean checkRequiredFields() {
        /*if (DataManager.getInstance().productionOrderBomListResultData.isItemProductionDateRequired()) {
            if (mProductionDate.length() == 0 || mProductionDate.length() < 10) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_PRODUCTION_DATE, NotificationManager.ALERT_MSG_INVALID_PRODUCTION_DATE);
                return false;
            }
        }*/
        if (DataManager.getInstance().productionOrderBomListResultData.isItemExpireDateRequired()) {
            if (mExpiryDate.length() == 0 || mExpiryDate.length() < 10) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_EXPIRY_DATE, NotificationManager.ALERT_MSG_INVALID_EXPIRY_DATE);
                return false;
            }
        }
        if (mQuantity.length() == 0 || mQuantity.equals("0") || mQuantity.equals("0.0")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
            return false;
        }
        if (DataManager.getInstance().productionOrderBomListResultData.isItemLotTrackingRequired()) {
            if (!DataManager.getInstance().productionOrderBomListResultData.isItemLotNoAutoAssigned()) {
                if (mLotNumber.length() == 0) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOT_NO, NotificationManager.ALERT_MSG_INVALID_LOT_NO);
                    return false;
                }
            }
        }
        return true;
    }

    private void initializeDisplayHeader() {
        TextView tempTextLabel = (TextView) findViewById(R.id.lblItemDescription);
        tempTextLabel.setText(DataManager.getInstance().productionOrderBomListResultData.getItemDescription());

        tempTextLabel = (TextView) findViewById(R.id.lblItemIDUom);
        tempTextLabel.setText("Item No: " + DataManager.getInstance().productionOrderBomListResultData.getItemNo() + DataConverter.CheckNullString(DataManager.getInstance().productionOrderBomListResultData.getBaseUom(), " / "));

        tempTextLabel = (TextView) findViewById(R.id.lblExpectedQuantity);
        tempTextLabel.setText("Exp. Qty: " + DataManager.getInstance().productionOrderBomListResultData.getExpectedQuantityBase());

        tempTextLabel = (TextView) findViewById(R.id.lblRemainingQuantity);
        tempTextLabel.setText("Rem. Qty: " + DataManager.getInstance().productionOrderBomListResultData.getRemainingQuantityBase());

        tempTextLabel = (TextView) findViewById(R.id.lblConsumeQuantity);
        tempTextLabel.setText("Qty to Con: " + DataManager.getInstance().productionOrderBomListResultData.getQuantityToConsumedBase());

    }

    /*
        private void updateDisplayHeader()
        {
            float total = 0;
            float total_not_uploaded = 0;
            for(int i = 0; i < temp_list.size(); i++)
            {
                total += Float.parseFloat(temp_list.get(i).QtyToConsume);
                if(!temp_list.get(i).IsUploaded)
                {
                    total_not_uploaded += Float.parseFloat(temp_list.get(i).QtyToConsume);
                }
            }

            float currBalance = mProductionExpectedQuantityBase - total_not_uploaded;


             //tempTextLabel = (TextView) findViewById(R.id.lblRemainingQuantity);
            //tempTextLabel.setText("Rem. Qty: " + Float.toString(currBalance));

            TextView tempTextLabel = (TextView) findViewById(R.id.lblConsumeQuantity);
            tempTextLabel.setText("Qty to Con: " + Float.toString(total));
        }
    */
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

    private void initializeDataEntry() {
        if (isScanned) {
            String[] splitString = scannedString.split("\\|");

            if (!splitString[4].isEmpty()) {
                if (DataManager.getInstance().productionOrderBomListResultData.isItemExpireDateRequired() && !splitString[4].isEmpty()) {
                    calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                    textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));
                }
                calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));
            }

            /*if(!splitString[3].isEmpty()) {
            *//*if (DataManager.getInstance().productionOrderBomListResultData.isItemProductionDateRequired() && !splitString[3].isEmpty()) {
                calProductionDate.set(Integer.parseInt(splitString[3].substring(0, 4)), Integer.parseInt(splitString[3].substring(4, 6)), Integer.parseInt(splitString[3].substring(6, 8)));

                textProductionDate.setText(splitString[3].substring(6, 8) + "/" + splitString[3].substring(4, 6) + "/" + splitString[3].substring(0, 4));
            }*//*
                calProductionDate.set(Integer.parseInt(splitString[3].substring(0, 4)), Integer.parseInt(splitString[3].substring(4, 6)), Integer.parseInt(splitString[3].substring(6, 8)));

                textProductionDate.setText(splitString[3].substring(6, 8) + "/" + splitString[3].substring(4, 6) + "/" + splitString[3].substring(0, 4));
            }*/

            if (DataManager.getInstance().productionOrderBomListResultData.isItemLotTrackingRequired()) {
                textLotNumber.setText(splitString[3]);
            }

            if (!DataManager.getInstance().productionOrderBomListResultData.isItemLotNoAutoAssigned()) {
                textLotNumber.setText(splitString[3]);
            }

            textQuantity.setText(splitString[1]);

            scannedLotNo = splitString[3];
            if (scannedLotNo != null) {
                textLotNumber.setText(scannedLotNo);
            }
            scannedSerialNo = splitString[5];
            enableTextFieldsEditing(true);
            //textQuantity.setEnabled(true);
        }

        Log.d("PO ITEM DETAILS", calProductionDate.toString());
        Log.d("PO ITEM DETAILS", calExpiryDate.toString());
    }

    public class PostProductionOrderItemEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        List<ProductionOrderItemTrackingLineParameter> mProductionOrderItemTrackingLineParameter;
        BaseResult mReturnBaseResult;

        PostProductionOrderItemEntriesTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mProductionOrderItemTrackingLineParameter = new ArrayList<ProductionOrderItemTrackingLineParameter>();

            for (int i = 0; i < temp_list.size(); i++) {
                if (!temp_list.get(i).IsUploaded) {
                    String production_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ProductionDate);
                    String expiry_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ExpiryDate);

                    ProductionOrderItemTrackingLineParameter temp_ProductionOrderItemTrackingLineParameter = new ProductionOrderItemTrackingLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mProductionOrderNo, mProductionOrderLineNo, mLineNo, production_date, expiry_date, temp_list.get(i).LotNo, "", Float.parseFloat(temp_list.get(i).QtyToConsume));
                    mProductionOrderItemTrackingLineParameter.add(temp_ProductionOrderItemTrackingLineParameter);

                }
            }

            Gson gson = new Gson();
            String json = gson.toJson(mProductionOrderItemTrackingLineParameter);
            Log.d("JSON", json);

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().PostProductionOrderItemTrackingLine(mProductionOrderItemTrackingLineParameter);

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

                        List<ProductionOrderBOMItemLineEntries> dataList = SugarRecord.find(ProductionOrderBOMItemLineEntries.class, "line_no = ?", String.valueOf(mLineNo));
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).delete();
                        }

                        // REMOVE BARCODE SERIAL LIST
                        DataManager.getInstance().DeleteSerialRecord(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GROUP, mItemNo, Integer.toString(mLineNo));

                        Intent intent = new Intent(getBaseContext(), ProductionPasteGroupBOMActivity.class);
                        intent.putExtra("PRODUCTION_ORDER_NO", mProductionOrderNo);
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

    public class GetProductionOrderLineLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ProductionOrderLineLotEntriesSearchParameter mProductionOrderLineLotEntriesSearchParameter;
        PasteGroupProductionOrderLineLotEntriesResult mPasteGroupProductionOrderLineLotEntriesResult;

        GetProductionOrderLineLotEntriesTask(Activity activity) {

            mActivity = activity;

            mProductionOrderLineLotEntriesSearchParameter = new ProductionOrderLineLotEntriesSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mProductionOrderNo, mProductionOrderLineNo, mLineNo);

            Gson gson = new Gson();
            String json = gson.toJson(mProductionOrderLineLotEntriesSearchParameter);
            Log.d("GETPRODUCTION", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PasteGroupProductionOrderLineLotEntriesResult> call = mApp.getNavBrokerService().GetProductionOrderLotEntries(mProductionOrderLineLotEntriesSearchParameter);

                mPasteGroupProductionOrderLineLotEntriesResult = call.execute().body();

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
                    if (mPasteGroupProductionOrderLineLotEntriesResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        if (mPasteGroupProductionOrderLineLotEntriesResult != null) {
                            if (mPasteGroupProductionOrderLineLotEntriesResult.getPasteGroupProductionOrderLineLotEntriesResultData() != null
                                    && mPasteGroupProductionOrderLineLotEntriesResult.getPasteGroupProductionOrderLineLotEntriesResultData().size() != 0) {
                                for (int i = 0; i < mPasteGroupProductionOrderLineLotEntriesResult.getPasteGroupProductionOrderLineLotEntriesResultData().size(); i++) {
                                    ProductionOrderLineLotEntriesResultData temp_result = mPasteGroupProductionOrderLineLotEntriesResult.getPasteGroupProductionOrderLineLotEntriesResultData().get(i);
                                    String temp_prod_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_result.getProductionDate());
                                    String temp_expire_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_result.getExpireDate());
                                    ProductionOrderEntry temp_entry = new ProductionOrderEntry(temp_list.size() + 1, temp_prod_date, temp_expire_date, Float.toString(temp_result.getQuantityToShip()), temp_result.getLotNo().toUpperCase(), "", ConfigurationManager.DATA_NO_LOCAL_ENTRY_INDEX, temp_result.getEntryNo(), ConfigurationManager.DATA_UPLOADED_TRUE);
                                    temp_list.add(temp_entry);

                                }
                            }
                        }

                        //check whether there are entries in the local database and append them to the list
                        List<ProductionOrderBOMItemLineEntries> entryList = SugarRecord.find(ProductionOrderBOMItemLineEntries.class, "line_no = ?", Integer.toString(mLineNo));

                        for (int i = 0; i < entryList.size(); i++) {
                            ProductionOrderEntry temp_entry = new ProductionOrderEntry(temp_list.size() + 1, entryList.get(i).getProductionDate(), entryList.get(i).getExpiryDate(), entryList.get(i).getQtyToConsume(), entryList.get(i).getLotNumber(), entryList.get(i).getSerialNo(), entryList.get(i).getEntryIndexNo(), ConfigurationManager.DATA_NO_SERVER_ENTRY_INDEX, ConfigurationManager.DATA_UPLOADED_FALSE);
                            temp_list.add(temp_entry);
                        }

                        historyAdapter = new ProductionPasteGroupBomLotEntryListArrayAdapter(mContext, temp_list);

                        historyList.setAdapter(historyAdapter);

                        mQueryProductionOrderAvailableLotNoParameter = new QueryProductionOrderAvailableLotNoParameter((Activity) mContext);
                        mQueryProductionOrderAvailableLotNoParameter.execute((Void) null);

                        UpdateDisplayQuantity();
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mPasteGroupProductionOrderLineLotEntriesResult.getMessage());
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

    public class QueryProductionOrderAvailableLotNoParameter extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ProductionOrderAvailableLotNoParameter mProductionOrderAvailableLotNoParameter;


        QueryProductionOrderAvailableLotNoParameter(Activity activity) {

            mActivity = activity;

            mProductionOrderAvailableLotNoParameter = new ProductionOrderAvailableLotNoParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mProductionOrderNo, mProductionOrderLineNo, mLineNo);

            Gson gson = new Gson();
            String json = gson.toJson(mProductionOrderAvailableLotNoParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<AvailableLotNoResult> call = mApp.getNavBrokerService().QueryProductionOrderAvailableLotNo(mProductionOrderAvailableLotNoParameter);

                mAvailableLotNoResult = call.execute().body();

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
                    if (mAvailableLotNoResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        if (mAvailableLotNoResult == null || mAvailableLotNoResult.AvailableLotNoData == null) {
                            mAvailableLotNoResult = new AvailableLotNoResult();
                            mAvailableLotNoResult.AvailableLotNoData = new ArrayList<AvailableLotNoResultData>();
                        }

                        if (isScanned) {
                            if (mAvailableLotNoResult != null && mAvailableLotNoResult.AvailableLotNoData.size() != 0) {
                                prepareAddEntryParameters();

                                if (validateEntry()) {
                                    btnAddEntry.callOnClick();
                                } else {
                                    clearInputFields();
                                }
                                btnScan.callOnClick();
                            } else {
                                NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_invalid_lotno), getResources().getString(R.string.notification_msg_invalid_lotno));
                                clearInputFields();
                                btnScan.callOnClick();
                            }
                        }
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mAvailableLotNoResult.getMessage());
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

    public class RemoveProductionOrderLineLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        RemoveProductionOrderLotEntryParameter mRemoveProductionOrderLotEntryParameter;
        BaseResult mReturnBaseResult;

        int mPositionDelete;
        float deductValue;

        RemoveProductionOrderLineLotEntriesTask(Activity activity, String ProductionNo, int LineNo, int EntryNo, int position, String lotSerialNo, float quantityToDeduct) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_DELETE_DATA, NotificationManager.DIALOG_MSG_DELETE);

            mActivity = activity;

            int SerialOrLotFlag = 0;
            if (DataManager.getInstance().productionOrderBomListResultData.isItemLotTrackingRequired()) {
                SerialOrLotFlag = 1;
            }

            mRemoveProductionOrderLotEntryParameter = new RemoveProductionOrderLotEntryParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), ProductionNo, mProductionOrderLineNo, LineNo, SerialOrLotFlag, lotSerialNo, quantityToDeduct);

            mPositionDelete = position;
            deductValue = quantityToDeduct;

            Gson gson = new Gson();
            String json = gson.toJson(mRemoveProductionOrderLotEntryParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().RemoveProductionOrderLotEntry(mRemoveProductionOrderLotEntryParameter);

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
                        mProductionConsumeQuantityBase = mProductionConsumeQuantityBase - deductValue;
                        UpdateDisplayQuantity();

                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_REMOVE_SUCCESSFUL, NotificationManager.MSG_REMOVE_MESSAGE);

                        mQueryProductionOrderAvailableLotNoParameter = new QueryProductionOrderAvailableLotNoParameter((Activity) mContext);
                        mQueryProductionOrderAvailableLotNoParameter.execute((Void) null);
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

    public void RemoveEntry(int position, int entryNo, String lotSerialNo, float qtyToDeduct) {
        //Log.d("SERVER REMOVE", "TO: " + mProductionOrderBom + " LineNo: " + mLineNo + " position: " + position + " EntryNO: " + entryNo);
        mRemoveProductionOrderPasteGroupLineLotEntriesTask = new RemoveProductionOrderLineLotEntriesTask((Activity) mContext, mProductionOrderNo, mLineNo, entryNo, position, lotSerialNo, qtyToDeduct);
        mRemoveProductionOrderPasteGroupLineLotEntriesTask.execute((Void) null);
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
                Intent intent = new Intent(getBaseContext(), ProductionPasteGroupBOMActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("PRODUCTION_ORDER_NO", mProductionOrderNo);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getBaseContext(), ProductionPasteGroupBOMActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("PRODUCTION_ORDER_NO", mProductionOrderNo);
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
        List<ProductionOrderBOMItemLineEntries> entryList = SugarRecord.find(ProductionOrderBOMItemLineEntries.class, "line_no = ?", Integer.toString(mLineNo));
        if (entryList.size() > 0) {
            for (int i = 0; i < entryList.size(); i++) {
                entryList.get(i).delete();
            }
        }

        // REMOVE BARCODE SERIAL LIST
        DataManager.getInstance().DeleteSerialRecord(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GROUP, mItemNo, Integer.toString(mLineNo));

        for (int i = temp_list.size() - 1; i >= 0; i--) {
            if (!temp_list.get(i).IsUploaded) {
                temp_list.remove(i);
            }
        }

        historyAdapter.notifyDataSetChanged();
        UpdateDisplayQuantity();
    }

    // CALLED BY ADAPTER TO REMOVE LOCAL ENTRY FROM SERIAL LIST
    public void RemoveSerialEntry(String mLotNo) {
        DataManager.getInstance().DeleteSerialRecord(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GROUP, mItemNo, Integer.toString(mLineNo), mLotNo);

        UpdateDisplayQuantity();
    }

    public void UpdateDisplayQuantity() {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            total += Float.parseFloat(temp_list.get(i).QtyToConsume);
        }
        TextView tempTextLabel = (TextView) findViewById(R.id.lblConsumeQuantity);
        tempTextLabel.setText("Qty to Con: " + Float.toString(total));
    }

}
