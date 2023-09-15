package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

/**
 * Created by nelin_000 on 07/28/2017.
 */

public class ApiSalesOrderLineResultData {

    /*String Key;
    String Document_No;
    String Line_Number;
    String Item_Number;
    String Location_Code;
    String UOM_number;
    Float Quantity;
    Float UnitPrice;
    Float Line_Amt_before_Discount;
    Float Discount_Amt;
    Float Line_Amt_after_Discount;
    String Line_Comment;
    String Driver_Code;
    String Created_By;
    String Created_DateTime;
    String Last_Modified_By;
    String Last_Modified_DateTime;
    boolean Transferred;
    String Last_Transferred_By;
    String Last_Transferred_DateTime;
    String Nav_Document_No;
    String Nav_Document_Line_No;*/


    String Key;
    Integer Type;
    String No;
    String Cross_Reference_No;
    String Driver_Code;
    String Description;
    Float Quantity;
    String Unit_of_Measure;
    boolean SalesPriceExist;
    float Unit_Price;
    float Line_Amount;
    boolean SalesLineDiscExists;
    String Line_Discount_Percent;
    Float Line_Discount_Amount;
    Float Qty_to_Invoice;
    Float Quantity_Invoiced;
    String Document_No;
    String Line_No;
    Float Total_Amount_Excl_VAT;
    Float Total_VAT_Amount;
    Float Total_Amount_Incl_VAT;
    float Amount;
    float Amount_Incl_VAT;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        Type = type;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getCross_Reference_No() {
        return Cross_Reference_No;
    }

    public void setCross_Reference_No(String cross_Reference_No) {
        Cross_Reference_No = cross_Reference_No;
    }

    public String getDriver_Code() {
        return Driver_Code;
    }

    public void setDriver_Code(String driver_Code) {
        Driver_Code = driver_Code;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Float getQuantity() {
        return Quantity;
    }

    public void setQuantity(Float quantity) {
        Quantity = quantity;
    }

    public String getUnit_of_Measure() {
        return Unit_of_Measure;
    }

    public void setUnit_of_Measure(String unit_of_Measure) {
        Unit_of_Measure = unit_of_Measure;
    }

    public boolean isSalesPriceExist() {
        return SalesPriceExist;
    }

    public void setSalesPriceExist(boolean salesPriceExist) {
        SalesPriceExist = salesPriceExist;
    }

    public float getUnit_Price() {
        return Unit_Price;
    }

    public void setUnit_Price(float unit_Price) {
        Unit_Price = unit_Price;
    }

    public float getLine_Amount() {
        return Line_Amount;
    }

    public void setLine_Amount(float line_Amount) {
        Line_Amount = line_Amount;
    }

    public boolean isSalesLineDiscExists() {
        return SalesLineDiscExists;
    }

    public void setSalesLineDiscExists(boolean salesLineDiscExists) {
        SalesLineDiscExists = salesLineDiscExists;
    }

    public String getLine_Discount_Percent() {
        return Line_Discount_Percent;
    }

    public void setLine_Discount_Percent(String line_Discount_Percent) {
        Line_Discount_Percent = line_Discount_Percent;
    }

    public Float getLine_Discount_Amount() {
        return Line_Discount_Amount;
    }

    public void setLine_Discount_Amount(Float line_Discount_Amount) {
        Line_Discount_Amount = line_Discount_Amount;
    }

    public Float getQty_to_Invoice() {
        return Qty_to_Invoice;
    }

    public void setQty_to_Invoice(Float qty_to_Invoice) {
        Qty_to_Invoice = qty_to_Invoice;
    }

    public Float getQuantity_Invoiced() {
        return Quantity_Invoiced;
    }

    public void setQuantity_Invoiced(Float quantity_Invoiced) {
        Quantity_Invoiced = quantity_Invoiced;
    }

    public String getDocument_No() {
        return Document_No;
    }

    public void setDocument_No(String document_No) {
        Document_No = document_No;
    }

    public String getLine_No() {
        return Line_No;
    }

    public void setLine_No(String line_No) {
        Line_No = line_No;
    }

    public Float getTotal_Amount_Excl_VAT() {
        return Total_Amount_Excl_VAT;
    }

    public void setTotal_Amount_Excl_VAT(Float total_Amount_Excl_VAT) {
        Total_Amount_Excl_VAT = total_Amount_Excl_VAT;
    }

    public Float getTotal_VAT_Amount() {
        return Total_VAT_Amount;
    }

    public void setTotal_VAT_Amount(Float total_VAT_Amount) {
        Total_VAT_Amount = total_VAT_Amount;
    }

    public Float getTotal_Amount_Incl_VAT() {
        return Total_Amount_Incl_VAT;
    }

    public void setTotal_Amount_Incl_VAT(Float total_Amount_Incl_VAT) {
        Total_Amount_Incl_VAT = total_Amount_Incl_VAT;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public float getAmount_Incl_VAT() {
        return Amount_Incl_VAT;
    }

    public void setAmount_Incl_VAT(float amount_Incl_VAT) {
        Amount_Incl_VAT = amount_Incl_VAT;
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

}
