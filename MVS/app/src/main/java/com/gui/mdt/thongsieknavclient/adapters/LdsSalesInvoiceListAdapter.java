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
import com.gui.mdt.thongsieknavclient.model.MsoSalesOrderList;
import com.gui.mdt.thongsieknavclient.ui.MsoSalesOrderActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 7/18/2017.
 */

public class LdsSalesInvoiceListAdapter extends RecyclerView.Adapter<LdsSalesInvoiceListAdapter.ItemViewHolder> {
    private ArrayList<MsoSalesOrderList> dataSet;

    private List<SalesOrder> salesOrdersList;
    private int rowLayout;
    private Context context;

    public LdsSalesInvoiceListAdapter(List<SalesOrder> salesOrders, int rowLayout, Context _context) {
        this.salesOrdersList = salesOrders;
        this.rowLayout = rowLayout;
        this.context = _context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final LdsSalesInvoiceListAdapter.ItemViewHolder viewHolder = new LdsSalesInvoiceListAdapter.ItemViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                SalesOrder salesOrderObj = salesOrdersList.get(position);
                // add by sajith..

                salesOrderObj.setStatus(context.getResources().getString(R.string.SalesOrderStatusConfirmed));

                String sales_order_jason_obj = salesOrderObj.toJson();

                Intent intent = new Intent(context, MsoSalesOrderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(context.getResources().getString(R.string.sales_order_jason_obj), sales_order_jason_obj);
                intent.putExtra("module", context.getResources().getString(R.string.module_lds));
                context.startActivity(intent);

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        holder.txtCustomerName.setText(salesOrdersList.get(position).getSelltoCustomerName());
        holder.txtSoNo.setText("SI No: " + salesOrdersList.get(position).getNo());
        holder.txtAddress.setText(salesOrdersList.get(position).getSelltoAddress());

        String sodate = salesOrdersList.get(position).getDocumentDate();

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date dt = fmt.parse(sodate);

            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
            holder.txtSoDate.setText("SI Date: " + (fmtOut.format(dt)).substring(0, 10));
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


        String status = context.getResources().getString(R.string.SalesOrderStatusDelivery);

        if (salesOrdersList.get(position).getStatus().equals(status))
        {
            holder.chkOrder.setChecked(true);
            holder.chkOrder.setEnabled(false);
            holder.layoutCustomerCard.setBackgroundColor(context.getResources().getColor(R.color.listConfirmed));
        }

    }

    @Override
    public int getItemCount() {
        return salesOrdersList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox chkOrder;
        TextView txtCustomerName, txtSoNo, txtSoDate, txtCreatedDate,txtAddress;
        LinearLayout layoutCustomerCard;

        public ItemViewHolder(final View v) {
            super(v);

            chkOrder = (CheckBox) v.findViewById(R.id.chkOrder);
            txtCustomerName = (TextView) v.findViewById(R.id.txtCustomerName);
            txtSoNo = (TextView) v.findViewById(R.id.txtSoNo);
            txtSoDate = (TextView) v.findViewById(R.id.txtSoDate);
            txtCreatedDate = (TextView) v.findViewById(R.id.txtCreatedDate);
            layoutCustomerCard = (LinearLayout) v.findViewById(R.id.layoutCustomerCard);
            txtAddress=(TextView)v.findViewById(R.id.txtAddress);

        }
    }



    private int lastPosition = -1;

}
