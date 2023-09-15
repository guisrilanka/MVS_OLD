package com.gui.mdt.thongsieknavclient.adapters.transferreceipt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptListResultData;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 05/09/16.
 */
public class TransferReceiptHeaderArrayAdapter extends ArrayAdapter<TransferReceiptListResultData> {
    Context context;

    ArrayList<TransferReceiptListResultData> dataArray;

    ArrayAdapter<TransferReceiptListResultData> adapter;

    public TransferReceiptHeaderArrayAdapter(Context c, ArrayList<TransferReceiptListResultData> dataSource) {
        super(c, R.layout.array_adapter_transfer_header_shared, R.id.lblTransferNo, dataSource);
        this.context = c;
        this.dataArray = dataSource;
        this.adapter = this;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_transfer_header_shared, parent, false);

            viewHolder = new ViewHolderItem(convertView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        if(dataArray.get(position) != null) {

            viewHolder.lblTransferNo.setText(dataArray.get(position).getTransferNo());
            viewHolder.lblTransferFrom.setText("From: " + dataArray.get(position).getTransferFrom());
            viewHolder.lblTransferTo.setText("To: " + dataArray.get(position).getTransferTo());
            String[] dateSplit = dataArray.get(position).getShipmentDate().split("T")[0].split("-");
            viewHolder.lblShipDate.setText("Ship Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);

            if(!dataArray.get(position).getShipmentDate().contains("0001-")) {
                dateSplit = dataArray.get(position).getShipmentDate().split("T")[0].split("-");
                viewHolder.lblShipDate.setText("Ship Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
            }
            else
            {
                viewHolder.lblShipDate.setText("Ship Date:   N.A.");
            }

        }
        else {
        }

        return convertView;

    }

    @Override
    public TransferReceiptListResultData getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public int getCount() {
        return dataArray.size();
    }

    static class ViewHolderItem {
        TextView lblTransferNo;
        TextView lblTransferFrom;
        TextView lblTransferTo;
        TextView lblShipDate;
        TextView lblPostingDate;

        public ViewHolderItem(View view)
        {
            lblTransferNo = (TextView) view.findViewById(R.id.lblTransferNo);
            lblTransferFrom = (TextView) view.findViewById(R.id.lblTransferFrom);
            lblTransferTo = (TextView) view.findViewById(R.id.lblTransferTo);
            lblShipDate = (TextView) view.findViewById(R.id.lblShipDate);
            lblPostingDate = (TextView) view.findViewById(R.id.lblPostingDate);
        }


    }
}
