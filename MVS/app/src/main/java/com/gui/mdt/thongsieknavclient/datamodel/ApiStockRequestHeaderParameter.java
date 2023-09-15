package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 10/25/2017.
 */

public class ApiStockRequestHeaderParameter {

    String FilterStockRequest;
    String FilterSalePersonNumber;
    String FilterCustomerNumber;
    Boolean FilterDelivered;
    Boolean FilterDeliveredSpecified;
    String FilterDriverCode;
    String FilterLastModifiedDate;
    String UserName;
    String Password;
    String UserCompany;

    public String getFilterDriverCode() {
        return FilterDriverCode;
    }

    public void setFilterDriverCode(String filterDriverCode) {
        FilterDriverCode = filterDriverCode;
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

    public String getFilterSalePersonNumber() {
        return FilterSalePersonNumber;
    }

    public void setFilterSalePersonNumber(String filterSalePersonNumber) {
        FilterSalePersonNumber = filterSalePersonNumber;
    }

    public String getFilterCustomerNumber() {
        return FilterCustomerNumber;
    }

    public void setFilterCustomerNumber(String filterCustomerNumber) {
        FilterCustomerNumber = filterCustomerNumber;
    }

    public Boolean getFilterDeliveredSpecified() {
        return FilterDeliveredSpecified;
    }

    public void setFilterDeliveredSpecified(Boolean filterDeliveredSpecified) {
        FilterDeliveredSpecified = filterDeliveredSpecified;
    }

    public String getFilterStockRequest() {
        return FilterStockRequest;
    }

    public void setFilterStockRequest(String filterStockRequest) {
        FilterStockRequest = filterStockRequest;
    }

    public Boolean getFilterDelivered() {
        return FilterDelivered;
    }

    public void setFilterDelivered(Boolean filterDelivered) {
        FilterDelivered = filterDelivered;
    }
}
