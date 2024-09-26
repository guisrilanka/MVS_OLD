package com.gui.mdt.thongsieknavclient.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.SalesCustomerArResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by bhanuka on 25/07/2017.
 */

public class SalesCustomerArAdapter extends RecyclerView.Adapter<SalesCustomerArAdapter.CusArViewHolder> {

    List<SalesCustomerArResponse.SalesCustomerAr> customerArList,mCheckedCustomerArList;
    private int mRowLayout;
    private Context mContext;
    private boolean isChkNeed = false;

    @Override
    public SalesCustomerArAdapter.CusArViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mview = LayoutInflater.from(parent.getContext()).inflate(mRowLayout, parent, false);
        final SalesCustomerArAdapter.CusArViewHolder viewHolder = new SalesCustomerArAdapter.CusArViewHolder(mview);

        viewHolder.mChkInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                if (viewHolder.mChkInvoice.isChecked()) {
                    customerArList.get(position).setArSelected(true);
                    addToCheckedList(customerArList.get(position));

                } else {
                    customerArList.get(position).setArSelected(false);
                    removeFromCheckedList(customerArList.get(position));
                }
            }
        });

        if(isChkNeed==true)
        {
            viewHolder.mChkInvoice.setVisibility(View.VISIBLE);
        }

        return viewHolder;
    }

    public void addToCheckedList(SalesCustomerArResponse.SalesCustomerAr  customerArObj_)
    {
        if(!mCheckedCustomerArList.isEmpty())
        {
            boolean isListContains = true;
            for(int i=0; i<mCheckedCustomerArList.size(); i++)
            {
                SalesCustomerArResponse.SalesCustomerAr arObj = mCheckedCustomerArList.get(i);
                if(!customerArObj_.getInvoiceNo().equals(arObj.getInvoiceNo()))
                {
                    isListContains = false;
                }
            }
            if(isListContains==false)
            {
                mCheckedCustomerArList.add(customerArObj_);
            }
        }
        else
        {
            mCheckedCustomerArList.add(customerArObj_);
        }
    }

    public void removeFromCheckedList(SalesCustomerArResponse.SalesCustomerAr  customerArObj_)
    {
        if(!mCheckedCustomerArList.isEmpty())
        {
            boolean isListContains = false;
            int index=-1;
            for(int i=0; i<mCheckedCustomerArList.size(); i++)
            {
                SalesCustomerArResponse.SalesCustomerAr arObj = mCheckedCustomerArList.get(i);
                if(customerArObj_.getInvoiceNo().equals(arObj.getInvoiceNo()))
                {
                    isListContains = true;
                    index=i;
                }
            }
            if(isListContains==true && index > -1)
            {
                mCheckedCustomerArList.remove(index);
            }
        }

    }

    @Override
    public void onBindViewHolder(SalesCustomerArAdapter.CusArViewHolder holder, int position) {

        holder.tvInvoiceNumber.setText(customerArList.get(position).getInvoiceNo());
        holder.tvBalance.setText("S$ " + customerArList.get(position).getBalance());

        String mDueDate = dateConverter(customerArList.get(position).getDue_Date());
        String mInvoiceDate = dateConverter(customerArList.get(position).getDocumentDate());

        holder.tvDueDate.setText(mDueDate);
        holder.tvInvoiceDate.setText(mInvoiceDate);

        if(customerArList.get(position).getArSelected() == true)
        {
            holder.mChkInvoice.setChecked(true);
        }
        else
        {
            holder.mChkInvoice.setChecked(false);
        }
    }

    public String dateConverter(String date_)
    {
        String date="";

        if (date_ != null) {
            if (!date_.equals("")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mContext.getResources().getString(R.string.date_format_dd_MM_yyyy));
                try {

                    Date initDate = new SimpleDateFormat(mContext.getResources().getString(R.string.date_format_yyyy_MM_dd), Locale.ENGLISH).parse(date_);
                    date = simpleDateFormat.format(initDate);

                } catch (Exception e) {
                    Log.e(mContext.getResources().getString(R.string.message_exception), e.getMessage().toString());
                }

            }
        }
        return date;
    }

    @Override
    public int getItemCount() {
        return customerArList.size();
    }

    public SalesCustomerArAdapter(List<SalesCustomerArResponse.SalesCustomerAr> data, int rowLayout, Context context) {
        this.mRowLayout = rowLayout;
        this.customerArList = data;
        this.mContext = context;
    }

    public SalesCustomerArAdapter(List<SalesCustomerArResponse.SalesCustomerAr> data,
                                  int rowLayout,
                                  Context context,
                                  boolean isChkNeed_,
                                  List<SalesCustomerArResponse.SalesCustomerAr> chackedList) {
        this.mRowLayout = rowLayout;
        this.customerArList = data;
        this.mContext = context;
        this.isChkNeed = isChkNeed_;
        this.mCheckedCustomerArList = chackedList;
    }

    public void clear() {
        customerArList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<SalesCustomerArResponse.SalesCustomerAr> list) {
        customerArList.addAll(list);
        notifyDataSetChanged();
    }

    public static class CusArViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceNumber, tvInvoiceDate, tvDueDate, tvBalance;
        CheckBox mChkInvoice;

        public CusArViewHolder(View v) {
            super(v);
            tvInvoiceDate = (TextView) v.findViewById(R.id.tvInvoiceDate);
            tvInvoiceNumber = (TextView) v.findViewById(R.id.tvInvoiceNumber);
            tvBalance = (TextView) v.findViewById(R.id.tvBalance);
            tvDueDate = (TextView) v.findViewById(R.id.tvDueDate);
            mChkInvoice = (CheckBox)v.findViewById(R.id.chkInvoice);
        }
    }


}


