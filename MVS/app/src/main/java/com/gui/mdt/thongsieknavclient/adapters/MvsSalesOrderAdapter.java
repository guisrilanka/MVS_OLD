package com.gui.mdt.thongsieknavclient.adapters;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.GSTPostingSetupDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesPricesDbHandler;
import com.gui.mdt.thongsieknavclient.ui.MvsSalesOrderActivity;
import com.gui.mdt.thongsieknavclient.ui.MvsSalesOrderItemActivity;

import java.util.List;


/**
 * Created by GUI-NB03 on 2017-07-21.
 */

public class MvsSalesOrderAdapter extends RecyclerView.Adapter<MvsSalesOrderAdapter.SalesItemViewHolder> {

    private static final int MVS_SALES_ORDER_ITEM_ACTIVITY_RESULT_CODE = 3;
    private List<SalesOrderLine> salesOrderLineList;
    private int rowLayout;
    private Activity activity;
    private String status="", deliveryDate = "",formName = "";
    private Customer tempCustomer = new Customer();
    private float mEnteredQuantity=0f, mTotalPrice=0f
            , mVatAmt=0f, mQuantity=0f, mTotalAmtInclVat=0f;


    @Override
    public MvsSalesOrderAdapter.SalesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final MvsSalesOrderAdapter.SalesItemViewHolder viewHolder = new MvsSalesOrderAdapter.SalesItemViewHolder(view);

