package com.gui.mdt.thongsieknavclient.model.itemmodels;

/**
 * Created by user on 12/29/2016.
 */

public class ItemCategoryListParameter {
    private String UserCompany;
    private String UserName;
    private String Password;

    public ItemCategoryListParameter(){}

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
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
}
