package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
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
import com.gui.mdt.thongsieknavclient.adapters.transferreceipt.TransferReceiptLotEntryListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialRecords;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.TransferReceiptItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.QueryTransferReceiptLineLotNoResult;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.QueryTransferReceiptLineLotNoResultData;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptAvailableLotNoParameter;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptEntry;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptItemTrackingLineParameter;
import com.orm.SugarRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

public class TransferInLotEntryActivity extends BaseActivity {

    Context mContext;
    private NavClientApp mApp;

    PostTransferReceiptItemEntriesTask mPostTransferReceiptItemEntriesTask = null;
    QueryTransferReceiptAvailableLotNoTask mQueryTransferReceiptAvailableLotNoTask = null;
    QueryTransferReceiptLineLotNoResult mQueryTransferReceiptLineLotNoResult;

    ListView historyList;
    TransferReceiptLotEntryListArrayAdapter transferReceiptLotEntryListArrayAdapter;
    ArrayList<TransferReceiptEntry> temp_list;

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
    TextView tvLotNo;
    EditText scanText;
    Button btnAddEntry;
    Button btnScan;
    LinearLayout mTopLayout;

    private String mTransferNo;
    private String mItemNo;
    private int mLineNo;
    private float mTransferOrderQuantityBase;
    private float mTransferOutstandingQuantityBase;

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
        return R.layout.activity_transfer_in_lot_entry;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_in_lot_entry);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Item No.: " + DataManager.getInstance().transitionTransferInData.getItemNo());

        textProductionDate = (EditText) findViewById(R.id.editProductionDate);
        textExpiryDate = (EditText) findViewById(R.id.editExpiryDate);
        textQuantity = (EditText) findViewById(R.id.editQtyReceived);
        textLotNumber = (EditText) findViewById(R.id.editLotNumber);


        mTopLayout = (LinearLayout) findViewById(R.id.topLayout);
        mLinearLayoutManual = (LinearLayout) findViewById(R.id.linearLayoutManual);
        mLinearLayoutScan = (LinearLayout) findViewById(R.id.linearLayoutScan);
        mTopLayout = (LinearLayout) findViewById(R.id.topLayout);

        entryIndexCounter = 0;
        scannedLotNo = "";
        scannedSerialNo = "";

        historyList = (ListView) findViewById(R.id.itemList);
        temp_list = new ArrayList<TransferReceiptEntry>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String isScanning = extras.getString("IS_SCANNING");
            //The key argument here must match that used in the other activity

            mTransferNo = DataManager.getInstance().TransferNo;

            if (isScanning.contains("YES")) {
                isScanned = true;
                scannedString = extras.getString("SCAN_CONTENT");
            }

            if (DataManager.getInstance().transitionTransferInData.getOrderQuantityBase() != null) {
                mTransferOrderQuantityBase = Float.parseFloat(DataManager.getInstance().transitionTransferInData.getOrderQuantityBase());
            }

            if (DataManager.getInstance().transitionTransferInData.getOutstandingQuantityBase() != null) {
                mTransferOutstandingQuantityBase = Float.parseFloat(DataManager.getInstance().transitionTransferInData.getOutstandingQuantityBase());
            }

            mLineNo = Integer.parseInt(DataManager.getInstance().transitionTransferInData.getLineNo());
            mItemNo = DataManager.getInstance().transitionTransferInData.getItemNo();

            Log.d("PO ITEM DETAILS", "PO: " + mTransferNo.toString());
            Log.d("PO ITEM DETAILS", Float.toString(mTransferOrderQuantityBase));
            Log.d("PO ITEM DETAILS", Float.toString(mTransferOutstandingQuantityBase));
            Log.d("PO ITEM DETAILS", Integer.toString(mLineNo));
        }

        calProductionDate = Calendar.getInstance();
        calExpiryDate = Calendar.getInstance();

        enableTextFieldsEditing(false);
        disableTextFields();
        initializeDisplayHeader();
        initializeDataEntry();      // for entries that are scanned from Layer 2
        //initializeDateFields();

        btnAddEntry = (Button) findViewById(R.id.btnAddEntry);
        btnAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prepareAddEntryParameters();

                //Log.d("ADD ENTRY", mProductionDate);
                Log.d("ADD ENTRY", mExpiryDate);
                Log.d("ADD ENTRY", scannedLotNo);
                Log.d("ADD ENTRY", scannedSerialNo);

                if (validateEntry()) {
                    //LOT NUMBER DEFINITELY IN THE LIST, SO MERGE WITH THE EXISTING QTY
                    if (mergeSameLotEntryQuantity(mLotNumber, mProductionDate, mQuantity)) {
                        DataManager.getInstance().AddSerialRecord(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_RECEIPT, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);
                        Toast.makeText(mContext, "Lot No exists! Quantity merged!", Toast.LENGTH_LONG).show();
                    } else {
                        /*for (int i = 0; i < temp_list.size(); i++) {
                            if (temp_list.get(i).LotNo.toUpperCase().equals(mLotNumber) && temp_list.get(i).ExpiryDate.toString().equals(mExpiryDate)) {
                                temp_list.get(i).QtyToReceive = Float.toString(Float.parseFloat(temp_list.get(i).QtyToReceive) + Float.parseFloat(mQuantity));
                            }
                        }*/
                        if (mQuantity.length() == 0 || mQuantity.equals("0.0")) {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                        } else {
                            DataManager.getInstance().AddSerialRecord(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_RECEIPT, mItemNo, Integer.toString(mLineNo), mLotNumber, scannedSerialNo);
                            entryIndexCounter++;

                            List<BarcodeSerialRecords> newEntryList = DataManager.getInstance().GetSerialRecords(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_RECEIPT, mItemNo, Integer.toString(mLineNo), mLotNumber);

                            for (int i = 0; i < newEntryList.size(); i++) {
                                Log.d("SUGARORM NEW---+++++", i + ": " + newEntryList.get(i).toString());
                            }
                        }
                    }
                    transferReceiptLotEntryListArrayAdapter.notifyDataSetChanged();
                    UpdateDisplayQuantity();
                    clearInputFields();
                }

            }
        });

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isDirtyFlag = false;
                for (int i = 0; i < temp_list.size(); i++)   // CHECK WHETHER THERE IS ANY DATA TO SAVE
                {
                    // IF THE QUANTITY TO UPDATE IS NOT EQUAL TO INITIAL QUANTITY RETRIEVED FROM DATABASE, THEN USER MADE CHANGES
                    if (Float.parseFloat(temp_list.get(i).QtyToReceive) != Float.parseFloat(temp_list.get(i).QtyToHandleBase)) {
                        isDirtyFlag = true;
                        break;
                    }
                }

                if (!isDirtyFlag) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_INFORMATION, "No entries to save!");
                } else {
                    mPostTransferReceiptItemEntriesTask = new PostTransferReceiptItemEntriesTask((Activity) mContext);
                    mPostTransferReceiptItemEntriesTask.execute((Void) null);
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
                    if (isInterfaceScanning) {
                        scanText.requestFocus();
                    } else {
                        scanText.setBackgroundResource(R.drawable.border_gray);
                    }
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
                            //isScanned = false;
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
                        scanText.setText("");
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

        mQueryTransferReceiptAvailableLotNoTask = new QueryTransferReceiptAvailableLotNoTask(this);
        mQueryTransferReceiptAvailableLotNoTask.execute((Void) null);

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
            //textQuantity.setEnabled(true);
        } else {
            textExpiryDate.setEnabled(false);
            textProductionDate.setEnabled(false);
            //textQuantity.setEnabled(false);
        }
    }

    private void disableTextFields() {
        /*
        textExpiryDate.setEnabled(true);
        textProductionDate.setEnabled(true);
        textLotNumber.setEnabled(true);
*/
        /*if (!DataManager.getInstance().transitionTransferInData.isItemExpireDateRequired()) {
            textExpiryDate.setBackgroundColor(Color.GRAY);
        }

        if (!DataManager.getInstance().transitionTransferInData.isItemProductionDateRequired()) {
            textProductionDate.setBackgroundColor(Color.GRAY);
        }*/

        if (!DataManager.getInstance().transitionTransferInData.isItemLotTrackingRequired()) {
            //textLotNumber.setBackgroundColor(Color.GRAY);
        }

        /*if(DataManager.getInstance().transitionTransferInData.isItemLotNoAutoAssigned())
        {
            textLotNumber.setBackgroundColor(Color.WHITE);
        }*/
    }

    private void prepareAddEntryParameters() {
        mQuantity = textQuantity.getText().toString();

        if (DataManager.getInstance().transitionTransferInData.isItemProductionDateRequired()) {
            mProductionDate = textProductionDate.getText().toString();
        } else {
            mProductionDate = "";
        }

        if (DataManager.getInstance().transitionTransferInData.isItemExpireDateRequired()) {
            mExpiryDate = textExpiryDate.getText().toString();
        } else {
            mExpiryDate = "";
        }

        mLotNumber = textLotNumber.getText().toString().toUpperCase();

    }

    private boolean mergeSameLotEntryQuantity(String lotNo, String prodDate, String quantity) {
        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(lotNo)) {
                if (!temp_list.get(i).IsUploaded) {
                    temp_list.get(i).QtyToReceive = Float.toString(Float.parseFloat(temp_list.get(i).QtyToReceive) + Float.parseFloat(quantity));

                    List<TransferReceiptItemLineEntries> entryList = SugarRecord.find(TransferReceiptItemLineEntries.class, "lot_number = ? and production_date = ?", lotNo, prodDate);
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
        if (!checkLotNoExist()) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOTNO, NotificationManager.ALERT_MSG_INVALID_LOTNO);
            return false;
        }

        if (!checkQuantity()) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_EXCEEDS_QUANTITY, getResources().getString(R.string.notification_msg_quantity_exceeds_available));
            return false;
        }

        return true;
    }

    private boolean checkSerialExist() {
        if (scannedSerialNo.isEmpty()) {
            return true;
        }

        List<BarcodeSerialRecords> tempSerialList = DataManager.getInstance().GetSerialRecords(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_RECEIPT, mItemNo, Integer.toString(mLineNo), mLotNumber);
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

    private boolean checkLotNoExist() {
        for (int i = 0; i < mQueryTransferReceiptLineLotNoResult.QueryTransferReceiptLineLotNoResultData.size(); i++) {
            if (mQueryTransferReceiptLineLotNoResult.QueryTransferReceiptLineLotNoResultData.get(i).getLotNo().toUpperCase().equals(mLotNumber)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkQuantity() {
        float lotQuantity = 0;
        float currQuantity = 0;

        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).LotNo.toUpperCase().equals(mLotNumber)) {
                lotQuantity = Float.parseFloat(temp_list.get(i).LotAvailableQuantity);
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

    private boolean checkRequiredFields() {
        /*if (DataManager.getInstance().transitionTransferInData.isItemProductionDateRequired()) {
            if (mProductionDate.length() == 0 || mProductionDate.length() < 10) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_PRODUCTION_DATE, NotificationManager.ALERT_MSG_INVALID_PRODUCTION_DATE);
                return false;
            }
        }*/
        if (DataManager.getInstance().transitionTransferInData.isItemExpireDateRequired()) {
            if (mExpiryDate.length() == 0 || mExpiryDate.length() < 10) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_EXPIRY_DATE, NotificationManager.ALERT_MSG_INVALID_EXPIRY_DATE);
                return false;
            }
        }
        if (mQuantity.length() == 0 || mQuantity.equals("0") || mQuantity.equals("0.0")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
            return false;
        }
        /*if(mLotNumber.length() == 0)    // for transfer receipt, lot no. must be in the list. Thus length must be greater than 0
        {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOT_NO, NotificationManager.ALERT_MSG_INVALID_LOT_NO);
            return false;
        }*/
        // Replaced by the above code
        if (DataManager.getInstance().transitionTransferInData.isItemLotTrackingRequired()) {
            if (!DataManager.getInstance().transitionTransferInData.isItemLotNoAutoAssigned()) {
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
        tempTextLabel.setText(DataManager.getInstance().transitionTransferInData.getItemDescription());

        tempTextLabel = (TextView) findViewById(R.id.lblItemIDUom);
        tempTextLabel.setText("Item No: " + DataManager.getInstance().transitionTransferInData.getItemNo() + DataConverter.CheckNullString(DataManager.getInstance().transitionTransferInData.getBaseUom(), " / "));

        tempTextLabel = (TextView) findViewById(R.id.lblPurchaseQuantity);
        tempTextLabel.setText("Ord Qty: " + DataManager.getInstance().transitionTransferInData.getOrderQuantityBase());

        tempTextLabel = (TextView) findViewById(R.id.lblBalanceQuantity);
        tempTextLabel.setText("Bal Qty: " + DataManager.getInstance().transitionTransferInData.getOutstandingQuantityBase());

        tempTextLabel = (TextView) findViewById(R.id.lblTransitQuantity);
        tempTextLabel.setText("Transit Qty: " + DataManager.getInstance().transitionTransferInData.getQuantityInTransitBase());

        tempTextLabel = (TextView) findViewById(R.id.lblShipQuantity);
        tempTextLabel.setText("Qty to Rec: " + DataManager.getInstance().transitionTransferInData.getQuantityToReceiveBase());

    }

    private void initializeDataEntry() {
        /*if(DataManager.getInstance().transitionTransferInData.isItemLotTrackingRequired())
            {
                textLotNumber.setText(splitString[3]);
            }

            if(DataManager.getInstance().transitionTransferInData.isItemLotNoAutoAssigned())
            {
                textLotNumber.setText(splitString[3]);
            }*/

            /*if(DataManager.getInstance().transitionTransferInData.isItemLotTrackingRequired())
            {
                if(!DataManager.getInstance().transitionTransferInData.isItemLotNoAutoAssigned())
                {
                    textLotNumber.setText(splitString[3]);
                }
            }

            if(DataManager.getInstance().transitionTransferInData.isItemLotTrackingRequired() && !DataManager.getInstance().transitionTransferInData.isItemLotNoAutoAssigned())
            {
                    textLotNumber.setText(splitString[3]);
            }*/

        if (isScanned) {
            String[] splitString = scannedString.split("\\|");

            if (DataManager.getInstance().transitionTransferInData.isItemLotTrackingRequired() && !DataManager.getInstance().transitionTransferInData.isItemLotNoAutoAssigned()) {
                textLotNumber.setText(splitString[3]);
            }

            if (!DataManager.getInstance().transitionTransferInData.isItemLotNoAutoAssigned()) {
                textLotNumber.setText(splitString[3]);
            }

            if (DataManager.getInstance().transitionTransferInData.isItemExpireDateRequired() && !splitString[4].isEmpty()) {
                calExpiryDate.set(Integer.parseInt(splitString[4].substring(0, 4)), Integer.parseInt(splitString[4].substring(4, 6)), Integer.parseInt(splitString[4].substring(6, 8)));

                textExpiryDate.setText(splitString[4].substring(6, 8) + "/" + splitString[4].substring(4, 6) + "/" + splitString[4].substring(0, 4));

                //Toast.makeText(mContext,"Barcode without expiry date",Toast.LENGTH_SHORT).show();
            }

            textQuantity.setText(splitString[1]);
            scannedSerialNo = splitString[5];
            enableTextFieldsEditing(false);
        }

        //Log.d("Prod Date:", calProductionDate.toString());
        Log.d("Exp Date", calExpiryDate.toString());
    }

    public class PostTransferReceiptItemEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        List<TransferReceiptItemTrackingLineParameter> mTransferReceiptItemTrackingLineParameter;
        BaseResult mReturnBaseResult;

        PostTransferReceiptItemEntriesTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mTransferReceiptItemTrackingLineParameter = new ArrayList<TransferReceiptItemTrackingLineParameter>();

            for (int i = 0; i < temp_list.size(); i++) {
                TransferReceiptItemTrackingLineParameter temp_TransferReceiptItemTrackingLineParameter = new TransferReceiptItemTrackingLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mTransferNo, mLineNo, temp_list.get(i).ServerEntryIndexNo, Float.parseFloat(temp_list.get(i).QtyToReceive));
                mTransferReceiptItemTrackingLineParameter.add(temp_TransferReceiptItemTrackingLineParameter);

            }

            Gson gson = new Gson();
            String json = gson.toJson(mTransferReceiptItemTrackingLineParameter);
            Log.d("JSON", json);

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().PostTransferReceiptItemTrackingLine(mTransferReceiptItemTrackingLineParameter);

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

                        // REMOVE BARCODE SERIAL LIST
                        DataManager.getInstance().DeleteSerialRecord(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_RECEIPT, mItemNo, Integer.toString(mLineNo));

                        Intent intent = new Intent(getBaseContext(), TransferInLineActivity.class);
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

    public class QueryTransferReceiptAvailableLotNoTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        TransferReceiptAvailableLotNoParameter mTransferReceiptAvailableLotNoParameter;


        QueryTransferReceiptAvailableLotNoTask(Activity activity) {

            mActivity = activity;

            mTransferReceiptAvailableLotNoParameter = new TransferReceiptAvailableLotNoParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mTransferNo, mLineNo);

            Gson gson = new Gson();
            String json = gson.toJson(mTransferReceiptAvailableLotNoParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<QueryTransferReceiptLineLotNoResult> call = mApp.getNavBrokerService().QueryTransferReceiptAvailableLotNo(mTransferReceiptAvailableLotNoParameter);

                mQueryTransferReceiptLineLotNoResult = call.execute().body();

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
                    if (mQueryTransferReceiptLineLotNoResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        Gson gson = new Gson();
                        String json = gson.toJson(mQueryTransferReceiptLineLotNoResult);
                        Log.d("TRANSFER IN", json);

                        if (mQueryTransferReceiptLineLotNoResult == null || mQueryTransferReceiptLineLotNoResult.QueryTransferReceiptLineLotNoResultData == null) {
                            mQueryTransferReceiptLineLotNoResult = new QueryTransferReceiptLineLotNoResult();
                            mQueryTransferReceiptLineLotNoResult.QueryTransferReceiptLineLotNoResultData = new ArrayList<QueryTransferReceiptLineLotNoResultData>();
                        }

                        if (mQueryTransferReceiptLineLotNoResult != null && mQueryTransferReceiptLineLotNoResult.QueryTransferReceiptLineLotNoResultData.size() != 0) {
                            for (int i = 0; i < mQueryTransferReceiptLineLotNoResult.QueryTransferReceiptLineLotNoResultData.size(); i++) {
                                QueryTransferReceiptLineLotNoResultData temp_data = mQueryTransferReceiptLineLotNoResult.QueryTransferReceiptLineLotNoResultData.get(i);
                                temp_list.add(new TransferReceiptEntry(temp_list.size() + 1, temp_data.getProductionDate(), temp_data.getExpirationDate(), temp_data.getQuantityToHandleBase(), temp_data.getQuantityToHandleBase(), temp_data.getQuantity(), temp_data.getLotNo().toUpperCase(), scannedSerialNo, ConfigurationManager.DATA_NO_LOCAL_ENTRY_INDEX, temp_data.getEntryNo(), ConfigurationManager.DATA_UPLOADED_FALSE));
                            }
                        }

                        transferReceiptLotEntryListArrayAdapter = new TransferReceiptLotEntryListArrayAdapter(mContext, temp_list);

                        historyList.setAdapter(transferReceiptLotEntryListArrayAdapter);
                        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                for (int y = 0; y < temp_list.size(); y++) {
                                    GetLotNumber(temp_list.get(position).LotNo);
                                    break;
                                }
                            }
                        });

                        UpdateDisplayQuantity();

                        if (isScanned) {

                            prepareAddEntryParameters();
                            //Toast.makeText(mContext, textQuantity.getText().toString() + " " + textLotNumber.getText().toString(), Toast.LENGTH_SHORT).show();

                            if (validateEntry()) {
                                btnAddEntry.callOnClick();
                            } else {
                                clearInputFields();
                            }
                            btnScan.callOnClick();

                        }
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mQueryTransferReceiptLineLotNoResult.getMessage());
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

    @Override
    public void onBackPressed() {
        if (temp_list.size() > 0) {
            boolean isDirtyFlag = false;
            for (int i = 0; i < temp_list.size(); i++) {
                // IF THE QUANTITY TO UPDATE IS NOT EQUAL TO INITIAL QUANTITY RETRIEVED FROM DATABASE, THEN USER MADE CHANGES
                if (Float.parseFloat(temp_list.get(i).QtyToReceive) != Float.parseFloat(temp_list.get(i).QtyToHandleBase)) {
                    isDirtyFlag = true;
                    break;
                }
            }

            if (isDirtyFlag) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_REQUEST, NotificationManager.DIALOG_MSG_SAVE_REQUEST);
            } else {
                Intent intent = new Intent(getBaseContext(), TransferInLineActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("TRANSFER_ORDER_NO", mTransferNo);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getBaseContext(), TransferInLineActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("TRANSFER_ORDER_NO", mTransferNo);
            startActivity(intent);
        }

    }

    // RESET QUANTITY VALUE = 0
    public void ClearEntryQuantity(int position, String lotNo) {
        temp_list.get(position).QtyToReceive = Float.toString(0);

        RemoveSerialEntry(lotNo);

        transferReceiptLotEntryListArrayAdapter.notifyDataSetChanged();
        UpdateDisplayQuantity();
    }

    // CALLED BY ADAPTER TO REMOVE LOCAL ENTRY FROM SERIAL LIST
    public void RemoveSerialEntry(String mLotNo) {
        DataManager.getInstance().DeleteSerialRecord(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_RECEIPT, mItemNo, Integer.toString(mLineNo), mLotNo);
    }

    public void UpdateDisplayQuantity() {
        float total = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            total += Float.parseFloat(temp_list.get(i).QtyToReceive);
            /*TextView tempTextLabel = (TextView) findViewById(R.id.lblShipQuantity);
            tempTextLabel.setText("Qty to Rec: " + Float.toString(total));*/
        }

        TextView tempTextLabel = (TextView) findViewById(R.id.lblShipQuantity);
        tempTextLabel.setText("Qty to Rec: " + Float.toString(total));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.transfer_in_lot_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_discardChanges:
                ResetChanges();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ResetChanges() {
        for (int i = 0; i < temp_list.size(); i++) {
            temp_list.get(i).QtyToReceive = temp_list.get(i).QtyToHandleBase;
            DataManager.getInstance().DeleteSerialRecord(mTransferNo, ConfigurationManager.ORDER_TYPE_TRANSFER_RECEIPT, mItemNo, Integer.toString(mLineNo), temp_list.get(i).LotNo);
        }

        transferReceiptLotEntryListArrayAdapter.notifyDataSetChanged();
        UpdateDisplayQuantity();
    }

    public void GetLotNumber(String lotNo) {

        for (int i = 0; i < mQueryTransferReceiptLineLotNoResult.QueryTransferReceiptLineLotNoResultData.size(); i++) {
            QueryTransferReceiptLineLotNoResultData temp_data = mQueryTransferReceiptLineLotNoResult.QueryTransferReceiptLineLotNoResultData.get(i);

            if (temp_data.getLotNo().toUpperCase() != "") {
                textLotNumber.setText(lotNo);
                if (lotNo.toUpperCase().matches(temp_data.getLotNo().toUpperCase())) {
                    if (DataManager.getInstance().transitionTransferInData.isItemExpireDateRequired() || DataManager.getInstance().transitionTransferInData.isItemProductionDateRequired()) {
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
