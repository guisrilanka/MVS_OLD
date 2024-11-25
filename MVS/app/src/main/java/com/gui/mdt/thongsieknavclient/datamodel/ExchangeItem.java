package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;


public class ExchangeItem {

    private Integer Id;

    private String itemCode;

    private String Description;

    private String itemImageUrl;

    private String uom;

    public float totalQty;
    public float issueQty;
    public float balanceQty;

    public String locationCode;
    public String getLocationCode() {
        return locationCode;
    }
    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public float getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(float totalQty) {
        this.totalQty = totalQty;
    }

    public float getIssueQty() {
        return issueQty;
    }

    public void setIssueQty(float issueQty) {
        this.issueQty = issueQty;
    }

    public float getBalanceQty() {
        return balanceQty;
    }

    public void setBalanceQty(float balanceQty) {
        this.balanceQty = balanceQty;
    }


    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }




    @Override
    public String toString() {
        return super.toString();
    }

    //Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static ExchangeItem fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ExchangeItem.class);
    }
}
