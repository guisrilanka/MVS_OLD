package com.gui.mdt.thongsieknavclient.model.grnmodels;

/**
 * Created by yeqim_000 on 30/08/16.
 */
public class PrintPurchaseOrderReceiptLineLabelParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String PurchaseOrderReceiptNo;
    private int PurchaseOrderReceiptLineNo;

    public PrintPurchaseOrderReceiptLineLabelParameter(String userCompany, String userName, String password, String purchaseOrderReceiptNo, int purchaseOrderReceiptLineNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        PurchaseOrderReceiptNo = purchaseOrderReceiptNo;
        PurchaseOrderReceiptLineNo = purchaseOrderReceiptLineNo;
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

    public String getPurchaseOrderReceiptNo() {
        return PurchaseOrderReceiptNo;
    }

    public void setPurchaseOrderReceiptNo(String purchaseOrderReceiptNo) {
        PurchaseOrderReceiptNo = purchaseOrderReceiptNo;
    }

    public int getPurchaseOrderReceiptLineNo() {
        return PurchaseOrderReceiptLineNo;
    }

    public void setPurchaseOrderReceiptLineNo(int purchaseOrderReceiptLineNo) {
        PurchaseOrderReceiptLineNo = purchaseOrderReceiptLineNo;
    }
}
