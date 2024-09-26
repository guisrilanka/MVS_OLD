package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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
import com.gui.mdt.thongsieknavclient.adapters.transferreceipt.TransferReceiptLineArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.TransferReceiptItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.PostTransferReceipt;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptLineData;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptResult;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptRetrieveParameter;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.UpdateTransferReceiptLineQuantityParameter;
import com.orm.SugarRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class TransferInLineActivity extends BaseActivity {

    GetTransferReceiptDetailsTask mGetTransferReceiptDetailsTask = null;
    PostTransferReceiptTask mPostTransferReceiptTask = null;
    PostUpdateItemQuantityTask mPostUpdateItemQuantityTask = null;

    ListView itemList;
    TransferReceiptLineArrayAdapter transferReceiptLineArrayAdapter;
    TransferReceiptResult mTransferReceiptResult;

    Context mContext;

    private NavClientApp mApp;

    private String mTransferOrderNo;
    private String mLineNo;
    private String mItemNo;

    private boolean isStarted = false;
    private boolean isEnded = false;
    ArrayList<TransferReceiptLineData> temp_list;

    protected int getLayoutResource() {
        return R.layout.activity_transfer_in_line;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_in_line);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mTransferOrderNo = extras.getString("TRANSFER_ORDER_NO");
            //The key argument here must match that used in the other activity
            Log.d("TRANSFER NO", mTransferOrderNo);
            mGetTransferReceiptDetailsTask = new GetTransferReceiptDetailsTask(this);
            mGetTransferReceiptDetailsTask.execute((Void) null);
        } else {
            NotificationManager.HideProgressDialog();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("TRANSFER NO: " + mTransferOrderNo);
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 1 && !isStarted)  // checking for device that scans everything then place all into the textfield in one go
                {
                    if (charSequence.toString().length() > 4 && !charSequence.toString().startsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL) && !charSequence.toString().endsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL)) {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
                        scanText.setText("");
                        scanText.requestFocus();
                    } else if (charSequence.toString().length() > 4 && charSequence.toString().startsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL) && charSequence.toString().endsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL)) {
                        isStarted = false;

                        String scannedString = charSequence.toString().replace(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL, "");
                        String[] splitString = scannedString.split("\\|");

                        //--------------------------------------------------------------------------------------------------------
                        //                                          START HERE
                        //--------------------------------------------------------------------------------------------------------
                        final ArrayList<TransferReceiptLineData> temp_list = new ArrayList<TransferReceiptLineData>();
                        if (mTransferReceiptResult != null) {
                            if (mTransferReceiptResult.TransferReceiptData != null && mTransferReceiptResult.TransferReceiptData.getLinesDataList() != null) {
                                temp_list.addAll(mTransferReceiptResult.TransferReceiptData.getLinesDataList());
                            }
                        }
                        mItemNo = scannedString.split(" / ")[0];
                        mLineNo = "";
                        boolean isItemTrackingRequired = false;
                        Float prevQuantity = 0f;

                        if (checkItemExist(splitString[0])) {
                            //NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
                            DataManager.getInstance().TransferNo = mTransferOrderNo;

                        } else {
                            //NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                            scanText.setText("");
                            scanText.requestFocus();    // clear and focus for next scan
                        }

                        for (int x = 0; x < temp_list.size(); x++) {
                            if (temp_list.get(x).getItemNo().contains(splitString[0])) {
                                Log.d("ITEM FOUND", mLineNo);
                                Log.d("ITEM FOUND", mTransferOrderNo);
                                mLineNo = temp_list.get(x).getLineNo();
                                isItemTrackingRequired = temp_list.get(x).isItemTrackingRequired();
                                prevQuantity = Float.parseFloat(splitString[1]);

                                if (isItemTrackingRequired) {
                                    DataManager.getInstance().clearTransitionTransferData();
                                    DataManager.getInstance().TransferNo = mTransferOrderNo;
                                    DataManager.getInstance().setTransitionTransferInData(temp_list.get(i));

                                }
                                break;
                            }
                        }
                        //--------------------------------------------------------------------------------------------------
                        //                                       END HERE
                        //--------------------------------------------------------------------------------------------------
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 1) {
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

                // checking for device that scans and input one character by one character
                if (editable.toString().length() > 4 && editable.toString().startsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL) && editable.toString().endsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL)) {
                    isStarted = false;

                    String scannedString = editable.toString().replace(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL, "");
                    String[] splitString = scannedString.split("\\|");

                    //--------------------------------------------------------------------------------------------------------
                    //                                              START HERE
                    //--------------------------------------------------------------------------------------------------------
                    final ArrayList<TransferReceiptLineData> temp_list = new ArrayList<TransferReceiptLineData>();
                    if (mTransferReceiptResult != null) {
                        if (mTransferReceiptResult.TransferReceiptData != null && mTransferReceiptResult.TransferReceiptData.getLinesDataList() != null) {
                            temp_list.addAll(mTransferReceiptResult.TransferReceiptData.getLinesDataList());
                        }
                    }
                    mItemNo = scannedString.split(" / ")[0];
                    mLineNo = "";
                    boolean isItemTrackingRequired = false;
                    Float prevQuantity = 0f;

                    for (int x = 0; x < temp_list.size(); x++) {
                        if (temp_list.get(x).getItemNo().contains(splitString[0])) {
                            Log.d("ITEM FOUND", mLineNo);
                            Log.d("ITEM FOUND", mTransferOrderNo);
                            mLineNo = temp_list.get(x).getLineNo();
                            isItemTrackingRequired = temp_list.get(x).isItemTrackingRequired();
                            prevQuantity = Float.parseFloat(splitString[1]);

                            if (isItemTrackingRequired) {
                                DataManager.getInstance().clearTransitionTransferData();
                                DataManager.getInstance().TransferNo = mTransferOrderNo;
                                DataManager.getInstance().setTransitionTransferInData(temp_list.get(x));

                            }
                            break;
                        }
                    }

                    if (splitString.length == 6) {
                        if (checkItemExist(splitString[0])) {
                            DataManager.getInstance().TransferNo = mTransferOrderNo;

                            if (isItemTrackingRequired) {
                                Intent intent = new Intent(getBaseContext(), TransferInLotEntryActivity.class);
                                intent.putExtra("IS_SCANNING", "YES");
                                intent.putExtra("SCAN_CONTENT", scannedString);
                                startActivity(intent);
                            } else {
                                scanText.setText("");
                                scanText.requestFocus();
                                final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

                                // Setting dialogview
                                Window window = dialog.getWindow();
                                window.setGravity(Gravity.CENTER);
                                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                window.setDimAmount(0.2f);

                                dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                                dialog.setContentView(R.layout.dialog_quantity_update);
                                dialog.setCancelable(false);

                                TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
                                title.setText("Item No: " + splitString[0]);

                                EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                editUpdateQuantity.setText(Float.toString(prevQuantity));

                                Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

                                btnSave.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                        String quantity = editUpdateQuantity.getText().toString();

                                        if (quantity.length() != 0) {
                                            mPostUpdateItemQuantityTask = new PostUpdateItemQuantityTask((Activity) mContext, Integer.parseInt(mLineNo), Float.parseFloat(quantity));
                                            mPostUpdateItemQuantityTask.execute((Void) null);

                                            dialog.dismiss();
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
                                String quantity = editUpdateQuantity.getText().toString();
                                if (quantity.length() != 0) {
                                    dialog.dismiss();
                                    btnSave.callOnClick();
                                }
                            }
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                            scanText.setText("");
                            scanText.requestFocus();    // clear and focus for next scan
                        }
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
                        scanText.setText("");
                        scanText.requestFocus();
                    }
                    //--------------------------------------------------------------------------------------------------------
                    //                                       END HERE
                    //--------------------------------------------------------------------------------------------------------
                }
            }
        });

        Button btnPost = (Button) findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemDescrip = "";
                String subsQty = "";
                String tmp = "0.0";
                for (int i = 0; i < itemList.getCount(); i++) {
                    /*view = itemList.getChildAt(i);
                    et = (TextView) view.findViewById(R.id.lblReceivedQuantity);
                    itemDesc = (TextView) view.findViewById(R.id.lblItemDescription);
                    Qty = et.getText().toString();
                    itemDescrip = itemDesc.getText().toString();
                    tmp_listDesc.add("Item Desc: " + itemDescrip);
                    tmp_list.add("Qty to Rec: " + Qty);
                    subsQty = Qty.substring(12);*/
                    subsQty = temp_list.get(i).getQuantityToReceive();
                    itemDescrip = temp_list.get(i).getItemDescription();
                    Log.d("Item<Qty to Rec>: ", subsQty);

                    if (subsQty.matches(tmp)) {
                        confirmPostScan(subsQty, itemDescrip);
                    } else if (!subsQty.matches(tmp)) {
                        confirmPost();
                    }
                    break;
                }
            }
        });
    }

    private boolean checkItemExist(String itemNo) {
        if (mTransferReceiptResult != null && mTransferReceiptResult.TransferReceiptData.getLinesDataList() != null) {
            for (int i = 0; i < mTransferReceiptResult.TransferReceiptData.getLinesDataList().size(); i++) {
                if (mTransferReceiptResult.TransferReceiptData.getLinesDataList().get(i).getItemNo().contains(itemNo)) {
                    DataManager.getInstance().setTransitionTransferInData(mTransferReceiptResult.TransferReceiptData.getLinesDataList().get(i));
                    return true;
                }
            }
        }

        return false;
    }

    public class GetTransferReceiptDetailsTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        TransferReceiptRetrieveParameter mTransferReceiptRetrieveParameter;

        GetTransferReceiptDetailsTask(Activity activity) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

            mActivity = activity;
            mTransferReceiptRetrieveParameter = new TransferReceiptRetrieveParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mTransferOrderNo);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<TransferReceiptResult> call = mApp.getNavBrokerService().GetTransferReceiptDetails(mTransferReceiptRetrieveParameter);

                mTransferReceiptResult = call.execute().body();

                Gson gson = new Gson();
                String json = gson.toJson(mTransferReceiptRetrieveParameter);
                Log.d("TRANSFER ORDER", json);

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

                    if (mTransferReceiptResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        itemList = (ListView) findViewById(R.id.orderList);

                        temp_list = new ArrayList<TransferReceiptLineData>();

                        if (mTransferReceiptResult != null) {
                            if (mTransferReceiptResult.TransferReceiptData != null && mTransferReceiptResult.TransferReceiptData.getLinesDataList() != null) {
                                temp_list.addAll(mTransferReceiptResult.TransferReceiptData.getLinesDataList());
                            }
                        }

                        transferReceiptLineArrayAdapter = new TransferReceiptLineArrayAdapter(mContext, temp_list);

                        itemList.setAdapter(transferReceiptLineArrayAdapter);

                        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                TextView lblItemIDUom = (TextView) view.findViewById(R.id.lblItemIDUom);

                                mItemNo = lblItemIDUom.getText().toString().replace("Item No: ", "").split(" / ")[0];
                                mLineNo = "";
                                boolean isItemTrackingRequired = false;
                                Float prevQuantity = 0f;

                                for (int index = 0; index < temp_list.size(); index++) {
                                    mLineNo = temp_list.get(i).getLineNo();
                                    if (temp_list.get(index).getItemNo().contains(mItemNo) && temp_list.get(index).getLineNo() == mLineNo) {
                                        Log.d("ITEM FOUND", mLineNo);
                                        Log.d("ITEM FOUND", mTransferOrderNo);
                                        mLineNo = temp_list.get(index).getLineNo();
                                        isItemTrackingRequired = temp_list.get(index).isItemTrackingRequired();
                                        prevQuantity = Float.parseFloat(temp_list.get(index).getQuantityToReceive());

                                        if (isItemTrackingRequired) {
                                            DataManager.getInstance().clearTransitionTransferData();
                                            DataManager.getInstance().TransferNo = mTransferOrderNo;
                                            DataManager.getInstance().setTransitionTransferInData(temp_list.get(i));

                                        }
                                        break;
                                    }
                                }

                                if (isItemTrackingRequired) {
                                    Intent intent = new Intent(getBaseContext(), TransferInLotEntryActivity.class);
                                    intent.putExtra("IS_SCANNING", "NO");
                                    startActivity(intent);
                                } else {
                                    final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

                                    // Setting dialogview
                                    Window window = dialog.getWindow();
                                    window.setGravity(Gravity.CENTER);
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                    window.setDimAmount(0.2f);

                                    dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                                    dialog.setContentView(R.layout.dialog_quantity_update);
                                    dialog.setCancelable(false);

                                    TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
                                    title.setText("Item No: " + mItemNo);

                                    EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                    editUpdateQuantity.setText(Float.toString(prevQuantity));

                                    Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

                                    btnSave.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                            String quantity = editUpdateQuantity.getText().toString();

                                            if (quantity.length() != 0) {
                                                mPostUpdateItemQuantityTask = new PostUpdateItemQuantityTask((Activity) mContext, Integer.parseInt(mLineNo), Float.parseFloat(quantity));
                                                mPostUpdateItemQuantityTask.execute((Void) null);

                                                dialog.dismiss();
                                        /*
                                        if(Float.parseFloat(quantity) > Float.parseFloat(mPurchaseOrderQuantity))
                                        {
                                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_EXCEEDS_QUANTITY, NotificationManager.ALERT_MSG_EXCEEDS_QUANTITY);
                                        }
                                        else
                                        {
                                            Log.d("LAYER 2", quantity);

                                        }*/
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
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mTransferReceiptResult.getMessage());
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, getResources().getString(R.string.notification_msg_server_no_response));

                Intent intent = new Intent(getBaseContext(), TransferInActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class PostTransferReceiptTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PostTransferReceipt mPostTransferReceipt;
        BaseResult mReturnBaseResult;

        PostTransferReceiptTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_UPLOAD_DATA, NotificationManager.DIALOG_MSG_UPLOAD);

            mActivity = activity;

            mPostTransferReceipt = new PostTransferReceipt(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mTransferOrderNo);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().PostTransferReceipt(mPostTransferReceipt);

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
                        new AlertDialog.Builder(mContext)
                                .setTitle(NotificationManager.TITLE_UPLOAD_SUCCESSFUL)
                                .setMessage(NotificationManager.MSG_UPLOAD_SUCCESSFUL)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        clearAllEntries();

                                        Intent intent = new Intent(getBaseContext(), TransferInActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .show();
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_UPLOAD_FAILED, mReturnBaseResult.getMessage());
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_UPLOAD_FAILED, getResources().getString(R.string.notification_msg_server_no_response));
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class PostUpdateItemQuantityTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        UpdateTransferReceiptLineQuantityParameter mUpdateTransferReceiptLineQuantityParameter;
        BaseResult mReturnBaseResult;

        PostUpdateItemQuantityTask(Activity activity, int lineNo, Float receivedQuantity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mUpdateTransferReceiptLineQuantityParameter = new UpdateTransferReceiptLineQuantityParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mTransferOrderNo, lineNo, receivedQuantity, ConfigurationManager.UPDATE_ITEM_QUANTITY_BASE_FALSE);

            Gson gson = new Gson();
            String json = gson.toJson(mUpdateTransferReceiptLineQuantityParameter);
            Log.d("TRANSFER ORDER", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().UpdateTransferReceiptLine(mUpdateTransferReceiptLineQuantityParameter);

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
                        new AlertDialog.Builder(mContext)
                                .setTitle(NotificationManager.MSG_SAVE_SUCCESSFUL)
                                .setMessage("Data has been saved!")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mGetTransferReceiptDetailsTask = new GetTransferReceiptDetailsTask((Activity) mContext);
                                        mGetTransferReceiptDetailsTask.execute((Void) null);
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
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

    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), TransferInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void clearAllEntries() {
        List<TransferReceiptItemLineEntries> entryList = SugarRecord.find(TransferReceiptItemLineEntries.class, "transfer_no = ?", mTransferOrderNo);
        if (entryList.size() > 0) {
            for (int i = 0; i < entryList.size(); i++) {
                entryList.get(i).delete();
            }
        }
    }

    private void confirmPost() {
        new AlertDialog.Builder(mContext)
                .setTitle(getResources().getString(R.string.notification_title_confirmation))
                .setMessage(getResources().getString(R.string.notification_msg_confirmation))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mPostTransferReceiptTask = new PostTransferReceiptTask((Activity) mContext);
                        mPostTransferReceiptTask.execute((Void) null);
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
        //"Item Details:- \n" + "---------------------------\n" + itemDescrip + "/QTY TO REC: " + qtytoRec + "\n"
        new AlertDialog.Builder(mContext)
                .setTitle(getResources().getString(R.string.notification_title_confirmation))
                .setMessage("Missing scanning line item,do you want to proceed?")
                .setPositiveButton(R.string.post, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mPostTransferReceiptTask = new PostTransferReceiptTask((Activity) mContext);
                        mPostTransferReceiptTask.execute((Void) null);
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
}
