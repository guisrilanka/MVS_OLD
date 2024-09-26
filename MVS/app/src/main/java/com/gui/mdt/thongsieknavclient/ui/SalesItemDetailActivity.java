package com.gui.mdt.thongsieknavclient.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;
import com.gui.mdt.thongsieknavclient.datamodel.ItemUom;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemBalancePdaDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemCategoryDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemUomDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.ItemBalancePdaSyncTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.util.List;

public class SalesItemDetailActivity extends AppCompatActivity implements AsyncResponse {

    private NavClientApp mApp;
    Toolbar myToolbar;
    TextView tvItemDetail, mTxtItemBarcode;
    Button btnSearchMain;
    String formName = "";
    LinearLayout qtyVehicleLayout, layoutVehicleRetrunQty;
    ItemBalancePdaSyncTask itemBalancePdaSyncTask;
    private String mItemCode;
    private Item tempItem;
    private EditText
            txtItemCode,
            txtItemDescription,
            txtProductGroup,
            txtWarehouseQty,
            txtVehicleQty,
            txtVehicleRetrunQty;
    private ImageView itemImage;
    private TableLayout tableLayoutUom;
    private List<ItemUom> uomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sales_item_detail);
        qtyVehicleLayout = (LinearLayout) findViewById(R.id.qtyVehicleLayout);
        layoutVehicleRetrunQty = (LinearLayout) findViewById(R.id.layoutVehicleRetrunQty);
        this.mApp = (NavClientApp) getApplicationContext();

        tvItemDetail = (TextView) findViewById(R.id.tvItemDetail);

        txtItemCode = (EditText) findViewById(R.id.txtItemCode);
        txtItemDescription = (EditText) findViewById(R.id.txtItemDescription);
        txtProductGroup = (EditText) findViewById(R.id.txtProductGroup);
        txtWarehouseQty = (EditText) findViewById(R.id.txtWarehouseQty);
        txtVehicleQty = (EditText) findViewById(R.id.txtVehicleQty);
        itemImage = (ImageView) findViewById(R.id.itemImg);
        tableLayoutUom = (TableLayout) findViewById(R.id.tableLayoutUom);
        txtVehicleRetrunQty = (EditText) findViewById(R.id.txtVehicleRetrunQty);
        mTxtItemBarcode = (TextView) findViewById(R.id.txtItemBarcode);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("formName")) {
                formName = getIntent().getStringExtra("formName");
            }
            if (extras.containsKey("_itemObject")) {
                String objAsJson = extras.getString("_itemObject");
                tempItem = Item.fromJson(objAsJson);
                this.mItemCode = tempItem.getItemCode();

                txtItemCode.setText(tempItem.getItemCode());
                txtItemDescription.setText(tempItem.getDescription());
                txtProductGroup.setText(tempItem.getProductGroupCode());
                //txtWarehouseQty.setText(getItemWarehouseQty(tempItem.getItemCode()));
                txtVehicleQty.setText("Vehicle Qty");
                txtVehicleRetrunQty.setText("Vehicle Return Qty");
                itemImage.setImageBitmap(loadImageFromStorage(tempItem.getItemCode()));
                mTxtItemBarcode.setText(tempItem.getIdentifierCode() == null ?
                        "" : tempItem.getIdentifierCode());
            }
        }
        if (formName.equals("LdsSalesItem")) {
            qtyVehicleLayout.setVisibility(View.INVISIBLE);
        }
        if (formName.equals("MsoSalesItemSearch")) {
            qtyVehicleLayout.setVisibility(View.GONE);
        }
        if (formName.equals("MvsTransferInItemSearch") || formName.equals("MvsTransferOutItemSearch")) {
            qtyVehicleLayout.setVisibility(View.VISIBLE);
            layoutVehicleRetrunQty.setVisibility(View.VISIBLE);
        }

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        btnSearchMain = (Button) findViewById(R.id.btnSearch);
        Drawable searchDrawable = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_search)
                .color(Color.WHITE)
                .sizeDp(30);
        btnSearchMain.setBackgroundDrawable(searchDrawable);
        btnSearchMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showSearchDialog();
                Intent intent = new Intent(SalesItemDetailActivity.this, SalesItemSearchActivity.class);
                intent.putExtra("IsPopupNeeded", true);
                intent.putExtra("formName", "LdsSalesItem");
                startActivity(intent);
                finish();
            }
        });
        btnSearchMain.setVisibility(View.GONE);
        getUomList(tempItem.getItemCode());
        SetVisibility();
        applyUomTable();
        startItemBalanceDownload();
        txtVehicleQty.setText(getVehicleQty(tempItem.getItemCode()));
        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("image", tempItem.getItemCode());
