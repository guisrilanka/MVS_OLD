package com.gui.mdt.thongsieknavclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.MsoSalesOrderList;

import java.util.ArrayList;

/**
 * Created by User on 7/11/2017.
 */

public class MvsSalesInvoicelistAdapter extends ArrayAdapter<MsoSalesOrderList> implements View.OnClickListener{
    private ArrayList<MsoSalesOrderList> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtSoNo;
        TextView txtSoDate;
        TextView txtDeliverDate;
        CheckBox chkOrder;
        LinearLayout layoutSalesOrderCard;

    }

    public MvsSalesInvoicelistAdapter(ArrayList<MsoSalesOrderList> data, Context context) {
        super(context, R.layout.item_mvs_sales_invoice_card, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        MsoSalesOrderList dataModel=(MsoSalesOrderList) object;
        Toast.makeText(getContext(), "Payment Collection Clicked", Toast.LENGTH_LONG).show();
        /*switch (v.getId())
        {
            case R.id.item_info:*/
               /* Snackbar.make(v, "Release date " +dataModel.getDeliverDate(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();*/
       /*         break;
        }*/
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MsoSalesOrderList dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_mvs_sales_invoice_card, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.tvCustomerName);
            viewHolder.txtSoNo = (TextView) convertView.findViewById(R.id.tvSoValue);
            viewHolder.txtSoDate = (TextView) convertView.findViewById(R.id.tvSoDate);
            viewHolder.txtDeliverDate = (TextView) convertView.findViewById(R.id.tvDiliveryDate);
            viewHolder.chkOrder = (CheckBox) convertView.findViewById(R.id.chkOrder);
            viewHolder.layoutSalesOrderCard = (LinearLayout) convertView.findViewById(R.id.layoutSalesOrderCard);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        /*Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);*/
        lastPosition = position;

        if(position == 0 || position == 1 || position == 2 || position == 3 || position == 4 || position == 5)
        {
            viewHolder.chkOrder.setChecked(true);
            viewHolder.layoutSalesOrderCard.setBackgroundColor(getContext().getResources().getColor(R.color.listConfirmed));
        }
        else if(position == 8 || position == 9)
        {
            viewHolder.chkOrder.setChecked(false);
            viewHolder.layoutSalesOrderCard.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
        }
        else
        {
            viewHolder.chkOrder.setChecked(false);
            viewHolder.layoutSalesOrderCard.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        }

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtSoNo.setText(dataModel.getSoNo());
        viewHolder.txtSoDate.setText(dataModel.getSoDate());
        viewHolder.txtSoDate.setText(dataModel.getDeliverDate());
        /*viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);*/
        // Return the completed view to render on screen
        return convertView;
    }
}
