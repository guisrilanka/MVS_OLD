package com.gui.mdt.thongsieknavclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceResponse;
import com.gui.mdt.thongsieknavclient.ui.SalesCustomerInvoiceDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by bhanuka on 25/07/2017.
 */

public class SalesCustomerInvoiceAdapter extends RecyclerView.Adapter<SalesCustomerInvoiceAdapter.ViewHolder> {

    List<ApiPostedSalesInvoiceResponse.ApiPostedSalesInvoice> invoiceList;

    Context mContext;
    int mRowLayout;

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    @Override
    public SalesCustomerInvoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mRowLayout, parent, false);

        final SalesCustomerInvoiceAdapter.ViewHolder viewHolder = new SalesCustomerInvoiceAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                ApiPostedSalesInvoiceResponse.ApiPostedSalesInvoice salesInvoiceObj = invoiceList.get(position);
                String objAsJson = salesInvoiceObj.toJson();
                Intent intent = new Intent(mContext, SalesCustomerInvoiceDetail.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("cusSalesInvoiceObj", objAsJson);
                mContext.startActivity(intent);

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SalesCustomerInvoiceAdapter.ViewHolder holder, int position) {

        String Amount = String.format("%.2f",Float.parseFloat(invoiceList.get(position).getAmount_Including_VAT()));
        String Balance = String.format("%.2f", Float.parseFloat(invoiceList.get(position).getAmount()));

        holder.tvAmount.setText("S$ " + Amount );
        holder.tvInvoiceNumber.setText(invoiceList.get(position).getNo());
        /*holder.tvBalance.setText("S$ " + Balance);*/
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String date = invoiceList.get(position).getDocument_Dat();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt = fmt.parse(date);

            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
            holder.tvDate.setText((fmtOut.format(dt)).substring(0,10));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public SalesCustomerInvoiceAdapter(List<ApiPostedSalesInvoiceResponse.ApiPostedSalesInvoice> data, int rowLayout, Context context) {
        this.mRowLayout = rowLayout;
        this.invoiceList = data;
        this.mContext = context;
    }

    public void clear() {
        invoiceList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ApiPostedSalesInvoiceResponse.ApiPostedSalesInvoice> list) {
        invoiceList.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceNumber, tvAmount, tvDate, tvBalance;

        public ViewHolder(View v) {
            super(v);

            tvAmount = (TextView) v.findViewById(R.id.tvAmount);
            tvInvoiceNumber = (TextView) v.findViewById(R.id.tvInvoiceNumber);
            /*tvBalance = (TextView) v.findViewById(R.id.tvBalance);*/
            tvDate = (TextView) v.findViewById(R.id.tvDate);
        }

    }
}