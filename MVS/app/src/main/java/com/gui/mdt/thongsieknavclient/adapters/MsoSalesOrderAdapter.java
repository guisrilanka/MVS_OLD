package com.gui.mdt.thongsieknavclient.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderLine;
import com.gui.mdt.thongsieknavclient.ui.MsoSalesOrderItemActivity;

import java.util.List;

/**
 * Created by User on 7/7/2017.
 */

public class MsoSalesOrderAdapter extends RecyclerView.Adapter<MsoSalesOrderAdapter.SalesItemViewHolder> {

    private static final int MSO_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE = 2;
    private static final int MVS_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE = 3;
    private List<SalesOrderLine> salesOrderLineList;
    private int rowLayout;
    private Activity activity;
    private String status="", deliveryDate = "",formName = "";
    private Customer tempCustomer = new Customer();


    @Override
    public MsoSalesOrderAdapter.SalesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final MsoSalesOrderAdapter.SalesItemViewHolder viewHolder = new MsoSalesOrderAdapter.SalesItemViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(status.equals(activity.getResources().getString(R.string.SalesOrderStatusPending)))
                {
                    int position = viewHolder.getAdapterPosition();

                    SalesOrderLine msoSalesOrderLineObj = salesOrderLineList.get(position);
                    String ObjToJason = msoSalesOrderLineObj.toJson();
                    if(tempCustomer !=  null)
                    {
                        String customerJsonObj = tempCustomer.toJson();

                        Intent intent = new Intent(activity, MsoSalesOrderItemActivity.class);
                        intent.putExtra(activity.getResources().getString(R.string.sales_order_line_obj),ObjToJason);
                        intent.putExtra(activity.getResources().getString(R.string.adapter_position) , position);
                        intent.putExtra(activity.getResources().getString(R.string.customer_json_obj),customerJsonObj);
                        intent.putExtra("deliveryDate", deliveryDate);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if(formName.equals(activity.getResources().getString(R.string.form_name_mvs_stock_Request)))
                        {
                            activity.startActivityForResult(intent, MVS_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE);
                        }
                        else
                        {
                            activity.startActivityForResult(intent, MSO_SALE_ORDER_ITEM_ACTIVITY_RESULT_CODE);
                        }
                    }
                }
            }
        });
        return viewHolder;
    }

    public MsoSalesOrderAdapter(List<SalesOrderLine> salesOrderLineList_, int rowLayout_, Activity activity_, String status_, Customer customer_, String deliveryDate_) {
        this.salesOrderLineList = salesOrderLineList_;
        this.rowLayout = rowLayout_;
        this.activity = activity_;
        this.status = status_;
        this.tempCustomer = customer_;
        this.deliveryDate = deliveryDate_;
    }

    public MsoSalesOrderAdapter(List<SalesOrderLine> salesOrderLineList_, int rowLayout_, Activity activity_, String status_, Customer customer_, String deliveryDate_,String formName_) {
        this.salesOrderLineList = salesOrderLineList_;
        this.rowLayout = rowLayout_;
        this.activity = activity_;
        this.status = status_;
        this.tempCustomer = customer_;
        this.deliveryDate = deliveryDate_;
        this.formName = formName_;
    }

    @Override
    public void onBindViewHolder(MsoSalesOrderAdapter.SalesItemViewHolder holder, int position) {

        float totalAmount = salesOrderLineList.get(position).getUnitPrice()*salesOrderLineList.get(position).getQuantity();
        //TextView tvNo, tvPrice, tvUom, tvQTY, tvTotal, tvItemDescroption;
        holder.tvNo.setText(String.valueOf(position + 1));
        holder.tvItemNo.setText(salesOrderLineList.get(position).getNo());
        holder.tvPrice.setText(String.format("%.2f",salesOrderLineList.get(position).getUnitPrice()));
        holder.tvUom.setText(salesOrderLineList.get(position).getUnitofMeasure());
        holder.tvQTY.setText(String.valueOf(Math.round(salesOrderLineList.get(position).getQuantity())));
        holder.tvTotal.setText(String.format("%.2f", totalAmount));
        holder.tvItemDescroption.setText(salesOrderLineList.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return salesOrderLineList.size();
    }

    public static class SalesItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvNo, tvItemNo, tvPrice, tvUom, tvQTY, tvTotal, tvItemDescroption;

        public SalesItemViewHolder(View v) {
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
