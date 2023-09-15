package com.gui.mdt.thongsieknavclient.model.sugarmodels;

import com.orm.SugarRecord;

/**
 * Created by user on 12/8/2016.
 */

public class ItemReclassItemLineEntries extends SugarRecord {
    private String itemRclsNo;
    private String ItemNo;
    private int LineNo;

    private String ProductionDate;
    private String ExpiryDate;
    private String QtyToShip;
    private String tmpQtyTotal;
    private String LotNumber;
    private String BarcodeSerialNo;
    private String SerialNo;

    private int EntryIndexNo;
    private boolean IsUploaded;
    private boolean IsNewAddLotNoNotExistEntry;
    private Long TimeStamp;

    public ItemReclassItemLineEntries() {
    }

    public ItemReclassItemLineEntries(String itemRclsNo, String itemNo, int lineNo, String productionDate, String expiryDate, String qtyToShip, String tmpQtyTotal, String lotNumber, String barcodeSerialNo, String serialNo, int entryIndexNo, boolean isUploaded, boolean isNewAddLotNoNotExistEntry, Long timeStamp) {
        this.itemRclsNo = itemRclsNo;
        ItemNo = itemNo;
        LineNo = lineNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToShip = qtyToShip;
        this.tmpQtyTotal = tmpQtyTotal;
        LotNumber = lotNumber;
        BarcodeSerialNo = barcodeSerialNo;
        SerialNo = serialNo;
        EntryIndexNo = entryIndexNo;
        IsUploaded = isUploaded;
        IsNewAddLotNoNotExistEntry = isNewAddLotNoNotExistEntry;
        TimeStamp = timeStamp;
    }

    public String getItemRclsNo() {
        return itemRclsNo;
    }

    public void setItemRclsNo(String itemRclsNo) {
        this.itemRclsNo = itemRclsNo;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

    public String getProductionDate() {
        return ProductionDate;
    }

    public void setProductionDate(String productionDate) {
        ProductionDate = productionDate;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getQtyToShip() {
        return QtyToShip;
    }

    public void setQtyToShip(String qtyToShip) {
        QtyToShip = qtyToShip;
    }

    public String getTmpQtyTotal() {
        return tmpQtyTotal;
    }

    public void setTmpQtyTotal(String tmpQtyTotal) {
        this.tmpQtyTotal = tmpQtyTotal;
    }

    public String getLotNumber() {
        return LotNumber;
    }

    public void setLotNumber(String lotNumber) {
        LotNumber = lotNumber;
    }

    public String getBarcodeSerialNo() {
        return BarcodeSerialNo;
    }

    public void setBarcodeSerialNo(String barcodeSerialNo) {
        BarcodeSerialNo = barcodeSerialNo;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public int getEntryIndexNo() {
        return EntryIndexNo;
    }

    public void setEntryIndexNo(int entryIndexNo) {
        EntryIndexNo = entryIndexNo;
    }

    public boolean isUploaded() {
        return IsUploaded;
    }

    public void setUploaded(boolean uploaded) {
        IsUploaded = uploaded;
    }

    public boolean isNewAddLotNoNotExistEntry() {
        return IsNewAddLotNoNotExistEntry;
    }

    public void setNewAddLotNoNotExistEntry(boolean newAddLotNoNotExistEntry) {
        IsNewAddLotNoNotExistEntry = newAddLotNoNotExistEntry;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return " DocNo: " + itemRclsNo + " ItemNo: " + ItemNo + " ProdDate: " + ProductionDate + " ExpDate: " + ExpiryDate
                + " QTY: " + QtyToShip + " LotNo: " + LotNumber;
    }

}
