package com.gui.mdt.thongsieknavclient.adapters.stocktake;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntry;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.StockTakeItemLineEntries;
import com.gui.mdt.thongsieknavclient.ui.StockTakeLotEntryActivity;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class StockTakeLotEntryListArrayAdapter extends ArrayAdapter<StockTakeEntry> {

    Context context;
    ArrayList<StockTakeEntry> dataArray;
    ArrayAdapter<StockTakeEntry> adapter;

    public StockTakeLotEntryListArrayAdapter(Context c, ArrayList<StockTakeEntry> dataSource) {
        super(c, R.layout.array_adapter_stock_take_lot_entry_list_received_entry, R.id.lblProductionDate, dataSource);
        this.context = c;
        this.dataArray = dataSource;
        this.adapter = this;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_stock_take_lot_entry_list_received_entry, parent, false);

            viewHolder = new ViewHolderItem(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        if (dataArray.get(position) != null) {

            if (dataArray.get(position).IsUploaded != false) {
                viewHolder.lblIndex.setBackgroundColor(0xff99cc00);
            } else {
                viewHolder.lblIndex.setBackgroundColor(0x00000000);
            }
            viewHolder.lblIndex.setText(String.valueOf(position + 1));
            viewHolder.lblProductionDate.setText(dataArray.get(position).ProductionDate);
            viewHolder.lblExpiryDate.setText(dataArray.get(position).ExpiryDate);
            viewHolder.lblLotNo.setText(dataArray.get(position).LotNo);
            boolean tmp = dataArray.get(position).IsDoNotShowSystemQty;

            if(!tmp) {
                viewHolder.lblQtyToReceive.setText(dataArray.get(position).QtyToReceive + "/" + dataArray.get(position).QtyToHandleBase);
            }else {
                viewHolder.lblQtyToReceive.setText(dataArray.get(position).QtyToReceive);
            }

            viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Remove Entry")
                            .setMessage("Are you sure you want to remove this entry?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                    if (dataArray.get(position).IsUploaded) {
                                        Log.d("DELETE", "UPLOADED");
                                        ((StockTakeLotEntryActivity) context).RemoveEntry(position, dataArray.get(position).ServerEntryIndexNo, dataArray.get(position).LotNo, Float.parseFloat(dataArray.get(position).QtyToReceive));
                                    } else {
                                        Log.d("DELETE", "NOT UPLOADED");
                                        List<StockTakeItemLineEntries> entryList = SugarRecord.find(StockTakeItemLineEntries.class, "entry_index_no = ?", String.valueOf(dataArray.get(position).LocalEntryIndexNo));

                                            String temp_lotNo = dataArray.get(position).LotNo;
                                            dataArray.remove(position);
                                            adapter.notifyDataSetChanged();

                                            ((StockTakeLotEntryActivity) context).RemoveSerialEntry(temp_lotNo);

                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            });
        }
        return convertView;
    }

    public StockTakeEntry getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    static class ViewHolderItem {
        TextView lblIndex;
        TextView lblProductionDate;
        TextView lblExpiryDate;
        TextView lblQtyToReceive;
        TextView lblLotNo;
        Button btnRemove;

        public ViewHolderItem(View view) {
            lblIndex = (TextView) view.findViewById(R.id.lblIndex);
            lblProductionDate = (TextView) view.findViewById(R.id.lblProductionDate);
            lblExpiryDate = (TextView) view.findViewById(R.id.lblExpiryDate);
            lblQtyToReceive = (TextView) view.findViewById(R.id.lblQtyToReceive);
            lblLotNo = (TextView) view.findViewById(R.id.lblLotNo);
            btnRemove = (Button) view.findViewById(R.id.btnRemove);
        }
    }
}