        viewHolder.mTvItemNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {

                if (status.equals(activity.getResources().getString(R.string.MVSSalesOrderStatusPending))
                        || status.equals(activity.getResources().getString(R.string.MVSSalesOrderStatusComplete))
                        || status.equals(activity.getResources().getString(R.string.MVSSalesOrderStatusVoid))) {
                    int position = viewHolder.getAdapterPosition();

                    SalesOrderLine msoSalesOrderLineObj = salesOrderLineList.get(position);
                    String ObjToJason = msoSalesOrderLineObj.toJson();
                    if (tempCustomer != null) {
                        String customerJsonObj = tempCustomer.toJson();

                        Intent intent = new Intent(activity, MvsSalesOrderItemActivity.class);
                        intent.putExtra(activity.getResources().getString(R.string.sales_order_line_obj), ObjToJason);
                        intent.putExtra(activity.getResources().getString(R.string.adapter_position), position);
                        intent.putExtra(activity.getResources().getString(R.string.customer_json_obj), customerJsonObj);
                        intent.putExtra("deliveryDate", deliveryDate);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        activity.startActivityForResult(intent, MVS_SALES_ORDER_ITEM_ACTIVITY_RESULT_CODE);

                    }
                }
            }
        });

        //Exchange Qty setup
        viewHolder.mTxtExchQTY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mTxtExchQTY.requestFocus();
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });

        viewHolder.mTxtExchQTY.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    viewHolder.mTxtExchQTY.clearFocus();
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                else {
                    viewHolder.mTxtExchQTY.requestFocus();
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                }
            }
        });

        viewHolder.mTxtExchQTY.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                   final int position = viewHolder.getAdapterPosition();
                   final SalesOrderLine sol = salesOrderLineList.get(position);

                    if (!viewHolder.mTxtExchQTY.getText().toString().equals("")) {

                        SalesPricesDbHandler spdb = new SalesPricesDbHandler(activity);
                        spdb.open();

                        float unitPrice_ = spdb.getUnitPriceWithQuantity(sol.getNo(),
                                tempCustomer.getCustomerPriceGroup(),
                                tempCustomer.getCode(),
                                sol.getUnitofMeasure() == null ? "" : sol.getUnitofMeasure(),
                                Integer.valueOf(viewHolder.mTxtExchQTY.getText().toString()),
                                deliveryDate
                        );
                        spdb.close();

                        float unitPrice = floatRound(unitPrice_,2);

                        float gstPresentage = getGstPercentage(tempCustomer.getCode(), sol.getNo());

                        sol.setUnitPrice(unitPrice);

                        mEnteredQuantity = Float.parseFloat(viewHolder.mTxtExchQTY.getText().toString());

                        if(Math.round(mEnteredQuantity) > Math.round(sol.getQuantity()))
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                            builder.setTitle(activity.getResources().getString(R.string.message_title_alert));
                            builder.setMessage("Quantity can not be applied. Exchange quantity is greater than sales quantity");
                            builder.setCancelable(false);

                            builder.setPositiveButton(activity.getResources().getString(R.string.button_text_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mEnteredQuantity = 0f;

                                    sol.setExchangedQty(mEnteredQuantity);

                                    salesOrderLineList.set(position, sol);
                                    viewHolder.mTxtExchQTY.setText(String.valueOf(Math.round(mEnteredQuantity)));

                                    applyColorCodes(sol.getQuantity(),sol.getExchangedQty(),viewHolder);

                                    MvsSalesOrderActivity mvsSalesOrderActivity = (MvsSalesOrderActivity) activity;
                                    mvsSalesOrderActivity.updateSummeryValues(salesOrderLineList);
                                    mvsSalesOrderActivity.isSaved = false;
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else
                        {
                            float oldExchQty = sol.getExchangedQty();

                            mQuantity = oldExchQty + sol.getQuantity()- mEnteredQuantity;

                            float totalprice_ = Math.round(mQuantity) * sol.getUnitPrice();
                            mTotalPrice = floatRound(totalprice_,2);

                            float vatAmt_ = (mTotalPrice * gstPresentage) / 100;
                            mVatAmt = floatRound(vatAmt_,2);

                            float totalAmtInclVat_ = mVatAmt + mTotalPrice;
                            mTotalAmtInclVat = floatRound(totalAmtInclVat_,2);

                            sol.setExchangedQty(mEnteredQuantity);
                            sol.setQuantity(mQuantity);
                            sol.setQtytoInvoice(mQuantity);
                            sol.setLineAmount(mTotalPrice);
                            sol.setTotalVATAmount(mVatAmt);
                            sol.setTotalAmountInclVAT(mTotalAmtInclVat);

                            salesOrderLineList.set(position, sol);

                            viewHolder.mTxtBillQTY.setText(String.valueOf(Math.round(sol.getQuantity())));
                            viewHolder.mTvPrice.setText(String.format("%.2f", unitPrice));
                            viewHolder.mTvTotal.setText(String.format("%.2f", mTotalPrice));

                            applyColorCodes(sol.getQuantity(), sol.getExchangedQty(), viewHolder);

                            MvsSalesOrderActivity mvsSalesOrderActivity = (MvsSalesOrderActivity) activity;
                            mvsSalesOrderActivity.updateSummeryValues(salesOrderLineList);
                            mvsSalesOrderActivity.isSaved = false;
                        }
                    } else {
                        mEnteredQuantity = 0f;

                        sol.setExchangedQty(mEnteredQuantity);

                        salesOrderLineList.set(position, sol);
                        viewHolder.mTxtExchQTY.setText(String.valueOf(Math.round(mEnteredQuantity)));

                        applyColorCodes(sol.getQuantity(), sol.getExchangedQty(), viewHolder);

                        MvsSalesOrderActivity mvsSalesOrderActivity = (MvsSalesOrderActivity) activity;
                        mvsSalesOrderActivity.updateSummeryValues(salesOrderLineList);
                        mvsSalesOrderActivity.isSaved = false;
                    }
                }
                return false;
            }
        });

        //Bill Qty set up
        viewHolder.mTxtBillQTY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mTxtBillQTY.requestFocus();
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });

        viewHolder.mTxtBillQTY.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    viewHolder.mTxtBillQTY.clearFocus();
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                else {
                    viewHolder.mTxtBillQTY.requestFocus();
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                }
            }
        });

        viewHolder.mTxtBillQTY.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    final int position = viewHolder.getAdapterPosition();
                    final SalesOrderLine sol = salesOrderLineList.get(position);

                    if (!viewHolder.mTxtBillQTY.getText().toString().equals("")) {

                        SalesPricesDbHandler spdb = new SalesPricesDbHandler(activity);
                        spdb.open();

                        float unitPrice_ = spdb.getUnitPriceWithQuantity(sol.getNo(),
                                tempCustomer.getCustomerPriceGroup(),
                                tempCustomer.getCode(),
                                sol.getUnitofMeasure() == null ? "" : sol.getUnitofMeasure(),
                                Integer.valueOf(viewHolder.mTxtBillQTY.getText().toString()),
                                deliveryDate
                        );
                        spdb.close();

                        float unitPrice = floatRound(unitPrice_,2);

                        float gstPresentage = getGstPercentage(tempCustomer.getCode(), sol.getNo());

                        sol.setUnitPrice(unitPrice);

                        mEnteredQuantity = Float.parseFloat(viewHolder.mTxtBillQTY.getText().toString());

                        float totalPrice_ = Math.round(mEnteredQuantity) * sol.getUnitPrice();
                        mTotalPrice = floatRound(totalPrice_,2);

                        float vatAmt_ = (mTotalPrice * gstPresentage) / 100;
                        mVatAmt = floatRound(vatAmt_,2);

                        float totalAmtInclVat_= mVatAmt + mTotalPrice;
                        mTotalAmtInclVat = floatRound(totalAmtInclVat_,2);

                        sol.setQuantity(mEnteredQuantity);
                        sol.setQtytoInvoice(mEnteredQuantity);
                        sol.setLineAmount(mTotalPrice);
                        sol.setTotalVATAmount(mVatAmt);
                        sol.setTotalAmountInclVAT( mTotalAmtInclVat);

                        salesOrderLineList.set(position, sol);

                        viewHolder.mTxtBillQTY.setText(String.valueOf(Math.round(sol.getQuantity())));
                        viewHolder.mTvPrice.setText(String.format("%.2f", unitPrice));
                        viewHolder.mTvTotal.setText(String.format("%.2f", mTotalPrice));

                        applyColorCodes(sol.getQuantity(), sol.getExchangedQty(), viewHolder);

                        MvsSalesOrderActivity mvsSalesOrderActivity = (MvsSalesOrderActivity) activity;
                        mvsSalesOrderActivity.updateSummeryValues(salesOrderLineList);
                        mvsSalesOrderActivity.isSaved = false;
                    }
                    else {
                        sol.setQuantity(0f);
                        sol.setQtytoInvoice(0f);
                        sol.setLineAmount(0f);
                        sol.setTotalVATAmount(0f);
                        sol.setTotalAmountInclVAT(0f);

                        salesOrderLineList.set(position, sol);

                        viewHolder.mTxtBillQTY.setText(String.valueOf(Math.round(sol.getQuantity())));
                        viewHolder.mTvPrice.setText(String.format("%.2f", sol.getUnitPrice()));
                        viewHolder.mTvTotal.setText(String.format("%.2f", 0f));

                        applyColorCodes(sol.getQuantity(), sol.getExchangedQty(), viewHolder);

                        MvsSalesOrderActivity mvsSalesOrderActivity = (MvsSalesOrderActivity) activity;
                        mvsSalesOrderActivity.updateSummeryValues(salesOrderLineList);
                        mvsSalesOrderActivity.isSaved = false;
                    }
                }
                return false;
            }
        });

        if(status.equals(activity.getResources().getString(R.string.MVSSalesOrderStatusConverted))
                || status.equals(activity.getResources().getString(R.string.MVSSalesOrderStatusConfirmed)))
        {
            viewHolder.mTxtExchQTY.setEnabled(false);
            viewHolder.mTxtBillQTY.setEnabled(false);
        }

        return viewHolder;
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

    //Apply color codes on Bill qty & Exch qty
    public void applyColorCodes(float qty
            , float exchQty
            , MvsSalesOrderAdapter.SalesItemViewHolder holder) {

        if (Math.round(qty) != 0) {
            holder.mTxtBillQTY.setBackgroundColor(activity.getResources().getColor(R.color.springGreen));
        } else {
            holder.mTxtBillQTY.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }

        if (Math.round(exchQty) != 0) {
            holder.mTxtExchQTY.setBackgroundColor(activity.getResources().getColor(R.color.colorExchQty));
        } else {
            holder.mTxtExchQTY.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }

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

    public MvsSalesOrderAdapter(List<SalesOrderLine> salesOrderLineList_, int rowLayout_, Activity activity_, String status_, Customer customer_, String deliveryDate_) {
        this.salesOrderLineList = salesOrderLineList_;
        this.rowLayout = rowLayout_;
        this.activity = activity_;
        this.status = status_;
        this.tempCustomer = customer_;
        this.deliveryDate = deliveryDate_;
    }

    @Override
    public void onBindViewHolder(MvsSalesOrderAdapter.SalesItemViewHolder holder, int position) {

        float totalAmount_ = floatRound( salesOrderLineList.get(position).getUnitPrice(),2)
                * Math.round(salesOrderLineList.get(position).getQuantity());
        float totalAmount = floatRound(totalAmount_,2);

        holder.mTvNo.setText(String.valueOf(position + 1));
        //holder.mTvItemNo.setText(salesOrderLineList.get(position).getNo());
        holder.mTvPrice.setText(String.format("%.2f",salesOrderLineList.get(position).getUnitPrice()));
        holder.mTvUom.setText(salesOrderLineList.get(position).getUnitofMeasure());
        holder.mTxtBillQTY.setText(String.valueOf(Math.round(salesOrderLineList.get(position).getQuantity())));
        holder.mTvTotal.setText(String.format("%.2f", totalAmount));
        //holder.mTvItemDescroption.setText(salesOrderLineList.get(position).getDescription());
        holder.mTxtExchQTY.setText(String.valueOf(Math.round(salesOrderLineList.get(position).getExchangedQty())));

        holder.mTvItemNo.setText(salesOrderLineList.get(position).getItemCrossReferenceNo());
        holder.mTvItemDescroption.setText( salesOrderLineList.get(position).getNo()+" - "+
                salesOrderLineList.get(position).getItemCrossReferenceDescription());

        applyColorCodes(salesOrderLineList.get(position).getQuantity(),
                salesOrderLineList.get(position).getExchangedQty(),
                holder);
    }

    @Override
    public int getItemCount() {
        return salesOrderLineList.size();
    }

    public static class SalesItemViewHolder extends RecyclerView.ViewHolder {

        TextView mTvNo, mTvItemNo, mTvPrice, mTvUom, mTvTotal, mTvItemDescroption;
        EditText mTxtExchQTY, mTxtBillQTY;

        public SalesItemViewHolder(View v) {
            super(v);

            mTvNo = (TextView) v.findViewById(R.id.tvNo);
            mTvItemNo = (TextView) v.findViewById(R.id.tvItemNo);
            mTvPrice = (TextView) v.findViewById(R.id.tvPrice);
            mTvUom = (TextView) v.findViewById(R.id.tvUom);
            mTxtBillQTY = (EditText) v.findViewById(R.id.txtBillQty);
            mTvTotal = (TextView) v.findViewById(R.id.tvTotal);
            mTvItemDescroption = (TextView) v.findViewById(R.id.tvItemDescroption);
            mTxtExchQTY = (EditText) v.findViewById(R.id.txtExchQTY);

            //set maximum num length on ExchQuantity field
            mTxtExchQTY.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9) });
            mTxtBillQTY.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9) });
        }
    }
}
