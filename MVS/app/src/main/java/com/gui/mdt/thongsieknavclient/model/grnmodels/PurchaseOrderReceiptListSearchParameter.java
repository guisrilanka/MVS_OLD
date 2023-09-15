package com.gui.mdt.thongsieknavclient.model.grnmodels;

import java.util.logging.Filter;

/**
 * Created by yeqim_000 on 22/08/16.
 */
public class PurchaseOrderReceiptListSearchParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String FilterVendorName;
    private String FilterPurchaseOrderNo;
    private String FilterPurchaseReceiptDate;

    public PurchaseOrderReceiptListSearchParameter(String userCompany, String userName, String password, String filterVendorName, String filterPurchaseOrderNo, String filterPurchaseReceiptDate) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        FilterVendorName = filterVendorName;
        FilterPurchaseOrderNo = filterPurchaseOrderNo;
        FilterPurchaseReceiptDate = filterPurchaseReceiptDate;
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

    public String getFilterVendorName() {return FilterVendorName; }

    public void setFilterVendorName(String filterVendorName) {
        FilterVendorName = filterVendorName;
    }

    public String getFilterPurchaseOrderNo() {return FilterPurchaseOrderNo; }

    public void setFilterPurchaseOrderNo(String filterPurchaseOrderNo) {
        FilterPurchaseOrderNo = filterPurchaseOrderNo;
    }

    public String getFilterPurchaseReceiptDate() {
        return FilterPurchaseReceiptDate;
    }

    public void setFilterPurchaseReceiptDate(String filterPurchaseReceiptDate) {
        FilterPurchaseReceiptDate = filterPurchaseReceiptDate;
    }
}
