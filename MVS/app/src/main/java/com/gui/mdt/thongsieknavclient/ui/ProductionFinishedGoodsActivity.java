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
import com.gui.mdt.thongsieknavclient.adapters.productionfinishedgoods.ProductionFinishedGoodsLineListArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.FinishedGoodsProductionOrderListResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.FinishedGoodsProductionOrderListResultData;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.UpdateProductionOrderLineParameter;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class ProductionFinishedGoodsActivity extends BaseActivity {

    private Context mContext;
    private GetProductionOrderListTask mGetProductionOrderListTask = null;
    private PostUpdateItemQuantityTask mPostUpdateItemQuantityTask = null;

    ListView historyList;
    ProductionFinishedGoodsLineListArrayAdapter historyAdapter;
    private String mProductionNo;
    private NavClientApp mApp;
    private String mFilterProductionNo;
    private String mFilterLineNo;
    private String mFilterItemNo;
    private boolean isStarted;

    protected int getLayoutResource() {
        return R.layout.activity_production_finished_goods;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_finished_goods);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        mApp = (NavClientApp) getApplication();

        historyList = (ListView) findViewById(R.id.orderList);
        ClearSearchParams();

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

                        mFilterProductionNo = splitString[0];
                        mFilterLineNo = splitString[1];

                        /*mGetProductionOrderListTask = new GetProductionOrderListTask((Activity) mContext);
                        mGetProductionOrderListTask.execute((Void) null);

                        scanText.setText("");
                        scanText.requestFocus();    // clear and focus for next scan*/

                        //--------------------------------------------------------------------------------------------------------------------
                        //                                              START HERE
                        //--------------------------------------------------------------------------------------------------------------------
                        /*if(splitString.length == 2){
                            mGetProductionOrderListTask = new GetProductionOrderListTask((Activity) mContext);
                            mGetProductionOrderListTask.execute((Void) null);
                        }else
                        {
                            scanText.setText("");
                            scanText.requestFocus();
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
                        }*/
                        //--------------------------------------------------------------------------------------------------------------------
                        //                                              END HERE
                        //--------------------------------------------------------------------------------------------------------------------
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

                    mFilterProductionNo = splitString[0];
                    mFilterLineNo = splitString[1];

                    /*mGetProductionOrderListTask = new GetProductionOrderListTask((Activity) mContext);
                    mGetProductionOrderListTask.execute((Void) null);

                    editable.clear();
                    scanText.requestFocus();*/

                    //--------------------------------------------------------------------------------------------------------------------
                    //                                              START HERE
                    //--------------------------------------------------------------------------------------------------------------------
                    if (splitString.length == 2) {
                        mGetProductionOrderListTask = new GetProductionOrderListTask((Activity) mContext);
                        mGetProductionOrderListTask.execute((Void) null);
                        editable.clear();
                        scanText.requestFocus();
                    } else {
                        editable.clear();
                        scanText.requestFocus();
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.ALERT_TITLE_INVALID_BARCODE, NotificationManager.ALERT_MSG_INVALID_BARCODE);
                    }
                    //--------------------------------------------------------------------------------------------------------------------
                    //                                              END HERE
                    //--------------------------------------------------------------------------------------------------------------------
                }
            }
        });

        mGetProductionOrderListTask = new GetProductionOrderListTask(this);
        mGetProductionOrderListTask.execute((Void) null);
    }

    public class GetProductionOrderListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        FinishedGoodsProductionOrderListResult mFinishedGoodsProductionOrderListResult;
        ProductionOrderListSearchParameter mProductionOrderListSearchParameter;

        GetProductionOrderListTask(Activity activity) {
            //NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mProductionOrderListSearchParameter = new ProductionOrderListSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mFilterProductionNo, mFilterLineNo, mFilterItemNo);

            Gson gson = new Gson();
            String json = gson.toJson(mProductionOrderListSearchParameter);
            Log.d("PRODUCTION", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<FinishedGoodsProductionOrderListResult> call = mApp.getNavBrokerService().GetFinishedGoodsProductionOrderList(mProductionOrderListSearchParameter);

                mFinishedGoodsProductionOrderListResult = call.execute().body();

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
                    if (mFinishedGoodsProductionOrderListResult != null) {

                        if (mFinishedGoodsProductionOrderListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                            Gson gson = new Gson();
                            String json = gson.toJson(mFinishedGoodsProductionOrderListResult);
                            Log.d("PRODUCTION FINISHED FIRST LAYER RETURN", json);

                            historyList.setAdapter(null);

                            ArrayList<FinishedGoodsProductionOrderListResultData> temp_list = new ArrayList<FinishedGoodsProductionOrderListResultData>();

                            if (mFinishedGoodsProductionOrderListResult.getProductionOrderListResultData() != null) {
                                temp_list.addAll(mFinishedGoodsProductionOrderListResult.getProductionOrderListResultData());
                            }

                            if (mFinishedGoodsProductionOrderListResult.getProductionOrderListResultData() == null || mFinishedGoodsProductionOrderListResult.getProductionOrderListResultData().size() == 0) {
                                TextView temp_view = (TextView) findViewById(R.id.lblNoData);
                                temp_view.setVisibility(View.VISIBLE);
                            } else {
                                TextView temp_view = (TextView) findViewById(R.id.lblNoData);
                                temp_view.setVisibility(View.INVISIBLE);
                            }

                            historyAdapter = new ProductionFinishedGoodsLineListArrayAdapter(mContext, temp_list);

                            historyList.setAdapter(historyAdapter);

                            DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_PRODUCTION_FINISHED_GOODS);
                            DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_PRODUCTION_FINISHED_GOODS_ROUTING);
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mFinishedGoodsProductionOrderListResult.getMessage());
                        }
                    } else {
                        TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                        lblNoData.setVisibility(View.VISIBLE);
                    }

                    ClearSearchParams();
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

        PostUpdateItemQuantityTask(Activity activity, String productionOrderNo, String lineNo, Float outputQuantity, Float scrapQuantity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_SAVE_DATA, NotificationManager.DIALOG_MSG_SAVE);

            mActivity = activity;

            mUpdateProductionOrderLineParameter = new UpdateProductionOrderLineParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), productionOrderNo, lineNo, "", outputQuantity, scrapQuantity);

            Gson gson = new Gson();
            String json = gson.toJson(mUpdateProductionOrderLineParameter);
            Log.d("PRODUCTION ORDER", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<BaseResult> call = mApp.getNavBrokerService().UpdateFinishedGoodsProductionOrderLine(mUpdateProductionOrderLineParameter);

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

    public void updateQuantity(String productionOrderNo, String lineNo, float outputQuantity, float scrapQuantity) {
        mPostUpdateItemQuantityTask = new PostUpdateItemQuantityTask((Activity) mContext, productionOrderNo, lineNo, outputQuantity, scrapQuantity);
        mPostUpdateItemQuantityTask.execute((Void) null);
    }

    void ClearSearchParams() {
        mFilterProductionNo = "";
        mFilterLineNo = "";
        mFilterItemNo = "";
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
                ClearSearchParams();
                mGetProductionOrderListTask = new GetProductionOrderListTask(this);
                mGetProductionOrderListTask.execute((Void) null);
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
        dialog.setContentView(R.layout.dialog_search_production);
        dialog.setCancelable(false);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterProductionNo);
                mFilterProductionNo = temp_filterParam.getText().toString();

                temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterLineNo);
                mFilterLineNo = temp_filterParam.getText().toString();

                temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterItemNo);
                mFilterItemNo = temp_filterParam.getText().toString();

                Log.d("DEBUGGING", mFilterProductionNo + " " + mFilterLineNo + " " + mFilterItemNo);
                mGetProductionOrderListTask = new GetProductionOrderListTask((Activity) mContext);
                mGetProductionOrderListTask.execute((Void) null);
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
        Intent intent = new Intent(getBaseContext(), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
