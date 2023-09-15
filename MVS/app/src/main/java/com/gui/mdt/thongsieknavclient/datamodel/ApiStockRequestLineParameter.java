package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 10/25/2017.
 */

public class ApiStockRequestLineParameter {

    String FilterRequestHeader;
    String UserName;
    String Password;
    String UserCompany;

    public String getFilterRequestHeader() {
        return FilterRequestHeader;
    }

    public void setFilterRequestHeader(String filterRequestHeader) {
        FilterRequestHeader = filterRequestHeader;
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
