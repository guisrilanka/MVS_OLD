package com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class WarehouseShipmentLineData {
    private String ItemDescription;
    private String ItemNo;
    private String LineNo;
    private String Quantity;
    private String QuantityToShip;
    private String BaseUom;
    private String Uom;
    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private boolean IsItemExpireDateRequired;
    private boolean IsItemProductionDateRequired;
    private boolean IsItemSNRequired;
    private boolean IsItemLotNoAutoAssigned;
    private String QuantityBase;
    private String QuantityToShipBase;

    public WarehouseShipmentLineData(WarehouseShipmentLineData lineData)
    {
        this.ItemDescription = lineData.getItemDescription();
        this.ItemNo = lineData.getItemNo();
        this.LineNo = lineData.getLineNo();
        this.Quantity = lineData.getQuantity();
        this.QuantityToShip = lineData.getQuantityToShip();
        this.BaseUom = lineData.getBaseUom();
        this.Uom = lineData.getUom();
        this.IsItemTrackingRequired = lineData.isItemTrackingRequired();
        this.IsItemLotTrackingRequired = lineData.isItemLotTrackingRequired();
        this.IsItemExpireDateRequired = lineData.isItemExpireDateRequired();
        this.IsItemProductionDateRequired = lineData.isItemProductionDateRequired();
        this.IsItemSNRequired = lineData.isItemSNRequired();
        this.IsItemLotNoAutoAssigned = lineData.isItemLotNoAutoAssigned();
        this.QuantityBase = lineData.getQuantityBase();
        this.QuantityToShipBase = lineData.getQuantityToShipBase();
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

    public String getQuantityToShip() {
        return QuantityToShip;
    }

    public void setQuantityToShip(String quantityToShip) {
        QuantityToShip = quantityToShip;
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

    public String getQuantityBase() {
        return QuantityBase;
    }

    public void setQuantityBase(String quantityBase) {
        QuantityBase = quantityBase;
    }

    public String getQuantityToShipBase() {
        return QuantityToShipBase;
    }

    public void setQuantityToShipBase(String quantityToShipBase) {
        QuantityToShipBase = quantityToShipBase;
    }
}
