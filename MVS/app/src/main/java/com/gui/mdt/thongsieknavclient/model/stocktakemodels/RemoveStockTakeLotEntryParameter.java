package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/28/2016.
 */

public class RemoveStockTakeLotEntryParameter {

    private String UserCompany;
    private String UserName;
    private String Password;
    private int StockTakeDetailLineNo;
    private String LotSerialNo;
    private float QuantityToDeduct;

    public RemoveStockTakeLotEntryParameter(String userCompany, String userName, String password, int stockTakeDetailLineNo, String lotSerialNo, float quantityToDeduct) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        StockTakeDetailLineNo = stockTakeDetailLineNo;
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

    public int getStockTakeDetailLineNo() {
        return StockTakeDetailLineNo;
    }

    public void setStockTakeDetailLineNo(int stockTakeDetailLineNo) {
        StockTakeDetailLineNo = stockTakeDetailLineNo;
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
