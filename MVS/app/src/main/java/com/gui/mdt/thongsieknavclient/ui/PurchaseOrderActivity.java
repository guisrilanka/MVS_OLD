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

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.ConfigurationManager;
import com.gui.mdt.thongsieknavclient.DataConverter;
import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.purchaseorder.PurchaseOrderHeaderArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderListItem;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderListResult;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.UpdatePurchaseOrderVendorshipmentNoParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;

public class PurchaseOrderActivity extends BaseActivity {

    private Context mContext;
    private GetPurchaseOrderListTask mGetPurchaseOrderListTask = null;
    private GetFilterPurchaseOrderListTaskResult mGetFilterPurchaseOrderListTaskResult = null;
    private UpdateVendorShipmentNoTask mUpdateVendorShipmentNoTask = null;

    ListView historyList;
    PurchaseOrderHeaderArrayAdapter historyAdapter;
    private String mPurchaseOrderNo;
    private String mVendorShiptNo;
    private NavClientApp mApp;
    Calendar calFilterDate;
    private String mFilterVendor;
    private String mFilterPurchaseNo;
    private String mFilterDate;

    protected int getLayoutResource() {
        return R.layout.activity_purchase_order;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        calFilterDate = Calendar.getInstance();
        mApp = (NavClientApp) getApplication();

        historyList = (ListView) findViewById(R.id.orderList);
        ClearSearchParams();

        mGetPurchaseOrderListTask = new GetPurchaseOrderListTask(this);
        mGetPurchaseOrderListTask.execute((Void) null);

    }

    public class GetPurchaseOrderListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        PurchaseOrderListResult mPurchaseOrderListResult;
        PurchaseOrderListSearchParameter mPurchaseOrderListSearchParameter;

        GetPurchaseOrderListTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mPurchaseOrderListSearchParameter = new PurchaseOrderListSearchParameter(mApp.getmUserCompany(), mFilterVendor, mFilterPurchaseNo, mFilterDate, mApp.getCurrentUserName(), mApp.getCurrentUserPassword());

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

                        if(mPurchaseOrderListResult.getPurchaseOrderList().size() != 0) {

                            if (mPurchaseOrderListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                                historyList.setAdapter(null);

                                ArrayList<PurchaseOrderListItem> temp_list = new ArrayList<PurchaseOrderListItem>();

                                if (mPurchaseOrderListResult.getPurchaseOrderList() != null) {
                                    temp_list.addAll(mPurchaseOrderListResult.getPurchaseOrderList());
                                }

                                historyAdapter = new PurchaseOrderHeaderArrayAdapter(mContext, temp_list);

                                historyList.setAdapter(historyAdapter);
                                historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        TextView purchaseText = (TextView) view.findViewById(R.id.lblPurchaseRecNo);

                                        mPurchaseOrderNo = purchaseText.getText().toString();

                                        Intent intent = new Intent(getBaseContext(), PurchaseOrderLineActivity.class);
                                        intent.putExtra("PURCHASE_ORDER_NO", mPurchaseOrderNo.split(": ")[1]);
                                        startActivity(intent);

                                    }
                                });

                                ClearSearchParams();

                                DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER);
                            } else {
                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mPurchaseOrderListResult.getMessage());
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
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class GetFilterPurchaseOrderListTaskResult extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        PurchaseOrderListResult mPurchaseOrderListResult;
        PurchaseOrderListSearchParameter mPurchaseOrderListSearchParameter;

        public GetFilterPurchaseOrderListTaskResult(Activity mContext, String filterVendorName, String filterPurchaseOrderNo, String filterPurchaseOrderDate) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = mContext;

            mPurchaseOrderListSearchParameter = new PurchaseOrderListSearchParameter(mApp.getmUserCompany(), filterVendorName, filterPurchaseOrderNo, filterPurchaseOrderDate, mApp.getCurrentUserName(), mApp.getCurrentUserPassword());

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
                    if (mPurchaseOrderListResult.getStatus() == BaseResult.BaseResultStatusOk) {
                        historyList.setAdapter(null);

                        ArrayList<PurchaseOrderListItem> temp_list = new ArrayList<PurchaseOrderListItem>();

                        if (mPurchaseOrderListResult.getPurchaseOrderList() != null) {
                            temp_list.addAll(mPurchaseOrderListResult.getPurchaseOrderList());
                        }

                        historyAdapter = new PurchaseOrderHeaderArrayAdapter(mContext, temp_list);

                        historyList.setAdapter(historyAdapter);
                        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                TextView purchaseText = (TextView) view.findViewById(R.id.lblPurchaseRecNo);

                                mPurchaseOrderNo = purchaseText.getText().toString();

                                Intent intent = new Intent(getBaseContext(), PurchaseOrderLineActivity.class);
                                intent.putExtra("PURCHASE_ORDER_NO", mPurchaseOrderNo.split(": ")[1]);
                                startActivity(intent);

                            }
                        });

                        ClearSearchParams();

                        DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER);
                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mPurchaseOrderListResult.getMessage());
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
//                    ArrayList<PurchaseOrderListItem> temp_list = new ArrayList<PurchaseOrderListItem>();
//
//                    if(mResult != null)
//                    {
//                        temp_list.addAll(mResult);
//                    }
                        mGetPurchaseOrderListTask = new GetPurchaseOrderListTask((Activity) mContext);
                        mGetPurchaseOrderListTask.execute((Void) null);

                        DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER);
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

    public void updateVendorShipmentInfo(final String poNo, String vendorShipmentNo) {

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
        if(vendorShipmentNo != null) {
            title.setText("Vendor Shipt No: " + vendorShipmentNo);
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

    void ClearSearchParams() {
        mFilterVendor = "";
        mFilterPurchaseNo = "";
        mFilterDate = "";
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
                mGetPurchaseOrderListTask = new GetPurchaseOrderListTask(this);
                mGetPurchaseOrderListTask.execute((Void) null);
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
        dialog.setContentView(R.layout.dialog_search);
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

                Log.d("DEBUGGING", mFilterVendor + " " + mFilterPurchaseNo + " " + mFilterDate);
                mGetFilterPurchaseOrderListTaskResult = new GetFilterPurchaseOrderListTaskResult((Activity) mContext, mFilterVendor, mFilterPurchaseNo, mFilterDate);
                mGetFilterPurchaseOrderListTaskResult.execute((Void) null);
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

    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
