package com.gui.mdt.thongsieknavclient.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.StockTransferRequestLine;
import com.gui.mdt.thongsieknavclient.ui.MsoSalesOrderItemActivity;
import com.gui.mdt.thongsieknavclient.ui.MvsStockTransferRequestActivity;

import java.util.List;

/**
 * Created by GUI-NB03 on 2017-09-20.
 */

public class MvsStockTransferRequestListLineAdapter extends
                            RecyclerView.Adapter<MvsStockTransferRequestListLineAdapter.ItemViewHolder> {

    private static final int MVS_STOCK_TRANSFER_ITEM_ACTIVITY_RESULT_CODE = 2;
    private List<StockTransferRequestLine> transferInLineList;
    private int rowLayout;
    private Activity activity;
    private String status="";


    public MvsStockTransferRequestListLineAdapter(List<StockTransferRequestLine> transferRequestInLineList, int _rowLayout, MvsStockTransferRequestActivity _activity, String _status) {
        this.transferInLineList = transferRequestInLineList;
        this.rowLayout = _rowLayout;
        this.activity = _activity;
        this.status = _status;
    }

    @Override
    public MvsStockTransferRequestListLineAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final MvsStockTransferRequestListLineAdapter.ItemViewHolder viewHolder = new MvsStockTransferRequestListLineAdapter.ItemViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(status.equals(activity.getResources().getString(R.string.StockTransferStatusPending)))
                {
                    int position = viewHolder.getAdapterPosition();

                    StockTransferRequestLine transferInLineObj = transferInLineList.get(position);
                    String ObjToJason = transferInLineObj.toJson();

                    Intent intent = new Intent(activity, MsoSalesOrderItemActivity.class);
                    intent.putExtra(activity.getResources().getString(R.string.stock_transfer_request_line),ObjToJason);
                    intent.putExtra(activity.getResources().getString(R.string.adapter_position) , position);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivityForResult(intent, MVS_STOCK_TRANSFER_ITEM_ACTIVITY_RESULT_CODE);
                }


            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MvsStockTransferRequestListLineAdapter.ItemViewHolder holder, int position) {
        holder.tvNo.setText(String.valueOf(position + 1));
        holder.tvItemNo.setText(transferInLineList.get(position).getItemNo());
        holder.tvUom.setText(transferInLineList.get(position).getUnitofMeasure() + " Kg");
        holder.tvQTY.setText(String.valueOf(Math.round(transferInLineList.get(position).getQuantity())));
        holder.tvItemDescroption.setText(transferInLineList.get(position).getItemDescription());
    }

    @Override
    public int getItemCount() {
        return transferInLineList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView tvNo,tvItemNo,tvItemDescroption,tvUom,tvQTY;

        public ItemViewHolder(View v) {
            super(v);

            tvNo = (TextView) v.findViewById(R.id.tvNo);
            tvItemNo = (TextView) v.findViewById(R.id.tvItemNo);
            tvUom = (TextView) v.findViewById(R.id.tvUom);
            tvQTY = (TextView) v.findViewById(R.id.tvQTY);
            tvItemDescroption = (TextView) v.findViewById(R.id.tvItemDescroption);
        }
    }
}
