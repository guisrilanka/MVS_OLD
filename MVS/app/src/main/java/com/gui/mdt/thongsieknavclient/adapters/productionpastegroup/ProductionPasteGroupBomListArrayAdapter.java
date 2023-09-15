package com.gui.mdt.thongsieknavclient.adapters.productionpastegroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderBomListResultData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqim_000 on 15/09/16.
 */
public class ProductionPasteGroupBomListArrayAdapter extends ArrayAdapter<ProductionOrderBomListResultData> implements Filterable {
    Context context;

    String filterProductionNo;
    String filterLineNo;
    boolean isClearFilter = false;

    ArrayList<ProductionOrderBomListResultData> dataArray;
    ArrayList<ProductionOrderBomListResultData> filteredArray;

    ArrayAdapter<ProductionOrderBomListResultData> adapter;

    public ProductionPasteGroupBomListArrayAdapter(Context c, ArrayList<ProductionOrderBomListResultData> dataSource) {
        super(c, R.layout.array_adapter_production_bom_shared, R.id.lblItemDescription, dataSource);
        this.context = c;
        this.dataArray = dataSource;

        this.filteredArray = dataSource;

        this.adapter = this;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_production_bom_shared, parent, false);

            viewHolder = new ViewHolderItem(convertView);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }


        // assign values if the object is not null
        if (filteredArray.get(position) != null) {

            viewHolder.lblItemDescription.setText(filteredArray.get(position).getItemDescription());

            viewHolder.lblItemIDUom.setText(filteredArray.get(position).getItemNo() + " / " + filteredArray.get(position).getBaseUom());
            viewHolder.lblExpectedQuantity.setText("Exp. Qty: " + filteredArray.get(position).getExpectedQuantity());
            viewHolder.lblRemainingQuantity.setText("Rem. Qty: " + filteredArray.get(position).getRemainingQuantity());
            viewHolder.lblConsumeQuantity.setText("Qty to Con: " + filteredArray.get(position).getQuantityToConsumed());
            viewHolder.lblQtyPer.setText("Qty.Per: "+ filteredArray.get(position).getQuantityPer());

            String balance = dataArray.get(position).getQuantityToConsumed();
            if(!balance.equals("0.0"))
            {
                viewHolder.lblConsumeQuantity.setBackgroundColor(0xff99cc00);
            }
            else
            {
                viewHolder.lblConsumeQuantity.setBackgroundColor(0xffffffff);
            }

            // SET "VERIFIED" TO VISIBLE IF THERE IS AN ENTRY INSIDE THE VERIFIED LIST
            if(filteredArray.get(position).isBarCodeScanned())
            {
                viewHolder.lblVerified.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.lblVerified.setVisibility(View.INVISIBLE);
            }

        } else {
        }

        return convertView;

    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filteredArray = (ArrayList<ProductionOrderBomListResultData>) results.values;

                if (results.count == 0)
                    notifyDataSetInvalidated();
                else {
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                filteredArray = new ArrayList<ProductionOrderBomListResultData>();

                if(isClearFilter)
                {
                    isClearFilter = false;

                    filteredArray = dataArray;
                }
                else
                {
                    // CHECK PRODUCTION ORDER NO
                    if(!filterProductionNo.isEmpty())
                    {
                        for (int i = 0; i < dataArray.size(); i++) {
                            if (dataArray.get(i).getProductionOrderNo().toLowerCase().contains(filterProductionNo))  {
                                filteredArray.add(dataArray.get(i));
                            }
                        }
                    }


                    // CHECK LINE NO
                    for(int i = 0; i < dataArray.size(); i++)
                    {
                        if (dataArray.get(i).getLineNo().toLowerCase().contains(filterLineNo))  {
                            if(!checkItemExist(dataArray.get(i).getLineNo()))    // add if item not in list yet, add
                            {
                                filteredArray.add(dataArray.get(i));
                            }
                        }
                    }

                    if(filteredArray.size() == 0)
                    {
                        NotificationManager.DisplayAlertDialog(context, NotificationManager.ALERT_TITLE_INVALID_TO, context.getResources().getString(R.string.notification_msg_no_item_found));
                        filteredArray = dataArray;
                    }

                }

                results.count = filteredArray.size();
                results.values = filteredArray;

                return results;
            }
        };

        return filter;
    }

    private boolean checkItemExist(String lineNo)
    {
        if(filteredArray != null)
        {
            for(int i = 0; i < filteredArray.size(); i++)
            {
                if(filteredArray.get(i).getLineNo().toLowerCase().contains(lineNo))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void setFilterParam(String prod_no, String line_no)
    {
        filterProductionNo = prod_no;
        filterLineNo = line_no;
        isClearFilter = false;
        this.getFilter().filter("");
    }

    public void clearFilter()
    {
        filterProductionNo = "";
        filterLineNo = "";
        isClearFilter = true;
        this.getFilter().filter("");
    }

    @Override
    public ProductionOrderBomListResultData getItem(int position) {
        return filteredArray.get(position);
    }

    @Override
    public int getCount() {
        return filteredArray.size();
    }

    static class ViewHolderItem {
        TextView lblItemDescription;
        TextView lblItemIDUom;
        TextView lblExpectedQuantity;
        TextView lblRemainingQuantity;
        TextView lblConsumeQuantity;
        TextView lblVerified;
        TextView lblQtyPer;

        public ViewHolderItem(View view) {
            lblItemDescription = (TextView) view.findViewById(R.id.lblItemDescription);
            lblItemIDUom = (TextView) view.findViewById(R.id.lblItemIDUom);
            lblExpectedQuantity = (TextView) view.findViewById(R.id.lblExpectedQuantity);
            lblRemainingQuantity = (TextView) view.findViewById(R.id.lblRemainingQuantity);
            lblConsumeQuantity = (TextView) view.findViewById(R.id.lblConsumeQuantity);
            lblVerified = (TextView) view.findViewById(R.id.lblVerified);
            lblQtyPer = (TextView) view.findViewById(R.id.lblQtyPer);

        }
    }
}
