package com.gui.mdt.thongsieknavclient.ui;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.ItemUom;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.datamodel.SalesPrices;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequestLine;
import com.gui.mdt.thongsieknavclient.datamodel.StockTransferRequestLine;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemBalancePdaDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemUomDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesPricesDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.model.MsoSalesOrder;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemBalancePdaSyncTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MsoSalesOrderItemActivity extends AppCompatActivity implements View.OnClickListener,  AsyncResponse {

    Toolbar myToolbar;
    ArrayList<MsoSalesOrder> dataModels;
    String uom, itemNo, header, deliveryDate = "";
    EditText txtItemDesc, txtUOM, txtUnitPrice, txtQTY, txtWarehouseQTY, txtTotalPrice;
    TextView tvSalesitemHeader, mTvItemNo;
    Button btnadd, btnBack;
    ImageView ivItemImage;
    SalesOrderLine tempSalesOrderLine;
    StockTransferRequestLine tempTransferInLine;
    StockRequestLine tempStockRequestLine;
    float totalPrice = 0, quantity = 0, unitPrice = 0f, enteredQuantity = 0f;
    int position = 0;
    Bundle extras;
    Spinner spnUom;
    List<SalesPrices> salesPricesList;
    ItemBalancePdaSyncTask itemBalancePdaSyncTask;
    private Item tempItem;
    private Customer tempCustomer;
    private SalesOrderLine existSalesOrderLine = new SalesOrderLine();
    private StockRequestLine existStockRequestLine = new StockRequestLine();
    private String mItemNo = "";
    private List<ItemUom> uomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_mso_sales_order_item);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        salesPricesList = new ArrayList<SalesPrices>();

        tvSalesitemHeader = (TextView) findViewById(R.id.tvSalesitemHeader);
        txtItemDesc = (EditText) findViewById(R.id.txtItemDesc);
        txtUnitPrice = (EditText) findViewById(R.id.txtUnitPrice);
        txtQTY = (EditText) findViewById(R.id.txtQTY);
        txtWarehouseQTY = (EditText) findViewById(R.id.txtWarehouseQTY);
        txtTotalPrice = (EditText) findViewById(R.id.txtTotalPrice);
        btnadd = (Button) findViewById(R.id.btnadd);
        ivItemImage = (ImageView) findViewById(R.id.itemImg);
        btnBack = (Button) findViewById(R.id.btnBack);
        spnUom = (Spinner) findViewById(R.id.spnUom);
        //txtUOM = (EditText) findViewById(R.id.txtUOM);
        mTvItemNo=(TextView)findViewById(R.id.tvItemNo);

        Drawable backArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left).color(Color.WHITE)
                .sizeDp(30);

        btnBack.setFocusable(false);
        btnBack.setBackgroundDrawable(backArrow);

        extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("deliveryDate")) {
                deliveryDate = extras.getString("deliveryDate");
            }

            if (extras.containsKey("existSalesOrderLineJasonObj")) {
                existSalesOrderLine =
                        SalesOrderLine.fromJson(extras.getString("existSalesOrderLineJasonObj"));
            }

            if (extras.containsKey("existStockRequestLineJasonObj")) {
                existStockRequestLine =
                        StockRequestLine.fromJson(extras.getString("existStockRequestLineJasonObj"));
            }

            //setSalesOrderLineEditFields
            if (extras.containsKey(getResources().getString(R.string.sales_order_line_obj))) {
                setSalesOrderLineEditFields();
            }

            //setStockRequestNewFields
            else if (extras.containsKey(getResources().getString(R.string.stock_transfer_request_line))) {
                setStockRequestNewFields();

            }

            //setStockRequestEditFields
            else if (extras.containsKey(getResources().getString(R.string.stock_request_line_jason_obj))) {
                setStockRequestEditFields();
            }
        }

        btnadd.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        txtQtyAddTextChangedListenerfor();
        txtQTY.requestFocus();

        startItemBalanceDownload();
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

        txtWarehouseQTY.setText(syncStatus.getScope());
    }

    @Override
    public void onClick(View v) {

        if (findViewById(R.id.btnadd) == v) {
            if (extras.containsKey(getResources().getString(R.string.sales_order_line_obj))) {
                if (tempSalesOrderLine.getUnitofMeasure().equals("")) {
                    Toast.makeText(MsoSalesOrderItemActivity.this, "Item can not be added!, Item UOM is not " +
                            "available", Toast.LENGTH_SHORT).show();
                } else {
                    if (tempSalesOrderLine.getUnitPrice() == 0f) {
                        Toast.makeText(MsoSalesOrderItemActivity.this, "Item can not be added!, Item unit price " +
                                "is zero", Toast.LENGTH_SHORT).show();
                    } else {

                        if (quantity == 0f) {
                            Toast.makeText(MsoSalesOrderItemActivity.this, "Item can not be added!, QTY can not " +
                                    "be zero", Toast.LENGTH_SHORT).show();
                        } else {
                            float lineAmount = quantity * tempSalesOrderLine.getUnitPrice();
                            float vatAmount = (lineAmount * 7) / 100;

                            tempSalesOrderLine.setQuantity(quantity);
                            tempSalesOrderLine.setQtytoInvoice(quantity);
                            tempSalesOrderLine.setLineAmount(lineAmount);
                                /*tempSalesOrderLine.setTotalAmountExclVAT(lineAmount);
                                tempSalesOrderLine.setTotalVATAmount(vatAmount);
                                tempSalesOrderLine.setTotalAmountInclVAT(lineAmount + vatAmount);*/

                            String objAsJson = tempSalesOrderLine.toJson();

                            Intent intent = new Intent();
                            intent.putExtra(getResources().getString(R.string.sales_order_line_obj), objAsJson);
                            intent.putExtra(getResources().getString(R.string.adapter_position), position);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }
            } else if (extras.containsKey(getResources().getString(R.string.stock_transfer_request_line))) {
                float lineAmount = quantity * unitPrice;

                tempTransferInLine.setQuantity(quantity);
                String objAsJson = tempTransferInLine.toJson();

                Intent intent = new Intent();
                intent.putExtra(getResources().getString(R.string.stock_transfer_request_line), objAsJson);
                intent.putExtra(getResources().getString(R.string.adapter_position), position);
                setResult(RESULT_OK, intent);
                finish();
            } else if (extras.containsKey(getResources().getString(R.string.stock_request_line_jason_obj))) {
                if (tempStockRequestLine.getUnitofMeasureCode().equals("")) {
                    Toast.makeText(MsoSalesOrderItemActivity.this, "Item can not be added!, Item UOM is non",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (tempStockRequestLine.getUnitPrice() == 0f) {
                        Toast.makeText(MsoSalesOrderItemActivity.this, "Item can not be added!, Item unit price " +
                                "is zero", Toast.LENGTH_SHORT).show();
                    } else {

                        if (quantity == 0f) {
                            Toast.makeText(MsoSalesOrderItemActivity.this, "Item can not be added!, QTY can not " +
                                    "be zero", Toast.LENGTH_SHORT).show();
                        } else {
                            float lineAmount = quantity * tempStockRequestLine.getUnitPrice();

                            tempStockRequestLine.setQuantity(quantity);
                            tempStockRequestLine.setAmount(lineAmount);
                            tempStockRequestLine.setLineAmount(lineAmount);

                            String objAsJson = tempStockRequestLine.toJson();

                            Intent intent = new Intent();
                            intent.putExtra(getResources().getString(R.string.stock_request_line_jason_obj),
                                    objAsJson);
                            intent.putExtra(getResources().getString(R.string.adapter_position), position);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }
            }

        } else if (findViewById(R.id.btnBack) == v) {
           onBackPressed();

        }
        else if (findViewById(R.id.btnBack) == v) {
            onBackPressed();

        }
    }

    private void txtQtyAddTextChangedListenerfor()
    {
        txtQTY.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Total Price
                if (!txtQTY.getText().toString().equals("")) {
                    enteredQuantity = Float.parseFloat(txtQTY.getText().toString());

                    if (extras.containsKey(getResources().getString(R.string.stock_transfer_request_line)))
                    {
                        totalPrice = enteredQuantity * unitPrice;
                    }
                    else if (extras.containsKey(getResources().getString(R.string.stock_request_line_jason_obj)))
                    {
                        totalPrice = enteredQuantity * tempStockRequestLine.getUnitPrice();
                    }
                    else
                    {
                        unitPrice = getUnitPriceWithQuantity(tempSalesOrderLine.getNo()
                                                            , tempCustomer.getCustomerPriceGroup()
                                                            , tempCustomer.getCode()
                                                            , tempSalesOrderLine.getUnitofMeasure()
                                                            , Math.round(enteredQuantity));
                        tempSalesOrderLine.setUnitPrice(unitPrice);
                        txtUnitPrice.setText(String.format("%.2f", tempSalesOrderLine.getUnitPrice()));
                        totalPrice = enteredQuantity * tempSalesOrderLine.getUnitPrice();
                    }


                    txtTotalPrice.setText(String.format("%.2f", totalPrice));
                    quantity = enteredQuantity;
                } else {
                    totalPrice = 0;
                    txtTotalPrice.setText(String.format("%.2f", totalPrice));
                    quantity = 0;
                }
            }
        });
    }

    private void startItemBalanceDownload() {

        if (extras.containsKey("existSalesOrderLineJasonObj")) {
            this.mItemNo = tempSalesOrderLine.getNo();
        } else if (extras.containsKey("existStockRequestLineJasonObj")) {
            this.mItemNo = tempStockRequestLine.getItemNo();
        } else if (extras.containsKey(getResources().getString(R.string.stock_transfer_request_line))) {
            this.mItemNo = tempTransferInLine.getItemNo();
        } else if (extras.containsKey(getResources().getString(R.string.stock_request_line_jason_obj))) {
            this.mItemNo = tempStockRequestLine.getItemNo();
        }else if (extras.containsKey(getResources().getString(R.string.sales_order_line_obj))) {
            this.mItemNo = tempSalesOrderLine.getNo();
        }

        itemBalancePdaSyncTask = new ItemBalancePdaSyncTask(getApplicationContext(), true, this.mItemNo);
        itemBalancePdaSyncTask.delegate = MsoSalesOrderItemActivity.this;
        itemBalancePdaSyncTask.execute((Void) null);

    }

    public void getUomList(String itemNo, Customer customer) {
        salesPricesList = new ArrayList<SalesPrices>();
        if (customer != null) {
            SalesPricesDbHandler spDb = new SalesPricesDbHandler(getApplicationContext());
            spDb.open();
            String customerPriceGroup = customer.getCustomerPriceGroup();

            //if customer price group is "" then sales type is 0.
            if (customer.getCustomerPriceGroup().equals("")) {
                customerPriceGroup = customer.getCode();
            }

            salesPricesList = spDb.getAllPriceList(customerPriceGroup, itemNo);

            spDb.close();
        }
    }

    public void getUomListByItemNo(String itemNo) {
        uomList = new ArrayList<ItemUom>();
        if (itemNo != null) {
            ItemUomDbHandler itemUOMDb = new ItemUomDbHandler(getApplicationContext());
            itemUOMDb.open();

            uomList = itemUOMDb.getUomListbyItemCode(itemNo);

            itemUOMDb.close();
        }
    }

    private String getItemWarehouseQty(String itemCode) {
        String qty = "0.0";
        ItemBalancePdaDbHandler dbAdapter = new ItemBalancePdaDbHandler(this);
        dbAdapter.open();

        qty = dbAdapter.getItemWarehouseQtyByItemCode(itemCode);

        dbAdapter.close();
        return qty;
    }

    private void getItem(String Code) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(this);
        dbAdapter.open();

        tempItem = dbAdapter.getItemByItemCode(Code);

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

    public float getUnitPrice(String itemCode, String customerPriceGroup, String customerCode, String itemUom) {
        float itemMasterUnitPrice = 0, groupItemPrice = 0, customerItemPrice = 0;

        //1 ItemMaster UnitPrice
        itemMasterUnitPrice = Float.parseFloat(getItemUnitPriceFromItemMaster(itemCode, itemUom) == "" ? "0" :
                getItemUnitPriceFromItemMaster(itemCode, itemUom));

        //2 Group ItemPrice
        groupItemPrice = Float.parseFloat(getGroupItemPrice(customerPriceGroup, 1, itemCode, deliveryDate, itemUom)
                == "" ? "0" : getGroupItemPrice(customerPriceGroup, 1, itemCode, deliveryDate, itemUom));

        //3 customer ItemPrice
        customerItemPrice = Float.parseFloat(getCustomerItemPrice(customerCode, 0, itemCode, deliveryDate, itemUom)
                == "" ? "0" : getCustomerItemPrice(customerCode, 0, itemCode, deliveryDate, itemUom));

        float minimunItemPrice = getMinItemPrice(itemMasterUnitPrice, groupItemPrice, customerItemPrice);

        return minimunItemPrice;
    }

    public float getMinItemPrice(float a_, float b_, float c_) {
        float a = a_, b = b_, c = c_;
        boolean isAZero = false, isBZero = false, isCZero = false;

        if (a == 0) {
            isAZero = true;
        }

        if (b == 0) {
            isBZero = true;
        }

        if (c == 0) {
            isCZero = true;
        }

        if (isAZero && isBZero && isCZero) {
            return 0;
        } else {
            if (isAZero && isBZero && !isCZero) {
                return c;
            }
            if (isAZero && !isBZero && isCZero) {
                return b;
            }
            if (!isAZero && isBZero && isCZero) {
                return a;
            }

            if (isAZero && !isBZero && !isCZero) {
                return Math.min(b, c);
            }
            if (!isAZero && !isBZero && isCZero) {
                return Math.min(a, b);
            }
            if (!isAZero && isBZero && !isCZero) {
                return Math.min(a, c);
            }
            if (!isAZero && !isBZero && !isCZero) {
                return Math.min(a, Math.min(b, c));
            }
        }
        return 0;
    }

    public String getCustomerItemPrice(String customerCode, int salesType, String itemCode, String deliveryDate,
                                       String itemUom) {
        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
        db.open();

        String itemPrice = db.getCustomerItemUnitPriceByCustomerCode(customerCode, salesType, itemCode, deliveryDate,
                itemUom);

        db.close();

        return itemPrice;
    }

    public String getGroupItemPrice(String customerGroup, int salesType, String itemCode, String deliveryDate, String
            itemUom) {
        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
        db.open();

        String itemPrice = db.getGroupItemPriceByCustomePriceGroup(customerGroup, salesType, itemCode, deliveryDate,
                itemUom);

        db.close();

        return itemPrice;
    }

    public String getItemUnitPriceFromItemMaster(String itemCode, String itemUom) {
        ItemDbHandler db = new ItemDbHandler(getApplicationContext());
        db.open();

        String unitprice = db.getItemPriceByItemCode(itemCode, itemUom);
        db.close();

        return unitprice;
    }

    //getUnitPrice method 2 ------------------------------------------------------------------------
    public float getUnitPriceWithQuantity(
            String itemCode
            , String customerPriceGroup
            , String customerCode
            , String itemUom
            , int quantity) {

        float itemMasterUnitPrice = 0, groupItemPrice = 0, customerItemPrice = 0;

        List<SalesPrices> customerGroupPriceList = new ArrayList<SalesPrices>(),
                customerPriceList = new ArrayList<SalesPrices>() ;

        //1 ItemMaster UnitPrice
        itemMasterUnitPrice = Float.parseFloat(getItemUnitPriceFromItemMaster(itemCode
                , itemUom) == "" ? "0" : getItemUnitPriceFromItemMaster(itemCode, itemUom));


        customerGroupPriceList = getGroupItemPriceList(customerPriceGroup
                , 1
                , itemCode
                , deliveryDate
                , itemUom);

        customerPriceList = getCustomerItemPriceList(customerCode
                , 0
                , itemCode
                , deliveryDate
                , itemUom);

        //2 Customer group unit price
        if(!customerGroupPriceList.isEmpty())
        {
            List<Float> priceList = new ArrayList<Float>();

            for(int i=0; i<customerGroupPriceList.size(); i++)
            {
                SalesPrices sp = customerGroupPriceList.get(i);
                int minimumQty = Math.round(Float.parseFloat(sp.getMinimumQuantity()));

                if(minimumQty <= quantity)
                {
                    priceList.add(Float.parseFloat(sp.getPublishedPrice()));
                }
            }

            if(priceList.size()==0)
            {
                groupItemPrice=0;
            }
            else if(priceList.size() == 1)
            {
                groupItemPrice = priceList.get(0);
            }
            else
            {
                Collections.sort(priceList);
                groupItemPrice = priceList.get(0);
            }
        }
        else
        {
            groupItemPrice = 0;
        }

        //3 Customer unit price
        if(!customerPriceList.isEmpty())
        {
            List<Float> priceList = new ArrayList<Float>();

            for(int i=0; i<customerPriceList.size(); i++)
            {
                SalesPrices sp = customerPriceList.get(i);
                int minimumQty = Math.round(Float.parseFloat(sp.getMinimumQuantity()));

                if(minimumQty <= quantity)
                {
                    priceList.add(Float.parseFloat(sp.getPublishedPrice()));
                }
            }

            if(priceList.size()==0)
            {
                customerItemPrice=0;
            }
            else if(priceList.size() == 1)
            {
                customerItemPrice = priceList.get(0);
            }
            else
            {
                Collections.sort(priceList);
                customerItemPrice = priceList.get(0);
            }
        }
        else
        {
            customerItemPrice = 0;
        }


        float minimunItemPrice = getMinItemPrice(itemMasterUnitPrice
                , groupItemPrice
                , customerItemPrice);

        return minimunItemPrice;
    }

    public List<SalesPrices> getCustomerItemPriceList(String customerCode
            , int salesType
            , String itemCode
            , String deliveryDate
            , String itemUom) {
        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
        db.open();

        List<SalesPrices> list= db.getCustomerPriceList(customerCode
                , salesType
                , itemCode
                , deliveryDate
                , itemUom);

        db.close();

        return list;
    }

    public List<SalesPrices> getGroupItemPriceList(String customerGroup
            , int salesType
            , String itemCode
            , String deliveryDate
            , String itemUom) {

        SalesPricesDbHandler db = new SalesPricesDbHandler(getApplicationContext());
        db.open();

        List<SalesPrices> list = db.getGroupPriceList(customerGroup
                , salesType
                , itemCode
                , deliveryDate
                , itemUom);

        db.close();

        return list;
    }
    //------------------------------getUnitPrices with quantity-----------End---------

    private void setSalesOrderLineEditFields() {
        String objAsJson = extras.getString(getResources().getString(R.string.sales_order_line_obj));

        //Selected Item postion in the List. do not change this value !!
        position = extras.getInt(getResources().getString(R.string.adapter_position));

        if (existSalesOrderLine != null) {
            if (existSalesOrderLine.getNo() != null) {
                tempSalesOrderLine = existSalesOrderLine;

            } else {
                tempSalesOrderLine = SalesOrderLine.fromJson(objAsJson);
            }
        } else {
            tempSalesOrderLine = SalesOrderLine.fromJson(objAsJson);
        }

        getItem(tempSalesOrderLine.getNo());

        if (extras.containsKey(getResources().getString(R.string.customer_json_obj))) {
            tempCustomer = Customer.fromJson(extras.getString(getResources().getString(R.string.customer_json_obj)));
        }

        header = tempSalesOrderLine.getNo();
        if (tempItem.getIdentifierCode() != null) {
            if (!tempItem.getIdentifierCode().isEmpty()) {
                header = header + " - " + tempItem.getIdentifierCode();
            }
        }

        quantity = tempSalesOrderLine.getQuantity();

        tvSalesitemHeader.setText(header);
        txtItemDesc.setText(tempSalesOrderLine.getDescription());

        //Get uom list base on itemCode and salesCode
        List<String> uomList = new ArrayList<String>();
        getUomList(tempSalesOrderLine.getNo(), tempCustomer);
        if (!salesPricesList.isEmpty()) {
            for (SalesPrices sp : salesPricesList) {
                if (sp.getUnitOfMeasureCode() == null) {
                    uomList.add("");
                } else {
                    uomList.add(sp.getUnitOfMeasureCode());
                }
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, uomList);
        //txtUOM.setText(tempSalesOrderLine.getUnitofMeasure());

        //Setting uom adapter to Spinner
        spnUom.setAdapter(adapter);

        // Setting default item in spinner
        int getDegaultItemPosition = adapter.getPosition(tempSalesOrderLine.getUnitofMeasure());
        spnUom.setSelection(getDegaultItemPosition);

        // Get seleted uom  from Spinner
        spnUom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedUom = spnUom.getSelectedItem().toString();

                tempSalesOrderLine.setUnitofMeasure(selectedUom);

                //e_unitPrice = getUnitPrice(tempSalesOrderLine.getNo(), tempCustomer.getCustomerPriceGroup(),
                //       tempCustomer.getCode(), selectedUom);
                unitPrice = getUnitPriceWithQuantity(tempSalesOrderLine.getNo()
                                                , tempCustomer.getCustomerPriceGroup()
                                                , tempCustomer.getCode()
                                                , selectedUom
                                                , Math.round(tempSalesOrderLine.getQuantity()));
                tempSalesOrderLine.setUnitPrice(unitPrice);

                txtUnitPrice.setText(String.format("%.2f", tempSalesOrderLine.getUnitPrice()));
                totalPrice = quantity * tempSalesOrderLine.getUnitPrice();
                txtTotalPrice.setText(String.format("%.2f", totalPrice));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        txtUnitPrice.setText(String.format("%.2f", tempSalesOrderLine.getUnitPrice()));
        txtQTY.setText(String.valueOf(Math.round(tempSalesOrderLine.getQuantity())));
        //txtWarehouseQTY.setText(getItemWarehouseQty(tempTransferInLine.getItemNo()));

        //Total Price
        totalPrice = tempSalesOrderLine.getQuantity() * tempSalesOrderLine.getUnitPrice();
        txtTotalPrice.setText(String.format("%.2f", totalPrice));

        ivItemImage.setImageBitmap(loadImageFromStorage(tempSalesOrderLine.getNo()));
    }

    //stock transfer module / edited by buddhika
    private void setStockRequestNewFields() {
        String objAsJson = extras.getString(getResources().getString(R.string.stock_transfer_request_line));

        //Selected Item postion in the List. do not change this value !!
        position = extras.getInt(getResources().getString(R.string.adapter_position));

        tempTransferInLine = StockTransferRequestLine.fromJson(objAsJson);

        getItem(tempTransferInLine.getItemNo());

        header = tempTransferInLine.getItemNo();

        if (tempItem.getIdentifierCode() != null) {
            if (!tempItem.getIdentifierCode().isEmpty()) {
                header = header + " - " + tempItem.getIdentifierCode();
            }
        }

        quantity = tempTransferInLine.getQuantity();

        //Get uom list base on itemCode
        List<String> uomList_ = new ArrayList<String>();
        getUomListByItemNo(tempTransferInLine.getItemNo());
        if (!uomList.isEmpty()) {
            for (ItemUom iu : uomList) {
                if (iu.getUomCode() == null) {
                    uomList_.add("");
                } else {
                    uomList_.add(iu.getUomCode());
                }
            }
        }

        if(uomList_.isEmpty())
        {
            uomList_.add(tempTransferInLine.getUnitofMeasure());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, uomList_);

        //Setting uom adapter to Spinner
        spnUom.setAdapter(adapter);

        // Setting default item in spinner
        int getDegaultItemPosition = adapter.getPosition(tempTransferInLine.getUnitofMeasure());
        spnUom.setSelection(getDegaultItemPosition);

        // Get seleted uom  from Spinner
        spnUom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedUom = spnUom.getSelectedItem().toString();
                tempTransferInLine.setUnitofMeasure(selectedUom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        tvSalesitemHeader.setText(header);
        txtItemDesc.setText(tempTransferInLine.getItemDescription());
        txtUnitPrice.setText(String.valueOf(tempItem.getUnitPrice()));
        txtQTY.setText(String.valueOf(Math.round(tempTransferInLine.getQuantity())));
        txtWarehouseQTY.setText(getItemWarehouseQty(tempTransferInLine.getItemNo()));

        //Total Price
        if (tempItem.getUnitPrice() != null)
            unitPrice = Float.parseFloat(tempItem.getUnitPrice());

        totalPrice = tempTransferInLine.getQuantity() * unitPrice;
        txtTotalPrice.setText(String.format("%.2f", totalPrice));

        ivItemImage.setImageBitmap(loadImageFromStorage(tempTransferInLine.getItemNo()));
    }

    private void setStockRequestEditFields() {
        String objAsJson = extras.getString(getResources().getString(R.string.stock_request_line_jason_obj));

        mTvItemNo.setVisibility(View.VISIBLE);
        //Selected Item postion in the List. do not change this value !!
        position = extras.getInt(getResources().getString(R.string.adapter_position));

        if (existStockRequestLine != null) {
            if (existStockRequestLine.getItemNo() != null) {
                tempStockRequestLine = existStockRequestLine;

            } else {
                tempStockRequestLine = StockRequestLine.fromJson(objAsJson);
            }
        } else {
            tempStockRequestLine = StockRequestLine.fromJson(objAsJson);
        }

        getItem(tempStockRequestLine.getItemNo());

        if (extras.containsKey(getResources().getString(R.string.customer_json_obj))) {
            tempCustomer = Customer.fromJson(extras.getString(getResources().getString(R.string.customer_json_obj)));
        }

        header = tempStockRequestLine.getItemCrossReferenceNo();
        if (tempItem.getIdentifierCode() != null) {
            if (!tempItem.getIdentifierCode().isEmpty()) {
                header = header + " - " + tempItem.getIdentifierCode();
            }
        }

        quantity = tempStockRequestLine.getQuantity();

        tvSalesitemHeader.setText(header);
        txtItemDesc.setText(tempStockRequestLine.getItemCrossReferenceDescription());
        mTvItemNo.setText(tempStockRequestLine.getItemNo());
        //Get uom list base on itemCode and salesCode
        List<String> uomList = new ArrayList<String>();
        getUomList(tempStockRequestLine.getItemNo(), tempCustomer);
        if (!salesPricesList.isEmpty()) {
            for (SalesPrices sp : salesPricesList) {
                if (sp.getUnitOfMeasureCode() == null) {
                    uomList.add("");
                } else {
                    uomList.add(sp.getUnitOfMeasureCode());
                }
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, uomList);
        //txtUOM.setText(tempSalesOrderLine.getUnitofMeasure());

        //Setting uom adapter to Spinner
        spnUom.setAdapter(adapter);

        // Setting default item in spinner
        int getDegaultItemPosition = adapter.getPosition(tempStockRequestLine.getUnitofMeasureCode());
        spnUom.setSelection(getDegaultItemPosition);

        // Get seleted uom  from Spinner
        spnUom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedUom = spnUom.getSelectedItem().toString();

                tempStockRequestLine.setUnitofMeasureCode(selectedUom);

                /*e_unitPrice = getUnitPrice(tempStockRequestLine.getItemNo(), tempCustomer.getCustomerPriceGroup(),
                        tempCustomer.getCode(), selectedUom);*/

                unitPrice = getUnitPriceWithQuantity(tempStockRequestLine.getItemNo()
                        , tempCustomer.getCustomerPriceGroup()
                        , tempCustomer.getCode()
                        , selectedUom
                        , Math.round(quantity));

                tempStockRequestLine.setUnitPrice(unitPrice);

                txtUnitPrice.setText(String.format("%.2f", tempStockRequestLine.getUnitPrice()));
                totalPrice = quantity * tempStockRequestLine.getUnitPrice();
                txtTotalPrice.setText(String.format("%.2f", totalPrice));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        txtUnitPrice.setText(String.format("%.2f", tempStockRequestLine.getUnitPrice()));
        txtQTY.setText(String.valueOf(Math.round(tempStockRequestLine.getQuantity())));
        //txtWarehouseQTY.setText(getItemWarehouseQty(tempStockRequestLine.getItemNo()));

        //Total Price
        totalPrice = tempStockRequestLine.getQuantity() * tempStockRequestLine.getUnitPrice();
        txtTotalPrice.setText(String.format("%.2f", totalPrice));

        ivItemImage.setImageBitmap(loadImageFromStorage(tempStockRequestLine.getItemNo()));
    }

}
