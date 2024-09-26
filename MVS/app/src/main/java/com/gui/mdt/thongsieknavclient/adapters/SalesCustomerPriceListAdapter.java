package com.gui.mdt.thongsieknavclient.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.SalesPrices;

import java.util.List;

/**
 * Created by GUI-NB03 on 2017-08-09.
 */

public class SalesCustomerPriceListAdapter extends RecyclerView.Adapter<SalesCustomerPriceListAdapter.CustomerPriceListViewHolder>{

    private int rowLayout;
    private Context context;
    List<SalesPrices> customerPriceList;
    Bundle extras;

    public SalesCustomerPriceListAdapter(List<SalesPrices> salesPriceList, Bundle _extras, int rowLayout, Context _context) {
        this.customerPriceList = salesPriceList;
        this.rowLayout = rowLayout;
        this.context = _context;
        this.extras = _extras;
    }

    @Override
    public CustomerPriceListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final CustomerPriceListViewHolder viewHolder = new CustomerPriceListViewHolder(view);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                SalesPrices salesPrices = customerPriceList.get(position);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomerPriceListViewHolder holder, int position) {
        holder.tvItemNo.setText(customerPriceList.get(position).getItemNo());
        holder.tvItemDesc.setText(customerPriceList.get(position).getItemDescription());
        holder.tvUom.setText(customerPriceList.get(position).getUnitofMeasureCode());

        String UnitPrice = String.format("%.2f",Float.parseFloat(customerPriceList.get(position).getPublishedPrice()));

        holder.tvUnitPrice.setText(UnitPrice);

        holder.tvStartDate.setText(getFormattedDate(customerPriceList.get(position).getStartingDate()));
        holder.tvEndDate.setText(getFormattedDate(customerPriceList.get(position).getEndingDate()));
        
        if(customerPriceList.get(position).getSalesType().intValue()==0)
        {
            holder.tvItemNo.setBackgroundColor(context.getResources().getColor(R.color.salesPrice));
            holder.tvItemDesc.setBackgroundColor(context.getResources().getColor(R.color.salesPrice));
            holder.tvUom.setBackgroundResource(R.drawable.table_cell_bg_blue);
            holder.tvUnitPrice.setBackgroundColor(context.getResources().getColor(R.color.salesPrice));
            holder.tvStartDate.setBackgroundResource(R.drawable.table_cell_bg_blue);
            holder.tvEndDate.setBackgroundColor(context.getResources().getColor(R.color.salesPrice));
            holder.tvItemNo.setTextColor(context.getResources().getColor(R.color.primaryText));
        }
        else
        {
            holder.tvItemNo.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tvItemDesc.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tvUom.setBackgroundResource(R.drawable.table_cell_bg);
            holder.tvUnitPrice.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tvStartDate.setBackgroundResource(R.drawable.table_cell_bg);
            holder.tvEndDate.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tvItemNo.setTextColor(context.getResources().getColor(R.color.blue));
        }

    }

    private String getFormattedDate(String date){
        String serverDate = date;     // full file name
        serverDate = date.substring(0,date.indexOf("T"));
        String[] parts = serverDate.split("-"); // String array, each element is text between dots

        return parts[2]+"-"+parts[1]+"-"+parts[0];
    }

    @Override
    public int getItemCount() {
        return customerPriceList.size();
    }

    public static class CustomerPriceListViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvItemNo,tvItemDesc,tvUom,tvUnitPrice,tvStartDate,tvEndDate;

        public CustomerPriceListViewHolder(View v) {
            super(v);

            tvItemNo = (TextView) v.findViewById(R.id.tvItemNo);
            tvItemDesc= (TextView) v.findViewById(R.id.tvItemDesc);
            tvUom= (TextView) v.findViewById(R.id.tvUom);
            tvUnitPrice= (TextView) v.findViewById(R.id.tvUnitPrice);
            tvStartDate= (TextView) v.findViewById(R.id.tvStartDate);
            tvEndDate= (TextView) v.findViewById(R.id.tvEndDate);
        }
    }
}
