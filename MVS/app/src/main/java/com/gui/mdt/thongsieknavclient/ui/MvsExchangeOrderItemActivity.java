package com.gui.mdt.thongsieknavclient.ui;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.ExchangeItem;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;
import com.gui.mdt.thongsieknavclient.datamodel.ItemUom;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.datamodel.SalesPrices;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ExchangeItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemBalancePdaDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemUomDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesPricesDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
//import com.gui.mdt.thongsieknavclient.syncTasks.ItemBalancePdaSyncTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class MvsExchangeOrderItemActivity extends AppCompatActivity implements View.OnClickListener,
        AsyncResponse {

    Toolbar myToolbar;
    TextView mTvHeader, mTxtItemDesc, mTxtAvailableExchangeQTY, mTvItemNo;
    EditText mTxtExchangeQTY;
    Spinner mSpnUom;
    ImageView mItemImg;
    Button mBtnAdd;
    Drawable mBackArrow;
//    SalesOrderLine mTempSalesOrderLine;
    String uom, itemNo, header, deliveryDate = "";
    float totalPrice = 0, quantity = 0, unitPrice = 0f, enteredQuantity = 0f;
    int position = 0;
    Bundle extras;
    List<SalesPrices> mSalesPricesList;
    private Item mTempItem;
    private Customer mTempCustomer;
//    private SalesOrderLine mExistSalesOrderLine = new SalesOrderLine();
    private ExchangeItem mExistExchangeItem = new ExchangeItem();
    ExchangeItem mTempExchangeItem;
//    private ItemBalancePdaSyncTask itemBalancePdaSyncTask;
    private NavClientApp mApp;
    private List<ItemUom> uomList;
    private List<ExchangeItem> exchangeItemList;
//    ExchangeItem exchangeItem;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvs_exchange_order_item);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mApp = (NavClientApp) getApplicationContext();

        mTvHeader = (TextView) findViewById(R.id.tvHeader);
        mTxtItemDesc = (TextView) findViewById(R.id.txtItemDesc);
//        mTxtUnitPrice = (TextView) findViewById(R.id.txtUnitPrice);
//        mTxtVehicleQTY = (TextView) findViewById(R.id.txtVehicleQTY);
//        mTxtWarehouseQTY = (TextView) findViewById(R.id.txtWarehouseQTY);
//        mTxtTotalPrice = (TextView) findViewById(R.id.txtTotalPrice);
        mTvItemNo = (TextView) findViewById(R.id.tvItemNo);

        mTxtExchangeQTY = (EditText) findViewById(R.id.txtExchangeQTY);
        mTxtAvailableExchangeQTY = (TextView) findViewById(R.id.txtAvailableExchangeQTY);

        mSpnUom = (Spinner) findViewById(R.id.spnUom);

        mItemImg = (ImageView) findViewById(R.id.itemImg);

        mBtnAdd = (Button) findViewById(R.id.btnAdd);

        initComponent();

        extras = getIntent().getExtras();

//        String objAsJson = extras.getString(getResources().getString(R.string.item_json_obj));
//         exchangeItem= ExchangeItem.fromJson(objAsJson);
        if (extras != null) {
//            if(extras.containsKey(getResources().getString(R.string.item_json_obj))){
//                mTvItemNo.setText(exchangeItem.getItemCode());
//                mTxtItemDesc.setText(exchangeItem.getDescription());
//                mTxtExchangeQTY.setText(String.valueOf(exchangeItem.getTotalQty()));
//                mTxtAvailableExchangeQTY.setText(String.valueOf(exchangeItem.getBalanceQty()));
//            }
//            if (extras.containsKey("deliveryDate")) {
//                deliveryDate = extras.getString("deliveryDate");
//            }
//
//            if (extras.containsKey("existSalesOrderLineJasonObj")) {
//                mExistSalesOrderLine =
//                        SalesOrderLine.fromJson(extras.getString("existSalesOrderLineJasonObj"));
//            }
//
//            if (extras.containsKey(getResources().getString(R.string.customer_json_obj))) {
//                mTempCustomer = Customer.fromJson(extras.getString(getResources().getString(R.string.customer_json_obj)));
//            }
//
//            if (extras.containsKey(getResources().getString(R.string.sales_order_line_obj))) {
//                initSalesOrderLine();
//            }
        }

        mBtnAdd.setOnClickListener(this);

        initSalesQtyTextListner();
        initSalesOrderLine();
        //Forcus Sales Qty onLoad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mTxtExchangeQTY.requestFocus();
        //mTxtExchQTY.requestFocus();
    }

    public void initComponent() {
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBackArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE).sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(mBackArrow);

