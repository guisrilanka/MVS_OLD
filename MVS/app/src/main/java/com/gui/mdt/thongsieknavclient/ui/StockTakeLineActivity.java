package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.ConfigurationManager;
import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.stocktake.StockTakeLineArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.PostStockTakeParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntriesListResultData;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntryParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeLineData;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeNewLineEntry;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.UpdateStockTakeLineParameter;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialRecords;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.StockTakeAddNewLineEntry;
import com.orm.SugarRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class StockTakeLineActivity extends BaseActivity {

    PostAllStockTakeLineTask mPostAllStockTakeLineTask = null;
    PostUpdateStockTakeItemQuantityTask mPostUpdateScannedItemQuantityTask = null;
    GetStockTakeLineDetailsTask mGetStockTakeLineDetailsTask = null;
    SearchFilterStockTakeLineDetailsTask mSearchFilterStockTakeLineDetailsTask = null;

    ListView itemList;
    Context mContext;
    private NavClientApp mApp;
    private TextView lblDocNo;
    private TextView lblLoc;

    private String mItemNo;
    private String mDocNo;
    private String mUom;
    private String scannedItemNo;
    private String scannedSerialNo;
    private String scannedLotNo;
    private String mFilterItemDesc;
    private String mFilterItemCode;
    private String mFilterLocationCode;
    private String mFilterLocationLineNo;
    private int mHeaderLineNumber;
    private int mLineNo;
    private int entryIndexCounter;
    private boolean mIsShowBinCode = false;
    private boolean isStarted = false;

    ArrayList<StockTakeNewLineEntry> tmp_list;
    StockTakeLineArrayAdapter stockTakeArrayAdapter;
    List<StockTakeLineData> mStockTakeEntriesListResult;
    ArrayList<StockTakeLineData> temp_list;

    protected int getLayoutResource() {
        return R.layout.activity_stock_take_line;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_line);

        mContext = this;
        mApp = (NavClientApp) getApplication();
        entryIndexCounter = 0;
        scannedLotNo = "";
        scannedSerialNo = "";
        tmp_list = new ArrayList<StockTakeNewLineEntry>();

        itemList = (ListView) findViewById(R.id.orderList);
        lblDocNo = (TextView) findViewById(R.id.lblDocNo);
        lblLoc = (TextView) findViewById(R.id.lblLoc);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            mIsShowBinCode = extras.getBoolean("IS_SHOW_BINCODE");
            mFilterLocationCode = extras.getString("LOCATION_CODE");
            mFilterLocationLineNo = extras.getString("LOCATION_LINE_NO");  //mFilterLocationLineNo = mHeaderLineNo
            mDocNo = extras.getString("DOCUMENT_NO");

            if (mDocNo != null) {
                lblDocNo.setText("Doc No: " + mDocNo);
            } else {
                lblDocNo.setText("Doc No: " + "");
            }

            if (mFilterLocationCode != null) {
                lblLoc.setText("Loc: " + mFilterLocationCode);
            } else {
                lblLoc.setText("Loc: " + "");
            }

            mHeaderLineNumber = DataManager.getInstance().getStockTakeListResultData().getStockTakeHeaderLineNo();

            mGetStockTakeLineDetailsTask = new GetStockTakeLineDetailsTask((Activity) mContext, Integer.valueOf(mFilterLocationLineNo));
            mGetStockTakeLineDetailsTask.execute((Void) null);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Stock Take Line");
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

                    temp_list = new ArrayList<StockTakeLineData>();

                    if (mStockTakeEntriesListResult != null) {
                        temp_list.addAll(mStockTakeEntriesListResult);
                    }

                    scannedItemNo = splitString[0];
                    mUom = splitString[2];
                    scannedLotNo = splitString[3];
                    scannedSerialNo = splitString[5];
                    mLineNo = 0;
                    boolean isItemTrackingRequired = false;
                    Float prevQuantity = 0f;

                    for (int x = 0; x < temp_list.size(); x++) {
                        if (temp_list.get(x).getItemNo().contains(splitString[0])) {
                            Log.d("ITEM FOUND", String.valueOf(mLineNo));
                            mLineNo = temp_list.get(x).getStockTakeEntryLineNo();
                            isItemTrackingRequired = temp_list.get(x).isItemTrackingRequired();
                            prevQuantity = temp_list.get(x).getQuantityPhysical();

                            if (isItemTrackingRequired) {
                                DataManager.getInstance().clearTransitionStockTakeLineData();
                                DataManager.getInstance().setmStockTakeLineData(temp_list.get(x));
                            }
                            break;
                        }
                    }

                    if (splitString.length == 6) {

                        if (isItemTrackingRequired) {
                            Intent intent = new Intent(getBaseContext(), StockTakeLotEntryActivity.class);
                            intent.putExtra("IS_SCANNING", "YES");
                            intent.putExtra("IS_NEW_ADD", "YES");
                            intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                            intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                            intent.putExtra("DOCUMENT_NO", mDocNo);
                            intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
                            intent.putExtra("SCAN_CONTENT", scannedString);
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

                                                    tmp_list.add(new StockTakeNewLineEntry(String.valueOf(mHeaderLineNumber), scannedItemNo, "", "", "", "", "", "", String.valueOf(splitString[1]), scannedLotNo, scannedSerialNo, entryIndexCounter, true, entryIndexCounter, -1, ConfigurationManager.DATA_UPLOADED_FALSE));

                                                    StockTakeAddNewLineEntry temp_entry = new StockTakeAddNewLineEntry(String.valueOf(mHeaderLineNumber), scannedItemNo, mLineNo, "", "", String.valueOf(splitString[1]), scannedLotNo, scannedSerialNo, "", true, entryIndexCounter, ConfigurationManager.DATA_UPLOADED_FALSE, System.currentTimeMillis());

                                                    temp_entry.save();

                                                    DataManager.getInstance().AddSerialRecord(String.valueOf(mHeaderLineNumber), ConfigurationManager.ORDER_TYPE_STOCK_TAKE, scannedItemNo, String.valueOf(mLineNo), scannedLotNo, scannedSerialNo);

                                                    entryIndexCounter++;

                                                    Log.d("SUGARORM", "ItemNo: " + scannedItemNo);

                                                    List<StockTakeAddNewLineEntry> tmp = SugarRecord.find(StockTakeAddNewLineEntry.class, "item_no = ?", scannedItemNo);

                                                    for (int i = 0; i < tmp.size(); i++) {

                                                        Log.d("SUGARORM", i + ": " + tmp.get(i).toString());

                                                    }

                                                    mPostUpdateScannedItemQuantityTask = new PostUpdateStockTakeItemQuantityTask((Activity) mContext, mLineNo, Float.parseFloat(quantity));
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

                                    } else {
                                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                                    }

                                    scanText.setText("");
                                    scanText.requestFocus();
                                } else {
                                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_MSG_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_SERIALNO);
                                    scanText.setText("");
                                    scanText.requestFocus();
                                }

                            } else {
                                Intent intent = new Intent(getBaseContext(), AddNewStockTakeLineActivity.class);
                                intent.putExtra("IS_SCANNING", "YES");
                                intent.putExtra("IS_NEW_ADD", "YES");
                                intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                                intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                                intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
                                intent.putExtra("DOCUMENT_NO", mDocNo);
                                intent.putExtra("SCAN_CONTENT", scannedString);
                                startActivity(intent);
                            }
                        }
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_MSG_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
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
                    subsQty = temp_list.get(i).getQuantityPhysical();
                    itemDescrip = temp_list.get(i).getDescription();
                    Log.d("Item<Qty to Rec>: ", String.valueOf(subsQty));

