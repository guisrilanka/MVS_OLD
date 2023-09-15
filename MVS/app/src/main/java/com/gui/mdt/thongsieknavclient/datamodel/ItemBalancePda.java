package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 08/16/2017.
 */

public class ItemBalancePda {

    String key;
    String  itemNo;
    String locationCode;
    String  binCode;
    float quantity;
    float openQty;
    float exchangedQty;
    String unitofMeasureCode;
    String itemDescription;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getOpenQty() {
        return openQty;
    }

    public void setOpenQty(float openQty) {
        this.openQty = openQty;
    }

    public float getExchangedQty() {
        return exchangedQty;
    }

    public void setExchangedQty(float exchangedQty) {
        this.exchangedQty = exchangedQty;
    }

    public String getUnitofMeasureCode() {
        return unitofMeasureCode;
    }

    public void setUnitofMeasureCode(String unitofMeasureCode) {
        this.unitofMeasureCode = unitofMeasureCode;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}
