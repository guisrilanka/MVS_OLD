package com.gui.mdt.thongsieknavclient.adapters.purchaseorder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.DataConverter;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.LineData;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 28/07/16.
 */
public class PurchaseOrderLineArrayAdapter extends ArrayAdapter<LineData> implements Filterable {

    Context context;

    ArrayList<LineData> dataArray;

    ArrayAdapter<LineData> adapter;

    public PurchaseOrderLineArrayAdapter(Context c, ArrayList<LineData> dataSource) {
        super(c, R.layout.array_adapter_order_item_details, R.id.lblItemDescription, dataSource);
        this.context = c;
        this.dataArray = dataSource;

        this.adapter = this;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_order_item_details, parent, false);

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

            if (dataArray.get(position).getItemDescription() != null) {
                viewHolder.lblItemDescription.setText(dataArray.get(position).getItemDescription());
            } else {
                viewHolder.lblItemDescription.setText("");
            }

            if (dataArray.get(position).getItemNo() != null) {
                viewHolder.lblItemIDUom.setText("Item No: " + dataArray.get(position).getItemNo() + DataConverter.CheckNullString(dataArray.get(position).getUom(), " / "));
            }

            viewHolder.lblPurchaseQuantity.setText("Ord Qty: " + dataArray.get(position).getQuantity());
            viewHolder.lblBalanceQuantity.setText("Bal Qty: " + dataArray.get(position).getOutstandingQuantity());
            viewHolder.lblReceivedQuantity.setText("Qty to Rec: " + dataArray.get(position).getQuantityToReceive());
            viewHolder.tvNoOfLabel.setText("No.of Label: " + String.valueOf(dataArray.get(position).getNoOfLabel()));

            if (!dataArray.get(position).getQuantityToReceive().equals("0.0")) {
                viewHolder.lblReceivedQuantity.setBackgroundColor(0xff99cc00);
            } else {
                viewHolder.lblReceivedQuantity.setBackgroundColor(0xffffffff);
            }

//            float balance = Float.parseFloat(dataArray.get(position).getOutstandingQuantity());
            if (!dataArray.get(position).getOutstandingQuantity().equals("0.0")) {
                viewHolder.lblBalanceQuantity.setBackgroundColor(0xff99cc00);
            } else {
                viewHolder.lblBalanceQuantity.setBackgroundColor(0xffffffff);
            }

            try {
                Log.d("ITEM QUANTITY", dataArray.get(position).getQuantity() + " " + dataArray.get(position).getQuantityReceived() + " " + dataArray.get(position).getOutstandingQuantity());

                Log.d("ITEM QUANTITY", String.valueOf(dataArray.get(position).getIsItemTrackingRequired())
                        + " " + String.valueOf(dataArray.get(position).getIsItemExpireDateRequired())
                        + " " + String.valueOf(dataArray.get(position).getIsItemLotTrackingRequired())
                        + " " + String.valueOf(dataArray.get(position).getIsItemProductionDateRequired()));

                Log.d("ITEM QUANTITY", "testing");
            } catch (Exception ex) {
                NotificationManager.DisplayAlertDialog(context, NotificationManager.MSG_ERROR, ex.toString());
            }
        }

        return convertView;

    }

    @Override
    public LineData getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public int getCount() {
        return dataArray.size();
    }

    static class ViewHolderItem {
        TextView lblItemDescription;
        TextView lblItemIDUom;
        TextView lblPurchaseQuantity;
        TextView lblBalanceQuantity;
        TextView lblReceivedQuantity;
        TextView tvNoOfLabel;
        TextView lblVendorShipNo;
        RelativeLayout relativeLayout;

        public ViewHolderItem(View view) {
            lblItemDescription = (TextView) view.findViewById(R.id.lblItemDescription);
            lblItemIDUom = (TextView) view.findViewById(R.id.lblItemIDUom);
            lblPurchaseQuantity = (TextView) view.findViewById(R.id.lblPurchaseQuantity);
            lblBalanceQuantity = (TextView) view.findViewById(R.id.lblBalanceQuantity);
            lblReceivedQuantity = (TextView) view.findViewById(R.id.lblReceivedQuantity);
            tvNoOfLabel = (TextView) view.findViewById(R.id.tvNoOfLabel);
            lblVendorShipNo = (TextView) view.findViewById(R.id.lblVendorShipNo);

            relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        }
    }
}
