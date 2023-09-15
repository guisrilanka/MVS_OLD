package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/8/2016.
 */

public class ItemReclassificationListSearchParameter {

    private String UserCompany;
    private String UserName;
    private String Password;
    private String FilterDocumentNo;

    public ItemReclassificationListSearchParameter(String userCompany, String userName, String password, String filterDocumentNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        FilterDocumentNo = filterDocumentNo;
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

    public String getFilterDocumentNo() {
        return FilterDocumentNo;
    }

    public void setFilterDocumentNo(String filterDocumentNo) {
        FilterDocumentNo = filterDocumentNo;
    }
}
