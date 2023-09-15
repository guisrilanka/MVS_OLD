package com.gui.mdt.thongsieknavclient.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.MsoTakePictureImage;
import com.gui.mdt.thongsieknavclient.ui.MsoTakePictureActivity;

import java.util.List;

/**
 * Created by GUI-NB03 on 2017-09-08.
 */

public class MsoTakePictureAdapter extends RecyclerView.Adapter<MsoTakePictureAdapter.ItemViewHolder>{

    private Activity activity;
    private List<MsoTakePictureImage> imageList;
    private int rowLayout;

    @Override
    public MsoTakePictureAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final MsoTakePictureAdapter.ItemViewHolder viewHolder = new MsoTakePictureAdapter.ItemViewHolder(view);

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                MsoTakePictureImage itemObj = imageList.get(position);
                String objAsJson = itemObj.toJson();

                Dialog builder = new Dialog(activity, SlidingPaneLayout.LayoutParams.MATCH_PARENT);
                builder.setCanceledOnTouchOutside(true);
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);

                ImageView imv = (ImageView) v.findViewById(R.id.thumbImage);

                Drawable ee = imv.getDrawable();

                int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.70);
                int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.70);

                builder.getWindow().setLayout(width, height);

                builder.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                    }
                });

                ImageView imageView = new ImageView(activity);
                imageView.setImageDrawable(ee);

                builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

                builder.show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MsoTakePictureAdapter.ItemViewHolder holder, int position) {

        setItemImage(holder,imageList.get(position).getItemImageUrl());

    }

    private void setItemImage(ItemViewHolder holder, String url) {
        Glide.with(activity.getApplicationContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgitem);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public MsoTakePictureAdapter(List<MsoTakePictureImage> _imageList, int _rowLayout, MsoTakePictureActivity _activity) {
        this.activity = _activity;
        this.imageList = _imageList;
        this.rowLayout = _rowLayout;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgitem;
        public ItemViewHolder(View v) {
            super(v);
            imgitem = (ImageView) v.findViewById(R.id.thumbImage);
        }
    }
}
