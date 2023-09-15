package com.gui.mdt.thongsieknavclient.adapters.transfershipment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentListResultData;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 25/08/16.
 */
public class TransferOutHeaderArrayAdapter extends ArrayAdapter<TransferShipmentListResultData> {

    Context context;

    ArrayList<TransferShipmentListResultData> dataArray;

    ArrayAdapter<TransferShipmentListResultData> adapter;

    public TransferOutHeaderArrayAdapter(Context c, ArrayList<TransferShipmentListResultData> dataSource) {
        super(c, R.layout.array_adapter_transfer_header_shared, R.id.lblTransferNo, dataSource);
        this.context = c;
        this.dataArray = dataSource;

        //this.filteredArray = dataSource;

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

            // store the holder with the view.
            convertView.setTag(viewHolder);
        }
        else
        {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }


        // assign values if the object is not null
        //if(position < filteredArray.size()) {
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
    public TransferShipmentListResultData getItem(int position) {
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
