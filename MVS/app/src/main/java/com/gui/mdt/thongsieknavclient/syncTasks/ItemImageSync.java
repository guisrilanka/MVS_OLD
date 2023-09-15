package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemImageParameter;
import com.gui.mdt.thongsieknavclient.datamodel.Item;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by User on 7/28/2017.
 */


public class ItemImageSync extends AsyncTask<Void, Void, Boolean> {

    public AsyncResponse delegate = null;
    Context context;
    SyncConfiguration syncConfig;
    List<Item> itemList;
    ApiItemImageParameter apiItemImageParameter;
    ResponseBody itemImageResponseBody;
    Bitmap itemImageBitmap;
    int terms = 0;
    Item tempItem;
    private NavClientApp mApp;
    private boolean isForcedSync = false;
    //private ItemImageSync.GetItemImageTask xGetItemImageTask = null;

    public ItemImageSync(Context context) {

        apiItemImageParameter = new ApiItemImageParameter();
        mApp = (NavClientApp) context;
        this.context = context;

        //set parameters for request
        apiItemImageParameter.setUserName(mApp.getCurrentUserName());
        apiItemImageParameter.setPassword(mApp.getCurrentUserPassword());
        apiItemImageParameter.setUserCompany(mApp.getmUserCompany());
        Gson gson = new Gson();
        String json = gson.toJson(apiItemImageParameter);
        Log.d("SYNC_ITEM_IMG_PARAMS ", json);

        getItemList();

/*
        if (!itemList.isEmpty()) {
            if (itemList.size() > 0) {
                getImages();
            }
        } else {
            Log.d("Empty", "Item List is Empty");
        }*/
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.d("onpre","4774");
    }

    @Override
    protected Boolean doInBackground(Void... params) {


        try {
            while (terms < itemList.size()) {
                tempItem = itemList.get(terms);
                //setting item code
                apiItemImageParameter.setItemCode(tempItem.getItemCode().toString());
                Gson gson = new Gson();
                String json = gson.toJson(apiItemImageParameter);
                Log.d("SYNC_ITEM_IMG_TERM ", "" + terms);

                itemImageResponseBody=null;
                if(isNetworkAvailable()) {
                    Call<ResponseBody> call = mApp.getNavBrokerService().GetItemPicture(apiItemImageParameter);
                    itemImageResponseBody = call.execute().body();
                }

                if (itemImageResponseBody != null) {
                    itemImageBitmap = BitmapFactory.decodeStream(itemImageResponseBody.byteStream());

                    //download image
                    if (!isImageAvailableInStorage(tempItem.getItemCode().toString())) {
                        downLoadImages(itemImageBitmap, tempItem.getItemCode().toString());
                        Log.d("SYNC_ITEM_IMG : ", tempItem.getItemCode().toString());
                    }
                }

                terms++;
            }

        } catch (Exception ex) {
            Log.d("SYNC_ITEM_IMG_TERM ", "Error" + ex);
        }

        /*try {


        } catch (Exception e) {
            Log.d("NAV_Client_Exception", e.toString());
            return false;
        }*/

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        SyncStatus syncStatus = new SyncStatus();
        syncStatus.setScope(context.getResources().getString(R.string.SyncScopeDownLoadItemImage));

        if (success) {

            if (terms < itemList.size()) {

            } else {
                delegate.onAsyncTaskFinished(syncStatus);
            }
        }
    }

    @Override
    protected void onCancelled() {
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager _connectivityManager
                = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = _connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getItemList() {
        ItemDbHandler dbAdapter = new ItemDbHandler(context);
        dbAdapter.open();
        itemList = dbAdapter.getAllItems();
        dbAdapter.close();
    }

    private boolean isImageAvailableInStorage(String itemCode) {
        boolean status = false;
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        try {
            File f = new File(directory, itemCode + ".png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            if (b == null) {
                status = false;
                return status;
            } else {
                status = true;
                return status;
            }
        } catch (FileNotFoundException e) {
            status = false;
            //e.printStackTrace();
            //Log.d("FileNotFoundException", e.toString());
        }
        return status;
    }

    public void downLoadImages(Bitmap bitmapImage, String imageName) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, imageName + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.d("IOE_Exception", e.toString());
            }
        }
    }
}


