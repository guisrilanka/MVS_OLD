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
import android.view.Menu;
import android.view.MenuInflater;
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
import com.gui.mdt.thongsieknavclient.adapters.purchaseorder.PurchaseOrderLineArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.LineData;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PostPurchaseOrder;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderListItem;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderListResult;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderResult;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderRetrieveParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.UpdatePurchaseOrderLineQuantityParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.UpdatePurchaseOrderVendorshipmentNoParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class PurchaseOrderLineActivity extends BaseActivity {

    private GetPurchaseOrderDetailsTask mGetPurchaseOrderDetailsTask = null;
    private PostPurchaseOrderTask mPostPurchaseOrderTask = null;
    private PostUpdateItemQuantityTask mPostUpdateItemQuantityTask = null;
    private UpdateVendorShipmentNoTask mUpdateVendorShipmentNoTask = null;
    private GetPurchaseOrderListTask mGetPurchaseOrderListTask = null;
    private GetFilterItemCodeandDescripPurchaseOrderDetailsTask mGetFilterItemCodeandDescripPurchaseOrderDetailsTask = null;

    ListView itemList;
    PurchaseOrderLineArrayAdapter purchaseOrderItemAdapter;
    PurchaseOrderResult mPurchaseOrderResult;
    BaseResult mReturnBaseResult;

    Context mContext;

    private NavClientApp mApp;

    private String mPurchaseOrderNo;
    private String mItemNo;
    private String mLineNo;
    private String vendorShiptNo;
    private String mFilterItemDesc = "";
    private String mFilterItemCode = "";
    private TextView lblVendorShipNo;

    private boolean isStarted = false;
    private boolean isEnded = false;
    ArrayList<LineData> temp_list;

    protected int getLayoutResource() {
        return R.layout.activity_purchase_order_line;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order_line);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPurchaseOrderNo = extras.getString("PURCHASE_ORDER_NO");

            mGetPurchaseOrderDetailsTask = new GetPurchaseOrderDetailsTask(this);
            mGetPurchaseOrderDetailsTask.execute((Void) null);

            mGetPurchaseOrderListTask = new GetPurchaseOrderListTask((Activity) mContext, mPurchaseOrderNo);
            mGetPurchaseOrderListTask.execute((Void) null);
        }

        lblVendorShipNo = (TextView) findViewById(R.id.lblVendorShipNo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("PO NO: " + mPurchaseOrderNo);
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
                if (charSequence.toString().length() > 1 && !isStarted) {
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
                        final ArrayList<LineData> temp_list = new ArrayList<LineData>();
                        if (mPurchaseOrderResult != null) {
                            if (mPurchaseOrderResult.PurchaseOrderData != null && mPurchaseOrderResult.PurchaseOrderData.getLinesDataList() != null) {
                                temp_list.addAll(mPurchaseOrderResult.PurchaseOrderData.getLinesDataList());
                            }
                        }
                        mItemNo = scannedString.split(" / ")[0];
                        mLineNo = "";
                        boolean isItemTrackingRequired = false;
                        Float prevQuantity = 0f;

                        if (checkItemExist(splitString[0])) {
                            //NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
                            DataManager.getInstance().PurchaseOrderNo = mPurchaseOrderNo;

                        } else {
                            //NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                            scanText.setText("");
                            scanText.requestFocus();    // clear and focus for next scan
                        }

                        for (int x = 0; x < temp_list.size(); x++) {
                            if (temp_list.get(x).getItemNo().contains(splitString[0])) {
                                Log.d("ITEM FOUND", mLineNo);
                                Log.d("ITEM FOUND", mPurchaseOrderNo);
                                mLineNo = temp_list.get(x).getLineNo();
                                isItemTrackingRequired = temp_list.get(x).getIsItemTrackingRequired();
                                prevQuantity = Float.parseFloat(splitString[1]);

                                if (isItemTrackingRequired) {
                                    DataManager.getInstance().clearTransitionData();
                                    DataManager.getInstance().PurchaseOrderNo = mPurchaseOrderNo;
                                    DataManager.getInstance().setItemTransitionLineData(temp_list.get(i));

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

                if (editable.toString().length() > 4 && editable.toString().startsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL) && editable.toString().endsWith(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL)) {
                    isStarted = false;

                    String scannedString = editable.toString().replace(ConfigurationManager.BARCODE_CHECK_SUM_SYMBOL, "");
                    String[] splitString = scannedString.split("\\|");
                    //--------------------------------------------------------------------------------------------------------
                    //                                              START HERE
                    //--------------------------------------------------------------------------------------------------------
                    final ArrayList<LineData> temp_list = new ArrayList<LineData>();
                    if (mPurchaseOrderResult != null) {
                        if (mPurchaseOrderResult.PurchaseOrderData != null && mPurchaseOrderResult.PurchaseOrderData.getLinesDataList() != null) {
                            temp_list.addAll(mPurchaseOrderResult.PurchaseOrderData.getLinesDataList());
                        }
                    }
                    mItemNo = scannedString.split(" / ")[0];
                    mLineNo = "";
                    boolean isItemTrackingRequired = false;
                    Float prevQuantity = 0f;

                    for (int x = 0; x < temp_list.size(); x++) {
                        if (temp_list.get(x).getItemNo().contains(splitString[0])) {
                            Log.d("ITEM FOUND", mLineNo);
                            Log.d("ITEM FOUND", mPurchaseOrderNo);
                            mLineNo = temp_list.get(x).getLineNo();
                            isItemTrackingRequired = temp_list.get(x).getIsItemTrackingRequired();
                            prevQuantity = Float.parseFloat(splitString[1]);

                            if (isItemTrackingRequired) {
                                DataManager.getInstance().clearTransitionData();
                                DataManager.getInstance().PurchaseOrderNo = mPurchaseOrderNo;
                                DataManager.getInstance().setItemTransitionLineData(temp_list.get(x));

                            }
                            break;
                        }
                    }

                    if (splitString.length == 6) {
                        if (checkItemExist(splitString[0])) {
                            DataManager.getInstance().PurchaseOrderNo = mPurchaseOrderNo;

                            if (isItemTrackingRequired) {
                                Intent intent = new Intent(getBaseContext(), PurchaseOrderLotEntryActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.po_line_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkItemExist(String itemNo) {
        if (mPurchaseOrderResult != null && mPurchaseOrderResult.PurchaseOrderData.getLinesDataList() != null) {
            for (int i = 0; i < mPurchaseOrderResult.PurchaseOrderData.getLinesDataList().size(); i++) {
                if (mPurchaseOrderResult.PurchaseOrderData.getLinesDataList().get(i).getItemNo().contains(itemNo)) {
                    DataManager.getInstance().setItemTransitionLineData(mPurchaseOrderResult.PurchaseOrderData.getLinesDataList().get(i));
                    return true;
                }
            }
        }
        return false;
    }

    public class GetPurchaseOrderDetailsTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PurchaseOrderRetrieveParameter mPurchaseOrderRetrieveParameter;

        GetPurchaseOrderDetailsTask(Activity activity) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

            mActivity = activity;
            mPurchaseOrderRetrieveParameter = new PurchaseOrderRetrieveParameter(mApp.getmUserCompany(), mPurchaseOrderNo, mApp.getCurrentUserName(), mApp.getCurrentUserPassword(),"","");
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PurchaseOrderResult> call = mApp.getNavBrokerService().GetPurchaseOrderDetails(mPurchaseOrderRetrieveParameter);

                mPurchaseOrderResult = call.execute().body();

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
                    if (mPurchaseOrderResult != null) {

                        if(mPurchaseOrderResult.PurchaseOrderData.getLinesDataList().size() != 0) {
                            Gson gson = new Gson();
                            String json = gson.toJson(mPurchaseOrderNo);
                            Log.d("PURCHASE ORDER", json);

                            if (mPurchaseOrderResult.getStatus() == BaseResult.BaseResultStatusOk) {
                                itemList = (ListView) findViewById(R.id.orderList);

                                temp_list = new ArrayList<LineData>();

                                if (mPurchaseOrderResult != null) {
                                    if (mPurchaseOrderResult.PurchaseOrderData != null && mPurchaseOrderResult.PurchaseOrderData.getLinesDataList() != null) {
                                        temp_list.addAll(mPurchaseOrderResult.PurchaseOrderData.getLinesDataList());
                                    }
                                }

                                purchaseOrderItemAdapter = new PurchaseOrderLineArrayAdapter(mContext, temp_list);

                                itemList.setAdapter(purchaseOrderItemAdapter);
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
                                                Log.d("ITEM FOUND", mPurchaseOrderNo);
                                                mLineNo = temp_list.get(index).getLineNo();
                                                isItemTrackingRequired = temp_list.get(index).getIsItemTrackingRequired();
                                                prevQuantity = Float.parseFloat(temp_list.get(index).getQuantityToReceive());

                                                if (isItemTrackingRequired) {
                                                    DataManager.getInstance().clearTransitionData();
                                                    DataManager.getInstance().PurchaseOrderNo = mPurchaseOrderNo;
                                                    DataManager.getInstance().setItemTransitionLineData(temp_list.get(i));
                                                }
                                                break;
                                            }
                                        }

                                        if (isItemTrackingRequired) {
                                            //NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

                                            Intent intent = new Intent(getBaseContext(), PurchaseOrderLotEntryActivity.class);
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

                                            Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

                                            EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                            editUpdateQuantity.setText(Float.toString(prevQuantity));

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
                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mPurchaseOrderResult.getMessage());
                            }
                        }else {
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

                Intent intent = new Intent(getBaseContext(), PurchaseOrderActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    //Filter Item Code and Desc
    public class GetFilterItemCodeandDescripPurchaseOrderDetailsTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PurchaseOrderRetrieveParameter mPurchaseOrderRetrieveParameter;

        GetFilterItemCodeandDescripPurchaseOrderDetailsTask(Activity activity, String xFilterItemCode, String xFilterItemDesc) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

            mActivity = activity;
            mPurchaseOrderRetrieveParameter = new PurchaseOrderRetrieveParameter(mApp.getmUserCompany(), mPurchaseOrderNo, mApp.getCurrentUserName(), mApp.getCurrentUserPassword(),xFilterItemCode,xFilterItemDesc);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PurchaseOrderResult> call = mApp.getNavBrokerService().GetPurchaseOrderDetails(mPurchaseOrderRetrieveParameter);

                mPurchaseOrderResult = call.execute().body();

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
                    if (mPurchaseOrderResult != null) {

                        Gson gson = new Gson();
                        String json = gson.toJson(mPurchaseOrderNo);
                        Log.d("PURCHASE ORDER", json);

                        if (mPurchaseOrderResult.getStatus() == BaseResult.BaseResultStatusOk) {
                            itemList = (ListView) findViewById(R.id.orderList);

                            temp_list = new ArrayList<LineData>();

                            if (mPurchaseOrderResult != null) {
                                if (mPurchaseOrderResult.PurchaseOrderData != null && mPurchaseOrderResult.PurchaseOrderData.getLinesDataList() != null) {
                                    temp_list.addAll(mPurchaseOrderResult.PurchaseOrderData.getLinesDataList());
                                }
                            }

                            purchaseOrderItemAdapter = new PurchaseOrderLineArrayAdapter(mContext, temp_list);

                            itemList.setAdapter(purchaseOrderItemAdapter);
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
                                            Log.d("ITEM FOUND", mPurchaseOrderNo);
                                            mLineNo = temp_list.get(index).getLineNo();
                                            isItemTrackingRequired = temp_list.get(index).getIsItemTrackingRequired();
                                            prevQuantity = Float.parseFloat(temp_list.get(index).getQuantityToReceive());

                                            if (isItemTrackingRequired) {
                                                DataManager.getInstance().clearTransitionData();
                                                DataManager.getInstance().PurchaseOrderNo = mPurchaseOrderNo;
                                                DataManager.getInstance().setItemTransitionLineData(temp_list.get(i));
                                            }
                                            break;
                                        }
                                    }

                                    if (isItemTrackingRequired) {
                                        //NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

                                        Intent intent = new Intent(getBaseContext(), PurchaseOrderLotEntryActivity.class);
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

                                        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

                                        EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                        editUpdateQuantity.setText(Float.toString(prevQuantity));

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
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mPurchaseOrderResult.getMessage());
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

                Intent intent = new Intent(getBaseContext(), PurchaseOrderActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class PostPurchaseOrderTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PostPurchaseOrder mPurchaseOrder;

        PostPurchaseOrderTask(Activity activity) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_UPLOAD_DATA, NotificationManager.DIALOG_MSG_UPLOAD);

            mActivity = activity;

            mPurchaseOrder = new PostPurchaseOrder(mApp.getmUserCompany(), mPurchaseOrderNo, mApp.getCurrentUserName(), mApp.getCurrentUserPassword());
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().PostPurchaseOrder(mPurchaseOrder);

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
                    if (mReturnBaseResult != null) {
                        if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {
                            new AlertDialog.Builder(mContext)
                                    .setTitle(NotificationManager.TITLE_UPLOAD_SUCCESSFUL)
                                    .setMessage(NotificationManager.MSG_UPLOAD_SUCCESSFUL)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(getBaseContext(), PurchaseOrderActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setCancelable(false)
                                    .show();

                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_UPLOAD_FAILED, mReturnBaseResult.getMessage());
                        }

                    } else {
                        TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                        lblNoData.setVisibility(View.VISIBLE);
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
        UpdatePurchaseOrderLineQuantityParameter mPurchaseOrderLineQuantityParameter;

        PostUpdateItemQuantityTask(Activity activity, int lineNo, Float receivedQuantity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mPurchaseOrderLineQuantityParameter = new UpdatePurchaseOrderLineQuantityParameter(mApp.getmUserCompany(), mPurchaseOrderNo, mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), lineNo, receivedQuantity);

            Log.d("LAYER 2", "LineNo: " + mLineNo);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().UpdatePurchaseOrderLine(mPurchaseOrderLineQuantityParameter);

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
                    if (mReturnBaseResult != null) {

                        if (mReturnBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {

                    /*new AlertDialog.Builder(mContext)
                            .setTitle(NotificationManager.MSG_SAVE_SUCCESSFUL)
                            .setMessage("Data has been saved!")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mGetPurchaseOrderDetailsTask = new GetPurchaseOrderDetailsTask((Activity) mContext);
                                    mGetPurchaseOrderDetailsTask.execute((Void) null);
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();*/
                            mGetPurchaseOrderDetailsTask = new GetPurchaseOrderDetailsTask((Activity) mContext);
                            mGetPurchaseOrderDetailsTask.execute((Void) null);
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_SAVE_FAILED, mReturnBaseResult.getMessage());
                        }

                    } else {
                        TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                        lblNoData.setVisibility(View.VISIBLE);
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
        //same as back pressed event
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), PurchaseOrderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void confirmPost() {

        new AlertDialog.Builder(mContext)
                .setTitle(getResources().getString(R.string.notification_title_confirmation))
                .setMessage(getResources().getString(R.string.notification_msg_confirmation))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mPostPurchaseOrderTask = new PostPurchaseOrderTask((Activity) mContext);
                        mPostPurchaseOrderTask.execute((Void) null);
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
                        mPostPurchaseOrderTask = new PostPurchaseOrderTask((Activity) mContext);
                        mPostPurchaseOrderTask.execute((Void) null);
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

    public void updateVendorShipmentInfo(final String poNo, String finalVendorShiptNo) {

        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_vendor_shipment_info);
        dialog.setCancelable(false);

        TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
        if(finalVendorShiptNo != null) {
            title.setText("Vendor Shipt No: " + finalVendorShiptNo);
        }else {
            title.setText("Vendor Shipt No: ");
        }

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

        EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                String vendorShiptInfo = editUpdateQuantity.getText().toString();

                if (vendorShiptInfo.length() != 0) {
                    mUpdateVendorShipmentNoTask = new UpdateVendorShipmentNoTask((Activity) mContext, poNo, vendorShiptInfo);
                    mUpdateVendorShipmentNoTask.execute((Void) null);
                    dialog.dismiss();
                } else {
                    NotificationManager.DisplayAlertDialog(mContext, "Unable to Save", "Please enter vendor shipment information.");
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
                mGetFilterItemCodeandDescripPurchaseOrderDetailsTask = new GetFilterItemCodeandDescripPurchaseOrderDetailsTask((Activity) mContext, mFilterItemCode,mFilterItemDesc);
                mGetFilterItemCodeandDescripPurchaseOrderDetailsTask.execute((Void) null);

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

    //update vendor shiptment no
    public class UpdateVendorShipmentNoTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        BaseResult mResult;
        UpdatePurchaseOrderVendorshipmentNoParameter mPurchaseOrderUpdateVendorShiptNoParameter;

        UpdateVendorShipmentNoTask(Activity activity, String poNumber, String shiptNo) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mPurchaseOrderUpdateVendorShiptNoParameter = new UpdatePurchaseOrderVendorshipmentNoParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), poNumber, shiptNo);

            Gson gson = new Gson();
            String json = gson.toJson(mPurchaseOrderUpdateVendorShiptNoParameter);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().UpdatePurchaseOrderVendorshipmentNo(mPurchaseOrderUpdateVendorShiptNoParameter);

                mResult = call.execute().body();

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
                    if (mResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        mGetPurchaseOrderListTask = new GetPurchaseOrderListTask((Activity) mContext, mPurchaseOrderNo);
                        mGetPurchaseOrderListTask.execute((Void) null);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mResult.getMessage());
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

    public class GetPurchaseOrderListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        PurchaseOrderListResult mPurchaseOrderListResult;
        PurchaseOrderListSearchParameter mPurchaseOrderListSearchParameter;
        List<PurchaseOrderListItem> PurchaseOrderListResultData;

        GetPurchaseOrderListTask(Activity activity, String poNumber) {
//            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mPurchaseOrderListSearchParameter = new PurchaseOrderListSearchParameter(mApp.getmUserCompany(), "", poNumber, "", mApp.getCurrentUserName(), mApp.getCurrentUserPassword());

            Gson gson = new Gson();
            String json = gson.toJson(mPurchaseOrderListSearchParameter);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PurchaseOrderListResult> call = mApp.getNavBrokerService().GetPurchaseOrderList(mPurchaseOrderListSearchParameter);

                mPurchaseOrderListResult = call.execute().body();

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
                    if (mPurchaseOrderListResult != null) {

                        if (mPurchaseOrderListResult.getStatus() == BaseResult.BaseResultStatusOk) {

                            PurchaseOrderListResultData = mPurchaseOrderListResult.getPurchaseOrderList();

                            for (int x = 0; x < PurchaseOrderListResultData.size(); x++) {
                                vendorShiptNo = PurchaseOrderListResultData.get(x).getVendorShipmentNo();
                                if(PurchaseOrderListResultData.get(x).getVendorShipmentNo() != null) {
                                    lblVendorShipNo.setText("Vendor Shipt No: " + PurchaseOrderListResultData.get(x).getVendorShipmentNo());
                                }else {
                                    lblVendorShipNo.setText("Vendor Shipt No: ");
                                }
                                break;
                            }
                            lblVendorShipNo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    updateVendorShipmentInfo(mPurchaseOrderNo, vendorShiptNo);
                                }
                            });
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mPurchaseOrderListResult.getMessage());
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
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }
}
