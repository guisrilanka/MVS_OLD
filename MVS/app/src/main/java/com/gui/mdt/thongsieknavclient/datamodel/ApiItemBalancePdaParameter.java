package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 08/16/2017.
 */

public class ApiItemBalancePdaParameter {
    String FilterLocationCode;
    String FilterItemCode;
    String UserName;
    String Password;
    String UserCompany;
    String FilterLastModifiedDate;
    String Driver_Code;

    public String getFilterLocationCode() {
        return FilterLocationCode;
    }

    public void setFilterLocationCode(String filterLocationCode) {
        FilterLocationCode = filterLocationCode;
    }

    public String getFilterItemCode() {
        return FilterItemCode;
    }

    public void setFilterItemCode(String filterItemCode) {
        FilterItemCode = filterItemCode;
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

    public String getFilterLastModifiedDate() {
        return FilterLastModifiedDate;
    }

    public void setFilterLastModifiedDate(String filterLastModifiedDate) {
        FilterLastModifiedDate = filterLastModifiedDate;
    }

    public String getDriver_Code() {
        return Driver_Code;
    }

    public void setDriver_Code(String driver_Code) {
        Driver_Code = driver_Code;
    }
}