//        mSalesPricesList = new ArrayList<SalesPrices>();
    }


    public void initSalesOrderLine() {

//        String objAsJson = extras.getString(getResources().getString(R.string.sales_order_line_obj));

//        //Selected Item postion in the List. do not change this value !!
//        position = extras.getInt(getResources().getString(R.string.adapter_position));
//
//        if (mExistSalesOrderLine != null) {
//            if (mExistSalesOrderLine.getNo() != null) {
//                mTempSalesOrderLine = mExistSalesOrderLine;
//
//            } else {
//                mTempSalesOrderLine = SalesOrderLine.fromJson(objAsJson);
//            }
//        } else {
//            mTempSalesOrderLine = SalesOrderLine.fromJson(objAsJson);
//        }


        String objAsJson = extras.getString(getResources().getString(R.string.item_json_obj));
        //Selected Item postion in the List. do not change this value !!
//        position = extras.getInt(getResources().getString(R.string.adapter_position));
        if (mExistExchangeItem != null) {
            if (mExistExchangeItem.getId() != null) {
                mTempExchangeItem = mExistExchangeItem;
            } else {
                mTempExchangeItem = ExchangeItem.fromJson(objAsJson);
            }
        } else {
            mTempExchangeItem = ExchangeItem.fromJson(objAsJson);
        }

        //update item balence pda
//        startItemBalanceDownload();

//        mTxtVehicleQTY.setText(getVehicleQty(mTempSalesOrderLine.getNo(),
//                mTempSalesOrderLine.getUnitofMeasure(),
//                mApp.getmCurrentDriverCode()));

//        getItem(mTempExchangeItem.getNo());

//        header = mTempExchangeItem.getItemCrossReferenceNo();
//        if (mTempItem.getIdentifierCode() != null) {
//            if (!mTempItem.getIdentifierCode().isEmpty()) {
//                header = header + " - " + mTempItem.getIdentifierCode();
//            }
//        }

//        quantity = mTempExchangeItem.getQuantity();

//        mTvHeader.setText(header);
        mTxtItemDesc.setText(mTempExchangeItem.getDescription());
        mTvItemNo.setText(mTempExchangeItem.getItemCode());

        //Get uom list base on itemCode and salesCode
        List<String> uomList = new ArrayList<String>();
        getExchangeItemListByItemNo(mTempExchangeItem.getItemCode());

        if(!exchangeItemList.isEmpty()){
            for(ExchangeItem ei:exchangeItemList){
                if(ei.getUom()==null){
                    uomList.add("");
                }else{
                    uomList.add(ei.getUom());
                }
            }
        }
//        if (!mSalesPricesList.isEmpty()) {
//            for (SalesPrices sp : mSalesPricesList) {
//                if (sp.getUnitOfMeasureCode() == null) {
//                    uomList.add("");
//                } else {
//                    uomList.add(sp.getUnitOfMeasureCode());
//                }
//            }
//        }
//
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, uomList);
//
        //Setting uom adapter to Spinner
        mSpnUom.setAdapter(adapter);

        // Setting default item in spinner
        int getDegaultItemPosition = adapter.getPosition(mTempExchangeItem.getUom());
        mSpnUom.setSelection(getDegaultItemPosition);

        // Get seleted uom  from Spinner
        mSpnUom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedUom = mSpnUom.getSelectedItem().toString();

                setSelectedExchangeItemByUom(selectedUom);
//                mTempExchangeItem.setUom(selectedUom);

