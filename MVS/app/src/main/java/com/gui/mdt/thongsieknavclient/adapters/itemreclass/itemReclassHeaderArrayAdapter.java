package com.gui.mdt.thongsieknavclient.adapters.itemreclass;

/**
 * Created by user on 1/17/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassListResultData;
import com.gui.mdt.thongsieknavclient.ui.ItemReclassificationActivity;

import java.util.ArrayList;


public class itemReclassHeaderArrayAdapter extends ArrayAdapter<ItemReclassListResultData> {

    Context context;
    ArrayList<ItemReclassListResultData> dataArray;
    ArrayAdapter<ItemReclassListResultData> adapter;

    public itemReclassHeaderArrayAdapter(Context context, ArrayList<ItemReclassListResultData> objects) {
        super(context, R.layout.item_reclassification_header_adapter, R.id.lblDocNo, objects);

        this.context = context;
        this.dataArray = objects;
        this.adapter = this;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_reclassification_header_adapter, parent, false);

            viewHolder = new ViewHolderItem(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        if (dataArray.get(position) != null) {
            viewHolder.lblDocNo.setText("Doc No: " + dataArray.get(position).getDocumentNo());
            viewHolder.lblLoc.setText("Loc: " + dataArray.get(position).getLocationCode());
            viewHolder.lblNewLoc.setText("New Loc: " + dataArray.get(position).getNewLocationCode());

            if (dataArray.get(position).getCreatedBy() != null) {
                viewHolder.llCreator.setVisibility(View.VISIBLE);
                viewHolder.lblCreator.setText("Created By: " + dataArray.get(position).getCreatedBy());
            } else {
                viewHolder.llCreator.setVisibility(View.GONE);
            }

            if (dataArray.get(position).getBinCode() != null) {
                viewHolder.lblBin.setText("Bin: " + dataArray.get(position).getBinCode());
            } else {
                viewHolder.lblBin.setVisibility(View.GONE);
            }

            if (dataArray.get(position).getNewBinCode() != null) {
                viewHolder.lblNewBin.setText("New Bin: " + dataArray.get(position).getNewBinCode());
            } else {
                viewHolder.lblNewBin.setVisibility(View.GONE);
            }

            String[] dateSplit = dataArray.get(position).getPostingDate().split("T")[0].split("-");
            viewHolder.lblPostDate.setText("Post Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);

            if (!dataArray.get(position).getPostingDate().contains("0001-")) {
                dateSplit = dataArray.get(position).getPostingDate().split("T")[0].split("-");
                viewHolder.lblPostDate.setText("Post Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
            } else {
                viewHolder.lblPostDate.setText("Post Date:   N.A.");
            }

            viewHolder.btnRemoveHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ItemReclassificationActivity) context).RemoveHeader(dataArray.get(position).getReclassHeaderLineNo());
                }
            });
        }

        return convertView;
    }

    @Override
    public ItemReclassListResultData getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public int getCount() {
        return dataArray.size();
    }

    static class ViewHolderItem {
        TextView lblDocNo;
        TextView lblPostDate;
        TextView lblLoc;
        TextView lblNewLoc;
        TextView lblBin;
        TextView lblNewBin;
        TextView lblCreator;
        Button btnRemoveHeader;
        LinearLayout llCreator;

        public ViewHolderItem(View view) {
            lblDocNo = (TextView) view.findViewById(R.id.lblDocNo);
            lblPostDate = (TextView) view.findViewById(R.id.lblPostDate);
            lblLoc = (TextView) view.findViewById(R.id.lblLoc);
            lblNewLoc = (TextView) view.findViewById(R.id.lblNewLoc);
            lblBin = (TextView) view.findViewById(R.id.lblBin);
            lblNewBin = (TextView) view.findViewById(R.id.lblNewBin);
            lblCreator = (TextView) view.findViewById(R.id.lblCreator);
            btnRemoveHeader = (Button) view.findViewById(R.id.btnRemove);
            llCreator = (LinearLayout) view.findViewById(R.id.llCreator);
        }
    }
}