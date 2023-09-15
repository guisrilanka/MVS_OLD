package com.gui.mdt.thongsieknavclient.adapters.stocktake;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntriesListResultData;

import java.util.ArrayList;

/**
 * Created by user on 12/28/2016.
 */

public class StockTakeHeaderArrayAdapter extends ArrayAdapter<StockTakeEntriesListResultData> {

    Context context;
    ArrayList<StockTakeEntriesListResultData> dataArray;

    public StockTakeHeaderArrayAdapter(Context context, ArrayList<StockTakeEntriesListResultData> objects) {
        super(context, R.layout.stock_take_header_adapter, R.id.lblBatchName, objects);
        this.context = context;
        this.dataArray = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stock_take_header_adapter, parent, false);

            viewHolder = new ViewHolderItem(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        if (dataArray.get(position) != null) {
            if(dataArray.get(position).getDocumentNo() != null) {
                viewHolder.lblBatchName.setText("Doc No: " + dataArray.get(position).getDocumentNo());
            }else {
                viewHolder.lblBatchName.setText("Doc No: " + "");
            }
            if(dataArray.get(position).getLocationCode() != null) {
                viewHolder.lblBatchDesc.setText("Loc: " + dataArray.get(position).getLocationCode());
            }else {
                viewHolder.lblBatchDesc.setText("Loc: " + "");
            }
        }

        return convertView;
    }

    static class ViewHolderItem {
        TextView lblBatchName;
        TextView lblBatchDesc;

        public ViewHolderItem(View view) {
            lblBatchName = (TextView) view.findViewById(R.id.lblBatchName);
            lblBatchDesc = (TextView) view.findViewById(R.id.lblBatchDesc);
        }
    }
}