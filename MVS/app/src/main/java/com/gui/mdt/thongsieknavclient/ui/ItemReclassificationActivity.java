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
import com.gui.mdt.thongsieknavclient.adapters.itemreclass.itemReclassHeaderArrayAdapter;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassListResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassListResultData;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassificationListSearchParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;


public class ItemReclassificationActivity extends BaseActivity {


    private GetItemReclassListTask mGetItemReclassListTask = null;
    private SearchItemReclassListTask mSearchItemReclassListTask = null;
    private RemoveItemReclassHeaderTask mRemoveItemReclassHeaderTask = null;

    ListView historyList;
    itemReclassHeaderArrayAdapter historyAdapter;
    Calendar calFilterDate;
    private Context mContext;
    private NavClientApp mApp;
    private String mDocNo;
    private String Loc;
    private String newLoc;
    private String Createdby;
    private String postDate;
    private String mFilterDocNo;
    private String mFilterDate;
    private int mHeaderLineNo;
    private boolean isShowBinCode = false;
    private boolean isShowNewBinCode = false;

    protected int getLayoutResource() {
        return R.layout.activity_item_reclassification;
    }

    @Override
    protected void handleHomeAsUpEvent() {
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_reclassification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        calFilterDate = Calendar.getInstance();
        mApp = (NavClientApp) getApplication();
        historyList = (ListView) findViewById(R.id.orderList);

        mGetItemReclassListTask = new GetItemReclassListTask(this);
        mGetItemReclassListTask.execute((Void) null);
    }

    public class GetItemReclassListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemReclassListResult XItemReclassListResult;
        ItemReclassificationListSearchParameter mItemReclassificationListSearchParameter;

