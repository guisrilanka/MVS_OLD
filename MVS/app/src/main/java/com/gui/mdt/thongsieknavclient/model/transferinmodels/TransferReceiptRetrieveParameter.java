package com.gui.mdt.thongsieknavclient.model.transferinmodels;

/**
 * Created by yeqim_000 on 05/09/16.
 */
public class TransferReceiptRetrieveParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String TransferNo;

    public TransferReceiptRetrieveParameter(String userCompany, String userName, String password, String transferNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        TransferNo = transferNo;
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

    public String getTransferNo() {
        return TransferNo;
    }

    public void setTransferNo(String transferNo) {
        TransferNo = transferNo;
    }
}
