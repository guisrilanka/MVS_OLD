package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 01/23/2018.
 */

public class ApiDriverParameter {

    String FilterDriverCode;
    String Password;
    String UserCompany;
    String UserName;

    public ApiDriverParameter(String userCompany, String userName, String password, String filterDeriverCode){
        FilterDriverCode = filterDeriverCode;
        Password = password;
        UserCompany = userCompany;
        UserName = userName;
    }
    public String getFilterDriverCode() {
        return FilterDriverCode;
    }

    public void setFilterDriverCode(String filterDriverCode) {
        FilterDriverCode = filterDriverCode;
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

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