//                    if (subsQty == tmp) {
//                        confirmPostScan(subsQty, itemDescrip);
//                    }else
                    if (subsQty != Float.parseFloat(tmp)) {
                        confirmPost(Integer.parseInt(mFilterLocationLineNo));
                    }
                    break;
                }
            }
        });
    }

    private void confirmPost(final int mHeaderLineNumber) {
        new AlertDialog.Builder(mContext)
                .setTitle(getResources().getString(R.string.notification_title_confirmation))
                .setMessage(getResources().getString(R.string.notification_msg_confirmation_stock_take))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mPostAllStockTakeLineTask = new PostAllStockTakeLineTask((Activity) mContext, mHeaderLineNumber);
                        mPostAllStockTakeLineTask.execute((Void) null);
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

    public void confirmPostScan(float qtytoRec, String itemDescrip) {
        new AlertDialog.Builder(mContext)
                .setTitle(getResources().getString(R.string.notification_title_confirmation))
                .setMessage("Missing scanning line item,do you want to proceed?")
                .setPositiveButton(R.string.post, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        mPostTransferShipmentTask = new PostTransferShipmentTask((Activity) mContext);
//                        mPostTransferShipmentTask.execute((Void) null);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), StockTakeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("LOCATION_CODE", mFilterLocationCode);
        intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
        intent.putExtra("DOCUMENT_NO", mDocNo);
        intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stock_take_line_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchDialog();
                return true;
            case R.id.action_AddItemReclassLine:
                showAddNewStockTakeLineActivity();
                return true;
            case R.id.action_clearEntry:
                clearAllEntries();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSearchDialog() {

        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_search_stock_take_line);
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
                mSearchFilterStockTakeLineDetailsTask = new SearchFilterStockTakeLineDetailsTask((Activity) mContext, mHeaderLineNumber, mFilterItemCode, mFilterItemDesc);
                mSearchFilterStockTakeLineDetailsTask.execute((Void) null);
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

    private void showAddNewStockTakeLineActivity() {

        DataManager.getInstance().clearTransitionStockTakeListResultData();
        DataManager.getInstance().setStockTakeListResultData(new StockTakeEntriesListResultData(Integer.valueOf(mFilterLocationLineNo), mDocNo, mFilterLocationCode, mIsShowBinCode));

        Intent i = new Intent(StockTakeLineActivity.this, AddNewStockTakeLineActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("LOCATION_CODE", mFilterLocationCode);
        i.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
        i.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
        i.putExtra("DOCUMENT_NO", mDocNo);
        i.putExtra("IS_NEW_ADD", "YES");
        i.putExtra("IS_SCANNING", "NO");
        startActivity(i);
    }

    public void clearAllEntries() {

        List<StockTakeAddNewLineEntry> entryList = SugarRecord.find(StockTakeAddNewLineEntry.class, "line_no = ?", Integer.toString(mLineNo));
        if (entryList.size() > 0) {
            for (int i = 0; i < entryList.size(); i++) {
                entryList.get(i).delete();
            }
        }

        // REMOVE BARCODE SERIAL NUMBER - Delete all serial number and set quantity to 0
        if (mHeaderLineNumber != 0 && scannedItemNo != null && scannedLotNo != null) {
            List<BarcodeSerialRecords> deleteSerialList = DataManager.getInstance().mDeleteSerialRecord(String.valueOf(mHeaderLineNumber), ConfigurationManager.ORDER_TYPE_STOCK_TAKE, scannedItemNo, Integer.toString(mLineNo), scannedLotNo);

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

    private boolean checkItemExist(final String itemNo, String Uom) {
        if (mStockTakeEntriesListResult != null) {
            for (int i = 0; i < mStockTakeEntriesListResult.size(); i++) {
                if (mStockTakeEntriesListResult.get(i).getItemNo().contains(itemNo)
                        && mStockTakeEntriesListResult.get(i).getUom().contains(Uom)) {

                    DataManager.getInstance().setmStockTakeLineData(mStockTakeEntriesListResult.get(i));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkSerialExist() {
        if (scannedSerialNo.isEmpty()) {
            return false;
        }

        List<BarcodeSerialRecords> tempSerialList = DataManager.getInstance().GetSerialRecords(String.valueOf(mHeaderLineNumber), ConfigurationManager.ORDER_TYPE_STOCK_TAKE, scannedItemNo, String.valueOf(mLineNo), scannedLotNo);
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

    //Update stock take item details
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
                try {
                    Log.d("RETURN STATUS", String.valueOf(mReturnBaseResult.getStatus()));
                    if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        mGetStockTakeLineDetailsTask = new GetStockTakeLineDetailsTask((Activity) mContext, Integer.valueOf(mFilterLocationLineNo));
                        mGetStockTakeLineDetailsTask.execute((Void) null);
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

    //Get all stock take line item
    public class GetStockTakeLineDetailsTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        StockTakeEntryParameter mStockTakeEntriesListParameter;

        GetStockTakeLineDetailsTask(Activity activity, int LocationLineNo) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mStockTakeEntriesListParameter = new StockTakeEntryParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), LocationLineNo, "", "");

            Gson gson = new Gson();
            String json = gson.toJson(mStockTakeEntriesListParameter);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<StockTakeLineData>> call = mApp.getNavBrokerService().StockTakeLineEntryList(mStockTakeEntriesListParameter);

                mStockTakeEntriesListResult = call.execute().body();

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
                    if (mStockTakeEntriesListResult != null) {

                        if (mStockTakeEntriesListResult.size() != 0) {
                            itemList.setAdapter(null);

                            temp_list = new ArrayList<StockTakeLineData>();

                            if (mStockTakeEntriesListResult != null) {
                                temp_list.addAll(mStockTakeEntriesListResult);
                            }

                            stockTakeArrayAdapter = new StockTakeLineArrayAdapter(mContext, temp_list);
                            itemList.setAdapter(stockTakeArrayAdapter);
                            itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    TextView lblItemIDUom = (TextView) view.findViewById(R.id.lblItemNoBaseUOM);
                                    mItemNo = lblItemIDUom.getText().toString().replace("Item No: ", "").split("/")[0];

                                    boolean isItemTrackingRequired = false;
                                    Float prevQuantity = 0f;

                                    for (int index = 0; index < temp_list.size(); index++) {
                                        mLineNo = temp_list.get(i).getStockTakeEntryLineNo();
                                        if (temp_list.get(index).getItemNo().contains(mItemNo) && temp_list.get(index).getStockTakeEntryLineNo() == mLineNo) {

                                            mLineNo = temp_list.get(index).getStockTakeEntryLineNo();
                                            isItemTrackingRequired = temp_list.get(index).isItemTrackingRequired();
                                            prevQuantity = temp_list.get(index).getQuantityPhysical();
                                            mUom = temp_list.get(index).getUom();

                                            DataManager.getInstance().clearTransitionStockTakeLineData();
                                            DataManager.getInstance().setmStockTakeLineData(temp_list.get(i));

                                            break;
                                        }
                                    }

                                    if (isItemTrackingRequired) {

                                        Intent intent = new Intent(getBaseContext(), StockTakeLotEntryActivity.class);
                                        intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                                        intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                                        intent.putExtra("DOCUMENT_NO", mDocNo);
                                        intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
                                        intent.putExtra("IS_SCANNING", "NO");
                                        intent.putExtra("IS_NEW_ADD", "NO");
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
                                                    mPostUpdateScannedItemQuantityTask = new PostUpdateStockTakeItemQuantityTask((Activity) mContext, mLineNo, Float.parseFloat(quantity));
                                                    mPostUpdateScannedItemQuantityTask.execute((Void) null);

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
                            TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                            lblNoData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                        lblNoData.setVisibility(View.VISIBLE);
                    }

                    //DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_STOCK_TAKE);
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else

            {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, getResources().getString(R.string.notification_msg_server_no_response));
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class SearchFilterStockTakeLineDetailsTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        List<StockTakeLineData> mStockTakeEntriesListResult;
        StockTakeEntryParameter mStockTakeEntriesListParameter;

        SearchFilterStockTakeLineDetailsTask(Activity activity, int FilterLocationLineNo, String mFilterItemCode, String mFilterItemDesc) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mStockTakeEntriesListParameter = new StockTakeEntryParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), FilterLocationLineNo, mFilterItemCode.toUpperCase(), mFilterItemDesc);

            Gson gson = new Gson();
            String json = gson.toJson(mStockTakeEntriesListParameter);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<StockTakeLineData>> call = mApp.getNavBrokerService().StockTakeLineEntryList(mStockTakeEntriesListParameter);

                mStockTakeEntriesListResult = call.execute().body();

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
                    TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                    Button btnPost = (Button) findViewById(R.id.btnPost);
                    if (mStockTakeEntriesListResult != null) {
                        if (mStockTakeEntriesListResult.size() != 0) {
                            itemList.setAdapter(null);
                            lblNoData.setVisibility(View.GONE);
                            itemList.setVisibility(View.VISIBLE);
                            btnPost.setVisibility(View.VISIBLE);

                            final ArrayList<StockTakeLineData> temp_list = new ArrayList<StockTakeLineData>();

                            if (mStockTakeEntriesListResult != null) {
                                temp_list.addAll(mStockTakeEntriesListResult);
                            }

                            stockTakeArrayAdapter = new StockTakeLineArrayAdapter(mContext, temp_list);
                            itemList.setAdapter(stockTakeArrayAdapter);
                            itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    TextView lblItemIDUom = (TextView) view.findViewById(R.id.lblItemNoBaseUOM);
                                    mItemNo = lblItemIDUom.getText().toString().replace("Item No: ", "").split("/")[0];

                                    boolean isItemTrackingRequired = false;
                                    Float prevQuantity = 0f;

                                    for (int index = 0; index < temp_list.size(); index++) {
                                        mLineNo = temp_list.get(i).getStockTakeEntryLineNo();
                                        if (temp_list.get(index).getItemNo().contains(mItemNo) && temp_list.get(index).getStockTakeEntryLineNo() == mLineNo) {

                                            mLineNo = temp_list.get(index).getStockTakeEntryLineNo();
                                            isItemTrackingRequired = temp_list.get(index).isItemTrackingRequired();
                                            prevQuantity = temp_list.get(index).getQuantityPhysical();
                                            mUom = temp_list.get(index).getUom();

                                            DataManager.getInstance().clearTransitionStockTakeLineData();
                                            DataManager.getInstance().setmStockTakeLineData(temp_list.get(i));
                                            break;
                                        }
                                    }

                                    if (isItemTrackingRequired) {

                                        Intent intent = new Intent(getBaseContext(), StockTakeLotEntryActivity.class);
                                        intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                                        intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                                        intent.putExtra("DOCUMENT_NO", mDocNo);
                                        intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
                                        intent.putExtra("IS_SCANNING", "NO");
                                        intent.putExtra("IS_NEW_ADD", "NO");
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
                                                    mPostUpdateScannedItemQuantityTask = new PostUpdateStockTakeItemQuantityTask((Activity) mContext, mLineNo, Float.parseFloat(quantity));
                                                    mPostUpdateScannedItemQuantityTask.execute((Void) null);

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

                            //DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_STOCK_TAKE);
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
            } else

            {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, getResources().getString(R.string.notification_msg_server_no_response));
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class PostAllStockTakeLineTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PostStockTakeParameter mPostItemReclassHeaderLineParameter;
        BaseResult mReturnBaseResult;

        PostAllStockTakeLineTask(Activity activity, int headerLineNo) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mPostItemReclassHeaderLineParameter = new PostStockTakeParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), String.valueOf(headerLineNo));

            Log.d("LAYER 2", "LineNo: " + mLineNo);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().PostStockTake(mPostItemReclassHeaderLineParameter);

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

                        Intent intent = new Intent(getBaseContext(), StockTakeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("LOCATION_CODE", mFilterLocationCode);
                        intent.putExtra("LOCATION_LINE_NO", mFilterLocationLineNo);
                        intent.putExtra("DOCUMENT_NO", mDocNo);
                        intent.putExtra("IS_SHOW_BINCODE", mIsShowBinCode);
                        startActivity(intent);

                        DataManager.getInstance().DeleteSerialRecord(String.valueOf(mHeaderLineNumber), ConfigurationManager.ORDER_TYPE_STOCK_TAKE);
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
}
