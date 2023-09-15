package com.gui.mdt.thongsieknavclient.model.transferinmodels;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class ValidateTransferReceiptLineLotNoParameter {
    private String UserCompany;
    private String Username;
    private String Password;
    private String TransferReceiptNo;
    private int LineNo;
    private String LotNo;

    public ValidateTransferReceiptLineLotNoParameter(String userCompany, String username, String password, String transferReceiptNo, int lineNo, String lotNo) {
        UserCompany = userCompany;
        Username = username;
        Password = password;
        TransferReceiptNo = transferReceiptNo;
        LineNo = lineNo;
        LotNo = lotNo;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getTransferReceiptNo() {
        return TransferReceiptNo;
    }

    public void setTransferReceiptNo(String transferReceiptNo) {
        TransferReceiptNo = transferReceiptNo;
    }

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }
}
