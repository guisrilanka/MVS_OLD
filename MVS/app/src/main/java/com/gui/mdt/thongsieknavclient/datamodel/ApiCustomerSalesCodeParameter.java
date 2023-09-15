package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 12/21/2017.
 */

public class ApiCustomerSalesCodeParameter {
    String FilterCustomerCode;
    String UserName;
    String Password;
    String UserCompany;

    public String getFilterCustomerCode() {
        return FilterCustomerCode;
    }

    public void setFilterCustomerCode(String filterCustomerCode) {
        FilterCustomerCode = filterCustomerCode;
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
