package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/28/2016.
 */

public class StockTakeItemTrackingLineParameter {

    private String UserCompany;
    private String Password;
    private String UserName;

    private int StockTakeDetailLineNo;
    private String ProductionDate;
    private String ExpireDate;
    private String LotNo;
    private String SerialNo;
    private float Quantity;
    private boolean IsScanLabel;

    public StockTakeItemTrackingLineParameter(String userCompany, String userName, String password, int stockTakeDetailLineNo, String productionDate, String expireDate, String lotNo, String serialNo, float quantity, boolean isScanLabel) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        StockTakeDetailLineNo = stockTakeDetailLineNo;
        ProductionDate = productionDate;
        ExpireDate = expireDate;
        LotNo = lotNo;
        SerialNo = serialNo;
        Quantity = quantity;
        IsScanLabel = isScanLabel;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getStockTakeDetailLineNo() {
        return StockTakeDetailLineNo;
    }

    public void setStockTakeDetailLineNo(int stockTakeDetailLineNo) {
        StockTakeDetailLineNo = stockTakeDetailLineNo;
    }

    public String getProductionDate() {
        return ProductionDate;
    }

    public void setProductionDate(String productionDate) {
        ProductionDate = productionDate;
    }

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public boolean isScanLabel() {
        return IsScanLabel;
    }

    public void setScanLabel(boolean scanLabel) {
        IsScanLabel = scanLabel;
    }
}
