package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

/**
 * Created by yeqim_000 on 26/07/16.
 */
public class PurchaseOrderRetrieveParameter {
    private String UserCompany;
    private String PurchaseOrderNo;
    private String UserName;
    private String Password;
    private String FilterItemCode;
    private String FilterItemDescription;
    private int NoOfLabel;

    public PurchaseOrderRetrieveParameter(String userCompany, String purchaseOrderNo, String userName, String password,String filterItemCode,String filterItemDescription) {
        UserCompany = userCompany;
        PurchaseOrderNo = purchaseOrderNo;
        UserName = userName;
        Password = password;
        FilterItemCode = filterItemCode;
        FilterItemDescription = filterItemDescription;
    }

    public String getFilterItemCode() {
        return FilterItemCode;
    }

    public void setFilterItemCode(String filterItemCode) {
        FilterItemCode = filterItemCode;
    }

    public String getFilterItemDescription() {
        return FilterItemDescription;
    }

    public void setFilterItemDescription(String filterItemDescription) {
        FilterItemDescription = filterItemDescription;
    }

    public int getNoOfLabel() {
        return NoOfLabel;
    }

    public void setNoOfLabel(int noOfLabel) {
        NoOfLabel = noOfLabel;
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
}
