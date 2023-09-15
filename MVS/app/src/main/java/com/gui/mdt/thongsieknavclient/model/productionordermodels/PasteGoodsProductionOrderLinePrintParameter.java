package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by user on 11/29/2016.
 */

public class PasteGoodsProductionOrderLinePrintParameter {

    private String UserCompany;
    private String UserName;
    private String Password;
    private String ProductionOrderNo;
    private String LineNo;
    private String ItemNo;
    private String LotNo;
    private int QuantityBase;

    public PasteGoodsProductionOrderLinePrintParameter(String userCompany, String userName, String password, String productionOrderNo, String lineNo, String itemNo, String lotNo, int quantityBase) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ProductionOrderNo = productionOrderNo;
        LineNo = lineNo;
        ItemNo = itemNo;
        LotNo = lotNo;
        QuantityBase = quantityBase;
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

    public String getLineNo() {
        return LineNo;
    }

    public void setLineNo(String lineNo) {
        LineNo = lineNo;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public int getQuantityBase() {
        return QuantityBase;
    }

    public void setQuantityBase(int quantityBase) {
        QuantityBase = quantityBase;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }
}