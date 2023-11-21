package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

/**
 * Created by nelin_000 on 07/25/2017.
 */

public class Item {

    private Integer Id;
    private String ItemCategoryCode;
    private String Description;
    private String ItemCode;
    private String ItemBaseUom;
    private boolean IsItemTrackingRequired;

    private String itemImageUrl;

            //new Fields
    public String ProductGroupCode;
    public String QtyOnPurchOrder;
    public String QtyOnSalesOrder;
    public Boolean Blocked;
    public String UnitPrice;
    public String NetInvoicedQty;
    public String IdentifierCode;
    public String VatProdPostingGroup;
    public float Qty;
    public float ExchQty;

    public boolean isInventoryValueZero() {
        return isInventoryValueZero;
    }

    public void setInventoryValueZero(boolean inventoryValueZero) {
        isInventoryValueZero = inventoryValueZero;
    }

    private boolean isInventoryValueZero;

    public String getVatProdPostingGroup() {
        return VatProdPostingGroup;
    }

    public void setVatProdPostingGroup(String vatProdPostingGroup) {
        VatProdPostingGroup = vatProdPostingGroup;
    }

    public String getIdentifierCode() {
        return IdentifierCode;
    }

    public void setIdentifierCode(String identifierCode) {
        IdentifierCode = identifierCode;
    }

    public String getProductGroupCode() {
        return ProductGroupCode;
    }

    public void setProductGroupCode(String productGroupCode) {
        ProductGroupCode = productGroupCode;
    }

    public String getQtyOnPurchOrder() {
        return QtyOnPurchOrder;
    }

    public void setQtyOnPurchOrder(String qtyOnPurchOrder) {
        QtyOnPurchOrder = qtyOnPurchOrder;
    }

    public String getQtyOnSalesOrder() {
        return QtyOnSalesOrder;
    }

    public void setQtyOnSalesOrder(String qtyOnSalesOrder) {
        QtyOnSalesOrder = qtyOnSalesOrder;
    }

    public Boolean getBlocked() {
        return Blocked;
    }

    public void setBlocked(Boolean blocked) {
        Blocked = blocked;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getNetInvoicedQty() {
        return NetInvoicedQty;
    }

    public void setNetInvoicedQty(String netInvoicedQty) {
        NetInvoicedQty = netInvoicedQty;
    }
    //

    public Item() {
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

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

    public float getQty() {
        return Qty;
    }

    public void setQty(float qty) {
        Qty = qty;
    }

    public float getExchQty() {
        return ExchQty;
    }

    public void setExchQty(float exchQty) {
        ExchQty = exchQty;
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

    public static Item fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Item.class);
    }
}
