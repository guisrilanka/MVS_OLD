package com.gui.mdt.thongsieknavclient.adapters.purchaseorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderListItem;
import com.gui.mdt.thongsieknavclient.ui.PurchaseOrderActivity;

import java.util.ArrayList;

public class PurchaseOrderHeaderArrayAdapter extends ArrayAdapter<PurchaseOrderListItem> implements Filterable {

    Context context;

    ArrayList<PurchaseOrderListItem> dataArray;

    ArrayAdapter<PurchaseOrderListItem> adapter;

    public PurchaseOrderHeaderArrayAdapter(Context c, ArrayList<PurchaseOrderListItem> dataSource) {
        super(c, R.layout.array_adapter_order_header_list, R.id.lblPurchaseRecNo, dataSource);
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
            convertView = inflater.inflate(R.layout.array_adapter_order_header_list, parent, false);

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

            if (dataArray.get(position).getVendorName() != null) {
                viewHolder.lblVendorName.setText(dataArray.get(position).getVendorName());
            } else {
                viewHolder.lblVendorName.setText("");
            }

            if (dataArray.get(position).getPurchaseOrderNo() != null) {
                viewHolder.lblPurchaseOrderNo.setText("PO No: " + dataArray.get(position).getPurchaseOrderNo());
            } else {
                viewHolder.lblPurchaseOrderNo.setText("PO No: ");
            }

            String[] dateSplit = dataArray.get(position).getPurchaseOrderDate().split("T")[0].split("-");
            viewHolder.lblPODate.setText("PO Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);

            if (dataArray.get(position).getVendorShipmentNo() != null) {
                viewHolder.lblVendorShipNo.setText("Vendor Shipt No: " + dataArray.get(position).getVendorShipmentNo());
            } else {
                viewHolder.lblVendorShipNo.setText("Vendor Shipt No: ");
            }
            viewHolder.lblVendorShipNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PurchaseOrderActivity) context).updateVendorShipmentInfo(dataArray.get(position).getPurchaseOrderNo(), dataArray.get(position).getVendorShipmentNo());
                }
            });

            if (!dataArray.get(position).getReceiveDate().contains("0001-")) {
                dateSplit = dataArray.get(position).getReceiveDate().split("T")[0].split("-");
                viewHolder.lblReceiveDate.setText("Receive Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
            } else {
                viewHolder.lblReceiveDate.setText("Receive Date:   N.A.");
            }
        }

        return convertView;

    }

    @Override
    public PurchaseOrderListItem getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public int getCount() {
        return dataArray.size();
    }

    static class ViewHolderItem {
        TextView lblVendorName;
        TextView lblPurchaseOrderNo;
        TextView lblPODate;
        TextView lblReceiveDate;
        TextView lblVendorShipNo;

        public ViewHolderItem(View view) {
            lblVendorName = (TextView) view.findViewById(R.id.lblVendorName);
            lblPurchaseOrderNo = (TextView) view.findViewById(R.id.lblPurchaseRecNo);
            lblPODate = (TextView) view.findViewById(R.id.lblPODate);
            lblReceiveDate = (TextView) view.findViewById(R.id.lblReceiveDate);
            lblVendorShipNo = (TextView) view.findViewById(R.id.lblVendorShipNo);
        }
    }
}
