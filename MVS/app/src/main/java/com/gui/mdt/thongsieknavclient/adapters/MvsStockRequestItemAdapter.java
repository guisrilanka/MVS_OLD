package com.gui.mdt.thongsieknavclient.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequestLine;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.GSTPostingSetupDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesPricesDbHandler;
import com.gui.mdt.thongsieknavclient.ui.MsoSalesOrderItemActivity;
import com.gui.mdt.thongsieknavclient.ui.MvsStockRequestActivity;

import java.util.List;

/**
 * Created by User on 10/24/2017.
 */

public class MvsStockRequestItemAdapter extends RecyclerView.Adapter<MvsStockRequestItemAdapter.StockItemViewHolder> {

    private static final int MVS_STOCK_REQUEST_ITEM_ACTIVITY_RESULT_CODE = 3;
    private List<StockRequestLine> stockRequestLineList;
    private int rowLayout;
    private Activity activity;
    private String status="", deliveryDate = "",formName = "";
    private Customer tempCustomer = new Customer();
    private float mEnteredQuantity=0f, mTotalPrice=0, mVatAmt=0f, mTotalAmtInclVat=0f;


    @Override
    public MvsStockRequestItemAdapter.StockItemViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final MvsStockRequestItemAdapter.StockItemViewHolder viewHolder = new MvsStockRequestItemAdapter.StockItemViewHolder(view);

