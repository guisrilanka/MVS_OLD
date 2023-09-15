package com.gui.mdt.thongsieknavclient.model.sugarmodels;

import android.content.ClipData;

import com.orm.SugarRecord;

/**
 * Created by user on 1/24/2017.
 */

public class ItemReclassAddNewLineEntry extends SugarRecord{

    private String DocNo;
    private String ItemNo;
    private int LineNo;

    private String ProductionDate;
    private String ExpiryDate;
    private String QtyToShip;
    private String LotNumber;
    private String BarcodeSerialNo;
    private String SerialNo;
    private boolean IsScanned;

    private int EntryIndexNo;
    private boolean IsUploaded;
    private Long TimeStamp;

    public ItemReclassAddNewLineEntry() {
    }

    public ItemReclassAddNewLineEntry(String docNo, String itemNo, int lineNo, String productionDate, String expiryDate, String qtyToShip, String lotNumber, String barcodeSerialNo, String serialNo, boolean isScanned, int entryIndexNo, boolean isUploaded, Long timeStamp) {
        DocNo = docNo;
        ItemNo = itemNo;
        LineNo = lineNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToShip = qtyToShip;
        LotNumber = lotNumber;
        BarcodeSerialNo = barcodeSerialNo;
        SerialNo = serialNo;
        IsScanned = isScanned;
        EntryIndexNo = entryIndexNo;
        IsUploaded = isUploaded;
        TimeStamp = timeStamp;
    }

    public boolean isScanned() {
        return IsScanned;
    }

    public void setScanned(boolean scanned) {
        IsScanned = scanned;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
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

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return " DocNo: " + DocNo + " ItemNo: " + ItemNo + " ProdDate: " + ProductionDate + " ExpDate: " + ExpiryDate
                + " QTY: " + QtyToShip + " LotNo: " + LotNumber + " SerialNo; " + BarcodeSerialNo;
    }
}
