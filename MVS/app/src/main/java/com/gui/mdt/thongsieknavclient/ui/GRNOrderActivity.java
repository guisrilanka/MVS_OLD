package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.DataConverter;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.grn.PurchaseOrderReceiptHeaderArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptListItem;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptListResult;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptListSearchParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;

public class GRNOrderActivity extends BaseActivity {

    private Context mContext;
    private GetPurchaseOrderReceiptListTask mGetPurchaseOrderReceiptListTask = null;
    private GetFilterPurchaseOrderReceiptListTaskResult mGetFilterPurchaseOrderReceiptListTaskResult = null;

    ListView historyList;
    PurchaseOrderReceiptHeaderArrayAdapter historyAdapter;
    Calendar calFilterDate;
    private String mPurchaseOrderReceiptNo;
    private NavClientApp mApp;
    private String mFilterVendor;
    private String mFilterPurchaseNo;
    private String mFilterDate;

    protected int getLayoutResource() {
        return R.layout.activity_grn;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        calFilterDate = Calendar.getInstance();
        mApp = (NavClientApp) getApplication();

        historyList = (ListView) findViewById(R.id.orderList);
        ClearSearchParams();

        mFilterVendor = "";
        mFilterPurchaseNo = "";
        mFilterDate = "";
        mGetPurchaseOrderReceiptListTask = new GetPurchaseOrderReceiptListTask(this, mFilterVendor, mFilterPurchaseNo, mFilterDate);
        mGetPurchaseOrderReceiptListTask.execute((Void) null);

    }

    public class GetPurchaseOrderReceiptListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        PurchaseOrderReceiptListResult mPurchaseOrderReceiptListResult;
        PurchaseOrderReceiptListSearchParameter mPurchaseOrderReceiptListSearchParameter;