        viewHolder.tvItemNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                if(status.equals(activity.getResources().getString(R.string.StockRequestStatusPending))
                        || status.equals(activity.getResources().getString(R.string.StockRequestStatusComplete)) )
                {
                    int position = viewHolder.getAdapterPosition();

                    StockRequestLine stockRequestLine = stockRequestLineList.get(position);
                    String ObjToJason = stockRequestLine.toJson();
                    if(tempCustomer !=  null) {
                        String customerJsonObj = tempCustomer.toJson();

                        Intent intent = new Intent(activity, MsoSalesOrderItemActivity.class);
                        intent.putExtra(activity.getResources().getString(R.string.stock_request_line_jason_obj), ObjToJason);
                        intent.putExtra(activity.getResources().getString(R.string.adapter_position), position);
                        intent.putExtra(activity.getResources().getString(R.string.customer_json_obj), customerJsonObj);
                        intent.putExtra("deliveryDate", deliveryDate);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivityForResult(intent, MVS_STOCK_REQUEST_ITEM_ACTIVITY_RESULT_CODE);

                    }
                }
            }
        });

        viewHolder.txtQTY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.txtQTY.requestFocus();
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });

        viewHolder.txtQTY.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    viewHolder.txtQTY.clearFocus();
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                else {
                    viewHolder.txtQTY.requestFocus();
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                }
            }
        });

        viewHolder.txtQTY.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    int position = viewHolder.getAdapterPosition();
                    StockRequestLine srl = stockRequestLineList.get(position);

                    if (!viewHolder.txtQTY.getText().toString().equals("")) {

                        SalesPricesDbHandler spdb = new SalesPricesDbHandler(activity);
                        spdb.open();
                        Item mTempItem  = getItemByCode(srl.getItemNo(), activity);
                        float itemUnitePrice = 0f;
                        if(mTempItem.isInventoryValueZero()){
                            itemUnitePrice = 0f;
                        }else {
                            itemUnitePrice = spdb.getUnitPriceWithQuantity(srl.getItemNo(),
                                tempCustomer.getCustomerPriceGroup(),
                                tempCustomer.getCode(),
                                srl.getUnitofMeasureCode() == null ? "" : srl.getUnitofMeasureCode(),
                                Integer.valueOf(viewHolder.txtQTY.getText().toString()),
                                deliveryDate
                        ); }
                        spdb.close();

                        float unitPrice = floatRound(itemUnitePrice,2);

                        float gstPresentage = getGstPercentage(tempCustomer.getCode(), srl.getItemNo());

                        srl.setUnitPrice(unitPrice);

                        mEnteredQuantity = Float.parseFloat(viewHolder.txtQTY.getText().toString());

                        float totalPrice_ = Math.round(mEnteredQuantity) * srl.getUnitPrice();
                        mTotalPrice = floatRound(totalPrice_,2);

                        float vatAmt_ = (mTotalPrice * gstPresentage) / 100;
                        mVatAmt = floatRound(vatAmt_,2);

                        float totalAmtInclVat_ = mVatAmt + mTotalPrice;
                        mTotalAmtInclVat = floatRound(totalAmtInclVat_,2);

                        srl.setQuantity(mEnteredQuantity);
                        srl.setLineAmount(mTotalPrice);
                        srl.setAmount(mTotalPrice);
                        srl.setTotalVATAmount(mVatAmt);
                        srl.setTotalAmountInclVAT(mTotalAmtInclVat);

                        stockRequestLineList.set(position, srl);

                        viewHolder.tvPrice.setText(String.format("%.2f", unitPrice));
                        viewHolder.tvTotal.setText(String.format("%.2f", mTotalPrice));

                        applyColorCodes(srl.getQuantity(),viewHolder);

                        MvsStockRequestActivity mvsStockRequestActivity = (MvsStockRequestActivity) activity;
                        mvsStockRequestActivity.updateSummeryValues(stockRequestLineList);
                        mvsStockRequestActivity.isSaved = false;
                    } else {
                        mEnteredQuantity = 0f;

                        SalesPricesDbHandler spdb = new SalesPricesDbHandler(activity);
                        spdb.open();
                        Item mTempItem  = getItemByCode(srl.getItemNo(), activity);
                        float itemUnitePrice = 0f;
                        if(mTempItem.isInventoryValueZero()){
                            itemUnitePrice = 0f;
                        }else {
                            itemUnitePrice = spdb.getUnitPriceWithQuantity(srl.getItemNo(),
                                tempCustomer.getCustomerPriceGroup(),
                                tempCustomer.getCode(),
                                srl.getUnitofMeasureCode() == null ? "" : srl.getUnitofMeasureCode(),
                                Math.round(mEnteredQuantity),
                                deliveryDate
                        ); }
                        spdb.close();

                        srl.setUnitPrice(floatRound(itemUnitePrice,2));

                        srl.setQuantity(0f);
                        srl.setLineAmount(0f);
                        srl.setAmount(0f);
                        srl.setTotalVATAmount(0f);
                        srl.setTotalAmountInclVAT(0f);

                        stockRequestLineList.set(position, srl);

                        viewHolder.tvPrice.setText(String.format("%.2f", itemUnitePrice));
                        viewHolder.tvTotal.setText(String.format("%.2f", 0f));

                        applyColorCodes(srl.getQuantity(),viewHolder);

                        MvsStockRequestActivity mvsStockRequestActivity = (MvsStockRequestActivity) activity;
                        mvsStockRequestActivity.updateSummeryValues(stockRequestLineList);
                        mvsStockRequestActivity.isSaved = false;
                    }
                }
                return false;
            }
        });

        if(status.equals(activity.getResources().getString(R.string.StockRequestStatusConfirmed)) )
        {
            viewHolder.txtQTY.setEnabled(false);
        }

        return viewHolder;
    }

    private Item getItemByCode(String itemCode, Context activity) {
        boolean status = false;
        ItemDbHandler dbAdapter = new ItemDbHandler(activity);
        dbAdapter.open();

        Item mTempItem = dbAdapter.getItemByCode(itemCode);

        dbAdapter.close();

        return mTempItem;
    }

    public float floatRound(float value_, int places) {

        double value = Double.parseDouble(String.valueOf(value_));
        float result = 0f;

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        result = Float.parseFloat(String.valueOf((double) tmp / factor));

        return result;
    }

    public float getGstPercentage(String customerNo, String itemNo) {
        String busPostingPrecent = null, productPostingPrecent = null;
        float gSTPercentage = 0;

        GSTPostingSetupDbHandler gstDb = new GSTPostingSetupDbHandler(activity);
        CustomerDbHandler customerDb = new CustomerDbHandler(activity);
        ItemDbHandler itemDb = new ItemDbHandler(activity);

        customerDb.open();
        Customer cusObj = customerDb.getCustomerByCustomerCode(customerNo);
        customerDb.close();

        itemDb.open();
        Item itemObj = itemDb.getItemByItemCode(itemNo);
        itemDb.close();

        if (cusObj.getCode() != null) {
            busPostingPrecent = cusObj.getVatBusPostingGroup();
        }

        if (itemObj.getItemCode() != null) {
            productPostingPrecent = itemObj.getVatProdPostingGroup();
        }

        gstDb.open();

        gSTPercentage = Float.valueOf(gstDb.getGSTPrecent(busPostingPrecent, productPostingPrecent));

        gstDb.close();

        return gSTPercentage;
    }

    public MvsStockRequestItemAdapter(List<StockRequestLine> stockRequestLineList_, int rowLayout_, Activity activity_, String status_, Customer customer_, String deliveryDate_,String formName_) {
        this.stockRequestLineList = stockRequestLineList_;
        this.rowLayout = rowLayout_;
        this.activity = activity_;
        this.status = status_;
        this.tempCustomer = customer_;
        this.deliveryDate = deliveryDate_;
        this.formName = formName_;
    }

    @Override
    public void onBindViewHolder(MvsStockRequestItemAdapter.StockItemViewHolder holder, int position) {

        float totalAmount_ = floatRound(stockRequestLineList.get(position).getUnitPrice(),2) * Math.round(stockRequestLineList.get(position).getQuantity());
        float totalAmount = floatRound(totalAmount_,2);

        holder.tvNo.setText(String.valueOf(position + 1));
        //holder.tvItemNo.setText(stockRequestLineList.get(position).getItemNo());
        holder.tvPrice.setText(String.format("%.2f",stockRequestLineList.get(position).getUnitPrice()));
        holder.tvUom.setText(stockRequestLineList.get(position).getUnitofMeasureCode());
        holder.txtQTY.setText(String.valueOf(Math.round(stockRequestLineList.get(position).getQuantity())));
        holder.tvTotal.setText(String.format("%.2f", totalAmount));
        //holder.tvItemDescroption.setText(stockRequestLineList.get(position).getDescription());

        holder.tvItemNo.setText(stockRequestLineList.get(position).getItemCrossReferenceNo());
        holder.tvItemDescroption.setText(stockRequestLineList.get(position).getItemNo()+" - "+
                stockRequestLineList.get(position).getItemCrossReferenceDescription());

        applyColorCodes(stockRequestLineList.get(position).getQuantity(), holder);
    }

    //Apply color codes on Qty
    public void applyColorCodes(float qty
            , MvsStockRequestItemAdapter.StockItemViewHolder holder) {

        if (qty != 0) {
            holder.txtQTY.setBackgroundColor(activity.getResources().getColor(R.color.springGreen));
        } else {
            holder.txtQTY.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return stockRequestLineList.size();
    }

    public static class StockItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvNo, tvItemNo, tvPrice, tvUom, txtQTY, tvTotal, tvItemDescroption;

        public StockItemViewHolder(View v) {
            super(v);

            tvNo = (TextView) v.findViewById(R.id.tvNo);
            tvItemNo = (TextView) v.findViewById(R.id.tvItemNo);
            tvPrice = (TextView) v.findViewById(R.id.tvPrice);
            tvUom = (TextView) v.findViewById(R.id.tvUom);
            txtQTY = (EditText) v.findViewById(R.id.txtQty);
            tvTotal = (TextView) v.findViewById(R.id.tvTotal);
            tvItemDescroption = (TextView) v.findViewById(R.id.tvItemDescroption);

            //set maximum num length on Quantity field
            txtQTY.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9) });
        }
    }
}
