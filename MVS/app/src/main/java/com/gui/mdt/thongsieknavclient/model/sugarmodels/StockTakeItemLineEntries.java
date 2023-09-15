package com.gui.mdt.thongsieknavclient.model.sugarmodels;

import com.orm.SugarRecord;

/**
 * Created by user on 12/30/2016.
 */

public class StockTakeItemLineEntries extends SugarRecord {
    private String StockTakeNo;
    private String ItemNo;
    private int LineNo;

    private String ProductionDate;
    private String ExpiryDate;
    private String QtyToShip;
    private String TmpQtyTotal;
    private String LotNumber;
    private String BarcodeSerialNo;
    private String SerialNo;

    private int EntryIndexNo;
    private boolean IsUploaded;
    private boolean IsNewAddLotNoNotExistEntry;
    private Long TimeStamp;

    public StockTakeItemLineEntries() {
    }

    public StockTakeItemLineEntries( String stockTakeNo, String itemNo, int lineNo, String productionDate, String expiryDate, String qtyToShip, String tmpQtyTotal, String lotNumber, String barcodeSerialNo, String serialNo, int entryIndexNo, boolean isUploaded, boolean isNewAddLotNoNotExistEntry, Long timeStamp) {
        TimeStamp = timeStamp;
        StockTakeNo = stockTakeNo;
        ItemNo = itemNo;
        LineNo = lineNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToShip = qtyToShip;
        TmpQtyTotal = tmpQtyTotal;
        LotNumber = lotNumber;
        BarcodeSerialNo = barcodeSerialNo;
        SerialNo = serialNo;
        EntryIndexNo = entryIndexNo;
        IsUploaded = isUploaded;
        IsNewAddLotNoNotExistEntry = isNewAddLotNoNotExistEntry;
    }

    public String getStockTakeNo() {
        return StockTakeNo;
    }

    public void setStockTakeNo(String stockTakeNo) {
        StockTakeNo = stockTakeNo;
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
        return TmpQtyTotal;
    }

    public void setTmpQtyTotal(String tmpQtyTotal) {
        TmpQtyTotal = tmpQtyTotal;
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
}