package com.gui.mdt.thongsieknavclient.adapters;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceItemResponse;

import java.util.List;

/**
 * Created by GUI-NB03 on 2017-08-29.
 */

public class SalesCustomerInvoiceItemAdapter extends RecyclerView.Adapter<SalesCustomerInvoiceItemAdapter.ViewHolder>{

    Context mContext;
    int mRowLayout;
    List<ApiPostedSalesInvoiceItemResponse.ApiPostedSalesInvoiceItem> invoiceItemList;

    @Override
    public SalesCustomerInvoiceItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mview = LayoutInflater.from(parent.getContext()).inflate(mRowLayout, parent, false);
        final SalesCustomerInvoiceItemAdapter.ViewHolder viewHolder = new SalesCustomerInvoiceItemAdapter.ViewHolder(mview);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SalesCustomerInvoiceItemAdapter.ViewHolder holder, int position) {

        float totalAmount = invoiceItemList.get(position).getUnit_Price()*invoiceItemList.get(position).getQuantity();
        TextView tvNo, tvPrice, tvUom, tvQTY, tvTotal, tvItemDescroption;
        holder.tvNo.setText(String.valueOf(position + 1));
        holder.tvItemNo.setText(invoiceItemList.get(position).getNo());
        holder.tvPrice.setText(String.format("%.2f",invoiceItemList.get(position).getUnit_Price()));
        holder.tvUom.setText(invoiceItemList.get(position).getUnit_of_Measure() );
        holder.tvQTY.setText(String.valueOf(invoiceItemList.get(position).getQuantity()));
        holder.tvTotal.setText(String.format("%.2f", totalAmount));
        holder.tvItemDescroption.setText(invoiceItemList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return invoiceItemList.size();
    }

    public SalesCustomerInvoiceItemAdapter(List<ApiPostedSalesInvoiceItemResponse.ApiPostedSalesInvoiceItem> data,
                                           int layout, Application context) {

        this.mContext = context;
        this.mRowLayout = layout;
        this.invoiceItemList = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvNo, tvItemNo, tvPrice, tvUom, tvQTY, tvTotal, tvItemDescroption;

        public ViewHolder(View v) {
            super(v);

            tvNo = (TextView) v.findViewById(R.id.tvNo);
            tvItemNo = (TextView) v.findViewById(R.id.tvItemNo);
            tvPrice = (TextView) v.findViewById(R.id.tvPrice);
            tvUom = (TextView) v.findViewById(R.id.tvUom);
            tvQTY = (TextView) v.findViewById(R.id.tvQTY);
            tvTotal = (TextView) v.findViewById(R.id.tvTotal);
            tvItemDescroption = (TextView) v.findViewById(R.id.tvItemDescroption);
        }
    }
}
