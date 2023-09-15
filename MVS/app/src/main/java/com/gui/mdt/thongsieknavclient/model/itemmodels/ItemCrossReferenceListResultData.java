package com.gui.mdt.thongsieknavclient.model.itemmodels;

/**
 * Created by bhanuka on 09/08/2017.
 */

public class ItemCrossReferenceListResultData {

    private String Key;
    private Integer Cross_Reference_Type;
    private String Cross_Reference_Type_No;
    private String Cross_Reference_No;
    private String Item_No;
    private String Variant_Code;
    private String Unit_of_Measure;
    private String Cross_Reference_UOM;
    private String Description;
    private String Discontinue_Bar_Code;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        this.Key = key;
    }

    public Integer getItemCrossReferenceType() {
        return Cross_Reference_Type;
    }

    public void setItemCrossReferenceType(Integer itemCrossReferenceType) {
        this.Cross_Reference_Type = itemCrossReferenceType;
    }

    public String getItemCrossReferenceTypeNo() {
        return Cross_Reference_Type_No;
    }

    public void setItemCrossReferenceTypeNo(String itemCrossReferenceTypeNo) {
        this.Cross_Reference_Type_No = itemCrossReferenceTypeNo;
    }

    public String getItemCrossReferenceNo() {
        return Cross_Reference_No;
    }

    public void setItemCrossReferenceNo(String itemCrossReferenceNo) {
        this.Cross_Reference_No = itemCrossReferenceNo;
    }

    public String getItemNo() {
        return Item_No;
    }

    public void setItemNo(String itemNo) {
        this.Item_No = itemNo;
    }

    public String getVariantCode() {
        return Variant_Code;
    }

    public void setVariantCode(String variantCode) {
        this.Variant_Code = variantCode;
    }

    public String getUnitOfMeasure() {
        return Unit_of_Measure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.Unit_of_Measure = unitOfMeasure;
    }

    public String getItemCrossReferenceUOM() {
        return Cross_Reference_UOM;
    }

    public void setItemCrossReferenceUOM(String itemCrossReferenceUOM) {
        this.Cross_Reference_UOM = itemCrossReferenceUOM;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getDiscontinueBarCode() {
        return Discontinue_Bar_Code;
    }

    public void setDiscontinueBarCode(String discontinueBarCode) {
        this.Discontinue_Bar_Code = discontinueBarCode;
    }
}
