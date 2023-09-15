package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 1/19/2017.
 */

public class RemoveItemReclassLineParameter {

    private String UserCompany;
    private String UserName;
    private String Password;
    private int LineNo;

    public RemoveItemReclassLineParameter(String userCompany, String userName, String password, int lineNo) {
        UserCompany = userCompany;
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
