package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

/**
 * Created by yeqim_000 on 19/08/16.
 */
public class PurchaseOrderLineLotEntriesSearchParameter {
    private String UserCompany;
    private String PurchaseOrderNo;
    private String UserName;
    private String Password;
    private int LineNo;

    public PurchaseOrderLineLotEntriesSearchParameter(String userCompany, String purchaseOrderNo, String userName, String password, int lineNo) {
        UserCompany = userCompany;
        PurchaseOrderNo = purchaseOrderNo;
        UserName = userName;
        Password = password;
        LineNo = lineNo;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getPurchaseOrderNo() {
        return PurchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        PurchaseOrderNo = purchaseOrderNo;
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

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

}
