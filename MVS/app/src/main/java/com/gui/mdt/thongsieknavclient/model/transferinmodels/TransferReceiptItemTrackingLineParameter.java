package com.gui.mdt.thongsieknavclient.model.transferinmodels;

/**
 * Created by yeqim_000 on 10/10/16.
 */
public class TransferReceiptItemTrackingLineParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String TransferNo;
    private int LineNo;
    private int EntryNo;
    private float QuantityToReceipt;

    public TransferReceiptItemTrackingLineParameter(String userCompany, String userName, String password, String transferNo, int lineNo, int entryNo, float quantityToReceipt) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        TransferNo = transferNo;
        LineNo = lineNo;
        EntryNo = entryNo;
        QuantityToReceipt = quantityToReceipt;
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

    public int getEntryNo() {
        return EntryNo;
    }

    public void setEntryNo(int entryNo) {
        EntryNo = entryNo;
    }

    public float getQuantityToReceipt() {
        return QuantityToReceipt;
    }

    public void setQuantityToReceipt(float quantityToReceipt) {
        QuantityToReceipt = quantityToReceipt;
    }
}
