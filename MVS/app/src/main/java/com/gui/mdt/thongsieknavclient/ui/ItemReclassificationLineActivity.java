package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.ConfigurationManager;
import com.gui.mdt.thongsieknavclient.DataConverter;
import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.itemreclass.itemReclassLineArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemUomListResultData;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemUomParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassData;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineData;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineUpdateParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassificationNewLineEntry;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.PostItemReclassParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.RemoveItemReclassLineParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.RemoveItemReclassLineResult;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialRecords;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.ItemReclassAddNewLineEntry;
import com.orm.SugarRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class ItemReclassificationLineActivity extends BaseActivity {

    PostUpdateItemQuantityTask mPostUpdateItemQuantityTask = null;
    PostUpdateScannedItemQuantityTask mPostUpdateScannedItemQuantityTask = null;
    GetItemReclassLinemQuantityPerUomTask mGetItemReclassLinemQuantityPerUomTask = null;
    GetItemReclassLineDetailsTask mGetItemReclassLineDetailsTask = null;
    GetFilterItemReclassLineItemCodeTask mGetFilterItemReclassLineItemCodeTask = null;
    RemoveItemReclassLineTask mRemoveItemReclassLineTask = null;
    PostAllItemReclassLineTask mPostAllItemReclassLineTask = null;

    ListView itemList;
    Context mContext;
    private NavClientApp mApp;
    private TextView tvDocNo;
    private TextView tvLoc;
    private TextView tvNewLoc;
    private TextView tvCreatedBy;
    private TextView tvPostingDate;
    private TextView tvBin;
    private TextView tvNewBin;
    private LinearLayout llCreator;

    private int mHeaderLineNumber;
    private int mLineNo;
    private int entryIndexCounter;
    private String mItemNo;
    private String mFilterItemDesc;
    private String mFilterItemCode;
    private String mFilterDate;
    private String mDocNo;
    private String mLoc;
    private String mNewLoc;
    private String mCreatedby;
    private String mPostDate;
    private String mUom;
    private String mBinCode;
    private String mNewBinCode;
    private String scannedSerialNo;
    private String scannedLotNo;
    private boolean mIsShowBinCode = false;
    private boolean mIsShowNewBinCode = false;
    private boolean isStarted = false;
    private boolean isEnded = false;
    private boolean isLastItem = false;
    private float mPrevQuantityPerUom = 0f;
    ArrayList<ItemReclassificationNewLineEntry> tmp_list;
    itemReclassLineArrayAdapter ItemReclassLineArrayAdapter;
    ItemReclassResult mItemReclassResult;
    ArrayList<ItemReclassLineData> temp_list;
    ArrayList<ItemReclassData> mlist;

    protected int getLayoutResource() {
        return R.layout.activity_item_reclassification_line;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_reclassification_line);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        entryIndexCounter = 0;
        scannedLotNo = "";
        scannedSerialNo = "";
        tmp_list = new ArrayList<ItemReclassificationNewLineEntry>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mHeaderLineNumber = extras.getInt("RECLASS_HEADER_LINE_NO");
            mDocNo = extras.getString("DOCUMENT_NO");
            mIsShowBinCode = extras.getBoolean("IS_SHOW_BIN_CODE");
            mIsShowNewBinCode = extras.getBoolean("IS_SHOW_NEW_BIN_CODE");
            mGetItemReclassLineDetailsTask = new GetItemReclassLineDetailsTask(this, mHeaderLineNumber);
            mGetItemReclassLineDetailsTask.execute((Void) null);
            Log.d("RECLASS_HEADER_LINE_NO", String.valueOf(mHeaderLineNumber));

        } else {
            NotificationManager.HideProgressDialog();
        }

        llCreator = (LinearLayout) findViewById(R.id.llCreator);
        tvDocNo = (TextView) findViewById(R.id.lblDocNo);
        tvLoc = (TextView) findViewById(R.id.lblLoc);
        tvNewLoc = (TextView) findViewById(R.id.lblNewLoc);
        tvCreatedBy = (TextView) findViewById(R.id.lblCreator);
        tvPostingDate = (TextView) findViewById(R.id.lblPostDate);
        tvBin = (TextView) findViewById(R.id.lblBin);
        tvNewBin = (TextView) findViewById(R.id.lblNewBin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Item Reclass Line");
        setSupportActionBar(toolbar);

        final EditText scanText = (EditText) findViewById(R.id.textScanCode);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 1 && !isStarted) {
                    if (s.toString().length() > 4 && !s.toString().startsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL) && !s.toString().endsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL)) {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
                        scanText.setText("");
                        scanText.requestFocus();
                    } else if (s.toString().length() > 4 && s.toString().startsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL) && s.toString().endsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL)) {
                        isStarted = false;

                        String scannedString = s.toString().replace(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL, "");
                        String[] splitString = scannedString.split("\\|");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() > 4 && s.toString().startsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL) && s.toString().endsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL)) {
                    isStarted = false;

                    String scannedString = s.toString().replace(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL, "");
                    final String[] splitString = scannedString.split("\\|");

                    final ArrayList<ItemReclassLineData> temp_list = new ArrayList<ItemReclassLineData>();
                    if (mItemReclassResult != null) {
                        if (mItemReclassResult.ItemReclassData != null && mItemReclassResult.ItemReclassData.getItemReclassLineDatasList() != null) {
                            temp_list.addAll(mItemReclassResult.ItemReclassData.getItemReclassLineDatasList());
                        }
                    }

                    mItemNo = splitString[0];
                    mUom = splitString[2];
                    scannedLotNo = splitString[3];
                    scannedSerialNo = splitString[5];
                    mLineNo = 0;
                    boolean isItemTrackingRequired = false;
                    Float prevQuantity = 0f;

                    for (int x = 0; x < temp_list.size(); x++) {
                        if (temp_list.get(x).getItemNo().contains(splitString[0])) {
                            Log.d("ITEM FOUND", String.valueOf(mLineNo));
                            Log.d("ITEM FOUND", mDocNo);
                            mLineNo = temp_list.get(x).getLineNo();
                            isItemTrackingRequired = temp_list.get(x).isItemTrackingRequired();
                            prevQuantity = temp_list.get(x).getQuantity();
                            mIsShowBinCode = mItemReclassResult.getItemReclassData().isShowBinCode();
                            mIsShowNewBinCode = mItemReclassResult.getItemReclassData().isShowNewBinCode();

                            if (isItemTrackingRequired) {
                                DataManager.getInstance().clearTransitionItemReclassLineData();
                                DataManager.getInstance().ItemReclassificationNo = mDocNo;
                                DataManager.getInstance().setTransitionItemReclassLinesData(temp_list.get(x));
                            }
                            break;
                        }
                    }

                    if (splitString.length == 6) {
//                        if (checkItemExist(splitString[0],mUom)) {
                        DataManager.getInstance().ItemReclassificationNo = mDocNo;
                        if (isItemTrackingRequired) {
                            Intent intent = new Intent(getBaseContext(), ItemReclassificationLotEntryActivity.class);
                            intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNumber);
                            intent.putExtra("IS_SCANNING", "YES");
                            intent.putExtra("IS_NEW_ADD", "YES");
                            intent.putExtra("DOCUMENT_NO", mDocNo);
                            intent.putExtra("SCAN_CONTENT", scannedString);
                            if (mIsShowBinCode) {
                                if (mBinCode != null) {
                                    intent.putExtra("SHOW_BINCODE", mIsShowBinCode);
                                    intent.putExtra("BINCODE", mBinCode);
                                }
                            }
                            if (mIsShowNewBinCode) {
                                if (mNewBinCode != null) {
                                    intent.putExtra("SHOW_NEWBINCODE", mIsShowNewBinCode);
                                    intent.putExtra("NEWBINCODE", mNewBinCode);
                                }
                            }
                            startActivity(intent);
                        } else {
                            scanText.setText("");
                            scanText.requestFocus();

                            if (checkItemExist(splitString[0], mUom)) {

                                if (!checkSerialExist()) {
                                    final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

                                    Window window = dialog.getWindow();
                                    window.setGravity(Gravity.CENTER);
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                    window.setDimAmount(0.2f);

                                    dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                                    dialog.setContentView(R.layout.dialog_update_quantity_new);
                                    dialog.setCancelable(false);

                                    TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
                                    title.setText("Item Exists: " + splitString[0] + "/" + mUom);

                                    final EditText editText = (EditText) dialog.findViewById(R.id.NeweditUpdateQuantity);
                                    editText.setText(splitString[1]);
                                    if (!checkSerialExist()) {
                                        float total = prevQuantity + Float.parseFloat(splitString[1]);
                                        editText.setText(String.valueOf(total));
                                    }

                                    Button mBtnSave = (Button) dialog.findViewById(R.id.mNewBtnSave);
                                    mBtnSave.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            String quantity = editText.getText().toString();

                                            if (quantity.length() != 0 && !quantity.equals(".")) {
                                                if (!checkSerialExist()) {

                                                    tmp_list.add(new ItemReclassificationNewLineEntry(mDocNo, mItemNo, mLoc, mNewLoc, mBinCode, mNewBinCode, "", "", String.valueOf(splitString[1]), scannedLotNo, scannedSerialNo, entryIndexCounter, true, entryIndexCounter, -1, ConfigurationManager.DATA_UPLOADED_FALSE));

                                                    ItemReclassAddNewLineEntry temp_entry = new ItemReclassAddNewLineEntry(mDocNo, mItemNo, mLineNo, "", "", String.valueOf(splitString[1]), scannedLotNo, scannedSerialNo, "", true, entryIndexCounter, ConfigurationManager.DATA_UPLOADED_FALSE, System.currentTimeMillis());

                                                    temp_entry.save();

                                                    DataManager.getInstance().AddSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS, mItemNo, String.valueOf(mLineNo), scannedLotNo, scannedSerialNo);

                                                    entryIndexCounter++;

                                                    Log.d("SUGARORM", "ItemNo: " + mItemNo);

                                                    List<ItemReclassAddNewLineEntry> tmp = SugarRecord.find(ItemReclassAddNewLineEntry.class, "item_no = ?", mItemNo);

                                                    for (int i = 0; i < tmp.size(); i++) {

                                                        Log.d("SUGARORM", i + ": " + tmp.get(i).toString());

                                                    }

                                                    mPostUpdateScannedItemQuantityTask = new PostUpdateScannedItemQuantityTask((Activity) mContext, mLineNo, mItemNo, Float.parseFloat(quantity), mUom);
                                                    mPostUpdateScannedItemQuantityTask.execute((Void) null);

                                                    dialog.dismiss();
                                                }
                                            } else {
                                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                                            }
                                            dialog.dismiss();
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
                                    String quantity = editText.getText().toString();
                                    if (quantity.length() != 0 && !quantity.equals(".")) {
//                                dialog.dismiss();
//                                mBtnSave.callOnClick();
                                    }

                                    scanText.setText("");
                                    scanText.requestFocus();
                                } else {
                                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_MSG_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_SERIALNO);
                                    scanText.setText("");
                                    scanText.requestFocus();
                                }

                            } else {

                                Intent intent = new Intent(getBaseContext(), AddNewItemReclassificationLineActivity.class);
                                intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNumber);
                                intent.putExtra("IS_SCANNING", "YES");
                                intent.putExtra("IS_NEW_ADD", "YES");
                                intent.putExtra("DOCUMENT_NO", mDocNo);
                                intent.putExtra("SCAN_CONTENT", scannedString);
                                if (mBinCode != null) {
                                    intent.putExtra("BINCODE", mBinCode);
                                }
                                if (mNewBinCode != null) {
                                    intent.putExtra("NEWBINCODE", mNewBinCode);
                                }
                                startActivity(intent);
                            }
                        }
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
                        scanText.setText("");
                        scanText.requestFocus();
                    }
                }
            }
        });

        Button btnPost = (Button) findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemDescrip = "";
                float subsQty = 0f;
                String tmp = "0.0";
                for (int i = 0; i < itemList.getCount(); i++) {
                    subsQty = temp_list.get(i).getQuantity();
                    itemDescrip = temp_list.get(i).getItemDescription();
                    Log.d("Item<Qty to Rec>: ", String.valueOf(subsQty));

//                    if (subsQty.matches(tmp)) {
                    //confirmPostScan(subsQty, itemDescrip);
//                    } else if (!subsQty.matches(tmp)) {
                    //confirmPost();
                    if (subsQty != Float.parseFloat(tmp)) {
                        confirmPost(mHeaderLineNumber);
                    }
//                    }
                    break;
                }
            }
        });
    }

    private boolean checkItemExist(final String itemNo, String Uom) {
        if (mItemReclassResult != null && mItemReclassResult.getItemReclassData().getItemReclassLineDatasList() != null) {
            for (int i = 0; i < mItemReclassResult.getItemReclassData().getItemReclassLineDatasList().size(); i++) {
                if (mItemReclassResult.getItemReclassData().getItemReclassLineDatasList().get(i).getItemNo().contains(itemNo)
                        && mItemReclassResult.getItemReclassData().getItemReclassLineDatasList().get(i).getUom().contains(Uom)) {

                    DataManager.getInstance().setTransitionItemReclassLinesData(mItemReclassResult.getItemReclassData().getItemReclassLineDatasList().get(i));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), ItemReclassificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void confirmPost() {
        new AlertDialog.Builder(mContext)
                .setTitle(getResources().getString(R.string.notification_title_confirmation))
                .setMessage(getResources().getString(R.string.notification_msg_confirmation))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    public void confirmPostScan(String qtytoRec, String itemDescrip) {
        new AlertDialog.Builder(mContext)
                .setTitle(getResources().getString(R.string.notification_title_confirmation))
                .setMessage("Missing scanning line item,do you want to proceed?")
                .setPositiveButton(R.string.post, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    public void confirmPost(final int headerLineNo) {
        new AlertDialog.Builder(mContext)
                .setTitle(getResources().getString(R.string.notification_title_confirmation))
                .setMessage(getResources().getString(R.string.notification_msg_confirmation_item_reclass))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mPostAllItemReclassLineTask = new PostAllItemReclassLineTask((Activity) mContext, headerLineNo);
                        mPostAllItemReclassLineTask.execute((Void) null);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    public void clearAllEntries() {

        List<ItemReclassAddNewLineEntry> entryList = SugarRecord.find(ItemReclassAddNewLineEntry.class, "line_no = ?", Integer.toString(mLineNo));
        if (entryList.size() > 0) {
            for (int i = 0; i < entryList.size(); i++) {
                entryList.get(i).delete();
            }
        }

        // REMOVE BARCODE SERIAL NUMBER - Delete all serial number and set quantity to 0
        if (mDocNo != null && mItemNo != null && scannedLotNo != null) {
            List<BarcodeSerialRecords> deleteSerialList = DataManager.getInstance().mDeleteSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS, mItemNo, Integer.toString(mLineNo), scannedLotNo);

            for (int i = 0; i < deleteSerialList.size(); i++) {
                Log.d("SUGARORM NEW", i + ": " + deleteSerialList.get(i).toString());
            }

            for (int i = 0; i < deleteSerialList.size(); i++) {
                if (deleteSerialList.get(i).getBarcodeSerialNo().length() > 0) {
                    if (deleteSerialList.get(i).getBarcodeSerialNo().contains(scannedSerialNo)) {
                        deleteSerialList.get(i).delete();
                    }
                }
            }
        } else {
            NotificationManager.DisplayAlertDialog(mContext, "Invalid Clear Entry", "Please scan first before you clear entry.");
        }

        for (int i = tmp_list.size() - 1; i >= 0; i--) {
            if (tmp_list.get(i).IsUploaded) {
                tmp_list.remove(i);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_reclass_line_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchDialog();
                return true;
            case R.id.action_AddItemReclassLine:
                showAddNewItemReclassificationLineActivity();
                return true;
            case R.id.action_clearEntry:
                clearAllEntries();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSearchDialog() {

        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_search_item_reclassification_line);
        dialog.setCancelable(false);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterItemDesc);
                mFilterItemDesc = temp_filterParam.getText().toString();

                temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterItemCode);
                mFilterItemCode = temp_filterParam.getText().toString();

                Log.d("DEBUGGING", mFilterItemDesc);
                mGetFilterItemReclassLineItemCodeTask = new GetFilterItemReclassLineItemCodeTask((Activity) mContext,mFilterItemCode,mFilterItemDesc);
                mGetFilterItemReclassLineItemCodeTask.execute((Void) null);

                dialog.dismiss();
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

    private void showAddNewItemReclassificationLineActivity() {

        Intent i = new Intent(ItemReclassificationLineActivity.this, AddNewItemReclassificationLineActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNumber);
        i.putExtra("DOCUMENT_NO", mDocNo);
        i.putExtra("IS_NEW_ADD", "YES");
        i.putExtra("IS_SCANNING", "NO");
        i.putExtra("IS_ACCUMULATE_QTY", "NO");
        if (mBinCode != null) {
            i.putExtra("BINCODE", mBinCode);
        }
        if (mNewBinCode != null) {
            i.putExtra("NEWBINCODE", mNewBinCode);
        }
        startActivity(i);
    }

    public void RemoveLine(String itemNo, int LineNo) {
        Log.d("SERVER REMOVE", "IR Line: " + LineNo);
        mRemoveItemReclassLineTask = new RemoveItemReclassLineTask((Activity) mContext, LineNo);
        mRemoveItemReclassLineTask.execute((Void) null);

        DataManager.getInstance().DeleteSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS, itemNo, Integer.toString(LineNo));
    }

    private boolean checkSerialExist() {
        if (scannedSerialNo.isEmpty()) {
            return false;
        }

        List<BarcodeSerialRecords> tempSerialList = DataManager.getInstance().GetSerialRecords(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS, mItemNo, String.valueOf(mLineNo), scannedLotNo);
        boolean serialExist = false;

        for (int i = 0; i < tempSerialList.size(); i++) {
            Log.d("SUGARORM NEW", i + ": " + tempSerialList.get(i).toString());
        }

        for (int i = 0; i < tempSerialList.size(); i++) {
            if (tempSerialList.get(i).getBarcodeSerialNo().length() > 0) {
                if (tempSerialList.get(i).getBarcodeSerialNo().contains(scannedSerialNo)) {
                    serialExist = true;
                    break;
                }
            }
        }

        if (serialExist) {
            return true;
        } else {
            return false;
        }
    }

    //NOT USING
    public class GetItemReclassLinemQuantityPerUomTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemUomParameter mItemReclassUOMListParameter;
        List<ItemUomListResultData> mUOMListResult;
        float mPrevQty = 0f;
        String mItemNo;
        String ScannedString;
        ItemReclassLineData mItemReclassLineData;

        public GetItemReclassLinemQuantityPerUomTask(Activity activity, String scannedString, String itemNo, float prevQty, ItemReclassLineData itemReclassLineData) {
            mActivity = activity;
            mItemReclassUOMListParameter = new ItemUomParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mItemNo);
            mItemNo = itemNo;
            mPrevQty = prevQty;
            ScannedString = scannedString;
            mItemReclassLineData = itemReclassLineData;

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

            if (success) {
                final ArrayList<ItemUomListResultData> temp_list = new ArrayList<>();

                if (mUOMListResult != null) {
                    temp_list.addAll(mUOMListResult);
                }

                for (int x = 0; x < temp_list.size(); x++) {
                    if (temp_list.get(x).getItemCode().contains(mItemNo)) {
                        mPrevQuantityPerUom = temp_list.get(x).getQuantityPerUom();
                    }
                }

                String[] splitString = ScannedString.split("\\|");
                String qty = splitString[1];

                float total = 0f;
                float totalQuantity = 0f;
                totalQuantity = Float.parseFloat(qty) + mPrevQty;
                total = totalQuantity * mPrevQuantityPerUom;
                mItemReclassLineData.setQuantity(totalQuantity);
                mItemReclassLineData.setQuantityBase(total);

            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, getResources().getString(R.string.notification_msg_server_no_response));
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    //accumulate and update scannedQuantity
    public class PostUpdateScannedItemQuantityTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemReclassLineUpdateParameter mUpdateItemReclassLineDetailsParameter;
        BaseResult mReturnBaseResult;

        PostUpdateScannedItemQuantityTask(Activity activity, int lineNo, String mItemNo, Float Quantity, String uom) {

            mActivity = activity;
            mUpdateItemReclassLineDetailsParameter = new ItemReclassLineUpdateParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), lineNo, mItemNo, "", Quantity, uom, "", "");

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
                    if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        mGetItemReclassLineDetailsTask = new GetItemReclassLineDetailsTask((Activity) mContext, mHeaderLineNumber);
                        mGetItemReclassLineDetailsTask.execute((Void) null);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_FAILED, mReturnBaseResult.getMessage());
                    }
//                DataManager.getInstance().DeleteSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS);
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

    public class PostUpdateItemQuantityTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemReclassLineUpdateParameter mUpdateItemReclassLineDetailsParameter;
        BaseResult mReturnBaseResult;

        public PostUpdateItemQuantityTask(Activity mContext, int mLineNo, float Qty, String uom) {
            mActivity = mContext;
            mUpdateItemReclassLineDetailsParameter = new ItemReclassLineUpdateParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mLineNo, mItemNo, "", Qty, uom, "", "");

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
                    if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        mGetItemReclassLineDetailsTask = new GetItemReclassLineDetailsTask((Activity) mContext, mHeaderLineNumber);
                        mGetItemReclassLineDetailsTask.execute((Void) null);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_FAILED, mReturnBaseResult.getMessage());
                    }
//                DataManager.getInstance().DeleteSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS);
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

    public class GetItemReclassLineDetailsTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemReclassParameter mItemReclassRetrieveParameter;

        GetItemReclassLineDetailsTask(Activity activity, int mHeaderLineNumber) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
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
                    if (mItemReclassResult != null) {

                        if (mItemReclassResult.getItemReclassData().getItemReclassLineDatasList().size() != 0) {

                            if (mItemReclassResult.getStatus() == BaseResult.BaseResultStatusOk) {
                                Gson gson = new Gson();
                                String json = gson.toJson(mHeaderLineNumber);
                                Log.d("ITEM RECLASS", json);

                                itemList = (ListView) findViewById(R.id.orderList);
                                temp_list = new ArrayList<ItemReclassLineData>();

                                if (mItemReclassResult != null) {
                                    if (mItemReclassResult.getItemReclassData() != null && mItemReclassResult.getItemReclassData().getItemReclassLineDatasList() != null) {
                                        if (mItemReclassResult.getItemReclassData().getItemReclassLineDatasList().size() == 1) {
                                            temp_list.addAll(mItemReclassResult.getItemReclassData().getItemReclassLineDatasList());
                                            isLastItem = true;
                                        } else {
                                            isLastItem = false;
                                            temp_list.addAll(mItemReclassResult.getItemReclassData().getItemReclassLineDatasList());
                                        }
                                    }
                                } else {
                                    Intent intent = new Intent(getBaseContext(), ItemReclassificationActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                                for (int x = 0; x < temp_list.size(); x++) {
                                    mLoc = mItemReclassResult.getItemReclassData().getLocationCode();
                                    mNewLoc = mItemReclassResult.getItemReclassData().getNewLocationCode();
                                    mCreatedby = mItemReclassResult.getItemReclassData().getCreatedBy();
                                    mPostDate = DataConverter.ConvertJsonDateToDayMonthYear(mItemReclassResult.getItemReclassData().getPostingDate());
                                    mIsShowBinCode = mItemReclassResult.getItemReclassData().isShowBinCode();
                                    mIsShowNewBinCode = mItemReclassResult.getItemReclassData().isShowNewBinCode();
                                    mDocNo = mItemReclassResult.getItemReclassData().getDocumentNo();
                                    mBinCode = mItemReclassResult.getItemReclassData().getBinCode();
                                    mNewBinCode = mItemReclassResult.getItemReclassData().getNewBinCode();
                                }

                                tvDocNo.setText("Doc No: " + mDocNo);
                                tvLoc.setText("Loc: " + mLoc);
                                tvNewLoc.setText("New Loc: " + mNewLoc);
                                if (mBinCode != null) {
                                    tvBin.setVisibility(View.VISIBLE);
                                    tvBin.setText(mBinCode);
                                } else {
                                    tvBin.setVisibility(View.GONE);
                                }

                                if (mNewBinCode != null) {
                                    tvNewBin.setVisibility(View.VISIBLE);
                                    tvNewBin.setText(mNewBinCode);
                                } else {
                                    tvNewBin.setVisibility(View.GONE);
                                }

                                if (mCreatedby != null) {
                                    llCreator.setVisibility(View.VISIBLE);
                                    tvCreatedBy.setText("Created By: " + mCreatedby);
                                } else {
                                    llCreator.setVisibility(View.GONE);
                                }
                                tvPostingDate.setText("Post. Date: " + mPostDate);

                                ItemReclassLineArrayAdapter = new itemReclassLineArrayAdapter(mContext, temp_list, mIsShowBinCode, mIsShowNewBinCode);
                                ItemReclassLineArrayAdapter.notifyDataSetChanged();
                                itemList.setAdapter(ItemReclassLineArrayAdapter);
                                itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        TextView lblItemIDUom = (TextView) view.findViewById(R.id.lblItemNoBaseUOM);

                                        mItemNo = lblItemIDUom.getText().toString().replace("Item No: ", "").split("/")[0];
                                        mLineNo = 0;
                                        boolean isItemTrackingRequired = false;
                                        Float prevQuantity = 0f;
                                        Float prevQuantityBased = 0f;

                                        for (int index = 0; index < temp_list.size(); index++) {
                                            mLineNo = temp_list.get(i).getLineNo();
                                            if (temp_list.get(index).getItemNo().contains(mItemNo) && temp_list.get(index).getLineNo() == mLineNo) {
                                                Log.d("ITEM FOUND", String.valueOf(mHeaderLineNumber));
                                                Log.d("ITEM FOUND", mDocNo);

                                                mLineNo = temp_list.get(index).getLineNo();
                                                isItemTrackingRequired = temp_list.get(index).isItemTrackingRequired();
                                                prevQuantity = temp_list.get(index).getQuantity();
                                                prevQuantityBased = temp_list.get(index).getQuantityBase();
                                                mUom = temp_list.get(index).getUom();
                                                mBinCode = temp_list.get(index).getBinCode();
                                                mNewBinCode = temp_list.get(index).getNewBinCode();

                                                DataManager.getInstance().clearTransitionItemReclassLineData();
                                                DataManager.getInstance().ItemReclassHeaderLineNo = mHeaderLineNumber;
                                                DataManager.getInstance().ItemReclassificationNo = mDocNo;
                                                DataManager.getInstance().setTransitionItemReclassLinesData(temp_list.get(i));

                                                break;
                                            }
                                        }

                                        if (isItemTrackingRequired) {
                                            Intent intent = new Intent(getBaseContext(), ItemReclassificationLotEntryActivity.class);
                                            intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNumber);
                                            intent.putExtra("IS_NEW_ADD", "NO");
                                            intent.putExtra("IS_SCANNING", "NO");
                                            intent.putExtra("DOCUMENT_NO", mDocNo);
                                            if (mIsShowBinCode) {
                                                if (mBinCode != null) {
                                                    intent.putExtra("SHOW_BINCODE", mIsShowBinCode);
                                                    intent.putExtra("BINCODE", mBinCode);
                                                }
                                            }
                                            if (mIsShowNewBinCode) {
                                                if (mNewBinCode != null) {
                                                    intent.putExtra("SHOW_NEWBINCODE", mIsShowNewBinCode);
                                                    intent.putExtra("NEWBINCODE", mNewBinCode);
                                                }
                                            }
                                            startActivity(intent);

                                        } else {

                                            final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
                                            Window window = dialog.getWindow();
                                            window.setGravity(Gravity.CENTER);
                                            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                            window.setDimAmount(0.2f);

                                            dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                                            dialog.setContentView(R.layout.quantity_update_item_reclass_line);
                                            dialog.setCancelable(false);

                                            TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
                                            title.setText("Item No: " + mItemNo + "/" + mUom);

                                            Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

                                            EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                            editUpdateQuantity.setText(Float.toString(prevQuantity));

                                            btnSave.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                                    String quantity = editUpdateQuantity.getText().toString();

                                                    if (quantity.length() != 0 && !quantity.equals(".")) {
                                                        mPostUpdateItemQuantityTask = new PostUpdateItemQuantityTask((Activity) mContext, mLineNo, Float.parseFloat(quantity), mUom);
                                                        mPostUpdateItemQuantityTask.execute((Void) null);

                                                        dialog.dismiss();
                                                    } else {
                                                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                                                    }
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
                                    }
                                });

                                //DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_ITEM_RECLASS);

                            } else {
                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mItemReclassResult.getMessage());
                            }
                        } else {
                            TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                            lblNoData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                        lblNoData.setVisibility(View.VISIBLE);
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

    public class PostAllItemReclassLineTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PostItemReclassParameter mPostItemReclassHeaderLineParameter;
        BaseResult mReturnBaseResult;

        PostAllItemReclassLineTask(Activity activity, int headerLineNo) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mPostItemReclassHeaderLineParameter = new PostItemReclassParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), headerLineNo);

            Log.d("LAYER 2", "LineNo: " + mLineNo);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().PostItemReclass(mPostItemReclassHeaderLineParameter);

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
                        Intent intent = new Intent(getBaseContext(), ItemReclassificationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        DataManager.getInstance().DeleteSerialRecord(mDocNo, ConfigurationManager.ORDER_TYPE_ITEM_RECLASS);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, "Unable to Post", mReturnBaseResult.getMessage());
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, "Unable to Post", getResources().getString(R.string.notification_msg_server_no_response));
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    //query search item code
    public class GetFilterItemReclassLineItemCodeTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemReclassParameter mItemReclassRetrieveParameter;

        GetFilterItemReclassLineItemCodeTask(Activity activity, String xFilterItemCode, String xFilterItemDesc) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;
            mItemReclassRetrieveParameter = new ItemReclassParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mHeaderLineNumber, xFilterItemCode.toUpperCase(), xFilterItemDesc);
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
                    Button btnPost = (Button) findViewById(R.id.btnPost);
                    TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                    if (mItemReclassResult != null) {

                        if (mItemReclassResult.getItemReclassData().getItemReclassLineDatasList().size() != 0) {
                            if (mItemReclassResult.getStatus() == BaseResult.BaseResultStatusOk) {
                                Gson gson = new Gson();
                                String json = gson.toJson(mHeaderLineNumber);
                                Log.d("ITEM RECLASS", json);

                                itemList = (ListView) findViewById(R.id.orderList);
                                lblNoData.setVisibility(View.GONE);
                                itemList.setVisibility(View.VISIBLE);
                                btnPost.setVisibility(View.VISIBLE);
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
                                    mDocNo = mItemReclassResult.getItemReclassData().getDocumentNo();
                                    mBinCode = mItemReclassResult.getItemReclassData().getBinCode();
                                    mNewBinCode = mItemReclassResult.getItemReclassData().getNewBinCode();
                                }

                                tvDocNo.setText("Doc No: " + mDocNo);
                                tvLoc.setText("Loc: " + mLoc);
                                tvNewLoc.setText("New Loc: " + mNewLoc);
                                if (mBinCode != null) {
                                    tvBin.setVisibility(View.VISIBLE);
                                    tvBin.setText(mBinCode);
                                } else {
                                    tvBin.setVisibility(View.GONE);
                                }

                                if (mNewBinCode != null) {
                                    tvNewBin.setVisibility(View.VISIBLE);
                                    tvNewBin.setText(mNewBinCode);
                                } else {
                                    tvNewBin.setVisibility(View.GONE);
                                }

                                if (mCreatedby != null) {
                                    llCreator.setVisibility(View.VISIBLE);
                                    tvCreatedBy.setText("Created By: " + mCreatedby);
                                } else {
                                    llCreator.setVisibility(View.GONE);
                                }
                                tvPostingDate.setText("Post. Date: " + mPostDate);

                                ItemReclassLineArrayAdapter = new itemReclassLineArrayAdapter(mContext, temp_list, mIsShowBinCode, mIsShowNewBinCode);
                                ItemReclassLineArrayAdapter.notifyDataSetChanged();
                                itemList.setAdapter(ItemReclassLineArrayAdapter);
                                itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        TextView lblItemIDUom = (TextView) view.findViewById(R.id.lblItemNoBaseUOM);

                                        mItemNo = lblItemIDUom.getText().toString().replace("Item No: ", "").split("/")[0];
                                        mLineNo = 0;
                                        boolean isItemTrackingRequired = false;
                                        Float prevQuantity = 0f;
                                        Float prevQuantityBased = 0f;

                                        for (int index = 0; index < temp_list.size(); index++) {
                                            mLineNo = temp_list.get(i).getLineNo();
                                            if (temp_list.get(index).getItemNo().contains(mItemNo) && temp_list.get(index).getLineNo() == mLineNo) {
                                                Log.d("ITEM FOUND", String.valueOf(mHeaderLineNumber));
                                                Log.d("ITEM FOUND", mDocNo);

                                                mLineNo = temp_list.get(index).getLineNo();
                                                isItemTrackingRequired = temp_list.get(index).isItemTrackingRequired();
                                                prevQuantity = temp_list.get(index).getQuantity();
                                                prevQuantityBased = temp_list.get(index).getQuantityBase();
                                                mUom = temp_list.get(index).getUom();
                                                mBinCode = temp_list.get(index).getBinCode();
                                                mNewBinCode = temp_list.get(index).getNewBinCode();

                                                DataManager.getInstance().clearTransitionItemReclassLineData();
                                                DataManager.getInstance().ItemReclassHeaderLineNo = mHeaderLineNumber;
                                                DataManager.getInstance().ItemReclassificationNo = mDocNo;
                                                DataManager.getInstance().setTransitionItemReclassLinesData(temp_list.get(i));
                                                break;
                                            }
                                        }

                                        if (isItemTrackingRequired) {   //default isItemTrackingRequired

                                            Intent intent = new Intent(getBaseContext(), ItemReclassificationLotEntryActivity.class);
                                            intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNumber);
                                            intent.putExtra("IS_NEW_ADD", "NO");
                                            intent.putExtra("IS_SCANNING", "NO");
                                            intent.putExtra("DOCUMENT_NO", mDocNo);
                                            if (mIsShowBinCode) {
                                                if (mBinCode != null) {
                                                    intent.putExtra("SHOW_BINCODE", mIsShowBinCode);
                                                    intent.putExtra("BINCODE", mBinCode);
                                                }
                                            }
                                            if (mIsShowNewBinCode) {
                                                if (mNewBinCode != null) {
                                                    intent.putExtra("SHOW_NEWBINCODE", mIsShowNewBinCode);
                                                    intent.putExtra("NEWBINCODE", mNewBinCode);
                                                }
                                            }
                                            intent.putExtra("LINE_NO", mLineNo);
                                            startActivity(intent);

                                        } else {

                                            final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
                                            Window window = dialog.getWindow();
                                            window.setGravity(Gravity.CENTER);
                                            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                            window.setDimAmount(0.2f);

                                            dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                                            dialog.setContentView(R.layout.quantity_update_item_reclass_line);
                                            dialog.setCancelable(false);

                                            TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
                                            title.setText("Item No: " + mItemNo + "/" + mUom);

                                            Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

                                            EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                            editUpdateQuantity.setText(Float.toString(prevQuantity));

                                            btnSave.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                                    String quantity = editUpdateQuantity.getText().toString();

                                                    if (quantity.length() != 0 && !quantity.equals(".")) {
                                                        mPostUpdateItemQuantityTask = new PostUpdateItemQuantityTask((Activity) mContext, mLineNo, Float.parseFloat(quantity), mUom);
                                                        mPostUpdateItemQuantityTask.execute((Void) null);

                                                        dialog.dismiss();
                                                    } else {
                                                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                                                    }

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
                                    }
                                });

                            } else {
                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mItemReclassResult.getMessage());
                            }
                        } else {
                            lblNoData.setVisibility(View.VISIBLE);
                            itemList.setVisibility(View.GONE);
                            btnPost.setVisibility(View.GONE);
                        }
                    } else {
                        lblNoData.setVisibility(View.VISIBLE);
                        itemList.setVisibility(View.GONE);
                        btnPost.setVisibility(View.GONE);
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

    public class RemoveItemReclassLineTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        RemoveItemReclassLineParameter mRemoveItemReclassLineParameter;
        RemoveItemReclassLineResult mReturnBaseResult;

        RemoveItemReclassLineTask(Activity activity, int LineNo) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_DELETE_DATA, NotificationManager.DIALOG_MSG_DELETE);
            mActivity = activity;
            mRemoveItemReclassLineParameter = new RemoveItemReclassLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), LineNo);
            Gson gson = new Gson();
            String json = gson.toJson(mRemoveItemReclassLineParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<RemoveItemReclassLineResult> call = mApp.getNavBrokerService().RemoveItemReclassLine(mRemoveItemReclassLineParameter);
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
                        if (!isLastItem) {
                            ItemReclassLineArrayAdapter.notifyDataSetChanged();
                            mHeaderLineNumber = mReturnBaseResult.getReclassHeaderLineNo();
                            mGetItemReclassLineDetailsTask = new GetItemReclassLineDetailsTask((Activity) mContext, mReturnBaseResult.getReclassHeaderLineNo());
                            mGetItemReclassLineDetailsTask.execute((Void) null);
                        } else {
                            Intent intent = new Intent(getBaseContext(), ItemReclassificationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } else {
                        if (isLastItem) {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_REMOVE_FAILED, mReturnBaseResult.getMessage());
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_REMOVE_FAILED, mReturnBaseResult.getMessage());
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