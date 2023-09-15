package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.UserSetup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nelin_000 on 07/26/2017.
 */

public class UserSetupDbHandler {
    SQLiteDatabase db;
    private Context context;
    private DatabaseHandler dbHelper;

    public UserSetupDbHandler(Context context) {
        this.context = context;
    }

    public UserSetupDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new user setup
    public void addUserSetup(UserSetup user) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_USER_NAME, user.getUserName());
        values.put(dbHelper.KEY_USER_PASSWORD, user.getPassword());
        values.put(dbHelper.KEY_USER_ENABLE_MOBILE, user.isEnableMobile());
        values.put(dbHelper.KEY_USER_ENABLE_DELIVERY, user.isEnableDelivery());
        values.put(dbHelper.KEY_USER_ENABLE_VANSALE, user.isEnableVanSale());
        values.put(dbHelper.KEY_USER_ENABLE_MOBILE_SALE, user.isEnableMobileSale());
        values.put(dbHelper.KEY_USER_DRIVER_CODE, user.getDriverCode());
        values.put(dbHelper.KEY_USER_SALES_PERSON_CODE, user.getSalesPersonCode());
        values.put(dbHelper.KEY_USER_SO_RUNNING_NO_MSO, user.getSoRunningNoMso());
        values.put(dbHelper.KEY_USER_SO_RUNNING_NO_MVS, user.getSoRunningNoMvs());
        values.put(dbHelper.KEY_USER_SI_RUNNING_NO, user.getSiRunningNo());
        values.put(dbHelper.KEY_USER_PAYMENT_RUNNING_NO, user.getPaymentRunningNo());
        values.put(dbHelper.KEY_USER_ENABLE_LDS, user.isEnableLDS());
        values.put(dbHelper.KEY_USER_IS_INITIAL_SYNC_RUN, user.isInitialSyncRun());
        values.put(dbHelper.KEY_USER_TRANSFER_IN_RUNNING_NO,user.getTransferInNumber());
        values.put(dbHelper.KEY_USER_TRANSFER_OUT_RUNNING_NO,user.getTransferOutNumber());
        values.put(dbHelper.KEY_USER_SR_RUNNING_NO_MVS, user.getSrRunningNoMvs());
        values.put(dbHelper.KEY_USER_DISPLAY_NAME, user.getUserDisplayName());

        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_USER, null, values);
    }

    public boolean isUserExist(String userName) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_USER + " WHERE " + dbHelper.KEY_USER_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userName});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    // Getting User
    public UserSetup getUserSetUp(String userName) {

        List<UserSetup> userList = new ArrayList<UserSetup>();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_USER + " WHERE " + dbHelper.KEY_USER_NAME + " = ?";
        Cursor c = db.rawQuery(query, new String[]{userName});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                UserSetup user = new UserSetup();
                user.setId(Integer.parseInt(c.getString(c.getColumnIndex(dbHelper.KEY_USER_ID))));
                user.setUserName(c.getString(c.getColumnIndex(dbHelper.KEY_USER_NAME)));
                user.setPassword(c.getString(c.getColumnIndex(dbHelper.KEY_USER_PASSWORD)));
                user.setEnableMobile(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_ENABLE_MOBILE)) > 0);
                user.setEnableDelivery(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_ENABLE_DELIVERY)) > 0);
                user.setEnableVanSale(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_ENABLE_VANSALE)) > 0);
                user.setEnableMobileSale(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_ENABLE_MOBILE_SALE)) > 0);
                user.setEnableTransferRequestIn(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_ENABLE_TRANS_REQ_IN)) > 0);
                user.setEnableTransferRequestOut(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_ENABLE_TRANS_REQ_OUT)) >0);
                //user.setEnablePaymentCollection(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_ENABLE_PAYMENT)) > 0);
                user.setEnableItems(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_ENABLE_ITEM)) > 0);
                user.setEnableCustomer(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_ENABLE_CUSTOMER)) > 0);
                user.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_USER_DRIVER_CODE)));
                user.setSalesPersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_USER_SALES_PERSON_CODE)));
                user.setSoRunningNoMso(c.getString(c.getColumnIndex(dbHelper.KEY_USER_SO_RUNNING_NO_MSO)));
                user.setSoRunningNoMvs(c.getString(c.getColumnIndex(dbHelper.KEY_USER_SO_RUNNING_NO_MVS)));
                user.setSiRunningNo(c.getString(c.getColumnIndex(dbHelper.KEY_USER_SI_RUNNING_NO)));
                user.setPaymentRunningNo(c.getString(c.getColumnIndex(dbHelper.KEY_USER_PAYMENT_RUNNING_NO)));
                user.setInitialSyncRun(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_IS_INITIAL_SYNC_RUN)) > 0);
                user.setEnableLDS(c.getInt(c.getColumnIndex(dbHelper.KEY_USER_ENABLE_LDS)) > 0);
                user.setTransferInNumber(c.getString(c.getColumnIndex(dbHelper.KEY_USER_TRANSFER_IN_RUNNING_NO)));
                user.setTransferOutNumber(c.getString(c.getColumnIndex(dbHelper.KEY_USER_TRANSFER_OUT_RUNNING_NO)));
                user.setSrRunningNoMvs(c.getString(c.getColumnIndex(dbHelper.KEY_USER_SR_RUNNING_NO_MVS)));
                user.setUserDisplayName(c.getString(c.getColumnIndex(dbHelper.KEY_USER_DISPLAY_NAME)));

                userList.add(user);
            } while (c.moveToNext());
        }

        c.close();
        if (userList.size() > 0) {
            return userList.get(0);
        } else {
            return new UserSetup();
        }

    }

    public boolean deleteUser(String userNmae) {
        boolean success = false;
        if (isUserExist(userNmae)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_USER, dbHelper.KEY_USER_NAME + "=?", new String[]{String.valueOf(userNmae)});
            success = !isUserExist(userNmae);
        } else {
            success = true;
        }
        return success;
    }

    public boolean authenticateOfflineUser(String username, String password) {
        boolean isAuthenticated = false;

        UserSetup user = getUserSetUp(username);
        if (user.getUserName() != null) {
            String savedPassword = user.getPassword();
            if (savedPassword.compareTo(password) == 0) {
                isAuthenticated = true;
            } else isAuthenticated = false;
        }
        return isAuthenticated;
    }

    public String getLatestSoRunningNo(String username,String deliveytDate) {

        String latestSoNo = "";
        UserSetup user = getUserSetUp(username);

        //get sales person code eg:"S09"
        String salesPersonCode = user.getSalesPersonCode();
        int salesPersonNo = Integer.parseInt(salesPersonCode.substring(1, salesPersonCode.length()).replaceFirst
                ("^0+(?!$)", ""));
        String shortedSalesPersonCode = "" + salesPersonCode.substring(0, 1) + String.format("%02d", salesPersonNo);

        //get Year eg:"17"
        //Calendar c = Calendar.getInstance();
        //String year = "" + (c.get(Calendar.YEAR) % 100);

        //get Year from delivery date eg:"17"
        String year = deliveytDate.substring(deliveytDate.length()-2,deliveytDate.length());

        //get saved so running no
        if (!((user.getSoRunningNoMso() == null) || (user.getSoRunningNoMso().equals("")))) {
            latestSoNo = user.getSoRunningNoMso();
        }

        if (latestSoNo.equals("")) {

            latestSoNo = "00001";
            //add prefixes
            latestSoNo = shortedSalesPersonCode + year + "-" + latestSoNo;

        } else {

            int currentNo = Integer.parseInt(latestSoNo.replaceFirst("^0+(?!$)", ""));
            int nextNo = currentNo + 1;
            latestSoNo = String.format("%05d", nextNo);
            //add prefixes
            latestSoNo = shortedSalesPersonCode + year + "-" + latestSoNo;
        }

        return latestSoNo;
    }

    public String getLatestSoRunningNoMVS(String username,String deliveytDate) {

        String latestSoNo = "";
        UserSetup user = getUserSetUp(username);

        //get driver code eg:"V09"
        String salesPersonCode = user.getDriverCode();
        int driverNo = Integer.parseInt(salesPersonCode.substring(1, salesPersonCode.length()).replaceFirst
                ("^0+(?!$)", ""));
        String shortedDriverCode = "S" + salesPersonCode.substring(0, 1) + String.format("%02d", driverNo);

        //get Year eg:"17"
        //Calendar c = Calendar.getInstance();
        //String year = "" + (c.get(Calendar.YEAR) % 100);

        //get Year from delivery date eg:"17"
        String year = deliveytDate.substring(deliveytDate.length()-2,deliveytDate.length());

        //get saved so running no
        if (!((user.getSoRunningNoMvs() == null) || (user.getSoRunningNoMvs().equals("")))) {
            latestSoNo = user.getSoRunningNoMvs();
        }

        if (latestSoNo.equals("")) {

            latestSoNo = "00001";
            //add prefixes
            latestSoNo = shortedDriverCode + year + "-" + latestSoNo;

        } else {

            int currentNo = Integer.parseInt(latestSoNo.replaceFirst("^0+(?!$)", ""));
            int nextNo = currentNo + 1;
            latestSoNo = String.format("%05d", nextNo);
            //add prefixes
            latestSoNo = shortedDriverCode + year + "-" + latestSoNo;
        }

        return latestSoNo;
    }

    public boolean setLatestSoRunningNo(String latestSoNo, String username) {

        boolean isSaveSuccess = false;
        UserSetup user = getUserSetUp(username);

        String strSaveRunningNo = latestSoNo.substring(latestSoNo.lastIndexOf("-") + 1);

        user.setSoRunningNoMso(strSaveRunningNo);

        if (deleteUser(username)) {
            addUserSetup(user);
            isSaveSuccess = true;
        }

        return isSaveSuccess;
    }

    /*
    * username = mapp.getCurrentUsername();
    * usercode =    (MSO) - mApp.getmCurrentSalesPersonCode()
    *               (MVS) - mApp.getmCurrentDriverCode()
    *               (LDS) - mApp.getmCurrentDriverCode()
    */
    public String getLatestPaymentRunningNo(String username, String userCode) {

        String latestPaymentNo = "";
        UserSetup user = getUserSetUp(username);

        //get sales person code eg:"S09"
        //String salesPersonCode = user.getSalesPersonCode();
        int userNo = Integer.parseInt(userCode.substring(1, userCode.length()).replaceFirst
                ("^0+(?!$)", ""));
        String shortedUserCode = "" + userCode.substring(0, 1) + String.format("%02d", userNo);

        //get Year eg:"17"
        Calendar c = Calendar.getInstance();
        String year = "" + (c.get(Calendar.YEAR) % 100);

        //get saved so running no
        if (!((user.getPaymentRunningNo() == null) || (user.getPaymentRunningNo().equals("")))) {
            latestPaymentNo = user.getPaymentRunningNo();
        }

        if (latestPaymentNo.equals("")) {
            latestPaymentNo = "00001";
        } else {
            int currentNo = Integer.parseInt(latestPaymentNo.replaceFirst("^0+(?!$)", ""));
            int nextNo = currentNo + 1;
            latestPaymentNo = String.format("%05d", nextNo);

        }

        //add prefixes ("PaymentCode" + "salespersonCode"  + "year YY" + "-" + "a_runningNo")
        latestPaymentNo = "P" + shortedUserCode + year + "-" + latestPaymentNo;

        return latestPaymentNo;
    }

    public boolean setLatestPaymentRunningNo(String latestPaymentNo, String username) {

        boolean isSaveSuccess = false;
        UserSetup user = getUserSetUp(username);

        String strSaveRunningNo = latestPaymentNo.substring(latestPaymentNo.lastIndexOf("-") + 1);

        user.setPaymentRunningNo(strSaveRunningNo);

        if (deleteUser(username)) {
            addUserSetup(user);
            isSaveSuccess = true;
        }

        return isSaveSuccess;
    }

    public String getLatestStockTransferRunningNo(String userName, String formName) {

        String latestTransferNo = "",prefixes="";
        UserSetup user = getUserSetUp(userName);

        //get sales person code eg:"S09"
        String driverCode = user.getDriverCode();
        int driverCodeNo = Integer.parseInt(driverCode.substring(1, driverCode.length()).replaceFirst
                ("^0+(?!$)", ""));
        String shortedDriverCode = "" + driverCode.substring(0, 1) + String.format("%02d", driverCodeNo);

        //get Year eg:"17"
        Calendar c = Calendar.getInstance();
        String year = "" + (c.get(Calendar.YEAR) % 100);

        if(formName.equals(context.getResources().getString(R.string.form_name_mvs_stock_transfer_in)))
        {
            if (!((user.getTransferInNumber() == null) || (user.getTransferInNumber().equals("")))) {
                latestTransferNo = user.getTransferInNumber();
            }
            prefixes = context.getResources().getString(R.string.stock_transfer_in_prefixes);
        }
        else if(formName.equals(context.getResources().getString(R.string.form_name_mvs_stock_transfer_out)))
        {
            if (!((user.getTransferOutNumber() == null) || (user.getTransferOutNumber().equals(""))))
            {
                latestTransferNo = user.getTransferOutNumber();
            }
            prefixes = context.getResources().getString(R.string.stock_transfer_out_prefixes);
        }
        if (latestTransferNo.equals("")) {

            latestTransferNo = "00001";
            //add prefixes
            latestTransferNo = prefixes+shortedDriverCode + year + "-" + latestTransferNo;

        } else {

            int currentNo = Integer.parseInt(latestTransferNo.replaceFirst("^0+(?!$)", ""));
            int nextNo = currentNo + 1;
            latestTransferNo = String.format("%05d", nextNo);
            //add prefixes
            latestTransferNo = prefixes+shortedDriverCode + year + "-" + latestTransferNo;
        }

        return latestTransferNo;
    }

    public boolean setLatestStockTransferRunningNo(String latestSTRNo,String userName,String formName) {

        boolean isSaveSuccess = false;
        UserSetup user = getUserSetUp(userName);

        String strSaveRunningNo = latestSTRNo.substring(latestSTRNo.lastIndexOf("-") + 1);

        if(formName.equals(context.getResources().getString(R.string.form_name_mvs_stock_transfer_in)))
        {
            user.setTransferInNumber(strSaveRunningNo);
        }
        else if(formName.equals(context.getResources().getString(R.string.form_name_mvs_stock_transfer_out)))
        {
            user.setTransferOutNumber(strSaveRunningNo);
        }

        if (deleteUser(userName)) {
            addUserSetup(user);
            isSaveSuccess = true;
        }

        return isSaveSuccess;
    }

    public String getLatestSrRunningNo(String username) {

        String latestSrNo = "";
        UserSetup user = getUserSetUp(username);
        String prefixes = context.getResources().getString(R.string.stock_request_prefixes);

        //get Driver code eg:"S09"
        String driverCode = user.getDriverCode();
        int driverNo = Integer.parseInt(driverCode.substring(1, driverCode.length()).replaceFirst
                ("^0+(?!$)", ""));
        String shortedDriverCode = "" + String.format("%02d", driverNo);

        //get Year eg:"17"
        Calendar c = Calendar.getInstance();
        String year = "" + (c.get(Calendar.YEAR) % 100);

        //get saved so running no
        if (!((user.getSrRunningNoMvs() == null) || (user.getSrRunningNoMvs().equals("")))) {
            latestSrNo = user.getSrRunningNoMvs();
        }

        if (latestSrNo.equals("")) {

            latestSrNo = "00001";
            //add prefixes
            latestSrNo = prefixes + shortedDriverCode + year + "-" + latestSrNo;

        } else {

            int currentNo = Integer.parseInt(latestSrNo.replaceFirst("^0+(?!$)", ""));
            int nextNo = currentNo + 1;
            latestSrNo = String.format("%05d", nextNo);
            //add prefixes
            latestSrNo = prefixes + shortedDriverCode + year + "-" + latestSrNo;
        }

        return latestSrNo;
    }

    public boolean setLatestSrRunningNo(String latestSrNo, String username) {

        boolean isSaveSuccess = false;
        UserSetup user = getUserSetUp(username);

        String strSaveRunningNo = latestSrNo.substring(latestSrNo.lastIndexOf("-") + 1);

        user.setSrRunningNoMvs(strSaveRunningNo);

        if (deleteUser(username)) {
            addUserSetup(user);
            isSaveSuccess = true;
        }

        return isSaveSuccess;
    }

    public boolean setLatestMvsSoRunningNo(String latestSoNo, String username) {

        boolean isSaveSuccess = false;
        UserSetup user = getUserSetUp(username);

        String strSaveRunningNo = latestSoNo.substring(latestSoNo.lastIndexOf("-") + 1);

        user.setSoRunningNoMvs(strSaveRunningNo);

        if (deleteUser(username)) {
            addUserSetup(user);
            isSaveSuccess = true;
        }

        return isSaveSuccess;
    }

    public String convertToInvoice( String soNo, String siNo_, String username) {

        String siNo = "";
        String latestSiNo = "";
        try
        {
            if(siNo_== null) {

                UserSetup user = getUserSetUp(username);

                //get driver code eg:"V09"
                String driverCode = user.getDriverCode();
                int driverNo = Integer.parseInt(driverCode.substring(1, driverCode.length()).replaceFirst
                        ("^0+(?!$)", ""));
                String shortedDriverCode = "" + driverCode.substring(0, 1) + String.format("%02d", driverNo);

                //get Year eg:"17"
                Calendar c = Calendar.getInstance();
                String year = "" + (c.get(Calendar.YEAR) % 100);

                //get saved si running no
                if (!((user.getSiRunningNo() == null) || (user.getSiRunningNo().equals("")))) {
                    latestSiNo = user.getSiRunningNo();
                }

                if (latestSiNo.equals("")) {

                    latestSiNo = "00001";
                    //add prefixes
                    latestSiNo = shortedDriverCode + year + "-" + latestSiNo;

                } else {

                    int currentNo = Integer.parseInt(latestSiNo.replaceFirst("^0+(?!$)", ""));
                    int nextNo = currentNo + 1;
                    latestSiNo = String.format("%05d", nextNo);
                    //add prefixes
                    latestSiNo = shortedDriverCode + year + "-" + latestSiNo;
                    siNo = latestSiNo;
                }
            }
            else
            {
                String value = new String(siNo_);

                String[] parts = value.split("-");

                if (parts.length == 2) {
                    siNo = parts[0] + "-" + parts[1] + "-1";
                } else if (parts.length == 3) {
                    String mm = parts[2];

                    String no_ = mm.replaceAll("[^0-9]", "");
                    int no = Integer.parseInt(no_) + 1;

                    String newSiNo = parts[0] + "-" + parts[1] + "-" + no;
                    siNo = newSiNo;
                } else {
                    siNo = "";
                }
            }

            SalesOrderDbHandler db=new SalesOrderDbHandler(context);
            db.open();

            db.updateSalesInvoiceNo(soNo, siNo,false);
            db.close();

            if(siNo_== null)
            {
                String value = new String(latestSiNo);
                String[] parts = value.split("-");
                String uploadSiNo = parts[1];

                UserSetup user = getUserSetUp(username);
                user.setSiRunningNo(uploadSiNo);

                if (deleteUser(username)) {
                    addUserSetup(user);
                }
            }
        }
        catch (Exception ee)
        {
            return "";
        }
        return siNo;
    }

}

