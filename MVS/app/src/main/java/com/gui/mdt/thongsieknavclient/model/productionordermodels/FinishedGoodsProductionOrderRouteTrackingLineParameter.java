package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 06/10/16.
 */
public class FinishedGoodsProductionOrderRouteTrackingLineParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String ProductionOrderNo;
    private int LineNo;
    private String OperationNo;
    private String ProductionDate;
    private String ExpireDate;
    private String LotNo;
    private String SerialNo;
    private Float QuantityProduced;

    public FinishedGoodsProductionOrderRouteTrackingLineParameter(String userCompany, String userName, String password, String productionOrderNo, int lineNo, String operationNo, String productionDate, String expireDate, String lotNo, String serialNo, Float quantityProduced) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ProductionOrderNo = productionOrderNo;
        LineNo = lineNo;
        OperationNo = operationNo;
        ProductionDate = productionDate;
        ExpireDate = expireDate;
        LotNo = lotNo;
        SerialNo = serialNo;
        QuantityProduced = quantityProduced;
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

    public String getProductionOrderNo() {
        return ProductionOrderNo;
    }

    public void setProductionOrderNo(String productionOrderNo) {
        ProductionOrderNo = productionOrderNo;
    }

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

    public String getOperationNo() {
        return OperationNo;
    }

    public void setOperationNo(String operationNo) {
        OperationNo = operationNo;
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

    public Float getQuantityProduced() {
        return QuantityProduced;
    }

    public void setQuantityProduced(Float quantityProduced) {
        QuantityProduced = quantityProduced;
    }
}
