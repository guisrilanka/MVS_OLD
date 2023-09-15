package com.gui.mdt.thongsieknavclient.model.itemmodels;

/**
 * Created by user on 12/29/2016.
 */

public class ItemListResultData {


    private String ItemCategoryCode;
    private String Description;
    private String ItemCode;
    private String ItemBaseUom;
    private boolean IsItemTrackingRequired;

    public ItemListResultData(String itemCategoryCode, String description, String itemCode, String itemBaseUom, boolean isItemTrackingRequired) {
        ItemCategoryCode = itemCategoryCode;
        Description = description;
        ItemCode = itemCode;
        ItemBaseUom = itemBaseUom;
        IsItemTrackingRequired = isItemTrackingRequired;
    }

    public String getItemCategoryCode() {
        return ItemCategoryCode;
    }

    public void setItemCategoryCode(String itemCategoryCode) {
        ItemCategoryCode = itemCategoryCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemBaseUom() {
        return ItemBaseUom;
    }

    public void setItemBaseUom(String itemBaseUom) {
        ItemBaseUom = itemBaseUom;
    }

    public boolean isItemTrackingRequired() {
        return IsItemTrackingRequired;
    }

    public void setItemTrackingRequired(boolean itemTrackingRequired) {
        IsItemTrackingRequired = itemTrackingRequired;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
