package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.ConfigurationManager;
import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.productionpastegroup.ProductionPasteGroupRoutingListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGoodsProductionOrderLinePrintParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderRoutingListResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderRoutingListResultData;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderRoutingEndRouteParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderRoutingListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderRoutingStartRouteParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.UpdateProductionOrderLineParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentResult;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class ProductionPasteGroupRoutingActivity extends BaseActivity {

    private TextView lblProductionOrderNo;
    private TextView lblDueDate;
    private TextView lblItemDescription;
    private TextView lblItemIDUom;
    private TextView lblPurchaseQuantity;
    private TextView lblFinishedQuantity;
    private TextView lblRemainingQuantity;

    private GetProductionRoutingListTask mGetProductionRoutingListTask = null;
    private PasteGroupProductionRoutingStartRouteTask mPasteGroupProductionRoutingStartRouteTask = null;
    private PasteGroupProductionRoutingEndRouteTask mPasteGroupProductionRoutingEndRouteTask = null;
    private PostUpdateItemQuantityTask mPostUpdateItemQuantityTask = null;
    private PrintPurchaseOrderReceiptLotNoPalletTask mPrintPurchaseOrderReceiptLotNoPalletTask;

    PasteGroupProductionOrderRoutingListResult mPasteGroupProductionOrderRoutingListResult;
    ListView historyList;
    ProductionPasteGroupRoutingListArrayAdapter historyAdapter;
    ArrayList<PasteGroupProductionOrderRoutingListResultData> temp_list;
    TransferShipmentResult mTrasferShipmentResult;

    private BaseResult mBaseResult;
    private NavClientApp mApp;
    private Context mContext;
    private String mProductionOrderNo;
    private String mLineNo;
    private String mItemNo;
    private float mPurchaseQtyBase;
    private float mFinishedQtyBase;
    private float mRemainingQtyBase;
    private String mLastLotNo;
    private TextView tvLotNumber;
    private String mOperationNo;
    private String mRoutingRefNo;
    private boolean isStarted;

    protected int getLayoutResource() {
        return R.layout.activity_production_paste_group_routing;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_paste_group_routing);

        mContext = this;
        mApp = (NavClientApp) getApplication();
        tvLotNumber = (TextView) findViewById(R.id.tvLastLotNo);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mLastLotNo = extras.getString("LAST_LOT_NO");
            tvLotNumber.setText("Lot No: " + mLastLotNo);
        } else {
            NotificationManager.HideProgressDialog();
        }

        historyList = (ListView) findViewById(R.id.orderList);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(DataManager.getInstance().ProductionOrderNo + " (Routing)");

        initializeVariables();
        initializeHeader();

        Button btnPrintPallet = (Button) findViewById(R.id.btnPrint);
        btnPrintPallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

                Window window = dialog.getWindow();
                window.setGravity(Gravity.CENTER);
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setDimAmount(0.2f);

                dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                dialog.setContentView(R.layout.dialog_label_quantity);
                dialog.setCancelable(false);

                Button btnPrintPallet = (Button) dialog.findViewById(R.id.btnPrint);
                btnPrintPallet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editQuantity = (EditText) dialog.findViewById(R.id.editQuantity);
                        String palletquantity = editQuantity.getText().toString();

                        if (palletquantity.length() != 0 && !palletquantity.equals("0")) {
                            mPrintPurchaseOrderReceiptLotNoPalletTask = new PrintPurchaseOrderReceiptLotNoPalletTask((Activity) mContext, palletquantity);
                            mPrintPurchaseOrderReceiptLotNoPalletTask.execute((Void) null);
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
        });


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
                                    if (isStarted) {
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
                    final String[] splitString = scannedString.split("\\|");

                    //--------------------------------------------------------------------------------------------------------------------
                    //                                              START HERE
                    //--------------------------------------------------------------------------------------------------------------------
                    temp_list = new ArrayList<PasteGroupProductionOrderRoutingListResultData>();

                    if (mPasteGroupProductionOrderRoutingListResult.getPasteGroupProductionOrderRoutingListResultList() != null) {
                        temp_list.addAll(mPasteGroupProductionOrderRoutingListResult.getPasteGroupProductionOrderRoutingListResultList());
                    }

                    boolean isLastRoute = false;
                    boolean isItemTrackingRequired = false;
                    String mOperationNo = "";
                    String RoutingNo = "";
                    String RoutingRefNo = "";
                    String Descrp = "";

                    Float prevOutputQty = 0f;
                    Float prevScrapQty = 0f;

                    if (splitString.length == 4) {
                        if (splitString[0].equals(mProductionOrderNo)) {
                            if (splitString[1].equals(mLineNo)) {
                                for (int index = 0; index < temp_list.size(); index++) {

                                    if (temp_list.get(index).getOperationNo().contains(splitString[3])) {
                                        RoutingNo = temp_list.get(index).getRoutingNo();
                                        isLastRoute = temp_list.get(index).isLastRoute();
                                        isItemTrackingRequired = temp_list.get(index).isItemTrackingRequired();
                                        mOperationNo = temp_list.get(index).getOperationNo();
                                        Descrp = temp_list.get(index).getDescription();

                                        break;
                                    }
                                }
                                if (!isLastRoute) {

                                    final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

                                    // Setting dialogview
                                    Window window = dialog.getWindow();
                                    window.setGravity(Gravity.CENTER);
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                    window.setDimAmount(0.2f);

                                    dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                                    dialog.setContentView(R.layout.dialog_routing_output_scrap_qty_update);
                                    dialog.setCancelable(false);

                                    TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
                                    //title.setText("Please Enter Quantity (Rout.No:" + RoutingNo + ") " + "(OperationNo:" + mOperationNo + ")");
                                    //title.setText("Please Enter Quantity");
                                    title.setText("Please Enter Quantity" + "\n" + Descrp + "(" + mOperationNo + ")");

                                    Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

                                    btnSave.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            EditText editOutputQuantity = (EditText) dialog.findViewById(R.id.editOutputQuantity);
                                            String quantity = editOutputQuantity.getText().toString();
                                            EditText editScrapQuantity = (EditText) dialog.findViewById(R.id.editScrapQuantity);

                                            if (quantity.length() != 0 && editScrapQuantity.getText().toString().length() != 0) {
                                                Float outputQuantity = 0.0f;
                                                Float scrapQuantity = 0.0f;

                                                outputQuantity = Float.parseFloat(quantity);
                                                scrapQuantity = Float.parseFloat(editScrapQuantity.getText().toString());

                                                updateQuantity(splitString[3], outputQuantity, scrapQuantity);
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
                                } else {

                                    for (int index = 0; index < temp_list.size(); index++) {
                                        if (temp_list.get(index).getOperationNo().contains(splitString[3])) {
                                            RoutingNo = temp_list.get(index).getRoutingNo();
                                            isLastRoute = temp_list.get(index).isLastRoute();
                                            isItemTrackingRequired = temp_list.get(index).isItemTrackingRequired();
                                            mOperationNo = temp_list.get(index).getOperationNo();
                                            Descrp = temp_list.get(index).getDescription();

                                            break;
                                        }
                                    }

                                    if (isItemTrackingRequired) {

                                        Intent intent = new Intent(mContext, ProductionPasteGroupRoutingLotEntryActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra("OPERATION_NO", mOperationNo);
                                        intent.putExtra("ITEM_DESCRIPTION", Descrp);
                                        intent.putExtra("IS_SCANNING", "YES");
                                        intent.putExtra("SCAN_CONTENT", scannedString);
                                        startActivity(intent);
                                    } else {

                                        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

                                        Window window = dialog.getWindow();
                                        window.setGravity(Gravity.CENTER);
                                        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                                        window.setDimAmount(0.2f);

                                        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                                        dialog.setContentView(R.layout.dialog_routing_output_scrap_qty_update);
                                        dialog.setCancelable(false);

                                        TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
                                        title.setText("Please Enter Quantity" + "\n" + Descrp + "(" + mOperationNo + ")");

                                        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
                                        btnSave.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                EditText editOutputQuantity = (EditText) dialog.findViewById(R.id.editOutputQuantity);
                                                String quantity = editOutputQuantity.getText().toString();
                                                EditText editScrapQuantity = (EditText) dialog.findViewById(R.id.editScrapQuantity);

                                                if (quantity.length() != 0 && editScrapQuantity.getText().toString().length() != 0) {
                                                    Float outputQuantity = 0.0f;
                                                    Float scrapQuantity = 0.0f;

                                                    outputQuantity = Float.parseFloat(quantity);
                                                    scrapQuantity = Float.parseFloat(editScrapQuantity.getText().toString());

                                                    updateQuantity(splitString[3], outputQuantity, scrapQuantity);
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
                            } else {
                                editable.clear();
                                scanText.requestFocus();
                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                            }
                        } else {
                            editable.clear();
                            scanText.requestFocus();
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                        }

                    } else {
                        editable.clear();
                        scanText.requestFocus();
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
                    }
                    editable.clear();
                    scanText.requestFocus();
                    //--------------------------------------------------------------------------------------------------------------------
                    //                                              END HERE
                    //--------------------------------------------------------------------------------------------------------------------
                }
            }
        });
        mGetProductionRoutingListTask = new GetProductionRoutingListTask((Activity) mContext);
        mGetProductionRoutingListTask.execute((Void) null);
    }

    private void initializeVariables() {
        lblProductionOrderNo = (TextView) findViewById(R.id.lblProductionOrderNo);
        lblDueDate = (TextView) findViewById(R.id.lblDueDate);
        lblItemDescription = (TextView) findViewById(R.id.lblItemDescription);
        lblItemIDUom = (TextView) findViewById(R.id.lblItemIDUom);
        lblPurchaseQuantity = (TextView) findViewById(R.id.lblPurchaseQuantity);
        lblFinishedQuantity = (TextView) findViewById(R.id.lblFinishedQuantity);
        lblRemainingQuantity = (TextView) findViewById(R.id.lblRemainingQuantity);

        mProductionOrderNo = DataManager.getInstance().ProductionOrderNo;
        mLineNo = DataManager.getInstance().pasteGroupProductionOrderListResultData.getLineNo();
        mItemNo = DataManager.getInstance().pasteGroupProductionOrderListResultData.getItemNo();
        mPurchaseQtyBase = Float.parseFloat(DataManager.getInstance().pasteGroupProductionOrderListResultData.getOrderQuantityBase());
        mFinishedQtyBase = Float.parseFloat(DataManager.getInstance().pasteGroupProductionOrderListResultData.getFinishedQuantityBase());
        mRemainingQtyBase = Float.parseFloat(DataManager.getInstance().pasteGroupProductionOrderListResultData.getRemainingQuantityBase());
    }

    private void initializeHeader() {
        lblProductionOrderNo.setText(DataManager.getInstance().pasteGroupProductionOrderListResultData.getProductionOrderNo());
        String[] dateSplit = DataManager.getInstance().pasteGroupProductionOrderListResultData.getProductionDate().split("T")[0].split("-");

        if (!DataManager.getInstance().pasteGroupProductionOrderListResultData.getProductionDate().contains("0001-")) {
            dateSplit = DataManager.getInstance().pasteGroupProductionOrderListResultData.getProductionDate().split("T")[0].split("-");
            lblDueDate.setText(dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
        } else {
            lblDueDate.setText(" ");
        }

        lblItemDescription.setText(DataManager.getInstance().pasteGroupProductionOrderListResultData.getItemDescription());

        lblItemIDUom.setText(DataManager.getInstance().pasteGroupProductionOrderListResultData.getItemNo() + " / " + DataManager.getInstance().pasteGroupProductionOrderListResultData.getBaseUom());
        lblPurchaseQuantity.setText("Ord. Qty: " + DataManager.getInstance().pasteGroupProductionOrderListResultData.getOrderQuantity());
        lblFinishedQuantity.setText("Fin. Qty: " + DataManager.getInstance().pasteGroupProductionOrderListResultData.getFinishedQuantity());
        lblRemainingQuantity.setText("Rem. Qty: " + DataManager.getInstance().pasteGroupProductionOrderListResultData.getRemainingQuantity());
    }

    /*private boolean checkItemExist(String b_itemNo)
    {
        if( mTrasferShipmentResult!= null && mTrasferShipmentResult.TransferShipmentData.getLinesDataList() != null)
        {
            for(int i = 0; i < mTrasferShipmentResult.TransferShipmentData.getLinesDataList().size(); i++)
            {
                if(mTrasferShipmentResult.TransferShipmentData.getLinesDataList().get(i).getItemNo().contains(b_itemNo))
                {
                    DataManager.getInstance().setTransitionTransferOutData(mTrasferShipmentResult.TransferShipmentData.getLinesDataList().get(i));
                    return true;
                }
            }
        }

        return false;
    }*/

    public class GetProductionRoutingListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ProductionOrderRoutingListSearchParameter mPasteGroupProductionOrderRoutingListSearchParameter;

        GetProductionRoutingListTask(Activity activity) {
            //NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mPasteGroupProductionOrderRoutingListSearchParameter = new ProductionOrderRoutingListSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mProductionOrderNo, mLineNo, mItemNo, "", mRoutingRefNo, mPurchaseQtyBase, mFinishedQtyBase, mRemainingQtyBase);

            Gson gson = new Gson();
            String json = gson.toJson(mPasteGroupProductionOrderRoutingListSearchParameter);
            Log.d("PRODUCTION ROUTING", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PasteGroupProductionOrderRoutingListResult> call = mApp.getNavBrokerService().GetProductionOrderRoute(mPasteGroupProductionOrderRoutingListSearchParameter);

                mPasteGroupProductionOrderRoutingListResult = call.execute().body();

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
                    if (mPasteGroupProductionOrderRoutingListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        Gson gson = new Gson();
                        String json = gson.toJson(mPasteGroupProductionOrderRoutingListResult);
                        Log.d("PRODUCTION GET RETURN", json);

                        historyList.setAdapter(null);

                        temp_list = new ArrayList<PasteGroupProductionOrderRoutingListResultData>();

                        if (mPasteGroupProductionOrderRoutingListResult.getPasteGroupProductionOrderRoutingListResultList() != null) {
                            temp_list.addAll(mPasteGroupProductionOrderRoutingListResult.getPasteGroupProductionOrderRoutingListResultList());
                        }

                        historyAdapter = new ProductionPasteGroupRoutingListArrayAdapter(mContext, temp_list);

                        historyList.setAdapter(historyAdapter);

                    /*historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                        }
                    });*/

                    } else {
                        //NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mPasteGroupProductionOrderRoutingListResult.getMessage());
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

    public class PasteGroupProductionRoutingStartRouteTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ProductionOrderRoutingStartRouteParameter mProductionOrderRoutingStartRouteParameter;

        PasteGroupProductionRoutingStartRouteTask(Activity activity, String operationNo) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_PROCESSING, NotificationManager.DIALOG_MSG_PROCESSING);
            mActivity = activity;

            mProductionOrderRoutingStartRouteParameter = new ProductionOrderRoutingStartRouteParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mProductionOrderNo, mLineNo, mItemNo, operationNo);

            Gson gson = new Gson();
            String json = gson.toJson(mProductionOrderRoutingStartRouteParameter);
            Log.d("PRODUCTION", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().ProductionOrderStartRoute(mProductionOrderRoutingStartRouteParameter);

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
                    if (mBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        Toast.makeText(mContext, "Process has started", Toast.LENGTH_SHORT).show();

                        mGetProductionRoutingListTask = new GetProductionRoutingListTask((Activity) mContext);
                        mGetProductionRoutingListTask.execute((Void) null);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mBaseResult.getMessage());
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

    public class PasteGroupProductionRoutingEndRouteTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ProductionOrderRoutingEndRouteParameter mProductionOrderRoutingEndRouteParameter;

        PasteGroupProductionRoutingEndRouteTask(Activity activity, String operationNo, Float outputQuantity, Float scrapQuantity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_PROCESSING, NotificationManager.DIALOG_MSG_PROCESSING);
            mActivity = activity;

            mProductionOrderRoutingEndRouteParameter = new ProductionOrderRoutingEndRouteParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mProductionOrderNo, mLineNo, mItemNo, operationNo, outputQuantity, scrapQuantity);

            Gson gson = new Gson();
            String json = gson.toJson(mProductionOrderRoutingEndRouteParameter);
            Log.d("PRODUCTION", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().ProductionOrderEndRoute(mProductionOrderRoutingEndRouteParameter);

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
                    if (mBaseResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        Toast.makeText(mContext, "Process has ended", Toast.LENGTH_SHORT).show();

                        mGetProductionRoutingListTask = new GetProductionRoutingListTask((Activity) mContext);
                        mGetProductionRoutingListTask.execute((Void) null);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mBaseResult.getMessage());
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

    public class PostUpdateItemQuantityTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        UpdateProductionOrderLineParameter mUpdateProductionOrderLineParameter;
        BaseResult mReturnBaseResult;

        PostUpdateItemQuantityTask(Activity activity, String productionOrderNo, String operationNo, Float outputQuantity, Float scrapQuantity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mUpdateProductionOrderLineParameter = new UpdateProductionOrderLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), productionOrderNo, mLineNo, operationNo, outputQuantity, scrapQuantity);

            Gson gson = new Gson();
            String json = gson.toJson(mUpdateProductionOrderLineParameter);
            Log.d("PRODUCTION ORDER", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().UpdateProductionOrderRouteLine(mUpdateProductionOrderLineParameter);

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

    public class PrintPurchaseOrderReceiptLotNoPalletTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        PasteGoodsProductionOrderLinePrintParameter mFinishedGoodsProductionOrderLinePrintPalletParameter;
        BaseResult mReturnBaseResult;

        PrintPurchaseOrderReceiptLotNoPalletTask(Activity activity, String Quantity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_PRINT, NotificationManager.DIALOG_MSG_PRINT);

            mActivity = activity;

            mFinishedGoodsProductionOrderLinePrintPalletParameter = new PasteGoodsProductionOrderLinePrintParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(),
                    mProductionOrderNo, mLineNo, mItemNo, "", Integer.parseInt(Quantity));

            Gson gson = new Gson();
            String json = gson.toJson(mFinishedGoodsProductionOrderLinePrintPalletParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().ProductionOrderLinePrintLabel(mFinishedGoodsProductionOrderLinePrintPalletParameter);

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

                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_INFORMATION, NotificationManager.MSG_PRINT_REQUEST_SENT);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mReturnBaseResult.getMessage());
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

    public void startRoute(String operationNo) {
        mPasteGroupProductionRoutingStartRouteTask = new PasteGroupProductionRoutingStartRouteTask((Activity) mContext, operationNo);
        mPasteGroupProductionRoutingStartRouteTask.execute((Void) null);
    }

    public void endRoute(String operationNo, Float outputQuantity, Float scrapQuantity) {
        mPasteGroupProductionRoutingEndRouteTask = new PasteGroupProductionRoutingEndRouteTask((Activity) mContext, operationNo, outputQuantity, scrapQuantity);
        mPasteGroupProductionRoutingEndRouteTask.execute((Void) null);
    }

    public void updateQuantity(String operationNo, float outputQuantity, float scrapQuantity) {
        mPostUpdateItemQuantityTask = new PostUpdateItemQuantityTask((Activity) mContext, mProductionOrderNo, operationNo, outputQuantity, scrapQuantity);
        mPostUpdateItemQuantityTask.execute((Void) null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.header_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchDialog();
                return true;
            case R.id.action_clearFilter:
                historyAdapter.clearFilter();
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
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_search_routing);
        dialog.setCancelable(false);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterRoutingNo);
                String routing_no = temp_filterParam.getText().toString();

                temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterOperationNo);
                String operation_no = temp_filterParam.getText().toString();

                if (routing_no.isEmpty() || operation_no.isEmpty()) {
                    NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_routing_missing_fields), getResources().getString(R.string.notification_msg_routing_missing_fields));
                } else {
                    historyAdapter.setFilterParam(routing_no, operation_no);
                    dialog.dismiss();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), ProductionPasteGroupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
