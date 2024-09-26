package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentHeader;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentLine;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.PaymentHeaderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.PaymentLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.gui.mdt.thongsieknavclient.syncTasks.SalesOrderDeliveryUploadSyncTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MsoPaymentActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE = 1;
    public static final int SALES_CUSTOMER_AR_SELECT_RESULT_CODE = 2;
    private NavClientApp mApp;
    Toolbar myToolbar;
    private Button btnSearch, btnCash, btnCheque, btnSave, btnCancel, btnSearchCustomer;
    FrameLayout btncalender;
    int year, month, date;
    private TextView txtCustomerName, txtTotal, txtCashAmount, txtChequeAmount, tvPaymentHeader,
            txtCustomerCode, txtCustomerCreditLimit, txtCustomerOutstanding,txtInvoiceNo,txtInvoiceAmount,tvViewCustomerAr;
    private String details = "", newPaymentNo = "",formName = "",mModule="",mDocumentNo="",mcurrentModule="", formatChequeDate = "",mInvoiceAmount="", mChequeName="";
    private PaymentHeader tempPaymentHeaderObj;
    private TableRow trCash, trCheque;
    private Customer customerObj;
    private PaymentLine tempCashPaymentLine, tempChequePaymentLine;
    private List<PaymentLine> paymentLineList;
    float chequeAmount = 0, cashAmount = 0, totalAmount = 0;
    boolean isSaved = true;
    int cashLineNo = -1, chequeLineNo = -1;
    private LinearLayout layoutCreditLimit,layoutOutStanding,layoutInvoiceNo,layoutInvoiceAmount;
    SimpleDateFormat dBDateFormat,uIDateFormat;
    TextView txtCashRemark, txtChequeRemark, txtDialogCashAmount, txtDialogChequeAmount;
    Drawable drbAddInvoice;

    private SalesOrderDeliveryUploadSyncTask salesOrderDeliveryUploadSyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_mso_payment);

        mApp = (NavClientApp) getApplicationContext();

        dBDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        uIDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        mcurrentModule= mApp.getmCurrentModule();
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        txtTotal = (TextView) findViewById(R.id.txtTotal);

        txtCashAmount = (TextView) findViewById(R.id.txtCashAmount);
        txtChequeAmount = (TextView) findViewById(R.id.txtChequeAmount);
        tvPaymentHeader = (TextView) findViewById(R.id.tvPaymentHeader);
        txtInvoiceAmount=(TextView) findViewById(R.id.txtInvoiceAmount);
        txtInvoiceNo=(TextView)findViewById(R.id.txtInvoiceNo);

        txtCustomerCode = (TextView) findViewById(R.id.txtCustomerCode);
        txtCustomerCreditLimit = (TextView) findViewById(R.id.txtCustomerCreditLimit);
        txtCustomerOutstanding = (TextView) findViewById(R.id.txtCustomerOutstanding);

        tvViewCustomerAr = (TextView)findViewById(R.id.tvViewCustomerAr);

        btnCash = (Button) findViewById(R.id.btnCash);
        btnCheque = (Button) findViewById(R.id.btnCheque);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSearchCustomer = (Button) findViewById(R.id.btnSearchCustomer);

        trCash = (TableRow) findViewById(R.id.trCash);
        trCheque = (TableRow) findViewById(R.id.trCheque);

        layoutCreditLimit=(LinearLayout)findViewById(R.id.layoutCustomerCreditLimit);
        layoutOutStanding=(LinearLayout)findViewById(R.id.layoutCustomerOutstanding);
        layoutInvoiceNo=(LinearLayout)findViewById(R.id.layoutInvoiceNo);
        layoutInvoiceAmount=(LinearLayout)findViewById(R.id.layoutInvoiceAmount);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Order List");

        btnCash.setOnClickListener(this);
        btnCheque.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSearchCustomer.setOnClickListener(this);
        tvViewCustomerAr.setOnClickListener(this);

        tvViewCustomerAr.setPaintFlags(tvViewCustomerAr.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Drawable backArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left).color(Color.WHITE).sizeDp(30);
        Drawable menuDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_ellipsis_v).color(Color.WHITE).sizeDp(30);
        Drawable cusSearchDrawable = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_search).color(getResources().getColor(R.color.colorPrimary)).sizeDp(20);
        drbAddInvoice = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_list_ul).color(Color.BLUE).sizeDp(30);

        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        myToolbar.setOverflowIcon(menuDrawable);
        btnSearchCustomer.setBackgroundDrawable(cusSearchDrawable);

        txtCustomerName.setBackgroundColor(getResources().getColor(R.color.white));
        txtCustomerName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("details")) {
                details = extras.getString("details");
            }
            if(extras.containsKey("formName"))
            {
                formName = extras.getString("formName");
            }

            if(extras.containsKey("module")){
                mModule=extras.getString("module");
            }

            if(extras.containsKey("DocumentNo")){
                mDocumentNo=extras.getString("DocumentNo");
            }

            if(extras.containsKey("ReferenceAmount")){
                mInvoiceAmount=extras.getString("ReferenceAmount");
            }

            if (extras.containsKey("paymentHeaderObj")) {

                tempPaymentHeaderObj = PaymentHeader.fromJson(extras.getString("paymentHeaderObj"));
                tvPaymentHeader.setText(tempPaymentHeaderObj.getPaymentNo());

                mDocumentNo=tempPaymentHeaderObj.getExternalDocumentNo();
                mInvoiceAmount=tempPaymentHeaderObj.getReferenceAmount();

                getPaymentLineList(tempPaymentHeaderObj.getPaymentNo());

                for (PaymentLine pl : paymentLineList) {
                    if (pl.getPaymentType().equals(getResources().getString(R.string.PaymentTypeCash))) {
                        tempCashPaymentLine = new PaymentLine();
                        trCash.setVisibility(View.VISIBLE);
                        txtCashAmount.setText(String.format("%.2f", pl.getAmount()));
                        cashAmount = pl.getAmount();
                        tempCashPaymentLine = pl;
                        cashLineNo = pl.getLineNo();
                    }
                    if (pl.getPaymentType().equals(getResources().getString(R.string.PaymentTypeCheque))) {
                        tempChequePaymentLine = new PaymentLine();
                        trCheque.setVisibility(View.VISIBLE);
                        txtChequeAmount.setText(String.format("%.2f", pl.getAmount()));
                        chequeAmount = pl.getAmount();
                        tempChequePaymentLine = pl;
                        chequeLineNo = pl.getLineNo();
                    }
                }
                txtTotal.setText(String.format("%.2f", Float.valueOf(tempPaymentHeaderObj.getTotalAmount())));

                getCustomer(tempPaymentHeaderObj.getCustomerNo());

                txtCustomerName.setText(tempPaymentHeaderObj.getCustomerName());
                txtCustomerCode.setText(customerObj.getCode());
                txtCustomerCreditLimit.setText(String.valueOf(customerObj.getCreditLimit()));
                txtCustomerOutstanding.setText(String.valueOf(customerObj.getBalance()));

                if (tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesOrderStatusConfirmed))
                        || tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesOrderStatusComplete))) {

                    btnSave.setEnabled(false);
                    btnSearchCustomer.setEnabled(false);

                    if(tempCashPaymentLine==null)
                    {
                        btnCash.setEnabled(false);
                    }
                    if(tempChequePaymentLine==null)
                    {
                        btnCheque.setEnabled(false);
                    }

                    Toast.makeText(mApp, getResources().getString(R.string.not_allowed_to_edit), Toast.LENGTH_LONG).show();
                }
            }
        }
        if (details.equals("NewPayment")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String todayDate = sdf.format(new Date());

            tempPaymentHeaderObj = new PaymentHeader();
            tempPaymentHeaderObj.setStatus(getResources().getString(R.string.SalesPaymentStatusPending));
            customerObj = new Customer();

            txtCustomerName.setText("");
            txtCashAmount.setText("");
            txtChequeAmount.setText("");

            trCash.setVisibility(View.GONE);
            trCheque.setVisibility(View.GONE);
            txtTotal.setText("");

            newPaymentNo = getNewPaymentNo();
            //newPaymentNo="P"+todayDate;
            tvPaymentHeader.setText(newPaymentNo);
            tempPaymentHeaderObj.setPaymentNo(newPaymentNo);
            totalAmount = 0f;
            txtTotal.setText(String.format("%.2f", totalAmount));
            /*tvPaymentHeader.setText("PH"+todayDate);
            tempPaymentHeaderObj.setPaymentNo("PH"+todayDate);*/
            tempChequePaymentLine = null;
            tempCashPaymentLine=null;

            chequeLineNo = -1;
            cashLineNo = -1;

            if(formName.equals("MsoSalesOrder") || mModule.equals(getResources().getString(R.string.module_lds)))
            {
                customerObj = Customer.fromJson(extras.getString("_customerDetailObject"));
                tempPaymentHeaderObj.setCustomerNo(customerObj.getCode());
                tempPaymentHeaderObj.setCustomerName(customerObj.getName());

                txtCustomerName.setText(customerObj.getName());
                txtCustomerCode.setText(customerObj.getCode());
                txtCustomerCreditLimit.setText(String.valueOf(customerObj.getCreditLimit()));
                txtCustomerOutstanding.setText(String.valueOf(customerObj.getBalance()));

            }

        }

        if(mcurrentModule.equals(getResources().getString(R.string.module_lds))){
            layoutCreditLimit.setVisibility(View.GONE);
            layoutOutStanding.setVisibility(View.GONE);
            tvViewCustomerAr.setVisibility(View.GONE);

            txtInvoiceNo.setText(mDocumentNo);
            txtInvoiceAmount.setText(mInvoiceAmount);
            if(formName.equals("")){

                btnCash.setEnabled(false);
                btnCheque.setEnabled(false);
                btnSave.setEnabled(false);
                btnCancel.setEnabled(false);
                btnSearchCustomer.setEnabled(false);

                btnSave.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
            }

            if(tempPaymentHeaderObj.getPaymentNo()!=null)
            {
                if(tempPaymentHeaderObj.getStatus()!=null)
                {
                    if(tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesPaymentStatusConfirmed)))
                    {
                        btnCash.setEnabled(false);
                        btnCheque.setEnabled(false);
                        btnSave.setEnabled(false);
                        btnCancel.setEnabled(false);
                        btnSearchCustomer.setEnabled(false);

                        btnSave.setVisibility(View.INVISIBLE);
                        btnCancel.setVisibility(View.INVISIBLE);

                        Toast.makeText(mApp, "Not Allow To Add Payment", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            else
            {
                Toast.makeText(mApp, "New Payment", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            layoutInvoiceNo.setVisibility(View.GONE);
            layoutInvoiceAmount.setVisibility(View.GONE);
        }

    }



    public void getCustomer(String customerCode) {
        CustomerDbHandler db = new CustomerDbHandler(getApplicationContext());

        db.open();
        customerObj = db.getCustomerByCustomerCode(customerCode);
        db.close();
    }

    public String getNewPaymentNo() {
        UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
        dbHandler.open();
        String paymentId="";
        if(mModule.equals(getResources().getString(R.string.module_lds)) || mApp.getmCurrentModule().equals(getResources().getString(R.string.module_mvs)))
        {
             paymentId = dbHandler.getLatestPaymentRunningNo(mApp.getCurrentUserName(), mApp.getmCurrentDriverCode());
        }
        else{
             paymentId = dbHandler.getLatestPaymentRunningNo(mApp.getCurrentUserName(), mApp.getmCurrentSalesPersonCode());
        }

        dbHandler.close();
        return paymentId;
    }

    public void updateTotalAmount() {
        totalAmount = cashAmount + chequeAmount;
        txtTotal.setText(String.format("%.2f", totalAmount));
    }

    public void getPaymentLineList(String paymentNo) {
        PaymentLineDbHandler db = new PaymentLineDbHandler(getApplicationContext());
        db.open();
        paymentLineList = db.getAllPaymentLinesByPaymentNo(paymentNo);
        db.close();
    }

    //------------------------------Click Events----------------------------------------------------
    @Override
    public void onClick(View v) {
        if (findViewById(R.id.btnCash) == v) {
            if (tempPaymentHeaderObj.getCustomerNo() == null) {
                showMessageBox("Alert","Please select a customer first!");
                txtCustomerName.setError("Please select a customer");
            } else {
                showCashDialog();
            }
        }
        if (findViewById(R.id.btnCheque) == v) {
            if (tempPaymentHeaderObj.getCustomerNo() == null) {
                showMessageBox("Alert","Please select a customer first!");
                txtCustomerName.setError("Please select a customer");
            } else {
                showChequeDialog();
            }
        }
        if (findViewById(R.id.btnSave) == v) {

            if (tempPaymentHeaderObj.getCustomerNo() == null) {
                showMessageBox("Alert", "Please select a customer first!");
                txtCustomerName.setError("Please select a customer");
            } else {

                String saveStatus = savePaymentHeader();

                if (saveStatus.equals("saved")) {
                    String jsonObj = tempPaymentHeaderObj.toJson();

                    Toast.makeText(mApp, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();

                    Intent intent = new Intent(this, MsoPaymentActivity.class);
                    intent.putExtra("paymentHeaderObj", jsonObj);
                    if (mcurrentModule.equals(getResources().getString(R.string.module_lds))) {
                        intent.putExtra("formName", "LdsSalesInvoice");
                    }
                    startActivity(intent);

                } else if (saveStatus.equals("deleted")) {
                    Toast.makeText(mApp, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(mApp, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (findViewById(R.id.btnCancel) == v) {
            onBackPressed();
        }
        if (findViewById(R.id.btnSearchCustomer) == v) {
            Intent intent = new Intent(getApplication(), SalesCustomerListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("formName", "MsoPayment");
            intent.putExtra("details", "AddCustomer");
            intent.putExtra(getResources().getString(R.string.is_search_pop_up_need), true);
            startActivityForResult(intent, SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE);
        }
        if (findViewById(R.id.tvViewCustomerAr) == v) {
            if (customerObj.getCode() == null) {
                showMessageBox("Alert", "Please select a customer first");
            } else {
                Intent intentCusAr = new Intent(this, SalesCustomerArActivity.class);
                intentCusAr.putExtra("cusNo", customerObj.getCode());
                intentCusAr.putExtra("cusName", customerObj.getName());
                intentCusAr.putExtra("outstanding", String.valueOf(customerObj.getBalance()));
                this.startActivity(intentCusAr);
            }
        }
    }

    public String savePaymentHeader() {
        SimpleDateFormat normalTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Date myDate = new Date();
        String date = normalTimeFormat.format(myDate);
        totalAmount = cashAmount + chequeAmount;
        String status = "";

        tempPaymentHeaderObj.setPaymentDate(date);

        if(mcurrentModule.equals(getResources().getString(R.string.module_lds)))
        {
            tempPaymentHeaderObj.setModuleId("2");//LDS -> 1
        }
        else{
            tempPaymentHeaderObj.setModuleId("1");//MSO -> 1
        }

        tempPaymentHeaderObj.setDriverCode(mApp.getmCurrentDriverCode());
        tempPaymentHeaderObj.setSalesPersonCode(mApp.getmCurrentSalesPersonCode());
        tempPaymentHeaderObj.setCreatedBy(mApp.getCurrentUserName());
        tempPaymentHeaderObj.setCreatedDateTime(date);
        tempPaymentHeaderObj.setTransferred(false);
        tempPaymentHeaderObj.setTotalAmount(String.format("%.2f", totalAmount));
        tempPaymentHeaderObj.setExternalDocumentNo(mDocumentNo);
        tempPaymentHeaderObj.setStatus(getResources().getString(R.string.SalesPaymentStatusPending));
        tempPaymentHeaderObj.setReferenceAmount(mInvoiceAmount);
       //tempPaymentHeaderObj.setExternalDocumentNo(getResources().getString(R.string.module_mso));

        PaymentHeaderDbHandler paymentHeaderDb = new PaymentHeaderDbHandler(getApplicationContext());

        paymentHeaderDb.open();

        if (paymentHeaderDb.isPaymentHeaderExist(tempPaymentHeaderObj.getPaymentNo())) {
            boolean isChequeAmtZero = false, isCashAmtZero = false;

            if(tempCashPaymentLine == null)
            {
                isCashAmtZero = true;
            }
            else {
                if (tempCashPaymentLine.getAmount() == 0f) {
                    isCashAmtZero = true;
                }
            }

            if(tempChequePaymentLine == null)
            {
                isChequeAmtZero = true;
            }
            else {
                if (tempChequePaymentLine.getAmount() == 0f) {
                    isChequeAmtZero = true;
                }
            }

            if(isCashAmtZero && isChequeAmtZero) {
                PaymentLineDbHandler paymentLineDb_ = new PaymentLineDbHandler(getApplicationContext());
                paymentLineDb_.open();

                if (paymentHeaderDb.deletePaymentHeader(tempPaymentHeaderObj.getPaymentNo())) {
                    Log.d("PaymentHeader Deleted", tempPaymentHeaderObj.getPaymentNo());

                    if (paymentLineDb_.isPaymentLineExist(cashLineNo)) {
                        if (paymentLineDb_.deletePaymentLine(cashLineNo)) {
                            Log.d("CashLine Deleted", tempCashPaymentLine.getPaymentNo());

                        }
                    }
                    if (paymentLineDb_.isPaymentLineExist(chequeLineNo)) {
                        if (paymentLineDb_.deletePaymentLine(chequeLineNo)) {
                            Log.d("ChequeLine Deleted", tempChequePaymentLine.getPaymentNo());
                        }
                    }
                }
                paymentLineDb_.close();
                paymentHeaderDb.close();
                status = "deleted";
                return status;
            }
            else
            {
                paymentHeaderDb.updatePaymentHeader(tempPaymentHeaderObj);
                Log.d("PaymentHeader Updated", tempPaymentHeaderObj.getPaymentNo());

                if (!saveCashDetails()) {
                    Toast.makeText(mApp, "Failed to save cash details", Toast.LENGTH_SHORT).show();
                }

                if (!saveChequeDetails()) {
                    Toast.makeText(mApp, "Failed to save cheque details", Toast.LENGTH_SHORT).show();
                }
            }

            status = "saved";
            isSaved = true;
        }
        else
        {
            if(totalAmount != 0f)
            {
                paymentHeaderDb.addPaymentHeader(tempPaymentHeaderObj);
                saveNewPaymentHeaderRunningNo(tempPaymentHeaderObj.getPaymentNo());


                Log.d("PaymentHeader Added", tempPaymentHeaderObj.getPaymentNo());

                if (!saveCashDetails()) {
                    Toast.makeText(mApp, "Failed to save cash details", Toast.LENGTH_SHORT).show();
                }

                if (!saveChequeDetails()) {
                    Toast.makeText(mApp, "Failed to save cheque details", Toast.LENGTH_SHORT).show();
                }
                status = "saved";
                isSaved = true;
            }
            else
            {
                showMessageBox("Alert","Total amount for payment cant be zero!");
            }
        }
        paymentHeaderDb.close();
        return status;
    }

    public boolean saveCashDetails()
    {
        PaymentLineDbHandler paymentLineDb = new PaymentLineDbHandler(getApplicationContext());
        paymentLineDb.open();

        boolean status = false;
        if (tempCashPaymentLine != null && tempCashPaymentLine.getAmount() != 0f) {
            if (paymentLineDb.isPaymentLineExist(cashLineNo)) {
                if (paymentLineDb.deletePaymentLine(cashLineNo)) {
                    paymentLineDb.addNewPayment(tempCashPaymentLine);

                    Log.d("PaymentLine Added", tempCashPaymentLine.getPaymentNo());
                    status = true;
                }
            } else {
                paymentLineDb.addNewPayment(tempCashPaymentLine);
                Log.d("PaymentLine Added", tempCashPaymentLine.getPaymentNo());
                status = true;
            }
            paymentLineDb.close();
        }
        else if(tempCashPaymentLine != null && tempCashPaymentLine.getAmount() == 0f) {

            if (paymentLineDb.isPaymentLineExist(cashLineNo)) {
                if (paymentLineDb.deletePaymentLine(cashLineNo)) {
                    Log.d("PaymentLine Deleted", tempChequePaymentLine.getPaymentNo());
                    cashLineNo = -1; cashAmount = 0f;
                    tempCashPaymentLine = null;
                    trCash.setVisibility(View.GONE);
                    txtCashAmount.setText(String.format("%.2f", 0f));
                    status = true;
                }
            }
        }
        else {
            paymentLineDb.close();
            status = true;
        }
        return status;
    }

    public boolean saveChequeDetails()
    {
        PaymentLineDbHandler paymentLineDb = new PaymentLineDbHandler(getApplicationContext());
        paymentLineDb.open();

        boolean status = false;
        if (tempChequePaymentLine != null && tempChequePaymentLine.getAmount() != 0f) {
            if (paymentLineDb.isPaymentLineExist(chequeLineNo)) {
                if (paymentLineDb.deletePaymentLine(chequeLineNo)) {
                    paymentLineDb.addNewPayment(tempChequePaymentLine);
                    Log.d("PaymentLine Added", tempChequePaymentLine.getPaymentNo());
                    status = true;
                }
            }
            else {
                paymentLineDb.addNewPayment(tempChequePaymentLine);
                Log.d("PaymentLine Added", tempChequePaymentLine.getPaymentNo());
                status = true;
            }

            paymentLineDb.close();
        }
        else if(tempChequePaymentLine != null && tempChequePaymentLine.getAmount() == 0f) {

            if (paymentLineDb.isPaymentLineExist(chequeLineNo)) {
                if (paymentLineDb.deletePaymentLine(chequeLineNo)) {
                    Log.d("PaymentLine Deleted", tempChequePaymentLine.getPaymentNo());
                    chequeLineNo = -1; chequeAmount = 0f;
                    tempChequePaymentLine = null;
                    trCheque.setVisibility(View.GONE);
                    txtChequeAmount.setText(String.format("%.2f", 0f));
                    status = true;
                }
            }

        }
        else
        {
            paymentLineDb.close();
            status = true;
        }
        return status;
    }

    public void saveNewPaymentHeaderRunningNo(String newPaymentId) {
        UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
        dbHandler.open();
        dbHandler.setLatestPaymentRunningNo(newPaymentId, mApp.getCurrentUserName());
        dbHandler.close();
    }


    public void UpdateSalesInvoiceStatus(String salesInvoiceNo){

        SalesOrderDbHandler dbHandler=new SalesOrderDbHandler(this);
        dbHandler.open();
        dbHandler.UpdateSalesInvoiceStatus(salesInvoiceNo);
        dbHandler.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Customer Change
        if (requestCode == SALES_CUSTOMER_LIST_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extraData = data.getExtras();
                if (extraData != null) {
                    if (extraData.containsKey("_customerDetailObject")) {
                        String objAsJson = extraData.getString("_customerDetailObject");
                        customerObj = Customer.fromJson(objAsJson);

                        txtCustomerName.setText(customerObj.getName());
                        tempPaymentHeaderObj.setCustomerNo(customerObj.getCode());
                        tempPaymentHeaderObj.setCustomerName(customerObj.getName());

                        txtCustomerCode.setText(customerObj.getCode());
                        txtCustomerCreditLimit.setText(String.valueOf(customerObj.getCreditLimit()));
                        txtCustomerOutstanding.setText(String.valueOf(customerObj.getBalance()));

                        txtCustomerName.setError(null);
                        Toast.makeText(mApp, "Customer added!", Toast.LENGTH_SHORT).show();
                        isSaved = false;
                    }
                }
            }
        }
        if (requestCode == SALES_CUSTOMER_AR_SELECT_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extraData = data.getExtras();
                if (extraData != null) {
                    if (extraData.containsKey("arNoList")) {
                        String details="";
                        if (extraData.containsKey(getResources().getString(R.string.intent_extra_details))) {
                            details = extraData.getString(getResources().getString(R.string.intent_extra_details));
                        }
                        String mArList = extraData.getString("arNoList");
                        String mTotalBalence = extraData.getString("totalBalence");

                        if(details.equals("cashRemark"))
                        {
                            txtCashRemark.setText(mArList);
                            txtDialogCashAmount.setText(mTotalBalence);
                        }
                        if(details.equals("chequeRemark"))
                        {
                            txtChequeRemark.setText(mArList);
                            txtDialogChequeAmount.setText(mTotalBalence);
                        }
                    }
                }
            }
        }
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public void showCashDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();

        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        if (tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesOrderStatusConfirmed))
                || tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesOrderStatusComplete))) {

            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        else
        {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        dialog.getWindow()
                .setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.getWindow().setContentView(R.layout.dialog_sales_payment_cash);
        dialog.setCancelable(false);

        Button btnAdd = (Button) dialog.findViewById(R.id.btnAdd);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        txtCashRemark = (TextView) dialog.findViewById(R.id.txtRemark);
        txtDialogCashAmount = (TextView) dialog.findViewById(R.id.txtAmount);
        Button mBtnAddInvoice = (Button) dialog.findViewById(R.id.btnAddInvoice);

        mBtnAddInvoice.setBackgroundDrawable(drbAddInvoice);

        txtCashRemark.setText(tempCashPaymentLine == null ? "" : tempCashPaymentLine.getRemark());
        txtDialogCashAmount.setText(tempCashPaymentLine == null ? String.valueOf(0f) : String.valueOf(tempCashPaymentLine.getAmount()));



        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remark = "";
                float amount = 0f;

                String aa=txtDialogCashAmount.getText().toString();

                if (txtDialogCashAmount.getText().toString().equals("") || txtDialogCashAmount.getText().toString().equals(".")) {
                    Toast.makeText(mApp, "Failed!", Toast.LENGTH_SHORT).show();

                    txtDialogCashAmount.setError("Please enter amount");
                } else {
                    remark = txtCashRemark.getText().toString();
                    amount = Float.parseFloat(txtDialogCashAmount.getText().toString().equals("") ? String.valueOf(0f) : txtDialogCashAmount.getText().toString());

                    updateCashDetails(remark, amount);
                    dialog.dismiss();
                }
            }
        });

        mBtnAddInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cusJasonObj = customerObj.toJson();

                Intent intent = new Intent(getApplication(), SalesCustomerArSelectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(getResources().getString(R.string.intent_extra_details), "cashRemark");
                intent.putExtra(getResources().getString(R.string.customer_json_obj), cusJasonObj);
                startActivityForResult(intent, SALES_CUSTOMER_AR_SELECT_RESULT_CODE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesOrderStatusConfirmed))
                || tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesOrderStatusComplete))) {
            mBtnAddInvoice.setEnabled(false);
            btnAdd.setEnabled(false);
            txtCashRemark.setEnabled(false);
            txtDialogCashAmount.clearFocus();
            txtDialogCashAmount.setEnabled(false);
            txtDialogCashAmount.setSelectAllOnFocus(false);

            txtCashRemark.setSelectAllOnFocus(false);
            txtCashRemark.clearFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        else {
            txtDialogCashAmount.setSelectAllOnFocus(true);
            txtDialogCashAmount.requestFocus();
        }

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    public  void updateCashDetails(String remark, float amount) {

        float formatAmount = Float.valueOf(String.format("%.2f", amount));
        tempCashPaymentLine = new PaymentLine();

        tempCashPaymentLine.setPaymentNo(tempPaymentHeaderObj.getPaymentNo());
        tempCashPaymentLine.setPaymentType(getResources().getString(R.string.PaymentTypeCash));
        tempCashPaymentLine.setAmount(formatAmount);
        tempCashPaymentLine.setRemark(remark);
        //tempCashPaymentLine.setChequeDate(dBDateFormat.format(new Date()));
        Toast.makeText(mApp, "Cash details updated!", Toast.LENGTH_SHORT).show();
        cashAmount = amount;
        updateTotalAmount();

        isSaved = false;
        trCash.setVisibility(View.VISIBLE);
        txtCashAmount.setText(String.format("%.2f", tempCashPaymentLine.getAmount()));

    }

    public void showMessageBox(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showChequeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();

        formatChequeDate = dBDateFormat.format(new Date());

        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        if (tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesOrderStatusConfirmed))
                || tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesOrderStatusComplete))) {

            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        else
        {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        dialog.getWindow()
                .setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_sales_payment_cheque);
        dialog.setCancelable(false);

        Button btnAdd = (Button) dialog.findViewById(R.id.btnAdd);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btncalender = (FrameLayout) dialog.findViewById(R.id.btncalender);

        txtDialogChequeAmount = (TextView) dialog.findViewById(R.id.txtAmount);
        txtChequeRemark = (TextView) dialog.findViewById(R.id.txtRemark);
        final TextView txtChequeNo = (TextView) dialog.findViewById(R.id.txtChequeNo);
        final TextView txtChequeDate = (TextView) dialog.findViewById(R.id.txtChequeDate);
        Button mBtnAddInvoice = (Button) dialog.findViewById(R.id.btnAddInvoice);
        final TextView mTxtChequeName = (TextView) dialog.findViewById(R.id.txtChequeName);

        mBtnAddInvoice.setBackgroundDrawable(drbAddInvoice);

        txtDialogChequeAmount.setText(tempChequePaymentLine == null ? String.valueOf(0f) : String.valueOf(tempChequePaymentLine.getAmount()));
        txtChequeRemark.setText(tempChequePaymentLine == null ? "" : tempChequePaymentLine.getRemark());
        txtChequeNo.setText(tempChequePaymentLine == null ? "" : tempChequePaymentLine.getChequeNo());
        mTxtChequeName.setText(tempChequePaymentLine == null ? "" : tempChequePaymentLine.getChequeName());

        if(tempChequePaymentLine == null)
        {
            //txtChequeDate.setText(tempChequePaymentLine == null ? uIFormat.format(new Date()) : tempChequePaymentLine.getChequeDate());
            txtChequeDate.setText(uIDateFormat.format(new Date()));
        }
        else
        {
            if (!tempChequePaymentLine.getChequeDate().equals("")) {
                formatChequeDate=tempChequePaymentLine.getChequeDate();
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                try {

                    Date initDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tempChequePaymentLine.getChequeDate());
                    txtChequeDate.setText(df.format(initDate));

                } catch (Exception e) {
                    Log.e("Exception :", e.getMessage().toString());
                }
            }
        }



        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String chequeDate = "", remark = "", chequeNo = "";
                float amount = 0f;

                if (txtChequeDate.getText().toString() == "" || txtChequeNo.getText().toString().equals("") || txtDialogChequeAmount.getText().toString().equals("") || txtDialogChequeAmount.getText().toString().equals(".")) {
                    Toast.makeText(mApp, "Failed!", Toast.LENGTH_SHORT).show();

                    if (txtChequeDate.getText().toString().equals("")) {
                        txtChequeDate.setError("Please select a date");
                    }
                    if (txtChequeNo.getText().toString().equals("")) {
                        txtChequeNo.setError("Please enter cheque no");
                    }
                    if (txtDialogChequeAmount.getText().toString().equals("") || txtDialogChequeAmount.getText().toString().equals(".")) {
                        txtDialogChequeAmount.setError("Please enter amount");
                    }

                } else {
                    amount = Float.parseFloat(txtDialogChequeAmount.getText().toString().equals("") ? String.valueOf(0f) : txtDialogChequeAmount.getText().toString());
                    remark = txtChequeRemark.getText().toString();
                    chequeDate = formatChequeDate;
                    chequeNo = txtChequeNo.getText().toString();
                    mChequeName=mTxtChequeName.getText().toString();

                    updateChequeDetails(chequeDate, amount, remark, chequeNo, mChequeName);
                    dialog.dismiss();
                }
            }
        });

        mBtnAddInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cusJasonObj = customerObj.toJson();

                Intent intent = new Intent(getApplication(), SalesCustomerArSelectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(getResources().getString(R.string.intent_extra_details), "chequeRemark");
                intent.putExtra(getResources().getString(R.string.customer_json_obj), cusJasonObj);
                startActivityForResult(intent, SALES_CUSTOMER_AR_SELECT_RESULT_CODE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btncalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCalender(view, dialog);
            }
        });

        if (tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesOrderStatusConfirmed))
                || tempPaymentHeaderObj.getStatus().equals(getResources().getString(R.string.SalesOrderStatusComplete))) {

            btnAdd.setEnabled(false);
            btncalender.setEnabled(false);
            mBtnAddInvoice.setEnabled(false);
            txtDialogChequeAmount.setEnabled(false);
            txtDialogChequeAmount.setSelectAllOnFocus(false);
            txtDialogChequeAmount.clearFocus();
            txtChequeRemark.setEnabled(false);
            txtChequeNo.setEnabled(false);
            mTxtChequeName.setEnabled(false);
        }
        else
        {
            txtDialogChequeAmount.setSelectAllOnFocus(true);
            txtDialogChequeAmount.requestFocus();
        }

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    public void updateChequeDetails(String chequeDate, float amount, String remark, String chequeNo, String chequeName) {

        float formatAmount = Float.valueOf(String.format("%.2f", amount));
        tempChequePaymentLine = new PaymentLine();

        tempChequePaymentLine.setPaymentNo(tempPaymentHeaderObj.getPaymentNo());
        tempChequePaymentLine.setPaymentType(getResources().getString(R.string.PaymentTypeCheque));
        tempChequePaymentLine.setRemark(remark);
        tempChequePaymentLine.setAmount(formatAmount);
        tempChequePaymentLine.setChequeNo(chequeNo);
        tempChequePaymentLine.setChequeDate(chequeDate);
        tempChequePaymentLine.setChequeName(chequeName);

        chequeAmount = amount;
        updateTotalAmount();
        isSaved = false;
        Toast.makeText(mApp, "Cheque details updated!", Toast.LENGTH_SHORT).show();

        trCheque.setVisibility(View.VISIBLE);
        txtChequeAmount.setText(String.format("%.2f", tempChequePaymentLine.getAmount()));

    }

    private void getCalender(View view, Dialog dialog) {
        Calendar cl, clNow;
        int nowYear, nowMonth, nowDay;
        cl = Calendar.getInstance();
        date = cl.get(Calendar.DAY_OF_MONTH);
        month = cl.get(Calendar.MONTH);
        year = cl.get(Calendar.YEAR);
        final TextView txtChequeDate;

        clNow = Calendar.getInstance();
        nowYear = clNow.get(Calendar.YEAR);
        nowMonth = clNow.get(Calendar.MONTH);
        nowDay = clNow.get(Calendar.DAY_OF_MONTH);
        txtChequeDate = (TextView) dialog.findViewById(R.id.txtChequeDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtChequeDate.setText( dayOfMonth + "-" + (month + 1) + "-" + year);
                formatChequeDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                txtChequeDate.setError(null);
            }
        }, date, month, year);

        datePickerDialog.updateDate(nowYear, nowMonth, nowDay);
        datePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        if (!isSaved) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Alert");
            alertDialogBuilder.setMessage("Changes are not saved. Do you want to exit?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            finish();
        }
    }


}

