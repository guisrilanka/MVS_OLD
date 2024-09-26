package com.gui.mdt.thongsieknavclient.adapters.stocktake;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeLineData;

import java.util.ArrayList;

public class StockTakeLineArrayAdapter extends ArrayAdapter<StockTakeLineData> {

    Context context;
    ArrayList<StockTakeLineData> dataArray;
    ArrayAdapter<StockTakeLineData> adapter;

    public StockTakeLineArrayAdapter(Context c, ArrayList<StockTakeLineData> dataSource) {
        super(c, R.layout.array_adapter_stock_take_line, R.id.lblItemDescription, dataSource);
        this.context = c;
        this.dataArray = dataSource;
        this.adapter = this;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_stock_take_line, parent, false);

            viewHolder = new ViewHolderItem(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        if (dataArray.get(position) != null) {

            String[] dateSplit = dataArray.get(position).getPostingDate().split("T")[0].split("-");
            viewHolder.lblPostDate.setText("Post Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
            viewHolder.lblItemDes.setText(dataArray.get(position).getDescription());
            viewHolder.lblItemNoBaseUOM.setText("Item No: " + dataArray.get(position).getItemNo() + "/" + dataArray.get(position).getUom());
            viewHolder.lblPhyQty.setText("Qty(Phy): " + String.valueOf(dataArray.get(position).getQuantityPhysical()));

            if(dataArray.get(position).isDoNotShowSystemQty() == false) {
                viewHolder.lblCalQty.setVisibility(View.VISIBLE);
                viewHolder.lblCalQty.setText("Qty(Calc): " + String.valueOf(dataArray.get(position).getQuantityCalculated()));
            }else {
                viewHolder.lblCalQty.setVisibility(View.GONE);
            }

            if (dataArray.get(position).getQuantityPhysical() != 0f) {
                viewHolder.lblPhyQty.setBackgroundColor(0xff99cc00);
            }
            if (dataArray.get(position).getLocationCode() != null) {
                viewHolder.lblLoc.setText("Loc: " + dataArray.get(position).getLocationCode());
            } else {
                viewHolder.lblLoc.setText("Loc: " + "");
            }

            if (dataArray.get(position).getBinCode() != null) {
                viewHolder.lblBin.setText("Bin: " + dataArray.get(position).getBinCode());
            } else {
                viewHolder.lblBin.setText("Bin: " + "");
            }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public StockTakeLineData getItem(int position) {
        return dataArray.get(position);
    }

    static class ViewHolderItem {

        TextView lblPostDate;
        TextView lblLoc;
        TextView lblBin;
        TextView lblItemDes;
        TextView lblItemNoBaseUOM;
        TextView lblPhyQty;
        TextView lblCalQty;

        public ViewHolderItem(View view) {
            lblPostDate = (TextView) view.findViewById(R.id.lblPostDate);
            lblLoc = (TextView) view.findViewById(R.id.lblLoc);
            lblBin = (TextView) view.findViewById(R.id.lblBin);
            lblItemDes = (TextView) view.findViewById(R.id.lblItemDesc);
            lblItemNoBaseUOM = (TextView) view.findViewById(R.id.lblItemNoBaseUOM);
            lblPhyQty = (TextView) view.findViewById(R.id.lblPhyQty);
            lblCalQty = (TextView) view.findViewById(R.id.lblCalQty);
        }
    }
}