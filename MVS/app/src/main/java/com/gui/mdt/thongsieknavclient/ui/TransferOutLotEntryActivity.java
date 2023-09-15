package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import com.gui.mdt.thongsieknavclient.adapters.transfershipment.TransferOutLotEntryListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.AvailableLotNoResult;
import com.gui.mdt.thongsieknavclient.model.AvailableLotNoResultData;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialRecords;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.TransferShipmentItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.RemoveTransferShipmentLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentAvailableLotNoParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentEntry;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentItemTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentLineLotEntriesResultData;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentLineLotEntriesSearchParameter;
import com.orm.SugarRecord;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class TransferOutLotEntryActivity extends BaseActivity {

    Context mContext;
    private NavClientApp mApp;

    RemoveTransferShipmentLineLotEntriesTask mRemoveTransferShipmentLineLotEntriesTask = null;
    PostTransferShipmentItemEntriesTask mPostTransferShipmentItemEntriesTask = null;
    GetTransferShipmentLineLotEntriesTask mGetTransferShipmentLineLotEntriesTask = null;
    QueryTransferShipmentAvailableLotNoParameter mQueryTransferShipmentAvailableLotNoParameter = null;

    AvailableLotNoResult mAvailableLotNoResult;

    ListView historyList;
    TransferOutLotEntryListArrayAdapter historyAdapter;
    ArrayList<TransferShipmentEntry> temp_list;

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
    Button btnAddEntry;
    Button btnScan;
    EditText scanText;

    private String mTransferNo;
    private String mItemNo;
    private int mLineNo;
    private float mTransferOrderQuantityBase;
    private float mTransferOutstandingQuantityBase;
    private float mTransferShipQuantityBase;

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
        return R.layout.activity_transfer_out_lot_entry;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_out_lot_entry);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Item No.: " + DataManager.getInstance().transitionTransferOutData.getItemNo());

        textProductionDate = (EditText) findViewById(R.id.editProductionDate);
        textExpiryDate = (EditText) findViewById(R.id.editExpiryDate);
        textQuantity = (EditText) findViewById(R.id.editQtyReceived);
        textLotNumber = (EditText) findViewById(R.id.editLotNumber);

        mLinearLayoutManual = (LinearLayout) findViewById(R.id.linearLayoutManual);
        mLinearLayoutScan = (LinearLayout) findViewById(R.id.linearLayoutScan);

        entryIndexCounter = 0;
        scannedLotNo = "";
        scannedSerialNo = "";

        historyList = (ListView) findViewById(R.id.itemList);
        temp_list = new ArrayList<TransferShipmentEntry>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String isScanning = extras.getString("IS_SCANNING");
            //The key argument here must match that used in the other activity

            mTransferNo = DataManager.getInstance().TransferNo;

            if (isScanning.contains("YES")) {
                isScanned = true;
                scannedString = extras.getString("SCAN_CONTENT");
            }

            if (DataManager.getInstance().transitionTransferOutData.getQuantityBase() != null) {
                mTransferOrderQuantityBase = Float.parseFloat(DataManager.getInstance().transitionTransferOutData.getQuantityBase());
            }

            if (DataManager.getInstance().transitionTransferOutData.getOutstandingQuantityBase() != null) {
                mTransferOutstandingQuantityBase = Float.parseFloat(DataManager.getInstance().transitionTransferOutData.getOutstandingQuantityBase());
            }

            if (DataManager.getInstance().transitionTransferOutData.getQuantityInTransitBase() != null) {
                mTransferShipQuantityBase = Float.parseFloat(DataManager.getInstance().transitionTransferOutData.getQuantityToShipBase());
            }

            mLineNo = Integer.parseInt(DataManager.getInstance().transitionTransferOutData.getLineNo());
            mItemNo = DataManager.getInstance().transitionTransferOutData.getItemNo();

            Log.d("PO ITEM DETAILS", "PO: " + mTransferNo.toString());
            Log.d("PO ITEM DETAILS", Float.toString(mTransferOrderQuantityBase));
            Log.d("PO ITEM DETAILS", Float.toString(mTransferOutstandingQuantityBase));
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
                        DataManager.getInstance().AddSerialRecord(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_SHIPMENT, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);
                        Toast.makeText(mContext, "Lot No exists! Quantity merged!", Toast.LENGTH_LONG).show();
                    } else {

                        if (mQuantity.length() == 0 || mQuantity.equals("0.0")) {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                        } else {
                            temp_list.add(new TransferShipmentEntry(temp_list.size() + 1, mProductionDate, mExpiryDate, mQuantity, mLotNumber, scannedSerialNo, entryIndexCounter, ConfigurationManager.DATA_NO_SERVER_ENTRY_INDEX, ConfigurationManager.DATA_UPLOADED_FALSE));

                            TransferShipmentItemLineEntries temp_entry = new TransferShipmentItemLineEntries(mTransferNo, mItemNo, mLineNo,
                                    mProductionDate, mExpiryDate, mQuantity, mLotNumber, scannedSerialNo, "", entryIndexCounter, ConfigurationManager.DATA_UPLOADED_FALSE, System.currentTimeMillis());

                            temp_entry.save();

                            DataManager.getInstance().AddSerialRecord(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_SHIPMENT, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);

                            entryIndexCounter++;


                            Log.d("SUGARORM", "ItemNo: " + mItemNo);
                            List<TransferShipmentItemLineEntries> entryList = SugarRecord.find(TransferShipmentItemLineEntries.class, "item_no = ?", mItemNo);

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
                    mPostTransferShipmentItemEntriesTask = new PostTransferShipmentItemEntriesTask((Activity) mContext);
                    mPostTransferShipmentItemEntriesTask.execute((Void) null);
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
                scanText.requestFocus();
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
                scanText.requestFocus();
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
                final ArrayList<AvailableLotNoResultData> temp_item_list = new ArrayList<AvailableLotNoResultData>(mAvailableLotNoResult.AvailableLotNoData);
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

        mGetTransferShipmentLineLotEntriesTask = new GetTransferShipmentLineLotEntriesTask(this);
        mGetTransferShipmentLineLotEntriesTask.execute((Void) null);
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
        scanText.requestFocus();
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
        /*Log.d("DISABLE", String.valueOf(DataManager.getInstance().transitionTransferOutData.isItemExpireDateRequired()));
        Log.d("DISABLE", String.valueOf(DataManager.getInstance().transitionTransferOutData.isItemProductionDateRequired()));
        Log.d("DISABLE", String.valueOf(DataManager.getInstance().transitionTransferOutData.isItemLotTrackingRequired()));
        Log.d("DISABLE", String.valueOf(DataManager.getInstance().transitionTransferOutData.isItemLotNoAutoAssigned()));*/

        /*if(!DataManager.getInstance().transitionTransferOutData.isItemExpireDateRequired())
        {
            textExpiryDate.setBackgroundColor(Color.GRAY);
            textExpiryDate.setEnabled(false);
        }*/

        /*if(!DataManager.getInstance().transitionTransferOutData.isItemProductionDateRequired())
        {
            textProductionDate.setBackgroundColor(Color.GRAY);
            textProductionDate.setEnabled(false);
        }*/

        /*if(!DataManager.getInstance().transitionTransferOutData.isItemLotTrackingRequired())
        {
            textLotNumber.setBackgroundColor(Color.GRAY);
            textLotNumber.setEnabled(false);  // For transfer, lot number will always be enabled
        }*/

        if (DataManager.getInstance().transitionTransferOutData.isItemLotNoAutoAssigned()) {
            textLotNumber.setBackgroundColor(Color.WHITE);
            textLotNumber.setEnabled(true);  // For transfer, lot number will always be disabled. Can only select from list or scan
        }
    }

    private float computeCurrentBalance() {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            if (!temp_list.get(i).IsUploaded)    // Those not uploaded are those that should be deducted from balance
            {
                total += Float.parseFloat(temp_list.get(i).QtyToShip);
            }
        }

        return mTransferOutstandingQuantityBase - total - mTransferShipQuantityBase;
    }

    private void prepareAddEntryParameters() {
        mQuantity = textQuantity.getText().toString();

        if (DataManager.getInstance().transitionTransferOutData.isItemProductionDateRequired()) {
            mProductionDate = textProductionDate.getText().toString();
        } else {
            mProductionDate = textProductionDate.getText().toString();
//            mProductionDate = "";
        }

        if (DataManager.getInstance().transitionTransferOutData.isItemExpireDateRequired()) {
            mExpiryDate = textExpiryDate.getText().toString();
        } else {
            mExpiryDate = textExpiryDate.getText().toString();
//            mExpiryDate = "";
        }

        mLotNumber = textLotNumber.getText().toString().toUpperCase();
        /*
        if (DataManager.getInstance().transitionTransferOutData.isItemLotTrackingRequired() && !DataManager.getInstance().transitionTransferOutData.isItemLotNoAutoAssigned()) {

        } else {
            mLotNumber = "";
        }*/
    }

    private boolean mergeSameLotEntryQuantity(String lotNo, String quantity, String prodDate) {
        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(lotNo) && temp_list.get(i).ProductionDate.toString().equals(prodDate)) {
                if (!temp_list.get(i).IsUploaded) {
                    temp_list.get(i).QtyToShip = Float.toString(Float.parseFloat(temp_list.get(i).QtyToShip) + Float.parseFloat(quantity));

                    List<TransferShipmentItemLineEntries> entryList = SugarRecord.find(TransferShipmentItemLineEntries.class, "lot_number = ? and production_date = ?", lotNo, prodDate);
                    if (entryList.size() > 0) {
                        entryList.get(0).setQtyToShip(temp_list.get(i).QtyToShip);
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

    private boolean validateEntry2() {
        if (!checkSerialExist()) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_SERIALNO, NotificationManager.ALERT_MSG_INVALID_SERIALNO);
            return false;
        }
        if (!checkRequiredFields2()) {
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

    private boolean checkLotNoExist() {
        if (!DataManager.getInstance().transitionTransferOutData.isItemLotNoAutoAssigned())     // if lot no is not auto-assigned, lot must exist within the available lot list
        {
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

    }

    //CHECK WHETHER THE SCANNED QUANTITY IS GREATER THAN THE QUANTITY AVAILABLE FOR THAT LOT
    private boolean checkLotQuantityOverflow(String scannedQuantity) {
        if (DataManager.getInstance().transitionTransferOutData.isItemLotNoAutoAssigned()) {
            return true;
        }

        float total = 0;
        float max_lot_qty = 0;

        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(mLotNumber) && !temp_list.get(i).IsUploaded)   // disregard those that have already been uploaded
            {
                total += Float.parseFloat(temp_list.get(i).QtyToShip);
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
        if (total > max_lot_qty) {
            Log.d("QTY CHECK", "OVERFLOW!!");
            return false;
        }

        return true;
    }

    // CHECK WHETHER QUANTITY IS GREATER THAN THE OUTSTANDING QUANTITY
    private boolean checkQuantity(String scannedQuantity) {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            total += Float.parseFloat(temp_list.get(i).QtyToShip);
        }

        total += Float.parseFloat(scannedQuantity);

        Log.d("QTY CHECK", Float.toString(total) + " " + Float.toString(mTransferOutstandingQuantityBase));
        if (total > mTransferOutstandingQuantityBase) {
            Log.d("VALIDATION OVERFLOW", Float.toString(mTransferOutstandingQuantityBase));
            Log.d("VALIDATION OVERFLOW", Float.toString(total));

            return false;
        } else {
            return true;
        }
    }

    private boolean checkSerialExist() {
        if (scannedSerialNo.isEmpty()) {
            return true;
        }

        List<BarcodeSerialRecords> tempSerialList = DataManager.getInstance().GetSerialRecords(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_SHIPMENT, mItemNo, Integer.toString(mLineNo), mLotNumber);
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
        /*if(DataManager.getInstance().transitionTransferOutData.isItemProductionDateRequired())
        {
            if(mProductionDate.length() == 0 || mProductionDate.length() < 10)
            {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_PRODUCTION_DATE, NotificationManager.ALERT_MSG_INVALID_PRODUCTION_DATE);
                return false;
            }
        }*/
        if (DataManager.getInstance().transitionTransferOutData.isItemExpireDateRequired()) {
            if (mExpiryDate.length() == 0 || mExpiryDate.length() < 10) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_EXPIRY_DATE, NotificationManager.ALERT_MSG_INVALID_EXPIRY_DATE);
                return false;
            }
        }
        if (mQuantity.length() == 0 || mQuantity.equals("0") || mQuantity.equals("0.0")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
            return false;
        }
        if (DataManager.getInstance().transitionTransferOutData.isItemLotTrackingRequired()) {
            if (!DataManager.getInstance().transitionTransferOutData.isItemLotNoAutoAssigned()) {
                if (mLotNumber.length() == 0) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOT_NO, NotificationManager.ALERT_MSG_INVALID_LOT_NO);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkRequiredFields2() {
        /*if(DataManager.getInstance().transitionTransferOutData.isItemProductionDateRequired())
        {
            if(mProductionDate.length() == 0 || mProductionDate.length() < 10)
            {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_PRODUCTION_DATE, NotificationManager.ALERT_MSG_INVALID_PRODUCTION_DATE);
                return false;
            }
        }
        if(DataManager.getInstance().transitionTransferOutData.isItemExpireDateRequired())
        {
            if(mExpiryDate.length() == 0 || mExpiryDate.length() < 10)
            {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_EXPIRY_DATE, NotificationManager.ALERT_MSG_INVALID_EXPIRY_DATE);
                return false;
            }
        }*/
        if (mQuantity.length() == 0 || mQuantity.equals("0") || mQuantity.equals("0.0")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
            return false;
        }
        if (DataManager.getInstance().transitionTransferOutData.isItemLotTrackingRequired()) {
            if (!DataManager.getInstance().transitionTransferOutData.isItemLotNoAutoAssigned()) {
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
        tempTextLabel.setText(DataManager.getInstance().transitionTransferOutData.getItemDescription());

        tempTextLabel = (TextView) findViewById(R.id.lblItemIDUom);
        tempTextLabel.setText("Item No: " + DataManager.getInstance().transitionTransferOutData.getItemNo() + DataConverter.CheckNullString(DataManager.getInstance().transitionTransferOutData.getBaseUom(), " / "));

        tempTextLabel = (TextView) findViewById(R.id.lblPurchaseQuantity);
        tempTextLabel.setText("Ord Qty: " + DataManager.getInstance().transitionTransferOutData.getQuantityBase());

        tempTextLabel = (TextView) findViewById(R.id.lblBalanceQuantity);
        tempTextLabel.setText("Bal Qty: " + DataManager.getInstance().transitionTransferOutData.getOutstandingQuantityBase());

        tempTextLabel = (TextView) findViewById(R.id.lblTransitQuantity);
        tempTextLabel.setText("Transit Qty: " + DataManager.getInstance().transitionTransferOutData.getQuantityInTransitBase());

        tempTextLabel = (TextView) findViewById(R.id.lblShipQuantity);
        tempTextLabel.setText("Qty to Ship: " + DataManager.getInstance().transitionTransferOutData.getQuantityToShipBase());

    }

    /*
    private void updateDisplayHeader()
    {
        float total = 0;
        float total_not_uploaded = 0;
        for(int i = 0; i < temp_list.size(); i++)
        {
            total += Float.parseFloat(temp_list.get(i).QtyToShip);
            if(!temp_list.get(i).IsUploaded)
            {
                total_not_uploaded += Float.parseFloat(temp_list.get(i).QtyToShip);
            }
        }

        float currBalance = mTransferOutstandingQuantityBase - total_not_uploaded;

        TextView tempTextLabel = (TextView) findViewById(R.id.lblShipQuantity);
        tempTextLabel.setText("Qty to Ship: " + Float.toString(total));


        //tempTextLabel = (TextView) findViewById(R.id.lblBalanceQuantity);
        //tempTextLabel.setText("Bal Qty: " + Float.toString(currBalance));

    }*/

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
                if (DataManager.getInstance().transitionTransferOutData.isItemExpireDateRequired() && !splitString[4].isEmpty()) {
                    calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                    textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));
                }
                calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));
            }

            /*if(!splitString[3].isEmpty()) {
            *//*if(DataManager.getInstance().transitionTransferOutData.isItemProductionDateRequired() && !splitString[3].isEmpty())
            {
                calProductionDate.set(Integer.parseInt(splitString[3].substring(0, 4)), Integer.parseInt(splitString[3].substring(4, 6)), Integer.parseInt(splitString[3].substring(6, 8)));

                textProductionDate.setText(splitString[3].substring(6, 8) + "/" + splitString[3].substring(4, 6) + "/" + splitString[3].substring(0, 4));
            }*//*
                calProductionDate.set(Integer.parseInt(splitString[3].substring(0, 4)), Integer.parseInt(splitString[3].substring(4, 6)), Integer.parseInt(splitString[3].substring(6, 8)));

                textProductionDate.setText(splitString[3].substring(6, 8) + "/" + splitString[3].substring(4, 6) + "/" + splitString[3].substring(0, 4));
            }*/

            if (DataManager.getInstance().transitionTransferOutData.isItemLotTrackingRequired()) {
                if (!DataManager.getInstance().transitionTransferOutData.isItemLotNoAutoAssigned()) {
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

            enableTextFieldsEditing(true);
        }

        Log.d("PO ITEM DETAILS", calProductionDate.toString());
        Log.d("PO ITEM DETAILS", calExpiryDate.toString());
    }

    public class PostTransferShipmentItemEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        List<TransferShipmentItemTrackingLineParameter> mTransferShipmentItemTrackingLineParameter;
        BaseResult mReturnBaseResult;

        PostTransferShipmentItemEntriesTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mTransferShipmentItemTrackingLineParameter = new ArrayList<TransferShipmentItemTrackingLineParameter>();

            for (int i = 0; i < temp_list.size(); i++) {
                if (!temp_list.get(i).IsUploaded) {
                    String production_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ProductionDate);
                    String expiry_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ExpiryDate);

                    TransferShipmentItemTrackingLineParameter temp_TransferShipmentItemTrackingLineParameter = new TransferShipmentItemTrackingLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mTransferNo, mLineNo, production_date, expiry_date, temp_list.get(i).LotNo, "", Float.parseFloat(temp_list.get(i).QtyToShip));
                    mTransferShipmentItemTrackingLineParameter.add(temp_TransferShipmentItemTrackingLineParameter);

                }
            }

            Gson gson = new Gson();
            String json = gson.toJson(mTransferShipmentItemTrackingLineParameter);
            Log.d("JSON", json);

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().PostTransferShipmentItemTrackingLine(mTransferShipmentItemTrackingLineParameter);

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

                        List<TransferShipmentItemLineEntries> dataList = SugarRecord.find(TransferShipmentItemLineEntries.class, "line_no = ?", String.valueOf(mLineNo));
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).delete();
                        }

                        // REMOVE BARCODE SERIAL LIST
                        DataManager.getInstance().DeleteSerialRecord(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_SHIPMENT, mItemNo, Integer.toString(mLineNo));

                        Intent intent = new Intent(getBaseContext(), TransferOutLineActivity.class);
                        intent.putExtra("TRANSFER_ORDER_NO", mTransferNo);
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

    public class GetTransferShipmentLineLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        TransferShipmentLineLotEntriesSearchParameter mTransferShipmentLineLotEntriesSearchParameter;
        TransferShipmentLineLotEntriesResult mTransferShipmentLineLotEntriesResult;

        GetTransferShipmentLineLotEntriesTask(Activity activity) {

            mActivity = activity;

            mTransferShipmentLineLotEntriesSearchParameter = new TransferShipmentLineLotEntriesSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mTransferNo, mLineNo);

            Gson gson = new Gson();
            String json = gson.toJson(mTransferShipmentLineLotEntriesSearchParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<TransferShipmentLineLotEntriesResult> call = mApp.getNavBrokerService().GetTransferShipmentLineLotEntries(mTransferShipmentLineLotEntriesSearchParameter);

                mTransferShipmentLineLotEntriesResult = call.execute().body();

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
                    if (mTransferShipmentLineLotEntriesResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        if (mTransferShipmentLineLotEntriesResult != null) {
                            if (mTransferShipmentLineLotEntriesResult.getTransferShipmentLineLotEntriesResultData() != null
                                    && mTransferShipmentLineLotEntriesResult.getTransferShipmentLineLotEntriesResultData().size() != 0) {
                                for (int i = 0; i < mTransferShipmentLineLotEntriesResult.getTransferShipmentLineLotEntriesResultData().size(); i++) {
                                    TransferShipmentLineLotEntriesResultData temp_result = mTransferShipmentLineLotEntriesResult.getTransferShipmentLineLotEntriesResultData().get(i);
                                    String temp_prod_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_result.getProductionDate());
                                    String temp_expire_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_result.getExpireDate());
                                    TransferShipmentEntry temp_entry = new TransferShipmentEntry(temp_list.size() + 1, temp_prod_date, temp_expire_date, Float.toString(temp_result.getQuantityToShip()), temp_result.getLotNo().toUpperCase(), "", ConfigurationManager.DATA_NO_LOCAL_ENTRY_INDEX, temp_result.getEntryNo(), ConfigurationManager.DATA_UPLOADED_TRUE);
                                    temp_list.add(temp_entry);

                                }
                            }
                        }

                        //check whether there are entries in the local database and append them to the list
                        List<TransferShipmentItemLineEntries> entryList = SugarRecord.find(TransferShipmentItemLineEntries.class, "line_no = ?", Integer.toString(mLineNo));

                        for (int i = 0; i < entryList.size(); i++) {
                            TransferShipmentEntry temp_entry = new TransferShipmentEntry(temp_list.size() + 1, entryList.get(i).getProductionDate(), entryList.get(i).getExpiryDate(), entryList.get(i).getQtyToShip(), entryList.get(i).getLotNumber(), entryList.get(i).getSerialNo(), entryList.get(i).getEntryIndexNo(), ConfigurationManager.DATA_NO_SERVER_ENTRY_INDEX, ConfigurationManager.DATA_UPLOADED_FALSE);
                            temp_list.add(temp_entry);
                        }

                        historyAdapter = new TransferOutLotEntryListArrayAdapter(mContext, temp_list);

                        historyList.setAdapter(historyAdapter);

                        mQueryTransferShipmentAvailableLotNoParameter = new QueryTransferShipmentAvailableLotNoParameter((Activity) mContext);
                        mQueryTransferShipmentAvailableLotNoParameter.execute((Void) null);

                        UpdateDisplayQuantity();
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mTransferShipmentLineLotEntriesResult.getMessage());
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

    public class QueryTransferShipmentAvailableLotNoParameter extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        TransferShipmentAvailableLotNoParameter mTransferShipmentAvailableLotNoParameter;


        QueryTransferShipmentAvailableLotNoParameter(Activity activity) {

            mActivity = activity;

            mTransferShipmentAvailableLotNoParameter = new TransferShipmentAvailableLotNoParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mTransferNo, mLineNo);

            Gson gson = new Gson();
            String json = gson.toJson(mTransferShipmentAvailableLotNoParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<AvailableLotNoResult> call = mApp.getNavBrokerService().QueryTransferShipmentAvailableLotNo(mTransferShipmentAvailableLotNoParameter);

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

    public class RemoveTransferShipmentLineLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        RemoveTransferShipmentLotEntryParameter mRemoveTransferShipmentLotEntryParameter;
        BaseResult mReturnBaseResult;

        int mPositionDelete;
        float deductValue;

        RemoveTransferShipmentLineLotEntriesTask(Activity activity, String TransferNo, int LineNo, int EntryNo, int position, String lotSerialNo, float quantityToDeduct) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_DELETE_DATA, NotificationManager.DIALOG_MSG_DELETE);

            mActivity = activity;

            int SerialOrLotFlag = 0;
            if (DataManager.getInstance().transitionTransferOutData.isItemLotTrackingRequired()) {
                SerialOrLotFlag = 1;
            }

            mRemoveTransferShipmentLotEntryParameter = new RemoveTransferShipmentLotEntryParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), TransferNo, LineNo, SerialOrLotFlag, lotSerialNo, quantityToDeduct);

            mPositionDelete = position;
            deductValue = quantityToDeduct;

            Gson gson = new Gson();
            String json = gson.toJson(mRemoveTransferShipmentLotEntryParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().RemoveTransferShipmentLotEntry(mRemoveTransferShipmentLotEntryParameter);

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
                        mTransferShipQuantityBase = mTransferShipQuantityBase - deductValue;
                        UpdateDisplayQuantity();

                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_REMOVE_SUCCESSFUL, NotificationManager.MSG_REMOVE_MESSAGE);

                        mQueryTransferShipmentAvailableLotNoParameter = new QueryTransferShipmentAvailableLotNoParameter((Activity) mContext);
                        mQueryTransferShipmentAvailableLotNoParameter.execute((Void) null);

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
        Log.d("SERVER REMOVE", "TO: " + mTransferNo + " LineNo: " + mLineNo + " position: " + position + " EntryNO: " + entryNo);
        mRemoveTransferShipmentLineLotEntriesTask = new RemoveTransferShipmentLineLotEntriesTask((Activity) mContext, mTransferNo, mLineNo, entryNo, position, lotSerialNo, quantityToDeduct);
        mRemoveTransferShipmentLineLotEntriesTask.execute((Void) null);
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
                Intent intent = new Intent(getBaseContext(), TransferOutLineActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("TRANSFER_ORDER_NO", mTransferNo);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getBaseContext(), TransferOutLineActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("TRANSFER_ORDER_NO", mTransferNo);
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
        List<TransferShipmentItemLineEntries> entryList = SugarRecord.find(TransferShipmentItemLineEntries.class, "line_no = ?", Integer.toString(mLineNo));
        if (entryList.size() > 0) {
            for (int i = 0; i < entryList.size(); i++) {
                entryList.get(i).delete();
            }
        }

        // REMOVE BARCODE SERIAL LIST
        DataManager.getInstance().DeleteSerialRecord(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_SHIPMENT, mItemNo, Integer.toString(mLineNo));

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
        DataManager.getInstance().DeleteSerialRecord(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_SHIPMENT, mItemNo, Integer.toString(mLineNo), mLotNo);

        UpdateDisplayQuantity();
    }

    public void UpdateDisplayQuantity() {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            total += Float.parseFloat(temp_list.get(i).QtyToShip);
            TextView tempTextLabel = (TextView) findViewById(R.id.lblShipQuantity);
            tempTextLabel.setText("Qty to Ship: " + Float.toString(total));
        }
    }
}
