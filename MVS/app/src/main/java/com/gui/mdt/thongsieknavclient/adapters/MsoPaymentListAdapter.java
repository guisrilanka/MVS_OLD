package com.gui.mdt.thongsieknavclient.adapters;

import android.app.Activity;
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
import com.gui.mdt.thongsieknavclient.datamodel.PaymentHeader;
import com.gui.mdt.thongsieknavclient.ui.MsoPaymentActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 8/28/2017.
 */

public class MsoPaymentListAdapter extends RecyclerView.Adapter<MsoPaymentListAdapter.PaymentItemViewHolder> {

    private List<PaymentHeader> paymentHeaderList;
    private int rowLayout;
    private Activity activity;

    @Override
    public MsoPaymentListAdapter.PaymentItemViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final MsoPaymentListAdapter.PaymentItemViewHolder viewHolder = new MsoPaymentListAdapter.PaymentItemViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = viewHolder.getAdapterPosition();
                PaymentHeader paymentHeaderObj = paymentHeaderList.get(position);
                String ObjToJason = paymentHeaderObj.toJson();

                Intent intent = new Intent(activity, MsoPaymentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("paymentHeaderObj", ObjToJason);
                activity.startActivity(intent);

            }
        });
        viewHolder.chkMarkPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                ;
                if (viewHolder.chkMarkPayment.isChecked()) {
                    paymentHeaderList.get(position).setChecked(true);
                } else {
                    paymentHeaderList.get(position).setChecked(false);
                }
            }
        });
        return viewHolder;
    }

    public MsoPaymentListAdapter(List<PaymentHeader> paymentHeader_, int rowLayout_, Activity activity_) {
        this.paymentHeaderList = paymentHeader_;
        this.rowLayout = rowLayout_;
        this.activity = activity_;
    }

    @Override
    public void onBindViewHolder(MsoPaymentListAdapter.PaymentItemViewHolder holder, int position) {

        if (paymentHeaderList.get(position).getStatus().equals(activity.getResources().getString(R.string.SalesOrderStatusConfirmed))) {
            holder.layoutHeader.setBackgroundColor(activity.getResources().getColor(R.color.listConfirmed));
            holder.chkMarkPayment.setChecked(true);
            holder.chkMarkPayment.setEnabled(false);
        } else {
            if (paymentHeaderList.get(position).getStatus().equals(activity.getResources().getString(R.string.SalesOrderStatusPending))) //mobile created not confirmed
            {
                holder.chkMarkPayment.setEnabled(true);
                holder.chkMarkPayment.setChecked(false);
                holder.layoutHeader.setBackgroundResource(R.drawable.all_rectangle_white_background_blue_border);
            } else if (paymentHeaderList.get(position).getStatus().equals(activity.getResources().getString(R.string.SalesOrderStatusComplete))) // HQ downloaded
            {
                holder.chkMarkPayment.setEnabled(false);
                holder.chkMarkPayment.setChecked(true);
                holder.layoutHeader.setBackgroundColor(activity.getResources().getColor(R.color.listHqDownloaded));
            }

            if (paymentHeaderList.get(position).isChecked()) {
                holder.chkMarkPayment.setChecked(true);
            } else {
                holder.chkMarkPayment.setChecked(false);
            }
        }
        holder.txtCustomerName.setText(paymentHeaderList.get(position).getCustomerName());
        holder.txtPaymentNo.setText(paymentHeaderList.get(position).getPaymentNo());
        holder.txtPaymentDate.setText(formatDate(paymentHeaderList.get(position).getPaymentDate()));

        String cashAmt="0.0", chequeAmt="0.0";

        if(paymentHeaderList.get(position).getCashAmount()==null ||
                paymentHeaderList.get(position).getCashAmount().equals(""))
        {
            cashAmt="0.0";
        }
        else
        {
            cashAmt = paymentHeaderList.get(position).getCashAmount();
        }

        if(paymentHeaderList.get(position).getChequeAmount()==null ||
                paymentHeaderList.get(position).getChequeAmount().equals(""))
        {
            chequeAmt="0.0";
        }
        else
        {
            chequeAmt = paymentHeaderList.get(position).getChequeAmount();
        }

        holder.mTxtCashAmt.setText("$ "+cashAmt);
        holder.mTxtChequeAmt.setText("$ "+chequeAmt);
    }

    public String formatDate(String date)
    {
        String convertedDate = "";
        if(!date.equals("")) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            try {

                Date initDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                convertedDate = df.format(initDate);

            } catch (Exception e) {
                Log.e("Exception :", e.getMessage().toString());
            }
        }
        return convertedDate;
    }

    @Override
    public int getItemCount() {
        return paymentHeaderList.size();
    }

    public static class PaymentItemViewHolder extends RecyclerView.ViewHolder {

        TextView txtCustomerName, txtPaymentNo, txtPaymentDate ,mTxtCashAmt, mTxtChequeAmt;
        LinearLayout layoutHeader;
        CheckBox chkMarkPayment;

        public PaymentItemViewHolder(View v) {
            super(v);

            layoutHeader = (LinearLayout) v.findViewById(R.id.layoutHeader);
            txtCustomerName = (TextView) v.findViewById(R.id.txtCustomerName);
            txtPaymentNo = (TextView) v.findViewById(R.id.txtPaymentNo);
            txtPaymentDate = (TextView) v.findViewById(R.id.txtPaymentDate);
            chkMarkPayment = (CheckBox) v.findViewById(R.id.chkMarkPayment);
            mTxtCashAmt = (TextView) v.findViewById(R.id.txtCashAmt);
            mTxtChequeAmt = (TextView) v.findViewById(R.id.txtChequeAmt);
        }
    }
}
