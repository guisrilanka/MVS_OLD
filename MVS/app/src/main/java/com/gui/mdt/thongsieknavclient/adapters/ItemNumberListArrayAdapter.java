package com.gui.mdt.thongsieknavclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemListResultData;

import java.util.ArrayList;

/**
 * Created by user on 12/8/2016.
 */

public class ItemNumberListArrayAdapter extends ArrayAdapter<ItemListResultData> implements Filterable {

    Context context;
    String currFilter;
    ArrayList<ItemListResultData> dataArray;
    ArrayList<ItemListResultData> filteredArray;
    ArrayAdapter<ItemListResultData> adapter;

    public ItemNumberListArrayAdapter(Context c, ArrayList<ItemListResultData> dataSource) {
        super(c, R.layout.array_adapter_item_code_list, R.id.lblItemCode, dataSource);
        this.context = c;
        this.dataArray = dataSource;
        this.filteredArray = dataSource;
        this.adapter = this;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.array_adapter_item_code_list, parent, false);

            viewHolder = new ViewHolderItem(convertView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        if(filteredArray.get(position) != null) {

            viewHolder.lblCode.setText(filteredArray.get(position).getItemCode());
            viewHolder.lblItemDesc.setText(filteredArray.get(position).getDescription());
        }
        else {
        }

        return convertView;

    }

    @Override
    public ItemListResultData getItem(int position) {
        return filteredArray.get(position);
    }

    @Override
    public int getCount() {
        return filteredArray.size();
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filteredArray = (ArrayList<ItemListResultData>) results.values;

                if (results.count == 0)
                    notifyDataSetInvalidated();
                else {
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                currFilter = constraint.toString().toUpperCase();

                FilterResults results = new FilterResults();
                filteredArray = new ArrayList<ItemListResultData>();

                if (constraint.length() > 0) {

                    for (int i = 0; i < dataArray.size(); i++) {
                        if (dataArray.get(i).getItemCode().toUpperCase().contains(currFilter))  {
                            filteredArray.add(dataArray.get(i));
                        }else if(dataArray.get(i).getDescription().toLowerCase().contains(currFilter))
                        {
                            filteredArray.add(dataArray.get(i));
                        }else if(dataArray.get(i).getItemCategoryCode().toLowerCase().contains(currFilter)){
                            filteredArray.add(dataArray.get(i));
                        }
                    }
                }
                else {
                    filteredArray = dataArray;
                }

                results.count = filteredArray.size();
                results.values = filteredArray;

                return results;
            }
        };

        return filter;
    }

    static class ViewHolderItem {
        TextView lblCode;
        TextView lblItemDesc;

        public ViewHolderItem(View view)
        {
            lblCode = (TextView) view.findViewById(R.id.lblItemCode);
            lblItemDesc = (TextView) view.findViewById(R.id.lblItemDescription);
        }
    }
}