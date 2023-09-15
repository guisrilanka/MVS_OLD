package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by GUI-NB03 on 2017-08-08.
 */

public class SalesCustomerArParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String CustomerNo;
    private String InvoiceNo;
    private String bookmarkKey;
    private String pageSize;


    public SalesCustomerArParameter(String userCompany, String userName, String password) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
    }

    public SalesCustomerArParameter()
    {

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

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        InvoiceNo = invoiceNo;
    }

    public String getBookmarkKey() {
        return bookmarkKey;
    }

    public void setBookmarkKey(String bookmarkKey) {
        this.bookmarkKey = bookmarkKey;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

}
