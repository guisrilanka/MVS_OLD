package com.gui.mdt.thongsieknavclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.multidex.MultiDex;
import android.util.Log;

import com.github.seanzor.prefhelper.SharedPrefHelper;
import com.gui.mdt.thongsieknavclient.interfaces.NavBrokerService;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.IoniconsModule;
import com.orm.SugarApp;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by User on 12/7/2016.
 */
public class NavClientApp extends SugarApp {
    private NavBrokerService mNavBrokerService;
    private String mCurrentUserName;
    private String mCurrentUserPassword;
    private String mUserCompany;
    private String mCurrentSalesPersonCode;
    private String mCurrentDriverCode;
    private String mCurrentModule;
    private String mCurrentUserDisplayName;
    private int mCurrentYear;
    Context mContext;

    @Override public void onCreate() {
        super.onCreate();

        mContext = this;

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        reinitializeRetrofit();

        //add IoniconsModule
        Iconify.with(new IoniconsModule());

        /*
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                Intent intent = new Intent (Intent.ACTION_SEND);
                intent.setType ("plain/text");
                intent.putExtra (Intent.EXTRA_EMAIL, new String[] {"onlineorderchecking@gmail.com"});
                intent.putExtra (Intent.EXTRA_SUBJECT, "MyApp log file");
                intent.putExtra (Intent.EXTRA_TEXT, Log.getStackTraceString(e)); // do this so some email clients don't complain about empty body.
                startActivity (intent);
            }
        });
        */
    }

    // Add by Sajith // 2017-09-07 // enable multidex
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    //To re-initialize  mNavBrokerService after user changes the server endpoint in preference
    public void reinitializeRetrofit()
    {
        SharedPreferences mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPrefHelper mPrefHelper =  new SharedPrefHelper(getResources(), mDefaultSharedPreferences);

        String mServerString = mPrefHelper.getString(R.string.pref_api_server_key).replace(" ", "");
        Log.d("IP ADDRESS", mServerString);

        Retrofit retrofit = null;

        if(mServerString != null && !mServerString.isEmpty())
        {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(mServerString)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client( client)
                    .build();
        }

        if(retrofit != null)
        {
            mNavBrokerService = null;
            mNavBrokerService = retrofit.create(NavBrokerService.class);
        }
    }

    public String getCurrentUserName() {
        return mCurrentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.mCurrentUserName = currentUserName;
    }

    public String getCurrentUserPassword() {
        return mCurrentUserPassword;
    }

    public void setCurrentUserPassword(String currentUserPassword) {
        this.mCurrentUserPassword = currentUserPassword;
    }

    public NavBrokerService getNavBrokerService() {
        return mNavBrokerService;
    }

    public String getmUserCompany() {
        return mUserCompany;
    }

    public void setmUserCompany(String mUserCompany) {
        this.mUserCompany = mUserCompany;
    }

    public String getmCurrentSalesPersonCode() {
        return mCurrentSalesPersonCode;
    }

    public void setmCurrentSalesPersonCode(String mSalesPersonCode) {
        this.mCurrentSalesPersonCode = mSalesPersonCode;
    }

    public String getmCurrentDriverCode() {
        return mCurrentDriverCode;
    }

    public void setmCurrentDriverCode(String mDriverCode) {
        this.mCurrentDriverCode = mDriverCode;
    }

    public String getmCurrentModule() {
        return mCurrentModule;
    }

    public void setmCurrentModule(String mCurrentModule) {
        this.mCurrentModule = mCurrentModule;
    }

    public int getmCurrentYear() {
        return mCurrentYear;
    }

    public void setmCurrentYear(int mCurrentYear) {
        this.mCurrentYear = mCurrentYear;
    }

    public String getCurrentUserDisplayName() {
        return mCurrentUserDisplayName;
    }

    public void setCurrentUserDisplayName(String mCurrentUserDisplayName) {
        this.mCurrentUserDisplayName = mCurrentUserDisplayName;
    }
}
