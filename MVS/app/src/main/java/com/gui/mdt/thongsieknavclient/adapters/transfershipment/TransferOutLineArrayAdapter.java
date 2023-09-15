package com.gui.mdt.thongsieknavclient.adapters.transfershipment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentLineData;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 25/08/16.
 */

public class TransferOutLineArrayAdapter extends ArrayAdapter<TransferShipmentLineData> {

    Context context;

    ArrayList<TransferShipmentLineData> dataArray;

    ArrayAdapter<TransferShipmentLineData> adapter;

    public TransferOutLineArrayAdapter(Context c, ArrayList<TransferShipmentLineData> dataSource) {
        super(c, R.layout.array_adapter_transfer_line_shared, R.id.lblItemDescription, dataSource);
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
            convertView = inflater.inflate(R.layout.array_adapter_transfer_line_shared, parent, false);

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

            viewHolder.lblItemDescription.setText(dataArray.get(position).getItemDescription());
            viewHolder.lblItemIDUom.setText("Item No: " + dataArray.get(position).getItemNo() + " / " + dataArray.get(position).getUom());
            viewHolder.lblPurchaseQuantity.setText("Ord Qty: " + dataArray.get(position).getQuantity());
            viewHolder.lblBalanceQuantity.setText("Bal Qty: " + dataArray.get(position).getOutstandingQuantity());
            viewHolder.lblTransitQuantity.setText("Transit Qty: " + dataArray.get(position).getQuantityInTransit());
            viewHolder.lblShipQuantity.setText("Qty to Ship: " + dataArray.get(position).getQuantityToShip());


            if(!dataArray.get(position).getQuantityToShip().equals("0.0"))
            {
                viewHolder.lblShipQuantity.setBackgroundColor(0xff99cc00);
            }

            /*String balance = dataArray.get(position).getQuantityInTransit();
            if(!balance.equals("0.0"))
            {
                viewHolder.lblTransitQuantity.setBackgroundColor(0xff99cc00);
            }
            else
            {
                viewHolder.lblTransitQuantity.setBackgroundColor(0xffffffff);
            }

            if(!dataArray.get(position).getOutstandingQuantity().equals("0.0"))
            {
                viewHolder.lblBalanceQuantity.setBackgroundColor(0xff99cc00);
            }

            if(!dataArray.get(position).getQuantity().equals("0.0"))
            {
                viewHolder.lblPurchaseQuantity.setBackgroundColor(0xff99cc00);
            }*/

            /*
            if(balance == 0)
            {
                viewHolder.relativeLayout.setBackgroundColor(0xff99cc00);
            }
            else
            {
                viewHolder.relativeLayout.setBackgroundColor(0xffffffff);
            }
            /*
            Log.d("ITEM QUANTITY", dataArray.get(position).getQuantity() + " " + dataArray.get(position).getQuantityReceived() + " " + dataArray.get(position).getOutstandingQuantity());


            Log.d("ITEM QUANTITY", String.valueOf(dataArray.get(position).getIsItemTrackingRequired())
                    + " " + String.valueOf(dataArray.get(position).getIsItemExpireDateRequired())
                    + " " + String.valueOf(dataArray.get(position).getIsItemLotTrackingRequired())
                    + " " + String.valueOf(dataArray.get(position).getIsItemProductionDateRequired()));

            Log.d("ITEM QUANTITY", "testing");
*/
        }
        else {
        }

        return convertView;

    }

    @Override
    public TransferShipmentLineData getItem(int position) {
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
        TextView lblTransitQuantity;
        TextView lblShipQuantity;
        RelativeLayout relativeLayout;

        //BUTTERKNIFE: INJECTED
        public ViewHolderItem(View view)
        {
            lblItemDescription = (TextView) view.findViewById(R.id.lblItemDescription);
            lblItemIDUom = (TextView) view.findViewById(R.id.lblItemIDUom);
            lblPurchaseQuantity = (TextView) view.findViewById(R.id.lblPurchaseQuantity);
            lblBalanceQuantity = (TextView) view.findViewById(R.id.lblBalanceQuantity);
            lblTransitQuantity = (TextView) view.findViewById(R.id.lblTransitQuantity);
            lblShipQuantity = (TextView) view.findViewById(R.id.lblShipQuantity);

            //relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        }


    }
}
