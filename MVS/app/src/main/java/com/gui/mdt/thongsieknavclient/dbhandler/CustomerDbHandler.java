package com.gui.mdt.thongsieknavclient.dbhandler;

/**
 * Created by nelin_000 on 07/19/2017.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerListParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerListResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesOrderHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesOrderResult;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.model.SalesCustomerArParameter;
import com.gui.mdt.thongsieknavclient.model.SalesCustomerArResponse;

import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class CustomerDbHandler {

    SQLiteDatabase db;
    SalesCustomerArParameter salesCustomerArParameter;
    SalesCustomerArResponse salesCustomerArResponse;
    List<SalesCustomerArResponse.SalesCustomerAr> arList;
    ApiSalesOrderHeaderParameter apiSalesOrderHeaderParameter;
    ApiSalesOrderResult apiSalesOrderResult;
    private Context context;
    private DatabaseHandler dbHelper;
    private NavClientApp mApp;
    private String mArCustomerCode;
    private String mInvCustomerCode;
    private Customer mCustomerBalanceLcy;

    public CustomerDbHandler(Context context) {
        this.context = context;
    }

    public CustomerDbHandler open() throws SQLException {
        dbHelper = new DatabaseHandler(context);
        this.mApp = (NavClientApp) context.getApplicationContext();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new customer
    public void addCustomer(Customer customer) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.KEY_CUS_NAME, customer.getName()); // customer Name
        values.put(dbHelper.KEY_CUS_PH_NO, customer.getPhoneNo()); // customer Phone Number
        values.put(dbHelper.KEY_CUS_CONTACT, customer.getContact()); // customer contact no
        values.put(dbHelper.KEY_CUS_CODE, customer.getCode()); // customer Code
        values.put(dbHelper.KEY_CUS_ADDRESS, customer.getAddress()); // customer address
        values.put(dbHelper.KEY_CUS_POSTAL_CODE, customer.getPostalCode());

        values.put(dbHelper.KEY_CUS_DRIVER_CODE, customer.getDriverCode());
        values.put(dbHelper.KEY_CUS_SALESPERSON_CODE, customer.getSalespersonCode());
        values.put(dbHelper.KEY_CUS_MINIMUM_SALES_AMOUNT, customer.getMinimumSalesAmount());
        values.put(dbHelper.KEY_CUS_BALANCE, customer.getBalance());

        values.put(dbHelper.KEY_CUS_CREDIT_LIMIT, customer.getCreditLimit());
        values.put(dbHelper.KEY_CUS_DUE_DATE_GRACE_PERIOD, customer.getDueDateGracePeriod());
        values.put(dbHelper.KEY_CUS_IS_BLOCKED, customer.isBlocked());
        values.put(dbHelper.KEY_CUS_EMAIL, customer.getEmail());
        values.put(dbHelper.KEY_CUS_CUSTOMER_PRICE_GROUP, customer.getCustomerPriceGroup());
        values.put(dbHelper.KEY_CUS_VAT_BUS_GROUP, customer.getVatBusPostingGroup());
        values.put(dbHelper.KEY_CUS_BILL_TO_NO, customer.getBillToCustomerNo());
        values.put(dbHelper.KEY_CUS_PAYMENT_TERMS, customer.getPaymentTerms());
        values.put(dbHelper.KEY_CUS_BILL_TO_NAME, customer.getBillToCustomerName());
        values.put(dbHelper.KEY_CUS_REF_NO, customer.getCustomerReferenceNo());
        values.put(dbHelper.KEY_CUS_NTUC_STORE_CODE, customer.getNtucBuyerCode());


        // Inserting Row
        db = dbHelper.getWritableDatabase();
        db.insert(dbHelper.TABLE_CUSTOMER, null, values);
    }

    public boolean isCustomerExist(String code) {

        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + dbHelper.TABLE_CUSTOMER + " WHERE " + dbHelper.KEY_CUS_CODE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{code});
        int count = cursor.getCount();
        cursor.close();
        return count > 0 ? true : false;
    }

    // Getting All Customers
    public List<Customer> getAllCustomer() {

        List<Customer> customerList = new ArrayList<Customer>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_CUSTOMER + " WHERE " + dbHelper.KEY_CUS_IS_BLOCKED +
                " = ?";
        Cursor c = db.rawQuery(selectQuery, new String[]{"0"});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Customer customer = new Customer();
                customer = getCustomerFromCursor(customer, c);
                customerList.add(customer);
            } while (c.moveToNext());
        }

        c.close();
        return customerList;
    }

    public List<Customer> getAllCustomer(Customer customerObj) {

        List<Customer> customerList = new ArrayList<Customer>();
        db = dbHelper.getReadableDatabase();
        String _filterCustomerName = customerObj.getName().isEmpty() ? "" : customerObj.getName();
        String _filterCustomerCode = customerObj.getCode().isEmpty() ? "" : customerObj.getCode();
        String _filterCustomerPriceGroup = customerObj.getCustomerPriceGroup().isEmpty() ? "" : customerObj
                .getCustomerPriceGroup();
        String _filterSalespersonCode = customerObj.getSalespersonCode().isEmpty() ? "" : customerObj
                .getSalespersonCode();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_CUSTOMER + " WHERE " + dbHelper
                .KEY_CUS_SALESPERSON_CODE + " = ? AND "
                + dbHelper.KEY_CUS_NAME + " LIKE ? AND "
                + dbHelper.KEY_CUS_CODE + " LIKE ? AND "
                + dbHelper.KEY_CUS_CUSTOMER_PRICE_GROUP + " LIKE ?"
                + " ORDER BY " + dbHelper.KEY_CUS_NAME;

        //Cursor c = db.rawQuery(selectQuery, null);
        Cursor c = db.rawQuery(selectQuery, new String[]{_filterSalespersonCode, "%" + _filterCustomerName + "%", "%"
                + _filterCustomerCode + "%", "%" + _filterCustomerPriceGroup + "%"});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Customer customer = new Customer();

                customer = getCustomerFromCursor(customer, c);

                customerList.add(customer);
            } while (c.moveToNext());
        }

        c.close();
        return customerList;
    }

    public List<Customer> getAllCustomerListForLdsMvs(Customer customerObj) {

        List<Customer> customerList = new ArrayList<Customer>();
        db = dbHelper.getReadableDatabase();
        String _filterCustomerName = customerObj.getName().isEmpty() ? "" : customerObj.getName();
        String _filterCustomerCode = customerObj.getCode().isEmpty() ? "" : customerObj.getCode();
        String _filterCustomerPriceGroup = customerObj.getCustomerPriceGroup().isEmpty() ? "" : customerObj
                .getCustomerPriceGroup();
        String _filterCustomerDriverCode = customerObj.getDriverCode().isEmpty() ? "" : customerObj.getDriverCode();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_CUSTOMER + " WHERE "
                + dbHelper.KEY_CUS_NAME + " LIKE ? AND "
                + dbHelper.KEY_CUS_CODE + " LIKE ? AND "
                + dbHelper.KEY_CUS_CUSTOMER_PRICE_GROUP + " LIKE ? AND "
                + dbHelper.KEY_CUS_DRIVER_CODE + " = ? AND "
                + dbHelper.KEY_CUS_IS_BLOCKED + " = ? "
                + " ORDER BY " + dbHelper.KEY_CUS_NAME;

        //Cursor c = db.rawQuery(selectQuery, null);
        Cursor c = db.rawQuery(selectQuery, new String[]{"%" + _filterCustomerName + "%", "%" + _filterCustomerCode +
                "%", "%" + _filterCustomerPriceGroup + "%", _filterCustomerDriverCode, "0"});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Customer customer = new Customer();

                customer = getCustomerFromCursor(customer, c);

                customerList.add(customer);
            } while (c.moveToNext());
        }

        c.close();
        return customerList;
    }

    // Getting customer Count
    public int getCustomersCount() {
        db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + dbHelper.TABLE_CUSTOMER;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public boolean deleteCustomer(String code) {
        boolean success = false;

        if (isCustomerExist(code)) {
            db = dbHelper.getWritableDatabase();
            db.delete(dbHelper.TABLE_CUSTOMER, dbHelper.KEY_CUS_CODE + "=?", new String[]{String.valueOf(code)});
            success = !isCustomerExist(code);
        } else {
            success = true;
        }
        return success;
    }

    public boolean deleteAllCustomer() {

        db = dbHelper.getWritableDatabase();
        int result = db.delete(dbHelper.TABLE_CUSTOMER, null, null);

        return result > 1 ? true : false;
    }


    public List<Customer> getAllCustomerPriceGroups(String salesPersonCode, String driverCode) {

        String filterSalesPersonCode = "", filterDriverCode = "", joinQuery = "";

        filterSalesPersonCode = salesPersonCode == null ? "" : salesPersonCode;
        filterDriverCode = driverCode == null ? "" : driverCode;

        if (!filterSalesPersonCode.equals("") && filterDriverCode.equals("")) {
            joinQuery = " AND " + dbHelper.KEY_CUS_SALESPERSON_CODE + " = '" + filterSalesPersonCode + "'";
        }

        if (filterSalesPersonCode.equals("") && !filterDriverCode.equals("")) {
            joinQuery = " AND " + dbHelper.KEY_CUS_DRIVER_CODE + " = '" + filterDriverCode + "'";
        }

        if (!filterDriverCode.equals("") && !filterSalesPersonCode.equals("")) {
            joinQuery = " AND " + dbHelper.KEY_CUS_SALESPERSON_CODE + " = '" + filterSalesPersonCode + "'"
                    + " AND " + dbHelper.KEY_CUS_DRIVER_CODE + " = '" + filterDriverCode + "'";
        }

        List<Customer> customerPriceGroupList = new ArrayList<Customer>();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  DISTINCT " + dbHelper.KEY_CUS_CUSTOMER_PRICE_GROUP
                + " FROM " + dbHelper.TABLE_CUSTOMER
                + " WHERE " + dbHelper.KEY_CUS_IS_BLOCKED + " = '0'"
                + joinQuery
                + " ORDER BY " + dbHelper.KEY_CUS_CUSTOMER_PRICE_GROUP;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Customer priceGroup = new Customer();
                priceGroup.setCustomerPriceGroup(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_CUSTOMER_PRICE_GROUP)));

                customerPriceGroupList.add(priceGroup);
            } while (c.moveToNext());
        }

        c.close();
        return customerPriceGroupList;
    }

    public Customer getCustomerByCustomerCode(String CustomerCode) {
        db = dbHelper.getReadableDatabase();
        Customer customer = new Customer();
        if (isCustomerExist(CustomerCode)) {
            String query = "SELECT * FROM " + dbHelper.TABLE_CUSTOMER + " WHERE " + dbHelper.KEY_CUS_CODE + " = ?";
            Cursor c = db.rawQuery(query, new String[]{CustomerCode});

            c.moveToFirst();

            customer = getCustomerFromCursor(customer, c);

            c.close();
            return customer;
        }
        return customer;
    }

    public List<Customer> getAllCustomerForMVSStockRequest(Customer searchCustomerObj) {

        List<Customer> customerList = new ArrayList<Customer>();
        db = dbHelper.getReadableDatabase();

        String _filterCustomerName = searchCustomerObj.getName().isEmpty() ? "" : searchCustomerObj.getName();
        String _filterCustomerCode = searchCustomerObj.getCode().isEmpty() ? "" : searchCustomerObj.getCode();
        String _filterCustomerPriceGroup = searchCustomerObj.getCustomerPriceGroup().isEmpty() ? "" :
                searchCustomerObj.getCustomerPriceGroup();
        String _filterDriverCode = searchCustomerObj.getDriverCode().isEmpty() ? "" : searchCustomerObj.getDriverCode();
        String customerNotBlockedStatus
                = context.getResources().getString(R.string.customerNotBlocked);

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_CUSTOMER + " WHERE "
                + dbHelper.KEY_CUS_NAME + " LIKE ? AND "
                + dbHelper.KEY_CUS_CODE + " LIKE ? AND "
                + dbHelper.KEY_CUS_CUSTOMER_PRICE_GROUP + " LIKE ? AND "
                + dbHelper.KEY_CUS_DRIVER_CODE + " = ? AND "
                + dbHelper.KEY_CUS_IS_BLOCKED + " = " + customerNotBlockedStatus
                + " ORDER BY " + dbHelper.KEY_CUS_NAME;

        Cursor c = db.rawQuery(selectQuery, new String[]{"%" + _filterCustomerName + "%",
                "%" + _filterCustomerCode + "%",
                "%" + _filterCustomerPriceGroup + "%",
                _filterDriverCode});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Customer customer = new Customer();

                customer = getCustomerFromCursor(customer, c);

                customerList.add(customer);
            } while (c.moveToNext());
        }
        c.close();
        return customerList;
    }

    private Customer getCustomerFromCursor(Customer customer, Cursor c) {
        //customer.setKey(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_KEY)));
        customer.setName(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_NAME)));
        customer.setPhoneNo(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_PH_NO)));
        customer.setContact(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_CONTACT)));
        customer.setCode(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_CODE)));
        customer.setAddress(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_ADDRESS)));
        customer.setDriverCode(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_DRIVER_CODE)));
        customer.setSalespersonCode(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_SALESPERSON_CODE)));
        customer.setMinimumSalesAmount(c.getDouble(c.getColumnIndex(dbHelper.KEY_CUS_MINIMUM_SALES_AMOUNT)));
        customer.setBalance(c.getDouble(c.getColumnIndex(dbHelper.KEY_CUS_BALANCE)));
        customer.setCreditLimit(c.getDouble(c.getColumnIndex(dbHelper.KEY_CUS_CREDIT_LIMIT)));
        customer.setDueDateGracePeriod(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_DUE_DATE_GRACE_PERIOD)));
        customer.setPaymentTerms(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_PAYMENT_TERMS)));
        customer.setBlocked(c.getInt(c.getColumnIndex(dbHelper.KEY_CUS_IS_BLOCKED)) > 0);
        customer.setEmail(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_EMAIL)));
        customer.setCustomerPriceGroup(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_CUSTOMER_PRICE_GROUP)));
        customer.setVatBusPostingGroup(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_VAT_BUS_GROUP)));
        customer.setPostalCode(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_POSTAL_CODE)));
        customer.setBillToCustomerNo(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_BILL_TO_NO)));
        customer.setBillToCustomerName(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_BILL_TO_NAME)));
        customer.setCustomerReferenceNo(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_REF_NO)));
        customer.setNtucBuyerCode(c.getString(c.getColumnIndex(dbHelper.KEY_CUS_NTUC_STORE_CODE)));
        return customer;
    }

    // if false; "Credit limit exceeded. Not allowed to save.
    public boolean validateCustomerCreditLimit(String customerCode, double invoiceValue, String invoiceNo) {

        boolean isValid = false;
        this.mInvCustomerCode = customerCode;
        Customer customer = getCustomerByCustomerCode(customerCode);
        double creditLimit = customer.getCreditLimit();
        double postedOutstanding = getCustomerBalance(customerCode);
        double unpostedOutstanding = 0;
        double mobileInvoiceOutstanding = 0;

        //if no credit limit set, all invoice will allow to create.
        if (creditLimit <= 0) {
            isValid = true;
            return isValid;
        }

        // get unposted invoice balance from nav.
        if (isNetworkConnected()) {
            try {

                GetCustomersUnpostedInvoiceBalanceTask task = new GetCustomersUnpostedInvoiceBalanceTask();
                task.execute().get();
                if (apiSalesOrderResult != null && apiSalesOrderResult.getSalesOrderListResultData()
                        .size() > 0) {

                    for (ApiSalesOrderResult.ApiSalesOrderListResultData so :
                            apiSalesOrderResult.getSalesOrderListResultData()) {
                        unpostedOutstanding = unpostedOutstanding +
                                Double.parseDouble(String.format("%.2f", so.getAmount_Including_VAT()));

                    }

                }

            } catch (Exception ex) {
                isValid = false;
                return isValid;
            }
        } else {
            isValid = false;
            return isValid; // when network not available, block adding SO.
            //// TODO: 11/15/2017 confirm with client
        }

        //get mobile created invoice balance
        SalesOrderDbHandler dbHandler = new SalesOrderDbHandler(context);
        dbHandler.open();
        List<SalesOrder> soList = dbHandler.getMobileSalesOrderForCustomer(mApp.getmCurrentSalesPersonCode(),
                mInvCustomerCode);
        if (soList.size() > 0) {
            for (SalesOrder so : soList) {
                if (!so.getNo().equals(invoiceNo)) {
                    mobileInvoiceOutstanding = mobileInvoiceOutstanding +
                            Double.parseDouble(String.format("%.2f", so.getAmountIncludingVAT()));
                }
            }
        }
        dbHandler.close();


        double finalOutstanding = postedOutstanding + unpostedOutstanding + mobileInvoiceOutstanding + invoiceValue;
        if (finalOutstanding > creditLimit) {
            isValid = false;
            return isValid;
        } else {
            isValid = true;
            return isValid;
        }

    }

    //if false; "Overdue invoice found. Not allowed to save.
    public boolean validateCustomerDueDate(String customerCode, boolean needToCheckCusOutStanding) {

        boolean isValid = false;
        this.mArCustomerCode = customerCode;
        //boolean isOverDue=false;

        int dueDateGrace = 0;

        Customer customer = getCustomerByCustomerCode(customerCode);
        String dueGracePeriod = customer.getDueDateGracePeriod();

        //if no grace period for customer, no need to validate.
        if (!(dueGracePeriod == null || dueGracePeriod.equals(""))) {
            dueDateGrace = Integer.parseInt(dueGracePeriod.replaceAll("D", ""));
        } else {
            isValid = true;
            return isValid;
        }

        //if customer has no outstanding, no need to validate.
        if (getCustomerBalance(customerCode) <= 1.00) {
            isValid = true;
            return isValid;
        }

        //if customer has outstanding check for due date
        if (needToCheckCusOutStanding) {
            if (isNetworkConnected()) {
                try {

                    GetCustomersArTask task = new GetCustomersArTask();
                    task.execute().get();

                    if (salesCustomerArResponse != null && salesCustomerArResponse
                            .getCustomerLedgerEntryListResultData()
                            .size() > 0) {
                        arList = salesCustomerArResponse.getCustomerLedgerEntryListResultData();

                        for (SalesCustomerArResponse.SalesCustomerAr ar : arList) {

                            //only consider ar invoices which has a balance of greater than 1$
                            if (Double.parseDouble(ar.getBalance()) >= 1.00) {
                                LocalDate d1 = LocalDate.parse(ar.getDue_Date().split("T")[0]);
                                LocalDate lOverDueDate = d1.plusDays(dueDateGrace);
                                LocalDate lToday = LocalDate.parse(salesCustomerArResponse.getServerDate().split(" ")
                                        [0]);

                                if (lOverDueDate.compareTo(lToday) >= 0) {
                                    isValid = true;
                                } else {
                                    isValid = false;
                                }
                                return isValid;
                            }
                        }

                        //if no due invoice(balance>1$) found in first 20 records. this customer is valid.
                        isValid = true;
                        return isValid;

                    } else {
                        isValid = true;
                        return isValid;
                    }

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                    isValid = false;
                }
            } else {
                isValid = false;
                return isValid; // when network not available, block confirm SO.
                //// TODO: 11/15/2017 confirm with client
            }
        } else {
            isValid = true;
            return isValid;
        }


        return isValid;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public double getCustomerBalance(String cusCode) {
        float balance = 0;
        mCustomerBalanceLcy = new Customer();
        mCustomerBalanceLcy.setCode(cusCode);
        if (isNetworkConnected()) {

            try {
                GetCustomerOutstandingTask task = new GetCustomerOutstandingTask();
                task.execute().get();
                return mCustomerBalanceLcy.getBalance();

            } catch (Exception ex) {
                return 0;
            }

        }

        return balance;
    }

    public class GetCustomersArTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                salesCustomerArParameter = new SalesCustomerArParameter();
                salesCustomerArParameter.setUserName(mApp.getCurrentUserName());
                salesCustomerArParameter.setPassword(mApp.getCurrentUserPassword());
                salesCustomerArParameter.setUserCompany(mApp.getmUserCompany());
                salesCustomerArParameter.setInvoiceNo("");
                salesCustomerArParameter.setCustomerNo(mArCustomerCode);
                salesCustomerArParameter.setBookmarkKey("");
                salesCustomerArParameter.setPageSize("20");
                Gson gson = new Gson();
                String json = gson.toJson(salesCustomerArParameter);
                Log.d("AR_PARAMS ", json);

                Call<SalesCustomerArResponse> call = mApp.getNavBrokerService()
                        .GetSalesCustomerAr(salesCustomerArParameter);

                salesCustomerArResponse = call.execute().body();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }
    }

    public class GetCustomersUnpostedInvoiceBalanceTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                apiSalesOrderHeaderParameter = new ApiSalesOrderHeaderParameter();
                apiSalesOrderHeaderParameter.setFilterSalesOrder("");
                apiSalesOrderHeaderParameter.setFilterSalePersonNumber("");
                apiSalesOrderHeaderParameter.setFilterSalePersonNumber("");
                apiSalesOrderHeaderParameter.setFilterDriverCode("");
                apiSalesOrderHeaderParameter.setFilterCustomerNumber(mInvCustomerCode);
                apiSalesOrderHeaderParameter.setUserName(mApp.getCurrentUserName());
                apiSalesOrderHeaderParameter.setPassword(mApp.getCurrentUserPassword());
                apiSalesOrderHeaderParameter.setUserCompany(mApp.getmUserCompany());
                apiSalesOrderHeaderParameter.setFilterLastModifiedDate("");

                Call<ApiSalesOrderResult> call = mApp.getNavBrokerService().GetSalesOrders
                        (apiSalesOrderHeaderParameter);


                apiSalesOrderResult = call.execute().body();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }
    }

    public class GetCustomerOutstandingTask extends AsyncTask<Void, Void, Customer> {

        ApiCustomerListResult apiCustomerListResult;
        ApiCustomerListParameter apiCustomerListParameter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //set parameter for request
            apiCustomerListParameter = new ApiCustomerListParameter();
            apiCustomerListParameter.setFilterCustomerCode(mCustomerBalanceLcy.getCode());
            apiCustomerListParameter.setFilterCustomerName("");
            apiCustomerListParameter.setUserCompany(mApp.getmUserCompany());
            apiCustomerListParameter.setUserName(mApp.getCurrentUserName());
            apiCustomerListParameter.setPassword(mApp.getCurrentUserPassword());
            apiCustomerListParameter.setFilterDriverCode(mApp.getmCurrentDriverCode());
            apiCustomerListParameter.setFilterSalesPersonCode(mApp.getmCurrentSalesPersonCode());
            apiCustomerListParameter.setFilterLastModifiedDate("");

        }

        @Override
        protected Customer doInBackground(Void... params) {

            Customer customer = new Customer();
            try {

                //if(isNetworkAvailable()) {
                Call<ApiCustomerListResult> call = mApp.getNavBrokerService()
                        .GetCustomers(apiCustomerListParameter);
                apiCustomerListResult = call.execute().body();

                if (apiCustomerListResult != null && apiCustomerListResult.CustomerListResultData.size() > 0) {
                    customer.setCode(apiCustomerListResult.CustomerListResultData.get(0).getNo());
                    customer.setBalance(apiCustomerListResult.CustomerListResultData.get(0).getBalance_LCY());
                    mCustomerBalanceLcy.setBalance(customer.getBalance());
                }
                //}

            } catch (IOException e) {
                Log.d("NAV_Client_Exception", e.toString());
                return new Customer();
            }

            return customer;
        }

        @Override
        protected void onPostExecute(final Customer customer) {

            mCustomerBalanceLcy = customer;
        }
    }
}
