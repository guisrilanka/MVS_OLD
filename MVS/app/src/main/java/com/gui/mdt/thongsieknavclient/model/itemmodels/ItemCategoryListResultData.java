package com.gui.mdt.thongsieknavclient.model.itemmodels;

/**
 * Created by user on 12/29/2016.
 */

public class ItemCategoryListResultData {

    private String Code;
    private String Description;

    public ItemCategoryListResultData(String code,String description){
        this.Code = code;
        this.Description = description;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
