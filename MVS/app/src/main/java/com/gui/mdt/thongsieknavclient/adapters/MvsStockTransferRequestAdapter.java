package com.gui.mdt.thongsieknavclient.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.MvsStockTransferRequest;

import java.util.ArrayList;

/**
 * Created by BhanukaBandara on 7/17/17.
 */

public class MvsStockTransferRequestAdapter extends ArrayAdapter<MvsStockTransferRequest> implements View.OnClickListener{

    private ArrayList<MvsStockTransferRequest> dataSet;
    Context mContext;
    int rowNo=1;

    // View lookup cache
    private static class ViewHolder {
        TextView tvNo,tvItemNo, tvPrice, tvUom, tvQTY, tvTotal, tvItemDescroption;
    }


    public MvsStockTransferRequestAdapter(ArrayList<MvsStockTransferRequest> data, Context context) {
        super(context, R.layout.item_stock_tansfer_request_card, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        MvsStockTransferRequest dataModel=(MvsStockTransferRequest) object;
        Toast.makeText(getContext(), "Payment Collection Clicked", Toast.LENGTH_LONG).show();

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MvsStockTransferRequest dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {



            viewHolder = new MvsStockTransferRequestAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_stock_tansfer_request_card, parent, false);
            viewHolder.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
            viewHolder.tvItemNo = (TextView) convertView.findViewById(R.id.tvItemNo);
            viewHolder.tvUom = (TextView) convertView.findViewById(R.id.tvUom);
            viewHolder.tvQTY = (TextView) convertView.findViewById(R.id.tvQTY);;
            viewHolder.tvItemDescroption = (TextView) convertView.findViewById(R.id.tvItemDescroption);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MvsStockTransferRequestAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.tvNo.setText(String.valueOf(position+1));
        viewHolder.tvItemNo.setText(dataModel.getItemNo());
        viewHolder.tvUom.setText(dataModel.getUom()+" Kg");
        viewHolder.tvQTY.setText(dataModel.getQTY());
        viewHolder.tvItemDescroption.setText(dataModel.getItemDescription());

        rowNo++;
        return convertView;
    }

}
