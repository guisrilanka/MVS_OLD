package com.gui.mdt.thongsieknavclient.adapters.productionpastegroup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.gui.mdt.thongsieknavclient.DataManager;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderListResultData;
import com.gui.mdt.thongsieknavclient.ui.ProductionPasteGroupActivity;
import com.gui.mdt.thongsieknavclient.ui.ProductionPasteGroupBOMActivity;
import com.gui.mdt.thongsieknavclient.ui.ProductionPasteGroupRoutingActivity;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 14/09/16.
 */
public class ProductionPasteGroupLineListArrayAdapter extends ArrayAdapter<PasteGroupProductionOrderListResultData> implements Filterable {
    Context context;

    String currFilter;

    ArrayList<PasteGroupProductionOrderListResultData> dataArray;
    ArrayList<PasteGroupProductionOrderListResultData> filteredArray;

    ArrayAdapter<PasteGroupProductionOrderListResultData> adapter;

    public ProductionPasteGroupLineListArrayAdapter(Context c, ArrayList<PasteGroupProductionOrderListResultData> dataSource) {
        super(c, R.layout.array_adapter_production_line, R.id.lblProductionOrderNo, dataSource);
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
            convertView = inflater.inflate(R.layout.array_adapter_production_line, parent, false);

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
        if(filteredArray.get(position) != null) {

            viewHolder.lblProductionOrderNo.setText(filteredArray.get(position).getProductionOrderNo());
            String[] dateSplit = filteredArray.get(position).getProductionDate().split("T")[0].split("-");

            if(!filteredArray.get(position).getProductionDate().contains("0001-")) {
                dateSplit = filteredArray.get(position).getProductionDate().split("T")[0].split("-");
                viewHolder.lblDueDate.setText(dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]);
            }
            else
            {
                viewHolder.lblDueDate.setText(" ");
            }

            viewHolder.lblItemDescription.setText(filteredArray.get(position).getItemDescription());

            viewHolder.lblItemIDUom.setText(filteredArray.get(position).getItemNo() + " / " + filteredArray.get(position).getBaseUom());
            viewHolder.lblPurchaseQuantity.setText("Ord. Qty: " + filteredArray.get(position).getOrderQuantity());
            viewHolder.lblFinishedQuantity.setText("Fin. Qty: " + filteredArray.get(position).getFinishedQuantity());
            viewHolder.lblRemainingQuantity.setText("Rem. Qty: " + filteredArray.get(position).getRemainingQuantity());

            if(!dataArray.get(position).getRemainingQuantity().equals("0.0"))
            {
                viewHolder.lblRemainingQuantity.setBackgroundColor(0xff99cc00);
            }

            String balance = dataArray.get(position).getFinishedQuantity();
            if(!balance.equals("0.0"))
            {
                viewHolder.lblFinishedQuantity.setBackgroundColor(0xff99cc00);
            }
            else
            {
                viewHolder.lblFinishedQuantity.setBackgroundColor(0xffffffff);
            }

            viewHolder.btnBOM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataManager.getInstance().clearProductionTransitionData();
                    DataManager.getInstance().ProductionOrderNo = filteredArray.get(position).getProductionOrderNo();
                    DataManager.getInstance().setPasteGroupProductionOrderListResultData(filteredArray.get(position));

                    Intent intent = new Intent(context, ProductionPasteGroupBOMActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("PRODUCTION_ORDER_NO", filteredArray.get(position).getProductionOrderNo());
                    ((Activity) context).startActivity(intent);
                }
            });

            if(filteredArray.get(position).isRoutingRequired())
            {
                viewHolder.btnRouting.setText("Routing");
                viewHolder.btnRouting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataManager.getInstance().clearProductionTransitionData();
                        DataManager.getInstance().ProductionOrderNo = filteredArray.get(position).getProductionOrderNo();
                        DataManager.getInstance().LastLotNo = filteredArray.get(position).getLastLotNo();
                        DataManager.getInstance().setPasteGroupProductionOrderListResultData(filteredArray.get(position));

                        String lot = filteredArray.get(position).getLastLotNo();
                        Intent intent = new Intent(context, ProductionPasteGroupRoutingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("PRODUCTION_ORDER_NO", filteredArray.get(position).getProductionOrderNo());
                        if(lot != null) {
                            intent.putExtra("LAST_LOT_NO", filteredArray.get(position).getLastLotNo());
                        }else{
                            intent.putExtra("LAST_LOT_NO", "N.A");
                        }
                        ((Activity) context).startActivity(intent);
                    }
                });
            }
            else
            {
                viewHolder.btnRouting.setText("Output");

                viewHolder.btnRouting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

                        // Setting dialogview
                        Window window = dialog.getWindow();
                        window.setGravity(Gravity.CENTER);
                        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                        window.setDimAmount(0.2f);

                        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
                        dialog.setContentView(R.layout.dialog_output_scrap_quantity_update);
                        dialog.setCancelable(false);

                        TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
                        title.setText("Prod. No: " + filteredArray.get(position).getProductionOrderNo());

                        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EditText editOutputQuantity = (EditText) dialog.findViewById(R.id.editOutputQuantity);
                                String quantity = editOutputQuantity.getText().toString();
                                EditText editScrapQuantity = (EditText) dialog.findViewById(R.id.editScrapQuantity);

                                if(quantity.length() != 0 && editScrapQuantity.getText().toString().length() != 0)
                                {
                                    Float outputQuantity = 0.0f;
                                    Float scrapQuantity = 0.0f;

                                    outputQuantity = Float.parseFloat(quantity);
                                    scrapQuantity = Float.parseFloat(editScrapQuantity.getText().toString());

                                    ((ProductionPasteGroupActivity) context).updateQuantity(filteredArray.get(position).getProductionOrderNo(), filteredArray.get(position).getLineNo(), outputQuantity, scrapQuantity);

                                    dialog.dismiss();
                                }
                                else
                                {
                                    NotificationManager.DisplayAlertDialog(context, NotificationManager.ALERT_TITLE_INVALID_QUANTITY, NotificationManager.ALERT_MSG_INVALID_QUANTITY);
                                }
                            }
                        });

                        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();


                    }
                });
            }
        }
        else {
        }

        return convertView;

    }

    @Override
    public PasteGroupProductionOrderListResultData getItem(int position) {
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

                filteredArray = (ArrayList<PasteGroupProductionOrderListResultData>) results.values;

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
                filteredArray = new ArrayList<PasteGroupProductionOrderListResultData>();

                if (constraint.length() > 0) {

                    // CHECK PRODUCTION ORDER NO
                    for (int i = 0; i < dataArray.size(); i++) {
                        if (dataArray.get(i).getProductionOrderNo().toLowerCase().contains(currFilter))  {
                            filteredArray.add(dataArray.get(i));
                        }
                    }

                    // CHECK LINE NO
                    for(int i = 0; i < dataArray.size(); i++)
                    {
                        if (dataArray.get(i).getLineNo().toLowerCase().contains(currFilter))  {
                            if(!checkItemExist(dataArray.get(i).getProductionOrderNo()))    // add if item not in list yet, add
                            {
                                filteredArray.add(dataArray.get(i));
                            }
                        }
                    }

                    // CHECK ITEM NO
                    for(int i = 0; i < dataArray.size(); i++)
                    {
                        if (dataArray.get(i).getItemNo().toLowerCase().contains(currFilter))  {
                            if(!checkItemExist(dataArray.get(i).getProductionOrderNo()))    // add if item not in list yet, add
                            {
                                filteredArray.add(dataArray.get(i));
                            }
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

    private boolean checkItemExist(String productionNo)
    {
        if(filteredArray != null)
        {
            for(int i = 0; i < filteredArray.size(); i++)
            {
                if(filteredArray.get(i).getProductionOrderNo().toLowerCase().contains(productionNo))
                {
                    return true;
                }
            }
        }

        return false;
    }

    static class ViewHolderItem {
        TextView lblProductionOrderNo;
        TextView lblDueDate;
        TextView lblItemDescription;
        TextView lblItemIDUom;
        TextView lblPurchaseQuantity;
        TextView lblFinishedQuantity;
        TextView lblRemainingQuantity;
        Button btnRouting;
        Button btnBOM;

        public ViewHolderItem(View view)
        {
            lblProductionOrderNo = (TextView) view.findViewById(R.id.lblProductionOrderNo);
            lblDueDate = (TextView) view.findViewById(R.id.lblDueDate);
            lblItemDescription = (TextView) view.findViewById(R.id.lblItemDescription);
            lblItemIDUom = (TextView) view.findViewById(R.id.lblItemIDUom);
            lblPurchaseQuantity = (TextView) view.findViewById(R.id.lblPurchaseQuantity);
            lblFinishedQuantity = (TextView) view.findViewById(R.id.lblFinishedQuantity);
            lblRemainingQuantity = (TextView) view.findViewById(R.id.lblRemainingQuantity);

            btnRouting = (Button) view.findViewById(R.id.btnRouting);
            btnBOM = (Button) view.findViewById(R.id.btnBOM);
        }


    }
}