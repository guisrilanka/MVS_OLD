package com.gui.mdt.thongsieknavclient.ui;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
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
import com.gui.mdt.thongsieknavclient.adapters.productionpastegroup.ProductionPasteGroupRoutingLotEntryListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderRouteLotEntriesSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderRouteTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderLineLotEntriesResultData;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderRouteLotEntry;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.RemovePasteGoodsProductionOrderRouteLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialRecords;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.ProductionOrderRoutingItemLineEntries;
import com.orm.SugarRecord;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created by user on 10/26/2016.
 */

public class ProductionPasteGroupRoutingLotEntryActivity extends BaseActivity {
    GetPasteGroupProductionOrderRouteLotEntriesTask mGetPasteGroupProductionOrderRouteLotEntriesTask = null;
    PostPasteGroupProductionOrderRouteItemEntriesTask mPostPasteGroupProductionOrderRouteItemEntriesTask = null;
    RemovePasteGroupProductionOrderRouteLotEntriesTask mRemovePasteGroupProductionOrderRouteLotEntriesTask = null;

    Context mContext;
    private NavClientApp mApp;

    //AvailableLotNoResult mAvailableLotNoResult;

    ListView historyList;
    ProductionPasteGroupRoutingLotEntryListArrayAdapter historyAdapter;
    ArrayList<ProductionOrderRouteLotEntry> temp_list;

    //ListView lotNumberList;
    //LotNumberListArrayAdapter lotNumberArrayAdapter;

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
    private String mOperationNo;
    private String mItemNo;
    private int mLineNo;
    private String mItemDescription;
    //private float mProductionExpectedQuantityBase;
    //private float mProductionRemainingQuantityBase;
    //private float mProductionConsumeQuantityBase;

    private String mProductionDate;
    private String mExpiryDate;
    private String mQuantity;
    private String mLotNumber;
    private String xLastLotNo;

    private boolean isInterfaceScanning = false;
    LinearLayout mLinearLayoutManual;
    LinearLayout mLinearLayoutScan;

    private boolean isStarted = false;

    private int entryIndexCounter;

    protected int getLayoutResource() {
        return R.layout.activity_production_finished_goods_routing_lot_entry;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_finished_goods_routing_lot_entry);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Item No.: " + DataManager.getInstance().pasteGroupProductionOrderListResultData.getItemNo() + "(Routing - Output)");

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
        temp_list = new ArrayList<ProductionOrderRouteLotEntry>();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String isScanning = extras.getString("IS_SCANNING");
            mOperationNo = extras.getString("OPERATION_NO");
            mItemDescription = extras.getString("ITEM_DESCRIPTION");
            //The key argument here must match that used in the other activity
            xLastLotNo = extras.getString("LAST_LOT_NO");
            mProductionOrderNo = DataManager.getInstance().ProductionOrderNo;

            if (isScanning.contains("YES")) {
                isScanned = true;
                scannedString = extras.getString("SCAN_CONTENT");
            }
/*
            if (DataManager.getInstance().pasteGroupProductionOrderListResultData.getExpectedQuantity() != null) {
                mProductionExpectedQuantity = Float.parseFloat(DataManager.getInstance().pasteGroupProductionOrderListResultData.getExpectedQuantity());
            }

            if (DataManager.getInstance().pasteGroupProductionOrderListResultData.getRemainingQuantity() != null) {
                mProductionRemainingQuantity = Float.parseFloat(DataManager.getInstance().pasteGroupProductionOrderListResultData.getRemainingQuantity());
            }

            if (DataManager.getInstance().pasteGroupProductionOrderListResultData.getQuantityToConsumed() != null) {
                mProductionConsumeQuantity = Float.parseFloat(DataManager.getInstance().pasteGroupProductionOrderListResultData.getQuantityToConsumed());
            }
*/
            mLineNo = Integer.parseInt(DataManager.getInstance().pasteGroupProductionOrderListResultData.getLineNo());
            mItemNo = DataManager.getInstance().pasteGroupProductionOrderListResultData.getItemNo();

