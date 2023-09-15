package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 01/09/2018.
 */

public class ApiMobileSalesOrderLineParameter {

    String Document_No;
    String UserName;
    String Password;
    String UserCompany;

    public String getDocument_No() {
        return Document_No;
    }

    public void setDocument_No(String document_No) {
        Document_No = document_No;
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
}
