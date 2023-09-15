package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by GUI-NB03 on 2017-08-17.
 */

public class ApiPostMobileSalesInvoiceHeaderParameter {
    String Key;   //key
    String Document_No;  //No
    String Customer_Number;  //SelltoCustomerNo
    String Ship_To_Number;  //SelltoCustomerNo
    String Saleperson_Number;  //SalespersonCode
    String Detail_Ordinal;  //“0 ”
    String Sales_Order_Date;  //DocumentDate
    String Due_Date; //DueDate
    String Purchase_Order_Number;  //ExternalDocumentNo
    String Total_Amt_after_Discount;  //AmountIncludingVAT 7
    String Discount_Amt; //“0”
    String Tax_Percentage; //“7”
    String Total_Amt_after_Tax; //AmountIncludingVAT
    String Driver_Code; //DriverCode
    String Comment;  //Comments
    String Stock_Request_No; //“”
    String Created_By;  //CreatedBy
    String Created_DateTime; //CreatedDateTime
    String Last_Modified_By; //LastModifiedBy
    String Last_Modified_DateTime; //LastModifiedDateTime
    String Last_Transferred_By;  //LastTransferredBy
    String Last_Transferred_DateTime; //LastTransferredDateTime
    String UserName; //mAppUser
    String Password; //mAppPassword
    String UserCompany; //mAppCompany
    String Sales_Order_No;

    public ApiPostMobileSalesInvoiceHeaderParameter(String userCompany, String userName, String password) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
    }

    public ApiPostMobileSalesInvoiceHeaderParameter() {

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

    public String getCustomer_Number() {
        return Customer_Number;
    }

    public void setCustomer_Number(String customer_Number) {
        Customer_Number = customer_Number;
    }

    public String getShip_To_Number() {
        return Ship_To_Number;
    }

    public void setShip_To_Number(String ship_To_Number) {
        Ship_To_Number = ship_To_Number;
    }

    public String getSaleperson_Number() {
        return Saleperson_Number;
    }

    public void setSaleperson_Number(String saleperson_Number) {
        Saleperson_Number = saleperson_Number;
    }

    public String getDetail_Ordinal() {
        return Detail_Ordinal;
    }

    public void setDetail_Ordinal(String detail_Ordinal) {
        Detail_Ordinal = detail_Ordinal;
    }

    public String getSales_Order_Date() {
        return Sales_Order_Date;
    }

    public void setSales_Order_Date(String sales_Order_Date) {
        Sales_Order_Date = sales_Order_Date;
    }

    public String getDue_Date() {
        return Due_Date;
    }

    public void setDue_Date(String due_Date) {
        Due_Date = due_Date;
    }

    public String getPurchase_Order_Number() {
        return Purchase_Order_Number;
    }

    public void setPurchase_Order_Number(String purchase_Order_Number) {
        Purchase_Order_Number = purchase_Order_Number;
    }

    public String getTotal_Amt_after_Discount() {
        return Total_Amt_after_Discount;
    }

    public void setTotal_Amt_after_Discount(String total_Amt_after_Discount) {
        Total_Amt_after_Discount = total_Amt_after_Discount;
    }

    public String getDiscount_Amt() {
        return Discount_Amt;
    }

    public void setDiscount_Amt(String discount_Amt) {
        Discount_Amt = discount_Amt;
    }

    public String getTax_Percentage() {
        return Tax_Percentage;
    }

    public void setTax_Percentage(String tax_Percentage) {
        Tax_Percentage = tax_Percentage;
    }

    public String getTotal_Amt_after_Tax() {
        return Total_Amt_after_Tax;
    }

    public void setTotal_Amt_after_Tax(String total_Amt_after_Tax) {
        Total_Amt_after_Tax = total_Amt_after_Tax;
    }

    public String getDriver_Code() {
        return Driver_Code;
    }

    public void setDriver_Code(String driver_Code) {
        Driver_Code = driver_Code;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getStock_Request_No() {
        return Stock_Request_No;
    }

    public void setStock_Request_No(String stock_Request_No) {
        Stock_Request_No = stock_Request_No;
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

    public String getSales_Order_No() {
        return Sales_Order_No;
    }

    public void setSales_Order_No(String sales_Order_No) {
        Sales_Order_No = sales_Order_No;
    }
}
