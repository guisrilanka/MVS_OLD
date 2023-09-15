package com.gui.mdt.thongsieknavclient.adapters.grn;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.grnmodels.ReceiptItemLotEntry;
import com.gui.mdt.thongsieknavclient.ui.GRNOrderLotEntryActivity;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 23/08/16.
 */
public class PurchaseOrderReceiptLotEntryListArrayAdapter extends ArrayAdapter<ReceiptItemLotEntry> {
    Context context;

    ArrayList<ReceiptItemLotEntry> dataArray;

    ArrayAdapter<ReceiptItemLotEntry> adapter;

    public PurchaseOrderReceiptLotEntryListArrayAdapter(Context c, ArrayList<ReceiptItemLotEntry> dataSource) {
        super(c, R.layout.array_adapter_grn_line_item_lot_entry, R.id.lblProductionDate, dataSource);
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
            convertView = inflater.inflate(R.layout.array_adapter_grn_line_item_lot_entry, parent, false);

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
        if(dataArray.get(position) != null) {
            viewHolder.lblIndex.setText(String.valueOf(position + 1));

            String production_date = dataArray.get(position).ProductionDate.split("T")[0];
            if(production_date.contains("0001-"))
            {
                viewHolder.lblProductionDate.setText("N.A");
            }
            else
            {
                viewHolder.lblProductionDate.setText(production_date);
            }

            String expiration_date = dataArray.get(position).ExpirationDate.split("T")[0];
            if(expiration_date.contains("0001-"))
            {
                viewHolder.lblExpiryDate.setText("N.A");
            }
            else
            {
                viewHolder.lblExpiryDate.setText(expiration_date);
            }

            viewHolder.lblQtyToReceive.setText(dataArray.get(position).Quantity);
            viewHolder.lblLotNo.setText(dataArray.get(position).LotNo);

            viewHolder.btnPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((GRNOrderLotEntryActivity) context).PrintLotEntry(dataArray.get(position).LotNo, dataArray.get(position).SerialNo, dataArray.get(position).Quantity);
                }
            });
        }
        else {
        }

        return convertView;

    }

    @Override
    public ReceiptItemLotEntry getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public int getCount() {
        return dataArray.size();
    }

    static class ViewHolderItem {
        TextView lblIndex;
        TextView lblProductionDate;
        TextView lblExpiryDate;
        TextView lblQtyToReceive;
        TextView lblLotNo;
        Button btnPrint;

        public ViewHolderItem(View view)
        {
            lblIndex = (TextView) view.findViewById(R.id.lblIndex);
            lblProductionDate = (TextView) view.findViewById(R.id.lblProductionDate);
            lblExpiryDate = (TextView) view.findViewById(R.id.lblExpiryDate);
            lblQtyToReceive = (TextView) view.findViewById(R.id.lblQtyToReceive);
            lblLotNo = (TextView) view.findViewById(R.id.lblLotNo);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
        }
    }
}
