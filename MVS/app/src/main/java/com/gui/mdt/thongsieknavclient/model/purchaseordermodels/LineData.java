package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

/**
 * Created by yeqim_000 on 26/07/16.
 */
public class LineData {
    private String ItemDescription;
    private String ItemNo;
    private String LineNo;
    private String Quantity;
    private String OutstandingQuantity;
    private String QuantityReceived;
    private String QuantityToReceive;
    private String BaseUom;
    private String Uom;
    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private boolean IsItemExpireDateRequired;
    private boolean IsItemProductionDateRequired;
    private boolean IsItemSNRequired;
    private boolean IsItemLotNoAutoAssigned;
    private String QuantityBase;
    private String QuantityReceivedBase;
    private String OutstandingQuantityBase;
    private String QuantityToReceiveBase;
    private int NoOfLabel;

    public LineData(LineData lineData)
    {
        this.ItemDescription = lineData.getItemDescription();
        this.ItemNo = lineData.getItemNo();
        this.LineNo = lineData.getLineNo();
        this.Quantity = lineData.getQuantity();
        this.OutstandingQuantity = lineData.getOutstandingQuantity();
        this.QuantityToReceive = lineData.getQuantityToReceive();
        this.QuantityReceived = lineData.getQuantityReceived();
        this.BaseUom = lineData.getBaseUom();
        this.Uom = lineData.getUom();
        this.IsItemTrackingRequired = lineData.getIsItemTrackingRequired();
        this.IsItemLotTrackingRequired = lineData.getIsItemLotTrackingRequired();
        this.IsItemExpireDateRequired = lineData.getIsItemExpireDateRequired();
        this.IsItemProductionDateRequired = lineData.getIsItemProductionDateRequired();
        this.IsItemSNRequired = lineData.getIsItemSNRequired();
        this.IsItemLotNoAutoAssigned = lineData.getIsItemLotNoAutoAssigned();
        this.QuantityBase = lineData.getQuantityBase();
        this.QuantityReceivedBase = lineData.getQuantityReceivedBase();
        this.OutstandingQuantityBase = lineData.getOutstandingQuantityBase();
        this.QuantityToReceiveBase = lineData.getQuantityToReceiveBase();
    }

    public int getNoOfLabel() {
        return NoOfLabel;
    }

    public void setNoOfLabel(int noOfLabel) {
        NoOfLabel = noOfLabel;
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

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getOutstandingQuantity() {
        return OutstandingQuantity;
    }

    public void setOutstandingQuantity(String outstandingQuantity) {
        OutstandingQuantity = outstandingQuantity;
    }

    public String getQuantityReceived() {
        return QuantityReceived;
    }

    public void setQuantityReceived(String quantityReceived) {
        QuantityReceived = quantityReceived;
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

    public boolean getIsItemTrackingRequired() {
        return IsItemTrackingRequired;
    }

    public void setIsItemTrackingRequired(boolean isItemTrackingRequired) {
        IsItemTrackingRequired = isItemTrackingRequired;
    }

    public boolean getIsItemLotTrackingRequired() {
        return IsItemLotTrackingRequired;
    }

    public void setIsItemLotTrackingRequired(boolean isItemLotTrackingRequired) {
        IsItemLotTrackingRequired = isItemLotTrackingRequired;
    }

    public boolean getIsItemExpireDateRequired() {
        return IsItemExpireDateRequired;
    }

    public void setIsItemExpireDateRequired(boolean isItemExpireDateRequired) {
        IsItemExpireDateRequired = isItemExpireDateRequired;
    }

    public boolean getIsItemProductionDateRequired() {
        return IsItemProductionDateRequired;
    }

    public void setIsItemProductionDateRequired(boolean isItemProductionDateRequired) {
        IsItemProductionDateRequired = isItemProductionDateRequired;
    }

    public boolean getIsItemSNRequired() {
        return IsItemSNRequired;
    }

    public void setIsItemSNRequired(boolean isItemSNRequired) {
        IsItemSNRequired = isItemSNRequired;
    }

    public boolean getIsItemLotNoAutoAssigned() {
        return IsItemLotNoAutoAssigned;
    }

    public void setIsItemLotNoAutoAssigned(boolean isItemLotNoAutoAssigned) {
        IsItemLotNoAutoAssigned = isItemLotNoAutoAssigned;
    }

    public String getQuantityBase() {
        return QuantityBase;
    }

    public void setQuantityBase(String quantityBase) {
        QuantityBase = quantityBase;
    }

    public String getQuantityReceivedBase() {
        return QuantityReceivedBase;
    }

    public void setQuantityReceivedBase(String quantityReceivedBase) {
        QuantityReceivedBase = quantityReceivedBase;
    }

    public String getOutstandingQuantityBase() {
        return OutstandingQuantityBase;
    }

    public void setOutstandingQuantityBase(String outstandingQuantityBase) {
        OutstandingQuantityBase = outstandingQuantityBase;
    }

    public String getQuantityToReceiveBase() {
        return QuantityToReceiveBase;
    }

    public void setQuantityToReceiveBase(String quantityToReceiveBase) {
        QuantityToReceiveBase = quantityToReceiveBase;
    }
}
