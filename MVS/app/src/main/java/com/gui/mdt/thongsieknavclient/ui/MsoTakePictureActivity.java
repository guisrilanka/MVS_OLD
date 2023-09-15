package com.gui.mdt.thongsieknavclient.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MsoTakePictureAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.MsoTakePictureImage;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrderImageUploadStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderImageUploadStatusDbHandler;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MsoTakePictureActivity extends AppCompatActivity {

    private static final int PERMISSIONS_ACCESS_STORAGE = 0;
    private static Context context;
    List<MsoTakePictureImage> imageList;// list of file paths
    File[] listFile;
    private MsoTakePictureAdapter msoTakePictureAdapter;

    String soNo, status;
    Toolbar myToolbar;
    private FloatingActionButton fabTakePicture;
    private RecyclerView recyclerViewImages;
    private boolean mAccessToStorageGranted;
    Bitmap mBitmapImage;

    private saveImagesTask saveImagesTask = null;
    private ProgressDialog mProgressDialog;
    private NavClientApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mso_take_picture);
        MsoTakePictureActivity.context = getApplicationContext();

        mApp = (NavClientApp) getApplicationContext();

        recyclerViewImages = (RecyclerView) findViewById(R.id.recyclerViewImages);
        Bundle extras = getIntent().getExtras();

        soNo = extras.getString("soNo");
        status = extras.getString("status");
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Take Picture");

        fabTakePicture = (FloatingActionButton) findViewById(R.id.fabTakePicture);
        fabTakePicture.bringToFront();

        fabTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MsoTakePictureActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    mAccessToStorageGranted = true;
                } else {
                    ActivityCompat.requestPermissions(MsoTakePictureActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_ACCESS_STORAGE);
                }

                if (mAccessToStorageGranted == true) {
                    CropImage.activity().start(MsoTakePictureActivity.this);
                }
            }
        });

        Drawable backArrow = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE)
                .sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        recyclerViewImages.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerViewImages.setLayoutManager(mLayoutManager);
        recyclerViewImages.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(4), true));
        recyclerViewImages.setItemAnimator(new DefaultItemAnimator());

        msoTakePictureAdapter = new MsoTakePictureAdapter(new ArrayList<MsoTakePictureImage>(), R.layout.item_take_picture_image_gallery_item, MsoTakePictureActivity.this);
        recyclerViewImages.setAdapter(msoTakePictureAdapter);

        AddImagesToGrid();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == MsoTakePictureActivity.RESULT_OK) {
                try {
                    mBitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());

                    if (mBitmapImage != null) {
                        saveImagesTask = new saveImagesTask();
                        saveImagesTask.execute((Void) null);

                        //SaveImage(mBitmapImage);
                        //AddImagesToGrid();
                    }

                } catch (Exception e) {

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void AddImagesToGrid() {
        GetImagesFromSdcard();
        msoTakePictureAdapter = new MsoTakePictureAdapter(imageList, R.layout.item_take_picture_image_gallery_item, MsoTakePictureActivity.this);
        recyclerViewImages.setAdapter(msoTakePictureAdapter);
        msoTakePictureAdapter.notifyDataSetChanged();
    }

    public void GetImagesFromSdcard() {
        String directoryName = GetDirectoryName();
        imageList = new ArrayList<MsoTakePictureImage>();
        File file = new File(Environment.getExternalStorageDirectory() + "/CapturedImages/" +
                soNo + "_" + directoryName);
        if (file.isDirectory()) {
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                MsoTakePictureImage imgeItem = new MsoTakePictureImage();
                imgeItem.setItemImageUrl(listFile[i].getAbsolutePath());
                imageList.add(imgeItem);
            }
        }
    }

    private void SaveImage(Bitmap image) {

        String directoryName = GetDirectoryName();
        File directory = new File(Environment.getExternalStorageDirectory() + "/CapturedImages/" +
                soNo + "_" + directoryName);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = GetFileName();

        FileOutputStream out = null;
        String fname = soNo + "_" + fileName + ".png";
        File file = new File(directory, fname);

        if (file.exists()) {
            file.delete();
        }

        try {
            if (isExternalStorageWritable()) {

                out = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();

                updateImageDetailsToDb(soNo, file.getName(), file.getPath(), image);
            } else {
                Toast toast = Toast.makeText(context, "enter sd card", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private String GetFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = sdf.format(new Date());
        return timeStamp;
    }

    private String GetDirectoryName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String timeStamp = sdf.format(new Date());
        return timeStamp;
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void updateImageDetailsToDb(String soNo_, String imageName, String imageUrl, Bitmap image)
    {
        try
        {
            SalesOrderImageUploadStatus salesOrderImageUploadStatus = new SalesOrderImageUploadStatus();
            salesOrderImageUploadStatus.setSoNo(soNo_);
            salesOrderImageUploadStatus.setImageName(imageName);
            salesOrderImageUploadStatus.setImageUrl(imageUrl);
            salesOrderImageUploadStatus.setTransferred(false);

            SalesOrderImageUploadStatusDbHandler db = new SalesOrderImageUploadStatusDbHandler(getApplicationContext());
            db.open();
            db.addItems(salesOrderImageUploadStatus);
            Log.d("SOImageStatus Added :", soNo_);
            db.close();
        }
        catch (Exception ee)
        {
            Log.d("SOImageStatus","Exception :" +ee.getMessage());
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private class saveImagesTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SaveImage(mBitmapImage);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MsoTakePictureActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    AddImagesToGrid();
                    mProgressDialog.dismiss();
                } catch (Exception ex) {
                }
                mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }
    }
}
