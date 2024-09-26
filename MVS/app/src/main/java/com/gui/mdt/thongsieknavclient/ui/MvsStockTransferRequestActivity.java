package com.gui.mdt.thongsieknavclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MvsStockTransferRequestListLineAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.StockTransferRequest;
import com.gui.mdt.thongsieknavclient.datamodel.StockTransferRequestLine;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockTransferRequestDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.StockTransferRequestLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MvsStockTransferRequestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MVS_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE = 2;
    private static final int SALES_ITEM_ACTIVITY_RESULT_CODE = 3;

    private NavClientApp mApp;
    List<StockTransferRequestLine> transferRequestLineList, removeTransferRequestLineList;
    StockTransferRequestLine tempTransferLine;
    Item tempItem;
    private StockTransferRequest tempTransfer;
    private MvsStockTransferRequestListLineAdapter transferListLineAdapter;
    private ProgressDialog xProgressDialog;

    LinearLayout llItem, llSummary;
    Button btnItem, btnSummary, btnSearch, btnSave, btnClear,btnDatePicker;
    Toolbar myToolbar;
    String transferNo;
    FloatingActionButton fabTopUpAddNewTransfer;
    TextView txtStockTransferNoItems, txtStockTransferTotalQTY, tvToolbarTitle;
    EditText txtStockTransferDate, textScanCode;
    RecyclerView recyclerViewTransferDetails;
    String listType = "",formName = "", stockTransferDate;
    boolean isStarted = false, isSaved = true,invalidDate = false;
    private int totalQty = 0;

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_mvs_stock_transfer_request, null);
        setContentView(view);
        createView(view);

        mApp = (NavClientApp) getApplicationContext();

        xProgressDialog = new ProgressDialog(this);
        xProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Transfer In: TI 10400");

        setSummaryTab();

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        Drawable searchDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_search)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        Drawable dtPickerDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_calendar)
                .color(getResources()
                .getColor(R.color.colorPrimary))
                .sizeDp(25);
        btnDatePicker.setBackgroundDrawable(dtPickerDrawable);

        btnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemTab();
            }
        });
        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSummaryTab();
            }
        });
        btnSave.setOnClickListener(this);//
        btnClear.setOnClickListener(this);//
        btnDatePicker.setOnClickListener(this);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });

        recyclerViewTransferDetails.setHasFixedSize(true);
        recyclerViewTransferDetails.setLayoutManager(new LinearLayoutManager(this));

        removeTransferRequestLineList = new ArrayList<StockTransferRequestLine>();
        initSwipeDelete();

        fabTopUpAddNewTransfer.bringToFront();
        fabTopUpAddNewTransfer.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(getResources().getString(R.string.intent_extra_form_name))) {
                formName = extras.getString(getResources().getString(R.string.intent_extra_form_name));
            }
            if (extras.containsKey(getResources().getString(R.string.list_type))) {
                listType = extras.getString(getResources().getString(R.string.list_type));
            }
            if (extras.containsKey(getResources().getString(R.string.stock_transfer_request))) {
                String objAsJson = extras.getString(getResources().getString(R.string.stock_transfer_request));
                tempTransfer = StockTransferRequest.fromJson(objAsJson);
                stockTransferDate = tempTransfer.getStockTransferDate();

                getStockTransferLineList(tempTransfer.getNo());

                tvToolbarTitle.setText(tempTransfer.getNo());
                txtStockTransferNoItems.setText(tempTransfer.getNoOfItems());
                txtStockTransferTotalQTY.setText(tempTransfer.getTotalQuantity());

                //date conversion
                if (tempTransfer.getStockTransferDate() != null) {
                    if (!tempTransfer.getStockTransferDate().equals("")) {
                        String date_ = tempTransfer.getStockTransferDate().toString();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_dd_MM_yyyy));
                        try {

                            Date initDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(tempTransfer.getStockTransferDate().toString());
                            date_ = simpleDateFormat.format(initDate);

                        } catch (Exception e) {
                            Log.e(getResources().getString(R.string.message_exception), e.getMessage().toString());
                        }

                        txtStockTransferDate.setText(date_);
                    }
                }

                updateSummeryValues(transferRequestLineList);

                clearRecyclerView();
                if (transferRequestLineList.size() > 0) {
                    transferListLineAdapter = new MvsStockTransferRequestListLineAdapter(transferRequestLineList,
                            R.layout.item_stock_tansfer_request_card, MvsStockTransferRequestActivity.this, tempTransfer.getStatus());
                    recyclerViewTransferDetails.setAdapter(transferListLineAdapter);
                    transferListLineAdapter.notifyDataSetChanged();
                }
            }
            else if(listType.equals(getResources().getString(R.string.mvs_new_stock_transfer))){
                SimpleDateFormat dBFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));
                SimpleDateFormat uIFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_dd_MM_yyyy));

                clearFields();

                String todayDate = uIFormat.format(new Date());
                txtStockTransferDate.setText(todayDate);
                stockTransferDate = dBFormat.format(new Date());

                transferNo = getLatestTransferNo();
                tvToolbarTitle.setText(transferNo);

                tempTransfer.setStockTransferDate(stockTransferDate);
                tempTransfer.setStatus(getResources().getString(R.string.StockTransferStatusPending));
                clearRecyclerView();
            }
        }

        if (tempTransfer.getStatus().equals(getResources().getString(R.string.StockTransferStatusConfirmed))
                || tempTransfer.getStatus().equals(getResources().getString(R.string.StockTransferStatusComplete))) {
            btnClear.setEnabled(false);
            btnSave.setEnabled(false);
            btnDatePicker.setEnabled(false);
            btnSave.setVisibility(View.GONE);
            fabTopUpAddNewTransfer.setEnabled(false);
            textScanCode.setEnabled(false);
            fabTopUpAddNewTransfer.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            Toast.makeText(mApp, getResources().getString(R.string.not_allowed_to_edit), Toast.LENGTH_LONG).show();
        }

        //------------------------  BarCode Scanner  -----------------------------------------------
        textScanCode.setInputType(InputType.TYPE_NULL);

        textScanCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textScanCode.setBackgroundResource(R.drawable.border_red);
                } else {
                    textScanCode.setBackgroundResource(R.drawable.border_gray);
                }
            }
        });

        textScanCode.requestFocus();
        textScanCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 1) {
                    isStarted = true;
                    Handler handler = new Handler();
                    xProgressDialog.setMessage(getResources().getString(R.string.progress_dialog__status_loading));
                    xProgressDialog.show();

                    handler.postDelayed(
                            new Runnable() {
                                public void run() {
                                    if (isStarted) {
                                        if (isItemAvailable(textScanCode.getText().toString())) {
                                            if (tempTransfer.getStockTransferDate() == null) {
                                                showMessageBox(getResources().getString(R.string.message_title_alert),
                                                        getResources().getString(R.string.message_body_select_stock_transfer_in_date));
                                            } else {
                                                getItem(textScanCode.getText().toString());
                                                addItem();
                                            }
                                            xProgressDialog.dismiss();
                                        } else {
                                            xProgressDialog.dismiss();
                                            showMessageBox(getResources().getString(R.string.message_title_alert),
                                                    getResources().getString(R.string.message_body_invalid_barcode));
                                        }
                                        textScanCode.setText("");
                                        textScanCode.requestFocus();
                                        isStarted = false;
                                    }
                                }
                            }, 1000);
                }
            }
        });
    }

    public void initSwipeDelete() {
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                    if (direction == ItemTouchHelper.RIGHT) {    //if swipe Right

                        AlertDialog.Builder builder = new AlertDialog.Builder(MvsStockTransferRequestActivity.this); //alert for confirm to delete
                        builder.setMessage("Do you want to Remove this item?");    //set message
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() { //when click on DELETE
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                StockTransferRequestLine strl= transferRequestLineList.get(position);
                                removeTransferRequestLineList.add(strl);

                                transferRequestLineList.remove(position);
                                updateSummeryValues(transferRequestLineList);
                                transferListLineAdapter.notifyDataSetChanged();

                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                transferListLineAdapter.notifyDataSetChanged();
                            }
                        }).show();
                    }
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerViewTransferDetails);

    }

    @Override
    public void onClick(View v) {
        OnButtonClick(v);
        if (findViewById(R.id.fabTopUpAddNewTransfer) == v) {
            if (txtStockTransferDate.getText().toString().equals("")) {
                showMessageBox(getResources().getString(R.string.message_title_alert),
                        getResources().getString(R.string.message_body_select_stock_transfer_in_date));
            }
            else {
                Intent intent = new Intent(getApplication(), SalesItemSearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(getResources().getString(R.string.intent_extra_form_name),
                        getResources().getString(R.string.form_name_mvs_stock_transfer_in));
                intent.putExtra(getResources().getString(R.string.intent_extra_details),
                        getResources().getString(R.string.intent_extra_add_new_item));
                startActivityForResult(intent, SALES_ITEM_ACTIVITY_RESULT_CODE);
            }
        }
        else if (findViewById(R.id.btnSearch) == v) {
            showSearchDialog();
        }
        else if (findViewById(R.id.btnItem) == v) {
            setItemTab();
        }
        else if (findViewById(R.id.btnSummary) == v) {
            setSummaryTab();
        }
        else if (findViewById(R.id.btnSave) == v) {

            validateStockTransferDate(stockTransferDate);

            if(!invalidDate)
            {
                if(transferRequestLineList.size()>0)
                {
                    if (saveStockTransfer()) {
                        StockTransferRequest transferInObj = tempTransfer;
                        String objAsJson = transferInObj.toJson();

                        Toast.makeText(mApp, getResources().getString(R.string.message_updated_successfully), Toast.LENGTH_SHORT).show();
                        finish();

                        Intent intent = new Intent(MvsStockTransferRequestActivity.this, MvsStockTransferRequestActivity.class);
                        if (formName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_in)))
                        {
                            intent.putExtra(getResources().getString(R.string.stock_transfer_request), objAsJson);
                            intent.putExtra("formName", getResources().getString(R.string.form_name_mvs_stock_transfer_in));
                        }
                        else if(formName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_out)))
                        {
                            intent.putExtra(getResources().getString(R.string.stock_transfer_request), objAsJson);
                            intent.putExtra("formName", getResources().getString(R.string.form_name_mvs_stock_transfer_out));
                        }
                        startActivity(intent);
                    } else {
                        Toast.makeText(mApp, getResources().getString(R.string.message_failed), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(mApp, getResources().getString(R.string.message_add_at_least_one_item), Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (findViewById(R.id.btnClear) == v) {
            if (transferRequestLineList.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(getResources().getString(R.string.message_title_alert));
                builder.setMessage(getResources().getString(R.string.message_item_qty_will_be_cleared));

                builder.setPositiveButton(getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        tempTransfer.setStatus(getResources().getString(R.string.StockTransferStatusPending));
                        for (StockTransferRequestLine sol : transferRequestLineList) {
                            sol.setQuantity(0f);
                        }
                        updateSummeryValues(transferRequestLineList);
                        transferListLineAdapter.notifyDataSetChanged();
                        Toast.makeText(mApp, getResources().getString(R.string.message_cleared), Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.button_text_no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        else if (findViewById(R.id.btnDatePicker) == v) {
            showDatePickerDialog();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MVS_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE) { //Updating Qty on specific item
            if (resultCode == RESULT_OK) {

                Bundle extraData = data.getExtras();

                if (extraData != null) {
                    if (extraData.containsKey(getResources().getString(R.string.stock_transfer_request_line))) {

                        String objAsJson = extraData.getString(getResources().getString(R.string.stock_transfer_request_line));

                        int position = extraData.getInt(getResources().getString(R.string.adapter_position));
                        tempTransferLine = StockTransferRequestLine.fromJson(objAsJson);

                        tempTransfer.setStatus(getResources().getString(R.string.StockTransferStatusPending));

                        //Setting SalesOrderLine item to the list
                        transferRequestLineList.set(position, tempTransferLine);
                        updateSummeryValues(transferRequestLineList);

                        //Updating the recyclerView
                        transferListLineAdapter.notifyDataSetChanged();
                        Toast.makeText(mApp, getResources().getString(R.string.message_qty_updated), Toast.LENGTH_SHORT).show();
                        isSaved = false;
                    }
                }
            }
        }
        if (requestCode == SALES_ITEM_ACTIVITY_RESULT_CODE) {  //Add new item to sales line
            if (resultCode == RESULT_OK) {

                Bundle extraData = data.getExtras();

                if (extraData != null) {
                    if (extraData.containsKey(getResources().getString(R.string.item_json_obj))) {

                        String objAsJson = extraData.getString(getResources().getString(R.string.item_json_obj));
                        tempItem = Item.fromJson(objAsJson);

                        addItem();
                    }
                }
            }
        }
    }

    public void addItem() {
        int position = 0;
        boolean isExist = false;

        for (int i = 0; i < transferRequestLineList.size(); i++) {
            StockTransferRequestLine trl = transferRequestLineList.get(i);
            if (trl.getItemNo().equals(tempItem.getItemCode())) {
                isExist = true;
                position = i;
            }
        }

        if (isExist) //if Item exist go to change Qty
        {
            StockTransferRequestLine TransferInLineObj = transferRequestLineList.get(position);
            String ObjToJason = TransferInLineObj.toJson();

            Intent intent = new Intent(this, MsoSalesOrderItemActivity.class);
            intent.putExtra(getResources().getString(R.string.stock_transfer_request_line), ObjToJason);
            intent.putExtra(getResources().getString(R.string.adapter_position), position);
            startActivityForResult(intent, MVS_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE);
        } else {

            StockTransferRequestLine transferInLine = new StockTransferRequestLine();

            String timeStamp = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd_HH_mm_ss))
                    .format(new Date());

            String key = timeStamp + tempTransfer.getDriverCode() + tempItem.getItemCode();

            transferInLine.setKey(key);
            transferInLine.setItemNo(tempItem.getItemCode());
            transferInLine.setDriverCode(tempTransfer.getDriverCode());
            transferInLine.setItemDescription(tempItem.getDescription());
            transferInLine.setStockTransferNo(tempTransfer.getNo());
            transferInLine.setQuantity(0f);  //New item
            transferInLine.setUnitofMeasure(tempItem.getItemBaseUom());

            tempTransfer.setStatus(getResources().getString(R.string.StockTransferStatusPending));

            transferRequestLineList.add(transferInLine);

            if (transferRequestLineList.size() == 1) {
                transferListLineAdapter = new MvsStockTransferRequestListLineAdapter(transferRequestLineList,
                        R.layout.item_stock_tansfer_request_card, this, tempTransfer.getStatus());
                recyclerViewTransferDetails.setAdapter(transferListLineAdapter);
            }
            updateSummeryValues(transferRequestLineList);
            transferListLineAdapter.notifyDataSetChanged();
            recyclerViewTransferDetails.smoothScrollToPosition(transferRequestLineList.size());

            String jasonObj = transferInLine.toJson();

            Intent intent = new Intent(this, MsoSalesOrderItemActivity.class);
            intent.putExtra(getResources().getString(R.string.stock_transfer_request_line), jasonObj);
            intent.putExtra(getResources().getString(R.string.adapter_position), transferRequestLineList.size() - 1);
            startActivityForResult(intent, MVS_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE);

            Toast.makeText(mApp, getResources().getString(R.string.message_item_added), Toast.LENGTH_SHORT).show();
            isSaved = false;
        }
    }

    private void getItem(String barCode) {
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        tempItem = dbAdapter.getItemByIdentifierCode(barCode);

        dbAdapter.close();
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

    private boolean isItemAvailable(String barCode) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        status = dbAdapter.isItemExistByIdentifierCode(barCode);

        dbAdapter.close();
        return status;
    }

    private String getLatestTransferNo() {
        UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
        dbHandler.open();
        String transferInNo = dbHandler.getLatestStockTransferRunningNo(mApp.getCurrentUserName(),formName);
        dbHandler.close();
        return transferInNo;
    }

    private void clearFields() {
        tempTransfer = new StockTransferRequest();
        tempTransferLine = new StockTransferRequestLine();
        transferRequestLineList = new ArrayList<StockTransferRequestLine>();

        txtStockTransferDate.setText("");
        txtStockTransferNoItems.setText("");
        txtStockTransferTotalQTY.setText("");
    }

    private void clearRecyclerView() {
        transferListLineAdapter = new MvsStockTransferRequestListLineAdapter(new ArrayList<StockTransferRequestLine>(),
                R.layout.item_stock_tansfer_request_card, MvsStockTransferRequestActivity.this, tempTransfer.getStatus());
        recyclerViewTransferDetails.setAdapter(transferListLineAdapter);
    }

    private void updateSummeryValues(List<StockTransferRequestLine> _transferRequestInLineList) {
        totalQty = 0;
        if (_transferRequestInLineList.size() > 0) {
            txtStockTransferNoItems.setText(String.valueOf(_transferRequestInLineList.size()));
            for (StockTransferRequestLine transferInLineObj : _transferRequestInLineList) {
                totalQty = totalQty + Math.round(transferInLineObj.getQuantity());
            }
            txtStockTransferTotalQTY.setText(String.valueOf(totalQty));
        } else {
            txtStockTransferNoItems.setText("");
            txtStockTransferTotalQTY.setText("");
        }
    }

    private void getStockTransferLineList(String no) {
        StockTransferRequestLineDbHandler dbAdapter = new StockTransferRequestLineDbHandler(this);
        dbAdapter.open();

        transferRequestLineList = dbAdapter.getAllTransferRequestInLinesByNo(no);

        dbAdapter.close();
    }

    private void createView(View view) {

        txtStockTransferNoItems = (TextView) view.findViewById(R.id.txtStockTransferNoItems);
        txtStockTransferTotalQTY = (TextView) view.findViewById(R.id.txtStockTransferTotalQTY);
        txtStockTransferDate = (EditText) view.findViewById(R.id.txtStockTransferDate);

        textScanCode = (EditText) view.findViewById(R.id.textScanCode);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnClear = (Button) view.findViewById(R.id.btnClear);
        btnDatePicker = (Button) view.findViewById(R.id.btnDatePicker);
        recyclerViewTransferDetails = (RecyclerView) view.findViewById(R.id.recyclerViewTransferDetails);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        llItem = (LinearLayout) findViewById(R.id.llItem);
        llSummary = (LinearLayout) findViewById(R.id.llSummary);
        btnItem = (Button) findViewById(R.id.btnItem);
        btnSummary = (Button) findViewById(R.id.btnSummary);
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        fabTopUpAddNewTransfer = (FloatingActionButton) findViewById(R.id.fabTopUpAddNewTransfer);
    }

    private void OnButtonClick(View view) {

    }

    private void setItemTab() {
        btnItem.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnSummary.setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
        llItem.setVisibility(View.VISIBLE);
        llSummary.setVisibility(View.GONE);
        fabTopUpAddNewTransfer.setVisibility(View.VISIBLE);
    }

    private void setSummaryTab() {
        btnSummary.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnItem.setBackgroundColor(getResources().getColor(R.color.hockeyapp_background_light));
        llSummary.setVisibility(View.VISIBLE);
        llItem.setVisibility(View.GONE);
        fabTopUpAddNewTransfer.setVisibility(View.GONE);
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final Dialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        ;
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow()
                .setLayout((int) (getScreenWidth(this) * .7), ViewGroup.LayoutParams.WRAP_CONTENT);


        window.setDimAmount(0.2f);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.setContentView(R.layout.dialog_sales_order_list_search);
        dialog.setCancelable(false);

        Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    public void validateStockTransferDate(String date)
    {
        SimpleDateFormat dfDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));

        try {
            if (dfDate.parse(date).before(dfDate.parse(dfDate.format(new Date()))))
            {
                invalidDate = true;
                txtStockTransferDate.setError("");
                Toast.makeText(this, getResources().getString(R.string.message_invalid_date), Toast.LENGTH_SHORT).show();
            }
            else
            {
                invalidDate = false;
                txtStockTransferDate.setError(null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private boolean saveStockTransfer() {
        boolean status = false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(getResources().getString(R.string.time_zone_gmt)));
        String todayDate = sdf.format(new Date());

        updateSummeryValues(transferRequestLineList);

        StockTransferRequestDbHandler dbAdapter = new StockTransferRequestDbHandler(getApplicationContext());
        dbAdapter.open();
        StockTransferRequestLineDbHandler dbLineAdapter = new StockTransferRequestLineDbHandler(getApplicationContext());
        dbLineAdapter.open();

        if (listType.equals(getResources().getString(R.string.mvs_new_stock_transfer))) {

            if (txtStockTransferDate.getText().toString().equals("")) {
                txtStockTransferDate.setError(getResources().getString(R.string.message_body_select_stock_transfer_in_date));
            } else {
                txtStockTransferDate.setError(null);
            }
            if (!txtStockTransferDate.getText().toString().equals(""))
            {
                if(formName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_in)))
                    tempTransfer.setKey(todayDate + getResources().getString(R.string.stock_transfer_in_prefixes) + mApp.getmCurrentDriverCode());
                else if(formName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_out)))
                    tempTransfer.setKey(todayDate + getResources().getString(R.string.stock_transfer_out_prefixes) + mApp.getmCurrentDriverCode());

                tempTransfer.setNo(transferNo);
                tempTransfer.setDriverCode(mApp.getmCurrentDriverCode());
                tempTransfer.setStockTransferDate(todayDate);
                tempTransfer.setDocumentDate(todayDate);

                if(formName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_in)))
                    tempTransfer.setStockTransferType(getResources().getString(R.string.StockTransferIn));
                else if(formName.equals(getResources().getString(R.string.form_name_mvs_stock_transfer_out)))
                    tempTransfer.setStockTransferType(getResources().getString(R.string.StockTransferOut));

                tempTransfer.setTotalQuantity(txtStockTransferTotalQTY.getText().toString());
                tempTransfer.setNoOfItems(txtStockTransferNoItems.getText().toString());
                tempTransfer.setStatus(getResources().getString(R.string.StockTransferStatusPending));
                tempTransfer.setTransferred("false");
                tempTransfer.setCreatedBy(mApp.getCurrentUserName());
                tempTransfer.setCreatedDateTime(todayDate);

                try {
                    if (dbAdapter.deleteStockTransfer(tempTransfer.getNo())) {
                        dbAdapter.addStockTransfer(tempTransfer);
                        saveStockTransferRunningNumber(tempTransfer.getNo());
                        Log.d(getResources().getString(R.string.message_st_added), tempTransfer.getNo() == null ? "" : tempTransfer.getNo());

                        if (transferRequestLineList != null && transferRequestLineList.size() > 0) {
                            int lineNo=1;
                            for (StockTransferRequestLine stl : transferRequestLineList) {
                                stl.setStockTransferLineNo(String.valueOf(lineNo));
                                stl.setStockTransferNo(transferNo);
                                stl.setDriverCode(mApp.getmCurrentDriverCode());
                                if (dbLineAdapter.deleteStockTransferLine(stl.getKey())) {
                                    dbLineAdapter.addStockTransferLine(stl);
                                    Log.d(getResources().getString(R.string.message_st_line_added), stl.getStockTransferLineNo() == null ? "" : stl.getStockTransferLineNo());
                                    lineNo++;
                                }
                            }
                        }
                    }
                    status = true;
                    listType = "";
                } catch (Exception ee) {
                    Log.d(getResources().getString(R.string.message_exception), ee.getMessage().toString());
                    dbAdapter.close();
                    dbLineAdapter.close();
                }
                dbAdapter.close();
                dbLineAdapter.close();
            }
        }
        else
        {
            tempTransfer.setStockTransferDate(stockTransferDate);
            tempTransfer.setDriverCode(mApp.getmCurrentDriverCode());
            tempTransfer.setStatus(getResources().getString(R.string.StockTransferStatusPending));
            tempTransfer.setTotalQuantity(txtStockTransferTotalQTY.getText().toString());
            tempTransfer.setNoOfItems(txtStockTransferNoItems.getText().toString());
            tempTransfer.setLastModifiedBy(mApp.getCurrentUserName());
            tempTransfer.setLastModifiedDateTime(todayDate);

            try {
                if (dbAdapter.deleteStockTransfer(tempTransfer.getNo())) {
                    dbAdapter.addStockTransfer(tempTransfer);
                    saveStockTransferRunningNumber(tempTransfer.getNo());
                    Log.d(getResources().getString(R.string.message_st_added), tempTransfer.getNo() == null ? "" : tempTransfer.getNo());

                    if (transferRequestLineList != null && transferRequestLineList.size() > 0) {
                        for (StockTransferRequestLine stl : transferRequestLineList) {
                            if (dbLineAdapter.deleteStockTransferLine(stl.getKey())) {
                                dbLineAdapter.addStockTransferLine(stl);
                                Log.d(getResources().getString(R.string.message_st_line_added), stl.getStockTransferLineNo() == null ? "" : stl.getStockTransferLineNo());
                            }
                        }
                    }

                    if (removeTransferRequestLineList != null && removeTransferRequestLineList.size() > 0) {
                        for (StockTransferRequestLine stl : removeTransferRequestLineList) {
                            if (dbLineAdapter.deleteStockTransferLine(stl.getKey())) {
                                Log.d("STI_LINE DELETED:", stl.getStockTransferLineNo() == null ? "" : stl.getStockTransferLineNo());
                            }
                        }
                    }
                }
                status = true;
                listType = "";
            } catch (Exception ee) {
                Log.d(getResources().getString(R.string.message_exception), ee.getMessage().toString());
                dbAdapter.close();
                dbLineAdapter.close();
            }
        }
        dbAdapter.close();
        dbLineAdapter.close();
        return status;
    }

    private void saveStockTransferRunningNumber(String no) {
        UserSetupDbHandler dbHandler = new UserSetupDbHandler(this);
        dbHandler.open();
        dbHandler.setLatestStockTransferRunningNo(no, mApp.getCurrentUserName(),formName);
        dbHandler.close();
    }

    private void showDatePickerDialog() {
        Calendar cl, clNow;
        int nowYear, nowMonth, nowDay,date,month,year;
        cl = Calendar.getInstance();
        date = cl.get(Calendar.DAY_OF_MONTH);
        month = cl.get(Calendar.MONTH);
        year = cl.get(Calendar.YEAR);

        clNow = Calendar.getInstance();
        nowYear = clNow.get(Calendar.YEAR);
        nowMonth = clNow.get(Calendar.MONTH);
        nowDay = clNow.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtStockTransferDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                setDeliveryDate(year + "-" + (month + 1) + "-" + dayOfMonth);
                validateStockTransferDate(year + "-" + (month + 1) + "-" + dayOfMonth);
                isSaved = false;
            }
        }, date, month, year);

        datePickerDialog.updateDate(nowYear, nowMonth, nowDay);
        datePickerDialog.show();
    }

    public void setDeliveryDate(String date) {
        if (!date.equals("")) {
            DateFormat df = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd));
            try {

                Date initDate = new SimpleDateFormat(getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(date);
                stockTransferDate = df.format(initDate);

            } catch (Exception e) {
                Log.e(getResources().getString(R.string.message_exception), e.getMessage().toString());
            }
        }
    }

}
