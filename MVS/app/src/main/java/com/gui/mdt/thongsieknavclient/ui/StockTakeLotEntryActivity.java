package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.gui.mdt.thongsieknavclient.adapters.stocktake.StockTakeLotEntryListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.AvailableLotNoResult;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.RemoveStockTakeLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntry;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntryLineLotEntriesParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntryLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntryLineLotEntriesResultData;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeItemTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.UpdateStockTakeLineParameter;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialRecords;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.StockTakeItemLineEntries;
import com.orm.SugarRecord;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class StockTakeLotEntryActivity extends BaseActivity {

    InsertStockTakeItemEntriesTask mInsertStockTakeItemEntriesTask = null;
    PostUpdateStockTakeItemQuantityTask mPostUpdateLotTrackingItemQuantityTask = null;
    QueryStockTakeAvailableLineLotEntriesNoTask mQueryStockTakeAvailableLotNoTask = null;
    RemoveStockTakeLineLotEntriesTask mRemoveStockTakeLineLotEntriesTask = null;

    Context mContext;
    private NavClientApp mApp;
    ListView historyList;
    private Calendar calProductionDate;
    private Calendar calExpiryDate;
    private TextView lblDocNo;
    private EditText textProductionDate;
    private EditText textExpiryDate;
    private EditText textQuantity;
    private EditText textLotNumber;
    private EditText scanText;
    private Button btnAddEntry;
    private Button btnScan;

    private String scannedString;
    private String scannedLotNo;
    private String scannedSerialNo;
    private String mLocationCode;
    private String mDocNo;
    private String mItemNo;
    private String mBinCode;
    private String mUom;
    private String mProductionDate;
    private String mExpiryDate;
    private String mQuantity;
    private String mLotNumber;
    private String mFilterLocationCode;
    private String mFilterLocationLineNo;
    private int entryIndexCounter;
    private int mLineNo;
    private float mPhysicalQuantity;
    private float mCalculatedQuantity;
    private boolean isInterfaceScanning = false;
    private boolean mIsShowBinCode = false;
    private boolean isScanned = false;
    private boolean isStarted = false;

    LinearLayout mLinearLayoutManual;
    LinearLayout mLinearLayoutScan;
    StockTakeEntryLineLotEntriesResult mQueryStockTakeLineLotNoResult;
    AvailableLotNoResult mAvailableLotNoResult;
    StockTakeLotEntryListArrayAdapter historyAdapter;
    ArrayList<StockTakeEntry> temp_list;
    ArrayList<StockTakeEntry> NotUploadedlist;

    protected int getLayoutResource() {
        return R.layout.activity_stock_take_lot_entry;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_lot_entry);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Item No: " + DataManager.getInstance().getmStockTakeLineData().getItemNo());

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
        temp_list = new ArrayList<StockTakeEntry>();
        NotUploadedlist = new ArrayList<StockTakeEntry>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String isScanning = extras.getString("IS_SCANNING");
            mFilterLocationCode = extras.getString("LOCATION_CODE");
            mFilterLocationLineNo = extras.getString("LOCATION_LINE_NO");
            mDocNo = extras.getString("DOCUMENT_NO");
            mIsShowBinCode = extras.getBoolean("IS_SHOW_BINCODE");

            mLineNo = DataManager.getInstance().getmStockTakeLineData().getStockTakeEntryLineNo();
            mLocationCode = DataManager.getInstance().getmStockTakeLineData().getLocationCode();

            if (isScanning.contains("YES")) {
                isScanned = true;
                scannedString = extras.getString("SCAN_CONTENT");
            }

            if (DataManager.getInstance().getmStockTakeLineData().getQuantityPhysical() != 0f) {
                mPhysicalQuantity = DataManager.getInstance().getmStockTakeLineData().getQuantityPhysical();
            }

            if (DataManager.getInstance().getmStockTakeLineData().getQuantityCalculated() != 0f) {
                mCalculatedQuantity = DataManager.getInstance().getmStockTakeLineData().getQuantityCalculated();
            }

            if (DataManager.getInstance().getmStockTakeLineData().getBinCode() != null) {
                mBinCode = DataManager.getInstance().getmStockTakeLineData().getBinCode();
            }

            if (DataManager.getInstance().getmStockTakeLineData().getUom() != null) {
                mUom = DataManager.getInstance().getmStockTakeLineData().getUom();
            }

            if (DataManager.getInstance().getmStockTakeLineData().getItemNo() != null) {
                mItemNo = DataManager.getInstance().getmStockTakeLineData().getItemNo();
            }
        }

        calProductionDate = Calendar.getInstance();
        calExpiryDate = Calendar.getInstance();

        enableTextFieldsEditing(true);
        initializeDisplayHeader();
        initializeDataEntry();
        initializeDateFields();
        SugarRecord.deleteAll(StockTakeItemLineEntries.class);
        btnAddEntry = (Button) findViewById(R.id.btnAddEntry);
        btnAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prepareAddEntryParameters();

                Log.d("ADD ENTRY", mExpiryDate);
                Log.d("ADD ENTRY", scannedLotNo);
                Log.d("ADD ENTRY", scannedSerialNo);

                if (validateEntry()) {

                    if (mergeSameLotEntryQuantity(mLotNumber, mQuantity, mProductionDate)) {

                        StockTakeItemLineEntries temp_entry = new StockTakeItemLineEntries(mDocNo, mItemNo, mLineNo,
                                mProductionDate, mExpiryDate, String.valueOf(mQuantity), String.valueOf(mQuantity), mLotNumber, scannedSerialNo, "", entryIndexCounter, ConfigurationManager.DATA_UPLOADED_FALSE, ConfigurationManager.DATA_UPLOADED_FALSE, System.currentTimeMillis());
                        temp_entry.save();

                        DataManager.getInstance().AddSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_STOCK_TAKE, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);
                        Toast.makeText(mContext, "Lot No exists! Quantity merged!", Toast.LENGTH_LONG).show();
                    } else {

                        if (mQuantity.length() == 0 || mQuantity.equals(0.0)) {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                        } else {
                            temp_list.add(new StockTakeEntry(temp_list.size() + 1, mProductionDate, mExpiryDate, String.valueOf(mQuantity), "0", "", mLotNumber, scannedSerialNo, entryIndexCounter, ConfigurationManager.DATA_NO_SERVER_ENTRY_INDEX, ConfigurationManager.DATA_UPLOADED_FALSE, true));
                            StockTakeItemLineEntries temp_entry = new StockTakeItemLineEntries(mDocNo, mItemNo, mLineNo,
                                    mProductionDate, mExpiryDate, String.valueOf(mQuantity), String.valueOf(mQuantity), mLotNumber, scannedSerialNo, "", entryIndexCounter, ConfigurationManager.DATA_UPLOADED_FALSE, ConfigurationManager.DATA_UPLOADED_TRUE, System.currentTimeMillis());
                            temp_entry.save();

                            DataManager.getInstance().AddSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_STOCK_TAKE, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);
                            entryIndexCounter++;

                            Log.d("SUGARORM", "ItemNo: " + mItemNo);
                            List<StockTakeItemLineEntries> entryList = SugarRecord.find(StockTakeItemLineEntries.class, "item_no = ?", mItemNo);

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
                    mInsertStockTakeItemEntriesTask = new InsertStockTakeItemEntriesTask((Activity) mContext);
                    mInsertStockTakeItemEntriesTask.execute((Void) null);
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
                                    if (isStarted) {
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

        mQueryStockTakeAvailableLotNoTask = new QueryStockTakeAvailableLineLotEntriesNoTask(this);
        mQueryStockTakeAvailableLotNoTask.execute((Void) null);
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
            textQuantity.setEnabled(true);
        } else {
            textExpiryDate.setEnabled(false);
            textProductionDate.setEnabled(false);
            textQuantity.setEnabled(false);
        }
    }

    private void prepareAddEntryParameters() {
        mQuantity = textQuantity.getText().toString();

        if (DataManager.getInstance().getmStockTakeLineData().isItemProductionDateRequired()) {
            mProductionDate = textProductionDate.getText().toString();
        } else {
            mProductionDate = "";
        }

        if (DataManager.getInstance().getmStockTakeLineData().isItemExpireDateRequired()) {
            mExpiryDate = textExpiryDate.getText().toString();
        } else {
            mExpiryDate = "";
        }

        mLotNumber = textLotNumber.getText().toString().toUpperCase();
    }

    private boolean mergeSameLotEntryQuantity(String lotNo, String quantity, String prodDate) {

        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(lotNo) && temp_list.get(i).ProductionDate.toString().equals(prodDate)) {
                temp_list.get(i).QtyToReceive = Float.toString(Float.parseFloat(temp_list.get(i).QtyToReceive) + Float.parseFloat(quantity));
                temp_list.get(i).IsUploaded = ConfigurationManager.DATA_UPLOADED_FALSE;

                List<StockTakeItemLineEntries> entryList = SugarRecord.find(StockTakeItemLineEntries.class, "lot_number = ? and production_date = ?", lotNo, prodDate);
                if (entryList.size() > 0) {
                    entryList.get(0).setQtyToShip(temp_list.get(i).QtyToReceive);
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
//        if (!checkLotNoExist()) {
//            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOTNO, NotificationManager.ALERT_MSG_INVALID_LOTNO);
//            return false;
//        }
        return true;
    }

    private boolean checkLotNoExist() {
        for (int i = 0; i < mQueryStockTakeLineLotNoResult.StockTakeEntryLineLotEntriesResultData.size(); i++) {
            if (mQueryStockTakeLineLotNoResult.StockTakeEntryLineLotEntriesResultData.get(i).getLotNo().toUpperCase().equals(mLotNumber)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkQuantity() {

        float lotQuantity = 0;
        float currQuantity = 0;

        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(mAvailableLotNoResult.AvailableLotNoData.get(i).getLotNo())) {
                lotQuantity = Float.parseFloat(mAvailableLotNoResult.AvailableLotNoData.get(i).getAvailableQuantity());
                currQuantity = Float.parseFloat(temp_list.get(i).QtyToReceive);

                if (currQuantity + Float.parseFloat(mQuantity) > lotQuantity) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkSerialExist() {
        if (scannedSerialNo.isEmpty()) {
            return true;
        }

        List<BarcodeSerialRecords> tempSerialList = DataManager.getInstance().GetSerialRecords(mDocNo, ConfigurationManager.ORDER_TYPE_STOCK_TAKE, mItemNo, Integer.toString(mLineNo), mLotNumber);
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

        if (DataManager.getInstance().getmStockTakeLineData().isItemExpireDateRequired()) {
            if (mExpiryDate.length() == 0 || mExpiryDate.length() < 10) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_EXPIRY_DATE, NotificationManager.ALERT_MSG_INVALID_EXPIRY_DATE);
                return false;
            }
        }
        if (mQuantity.length() == 0 || mQuantity.equals("0") || mQuantity.equals("0.0")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
            return false;
        }
        if (DataManager.getInstance().getmStockTakeLineData().isItemLotTrackingRequired()) {
            if (!DataManager.getInstance().getmStockTakeLineData().isItemSNRequired()) {
                if (mLotNumber.length() == 0) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOT_NO, NotificationManager.ALERT_MSG_INVALID_LOT_NO);
                    return false;
                }
            }
        }
        return true;
    }

    private void initializeDisplayHeader() {

        TextView tempTextLabel = (TextView) findViewById(R.id.lblLoc);
        tempTextLabel.setText("Loc: " + DataManager.getInstance().getmStockTakeLineData().getLocationCode());

        tempTextLabel = (TextView) findViewById(R.id.lblDocNo);
        tempTextLabel.setText("Doc No: " + mDocNo);

        tempTextLabel = (TextView) findViewById(R.id.lblReqLoc);
        mBinCode = DataManager.getInstance().getmStockTakeLineData().getBinCode();
        if (mIsShowBinCode) {
            if (mBinCode != null) {
                tempTextLabel.setText("Bin:" + mBinCode);
                tempTextLabel.setVisibility(View.VISIBLE);
            } else {
                tempTextLabel.setText("Bin:" + "");
                tempTextLabel.setVisibility(View.VISIBLE);
            }
        } else {
            tempTextLabel.setVisibility(View.GONE);
        }

        tempTextLabel = (TextView) findViewById(R.id.lblItemDesc);
        tempTextLabel.setText(DataManager.getInstance().getmStockTakeLineData().getDescription());

        tempTextLabel = (TextView) findViewById(R.id.lblItemNoBaseUOM);
        tempTextLabel.setText(DataManager.getInstance().getmStockTakeLineData().getItemNo() + DataConverter.CheckNullString(DataManager.getInstance().getmStockTakeLineData().getUom(), "/"));

        tempTextLabel = (TextView) findViewById(R.id.lblReqQty);
        tempTextLabel.setText("Qty(Phy): " + DataManager.getInstance().getmStockTakeLineData().getQuantityPhysical());

        tempTextLabel = (TextView) findViewById(R.id.lblShipQty);
        tempTextLabel.setText("Qty(Calc): " + DataManager.getInstance().getmStockTakeLineData().getQuantityCalculated());

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

            if (DataManager.getInstance().getmStockTakeLineData().isItemExpireDateRequired() && !splitString[4].isEmpty()) {
                calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));
            }

            if (DataManager.getInstance().getmStockTakeLineData().isItemLotTrackingRequired()) {
                textLotNumber.setText(splitString[3]);
            }

            textQuantity.setText(splitString[1]);
            scannedLotNo = splitString[3];
            scannedSerialNo = splitString[5];
            enableTextFieldsEditing(true);
        }
    }

    public class InsertStockTakeItemEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        List<StockTakeItemTrackingLineParameter> mStockTakeItemTrackingLineParameter;
        BaseResult mReturnBaseResult;

        InsertStockTakeItemEntriesTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mStockTakeItemTrackingLineParameter = new ArrayList<StockTakeItemTrackingLineParameter>();

            StockTakeItemTrackingLineParameter temp_TransferShipmentItemTrackingLineParameter;
            String lotNo = "";
            String tmpQty = "";
            for (int i = 0; i < temp_list.size(); i++) {
                if (!temp_list.get(i).IsUploaded) {

                    String production_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ProductionDate);
                    String expiry_date = DataConverter.ConvertDateToYearMonthDay(temp_list.get(i).ExpiryDate);

                    List<StockTakeItemLineEntries> entryList = SugarRecord.listAll(StockTakeItemLineEntries.class, "lot_number = ? and production_date = ?");
                    if (entryList.size() > 0) {
                        for (int x = 0; x < entryList.size(); x++) {
                            lotNo = entryList.get(x).getLotNumber();
                            tmpQty = entryList.get(x).getTmpQtyTotal();

                            if (temp_list.get(i).LotNo.equals(lotNo)) {
                                if (temp_list.get(i).QtyToReceive != "0.0") {
                                    temp_TransferShipmentItemTrackingLineParameter = new StockTakeItemTrackingLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mLineNo, production_date, expiry_date, temp_list.get(i).LotNo, "", Float.parseFloat(tmpQty), isInterfaceScanning);
                                    mStockTakeItemTrackingLineParameter.add(temp_TransferShipmentItemTrackingLineParameter);

                                } else {
                                    temp_TransferShipmentItemTrackingLineParameter = new StockTakeItemTrackingLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mLineNo, production_date, expiry_date, temp_list.get(i).LotNo, "", Float.parseFloat(temp_list.get(i).QtyToReceive) + Float.parseFloat(tmpQty), isInterfaceScanning);
                                    mStockTakeItemTrackingLineParameter.add(temp_TransferShipmentItemTrackingLineParameter);
                                }
                            }
                        }
                    }
                }
            }

            Gson gson = new Gson();
            String json = gson.toJson(mStockTakeItemTrackingLineParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().InsertStockTakeLineLotEntries(mStockTakeItemTrackingLineParameter);

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

                        List<StockTakeItemLineEntries> dataList = SugarRecord.find(StockTakeItemLineEntries.class, "line_no = ?", String.valueOf(mLineNo));
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).delete();
                        }
                        // REMOVE BARCODE SERIAL LIST
                        DataManager.getInstance().DeleteSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_STOCK_TAKE, mItemNo, Integer.toString(mLineNo));

                        Intent intent = new Intent(getBaseContext(), StockTakeLineActivity.class);
                        intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                        intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                        intent.putExtra("DOCUMENT_NO", mDocNo);
                        intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
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

    //Update stock take lot tracking item details
    public class PostUpdateStockTakeItemQuantityTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        UpdateStockTakeLineParameter mUpdateItemReclassLineDetailsParameter;
        BaseResult mReturnBaseResult;

        PostUpdateStockTakeItemQuantityTask(Activity activity, int lineNo, Float Quantity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mUpdateItemReclassLineDetailsParameter = new UpdateStockTakeLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), lineNo, Quantity);

            Gson gson = new Gson();
            String json = gson.toJson(mUpdateItemReclassLineDetailsParameter);
            Log.d("TRANSFER ORDER", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().UpdateStockTakeLine(mUpdateItemReclassLineDetailsParameter);

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
                Log.d("RETURN STATUS", String.valueOf(mReturnBaseResult.getStatus()));
                if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {
                    Intent intent = new Intent(getBaseContext(), StockTakeLineActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                    intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                    startActivity(intent);
                } else {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_FAILED, mReturnBaseResult.getMessage());
                }
                DataManager.getInstance().DeleteSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_STOCK_TAKE, mItemNo, scannedLotNo);

            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_FAILED, getResources().getString(R.string.notification_msg_server_no_response));
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class QueryStockTakeAvailableLineLotEntriesNoTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        StockTakeEntryLineLotEntriesParameter mTransferReceiptAvailableLotNoParameter;

        QueryStockTakeAvailableLineLotEntriesNoTask(Activity activity) {

            mActivity = activity;
            mTransferReceiptAvailableLotNoParameter = new StockTakeEntryLineLotEntriesParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mLineNo);

            Gson gson = new Gson();
            String json = gson.toJson(mTransferReceiptAvailableLotNoParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<StockTakeEntryLineLotEntriesResult> call = mApp.getNavBrokerService().StockTakeLineLotEntries(mTransferReceiptAvailableLotNoParameter);

                mQueryStockTakeLineLotNoResult = call.execute().body();

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
                    if (mQueryStockTakeLineLotNoResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        Gson gson = new Gson();
                        String json = gson.toJson(mQueryStockTakeLineLotNoResult);
                        Log.d("STOCK TAKE", json);

                        if (mQueryStockTakeLineLotNoResult == null || mQueryStockTakeLineLotNoResult.StockTakeEntryLineLotEntriesResultData == null) {
                            mQueryStockTakeLineLotNoResult = new StockTakeEntryLineLotEntriesResult();
                            mQueryStockTakeLineLotNoResult.StockTakeEntryLineLotEntriesResultData = new ArrayList<StockTakeEntryLineLotEntriesResultData>();
                        }

                        if (mQueryStockTakeLineLotNoResult != null) {
                            if (mQueryStockTakeLineLotNoResult.StockTakeEntryLineLotEntriesResultData != null && mQueryStockTakeLineLotNoResult.StockTakeEntryLineLotEntriesResultData.size() != 0) {
                                for (int i = 0; i < mQueryStockTakeLineLotNoResult.StockTakeEntryLineLotEntriesResultData.size(); i++) {
                                    StockTakeEntryLineLotEntriesResultData temp_data = mQueryStockTakeLineLotNoResult.StockTakeEntryLineLotEntriesResultData.get(i);
                                    String tmp_prod_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_data.getProductionDate());
                                    String tmp_expire_date = DataConverter.ConvertJsonDateToDayMonthYear(temp_data.getExpireDate());
                                    temp_list.add(new StockTakeEntry(temp_list.size() + 1, tmp_prod_date, tmp_expire_date, String.valueOf(temp_data.getUserEnterQty()), String.valueOf(temp_data.getSystemCalculatedQty()), "", temp_data.getLotNo().toUpperCase(), scannedSerialNo, ConfigurationManager.DATA_NO_LOCAL_ENTRY_INDEX, temp_data.getEntryNo(), ConfigurationManager.DATA_UPLOADED_TRUE, temp_data.isDoNotShowSystemQty()));
                                }
                            }
                        }

                        historyAdapter = new StockTakeLotEntryListArrayAdapter(mContext, temp_list);
                        historyList.setAdapter(historyAdapter);
                        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                for (int y = 0; y < temp_list.size(); y++) {
                                    GetLotNumber(temp_list.get(position).LotNo);
                                    break;
                                }
                            }
                        });

                        if (isScanned) {

                            prepareAddEntryParameters();

                            if (validateEntry()) {
                                btnAddEntry.callOnClick();
                            } else {
                                clearInputFields();
                            }
                            btnScan.callOnClick();

                        }
                        UpdateDisplayQuantity();

                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mQueryStockTakeLineLotNoResult.getMessage());
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

    public class RemoveStockTakeLineLotEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        RemoveStockTakeLotEntryParameter mRemoveStockTakeLotEntryParameter;
        BaseResult mReturnBaseResult;

        int mPositionDelete;
        float deductValue;

        RemoveStockTakeLineLotEntriesTask(Activity activity, String TransferNo, int LineNo, int position, String lotSerialNo, float quantityToDeduct) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_DELETE_DATA, NotificationManager.DIALOG_MSG_DELETE);

            mActivity = activity;

            int SerialOrLotFlag = 0;
            if (DataManager.getInstance().getmStockTakeLineData().isItemLotTrackingRequired()) {
                SerialOrLotFlag = 1;
            }

            mRemoveStockTakeLotEntryParameter = new RemoveStockTakeLotEntryParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), LineNo, lotSerialNo, quantityToDeduct);

            mPositionDelete = position;
            deductValue = quantityToDeduct;

            Gson gson = new Gson();
            String json = gson.toJson(mRemoveStockTakeLotEntryParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().RemoveStockTakeLineLotEntry(mRemoveStockTakeLotEntryParameter);

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
        Log.d("SERVER REMOVE", "DOCNO: " + mDocNo + " LineNo: " + mLineNo + " position: " + position + " EntryNO: " + entryNo);
        mRemoveStockTakeLineLotEntriesTask = new RemoveStockTakeLineLotEntriesTask((Activity) mContext, mDocNo, mLineNo, position, lotSerialNo, quantityToDeduct);
        mRemoveStockTakeLineLotEntriesTask.execute((Void) null);
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
                Intent intent = new Intent(getBaseContext(), StockTakeLineActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                intent.putExtra("DOCUMENT_NO", mDocNo);
                intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getBaseContext(), StockTakeLineActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("LOCATION_CODE", mFilterLocationCode);
            intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
            intent.putExtra("DOCUMENT_NO", mDocNo);
            intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
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
        List<StockTakeItemLineEntries> entryList = SugarRecord.listAll(StockTakeItemLineEntries.class, "line_no = ?");
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

        SugarRecord.deleteAll(StockTakeItemLineEntries.class);
    }

    public void clearEntries(String mLotNo, String mQty, String mProdDate, boolean newAddLotNoNotExistEntry, boolean tmpIsUpload) {

        for (int i = temp_list.size() - 1; i >= 0; i--) {

            if (temp_list.get(i).LotNo.equals(mLotNo)) {

                temp_list.get(i).IsUploaded = tmpIsUpload;

                if (!temp_list.get(i).IsUploaded) {

                    temp_list.get(i).QtyToReceive = String.valueOf(Float.valueOf(Float.parseFloat(temp_list.get(i).QtyToReceive) - Float.parseFloat(mQty)));
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
        DataManager.getInstance().DeleteSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_STOCK_TAKE, mItemNo, Integer.toString(mLineNo), mLotNo);
    }

    public void UpdateDisplayQuantity() {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            total += Float.parseFloat(temp_list.get(i).QtyToReceive);
        }

        TextView tempTextLabel = (TextView) findViewById(R.id.lblReqQty);
        tempTextLabel.setText("Qty(Phy): " + Float.toString(total));
    }

    public void GetLotNumber(String lotNo) {

        for (int i = 0; i < mQueryStockTakeLineLotNoResult.StockTakeEntryLineLotEntriesResultData.size(); i++) {
            StockTakeEntryLineLotEntriesResultData temp_data = mQueryStockTakeLineLotNoResult.StockTakeEntryLineLotEntriesResultData.get(i);

            if (temp_data.getLotNo().toUpperCase() != "") {
                textLotNumber.setText(lotNo);
                if (lotNo.toUpperCase().matches(temp_data.getLotNo().toUpperCase())) {
                    if (DataManager.getInstance().getmStockTakeLineData().isItemExpireDateRequired() || DataManager.getInstance().getmStockTakeLineData().isItemProductionDateRequired()) {
                        textExpiryDate.setText(mExpiryDate);
                        textProductionDate.setText(mProductionDate);
                    } else {
                        textExpiryDate.setText("N.A.");
                        textProductionDate.setText("N.A.");
                    }
                }
            }
        }
    }
}