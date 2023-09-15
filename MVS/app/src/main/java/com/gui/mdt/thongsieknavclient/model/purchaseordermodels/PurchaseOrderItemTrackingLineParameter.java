package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

/**
 * Created by yeqim_000 on 15/08/16.
 */
public class PurchaseOrderItemTrackingLineParameter {

    private String UserCompany;
    private String Password;
    private String UserName;
    private String PurchaseOrderNo;
    private int LineNo;
    private String ProductionDate;
    private String ExpireDate;
    private String LotNo;
    private String SerialNo;
    private Float ReceivedQuantity;
    private boolean IsScanLabel;

    public PurchaseOrderItemTrackingLineParameter(String userCompany, String userName, String password, String purchaseOrderNo, int lineNo, String productionDate, String expireDate, String lotNo, String serialNo, float receivedQuantity, boolean isScanLabel) {
        UserCompany = userCompany;
        Password = password;
        UserName = userName;
        PurchaseOrderNo = purchaseOrderNo;
        LineNo = lineNo;
        ProductionDate = productionDate;
        ExpireDate = expireDate;
        LotNo = lotNo;
        SerialNo = serialNo;
        ReceivedQuantity = receivedQuantity;
        IsScanLabel = isScanLabel;
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

    public String getPurchaseOrderNo() {
        return PurchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        PurchaseOrderNo = purchaseOrderNo;
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

    public float getReceivedQuantity() {
        return ReceivedQuantity;
    }

    public void setReceivedQuantity(float receivedQuantity) {
        ReceivedQuantity = receivedQuantity;
    }

    public boolean getIsScanLabel() {
        return IsScanLabel;
    }

    public void setIsScanLabel(boolean isScanLabel) {
        IsScanLabel = isScanLabel;
    }
}
