package com.gui.mdt.thongsieknavclient.adapters.productionfinishedgoods;

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

import com.gui.mdt.thongsieknavclient.DataConverter;
import com.gui.mdt.thongsieknavclient.NotificationManager;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderRoutingListResultData;
import com.gui.mdt.thongsieknavclient.ui.ProductionFinishedGoodsRoutingActivity;
import com.gui.mdt.thongsieknavclient.ui.ProductionFinishedGoodsRoutingLotEntryActivity;

import java.util.ArrayList;

/**
 * Created by yeqim_000 on 29/09/16.
 */
public class ProductionFinishedGoodsRoutingListArrayAdapter extends ArrayAdapter<PasteGroupProductionOrderRoutingListResultData> implements Filterable {
    Context context;

    String filterRoutingNo;
    String filterOperationNo;
    boolean isClearFilter = false;

    ArrayList<PasteGroupProductionOrderRoutingListResultData> dataArray;
    ArrayList<PasteGroupProductionOrderRoutingListResultData> filteredArray;

    ArrayAdapter<PasteGroupProductionOrderRoutingListResultData> adapter;

    public ProductionFinishedGoodsRoutingListArrayAdapter(Context c, ArrayList<PasteGroupProductionOrderRoutingListResultData> dataSource) {
        super(c, R.layout.array_adapter_production_routing_shared, R.id.lblItemDescription, dataSource);
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
            convertView = inflater.inflate(R.layout.array_adapter_production_routing_shared, parent, false);

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

            String type = "";

            if (filteredArray.get(position).getProductionOrderType().toLowerCase().contains("machine")) {
                type = "MC: ";
            } else {
                type = "WC: ";
            }

            viewHolder.lblItemDescription.setText(type + filteredArray.get(position).getDescription() + "(" + filteredArray.get(position).getOperationNo() + ")");

            String startTime = "";
            String endTime = "";

            if (filteredArray.get(position).getStartTime() != null && !filteredArray.get(position).getStartTime().contains("0001-")) {
                startTime = DataConverter.ConvertDateToDayMonthYear(filteredArray.get(position).getStartTime()) + " " + DataConverter.GetNormalTimePortion(filteredArray.get(position).getStartTime());
                viewHolder.lblStartTime.setText("Start Time: " + DataConverter.GetNormalTimePortion(filteredArray.get(position).getStartTime()));

                if (filteredArray.get(position).getEndTime() != null && !filteredArray.get(position).getEndTime().contains("0001-")) {
                    endTime = DataConverter.ConvertDateToDayMonthYear(filteredArray.get(position).getEndTime()) + " " + DataConverter.GetNormalTimePortion(filteredArray.get(position).getEndTime());
                    viewHolder.lblEndTime.setText("End Time: " + DataConverter.GetNormalTimePortion(filteredArray.get(position).getEndTime()));
                } else {
                    viewHolder.lblEndTime.setText("End Time: ");
                }
                viewHolder.lblPurchaseQuantity.setText("Ord. Qty: " + dataArray.get(position).getOrderQuantityBase());
                viewHolder.lblFinishedQuantity.setText("Fin. Qty: " + dataArray.get(position).getFinishedQuantityBase());
                viewHolder.lblRemainingQuantity.setText("Rem. Qty: " + dataArray.get(position).getRemainingQuantityBase());
            } else {
                viewHolder.lblStartTime.setText("Start Time: ");
                viewHolder.lblEndTime.setText("End Time: ");
                viewHolder.lblPurchaseQuantity.setText("Ord. Qty: " + dataArray.get(position).getOrderQuantityBase());
                viewHolder.lblFinishedQuantity.setText("Fin. Qty: " + dataArray.get(position).getFinishedQuantityBase());
                viewHolder.lblRemainingQuantity.setText("Rem. Qty: " + dataArray.get(position).getRemainingQuantityBase());
            }

            float balance = dataArray.get(position).getRemainingQuantityBase();
            if(balance!=0)
            {
                viewHolder.lblRemainingQuantity.setBackgroundColor(0xff99cc00);
            }
            else
            {
                viewHolder.lblRemainingQuantity.setBackgroundColor(0xffffffff);
            }

            if (startTime.length() > 0) {
                viewHolder.lblTime.setText(startTime + " - " + endTime);
            } else {
                viewHolder.lblTime.setText("");
            }

            viewHolder.btnStart.setVisibility(View.GONE);
            viewHolder.btnEnd.setVisibility(View.GONE);
            viewHolder.btnClose.setVisibility(View.GONE);

            viewHolder.btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.btnStart.setVisibility(View.GONE);
                    viewHolder.btnClose.setVisibility(View.GONE);
                    ((ProductionFinishedGoodsRoutingActivity) context).startRoute(filteredArray.get(position).getOperationNo());
                }
            });

            viewHolder.btnEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.btnEnd.setVisibility(View.GONE);
                    viewHolder.btnClose.setVisibility(View.GONE);
                    ((ProductionFinishedGoodsRoutingActivity) context).endRoute(filteredArray.get(position).getOperationNo(), 0.0f, 0.0f);
                }
            });

            viewHolder.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.btnStart.setVisibility(View.GONE);
                    viewHolder.btnEnd.setVisibility(View.GONE);
                    viewHolder.btnClose.setVisibility(View.GONE);
                }
            });

            viewHolder.btnRuntime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.btnStart.setVisibility(View.GONE);
                    viewHolder.btnEnd.setVisibility(View.GONE);
                    viewHolder.btnClose.setVisibility(View.GONE);

                    if (filteredArray.get(position).getStartTime() == null || filteredArray.get(position).getStartTime().contains("0001-")) {
                        viewHolder.btnStart.setVisibility(View.VISIBLE);
                        viewHolder.btnClose.setVisibility(View.VISIBLE);
                    } else if (filteredArray.get(position).getStartTime() != null && !filteredArray.get(position).getStartTime().contains("0001-")) {
                        viewHolder.btnEnd.setVisibility(View.VISIBLE);
                        viewHolder.btnClose.setVisibility(View.VISIBLE);
                    }
                }
            });

            viewHolder.btnOutput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (filteredArray.get(position).isLastRoute()) {
                        if (filteredArray.get(position).isItemTrackingRequired()) {
                            String lot = filteredArray.get(position).getLastLotNo();
                            Intent intent = new Intent(context, ProductionFinishedGoodsRoutingLotEntryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("OPERATION_NO", filteredArray.get(position).getOperationNo());
                            intent.putExtra("ITEM_DESCRIPTION", filteredArray.get(position).getDescription());
                            intent.putExtra("IS_SCANNING", "NO");
                            if(lot != null) {
                                intent.putExtra("LAST_LOT_NO", filteredArray.get(position).getLastLotNo());
                            }else{
                                intent.putExtra("LAST_LOT_NO", "N.A");
                            }
                            ((Activity) context).startActivity(intent);
                        }
                    } else {
                        viewHolder.btnStart.setVisibility(View.GONE);
                        viewHolder.btnEnd.setVisibility(View.GONE);
                        viewHolder.btnClose.setVisibility(View.GONE);

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
                        title.setText("Please Enter Quantity");

                        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EditText editOutputQuantity = (EditText) dialog.findViewById(R.id.editOutputQuantity);
                                String quantity = editOutputQuantity.getText().toString();
                                EditText editScrapQuantity = (EditText) dialog.findViewById(R.id.editScrapQuantity);

                                if (quantity.length() != 0 && editScrapQuantity.getText().toString().length() != 0) {
                                    Float outputQuantity = 0.0f;
                                    Float scrapQuantity = 0.0f;

                                    outputQuantity = Float.parseFloat(quantity);
                                    scrapQuantity = Float.parseFloat(editScrapQuantity.getText().toString());

                                    ((ProductionFinishedGoodsRoutingActivity) context).updateQuantity(filteredArray.get(position).getOperationNo(), outputQuantity, scrapQuantity);
                                    dialog.dismiss();
                                } else {
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

                }
            });
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

                filteredArray = (ArrayList<PasteGroupProductionOrderRoutingListResultData>) results.values;

                if (results.count == 0)
                    notifyDataSetInvalidated();
                else {
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                filteredArray = new ArrayList<PasteGroupProductionOrderRoutingListResultData>();

                if (isClearFilter) {
                    isClearFilter = false;

                    filteredArray = dataArray;
                } else {
                    // CHECK FOR ITEM WITH THE CORRECT ROUTING NO & OPERATION NO
                    for (int i = 0; i < dataArray.size(); i++) {
                        if (dataArray.get(i).getRoutingNo().toLowerCase().equals(filterRoutingNo) && dataArray.get(i).getOperationNo().toLowerCase().equals(filterOperationNo)) {
                            filteredArray.add(dataArray.get(i));
                        }
                    }
                    if (filteredArray.size() == 0) {
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

    private boolean checkItemExist(String operationNo) {
        if (filteredArray != null) {
            for (int i = 0; i < filteredArray.size(); i++) {
                if (filteredArray.get(i).getOperationNo().toLowerCase().contains(operationNo)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setFilterParam(String routing_no, String operation_no) {
        filterRoutingNo = routing_no;
        filterOperationNo = operation_no;
        this.getFilter().filter("");
    }

    public void clearFilter() {
        filterRoutingNo = "";
        filterOperationNo = "";
        isClearFilter = true;
        this.getFilter().filter("");
    }

    @Override
    public PasteGroupProductionOrderRoutingListResultData getItem(int position) {
        return filteredArray.get(position);
    }

    @Override
    public int getCount() {
        return filteredArray.size();
    }

    static class ViewHolderItem {
        TextView lblItemDescription;
        TextView lblTime;
        TextView lblStartTime;
        TextView lblEndTime;
        TextView lblPurchaseQuantity;
        TextView lblFinishedQuantity;
        TextView lblRemainingQuantity;

        Button btnStart;
        Button btnEnd;
        Button btnClose;
        Button btnRuntime;
        Button btnOutput;

        public ViewHolderItem(View view) {
            lblItemDescription = (TextView) view.findViewById(R.id.lblItemDescription);
            lblTime = (TextView) view.findViewById(R.id.lblTime);
            lblStartTime = (TextView) view.findViewById(R.id.lblStartTime);
            lblEndTime = (TextView) view.findViewById(R.id.lblEndTime);
            lblPurchaseQuantity = (TextView) view.findViewById(R.id.lblPurchaseQuantity);
            lblFinishedQuantity = (TextView) view.findViewById(R.id.lblFinishedQuantity);
            lblRemainingQuantity = (TextView) view.findViewById(R.id.lblRemainingQuantity);

            btnStart = (Button) view.findViewById(R.id.btnStart);
            btnEnd = (Button) view.findViewById(R.id.btnEnd);
            btnClose = (Button) view.findViewById(R.id.btnClose);

            btnRuntime = (Button) view.findViewById(R.id.btnRuntime);
            btnOutput = (Button) view.findViewById(R.id.btnOutput);
        }


    }
}