package com.gui.mdt.thongsieknavclient.model.itemmodels;

/**
 * Created by bhanuka on 09/08/2017.
 */

public class ItemCrossReferenceListParameter {

    private String CustomerCode;
    private String CustomerName;
    private String ItemCode;
    private String ItemName;
    private String UserName;
    private String Password;
    private String UserCompany;

    public ItemCrossReferenceListParameter(){}

    public String getCustomerCode() {
        return CustomerCode;
    }

    public void setCustomerCode(String customerCodes) {
        CustomerCode = customerCodes;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
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
