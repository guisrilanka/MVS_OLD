package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.ItemNumberListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemListParameter;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemListResultData;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemUomListResultData;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemUomParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.CalculateItemReclassQuantityParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.CalculateItemReclassificationResultData;
import com.gui.mdt.thongsieknavclient.model.location.LocationBinListParameter;
import com.gui.mdt.thongsieknavclient.model.location.LocationBinListResultData;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.InsertStockTakeParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class AddNewStockTakeLineActivity extends BaseActivity {

    GetItemReclassBinListTask mGetItemReclassBinListTask = null;
    CreateNewStockTakeLineItemEntriesTask mCreateNewItemReclassLineItemEntriesTask = null;
    GetFilterStockTakeUomTask mGetFilterStockTakeUomTask = null;
    GetStockTakeUomSpecialTask mGetItemReclassUomSpecialTask = null;
    GetScannedStockTakeItemNoUomSpecialTask mGetScannedItemReclassItemNoUomSpecialTask = null;
    CalculateScannedItemNoQuantityBasedStockTakeQuantityTask mCalculateScannedItemNoQuantityBasedItemReclassQuantityTask = null;
    CalculateStockTakeQuantityTask mCalculateItemReclassQuantity = null;
    GetStockTakeItemCodeListTask mGetItemReclassItemCodeTask = null;
    GetNormalItemReclassItemCodeListTask mGetNormalItemReclassItemCodeListTask = null;
    GetFilterItemReclassItemCodeListTask mFilterGetItemReclassItemCodeTask = null;
    GetFilterScanItemIsItemLotTrackingTask mGetFilterScanItemIsItemLotTrackingTask = null;
    GetUserInputItemCodeandRetrieveItemDetailsTask mGetUserInputItemCodeandRetrieveItemDetailsTask = null;
    GetUserKeyInItemCodeUomSpecialTask mGetUserKeyInItemCodeUomSpecialTask = null;
    GetItemCodeListTask mGetItemCodeListTask = null;

    Context mContext;
    ListView itemNumberList;
    ListView itemCodeList;
    private ArrayAdapter<String> adapter;
    private NavClientApp mApp;
    private Spinner spinnerBin;
    private Button btnSearch;
    private Button btnCancel;
    private Button btnSave;
    private Button btnLookUp;
    private Spinner spinnerUOM;
    private EditText etQty;
    private EditText etItemNo;
    private EditText etQtyBase;
    private TextView tvEntryNo;
    private TextView tvLoc;
    private TextView tvBin;

    private String mDocNo;
    private String mLoc;
    private String mPostDate;
    private String mUom;
    private String mPhyQuantity;
    private String mQuantityPerUom;
    private String mScannedItemNoQuantityPerUom;
    private String mQuantityBased;
    private String mItemNo;
    private String mBinCode;
    private String IsScanning;
    private String IsNew;
    private String scannedString;
    private String scannedSerialNo;
    private String scannedLotNo;
    private String scannedQuantity;
    private String mFilterLocationCode;
    private String mFilterLocationLineNo;
    private String dStrItemCat;
    private String dStrItemCode;
    private String dStrItemDesc;
    private String dStrItemBaseUom;
    private int entryIndexCounter;
    private int mStockTakeHeaderLineNo;
    private boolean mIsShowBinCode = false;
    private boolean mIsShowNewBinCode = false;
    private boolean isScanned = false;
    private boolean mIsItemTrackingRequired = false;
    private boolean mIsItemEditText = true;

    ArrayAdapter<LocationBinListResultData> spinnerBinAdapter;
    ArrayAdapter<ItemUomListResultData> spinnerUOMAdapter;
    ItemNumberListArrayAdapter itemNumberArrayAdapter;
    List<ItemListResultData> myItemListResult;
    ItemNumberListArrayAdapter filteritemNumberArrayAdapter;
    List<ItemListResultData> xItemListResult;
    ArrayList<ItemListResultData> normal_temp_list;
    ArrayList<ItemListResultData> temp_item_list;
    List<ItemListResultData> nItemListResult;
    List<ItemListResultData> oItemListResult;
    ArrayList<ItemListResultData> zTemp_item_list;
    List<ItemListResultData> zItemListResult;

    protected int getLayoutResource() {
        return R.layout.activity_add_new_stock_take_order;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_stock_take_order);

        mContext = this;
        mApp = (NavClientApp) getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        entryIndexCounter = 0;
        scannedLotNo = "";
        scannedSerialNo = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            mIsShowBinCode = extras.getBoolean("IS_SHOW_BINCODE");
            mFilterLocationCode = extras.getString("LOCATION_CODE");
            mFilterLocationLineNo = extras.getString("LOCATION_LINE_NO");
            mDocNo = extras.getString("DOCUMENT_NO");
            IsScanning = extras.getString("IS_SCANNING");
            IsNew = extras.getString("IS_NEW_ADD");

            mLoc = DataManager.getInstance().getStockTakeListResultData().getLocationCode();
            mStockTakeHeaderLineNo = DataManager.getInstance().getStockTakeListResultData().getStockTakeHeaderLineNo();

            dStrItemCat = "";
            dStrItemDesc = "";
            dStrItemCode = "";

            if (IsScanning.contains("YES")) {
                isScanned = true;
                scannedString = extras.getString("SCAN_CONTENT");
            }

            if (IsNew.equals("YES")) {
                toolbar.setTitle("New Stock Take Line");

                mGetNormalItemReclassItemCodeListTask = new GetNormalItemReclassItemCodeListTask((Activity) mContext);
                mGetNormalItemReclassItemCodeListTask.execute((Void) null);

                mGetItemReclassItemCodeTask = new GetStockTakeItemCodeListTask((Activity) mContext, dStrItemCat, dStrItemDesc, dStrItemCode);
                mGetItemReclassItemCodeTask.execute((Void) null);

            }
        }

        mBinCode = "";
        mPostDate = "";

        itemCodeList = (ListView) findViewById(R.id.lvItemNo);
        tvBin = (TextView) findViewById(R.id.tvBin);
        spinnerBin = (Spinner) findViewById(R.id.spBin);
        etQty = (EditText) findViewById(R.id.etQty);
        etItemNo = (EditText) findViewById(R.id.etItemNo);
        etQtyBase = (EditText) findViewById(R.id.etQtyBase);
        btnLookUp = (Button) findViewById(R.id.btnShowItemNoDialog);
        spinnerUOM = (Spinner) findViewById(R.id.spinnerUOM);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        etItemNo.setEnabled(true);
        if (IsNew.equals("YES")) {
            etQty.setEnabled(false);
            etQtyBase.setEnabled(false);
            etItemNo.setText("");
            etQty.setText("");
            etQtyBase.setText("");
        } else {

            etItemNo.setText(mItemNo);
            mGetItemReclassUomSpecialTask = new GetStockTakeUomSpecialTask((Activity) mContext, etItemNo.getText().toString());
            mGetItemReclassUomSpecialTask.execute((Void) null);
            etQty.setText(mPhyQuantity);
            if (mQuantityBased.length() == 0 || mQuantityBased.equals("0") || mQuantityBased.equals("0.0")) {
            }
        }

        if (mIsShowBinCode) {
            mGetItemReclassBinListTask = new GetItemReclassBinListTask((Activity) mContext, mFilterLocationCode);
            mGetItemReclassBinListTask.execute((Void) null);
            tvBin.setVisibility(View.VISIBLE);
            spinnerBin.setVisibility(View.VISIBLE);
        } else {
            tvBin.setVisibility(View.GONE);
            spinnerBin.setVisibility(View.GONE);
        }

        initializeDisplayHeader();
        initializeDataEntry();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prepareAddEntryParameters();
                if (validateEntry()) {
                    if (IsNew.equals("YES")) {
                        mCreateNewItemReclassLineItemEntriesTask = new CreateNewStockTakeLineItemEntriesTask((Activity) mContext);
                        mCreateNewItemReclassLineItemEntriesTask.execute((Void) null);

                    }
                }
            }
        });

        Button btnLookUp = (Button) findViewById(R.id.btnShowItemNoDialog);
        btnLookUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

                Window window = dialog.getWindow();
                window.setGravity(Gravity.CENTER);
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                window.setDimAmount(0.2f);

                dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                dialog.setContentView(R.layout.dialog_item_list_selection);
                dialog.setCancelable(false);

                final Button btnClearText1 = (Button) dialog.findViewById(R.id.btnClearText1);
                btnClearText1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editFilterItem = (EditText) dialog.findViewById(R.id.editFilterItemCategory);
                        editFilterItem.setText("");
                    }
                });

                final Button btnClearText2 = (Button) dialog.findViewById(R.id.btnClearText2);
                btnClearText2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editFilterItem = (EditText) dialog.findViewById(R.id.editFilterItemCode);
                        editFilterItem.setText("");
                    }
                });

                final Button btnClearText3 = (Button) dialog.findViewById(R.id.btnClearText3);
                btnClearText3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editFilterItem = (EditText) dialog.findViewById(R.id.editFilterItemDescription);
                        editFilterItem.setText("");
                    }
                });

                if (xItemListResult != null) {
                    if (xItemListResult.size() != 0) {
                        normal_temp_list = new ArrayList<ItemListResultData>(xItemListResult);
                        itemNumberArrayAdapter = new ItemNumberListArrayAdapter(mContext, normal_temp_list);
                        itemNumberList = (ListView) dialog.findViewById(R.id.itemList);
                        itemNumberList.setAdapter(itemNumberArrayAdapter);
                        itemNumberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                ItemListResultData temp = itemNumberArrayAdapter.getItem(i);
                                mIsItemTrackingRequired = temp.isItemTrackingRequired();
                                etItemNo.setText(temp.getItemCode());
                                etItemNo.setSelection(etItemNo.getText().length());
                                etItemNo.selectAll();
                                itemCodeList = (ListView) findViewById(R.id.lvItemNo);
                                itemCodeList.setVisibility(View.GONE);
                                if (!mIsItemTrackingRequired) {
                                    etQty.setText("");
                                    etQty.setEnabled(true);
                                } else {
                                    etQty.setText("0");
                                    etQtyBase.setText("0.0");
                                    etQty.setEnabled(false);
                                }
                                etQtyBase.setEnabled(false);
                                dStrItemCode = temp.getItemCode();
                                dStrItemBaseUom = temp.getItemBaseUom();

                                mGetFilterStockTakeUomTask = new GetFilterStockTakeUomTask((Activity) mContext, dStrItemCode);
                                mGetFilterStockTakeUomTask.execute((Void) null);
                                dialog.dismiss();
                            }
                        });
                    } else {
                        TextView tvLblNoData = (TextView) dialog.findViewById(R.id.lblNoData);
                        tvLblNoData.setVisibility(View.VISIBLE);
                        itemNumberList.setAdapter(null);
                    }
                } else {
                    TextView tvLblNoData = (TextView) dialog.findViewById(R.id.lblNoData);
                    tvLblNoData.setVisibility(View.VISIBLE);
                    itemNumberList.setAdapter(null);
                }

                btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
                final EditText editFilterItemCat = (EditText) dialog.findViewById(R.id.editFilterItemCategory);
                final EditText editFilterItemCode = (EditText) dialog.findViewById(R.id.editFilterItemCode);
                final EditText editFilterItemDesc = (EditText) dialog.findViewById(R.id.editFilterItemDescription);
                btnSearch.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dStrItemCat = editFilterItemCat.getText().toString().trim();
                        dStrItemCode = editFilterItemCode.getText().toString().trim().toUpperCase();
                        dStrItemDesc = editFilterItemDesc.getText().toString().trim();

                        mFilterGetItemReclassItemCodeTask = new GetFilterItemReclassItemCodeListTask((Activity) mContext, editFilterItemCat.getText().toString(), editFilterItemDesc.getText().toString(), editFilterItemCode.getText().toString(), dialog);
                        mFilterGetItemReclassItemCodeTask.execute((Void) null);
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

        mGetItemCodeListTask = new GetItemCodeListTask((Activity) mContext);
        mGetItemCodeListTask.execute((Void) null);

        etQty.addTextChangedListener(new CustomTextWatcher(etQty, !mIsItemEditText));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), StockTakeLineActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
                intent.putExtra("DOCUMENT_NO", mDocNo);
                startActivity(intent);
            }
        });
    }

    private class CustomTextWatcher implements TextWatcher {
        private EditText mEdiText;
        private boolean isItemETFlag;

        public CustomTextWatcher(EditText e, boolean isItemET) {
            mEdiText = e;
            isItemETFlag = isItemET;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {

            if (isItemETFlag) {
//                mItemNo = s.toString().toUpperCase();
//
//                Handler handler = new Handler();
//                handler.postDelayed(
//                        new Runnable() {
//                            public void run() {
//                                mGetUserInputItemCodeandRetrieveItemDetailsTask = new GetUserInputItemCodeandRetrieveItemDetailsTask((Activity) mContext, "", "", mItemNo);
//                                mGetUserInputItemCodeandRetrieveItemDetailsTask.execute((Void) null);
//                            }
//                        }, 4000);

            } else {
                mPhyQuantity = s.toString();
                if (mPhyQuantity.length() == 0 || mPhyQuantity.equals("0") || mPhyQuantity.equals("0.0") || mPhyQuantity.equals("")) {
                    etQtyBase.setText("");
                } else {
                    if (mScannedItemNoQuantityPerUom != null || mQuantityPerUom != null) {
                        if (mQuantityPerUom != null) {
                            if (mPhyQuantity != null && mPhyQuantity.length() != 0 && !mPhyQuantity.equals("0.0") && !mPhyQuantity.equals("0") && !mPhyQuantity.equals(".")) {
                                mCalculateItemReclassQuantity = new CalculateStockTakeQuantityTask((Activity) mContext, Float.parseFloat(mPhyQuantity), Float.parseFloat(mQuantityPerUom));
                                mCalculateItemReclassQuantity.execute((Void) null);
                            }
                        } else {
                            if (mPhyQuantity != null && mPhyQuantity.length() != 0 && !mPhyQuantity.equals("0.0") && !mPhyQuantity.equals("0") && !mPhyQuantity.equals(".")) {
                                mCalculateItemReclassQuantity = new CalculateStockTakeQuantityTask((Activity) mContext, Float.parseFloat(mPhyQuantity), Float.parseFloat(mScannedItemNoQuantityPerUom));
                                mCalculateItemReclassQuantity.execute((Void) null);
                            }
                        }
                    }
                }
            }
        }
    }

    private void clearQtyInputFields() {
        etQtyBase.setText("");
    }

    private void initializeDisplayHeader() {
        tvEntryNo = (TextView) findViewById(R.id.lblDocNo);
        if (mDocNo != null) {
            tvEntryNo.setText("Doc No: " + mDocNo);
        } else {
            tvEntryNo.setText("Doc No: " + "");
        }

        tvLoc = (TextView) findViewById(R.id.lblLoc);
        if (mFilterLocationCode != null) {
            tvLoc.setText("Loc: " + mFilterLocationCode);
        } else {
            tvLoc.setText("Loc: " + "");
        }
    }

    private void initializeDataEntry() {
        if (isScanned) {
            String[] splitString = scannedString.split("\\|");

            if (splitString[0] != null) {
                mItemNo = splitString[0];
            } else {
                NotificationManager.ShowProgressDialog(mContext, "Invalid Item No", "Barcode contains no item number.");
            }

            if (splitString[1] != null) {
                scannedQuantity = splitString[1];
            } else {
                NotificationManager.ShowProgressDialog(mContext, "Invalid Quantity", "Barcode contains no quantity value.");
            }

            if (splitString[2] != null) {
                mUom = splitString[2];
            } else {
                NotificationManager.ShowProgressDialog(mContext, "Invalid Uom", "Barcode contains no uom value.");
            }

            if (splitString[3] != null) {
                scannedLotNo = splitString[3];
            } else {
                NotificationManager.ShowProgressDialog(mContext, "Invalid Lot", "Barcode contains no lot value.");
            }

            if (splitString[5] != null) {
                scannedSerialNo = splitString[5];
            } else {
                NotificationManager.ShowProgressDialog(mContext, "Invalid Serial No", "Barcode contains no serial number.");
            }
            etItemNo.setText(mItemNo);
            etQty.setText(scannedQuantity);
            mGetScannedItemReclassItemNoUomSpecialTask = new GetScannedStockTakeItemNoUomSpecialTask((Activity) mContext, etItemNo.getText().toString(), mUom);
            mGetScannedItemReclassItemNoUomSpecialTask.execute((Void) null);
            enableTextFieldsEditing(true);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), StockTakeLineActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("LOCATION_CODE", mFilterLocationCode);
        intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
        intent.putExtra("DOCUMENT_NO", mDocNo);
        startActivity(intent);
    }

    private void enableTextFieldsEditing(boolean isEnable) {
        if (isEnable) {
            etQty.setEnabled(true);
            etQtyBase.setEnabled(false);
        } else {
            etQty.setEnabled(false);
            etQtyBase.setEnabled(false);
        }
    }

    private boolean validateEntry() {
        if (!checkRequiredFields()) {
            return false;
        }
        return true;
    }

    private boolean checkRequiredFields() {
        etItemNo.setEnabled(true);
        mItemNo = etItemNo.getText().toString();
        mPhyQuantity = etQty.getText().toString();
        mQuantityBased = etQtyBase.getText().toString();

        if (mItemNo.length() == 0 || mItemNo.equals("")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_ITEMNO, NotificationManager.ALERT_MSG_INVALID_ITEMNO);
            return false;
        }

        if (!mIsItemTrackingRequired) {
            if (mPhyQuantity.length() == 0 || mPhyQuantity.equals("0") || mPhyQuantity.equals("0.0") || mPhyQuantity.startsWith(".") || mPhyQuantity.endsWith(".")) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                return false;
            }
        } else {
            if (mPhyQuantity.length() == 0 || mPhyQuantity.equals("") || mPhyQuantity.isEmpty()) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                return false;
            }
        }

        if (!mIsItemTrackingRequired) {
            if (mQuantityBased.length() == 0 || mQuantityBased.equals("") || mQuantityBased.equals("0.0")) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                return false;
            }
        }

        if (spinnerUOM == null || spinnerUOM.getSelectedItem() == null) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_UOM, NotificationManager.ALERT_MSG_INVALID_UOM);
            return false;
        } else {
            mUom = spinnerUOM.getSelectedItem().toString();
        }
        return true;
    }

    private void prepareAddEntryParameters() {
        mPhyQuantity = etQty.getText().toString();
        mItemNo = etItemNo.getText().toString().toUpperCase();
    }

    public class CreateNewStockTakeLineItemEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        InsertStockTakeParameter mCreateStockTakeLineParameter;
        BaseResult mBaseResult;

        CreateNewStockTakeLineItemEntriesTask(Activity activity) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);
            mActivity = activity;
            mCreateStockTakeLineParameter = new InsertStockTakeParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mStockTakeHeaderLineNo, "", mItemNo.toUpperCase(), "", mLoc, mBinCode, scannedLotNo, scannedSerialNo, "", "", Float.parseFloat(mPhyQuantity), mUom);

            Gson gson = new Gson();
            String json = gson.toJson(mCreateStockTakeLineParameter);
            Log.d("JSON", json);

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().InsertStockTakeLine(mCreateStockTakeLineParameter);

                mBaseResult = call.execute().body();

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
                    if (mBaseResult != null) {

                        if (mBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {

                            Intent intent = new Intent(getBaseContext(), StockTakeLineActivity.class);
                            intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
                            intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                            intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                            intent.putExtra("DOCUMENT_NO", mDocNo);
                            startActivity(intent);
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_FAILED, mBaseResult.getMessage());
                        }

                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_FAILED, mBaseResult.getMessage());
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


    public class GetFilterStockTakeUomTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemUomParameter mItemReclassUOMListParameter;
        List<ItemUomListResultData> mUOMListResult;

        public GetFilterStockTakeUomTask(Activity activity, String mItemNo) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;
            mItemReclassUOMListParameter = new ItemUomParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mItemNo);

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassUOMListParameter);
            Log.d("Bin List ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemUomListResultData>> call = mApp.getNavBrokerService().ItemUomList(mItemReclassUOMListParameter);

                mUOMListResult = call.execute().body();

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
                    spinnerUOM.setAdapter(null);
                    final ArrayList<ItemUomListResultData> temp_list = new ArrayList<>();

                    if (mUOMListResult != null) {
                        temp_list.addAll(mUOMListResult);
                    }

                    spinnerUOMAdapter = new ArrayAdapter<ItemUomListResultData>(mContext, android.R.layout.simple_spinner_item, temp_list);
                    spinnerUOM.setAdapter(spinnerUOMAdapter);

                    for (int i = 0; i < temp_list.size(); i++) {
                        if (temp_list.get(i).getCode().equals(dStrItemBaseUom)) {
                            int spinnerPos = spinnerUOMAdapter.getPosition(temp_list.get(i));
                            spinnerUOM.setSelection(spinnerPos);
                        }
                    }

                    spinnerUOM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            for (int i = 0; i < temp_list.size(); i++) {
                                mQuantityPerUom = String.valueOf(temp_list.get(position).getQuantityPerUom());
                                if (mQuantityPerUom.length() == 0 || mQuantityPerUom.equals("0") || mQuantityPerUom.equals("0.0")) {
                                    NotificationManager.DisplayAlertDialog(mContext, "Invalid Quantity Per UOM", "Invalid quantity per uom.");
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    //DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_ITEM_RECLASS);
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

    public class GetStockTakeUomSpecialTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemUomParameter mItemReclassUOMListParameter;
        List<ItemUomListResultData> mUOMListResult;

        public GetStockTakeUomSpecialTask(Activity activity, String mItemNo) {
            mActivity = activity;
            mItemReclassUOMListParameter = new ItemUomParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mItemNo); //"80100" testing purpose

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassUOMListParameter);
            Log.d("Bin List ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemUomListResultData>> call = mApp.getNavBrokerService().ItemUomList(mItemReclassUOMListParameter);

                mUOMListResult = call.execute().body();

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
                    spinnerUOM.setAdapter(null);
                    final ArrayList<ItemUomListResultData> temp_list = new ArrayList<>();

                    if (mUOMListResult != null) {
                        temp_list.addAll(mUOMListResult);
                    }

                    spinnerUOMAdapter = new ArrayAdapter<ItemUomListResultData>(mContext, android.R.layout.simple_spinner_item, temp_list);
                    spinnerUOM.setAdapter(spinnerUOMAdapter);

                    for (int i = 0; i < temp_list.size(); i++) {
                        if (temp_list.get(i).getCode().equals(mUom)) {
                            int spinnerPos = spinnerUOMAdapter.getPosition(temp_list.get(i));
                            spinnerUOM.setSelection(spinnerPos);
                        }
                    }

                    spinnerUOM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            for (int i = 0; i < temp_list.size(); i++) {
                                mQuantityPerUom = String.valueOf(temp_list.get(position).getQuantityPerUom());
                                etQty.setText("");
                                if (mQuantityPerUom.length() == 0 || mQuantityPerUom.equals("0") || mQuantityPerUom.equals("0.0")) {
                                    NotificationManager.DisplayAlertDialog(mContext, "Invalid Quantity Per UOM", "Invalid quantity per uom.");
                                } else {
                                    etQtyBase.setText(mQuantityPerUom);
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    //DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_ITEM_RECLASS);
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

    //Get user key in item code to retrieve uom
    public class GetUserKeyInItemCodeUomSpecialTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemUomParameter mItemReclassUOMListParameter;
        List<ItemUomListResultData> mUOMListResult;

        public GetUserKeyInItemCodeUomSpecialTask(Activity activity, String mItemNo) {
//            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            Toast.makeText(mContext, getString(R.string.searching), Toast.LENGTH_SHORT).show();
            mActivity = activity;
            mItemReclassUOMListParameter = new ItemUomParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mItemNo); //"80100" testing purpose

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassUOMListParameter);
            Log.d("Bin List ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemUomListResultData>> call = mApp.getNavBrokerService().ItemUomList(mItemReclassUOMListParameter);

                mUOMListResult = call.execute().body();

            } catch (Exception e) {
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
                    spinnerUOM.setAdapter(null);
                    final ArrayList<ItemUomListResultData> temp_list = new ArrayList<>();

                    if (mUOMListResult != null) {
                        temp_list.addAll(mUOMListResult);
                    }

                    spinnerUOMAdapter = new ArrayAdapter<ItemUomListResultData>(mContext, android.R.layout.simple_spinner_item, temp_list);
                    spinnerUOM.setAdapter(spinnerUOMAdapter);

                    for (int i = 0; i < temp_list.size(); i++) {
                        if (temp_list.get(i).getCode().equals(dStrItemBaseUom)) {
                            int spinnerPos = spinnerUOMAdapter.getPosition(temp_list.get(i));
                            spinnerUOM.setSelection(spinnerPos);
                        }
                    }

                    spinnerUOM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            for (int i = 0; i < temp_list.size(); i++) {
                                mQuantityPerUom = String.valueOf(temp_list.get(position).getQuantityPerUom());
                                if (mQuantityPerUom.length() == 0 || mQuantityPerUom.equals("0") || mQuantityPerUom.equals("0.0")) {
                                    NotificationManager.DisplayAlertDialog(mContext, "Invalid Quantity Per UOM", "Invalid quantity per uom.");
                                } else {
                                    if (mIsItemTrackingRequired) {
                                        etQty.setText("0");
                                        etQtyBase.setText("0.0");
                                        etQty.setEnabled(false);
                                        spinnerUOM.setEnabled(true);
                                    } else {
                                        etQty.setText("");
                                        etQty.setEnabled(true);
                                        spinnerUOM.setEnabled(true);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            clearQtyInputFields();
                        }
                    });
                    //DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_ITEM_RECLASS);
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

    public class GetScannedStockTakeItemNoUomSpecialTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemUomParameter mItemReclassUOMListParameter;
        List<ItemUomListResultData> mUOMListResult;

        public GetScannedStockTakeItemNoUomSpecialTask(Activity activity, String itemNo, String uom) {
            mActivity = activity;
            mItemReclassUOMListParameter = new ItemUomParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mItemNo); //"80100" testing purpose

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassUOMListParameter);
            Log.d("Bin List ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemUomListResultData>> call = mApp.getNavBrokerService().ItemUomList(mItemReclassUOMListParameter);

                mUOMListResult = call.execute().body();

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
                    spinnerUOM.setAdapter(null);
                    final ArrayList<ItemUomListResultData> temp_list = new ArrayList<>();

                    if (mUOMListResult != null) {
                        temp_list.addAll(mUOMListResult);
                    }

                    spinnerUOMAdapter = new ArrayAdapter<ItemUomListResultData>(mContext, android.R.layout.simple_spinner_item, temp_list);
                    spinnerUOM.setAdapter(spinnerUOMAdapter);

                    for (int i = 0; i < temp_list.size(); i++) {
                        if (temp_list.get(i).getCode().equals(mUom)) {
                            int spinnerPos = spinnerUOMAdapter.getPosition(temp_list.get(i));
                            spinnerUOM.setSelection(spinnerPos);
                        }
                    }

                    spinnerUOM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            for (int i = 0; i < temp_list.size(); i++) {
                                mScannedItemNoQuantityPerUom = String.valueOf(temp_list.get(position).getQuantityPerUom());
                                if (mScannedItemNoQuantityPerUom.length() == 0 || mScannedItemNoQuantityPerUom.equals("0") || mScannedItemNoQuantityPerUom.equals("0.0")) {
                                    NotificationManager.DisplayAlertDialog(mContext, "Invalid Quantity Per UOM", "Invalid quantity per uom.");
                                } else {
                                    etQtyBase.setText(mScannedItemNoQuantityPerUom);
                                }
                            }
                            mCalculateScannedItemNoQuantityBasedItemReclassQuantityTask = new CalculateScannedItemNoQuantityBasedStockTakeQuantityTask((Activity) mContext, Float.parseFloat(etQty.getText().toString()), Float.parseFloat(etQtyBase.getText().toString()));
                            mCalculateScannedItemNoQuantityBasedItemReclassQuantityTask.execute((Void) null);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    //DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_ITEM_RECLASS);
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

    public class CalculateStockTakeQuantityTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        CalculateItemReclassQuantityParameter mCalculateItemReclassQuantityParameter;
        CalculateItemReclassificationResultData mCalculateItemReclassificationResultData;

        CalculateStockTakeQuantityTask(Activity activity, float mQuantity, float mQuantityPerUom) {
            mActivity = activity;
            mCalculateItemReclassQuantityParameter = new CalculateItemReclassQuantityParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mQuantity, mQuantityPerUom);

            Gson gson = new Gson();
            String json = gson.toJson(mCalculateItemReclassQuantityParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<CalculateItemReclassificationResultData> call = mApp.getNavBrokerService().CalculateItemReclassQuantity(mCalculateItemReclassQuantityParameter);

                mCalculateItemReclassificationResultData = call.execute().body();

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
                    if (mCalculateItemReclassificationResultData != null) {
                        mQuantityBased = String.valueOf(mCalculateItemReclassificationResultData.getOutputQuantity());
                        if (mQuantityBased.length() == 0 || mQuantityBased.equals("0") || mQuantityBased.equals("0.0")) {
                            etQtyBase.setText("");
                        } else {
                            etQtyBase.setText(mQuantityBased);
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

    public class GetItemCodeListTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemListParameter mItemReclassItemListParameter;
        List<ItemListResultData> itemListResult;

        GetItemCodeListTask(Activity activity) {
            mActivity = activity;
            mItemReclassItemListParameter = new ItemListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), "", "", "");

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassItemListParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemListResultData>> call = mApp.getNavBrokerService().ItemList(mItemReclassItemListParameter);

                itemListResult = call.execute().body();

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
                    final ListView itemCodeList = (ListView) findViewById(R.id.lvItemNo);
                    ArrayList<ItemListResultData> temp_list = new ArrayList<ItemListResultData>();
                    if (itemListResult != null) {
                        temp_list.addAll(itemListResult);
                    }

                    final ArrayList<String> products = new ArrayList<String>();
                    for (int x = 0; x < temp_list.size(); x++) {
                        products.add(temp_list.get(x).getItemCode());
                    }
                    adapter = new ArrayAdapter<String>(mContext, R.layout.list_item_code, R.id.item_code, products);
                    adapter.setDropDownViewResource(R.layout.list_item_code);
                    itemCodeList.setAdapter(adapter);

                    final EditText etItemNo = (EditText) findViewById(R.id.etItemNo);
                    etItemNo.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                            // When user changed the Text
                            try {
                                itemCodeList.setVisibility(View.VISIBLE);
                                adapter.getFilter().filter(cs);
                            } catch (Exception ex) {
                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                            }
                        }

                        @Override
                        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                      int arg3) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void afterTextChanged(Editable arg0) {
                            // TODO Auto-generated method stub
                            if (arg0.length() == 0) {
                                itemCodeList.setVisibility(View.GONE);
                            }

                            if(isScanned){
                                mItemNo = arg0.toString().toUpperCase();
                                mGetUserInputItemCodeandRetrieveItemDetailsTask = new GetUserInputItemCodeandRetrieveItemDetailsTask((Activity) mContext, "", "", mItemNo);
                                mGetUserInputItemCodeandRetrieveItemDetailsTask.execute((Void) null);
                            }

                        }
                    });

                    itemCodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mItemNo = (itemCodeList.getItemAtPosition(position).toString());
                            etItemNo.setText(mItemNo);
                            etItemNo.setSelection(etItemNo.getText().length());
                            etItemNo.selectAll();
                            mGetUserInputItemCodeandRetrieveItemDetailsTask = new GetUserInputItemCodeandRetrieveItemDetailsTask((Activity) mContext, "", "", mItemNo);
                            mGetUserInputItemCodeandRetrieveItemDetailsTask.execute((Void) null);
                            parent.setVisibility(View.GONE);
                        }
                    });

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

    public class GetStockTakeItemCodeListTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemListParameter mItemReclassItemListParameter;

        GetStockTakeItemCodeListTask(Activity activity, String dStrItemCat, String dStrItemDesc, String dStrItemCode) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;
            mItemReclassItemListParameter = new ItemListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), dStrItemCat, dStrItemDesc, dStrItemCode);

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassItemListParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemListResultData>> call = mApp.getNavBrokerService().ItemList(mItemReclassItemListParameter);

                myItemListResult = call.execute().body();

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
                    ArrayList<ItemListResultData> temp_list = new ArrayList<ItemListResultData>();
                    if (myItemListResult != null) {
                        temp_list.addAll(myItemListResult);
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

    //Normal search
    public class GetNormalItemReclassItemCodeListTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemListParameter mItemReclassItemListParameter;

        GetNormalItemReclassItemCodeListTask(Activity activity) {
            mActivity = activity;
            mItemReclassItemListParameter = new ItemListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), dStrItemCat, dStrItemDesc, dStrItemCode);

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassItemListParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemListResultData>> call = mApp.getNavBrokerService().ItemList(mItemReclassItemListParameter);

                xItemListResult = call.execute().body();

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
                    normal_temp_list = new ArrayList<ItemListResultData>();
                    if (xItemListResult != null) {
                        normal_temp_list.addAll(xItemListResult);
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

    //Filter search
    public class GetFilterItemReclassItemCodeListTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemListParameter mItemReclassItemListParameter;
        Dialog mDialog;

        public GetFilterItemReclassItemCodeListTask(Activity mContext, String mStrItemCat, String mStrItemDesc, String mStrItemCode, Dialog dialogItemCode) {
            mActivity = mContext;
            mItemReclassItemListParameter = new ItemListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mStrItemCat, mStrItemDesc, mStrItemCode);
            mDialog = dialogItemCode;

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassItemListParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemListResultData>> call = mApp.getNavBrokerService().ItemList(mItemReclassItemListParameter);

                nItemListResult = call.execute().body();

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
                    temp_item_list = new ArrayList<ItemListResultData>();
                    if (nItemListResult != null) {
                        if (nItemListResult.size() != 0) {
                            temp_item_list.addAll(nItemListResult);

                            temp_item_list = new ArrayList<ItemListResultData>(nItemListResult);
                            filteritemNumberArrayAdapter = new ItemNumberListArrayAdapter(mContext, temp_item_list);
                            itemNumberList.setAdapter(filteritemNumberArrayAdapter);
                            itemNumberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    ItemListResultData temp = filteritemNumberArrayAdapter.getItem(i);
                                    mIsItemTrackingRequired = temp.isItemTrackingRequired();
                                    etItemNo.setText(temp.getItemCode());
                                    etItemNo.setSelection(etItemNo.getText().length());
                                    etItemNo.selectAll();
                                    itemCodeList = (ListView) findViewById(R.id.lvItemNo);
                                    itemCodeList.setVisibility(View.GONE);
                                    if (!mIsItemTrackingRequired) {
                                        etQty.setText("");
                                        etQty.setEnabled(true);
                                    } else {
                                        etQty.setText("0");
                                        etQtyBase.setText("0.0");
                                        etQty.setEnabled(false);
                                    }
                                    etQtyBase.setEnabled(false);
                                    dStrItemCode = temp.getItemCode();
                                    dStrItemBaseUom = temp.getItemBaseUom();

                                    mGetFilterStockTakeUomTask = new GetFilterStockTakeUomTask((Activity) mContext, dStrItemCode);
                                    mGetFilterStockTakeUomTask.execute((Void) null);
                                    mDialog.dismiss();
                                }
                            });
                        }else {
                            TextView tvLblNoData = (TextView) mDialog.findViewById(R.id.lblNoData);
                            tvLblNoData.setVisibility(View.VISIBLE);
                            itemNumberList.setAdapter(null);
                        }
                    }else {
                        TextView tvLblNoData = (TextView) mDialog.findViewById(R.id.lblNoData);
                        tvLblNoData.setVisibility(View.VISIBLE);
                        itemNumberList.setAdapter(null);
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

    //Check whether user key in item code is exist and retrieve item details
    public class GetUserInputItemCodeandRetrieveItemDetailsTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemListParameter mItemReclassItemListParameter;

        public GetUserInputItemCodeandRetrieveItemDetailsTask(Activity mContext, String mStrItemCat, String mStrItemDesc, String mStrItemCode) {
            mActivity = mContext;
            mItemReclassItemListParameter = new ItemListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mStrItemCat, mStrItemDesc, mStrItemCode);

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassItemListParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemListResultData>> call = mApp.getNavBrokerService().ItemList(mItemReclassItemListParameter);

                zItemListResult = call.execute().body();

            } catch (Exception e) {
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
                    ItemListResultData tmp = null;
                    zTemp_item_list = new ArrayList<ItemListResultData>();
                    if (zItemListResult != null) {
                        zTemp_item_list.addAll(zItemListResult);
                        for (int x = 0; x < zItemListResult.size(); x++) {
                            tmp = new ItemListResultData(zItemListResult.get(x).getItemCategoryCode(), zItemListResult.get(x).getDescription(), zItemListResult.get(x).getItemCode(), zItemListResult.get(x).getItemBaseUom(), zItemListResult.get(x).isItemTrackingRequired());
                            break;
                        }

                    } else {
                        spinnerUOM.setEnabled(false);
                    }

                    if (tmp != null) {

                        for (int y = 0; y < normal_temp_list.size(); y++) {
                            if (normal_temp_list.get(y).getItemCode().equals(mItemNo)) {

                                dStrItemBaseUom = tmp.getItemBaseUom();
                                mGetFilterScanItemIsItemLotTrackingTask = new GetFilterScanItemIsItemLotTrackingTask((Activity) mContext, "", "", mItemNo);
                                mGetFilterScanItemIsItemLotTrackingTask.execute((Void) null);

                                mGetUserKeyInItemCodeUomSpecialTask = new GetUserKeyInItemCodeUomSpecialTask((Activity) mContext, mItemNo);
                                mGetUserKeyInItemCodeUomSpecialTask.execute((Void) null);

                                etQty.setEnabled(true);
                                spinnerUOM.setEnabled(true);
                                break;
                            } else {
                                spinnerUOM.setEnabled(false);
                                etQty.setText("");
                                etQty.setEnabled(false);
                                etQtyBase.setText("");
                            }
                        }
                    } else {
                        etQtyBase.setText("");
                        etQty.setEnabled(false);
                        etItemNo.requestFocus();
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

    //Check whether scanned item is item lot tracking required
    public class GetFilterScanItemIsItemLotTrackingTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemListParameter mItemReclassItemListParameter;

        public GetFilterScanItemIsItemLotTrackingTask(Activity mContext, String mStrItemCat, String mStrItemDesc, String mStrItemCode) {
            mActivity = mContext;
            mItemReclassItemListParameter = new ItemListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mStrItemCat, mStrItemDesc, mStrItemCode);

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassItemListParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemListResultData>> call = mApp.getNavBrokerService().ItemList(mItemReclassItemListParameter);

                oItemListResult = call.execute().body();

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
                    temp_item_list = new ArrayList<ItemListResultData>();
                    if (oItemListResult != null) {
                        temp_item_list.addAll(oItemListResult);
                    }

                    for (int x = 0; x < oItemListResult.size(); x++) {
                        mIsItemTrackingRequired = oItemListResult.get(x).isItemTrackingRequired();

                        if (mIsItemTrackingRequired) {
                            etQty.setText("0");
                            etQtyBase.setText("0.0");
                            etQty.setEnabled(false);
                            spinnerUOM.setEnabled(true);
                            break;
                        }
                        break;
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

    public class CalculateScannedItemNoQuantityBasedStockTakeQuantityTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        CalculateItemReclassQuantityParameter mCalculateItemReclassQuantityParameter;
        CalculateItemReclassificationResultData mCalculateItemReclassificationResultData;

        CalculateScannedItemNoQuantityBasedStockTakeQuantityTask(Activity activity, float mQuantity, float mQuantityPerUom) {
            mActivity = activity;
            mCalculateItemReclassQuantityParameter = new CalculateItemReclassQuantityParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mQuantity, mQuantityPerUom);

            Gson gson = new Gson();
            String json = gson.toJson(mCalculateItemReclassQuantityParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<CalculateItemReclassificationResultData> call = mApp.getNavBrokerService().CalculateItemReclassQuantity(mCalculateItemReclassQuantityParameter);

                mCalculateItemReclassificationResultData = call.execute().body();

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
                    if (mCalculateItemReclassificationResultData != null) {
                        mQuantityBased = String.valueOf(mCalculateItemReclassificationResultData.getOutputQuantity());
                        etQtyBase.setText(mQuantityBased);
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

    public class GetItemReclassBinListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        LocationBinListParameter mItemReclassBinListParameter;
        List<LocationBinListResultData> mBinListResult;

        public GetItemReclassBinListTask(Activity activity, String spinnerLocString) {
            mActivity = activity;

            mItemReclassBinListParameter = new LocationBinListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), spinnerLocString); //"BLUE"

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassBinListParameter);
            Log.d("Bin List ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<LocationBinListResultData>> call = mApp.getNavBrokerService().LocationBinList(mItemReclassBinListParameter);

                mBinListResult = call.execute().body();

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
                    spinnerBin.setAdapter(null);
                    ArrayList<LocationBinListResultData> temp_list = new ArrayList<>();

                    if (mBinListResult != null) {
                        if (mBinListResult.size() != 0) {
                            tvBin.setVisibility(View.VISIBLE);
                            spinnerBin.setVisibility(View.VISIBLE);
                            temp_list.addAll(mBinListResult);
                        } else {
                            mBinCode = "";
                            tvBin.setVisibility(View.VISIBLE);
                            spinnerBin.setVisibility(View.VISIBLE);
                        }
                    }

                    spinnerBinAdapter = new ArrayAdapter<LocationBinListResultData>(mContext, android.R.layout.simple_spinner_item, temp_list);
                    spinnerBin.setAdapter(spinnerBinAdapter);
                    spinnerBin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            mBinCode = spinnerBin.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            mBinCode = "";
                        }
                    });
                    //DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_ITEM_RECLASS);
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
}
