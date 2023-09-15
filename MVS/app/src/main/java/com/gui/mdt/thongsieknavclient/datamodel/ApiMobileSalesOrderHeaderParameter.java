package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 01/05/2018.
 */

public class ApiMobileSalesOrderHeaderParameter {

    String FilterDriverCode;
    String UserName;
    String Password;
    String UserCompany;
    String FilterSONumber;

    public String getFilterDriverCode() {
        return FilterDriverCode;
    }

    public void setFilterDriverCode(String filterDriverCode) {
        FilterDriverCode = filterDriverCode;
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

    public String getFilterSONumber() {
        return FilterSONumber;
    }

    public void setFilterSONumber(String filterSONumber) {
        FilterSONumber = filterSONumber;
    }
}
