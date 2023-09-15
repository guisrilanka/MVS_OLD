package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 06/10/16.
 */
public class RemoveFinishedGoodsProductionOrderRouteLotEntryParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String ProductionOrderNo;
    private int LineNo;
    private String OperationNo;
    private String LotSerialNo;
    private Float QuantityToDeduct;

    public RemoveFinishedGoodsProductionOrderRouteLotEntryParameter(String userCompany, String userName, String password, String productionOrderNo, int lineNo, String operationNo, String lotSerialNo, Float quantityToDeduct) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ProductionOrderNo = productionOrderNo;
        LineNo = lineNo;
        OperationNo = operationNo;
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
