package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 12/28/2017.
 */

public class ApiItemCrossReferenceResponse {

    String Key;
    String Cross_Reference_Type;
    String Cross_Reference_Type_No;
    String Cross_Reference_No;
    String Item_No;
    String Variant_Code;
    String Unit_of_Measure;
    String Cross_Reference_UOM;
    String Description;
    String Discontinue_Bar_Code;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getCross_Reference_Type() {
        return Cross_Reference_Type;
    }

    public void setCross_Reference_Type(String cross_Reference_Type) {
        Cross_Reference_Type = cross_Reference_Type;
    }

    public String getCross_Reference_Type_No() {
        return Cross_Reference_Type_No;
    }

    public void setCross_Reference_Type_No(String cross_Reference_Type_No) {
        Cross_Reference_Type_No = cross_Reference_Type_No;
    }

    public String getCross_Reference_No() {
        return Cross_Reference_No;
    }

    public void setCross_Reference_No(String cross_Reference_No) {
        Cross_Reference_No = cross_Reference_No;
    }

    public String getItem_No() {
        return Item_No;
    }

    public void setItem_No(String item_No) {
        Item_No = item_No;
    }

    public String getVariant_Code() {
        return Variant_Code;
    }

    public void setVariant_Code(String variant_Code) {
        Variant_Code = variant_Code;
    }

    public String getUnit_of_Measure() {
        return Unit_of_Measure;
    }

    public void setUnit_of_Measure(String unit_of_Measure) {
        Unit_of_Measure = unit_of_Measure;
    }

    public String getCross_Reference_UOM() {
        return Cross_Reference_UOM;
    }

    public void setCross_Reference_UOM(String cross_Reference_UOM) {
        Cross_Reference_UOM = cross_Reference_UOM;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDiscontinue_Bar_Code() {
        return Discontinue_Bar_Code;
    }

    public void setDiscontinue_Bar_Code(String discontinue_Bar_Code) {
        Discontinue_Bar_Code = discontinue_Bar_Code;
    }
}
