package com.gui.mdt.thongsieknavclient.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.StockRequest;
import com.gui.mdt.thongsieknavclient.ui.MvsStockRequestActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 10/2/2017.
 */

public class MvsStockRequestListAdapter extends RecyclerView.Adapter<MvsStockRequestListAdapter.ItemViewHolder> {


    private List<StockRequest> stockRequestList;
    private int rowLayout;
    private Context context;

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final MvsStockRequestListAdapter.ItemViewHolder viewHolder = new MvsStockRequestListAdapter.ItemViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                StockRequest stockRequestObj = stockRequestList.get(position);
                String objAsJson = stockRequestObj.toJson();

                Intent intent = new Intent(context, MvsStockRequestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(context.getResources().getString(R.string.stock_request_jason_obj), objAsJson);
                context.startActivity(intent);

            }
        });

        viewHolder.chkStockRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                if (viewHolder.chkStockRequest.isChecked()) {
                    stockRequestList.get(position).setConfirmedSr(true);
                } else {
                    stockRequestList.get(position).setConfirmedSr(false);
                }
            }
        });

        return viewHolder;
    }

    public MvsStockRequestListAdapter(List<StockRequest> _stockRequestList, int _rowLayout, Context _context) {
        this.stockRequestList = _stockRequestList;
        this.rowLayout = _rowLayout;
        this.context = _context;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        String amount = stockRequestList.get(position).getAmountInclVAT() == null?"0":
                String.format("%.2f", stockRequestList.get(position).getAmountInclVAT());
        String mDeliveryDate = dateConverter(stockRequestList.get(position).getOrderDate());

        holder.txtCustomerName.setText(stockRequestList.get(position).getSelltoCustomerName());
        holder.txtSrNo.setText("SR No: " + stockRequestList.get(position).getNo());
        holder.txtAmount.setText("Total Amount: $" + amount);
        //holder.txtAddress.setText(stockRequestList.get(position).getSellToCustomerAddress());
        holder.mTxtDeliveryDate.setText("Delivery date: "+mDeliveryDate);

        holder.mTxtTotalQty.setText("Total Quantity: "+String.valueOf(stockRequestList.get(position).getTotalQty()));
        holder.mTxtCusCode.setText(stockRequestList.get(position).getSelltoCustomerNo());

        if (stockRequestList.get(position).getStatus().equals(context.getResources().getString(R.string.StockRequestStatusConfirmed))) {
            //holder.layoutStockRequestCard.setBackgroundColor(context.getResources().getColor(R.color.listHqDownloaded));
            holder.chkStockRequest.setChecked(true);
            holder.chkStockRequest.setEnabled(false);

            if (stockRequestList.get(position).getTransferred() == true) {
                holder.layoutStockRequestCard.setBackgroundColor(context.getResources().getColor(R.color.listHqDownloaded));
                holder.txtSrNo.setTextColor(context.getResources().getColor(R.color.hockeyapp_text_black));
            } else {
                holder.layoutStockRequestCard.setBackgroundColor(context.getResources().getColor(R.color.listConfirmed));
                holder.txtSrNo.setTextColor(context.getResources().getColor(R.color.errorRed));
            }
        } else {
            if (stockRequestList.get(position).getStatus().equals(context.getResources().getString(R.string.StockRequestStatusPending))) //mobile created not confirmed
            {
                holder.chkStockRequest.setEnabled(true);
                holder.chkStockRequest.setChecked(false);
                holder.layoutStockRequestCard.setBackgroundResource(R.drawable.all_rectangle_white_background_blue_border);

                holder.txtSrNo.setTextColor(context.getResources().getColor(R.color.hockeyapp_text_black)); // updated on 2018-05-02
            }
            /*else if (stockRequestList.get(position).getStatus().equals(context.getResources().getString(R.string.StockRequestStatusComplete))) // HQ downloaded
            {
                holder.chkStockRequest.setEnabled(true);
                holder.chkStockRequest.setChecked(false);
                holder.layoutStockRequestCard.setBackgroundColor(context.getResources().getColor(R.color.listHqDownloaded));
            }*/

            if (stockRequestList.get(position).isConfirmedSr()) {
                holder.chkStockRequest.setChecked(true);
                holder.chkStockRequest.setEnabled(false);
            } else {
                holder.chkStockRequest.setEnabled(true);
                holder.chkStockRequest.setChecked(false);
                holder.layoutStockRequestCard.setBackgroundResource(R.drawable.all_rectangle_white_background_blue_border);
            }
        }
    }

    public String dateConverter(String date_)
    {
        String date="";

        if (date_ != null) {
            if (!date_.equals("")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                        context.getResources().getString(R.string.date_format_dd_MM_yyyy));
                try {

                    Date initDate = new SimpleDateFormat(
                            context.getResources().getString(R.string.date_format_yyyy_MM_dd)
                            , Locale.ENGLISH).parse(date_);
                    date = simpleDateFormat.format(initDate);

                } catch (Exception e) {
                    Log.e(context.getResources().getString(R.string.message_exception)
                            , e.getMessage().toString());
                }

            }
        }
        return date;
    }

    @Override
    public int getItemCount() {
        return stockRequestList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        CheckBox chkStockRequest;
        TextView txtCustomerName, txtAddress, txtSrNo, txtAmount, mTxtDeliveryDate;
        LinearLayout layoutStockRequestCard;
        TextView mTxtTotalQty, mTxtCusCode;

        public ItemViewHolder(final View v) {
            super(v);

            chkStockRequest = (CheckBox) v.findViewById(R.id.chkStockRequest);
            txtCustomerName = (TextView) v.findViewById(R.id.txtCustomerName);
            txtAddress = (TextView) v.findViewById(R.id.txtAddress);
            txtSrNo = (TextView) v.findViewById(R.id.txtSrNo);
            txtAmount = (TextView) v.findViewById(R.id.txtAmount);
            layoutStockRequestCard = (LinearLayout) v.findViewById(R.id.layoutStockRequestCard);
            mTxtDeliveryDate = (TextView) v.findViewById(R.id.txtDeliveryDate);

            mTxtTotalQty = (TextView) v.findViewById(R.id.txtTotalQty);
            mTxtCusCode = (TextView) v.findViewById(R.id.txtCusCode);
        }
    }
}
