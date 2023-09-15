package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

/**
 * Created by yeqim_000 on 20/07/16.
 */
public class PurchaseOrderListSearchParameter {
    private String UserCompany;
    private String FilterVendorName;
    private String FilterPurchaseOrderNo;
    private String FilterReceiveDate;
    private String UserName;
    private String Password;

    public PurchaseOrderListSearchParameter(String userCompany, String filterVendorName, String filterPurchaseOrderNo, String filterReceiveDate, String userName, String password) {
        UserCompany = userCompany;
        FilterVendorName = filterVendorName;
        FilterPurchaseOrderNo = filterPurchaseOrderNo;
        FilterReceiveDate = filterReceiveDate;
        UserName = userName;
        Password = password;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getFilterVendorName() {
        return FilterVendorName;
    }

    public void setFilterVendorName(String filterVendorName) {
        FilterVendorName = filterVendorName;
    }

    public String getFilterPurchaseOrderNo() {
        return FilterPurchaseOrderNo;
    }

    public void setFilterPurchaseOrderNo(String filterPurchaseOrderNo) {
        FilterPurchaseOrderNo = filterPurchaseOrderNo;
    }

    public String getFilterReceiveDate() {
        return FilterReceiveDate;
    }

    public void setFilterReceiveDate(String filterReceiveDate) {
        FilterReceiveDate = filterReceiveDate;
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


}