//                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (itemBalancePdaSyncTask != null) {
            itemBalancePdaSyncTask.cancel(true);
        }
        onBackPressed();
        return true;
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {

        txtWarehouseQty.setText(syncStatus.getScope());
    }

    private void startItemBalanceDownload() {

        if (this.mItemCode.equals("") || this.mItemCode == null) {
            itemBalancePdaSyncTask = new ItemBalancePdaSyncTask(getApplicationContext(), true);
            itemBalancePdaSyncTask.delegate = SalesItemDetailActivity.this;
            itemBalancePdaSyncTask.execute((Void) null);
        } else {
            itemBalancePdaSyncTask = new ItemBalancePdaSyncTask(getApplicationContext(), true, this.mItemCode);
            itemBalancePdaSyncTask.delegate = SalesItemDetailActivity.this;
            itemBalancePdaSyncTask.execute((Void) null);
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

    private String getCategoryDescription(String categoryCode) {
        String categoryDescription = "";
        ItemCategoryDbHandler dbAdapter = new ItemCategoryDbHandler(this);
        dbAdapter.open();

        categoryDescription = dbAdapter.getCategoryByCategoryCode(categoryCode);

        dbAdapter.close();
        return categoryDescription;
    }

    private void getUomList(String itemCode) {
        ItemUomDbHandler dbAdapter = new ItemUomDbHandler(this);
        dbAdapter.open();

        uomList = dbAdapter.getUomListbyItemCode(itemCode);

        dbAdapter.close();
    }

    private void applyUomTable() {
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.0f);

        TableRow tableRow = new TableRow(getApplicationContext());

        TextView lblUnitofMeasure = new TextView(getApplicationContext());
        lblUnitofMeasure.setTextColor(Color.BLACK);
        lblUnitofMeasure.setPadding(20, 20, 20, 20);
        lblUnitofMeasure.setGravity(Gravity.CENTER);
        lblUnitofMeasure.setBackgroundResource(R.drawable.cell_border_header);
        lblUnitofMeasure.setTextSize(16);

        TextView lblConversion = new TextView(getApplicationContext());
        lblConversion.setTextColor(Color.BLACK);
        lblConversion.setPadding(20, 20, 20, 20);
        lblConversion.setGravity(Gravity.CENTER);
        lblConversion.setBackgroundResource(R.drawable.cell_border_header);
        lblConversion.setTextSize(16);

        lblUnitofMeasure.setLayoutParams(params);
        lblConversion.setLayoutParams(params);

        lblUnitofMeasure.setText("Unit of Measure");
        lblConversion.setText("Conversion");

        tableRow.addView(lblUnitofMeasure);
        tableRow.addView(lblConversion);
        tableLayoutUom.addView(tableRow);

        for (int i = 0; i < uomList.size(); i++) {
            TableRow tableRow_ = new TableRow(getApplicationContext());

            TextView lblUOMDescription = new TextView(getApplicationContext());
            lblUOMDescription.setTextColor(Color.BLACK);
            lblUOMDescription.setPadding(20, 20, 20, 20);
            lblUOMDescription.setGravity(Gravity.CENTER);
            lblUOMDescription.setBackgroundResource(R.drawable.cell_border);
            lblUOMDescription.setTextSize(16);

            TextView lblUOMValue = new TextView(getApplicationContext());

            lblUOMValue.setTextColor(Color.BLACK);
            lblUOMValue.setPadding(20, 20, 20, 20);
            lblUOMValue.setGravity(Gravity.CENTER);
            lblUOMValue.setBackgroundResource(R.drawable.cell_border);
            lblUOMValue.setTextSize(16);

            lblUOMDescription.setLayoutParams(params);
            lblUOMValue.setLayoutParams(params);

            lblUOMDescription.setText(uomList.get(i).getUomCode().toString());
            lblUOMValue.setText(String.valueOf(uomList.get(i).getConvertion()));

            tableRow_.addView(lblUOMDescription);
            tableRow_.addView(lblUOMValue);

            tableLayoutUom.addView(tableRow_);
        }
    }

    private Bitmap loadImageFromStorage(String itemCode) {
        Bitmap itemImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable
                .item_no_image);
        File imageFile = new File("/data/data/com.gui.mvs.thongsieknavclient/app_imageDir/" + itemCode + ".png");

        if (imageFile.canRead()) {
            itemImage = BitmapFactory.decodeFile(imageFile.getPath());
        }
        return itemImage;
    }

    private void SetVisibility() {
        if (getIntent().getBooleanExtra("ShowVehicleReturnQty", false)) {
            layoutVehicleRetrunQty.setVisibility(View.VISIBLE);
        }
    }

    private String getVehicleQty(String itemNo) {
        int returnQty = 0;

        ItemBalancePdaDbHandler ibpDb
                = new ItemBalancePdaDbHandler(getApplicationContext());
        ibpDb.open();
        ItemBalancePda itemPdaObj = ibpDb.getItemBalencePda(tempItem.getItemCode(), tempItem.getItemBaseUom(), mApp.getmCurrentDriverCode());
        ibpDb.close();

        float qty = itemPdaObj.getOpenQty() - itemPdaObj.getQuantity();


        return String.valueOf(qty);
    }

}
