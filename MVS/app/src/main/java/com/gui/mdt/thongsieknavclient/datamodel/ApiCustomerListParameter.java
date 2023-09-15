package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 07/19/2017.
 */

public class ApiCustomerListParameter {

    String FilterCustomerCode;
    String FilterCustomerName;
    String FilterDriverCode;
    String FilterSalesPersonCode;
    String UserName;
    String Password;
    String UserCompany;
    String FilterLastModifiedDate;

    public String getFilterCustomerCode() {
        return FilterCustomerCode;
    }

    public void setFilterCustomerCode(String filterCustomerCode) {
        FilterCustomerCode = filterCustomerCode;
    }

    public String getFilterCustomerName() {
        return FilterCustomerName;
    }

    public void setFilterCustomerName(String filterCustomerName) {
        FilterCustomerName = filterCustomerName;
    }

    public String getFilterDriverCode() {
        return FilterDriverCode;
    }

    public void setFilterDriverCode(String filterDriverCode) {
        FilterDriverCode = filterDriverCode;
    }

    public String getFilterSalesPersonCode() {
        return FilterSalesPersonCode;
    }

    public void setFilterSalesPersonCode(String filterSalesPersonCode) {
        FilterSalesPersonCode = filterSalesPersonCode;
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
}
