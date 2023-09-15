package com.gui.mdt.thongsieknavclient.model.grnmodels;

/**
 * Created by yeqim_000 on 23/08/16.
 */
public class PurchaseOrderReceiptRetrieveParameter {
    private String UserCompany;
    private String PurchaseOrderReceiptNo;
    private String UserName;
    private String Password;

    public PurchaseOrderReceiptRetrieveParameter(String userCompany, String purchaseOrderReceiptNo, String userName, String password) {
        UserCompany = userCompany;
        PurchaseOrderReceiptNo = purchaseOrderReceiptNo;
        UserName = userName;
        Password = password;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getPurchaseOrderReceiptNo() {
        return PurchaseOrderReceiptNo;
    }

    public void setPurchaseOrderReceiptNo(String purchaseOrderReceiptNo) {
        PurchaseOrderReceiptNo = purchaseOrderReceiptNo;
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
