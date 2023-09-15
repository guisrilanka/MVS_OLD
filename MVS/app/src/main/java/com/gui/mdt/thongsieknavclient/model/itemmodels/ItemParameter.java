package com.gui.mdt.thongsieknavclient.model.itemmodels;

/**
 * Created by user on 12/30/2016.
 */

public class ItemParameter {
    private String UserCompany;
    private String Password;
    private String UserName;
    private String ItemCode;

    public ItemParameter(String userCompany, String password, String userName, String itemCode) {
        UserCompany = userCompany;
        Password = password;
        UserName = userName;
        ItemCode = itemCode;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }
}
