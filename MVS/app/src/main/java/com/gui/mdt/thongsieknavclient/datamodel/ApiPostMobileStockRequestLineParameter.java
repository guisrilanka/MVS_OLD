package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 11/10/2017.
 */

public class ApiPostMobileStockRequestLineParameter {

    String UserName;    //"muser02"
    String Password;    //"1"
    String UserCompany; //"DODO Marketing"
    String Key;         //""
    String Document_No; //"SDFSD232342"
    String Line_Number; //"0";
    String Item_Number; //"CVB03A"
    String Location_Code;   //""
    String UOM_number;  //""
    String Quantity;    //"7"
    String Unit_Price;  //"7.34"
    String Line_Amt_before_Discount;    //"23.454"
    String Discount_Amt;    //"2.2"
    String Line_Amt_after_Discount; //"5.67"
    String Line_Comment;    //"COMMENTS"
    String Driver_Code; //"V0002"
    String Created_DateTime;    //""
    String Last_Modified_DateTime;  //"2017-08-14T00:00:00"
    String Transferred; //"1"
    String Last_Transferred_By; //"RANJAN"
    String Last_Transferred_DateTime;   //"2017-08-14T00:00:00"
    String Nav_Document_No; //"ERE"
    String Nav_Document_Line_No;    //"SDS"
    String Gen_Prod_Posting_Group;

    public ApiPostMobileStockRequestLineParameter(String userCompany, String userName, String password) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
    }

    public ApiPostMobileStockRequestLineParameter() {

    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getDocument_No() {
        return Document_No;
    }

    public void setDocument_No(String document_No) {
        Document_No = document_No;
    }

    public String getLine_Number() {
        return Line_Number;
    }

    public void setLine_Number(String line_Number) {
        Line_Number = line_Number;
    }

    public String getItem_Number() {
        return Item_Number;
    }

    public void setItem_Number(String item_Number) {
        Item_Number = item_Number;
    }

    public String getLocation_Code() {
        return Location_Code;
    }

    public void setLocation_Code(String location_Code) {
        Location_Code = location_Code;
    }

    public String getUOM_number() {
        return UOM_number;
    }

    public void setUOM_number(String UOM_number) {
        this.UOM_number = UOM_number;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getUnit_Price() {
        return Unit_Price;
    }

    public void setUnit_Price(String unit_Price) {
        Unit_Price = unit_Price;
    }

    public String getLine_Amt_before_Discount() {
        return Line_Amt_before_Discount;
    }

    public void setLine_Amt_before_Discount(String line_Amt_before_Discount) {
        Line_Amt_before_Discount = line_Amt_before_Discount;
    }

    public String getDiscount_Amt() {
        return Discount_Amt;
    }

    public void setDiscount_Amt(String discount_Amt) {
        Discount_Amt = discount_Amt;
    }

    public String getLine_Amt_after_Discount() {
        return Line_Amt_after_Discount;
    }

    public void setLine_Amt_after_Discount(String line_Amt_after_Discount) {
        Line_Amt_after_Discount = line_Amt_after_Discount;
    }

    public String getLine_Comment() {
        return Line_Comment;
    }

    public void setLine_Comment(String line_Comment) {
        Line_Comment = line_Comment;
    }

    public String getDriver_Code() {
        return Driver_Code;
    }

    public void setDriver_Code(String driver_Code) {
        Driver_Code = driver_Code;
    }

    public String getCreated_DateTime() {
        return Created_DateTime;
    }

    public void setCreated_DateTime(String created_DateTime) {
        Created_DateTime = created_DateTime;
    }

    public String getLast_Modified_DateTime() {
        return Last_Modified_DateTime;
    }

    public void setLast_Modified_DateTime(String last_Modified_DateTime) {
        Last_Modified_DateTime = last_Modified_DateTime;
    }

    public String getTransferred() {
        return Transferred;
    }

    public void setTransferred(String transferred) {
        Transferred = transferred;
    }

    public String getLast_Transferred_By() {
        return Last_Transferred_By;
    }

    public void setLast_Transferred_By(String last_Transferred_By) {
        Last_Transferred_By = last_Transferred_By;
    }

    public String getLast_Transferred_DateTime() {
        return Last_Transferred_DateTime;
    }

    public void setLast_Transferred_DateTime(String last_Transferred_DateTime) {
        Last_Transferred_DateTime = last_Transferred_DateTime;
    }

    public String getNav_Document_No() {
        return Nav_Document_No;
    }

    public void setNav_Document_No(String nav_Document_No) {
        Nav_Document_No = nav_Document_No;
    }

    public String getNav_Document_Line_No() {
        return Nav_Document_Line_No;
    }

    public void setNav_Document_Line_No(String nav_Document_Line_No) {
        Nav_Document_Line_No = nav_Document_Line_No;
    }

    public String getGen_Prod_Posting_Group() {
        return Gen_Prod_Posting_Group;
    }

    public void setGen_Prod_Posting_Group(String gen_Prod_Posting_Group) {
        Gen_Prod_Posting_Group = gen_Prod_Posting_Group;
    }
}
