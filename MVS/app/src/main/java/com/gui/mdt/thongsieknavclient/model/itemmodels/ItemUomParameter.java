package com.gui.mdt.thongsieknavclient.model.itemmodels;

/**
 * Created by user on 12/30/2016.
 */

public class ItemUomParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String FilterItemCode;

    public ItemUomParameter(String userCompany, String userName, String password, String filterItemCode) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        FilterItemCode = filterItemCode;
    }

    public ItemUomParameter(){}

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

    public String getFilterItemCode() {
        return FilterItemCode;
    }

    public void setFilterItemCode(String filterItemCode) {
        FilterItemCode = filterItemCode;
    }


}
