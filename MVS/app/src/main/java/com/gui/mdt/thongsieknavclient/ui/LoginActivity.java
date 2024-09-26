package com.gui.mdt.thongsieknavclient.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.seanzor.prefhelper.SharedPrefHelper;
import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.BuildConfig;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.gui.mdt.thongsieknavclient.model.AuthenticateUserParameter;
import com.gui.mdt.thongsieknavclient.model.AuthenticateUserResult;
import com.gui.mdt.thongsieknavclient.utils.Log4jHelper;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    Context mContext;
    private UserLoginTask mAuthTask = null;
    private InitializeActivityTask mInitTask = null;

    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;
    private Spinner mCompany;
    private View mProgressView;
    private View mLoginFormView;
    private NavClientApp mApp;


    SharedPreferences mDefaultSharedPreferences;
    SharedPrefHelper mPrefHelper;

    private String prevServerEntPoint;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int INITIAL_REQUEST=1337;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!canAccessCamera()||!canAccessStorage()||!canAccessLocation()) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }

        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefHelper = new SharedPrefHelper(getResources(), mDefaultSharedPreferences);


        displayVersion();
        checkForUpdates();

        // configure log4j
        configureLog4j();

        // For comparison when user changes the server pref string, so that activity can reload company
        prevServerEntPoint = mPrefHelper.getString(R.string.pref_api_server_key);

        mContext = this;

        // Set up the login form.
        mUserView = (EditText) findViewById(R.id.userName);

        //display last login user name
        String lastUserName = mPrefHelper.getString(R.string.pref_last_user_name);
        mUserView.setText(lastUserName);

        mPasswordView = (EditText) findViewById(R.id.password);

        //display last login user password
        String lastUserPassword = mPrefHelper.getString(R.string.pref_last_user_password);
        mPasswordView.setText(lastUserPassword);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mCompany = (Spinner) findViewById(R.id.company);

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (isNetworkConnected()) {
//                    startActivity(new Intent( getApplicationContext() , SaleOrderActivity.class));
                    attemptLogin();
                //} else {
                    //NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_no_connection), getResources().getString(R.string.notification_msg_no_connection));
                //}
            }
        });

        Button mSettingButton = (Button) findViewById(R.id.pref_button);
        mSettingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GeneralSettingsActivity.class));
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mApp = (NavClientApp) getApplication();

        initializeActivity();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean isLoginSuccess = extras.getBoolean("LOGIN");    // NO LONGER NECESSARY, CAN BE DELETED

        }

        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }

    }

    private void displayVersion() {
        //display verison no
        String versionName = BuildConfig.VERSION_NAME;

        TextView textVersionName = (TextView) findViewById(R.id.textVersionName);
        textVersionName.setText("version: " + versionName);
    }

    //hockeyapp integration
    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void initializeActivity() {
        /** 27-07-2016 no initialization of compaines required , compaines created in NAV will be hardcoded **/

        checkConnection();

        /*
        ArrayAdapter simpleAdapter = new ArrayAdapter( this  , android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.default_nav_compaines));
        mCompany.setAdapter(simpleAdapter);*/
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if  the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;

        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(userName, password,isNetworkConnected());
            mAuthTask.execute((Void) null);
        }

    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        //return password.length() > 4;
        return true;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserName;
        private final String mPassword;
        private final String mSelectedCompany;
        private final boolean isNetworkConnected;

        UserLoginTask(String userName, String password, boolean isNetworkConnected) {
            mUserName = userName;
            mPassword = password;
            this.isNetworkConnected = isNetworkConnected;

            if(isNetworkConnected) {
                if (mCompany.getAdapter().getCount() > 0) {
                    mSelectedCompany = mCompany.getSelectedItem().toString();
                } else {
                    mSelectedCompany = "";
                }
            }else {
                //consider getting company from default shared preferences if app using offline.
                mSelectedCompany = "DODO Marketing";
            }
        }

        UserLoginTask(String userName, String password, String company,boolean isNetworkConnected) {
            mUserName = userName;
            mPassword = password;
            mSelectedCompany = company;
            this.isNetworkConnected=isNetworkConnected;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                if (mSelectedCompany.isEmpty() || mPassword.isEmpty())      // for cases where company list is empty
                {
                    return false;
                } else {

                    AuthenticateUserResult result = new AuthenticateUserResult();

                    //if the network not available sales app should support for offline logging.
                    if(isNetworkConnected){
                        Call<AuthenticateUserResult> call = mApp.getNavBrokerService().AuthenticateUser(
                                new AuthenticateUserParameter(mSelectedCompany, mUserName, mPassword)
                        );
                        result = call.execute().body();
                    }
                    else
                    {
                        UserSetupDbHandler dbAdapter = new UserSetupDbHandler(getApplicationContext());
                        dbAdapter.open();
                        boolean authStatus =dbAdapter.authenticateOfflineUser(mUserName,mPassword);
                        result.setAuthenticated(authStatus);
                        dbAdapter.close();
                    }


                    Gson gson = new Gson();
                    String json = gson.toJson(new AuthenticateUserParameter(mSelectedCompany, mUserName, mPassword));
                    Log.d("LOGIN", json);

                    if (result != null) {
                        mPrefHelper.applyInt(R.string.pref_gst_percentage_key, result.getGstPercentage() == 0 ? 7 :result.getGstPercentage());
                        return result.isAuthenticated();
                    } else {
                        return false;
                    }
                }

            } catch (IOException e) {
                Log.d("UserLoginTask", e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                try {
                    boolean rememberUser = mPrefHelper.getBoolean(R.string.pref_remember_user_name_key, true);
                    boolean rememberPassword = mPrefHelper.getBoolean(R.string.pref_remember_password_key, true);

                    if (rememberUser) {
                        mPrefHelper.applyString(R.string.pref_last_user_name, mUserName);
                    }
                    if (rememberPassword) {
                        mPrefHelper.applyString(R.string.pref_last_user_password, mPassword);
                    }else {
                        mPrefHelper.applyString(R.string.pref_last_user_password, "");
                    }

                   /* if(mPrefHelper.getInt(R.string.pref_last_logging_year)!=DateTime.now().getYear()){
                        mPrefHelper.applyInt(R.string.pref_last_logging_year, DateTime.now().getYear());
                    }*/


                    mApp.setCurrentUserName(mUserName);
                    mApp.setCurrentUserPassword(mPassword);

                    if(isNetworkConnected) {
                        mApp.setmUserCompany(mCompany.getItemAtPosition(mCompany.getSelectedItemPosition()).toString());
                    } else
                    {
                        //TODO : how to get company if offline
                        mApp.setmUserCompany(mSelectedCompany);
                    }

                    Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
                    intent.putExtra("isOfflineLogin",!isNetworkConnected);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                try {
                    if (mSelectedCompany.isEmpty()) {
                        NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_error), getResources().getString(R.string.notification_msg_no_company));
                    } else {
                        mUserView.setError(getString(R.string.error_incorrect_user_and_password));
                        mUserView.requestFocus();

                    }
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class InitializeActivityTask extends AsyncTask<Void, Void, Boolean> {
        List<String> mCompanies;
        Activity mActivity;

        InitializeActivityTask(Activity activity) {

            mActivity = activity;
            NotificationManager.ShowProgressDialog(mContext, NotificationManager.DIALOG_TITLE_LOAD_DATA, NotificationManager.DIALOG_MSG_LOAD);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<List<String>> call = mApp.getNavBrokerService().GetCompaines();
                mCompanies = call.execute().body();

            } catch (IllegalStateException e) {
                return false;
            } catch (IOException e) {
                Log.d("GetCompanies", e.toString());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mInitTask = null;
            NotificationManager.HideProgressDialog();

            if (success) {
                try {
                    if (mCompanies != null && mCompanies.size() > 0) {
                        ArrayAdapter simpleAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, mCompanies);
                        mCompany.setAdapter(simpleAdapter);
                    } else {
                        ArrayAdapter simpleAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
                        mCompany.setAdapter(simpleAdapter);
                        NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_error), getResources().getString(R.string.notification_msg_unable_to_retrieve));
                    }
                    //showProgress(false);
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                try {
                    ArrayAdapter simpleAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
                    mCompany.setAdapter(simpleAdapter);
                    NotificationManager.DisplayAlertDialog(mContext, getResources().getString(R.string.notification_title_error), getResources().getString(R.string.notification_msg_unable_to_retrieve));
                } catch (Exception ex) {
                    NotificationManager.DisplayAlertDialog(mContext, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mInitTask = null;
            NotificationManager.HideProgressDialog();
            //showProgress(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String newServerEndPoint = mPrefHelper.getString(R.string.pref_api_server_key);
        if (!newServerEndPoint.equals(prevServerEntPoint)) {
            mApp.reinitializeRetrofit();
            initializeActivity();
        }

        checkForCrashes();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ctvege_login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_reloadCompany:
                initializeActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkConnection() {
        if (!isNetworkConnected()) {
            new AlertDialog.Builder(mContext)
                    .setTitle(getResources().getString(R.string.notification_title_no_connection))
                    .setMessage(getResources().getString(R.string.notification_msg_no_connection))
                    .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkConnection();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            //application should allow to use offline. can't kill the process
                            //android.os.Process.killProcess(android.os.Process.myPid());
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        } else {
            Log.d("LOGIN", "PASSED");

            if (mApp.getNavBrokerService() != null) {
                mInitTask = new InitializeActivityTask(this);
                mInitTask.execute((Void) null);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
                case INITIAL_REQUEST:
                    if (!canAccessCamera()) {
                        Toast.makeText(this, "Camera not allowed!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Camera allowed!", Toast.LENGTH_SHORT).show();
                    }
                    if (!canAccessCamera()) {
                        Toast.makeText(this, "Write to external storage not allowed!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Write to external storage allowed!", Toast.LENGTH_SHORT).show();
                    }
                    if (!canAccessCamera()) {
                        Toast.makeText(this, "Location access not allowed!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Location access allowed!", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }

    }

    private void configureLog4j() {

        String fileName = Environment.getExternalStorageDirectory() + "/" + "log4j.log";
        String filePattern = "%d - [%c] - %p : %m%n";
        int maxBackupSize = 10;
        long maxFileSize = 1024 * 1024;

        if(canAccessStorage()) {
            Log4jHelper.Configure(fileName, filePattern, maxBackupSize, maxFileSize);
        }
    }

    private boolean canAccessCamera() {

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }else {
            return true;
        }

    }

    private boolean canAccessStorage() {

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }else {
            return true;
        }

    }

    private boolean canAccessLocation() {

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }else {
            return true;
        }

    }

}

