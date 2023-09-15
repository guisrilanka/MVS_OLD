package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.DataConverter;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.ItemNumberListArrayAdapter;
import com.gui.mdt.thongsieknavclient.adapters.itemreclass.itemReclassHeaderArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemListParameter;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemListResultData;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemUomListResultData;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemUomParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.CalculateItemReclassQuantityParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.CalculateItemReclassificationResultData;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.CreateItemReclassBaseResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.CreateItemReclassHeaderParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassificationEntry;
import com.gui.mdt.thongsieknavclient.model.location.LocationBinListParameter;
import com.gui.mdt.thongsieknavclient.model.location.LocationBinListResultData;
import com.gui.mdt.thongsieknavclient.model.location.LocationListParameter;
import com.gui.mdt.thongsieknavclient.model.location.LocationListResultData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class AddNewItemReclassificationHeaderActivity extends BaseActivity {

    InsertItemReclassificationHeaderItemEntriesTask mInsertItemReclassificationHeaderItemEntriesTask = null;
    QueryItemReclassItemUomListTask mQueryItemReclassItemUomList = null;
    GetItemReclassBinListTask mGetItemReclassBinTask = null;
    GetItemReclassNewBinListTask mGetItemReclassNewBinTask = null;
    GetItemReclassLocationTask mGetItemReclassLocationTask = null;
    GetItemReclassUomTask mGetItemReclassUomTask = null;
    GetFilterItemReclassItemCodeListTask mGetFilterItemReclassItemCodeListTask = null;
    CalculateItemReclassQuantityTask mCalculateItemReclassQuantity = null;
    GetNormalItemReclassItemCodeListTask mGetNormalItemReclassItemCodeListTask = null;
    GetUserInputItemCodeandRetrieveItemDetailsTask mGetUserInputItemCodeandRetrieveItemDetailsTask = null;
    GetUserKeyInItemCodeUomSpecialTask mGetUserKeyInItemCodeUomSpecialTask = null;
    GetFilterScannedItemIsLotTrackingRequiredTask mGetFilterScannedItemIsLotTrackingRequiredTask = null;
    GetItemCodeListTask mGetItemCodeListTask = null;

    ListView itemNumberList;
    ListView itemCodeList;
    private ArrayAdapter<String> adapter;
    private NavClientApp mApp;
    private Context mContext;

    private Calendar calPostingDate;
    private TextView tvBin;
    private TextView tvNewBin;
    private EditText textPostingDate;
    private EditText etQty;
    private EditText etItemNo;
    private EditText etQtyBase;
    private EditText etItemReclsNo;
    private Button btnSave;
    private Button btnSearch;
    private Button btnDelete;
    private Button btnCancel;
    private Button btnLookUp;
    private Spinner spinnerUOM;
    private Spinner spinnerLoc;
    private Spinner spinnerNewLoc;
    private Spinner spinnerBin;
    private Spinner spinnerNewBin;

    private String dStrItemCat;
    private String dStrItemCode;
    private String dStrItemDesc;
    private String dStrItemBaseUom;
    private String mDocNo;
    private String mLoc;
    private String mNewLoc;
    private String mBin;
    private String mNewBin;
    private String ScannedSerialNo;
    private String mQuantity;
    private String mItemNo;
    private String mQuantityPerUom;
    private String mQuantityBased;
    private String mUom;
    private String scannedItemNo;
    private String mPostDate;
    private String spinnerLocString;
    private String spinnerNewLocString;
    private int mLineNo;
    private int entryIndexCounter;
    private boolean isShowBinCode = false;
    private boolean isScanned = false;
    private boolean mIsItemTrackingRequired = false;
    private boolean mIsItemEditText = true;

    ArrayAdapter<LocationListResultData> spinnerLocAdapter;
    ArrayAdapter<LocationBinListResultData> spinnerBinAdapter;
    ArrayAdapter<ItemUomListResultData> spinnerUOMAdapter;
    ItemNumberListArrayAdapter itemNumberArrayAdapter;
    ItemNumberListArrayAdapter filteritemNumberArrayAdapter;
    itemReclassHeaderArrayAdapter historyAdapter;
    List<ItemUomListResultData> mItemUomListResult;
    CalculateItemReclassificationResultData mCalculateItemReclassificationResultData;
    ItemReclassificationEntry temp_list;
    List<ItemListResultData> mItemListResult;
    ArrayList<ItemListResultData> temp_item_list;
    List<ItemListResultData> xItemListResult;
    ArrayList<ItemListResultData> normal_temp_list;
    List<ItemListResultData> oItemListResult;
    ArrayList<ItemListResultData> zTemp_item_list;
    List<ItemListResultData> zItemListResult;

    protected int getLayoutResource() {
        return R.layout.activity_add_new_transfer_order;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), ItemReclassificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_transfer_order);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Item Reclass");
        setSupportActionBar(toolbar);

        etQty = (EditText) findViewById(R.id.etQty);
        etItemNo = (EditText) findViewById(R.id.etItemNo);
        etQtyBase = (EditText) findViewById(R.id.etQtyBase);
        etItemReclsNo = (EditText) findViewById(R.id.etTransferNo);
        textPostingDate = (EditText) findViewById(R.id.editPostingDate);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnLookUp = (Button) findViewById(R.id.btnShowItemNoDialog);
        spinnerUOM = (Spinner) findViewById(R.id.spinnerUOM);
        spinnerLoc = (Spinner) findViewById(R.id.spLoc);
        spinnerNewLoc = (Spinner) findViewById(R.id.spNewLoc);
        tvBin = (TextView) findViewById(R.id.tvBin);
        tvNewBin = (TextView) findViewById(R.id.tvNewBin);
        spinnerBin = (Spinner) findViewById(R.id.spBin);
        spinnerNewBin = (Spinner) findViewById(R.id.spNewBin);
        itemCodeList = (ListView) findViewById(R.id.lvItemNo);

        etItemNo.setEnabled(true);
        etQty.setEnabled(false);
        etQtyBase.setEnabled(false);

        ScannedSerialNo = "";
        entryIndexCounter = 0;
        scannedItemNo = "";

        dStrItemCat = "";
        dStrItemDesc = "";
        dStrItemCode = "";

        calPostingDate = Calendar.getInstance();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prepareAddEntryParameters();

                if (validateEntry()) {
                    mInsertItemReclassificationHeaderItemEntriesTask = new InsertItemReclassificationHeaderItemEntriesTask((Activity) mContext);
                    mInsertItemReclassificationHeaderItemEntriesTask.execute((Void) null);
                }

            }
        });

        Button btnLookUp = (Button) findViewById(R.id.btnShowItemNoDialog);
        mGetNormalItemReclassItemCodeListTask = new GetNormalItemReclassItemCodeListTask((Activity) mContext);
        mGetNormalItemReclassItemCodeListTask.execute((Void) null);
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

                                mGetItemReclassUomTask = new GetItemReclassUomTask((Activity) mContext, dStrItemCode);
                                mGetItemReclassUomTask.execute((Void) null);
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

                final EditText editFilterItemCat = (EditText) dialog.findViewById(R.id.editFilterItemCategory);
                final EditText editFilterItemCode = (EditText) dialog.findViewById(R.id.editFilterItemCode);
                final EditText editFilterItemDesc = (EditText) dialog.findViewById(R.id.editFilterItemDescription);

                btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
                btnSearch.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dStrItemCat = editFilterItemCat.getText().toString();
                        dStrItemCode = editFilterItemCode.getText().toString();
                        dStrItemDesc = editFilterItemDesc.getText().toString();

                        mGetFilterItemReclassItemCodeListTask = new GetFilterItemReclassItemCodeListTask((Activity) mContext, editFilterItemCat.getText().toString(), editFilterItemDesc.getText().toString(), editFilterItemCode.getText().toString(), dialog);
                        mGetFilterItemReclassItemCodeListTask.execute((Void) null);

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

        initializeDateFields();

        mGetItemCodeListTask = new GetItemCodeListTask((Activity) mContext);
        mGetItemCodeListTask.execute((Void) null);

        mGetItemReclassLocationTask = new GetItemReclassLocationTask((Activity) mContext);
        mGetItemReclassLocationTask.execute((Void) null);

        etQty.addTextChangedListener(new CustomTextWatcher(etQty, !mIsItemEditText));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ItemReclassificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
