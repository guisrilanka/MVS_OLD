package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/29/2016.
 */

public class ItemReclassLineLotEntriesParameter {

    private String UserCompany;
    private String UserName;
    private String Password;
    private String DocumentNo;
    private int LineNo;

    public ItemReclassLineLotEntriesParameter(String userCompany, String userName, String password, String documentNo, int lineNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        DocumentNo = documentNo;
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

    public String getDocumentNo() {
        return DocumentNo;
    }

    public void setDocumentNo(String documentNo) {
        DocumentNo = documentNo;
    }

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }
}
