package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/8/2016.
 */

public class RemoveItemReclassLotEntryParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private int LineNo;
    private String LotSerialNo;
    private float QuantityToDeduct;

    public RemoveItemReclassLotEntryParameter(String userCompany, String userName, String password, int lineNo, String lotSerialNo, float quantityToDeduct) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        LineNo = lineNo;
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

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
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
