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
import com.gui.mdt.thongsieknavclient.adapters.productionfinishedgoods.ProductionFinishedGoodsBomListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderBomListResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderBomListResultData;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderBomListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.UpdateProductionOrderBomParameter;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class ProductionFinishedGoodsBOMActivity extends BaseActivity {

    GetProductionOrderBOMListTask mGetProductionOrderBOMListTask = null;
    PostUpdateItemQuantityTask mPostUpdateItemQuantityTask = null;
    PasteGroupProductionOrderBomListResult mPasteGroupProductionOrderBomListResult;

    ListView historyList;
    ProductionFinishedGoodsBomListArrayAdapter historyAdapter;
    ArrayList<ProductionOrderBomListResultData> temp_list;
    private TextView lblProductionOrderNo;
    private TextView lblDueDate;
    private TextView lblItemDescription;
    private TextView lblItemIDUom;
    private TextView lblPurchaseQuantity;
    private TextView lblFinishedQuantity;
    private TextView lblRemainingQuantity;
    private Context mContext;
    private NavClientApp mApp;
    private String mProductionOrderNo;
    private String mProductionLineNo;
    private String mLineNo;
    private String mItemNo;
    private float mQtyPer;
    private boolean isStarted = false;

    protected int getLayoutResource() {
        return R.layout.activity_production_finished_goods_bom;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_finished_goods_bom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(DataManager.getInstance().ProductionOrderNo + " (BOM)");
        setSupportActionBar(toolbar);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        historyList = (ListView) findViewById(R.id.orderList);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            mProductionOrderNo = DataManager.getInstance().ProductionOrderNo;
            mProductionLineNo = DataManager.getInstance().finishedGoodsProductionOrderListResultData.getLineNo();
            mItemNo = DataManager.getInstance().finishedGoodsProductionOrderListResultData.getItemNo();
            mQtyPer = DataManager.getInstance().finishedGoodsProductionOrderListResultData.getQuantityPer();

            mGetProductionOrderBOMListTask = new GetProductionOrderBOMListTask(this);
            mGetProductionOrderBOMListTask.execute((Void) null);
        } else {
            NotificationManager.HideProgressDialog();
        }

        initializeVariables();
        initializeHeader();

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

                        //--------------------------------------------------------------------------------------------------------------------
                        //                                              START HERE
                        //--------------------------------------------------------------------------------------------------------------------
                        final ArrayList<ProductionOrderBomListResultData> temp_list = new ArrayList<ProductionOrderBomListResultData>();
                        if (mPasteGroupProductionOrderBomListResult.getProductionOrderBomResultList() != null) {
                            temp_list.addAll(mPasteGroupProductionOrderBomListResult.getProductionOrderBomResultList());
                        }
                        TextView lblItemIDUom = (TextView) findViewById(R.id.lblItemIDUom);
                        mItemNo = lblItemIDUom.getText().toString().replace("Item No: ", "").split(" / ")[0];
                        mLineNo = "";
                        boolean isItemTrackingRequired = false;
                        Float prevQuantity = 0f;
                        //-----------------------------------------------------------------------------------------------------------------
                        //                                       END HERE
                        //-----------------------------------------------------------------------------------------------------------------
                        //Originally By Qiming
                        /*if (splitString.length == 3) {
                            if (splitString[1].equals(mProductionLineNo))    // check ProductionOrderLineNo here, no need pass in
                            {
                                historyAdapter.setFilterParam(splitString[0], splitString[2]);
                            } else {
                                //NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                            }
                        } else {
                            //NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                        }
                        scanText.setText("");
                        scanText.requestFocus();    // clear and focus for next scan*/

                        //This block comment is  by Qi ming
                        /*if(checkItemExist(splitString[0]))
                        {
                            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

                            DataManager.getInstance().ProductionOrderNo = mProductionOrderNo;
                            Intent intent = new Intent(getBaseContext(), ProductionFinishedGoodsBOMLotEntryActivity.class);
                            intent.putExtra("IS_SCANNING", "YES");
                            intent.putExtra("SCAN_CONTENT", scannedString);
                            startActivity(intent);
                        }
                        else
                        {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                            scanText.setText("");
                            scanText.requestFocus();    // clear and focus for next scan
                        }*/

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

                    //Originally By Qiming
                    /*if (splitString.length == 3) {
                        if (splitString[1].equals(mProductionLineNo))    // check ProductionOrderLineNo here, no need pass in
                        {
                            historyAdapter.setFilterParam(splitString[0], splitString[2]);
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                        }
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                    }
                    scanText.setText("");
                    scanText.requestFocus();    // clear and focus for next scan*/

                    //This block comment code is written by Qi ming
                    /*if(checkItemExist(splitString[0]))
                    {
                        NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);

                        DataManager.getInstance().ProductionOrderNo = mProductionOrderNo;
                        Intent intent = new Intent(getBaseContext(), ProductionFinishedGoodsBOMLotEntryActivity.class);
                        intent.putExtra("IS_SCANNING", "YES");
                        intent.putExtra("SCAN_CONTENT", scannedString);
                        startActivity(intent);
                    }
                    else
                    {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                        scanText.setText("");
                        scanText.requestFocus();    // clear and focus for next scan
                    }*/

                    //--------------------------------------------------------------------------------------------------------------------
                    //                                              START HERE
                    //--------------------------------------------------------------------------------------------------------------------
                    final ArrayList<ProductionOrderBomListResultData> temp_list = new ArrayList<ProductionOrderBomListResultData>();
                    if (mPasteGroupProductionOrderBomListResult.getProductionOrderBomResultList() != null) {
                        temp_list.addAll(mPasteGroupProductionOrderBomListResult.getProductionOrderBomResultList());
                    }
                    TextView lblItemIDUom = (TextView) findViewById(R.id.lblItemIDUom);
                    mItemNo = lblItemIDUom.getText().toString().replace("Item No: ", "").split(" / ")[0];
                    mLineNo = "";
                    boolean isItemTrackingRequired = false;
                    Float prevQuantity = 0f;

                    if (splitString.length == 3 || splitString.length == 6) {

                        if (splitString.length == 6) {
                            for (int index = 0; index < temp_list.size(); index++) {
                                if (temp_list.get(index).getItemNo().contains(splitString[0])) {
                                    Log.d("ITEM FOUND", mLineNo);
                                    mLineNo = temp_list.get(index).getLineNo();
                                    isItemTrackingRequired = temp_list.get(index).isItemTrackingRequired();
                                    prevQuantity = Float.parseFloat(temp_list.get(index).getQuantityToConsumed());

                                    if (isItemTrackingRequired) {
                                        DataManager.getInstance().clearProductionOrderBomData();
                                        DataManager.getInstance().ProductionOrderNo = mProductionOrderNo;
                                        DataManager.getInstance().setProductionOrderBomListResultData(temp_list.get(index));

                                    }
                                    break;
                                }
                            }
                            if (checkItemExist(splitString[0])) {
                                if (isItemTrackingRequired == true) {
                                    Intent intent = new Intent(getBaseContext(), ProductionFinishedGoodsBOMLotEntryActivity.class);
                                    intent.putExtra("IS_SCANNING", "YES");
                                    intent.putExtra("SCAN_CONTENT", scannedString);
                                    startActivity(intent);
                                } else {
                                    scanText.setText("");
                                    scanText.requestFocus();
                                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                                }
                            } else {
                                scanText.setText("");
                                scanText.requestFocus();
                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                            }

                        } else if (splitString.length == 3) {
                            if (splitString[1].equals(mProductionLineNo)) {
                                for (int index = 0; index < temp_list.size(); index++) {
                                    if (temp_list.get(index).getLineNo().contains(splitString[2])) {
                                        Log.d("ITEM FOUND", mLineNo);
                                        mLineNo = temp_list.get(index).getLineNo();
                                        mItemNo = temp_list.get(index).getItemNo();
                                        isItemTrackingRequired = temp_list.get(index).isItemTrackingRequired();
                                        prevQuantity = Float.parseFloat(temp_list.get(index).getQuantityToConsumed());

                                        if (isItemTrackingRequired) {
                                            DataManager.getInstance().clearProductionOrderBomData();
                                            DataManager.getInstance().ProductionOrderNo = mProductionOrderNo;
                                            DataManager.getInstance().setProductionOrderBomListResultData(temp_list.get(index));

                                        }
                                        break;
                                    }
                                }

                                if (checkItemExist(mItemNo)) {
                                    if (!isItemTrackingRequired) {
                                        historyAdapter.setFilterParam(splitString[0], splitString[2]);

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
                                        title.setText("Item No: " + mItemNo);

                                        EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                        editUpdateQuantity.setText(Float.toString(prevQuantity));

                                        final Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

                                        btnSave.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                EditText editUpdateQuantity = (EditText) dialog.findViewById(R.id.editUpdateQuantity);
                                                String quantity = editUpdateQuantity.getText().toString();

                                                if (quantity.length() != 0) {
                                                    mPostUpdateItemQuantityTask = new PostUpdateItemQuantityTask((Activity) mContext, mLineNo, Float.parseFloat(quantity));
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
                                        /*if(quantity.length() != 0)
                                                        {
                                                            dialog.dismiss();
                                                            btnSave.callOnClick();
                                                        }
                                                        */
                                    }
                                } else {
                                    scanText.setText("");
                                    scanText.requestFocus();
                                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                                }

                            } else {
                                scanText.setText("");
                                scanText.requestFocus();
                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                            }

                        } else {
                            scanText.setText("");
                            scanText.requestFocus();
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_TO, NotificationManager.ALERT_MSG_INVALID_TO);
                        }

                        //-----------------------------------------------------------------------------------------------------------------
                        //                                       END HERE
                        //-----------------------------------------------------------------------------------------------------------------

                    } else {
                        scanText.setText("");
                        scanText.requestFocus();
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
                    }
                    mGetProductionOrderBOMListTask = new GetProductionOrderBOMListTask((Activity) mContext);
                    mGetProductionOrderBOMListTask.execute((Void) null);
                }
            }
        });
    }

    private void initializeVariables() {
        lblProductionOrderNo = (TextView) findViewById(R.id.lblProductionOrderNo);
        lblDueDate = (TextView) findViewById(R.id.lblDueDate);
        lblItemDescription = (TextView) findViewById(R.id.lblItemDescription);
        lblItemIDUom = (TextView) findViewById(R.id.lblItemIDUom);
        lblPurchaseQuantity = (TextView) findViewById(R.id.lblPurchaseQuantity);
        lblFinishedQuantity = (TextView) findViewById(R.id.lblFinishedQuantity);
        lblRemainingQuantity = (TextView) findViewById(R.id.lblRemainingQuantity);
    }

    private void initializeHeader() {
        lblProductionOrderNo.setText(DataManager.getInstance().finishedGoodsProductionOrderListResultData.getProductionOrderNo());
        String[] dateSplit = DataManager.getInstance().finishedGoodsProductionOrderListResultData.getProductionDate().split("T")[0].split("-");

        if (!DataManager.getInstance().finishedGoodsProductionOrderListResultData.getProductionDate().contains("0001-")) {
            dateSplit = DataManager.getInstance().finishedGoodsProductionOrderListResultData.getProductionDate().split("T")[0].split("-");
            lblDueDate.setText(dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
        } else {
            lblDueDate.setText(" ");
        }

        lblItemDescription.setText(DataManager.getInstance().finishedGoodsProductionOrderListResultData.getItemDescription());

        lblItemIDUom.setText(DataManager.getInstance().finishedGoodsProductionOrderListResultData.getItemNo() + " / " + DataManager.getInstance().finishedGoodsProductionOrderListResultData.getBaseUom());
        lblPurchaseQuantity.setText("Ord. Qty: " + DataManager.getInstance().finishedGoodsProductionOrderListResultData.getOrderQuantityBase());
        lblFinishedQuantity.setText("Fin. Qty: " + DataManager.getInstance().finishedGoodsProductionOrderListResultData.getFinishedQuantityBase());
        lblRemainingQuantity.setText("Rem. Qty: " + DataManager.getInstance().finishedGoodsProductionOrderListResultData.getRemainingQuantityBase());
    }

    private boolean checkItemExist(String itemNo) {
        if (mPasteGroupProductionOrderBomListResult != null && mPasteGroupProductionOrderBomListResult.getProductionOrderBomResultList() != null) {
            for (int i = 0; i < mPasteGroupProductionOrderBomListResult.getProductionOrderBomResultList().size(); i++) {
                if (mPasteGroupProductionOrderBomListResult.getProductionOrderBomResultList().get(i).getItemNo().contains(itemNo)) {
                    DataManager.getInstance().setProductionOrderBomListResultData(mPasteGroupProductionOrderBomListResult.getProductionOrderBomResultList().get(i));
                    return true;
                }
            }
        }

        return false;
    }

    public class GetProductionOrderBOMListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ProductionOrderBomListSearchParameter mProductionOrderBomListSearchParameter;

        GetProductionOrderBOMListTask(Activity activity) {
            //NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mProductionOrderBomListSearchParameter = new ProductionOrderBomListSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mProductionOrderNo, mProductionLineNo, mItemNo, mQtyPer);

            Gson gson = new Gson();
            String json = gson.toJson(mProductionOrderBomListSearchParameter);
            Log.d("PRODUCTION BOM", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PasteGroupProductionOrderBomListResult> call = mApp.getNavBrokerService().GetPasteGroupProductionOrderBom(mProductionOrderBomListSearchParameter);

                mPasteGroupProductionOrderBomListResult = call.execute().body();

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
                    Gson gson = new Gson();
                    String json = gson.toJson(mPasteGroupProductionOrderBomListResult);
                    Log.d("PRODUCTION BOM", json);

                    if (mPasteGroupProductionOrderBomListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        historyList.setAdapter(null);

                        temp_list = new ArrayList<ProductionOrderBomListResultData>();

                        if (mPasteGroupProductionOrderBomListResult.getProductionOrderBomResultList() != null) {
                            temp_list.addAll(mPasteGroupProductionOrderBomListResult.getProductionOrderBomResultList());
                        }

                        historyAdapter = new ProductionFinishedGoodsBomListArrayAdapter(mContext, temp_list);

                        historyList.setAdapter(historyAdapter);

                        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                        mLineNo = temp_list.get(index).getLineNo();
                                        isItemTrackingRequired = temp_list.get(index).isItemTrackingRequired();
                                        prevQuantity = Float.parseFloat(temp_list.get(index).getQuantityToConsumed());

                                        if (isItemTrackingRequired) {
                                            DataManager.getInstance().clearProductionOrderBomData();
                                            DataManager.getInstance().ProductionOrderNo = mProductionOrderNo;
                                            DataManager.getInstance().setProductionOrderBomListResultData(temp_list.get(i));

                                        }
                                        break;
                                    }
                                }

                                if (isItemTrackingRequired) {
                                    Intent intent = new Intent(getBaseContext(), ProductionFinishedGoodsBOMLotEntryActivity.class);
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
                                                mPostUpdateItemQuantityTask = new PostUpdateItemQuantityTask((Activity) mContext, mLineNo, Float.parseFloat(quantity));
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
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mPasteGroupProductionOrderBomListResult.getMessage());
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
        UpdateProductionOrderBomParameter mUpdateProductionOrderBomParameter;
        BaseResult mReturnBaseResult;

        PostUpdateItemQuantityTask(Activity activity, String lineNo, Float qtyToConsume) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mUpdateProductionOrderBomParameter = new UpdateProductionOrderBomParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mProductionOrderNo, mProductionLineNo, lineNo, qtyToConsume);

            Gson gson = new Gson();
            String json = gson.toJson(mUpdateProductionOrderBomParameter);
            Log.d("PRODUCTION ORDER BOM", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().UpdateProductionOrderBom(mUpdateProductionOrderBomParameter);

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
                        mGetProductionOrderBOMListTask = new GetProductionOrderBOMListTask((Activity) mContext);
                        mGetProductionOrderBOMListTask.execute((Void) null);
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
        dialog.setContentView(R.layout.dialog_search_line_no);
        dialog.setCancelable(false);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterLineNo);
                String temp_line_no = temp_filterParam.getText().toString();

                if (!temp_line_no.isEmpty()) {
                    historyAdapter.setFilterParam("", temp_line_no);
                } else {
                    historyAdapter.clearFilter();
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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), ProductionFinishedGoodsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
