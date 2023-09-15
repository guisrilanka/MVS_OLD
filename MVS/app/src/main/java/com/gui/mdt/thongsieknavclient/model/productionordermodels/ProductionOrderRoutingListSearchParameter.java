package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 15/09/16.
 */
public class ProductionOrderRoutingListSearchParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String ProductionOrderNo;
    private String LineNo;
    private String ItemNo;
    private String OperationNo;
    private String RoutingRefNo;
    private float OrderQuantityBase;
    private float FinishedQuantityBase ;
    private float RemainingQuantityBase ;

    public ProductionOrderRoutingListSearchParameter(String userCompany, String userName, String password, String productionOrderNo, String lineNo, String itemNo, String operationNo,String routingRefNo,float orderQtyBase,float finishQtyBase,float remainQtyBase) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ProductionOrderNo = productionOrderNo;
        LineNo = lineNo;
        ItemNo = itemNo;
        OperationNo = operationNo;
        RoutingRefNo = routingRefNo;
        OrderQuantityBase = orderQtyBase;
        FinishedQuantityBase = finishQtyBase;
        RemainingQuantityBase = remainQtyBase;
    }

    public float getOrderQuantityBase() {
        return OrderQuantityBase;
    }

    public void setOrderQuantityBase(float orderQuantityBase) {
        OrderQuantityBase = orderQuantityBase;
    }

    public float getFinishedQuantityBase() {
        return FinishedQuantityBase;
    }

    public void setFinishedQuantityBase(float finishedQuantityBase) {
        FinishedQuantityBase = finishedQuantityBase;
    }

    public float getRemainingQuantityBase() {
        return RemainingQuantityBase;
    }

    public void setRemainingQuantityBase(float remainingQuantityBase) {
        RemainingQuantityBase = remainingQuantityBase;
    }

    public String getRoutingRefNo() {
        return RoutingRefNo;
    }

    public void setRoutingRefNo(String routingRefNo) {
        RoutingRefNo = routingRefNo;
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
}
