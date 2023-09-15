package com.gui.mdt.thongsieknavclient.model.transferoutmodels;

/**
 * Created by yeqim_000 on 02/09/16.
 */
public class TransferShipmentItemTrackingLineParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String TransferNo;
    private int LineNo;
    private String ProductionDate;
    private String ExpireDate;
    private String LotNo;
    private String SerialNo;
    private Float QuantityToShip;

    public TransferShipmentItemTrackingLineParameter(String userCompany, String userName, String password, String transferNo, int lineNo, String productionDate, String expireDate, String lotNo, String serialNo, Float quantityToShip) {
        UserCompany = userCompany;
        Password = password;
        UserName = userName;
        TransferNo = transferNo;
        LineNo = lineNo;
        ProductionDate = productionDate;
        ExpireDate = expireDate;
        LotNo = lotNo;
        SerialNo = serialNo;
        QuantityToShip = quantityToShip;
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

    public Float getQuantityToShip() {
        return QuantityToShip;
    }

    public void setQuantityToShip(Float quantityToShip) {
        QuantityToShip = quantityToShip;
    }
}
