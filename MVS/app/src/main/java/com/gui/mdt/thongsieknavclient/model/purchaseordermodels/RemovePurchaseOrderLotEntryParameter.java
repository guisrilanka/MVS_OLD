package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

/**
 * Created by yeqim_000 on 19/08/16.
 */
public class RemovePurchaseOrderLotEntryParameter {
    private String UserCompany;
    private String PurchaseOrderNo;
    private String UserName;
    private String Password;
    private int LineNo;
    private int EntryNo;
    private String LotSerialNo;
    private float QuantityToDeduct;

    public RemovePurchaseOrderLotEntryParameter(String userCompany, String userName, String password, String purchaseOrderNo, int lineNo, int entryNo, String lotSerialNo, float quantityToDeduct) {
        UserCompany = userCompany;
        PurchaseOrderNo = purchaseOrderNo;
        UserName = userName;
        Password = password;
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

    public float getQuantityToDeduct() {
        return QuantityToDeduct;
    }

    public void setQuantityToDeduct(float quantityToDeduct) {
        QuantityToDeduct = quantityToDeduct;
    }
}
