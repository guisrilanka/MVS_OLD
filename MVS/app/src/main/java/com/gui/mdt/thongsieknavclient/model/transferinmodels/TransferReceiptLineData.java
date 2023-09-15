package com.gui.mdt.thongsieknavclient.model.transferinmodels;

/**
 * Created by yeqim_000 on 05/09/16.
 */
public class TransferReceiptLineData {

    private String ItemDescription;
    private String ItemNo;
    private String LineNo;
    private String OrderQuantity;
    private String OutstandingQuantity;
    private String QuantityInTransit;
    private String QuantityToReceive;
    private String BaseUom;
    private String Uom;
    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private boolean IsItemExpireDateRequired;
    private boolean IsItemProductionDateRequired;
    private boolean IsItemSNRequired;
    private boolean IsItemLotNoAutoAssigned;
    private String OrderQuantityBase;
    private String OutstandingQuantityBase;
    private String QuantityInTransitBase;
    private String QuantityToReceiveBase;

    public TransferReceiptLineData(TransferReceiptLineData lineData)
    {
        this.ItemDescription = lineData.getItemDescription();
        this.ItemNo = lineData.getItemNo();
        this.LineNo = lineData.getLineNo();
        this.OrderQuantity = lineData.getOrderQuantity();
        this.OutstandingQuantity = lineData.getOutstandingQuantity();
        this.QuantityInTransit = lineData.getQuantityInTransit();
        this.QuantityToReceive = lineData.getQuantityToReceive();
        this.BaseUom = lineData.getBaseUom();
        this.Uom = lineData.getUom();
        this.IsItemTrackingRequired = lineData.isItemTrackingRequired();
        this.IsItemLotTrackingRequired = lineData.isItemLotTrackingRequired();
        this.IsItemExpireDateRequired = lineData.isItemExpireDateRequired();
        this.IsItemProductionDateRequired = lineData.isItemProductionDateRequired();
        this.IsItemSNRequired = lineData.isItemSNRequired();
        this.IsItemLotNoAutoAssigned = lineData.isItemLotNoAutoAssigned();
        this.OrderQuantityBase = lineData.getOrderQuantityBase();
        this.OutstandingQuantityBase = lineData.getOutstandingQuantityBase();
        this.QuantityInTransitBase = lineData.getQuantityInTransitBase();
        this.QuantityToReceiveBase = lineData.getQuantityToReceiveBase();
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

    public String getOrderQuantity() {
        return OrderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
        OrderQuantity = orderQuantity;
    }

    public String getOutstandingQuantity() {
        return OutstandingQuantity;
    }

    public void setOutstandingQuantity(String outstandingQuantity) {
        OutstandingQuantity = outstandingQuantity;
    }

    public String getQuantityInTransit() {
        return QuantityInTransit;
    }

    public void setQuantityInTransit(String quantityInTransit) {
        QuantityInTransit = quantityInTransit;
    }

    public String getQuantityToReceive() {
        return QuantityToReceive;
    }

    public void setQuantityToReceive(String quantityToReceive) {
        QuantityToReceive = quantityToReceive;
    }

    public String getBaseUom() {
        return BaseUom;
    }

    public void setBaseUom(String baseUom) {
        BaseUom = baseUom;
    }

    public String getUom() {
        return Uom;
    }

    public void setUom(String uom) {
        Uom = uom;
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

    public String getOrderQuantityBase() {
        return OrderQuantityBase;
    }

    public void setOrderQuantityBase(String orderQuantityBase) {
        OrderQuantityBase = orderQuantityBase;
    }

    public String getOutstandingQuantityBase() {
        return OutstandingQuantityBase;
    }

    public void setOutstandingQuantityBase(String outstandingQuantityBase) {
        OutstandingQuantityBase = outstandingQuantityBase;
    }

    public String getQuantityInTransitBase() {
        return QuantityInTransitBase;
    }

    public void setQuantityInTransitBase(String quantityInTransitBase) {
        QuantityInTransitBase = quantityInTransitBase;
    }

    public String getQuantityToReceiveBase() {
        return QuantityToReceiveBase;
    }

    public void setQuantityToReceiveBase(String quantityToReceiveBase) {
        QuantityToReceiveBase = quantityToReceiveBase;
    }
}
