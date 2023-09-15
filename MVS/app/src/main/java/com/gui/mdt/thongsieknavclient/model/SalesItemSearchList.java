package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by bhanuka on 26/07/2017.
 */

public class SalesItemSearchList {

    String itemcode;
    String description;

    public SalesItemSearchList(String itemcode_, String description_) {

        this.itemcode=itemcode_;
        this.description=description_;
    }

    public String getItemCode() {
        return itemcode;
    }

    public String getDescription() {
        return description;
    }

}
