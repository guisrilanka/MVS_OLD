package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by GUI-NB03 on 2017-09-28.
 */

public class ApiPostMobileStockTransferHeaderParameter {
    String Key;   //key
    String Document_No;  //No
    String Stock_Transfer_Date;  //DocumentDate
    String Total_Qty;
    String No_of_Items;
    String Driver_Code; //DriverCode
    String Stock_Transfer_No; //“”
    String Created_By;  //CreatedBy
    String Created_DateTime; //CreatedDateTime
    String Last_Modified_By; //LastModifiedBy
    String Last_Modified_DateTime; //LastModifiedDateTime
    String Last_Transferred_By;  //LastTransferredBy
    String Last_Transferred_DateTime; //LastTransferredDateTime
    String UserName; //mAppUser
    String Password; //mAppPassword
    String UserCompany; //mAppCompany

    public ApiPostMobileStockTransferHeaderParameter(String userCompany, String userName, String password) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
    }

    public ApiPostMobileStockTransferHeaderParameter() {

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

    public String getStock_Transfer_Date() {
        return Stock_Transfer_Date;
    }

    public void setStock_Transfer_Date(String stock_Transfer_Date) {
        Stock_Transfer_Date = stock_Transfer_Date;
    }

    public String getTotal_Qty() {
        return Total_Qty;
    }

    public void setTotal_Qty(String total_Qty) {
        Total_Qty = total_Qty;
    }

    public String getNo_of_Items() {
        return No_of_Items;
    }

    public void setNo_of_Items(String no_of_Items) {
        No_of_Items = no_of_Items;
    }

    public String getDriver_Code() {
        return Driver_Code;
    }

    public void setDriver_Code(String driver_Code) {
        Driver_Code = driver_Code;
    }

    public String getStock_Transfer_No() {
        return Stock_Transfer_No;
    }

    public void setStock_Transfer_No(String stock_Transfer_No) {
        Stock_Transfer_No = stock_Transfer_No;
    }

    public String getCreated_By() {
        return Created_By;
    }

    public void setCreated_By(String created_By) {
        Created_By = created_By;
    }

    public String getCreated_DateTime() {
        return Created_DateTime;
    }

    public void setCreated_DateTime(String created_DateTime) {
        Created_DateTime = created_DateTime;
    }

    public String getLast_Modified_By() {
        return Last_Modified_By;
    }

    public void setLast_Modified_By(String last_Modified_By) {
        Last_Modified_By = last_Modified_By;
    }

    public String getLast_Modified_DateTime() {
        return Last_Modified_DateTime;
    }

    public void setLast_Modified_DateTime(String last_Modified_DateTime) {
        Last_Modified_DateTime = last_Modified_DateTime;
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
}
