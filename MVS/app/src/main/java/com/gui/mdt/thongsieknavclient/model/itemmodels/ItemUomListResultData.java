package com.gui.mdt.thongsieknavclient.model.itemmodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

/**
 * Created by user on 12/30/2016.
 */

public class ItemUomListResultData{

    private String Code;
    private String Uom;
    private String ItemCode;
    private float QuantityPerUom;

    public ItemUomListResultData(String code, String uom, String itemCode, float quantityPerUom) {
        Code = code;
        Uom = uom;
        ItemCode = itemCode;
        QuantityPerUom = quantityPerUom;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getItemUnitOfMeasure() {
        return Uom;
    }

    public void setItemUnitOfMeasure(String itemUnitOfMeasure) {
        Uom = itemUnitOfMeasure;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public float getQuantityPerUom() {
        return QuantityPerUom;
    }

    public void setQuantityPerUom(float quantityPerUom) {
        QuantityPerUom = quantityPerUom;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
