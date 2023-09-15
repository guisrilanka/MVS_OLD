package com.gui.mdt.thongsieknavclient.adapters.grn;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptListItem;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 22/08/16.
 */
public class PurchaseOrderReceiptHeaderArrayAdapter extends ArrayAdapter<PurchaseOrderReceiptListItem> implements Filterable {

    Context context;

    ArrayList<PurchaseOrderReceiptListItem> dataArray;
    ArrayList<PurchaseOrderReceiptListItem> filteredArray;

    ArrayAdapter<PurchaseOrderReceiptListItem> adapter;

    private String mFilterPurchaseOrderNo;
    private String mFilterVendorName;
    private String mFilterDate;

    public PurchaseOrderReceiptHeaderArrayAdapter(Context c, ArrayList<PurchaseOrderReceiptListItem> dataSource) {
        super(c, R.layout.array_adapter_order_list, R.id.lblPurOrdNum, dataSource);
        this.context = c;
        this.dataArray = dataSource;

        //this.filteredArray = dataSource;

        this.adapter = this;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_order_list, parent, false);

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
//        if(filteredArray.get(position) != null) {
//
//            viewHolder.lblVendorName.setText(filteredArray.get(position).getVendorName());
//            viewHolder.lblPurchaseRecNo.setText("PR No: " + filteredArray.get(position).getPurchaseOrderReceiptNo());
//            viewHolder.lblPurchaseOrdNo.setText("PO No: " + filteredArray.get(position).getPurchaseOrderNo());
//            String[] dateSplit = filteredArray.get(position).getPurchaseOrderDate().split("T")[0].split("-");
//            viewHolder.lblPODate.setText("Ord. Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
//            if(!filteredArray.get(position).getReceiveDate().contains("0001-")) {
//                dateSplit = filteredArray.get(position).getReceiveDate().split("T")[0].split("-");
//                viewHolder.lblReceiveDate.setText("Rec. Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
//            }
//            else
//            {
//                viewHolder.lblReceiveDate.setText("Rec. Date:   N.A.");
//            }
//
//        }
//        else {
//        }