//                if(mTempItem.isInventoryValueZero()){
//                    mTempExchangeItem.setTotalQty(0.00f);
//                }else{
//                    unitPrice = getUnitPrice(mTempExchangeItem.getNo(), mTempCustomer.getCustomerPriceGroup(),
//                            mTempCustomer.getCode(), selectedUom);
//                    mTempExchangeItem.setUnitPrice(unitPrice);
//                }


//                mTxtUnitPrice.setText(String.format("%.2f", mTempSalesOrderLine.getUnitPrice()));
//                totalPrice = quantity * mTempSalesOrderLine.getUnitPrice();
                mTxtExchangeQTY.setText(String.format("%.2f", mTempExchangeItem.getTotalQty()));
                mTxtAvailableExchangeQTY.setText(String.format("%.2f", mTempExchangeItem.getBalanceQty()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
//
////        mTxtUnitPrice.setText(String.valueOf(String.format("%.2f", mTempSalesOrderLine.getUnitPrice())));
//        mTxtExchangeQTY.setText(String.valueOf(Math.round(mTempSalesOrderLine.getQuantity()
//                                            + mTempSalesOrderLine.getExchangedQty())));
//        mTxtAvailableExchangeQTY.setText(String.valueOf(Math.round(mTempSalesOrderLine.getExchangedQty())));
//        //Total Price
////        totalPrice = mTempSalesOrderLine.getQuantity() * mTempSalesOrderLine.getUnitPrice();
////        mTxtTotalPrice.setText(String.format("%.2f", totalPrice));
//
        mItemImg.setImageBitmap(loadImageFromStorage(mTempExchangeItem.getItemCode()));
//
////        mTxtWarehouseQTY.setText(getItemWarehouseQty(mTempSalesOrderLine.getNo()));
    }

    private void initSalesQtyTextListner() {
        mTxtExchangeQTY.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Total Price
                if (!mTxtExchangeQTY.getText().toString().equals("")) {
                    enteredQuantity = Float.parseFloat(mTxtExchangeQTY.getText().toString());

//                    totalPrice = enteredQuantity * mTempSalesOrderLine.getUnitPrice();
//
//                    mTxtTotalPrice.setText(String.format("%.2f", totalPrice));
//                    quantity = enteredQuantity;
                } else {
//                    totalPrice = 0;
//                    mTxtTotalPrice.setText(String.format("%.2f", totalPrice));
                    quantity = 0;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

//        if (findViewById(R.id.btnAdd) == v) {
//            if (extras.containsKey(getResources().getString(R.string.sales_order_line_obj))) {
//                if (mTempSalesOrderLine.getUnitofMeasure().equals("")) {
//                    Toast.makeText(MvsExchangeOrderItemActivity.this, "Item can not be added!, Item UOM is not " +
//                            "available", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (mTempSalesOrderLine.getUnitPrice() == 0f && !mTempItem.isInventoryValueZero()) {
//                        Toast.makeText(MvsExchangeOrderItemActivity.this, "Item can not be added!, Item unit price " +
//                                "is zero", Toast.LENGTH_SHORT).show();
//                    } else {
//
//                        /*if (quantity == 0f) {
//                            Toast.makeText(MvsSalesOrderItemActivity.this, "Item can not be added!, QTY can not " +
//                                    "be zero", Toast.LENGTH_SHORT).show();
//                        } else {*/
//                            float salesQty = 0f;
//                            if(mTxtExchangeQTY.getText().toString().equals(""))
//                            {
//                                salesQty = 0f;
//                            }
//                            else
//                            {
//                                salesQty = Float.parseFloat(mTxtExchangeQTY.getText().toString());
//                            }
//
//                            if(mTxtAvailableExchangeQTY.getText().toString().equals(""))
//                            {
//                                mTempSalesOrderLine.setExchangedQty(0f);
//                            }
//                            else {
//                                mTempSalesOrderLine.setExchangedQty(Float.parseFloat(mTxtAvailableExchangeQTY.getText().toString()));
//                            }
//
//                            float billQty = salesQty - mTempSalesOrderLine.getExchangedQty();
//                            float lineAmount = billQty * mTempSalesOrderLine.getUnitPrice();
//
//                            if(salesQty >= mTempSalesOrderLine.getExchangedQty())
//                            {
//                                mTempSalesOrderLine.setQuantity(billQty);
//                                mTempSalesOrderLine.setQtytoInvoice(billQty);
//                                mTempSalesOrderLine.setLineAmount(lineAmount);
//
//                                String objAsJson = mTempSalesOrderLine.toJson();
//
//                                Intent intent = new Intent();
//                                intent.putExtra(getResources().getString(R.string.sales_order_line_obj), objAsJson);
//                                intent.putExtra(getResources().getString(R.string.adapter_position), position);
//                                setResult(RESULT_OK, intent);
//                                finish();
//                            }
//                            else {
//                                Toast.makeText(MvsExchangeOrderItemActivity.this
//                                        , "Item can not be added!, Exchange quantity is greater than sales quantity"
//                                        , Toast.LENGTH_SHORT).show();
//                            }
//                       /* }*/
//                    }
//                }
//            }
//        }
    }

//    private String getItemWarehouseQty(String itemCode) {
//        String qty = "0.0";
//        ItemBalancePdaDbHandler dbAdapter = new ItemBalancePdaDbHandler(this);
//        dbAdapter.open();
//
//        qty = dbAdapter.getItemWarehouseQtyByItemCode(itemCode);
//
//        dbAdapter.close();
//        return qty;
//    }

    private void getItem(String Code) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        mTempItem = dbAdapter.getItemByItemCode(Code);

        dbAdapter.close();
    }

    private Bitmap loadImageFromStorage(String itemCode) {
        Bitmap temp = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.item_no_image);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", getApplicationContext().MODE_PRIVATE);

        try {
            File f = new File(directory, itemCode + ".png");
            temp = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            //Log.d("FileNotFoundException", e.toString());
        }
        return temp;
    }

