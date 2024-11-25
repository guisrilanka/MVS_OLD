package com.gui.mdt.thongsieknavclient.adapters;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ExchangeItem;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.ui.MvsExchangeOrderItemActivity;
import com.gui.mdt.thongsieknavclient.ui.SalesItemDetailActivity;

import java.io.File;
import java.util.List;


public class ExchangeItemSearchAdapter extends RecyclerView.Adapter<ExchangeItemSearchAdapter.ItemViewHolder> {

    private Activity activity;
    private List<ExchangeItem> itemList;
    private int rowLayout;
    private Bundle extras;
    private String formName = "", details="", deliveryDate="";
    private NavClientApp mApp;

    @Override
    public ExchangeItemSearchAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        final ExchangeItemSearchAdapter.ItemViewHolder viewHolder = new ExchangeItemSearchAdapter.ItemViewHolder(view, activity.getApplicationContext());

        if (extras != null) {
            if (extras.containsKey(activity.getResources().getString(R.string.intent_extra_form_name))) {
                formName = extras.getString(activity.getResources().getString(R.string.intent_extra_form_name));
            }
            if(extras.containsKey(activity.getResources().getString(R.string.intent_extra_details)))
            {
                details = extras.getString(activity.getResources().getString(R.string.intent_extra_details));
            }
            if(extras.containsKey("deliveryDate"))
            {
                deliveryDate = extras.getString("deliveryDate");
            }

        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = viewHolder.getAdapterPosition();
                ExchangeItem itemObj = itemList.get(position);
                String objAsJson = itemObj.toJson();

//                if ( formName.equals(activity.getResources().getString(R.string.form_name_mso_sales_order))
//                        && details.equals(activity.getResources().getString(R.string.intent_extra_add_new_item)) ) //from MsoSaleOrderActivity  -> Add New Item
//                {
//                    Intent intent = new Intent();
//                    intent.putExtra(activity.getResources().getString(R.string.item_json_obj),objAsJson);
//                    intent.putExtra("deliveryDate", deliveryDate);
//                    activity.setResult(RESULT_OK, intent);
//                    activity.finish();
//                }
//                else if (formName.equals("MsoHome")) //from MsoHome -> Stock Info -> SalesItemDetailActivity
//                {
//                    Intent intent = new Intent(activity, SalesItemDetailActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("formName", "MsoSalesItemSearch");
//                    intent.putExtra("_itemObject", objAsJson);
//                    activity.startActivity(intent);
//                }
//                else if(formName.equals(activity.getResources().getString(R.string.form_name_mvs_stock_transfer_in))
//                        && details.equals(activity.getResources().getString(R.string.intent_extra_add_new_item)) ) //from MvsTransferInActivity  -> Add New Item
//                {
//                    Intent intent = new Intent();
//                    intent.putExtra(activity.getResources().getString(R.string.item_json_obj),objAsJson);
//                    activity.setResult(RESULT_OK, intent);
//                    activity.finish();
//                }
//                else if(formName.equals(activity.getResources().getString(R.string.form_name_mvs_stock_transfer_in)))
//                {
//                    Intent intent = new Intent(activity, SalesItemDetailActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("formName", "MvsSalesItemSearch");
//                    intent.putExtra("_itemObject", objAsJson);
//                    activity.startActivity(intent);
//                }
//                else if(formName.equals(activity.getResources().getString(R.string.form_name_mvs_stock_Request))
//                        && details.equals(activity.getResources().getString(R.string.intent_extra_add_new_item)))
//                {
//                    Intent intent = new Intent();
//                    intent.putExtra(activity.getResources().getString(R.string.item_json_obj),objAsJson);
//                    intent.putExtra("deliveryDate", deliveryDate);
//                    activity.setResult(RESULT_OK, intent);
//                    activity.finish();
//                }
//                else if(formName.equals(activity.getResources().getString(R.string.form_name_mvs_sales_order))
//                        && details.equals(activity.getResources().getString(R.string.intent_extra_add_new_item)))
//                {
                    Intent intent = new Intent(activity, MvsExchangeOrderItemActivity.class);
                    intent.putExtra(activity.getResources().getString(R.string.item_json_obj),objAsJson);

//                    intent.putExtra("deliveryDate", deliveryDate);
//                    activity.setResult(RESULT_OK, intent);
//                    activity.finish();
                    activity.startActivity(intent);
//                }
//                else {
//                    Intent intent = new Intent(activity, SalesItemDetailActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("_itemObject", objAsJson);
//                    activity.startActivity(intent);
//                }
            }
        });
        return viewHolder;
    }

    private Bitmap getItemImage(String itemCode) {

        Bitmap itemImage = BitmapFactory.decodeResource(activity.getResources(),R.drawable.item_no_image);
        File imageFile = new File("/data/data/com.gui.mdt.thongsieknavclient/app_imageDir/"+itemCode+".png");

        if (imageFile.canRead()) {
            itemImage = BitmapFactory.decodeFile(imageFile.getPath());
        }
        return itemImage;
    }

    private void setItemImage(ExchangeItemSearchAdapter.ItemViewHolder holder, String url) {
        Glide.with(activity.getApplicationContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgitem);
    }

    public ExchangeItemSearchAdapter(List<ExchangeItem> itemList, Bundle extras_, int rowLayout_, Activity activity_) {
        this.activity = activity_;
        this.itemList = itemList;
        this.rowLayout = rowLayout_;
        this.extras = extras_;
        this.mApp = (NavClientApp) activity_.getApplicationContext();
    }

    @Override
    public void onBindViewHolder(ExchangeItemSearchAdapter.ItemViewHolder holder, int position) {

        ExchangeItem item = itemList.get(position);

            holder.item_code.setText(item.getItemCode());
            holder.itemDescription.setText(item.getDescription());
            setItemImage(holder,itemList.get(position).getItemImageUrl());
//            String vehicleQty = "Vehicle Qty: " + "<b>" + Math.round(item.getQty()) + "</b> ";
//            holder.mTvVehicleQTY.setText(Html.fromHtml(vehicleQty));
            String exchQty = "Exchange Qty: " + "<b>" + Math.round(item.getBalanceQty() )+ "</b> ";
            holder.mTvExchQTY.setText(Html.fromHtml(exchQty));
//            holder.mTxtItemBarcode.setText(itemList.get(position).getIdentifierCode() == null ?
//                    "" : itemList.get(position).getIdentifierCode());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView item_code, itemDescription;
        public ImageView imgitem;
        private NavClientApp mApp;
        public TextView mTvExchQTY;

        public ItemViewHolder(View v, Context context) {
            super(v);
            mApp = (NavClientApp) context;

//            if(mApp.getmCurrentModule().equals(context.getResources().getString(R.string.module_mso)))
//            {
//                item_code = (TextView) v.findViewById(R.id.item_code);
//                itemDescription = (TextView) v.findViewById(R.id.itemDescription);
//                imgitem = (ImageView) v.findViewById(R.id.imgitem1);
//            }
//            else
//            {
                item_code = (TextView) v.findViewById(R.id.item_code);
                itemDescription = (TextView) v.findViewById(R.id.itemDescription);
                imgitem = (ImageView) v.findViewById(R.id.imgitem1);

//                mTvVehicleQTY=(TextView) v.findViewById(R.id.txtVehicleQTY);
                mTvExchQTY=(TextView) v.findViewById(R.id.txtExchQTY);
//                mTxtItemBarcode = (TextView) v.findViewById(R.id.txtItemBarcode);
//            }

        }
    }
}