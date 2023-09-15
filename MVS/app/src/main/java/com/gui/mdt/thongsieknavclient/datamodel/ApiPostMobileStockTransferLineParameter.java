package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by GUI-NB03 on 2017-09-28.
 */

public class ApiPostMobileStockTransferLineParameter {

    String UserName;    //"muser02"
    String Password;    //"1"
    String UserCompany; //"DODO Marketing"
    String Key;
    String Document_No;
    String Item_Number;
    String UOM_number;
    String Quantity;
    String Driver_Code;
    String Created_DateTime;
    String Last_Modified_DateTime;
    String Transferred;
    String Last_Transferred_By;
    String Last_Transferred_DateTime;

    public ApiPostMobileStockTransferLineParameter(String userCompany, String userName, String password) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
    }

    public ApiPostMobileStockTransferLineParameter() {

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

    public String getItem_Number() {
        return Item_Number;
    }

    public void setItem_Number(String item_Number) {
        Item_Number = item_Number;
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
}
