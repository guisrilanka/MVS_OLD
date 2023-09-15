package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

/**
 * Created by GUI-NB03 on 2017-09-18.
 */

public class StockTransferRequestLine {
    String key;
    String stockTransferLineNo;
    String stockTransferNo;
    String driverCode;
    Float quantity;
    String unitofMeasure;
    String itemNo;
    String itemDescription;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStockTransferLineNo() {
        return stockTransferLineNo;
    }

    public void setStockTransferLineNo(String stockTransferLineNo) {
        this.stockTransferLineNo = stockTransferLineNo;
    }

    public String getStockTransferNo() {
        return stockTransferNo;
    }

    public void setStockTransferNo(String stockTransferNo) {
        this.stockTransferNo = stockTransferNo;
    }

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getUnitofMeasure() {
        return unitofMeasure;
    }

    public void setUnitofMeasure(String unitofMeasure) {
        this.unitofMeasure = unitofMeasure;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }


    //Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static StockTransferRequestLine fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, StockTransferRequestLine.class);
    }
}
