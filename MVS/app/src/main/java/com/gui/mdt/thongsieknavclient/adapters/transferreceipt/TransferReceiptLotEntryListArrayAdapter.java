package com.gui.mdt.thongsieknavclient.adapters.transferreceipt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.DataConverter;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.TransferReceiptItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptEntry;
import com.gui.mdt.thongsieknavclient.ui.TransferInLotEntryActivity;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class TransferReceiptLotEntryListArrayAdapter extends ArrayAdapter<TransferReceiptEntry> {
    Context context;

    EditText textLotNo;

    ArrayList<TransferReceiptEntry> dataArray;

    ArrayAdapter<TransferReceiptEntry> adapter;

    public TransferReceiptLotEntryListArrayAdapter(Context c, ArrayList<TransferReceiptEntry> dataSource) {
        super(c, R.layout.array_adapter_received_entry, R.id.lblProductionDate, dataSource);
        this.context = c;
        this.dataArray = dataSource;

        this.adapter = this;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_received_entry, parent, false);

            viewHolder = new ViewHolderItem(convertView);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }


        // assign values if the object is not null
        if (dataArray.get(position) != null) {
            viewHolder.lblIndex.setBackgroundColor(0xff99cc00);

            viewHolder.lblIndex.setText(String.valueOf(position + 1));

            viewHolder.lblProductionDate.setText(DataConverter.ConvertJsonDateToDayMonthYear(dataArray.get(position).ProductionDate));
            viewHolder.lblExpiryDate.setText(DataConverter.ConvertJsonDateToDayMonthYear(dataArray.get(position).ExpiryDate));
            viewHolder.lblQtyToReceive.setText(dataArray.get(position).QtyToReceive + "/" + dataArray.get(position).LotAvailableQuantity);
            viewHolder.lblLotNo.setText(dataArray.get(position).LotNo);

            //  Lot number are retrieved from server and cannot be removed, only clear the local quantity value
            viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (dataArray.get(position).QtyToReceive.equals("0.0") || dataArray.get(position).QtyToReceive.equals("0")) {
                        NotificationManager.DisplayAlertDialog(context, context.getResources().getString(R.string.notification_title_clear_invalid), context.getResources().getString(R.string.notification_msg_clear_invalid));
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle("Clear Entry")
                                .setMessage("Are you sure you want to clear this entry?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        ((TransferInLotEntryActivity) context).ClearEntryQuantity(position, dataArray.get(position).LotNo);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    }
                }
            });

        } else {
        }

        return convertView;

    }

    @Override
    public TransferReceiptEntry getItem(int position) {
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
