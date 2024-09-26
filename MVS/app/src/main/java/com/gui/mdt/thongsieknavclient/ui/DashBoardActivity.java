package com.gui.mdt.thongsieknavclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.seanzor.prefhelper.SharedPrefHelper;
import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.GridViewAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiDriverParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiDriverResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMSDAuthorizedModulesResponse;
import com.gui.mdt.thongsieknavclient.datamodel.UserSetup;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.gui.mdt.thongsieknavclient.model.AuthenticateUserParameter;
import com.gui.mdt.thongsieknavclient.model.AuthenticateUserResult;
import com.gui.mdt.thongsieknavclient.model.AuthorizedModuleResult;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.ImageItem;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class DashBoardActivity extends AppCompatActivity {

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private View mProgressView;
    private View mDashboardView;
    private NavClientApp mApp;
    private InitializeActivityTask mInitTask = null;
    private AuthorizedModuleResult mAuthroizedData;
    private AuthorizedModuleResult mMSDAuthorizedData;
    private ApiMSDAuthorizedModulesResponse mApiMSDAuthorizedModulesResponse;
    private ApiDriverResponse mApiDriverResponse;
    private boolean isOfflineLogin;
    private UserSetup mUserSetup;
    boolean isRunningNoUpdate;


    SharedPreferences mDefaultSharedPreferences;
    SharedPrefHelper mPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefHelper = new SharedPrefHelper(getResources(), mDefaultSharedPreferences);

        mApp = (NavClientApp) getApplication();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mApp.getmUserCompany());

        mDashboardView = findViewById(R.id.dashboard_form);
        mProgressView = findViewById(R.id.dashboard_progress);

        isOfflineLogin = getIntent().getBooleanExtra("isOfflineLogin", false);
        isRunningNoUpdate = mPrefHelper.getBoolean(R.string.pref_update_sync_running_no, false);
        if (!isRunningNoUpdate) {
            initializeActivity();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(NotificationManager.MSG_INFORMATION)
                    .setMessage("Do you wish to running no update from server ?")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            initializeActivity();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            isRunningNoUpdate = false;
                            mPrefHelper.commitBoolean(R.string.pref_update_sync_running_no, false);
                            initializeActivity();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    private void initializeActivity() {
        showProgress(true);
        mInitTask = new InitializeActivityTask(this);
        mInitTask.execute((Void) null);
    }

    private void setupDashboardItems() {

        //start new intent for MVS/MSO/LDS modules separately
        if (mAuthroizedData.isEnableMobileSale()) {

            mApp.setmCurrentModule(getString(R.string.module_mso));
            startActivity(new Intent(getApplicationContext(), MsoHomeActivity.class));
            this.finish();

        } else if (mAuthroizedData.isEnableVanSale()) {

            mApp.setmCurrentModule(getString(R.string.module_mvs));
            startActivity(new Intent(getApplicationContext(), MvsHomeActivity.class));
            this.finish();

        } else if (mAuthroizedData.isEnableLDS()) {

            mApp.setmCurrentModule(getString(R.string.module_lds));
            startActivity(new Intent(getApplicationContext(), LdsHomeActivity.class));
            this.finish();
        }

        ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
        TypedArray images = getResources().obtainTypedArray(R.array.dashboard_grid_rows_images);  //make change here
        // dashboard_grid_rows_images
        String[] values = getResources().getStringArray(R.array.dashboard_grid_rows_values);    //make change here
        // dashboard_grid_rows_values

        for (int i = 0; i < images.length(); i++) {
            boolean add = false;
            //below 2 lines is added for sales order module
            try {
                switch (i) {
                    case 0:
                        add = mAuthroizedData.isEnablePurchaseOrder();
                        break;
                    case 1:
                        add = mAuthroizedData.isEnableGoodsReceive();
                        break;
                    case 2:
                        add = mAuthroizedData.isEnableTransferOut();
                        break;
                    case 3:
                        add = mAuthroizedData.isEnableTransferIn();
                        break;
                    case 4:
                        add = mAuthroizedData.isEnableProduction();
                        break;
                    case 5:
                        add = mAuthroizedData.isEnableProduction();
                        break;
                    case 6:
                        add = mAuthroizedData.isEnableDelivery();
                        break;
                    case 7:
                        add = mAuthroizedData.isEnableItemReclass();
                        break;
                    case 8:
                        add = mAuthroizedData.isEnableStockTake();
                        break;
                    case 9:
                        add = true; //exit
                }
            } catch (Exception ex) {
                NotificationManager.DisplayAlertDialog(this, NotificationManager.MSG_ERROR, ex.getMessage());
            }

            if (add) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                            images.getResourceId(i, -1));
                    imageItems.add(new ImageItem(bitmap, values[i]));
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(this, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            }
        }

        mGridView = (GridView) this.findViewById(R.id.gridView);
        mGridAdapter = new GridViewAdapter(this, R.xml.dashboard_grid_row, imageItems);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView temp_text = (TextView) view.findViewById(R.id.text);
                Toast.makeText(getApplicationContext(), temp_text.getText().toString(), Toast.LENGTH_LONG).show();

                switch (temp_text.getText().toString()) {
                    case "PO":
                        startActivity(new Intent(getApplicationContext(), PurchaseOrderActivity.class));
                        break;
                    case "GRN":
                        startActivity(new Intent(getApplicationContext(), GRNOrderActivity.class));
                        break;
                    case "Transfer Out":
                        startActivity(new Intent(getApplicationContext(), TransferOutActivity.class));
                        break;
                    case "Transfer In":
                        startActivity(new Intent(getApplicationContext(), TransferInActivity.class));
                        break;
                    case "Prod. Semi":
                        startActivity(new Intent(getApplicationContext(), ProductionPasteGroupActivity.class));
                        break;
                    case "Prod. Fin":
                        startActivity(new Intent(getApplicationContext(), ProductionFinishedGoodsActivity.class));
                        break;
                    case "Delivery":
                        startActivity(new Intent(getApplicationContext(), WarehouseShipmentActivity.class));
                        break;
                    case "Item Reclass":
                        startActivity(new Intent(getApplicationContext(), ItemReclassificationActivity.class));
                        break;
                    case "Stock Take":
                        startActivity(new Intent(getApplicationContext(), StockTakeActivity.class));
                        break;
                    case "Exit":
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        // set the new task and clear flags
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mDashboardView.setVisibility(show ? View.GONE : View.VISIBLE);
            mDashboardView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDashboardView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mDashboardView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void forceReturnLoginActivity(boolean isAuthorized) {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.putExtra("LOGIN", isAuthorized);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public class InitializeActivityTask extends AsyncTask<Void, Void, Boolean> {
        Activity mActivity;
        //boolean isOfflineLogin = false;

        InitializeActivityTask(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (!isOfflineLogin) {
                    Call<AuthorizedModuleResult> call = mApp.getNavBrokerService()
                            .GetAuthorizedModules(new AuthenticateUserParameter(mApp.getmUserCompany(), mApp
                                    .getCurrentUserName(), mApp.getCurrentUserPassword()));

                    //new service provided for sales app. (nalaka-2017-09-11)
                    Call<ApiMSDAuthorizedModulesResponse> callMsd = mApp.getNavBrokerService()
                            .GetMSDAuthorizedModules(new AuthenticateUserParameter(mApp.getmUserCompany(), mApp
                                    .getCurrentUserName(), mApp.getCurrentUserPassword()));

                    mAuthroizedData = call.execute().body();
                    mApiMSDAuthorizedModulesResponse = callMsd.execute().body();

                    String driverCode = "";

                    if (mApiMSDAuthorizedModulesResponse != null) {
                        if (mApiMSDAuthorizedModulesResponse.getMSDUserSetupResultData() != null)
                            if (mApiMSDAuthorizedModulesResponse.getMSDUserSetupResultData().size() > 0) {
                                driverCode = mApiMSDAuthorizedModulesResponse.getMSDUserSetupResultData().get(0).DriverCode;
                            }
                    }

                    //new service provided for sales app to get user name. (nalaka-2018-01-23)
                    Call<ApiDriverResponse> callDriver = mApp.getNavBrokerService()
                            .GetDriver(
                                    new ApiDriverParameter(
                                            mApp.getmUserCompany()
                                            , mApp.getCurrentUserName()
                                            , mApp.getCurrentUserPassword()
                                            , driverCode == null ? "" : driverCode));

                    mApiDriverResponse = callDriver.execute().body();
                    //mAuthroizedData = mApiMSDAuthorizedModulesResponse.getMSDUserSetupResultData().get(0);

                } else {
                    //when app running offline, saved authentications will use.
                    UserSetupDbHandler db = new UserSetupDbHandler(getApplicationContext());
                    db.open();

                    if (db.authenticateOfflineUser(mApp.getCurrentUserName(), mApp.getCurrentUserPassword())) {
                        mUserSetup = new UserSetup();
                        mUserSetup = db.getUserSetUp(mApp.getCurrentUserName());
                        mAuthroizedData = new AuthorizedModuleResult();
                        mAuthroizedData.setStatus(BaseResult.BaseResultStatusOk);
                        mAuthroizedData.setEnableMobile(mUserSetup.isEnableMobile());
                        mAuthroizedData.setEnableMobileSale(mUserSetup.isEnableMobileSale());
                        mAuthroizedData.setEnableVanSale(mUserSetup.isEnableVanSale());
                        //mAuthroizedData.setEnableDelivery(mUserSetup.isEnableDelivery());
                        mAuthroizedData.setDriverCode(mUserSetup.getDriverCode());
                        mAuthroizedData.setSalespersonCode(mUserSetup.getSalesPersonCode());
                        mAuthroizedData.setEnableLDS(mUserSetup.isEnableLDS());

                        mMSDAuthorizedData = new AuthorizedModuleResult();
                        mMSDAuthorizedData.setStatus(BaseResult.BaseResultStatusOk);
                        mMSDAuthorizedData.setEnableMobile(mUserSetup.isEnableMobile());
                        mMSDAuthorizedData.setEnableMobileSale(mUserSetup.isEnableMobileSale());
                        mMSDAuthorizedData.setEnableVanSale(mUserSetup.isEnableVanSale());
                        //mMSDAuthorizedData.setEnableDelivery(mUserSetup.isEnableDelivery());
                        mMSDAuthorizedData.setDriverCode(mUserSetup.getDriverCode());
                        mMSDAuthorizedData.setSalespersonCode(mUserSetup.getSalesPersonCode());
                        mMSDAuthorizedData.setEnableLDS(mUserSetup.isEnableLDS());
                    }

                }


                Gson gson = new Gson();
                String json = gson.toJson(new AuthenticateUserParameter(mApp.getmUserCompany(), mApp
                        .getCurrentUserName(), mApp.getCurrentUserPassword()));
                Log.d("DASHBOARD", json);

                if (mAuthroizedData != null) {
                    return mAuthroizedData.getStatus() == AuthenticateUserResult.BaseResultStatusOk;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.d("NAV_Client_Exception", e.toString());
                return false;
            }
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mInitTask = null;

            Log.d("DASHBOARD", "RETURNED");
            if (success) {
                try {
                    if (mAuthroizedData.getStatus() == BaseResult.BaseResultStatusOk) {

                        // for every successful online login, update offline user data.
                        if (!isOfflineLogin) {

                            mMSDAuthorizedData = mApiMSDAuthorizedModulesResponse.getMSDUserSetupResultData().get(0);

                            String currentDriver = mMSDAuthorizedData.getDriverCode();
                            String currentSalesPerson = mMSDAuthorizedData.getSalespersonCode();
                            mApp.setmCurrentDriverCode(currentDriver == null ? "" : currentDriver);
                            mApp.setmCurrentSalesPersonCode(currentSalesPerson == null ? "" : currentSalesPerson);

                            String currentUserDisplayName = "";
                            if (mApiDriverResponse != null && mApiDriverResponse.getDriverResultData() != null) {
                                for (ApiDriverResponse.ApiDriverResultData driver :
                                        mApiDriverResponse.getDriverResultData()) {

                                    if (driver.getCode() != null && driver.getDescription() != null) {
                                        if ((!mApp.getmCurrentDriverCode().equals("")) &&
                                                driver.getCode().equals(mApp.getmCurrentDriverCode())) {
                                            currentUserDisplayName = driver.getDescription();
                                            mApp.setCurrentUserDisplayName(currentUserDisplayName);
                                        }
                                    }

                                /*else if(!mApp.getmCurrentSalesPersonCode().equals("") &&
                                        driver.getCode()==mApp.getmCurrentSalesPersonCode()){
                                    currentUserDisplayName=driver.getDescription();
                                }*/
                                }
                            }

                            updateUserData();

                        } else {
                            String currentUserDisplayName = mUserSetup.getUserDisplayName();
                            mApp.setCurrentUserDisplayName(currentUserDisplayName
                                    == null ? "" : currentUserDisplayName);
                        }

                        String currentDriver = mMSDAuthorizedData.getDriverCode();
                        String currentSalesPerson = mMSDAuthorizedData.getSalespersonCode();
                        mApp.setmCurrentDriverCode(currentDriver == null ? "" : currentDriver);
                        mApp.setmCurrentSalesPersonCode(currentSalesPerson == null ? "" : currentSalesPerson);

                        if (mAuthroizedData.EnableMobile) {
                            setupDashboardItems();
                            showProgress(false);
                        } else {
                            String msg = "";

                            if (mAuthroizedData.getMessage() != null && !mAuthroizedData.getMessage().isEmpty()) {
                                msg = mAuthroizedData.getMessage();
                            } else {
                                msg = getResources().getString(R.string.notification_msg_no_permission);
                            }

                            new AlertDialog.Builder(mActivity)
                                    .setTitle(getResources().getString(R.string.notification_title_no_permission))
                                    .setMessage(msg)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            showProgress(false);
                                            forceReturnLoginActivity(true);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                    } else {
                        new AlertDialog.Builder(mActivity)
                                .setTitle(NotificationManager.MSG_ERROR)
                                .setMessage(mAuthroizedData.getMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgress(false);
                                        forceReturnLoginActivity(false);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mActivity, NotificationManager.MSG_ERROR, ex.getMessage());
                }

            } else {
                try {
                    if (mAuthroizedData != null && !mAuthroizedData.EnableMobile && mAuthroizedData.getMessage()
                            .length() > 0) {
                        new AlertDialog.Builder(mActivity)
                                .setTitle(NotificationManager.MSG_ERROR)
                                .setMessage(mAuthroizedData.getMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgress(false);
                                        forceReturnLoginActivity(true);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        if (mAuthroizedData != null) {
                            if (mAuthroizedData.getMessage() != null && !mAuthroizedData.getMessage().isEmpty()) {
                                NotificationManager.DisplayAlertDialog(mActivity, getResources().getString(R.string
                                        .notification_title_error), mAuthroizedData.getMessage());
                            } else {
                                forceReturnLoginActivity(false);
                            }
                        } else {
                            String msg = getResources().getString(R.string
                                    .notification_msg_no_permission_access_selected_company);
                            new AlertDialog.Builder(mActivity)
                                    .setTitle(getResources().getString(R.string.notification_title_no_permission))
                                    .setMessage(msg)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            showProgress(false);
                                            forceReturnLoginActivity(true);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mActivity, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mInitTask = null;
            showProgress(false);
        }

        private void updateUserData() {
            if (mMSDAuthorizedData != null) {
                mAuthroizedData.setEnableLDS(mMSDAuthorizedData.isEnableLDS());
                mAuthroizedData.setEnableMobileSale(mMSDAuthorizedData.isEnableMobileSale());
                mAuthroizedData.setEnableVanSale(mMSDAuthorizedData.isEnableVanSale());

                UserSetupDbHandler db = new UserSetupDbHandler(getApplicationContext());
                db.open();
                //TODO set running no's from server;
                UserSetup u = db.getUserSetUp(mApp.getCurrentUserName());
                try {
                    if (db.deleteUser(mApp.getCurrentUserName())) {

                        UserSetup user = new UserSetup();
                        user.setUserName(mApp.getCurrentUserName());
                        user.setPassword(mApp.getCurrentUserPassword());
                        user.setEnableMobile(mMSDAuthorizedData.isEnableMobile());
                        user.setEnableDelivery(mMSDAuthorizedData.isEnableDelivery());
                        user.setEnableVanSale(mMSDAuthorizedData.isEnableVanSale());
                        user.setEnableMobileSale(mMSDAuthorizedData.isEnableMobileSale());
                        user.setDriverCode(mMSDAuthorizedData.getDriverCode());
                        user.setSalesPersonCode(mMSDAuthorizedData.getSalespersonCode());
                        user.setInitialSyncRun(u.isInitialSyncRun());
                        user.setEnableLDS(mMSDAuthorizedData.isEnableLDS());
                        user.setUserDisplayName(mApp.getCurrentUserDisplayName());

                        String soNoMSO = "";
                        String soNoMVS = "";
                        String siNo = "";
                        String paymentNo = "";
                        String transferInNo = "";
                        String transferOutNo = "";
                        String stockRequestNo = "";

//                        boolean isRunningNoUpdate = mPrefHelper.getBoolean(R.string.pref_update_sync_running_no, false);

                        if (mMSDAuthorizedData.getSO_Number_MSO() != null && mMSDAuthorizedData.getSO_Number_MSO() != "") {
                            soNoMSO = mMSDAuthorizedData.getSO_Number_MSO();
                            String savedNo = u.getSoRunningNoMso();
                            savedNo = savedNo == null ? "0" : savedNo;

                            if (Integer.parseInt(soNoMSO) == 0) {
                                soNoMSO = "00000";
                            } else if (Integer.parseInt(soNoMSO) < Integer.parseInt(savedNo)) {
                                soNoMSO = savedNo;
                            }

                            user.setSoRunningNoMso(soNoMSO);
                        } else {
                            user.setSoRunningNoMso(u.getSoRunningNoMso());
                        }

                        if (mMSDAuthorizedData.getSO_Number_MVS() != null && mMSDAuthorizedData.getSO_Number_MVS() != "") {
                            soNoMVS = mMSDAuthorizedData.getSO_Number_MVS();
                            String savedNo = u.getSoRunningNoMvs();
                            savedNo = savedNo == null ? "0" : savedNo;
                            if (Integer.parseInt(soNoMVS) == 0) {
                                soNoMVS = "00000";
                            } else if (Integer.parseInt(soNoMVS) < Integer.parseInt(savedNo) && !isRunningNoUpdate) {
                                soNoMVS = savedNo;
                            }

                            user.setSoRunningNoMvs(soNoMVS);
                        } else {
                            user.setSoRunningNoMvs(u.getSoRunningNoMvs());
                        }

                        if (mMSDAuthorizedData.getSI_Number() != null && mMSDAuthorizedData.getSI_Number() != "") {
                            siNo = mMSDAuthorizedData.getSI_Number();
                            String savedNo = u.getSiRunningNo();
                            savedNo = savedNo == null ? "0" : savedNo;

                            if (Integer.parseInt(siNo) < Integer.parseInt(savedNo) && !isRunningNoUpdate) {
                                siNo = savedNo;
                            }

                            user.setSiRunningNo(siNo);
                        } else {
                            user.setSiRunningNo(u.getSiRunningNo());
                        }

                        if (mMSDAuthorizedData.getPayment_Number() != null && mMSDAuthorizedData.getPayment_Number() != "") {
                            paymentNo = mMSDAuthorizedData.getPayment_Number();
                            String savedNo = u.getPaymentRunningNo();
                            savedNo = savedNo == null ? "0" : savedNo;

                            if (Integer.parseInt(paymentNo) < Integer.parseInt(savedNo)) {
                                paymentNo = savedNo;
                            }

                            user.setPaymentRunningNo(paymentNo);
                        } else {
                            user.setPaymentRunningNo(u.getPaymentRunningNo());
                        }

                        if (mMSDAuthorizedData.getTransfer_In_Number() != null && mMSDAuthorizedData.getTransfer_In_Number() != "") {
                            transferInNo = mMSDAuthorizedData.getTransfer_In_Number();
                            String savedNo = u.getTransferInNumber();
                            savedNo = savedNo == null ? "0" : savedNo;

                            if (Integer.parseInt(transferInNo) < Integer.parseInt(savedNo)) {
                                transferInNo = savedNo;
                            }

                            user.setTransferInNumber(transferInNo);
                        } else {
                            user.setTransferInNumber(u.getTransferInNumber());
                        }


                        if (mMSDAuthorizedData.getTransfer_Out_Number() != null && mMSDAuthorizedData.getTransfer_Out_Number() != "") {
                            transferOutNo = mMSDAuthorizedData.getTransfer_Out_Number();
                            String savedNo = u.getTransferOutNumber();
                            savedNo = savedNo == null ? "0" : savedNo;

                            if (Integer.parseInt(transferOutNo) < Integer.parseInt(savedNo)) {
                                transferOutNo = savedNo;
                            }

                            user.setTransferOutNumber(transferOutNo);
                        } else {
                            user.setTransferOutNumber(u.getTransferOutNumber());
                        }

                        if (mMSDAuthorizedData.getStock_Request_No() != null && mMSDAuthorizedData.getStock_Request_No() != "") {
                            stockRequestNo = mMSDAuthorizedData.getStock_Request_No();
                            String savedNo = u.getSrRunningNoMvs();
                            savedNo = savedNo == null ? "0" : savedNo;

                            if (Integer.parseInt(stockRequestNo) < Integer.parseInt(savedNo) && !isRunningNoUpdate) {
                                stockRequestNo = savedNo;
                            }

                            user.setSrRunningNoMvs(stockRequestNo);
                        } else {
                            user.setSrRunningNoMvs(u.getSrRunningNoMvs());
                        }

                        mPrefHelper.commitBoolean(R.string.pref_update_sync_running_no, false);

                        db.addUserSetup(user);
                        mUserSetup = user;

                    }

                } catch (Exception e) {
                    Log.d("NAV_Client_Exception", e.toString());
                }
                db.close();
            }

        }

    }
}
