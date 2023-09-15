package com.gui.mdt.thongsieknavclient.model.transferoutmodels;

/**
 * Created by yeqim_000 on 05/09/16.
 */
public class RemoveTransferShipmentLotEntryParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String TransferShipmentNo;
    private int LineNo;
    private int EntryNo;
    private String LotSerialNo;
    private Float QuantityToDeduct;

    public RemoveTransferShipmentLotEntryParameter(String userCompany, String userName, String password, String transferShipmentNo, int lineNo, int entryNo, String lotSerialNo, float quantityToDeduct) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        TransferShipmentNo = transferShipmentNo;
        LineNo = lineNo;
        EntryNo = entryNo;
        LotSerialNo = lotSerialNo;
        QuantityToDeduct = quantityToDeduct;
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

    public String getTransferShipmentNo() {
        return TransferShipmentNo;
    }

    public void setTransferShipmentNo(String transferShipmentNo) {
        TransferShipmentNo = transferShipmentNo;
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

    public String getLotSerialNo() {
        return LotSerialNo;
    }

    public void setLotSerialNo(String lotSerialNo) {
        LotSerialNo = lotSerialNo;
    }

    public Float getQuantityToDeduct() {
        return QuantityToDeduct;
    }

    public void setQuantityToDeduct(Float quantityToDeduct) {
        QuantityToDeduct = quantityToDeduct;
    }
}