            Log.d("PO ITEM DETAILS", "PO: " + mProductionOrderNo.toString());
            Log.d("PO ITEM DETAILS", mOperationNo);
            Log.d("PO ITEM DETAILS", mItemNo);
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
                        DataManager.getInstance().AddSerialRecord(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GOODS_ROUTING, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);
                        Toast.makeText(mContext, "Lot No exists! Quantity merged!", Toast.LENGTH_LONG).show();
                    } else {
                        temp_list.add(new ProductionOrderRouteLotEntry(temp_list.size() + 1, mProductionDate, mExpiryDate, mQuantity, mLotNumber, scannedSerialNo, entryIndexCounter, ConfigurationManager.DATA_NO_SERVER_ENTRY_INDEX, ConfigurationManager.DATA_UPLOADED_FALSE));

                        ProductionOrderRoutingItemLineEntries temp_entry = new ProductionOrderRoutingItemLineEntries(mProductionOrderNo, mOperationNo, mItemNo, mLineNo,
                                mProductionDate, mExpiryDate, mQuantity, mLotNumber, scannedSerialNo, "", entryIndexCounter, ConfigurationManager.DATA_UPLOADED_FALSE, System.currentTimeMillis());
                        temp_entry.save();

                        DataManager.getInstance().AddSerialRecord(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GOODS_ROUTING, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);

                        entryIndexCounter++;


                        Log.d("SUGARORM", "ItemNo: " + mItemNo);
                        List<ProductionOrderRoutingItemLineEntries> entryList = SugarRecord.find(ProductionOrderRoutingItemLineEntries.class, "item_no = ?", mItemNo);

                        for (int i = 0; i < entryList.size(); i++) {
                            Log.d("SUGARORM", i + ": " + entryList.get(i).toString());
                        }
                    }

                    historyAdapter.notifyDataSetChanged();
                    //updateDisplayHeader();
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
                    mPostPasteGroupProductionOrderRouteItemEntriesTask = new PostPasteGroupProductionOrderRouteItemEntriesTask((Activity) mContext);
                    mPostPasteGroupProductionOrderRouteItemEntriesTask.execute((Void) null);
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
                        Toast.makeText(mContext, splitString[ConfigurationManager.BARCODE_ARRAY_SERIAL], Toast.LENGTH_SHORT).show();
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
/*
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
        });*/

        mGetPasteGroupProductionOrderRouteLotEntriesTask = new GetPasteGroupProductionOrderRouteLotEntriesTask(this);
        mGetPasteGroupProductionOrderRouteLotEntriesTask.execute((Void) null);
    }

    private void prepareAddEntryParameters() {
        mQuantity = textQuantity.getText().toString();

        if (DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemProductionDateRequired()) {
            mProductionDate = textProductionDate.getText().toString();
        } else {
            mProductionDate = textProductionDate.getText().toString();
            //mProductionDate = "";
        }

        if (DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemExpireDateRequired()) {
            mExpiryDate = textExpiryDate.getText().toString();
        } else {
            mExpiryDate = textExpiryDate.getText().toString();
            //mExpiryDate = "";
        }

        mLotNumber = textLotNumber.getText().toString().toUpperCase();

    }

    private boolean mergeSameLotEntryQuantity(String lotNo, String quantity, String prodDate) {
        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(lotNo) && temp_list.get(i).ProductionDate.toString().equals(prodDate)) {
                if (!temp_list.get(i).IsUploaded) {
                    temp_list.get(i).QtyProduced = Float.toString(Float.parseFloat(temp_list.get(i).QtyProduced) + Float.parseFloat(quantity));

                    List<ProductionOrderRoutingItemLineEntries> entryList = SugarRecord.find(ProductionOrderRoutingItemLineEntries.class, "lot_number = ? and production_date = ?", lotNo, prodDate);
                    if (entryList.size() > 0) {
                        entryList.get(0).setQtyProduced(temp_list.get(i).QtyProduced);
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

//        if (!checkQuantity(mQuantity)) {
//            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_EXCEEDS_QUANTITY, NotificationManager.ALERT_MSG_EXCEEDS_QUANTITY);
//            return false;
//        }

        return true;
    }

    private boolean checkRequiredFields() {
        if (DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemExpireDateRequired()) {
            if (mExpiryDate.length() == 0 || mExpiryDate.length() < 10) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_EXPIRY_DATE, NotificationManager.ALERT_MSG_INVALID_EXPIRY_DATE);
                return false;
            }
        }
        if (mQuantity.length() == 0 || mQuantity.equals("0") || mQuantity.equals("0.0")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
            return false;
        }


        if (DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemLotTrackingRequired()) {
            if (!DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemLotNoAutoAssigned()) {
                if (mLotNumber.length() == 0) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOT_NO, NotificationManager.ALERT_MSG_INVALID_LOT_NO);
                    return false;
                }
            }
        }
        return true;
    }