        GetItemReclassListTask(Activity activity) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mItemReclassificationListSearchParameter = new ItemReclassificationListSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), "");

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassificationListSearchParameter);
            Log.d("Item Reclass Header ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<ItemReclassListResult> call = mApp.getNavBrokerService().GetItemReclassList(mItemReclassificationListSearchParameter);

                XItemReclassListResult = call.execute().body();

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

                    if (XItemReclassListResult != null) {

                        if (XItemReclassListResult.getItemReclassListResultData().size() != 0) {

                            if (XItemReclassListResult.getStatus() == BaseResult.BaseResultStatusOk) {

                                historyList.setAdapter(null);
                                final ArrayList<ItemReclassListResultData> temp_list = new ArrayList<>();

                                if (XItemReclassListResult.getItemReclassListResultData() != null) {
                                    temp_list.addAll(XItemReclassListResult.getItemReclassListResultData());
                                }

                                historyAdapter = new itemReclassHeaderArrayAdapter(mContext, temp_list);
                                historyAdapter.notifyDataSetChanged();
                                historyList.setAdapter(historyAdapter);
                                historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        TextView DocNo = (TextView) view.findViewById(R.id.lblDocNo);
                                        TextView tvLoc = (TextView) view.findViewById(R.id.lblLoc);
                                        TextView tvNewLoc = (TextView) view.findViewById(R.id.lblNewLoc);
                                        TextView Creator = (TextView) view.findViewById(R.id.lblCreator);
                                        TextView PostDate = (TextView) view.findViewById(R.id.lblPostDate);

                                        mDocNo = temp_list.get(i).getDocumentNo();
                                        Loc = temp_list.get(i).getLocationCode();
                                        newLoc = temp_list.get(i).getNewLocationCode();
                                        Createdby = temp_list.get(i).getCreatedBy();
                                        postDate = PostDate.getText().toString();

                                        mHeaderLineNo = temp_list.get(i).getReclassHeaderLineNo();
                                        isShowBinCode = temp_list.get(i).isShowBinCode();
                                        isShowNewBinCode = temp_list.get(i).isShowNewBinCode();

                                        DataManager.getInstance().clearItemReclassData();
                                        DataManager.getInstance().ItemReclassificationNo = mDocNo;
                                        DataManager.getInstance().ItemReclassHeaderLineNo = mHeaderLineNo;
                                        DataManager.getInstance().PostingDate = postDate;
                                        DataManager.getInstance().setTransitionItemReclassListResultData(temp_list.get(i));

                                        Log.d("RECLASS_HEADER_LINE_NO", String.valueOf(mHeaderLineNo));
                                        Intent intent = new Intent(getBaseContext(), ItemReclassificationLineActivity.class);
                                        intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNo);
                                        intent.putExtra("IS_SHOW_BIN_CODE", isShowBinCode);
                                        intent.putExtra("IS_SHOW_NEW_BIN_CODE", isShowNewBinCode);
                                        intent.putExtra("DOCUMENT_NO", mDocNo);
                                        startActivity(intent);

                                    }
                                });

                            } else {
                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, XItemReclassListResult.getMessage());
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
            }
        }

        @Override
        protected void onCancelled() {
            NotificationManager.HideProgressDialog();
        }
    }

    public class SearchItemReclassListTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        ItemReclassListResult mItemReclassListResult;
        ItemReclassificationListSearchParameter mItemReclassificationListSearchParameter;

        SearchItemReclassListTask(Activity activity, String mFilterDocNo) {
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
            mActivity = activity;

            mItemReclassificationListSearchParameter = new ItemReclassificationListSearchParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), mFilterDocNo);

            Gson gson = new Gson();
            String json = gson.toJson(mItemReclassificationListSearchParameter);
            Log.d("Item Reclass Header ", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<ItemReclassListResult> call = mApp.getNavBrokerService().GetItemReclassList(mItemReclassificationListSearchParameter);

                mItemReclassListResult = call.execute().body();

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
                    if (mItemReclassListResult != null) {

                        if (mItemReclassListResult.getItemReclassListResultData().size() != 0) {

                            if (mItemReclassListResult.getStatus() == BaseResult.BaseResultStatusOk) {

                                historyList.setAdapter(null);
                                historyList.setVisibility(View.VISIBLE);
                                final ArrayList<ItemReclassListResultData> temp_list = new ArrayList<>();

                                if (mItemReclassListResult.getItemReclassListResultData() != null) {
                                    temp_list.addAll(mItemReclassListResult.getItemReclassListResultData());
                                }

                                historyAdapter = new itemReclassHeaderArrayAdapter(mContext, temp_list);
                                historyAdapter.notifyDataSetChanged();
                                historyList.setAdapter(historyAdapter);
                                historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        TextView DocNo = (TextView) view.findViewById(R.id.lblDocNo);
                                        TextView tvLoc = (TextView) view.findViewById(R.id.lblLoc);
                                        TextView tvNewLoc = (TextView) view.findViewById(R.id.lblNewLoc);
                                        TextView Creator = (TextView) view.findViewById(R.id.lblCreator);
                                        TextView PostDate = (TextView) view.findViewById(R.id.lblPostDate);

                                        mDocNo = temp_list.get(i).getDocumentNo();
                                        Loc = temp_list.get(i).getLocationCode();
                                        newLoc = temp_list.get(i).getNewLocationCode();
                                        Createdby = temp_list.get(i).getCreatedBy();
                                        postDate = PostDate.getText().toString();

                                        mHeaderLineNo = temp_list.get(i).getReclassHeaderLineNo();
                                        isShowBinCode = temp_list.get(i).isShowBinCode();
                                        isShowNewBinCode = temp_list.get(i).isShowNewBinCode();

                                        DataManager.getInstance().clearItemReclassData();
                                        DataManager.getInstance().ItemReclassificationNo = mDocNo;
                                        DataManager.getInstance().ItemReclassHeaderLineNo = mHeaderLineNo;
                                        DataManager.getInstance().PostingDate = postDate;
                                        DataManager.getInstance().setTransitionItemReclassListResultData(temp_list.get(i));

                                        Log.d("RECLASS_HEADER_LINE_NO", String.valueOf(mHeaderLineNo));
                                        Intent intent = new Intent(getBaseContext(), ItemReclassificationLineActivity.class);
                                        intent.putExtra("RECLASS_HEADER_LINE_NO", mHeaderLineNo);
                                        intent.putExtra("IS_SHOW_BIN_CODE", isShowBinCode);
                                        intent.putExtra("IS_SHOW_NEW_BIN_CODE", isShowNewBinCode);
                                        intent.putExtra("DOCUMENT_NO", mDocNo);
                                        startActivity(intent);

                                    }
                                });

                                DataManager.getInstance().ClearOverdueRecordsData(ConfigurationManager.ORDER_TYPE_ITEM_RECLASS);
                            } else {
                                NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, mItemReclassListResult.getMessage());
                            }
                        } else {
                            lblNoData.setVisibility(View.VISIBLE);
                            historyList.setVisibility(View.GONE);
                        }
                    } else {
                        lblNoData.setVisibility(View.VISIBLE);
                        historyList.setVisibility(View.GONE);
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

    public class RemoveItemReclassHeaderTask extends AsyncTask<Void, Void, Boolean> {

        Activity mActivity;
        ItemReclassParameter mRemoveItemReclassHeaderParameter;
        BaseResult mReturnBaseResult;

        RemoveItemReclassHeaderTask(Activity activity, int HeaderLineNo) {

            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_DELETE_DATA, NotificationManager.DIALOG_MSG_DELETE);
            mActivity = activity;
            mRemoveItemReclassHeaderParameter = new ItemReclassParameter(mApp.getmUserCompany(), mApp.getCurrentUserName(), mApp.getCurrentUserPassword(), HeaderLineNo, "", "");

            Gson gson = new Gson();
            String json = gson.toJson(mRemoveItemReclassHeaderParameter);
            Log.d("JSON", json);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Call<BaseResult> call = mApp.getNavBrokerService().RemoveItemReclass(mRemoveItemReclassHeaderParameter);
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

                        historyAdapter.notifyDataSetChanged();
                        mGetItemReclassListTask = new GetItemReclassListTask((Activity) mContext);
                        mGetItemReclassListTask.execute((Void) null);

                    } else {
                        NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_REMOVE_FAILED, mReturnBaseResult.getMessage());
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_reclass_header_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchDialog();
                return true;
            case R.id.action_AddItemReclass:
                showAddTransferOrderActivity();
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
        dialog.setContentView(R.layout.dialog_search_item_reclassification_header);
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
                EditText temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterTransferOrdNo);
                mFilterDocNo = temp_filterParam.getText().toString();

//                temp_filterParam = (EditText) dialog.findViewById(R.id.editFilterDate);
//                mFilterDate = DataConverter.ConvertDateToYearMonthDay(temp_filterParam.getText().toString());

                mSearchItemReclassListTask = new SearchItemReclassListTask((Activity) mContext, mFilterDocNo);
                mSearchItemReclassListTask.execute((Void) null);

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

    private void showAddTransferOrderActivity() {
        Intent i = new Intent(ItemReclassificationActivity.this, AddNewItemReclassificationHeaderActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void RemoveHeader(int HeaderLineNo) {
        Log.d("SERVER REMOVE", "IR HEADER: " + HeaderLineNo);
        mRemoveItemReclassHeaderTask = new RemoveItemReclassHeaderTask((Activity) mContext, HeaderLineNo);
        mRemoveItemReclassHeaderTask.execute((Void) null);
    }
}