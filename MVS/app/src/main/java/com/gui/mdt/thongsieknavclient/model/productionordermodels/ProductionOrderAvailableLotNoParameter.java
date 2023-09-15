package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 28/09/16.
 */
public class ProductionOrderAvailableLotNoParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String ProductionOrderNo;
    private int ProductionOrderLineNo;
    private int LineNo;

    public ProductionOrderAvailableLotNoParameter(String userCompany, String userName, String password, String productionOrderNo, int productionOrderLineNo, int lineNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ProductionOrderNo = productionOrderNo;
        ProductionOrderLineNo = productionOrderLineNo;
        LineNo = lineNo;
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
}
