package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 15/09/16.
 */
public class PasteGroupProductionOrderRoutingListResultData {
    private String Description;
    private String StartTime;
    private String EndTime;
    private String RoutingNo;
    private String OperationNo;
    private String RoutingRefNo;
    private String ProductionOrderType;
    private boolean IsLastRoute;
    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private float OrderQuantityBase;
    private float FinishedQuantityBase ;
    private float RemainingQuantityBase ;
    private String LastLotNo;

    public String getLastLotNo() {
        return LastLotNo;
    }

    public void setLastLotNo(String lastLotNo) {
        LastLotNo = lastLotNo;
    }

    public boolean isItemTrackingRequired() {
        return IsItemTrackingRequired;
    }

    public void setItemTrackingRequired(boolean itemTrackingRequired) {
        IsItemTrackingRequired = itemTrackingRequired;
    }

    public boolean isItemLotTrackingRequired() {
        return IsItemLotTrackingRequired;
    }

    public void setItemLotTrackingRequired(boolean itemLotTrackingRequired) {
        IsItemLotTrackingRequired = itemLotTrackingRequired;
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getOperationNo() {
        return OperationNo;
    }

    public void setOperationNo(String operationNo) {
        OperationNo = operationNo;
    }

    public String getProductionOrderType() {
        return ProductionOrderType;
    }

    public void setProductionOrderType(String productionOrderType) {
        ProductionOrderType = productionOrderType;
    }

    public boolean isLastRoute() {
        return IsLastRoute;
    }

    public void setLastRoute(boolean lastRoute) {
        IsLastRoute = lastRoute;
    }

    public String getRoutingNo() {
        return RoutingNo;
    }

    public void setRoutingNo(String routingNo) {
        RoutingNo = routingNo;
    }
}
