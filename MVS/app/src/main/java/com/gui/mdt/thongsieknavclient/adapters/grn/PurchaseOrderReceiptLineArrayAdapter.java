package com.gui.mdt.thongsieknavclient.adapters.grn;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptListItem;
import com.gui.mdt.thongsieknavclient.model.grnmodels.ReceiptItemEntry;
import com.gui.mdt.thongsieknavclient.ui.GRNOrderLineActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by yeqim_000 on 23/08/16.
 */
public class PurchaseOrderReceiptLineArrayAdapter extends ArrayAdapter<ReceiptItemEntry>  {

    Context context;

    ArrayList<ReceiptItemEntry> dataArray;
    ArrayAdapter<ReceiptItemEntry> adapter;

    public PurchaseOrderReceiptLineArrayAdapter(Context c, ArrayList<ReceiptItemEntry> dataSource) {
        super(c, R.layout.array_adapter_grn_line_item_details, R.id.lblItemDescription, dataSource);
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
            convertView = inflater.inflate(R.layout.array_adapter_grn_line_item_details, parent, false);

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
            String mPurchaseOrderReceiptNo = DataManager.getInstance().mPurchaseOrderReceiptNo;

            viewHolder.lblItemDescription.setText(dataArray.get(position).ItemDescription);
            viewHolder.lblItemIDUom.setText("Item No: " + dataArray.get(position).ItemNo + " / " + dataArray.get(position).Uom);
            viewHolder.lblPurchaseQuantity.setText("Quantity: " + dataArray.get(position).Quantity);
            viewHolder.lblLineNo.setText(dataArray.get(position).LineNo);
            viewHolder.lblPurOrdNo.setText("PO NO: " + mPurchaseOrderReceiptNo);

            viewHolder.btnPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((GRNOrderLineActivity) context).PrintEntry(dataArray.get(position).LineNo);
                }
            });
        }
        else {
        }

        return convertView;

    }

    @Override
    public ReceiptItemEntry getItem(int position) {
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
        TextView lblLineNo;
        TextView lblPurOrdNo;
        Button btnPrint;

        public ViewHolderItem(View view)
        {
            lblItemDescription = (TextView) view.findViewById(R.id.lblItemDescription);
            lblItemIDUom = (TextView) view.findViewById(R.id.lblItemIDUom);
            lblPurchaseQuantity = (TextView) view.findViewById(R.id.lblPurchaseQuantity);
            lblLineNo = (TextView) view.findViewById(R.id.lblLineNo);
            lblPurOrdNo = (TextView) view.findViewById(R.id.lblPONum);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
        }


    }
}
