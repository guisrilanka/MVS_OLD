package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 15/09/16.
 */
public class ProductionOrderBomListResultData {

    private String ProductionOrderNo;
    private String ItemDescription;
    private String ItemNo;
    private String LineNo;
    private String ExpectedQuantity;
    private String QuantityToConsumed;
    private String RemainingQuantity;
    private String BaseUom;
    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private boolean IsItemExpireDateRequired;
    private boolean IsItemProductionDateRequired;
    private boolean IsItemSNRequired;
    private boolean IsItemLotNoAutoAssigned;
    private boolean IsAllowChangeQty;
    private String ExpectedQuantityBase;
    private String QuantityToConsumedBase;
    private String RemainingQuantityBase;
    private boolean BarCodeScanned;
    private float QuantityPer;

    public ProductionOrderBomListResultData(ProductionOrderBomListResultData lineData)
    {
        this.ProductionOrderNo = lineData.getProductionOrderNo();
        this.ItemDescription = lineData.getItemDescription();
        this.ItemNo = lineData.getItemNo();
        this.LineNo = lineData.getLineNo();
        this.ExpectedQuantity = lineData.getExpectedQuantity();
        this.QuantityToConsumed = lineData.getQuantityToConsumed();
        this.RemainingQuantity = lineData.getRemainingQuantity();
        this.BaseUom = lineData.getBaseUom();
        this.IsItemTrackingRequired = lineData.isItemTrackingRequired();
        this.IsItemLotTrackingRequired = lineData.isItemLotTrackingRequired();
        this.IsItemExpireDateRequired = lineData.isItemExpireDateRequired();
        this.IsItemProductionDateRequired = lineData.isItemProductionDateRequired();
        this.IsItemSNRequired = lineData.isItemSNRequired();
        this.IsItemLotNoAutoAssigned = lineData.isItemLotNoAutoAssigned();
        this.IsAllowChangeQty = lineData.isAllowChangeQty();
        this.ExpectedQuantityBase = lineData.getExpectedQuantityBase();
        this.QuantityToConsumedBase = lineData.getQuantityToConsumedBase();
        this.RemainingQuantityBase = lineData.getRemainingQuantityBase();
        this.BarCodeScanned = lineData.isBarCodeScanned();
        this.QuantityPer = lineData.getQuantityPer();

    }

    public float getQuantityPer() {
        return QuantityPer;
    }

    public void setQuantityPer(float quantityPer) {
        QuantityPer = quantityPer;
    }

    public String getProductionOrderNo() {
        return ProductionOrderNo;
    }

    public void setProductionOrderNo(String productionOrderNo) {
        ProductionOrderNo = productionOrderNo;
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

    public String getExpectedQuantity() {
        return ExpectedQuantity;
    }

    public void setExpectedQuantity(String expectedQuantity) {
        ExpectedQuantity = expectedQuantity;
    }

    public String getQuantityToConsumed() {
        return QuantityToConsumed;
    }

    public void setQuantityToConsumed(String quantityToConsumed) {
        QuantityToConsumed = quantityToConsumed;
    }

    public String getRemainingQuantity() {
        return RemainingQuantity;
    }

    public void setRemainingQuantity(String remainingQuantity) {
        RemainingQuantity = remainingQuantity;
    }

    public String getBaseUom() {
        return BaseUom;
    }

    public void setBaseUom(String baseUom) {
        BaseUom = baseUom;
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

    public boolean isAllowChangeQty() {
        return IsAllowChangeQty;
    }

    public void setAllowChangeQty(boolean allowChangeQty) {
        IsAllowChangeQty = allowChangeQty;
    }

    public String getExpectedQuantityBase() {
        return ExpectedQuantityBase;
    }

    public void setExpectedQuantityBase(String expectedQuantityBase) {
        ExpectedQuantityBase = expectedQuantityBase;
    }

    public String getQuantityToConsumedBase() {
        return QuantityToConsumedBase;
    }

    public void setQuantityToConsumedBase(String quantityToConsumedBase) {
        QuantityToConsumedBase = quantityToConsumedBase;
    }

    public String getRemainingQuantityBase() {
        return RemainingQuantityBase;
    }

    public void setRemainingQuantityBase(String remainingQuantityBase) {
        RemainingQuantityBase = remainingQuantityBase;
    }

    public boolean isBarCodeScanned() {
        return BarCodeScanned;
    }

    public void setBarCodeScanned(boolean barCodeScanned) {
        BarCodeScanned = barCodeScanned;
    }
}