//    public float getUnitPrice(String itemCode, String customerPriceGroup, String customerCode, String itemUom) {
//        float itemMasterUnitPrice = 0, groupItemPrice = 0, customerItemPrice = 0;
//
//        //1 ItemMaster UnitPrice
//        itemMasterUnitPrice = Float.parseFloat(getItemUnitPriceFromItemMaster(itemCode, itemUom) == "" ? "0" :
//                getItemUnitPriceFromItemMaster(itemCode, itemUom));
//
//        //2 Group ItemPrice
//        groupItemPrice = Float.parseFloat(getGroupItemPrice(customerPriceGroup, 1, itemCode, deliveryDate, itemUom)
//                == "" ? "0" : getGroupItemPrice(customerPriceGroup, 1, itemCode, deliveryDate, itemUom));
//
//        //3 customer ItemPrice
//        customerItemPrice = Float.parseFloat(getCustomerItemPrice(customerCode, 0, itemCode, deliveryDate, itemUom)
//                == "" ? "0" : getCustomerItemPrice(customerCode, 0, itemCode, deliveryDate, itemUom));
//
//        float minimunItemPrice = getMinItemPrice(itemMasterUnitPrice, groupItemPrice, customerItemPrice);
//
//        return minimunItemPrice;
//    }

//    public float getMinItemPrice(float a_, float b_, float c_) {
//        float a = a_, b = b_, c = c_;
//        boolean isAZero = false, isBZero = false, isCZero = false;
//
//        if (a == 0) {
//            isAZero = true;
//        }
//
//        if (b == 0) {
//            isBZero = true;
//        }
//
//        if (c == 0) {
//            isCZero = true;
//        }
//
//        if (isAZero && isBZero && isCZero) {
//            return 0;
//        } else {
//            if (isAZero && isBZero && !isCZero) {
//                return c;
//            }
//            if (isAZero && !isBZero && isCZero) {
//                return b;
//            }
//            if (!isAZero && isBZero && isCZero) {
//                return a;
//            }
//
//            if (isAZero && !isBZero && !isCZero) {
//                return Math.min(b, c);
//            }
//            if (!isAZero && !isBZero && isCZero) {
//                return Math.min(a, b);
//            }
//            if (!isAZero && isBZero && !isCZero) {
//                return Math.min(a, c);
//            }
//            if (!isAZero && !isBZero && !isCZero) {
//                return Math.min(a, Math.min(b, c));
//            }
//        }
//        return 0;
//    }

