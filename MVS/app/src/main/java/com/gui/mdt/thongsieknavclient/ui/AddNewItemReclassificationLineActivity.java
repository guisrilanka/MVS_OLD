package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.DataConverter;
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
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.InsertReclassLineParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineData;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineUpdateParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassificationNewLineEntry;
import com.gui.mdt.thongsieknavclient.model.location.LocationBinListParameter;
import com.gui.mdt.thongsieknavclient.model.location.LocationBinListResultData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class AddNewItemReclassificationLineActivity extends BaseActivity {

    PostUpdateItemDetailsTask mPostUpdateItemDetailsTask = null;
    CreateNewItemReclassLineItemEntriesTask mCreateNewItemReclassLineItemEntriesTask = null;
    GetFilterItemReclassUomTask mGetFilterItemReclassUomTask = null;
    GetItemReclassUomSpecialTask mGetItemReclassUomSpecialTask = null;
    GetScannedItemReclassItemNoUomSpecialTask mGetScannedItemReclassItemNoUomSpecialTask = null;
    GetItemReclassBinListTask mGetItemReclassBinTask = null;
    GetItemReclassNewBinListTask mGetItemReclassNewBinTask = null;
    CalculateScannedItemNoQuantityBasedItemReclassQuantityTask mCalculateScannedItemNoQuantityBasedItemReclassQuantityTask = null;
    CalculateItemReclassQuantityTask mCalculateItemReclassQuantity = null;
    GetFilterScannedItemIsLotTrackingRequiredTask mGetFilterScannedItemIsLotTrackingRequiredTask = null;
    GetItemReclassItemCodeListTask mGetItemReclassItemCodeTask = null;
    GetItemReclassTask mGetItemReclassLineDetailsTask = null;
    GetNormalItemReclassItemCodeListTask mGetNormalItemReclassItemCodeListTask = null;
    GetFilterItemReclassItemCodeListTask mFilterGetItemReclassItemCodeTask = null;
    GetUserInputItemCodeandRetrieveItemDetailsTask mGetUserInputItemCodeandRetrieveItemDetailsTask = null;
    GetUserKeyInItemCodeUomSpecialTask mGetUserKeyInItemCodeUomSpecialTask = null;
    GetItemCodeListTask mGetItemCodeListTask = null;

    Context mContext;
    ListView itemNumberList;
    ListView itemCodeList;
    private ArrayAdapter<String> adapter;
    private NavClientApp mApp;
    private Spinner spinnerBin;
    private Spinner spinnerNewBin;
    private Spinner spinnerUOM;
    private Button btnSearch;
    private Button btnCancel;
    private Button btnSave;
    private Button btnLookUp;
    private EditText etQty;
    private EditText etItemNo;
    private EditText etQtyBase;
    private TextView tvDocNo;
    private TextView tvLoc;
    private TextView tvNewLoc;
    private TextView tvCreatedBy;
    private TextView tvPostingDate;
    private TextView tvBin;
    private TextView tvNewBin;
    private TextView lblBin;
    private TextView lblNewBin;
    private LinearLayout llCreator;

    private String mDocNo;
    private String mLoc;
    private String mNewLoc;
    private String mCreatedby;
    private String mPostDate;
    private String mUom;
    private String mQuantity;
    private String mQuantityPerUom;
    private String mScannedItemNoQuantityPerUom;
    private String mQuantityBased;
    private String mItemNo;
    private String mBinCode;
    private String mNewBinCode;
    private String IsScanning;
    private String IsNew;
    private String scannedString;
    private String scannedSerialNo;
    private String scannedLotNo;
    private String scannedQuantity;
    private String dStrItemCat;
    private String dStrItemCode;
    private String dStrItemDesc;
    private String dStrItemBaseUom;
    private int entryIndexCounter;
    private int mHeaderLineNumber;
    private int mLineNo;
    private boolean mIsEdit = false;
    private boolean isItemExist = false;
    private boolean mIsShowBinCode = false;
    private boolean mIsShowNewBinCode = false;
    private boolean isScanned = false;
    private boolean isStarted = false;
    private boolean mIsItemTrackingRequired = true;
    private boolean mIsItemEditText = true;
    private boolean mIsQuantityEditText = false;

    ItemReclassResult mItemReclassResult;
    ArrayAdapter<LocationBinListResultData> spinnerBinAdapter;
    ArrayAdapter<ItemUomListResultData> spinnerUOMAdapter;
    ItemNumberListArrayAdapter itemNumberArrayAdapter;
    ArrayList<ItemReclassificationNewLineEntry> tmp_list;
    ArrayList<ItemReclassLineData> temp_list;
    List<ItemListResultData> mItemListResult;
    ArrayList<ItemListResultData> mList;
    ItemNumberListArrayAdapter filteritemNumberArrayAdapter;
    List<ItemListResultData> xItemListResult;
    ArrayList<ItemListResultData> normal_temp_list;
    ArrayList<ItemListResultData> temp_item_list;
    List<ItemListResultData> nItemListResult;
    List<ItemListResultData> oItemListResult;
    ArrayList<ItemListResultData> zTemp_item_list;
    List<ItemListResultData> zItemListResult;

    protected int getLayoutResource() {
        return R.layout.activity_add_new_item_reclassification_line;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item_reclassification_line);

        mContext = this;
        mApp = (NavClientApp) getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        entryIndexCounter = 0;
        scannedLotNo = "";
        scannedSerialNo = "";
        tmp_list = new ArrayList<ItemReclassificationNewLineEntry>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mHeaderLineNumber = extras.getInt("RECLASS_HEADER_LINE_NO");
            mDocNo = extras.getString("DOCUMENT_NO");
            IsScanning = extras.getString("IS_SCANNING");
            IsNew = extras.getString("IS_NEW_ADD");

            dStrItemCat = "";
            dStrItemDesc = "";
            dStrItemCode = "";

            if (IsScanning.contains("YES")) {
                isScanned = true;
                scannedString = extras.getString("SCAN_CONTENT");
            }

            if (IsNew.equals("YES")) {
                toolbar.setTitle("New Item Reclass Line");

                mGetNormalItemReclassItemCodeListTask = new GetNormalItemReclassItemCodeListTask((Activity) mContext);
                mGetNormalItemReclassItemCodeListTask.execute((Void) null);

                mGetItemReclassItemCodeTask = new GetItemReclassItemCodeListTask((Activity) mContext, dStrItemCat, dStrItemDesc, dStrItemCode);
                mGetItemReclassItemCodeTask.execute((Void) null);

            } else {
                toolbar.setTitle("Edit Line");
                toolbar.setSubtitle("Item Reclass: " + mDocNo);

                mGetItemReclassItemCodeTask = new GetItemReclassItemCodeListTask((Activity) mContext, dStrItemCat, dStrItemDesc, dStrItemCode);
                mGetItemReclassItemCodeTask.execute((Void) null);

                if (DataManager.getInstance().transitionItemReclassLinesData.getItemNo() != null) {
                    mItemNo = DataManager.getInstance().transitionItemReclassLinesData.getItemNo();
                }

                if (DataManager.getInstance().transitionItemReclassLinesData.getLineNo() != 0) {
                    mLineNo = DataManager.getInstance().transitionItemReclassLinesData.getLineNo();
                }

                if (DataManager.getInstance().transitionItemReclassLinesData.getQuantity() != 0) {
                    mQuantity = String.valueOf(DataManager.getInstance().transitionItemReclassLinesData.getQuantity());
                }

                if (DataManager.getInstance().transitionItemReclassLinesData.getUom() != null) {
                    mUom = DataManager.getInstance().transitionItemReclassLinesData.getUom();
                }

                if (DataManager.getInstance().transitionItemReclassLinesData.getQuantityBase() != 0) {
                    mQuantityBased = String.valueOf(DataManager.getInstance().transitionItemReclassLinesData.getQuantityBase());
                }

                if (!mIsShowBinCode && !mIsShowNewBinCode) {
                    if (DataManager.getInstance().transitionItemReclassLinesData.getBinCode() != null) {
                        mBinCode = DataManager.getInstance().transitionItemReclassLinesData.getBinCode();
                    }
                }

                if (!mIsShowBinCode && !mIsShowNewBinCode) {
                    if (DataManager.getInstance().transitionItemReclassLinesData.getNewBinCode() != null) {
                        mNewBinCode = DataManager.getInstance().transitionItemReclassLinesData.getNewBinCode();
                    }
                }
            }

            Log.d("RECLASS_HEADER_LINE_NO", String.valueOf(mHeaderLineNumber));
            mGetItemReclassLineDetailsTask = new GetItemReclassTask((Activity) mContext, mHeaderLineNumber);
            mGetItemReclassLineDetailsTask.execute((Void) null);

        } else {
            NotificationManager.HideProgressDialog();
        }

        lblBin = (TextView) findViewById(R.id.lblBin);
        lblNewBin = (TextView) findViewById(R.id.lblNewBin);
        llCreator = (LinearLayout) findViewById(R.id.llCreator);
        etQty = (EditText) findViewById(R.id.etQty);
        etItemNo = (EditText) findViewById(R.id.etItemNo);
        etQtyBase = (EditText) findViewById(R.id.etQtyBase);
        btnLookUp = (Button) findViewById(R.id.btnShowItemNoDialog);
        spinnerUOM = (Spinner) findViewById(R.id.spinnerUOM);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        itemCodeList = (ListView) findViewById(R.id.lvItemNo);

        tvDocNo = (TextView) findViewById(R.id.lblDocNo);
        tvLoc = (TextView) findViewById(R.id.lblLoc);
        tvNewLoc = (TextView) findViewById(R.id.lblNewLoc);
        tvCreatedBy = (TextView) findViewById(R.id.lblCreator);
        tvPostingDate = (TextView) findViewById(R.id.lblPostDate);
        tvBin = (TextView) findViewById(R.id.tvBin);
        tvNewBin = (TextView) findViewById(R.id.tvNewBin);
        spinnerBin = (Spinner) findViewById(R.id.spBin);
        spinnerNewBin = (Spinner) findViewById(R.id.spNewBin);

        etItemNo.setEnabled(true);
        if (IsNew.equals("YES")) {
            etQty.setEnabled(false);
            etQtyBase.setEnabled(false);
            etItemNo.setText("");
            etQty.setText("");
            etQtyBase.setText("");

        } else {

            etItemNo.setText(mItemNo);
            mGetItemReclassUomSpecialTask = new GetItemReclassUomSpecialTask((Activity) mContext, etItemNo.getText().toString());
            mGetItemReclassUomSpecialTask.execute((Void) null);
            etQty.setText(mQuantity);
            if (mQuantityBased.length() == 0 || mQuantityBased.equals("0") || mQuantityBased.equals("0.0")) {
            } else {
                etQtyBase.setText(mQuantityBased);
            }

        }

        initializeDataEntry();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prepareAddEntryParameters();
                if (validateEntry()) {
                    if (IsNew.equals("YES")) {
                        mCreateNewItemReclassLineItemEntriesTask = new CreateNewItemReclassLineItemEntriesTask((Activity) mContext);
                        mCreateNewItemReclassLineItemEntriesTask.execute((Void) null);

                    } else {
                        mPostUpdateItemDetailsTask = new PostUpdateItemDetailsTask((Activity) mContext, mLineNo, mItemNo, "", Float.parseFloat(mQuantity), mUom, mBinCode, mNewBinCode);
                        mPostUpdateItemDetailsTask.execute((Void) null);
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

//                        isScanned = false;
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

                                mGetFilterItemReclassUomTask = new GetFilterItemReclassUomTask((Activity) mContext, dStrItemCode);
                                mGetFilterItemReclassUomTask.execute((Void) null);
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
                Intent intent = new Intent(getBaseContext(), ItemReclassificationLineActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNumber);
                intent.putExtra("IS_SCANNING", "NO");
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
                mQuantity = s.toString();
                if (mQuantity.length() == 0 || mQuantity.equals("0") || mQuantity.equals("0.0") || mQuantity.equals("")) {
                    etQtyBase.setText("");
                } else {
                    if (mScannedItemNoQuantityPerUom != null || mQuantityPerUom != null) {
                        if (mQuantityPerUom != null) {
                            if (mQuantity != null && mQuantity.length() != 0 && !mQuantity.equals("0.0") && !mQuantity.equals("0") && !mQuantity.equals(".")) {
                                mCalculateItemReclassQuantity = new CalculateItemReclassQuantityTask((Activity) mContext, Float.parseFloat(mQuantity), Float.parseFloat(mQuantityPerUom));
                                mCalculateItemReclassQuantity.execute((Void) null);
                            }
                        } else {
                            if (mQuantity != null && mQuantity.length() != 0 && !mQuantity.equals("0.0") && !mQuantity.equals("0") && !mQuantity.equals(".")) {
                                mCalculateItemReclassQuantity = new CalculateItemReclassQuantityTask((Activity) mContext, Float.parseFloat(mQuantity), Float.parseFloat(mScannedItemNoQuantityPerUom));
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

    private void prepareAddEntryParameters() {
        mQuantity = etQty.getText().toString();
        mItemNo = etItemNo.getText().toString().toUpperCase();
    }

    private boolean validateEntry() {
        if (!checkRequiredFields()) {
            return false;
        }
        return true;
    }

    private boolean checkRequiredFields() {

        mItemNo = etItemNo.getText().toString();
        mQuantity = etQty.getText().toString();
        mQuantityBased = etQtyBase.getText().toString();

        if (mIsShowBinCode) {
            mBinCode = spinnerBin.getSelectedItem().toString();
        } else {
            mBinCode = "";
        }

        if (mIsShowNewBinCode) {
            mNewBinCode = spinnerNewBin.getSelectedItem().toString();
        } else {
            mNewBinCode = "";
        }

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

        if (mLoc.length() == 0 || mLoc.equals("")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_LOCATION, NotificationManager.ALERT_MSG_INVALID_LOCATION);
            return false;
        }

        if (mNewLoc.length() == 0 || mNewLoc.equals("")) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_NEW_LOCATION, NotificationManager.ALERT_MSG_INVALID_NEW_LOCATION);
            return false;
        }

        if (spinnerUOM == null || spinnerUOM.getSelectedItem() == null) {
            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_UOM, NotificationManager.ALERT_MSG_INVALID_UOM);
            return false;
        } else {
            mUom = spinnerUOM.getSelectedItem().toString();
        }

        if (mIsShowBinCode) {
            if (mBinCode.length() == 0 || mBinCode.equals("")) {
                NotificationManager.DisplayAlertDialog(mContext, "Error", "Invalid bin code.");
                return false;
            }
        }

        if (mIsShowNewBinCode) {
            if (mNewBinCode.length() == 0 || mNewBinCode.equals("")) {
                NotificationManager.DisplayAlertDialog(mContext, "Error", "Invalid new bin code.");
                return false;
            }
        }
        return true;
    }

    private void initializeDataEntry() {
        if (isScanned) {
            String[] splitString = scannedString.split("\\|");

            if (splitString[0] != null) {
                mItemNo = splitString[0];
            } else {
                NotificationManager.ShowProgressDialog(mContext, "Invalid Item Code", "Barcode contains no item code.");
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
            mGetScannedItemReclassItemNoUomSpecialTask = new GetScannedItemReclassItemNoUomSpecialTask((Activity) mContext, etItemNo.getText().toString(), mUom);
            mGetScannedItemReclassItemNoUomSpecialTask.execute((Void) null);
            enableTextFieldsEditing(true);
        }
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

    private boolean checkItemExist(final String itemNo, String Uom, String scannedQty) {
        if (mItemReclassResult != null && mItemReclassResult.getItemReclassData().getItemReclassLineDatasList() != null) {
            for (int i = 0; i < mItemReclassResult.getItemReclassData().getItemReclassLineDatasList().size(); i++) {
                if (mItemReclassResult.getItemReclassData().getItemReclassLineDatasList().get(i).getItemNo().contains(itemNo)
                        && mItemReclassResult.getItemReclassData().getItemReclassLineDatasList().get(i).getUom().contains(Uom)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), ItemReclassificationLineActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNumber);
        intent.putExtra("IS_SCANNING", "NO");
        intent.putExtra("DOCUMENT_NO", mDocNo);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class CreateNewItemReclassLineItemEntriesTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        InsertReclassLineParameter mCreateItemReclassLineParameter;
        BaseResult mReturnBaseResult;

        CreateNewItemReclassLineItemEntriesTask(Activity activity) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);
            mActivity = activity;

            mCreateItemReclassLineParameter = new InsertReclassLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mHeaderLineNumber, mDocNo, mItemNo.toUpperCase(), "", Float.parseFloat(mQuantity), mUom, mBinCode, mNewBinCode);

            Gson gson = new Gson();
            String json = gson.toJson(mCreateItemReclassLineParameter);
            Log.d("JSON", json);

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().InsertItemReclassLine(mCreateItemReclassLineParameter);

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

                            Intent intent = new Intent(getBaseContext(), ItemReclassificationLineActivity.class);
                            intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNumber);
                            intent.putExtra("DOCUMENT_NO", mDocNo);
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

    public class GetFilterItemReclassUomTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemUomParameter mItemReclassUOMListParameter;
        List<ItemUomListResultData> mUOMListResult;

        public GetFilterItemReclassUomTask(Activity activity, String mItemNo) {
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

    public class GetItemReclassUomSpecialTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemUomParameter mItemReclassUOMListParameter;
        List<ItemUomListResultData> mUOMListResult;

        public GetItemReclassUomSpecialTask(Activity activity, String mItemNo) {

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

    //Get user key in item code special uom
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

    //ScannedItemNoQuantityPerUom - passing itemCode and scanned uom to get mQuantityPerUom
    public class GetScannedItemReclassItemNoUomSpecialTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemUomParameter mItemReclassUOMListParameter;
        List<ItemUomListResultData> mUOMListResult;

        public GetScannedItemReclassItemNoUomSpecialTask(Activity activity, String itemNo, String uom) {
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
                            mCalculateScannedItemNoQuantityBasedItemReclassQuantityTask = new CalculateScannedItemNoQuantityBasedItemReclassQuantityTask((Activity) mContext, Float.parseFloat(etQty.getText().toString()), Float.parseFloat(etQtyBase.getText().toString()));
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


    public class CalculateItemReclassQuantityTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        CalculateItemReclassQuantityParameter mCalculateItemReclassQuantityParameter;
        CalculateItemReclassificationResultData mCalculateItemReclassificationResultData;

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

    public class CalculateScannedItemNoQuantityBasedItemReclassQuantityTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        CalculateItemReclassQuantityParameter mCalculateItemReclassQuantityParameter;
        CalculateItemReclassificationResultData mCalculateItemReclassificationResultData;

        CalculateScannedItemNoQuantityBasedItemReclassQuantityTask(Activity activity, float mQuantity, float mQuantityPerUom) {
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

    public class GetItemReclassItemCodeListTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemListParameter mItemReclassItemListParameter;

        GetItemReclassItemCodeListTask(Activity activity, String dStrItemCat, String dStrItemDesc, String dStrItemCode) {

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
                    mList = new ArrayList<ItemListResultData>();
                    if (mItemListResult != null) {
                        mList.addAll(mItemListResult);
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

                            if (isScanned) {
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
    //Normal search

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
                        if (mItemListResult.size() != 0) {
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

                                    mGetFilterItemReclassUomTask = new GetFilterItemReclassUomTask((Activity) mContext, dStrItemCode);
                                    mGetFilterItemReclassUomTask.execute((Void) null);
                                    mDialog.dismiss();
                                }
                            });
                        } else {
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

    //---check whether user key in item code is exist and retrieve item details---//
    public class GetUserInputItemCodeandRetrieveItemDetailsTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemListParameter mItemReclassItemListParameter;

        public GetUserInputItemCodeandRetrieveItemDetailsTask(Activity mContext, String mStrItemCat, String mStrItemDesc, String mStrItemCode) {
            mActivity = mContext;
            mItemReclassItemListParameter = new ItemListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mStrItemCat, mStrItemDesc, mStrItemCode.toUpperCase());

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

    //Pass header line no to this asynctask to get all data of the header line no
    public class GetItemReclassTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemReclassParameter mItemReclassRetrieveParameter;

        GetItemReclassTask(Activity activity, int mHeaderLineNumber) {

            mActivity = activity;
            mItemReclassRetrieveParameter = new ItemReclassParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mHeaderLineNumber, "", "");

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassRetrieveParameter);
            Log.d("Item Reclass Line ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<ItemReclassResult> call = mApp.getNavBrokerService().GetItemReclassLine(mItemReclassRetrieveParameter);

                mItemReclassResult = call.execute().body();

                Gson gson = new Gson();
                String json = gson.toJson(mItemReclassRetrieveParameter);
                Log.d("Item Reclass Line Data", json);

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
                    if (mItemReclassResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        Gson gson = new Gson();
                        String json = gson.toJson(mHeaderLineNumber);
                        Log.d("ITEM RECLASS", json);

                        temp_list = new ArrayList<ItemReclassLineData>();

                        if (mItemReclassResult != null) {
                            if (mItemReclassResult.getItemReclassData() != null && mItemReclassResult.getItemReclassData().getItemReclassLineDatasList() != null) {
                                temp_list.addAll(mItemReclassResult.getItemReclassData().getItemReclassLineDatasList());
                            }
                        }

                        for (int x = 0; x < temp_list.size(); x++) {
                            mLoc = mItemReclassResult.getItemReclassData().getLocationCode();
                            mNewLoc = mItemReclassResult.getItemReclassData().getNewLocationCode();
                            mCreatedby = mItemReclassResult.getItemReclassData().getCreatedBy();
                            mPostDate = DataConverter.ConvertJsonDateToDayMonthYear(mItemReclassResult.getItemReclassData().getPostingDate());
                            mIsShowBinCode = mItemReclassResult.getItemReclassData().isShowBinCode();
                            mIsShowNewBinCode = mItemReclassResult.getItemReclassData().isShowNewBinCode();
                            mBinCode = mItemReclassResult.getItemReclassData().getBinCode();
                            mNewBinCode = mItemReclassResult.getItemReclassData().getNewBinCode();
                            mDocNo = mItemReclassResult.getItemReclassData().getDocumentNo();
                        }

                        if (mBinCode != null) {
                            lblBin.setVisibility(View.VISIBLE);
                            lblBin.setText("Bin: " + mBinCode);
                        } else {
                            lblBin.setVisibility(View.GONE);
                        }

                        if (mNewBinCode != null) {
                            lblNewBin.setVisibility(View.VISIBLE);
                            lblNewBin.setText("New Bin: " + mNewBinCode);
                        } else {
                            lblNewBin.setVisibility(View.GONE);
                        }

                        tvDocNo.setText("Doc No: " + mDocNo);
                        tvLoc.setText("Loc: " + mLoc);
                        tvNewLoc.setText("New Loc: " + mNewLoc);
                        if (mCreatedby != null) {
                            llCreator.setVisibility(View.VISIBLE);
                            tvCreatedBy.setText("Created By: " + mCreatedby);
                        } else {
                            llCreator.setVisibility(View.GONE);
                        }
                        tvPostingDate.setText("Post Date: " + mPostDate);

                        if (mIsShowBinCode) {
                            tvBin.setVisibility(View.VISIBLE);
                            spinnerBin.setVisibility(View.VISIBLE);
                            mLoc = mItemReclassResult.getItemReclassData().getLocationCode();
                            if (mLoc != null) {
                                mGetItemReclassBinTask = new GetItemReclassBinListTask((Activity) mContext, mLoc);
                                mGetItemReclassBinTask.execute((Void) null);
                            }
                        }

                        if (mIsShowNewBinCode) {
                            tvNewBin.setVisibility(View.VISIBLE);
                            spinnerNewBin.setVisibility(View.VISIBLE);
                            mNewLoc = mItemReclassResult.getItemReclassData().getNewLocationCode();
                            if (mNewLoc != null) {
                                mGetItemReclassNewBinTask = new GetItemReclassNewBinListTask((Activity) mContext, mNewLoc);
                                mGetItemReclassNewBinTask.execute((Void) null);
                            }
                        }

                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mItemReclassResult.getMessage());
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, getResources().getString(R.string.notification_msg_server_no_response));
                Intent intent = new Intent(getBaseContext(), ItemReclassificationActivity.class);
                startActivity(intent);
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
                        temp_list.addAll(mBinListResult);
                    }

                    spinnerBinAdapter = new ArrayAdapter<LocationBinListResultData>(mContext, android.R.layout.simple_spinner_item, temp_list);
                    spinnerBin.setAdapter(spinnerBinAdapter);

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

    public class GetItemReclassNewBinListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        LocationBinListParameter mItemReclassNewBinListParameter;
        List<LocationBinListResultData> mNewBinListResult;

        public GetItemReclassNewBinListTask(Activity activity, String spinnerLocString) {
            mActivity = activity;

            mItemReclassNewBinListParameter = new LocationBinListParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), spinnerLocString); //"BLUE"

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassNewBinListParameter);
            Log.d("NewBin List ", json);
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
                        temp_list.addAll(mNewBinListResult);
                    }

                    spinnerBinAdapter = new ArrayAdapter<LocationBinListResultData>(mContext, android.R.layout.simple_spinner_item, temp_list);
                    spinnerNewBin.setAdapter(spinnerBinAdapter);

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

    public class PostUpdateItemDetailsTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemReclassLineUpdateParameter mUpdateItemReclassLineDetailsParameter;
        BaseResult mReturnBaseResult;

        PostUpdateItemDetailsTask(Activity activity, int lineNo, String mItemNo, String s, Float Quantity, String mUom, String mBinCode, String mNewBinCode) {

            mActivity = activity;
            mUpdateItemReclassLineDetailsParameter = new ItemReclassLineUpdateParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), lineNo, mItemNo, s, Quantity, mUom, mBinCode, mNewBinCode);

            Gson gson = new Gson();
            String json = gson.toJson(mUpdateItemReclassLineDetailsParameter);
            Log.d("TRANSFER ORDER", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().UpdateItemReclassLine(mUpdateItemReclassLineDetailsParameter);

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
                        Intent intent = new Intent(getBaseContext(), ItemReclassificationLineActivity.class);
                        intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNumber);
                        intent.putExtra("DOCUMENT_NO", mDocNo);
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
}
