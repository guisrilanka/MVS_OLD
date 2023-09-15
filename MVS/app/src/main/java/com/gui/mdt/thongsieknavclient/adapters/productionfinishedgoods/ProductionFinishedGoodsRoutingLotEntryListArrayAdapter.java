package com.gui.mdt.thongsieknavclient.adapters.productionfinishedgoods;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderRouteLotEntry;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.ProductionOrderRoutingItemLineEntries;
import com.gui.mdt.thongsieknavclient.ui.ProductionFinishedGoodsRoutingLotEntryActivity;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqim_000 on 06/10/16.
 */
public class ProductionFinishedGoodsRoutingLotEntryListArrayAdapter extends ArrayAdapter<ProductionOrderRouteLotEntry> {
    Context context;

    ArrayList<ProductionOrderRouteLotEntry> dataArray;

    ArrayAdapter<ProductionOrderRouteLotEntry> adapter;

    public ProductionFinishedGoodsRoutingLotEntryListArrayAdapter(Context c, ArrayList<ProductionOrderRouteLotEntry> dataSource) {
        super(c, R.layout.array_adapter_received_entry, R.id.lblProductionDate, dataSource);
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
            convertView = inflater.inflate(R.layout.array_adapter_received_entry, parent, false);

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
            if(dataArray.get(position).IsUploaded)
            {
                viewHolder.lblIndex.setBackgroundColor(0xff99cc00);
            }
            else
            {
                viewHolder.lblIndex.setBackgroundColor(0xffffffff);
            }
            viewHolder.lblIndex.setText(String.valueOf(position + 1));

            viewHolder.lblProductionDate.setText(dataArray.get(position).ProductionDate);
            viewHolder.lblExpiryDate.setText(dataArray.get(position).ExpiryDate);
            viewHolder.lblQtyToReceive.setText(dataArray.get(position).QtyProduced);
            viewHolder.lblLotNo.setText(dataArray.get(position).LotNo);

            viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Remove Entry")
                            .setMessage("Are you sure you want to remove this entry?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Log.d("DELETE", Boolean.toString(dataArray.get(position).IsUploaded));
                                    if(dataArray.get(position).IsUploaded)
                                    {
                                        Log.d("DELETE", "UPLOADED");
                                        //NotificationManager.ShowProgressDialog(context, NotificationManager.DIALOG_TITLE_DELETE_DATA, NotificationManager.DIALOG_MSG_DELETE);
                                        ((ProductionFinishedGoodsRoutingLotEntryActivity) context).RemoveEntry(position, dataArray.get(position).ServerEntryIndexNo, dataArray.get(position).LotNo, Float.parseFloat(dataArray.get(position).QtyProduced));
                                    }
                                    else
                                    {
                                        Log.d("DELETE", "NOT UPLOADED");
                                        List<ProductionOrderRoutingItemLineEntries> entryList = SugarRecord.find(ProductionOrderRoutingItemLineEntries.class, "entry_index_no = ?", String.valueOf(dataArray.get(position).LocalEntryIndexNo));

                                        if(entryList.size() == 1)
                                        {
                                            entryList.get(0).delete();

                                            String temp_lotNo = dataArray.get(position).LotNo;
                                            dataArray.remove(position);
                                            adapter.notifyDataSetChanged();
                                            ((ProductionFinishedGoodsRoutingLotEntryActivity) context).RemoveSerialEntry(temp_lotNo);
                                        }
                                        else
                                        {
                                            NotificationManager.DisplayAlertDialog(context, NotificationManager.ALERT_TITLE_INTERNAL_ERROR, NotificationManager.ALERT_MSG_INTERNAL_ERROR);
                                        }
                                    }

                                }})
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            });

        }
        else {
        }

        return convertView;

    }

    @Override
    public ProductionOrderRouteLotEntry getItem(int position) {
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

        public ViewHolderItem(View view)
        {
            lblIndex = (TextView) view.findViewById(R.id.lblIndex);
            lblProductionDate = (TextView) view.findViewById(R.id.lblProductionDate);
            lblExpiryDate = (TextView) view.findViewById(R.id.lblExpiryDate);
            lblQtyToReceive = (TextView) view.findViewById(R.id.lblQtyToReceive);
            lblLotNo = (TextView) view.findViewById(R.id.lblLotNo);
            btnRemove = (Button) view.findViewById(R.id.btnRemove);
        }


    }
}
