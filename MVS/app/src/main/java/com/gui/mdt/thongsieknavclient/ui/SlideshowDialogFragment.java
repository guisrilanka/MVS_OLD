package com.gui.mdt.thongsieknavclient.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gui.mdt.thongsieknavclient.R;

import java.io.File;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private String path;
    ImageView close;

    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        ImageView imageViewPreview = (ImageView) v.findViewById(R.id.image_preview);
        path = getArguments().getString("image");
        imageViewPreview.setImageBitmap(loadImageFromStorage(path));
        close = (ImageView) v.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    private Bitmap loadImageFromStorage(String itemCode) {
        Bitmap itemImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable
                .item_no_image);
        File imageFile = new File("/data/data/com.gui.mvs.thongsieknavclient/app_imageDir/" + itemCode + ".png");

        if (imageFile.canRead()) {
            itemImage = BitmapFactory.decodeFile(imageFile.getPath());
        }
        return itemImage;
    }


}