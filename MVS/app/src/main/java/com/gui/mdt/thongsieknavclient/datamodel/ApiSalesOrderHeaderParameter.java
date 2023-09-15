package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 07/26/2017.
 */

public class ApiSalesOrderHeaderParameter {
    String FilterSalesOrder;
    String FilterSalePersonNumber;
    String FilterDriverCode;
    String FilterCustomerNumber;
    String FilterLastModifiedDate;
    String UserName;
    String Password;
    String UserCompany;

    public String getFilterSalesOrder() {
        return FilterSalesOrder;
    }

    public void setFilterSalesOrder(String filterSalesOrder) {
        FilterSalesOrder = filterSalesOrder;
    }

    public String getFilterSalePersonNumber() {
        return FilterSalePersonNumber;
    }

    public void setFilterSalePersonNumber(String filterSalePersonNumber) {
        FilterSalePersonNumber = filterSalePersonNumber;
    }

    public String getFilterDriverCode() {
        return FilterDriverCode;
    }

    public void setFilterDriverCode(String filterDriverCode) {
        FilterDriverCode = filterDriverCode;
    }

    public String getFilterCustomerNumber() {
        return FilterCustomerNumber;
    }

    public void setFilterCustomerNumber(String filterCustomerNumber) {
        FilterCustomerNumber = filterCustomerNumber;
    }

    public String getFilterLastModifiedDate() {
        return FilterLastModifiedDate;
    }

    public void setFilterLastModifiedDate(String filterLastModifiedDate) {
        FilterLastModifiedDate = filterLastModifiedDate;
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