//                Handler handler = new Handler();
//                handler.postDelayed(
//                        new Runnable() {
//                            public void run() {
//                                mGetUserInputItemCodeandRetrieveItemDetailsTask = new GetUserInputItemCodeandRetrieveItemDetailsTask((Activity) mContext, "", "", mItemNo);
//                                mGetUserInputItemCodeandRetrieveItemDetailsTask.execute((Void) null);
//                            }
//                        }, 4000);

            } else {

                mQuantity = s.toString();
                if (mQuantity.length() == 0 || mQuantity.equals("0") || mQuantity.equals("0.0") || mQuantity.equals("")) {
                    etQtyBase.setText("");
                } else {
                    if (mQuantityPerUom != null) {
                        if (mQuantityPerUom != null) {
                            if (mQuantity != null && mQuantity.length() != 0 && !mQuantity.equals("0.0") && !mQuantity.equals("0") && !mQuantity.equals(".")) {
                                mCalculateItemReclassQuantity = new CalculateItemReclassQuantityTask((Activity) mContext, Float.parseFloat(mQuantity), Float.parseFloat(mQuantityPerUom));
                                mCalculateItemReclassQuantity.execute((Void) null);
                            }
                        }
                    }
                }
            }
        }
    }

    private void initializeDateFields() {
        textPostingDate = (EditText) findViewById(R.id.editPostingDate);
        textPostingDate.setInputType(InputType.TYPE_NULL);

        final DatePickerDialog.OnDateSetListener dateOfProductionPicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calPostingDate.set(Calendar.YEAR, year);
                calPostingDate.set(Calendar.MONTH, monthOfYear);
                calPostingDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
                textPostingDate.setText(simpleDateFormat.format(calPostingDate.getTime()));
            }
        };

        textPostingDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    new DatePickerDialog(mContext, dateOfProductionPicker, calPostingDate.get(Calendar.YEAR), calPostingDate.get(Calendar.MONTH), calPostingDate.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        textPostingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mContext, dateOfProductionPicker, calPostingDate.get(Calendar.YEAR), calPostingDate.get(Calendar.MONTH), calPostingDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private boolean validateEntry() {

        if (!checkRequiredFields()) {
            return false;
        }
        return true;
    }

    private boolean checkRequiredFields() {
        mPostDate = textPostingDate.getText().toString();
        mDocNo = etItemReclsNo.getText().toString();
        mItemNo = etItemNo.getText().toString();
        mQuantity = etQty.getText().toString();
        mQuantityBased = etQtyBase.getText().toString();

        if (mItemNo.length() == 0 || mItemNo.equals("")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_ITEMNO, NotificationManager.ALERT_MSG_INVALID_ITEMNO);
            return false;
        }

        if (!mIsItemTrackingRequired) {
            if (mQuantity.length() == 0 || mQuantity.equals("0") || mQuantity.equals("0.0") || mQuantity.startsWith(".") || mQuantity.endsWith(".")) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                return false;
            }
        } else {
            if (mQuantity.length() == 0 || mQuantity.equals("") || mQuantity.isEmpty()) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                return false;
            }
        }

        if (!mIsItemTrackingRequired) {
            if (mQuantityBased.length() == 0 || mQuantityBased.equals("0") || mQuantityBased.equals("0.0")) {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QTY_BASED, NotificationManager.ALERT_MSG_INVALID_QTY_BASED);
                return false;
            }
        }

        if (spinnerLoc == null || spinnerLoc.getSelectedItem() == null) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOCATION, NotificationManager.ALERT_MSG_INVALID_LOCATION);
            return false;
        } else {
            mLoc = spinnerLoc.getSelectedItem().toString();
        }

        if (spinnerNewLoc == null || spinnerNewLoc.getSelectedItem() == null) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_NEW_LOCATION, NotificationManager.ALERT_MSG_INVALID_NEW_LOCATION);
            return false;
        } else {
            mNewLoc = spinnerNewLoc.getSelectedItem().toString();
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

        mQuantity = etQty.getText().toString();
        mItemNo = etItemNo.getText().toString().toUpperCase();

        if (spinnerLoc == null || spinnerLoc.getSelectedItem() == null) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOCATION, NotificationManager.ALERT_MSG_INVALID_LOCATION);
        } else {
            mLoc = spinnerLoc.getSelectedItem().toString();
        }

        if (spinnerNewLoc == null || spinnerNewLoc.getSelectedItem() == null) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_NEW_LOCATION, NotificationManager.ALERT_MSG_INVALID_NEW_LOCATION);
        } else {
            mNewLoc = spinnerNewLoc.getSelectedItem().toString();
        }
    }

    private void clearQtyInputFields() {
        etQtyBase.setText("");
    }

    public class InsertItemReclassificationHeaderItemEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        CreateItemReclassHeaderParameter mCreateItemReclassHeaderParameter;
        CreateItemReclassBaseResult mReturnBaseResult;

        InsertItemReclassificationHeaderItemEntriesTask(Activity activity) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);
            mActivity = activity;
            String posting_date = DataConverter.ConvertDateToYearMonthDay(mPostDate);
            mCreateItemReclassHeaderParameter = new CreateItemReclassHeaderParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mDocNo, posting_date, mItemNo.toUpperCase(), "", mLoc, mNewLoc, mBin, mNewBin, Float.parseFloat(mQuantity), mUom, "", "", "", "");

            Gson gson = new Gson();
            String json = gson.toJson(mCreateItemReclassHeaderParameter);
            Log.d("JSON", json);

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<CreateItemReclassBaseResult> call = mApp.getNavBrokerService().CreateItemReclass(mCreateItemReclassHeaderParameter);

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

                    if (mReturnBaseResult != null) {

                        if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {

                            mLineNo = mReturnBaseResult.getReclassHeaderLineNo();

                            Intent intent = new Intent(getBaseContext(), ItemReclassificationLineActivity.class);
                            intent.putExtra("IS_SHOW_BIN_CODE", isShowBinCode);
                            intent.putExtra("RECLASS_HEADER_LINE_NO", mLineNo);
                            startActivity(intent);

                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_FAILED, mReturnBaseResult.getMessage());
                        }

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

    //---check whether user key in item code is exist and get the item details---//
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

                                mGetFilterScannedItemIsLotTrackingRequiredTask = new GetFilterScannedItemIsLotTrackingRequiredTask((Activity) mContext, "", "", mItemNo);
                                mGetFilterScannedItemIsLotTrackingRequiredTask.execute((Void) null);

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

    //Get user key in item code's uom
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

    //Check whether scan item is lot tracking required
    public class GetFilterScannedItemIsLotTrackingRequiredTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemListParameter mItemReclassItemListParameter;

        public GetFilterScannedItemIsLotTrackingRequiredTask(Activity mContext, String mStrItemCat, String mStrItemDesc, String mStrItemCode) {

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

    //NOT USING
    public class QueryItemReclassItemUomListTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemUomParameter mItemReclassItemUomListParameter;

        QueryItemReclassItemUomListTask(Activity activity, String dStrItemCode) {
            mActivity = activity;
            mItemReclassItemUomListParameter = new ItemUomParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), dStrItemCode);

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassItemUomListParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<ItemUomListResultData>> call = mApp.getNavBrokerService().ItemUomList(mItemReclassItemUomListParameter);

                mItemUomListResult = call.execute().body();

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

                    ArrayList<ItemUomListResultData> temp_list = new ArrayList<ItemUomListResultData>();
                    if (mItemUomListResult != null) {
                        temp_list.addAll(mItemUomListResult);
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
                    final ArrayList<ItemListResultData> temp_list = new ArrayList<ItemListResultData>();
                    if (itemListResult != null) {
                        temp_list.addAll(itemListResult);
                    }

                    final ArrayList<String> products = new ArrayList<String>();
                    for (int x = 0; x < temp_list.size(); x++) {
                        products.add(temp_list.get(x).getItemCode());
                    }
                    adapter = new ArrayAdapter<String>(mContext, R.layout.list_item_code, R.id.item_code, products);
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
                        }
                    });

                    itemCodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            ItemListResultData temp = itemListResult.get(position);
