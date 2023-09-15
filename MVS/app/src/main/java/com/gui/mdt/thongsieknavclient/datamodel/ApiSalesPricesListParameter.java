package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by bhanuka on 09/08/2017.
 */

public class ApiSalesPricesListParameter {

    String SalesCode;
    String CustomerName;
    String Starting_Date;
    String Ending_Date;
    String SalesType;
    String UserName;
    String Password;
    String UserCompany;
    String FilterLastModifiedDate;

    public ApiSalesPricesListParameter(){}

    public String getSalesCode() {
        return SalesCode;
    }

    public void setSalesCode(String salesCode) {
        SalesCode = salesCode;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getStarting_Date() {
        return Starting_Date;
    }

    public void setStarting_Date(String starting_Date) {
        Starting_Date = starting_Date;
    }

    public String getEnding_Date() {
        return Ending_Date;
    }

    public void setEnding_Date(String ending_Date) {
        Ending_Date = ending_Date;
    }

    public String getSalesType() {
        return SalesType;
    }

    public void setSalesType(String salesType) {
        SalesType = salesType;
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