        if(dataArray.get(position) != null) {

            viewHolder.lblVendorName.setText(dataArray.get(position).getVendorName());
            viewHolder.lblPurchaseRecNo.setText("PR No: " + dataArray.get(position).getPurchaseOrderReceiptNo());
            viewHolder.lblPurchaseOrdNo.setText("PO No: " + dataArray.get(position).getPurchaseOrderNo());
            String[] dateSplit = dataArray.get(position).getPurchaseOrderDate().split("T")[0].split("-");
            viewHolder.lblPODate.setText("Ord. Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
            if(!dataArray.get(position).getReceiveDate().contains("0001-")) {
                dateSplit = dataArray.get(position).getReceiveDate().split("T")[0].split("-");
                viewHolder.lblReceiveDate.setText("Rec. Date: " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
            }
            else
            {
                viewHolder.lblReceiveDate.setText("Rec. Date:   N.A.");
            }

        }
        else {
        }

        return convertView;

    }

    @Override
    public PurchaseOrderReceiptListItem getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public int getCount() {
        return dataArray.size(); //filteredArray
    }

//    @Override
//    public Filter getFilter() {
//
//        Filter filter = new Filter() {
//
//            @SuppressWarnings("unchecked")
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//
//                filteredArray = (ArrayList<PurchaseOrderReceiptListItem>) results.values;
//
//                if (results.count == 0)
//                {
//                    Log.d("FILTER STRING", "RESULT");
//                    notifyDataSetInvalidated();
//                    //NotificationManager.DisplayAlertDialog(context, NotificationManager.ALERT_TITLE_FILTER_RESULT, NotificationManager.ALERT_MSG_FILTER_NO_MATCH);
//                }
//                else {
//
//                    notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                Log.d("FILTER STRING", constraint.toString());
//                String[] filterSplit = constraint.toString().split("\\|\\|");
//
//                mFilterVendorName = filterSplit[0].toLowerCase();
//                mFilterPurchaseOrderNo = filterSplit[1].toLowerCase();
//                mFilterDate = filterSplit[2].trim();
//
//                Log.d("FILTER STRING", "1: " + mFilterPurchaseOrderNo + " 2: " + mFilterVendorName + " 3: " + mFilterDate);
//
//                FilterResults results = new FilterResults();
//                ArrayList<PurchaseOrderReceiptListItem> purchaseFilteredArray = new ArrayList<PurchaseOrderReceiptListItem>();
//                ArrayList<PurchaseOrderReceiptListItem> vendorNameFilteredArray = new ArrayList<PurchaseOrderReceiptListItem>();
//                filteredArray = new ArrayList<PurchaseOrderReceiptListItem>();
//
//                if(mFilterPurchaseOrderNo.length() > 0)
//                {
//                    FilterByPurchaseOrderNo(mFilterPurchaseOrderNo, dataArray, purchaseFilteredArray);
//                }
//                else
//                {
//                    purchaseFilteredArray = dataArray;
//                }
//
//                if(mFilterVendorName.length() > 0)
//                {
//                    FilterByVendorName(mFilterVendorName, purchaseFilteredArray, vendorNameFilteredArray);
//                }
//                else
//                {
//                    vendorNameFilteredArray = purchaseFilteredArray;
//                }
//
//                if (mFilterDate.length() > 0) {
//                    FilterByDate(mFilterDate, vendorNameFilteredArray, filteredArray);
//                }
//                else {
//                    filteredArray = vendorNameFilteredArray;
//                }
//
//                Log.d("FILTER STRING", "SIZE" + vendorNameFilteredArray.size());
//                results.count = filteredArray.size();
//                results.values = filteredArray;
//
//                return results;
//            }
//        };
//
//        return filter;
//    }
//
//    private void FilterByPurchaseOrderNo(String currfilter, ArrayList<PurchaseOrderReceiptListItem> originalDataArray, ArrayList<PurchaseOrderReceiptListItem> filteredDataArray)
//    {
//        for (int i = 0; i < originalDataArray.size(); i++) {
//            if (originalDataArray.get(i).getPurchaseOrderReceiptNo().toLowerCase().contains(currfilter)) {
//                filteredDataArray.add(originalDataArray.get(i));
//            }
//        }
//    }
//
//    private void FilterByVendorName(String currFilter, ArrayList<PurchaseOrderReceiptListItem> originalDataArray, ArrayList<PurchaseOrderReceiptListItem> filteredDataArray)
//    {
//        for (int i = 0; i < originalDataArray.size(); i++) {
//            if (originalDataArray.get(i).getVendorName().toLowerCase().contains(currFilter)) {
//                filteredDataArray.add(originalDataArray.get(i));
//            }
//        }
//    }
//
//    private void FilterByDate(String currFilter, ArrayList<PurchaseOrderReceiptListItem> originalDataArray, ArrayList<PurchaseOrderReceiptListItem> filteredDataArray)
//    {
//        for (int i = 0; i < originalDataArray.size(); i++) {
//            String[] dateSplit = originalDataArray.get(i).getReceiveDate().split("T")[0].split("-");
//            String dateString = dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0];
//
//            if (dateString.contains(currFilter)) {
//                filteredDataArray.add(originalDataArray.get(i));
//            }
//        }
//    }

    static class ViewHolderItem {
        TextView lblVendorName;
        TextView lblPurchaseRecNo;
        TextView lblPODate;
        TextView lblReceiveDate;
        TextView lblPurchaseOrdNo;

        public ViewHolderItem(View view)
        {
            lblVendorName = (TextView) view.findViewById(R.id.lblVendorName);
            lblPurchaseOrdNo = (TextView) view.findViewById(R.id.lblPurOrdNum);
            lblPurchaseRecNo = (TextView) view.findViewById(R.id.lblPurchaseRecNo);
            lblPODate = (TextView) view.findViewById(R.id.lblPODate);
            lblReceiveDate = (TextView) view.findViewById(R.id.lblReceiveDate);
        }


    }
}