//                            mIsItemTrackingRequired = temp.isItemTrackingRequired();
                            mItemNo = (itemCodeList.getItemAtPosition(position).toString());
                            etItemNo.setText(mItemNo);
                            etItemNo.setSelection(etItemNo.getText().length());
                            etItemNo.selectAll();

                            mGetUserInputItemCodeandRetrieveItemDetailsTask = new GetUserInputItemCodeandRetrieveItemDetailsTask((Activity) mContext, "", "", mItemNo);
                            mGetUserInputItemCodeandRetrieveItemDetailsTask.execute((Void) null);

//                            mGetItemReclassUomTask = new GetItemReclassUomTask((Activity) mContext, mItemNo);
//                            mGetItemReclassUomTask.execute((Void) null);

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

                mItemListResult = call.execute().body();

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
                    if (mItemListResult != null) {
                        if (mItemListResult.size() != 0) {
                            temp_item_list.addAll(mItemListResult);

                            temp_item_list = new ArrayList<ItemListResultData>(mItemListResult);
                            filteritemNumberArrayAdapter = new ItemNumberListArrayAdapter(mContext, temp_item_list);
                            itemNumberList.setAdapter(filteritemNumberArrayAdapter);
                            itemNumberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                        isScanned = false;
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

                                    mGetItemReclassUomTask = new GetItemReclassUomTask((Activity) mContext, dStrItemCode);
                                    mGetItemReclassUomTask.execute((Void) null);
                                    mDialog.dismiss();
                                }
                            });
                        } else {
                            TextView tvLblNoData = (TextView) mDialog.findViewById(R.id.lblNoData);
                            tvLblNoData.setVisibility(View.VISIBLE);
                            itemNumberList.setAdapter(null);
                        }
                    } else {
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

    public class GetItemReclassLocationTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        LocationListParameter mItemReclassificationListSearchParameter;
        List<LocationListResultData> mLocationListResult;

        public GetItemReclassLocationTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;
            mItemReclassificationListSearchParameter = new LocationListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword());

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassificationListSearchParameter);
            Log.d("Location List ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<LocationListResultData>> call = mApp.getNavBrokerService().LocationList(mItemReclassificationListSearchParameter);
                mLocationListResult = call.execute().body();

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

                    spinnerLoc.setAdapter(null);
                    spinnerNewLoc.setAdapter(null);
                    final ArrayList<LocationListResultData> temp_list = new ArrayList<>();

                    if (mLocationListResult != null) {
                        temp_list.addAll(mLocationListResult);
                    }

                    for (int i = 0; i < temp_list.size(); i++) {   //isShowBinCode = true;
                        isShowBinCode = temp_list.get(i).isShowBinCode();
                        if (isShowBinCode) {
                            isShowBinCode = true;
                            break;
                        }
                    }

                    spinnerLocAdapter = new ArrayAdapter<LocationListResultData>(mContext, android.R.layout.simple_spinner_item, temp_list);
                    spinnerLoc.setAdapter(spinnerLocAdapter);
                    spinnerLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinnerLocString = parent.getSelectedItem().toString();
                            if (spinnerLocString != null) {
                                if (temp_list.get(position).isShowBinCode()) {
                                    mGetItemReclassBinTask = new GetItemReclassBinListTask((Activity) mContext, spinnerLocString);
                                    mGetItemReclassBinTask.execute((Void) null);
                                } else {
                                    mBin = "";
                                    tvBin.setVisibility(View.GONE);
                                    spinnerBin.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    spinnerNewLoc.setAdapter(spinnerLocAdapter);
                    spinnerNewLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinnerNewLocString = parent.getSelectedItem().toString();
                            if (spinnerNewLocString != null) {
                                if (temp_list.get(position).isShowBinCode()) {
                                    mGetItemReclassNewBinTask = new GetItemReclassNewBinListTask((Activity) mContext, spinnerNewLocString);
                                    mGetItemReclassNewBinTask.execute((Void) null);
                                } else {
                                    mNewBin = "";
                                    tvNewBin.setVisibility(View.GONE);
                                    spinnerNewBin.setVisibility(View.GONE);
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
                            mBin = "";
                            tvBin.setVisibility(View.VISIBLE);
                            spinnerBin.setVisibility(View.VISIBLE);
                        }
                    }

                    spinnerBinAdapter = new ArrayAdapter<LocationBinListResultData>(mContext, android.R.layout.simple_spinner_item, temp_list);
                    spinnerBin.setAdapter(spinnerBinAdapter);
                    spinnerBin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            mBin = parent.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            mBin = "";
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

    //Spinner New Bin task
    public class GetItemReclassNewBinListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        LocationBinListParameter mItemReclassNewBinListParameter;
        List<LocationBinListResultData> mNewBinListResult;

        public GetItemReclassNewBinListTask(Activity activity, String spinnerLocString) {

            mActivity = activity;
            mItemReclassNewBinListParameter = new LocationBinListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), spinnerLocString); //"BLUE"

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassNewBinListParameter);
            Log.d("Bin List ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<LocationBinListResultData>> call = mApp.getNavBrokerService().LocationBinList(mItemReclassNewBinListParameter);

                mNewBinListResult = call.execute().body();

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

                    spinnerNewBin.setAdapter(null);
                    ArrayList<LocationBinListResultData> temp_list = new ArrayList<>();

                    if (mNewBinListResult != null) {
                        if (mNewBinListResult.size() != 0) {
                            tvNewBin.setVisibility(View.VISIBLE);
                            spinnerNewBin.setVisibility(View.VISIBLE);
                            temp_list.addAll(mNewBinListResult);

                        } else {
                            mNewBin = "";
                            tvNewBin.setVisibility(View.VISIBLE);
                            spinnerNewBin.setVisibility(View.VISIBLE);
                        }
                    }

                    spinnerBinAdapter = new ArrayAdapter<LocationBinListResultData>(mContext, android.R.layout.simple_spinner_item, temp_list);
                    spinnerNewBin.setAdapter(spinnerBinAdapter);
                    spinnerNewBin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            mNewBin = parent.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            mNewBin = "";
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

    public class GetItemReclassUomTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemUomParameter mItemReclassUOMListParameter;
        List<ItemUomListResultData> mUOMListResult;

        public GetItemReclassUomTask(Activity activity, String mItemNo) {
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
                                if (mQuantityPerUom != null || mQuantityPerUom != "" || !mQuantityPerUom.equals("0.0") || !mQuantityPerUom.equals("0")) {

                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

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

    public class CalculateItemReclassQuantityTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        CalculateItemReclassQuantityParameter mCalculateItemReclassQuantityParameter;

        CalculateItemReclassQuantityTask(Activity activity, float mQuantity, float mQuantityPerUom) {
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
}
