package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 08/08/2017.
 */

public class ApiPostedSalesInvoiceParameter {


    private String CustomerNo;
    private String CustomerName;
    private String bookmarkKey;
    private int pageSize;
    private String ItemCode;
    private String ItemName;
    private String UserName;
    private String Password;
    private String UserCompany;
    private String InvoiceNo;

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getBookmarkKey() {
        return bookmarkKey;
    }

    public void setBookmarkKey(String bookmarkKey) {
        this.bookmarkKey = bookmarkKey;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
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

    public String getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        InvoiceNo = invoiceNo;
    }
}
