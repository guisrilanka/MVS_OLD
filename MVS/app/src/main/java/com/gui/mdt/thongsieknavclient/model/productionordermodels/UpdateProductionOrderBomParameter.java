package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 28/09/16.
 */
public class UpdateProductionOrderBomParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String ProductionOrderNo;
    private String ProductionOrderLineNo;
    private String LineNo;
    private float QuantityToConsumed;

    public UpdateProductionOrderBomParameter(String userCompany, String userName, String password, String productionOrderNo, String productionOrderLineNo, String lineNo, float quantityToConsumed) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ProductionOrderNo = productionOrderNo;
        ProductionOrderLineNo = productionOrderLineNo;
        LineNo = lineNo;
        QuantityToConsumed = quantityToConsumed;
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

    public String getProductionOrderLineNo() {
        return ProductionOrderLineNo;
    }

    public void setProductionOrderLineNo(String productionOrderLineNo) {
        ProductionOrderLineNo = productionOrderLineNo;
    }

    public String getLineNo() {
        return LineNo;
    }

    public void setLineNo(String lineNo) {
        LineNo = lineNo;
    }

    public float getQuantityToConsumed() {
        return QuantityToConsumed;
    }

    public void setQuantityToConsumed(float quantityToConsumed) {
        QuantityToConsumed = quantityToConsumed;
    }
}
