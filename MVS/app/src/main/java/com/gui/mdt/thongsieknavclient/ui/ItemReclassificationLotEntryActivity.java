package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
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
import com.gui.mdt.thongsieknavclient.adapters.itemreclass.itemReclassLotEntryListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.AvailableLotNoResult;
import com.gui.mdt.thongsieknavclient.model.AvailableLotNoResultData;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineLotEntriesParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineLotEntriesResultData;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassListResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassListResultData;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassificationAvailableLotNoParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassificationEntry;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassificationListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.RemoveItemReclassLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialRecords;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.ItemReclassItemLineEntries;
import com.orm.SugarRecord;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;


public class ItemReclassificationLotEntryActivity extends BaseActivity {

    RemoveItemReclassLineLotEntriesTask mRemoveItemReclassLineLotEntriesTask = null;
    PostItemReclassItemEntriesTask mPostItemReclassItemEntriesTask = null;
    GetItemReclassLineLotEntriesTask mGetItemReclassLineLotEntriesTask = null;
    QueryItemReclassAvailableLotNoParameter mQueryItemReclassAvailableLotNoParameter = null;
    GetItemReclassListTask mGetItemReclassListTask = null;

    Context mContext;
    private NavClientApp mApp;

    private ListView historyList;
    private ListView lotNumberList;
    private Calendar calProductionDate;
    private Calendar calExpiryDate;
    private EditText textProductionDate;
    private EditText textExpiryDate;
    private EditText textQuantity;
    private EditText textLotNumber;
    private EditText scanText;
    private Button btnAddEntry;
    private Button btnScan;

    private String mDocNo;
    private String mItemNo;
    private String mPostDate;
    private String mProductionDate;
    private String mExpiryDate;
    private String mUom;
    private String mLocCode;
    private String mNewLocCode;
    private String mBinCode;
    private String mNewBinCode;
    private String mLotNumber;
    private String mCreatedBy;
    private String scannedString;
    private String scannedLotNo;
    private String scannedSerialNo;
    private int entryIndexCounter;
    private int mHeaderLineNo;
    private int mLineNo;
    private float mQuantity = 0f;
    private float mQuantityBased;
    private boolean isStarted = false;
    private boolean mIsShowBinCode = false;
    private boolean mIsShowNewBinCode = false;
    private boolean isScanned = false;
    private boolean isInterfaceScanning = false;

    LinearLayout mLinearLayoutManual;
    LinearLayout mLinearLayoutScan;

    AvailableLotNoResult mAvailableLotNoResult;
    itemReclassLotEntryListArrayAdapter historyAdapter;
    ArrayList<ItemReclassificationEntry> temp_list;
    LotNumberListArrayAdapter lotNumberArrayAdapter;

    protected int getLayoutResource() {
        return R.layout.activity_item_reclassification_lot_entry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_reclassification_lot_entry);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Item No: " + DataManager.getInstance().transitionItemReclassLinesData.getItemNo());

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
        temp_list = new ArrayList<ItemReclassificationEntry>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String isScanning = extras.getString("IS_SCANNING");
            mDocNo = extras.getString("DOCUMENT_NO");

            mGetItemReclassListTask = new GetItemReclassListTask(this);
            mGetItemReclassListTask.execute((Void) null);

            if (isScanning.contains("YES")) {
                isScanned = true;
                scannedString = extras.getString("SCAN_CONTENT");
            }

//            if (DataManager.getInstance().itemReclassListResultData.getLocationCode() != null) {
//                mLocCode = DataManager.getInstance().itemReclassListResultData.getLocationCode();
//            }
//
//            if (DataManager.getInstance().itemReclassListResultData.getNewLocationCode() != null) {
//                mNewLocCode = DataManager.getInstance().itemReclassListResultData.getNewLocationCode();
//            }
//
//            if (DataManager.getInstance().itemReclassListResultData.getCreatedBy() != null) {
//                mCreatedBy = DataManager.getInstance().itemReclassListResultData.getCreatedBy();
//            }
//
//            if (DataManager.getInstance().itemReclassListResultData.getPostingDate() != null) {
//                mPostDate = DataManager.getInstance().itemReclassListResultData.getPostingDate();
//            }

