package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.UserSetup;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.gui.mdt.thongsieknavclient.model.AuthenticateUserParameter;
import com.gui.mdt.thongsieknavclient.model.AuthorizedModuleResult;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by nelin_000 on 07/19/2017.
 */

public class UserSetupSync {
    AuthorizedModuleResult authorizedModuleResult;
    Context context;
    private UserSetupSync.GetUserSetupTask getUserTask = null;
    private NavClientApp mApp;

    public UserSetupSync(Context context) {

        this.context = context;
        mApp = (NavClientApp) context;

        getUserTask = new UserSetupSync.GetUserSetupTask();
        getUserTask.execute((Void) null);

    }

    public class GetUserSetupTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<AuthorizedModuleResult> call = mApp.getNavBrokerService()
                        .GetAuthorizedModules(
                                new AuthenticateUserParameter(
                                mApp.getmUserCompany(),
                                mApp.getCurrentUserName(),
                                mApp.getCurrentUserPassword()));

                authorizedModuleResult = call.execute().body();

            } catch (IOException e) {
                Log.d("NAV_Client_Exception", e.toString());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            UserSetupDbHandler dbAdapter = new UserSetupDbHandler(context);
            dbAdapter.open();

            if (success) {
                try {
                    if (dbAdapter.deleteUser("muser02")) {

                        UserSetup user = new UserSetup();
                        user.setUserName("muser02");
                        user.setPassword("a");
                        user.setEnableMobile(authorizedModuleResult.isEnableMobile());
                        user.setEnableDelivery(authorizedModuleResult.isEnableDelivery());
                        user.setEnableVanSale(authorizedModuleResult.isEnableVanSale());
                        user.setEnableMobileSale(authorizedModuleResult.isEnableMobileSale());
                        user.setEnableTransferRequestOut(authorizedModuleResult.isEnableTransferRequestOut());
                        user.setEnableTransferRequestIn(authorizedModuleResult.isEnableTransferRequestIn());
                        user.setEnablePaymentCollection(authorizedModuleResult.isEnablePaymentCollection());
                        user.setEnableItems(authorizedModuleResult.isEnableItems());
                        user.setEnableCustomer(authorizedModuleResult.isEnableCustomer());

                        dbAdapter.addUserSetup(user);
                        Log.d("SYNC_USER_SETUP_ADDED: ", user.getUserName());
                    }
                    dbAdapter.close();

                } catch (Exception ex) {
                    //NotificationManager.DisplayAlertDialog(context, NotificationManager.MSG_ERROR, ex.getMessage());
                }
            } else {
                NotificationManager.DisplayAlertDialog(context, NotificationManager.MSG_ERROR, context.getString(R.string.notification_msg_server_no_response));
            }

            dbAdapter.close();
        }

        @Override
        protected void onCancelled() {

        }

    }

}