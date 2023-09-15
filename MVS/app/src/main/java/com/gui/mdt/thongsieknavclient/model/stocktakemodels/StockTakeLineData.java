package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/28/2016.
 */

public class StockTakeLineData {

    private int StockTakeEntryLineNo;
    private String DocumentNo;
    private String ItemNo;
    private String PostingDate;
    private String LocationCode;
    private String BinCode;
    private String Description;
    private float QuantityPhysical;
    private float QuantityCalculated;
    private String Uom;
    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private boolean IsItemExpireDateRequired;
    private boolean IsItemProductionDateRequired;
    private boolean IsItemSNRequired;
    private boolean DoNotShowSystemQty;

    public StockTakeLineData(int stockTakeEntryLineNo, String documentNo, String itemNo, String postingDate, String locationCode, String binCode, String description, float quantityPhysical, float quantityCalculated, String uom, boolean isItemTrackingRequired, boolean isItemLotTrackingRequired, boolean isItemExpireDateRequired, boolean isItemProductionDateRequired, boolean isItemSNRequired, boolean doNotShowSystemQty) {
        StockTakeEntryLineNo = stockTakeEntryLineNo;
        DocumentNo = documentNo;
        ItemNo = itemNo;
        PostingDate = postingDate;
        LocationCode = locationCode;
        BinCode = binCode;
        Description = description;
        QuantityPhysical = quantityPhysical;
        QuantityCalculated = quantityCalculated;
        Uom = uom;
        IsItemTrackingRequired = isItemTrackingRequired;
        IsItemLotTrackingRequired = isItemLotTrackingRequired;
        IsItemExpireDateRequired = isItemExpireDateRequired;
        IsItemProductionDateRequired = isItemProductionDateRequired;
        IsItemSNRequired = isItemSNRequired;
        DoNotShowSystemQty = doNotShowSystemQty;
    }

    public int getStockTakeEntryLineNo() {
        return StockTakeEntryLineNo;
    }

    public void setStockTakeEntryLineNo(int stockTakeEntryLineNo) {
        StockTakeEntryLineNo = stockTakeEntryLineNo;
    }

    public String getDocumentNo() {
        return DocumentNo;
    }

    public void setDocumentNo(String documentNo) {
        DocumentNo = documentNo;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getPostingDate() {
        return PostingDate;
    }

    public void setPostingDate(String postingDate) {
        PostingDate = postingDate;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public String getBinCode() {
        return BinCode;
    }

    public void setBinCode(String binCode) {
        BinCode = binCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public float getQuantityPhysical() {
        return QuantityPhysical;
    }

    public void setQuantityPhysical(float quantityPhysical) {
        QuantityPhysical = quantityPhysical;
    }

    public float getQuantityCalculated() {
        return QuantityCalculated;
    }

    public void setQuantityCalculated(float quantityCalculated) {
        QuantityCalculated = quantityCalculated;
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

    public boolean isDoNotShowSystemQty() {
        return DoNotShowSystemQty;
    }

    public void setDoNotShowSystemQty(boolean doNotShowSystemQty) {
        DoNotShowSystemQty = doNotShowSystemQty;
    }
}


