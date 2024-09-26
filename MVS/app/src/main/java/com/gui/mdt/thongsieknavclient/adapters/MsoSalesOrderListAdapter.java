package com.gui.mdt.thongsieknavclient.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.ui.MsoSalesOrderActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 7/7/2017.
 */

public class MsoSalesOrderListAdapter extends RecyclerView.Adapter<MsoSalesOrderListAdapter.ItemViewHolder> {


    private List<SalesOrder> salesOrdersList;
    private int rowLayout;
    private Context context;
    private Boolean IsMso = false, IsMvs = false;

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final MsoSalesOrderListAdapter.ItemViewHolder viewHolder = new MsoSalesOrderListAdapter.ItemViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                SalesOrder salesOrderObj = salesOrdersList.get(position);
                String objAsJson = salesOrderObj.toJson();

                Intent intent = new Intent(context, MsoSalesOrderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(context.getResources().getString(R.string.sales_order_jason_obj), objAsJson);
                context.startActivity(intent);

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        String cusName =salesOrdersList.get(position).getSelltoCustomerName();
        holder.txtCustomerName.setText(cusName);
        holder.txtSoNo.setText("SO No: " + salesOrdersList.get(position).getNo());
        String sodate = salesOrdersList.get(position).getDocumentDate();

        holder.mTxtTotalAmtInclVat.setText("Amount: $"
                + String.format("%.2f", salesOrdersList.get(position).getAmountIncludingVAT()));

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date dt = fmt.parse(sodate);
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
            holder.txtSoDate.setText("Order Date: " + (fmtOut.format(dt)).substring(0, 10));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        String deliveryDate = salesOrdersList.get(position).getShipmentDate();
        try
        {
            Date dt = fmt.parse(deliveryDate);
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
            holder.txtCreatedDate.setText("Delivery Date: " + (fmtOut.format(dt)).substring(0, 10));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        holder.chkOrder.setTag(position);

        String status = context.getResources().getString(R.string.SalesOrderStatusConfirmed);
        if (salesOrdersList.get(position).getStatus().equals(status)) //mobile created confirmed
        {
            if(!salesOrdersList.get(position).isTransferred()){ // not synced to navi.
                holder.chkOrder.setChecked(true);
                holder.chkOrder.setEnabled(false);
                holder.layoutCustomerCard.setBackgroundColor(context.getResources().getColor(R.color.listConfirmed));
                //holder.txtSoNo.setBackgroundColor(context.getResources().getColor(R.color.errorRed));
                holder.txtSoNo.setTextColor(context.getResources().getColor(R.color.errorRed));
            }
            else {
                holder.chkOrder.setChecked(true);
                holder.chkOrder.setEnabled(false);
                holder.layoutCustomerCard.setBackgroundColor(context.getResources().getColor(R.color.listConfirmed));
                //holder.txtSoNo.setBackgroundDrawable(null);
                holder.txtSoNo.setTextColor(context.getResources().getColor(R.color.hockeyapp_text_black));
            }

            holder.chkOrder.setEnabled(false);
            holder.layoutCustomerCard.setBackgroundColor(context.getResources().getColor(R.color.listConfirmed));
        }
        else
        {
            if(salesOrdersList.get(position).getStatus().equals(
                    context.getResources().getString(R.string.SalesOrderStatusPending))) //mobile created not confirmed
            {
                holder.chkOrder.setEnabled(true);
                holder.chkOrder.setChecked(false);
                holder.layoutCustomerCard.setBackgroundResource(R.drawable.all_rectangle_white_background_blue_border);
            }
            else if(salesOrdersList.get(position).getStatus().equals(
                    context.getResources().getString(R.string.SalesOrderStatusComplete))) // HQ downloaded
            {
                holder.chkOrder.setEnabled(false);
                holder.chkOrder.setChecked(true);
                holder.layoutCustomerCard.setBackgroundColor(context.getResources().getColor(R.color.listHqDownloaded));
            }

            if(salesOrdersList.get(position).isConfirmedSo())
            {

                holder.chkOrder.setChecked(true);


            }
            else
            {
                holder.chkOrder.setChecked(false);

            }
        }

        holder.chkOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer pos = (Integer) holder.chkOrder.getTag();
                if (holder.chkOrder.isChecked()) {
                    salesOrdersList.get(pos).setConfirmedSo(true);
                } else {
                    salesOrdersList.get(pos).setConfirmedSo(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        if(salesOrdersList!=null) {
            return salesOrdersList.size();
        }else {return 0;}
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox chkOrder;
        TextView txtCustomerName, txtSoNo, txtSoDate, txtCreatedDate, mTxtTotalAmtInclVat;
        LinearLayout layoutCustomerCard;

        public ItemViewHolder(final View v) {
            super(v);

            chkOrder = (CheckBox) v.findViewById(R.id.chkOrder);
            txtCustomerName = (TextView) v.findViewById(R.id.txtCustomerName);
            txtSoNo = (TextView) v.findViewById(R.id.txtSoNo);
            txtSoDate = (TextView) v.findViewById(R.id.txtSoDate);
            txtCreatedDate = (TextView) v.findViewById(R.id.txtCreatedDate);
            layoutCustomerCard = (LinearLayout) v.findViewById(R.id.layoutCustomerCard);
            mTxtTotalAmtInclVat = (TextView) v.findViewById(R.id.txtTotalAmtInclVat);
        }
    }

    public MsoSalesOrderListAdapter(List<SalesOrder> _salesOrders, int _rowLayout,Context _context) {
        this.salesOrdersList = _salesOrders;
        this.rowLayout = _rowLayout;
        this.context = _context;
    }
}
