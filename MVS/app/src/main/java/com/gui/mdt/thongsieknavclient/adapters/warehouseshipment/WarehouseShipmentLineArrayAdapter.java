package com.gui.mdt.thongsieknavclient.adapters.warehouseshipment;

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
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentLineData;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class WarehouseShipmentLineArrayAdapter extends ArrayAdapter<WarehouseShipmentLineData> implements Filterable {

    Context context;

    ArrayList<WarehouseShipmentLineData> dataArray;

    ArrayAdapter<WarehouseShipmentLineData> adapter;

    public WarehouseShipmentLineArrayAdapter(Context c, ArrayList<WarehouseShipmentLineData> dataSource) {
        super(c, R.layout.array_adapter_warehouse_shipment_line, R.id.lblItemDescription, dataSource);
        this.context = c;
        this.dataArray = dataSource;

        this.adapter = this;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_warehouse_shipment_line, parent, false);

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

            viewHolder.lblItemDescription.setText(dataArray.get(position).getItemDescription());
            viewHolder.lblItemIDUom.setText("Item No: " + dataArray.get(position).getItemNo() + DataConverter.CheckNullString(dataArray.get(position).getUom(), " / "));
            viewHolder.lblPurchaseQuantity.setText("Ord Qty: " + dataArray.get(position).getQuantity());
            viewHolder.lblScanQuantity.setText("Qty to Scan: " + dataArray.get(position).getQuantityToShip());

            if(!dataArray.get(position).getQuantityToShip().equals("0.0"))
            {
                viewHolder.lblScanQuantity.setBackgroundColor(0xff99cc00);
            }


            /*String balance = dataArray.get(position).getQuantity();
            if(balance.equals("0.0"))
            {
                viewHolder.lblPurchaseQuantity.setBackgroundColor(0xff99cc00);
            }
            else
            {
                viewHolder.lblPurchaseQuantity.setBackgroundColor(0xffffffff);
            }*/
        } else {
        }

        return convertView;

    }

    @Override
    public WarehouseShipmentLineData getItem(int position) {
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
        TextView lblScanQuantity;

        //BUTTERKNIFE: INJECTED
        public ViewHolderItem(View view) {
            lblItemDescription = (TextView) view.findViewById(R.id.lblItemDescription);
            lblItemIDUom = (TextView) view.findViewById(R.id.lblItemIDUom);
            lblPurchaseQuantity = (TextView) view.findViewById(R.id.lblPurchaseQuantity);
            lblScanQuantity = (TextView) view.findViewById(R.id.lblScanQuantity);
        }
    }
}
