package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 28/09/16.
 */
public class FinishedGoodsProductionOrderListResultData {
    private String ItemDescription;
    private String ItemNo;
    private String LineNo;
    private String FinishedQuantity;
    private String BaseUom;
    private String OrderQuantity;
    private String RemainingQuantity;
    private String ProductionOrderNo;
    private String ProductionDate;
    private boolean IsRoutingRequired;

    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private boolean IsItemExpireDateRequired;
    private boolean IsItemProductionDateRequired;
    private boolean IsItemSNRequired;
    private boolean IsItemLotNoAutoAssigned;
    private String FinishedQuantityBase;
    private String OrderQuantityBase;
    private String RemainingQuantityBase;
    private float QuantityPer;
    private String LastLotNo;

    public FinishedGoodsProductionOrderListResultData(FinishedGoodsProductionOrderListResultData targetData)
    {
        this.ItemDescription = targetData.getItemDescription();
        this.ItemNo = targetData.getItemNo();
        this.LineNo = targetData.getLineNo();
        this.FinishedQuantity = targetData.getFinishedQuantity();
        this.BaseUom = targetData.getBaseUom();
        this.OrderQuantity = targetData.getOrderQuantity();
        this.RemainingQuantity = targetData.getRemainingQuantity();
        this.ProductionOrderNo = targetData.getProductionOrderNo();
        this.ProductionDate = targetData.getProductionDate();
        this.IsRoutingRequired = targetData.isRoutingRequired();

        this.IsItemTrackingRequired = targetData.isItemLotNoAutoAssigned();
        this.IsItemLotTrackingRequired = targetData.isItemLotTrackingRequired();
        this.IsItemExpireDateRequired = targetData.isItemExpireDateRequired();
        this.IsItemProductionDateRequired = targetData.isItemProductionDateRequired();
        this.IsItemSNRequired = targetData.isItemSNRequired();
        this.IsItemLotNoAutoAssigned = targetData.isItemLotNoAutoAssigned();

        this.FinishedQuantityBase = targetData.getFinishedQuantityBase();
        this.OrderQuantityBase = targetData.getOrderQuantityBase();
        this.RemainingQuantityBase = targetData.getRemainingQuantityBase();
        this.QuantityPer = targetData.getQuantityPer();
        this.LastLotNo = targetData.getLastLotNo();
    }

    public String getLastLotNo() {
        return LastLotNo;
    }

    public void setLastLotNo(String lastLotNo) {
        LastLotNo = lastLotNo;
    }

    public float getQuantityPer() {
        return QuantityPer;
    }

    public void setQuantityPer(float quantityPer) {
        QuantityPer = quantityPer;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getLineNo() {
        return LineNo;
    }

    public void setLineNo(String lineNo) {
        LineNo = lineNo;
    }

    public String getFinishedQuantity() {
        return FinishedQuantity;
    }

    public void setFinishedQuantity(String finishedQuantity) {
        FinishedQuantity = finishedQuantity;
    }

    public String getBaseUom() {
        return BaseUom;
    }

    public void setBaseUom(String baseUom) {
        BaseUom = baseUom;
    }

    public String getOrderQuantity() {
        return OrderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
        OrderQuantity = orderQuantity;
    }

    public String getRemainingQuantity() {
        return RemainingQuantity;
    }

    public void setRemainingQuantity(String remainingQuantity) {
        RemainingQuantity = remainingQuantity;
    }

    public String getProductionOrderNo() {
        return ProductionOrderNo;
    }

    public void setProductionOrderNo(String productionOrderNo) {
        ProductionOrderNo = productionOrderNo;
    }

    public String getProductionDate() {
        return ProductionDate;
    }

    public void setProductionDate(String productionDate) {
        ProductionDate = productionDate;
    }

    public boolean isRoutingRequired() {
        return IsRoutingRequired;
    }

    public void setRoutingRequired(boolean routingRequired) {
        IsRoutingRequired = routingRequired;
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

    public boolean isItemExpireDateRequired() {
        return IsItemExpireDateRequired;
    }

    public void setItemExpireDateRequired(boolean itemExpireDateRequired) {
        IsItemExpireDateRequired = itemExpireDateRequired;
    }

    public boolean isItemProductionDateRequired() {
        return IsItemProductionDateRequired;
    }

    public void setItemProductionDateRequired(boolean itemProductionDateRequired) {
        IsItemProductionDateRequired = itemProductionDateRequired;
    }

    public boolean isItemSNRequired() {
        return IsItemSNRequired;
    }

    public void setItemSNRequired(boolean itemSNRequired) {
        IsItemSNRequired = itemSNRequired;
    }

    public boolean isItemLotNoAutoAssigned() {
        return IsItemLotNoAutoAssigned;
    }

    public void setItemLotNoAutoAssigned(boolean itemLotNoAutoAssigned) {
        IsItemLotNoAutoAssigned = itemLotNoAutoAssigned;
    }

    public String getFinishedQuantityBase() {
        return FinishedQuantityBase;
    }

    public void setFinishedQuantityBase(String finishedQuantityBase) {
        FinishedQuantityBase = finishedQuantityBase;
    }

    public String getOrderQuantityBase() {
        return OrderQuantityBase;
    }

    public void setOrderQuantityBase(String orderQuantityBase) {
        OrderQuantityBase = orderQuantityBase;
    }

    public String getRemainingQuantityBase() {
        return RemainingQuantityBase;
    }

    public void setRemainingQuantityBase(String remainingQuantityBase) {
        RemainingQuantityBase = remainingQuantityBase;
    }
}