//    public String getCustomerItemPrice(String customerCode, int salesType, String itemCode, String deliveryDate,
//                                       String itemUom) {
//        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
//        db.open();
//
//        String itemPrice = db.getCustomerItemUnitPriceByCustomerCode(customerCode, salesType, itemCode, deliveryDate,
//                itemUom);
//
//        db.close();
//
//        return itemPrice;
//    }

//    public String getGroupItemPrice(String customerGroup, int salesType, String itemCode, String deliveryDate, String
//            itemUom) {
//        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
//        db.open();
//
//        String itemPrice = db.getGroupItemPriceByCustomePriceGroup(customerGroup, salesType, itemCode, deliveryDate,
//                itemUom);
//
//        db.close();
//
//        return itemPrice;
//    }

//    public String getItemUnitPriceFromItemMaster(String itemCode, String itemUom) {
//        ItemDbHandler db = new ItemDbHandler(getApplicationContext());
//        db.open();
//
//        String unitprice = db.getItemPriceByItemCode(itemCode, itemUom);
//        db.close();
//
//        return unitprice;
//    }


    public void getExchangeItemListByItemNo(String itemNo) {
        uomList = new ArrayList<ItemUom>();
        ExchangeItemDbHandler dbAdapter=new ExchangeItemDbHandler(this);
        dbAdapter.open();
        exchangeItemList=dbAdapter.getExchangeItemByItemCode(itemNo);
        dbAdapter.close();

////        if (customer != null) {
//            ItemUomDbHandler dbAdapter = new ItemUomDbHandler(this);
//            dbAdapter.open();
//            uomList = dbAdapter.getUomListbyItemCode(itemNo);
////            String customerPriceGroup = customer.getCustomerPriceGroup();
//
//            //if customer price group is "" then sales type is 0.
////            if (customer.getCustomerPriceGroup().equals("")) {
////                customerPriceGroup = customer.getCode();
////            }
//
////            mSalesPricesList = uom.getAllPriceList(customerPriceGroup, itemNo);
//
//            dbAdapter.close();
////        }
    }
    public void setSelectedExchangeItemByUom(String uom){
        for (ExchangeItem ei : exchangeItemList) {
                if (ei.getUom() == uom) {
                    mTempExchangeItem=ei;
                }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
//        if(itemBalancePdaSyncTask != null){
//            itemBalancePdaSyncTask.cancel(true);
//        }
        onBackPressed();
        finish();
        return true;
    }

//    private void startItemBalanceDownload() {
//
//        if (mTempSalesOrderLine.getNo().equals("") || mTempSalesOrderLine.getNo() == null) {
//            itemBalancePdaSyncTask = new ItemBalancePdaSyncTask(getApplicationContext()
//                    , true);
//            itemBalancePdaSyncTask.delegate = MvsExchangeOrderItemActivity.this;
//            itemBalancePdaSyncTask.execute((Void) null);
//        } else {
//            itemBalancePdaSyncTask = new ItemBalancePdaSyncTask(getApplicationContext()
//                    , true, mTempSalesOrderLine.getNo());
//            itemBalancePdaSyncTask.delegate = MvsExchangeOrderItemActivity.this;
//            itemBalancePdaSyncTask.execute((Void) null);
//        }
//    }

    private String getVehicleQty(String itemNo, String itemUom, String driverCode){

        ItemBalancePdaDbHandler ibpDb
                = new ItemBalancePdaDbHandler(getApplicationContext());
        ibpDb.open();
        ItemBalancePda itemPdaObj = ibpDb.getItemBalencePda(itemNo
                , itemUom
                , driverCode);
        ibpDb.close();

        float qty = itemPdaObj.getOpenQty() - itemPdaObj.getQuantity();

        return String.valueOf(Math.round(qty));
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {
//        mTxtWarehouseQTY.setText(syncStatus.getScope());
    }
}
