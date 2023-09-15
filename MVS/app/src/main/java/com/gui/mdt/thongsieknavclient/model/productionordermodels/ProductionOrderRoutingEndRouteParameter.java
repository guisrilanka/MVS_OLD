package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 15/09/16.
 */
public class ProductionOrderRoutingEndRouteParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String ProductionOrderNo;
    private String LineNo;
    private String ItemNo;
    private String OperationNo;
    private float OutputQuantity;
    private float ScrapQuantity;

    public ProductionOrderRoutingEndRouteParameter(String userCompany, String userName, String password, String productionOrderNo, String lineNo, String itemNo, String operationNo, float outputQuantity, float scrapQuantity) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ProductionOrderNo = productionOrderNo;
        LineNo = lineNo;
        ItemNo = itemNo;
        OperationNo = operationNo;
        OutputQuantity = outputQuantity;
        ScrapQuantity = scrapQuantity;
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

    public String getOperationNo() {
        return OperationNo;
    }

    public void setOperationNo(String operationNo) {
        OperationNo = operationNo;
    }

    public float getOutputQuantity() {
        return OutputQuantity;
    }

    public void setOutputQuantity(float outputQuantity) {
        OutputQuantity = outputQuantity;
    }

    public float getScrapQuantity() {
        return ScrapQuantity;
    }

    public void setScrapQuantity(float scrapQuantity) {
        ScrapQuantity = scrapQuantity;
    }
}
