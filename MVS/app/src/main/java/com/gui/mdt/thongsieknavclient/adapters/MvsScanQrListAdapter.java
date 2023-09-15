package com.gui.mdt.thongsieknavclient.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.interfaces.ItemOnClickListener;
import com.gui.mdt.thongsieknavclient.model.QrScanHeadModel;

import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MvsScanQrListAdapter extends RecyclerView.Adapter<MvsScanQrListAdapter.ViewHolder> {

    private List<QrScanHeadModel> qrScanHeadModels;
    Context context;
    int smallerDimension;
    public String TAG = "Scan_QR_CODE";
    public ItemOnClickListener itemOnClickListener;

    public MvsScanQrListAdapter(List<QrScanHeadModel> qrScanHeadModels, Context context) {
        this.qrScanHeadModels = qrScanHeadModels;
        this.context = context;


    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mvs_scan_qr, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QrScanHeadModel headModel = qrScanHeadModels.get(position);

        holder.txtCount.setText((position + 1) + " of " + qrScanHeadModels.size());
        if (position == 0) {
            holder.btnPrevious.setEnabled(false);
        } else {
            holder.btnPrevious.setEnabled(true);
        }

        if (position == (qrScanHeadModels.size() - 1)) {
            holder.btnNext.setEnabled(false);
        } else {
            holder.btnNext.setEnabled(true);
        }

        Gson gson = new Gson();
        String barcodeDetails = gson.toJson(headModel);

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 7 / 8;

//        if (headModel.d_iT.size() > 0) {
        holder.mStartRunningNo.setText(headModel.d_iT.get(0).a_runningNo + "");
        holder.mEndRunningNo.setText(headModel.d_iT.get((headModel.d_iT.size() - 1)).a_runningNo + "");
//        }
//        holder.mInvoiceNo.setText(headModel.b_invoiceNo);
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        QRGEncoder qrgEncoder = new QRGEncoder(barcodeDetails, null, QRGContents.Type.TEXT, smallerDimension);
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);
        try {
            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView
            holder.qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }

    }

    @Override
    public int getItemCount() {
        return qrScanHeadModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mStartRunningNo, mEndRunningNo, txtCount;
        public ImageView qrImage;
        public Button btnNext, btnPrevious;

        public ViewHolder(View itemView) {
            super(itemView);
            qrImage = (ImageView) itemView.findViewById(R.id.qrImage);
//            mInvoiceNo = (TextView) itemView.findViewById(R.id.txtInvoiceNo);
            mStartRunningNo = (TextView) itemView.findViewById(R.id.txtStartRunningNo);
            mEndRunningNo = (TextView) itemView.findViewById(R.id.txtEndRunningNo);
            btnNext = (Button) itemView.findViewById(R.id.btnNext);
            btnPrevious = (Button) itemView.findViewById(R.id.btnPrevious);
            txtCount = (TextView) itemView.findViewById(R.id.txtCount);

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemOnClickListener.itemOnClicked("next", getAdapterPosition());

                }
            });

            btnPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemOnClickListener.itemOnClicked("previous", getAdapterPosition());
                }
            });
        }
    }
}
