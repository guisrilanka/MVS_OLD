package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

/**
 * Created by GUI-NB03 on 2017-09-08.
 */

public class MsoTakePictureImage {

    private String itemImageUrl;

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    //Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
