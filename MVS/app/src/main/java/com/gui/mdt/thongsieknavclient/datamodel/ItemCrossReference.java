package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by bhanuka on 09/08/2017.
 */

public class ItemCrossReference {

    private String key;
    private Integer itemCrossReferenceType;
    private String itemCrossReferenceTypeNo;
    private String itemCrossReferenceNo;
    private String itemNo;
    private String variantCode;
    private String unitOfMeasure;
    private String itemCrossReferenceUOM;
    private String description;
    private String discontinueBarCode;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getItemCrossReferenceType() {
        return itemCrossReferenceType;
    }

    public void setItemCrossReferenceType(Integer itemCrossReferenceType) {
        this.itemCrossReferenceType = itemCrossReferenceType;
    }

    public String getItemCrossReferenceTypeNo() {
        return itemCrossReferenceTypeNo;
    }

    public void setItemCrossReferenceTypeNo(String itemCrossReferenceTypeNo) {
        this.itemCrossReferenceTypeNo = itemCrossReferenceTypeNo;
    }

    public String getItemCrossReferenceNo() {
        return itemCrossReferenceNo;
    }

    public void setItemCrossReferenceNo(String itemCrossReferenceNo) {
        this.itemCrossReferenceNo = itemCrossReferenceNo;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getItemCrossReferenceUOM() {
        return itemCrossReferenceUOM;
    }

    public void setItemCrossReferenceUOM(String itemCrossReferenceUOM) {
        this.itemCrossReferenceUOM = itemCrossReferenceUOM;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscontinueBarCode() {
        return discontinueBarCode;
    }

    public void setDiscontinueBarCode(String discontinueBarCode) {
        this.discontinueBarCode = discontinueBarCode;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
