package com.gui.mdt.thongsieknavclient.adapters.warehouseshipment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentListResultData;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class WarehouseShipmentHeaderArrayAdapter extends ArrayAdapter<WarehouseShipmentListResultData> {

    Context context;

    ArrayList<WarehouseShipmentListResultData> dataArray;

    ArrayAdapter<WarehouseShipmentListResultData> adapter;

    public WarehouseShipmentHeaderArrayAdapter(Context c, ArrayList<WarehouseShipmentListResultData> dataSource) {
        super(c, R.layout.array_adapter_warehouse_shipment_header, R.id.lblWarehouseShipmentNo, dataSource);
        this.context = c;
        this.dataArray = dataSource;

        //this.filteredArray = dataSource;

        this.adapter = this;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_warehouse_shipment_header, parent, false);

            viewHolder = new ViewHolderItem(convertView);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }


        // assign values if the object is not null
        //if(position < filteredArray.size()) {
        if (dataArray.get(position) != null) {

            viewHolder.lblWarehouseShipmentNo.setText(dataArray.get(position).getWarehouseShipmentNo());

            if(dataArray.get(position).getDriverCode() != null)
            {
                viewHolder.lblDriverCode.setText("Driver Code: " + dataArray.get(position).getDriverCode());
            }
            else
            {
                viewHolder.lblDriverCode.setText("Driver Code: N.A.");
            }
            String[] dateSplit;


            if (!dataArray.get(position).getShipmentDate().contains("0001-")) {
                dateSplit = dataArray.get(position).getShipmentDate().split("T")[0].split("-");
                viewHolder.lblShipDate.setText("Ship Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
            } else {
                viewHolder.lblShipDate.setText("Ship Date:   N.A.");
            }

        } else {
        }

        return convertView;

    }

    @Override
    public WarehouseShipmentListResultData getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public int getCount() {
        return dataArray.size();
    }

    static class ViewHolderItem {
        TextView lblWarehouseShipmentNo;
        TextView lblDriverCode;
        TextView lblShipDate;
        TextView lblPostingDate;

        public ViewHolderItem(View view) {
            lblWarehouseShipmentNo = (TextView) view.findViewById(R.id.lblWarehouseShipmentNo);
            lblDriverCode = (TextView) view.findViewById(R.id.lblDriverCode);
            lblShipDate = (TextView) view.findViewById(R.id.lblShipDate);
            lblPostingDate = (TextView) view.findViewById(R.id.lblPostingDate);
        }

    }
}