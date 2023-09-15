package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/8/2016.
 */

public class ItemReclassLineData {

    private int LineNo;
    private String DocumentNo;
    private String BinCode;
    private String NewBinCode;
    private String ItemNo;
    private float Quantity;
    private float QuantityBase;
    private String Uom;
    private String ItemDescription;

    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private boolean IsItemExpireDateRequired;
    private boolean IsItemProductionDateRequired;
    private boolean IsItemSNRequired;

    public ItemReclassLineData(ItemReclassLineData lineData) {
        this.LineNo = lineData.getLineNo();
        this.DocumentNo = lineData.getDocNo();
        this.BinCode = lineData.getBinCode();
        this.NewBinCode = lineData.getNewBinCode();
        this.IsItemTrackingRequired = lineData.isItemTrackingRequired();
        this.IsItemLotTrackingRequired = lineData.isItemLotTrackingRequired();
        this.IsItemExpireDateRequired = lineData.isItemExpireDateRequired();
        this.IsItemProductionDateRequired = lineData.isItemProductionDateRequired();
        this.IsItemSNRequired = lineData.isItemSNRequired();

        this.ItemNo = lineData.getItemNo();
        this.Uom = lineData.getUom();
        this.ItemDescription = lineData.getItemDescription();
        this.Quantity = lineData.getQuantity();
        this.QuantityBase = lineData.getQuantityBase();
    }

    public ItemReclassLineData(int mLineNo,String mDocNo, String mBinCode, String mNewBinCode, String mItemNo, String mUom,float mQuantity, float mQuantityBased) {
        this.LineNo = mLineNo;
        this.DocumentNo = mDocNo;
        this.BinCode = mBinCode;
        this.NewBinCode = mNewBinCode;
        this.ItemNo = mItemNo;
        this.Uom = mUom;
        this.Quantity = mQuantity;
        this.QuantityBase = mQuantityBased;
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

    public String getUom() {
        return Uom;
    }

    public void setUom(String uom) {
        Uom = uom;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public float getQuantityBase() {
        return QuantityBase;
    }

    public void setQuantityBase(float quantityBase) {
        QuantityBase = quantityBase;
    }

    public String getDocNo() {
        return DocumentNo;
    }

    public void setDocNo(String docNo) {
        DocumentNo = docNo;
    }

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

    public String getBinCode() {
        return BinCode;
    }

    public void setBinCode(String binCode) {
        BinCode = binCode;
    }

    public String getNewBinCode() {
        return NewBinCode;
    }

    public void setNewBinCode(String newBinCode) {
        NewBinCode = newBinCode;
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
}
