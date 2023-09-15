package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by bhanuka on 02/08/2017.
 */

public class ItemCategory {

    private Integer Id;
    private String ItemCategoryCode;
    private String Description;

    public ItemCategory(){}

    /*public ItemCategory(String itemCategoryCode,String description){

        ItemCategoryCode=itemCategoryCode;
        Description=description;

    }*/
    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
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

    @Override
    public String toString() {
        return super.toString();
    }

}
