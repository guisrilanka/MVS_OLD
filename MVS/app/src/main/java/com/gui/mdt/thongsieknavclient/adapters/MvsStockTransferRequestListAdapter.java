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
import com.gui.mdt.thongsieknavclient.datamodel.StockTransferRequest;
import com.gui.mdt.thongsieknavclient.ui.MvsStockTransferRequestActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by GUI-NB03 on 2017-09-18.
 */

public class MvsStockTransferRequestListAdapter extends
        RecyclerView.Adapter<MvsStockTransferRequestListAdapter.ItemViewHolder> {

    private List<StockTransferRequest> stockTransferRequestList;
    private int rowLayout;
    private Context context;
    private String formName;

    public MvsStockTransferRequestListAdapter(List<StockTransferRequest> _stockTransferRequestList,
                                              int _rowLayout, Context _context, String _formName) {
        this.stockTransferRequestList = _stockTransferRequestList;
        this.rowLayout = _rowLayout;
        this.context = _context;
        this.formName = _formName;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final MvsStockTransferRequestListAdapter.ItemViewHolder viewHolder = new MvsStockTransferRequestListAdapter.ItemViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                StockTransferRequest stockTransferRequestObj = stockTransferRequestList.get(position);
                String objAsJson = stockTransferRequestObj.toJson();

                Intent intent = new Intent(context, MvsStockTransferRequestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra(context.getString(R.string.stock_transfer_request), objAsJson);
                intent.putExtra(context.getString(R.string.intent_extra_form_name), formName);

                context.startActivity(intent);

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        if(formName.equals(context.getResources().getString(R.string.form_name_mvs_stock_transfer_in)))
        {
            holder.tvStockTransferNo.setText("Stock Transfer In No:");
            holder.tvStockTransferDate.setText("Stock Transfer In Date:");
        }
        else if(formName.equals(context.getResources().getString(R.string.form_name_mvs_stock_transfer_out)))
        {
            holder.tvStockTransferNo.setText("Stock Transfer Out No:");
            holder.tvStockTransferDate.setText("Stock Transfer Out Date:");
        }

        holder.txtStockTransferNo.setText(stockTransferRequestList.get(position).getNo());
        String stockTransferInDate = stockTransferRequestList.get(position).getStockTransferDate();

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date dt = fmt.parse(stockTransferInDate);

            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
            holder.txtStockTransferDate.setText(fmtOut.format(dt));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        holder.chkStockTransfer.setTag(position);

        String status = context.getResources().getString(R.string.StockTransferStatusConfirmed);

        if (stockTransferRequestList.get(position).getStatus().equals(status))
        {
            holder.chkStockTransfer.setChecked(true);
            holder.chkStockTransfer.setEnabled(false);
            holder.layoutStockTransferCard.setBackgroundColor(context.getResources().getColor(R.color.listConfirmed));
        }
        else
        {
            if(stockTransferRequestList.get(position).getStatus().equals(
                    context.getResources().getString(R.string.StockTransferStatusPending)))
            {
                holder.chkStockTransfer.setEnabled(true);
                holder.chkStockTransfer.setChecked(false);
                holder.layoutStockTransferCard.setBackgroundResource(R.drawable.all_rectangle_white_background_blue_border);
            }

            if(stockTransferRequestList.get(position).isConfirmedSt())
            {
                holder.chkStockTransfer.setChecked(true);
            }
            else
            {
                holder.chkStockTransfer.setChecked(false);
            }
        }

        holder.chkStockTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer pos = (Integer) holder.chkStockTransfer.getTag();
                if (holder.chkStockTransfer.isChecked()) {
                    stockTransferRequestList.get(pos).setConfirmedSt(true);
                } else {
                    stockTransferRequestList.get(pos).setConfirmedSt(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stockTransferRequestList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        CheckBox chkStockTransfer;
        TextView txtStockTransferNo, txtStockTransferDate, tvStockTransferNo, tvStockTransferDate;
        LinearLayout layoutStockTransferCard;

        public ItemViewHolder(View v) {
            super(v);

            chkStockTransfer = (CheckBox) v.findViewById(R.id.chkStockTransfer);
            txtStockTransferNo = (TextView) v.findViewById(R.id.txtStockTransferNo);
            txtStockTransferDate = (TextView) v.findViewById(R.id.txtStockTransferDate);
            layoutStockTransferCard = (LinearLayout) v.findViewById(R.id.layoutStockTransferCard);
            tvStockTransferNo = (TextView) v.findViewById(R.id.tvStockTransferNo);
            tvStockTransferDate = (TextView) v.findViewById(R.id.tvStockTransferDate);
        }
    }
}