//    private boolean checkQuantity(String scannedQuantity)
//    {
//        float total = 0;
//        for(int i = 0; i < temp_list.size(); i++)
//        {
//            total += Float.parseFloat(temp_list.get(i).QtyProduced);
//        }
//
//        total += Float.parseFloat(scannedQuantity);
//
//        Toast.makeText(mContext, Float.toString(total) + " " + Float.toString(mPurchaseOrderBalanceQuantity), Toast.LENGTH_SHORT).show();
//        if(total > mPurchaseOrderBalanceQuantity)
//        {
//            Log.d("VALIDATION", Float.toString(mPurchaseOrderQuantity));
//            Log.d("VALIDATION", Float.toString(total));
//
//            return false;
//        }
//        else
//        {
//            return true;
//        }
//    }

    private boolean checkSerialExist() {
        boolean serialExist = false;

        List<BarcodeSerialRecords> tempSerialList = DataManager.getInstance().GetSerialRecords(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GOODS_ROUTING, mItemNo, Integer.toString(mLineNo), mLotNumber);
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

    private void enableTextFieldsEditing(boolean isEnable) {
        if (isEnable) {
            textExpiryDate.setEnabled(true);
            textProductionDate.setEnabled(true);
            textLotNumber.setEnabled(true);
            textQuantity.setEnabled(true);
        } else {
            textExpiryDate.setEnabled(false);
            textProductionDate.setEnabled(false);
            textLotNumber.setEnabled(false);
            textQuantity.setEnabled(false);
        }

    }

    private void disableTextFields() {
        textExpiryDate.setEnabled(true);
        textProductionDate.setEnabled(true);
        textLotNumber.setEnabled(true);

        /*
        Log.d("DISABLE", String.valueOf(DataManager.getInstance().pasteGroupProductionOrderListResultData.getIsItemExpireDateRequired()));
        Log.d("DISABLE", String.valueOf(DataManager.getInstance().pasteGroupProductionOrderListResultData.getIsItemProductionDateRequired()));
        Log.d("DISABLE", String.valueOf(DataManager.getInstance().pasteGroupProductionOrderListResultData.getIsItemLotTrackingRequired()));
        Log.d("DISABLE", String.valueOf(DataManager.getInstance().pasteGroupProductionOrderListResultData.getIsItemLotNoAutoAssigned()));
*/

        /*if(!DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemExpireDateRequired())
        {
            textExpiryDate.setBackgroundColor(Color.GRAY);
            textExpiryDate.setEnabled(false);
        }*/

        /*if(!DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemProductionDateRequired())
        {
            textProductionDate.setBackgroundColor(Color.GRAY);
            textProductionDate.setEnabled(false);
        }*/

        /*if(!DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemLotTrackingRequired())
        {
            textLotNumber.setBackgroundColor(Color.GRAY);
            textLotNumber.setEnabled(false);
        }*/

        if (DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemLotNoAutoAssigned()) {
            textLotNumber.setBackgroundColor(Color.WHITE);
            textLotNumber.setEnabled(true);
        }
    }

    private void initializeDisplayHeader() {
        TextView tempTextLabel = (TextView) findViewById(R.id.lblProductionOrderNo);
        tempTextLabel.setText(DataManager.getInstance().pasteGroupProductionOrderListResultData.getProductionOrderNo());

        tempTextLabel = (TextView) findViewById(R.id.lblDueDate);

        if (!DataManager.getInstance().pasteGroupProductionOrderListResultData.getProductionDate().contains("0001-")) {
            String[] dateSplit = DataManager.getInstance().pasteGroupProductionOrderListResultData.getProductionDate().split("T")[0].split("-");
            tempTextLabel.setText(dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
        } else {
            tempTextLabel.setText(" ");
        }

        tempTextLabel = (TextView) findViewById(R.id.lblItemDescription);
        tempTextLabel.setText(mItemDescription);

        tempTextLabel = (TextView) findViewById(R.id.lblItemIDUom);
        tempTextLabel.setText("Item No: " + DataManager.getInstance().pasteGroupProductionOrderListResultData.getItemNo() + DataConverter.CheckNullString(DataManager.getInstance().pasteGroupProductionOrderListResultData.getBaseUom(), " / "));

        tempTextLabel = (TextView) findViewById(R.id.lblLineDescription);
        tempTextLabel.setText(DataManager.getInstance().pasteGroupProductionOrderListResultData.getItemDescription());

        tempTextLabel = (TextView) findViewById(R.id.lblLineIDUom);
        tempTextLabel.setText(DataManager.getInstance().pasteGroupProductionOrderListResultData.getItemNo() + " / " + DataManager.getInstance().pasteGroupProductionOrderListResultData.getBaseUom());
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
    }

    private void initializeDataEntry() {
        if (isScanned) {
            String[] splitString = scannedString.split("\\|");
            if (!splitString[4].isEmpty()) {
                if (DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemExpireDateRequired()) {
                    calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                    textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));
                }
                calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));
            }

            /*if(!splitString[3].isEmpty()) {
                if (DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemProductionDateRequired()) {
                    calProductionDate.set(Integer.parseInt(splitString[3].substring(0, 4)), Integer.parseInt(splitString[3].substring(4, 6)), Integer.parseInt(splitString[3].substring(6, 8)));

                    textProductionDate.setText(splitString[3].substring(6, 8) + "/" + splitString[3].substring(4, 6) + "/" + splitString[3].substring(0, 4));
                }
                calProductionDate.set(Integer.parseInt(splitString[3].substring(0, 4)), Integer.parseInt(splitString[3].substring(4, 6)), Integer.parseInt(splitString[3].substring(6, 8)));

                textProductionDate.setText(splitString[3].substring(6, 8) + "/" + splitString[3].substring(4, 6) + "/" + splitString[3].substring(0, 4));
            }*/
            if (DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemLotTrackingRequired()) {
                if (!DataManager.getInstance().pasteGroupProductionOrderListResultData.isItemLotNoAutoAssigned()) {
                    textLotNumber.setText(splitString[3]);
                }
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

    public class PostPasteGroupProductionOrderRouteItemEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        List<PasteGroupProductionOrderRouteTrackingLineParameter> mPasteGroupProductionOrderRouteTrackingLineParameter;
        BaseResult mReturnBaseResult;

        PostPasteGroupProductionOrderRouteItemEntriesTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mPasteGroupProductionOrderRouteTrackingLineParameter = new ArrayList<PasteGroupProductionOrderRouteTrackingLineParameter>();

            for (int i = 0; i < temp_list.size(); i++) {
                if (!temp_list.get(i).IsUploaded) {
                    String production_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ProductionDate);
                    String expiry_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ExpiryDate);

                    PasteGroupProductionOrderRouteTrackingLineParameter temp_ProductionOrderItemTrackingLineParameter = new PasteGroupProductionOrderRouteTrackingLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mProductionOrderNo, mLineNo, mOperationNo, production_date, expiry_date, temp_list.get(i).LotNo, "", Float.parseFloat(temp_list.get(i).QtyProduced));
                    mPasteGroupProductionOrderRouteTrackingLineParameter.add(temp_ProductionOrderItemTrackingLineParameter);

                }
            }

            Gson gson = new Gson();
            String json = gson.toJson(mPasteGroupProductionOrderRouteTrackingLineParameter);
            Log.d("JSON", json);

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().PostProductionOrderRouteTrackingLine(mPasteGroupProductionOrderRouteTrackingLineParameter);

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

                        List<ProductionOrderRoutingItemLineEntries> dataList = SugarRecord.find(ProductionOrderRoutingItemLineEntries.class, "line_no = ?", String.valueOf(mLineNo));
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).delete();
                        }

                        // REMOVE BARCODE SERIAL LIST
                        DataManager.getInstance().DeleteSerialRecord(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GOODS_ROUTING, mItemNo, Integer.toString(mLineNo));

                        Intent intent = new Intent(getBaseContext(), ProductionPasteGroupRoutingActivity.class);
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

    public class GetPasteGroupProductionOrderRouteLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PasteGroupProductionOrderRouteLotEntriesSearchParameter mPasteGroupProductionOrderRouteLotEntriesSearchParameter;
        PasteGroupProductionOrderLineLotEntriesResult mPasteGroupProductionOrderRouteLotEntriesResult;

        GetPasteGroupProductionOrderRouteLotEntriesTask(Activity activity) {

            mActivity = activity;

            mPasteGroupProductionOrderRouteLotEntriesSearchParameter = new PasteGroupProductionOrderRouteLotEntriesSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mProductionOrderNo, mLineNo, mOperationNo);

            Gson gson = new Gson();
            String json = gson.toJson(mPasteGroupProductionOrderRouteLotEntriesSearchParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PasteGroupProductionOrderLineLotEntriesResult> call = mApp.getNavBrokerService().ProductionOrderRouteLotEntries(mPasteGroupProductionOrderRouteLotEntriesSearchParameter);

                mPasteGroupProductionOrderRouteLotEntriesResult = call.execute().body();

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
                    if (mPasteGroupProductionOrderRouteLotEntriesResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        if (mPasteGroupProductionOrderRouteLotEntriesResult != null) {
                            if (mPasteGroupProductionOrderRouteLotEntriesResult.getPasteGroupProductionOrderLineLotEntriesResultData() != null
                                    && mPasteGroupProductionOrderRouteLotEntriesResult.getPasteGroupProductionOrderLineLotEntriesResultData().size() != 0) {
                                for (int i = 0; i < mPasteGroupProductionOrderRouteLotEntriesResult.getPasteGroupProductionOrderLineLotEntriesResultData().size(); i++) {
                                    ProductionOrderLineLotEntriesResultData temp_result = mPasteGroupProductionOrderRouteLotEntriesResult.getPasteGroupProductionOrderLineLotEntriesResultData().get(i);
                                    String temp_prod_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_result.getProductionDate());
                                    String temp_expire_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_result.getExpireDate());
                                    ProductionOrderRouteLotEntry temp_entry = new ProductionOrderRouteLotEntry(temp_list.size() + 1, temp_prod_date, temp_expire_date, Float.toString(temp_result.getQuantityToShip()), temp_result.getLotNo().toUpperCase(), "", ConfigurationManager.DATA_NO_LOCAL_ENTRY_INDEX, temp_result.getEntryNo(), ConfigurationManager.DATA_UPLOADED_TRUE);
                                    temp_list.add(temp_entry);

                                }
                            }
                        }

                        //check whether there are entries in the local database and append them to the list
                        List<ProductionOrderRoutingItemLineEntries> entryList = SugarRecord.find(ProductionOrderRoutingItemLineEntries.class, "line_no = ?", Integer.toString(mLineNo));

                        for (int i = 0; i < entryList.size(); i++) {
                            ProductionOrderRouteLotEntry temp_entry = new ProductionOrderRouteLotEntry(temp_list.size() + 1, entryList.get(i).getProductionDate(), entryList.get(i).getExpiryDate(), entryList.get(i).getQtyProduced(), entryList.get(i).getLotNumber(), entryList.get(i).getSerialNo(), entryList.get(i).getEntryIndexNo(), ConfigurationManager.DATA_NO_SERVER_ENTRY_INDEX, ConfigurationManager.DATA_UPLOADED_FALSE);
                            temp_list.add(temp_entry);
                        }

                        historyAdapter = new ProductionPasteGroupRoutingLotEntryListArrayAdapter(mContext, temp_list);

                        historyList.setAdapter(historyAdapter);

                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mPasteGroupProductionOrderRouteLotEntriesResult.getMessage());
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

    public class RemovePasteGroupProductionOrderRouteLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        RemovePasteGoodsProductionOrderRouteLotEntryParameter mRemovePasteGoodsProductionOrderRouteLotEntryParameter;
        BaseResult mReturnBaseResult;

        int mPositionDelete;

        RemovePasteGroupProductionOrderRouteLotEntriesTask(Activity activity, String ProductionNo, int LineNo, int EntryNo, int position, String lotSerialNo, float quantityToDeduct) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_DELETE_DATA, NotificationManager.DIALOG_MSG_DELETE);

            mActivity = activity;

            int SerialOrLotFlag = 0;
            if (DataManager.getInstance().pasteGroupProductionOrderListResultData.isRoutingRequired()) {
                SerialOrLotFlag = 1;
            }

            mRemovePasteGoodsProductionOrderRouteLotEntryParameter = new RemovePasteGoodsProductionOrderRouteLotEntryParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), ProductionNo, LineNo, mOperationNo, lotSerialNo, quantityToDeduct);

            mPositionDelete = position;

            Gson gson = new Gson();
            String json = gson.toJson(mRemovePasteGoodsProductionOrderRouteLotEntryParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().RemoveProductionOrderRouteLotEntry(mRemovePasteGoodsProductionOrderRouteLotEntryParameter);

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

    public void RemoveEntry(int position, int entryNo, String lotSerialNo, float qtyToDeduct) {
        //Log.d("SERVER REMOVE", "TO: " + mProductionOrderBom + " LineNo: " + mLineNo + " position: " + position + " EntryNO: " + entryNo);
        mRemovePasteGroupProductionOrderRouteLotEntriesTask = new RemovePasteGroupProductionOrderRouteLotEntriesTask((Activity) mContext, mProductionOrderNo, mLineNo, entryNo, position, lotSerialNo, qtyToDeduct);
        mRemovePasteGroupProductionOrderRouteLotEntriesTask.execute((Void) null);
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
                Intent intent = new Intent(getBaseContext(), ProductionPasteGroupRoutingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("PRODUCTION_ORDER_NO", mProductionOrderNo);
                intent.putExtra("LAST_LOT_NO", xLastLotNo);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getBaseContext(), ProductionPasteGroupRoutingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("PRODUCTION_ORDER_NO", mProductionOrderNo);
            intent.putExtra("LAST_LOT_NO", xLastLotNo);
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
        List<ProductionOrderRoutingItemLineEntries> entryList = SugarRecord.find(ProductionOrderRoutingItemLineEntries.class, "line_no = ?", Integer.toString(mLineNo));
        if (entryList.size() > 0) {
            for (int i = 0; i < entryList.size(); i++) {
                entryList.get(i).delete();
            }
        }

        // REMOVE BARCODE SERIAL LIST
        DataManager.getInstance().DeleteSerialRecord(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GOODS_ROUTING, mItemNo, Integer.toString(mLineNo));

        for (int i = temp_list.size() - 1; i >= 0; i--) {
            if (!temp_list.get(i).IsUploaded) {
                temp_list.remove(i);
            }
        }

        historyAdapter.notifyDataSetChanged();
    }

    // CALLED BY ADAPTER TO REMOVE LOCAL ENTRY FROM SERIAL LIST
    public void RemoveSerialEntry(String mLotNo) {
        DataManager.getInstance().DeleteSerialRecord(mProductionOrderNo, ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GOODS_ROUTING, mItemNo, Integer.toString(mLineNo), mLotNo);

    }

    // NO NEED TO UPDATE QUANTITY IN THE DISPLAY HEADER
    /*
    public void UpdateDisplayQuantity()
    {
        float total = 0;
        for(int i = 0; i < temp_list.size(); i++)
        {
            total += Float.parseFloat(temp_list.get(i).QtyToConsume);
        }

        TextView tempTextLabel = (TextView) findViewById(R.id.lblConsumeQuantity);
        tempTextLabel.setText("Qty to Con: " + Float.toString(total));
    }*/
}
