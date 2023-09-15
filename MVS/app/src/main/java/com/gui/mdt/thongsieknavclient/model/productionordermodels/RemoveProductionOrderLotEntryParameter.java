package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 27/09/16.
 */
public class RemoveProductionOrderLotEntryParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String ProductionOrderNo;
    private int ProductionOrderLineNo;
    private int LineNo;
    private int EntryNo;
    private String LotSerialNo;
    private Float QuantityToDeduct;

    public RemoveProductionOrderLotEntryParameter(String userCompany, String userName, String password, String productionOrderNo, int productionOrderLineNo, int lineNo, int entryNo, String lotSerialNo, float quantityToDeduct) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ProductionOrderNo = productionOrderNo;
        ProductionOrderLineNo = productionOrderLineNo;
        LineNo = lineNo;
        EntryNo = entryNo;
        LotSerialNo = lotSerialNo;
        QuantityToDeduct = quantityToDeduct;
    }

    public int getProductionOrderLineNo() {
        return ProductionOrderLineNo;
    }

    public void setProductionOrderLineNo(int productionOrderLineNo) {
        ProductionOrderLineNo = productionOrderLineNo;
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