            if (DataManager.getInstance().transitionItemReclassLinesData.getUom() != null) {
                mUom = DataManager.getInstance().transitionItemReclassLinesData.getUom();
            }

            if (DataManager.getInstance().transitionItemReclassLinesData.getQuantity() != 0) {
//                mQuantity = DataManager.getInstance().transitionItemReclassLinesData.getQuantity();
            }

            if (DataManager.getInstance().transitionItemReclassLinesData.getQuantityBase() != 0) {
                mQuantityBased = DataManager.getInstance().transitionItemReclassLinesData.getQuantityBase();
            }

            mHeaderLineNo = DataManager.getInstance().ItemReclassHeaderLineNo;
            mLineNo = DataManager.getInstance().transitionItemReclassLinesData.getLineNo();
            mDocNo = DataManager.getInstance().ItemReclassificationNo;
            mItemNo = DataManager.getInstance().transitionItemReclassLinesData.getItemNo();

            Log.d("ITEM DETAILS", "HEADER_LINE: " + mHeaderLineNo);
            Log.d("ITEM DETAILS", "LINE_NO: " + String.valueOf(mLineNo));
            Log.d("ITEM DETAILS", "DOC_NO: " + mDocNo);
            Log.d("ITEM DETAILS", "ITEM_NO: " + mItemNo);
        }

        calProductionDate = Calendar.getInstance();
        calExpiryDate = Calendar.getInstance();

        enableTextFieldsEditing(true);
        //disableTextFields();
        initializeDisplayHeader();
        initializeDataEntry();      // for entries that are scanned from Layer 2
        initializeDateFields();
        SugarRecord.deleteAll(ItemReclassItemLineEntries.class);
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
                    if (mergeSameLotEntryQuantity(mLotNumber, String.valueOf(mQuantity), mProductionDate)) {

                        ItemReclassItemLineEntries temp_entry = new ItemReclassItemLineEntries(mDocNo, mItemNo, mLineNo,
                                mProductionDate, mExpiryDate, String.valueOf(mQuantity), String.valueOf(mQuantity), mLotNumber, scannedSerialNo, "", entryIndexCounter, ConfigurationManager.DATA_UPLOADED_FALSE, ConfigurationManager.DATA_UPLOADED_FALSE, System.currentTimeMillis());
                        temp_entry.save();

                        DataManager.getInstance().AddSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);
                        Toast.makeText(mContext, "Lot No exists! Quantity merged!", Toast.LENGTH_LONG).show();
                    } else {

                        if (mQuantity == 0 || mQuantity == 0.0) {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                        } else {
                            temp_list.add(new ItemReclassificationEntry(mProductionDate, mExpiryDate, String.valueOf(mQuantity), mLotNumber, scannedSerialNo, entryIndexCounter, ConfigurationManager.DATA_NO_SERVER_ENTRY_INDEX, temp_list.size() + 1, ConfigurationManager.DATA_UPLOADED_FALSE));
                            ItemReclassItemLineEntries temp_entry = new ItemReclassItemLineEntries(mDocNo, mItemNo, mLineNo,
                                    mProductionDate, mExpiryDate, String.valueOf(mQuantity), String.valueOf(mQuantity), mLotNumber, scannedSerialNo, "", entryIndexCounter, ConfigurationManager.DATA_UPLOADED_FALSE, ConfigurationManager.DATA_UPLOADED_TRUE, System.currentTimeMillis());
                            temp_entry.save();

                            DataManager.getInstance().AddSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);
                            entryIndexCounter++;

                            Log.d("SUGARORM", "ItemNo: " + mItemNo);
                            List<ItemReclassItemLineEntries> entryList = SugarRecord.find(ItemReclassItemLineEntries.class, "item_no = ?", mItemNo);

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
                    mPostItemReclassItemEntriesTask = new PostItemReclassItemEntriesTask((Activity) mContext);
                    mPostItemReclassItemEntriesTask.execute((Void) null);
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

                        String AvailableQty = String.valueOf(temp.getAvailableQuantity());
                        textQuantity.setText(AvailableQty);

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

        mGetItemReclassLineLotEntriesTask = new GetItemReclassLineLotEntriesTask(this);
        mGetItemReclassLineLotEntriesTask.execute((Void) null);
    }

    private void clearInputFields() {

        textProductionDate.setText("");
        textExpiryDate.setText("");
        textQuantity.setText("");
        textLotNumber.setText("");
        scannedString = "";
        scannedLotNo = "";
        scannedSerialNo = "";
        scanText.requestFocus();
        isScanned = false;
    }

    private void enableTextFieldsEditing(boolean isEnable) {
        if (isEnable) {
            textExpiryDate.setEnabled(true);
            textProductionDate.setEnabled(true);
            textQuantity.setEnabled(true);
        } else {
            textExpiryDate.setEnabled(false);
            textProductionDate.setEnabled(false);
            textQuantity.setEnabled(false);
        }
    }

    private void prepareAddEntryParameters() {
        if (!textQuantity.getText().toString().isEmpty()) {
            mQuantity = Float.parseFloat(textQuantity.getText().toString());
        }

        if (DataManager.getInstance().transitionItemReclassLinesData.isItemProductionDateRequired()) {
            mProductionDate = textProductionDate.getText().toString();
        } else {
            mProductionDate = textProductionDate.getText().toString();
        }

        if (DataManager.getInstance().transitionItemReclassLinesData.isItemExpireDateRequired()) {
            mExpiryDate = textExpiryDate.getText().toString();
        } else {
            mExpiryDate = textExpiryDate.getText().toString();
        }

        mLotNumber = textLotNumber.getText().toString().toUpperCase();
    }

    private boolean mergeSameLotEntryQuantity(String lotNo, String prevQuantity, String prodDate) {
        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(lotNo) && temp_list.get(i).ProductionDate.toString().equals(prodDate)) {

                temp_list.get(i).QtyToShip = Float.toString(Float.parseFloat(temp_list.get(i).QtyToShip) + Float.parseFloat(prevQuantity));
                temp_list.get(i).IsUploaded = ConfigurationManager.DATA_UPLOADED_FALSE;

                List<ItemReclassItemLineEntries> entryList = SugarRecord.find(ItemReclassItemLineEntries.class, "lot_number = ? and production_date = ?", lotNo, prodDate);
                if (entryList.size() > 0) {
                    entryList.get(0).setQtyToShip(temp_list.get(i).QtyToShip);
                    entryList.get(0).setUploaded(temp_list.get(i).IsUploaded);
                    entryList.get(0).save();
                }

                return true;
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
        if (!checkLotQuantityOverflow(String.valueOf(mQuantity))) {
            NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_quantity_exceeds), getResources().getString(R.string.notification_msg_quantity_exceeds));
            return false;
        }
        return true;
    }

    private boolean checkLotNoExist() {
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
    }

    //CHECK WHETHER THE SCANNED QUANTITY IS GREATER THAN THE QUANTITY AVAILABLE FOR THAT LOT
    private boolean checkLotQuantityOverflow(String scannedQuantity) {
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

        Log.d("QTY CHECK", Float.toString(total) + " " + Float.toString(mQuantityBased));
        if (total > mQuantityBased) {
            Log.d("VALIDATION OVERFLOW", Float.toString(mQuantityBased));
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

        List<BarcodeSerialRecords> tempSerialList = DataManager.getInstance().GetSerialRecords(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS, mItemNo, Integer.toString(mLineNo), mLotNumber);
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
        if (DataManager.getInstance().transitionItemReclassLinesData.isItemExpireDateRequired()) {
            if (mExpiryDate.length() == 0 || mExpiryDate.length() < 10) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_EXPIRY_DATE, NotificationManager.ALERT_MSG_INVALID_EXPIRY_DATE);
                return false;
            }
        }
        if (mQuantity == 0 || mQuantity == 0.0) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
            return false;
        }
        if (DataManager.getInstance().transitionItemReclassLinesData.isItemLotTrackingRequired()) {
            if (!DataManager.getInstance().transitionItemReclassLinesData.isItemSNRequired()) {
                if (mLotNumber.length() == 0) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOT_NO, NotificationManager.ALERT_MSG_INVALID_LOT_NO);
                    return false;
                }
            }
        }
        return true;
    }

    private void initializeDisplayHeader() {

        TextView tempTextLabel = (TextView) findViewById(R.id.lblItemDesc);
        tempTextLabel.setText(DataManager.getInstance().transitionItemReclassLinesData.getItemDescription());

        tempTextLabel = (TextView) findViewById(R.id.lblItemNoBaseUOM);
        tempTextLabel.setText(DataManager.getInstance().transitionItemReclassLinesData.getItemNo() + DataConverter.CheckNullString(DataManager.getInstance().transitionItemReclassLinesData.getUom(), "/"));

        tempTextLabel = (TextView) findViewById(R.id.lblReqQty);
        tempTextLabel.setText("Req Qty: " + DataManager.getInstance().transitionItemReclassLinesData.getQuantity());

        tempTextLabel = (TextView) findViewById(R.id.lblShipQty);
        tempTextLabel.setText("Qty to Trn: " + DataManager.getInstance().transitionItemReclassLinesData.getQuantityBase());
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

    private void initializeDataEntry() {
        if (isScanned) {
            String[] splitString = scannedString.split("\\|");

            if (DataManager.getInstance().transitionItemReclassLinesData.isItemExpireDateRequired() && !splitString[4].isEmpty()) {
                calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));
            }

            if (DataManager.getInstance().transitionItemReclassLinesData.isItemLotTrackingRequired()) {
                textLotNumber.setText(splitString[3]);
            }

            textQuantity.setText(splitString[1]);
            //scannedLotNo = splitString[3];
            scannedSerialNo = splitString[5];

            enableTextFieldsEditing(true);
        }

        Log.d("PO ITEM DETAILS", calProductionDate.toString());
        Log.d("PO ITEM DETAILS", calExpiryDate.toString());
    }

    public class PostItemReclassItemEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        List<ItemReclassTrackingLineParameter> mItemReclassItemTrackingLineParameter;
        BaseResult mReturnBaseResult;

        PostItemReclassItemEntriesTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mItemReclassItemTrackingLineParameter = new ArrayList<ItemReclassTrackingLineParameter>();

            ItemReclassTrackingLineParameter temp_TransferShipmentItemTrackingLineParameter;
            String lotNo = "";
            String tmpQty = "";
            for (int i = 0; i < temp_list.size(); i++) {
                if (!temp_list.get(i).IsUploaded) {
                    String production_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ProductionDate);
                    String expiry_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ExpiryDate);

