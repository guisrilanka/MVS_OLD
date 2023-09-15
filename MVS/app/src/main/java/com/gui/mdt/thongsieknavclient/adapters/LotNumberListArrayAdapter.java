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
import com.gui.mdt.thongsieknavclient.model.AvailableLotNoResultData;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 06/09/16.
 */
public class LotNumberListArrayAdapter extends ArrayAdapter<AvailableLotNoResultData> implements Filterable {

    Context context;

    String currFilter;

    ArrayList<AvailableLotNoResultData> dataArray;
    ArrayList<AvailableLotNoResultData> filteredArray;

    ArrayAdapter<AvailableLotNoResultData> adapter;

    public LotNumberListArrayAdapter(Context c, ArrayList<AvailableLotNoResultData> dataSource) {
        super(c, R.layout.array_adapter_lot_number_list, R.id.lblLotNo, dataSource);
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
            convertView = inflater.inflate(R.layout.array_adapter_lot_number_list, parent, false);

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
        //if(position < filteredArray.size()) {
        if(filteredArray.get(position) != null) {

            viewHolder.lblLotNo.setText(filteredArray.get(position).getLotNo());
            viewHolder.lblLotQuantity.setText(filteredArray.get(position).getAvailableQuantity());
        }
        else {
        }

        return convertView;

    }

    @Override
    public AvailableLotNoResultData getItem(int position) {
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

                filteredArray = (ArrayList<AvailableLotNoResultData>) results.values;

                if (results.count == 0)
                    notifyDataSetInvalidated();
                else {
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                currFilter = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();
                filteredArray = new ArrayList<AvailableLotNoResultData>();

                if (constraint.length() > 0) {

                    for (int i = 0; i < dataArray.size(); i++) {
                        if (dataArray.get(i).getLotNo().toLowerCase().contains(currFilter))  {
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
        TextView lblLotNo;
        TextView lblLotQuantity;

        public ViewHolderItem(View view)
        {
            lblLotNo = (TextView) view.findViewById(R.id.lblLotNo);
            lblLotQuantity = (TextView) view.findViewById(R.id.lblLotQuantity);
        }
    }
}