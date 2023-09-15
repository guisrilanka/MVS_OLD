package com.gui.mdt.thongsieknavclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.ui.MvsSalesOrderActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by GuiUser on 11/6/2017.
 */

public class MvsSalesOrderListAdapter extends RecyclerView.Adapter<MvsSalesOrderListAdapter.ItemViewHolder> {


    private List<SalesOrder> salesOrdersList;
    private int rowLayout;
    private Context context;

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final MvsSalesOrderListAdapter.ItemViewHolder viewHolder = new MvsSalesOrderListAdapter.ItemViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                SalesOrder salesOrderObj = salesOrdersList.get(position);
                String objAsJson = salesOrderObj.toJson();

                Intent intent = new Intent(context, MvsSalesOrderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(context.getResources().getString(R.string.sales_order_jason_obj), objAsJson);
                context.startActivity(intent);

            }
        });

        viewHolder.chkSalesOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                if (viewHolder.chkSalesOrder.isChecked()) {
                    salesOrdersList.get(position).setConfirmedSo(true);
                } else {
                    salesOrdersList.get(position).setConfirmedSo(false);
                }
            }
        });

        return viewHolder;
    }

    public MvsSalesOrderListAdapter(List<SalesOrder> _salesOrders, int _rowLayout,Context _context) {
        this.salesOrdersList = _salesOrders;
        this.rowLayout = _rowLayout;
        this.context = _context;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        holder.txtCustomerName.setText(salesOrdersList.get(position).getSelltoCustomerName());
        holder.txtDeliveryDate.setText("Delivery Date: "+dateConverter(salesOrdersList.get(position).getShipmentDate()));
        //holder.txtCustomerAddress.setText(salesOrdersList.get(position).getSelltoAddress());
        holder.txtSoNo.setText("SO No: " + salesOrdersList.get(position).getNo());
        holder.txtSoDate.setText("SO Date: " +dateConverter(salesOrdersList.get(position).getOrderDate()));

        String siNo = salesOrdersList.get(position).getSINo() == null?"":salesOrdersList.get(position).getSINo();
        String siDate = salesOrdersList.get(position).getSIDate() == null
                ?"":dateConverter(salesOrdersList.get(position).getSIDate());

        holder.txtSINo.setText("SI No: " + siNo);
        holder.txtSIDate.setText("SI Date: " + siDate);

        holder.mTxtCustomerCode.setText(salesOrdersList.get(position).getSelltoCustomerNo());
        holder.mTxtTotalAmt.setText("Total Amount: $"
                + String.format("%.2f", salesOrdersList.get(position).getAmountIncludingVAT()));

        holder.mTxtTotalBillQty.setText("Total Bill Quantity: "
                + String.valueOf(salesOrdersList.get(position).getTotalBillQty()));

        if (salesOrdersList.get(position).getStatus().equals(context.getResources().getString(R.string.MVSSalesOrderStatusConfirmed)))
        {
            holder.chkSalesOrder.setChecked(true);
            holder.chkSalesOrder.setEnabled(false);
            //holder.layoutMvsSalesOrderCard.setBackgroundColor(context.getResources().getColor(R.color.listHqDownloaded));
            if(salesOrdersList.get(position).isTransferred()){
                holder.layoutMvsSalesOrderCard.setBackgroundColor(context.getResources().getColor(R.color.listHqDownloaded));
                holder.txtSINo.setTextColor(context.getResources().getColor(R.color.hockeyapp_text_black));
            }
            else {
                holder.layoutMvsSalesOrderCard.setBackgroundColor(context.getResources().getColor(R.color.listConfirmed));
                holder.txtSINo.setTextColor(context.getResources().getColor(R.color.errorRed));
            }
        }
        else
        {
            if(salesOrdersList.get(position).getStatus().equals(
                    context.getResources().getString(R.string.MVSSalesOrderStatusPending)) ||
                    salesOrdersList.get(position).getStatus().equals(
                            context.getResources().getString(R.string.MVSSalesOrderStatusComplete)))
            {
                holder.chkSalesOrder.setEnabled(false);
                holder.chkSalesOrder.setChecked(false);
                holder.layoutMvsSalesOrderCard.setBackgroundResource(R.drawable.all_rectangle_white_background_blue_border);
                holder.txtSINo.setText("SI No:                        ");
                holder.txtSIDate.setText("SI Date:                     ");
            }
            else if(salesOrdersList.get(position).getStatus().equals(
                    context.getResources().getString(R.string.MVSSalesOrderStatusConverted)))
            {
                holder.chkSalesOrder.setEnabled(true);
                holder.chkSalesOrder.setChecked(false);
                holder.layoutMvsSalesOrderCard.setBackgroundColor(context.getResources().getColor(R.color.listConfirmed));

            }
            else if(salesOrdersList.get(position).getStatus().equals(
                    context.getResources().getString(R.string.MVSSalesOrderStatusVoid)))
            {
                holder.chkSalesOrder.setEnabled(false);
                holder.chkSalesOrder.setChecked(false);
                holder.layoutMvsSalesOrderCard.setBackgroundColor(context.getResources().getColor(R.color.listVoid));
            }

            if(salesOrdersList.get(position).isConfirmedSo())
            {
                holder.chkSalesOrder.setChecked(true);
            }
            else
            {
                holder.chkSalesOrder.setChecked(false);
            }
        }
    }

    public String dateConverter(String date_)
    {
        String date="";

        if (date_ != null) {
            if (!date_.equals("")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getResources().getString(R.string.date_format_dd_MM_yyyy));
                try {

                    Date initDate = new SimpleDateFormat(context.getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(date_);
                    date = simpleDateFormat.format(initDate);

                } catch (Exception e) {
                    Log.e(context.getResources().getString(R.string.message_exception), e.getMessage().toString());
                }

            }
        }
        return date;
    }

    @Override
    public int getItemCount() {
        return salesOrdersList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        CheckBox chkSalesOrder;
        TextView txtCustomerName,txtDeliveryDate,txtCustomerAddress, txtSoNo,txtSoDate,txtSINo,
                 txtSIDate, mTxtTotalAmt, mTxtTotalBillQty  ,mTxtCustomerCode;

        LinearLayout layoutMvsSalesOrderCard;

        public ItemViewHolder(final View v) {
            super(v);

            chkSalesOrder = (CheckBox) v.findViewById(R.id.chkSalesOrder);

            txtCustomerName = (TextView) v.findViewById(R.id.txtCustomerName);
            txtDeliveryDate = (TextView) v.findViewById(R.id.txtDeliveryDate);
            //txtCustomerAddress = (TextView) v.findViewById(R.id.txtCustomerAddress);
            txtSoNo = (TextView) v.findViewById(R.id.txtSoNo);

            txtSoDate = (TextView) v.findViewById(R.id.txtSoDate);
            txtSINo = (TextView) v.findViewById(R.id.txtSINo);
            txtSIDate = (TextView) v.findViewById(R.id.txtSIDate);

            layoutMvsSalesOrderCard = (LinearLayout) v.findViewById(R.id.layoutMvsSalesOrderCard);

            mTxtTotalAmt = (TextView) v.findViewById(R.id.txtTotalAmt);
            mTxtTotalBillQty = (TextView) v.findViewById(R.id.txtTotalBillQty);
            mTxtCustomerCode = (TextView) v.findViewById(R.id.txtCustomerCode);
        }
    }
}