        GetPurchaseOrderReceiptListTask(Activity activity, String filterVendorName, String filterPurchaseOrderNo, String filterPurchaseOrderDate) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mPurchaseOrderReceiptListSearchParameter = new PurchaseOrderReceiptListSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), filterVendorName, filterPurchaseOrderNo, filterPurchaseOrderDate);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PurchaseOrderReceiptListResult> call = mApp.getNavBrokerService().GetPurchaseOrderReceiptList(mPurchaseOrderReceiptListSearchParameter);

                mPurchaseOrderReceiptListResult = call.execute().body();

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
                    if (mPurchaseOrderReceiptListResult != null) {

                        if (mPurchaseOrderReceiptListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                            historyList.setAdapter(null);

                            ArrayList<PurchaseOrderReceiptListItem> temp_list = new ArrayList<PurchaseOrderReceiptListItem>();

                            if (mPurchaseOrderReceiptListResult.getPurchaseOrderReceiptList() != null) {
                                temp_list.addAll(mPurchaseOrderReceiptListResult.getPurchaseOrderReceiptList());
                            }

                            if (temp_list.size() == 0) {
                                TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                                lblNoData.setVisibility(View.VISIBLE);
                            } else {
                                TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                                lblNoData.setVisibility(View.INVISIBLE);
                            }

                            historyAdapter = new PurchaseOrderReceiptHeaderArrayAdapter(mContext, temp_list);

                            historyList.setAdapter(historyAdapter);
                            historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    TextView purchaseText = (TextView) view.findViewById(R.id.lblPurchaseRecNo);

                                    mPurchaseOrderReceiptNo = purchaseText.getText().toString();

                                    Intent intent = new Intent(getBaseContext(), GRNOrderLineActivity.class);
                                    intent.putExtra("PURCHASE_ORDER_RECEIPT_NO", mPurchaseOrderReceiptNo.split(": ")[1]);
                                    startActivity(intent);

                                }
                            });

                            ClearSearchParams();
                        } else {
                            NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_error), mPurchaseOrderReceiptListResult.getMessage());
                        }

                    } else {
                        TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                        lblNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_error), getResources().getString(R.string.notification_msg_server_no_response));
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class GetFilterPurchaseOrderReceiptListTaskResult extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        PurchaseOrderReceiptListResult mPurchaseOrderReceiptListResult;
        PurchaseOrderReceiptListSearchParameter mPurchaseOrderReceiptListSearchParameter;

        GetFilterPurchaseOrderReceiptListTaskResult(Activity activity, String filterVendorName, String filterPurchaseOrderNo, String filterPurchaseOrderDate) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mPurchaseOrderReceiptListSearchParameter = new PurchaseOrderReceiptListSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), filterVendorName, filterPurchaseOrderNo, filterPurchaseOrderDate);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<PurchaseOrderReceiptListResult> call = mApp.getNavBrokerService().GetPurchaseOrderReceiptList(mPurchaseOrderReceiptListSearchParameter);

                mPurchaseOrderReceiptListResult = call.execute().body();

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
                    if (mPurchaseOrderReceiptListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        historyList.setAdapter(null);

                        ArrayList<PurchaseOrderReceiptListItem> temp_list = new ArrayList<PurchaseOrderReceiptListItem>();

                        if (mPurchaseOrderReceiptListResult.getPurchaseOrderReceiptList() != null) {
                            temp_list.addAll(mPurchaseOrderReceiptListResult.getPurchaseOrderReceiptList());
                        }

                        if (temp_list.size() == 0) {
                            TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                            lblNoData.setVisibility(View.VISIBLE);
                        } else {
                            TextView lblNoData = (TextView) findViewById(R.id.lblNoData);
                            lblNoData.setVisibility(View.INVISIBLE);
                        }

                        historyAdapter = new PurchaseOrderReceiptHeaderArrayAdapter(mContext, temp_list);

                        historyList.setAdapter(historyAdapter);
                        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                TextView purchaseText = (TextView) view.findViewById(R.id.lblPurchaseRecNo);

                                mPurchaseOrderReceiptNo = purchaseText.getText().toString();

                                Intent intent = new Intent(getBaseContext(), GRNOrderLineActivity.class);
                                intent.putExtra("PURCHASE_ORDER_RECEIPT_NO", mPurchaseOrderReceiptNo.split(": ")[1]);
                                startActivity(intent);

                            }
                        });

                        ClearSearchParams();
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_error), mPurchaseOrderReceiptListResult.getMessage());
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_error), getResources().getString(R.string.notification_msg_server_no_response));
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    void ClearSearchParams() {
        mFilterVendor = "";
        mFilterPurchaseNo = "";
        mFilterDate = "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grn_header_menu, menu);
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

    private void showSearchDialog() {

        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_search_grn);
        dialog.setCancelable(false);

        final EditText textDate = (EditText) dialog.findViewById(R.id.editFilterDate);
        textDate.setInputType(InputType.TYPE_NULL);

        final DatePickerDialog.OnDateSetListener dateOfBirthPicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calFilterDate.set(Calendar.YEAR, year);
                calFilterDate.set(Calendar.MONTH, monthOfYear);
                calFilterDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                textDate.setText(DataConverter.ConvertCalendarToDayMonthYear(calFilterDate.getTime()));
            }
        };

        textDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    new DatePickerDialog(mContext, dateOfBirthPicker, calFilterDate.get(Calendar.YEAR), calFilterDate.get(Calendar.MONTH), calFilterDate.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(mContext, dateOfBirthPicker, calFilterDate.get(Calendar.YEAR), calFilterDate.get(Calendar.MONTH), calFilterDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterVendorName);
                mFilterVendor = temp_filterParam.getText().toString();

                temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterPurchaseNo);
                mFilterPurchaseNo = temp_filterParam.getText().toString();

                temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterDate);
                mFilterDate = DataConverter.ConvertDateToYearMonthDay(temp_filterParam.getText().toString());

                mGetFilterPurchaseOrderReceiptListTaskResult = new GetFilterPurchaseOrderReceiptListTaskResult((Activity) mContext, mFilterVendor, mFilterPurchaseNo, mFilterDate);
                mGetFilterPurchaseOrderReceiptListTaskResult.execute((Void) null);
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
