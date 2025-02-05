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


public class MvsExchangeOrderItemActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar myToolbar;
    TextView mTvHeader, mTxtItemDesc, mTxtAvailableExchangeQTY, mTvItemNo;
    EditText mTxtExchangeQTY;
    Spinner mSpnUom;
    ImageView mItemImg;
    Button mBtnAdd;
    Drawable mBackArrow;
    SalesOrderLine mTempSalesOrderLine;

    float totalPrice = 0, quantity = 0, unitPrice = 0f, enteredQuantity = 0f;
    int position = 0;
    Bundle extras;

    private SalesOrderLine mExistSalesOrderLine = new SalesOrderLine();

    ExchangeItem mTempExchangeItem;

    private NavClientApp mApp;
    private List<ItemUom> uomList;
    private List<ExchangeItem> exchangeItemList;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvs_exchange_order_item);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mApp = (NavClientApp) getApplicationContext();

        mTvHeader = (TextView) findViewById(R.id.tvHeader);
        mTxtItemDesc = (TextView) findViewById(R.id.txtItemDesc);
        mTvItemNo = (TextView) findViewById(R.id.tvItemNo);

        mTxtExchangeQTY = (EditText) findViewById(R.id.txtExchangeQTY);
        mTxtAvailableExchangeQTY = (TextView) findViewById(R.id.txtAvailableExchangeQTY);

        mSpnUom = (Spinner) findViewById(R.id.spnUom);
        mItemImg = (ImageView) findViewById(R.id.itemImg);
        mBtnAdd = (Button) findViewById(R.id.btnAdd);

        initComponent();

        extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("existSalesOrderLineJasonObj")) {
                mExistSalesOrderLine =
                        SalesOrderLine.fromJson(extras.getString("existSalesOrderLineJasonObj"));
            }
        }

        mBtnAdd.setOnClickListener(this);

        initSalesQtyTextListner();
        initSalesOrderLine();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mTxtExchangeQTY.requestFocus();

    }

    public void initComponent() {
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBackArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE).sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(mBackArrow);

    }


    public void initSalesOrderLine() {

        String objAsJson = extras.getString(getResources().getString(R.string.sales_order_line_obj));

        //Selected Item postion in the List. do not change this value !!
        position = extras.getInt(getResources().getString(R.string.adapter_position));

        if (mExistSalesOrderLine != null) {
            if (mExistSalesOrderLine.getNo() != null) {
                mTempSalesOrderLine = mExistSalesOrderLine;

            } else {
                mTempSalesOrderLine = SalesOrderLine.fromJson(objAsJson);
            }
        } else {
            mTempSalesOrderLine = SalesOrderLine.fromJson(objAsJson);
        }

        mTxtItemDesc.setText(mTempSalesOrderLine.getDescription());
        mTvItemNo.setText(mTempSalesOrderLine.getNo());
        mTxtExchangeQTY.setText(String.format("%.2f", mTempSalesOrderLine.getQuantity()));

        //Get uom list base on itemCode and salesCode
        List<String> uomList = new ArrayList<String>();
        getExchangeItemListByItemNo(mTempSalesOrderLine.getNo());

        if(!exchangeItemList.isEmpty()){
            for(ExchangeItem ei:exchangeItemList){
                if(ei.getUom()==null){
                    uomList.add("");
                }else{
                    uomList.add(ei.getUom());
                }
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, uomList);
//
        //Setting uom adapter to Spinner
        mSpnUom.setAdapter(adapter);

        // Setting default item in spinner
        int getDegaultItemPosition = adapter.getPosition(mTempSalesOrderLine.getUnitofMeasure());
        mSpnUom.setSelection(getDegaultItemPosition);

        // Get seleted uom  from Spinner
        mSpnUom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedUom = mSpnUom.getSelectedItem().toString();

                setSelectedExchangeItemByUom(selectedUom);


                mTxtAvailableExchangeQTY.setText(String.format("%.2f", mTempExchangeItem.getBalanceQty()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        mItemImg.setImageBitmap(loadImageFromStorage(mTempSalesOrderLine.getNo()));

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

                    quantity = enteredQuantity;
                } else {

                    quantity = 0;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (findViewById(R.id.btnAdd) == v) {
            if (extras.containsKey(getResources().getString(R.string.sales_order_line_obj))) {
                if (mTempSalesOrderLine.getUnitofMeasure().equals("")) {
                    Toast.makeText(MvsExchangeOrderItemActivity.this, "Item can not be added!, Item UOM is not " +
                            "available", Toast.LENGTH_SHORT).show();
                } else {

                            float salesQty = 0f;
                            if(mTxtExchangeQTY.getText().toString().equals(""))
                            {
                                salesQty = 0f;
                            }
                            else
                            {
                                salesQty = Float.parseFloat(mTxtExchangeQTY.getText().toString());
                            }


                            if(salesQty <= mTempExchangeItem.getBalanceQty())
                            {
                                mTempSalesOrderLine.setUnitPrice(0f);
                                mTempSalesOrderLine.setQuantity(salesQty);
                                mTempSalesOrderLine.setQtytoInvoice(salesQty);
                                mTempSalesOrderLine.setExchangeItem(true);

                                String objAsJson = mTempSalesOrderLine.toJson();

                                Intent intent = new Intent();
                                intent.putExtra(getResources().getString(R.string.sales_order_line_obj), objAsJson);
                                intent.putExtra(getResources().getString(R.string.adapter_position), position);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            else {
                                Toast.makeText(MvsExchangeOrderItemActivity.this
                                        , "Item can not be added!, Exchange quantity is greater than balance quantity"
                                        , Toast.LENGTH_SHORT).show();
                            }
                       /* }*/
//                    }
                }
            }
        }
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


    public void getExchangeItemListByItemNo(String itemNo) {
        uomList = new ArrayList<ItemUom>();
        ExchangeItemDbHandler dbAdapter=new ExchangeItemDbHandler(this);
        dbAdapter.open();
        exchangeItemList=dbAdapter.getExchangeItemByItemCode(itemNo);
        dbAdapter.close();

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

        onBackPressed();
        finish();
        return true;
    }

}
