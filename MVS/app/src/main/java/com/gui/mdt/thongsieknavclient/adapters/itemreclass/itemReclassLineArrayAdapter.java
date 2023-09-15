package com.gui.mdt.thongsieknavclient.adapters.itemreclass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineData;
import com.gui.mdt.thongsieknavclient.ui.ItemReclassificationLineActivity;

import java.util.ArrayList;


public class itemReclassLineArrayAdapter extends ArrayAdapter<ItemReclassLineData> {

    Context context;
    ArrayList<ItemReclassLineData> dataArray;
    ArrayAdapter<ItemReclassLineData> adapter;
    boolean isShowBinCode, isShowNewBinCode;

    public itemReclassLineArrayAdapter(Context c, ArrayList<ItemReclassLineData> dataSource, boolean mIsShowBinCode, boolean mIsShowNewBinCode) {
        super(c, R.layout.array_adapter_item_reclass_line, R.id.lblItemDes, dataSource);
        this.context = c;
        this.dataArray = dataSource;
        this.adapter = this;
        this.isShowBinCode = mIsShowBinCode;
        this.isShowNewBinCode = mIsShowNewBinCode;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_item_reclass_line, parent, false);

            viewHolder = new ViewHolderItem(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }


        if (dataArray.get(position) != null) {

            viewHolder.lblNewBin.setText("New Bin: " + dataArray.get(position).getNewBinCode());
            viewHolder.lblItemDes.setText(dataArray.get(position).getItemDescription());
            viewHolder.lblItemNoBaseUOM.setText("Item No: " + dataArray.get(position).getItemNo() + "/" + dataArray.get(position).getUom());
            viewHolder.lblShipQty.setText("Qty to Trn: " + dataArray.get(position).getQuantity());
            viewHolder.lblQtyBase.setText("Qty Base: " + dataArray.get(position).getQuantityBase());

            if (dataArray.get(position).getQuantity() != 0f) {
                viewHolder.lblShipQty.setBackgroundColor(0xff99cc00);
            }

            if (isShowBinCode) {
                if (dataArray.get(position).getBinCode() != null) {
                    viewHolder.lblBin.setVisibility(View.VISIBLE);
                    viewHolder.lblBin.setText("Bin: " + dataArray.get(position).getBinCode());
                } else {
                    viewHolder.lblBin.setText("Bin: " + "");
                    viewHolder.lblBin.setVisibility(View.VISIBLE);
                }
            } else {
                viewHolder.lblBin.setText("Bin: " + "");
                viewHolder.lblBin.setVisibility(View.VISIBLE);
            }

            if (isShowNewBinCode) {
                if (dataArray.get(position).getNewBinCode() != null) {
                    viewHolder.lblNewBin.setVisibility(View.VISIBLE);
                    viewHolder.lblNewBin.setText("New Bin: " + dataArray.get(position).getNewBinCode());
                } else {
                    viewHolder.lblNewBin.setText("New Bin: " + "");
                    viewHolder.lblNewBin.setVisibility(View.VISIBLE);
                }
            }else {
                viewHolder.lblNewBin.setText("New Bin: " + "");
            }

            viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ItemReclassificationLineActivity) context).RemoveLine(dataArray.get(position).getItemNo(), dataArray.get(position).getLineNo());
                }
            });
        }

        return convertView;
    }

    @Override
    public ItemReclassLineData getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public int getCount() {
        return dataArray.size();
    }

    static class ViewHolderItem {
        TextView lblLoc;
        TextView lblNewLoc;
        TextView lblBin;
        TextView lblNewBin;
        TextView lblItemDes;
        TextView lblItemNoBaseUOM;
        TextView lblShipQty;
        TextView lblQtyBase;
        Button btnRemove;
        RelativeLayout relativeLayout;

        public ViewHolderItem(View view) {
            lblLoc = (TextView) view.findViewById(R.id.lblLoc);
            lblNewLoc = (TextView) view.findViewById(R.id.lblNewLoc);
            lblBin = (TextView) view.findViewById(R.id.lblBin);
            lblNewBin = (TextView) view.findViewById(R.id.lblNewBin);
            lblItemDes = (TextView) view.findViewById(R.id.lblItemDes);
            lblItemNoBaseUOM = (TextView) view.findViewById(R.id.lblItemNoBaseUOM);
            lblShipQty = (TextView) view.findViewById(R.id.lblShipQty);
            lblQtyBase = (TextView) view.findViewById(R.id.lblQtyBase);
            btnRemove = (Button) view.findViewById(R.id.btnRemove);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        }
    }
}