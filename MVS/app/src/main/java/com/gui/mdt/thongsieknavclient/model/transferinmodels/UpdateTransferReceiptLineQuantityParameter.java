package com.gui.mdt.thongsieknavclient.model.transferinmodels;

/**
 * Created by yeqim_000 on 05/09/16.
 */
public class UpdateTransferReceiptLineQuantityParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String TransferNo;
    private int LineNo;
    private Float QuantityToReceipt;
    private boolean IsUpdateBase;

    public UpdateTransferReceiptLineQuantityParameter(String userCompany, String userName, String password, String transferNo, int lineNo, Float quantityToReceipt, boolean isUpdateBase) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        TransferNo = transferNo;
        LineNo = lineNo;
        QuantityToReceipt = quantityToReceipt;
        IsUpdateBase = isUpdateBase;
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

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

    public Float getQuantityToReceipt() {
        return QuantityToReceipt;
    }

    public void setQuantityToReceipt(Float quantityToReceipt) {
        QuantityToReceipt = quantityToReceipt;
    }

    public boolean isUpdateBase() {
        return IsUpdateBase;
    }

    public void setUpdateBase(boolean updateBase) {
        IsUpdateBase = updateBase;
    }
}
