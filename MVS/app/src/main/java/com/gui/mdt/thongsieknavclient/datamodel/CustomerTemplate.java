package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 12/21/2017.
 */

public class CustomerTemplate {

    String SalesCode;
    String ItemNo;
    String Description;
    float Quantity;
    String itemUom;

    public String getSalesCode() {
        return SalesCode;
    }

    public void setSalesCode(String salesCode) {
        SalesCode = salesCode;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public String getItemUom() {
        return itemUom;
    }

    public void setItemUom(String itemUom) {
        this.itemUom = itemUom;
    }
}