//                    ItemReclassTrackingLineParameter temp_TransferShipmentItemTrackingLineParameter = new ItemReclassTrackingLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mLineNo, production_date, expiry_date, temp_list.get(i).LotNo, "", Float.parseFloat(temp_list.get(i).QtyToShip) - mQuantity, isScanned);
//                    mItemReclassItemTrackingLineParameter.add(temp_TransferShipmentItemTrackingLineParameter);

                    List<ItemReclassItemLineEntries> entryList = SugarRecord.listAll(ItemReclassItemLineEntries.class, "line_no = ?");
                    if (entryList.size() > 0) {
                        for (int x = 0; x < entryList.size(); x++) {
                            lotNo = entryList.get(x).getLotNumber();
                            tmpQty = entryList.get(x).getTmpQtyTotal();

                            if (temp_list.get(i).LotNo.equals(lotNo)) {
                                if (temp_list.get(i).QtyToShip != "0.0") {
                                    temp_TransferShipmentItemTrackingLineParameter = new ItemReclassTrackingLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mLineNo, production_date, expiry_date, temp_list.get(i).LotNo, "", Float.parseFloat(tmpQty), isScanned);
                                    mItemReclassItemTrackingLineParameter.add(temp_TransferShipmentItemTrackingLineParameter);

                                } else {
                                    temp_TransferShipmentItemTrackingLineParameter = new ItemReclassTrackingLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mLineNo, production_date, expiry_date, temp_list.get(i).LotNo, "", Float.parseFloat(temp_list.get(i).QtyToShip) + Float.parseFloat(tmpQty), isScanned);
                                    mItemReclassItemTrackingLineParameter.add(temp_TransferShipmentItemTrackingLineParameter);
                                }
                            }
                        }
                    }
                }
            }

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassItemTrackingLineParameter);
            Log.d("JSON", json);

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().InsertItemReclassLineLotEntries(mItemReclassItemTrackingLineParameter);

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
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_SUCCESSFUL, "Data has been saved!");

                        List<ItemReclassItemLineEntries> dataList = SugarRecord.find(ItemReclassItemLineEntries.class, "line_no = ?", String.valueOf(mLineNo));
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).delete();
                        }

                        // REMOVE BARCODE SERIAL LIST
                        DataManager.getInstance().DeleteSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS, mItemNo, Integer.toString(mLineNo));

                        Intent intent = new Intent(getBaseContext(), ItemReclassificationLineActivity.class);
                        intent.putExtra("DOCUMENT_NO", mDocNo);
                        intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNo);
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

    public class GetItemReclassLineLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemReclassLineLotEntriesParameter mItemReclassLineLotEntriesSearchParameter;
        ItemReclassLineLotEntriesResult mItemReclassLineLotEntriesResult;

        GetItemReclassLineLotEntriesTask(Activity activity) {

            mActivity = activity;
            mItemReclassLineLotEntriesSearchParameter = new ItemReclassLineLotEntriesParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mDocNo, mLineNo);

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassLineLotEntriesSearchParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<ItemReclassLineLotEntriesResult> call = mApp.getNavBrokerService().GetItemReclassLineLotEntries(mItemReclassLineLotEntriesSearchParameter);

                mItemReclassLineLotEntriesResult = call.execute().body();

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
                    if (mItemReclassLineLotEntriesResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        if (mItemReclassLineLotEntriesResult != null) {
                            if (mItemReclassLineLotEntriesResult.getmItemReclassLineLotEntriesResultData() != null
                                    && mItemReclassLineLotEntriesResult.getmItemReclassLineLotEntriesResultData().size() != 0) {
                                for (int i = 0; i < mItemReclassLineLotEntriesResult.getmItemReclassLineLotEntriesResultData().size(); i++) {
                                    ItemReclassLineLotEntriesResultData temp_result = mItemReclassLineLotEntriesResult.getmItemReclassLineLotEntriesResultData().get(i);
                                    String temp_prod_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_result.getProductionDate());
                                    String temp_expire_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_result.getExpireDate());
                                    ItemReclassificationEntry temp_entry = new ItemReclassificationEntry(temp_prod_date, temp_expire_date, Float.toString(temp_result.getQuantity()), temp_result.getLotNo().toUpperCase(), "", temp_list.size() + 1, ConfigurationManager.DATA_NO_LOCAL_ENTRY_INDEX, temp_result.getEntryNo(), ConfigurationManager.DATA_UPLOADED_TRUE);
                                    temp_list.add(temp_entry);

                                }
                            }
                        }

                        List<ItemReclassItemLineEntries> entryList = SugarRecord.find(ItemReclassItemLineEntries.class, "line_no = ?", Integer.toString(mLineNo));
                        for (int i = 0; i < entryList.size(); i++) {
                            ItemReclassificationEntry temp_entry = new ItemReclassificationEntry(entryList.get(i).getProductionDate(), entryList.get(i).getExpiryDate(), entryList.get(i).getQtyToShip(), entryList.get(i).getLotNumber(), entryList.get(i).getSerialNo(), entryList.get(i).getEntryIndexNo(), temp_list.size() + 1, ConfigurationManager.DATA_NO_SERVER_ENTRY_INDEX, ConfigurationManager.DATA_UPLOADED_FALSE);
                            temp_list.add(temp_entry);
                        }

                        historyAdapter = new itemReclassLotEntryListArrayAdapter(mContext, temp_list);
                        historyList.setAdapter(historyAdapter);

                        mQueryItemReclassAvailableLotNoParameter = new QueryItemReclassAvailableLotNoParameter((Activity) mContext);
                        mQueryItemReclassAvailableLotNoParameter.execute((Void) null);

                        UpdateDisplayQuantity();
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mItemReclassLineLotEntriesResult.getMessage());
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

    //query available lot no
    public class QueryItemReclassAvailableLotNoParameter extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemReclassificationAvailableLotNoParameter mItemReclassAvailableLotNoParameter;


        QueryItemReclassAvailableLotNoParameter(Activity activity) {

            mActivity = activity;

            mItemReclassAvailableLotNoParameter = new ItemReclassificationAvailableLotNoParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mItemNo, mLineNo);

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassAvailableLotNoParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<AvailableLotNoResult> call = mApp.getNavBrokerService().QueryItemReclassAvailableLotNo(mItemReclassAvailableLotNoParameter);

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

    public class RemoveItemReclassLineLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        RemoveItemReclassLotEntryParameter mRemoveTransferShipmentLotEntryParameter;
        BaseResult mReturnBaseResult;

        int mPositionDelete;
        float deductValue;

        RemoveItemReclassLineLotEntriesTask(Activity activity, String TransferNo, int LineNo, int position, String lotSerialNo, float quantityToDeduct) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_DELETE_DATA, NotificationManager.DIALOG_MSG_DELETE);

            mActivity = activity;

            int SerialOrLotFlag = 0;
            if (DataManager.getInstance().transitionItemReclassLinesData.isItemLotTrackingRequired()) {
                SerialOrLotFlag = 1;
            }

            mRemoveTransferShipmentLotEntryParameter = new RemoveItemReclassLotEntryParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), LineNo, lotSerialNo, quantityToDeduct);

            mPositionDelete = position;
            deductValue = quantityToDeduct;

            Gson gson = new Gson();
            String json = gson.toJson(mRemoveTransferShipmentLotEntryParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().RemoveItemReclassLineLotEntry(mRemoveTransferShipmentLotEntryParameter);

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

                        mQueryItemReclassAvailableLotNoParameter = new QueryItemReclassAvailableLotNoParameter((Activity) mContext);
                        mQueryItemReclassAvailableLotNoParameter.execute((Void) null);
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

    public class GetItemReclassListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemReclassListResult mItemReclassListResult;
        ItemReclassificationListSearchParameter mItemReclassificationListSearchParameter;

        GetItemReclassListTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mItemReclassificationListSearchParameter = new ItemReclassificationListSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mDocNo);

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassificationListSearchParameter);
            Log.d("Item Reclass Header ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<ItemReclassListResult> call = mApp.getNavBrokerService().GetItemReclassList(mItemReclassificationListSearchParameter);

                mItemReclassListResult = call.execute().body();

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

                    if (mItemReclassListResult != null) {
                        List<ItemReclassListResultData> m = mItemReclassListResult.getItemReclassListResultData();
                        for (int x = 0; x < m.size(); x++) {
                            mLocCode = m.get(x).getLocationCode();
                            mNewLocCode = m.get(x).getNewLocationCode();
                            mPostDate = m.get(x).getPostingDate();
                            mCreatedBy = m.get(x).getCreatedBy();
                            mIsShowBinCode = m.get(x).isShowBinCode();
                            mBinCode = m.get(x).getBinCode();
                            break;
                        }
                        TextView tempTextLabel = (TextView) findViewById(R.id.lblLoc);
                        tempTextLabel.setText("Loc: " + mLocCode);

                        tempTextLabel = (TextView) findViewById(R.id.lblDocNo);
                        tempTextLabel.setText("Doc No: " + mDocNo);

                        tempTextLabel = (TextView) findViewById(R.id.lblReqLoc);
                        tempTextLabel.setText("New Loc: " + mNewLocCode);

                        tempTextLabel = (TextView) findViewById(R.id.lblBin);
                        mBinCode = DataManager.getInstance().transitionItemReclassLinesData.getBinCode();
                        if (mIsShowBinCode) {
                            if (mBinCode != null) {
                                tempTextLabel.setVisibility(View.VISIBLE);
                                tempTextLabel.setText("Bin: " + mBinCode);
                            } else {
                                tempTextLabel.setVisibility(View.GONE);
                            }
                        } else {
                            tempTextLabel.setVisibility(View.GONE);
                        }

                        tempTextLabel = (TextView) findViewById(R.id.lblNewBin);
                        mNewBinCode = DataManager.getInstance().transitionItemReclassLinesData.getNewBinCode();
                        if (mIsShowNewBinCode) {
                            if (mNewBinCode != null) {
                                tempTextLabel.setVisibility(View.VISIBLE);
                                tempTextLabel.setText("Req Bin: " + mNewBinCode);
                            } else {
                                tempTextLabel.setVisibility(View.GONE);
                            }
                        } else {
                            tempTextLabel.setVisibility(View.GONE);
                        }
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
        Log.d("SERVER REMOVE", "TO: " + mDocNo + " LineNo: " + mLineNo + " position: " + position + " EntryNO: " + entryNo);
        mRemoveItemReclassLineLotEntriesTask = new RemoveItemReclassLineLotEntriesTask((Activity) mContext, mDocNo, mLineNo, position, lotSerialNo, quantityToDeduct);
        mRemoveItemReclassLineLotEntriesTask.execute((Void) null);
    }

    protected void handleHomeAsUpEvent() {
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
                Intent intent = new Intent(getBaseContext(), ItemReclassificationLineActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNo);
                intent.putExtra("DOCUMENT_NO", mDocNo);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getBaseContext(), ItemReclassificationLineActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNo);
            intent.putExtra("DOCUMENT_NO", mDocNo);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_reclass_clear_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_clearEntry:
                clearAllEntries();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clearAllEntries() {
        List<ItemReclassItemLineEntries> entryList = SugarRecord.listAll(ItemReclassItemLineEntries.class, "line_no = ?");
        String mLotNo = "";
        String mQty = "";
        String mProdDate = "";
        boolean mNewAddLotNoNotExistEntry = false;
        boolean tmpIsUpload = false;

        for (int y = entryList.size() - 1; y >= 0; y--) {

            mLotNo = entryList.get(y).getLotNumber();
            mQty = entryList.get(y).getTmpQtyTotal();
            mProdDate = entryList.get(y).getProductionDate();
            mNewAddLotNoNotExistEntry = entryList.get(y).isNewAddLotNoNotExistEntry();
            tmpIsUpload = entryList.get(y).isUploaded();

            for (int z = entryList.size() - 1; z >= 0; z--) {

                if (entryList.get(z).getLotNumber().equals(mLotNo)) {
                    clearEntries(mLotNo, mQty, mProdDate, mNewAddLotNoNotExistEntry, tmpIsUpload);
                    entryList.remove(z);
                }
                break;
            }
        }

        SugarRecord.deleteAll(ItemReclassItemLineEntries.class);
    }

    public void clearEntries(String mLotNo, String mQty, String mProdDate, boolean newAddLotNoNotExistEntry, boolean tmpIsUpload) {

        for (int i = temp_list.size() - 1; i >= 0; i--) {

            if (temp_list.get(i).LotNo.equals(mLotNo)) {

                temp_list.get(i).IsUploaded = tmpIsUpload;

                if (!temp_list.get(i).IsUploaded) {

                    temp_list.get(i).QtyToShip = String.valueOf(Float.valueOf(Float.parseFloat(temp_list.get(i).QtyToShip) - Float.parseFloat(mQty)));
                    temp_list.get(i).IsUploaded = ConfigurationManager.DATA_UPLOADED_TRUE;

                    if (newAddLotNoNotExistEntry) {
                        temp_list.remove(i);
                    }
                }
            }
        }
        historyAdapter.notifyDataSetChanged();
        UpdateDisplayQuantity();
    }

    // CALLED BY ADAPTER TO REMOVE LOCAL ENTRY FROM SERIAL LIST

    public void RemoveSerialEntry(String mLotNo) {
        DataManager.getInstance().DeleteSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS, mItemNo, Integer.toString(mLineNo), mLotNo);
        UpdateDisplayQuantity();
    }

    public void UpdateDisplayQuantity() {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            total += Float.parseFloat(temp_list.get(i).QtyToShip);
            TextView tempTextLabel = (TextView) findViewById(R.id.lblShipQty);
            tempTextLabel.setText("Qty to Trn: " + Float.toString(total));
        }
    }
}

